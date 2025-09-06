package com.example.controller;

import com.example.generator.JavaCodeGenerator;
import com.example.generator.JavaCodeGenerator.VariableInfo;
import com.example.slicer.SliceExecutor;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.expr.NameExpr;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Controller
@RequestMapping("/slice")
public class SliceController {

    @Autowired
    private JavaCodeGenerator javaCodeGenerator;

    @Autowired
    private SliceExecutor sliceExecutor;

    private final JavaParser javaParser = new JavaParser();

    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("/controlflow")
    public String controlflow() {
        return "controlflow";
    }

    @PostMapping("/test")
    @ResponseBody
    public Map<String, Object> runSliceTest(@RequestParam int numMutations) {

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> testResults = new ArrayList<>();

        try {
            log.info("Starting JSmith metamorphic test with {} mutations", numMutations);

            // 清理之前的切片文件
            cleanupSliceFiles();

            // 使用JSmith生成器生成变异文件
            List<String> mutatedFiles = javaCodeGenerator.generateJSmithVariableRenameTestFiles(numMutations);
            log.info("Generated {} JSmith test files", mutatedFiles.size());
            
            // 过滤出原始文件（mutated目录中的文件）
            mutatedFiles = mutatedFiles.stream()
                .filter(file -> file.contains("mutated") && file.contains("JSmith"))
                .collect(java.util.stream.Collectors.toList());
            log.info("Filtered to {} JSmith mutated files for testing", mutatedFiles.size());

            // 对每个变异文件进行切片
            for (String file : mutatedFiles) {
                log.info("Processing file: {}", file);
                Map<String, Object> testResult = new HashMap<>();
                testResult.put("originalFile", file);

                try {
                    // 获取对应的JSmith重命名文件
                    String renamedFile = file.replace("mutated", "renamed").replace("JSmith_mutated_", "JSmith_renamed_");
                    testResult.put("renamedFile", renamedFile);

                    // 读取原始文件内容用于显示
                    String originalContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(file)));
                    testResult.put("originalFileContent", originalContent);

                    // 读取重命名文件内容用于显示
                    String renamedContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(renamedFile)));
                    testResult.put("renamedFileContent", renamedContent);

                    // 先对原始文件选择切片变量
                    VariableInfo originalVariableInfo = javaCodeGenerator.findVariableForSlicing(file);
                    if (originalVariableInfo == null) {
                        throw new RuntimeException("No suitable variable found for slicing in original file: " + file);
                    }

                    // 根据变量映射关系，找到重命名文件中对应的变量名
                    String originalVarName = originalVariableInfo.getVariableName();

                    log.info("=== Variable Mapping Debug ===");
                    log.info("Original file: {}", file);
                    log.info("Renamed file: {}", renamedFile);
                    log.info("Original variable: {} at line {}", originalVarName, originalVariableInfo.getLineNumber());

                    // 对于变量重命名蜕变关系，直接在重命名文件的相同行号查找变量
                    log.info("=== Attempting to find variable at same line ===");
                    log.info("Looking for variable at line {} in file: {}", originalVariableInfo.getLineNumber(), renamedFile);
                    String renamedVarName = findVariableAtSameLine(renamedFile, originalVariableInfo.getLineNumber());
                    log.info("findVariableAtSameLine returned: {}", renamedVarName);

                    if (renamedVarName == null) {
                        log.error("=== Variable Mapping Failed ===");
                        log.error("Could not find variable at line {} in renamed file: {}", originalVariableInfo.getLineNumber(), renamedFile);
                        log.error("This violates the metamorphic relation requirement: variables must exist at the same line");

                        // 对于蜕变测试，我们不能回退到不同的变量选择
                        // 这会破坏蜕变关系的一致性
                        throw new RuntimeException(String.format(
                            "Metamorphic relation violation: Could not find corresponding variable at line %d in renamed file. " +
                            "Original variable '%s' at line %d should have a corresponding renamed variable at the same line.",
                            originalVariableInfo.getLineNumber(), originalVarName, originalVariableInfo.getLineNumber()));
                    }

                    log.info("Found renamed variable: {}", renamedVarName);

                    // 验证变量确实存在于对应的文件中
                    boolean originalVarExists = verifyVariableExists(file, originalVarName);
                    boolean renamedVarExists = verifyVariableExists(renamedFile, renamedVarName);

                    log.info("Variable existence verification:");
                    log.info("  Original variable '{}' exists in {}: {}", originalVarName, file, originalVarExists);
                    log.info("  Renamed variable '{}' exists in {}: {}", renamedVarName, renamedFile, renamedVarExists);

                    if (!originalVarExists) {
                        throw new RuntimeException("Original variable '" + originalVarName + "' does not exist in file: " + file);
                    }

                    if (!renamedVarExists) {
                        throw new RuntimeException("Renamed variable '" + renamedVarName + "' does not exist in file: " + renamedFile);
                    }

                    log.info("Original variable: {} -> Renamed variable: {} at line {}",
                            originalVarName, renamedVarName, originalVariableInfo.getLineNumber());

                    // 对变异文件执行切片（使用已选择的变量和行号）
                    log.info("Executing slice for mutated file: {} with variable: {} at line {}",
                            file, originalVarName, originalVariableInfo.getLineNumber());
                    String mutatedSliceContent = sliceExecutor.executeSliceWithVariable(file, originalVarName, originalVariableInfo.getLineNumber());
                    log.info("Mutated slice content: {}", mutatedSliceContent);
                    testResult.put("mutatedSliceContent", mutatedSliceContent);

                    // 对于变量重命名蜕变关系，应该使用相同的行号
                    // 因为重命名只是改变了变量名，代码结构和行号应该保持一致
                    int targetLineNumber = originalVariableInfo.getLineNumber();

                    log.info("=== Line Number Debug ===");
                    log.info("Original variable info: {} at line {}", originalVariableInfo.getVariableName(), originalVariableInfo.getLineNumber());
                    log.info("Target line number for renamed file: {}", targetLineNumber);
                    log.info("Renamed variable found: {}", renamedVarName);

                    log.info("Using same line number for renamed variable: {} at line {} (variable rename metamorphic relation)",
                            renamedVarName, targetLineNumber);

                    // 对重命名文件执行切片（使用相同的行号，但是重命名的变量）
                    log.info("Executing slice for renamed file: {} with variable: {} at line {}",
                            renamedFile, renamedVarName, targetLineNumber);
                    String renamedSliceContent = sliceExecutor.executeSliceWithVariable(renamedFile, renamedVarName, targetLineNumber);
                    log.info("Renamed slice content: {}", renamedSliceContent);
                    testResult.put("renamedSliceContent", renamedSliceContent);

                    // 比较切片是否等价
                    boolean isEquivalent = compareSlices(mutatedSliceContent, renamedSliceContent);
                    log.info("Slices are {} equivalent", isEquivalent ? "" : "not");
                    testResult.put("equivalent", isEquivalent);

                    testResult.put("success", true);

                } catch (Exception e) {
                    log.error("Error processing file: " + file, e);
                    testResult.put("error", e.getMessage());
                    testResult.put("success", false);
                }

                testResults.add(testResult);
            }

            // 统计结果
            result.put("total", testResults.size());
            result.put("results", testResults);

        } catch (Exception e) {
            log.error("Error running test", e);
            result.put("error", e.getMessage());
            result.put("total", 0);
            result.put("results", new ArrayList<>());
        }

        return result;
    }

    @PostMapping("/test-deadcode")
    @ResponseBody
    public Map<String, Object> runDeadCodeTest(@RequestParam int numMutations) {

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> testResults = new ArrayList<>();

        try {
            log.info("Starting dead code metamorphic test with {} mutations", numMutations);

            // 测试isDeadCodeLine方法
            log.info("=== Testing isDeadCodeLine method ===");
            String testLine1 = "int unusedVar715 = 47;";
            String testLine2 = "if (false) { int x = 50; }";
            String testLine3 = "int temp20 = 63;";
            log.info("Test line 1: '{}' - isDeadCode: {}", testLine1, isDeadCodeLine(testLine1));
            log.info("Test line 2: '{}' - isDeadCode: {}", testLine2, isDeadCodeLine(testLine2));
            log.info("Test line 3: '{}' - isDeadCode: {}", testLine3, isDeadCodeLine(testLine3));

            // 生成JSmith变异文件（只生成mutated文件，不生成renamed文件）
            List<String> mutatedFiles = generateJSmithMutatedFilesOnly(numMutations);
            log.info("Generated {} JSmith mutated files", mutatedFiles.size());

            // 为每个JSmith文件生成对应的死代码文件
            List<String> deadCodeFiles = new ArrayList<>();
            for (String mutatedFile : mutatedFiles) {
                if (mutatedFile.contains("_mutated_")) {
                    try {
                        // 读取原始文件内容
                        byte[] bytes = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(mutatedFile));
                        String originalContent = new String(bytes, StandardCharsets.UTF_8);

                        // 选择切片变量
                        VariableInfo variableInfo = javaCodeGenerator.findVariableForSlicing(mutatedFile);
                        if (variableInfo != null) {
                            // 添加死代码
                            String deadCodeContent = addDeadCodeToJSmithFile(originalContent, variableInfo.getVariableName());

                            // 保存死代码文件
                            String deadCodeFile = mutatedFile.replace("mutated", "deadcode").replace("_mutated_", "_deadcode_");
                            java.nio.file.Files.write(java.nio.file.Paths.get(deadCodeFile), deadCodeContent.getBytes(StandardCharsets.UTF_8));
                            deadCodeFiles.add(deadCodeFile);
                            log.info("Generated dead code file: {}", deadCodeFile);
                        }
                    } catch (Exception e) {
                        log.error("Error generating dead code for file: {}", mutatedFile, e);
                    }
                }
            }

            // 只处理有对应死代码文件的mutated文件
            mutatedFiles = mutatedFiles.stream()
                .filter(f -> f.contains("_mutated_"))
                .filter(f -> deadCodeFiles.contains(f.replace("mutated", "deadcode").replace("_mutated_", "_deadcode_")))
                .collect(java.util.stream.Collectors.toList());

            log.info("Generated {} dead code files for testing", deadCodeFiles.size());

            // 对每个变异文件进行切片
            for (String file : mutatedFiles) {
                log.info("Processing file: {}", file);
                Map<String, Object> testResult = new HashMap<>();
                testResult.put("originalFile", file);

                try {
                    // 获取对应的无用代码文件
                    String deadCodeFile = file.replace("mutated", "deadcode").replace("_mutated_", "_deadcode_");
                    testResult.put("deadCodeFile", deadCodeFile);

                    // 读取原始文件内容用于显示
                    byte[] originalBytes = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(file));
                    String originalContent = new String(originalBytes, StandardCharsets.UTF_8);
                    testResult.put("originalFileContent", originalContent);

                    // 读取无用代码文件内容用于显示
                    byte[] deadCodeBytes = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(deadCodeFile));
                    String deadCodeContent = new String(deadCodeBytes, StandardCharsets.UTF_8);
                    testResult.put("deadCodeFileContent", deadCodeContent);

                    // 对原始文件选择切片变量
                    VariableInfo originalVariableInfo = javaCodeGenerator.findVariableForSlicing(file);
                    if (originalVariableInfo == null) {
                        throw new RuntimeException("No suitable variable found for slicing in original file: " + file);
                    }

                    log.info("Selected variable for slicing: {} at line {}",
                            originalVariableInfo.getVariableName(), originalVariableInfo.getLineNumber());

                    // 计算无用代码文件中的行号偏移
                    log.info("=== About to call calculateLineOffset ===");
                    int lineOffset = calculateLineOffset(originalContent, deadCodeContent, originalVariableInfo.getLineNumber());
                    log.info("=== calculateLineOffset returned: {} ===", lineOffset);
                    int adjustedLineNumber = originalVariableInfo.getLineNumber() + lineOffset;

                    log.info("Original line: {}, Dead code line: {} (offset: {})",
                            originalVariableInfo.getLineNumber(), adjustedLineNumber, lineOffset);

                    // 对原始文件执行切片
                    log.info("Executing slice for original file: {}", file);
                    String originalSliceContent = sliceExecutor.executeSliceWithVariable(file, originalVariableInfo.getVariableName(), originalVariableInfo.getLineNumber());
                    log.info("Original slice content: {}", originalSliceContent);
                    testResult.put("originalSliceContent", originalSliceContent);

                    // 对无用代码文件执行切片（使用相同的变量名和调整后的行号）
                    log.info("Executing slice for dead code file: {} with variable: {} at line {}",
                            deadCodeFile, originalVariableInfo.getVariableName(), adjustedLineNumber);
                    
                    // 检查无用代码文件是否存在
                    if (!java.nio.file.Files.exists(java.nio.file.Paths.get(deadCodeFile))) {
                        throw new RuntimeException("Dead code file does not exist: " + deadCodeFile);
                    }
                    
                    String deadCodeSliceContent = sliceExecutor.executeSliceWithVariable(deadCodeFile, originalVariableInfo.getVariableName(), adjustedLineNumber);
                    log.info("Dead code slice content: {}", deadCodeSliceContent);
                    testResult.put("deadCodeSliceContent", deadCodeSliceContent);

                    // 比较切片是否等价
                    boolean isEquivalent = compareSlices(originalSliceContent, deadCodeSliceContent);
                    log.info("Slices are {} equivalent", isEquivalent ? "" : "not");
                    testResult.put("equivalent", isEquivalent);

                    testResult.put("success", true);

                } catch (Exception e) {
                    log.error("Error processing file: " + file, e);
                    testResult.put("error", e.getMessage());
                    testResult.put("success", false);
                }

                testResults.add(testResult);
            }

            // 统计结果
            result.put("total", testResults.size());
            result.put("results", testResults);

        } catch (Exception e) {
            log.error("Error running dead code test", e);
            result.put("error", e.getMessage());
            result.put("total", 0);
            result.put("results", new ArrayList<>());
        }

        return result;
    }

    @PostMapping("/test-reorder")
    @ResponseBody
    public Map<String, Object> runStatementReorderTest(@RequestParam int numMutations) {

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> testResults = new ArrayList<>();

        try {
            log.info("Starting statement reorder metamorphic test with {} mutations", numMutations);

            // 生成语句重排序的变异文件
            List<String> originalFiles = javaCodeGenerator.generateStatementReorderFiles("", numMutations);
            log.info("Generated {} statement reorder files", originalFiles.size());

            // 对每个原始文件进行切片
            for (String originalFile : originalFiles) {
                log.info("Processing file: {}", originalFile);
                Map<String, Object> testResult = new HashMap<>();
                testResult.put("originalFile", originalFile);

                try {
                    // 获取对应的重排序文件
                    String reorderedFile = originalFile.replace("mutated", "reordered").replace("_original_", "_reordered_");
                    testResult.put("reorderedFile", reorderedFile);

                    // 读取原始文件内容用于显示
                    String originalContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(originalFile)));
                    testResult.put("originalFileContent", originalContent);

                    // 读取重排序文件内容用于显示
                    String reorderedContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(reorderedFile)));
                    testResult.put("reorderedFileContent", reorderedContent);

                    // 对原始文件选择切片变量
                    VariableInfo originalVariableInfo = javaCodeGenerator.findVariableForSlicing(originalFile);
                    if (originalVariableInfo == null) {
                        throw new RuntimeException("No suitable variable found for slicing in original file: " + originalFile);
                    }

                    log.info("Selected variable for slicing in original file: {} at line {}",
                            originalVariableInfo.getVariableName(), originalVariableInfo.getLineNumber());

                    // 对重排序文件选择切片变量
                    VariableInfo reorderedVariableInfo = javaCodeGenerator.findVariableForSlicing(reorderedFile);
                    if (reorderedVariableInfo == null) {
                        throw new RuntimeException("No suitable variable found for slicing in reordered file: " + reorderedFile);
                    }

                    log.info("Selected variable for slicing in reordered file: {} at line {}",
                            reorderedVariableInfo.getVariableName(), reorderedVariableInfo.getLineNumber());

                    // 对原始文件执行切片
                    log.info("Executing slice for original file: {}", originalFile);
                    String originalSliceContent = sliceExecutor.executeSliceWithVariable(originalFile, originalVariableInfo.getVariableName(), originalVariableInfo.getLineNumber());
                    log.info("Original slice content: {}", originalSliceContent);
                    testResult.put("originalSliceContent", originalSliceContent);

                    // 对重排序文件执行切片（使用重排序文件中的变量和行号）
                    log.info("Executing slice for reordered file: {} with variable: {} at line {}",
                            reorderedFile, reorderedVariableInfo.getVariableName(), reorderedVariableInfo.getLineNumber());
                    String reorderedSliceContent = sliceExecutor.executeSliceWithVariable(reorderedFile, reorderedVariableInfo.getVariableName(), reorderedVariableInfo.getLineNumber());
                    log.info("Reordered slice content: {}", reorderedSliceContent);
                    testResult.put("reorderedSliceContent", reorderedSliceContent);

                    // 比较切片是否等价
                    boolean isEquivalent = compareSlices(originalSliceContent, reorderedSliceContent);
                    log.info("Slices are {} equivalent", isEquivalent ? "" : "not");
                    testResult.put("equivalent", isEquivalent);

                    testResult.put("success", true);

                } catch (Exception e) {
                    log.error("Error processing file: " + originalFile, e);
                    testResult.put("error", e.getMessage());
                    testResult.put("success", false);
                }

                testResults.add(testResult);
            }

            // 统计结果
            result.put("total", testResults.size());
            result.put("results", testResults);

        } catch (Exception e) {
            log.error("Error running statement reorder test", e);
            result.put("error", e.getMessage());
            result.put("total", 0);
            result.put("results", new ArrayList<>());
        }

        return result;
    }

    @PostMapping("/test-controlflow")
    @ResponseBody
    public Map<String, Object> runControlFlowTest(@RequestParam int numMutations) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> testResults = new ArrayList<>();

        try {
            log.info("Starting control flow metamorphic test with {} mutations", numMutations);

            // 生成原始文件
            List<String> originalFiles = javaCodeGenerator.generateMutatedFiles("", numMutations);
            log.info("Generated {} original files", originalFiles.size());

            // 对每个原始文件生成对应的控制流变换文件
            for (int i = 0; i < originalFiles.size(); i++) {
                String originalFile = originalFiles.get(i);
                
                // 读取原始文件内容
                String originalFileContent = Files.readString(Paths.get(originalFile), StandardCharsets.UTF_8);
                
                // 对原始内容进行控制流变换
                String transformedContent = javaCodeGenerator.transformControlFlow(originalFileContent);
                
                // 保存变换后的文件
                String controlFlowFileName = String.format("Example_controlflow_%d.java", i);
                String controlFlowFilePath = Paths.get("controlflow", controlFlowFileName).toString();
                Files.write(Paths.get(controlFlowFilePath), transformedContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                
                String controlFlowFile = controlFlowFilePath;
                
                log.info("Processing file pair: {} and {}", originalFile, controlFlowFile);
                Map<String, Object> testResult = new HashMap<>();
                testResult.put("originalFile", originalFile);
                testResult.put("controlFlowFile", controlFlowFile);

                try {

                    // 读取原始文件内容用于显示
                    testResult.put("originalFileContent", originalFileContent);

                    // 读取控制流变换文件内容用于显示
                    String controlFlowContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(controlFlowFile)));
                    testResult.put("controlflowFileContent", controlFlowContent);

                    // 对原始文件选择切片变量
                    VariableInfo originalVariableInfo = javaCodeGenerator.findVariableForSlicing(originalFile);
                    if (originalVariableInfo == null) {
                        throw new RuntimeException("No suitable variable found for slicing in original file: " + originalFile);
                    }

                    log.info("Selected variable for slicing: {} at line {}",
                            originalVariableInfo.getVariableName(), originalVariableInfo.getLineNumber());

                    // 对原始文件执行切片
                    log.info("Executing slice for original file: {}", originalFile);
                    String originalSliceContent = sliceExecutor.executeSliceWithVariable(originalFile, originalVariableInfo.getVariableName(), originalVariableInfo.getLineNumber());
                    log.info("Original slice content: {}", originalSliceContent);
                    testResult.put("originalSliceContent", originalSliceContent);

                    // 对控制流变换文件重新查找变量最新行号
                    VariableInfo controlFlowVariableInfo = javaCodeGenerator.findVariableLastAssignment(controlFlowFile, originalVariableInfo.getVariableName());
                    if (controlFlowVariableInfo == null) {
                        throw new RuntimeException("No suitable variable found for slicing in control flow file: " + controlFlowFile);
                    }

                    log.info("Executing slice for control flow file: {} with variable: {} at line {}",
                            controlFlowFile, controlFlowVariableInfo.getVariableName(), controlFlowVariableInfo.getLineNumber());
                    String controlFlowSliceContent = sliceExecutor.executeSliceWithVariable(controlFlowFile, controlFlowVariableInfo.getVariableName(), controlFlowVariableInfo.getLineNumber());
                    log.info("Control flow slice content: {}", controlFlowSliceContent);
                    testResult.put("controlflowSliceContent", controlFlowSliceContent);

                    // 比较切片是否等价
                    boolean isEquivalent = compareSlices(originalSliceContent, controlFlowSliceContent);
                    log.info("Slices are {} equivalent", isEquivalent ? "" : "not");
                    testResult.put("equivalent", isEquivalent);

                    testResult.put("success", true);

                } catch (Exception e) {
                    log.error("Error processing file: " + originalFile, e);
                    testResult.put("error", e.getMessage());
                    testResult.put("success", false);
                }

                testResults.add(testResult);
            }

            // 统计结果
            result.put("total", testResults.size());
            result.put("results", testResults);

        } catch (Exception e) {
            log.error("Error running control flow test", e);
            result.put("error", e.getMessage());
            result.put("total", 0);
            result.put("results", new ArrayList<>());
        }

        return result;
    }

    @PostMapping("/test-dataflow")
    @ResponseBody
    public Map<String, Object> runDataFlowTest(@RequestParam int numMutations) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> testResults = new ArrayList<>();

        try {
            log.info("Starting data flow metamorphic test with {} mutations", numMutations);

            // 生成数据流等价变换的变异文件
            List<String> originalFiles = javaCodeGenerator.generateDataFlowFiles("", numMutations);
            log.info("Generated {} data flow files", originalFiles.size());

            // 对每个原始文件进行切片
            for (String originalFile : originalFiles) {
                log.info("Processing file: {}", originalFile);
                Map<String, Object> testResult = new HashMap<>();
                testResult.put("originalFile", originalFile);

                try {
                    // 获取对应的数据流变换文件
                    String dataFlowFile = originalFile.replace("mutated", "dataflow").replace("_original_", "_dataflow_");
                    testResult.put("dataflowFile", dataFlowFile);

                    // 读取原始文件内容用于显示
                    String originalContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(originalFile)));
                    testResult.put("originalFileContent", originalContent);

                    // 读取数据流变换文件内容用于显示
                    String dataFlowContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(dataFlowFile)));
                    testResult.put("dataflowFileContent", dataFlowContent);

                    // 对原始文件选择切片变量
                    VariableInfo originalVariableInfo = javaCodeGenerator.findVariableForSlicing(originalFile);
                    if (originalVariableInfo == null) {
                        throw new RuntimeException("No suitable variable found for slicing in original file: " + originalFile);
                    }

                    log.info("Selected variable for slicing: {} at line {}",
                            originalVariableInfo.getVariableName(), originalVariableInfo.getLineNumber());

                    // 对原始文件执行切片
                    log.info("Executing slice for original file: {}", originalFile);
                    String originalSliceContent = sliceExecutor.executeSliceWithVariable(originalFile, originalVariableInfo.getVariableName(), originalVariableInfo.getLineNumber());
                    log.info("Original slice content: {}", originalSliceContent);
                    testResult.put("originalSliceContent", originalSliceContent);

                    // 对数据流变换文件重新查找变量最新行号
                    VariableInfo dataFlowVariableInfo = javaCodeGenerator.findVariableLastAssignment(dataFlowFile, originalVariableInfo.getVariableName());
                    if (dataFlowVariableInfo == null) {
                        throw new RuntimeException("No suitable variable found for slicing in data flow file: " + dataFlowFile);
                    }

                    log.info("Executing slice for data flow file: {} with variable: {} at line {}",
                            dataFlowFile, dataFlowVariableInfo.getVariableName(), dataFlowVariableInfo.getLineNumber());
                    String dataFlowSliceContent = sliceExecutor.executeSliceWithVariable(dataFlowFile, dataFlowVariableInfo.getVariableName(), dataFlowVariableInfo.getLineNumber());
                    log.info("Data flow slice content: {}", dataFlowSliceContent);
                    testResult.put("dataflowSliceContent", dataFlowSliceContent);

                    // 比较切片是否等价
                    boolean isEquivalent = compareSlices(originalSliceContent, dataFlowSliceContent);
                    log.info("Slices are {} equivalent", isEquivalent ? "" : "not");
                    testResult.put("equivalent", isEquivalent);

                    testResult.put("success", true);

                } catch (Exception e) {
                    log.error("Error processing file: " + originalFile, e);
                    testResult.put("error", e.getMessage());
                    testResult.put("success", false);
                }

                testResults.add(testResult);
            }

            // 统计结果
            result.put("total", testResults.size());
            result.put("results", testResults);

        } catch (Exception e) {
            log.error("Error running data flow test", e);
            result.put("error", e.getMessage());
            result.put("total", 0);
            result.put("results", new ArrayList<>());
        }

        return result;
    }

    /**
     * 比较两个切片是否等价
     * 通过解析AST并比较结构来判断等价性，使用公共变量名标准化
     */
    private boolean compareSlices(String slice1, String slice2) {
        if (slice1 == null || slice2 == null) {
            return false;
        }

        try {
            // 提取 Java 代码部分
            String code1 = extractJavaCode(slice1);
            String code2 = extractJavaCode(slice2);

            // 解析代码
            CompilationUnit cu1 = javaParser.parse(code1).getResult().orElseThrow(() ->
                    new RuntimeException("Failed to parse first slice"));
            CompilationUnit cu2 = javaParser.parse(code2).getResult().orElseThrow(() ->
                    new RuntimeException("Failed to parse second slice"));

            // 获取所有变量声明
            List<VariableDeclarator> vars1 = cu1.findAll(VariableDeclarator.class);
            List<VariableDeclarator> vars2 = cu2.findAll(VariableDeclarator.class);

            // 检查变量数量是否相同
            if (vars1.size() != vars2.size()) {
                log.info("Different number of variables: {} vs {}", vars1.size(), vars2.size());
                return false;
            }

            // 创建变量映射表 - 将每个变量映射到一个标准名称
            Map<String, String> varMap1 = createVariableMapping(vars1);
            Map<String, String> varMap2 = createVariableMapping(vars2);
            
            // 找出所有引用的但未声明的变量（如字段引用）
            List<String> undeclaredRefs1 = findUndeclaredReferences(cu1, varMap1.keySet());
            List<String> undeclaredRefs2 = findUndeclaredReferences(cu2, varMap2.keySet());
            
            // 检查未声明引用的数量是否相同
            if (undeclaredRefs1.size() != undeclaredRefs2.size()) {
                log.info("Different number of undeclared references: {} vs {}", 
                         undeclaredRefs1.size(), undeclaredRefs2.size());
                return false;
            }
            
            // 为未声明的引用创建映射
            int refCounter = 1;
            for (String ref : undeclaredRefs1) {
                varMap1.put(ref, "EXTERNAL" + refCounter);
                refCounter++;
            }
            
            refCounter = 1;
            for (String ref : undeclaredRefs2) {
                varMap2.put(ref, "EXTERNAL" + refCounter);
                refCounter++;
            }
            
            log.info("Variable mapping for slice 1: {}", varMap1);
            log.info("Variable mapping for slice 2: {}", varMap2);

            // 标准化第一个切片中的变量名
            String normalizedSlice1 = normalizeSlice(cu1, varMap1);

            // 标准化第二个切片中的变量名
            String normalizedSlice2 = normalizeSlice(cu2, varMap2);

            // 去除注释和空白字符后比较
            normalizedSlice1 = removeCommentsAndWhitespace(normalizedSlice1);
            normalizedSlice2 = removeCommentsAndWhitespace(normalizedSlice2);

            boolean isEquivalent = normalizedSlice1.equals(normalizedSlice2);
            if (!isEquivalent) {
                log.info("Slices are not equivalent after normalization");
                log.info("Normalized slice 1: {}", normalizedSlice1);
                log.info("Normalized slice 2: {}", normalizedSlice2);
            }

            return isEquivalent;

        } catch (Exception e) {
            log.error("Error comparing slices: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 找出所有未在本地声明但被引用的变量名
     */
    private List<String> findUndeclaredReferences(CompilationUnit cu, Set<String> declaredVars) {
        List<String> undeclaredRefs = new ArrayList<>();
        
        // 找出所有名称引用
        cu.findAll(NameExpr.class).forEach(nameExpr -> {
            String name = nameExpr.getNameAsString();
            // 如果名称未在本地声明但被引用，则添加到未声明引用列表
            if (!declaredVars.contains(name) && !undeclaredRefs.contains(name)) {
                undeclaredRefs.add(name);
                log.info("Found undeclared reference: {}", name);
            }
        });
        
        return undeclaredRefs;
    }

    /**
     * 为所有变量创建映射关系
     */
    private Map<String, String> createVariableMapping(List<VariableDeclarator> variables) {
        Map<String, String> varMap = new HashMap<>();
        
        // 为每个变量分配一个标准名称 (VAR1, VAR2, ...)
        int varCounter = 1;
        for (VariableDeclarator vd : variables) {
            String varName = vd.getNameAsString();
            String standardName = "VAR" + varCounter++;
            varMap.put(varName, standardName);
        }
        
        return varMap;
    }

    /**
     * 去除注释和空白字符，用于切片比较
     */
    private String removeCommentsAndWhitespace(String code) {
        try {
            // 去除多行注释 /* ... */
            code = code.replaceAll("/\\*[\\s\\S]*?\\*/", "");

            // 去除单行注释 // ...
            code = code.replaceAll("//.*", "");

            // 去除所有空白字符（空格、制表符、换行符等）
            code = code.replaceAll("\\s+", "");

            return code.trim();

        } catch (Exception e) {
            log.error("Error removing comments and whitespace: {}", e.getMessage());
            return code.replaceAll("\\s+", "").trim();
        }
    }

    /**
     * 标准化切片中的所有变量名
     */
    private String normalizeSlice(CompilationUnit cu, Map<String, String> variableMapping) {
        try {
            // 创建一个新的CompilationUnit来避免修改原始对象
            CompilationUnit normalizedCu = cu.clone();
            
            // 替换所有变量声明
            normalizedCu.findAll(VariableDeclarator.class).forEach(vd -> {
                String varName = vd.getNameAsString();
                if (variableMapping.containsKey(varName)) {
                    vd.setName(variableMapping.get(varName));
                    log.debug("Normalized variable declaration: {} -> {}", varName, variableMapping.get(varName));
                }
            });

            // 替换所有变量使用，包括未声明的引用
            normalizedCu.findAll(NameExpr.class).forEach(nameExpr -> {
                String varName = nameExpr.getNameAsString();
                if (variableMapping.containsKey(varName)) {
                    nameExpr.setName(variableMapping.get(varName));
                    log.debug("Normalized variable/reference usage: {} -> {}", varName, variableMapping.get(varName));
                }
            });
            
            // 标准化方法参数
            normalizedCu.findAll(com.github.javaparser.ast.body.Parameter.class).forEach(param -> {
                String paramName = param.getNameAsString();
                if (variableMapping.containsKey(paramName)) {
                    param.setName(variableMapping.get(paramName));
                    log.debug("Normalized method parameter: {} -> {}", paramName, variableMapping.get(paramName));
                }
            });

            return normalizedCu.toString();

        } catch (Exception e) {
            log.error("Error normalizing slice: {}", e.getMessage());
            return cu.toString();
        }
    }

    /**
     * 提取 Java 代码部分，忽略注释
     */
    private String extractJavaCode(String slice) {
        // 找到第一个 public class 的位置
        int startIndex = slice.indexOf("public class");
        if (startIndex == -1) {
            return slice;
        }

        // 从 public class 开始截取
        return slice.substring(startIndex);
    }

    /**
     * 根据原始变量名，在重命名文件中找到对应的重命名变量名
     */
    private String findRenamedVariableName(String file, String originalVarName) {
        try {
            // 从JavaCodeGenerator中获取变量映射关系
            Map<String, String> variableMapping = javaCodeGenerator.getVariableMapping(file);

            if (variableMapping.containsKey(originalVarName)) {
                String renamedVarName = variableMapping.get(originalVarName);
                log.info("Found renamed variable using mapping: {} -> {}", originalVarName, renamedVarName);
                return renamedVarName;
            }

            log.error("Variable mapping not found for: {}", originalVarName);
            return null;

        } catch (Exception e) {
            log.error("Error finding renamed variable: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 根据原始变量名，在JSmith重命名文件中找到对应的重命名变量名
     */
    private String findJSmithRenamedVariableName(String file, String originalVarName) {
        try {
            // 从文件路径中提取基础文件名
            String fileName = java.nio.file.Paths.get(file).getFileName().toString();
            
            // 从JavaCodeGenerator中获取JSmith变量映射关系
            Map<String, String> variableMapping = javaCodeGenerator.getVariableMapping(fileName);

            if (variableMapping != null && variableMapping.containsKey(originalVarName)) {
                String renamedVarName = variableMapping.get(originalVarName);
                log.info("Found JSmith renamed variable using mapping: {} -> {}", originalVarName, renamedVarName);
                return renamedVarName;
            }

            // 如果映射中没有找到，尝试直接从重命名文件中分析
            String renamedFile = file.replace("mutated", "renamed").replace("JSmith_mutated_", "JSmith_renamed_");
            if (java.nio.file.Files.exists(java.nio.file.Paths.get(renamedFile))) {
                String renamedContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(renamedFile)));
                String originalContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(file)));
                
                // 通过比较两个文件来推断变量映射
                String inferredRenamedVar = inferVariableMapping(originalContent, renamedContent, originalVarName);
                if (inferredRenamedVar != null) {
                    log.info("Inferred JSmith renamed variable: {} -> {}", originalVarName, inferredRenamedVar);
                    return inferredRenamedVar;
                }
            }

            log.error("JSmith variable mapping not found for: {} in file: {}", originalVarName, fileName);
            return null;

        } catch (Exception e) {
            log.error("Error finding JSmith renamed variable: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 通过比较原始文件和重命名文件来推断变量映射
     * 专门针对变量重命名蜕变关系：基于行号和位置来匹配变量
     */
    private String inferVariableMapping(String originalContent, String renamedContent, String originalVarName) {
        try {
            // 解析两个文件
            CompilationUnit originalCu = javaParser.parse(originalContent).getResult().orElse(null);
            CompilationUnit renamedCu = javaParser.parse(renamedContent).getResult().orElse(null);

            if (originalCu == null || renamedCu == null) {
                log.warn("Failed to parse one or both files for variable mapping inference");
                return null;
            }

            // 获取变量声明
            List<VariableDeclarator> originalVars = originalCu.findAll(VariableDeclarator.class);
            List<VariableDeclarator> renamedVars = renamedCu.findAll(VariableDeclarator.class);

            log.info("Original file has {} variables, renamed file has {} variables",
                    originalVars.size(), renamedVars.size());

            // 找到目标变量在原始文件中的行号
            int targetLineNumber = -1;
            String targetType = null;
            String targetInitValue = null;

            for (VariableDeclarator var : originalVars) {
                if (var.getNameAsString().equals(originalVarName)) {
                    if (var.getBegin().isPresent()) {
                        targetLineNumber = var.getBegin().get().line;
                        targetType = var.getTypeAsString();
                        targetInitValue = var.getInitializer().map(Object::toString).orElse("");
                        break;
                    }
                }
            }

            if (targetLineNumber == -1) {
                log.warn("Could not find line number for target variable '{}' in original file", originalVarName);
                return null;
            }

            log.info("Target variable '{}' found at line {} (type={}, initValue={})",
                    originalVarName, targetLineNumber, targetType, targetInitValue);

            // 在重命名文件中查找相同行号的变量
            for (VariableDeclarator var : renamedVars) {
                if (var.getBegin().isPresent()) {
                    int varLineNumber = var.getBegin().get().line;

                    // 检查是否在相同行号
                    if (varLineNumber == targetLineNumber) {
                        String candidateType = var.getTypeAsString();
                        String candidateInitValue = var.getInitializer().map(Object::toString).orElse("");

                        // 验证类型和初始值是否匹配（应该完全一致，除了变量名）
                        if (targetType.equals(candidateType) && targetInitValue.equals(candidateInitValue)) {
                            String candidateName = var.getNameAsString();
                            log.info("Found matching variable at same line {}: '{}' -> '{}' (type={}, initValue={})",
                                    targetLineNumber, originalVarName, candidateName, candidateType, candidateInitValue);
                            return candidateName;
                        } else {
                            log.warn("Variable at line {} has different type/value: type {} vs {}, initValue {} vs {}",
                                    varLineNumber, targetType, candidateType, targetInitValue, candidateInitValue);
                        }
                    }
                }
            }

            // 如果按行号匹配失败，回退到位置索引匹配
            log.warn("Line-based matching failed, trying position-based matching");

            int targetIndex = -1;
            for (int i = 0; i < originalVars.size(); i++) {
                if (originalVars.get(i).getNameAsString().equals(originalVarName)) {
                    targetIndex = i;
                    break;
                }
            }

            if (targetIndex >= 0 && targetIndex < renamedVars.size()) {
                VariableDeclarator candidateVar = renamedVars.get(targetIndex);
                String candidateType = candidateVar.getTypeAsString();
                String candidateInitValue = candidateVar.getInitializer().map(Object::toString).orElse("");

                if (targetType.equals(candidateType) && targetInitValue.equals(candidateInitValue)) {
                    String candidateName = candidateVar.getNameAsString();
                    log.info("Found matching variable by position {}: '{}' -> '{}' (type={}, initValue={})",
                            targetIndex, originalVarName, candidateName, candidateType, candidateInitValue);
                    return candidateName;
                }
            }

            log.error("Could not find matching variable for '{}' in renamed file", originalVarName);
            return null;

        } catch (Exception e) {
            log.error("Error inferring variable mapping: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 计算无用代码文件中的行号偏移
     */
    private int calculateLineOffset(String originalContent, String deadCodeContent, int targetLine) {
        try {
            log.info("=== Simple fixed line offset calculation ===");
            log.info("Original target line: {}", targetLine);

            // 死代码固定为3行，直接返回3作为偏移
            int fixedOffset = 3;
            log.info("Using fixed offset of {} lines for dead code", fixedOffset);

            return fixedOffset;

        } catch (Exception e) {
            log.error("Error calculating line offset", e);
            return 3; // 默认返回3行偏移
        }
    }



    /**
     * 通过计算无用代码行数来计算偏移
     */
    private int calculateOffsetByLineCount(String[] originalLines, String[] deadCodeLines, int targetLine) {
        try {
            log.info("=== Calculating offset by counting dead code lines ===");
            
            // 计算目标行之前添加的无用代码行数
            int deadCodeLinesBeforeTarget = 0;
            
            // 从main方法开始到目标行之前，计算无用代码行数
            boolean inMainMethod = false;
            for (int i = 0; i < Math.min(targetLine - 1, originalLines.length); i++) {
                String line = originalLines[i].trim();
                
                // 检查是否进入main方法
                if (line.contains("public static void main")) {
                    inMainMethod = true;
                    log.info("Found main method start at line {}", i + 1);
                    continue;
                }
                
                // 如果在main方法内，检查下一行是否为无用代码
                if (inMainMethod && i + 1 < deadCodeLines.length) {
                    String nextDeadCodeLine = deadCodeLines[i + 1].trim();
                    if (isDeadCodeLine(nextDeadCodeLine)) {
                        deadCodeLinesBeforeTarget++;
                        log.info("Found dead code line at position {}: '{}'", i + 2, nextDeadCodeLine);
                    }
                }
            }
            
            log.info("Calculated offset by counting: {} dead code lines before target line {}", 
                    deadCodeLinesBeforeTarget, targetLine);
            return deadCodeLinesBeforeTarget;
            
        } catch (Exception e) {
            log.error("Error calculating offset by line count", e);
            return 0;
        }
    }

    /**
     * 在文件中找到指定变量的声明行号
     */
    private int findVariableDeclarationLine(String[] lines, String variableName) {
        try {
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                String trimmedLine = line.trim();
                
                // 检查是否包含变量声明
                if (trimmedLine.matches("^int\\s+\\w+.*")) {
                    String extractedVarName = extractVariableNameFromLine(line);
                    if (variableName.equals(extractedVarName)) {
                        log.info("Found variable '{}' declaration at line {}: '{}'", variableName, i + 1, trimmedLine);
                        return i + 1;
                    }
                }
            }
            return -1;
        } catch (Exception e) {
            log.error("Error finding variable declaration in file", e);
            return -1;
        }
    }

    /**
     * 从行中提取变量名
     */
    private String extractVariableNameFromLine(String line) {
        try {
            String trimmedLine = line.trim();
            log.info("Extracting variable name from line: '{}'", trimmedLine);
            
            // 匹配变量声明模式：int varName = value 或 int varName, varName2, varName3 = value
            if (trimmedLine.matches("^int\\s+\\w+.*")) {
                String[] parts = trimmedLine.substring(4).trim().split("\\s*[=,;]\\s*");
                String varName = parts[0].trim();
                log.info("Found variable declaration: '{}'", varName);
                return varName;
            }
            
            // 匹配复合赋值操作符模式：varName *= expression 或 varName += expression
            if (trimmedLine.matches("^\\w+\\s*[+=*/-].*")) {
                String varName = trimmedLine.split("\\s*[+=*/-]")[0].trim();
                log.info("Found variable usage (compound assignment): '{}'", varName);
                return varName;
            }
            
            // 匹配简单赋值模式：varName = expression
            if (trimmedLine.matches("^\\w+\\s*=.*")) {
                String varName = trimmedLine.split("\\s*=")[0].trim();
                log.info("Found variable usage (simple assignment): '{}'", varName);
                return varName;
            }
            
            // 匹配System.out.println(varName)模式
            if (trimmedLine.matches("^System\\.out\\.println\\(\\w+\\)\\s*;.*")) {
                String varName = trimmedLine.replaceAll("^System\\.out\\.println\\((\\w+)\\).*", "$1");
                log.info("Found variable usage (System.out.println): '{}'", varName);
                return varName;
            }
            
            // 匹配if语句中的变量使用：if (...) varName += ...; else varName -= ...
            if (trimmedLine.matches("^if\\s*\\(.*\\)\\s*\\w+\\s*[+=].*")) {
                // 提取if语句中的变量名 - 更精确的匹配
                // 匹配模式：if (条件) 变量名 += 表达式; else 变量名 -= 表达式;
                String pattern = "^if\\s*\\([^)]*\\)\\s*(\\w+)\\s*[+=].*";
                java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
                java.util.regex.Matcher m = p.matcher(trimmedLine);
                if (m.find()) {
                    String varName = m.group(1);
                    log.info("Found variable usage (in if statement): '{}'", varName);
                    return varName;
                }
            }
            
            // 匹配其他变量使用模式：varName(expression) 或 varName.method()
            if (trimmedLine.matches("^\\w+\\s*[.(].*")) {
                String varName = trimmedLine.split("\\s*[.(]")[0].trim();
                log.info("Found variable usage (method call): '{}'", varName);
                return varName;
            }
            
            log.warn("Could not extract variable name from line: '{}'", trimmedLine);
            return null;
        } catch (Exception e) {
            log.error("Error extracting variable name from line: {}", line, e);
            return null;
        }
    }

    /**
     * 验证指定变量是否存在于文件中
     */
    private boolean verifyVariableExists(String filePath, String variableName) {
        try {
            String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath)));
            CompilationUnit cu = javaParser.parse(content).getResult().orElse(null);

            if (cu == null) {
                log.error("Failed to parse file for variable verification: {}", filePath);
                return false;
            }

            // 查找变量声明
            List<VariableDeclarator> variables = cu.findAll(VariableDeclarator.class);
            boolean foundDeclaration = variables.stream()
                    .anyMatch(v -> v.getNameAsString().equals(variableName));

            if (foundDeclaration) {
                log.debug("Variable '{}' found as declaration in file: {}", variableName, filePath);
                return true;
            }

            // 查找变量使用
            List<NameExpr> nameExprs = cu.findAll(NameExpr.class);
            boolean foundUsage = nameExprs.stream()
                    .anyMatch(n -> n.getNameAsString().equals(variableName));

            if (foundUsage) {
                log.debug("Variable '{}' found as usage in file: {}", variableName, filePath);
                return true;
            }

            log.warn("Variable '{}' not found in file: {}", variableName, filePath);
            return false;

        } catch (Exception e) {
            log.error("Error verifying variable '{}' in file: {}", variableName, filePath, e);
            return false;
        }
    }

    /**
     * 在指定文件的指定行号查找变量
     * 专门用于变量重命名蜕变关系测试
     */
    private String findVariableAtSameLine(String filePath, int lineNumber) {
        try {
            log.info("=== Finding variable at same line ===");
            log.info("File: {}, Target line: {}", filePath, lineNumber);

            String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath)));
            CompilationUnit cu = javaParser.parse(content).getResult().orElse(null);

            if (cu == null) {
                log.error("Failed to parse file: {}", filePath);
                return null;
            }

            // 查找所有变量声明和使用
            List<VariableDeclarator> variables = cu.findAll(VariableDeclarator.class);
            List<NameExpr> nameExprs = cu.findAll(NameExpr.class);

            // 首先查找变量声明
            for (VariableDeclarator var : variables) {
                if (var.getBegin().isPresent() && var.getBegin().get().line == lineNumber) {
                    String varName = var.getNameAsString();
                    log.info("Found variable declaration at line {}: {}", lineNumber, varName);
                    return varName;
                }
            }

            // 如果没有找到声明，查找变量使用
            log.info("Searching for variable usage at line {}", lineNumber);
            log.info("Total NameExpr found: {}", nameExprs.size());

            for (NameExpr nameExpr : nameExprs) {
                if (nameExpr.getBegin().isPresent()) {
                    int exprLine = nameExpr.getBegin().get().line;
                    String varName = nameExpr.getNameAsString();

                    log.debug("Checking NameExpr '{}' at line {}", varName, exprLine);

                    if (exprLine == lineNumber) {
                        // 验证这是一个已声明的变量
                        boolean isDeclaredVariable = variables.stream()
                                .anyMatch(v -> v.getNameAsString().equals(varName));

                        log.info("Found NameExpr '{}' at target line {}, isDeclaredVariable: {}",
                                varName, lineNumber, isDeclaredVariable);

                        if (isDeclaredVariable) {
                            log.info("Found variable usage at line {}: {}", lineNumber, varName);
                            return varName;
                        }
                    }
                }
            }

            // 如果还是没找到，尝试更宽泛的搜索：查找该行的所有变量引用
            log.info("Trying broader search for line {}", lineNumber);
            String lineContent = getLineContent(content, lineNumber);
            log.info("Line {} content: '{}'", lineNumber, lineContent);

            // 从该行内容中提取可能的变量名
            for (VariableDeclarator var : variables) {
                String varName = var.getNameAsString();
                if (lineContent.contains(varName)) {
                    log.info("Found variable '{}' in line content at line {}", varName, lineNumber);
                    return varName;
                }
            }

            log.warn("No variable found at line {} in file: {}", lineNumber, filePath);
            return null;

        } catch (Exception e) {
            log.error("Error finding variable at line {} in file: {}", lineNumber, filePath, e);
            return null;
        }
    }

    /**
     * 只生成JSmith mutated文件，不生成renamed文件
     */
    private List<String> generateJSmithMutatedFilesOnly(int numFiles) {
        List<String> mutatedFiles = new ArrayList<>();

        try {
            // 清理目录
            javaCodeGenerator.cleanupDirectory("mutated");
            javaCodeGenerator.cleanupDirectory("deadcode");

            for (int i = 0; i < numFiles; i++) {
                try {
                    log.info("Generating JSmith mutated file {} of {}", i + 1, numFiles);

                    // 使用JSmith生成随机Java类
                    long seed = System.currentTimeMillis() + i * 1000;
                    String originalContent = javaCodeGenerator.generateRandomJavaClass();

                    // 保存mutated文件
                    String mutatedFileName = String.format("JSmith_mutated_%d.java", i);
                    String mutatedFilePath = java.nio.file.Paths.get("mutated", mutatedFileName).toString();

                    java.nio.file.Files.write(java.nio.file.Paths.get(mutatedFilePath),
                        originalContent.getBytes(StandardCharsets.UTF_8));

                    mutatedFiles.add(mutatedFilePath);
                    log.info("Generated JSmith mutated file: {}", mutatedFilePath);

                } catch (Exception e) {
                    log.error("Error generating JSmith mutated file {}: {}", i, e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            log.error("Error in generateJSmithMutatedFilesOnly: {}", e.getMessage(), e);
        }

        return mutatedFiles;
    }

    /**
     * 为JSmith文件添加死代码
     */
    private String addDeadCodeToJSmithFile(String originalContent, String selectedVariable) {
        try {
            log.info("Adding dead code to JSmith file with selected variable: {}", selectedVariable);

            // 生成死代码语句
            List<String> deadCodeStatements = generateDeadCodeStatementsForJSmith(selectedVariable);

            if (deadCodeStatements.isEmpty()) {
                log.warn("No dead code statements generated for JSmith file");
                return originalContent;
            }

            // 使用字符串操作精确插入死代码，避免JavaParser重组代码结构
            String[] lines = originalContent.split("\n");
            StringBuilder result = new StringBuilder();

            // 找到变量声明的行号
            int variableDeclarationLine = -1;
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.contains(selectedVariable) &&
                    (line.contains("boolean ") || line.contains("long ") || line.contains("int ")) &&
                    line.contains("=") && line.endsWith(";")) {
                    variableDeclarationLine = i;
                    log.info("Found variable '{}' declaration at line {}: {}", selectedVariable, i + 1, line);
                    break;
                }
            }

            boolean deadCodeInserted = false;

            for (int i = 0; i < lines.length; i++) {
                result.append(lines[i]).append("\n");

                // 在变量声明后的第一个语句后插入死代码
                if (!deadCodeInserted && variableDeclarationLine != -1 && i > variableDeclarationLine) {
                    String currentLine = lines[i].trim();
                    // 找到变量声明后的第一个完整语句
                    if (currentLine.endsWith(";") && !currentLine.startsWith("//") && !currentLine.isEmpty()) {
                        // 插入死代码
                        for (String deadCode : deadCodeStatements) {
                            // 添加适当的缩进
                            String[] deadCodeLines = deadCode.split("\n");
                            for (String deadCodeLine : deadCodeLines) {
                                result.append("        ").append(deadCodeLine).append("\n");
                            }
                        }
                        deadCodeInserted = true;
                        log.info("Inserted dead code after line {}: {}", i + 1, currentLine);
                    }
                }
            }

            if (!deadCodeInserted) {
                log.warn("Could not find suitable location to insert dead code for variable: {}", selectedVariable);
                return originalContent;
            }

            return result.toString();

        } catch (Exception e) {
            log.error("Error adding dead code to JSmith file", e);
            return originalContent;
        }
    }



    /**
     * 为JSmith文件生成死代码语句
     */
    private List<String> generateDeadCodeStatementsForJSmith(String selectedVariable) {
        List<String> statements = new ArrayList<>();
        Random random = new Random();

        // 固定生成一个3行的死代码块
        String deadCode = String.format("if (false) {\n    %s = %s + %d;\n}",
            selectedVariable, selectedVariable, random.nextInt(100) + 1);
        statements.add(deadCode);

        return statements;
    }



    /**
     * 判断是否为无用代码行
     */
    private boolean isDeadCodeLine(String line) {
        // 去除前导和尾随空格
        String trimmedLine = line.trim();

        // 无用代码的特征 - 覆盖所有生成的无用代码类型
        boolean isDeadCode =
            trimmedLine.startsWith("if (false)") ||
            trimmedLine.startsWith("for (int i = 0; i < 0;") ||
            trimmedLine.matches("^int (unusedVar|temp)\\d+.*") ||
            trimmedLine.matches("^int x = \\d+;") ||
            trimmedLine.equals("for (int i = 0; i < 0; i++) { }") ||
            trimmedLine.matches("^int temp\\d+ = \\d+;") ||
            trimmedLine.matches("^int unusedVar\\d+ = \\d+;") ||
            trimmedLine.matches("^if \\(false\\) \\{ int x = \\d+; \\}") ||
            // 简化的JSmith死代码模式（固定3行if (false)格式）
            trimmedLine.equals("if (false) {") ||                // if死代码开始
            trimmedLine.matches(".*= .* \\+ \\d+;");             // 变量加法操作（死代码内容）

        log.debug("Checking line: '{}' (trimmed: '{}') - isDeadCode: {}", line, trimmedLine, isDeadCode);
        return isDeadCode;
    }

    /**
     * 获取指定行的内容
     */
    private String getLineContent(String content, int lineNumber) {
        try {
            String[] lines = content.split("\n");
            if (lineNumber > 0 && lineNumber <= lines.length) {
                return lines[lineNumber - 1].trim();
            }
            return "";
        } catch (Exception e) {
            log.warn("Error getting line content for line {}: {}", lineNumber, e.getMessage());
            return "";
        }
    }

    /**
     * 清理之前的切片文件
     */
    private void cleanupSliceFiles() {
        try {
            Path sliceDir = Paths.get("slice");
            if (Files.exists(sliceDir)) {
                log.info("Cleaning up slice directory: {}", sliceDir.toAbsolutePath());

                // 删除slice目录下的所有.java文件
                Files.walk(sliceDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            log.debug("Deleted slice file: {}", path.getFileName());
                        } catch (IOException e) {
                            log.warn("Failed to delete slice file {}: {}", path.getFileName(), e.getMessage());
                        }
                    });

                log.info("Slice directory cleanup completed");
            } else {
                log.info("Slice directory does not exist, no cleanup needed");
            }
        } catch (IOException e) {
            log.error("Error during slice directory cleanup: {}", e.getMessage());
        }
    }
}