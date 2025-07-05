package com.example.controller;

import com.example.generator.JavaCodeGenerator;
import com.example.generator.JavaCodeGenerator.VariableInfo;
import com.example.slicer.SliceExecutor;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.io.IOException;
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
            log.info("Starting metamorphic test with {} mutations", numMutations);

            // 生成变异文件（使用自定义生成器生成随机代码）
            List<String> mutatedFiles = javaCodeGenerator.generateMutatedFiles("", numMutations);
            log.info("Generated {} mutated files", mutatedFiles.size());

            // 对每个变异文件进行切片
            for (String file : mutatedFiles) {
                log.info("Processing file: {}", file);
                Map<String, Object> testResult = new HashMap<>();
                testResult.put("originalFile", file);

                try {
                    // 获取对应的重命名文件
                    String renamedFile = file.replace("mutated", "renamed").replace("_mutated_", "_renamed_");
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
                    String renamedVarName = findRenamedVariableName(file, originalVarName);

                    if (renamedVarName == null) {
                        throw new RuntimeException("Could not find renamed variable for: " + originalVarName);
                    }

                    log.info("Original variable: {} -> Renamed variable: {} at line {}",
                            originalVarName, renamedVarName, originalVariableInfo.getLineNumber());

                    // 对变异文件执行切片
                    log.info("Executing slice for mutated file: {}", file);
                    String mutatedSliceContent = sliceExecutor.executeSlice(file);
                    log.info("Mutated slice content: {}", mutatedSliceContent);
                    testResult.put("mutatedSliceContent", mutatedSliceContent);

                    // 对重命名文件执行切片（使用对应的重命名变量，但行号与原文件相同）
                    log.info("Executing slice for renamed file: {} with variable: {} at line {}",
                            renamedFile, renamedVarName, originalVariableInfo.getLineNumber());
                    String renamedSliceContent = sliceExecutor.executeSliceWithVariable(renamedFile, renamedVarName, originalVariableInfo.getLineNumber());
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

            // 生成添加无用代码的变异文件
            List<String> mutatedFiles = javaCodeGenerator.generateDeadCodeFiles("", numMutations);
            log.info("Generated {} dead code files", mutatedFiles.size());

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
                    String originalContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(file)));
                    testResult.put("originalFileContent", originalContent);

                    // 读取无用代码文件内容用于显示
                    String deadCodeContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(deadCodeFile)));
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

            // 生成控制流等价变换的变异文件
            List<String> originalFiles = javaCodeGenerator.generateControlFlowFiles("", numMutations);
            log.info("Generated {} control flow files", originalFiles.size());

            // 对每个原始文件进行切片
            for (String originalFile : originalFiles) {
                log.info("Processing file: {}", originalFile);
                Map<String, Object> testResult = new HashMap<>();
                testResult.put("originalFile", originalFile);

                try {
                    // 获取对应的控制流变换文件
                    String controlFlowFile = originalFile.replace("mutated", "controlflow").replace("_original_", "_controlflow_");
                    testResult.put("controlFlowFile", controlFlowFile);

                    // 读取原始文件内容用于显示
                    String originalContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(originalFile)));
                    testResult.put("originalFileContent", originalContent);

                    // 读取控制流变换文件内容用于显示
                    String controlFlowContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(controlFlowFile)));
                    testResult.put("controlFlowFileContent", controlFlowContent);

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

                    // 对控制流变换文件执行切片（使用相同的变量名和行号）
                    log.info("Executing slice for control flow file: {} with variable: {} at line {}",
                            controlFlowFile, originalVariableInfo.getVariableName(), originalVariableInfo.getLineNumber());
                    String controlFlowSliceContent = sliceExecutor.executeSliceWithVariable(controlFlowFile, originalVariableInfo.getVariableName(), originalVariableInfo.getLineNumber());
                    log.info("Control flow slice content: {}", controlFlowSliceContent);
                    testResult.put("controlFlowSliceContent", controlFlowSliceContent);

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

            // 移除空白字符后比较
            normalizedSlice1 = normalizedSlice1.replaceAll("\\s+", "").trim();
            normalizedSlice2 = normalizedSlice2.replaceAll("\\s+", "").trim();

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
     * 计算无用代码文件中的行号偏移
     */
    private int calculateLineOffset(String originalContent, String deadCodeContent, int targetLine) {
        try {
            String[] originalLines = originalContent.split("\n");
            String[] deadCodeLines = deadCodeContent.split("\n");

            log.info("=== Line offset calculation start ===");
            log.info("Original target line: {}", targetLine);
            log.info("Original content has {} lines", originalLines.length);
            log.info("Dead code content has {} lines", deadCodeLines.length);

            // 找到目标变量在原始文件中的行号
            String targetVariableName = extractVariableNameFromLine(originalLines[targetLine - 1]);
            log.info("Target variable name: {}", targetVariableName);

            if (targetVariableName == null) {
                log.error("Could not extract variable name from line {}", targetLine);
                // 如果无法提取变量名，尝试通过行号差异来计算偏移
                log.info("Falling back to line count difference method");
                return calculateOffsetByLineCount(originalLines, deadCodeLines, targetLine);
            }

            // 找到变量在原始文件中的声明行号
            int originalDeclarationLine = findVariableDeclarationLine(originalLines, targetVariableName);
            log.info("Found variable '{}' declaration at line {} in original file", targetVariableName, originalDeclarationLine);

            if (originalDeclarationLine == -1) {
                log.error("Could not find variable '{}' declaration in original file", targetVariableName);
                // 如果找不到声明行，尝试通过行号差异来计算偏移
                log.info("Falling back to line count difference method");
                return calculateOffsetByLineCount(originalLines, deadCodeLines, targetLine);
            }

            // 在无用代码文件中找到相同变量的声明行号
            int deadCodeDeclarationLine = findVariableDeclarationLine(deadCodeLines, targetVariableName);
            log.info("Found variable '{}' declaration at line {} in dead code file", targetVariableName, deadCodeDeclarationLine);

            if (deadCodeDeclarationLine == -1) {
                log.error("Could not find variable '{}' declaration in dead code file", targetVariableName);
                // 如果找不到声明行，尝试通过行号差异来计算偏移
                log.info("Falling back to line count difference method");
                return calculateOffsetByLineCount(originalLines, deadCodeLines, targetLine);
            }

            // 计算声明行的偏移
            int declarationOffset = deadCodeDeclarationLine - originalDeclarationLine;
            log.info("Declaration line offset: {} - {} = {}", deadCodeDeclarationLine, originalDeclarationLine, declarationOffset);

            // 计算目标行的偏移（基于声明行偏移）
            int targetOffset = declarationOffset;
            log.info("=== Line offset calculation result ===");
            log.info("Original target line: {}", targetLine);
            log.info("Original declaration line: {}", originalDeclarationLine);
            log.info("Dead code declaration line: {}", deadCodeDeclarationLine);
            log.info("Calculated target offset: {}", targetOffset);

            return targetOffset;

        } catch (Exception e) {
            log.error("Error calculating line offset", e);
            return 0;
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
            trimmedLine.matches("^if \\(false\\) \\{ int x = \\d+; \\}");
        
        log.info("Checking line: '{}' (trimmed: '{}') - isDeadCode: {}", line, trimmedLine, isDeadCode);
        return isDeadCode;
    }
} 