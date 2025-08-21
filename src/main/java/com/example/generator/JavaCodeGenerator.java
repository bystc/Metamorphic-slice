package com.example.generator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.Node;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Component
public class JavaCodeGenerator {
    private static final String MUTATED_DIR = "mutated";
    private static final String RENAMED_DIR = "renamed";
    private static final String CONTROLFLOW_DIR = "controlflow";
    private static final String DATAFLOW_DIR = "dataflow";
    private static final String DEADCODE_DIR = "deadcode";
    private static final String REORDERED_DIR = "reordered";
    private static final Random random = new Random();
    private final JavaParser javaParser;
    
    @Autowired
    private JSmithCodeGenerator jsmithCodeGenerator;

    // 保存变量映射关系：文件名 -> 变量映射
    private final Map<String, Map<String, String>> variableMappings = new HashMap<>();

    // 预定义的变量名池
    private static final String[] VARIABLE_NAMES = {
            "sum", "prod", "count", "total", "result", "value", "temp", "index", "size", "length",
            "max", "min", "avg", "diff", "ratio", "factor", "base", "offset", "limit", "threshold",
            "score", "weight", "price", "amount", "quantity", "rate", "percent", "ratio", "scale", "level"
    };

    // 预定义的操作符
    private static final String[] OPERATORS = {"+", "-", "*", "/", "%", "&", "|", "^", "<<", ">>"};

    public JavaCodeGenerator() {
        createDirectories();
        ParserConfiguration configuration = new ParserConfiguration();
        this.javaParser = new JavaParser(configuration);
        this.jsmithCodeGenerator = new JSmithCodeGenerator();
    }

    private void createDirectories() {
        try {
            Files.createDirectories(Paths.get(MUTATED_DIR));
            Files.createDirectories(Paths.get(RENAMED_DIR));
            log.info("Created directories: {}, {}", MUTATED_DIR, RENAMED_DIR);
        } catch (IOException e) {
            log.error("Failed to create directories", e);
        }
    }

    public List<String> generateMutatedFiles(String sourceFile, int numMutations) {
        List<String> mutatedFiles = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numMutations; i++) {
            try {
                log.info("Generating Java class {} at {}", i, new Date());

                // 1. Generate a random Java class
                String originalContent = generateRandomJavaClass();

                // 2. Save the original file
                String mutatedFileName = String.format("Example_mutated_%d.java", i);
                String mutatedFilePath = Paths.get(MUTATED_DIR, mutatedFileName).toString();
                try (FileWriter writer = new FileWriter(mutatedFilePath)) {
                    writer.write(originalContent);
                }
                mutatedFiles.add(mutatedFilePath);
                log.info("Generated original file: {}", mutatedFilePath);

                // 3. Create renamed version for metamorphic testing
                CompilationUnit cu = javaParser.parse(originalContent).getResult().orElseThrow(() ->
                        new RuntimeException("Failed to parse generated file: " + mutatedFilePath));

                Map<String, String> variableMap = new HashMap<>();
                cu.findAll(VariableDeclarator.class).forEach(vd -> {
                    String oldName = vd.getNameAsString();
                    if (!variableMap.containsKey(oldName)) {
                        String newName = generateNewVariableName(oldName);
                        variableMap.put(oldName, newName);
                    }
                });

                if (variableMap.isEmpty()) {
                    log.warn("No variables found in generated file: {}, skipping rename.", mutatedFilePath);
                    continue;
                }

                // 保存变量映射关系
                variableMappings.put(mutatedFilePath, new HashMap<>(variableMap));
                log.info("Saved variable mapping for {}: {}", mutatedFilePath, variableMap);

                ModifierVisitor<Void> visitor = new ModifierVisitor<Void>() {
                    @Override
                    public Visitable visit(VariableDeclarator vd, Void arg) {
                        String oldName = vd.getNameAsString();
                        if (variableMap.containsKey(oldName)) {
                            vd.setName(variableMap.get(oldName));
                        }
                        return super.visit(vd, arg);
                    }

                    @Override
                    public Visitable visit(NameExpr nameExpr, Void arg) {
                        String oldName = nameExpr.getNameAsString();
                        if (variableMap.containsKey(oldName)) {
                            nameExpr.setName(variableMap.get(oldName));
                        }
                        return super.visit(nameExpr, arg);
                    }
                };
                cu.accept(visitor, null);

                // 保持原始格式，只替换变量名
                String renamedContent = originalContent;
                for (Map.Entry<String, String> entry : variableMap.entrySet()) {
                    String oldName = entry.getKey();
                    String newName = entry.getValue();
                    // 使用正则表达式确保只替换完整的变量名，避免部分匹配
                    renamedContent = renamedContent.replaceAll("\\b" + oldName + "\\b", newName);
                }

                String renamedFileName = String.format("Example_renamed_%d.java", i);
                String renamedFilePath = Paths.get(RENAMED_DIR, renamedFileName).toString();

                try (FileWriter writer = new FileWriter(renamedFilePath)) {
                    writer.write(renamedContent);
                }
                log.info("Generated renamed file: {}", renamedFilePath);

            } catch (Exception e) {
                log.error("Error during code generation or renaming: {}", e.getMessage(), e);
            }
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        log.info("Total time for generating {} file pairs: {} ms", mutatedFiles.size(), totalTime);

        log.info("Generated {} mutated files:", mutatedFiles.size());
        for (String file : mutatedFiles) {
            log.info("- {}", file);
        }

        return mutatedFiles;
    }

    /**
     * 生成结构和内容都不同的Java类
     */
    public String generateRandomJavaClass() {
        // 使用JSmith生成器替代原有的模板方法
        try {
            log.info("Using JSmith generator to create random Java class");
            return jsmithCodeGenerator.generateSliceableJavaClass();
        } catch (Exception e) {
            log.warn("JSmith generation failed, falling back to original method: {}", e.getMessage());
            // 如果JSmith生成失败，回退到原有的方法
            return generateClassWithSwitchStatements();
        }
    }
    
    /**
     * 批量生成复杂的随机Java文件（整合BatchGenerator功能）
     * @param outputDir 输出目录
     * @param count 生成数量
     * @return 生成的文件路径列表
     */
    public List<String> generateComplexJavaFiles(String outputDir, int count) {
        try {
            long baseSeed = System.currentTimeMillis();
            log.info("Generating {} complex Java files using integrated BatchGenerator approach", count);
            return jsmithCodeGenerator.generateComplexJavaFiles(count, outputDir, baseSeed);
        } catch (Exception e) {
            log.error("Failed to generate complex Java files: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 使用JSmith生成器生成用于变量重命名蜕变关系测试的文件对
     * @param numPairs 生成的文件对数量
     * @return 生成的文件路径列表（包含原始文件和重命名文件）
     */
    public List<String> generateJSmithVariableRenameTestFiles(int numPairs) {
        List<String> generatedFiles = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // 确保目录存在
            Files.createDirectories(Paths.get(MUTATED_DIR));
            Files.createDirectories(Paths.get(RENAMED_DIR));
            log.info("Created directories for JSmith variable rename test: {}, {}", MUTATED_DIR, RENAMED_DIR);
            
            for (int i = 0; i < numPairs; i++) {
                try {
                    log.info("Generating JSmith file pair {} at {}", i + 1, new Date());
                    
                    // 1. 使用JSmith生成复杂的Java类
                    // 使用更加随机的种子生成策略
                    long seed = generateHighEntropyRandomSeed(startTime, i);
                    String originalContent = jsmithCodeGenerator.generateRandomJavaClassWithEnhancedRandomness(seed);
                    
                    // 2. 保存原始文件到mutated目录
                    String mutatedFileName = String.format("JSmith_mutated_%d.java", i);
                    String mutatedFilePath = Paths.get(MUTATED_DIR, mutatedFileName).toString();

                    // 移除package声明并标准化原始文件格式
                    String cleanedContent = removePackageDeclaration(originalContent);
                    String standardizedContent = standardizeJavaFormat(cleanedContent);

                    try (FileWriter writer = new FileWriter(mutatedFilePath)) {
                        writer.write(standardizedContent);
                    }
                    generatedFiles.add(mutatedFilePath);
                    log.info("Generated JSmith original file: {}", mutatedFilePath);
                    
                    // 3. 创建变量重命名版本（使用标准化的内容）
                    String renamedFilePath = createJSmithRenamedVersion(standardizedContent, i);
                    if (renamedFilePath != null) {
                        generatedFiles.add(renamedFilePath);
                        log.info("Generated JSmith renamed file: {}", renamedFilePath);
                        
                        // 4. 验证重命名是否成功
                        if (validateRenamedFile(mutatedFilePath, renamedFilePath)) {
                            log.info("Successfully validated JSmith file pair: {} <-> {}", mutatedFilePath, renamedFilePath);
                        } else {
                            log.warn("Validation failed for JSmith file pair: {} <-> {}", mutatedFilePath, renamedFilePath);
                        }
                    } else {
                        log.warn("Failed to create renamed version for JSmith file: {}", mutatedFilePath);
                    }
                    
                } catch (Exception e) {
                    log.error("Error generating JSmith file pair {}: {}", i + 1, e.getMessage(), e);
                }
            }
            
            long endTime = System.currentTimeMillis();
            log.info("JSmith variable rename test file generation completed. Generated {} files in {} ms", 
                    generatedFiles.size(), endTime - startTime);
            
        } catch (IOException e) {
            log.error("Failed to create directories for JSmith test files", e);
        }
        
        return generatedFiles;
    }
    
    /**
     * 为JSmith生成的代码创建变量重命名版本
     * @param originalContent 原始代码内容
     * @param index 文件索引
     * @return 重命名文件的路径，如果失败返回null
     */
    private String createJSmithRenamedVersion(String originalContent, int index) {
        try {
            String renamedFileName = String.format("JSmith_renamed_%d.java", index);
            String renamedFilePath = Paths.get(RENAMED_DIR, renamedFileName).toString();
            
            // 解析原始代码
            CompilationUnit cu = javaParser.parse(originalContent).getResult().orElseThrow(() ->
                    new RuntimeException("Failed to parse JSmith generated code"));
            
            // 创建变量名映射（专门处理JSmith生成的复杂变量名）
            Map<String, String> variableMap = createJSmithVariableMapping(cu);
            
            if (variableMap.isEmpty()) {
                log.warn("No variables found in JSmith generated code, copying file as is");
                try (FileWriter writer = new FileWriter(renamedFilePath)) {
                    writer.write(originalContent);
                }
                return renamedFilePath;
            }
            
            // 保存变量映射关系
            String baseFileName = "JSmith_mutated_" + index + ".java";
            variableMappings.put(baseFileName, variableMap);
            log.info("Saved JSmith variable mapping for {}: {}", baseFileName, variableMap);
            
            // 应用变量重命名
            String renamedContent = applyJSmithVariableRenaming(cu, variableMap);
            
            // 验证重命名后的代码
            if (validateJSmithRenamedCode(renamedContent, variableMap)) {
                try (FileWriter writer = new FileWriter(renamedFilePath)) {
                    writer.write(renamedContent);
                }
                return renamedFilePath;
            } else {
                log.error("JSmith renamed code validation failed, copying original");
                try (FileWriter writer = new FileWriter(renamedFilePath)) {
                    writer.write(originalContent);
                }
                return renamedFilePath;
            }
            
        } catch (Exception e) {
            log.error("Error creating JSmith renamed version: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 为JSmith生成的代码创建变量映射（处理复杂的变量名）
     * @param cu 编译单元
     * @return 变量映射
     */
    private Map<String, String> createJSmithVariableMapping(CompilationUnit cu) {
        Map<String, String> variableMap = new HashMap<>();
        
        // 收集所有变量声明
        cu.findAll(VariableDeclarator.class).forEach(vd -> {
            String oldName = vd.getNameAsString();
            if (!variableMap.containsKey(oldName)) {
                // 为JSmith生成的复杂变量名创建更合适的新名称
                String newName = generateJSmithVariableName(oldName);
                variableMap.put(oldName, newName);
                log.debug("JSmith variable mapping: {} -> {}", oldName, newName);
            }
        });
        
        return variableMap;
    }
    
    /**
     * 为JSmith变量生成新的变量名
     * 使用长度一致的重命名策略
     * @param oldName 原变量名
     * @return 新变量名
     */
    private String generateJSmithVariableName(String oldName) {
        // 使用新的长度一致的变量名生成策略
        return generateNewVariableName(oldName);
    }
    
    /**
     * 检查变量名是否已被使用
     * @param name 变量名
     * @return 是否已被使用
     */
    private boolean isVariableNameUsed(String name) {
        // 简单的检查，可以根据需要扩展
        return name.equals("args") || name.equals("main") || name.equals("String") || name.equals("System");
    }
    
    /**
     * 应用JSmith变量重命名
     * @param cu 编译单元
     * @param variableMap 变量映射
     * @return 重命名后的代码
     */
    private String applyJSmithVariableRenaming(CompilationUnit cu, Map<String, String> variableMap) {
        // 创建访问者来重命名变量
        ModifierVisitor<Void> visitor = new ModifierVisitor<Void>() {
            @Override
            public Visitable visit(VariableDeclarator vd, Void arg) {
                String oldName = vd.getNameAsString();
                if (variableMap.containsKey(oldName)) {
                    String newName = variableMap.get(oldName);
                    vd.setName(newName);
                    log.debug("Renamed JSmith variable declaration: {} -> {}", oldName, newName);
                }
                return super.visit(vd, arg);
            }
            
            @Override
            public Visitable visit(NameExpr nameExpr, Void arg) {
                String oldName = nameExpr.getNameAsString();
                if (variableMap.containsKey(oldName)) {
                    String newName = variableMap.get(oldName);
                    nameExpr.setName(newName);
                    log.debug("Renamed JSmith variable usage: {} -> {}", oldName, newName);
                }
                return super.visit(nameExpr, arg);
            }
        };
        
        // 应用访问者
        cu.accept(visitor, null);
        
        return cu.toString();
    }
    
    /**
     * 验证JSmith重命名后的代码
     * @param renamedContent 重命名后的代码
     * @param variableMap 变量映射
     * @return 是否验证通过
     */
    private boolean validateJSmithRenamedCode(String renamedContent, Map<String, String> variableMap) {
        try {
            // 尝试解析重命名后的代码
            CompilationUnit parsedCu = javaParser.parse(renamedContent).getResult().orElseThrow(() ->
                    new RuntimeException("Failed to parse JSmith renamed code"));
            
            // 验证基本结构
            if (parsedCu.getTypes().isEmpty()) {
                log.error("JSmith renamed code is missing class declarations");
                return false;
            }
            
            // 验证变量重命名
            List<VariableDeclarator> variables = parsedCu.findAll(VariableDeclarator.class);
            if (variables.isEmpty()) {
                log.warn("JSmith renamed code has no variable declarations");
                return true; // 没有变量也是有效的
            }
            
            // 检查是否有变量被正确重命名
            boolean hasRenamedVariables = false;
            for (VariableDeclarator vd : variables) {
                String name = vd.getNameAsString();
                if (variableMap.containsValue(name)) {
                    hasRenamedVariables = true;
                    break;
                }
            }
            
            if (!hasRenamedVariables) {
                log.warn("No variables appear to have been renamed in JSmith code");
            }
            
            return true;
            
        } catch (Exception e) {
            log.error("JSmith renamed code validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 验证重命名文件对
     * @param originalFile 原始文件路径
     * @param renamedFile 重命名文件路径
     * @return 是否验证通过
     */
    private boolean validateRenamedFile(String originalFile, String renamedFile) {
        try {
            String originalContent = Files.readString(Paths.get(originalFile), StandardCharsets.UTF_8);
            String renamedContent = Files.readString(Paths.get(renamedFile), StandardCharsets.UTF_8);
            
            // 基本检查：两个文件都应该能被解析
            CompilationUnit originalCu = javaParser.parse(originalContent).getResult().orElse(null);
            CompilationUnit renamedCu = javaParser.parse(renamedContent).getResult().orElse(null);
            
            if (originalCu == null || renamedCu == null) {
                log.error("Failed to parse one or both files: {} / {}", originalFile, renamedFile);
                return false;
            }
            
            // 检查类的数量是否相同
            if (originalCu.getTypes().size() != renamedCu.getTypes().size()) {
                log.error("Different number of types in original vs renamed file");
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            log.error("Error validating renamed file pair: {}", e.getMessage());
            return false;
        }
    }

    // 模板1：复杂的main方法，包含多个变量、条件语句和循环
    private String generateComplexMainClass() {
        StringBuilder code = new StringBuilder();
        String className = "Example" + random.nextInt(1000);
        code.append("public class ").append(className).append(" {\n");
        code.append("    public static void main(String[] args) {\n");

        // 生成多个不同类型的变量声明
        int numVars = 6 + random.nextInt(6); // 6-11个变量
        List<String> vars = new ArrayList<>();

        // 添加一些临时变量和计算变量
        for (int i = 0; i < numVars - 2; i++) {
            String var = VARIABLE_NAMES[random.nextInt(VARIABLE_NAMES.length)] + i;
            vars.add(var);
            code.append("        int ").append(var).append(" = ").append(random.nextInt(100)).append(";\n");
        }

        // 添加一些条件判断和计算
        code.append("        int temp = 0;\n");
        code.append("        for (int i = 0; i < 5; i++) {\n");
        code.append("            if (i % 2 == 0) {\n");
        code.append("                temp += i * 2;\n");
        code.append("            } else {\n");
        code.append("                temp -= i;\n");
        code.append("            }\n");
        code.append("        }\n");

        // 添加更多变量声明
        for (int i = 0; i < 3; i++) {
            String var = "calc" + i;
            vars.add(var);
            code.append("        int ").append(var).append(" = temp + ").append(random.nextInt(50)).append(";\n");
        }

        // 添加嵌套循环
        code.append("        for (int i = 0; i < 10; i++) {\n");
        code.append("            for (int j = 0; j < 3; j++) {\n");
        for (String var : vars) {
            code.append("                ").append(var).append(" += i * j;\n");
        }
        code.append("            }\n");
        code.append("        }\n");

        // 添加条件语句
        code.append("        if (temp > 10) {\n");
        for (String var : vars) {
            code.append("            ").append(var).append(" *= 2;\n");
        }
        code.append("        } else {\n");
        for (String var : vars) {
            code.append("            ").append(var).append(" /= 2;\n");
        }
        code.append("        }\n");

        // 最终输出
        for (String var : vars) {
            code.append("        System.out.println(").append(var).append(");\n");
        }
        code.append("    }\n}");
        return code.toString();
    }

    // 模板2：有多个方法的复杂类
    private String generateClassWithMultipleMethods() {
        StringBuilder code = new StringBuilder();
        String className = "Example" + random.nextInt(1000);
        code.append("public class ").append(className).append(" {\n");

        // 静态方法
        code.append("    public static int add(int a, int b) { return a + b; }\n");
        code.append("    public static int mul(int a, int b) { return a * b; }\n");
        code.append("    public static int sub(int a, int b) { return a - b; }\n");
        code.append("    public static int div(int a, int b) { return b != 0 ? a / b : 0; }\n");

        // main方法
        code.append("    public static void main(String[] args) {\n");
        code.append("        int x = 1, y = 2, z = 3;\n");
        code.append("        int temp1 = add(x, y);\n");
        code.append("        int temp2 = mul(temp1, z);\n");
        code.append("        int temp3 = sub(temp2, x);\n");
        code.append("        int temp4 = div(temp3, y);\n");
        code.append("        int temp5 = add(temp4, z);\n");
        code.append("        int temp6 = mul(temp5, temp1);\n");
        code.append("        System.out.println(temp1);\n");
        code.append("        System.out.println(temp2);\n");
        code.append("        System.out.println(temp3);\n");
        code.append("        System.out.println(temp4);\n");
        code.append("        System.out.println(temp5);\n");
        code.append("        System.out.println(temp6);\n");
        code.append("    }\n}");
        return code.toString();
    }

    // 模板3：有数组和复杂逻辑的类
    private String generateClassWithArraysAndComplexLogic() {
        StringBuilder code = new StringBuilder();
        String className = "Example" + random.nextInt(1000);
        code.append("public class ").append(className).append(" {\n");
        code.append("    public static void main(String[] args) {\n");
        code.append("        int[] arr1 = new int[10];\n");
        code.append("        int[] arr2 = new int[8];\n");
        code.append("        int[] arr3 = new int[12];\n");

        // 初始化数组
        code.append("        for (int i = 0; i < arr1.length; i++) { arr1[i] = i * 2; }\n");
        code.append("        for (int i = 0; i < arr2.length; i++) { arr2[i] = i * 3; }\n");
        code.append("        for (int i = 0; i < arr3.length; i++) { arr3[i] = i * 4; }\n");

        // 复杂计算
        code.append("        int sum1 = 0, sum2 = 0, sum3 = 0;\n");
        code.append("        for (int v : arr1) {\n");
        code.append("            if (v % 3 == 0) sum1 += v; else sum1 -= v;\n");
        code.append("        }\n");
        code.append("        for (int v : arr2) {\n");
        code.append("            if (v % 2 == 0) sum2 += v * 2; else sum2 += v;\n");
        code.append("        }\n");
        code.append("        for (int v : arr3) {\n");
        code.append("            if (v % 5 == 0) sum3 += v / 2; else sum3 += v;\n");
        code.append("        }\n");

        // 最终计算
        code.append("        int result1 = sum1 + sum2;\n");
        code.append("        int result2 = sum2 + sum3;\n");
        code.append("        int result3 = sum1 + sum3;\n");
        code.append("        int finalResult = result1 + result2 + result3;\n");

        code.append("        System.out.println(sum1);\n");
        code.append("        System.out.println(sum2);\n");
        code.append("        System.out.println(sum3);\n");
        code.append("        System.out.println(result1);\n");
        code.append("        System.out.println(result2);\n");
        code.append("        System.out.println(result3);\n");
        code.append("        System.out.println(finalResult);\n");
        code.append("    }\n}");
        return code.toString();
    }

    // 模板4：有静态成员和嵌套循环的复杂类
    private String generateClassWithStaticMembersAndNestedLoops() {
        StringBuilder code = new StringBuilder();
        String className = "Example" + random.nextInt(1000);
        code.append("public class ").append(className).append(" {\n");
        code.append("    static int factor = ").append(2 + random.nextInt(8)).append(";\n");
        code.append("    static int multiplier = ").append(3 + random.nextInt(5)).append(";\n");
        code.append("    static int divisor = ").append(2 + random.nextInt(3)).append(";\n");

        code.append("    public static void main(String[] args) {\n");
        code.append("        int res1 = 1, res2 = 1, res3 = 1;\n");
        code.append("        int temp1 = 0, temp2 = 0, temp3 = 0;\n");

        // 复杂的嵌套循环
        code.append("        for (int i = 1; i <= 5; i++) {\n");
        code.append("            for (int j = 1; j <= 3; j++) {\n");
        code.append("                for (int k = 1; k <= 2; k++) {\n");
        code.append("                    res1 *= factor * i * j * k;\n");
        code.append("                    res2 += multiplier * i + j - k;\n");
        code.append("                    res3 = res3 / divisor + i * j * k;\n");
        code.append("                    temp1 += i * j;\n");
        code.append("                    temp2 += j * k;\n");
        code.append("                    temp3 += i * k;\n");
        code.append("                }\n");
        code.append("            }\n");
        code.append("        }\n");

        // 条件判断
        code.append("        if (res1 > 1000) {\n");
        code.append("            res1 = res1 / 10;\n");
        code.append("            res2 = res2 * 2;\n");
        code.append("        }\n");
        code.append("        if (res2 > 500) {\n");
        code.append("            res2 = res2 / 5;\n");
        code.append("            res3 = res3 * 3;\n");
        code.append("        }\n");

        code.append("        System.out.println(res1);\n");
        code.append("        System.out.println(res2);\n");
        code.append("        System.out.println(res3);\n");
        code.append("        System.out.println(temp1);\n");
        code.append("        System.out.println(temp2);\n");
        code.append("        System.out.println(temp3);\n");
        code.append("    }\n}");
        return code.toString();
    }

    // 模板5：有内部类和方法的复杂类
    private String generateClassWithInnerClassAndMethods() {
        StringBuilder code = new StringBuilder();
        String className = "Example" + random.nextInt(1000);
        code.append("public class ").append(className).append(" {\n");
        code.append("    static class Helper {\n");
        code.append("        int square(int x) { return x * x; }\n");
        code.append("        int cube(int x) { return x * x * x; }\n");
        code.append("        int factorial(int x) { return x <= 1 ? 1 : x * factorial(x - 1); }\n");
        code.append("    }\n");

        code.append("    public static void main(String[] args) {\n");
        code.append("        Helper h = new Helper();\n");
        code.append("        int val1 = 0, val2 = 0, val3 = 0;\n");
        code.append("        int temp1 = 0, temp2 = 0, temp3 = 0;\n");

        code.append("        for (int i = 0; i < 5; i++) { \n");
        code.append("            val1 += h.square(i); \n");
        code.append("            val2 += h.cube(i); \n");
        code.append("            val3 += h.factorial(i); \n");
        code.append("            temp1 += i * 2; \n");
        code.append("            temp2 += i * 3; \n");
        code.append("            temp3 += i * 4; \n");
        code.append("        }\n");

        code.append("        int result1 = val1 + temp1;\n");
        code.append("        int result2 = val2 + temp2;\n");
        code.append("        int result3 = val3 + temp3;\n");

        code.append("        System.out.println(val1);\n");
        code.append("        System.out.println(val2);\n");
        code.append("        System.out.println(val3);\n");
        code.append("        System.out.println(result1);\n");
        code.append("        System.out.println(result2);\n");
        code.append("        System.out.println(result3);\n");
        code.append("    }\n}");
        return code.toString();
    }

    // 模板6：有异常处理的复杂类
    private String generateClassWithExceptionHandling() {
        StringBuilder code = new StringBuilder();
        String className = "Example" + random.nextInt(1000);
        code.append("public class ").append(className).append(" {\n");
        code.append("    public static void main(String[] args) {\n");
        code.append("        int[] arr = new int[10];\n");
        code.append("        int sum = 0, count = 0, avg = 0;\n");
        code.append("        int temp1 = 0, temp2 = 0, temp3 = 0;\n");

        // 初始化数组
        code.append("        for (int i = 0; i < arr.length; i++) {\n");
        code.append("            arr[i] = i * 2 + 1;\n");
        code.append("        }\n");

        // 异常处理
        code.append("        try {\n");
        code.append("            for (int i = 0; i < arr.length; i++) {\n");
        code.append("                sum += arr[i];\n");
        code.append("                count++;\n");
        code.append("                temp1 += i * 2;\n");
        code.append("                temp2 += i * 3;\n");
        code.append("                temp3 += i * 4;\n");
        code.append("            }\n");
        code.append("            avg = sum / count;\n");
        code.append("        } catch (Exception e) {\n");
        code.append("            avg = 0;\n");
        code.append("            temp1 = 0;\n");
        code.append("            temp2 = 0;\n");
        code.append("            temp3 = 0;\n");
        code.append("        }\n");

        code.append("        int result1 = sum + temp1;\n");
        code.append("        int result2 = avg + temp2;\n");
        code.append("        int result3 = count + temp3;\n");

        code.append("        System.out.println(sum);\n");
        code.append("        System.out.println(avg);\n");
        code.append("        System.out.println(count);\n");
        code.append("        System.out.println(result1);\n");
        code.append("        System.out.println(result2);\n");
        code.append("        System.out.println(result3);\n");
        code.append("    }\n}");
        return code.toString();
    }

    // 模板7：有switch语句的复杂类
    private String generateClassWithSwitchStatements() {
        StringBuilder code = new StringBuilder();
        String className = "Example" + random.nextInt(1000);
        code.append("public class ").append(className).append(" {\n");
        code.append("    public static void main(String[] args) {\n");
        code.append("        int choice = ").append(random.nextInt(4)).append(";\n");

        // 插入3-5条互不依赖的无关变量声明
        int unrelatedCount = 3 + random.nextInt(3); // 3-5条
        for (int i = 0; i < unrelatedCount; i++) {
            int type = random.nextInt(4);
            switch (type) {
                case 0:
                    code.append("        int unrelatedInt").append(i).append(" = ").append(100 + random.nextInt(100)).append(";\n");
                    break;
                case 1:
                    code.append("        double unrelatedDouble").append(i).append(" = ").append(String.format("%.2f", random.nextDouble() * 100)).append(";\n");
                    break;
                case 2:
                    code.append("        String unrelatedStr").append(i).append(" = \"str").append(random.nextInt(100)).append("\";\n");
                    break;
                case 3:
                    code.append("        int[] unrelatedArr").append(i).append(" = { ").append(random.nextInt(10)).append(", ").append(random.nextInt(10)).append(", ").append(random.nextInt(10)).append(" };\n");
                    break;
            }
        }

        // 添加与切片变量无关的循环结构（用于控制流变换）
        code.append("        int loopCounter = 0;\n");
        code.append("        for (int i = 0; i < 3; i++) {\n");
        code.append("            loopCounter += i * 2;\n");
        code.append("            if (i % 2 == 0) {\n");
        code.append("                loopCounter -= 1;\n");
        code.append("            }\n");
        code.append("        }\n");
        
        // 添加另一个循环结构
        code.append("        int whileCounter = 0;\n");
        code.append("        int j = 0;\n");
        code.append("        while (j < 2) {\n");
        code.append("            whileCounter += j * 3;\n");
        code.append("            j++;\n");
        code.append("        }\n");

        code.append("        int val1 = 0, val2 = 0, val3 = 0;\n");
        code.append("        int temp1 = 10, temp2 = 20, temp3 = 30;\n");

        code.append("        switch (choice) {\n");
        code.append("            case 0:\n");
        code.append("                val1 = temp1 * 2;\n");
        code.append("                val2 = temp2 + 5;\n");
        code.append("                val3 = temp3 - 3;\n");
        code.append("                break;\n");
        code.append("            case 1:\n");
        code.append("                val1 = temp1 + temp2;\n");
        code.append("                val2 = temp2 * temp3;\n");
        code.append("                val3 = temp3 / temp1;\n");
        code.append("                break;\n");
        code.append("            case 2:\n");
        code.append("                val1 = temp1 - temp2;\n");
        code.append("                val2 = temp2 / temp3;\n");
        code.append("                val3 = temp3 * temp1;\n");
        code.append("                break;\n");
        code.append("            default:\n");
        code.append("                val1 = temp1;\n");
        code.append("                val2 = temp2;\n");
        code.append("                val3 = temp3;\n");
        code.append("                break;\n");
        code.append("        }\n");

        code.append("        int result1 = val1 + val2;\n");
        code.append("        int result2 = val2 + val3;\n");
        code.append("        int result3 = val1 + val3;\n");

        code.append("        System.out.println(val1);\n");
        code.append("        System.out.println(val2);\n");
        code.append("        System.out.println(val3);\n");
        code.append("        System.out.println(result1);\n");
        code.append("        System.out.println(result2);\n");
        code.append("        System.out.println(result3);\n");
        code.append("    }\n}");
        return code.toString();
    }

    // 模板8：有复杂计算的类
    private String generateClassWithComplexCalculations() {
        StringBuilder code = new StringBuilder();
        String className = "Example" + random.nextInt(1000);
        code.append("public class ").append(className).append(" {\n");
        code.append("    public static void main(String[] args) {\n");
        code.append("        int a = ").append(random.nextInt(20) + 1).append(";\n");
        code.append("        int b = ").append(random.nextInt(20) + 1).append(";\n");
        code.append("        int c = ").append(random.nextInt(20) + 1).append(";\n");
        code.append("        int d = ").append(random.nextInt(20) + 1).append(";\n");
        code.append("        int e = ").append(random.nextInt(20) + 1).append(";\n");
        code.append("        int f = ").append(random.nextInt(20) + 1).append(";\n");

        // 复杂计算
        code.append("        int temp1 = a * b + c;\n");
        code.append("        int temp2 = d * e - f;\n");
        code.append("        int temp3 = (a + b) * (c + d);\n");
        code.append("        int temp4 = (e + f) / (a + 1);\n");
        code.append("        int temp5 = a * b * c / d;\n");
        code.append("        int temp6 = e * f + a * b;\n");

        // 更多计算
        code.append("        int result1 = temp1 + temp2;\n");
        code.append("        int result2 = temp3 - temp4;\n");
        code.append("        int result3 = temp5 * temp6;\n");
        code.append("        int result4 = result1 + result2;\n");
        code.append("        int result5 = result2 + result3;\n");
        code.append("        int result6 = result1 + result3;\n");

        code.append("        System.out.println(temp1);\n");
        code.append("        System.out.println(temp2);\n");
        code.append("        System.out.println(temp3);\n");
        code.append("        System.out.println(result1);\n");
        code.append("        System.out.println(result2);\n");
        code.append("        System.out.println(result3);\n");
        code.append("        System.out.println(result4);\n");
        code.append("        System.out.println(result5);\n");
        code.append("        System.out.println(result6);\n");
        code.append("    }\n}");
        return code.toString();
    }

    /**
     * 获取随机数据类型
     */
    private String getRandomType() {
        String[] types = {"int", "long", "double", "float"};
        return types[random.nextInt(types.length)];
    }

    /**
     * 根据类型获取随机初始值
     */
    private String getRandomInitialValue(String type) {
        switch (type) {
            case "int":
                return String.valueOf(random.nextInt(100));
            case "long":
                return String.valueOf(random.nextLong() % 1000);
            case "double":
                return String.valueOf(random.nextDouble() * 100);
            case "float":
                return String.valueOf(random.nextFloat() * 100) + "f";
            default:
                return "0";
        }
    }

    public String renameVariables(String sourceFile) {
        try {
            String content = Files.readString(Paths.get(sourceFile), StandardCharsets.UTF_8);
            String baseName = getBaseName(sourceFile);
            String renamedFileName = baseName + "_renamed.java";
            String renamedFilePath = Paths.get(RENAMED_DIR, renamedFileName).toString();

            // 首先验证源文件是否可以被解析
            CompilationUnit cu = javaParser.parse(content).getResult().orElseThrow(() ->
                    new RuntimeException("Failed to parse source file: " + sourceFile));

            // 创建变量名映射
            Map<String, String> variableMap = new HashMap<>();

            // 访问所有变量声明
            cu.findAll(VariableDeclarator.class).forEach(vd -> {
                String oldName = vd.getNameAsString();
                if (!variableMap.containsKey(oldName)) {
                    String newName = generateNewVariableName(oldName);
                    variableMap.put(oldName, newName);
                    log.info("Mapping variable: {} -> {}", oldName, newName);
                }
            });

            // 如果没有找到任何变量，直接复制文件
            if (variableMap.isEmpty()) {
                log.warn("No variables found in file: {}, copying file as is", sourceFile);
                Files.copy(Paths.get(sourceFile), Paths.get(renamedFilePath));
                return renamedFilePath;
            }

            // 创建访问者来重命名变量
            ModifierVisitor<Void> visitor = new ModifierVisitor<Void>() {
                @Override
                public Visitable visit(VariableDeclarator vd, Void arg) {
                    String oldName = vd.getNameAsString();
                    if (variableMap.containsKey(oldName)) {
                        String newName = variableMap.get(oldName);
                        vd.setName(newName);
                        log.info("Renamed variable declaration: {} -> {}", oldName, newName);
                    }
                    return super.visit(vd, arg);
                }

                @Override
                public Visitable visit(NameExpr nameExpr, Void arg) {
                    String oldName = nameExpr.getNameAsString();
                    if (variableMap.containsKey(oldName)) {
                        String newName = variableMap.get(oldName);
                        nameExpr.setName(newName);
                        log.info("Renamed variable usage: {} -> {}", oldName, newName);
                    }
                    return super.visit(nameExpr, arg);
                }
            };

            // 应用访问者
            cu.accept(visitor, null);

            // 使用字符串替换保持原始格式
            String renamedContent = renameVariablesPreservingFormat(content, variableMap);
            try {
                // 尝试解析重命名后的代码
                CompilationUnit parsedCu = javaParser.parse(renamedContent).getResult().orElseThrow(() ->
                        new RuntimeException("Failed to parse renamed code"));

                // 验证重命名后的代码是否包含所有必要的元素
                if (parsedCu.getTypes().isEmpty()) {
                    log.error("Renamed code is missing class declarations");
                    Files.copy(Paths.get(sourceFile), Paths.get(renamedFilePath));
                    return renamedFilePath;
                }

                // 验证重命名后的代码是否包含所有变量
                List<VariableDeclarator> variables = parsedCu.findAll(VariableDeclarator.class);
                if (variables.isEmpty()) {
                    log.error("Renamed code is missing variable declarations");
                    Files.copy(Paths.get(sourceFile), Paths.get(renamedFilePath));
                    return renamedFilePath;
                }

                // 验证变量是否被正确重命名
                boolean allVariablesRenamed = true;
                for (VariableDeclarator vd : variables) {
                    String name = vd.getNameAsString();
                    if (!name.contains("_")) {
                        allVariablesRenamed = false;
                        log.error("Variable not renamed: {}", name);
                        break;
                    }
                }

                if (!allVariablesRenamed) {
                    log.error("Not all variables were renamed");
                    Files.copy(Paths.get(sourceFile), Paths.get(renamedFilePath));
                    return renamedFilePath;
                }

                // 写入重命名后的文件
                try (FileWriter writer = new FileWriter(renamedFilePath)) {
                    writer.write(renamedContent);
                }

                log.info("Generated renamed file: {}", renamedFilePath);
                return renamedFilePath;

            } catch (Exception e) {
                log.error("Renamed code is invalid: {}", e.getMessage());
                Files.copy(Paths.get(sourceFile), Paths.get(renamedFilePath));
                return renamedFilePath;
            }

        } catch (IOException e) {
            log.error("Error renaming variables: {}", e.getMessage());
            throw new RuntimeException("Failed to rename variables: " + e.getMessage(), e);
        }
    }

    private String generateNewVariableName(String oldName) {
        // 生成新的变量名，保持与原变量名完全相同的长度
        int originalLength = oldName.length();

        // 策略：通过字符替换生成相同长度的新变量名
        StringBuilder newName = new StringBuilder();

        for (int i = 0; i < originalLength; i++) {
            char originalChar = oldName.charAt(i);
            char newChar;

            if (i == 0) {
                // 第一个字符必须是字母
                if (Character.isLetter(originalChar)) {
                    // 字母偏移：a->n, b->o, c->p, ..., z->m
                    if (Character.isLowerCase(originalChar)) {
                        newChar = (char) ((originalChar - 'a' + 13) % 26 + 'a');
                    } else {
                        newChar = (char) ((originalChar - 'A' + 13) % 26 + 'A');
                    }
                } else {
                    // 如果原来不是字母，用'v'替换
                    newChar = 'v';
                }
            } else {
                // 其他位置的字符
                if (Character.isLetter(originalChar)) {
                    // 字母偏移
                    if (Character.isLowerCase(originalChar)) {
                        newChar = (char) ((originalChar - 'a' + 13) % 26 + 'a');
                    } else {
                        newChar = (char) ((originalChar - 'A' + 13) % 26 + 'A');
                    }
                } else if (Character.isDigit(originalChar)) {
                    // 数字偏移：0->5, 1->6, ..., 9->4
                    newChar = (char) ((originalChar - '0' + 5) % 10 + '0');
                } else {
                    // 特殊字符保持不变或替换为数字
                    newChar = (char) ('0' + (i % 10));
                }
            }

            newName.append(newChar);
        }

        String result = newName.toString();

        // 验证长度完全一致
        if (result.length() != originalLength) {
            log.error("Length mismatch in variable renaming: {} ({}) -> {} ({})",
                    oldName, originalLength, result, result.length());
            throw new RuntimeException("Variable renaming failed to maintain length consistency");
        }

        log.debug("Generated new variable name: {} -> {} (length: {} -> {})",
                oldName, result, originalLength, result.length());

        return result;
    }

    /**
     * 移除Java代码中的package声明
     * @param content 原始代码内容
     * @return 移除package声明后的代码内容
     */
    private String removePackageDeclaration(String content) {
        try {
            String result = content;

            // 移除package声明行
            result = result.replaceAll("package\\s+[^;]+;\\s*\\n?", "");

            // 移除开头的空行
            result = result.replaceAll("^\\s*\\n+", "");

            log.debug("Removed package declaration from Java code");
            return result;

        } catch (Exception e) {
            log.warn("Failed to remove package declaration: {}", e.getMessage());
            return content;
        }
    }

    /**
     * 标准化Java代码格式
     * @param content 原始代码内容
     * @return 标准化后的代码内容
     */
    private String standardizeJavaFormat(String content) {
        try {
            // 先进行基本的清理，移除可能导致解析问题的内容
            String cleanedContent = cleanJavaCode(content);

            // 使用JavaParser解析并重新格式化代码
            CompilationUnit cu = javaParser.parse(cleanedContent).getResult().orElseThrow(() ->
                    new RuntimeException("Failed to parse Java code for standardization"));

            // 返回标准格式的代码
            String standardized = cu.toString();
            log.debug("Standardized Java code format");
            return standardized;

        } catch (Exception e) {
            log.warn("Failed to standardize Java format, using cleaned content: {}", e.getMessage());
            // 如果标准化失败，至少返回清理后的内容
            return cleanJavaCode(content);
        }
    }

    /**
     * 清理Java代码，移除可能导致解析问题的内容
     * @param content 原始代码内容
     * @return 清理后的代码内容
     */
    private String cleanJavaCode(String content) {
        try {
            String cleaned = content;

            // 完全移除package声明
            cleaned = cleaned.replaceAll("package\\s+[^;]+;\\s*", "");

            // 移除package声明后可能留下的空行
            cleaned = cleaned.replaceAll("^\\s*\\n", "");

            // 移除可能有问题的静态导入
            cleaned = cleaned.replaceAll("import static [^;]+;", "");

            // 移除复杂的导入，只保留基本的java.util导入
            cleaned = cleaned.replaceAll("import java\\.time[^;]*;", "");
            cleaned = cleaned.replaceAll("import java\\.nio[^;]*;", "");

            // 修复八进制数字（如 01 -> 1）
            cleaned = cleaned.replaceAll("\\b0([1-7])\\b", "$1");

            // 修复十六进制数字格式
            cleaned = cleaned.replaceAll("0x([A-Fa-f0-9]+)", "0x$1");

            log.debug("Cleaned Java code to improve compatibility");
            return cleaned;

        } catch (Exception e) {
            log.warn("Failed to clean Java code: {}", e.getMessage());
            return content;
        }
    }

    /**
     * 为切片选择变量的行号
     * 使用随机策略选择能产生丰富切片的变量使用位置
     * @param allLines 变量出现的所有行号
     * @param variableName 变量名
     * @return 选择的行号
     */
    private int selectVariableLineForSlicing(List<Integer> allLines, String variableName) {
        if (allLines.size() <= 1) {
            // 只有声明，选择声明行
            int lineNumber = allLines.get(0);
            log.info("Selected variable: {} at line {} (declaration only)", variableName, lineNumber);
            return lineNumber;
        }

        // 有多次使用，随机选择一个使用位置
        List<Integer> usageLines = allLines.subList(1, allLines.size());
        int randomIndex = random.nextInt(usageLines.size());
        int lineNumber = usageLines.get(randomIndex);

        log.info("Selected variable: {} at randomly chosen line {} (usage #{} out of {} usages)",
                variableName, lineNumber, randomIndex + 1, usageLines.size());
        log.info("All usage lines for {}: {}", variableName, usageLines);

        return lineNumber;
    }

    /**
     * 使用字符串替换重命名变量，并标准化格式
     * @param content 原始文件内容
     * @param variableMap 变量映射表
     * @return 重命名后的内容
     */
    private String renameVariablesPreservingFormat(String content, Map<String, String> variableMap) {
        String result = content;

        // 按变量名长度降序排序，避免短变量名被长变量名的一部分替换
        List<Map.Entry<String, String>> sortedEntries = variableMap.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getKey().length(), e1.getKey().length()))
                .collect(java.util.stream.Collectors.toList());

        for (Map.Entry<String, String> entry : sortedEntries) {
            String oldName = entry.getKey();
            String newName = entry.getValue();

            // 使用正则表达式确保只替换完整的变量名，不替换变量名的一部分
            // \\b 表示单词边界，确保只匹配完整的标识符
            String regex = "\\b" + java.util.regex.Pattern.quote(oldName) + "\\b";
            result = result.replaceAll(regex, newName);

            log.debug("String replacement: {} -> {} (preserving format)", oldName, newName);
        }

        // 对重命名后的代码进行标准化格式处理
        return standardizeJavaFormat(result);
    }

    /**
     * 生成高熵随机种子
     * 结合多种随机源以增加种子的随机性
     * @param baseTime 基础时间戳
     * @param index 文件索引
     * @return 高熵随机种子
     */
    private long generateHighEntropyRandomSeed(long baseTime, int index) {
        // 结合多种随机源
        long nanoTime = System.nanoTime();
        long hashCode = System.identityHashCode(new Object());
        long memoryHash = Runtime.getRuntime().freeMemory();

        // 使用质数和位运算增加随机性
        long seed = baseTime * 31L +
                   nanoTime * 37L +
                   hashCode * 41L +
                   memoryHash * 43L +
                   index * 47L;

        // 进一步混合位
        seed ^= (seed >>> 32);
        seed ^= (seed << 13);
        seed ^= (seed >>> 17);
        seed ^= (seed << 5);

        log.debug("Generated high entropy seed for index {}: {}", index, seed);
        return seed;
    }

    private String getBaseName(String filePath) {
        return Paths.get(filePath).getFileName().toString().replace(".java", "");
    }

    public VariableInfo findVariableForSlicing(String sourceFile) {
        try {
            String content = Files.readString(Paths.get(sourceFile), StandardCharsets.UTF_8);
            CompilationUnit cu = javaParser.parse(content).getResult().orElseThrow(() ->
                    new RuntimeException("Failed to parse Java file"));

            // 获取所有变量声明
            List<VariableDeclarator> variables = cu.findAll(VariableDeclarator.class);

            // 统计每个变量出现的次数
            Map<String, Integer> variableCounts = new HashMap<>();
            Map<String, Integer> variableFirstLine = new HashMap<>();
            Map<String, List<Integer>> variableLines = new HashMap<>();

            log.info("=== Variable Analysis for file: {} ===", sourceFile);

            // 首先统计变量声明
            for (VariableDeclarator vd : variables) {
                String name = vd.getNameAsString();
                int line = vd.getBegin().get().line;

                variableCounts.merge(name, 1, Integer::sum);
                variableFirstLine.putIfAbsent(name, line);
                variableLines.computeIfAbsent(name, k -> new ArrayList<>()).add(line);

                log.info("Variable declaration: {} at line {}", name, line);
            }

            // 然后查找所有变量使用
            cu.findAll(NameExpr.class).forEach(nameExpr -> {
                String name = nameExpr.getNameAsString();
                if (variableCounts.containsKey(name)) {
                    int line = nameExpr.getBegin().get().line;
                    
                    // 检查这一行是否为死代码
                    String lineContent = getLineContent(sourceFile, line);
                    boolean isDeadCodeLine = isDeadCodeLine(lineContent.trim());
                    
                    // 只统计非死代码中的变量使用
                    if (!isDeadCodeLine) {
                    variableCounts.merge(name, 1, Integer::sum);
                    variableLines.computeIfAbsent(name, k -> new ArrayList<>()).add(line);
                        log.info("Variable usage (non-dead code): {} at line {}", name, line);
                    } else {
                        log.info("Variable usage (dead code): {} at line {} - SKIPPED", name, line);
                    }
                }
            });

            // 打印所有变量的行号信息
            log.info("=== Variable Usage Analysis ===");
            log.info("Found {} variables in total", variableLines.size());
            for (Map.Entry<String, List<Integer>> entry : variableLines.entrySet()) {
                String varName = entry.getKey();
                List<Integer> lines = entry.getValue();
                log.info("Variable '{}': lines = {} (count: {})", varName, lines, lines.size());
                if (lines.size() >= 2) {
                    log.info("  -> '{}' is a CANDIDATE for slicing (multiple usage)", varName);
                } else {
                    log.info("  -> '{}' is NOT suitable (single usage only)", varName);
                }
            }

            // 查找合适的变量（优先选择被多次使用且位置靠后的变量，适合前向切片）
            log.info("=== Filtering variables for multiple usage ===");
            List<Map.Entry<String, List<Integer>>> suitableVariables = variableLines.entrySet().stream()
                    .filter(e -> {
                        boolean hasMultipleUsage = e.getValue().size() >= 2;
                        log.info("Variable '{}': usage count = {}, hasMultipleUsage = {}",
                                e.getKey(), e.getValue().size(), hasMultipleUsage);
                        return hasMultipleUsage;
                    })
                    .filter(e -> {
                        List<Integer> lines = e.getValue();
                        int firstLine = lines.get(0);
                        int lastLine = lines.get(lines.size() - 1);
                        boolean valid = lastLine > firstLine; // 确保有实际的使用，不只是声明
                        log.info("Variable '{}': first line = {}, last line = {}, usage count = {}, valid = {}",
                                e.getKey(), firstLine, lastLine, lines.size(), valid);
                        return valid;
                    })
                    .sorted((e1, e2) -> {
                        // 按照以下优先级排序：
                        // 1. 使用次数更多的变量
                        // 2. 最后使用位置更靠后的变量（适合前向切片）
                        List<Integer> lines1 = e1.getValue();
                        List<Integer> lines2 = e2.getValue();

                        int usageCount1 = lines1.size();
                        int usageCount2 = lines2.size();

                        if (usageCount1 != usageCount2) {
                            return Integer.compare(usageCount2, usageCount1); // 使用次数多的优先
                        }

                        int lastLine1 = lines1.get(lines1.size() - 1);
                        int lastLine2 = lines2.get(lines2.size() - 1);

                        return Integer.compare(lastLine2, lastLine1); // 最后使用位置靠后的优先
                    })
                    .collect(Collectors.toList());
            
            // 如果没有找到至少出现两次的变量，尝试使用只出现一次的变量（JSmith代码的特殊处理）
            if (suitableVariables.isEmpty()) {
                log.info("No variables with multiple usages found, trying single-usage variables for JSmith code");
                suitableVariables = variableLines.entrySet().stream()
                        .filter(e -> e.getValue().size() >= 1)
                        .filter(e -> {
                            String varName = e.getKey();
                            // 排除一些不适合切片的变量名
                            boolean suitable = !varName.equals("args") && 
                                             !varName.equals("main") && 
                                             !varName.matches(".*temp.*") &&
                                             !varName.matches(".*unused.*");
                            log.info("Single-usage variable '{}': suitable = {}", varName, suitable);
                            return suitable;
                        })
                        .collect(Collectors.toList());
            }

            log.info("Found {} suitable variables", suitableVariables.size());

            if (!suitableVariables.isEmpty()) {
                // 由于已经按优先级排序，直接选择第一个变量
                Map.Entry<String, List<Integer>> selectedVariable = suitableVariables.get(0);
                String variableName = selectedVariable.getKey();
                List<Integer> allLines = selectedVariable.getValue();

                // 为蜕变测试选择确定性的位置，为其他测试选择随机位置
                int lineNumber = selectVariableLineForSlicing(allLines, variableName);

                log.info("Variable '{}' usage pattern: {}", variableName, allLines);
                log.info("This variable appears {} times, making it suitable for forward slicing", allLines.size());

                return new VariableInfo(variableName, lineNumber);
            }

            // 如果没有找到多次使用的变量，选择单次使用但位置靠后的变量
            if (!suitableVariables.isEmpty()) {
                Map.Entry<String, List<Integer>> bestVariable = suitableVariables.stream()
                        .filter(e -> {
                            // 检查变量的使用是否在无用代码中
                            List<Integer> lines = e.getValue();
                            int checkLine = lines.size() > 1 ? lines.get(lines.size() - 1) : lines.get(0); // 优先检查最后一次使用，否则检查声明
                            String lineContent = getLineContent(sourceFile, checkLine);
                            boolean isInDeadCode = isDeadCodeLine(lineContent.trim());
                            log.info("Variable '{}' at line {}: '{}', isDeadCode: {}",
                                    e.getKey(), checkLine, lineContent.trim(), isInDeadCode);
                            return !isInDeadCode;
                        })
                        .max(Comparator.comparingInt(e -> {
                            List<Integer> lines = e.getValue();
                            return lines.size() > 1 ? lines.get(lines.size() - 1) : lines.get(0); // 按最后一次使用或声明行排序
                        }))
                        .orElse(suitableVariables.stream()
                                .max(Comparator.comparingInt(e -> {
                                    List<Integer> lines = e.getValue();
                                    return lines.size() > 1 ? lines.get(lines.size() - 1) : lines.get(0);
                                }))
                                .orElse(suitableVariables.get(0)));

                String variableName = bestVariable.getKey();
                List<Integer> allLines = bestVariable.getValue();

                // 为蜕变测试选择确定性的位置，为其他测试选择随机位置
                int lineNumber = selectVariableLineForSlicing(allLines, variableName);

                log.info("All lines for {}: {}", variableName, allLines);
                return new VariableInfo(variableName, lineNumber);
            }

            // 如果没有找到合适的变量，尝试使用第一个变量
            if (!variables.isEmpty()) {
                VariableDeclarator firstVar = variables.get(0);
                String variableName = firstVar.getNameAsString();
                int lineNumber = firstVar.getBegin().get().line;
                log.info("Using first variable: {} at line {}", variableName, lineNumber);
                return new VariableInfo(variableName, lineNumber);
            }

            log.warn("No variables found in file: {}", sourceFile);
            return null;

        } catch (IOException e) {
            log.error("Error finding variable for slicing", e);
            return null;
        }
    }

    /**
     * 获取变量映射关系
     */
    public Map<String, String> getVariableMapping(String originalFile) {
        return variableMappings.getOrDefault(originalFile, new HashMap<>());
    }

    public static class VariableInfo {
        private final String variableName;
        private final int lineNumber;

        public VariableInfo(String variableName, int lineNumber) {
            this.variableName = variableName;
            this.lineNumber = lineNumber;
        }

        public String getVariableName() {
            return variableName;
        }

        public int getLineNumber() {
            return lineNumber;
        }
    }

    private void cleanupDirectory(String directory) {
        try {
            Path dirPath = Paths.get(directory);
            if (Files.exists(dirPath)) {
                log.info("Cleaning up directory: {}", directory);
                // 使用同步块保护清理操作
                synchronized (this) {
                    Files.walk(dirPath)
                            .filter(Files::isRegularFile)
                            .forEach(path -> {
                                try {
                                    Files.delete(path);
                                    log.info("Deleted file: {}", path);
                                } catch (IOException e) {
                                    log.error("Error deleting file: {}", path, e);
                                }
                            });
                }
            }
        } catch (IOException e) {
            log.error("Error cleaning up directory: {}", directory, e);
        }
    }

    /**
     * 生成添加无用代码的变异文件
     * 在原始代码中添加不影响程序逻辑的无用代码
     */
    public List<String> generateDeadCodeFiles(String sourceFile, int numMutations) {
        List<String> generatedFiles = new ArrayList<>();

        try {
            // 确保目录存在
            Files.createDirectories(Paths.get(MUTATED_DIR));
            Files.createDirectories(Paths.get("deadcode"));

            for (int i = 0; i < numMutations; i++) {
                // 读取现有的mutated文件
                String mutatedFileName = String.format("Example_mutated_%d.java", i);
                String mutatedFilePath = Paths.get(MUTATED_DIR, mutatedFileName).toString();
                
                if (!Files.exists(Paths.get(mutatedFilePath))) {
                    // 如果mutated文件不存在，先生成原始代码
                    String originalContent = generateRandomJavaClass();
                    Files.write(Paths.get(mutatedFilePath), originalContent.getBytes(StandardCharsets.UTF_8));
                }
                
                // 读取mutated文件内容
                String originalContent = Files.readString(Paths.get(mutatedFilePath), StandardCharsets.UTF_8);

                // 先选择切片变量
                VariableInfo variableInfo = findVariableForSlicing(mutatedFilePath);
                if (variableInfo == null) {
                    log.warn("No suitable variable found for slicing in file: {}", mutatedFilePath);
                    continue;
                }

                String selectedVariable = variableInfo.getVariableName();
                log.info("Selected variable for slicing: {} in file: {}", selectedVariable, mutatedFilePath);

                // 添加无用代码，使用选定的切片变量
                String deadCodeContent = addDeadCodeWithSelectedVariable(originalContent, selectedVariable);

                // 保存添加无用代码的文件
                String deadCodeFileName = String.format("Example_deadcode_%d.java", i);
                String deadCodeFilePath = Paths.get("deadcode", deadCodeFileName).toString();
                Files.write(Paths.get(deadCodeFilePath), deadCodeContent.getBytes(StandardCharsets.UTF_8));

                generatedFiles.add(mutatedFilePath);
                log.info("Generated dead code file: {} with selected variable: {}", deadCodeFilePath, selectedVariable);
            }

        } catch (IOException e) {
            log.error("Error generating dead code files", e);
        }

        return generatedFiles;
    }

    /**
     * 添加无用代码到原始内容中，使用选定的切片变量
     */
    private String addDeadCodeWithSelectedVariable(String originalContent, String selectedVariable) {
        try {
            log.info("Adding dead code with selected variable: {}", selectedVariable);
            
            // 生成无用代码语句列表，专门使用选定的变量
            List<String> deadCodeStatements = generateDeadCodeStatementsWithSelectedVariable(selectedVariable);
            
            if (deadCodeStatements.isEmpty()) {
                log.warn("No dead code statements generated");
                return originalContent;
            }

            // 解析原始代码
            CompilationUnit cu = javaParser.parse(originalContent).getResult().orElseThrow(() ->
                    new RuntimeException("Failed to parse original content"));

            // 找到main方法
            Optional<MethodDeclaration> mainMethod = cu.findFirst(MethodDeclaration.class, md ->
                    md.getNameAsString().equals("main"));

            if (mainMethod.isPresent()) {
                MethodDeclaration method = mainMethod.get();
                BlockStmt body = method.getBody().orElse(new BlockStmt());

                // 在方法开始处添加无用代码
                                for (String deadCode : deadCodeStatements) {
                    try {
                        Statement deadCodeStmt = javaParser.parseStatement(deadCode).getResult().orElse(null);
                        if (deadCodeStmt != null) {
                            body.addStatement(0, deadCodeStmt);
                            log.info("Added dead code with selected variable: {}", deadCode);
                                        }
                    } catch (Exception e) {
                        log.error("Failed to parse dead code statement: {}", deadCode, e);
                            }
                        }

                return cu.toString();
                    }

            return originalContent;

        } catch (Exception e) {
            log.error("Error adding dead code with selected variable", e);
            return originalContent;
        }
    }

    /**
     * 生成无用代码语句列表，使用选定的切片变量
     */
    private List<String> generateDeadCodeStatementsWithSelectedVariable(String selectedVariable) {
        List<String> statements = new ArrayList<>();
        Random random = new Random();
        int deadCodeCount = random.nextInt(3) + 1; // 添加1-3个无用代码

        for (int i = 0; i < deadCodeCount; i++) {
            String deadCode = generateDeadCodeStatementWithSelectedVariable(selectedVariable);
            if (deadCode != null) {
                statements.add(deadCode);
            }
        }

        return statements;
    }

    /**
     * 生成无用代码语句字符串，使用选定的切片变量
     */
    private String generateDeadCodeStatementWithSelectedVariable(String selectedVariable) {
        Random random = new Random();
        
        // 优先生成包含切片变量的死代码（类型4和5）
        int type = random.nextInt(4) + 4; // 只选择类型4和5，确保使用切片变量

        switch (type) {
            case 4:
                // 不可达循环，但包含选定的切片变量（永远不会执行）
                return generateUnreachableLoopWithSelectedVariable(selectedVariable);

            case 5:
                // 不可达条件语句，但包含选定的切片变量（永远不会执行）
                return generateUnreachableConditionWithSelectedVariable(selectedVariable);

            default:
                // 如果随机数超出范围，默认使用不可达循环
                return generateUnreachableLoopWithSelectedVariable(selectedVariable);
                                }
    }

    /**
     * 生成包含选定切片变量的不可达循环
     */
    private String generateUnreachableLoopWithSelectedVariable(String selectedVariable) {
        Random random = new Random();
        StringBuilder loop = new StringBuilder();
        
        // 生成永远不会执行的循环条件
        String condition = "i < 0"; // 永远不会为真
        
        // 生成循环体，包含选定的切片变量
        String loopBody = generateSliceVariableOperationsWithSelectedVariable(selectedVariable);
        
        loop.append("for (int i = 0; ").append(condition).append("; i++) {");
        loop.append("\n    ").append(loopBody);
        loop.append("\n}");
        
        log.info("Generated unreachable loop with selected variable {}: {}", selectedVariable, loop.toString());
        return loop.toString();
                        }

    /**
     * 生成包含选定切片变量的不可达条件语句
     */
    private String generateUnreachableConditionWithSelectedVariable(String selectedVariable) {
        Random random = new Random();
        StringBuilder condition = new StringBuilder();
        
        // 生成永远不会为真的条件
        String falseCondition = "false";
        
        // 生成条件体，包含选定的切片变量
        String conditionBody = generateSliceVariableOperationsWithSelectedVariable(selectedVariable);
        
        condition.append("if (").append(falseCondition).append(") {");
        condition.append("\n    ").append(conditionBody);
        condition.append("\n}");
        
        log.info("Generated unreachable condition with selected variable {}: {}", selectedVariable, condition.toString());
        return condition.toString();
    }

    /**
     * 生成选定切片变量的操作语句
     */
    private String generateSliceVariableOperationsWithSelectedVariable(String selectedVariable) {
        Random random = new Random();
        List<String> operations = new ArrayList<>();
        
        // 生成1-3个切片变量操作
        int operationCount = random.nextInt(3) + 1;

        for (int i = 0; i < operationCount; i++) {
            String operation = generateSingleSliceVariableOperationWithSelectedVariable(selectedVariable);
            if (operation != null) {
                operations.add(operation);
            }
        }

        return String.join("\n    ", operations);
    }

    /**
     * 生成单个选定切片变量操作
     */
    private String generateSingleSliceVariableOperationWithSelectedVariable(String selectedVariable) {
        Random random = new Random();
        int type = random.nextInt(4);

        switch (type) {
            case 0:
                // 切片变量赋值操作
                return selectedVariable + " = " + selectedVariable + " + " + random.nextInt(100) + ";";

            case 1:
                // 切片变量计算操作
                return selectedVariable + " = " + selectedVariable + " * " + random.nextInt(10) + " + " + random.nextInt(50) + ";";

            case 2:
                // 切片变量条件操作
                return "if (" + selectedVariable + " > 0) { " + selectedVariable + " = " + selectedVariable + " - " + random.nextInt(20) + "; }";

            case 3:
                // 切片变量循环操作
                return "for (int j = 0; j < 5; j++) { " + selectedVariable + " = " + selectedVariable + " + j; }";

            default:
                return selectedVariable + " = " + selectedVariable + " + 1;";
        }
    }

    /**
     * 重排序语句，调整不影响切片变量的语句顺序
     */
    private String reorderStatements(String originalContent) {
        try {
            // 使用AST解析来重排序，更准确地识别语句
            return reorderStatementsWithAST(originalContent);
        } catch (Exception e) {
            log.error("Error reordering statements", e);
            return originalContent;
        }
    }

    /**
     * 使用AST解析重排序main方法内的语句（分组shuffle：连续可重排序语句分组，组内shuffle，其他保持原位）
     */
    private String reorderStatementsWithAST(String originalContent) {
        try {
            CompilationUnit cu = javaParser.parse(originalContent).getResult().orElseThrow(() ->
                    new RuntimeException("Failed to parse content for reordering"));

            // 找到main方法
            Optional<MethodDeclaration> mainMethod = cu.findFirst(MethodDeclaration.class, md ->
                    md.getNameAsString().equals("main"));

            if (mainMethod.isPresent()) {
                MethodDeclaration method = mainMethod.get();
                BlockStmt body = method.getBody().orElse(null);
                if (body != null) {
                    List<Statement> statements = new ArrayList<>(body.getStatements());
                    List<List<Statement>> groups = new ArrayList<>();
                    List<Statement> currentGroup = new ArrayList<>();
                    boolean lastReorderable = false;
                    for (Statement stmt : statements) {
                        boolean reorderable = isReorderableStatement(stmt);
                        if (reorderable) {
                            currentGroup.add(stmt);
                            lastReorderable = true;
                        } else {
                            if (!currentGroup.isEmpty()) {
                                groups.add(new ArrayList<>(currentGroup));
                                currentGroup.clear();
                            }
                            // 非可重排序语句单独成组
                            List<Statement> single = new ArrayList<>();
                            single.add(stmt);
                            groups.add(single);
                            lastReorderable = false;
                        }
                    }
                    if (!currentGroup.isEmpty()) {
                        groups.add(new ArrayList<>(currentGroup));
                    }
                    // shuffle每个可重排序组
                    List<Statement> newStatements = new ArrayList<>();
                    for (List<Statement> group : groups) {
                        if (group.size() > 1 && isReorderableStatement(group.get(0))) {
                            Collections.shuffle(group);
                        }
                        newStatements.addAll(group);
                    }
                    // 重建方法体
                    body.getStatements().clear();
                    for (Statement stmt : newStatements) {
                        body.addStatement(stmt);
                    }
                    return cu.toString();
                }
            }
            return originalContent;
        } catch (Exception e) {
            log.error("Error in AST-based reordering", e);
            return originalContent;
        }
    }

    /**
     * 判断语句是否可重排序（与切片变量无关的声明/赋值/输出/循环等）
     */
    private boolean isReorderableStatement(Statement stmt) {
        // 1. 变量声明语句（不包含切片相关关键词）
        if (stmt instanceof ExpressionStmt) {
            ExpressionStmt exprStmt = (ExpressionStmt) stmt;
            Expression expr = exprStmt.getExpression();
            if (expr instanceof VariableDeclarationExpr) {
                VariableDeclarationExpr vde = (VariableDeclarationExpr) expr;
                for (VariableDeclarator vd : vde.getVariables()) {
                    if (isSliceRelatedVariable(vd.getNameAsString())) {
                        return false;
                    }
                }
                return true;
            }
            // 2. 赋值语句（不涉及切片变量）
            if (expr instanceof AssignExpr) {
                AssignExpr assign = (AssignExpr) expr;
                String target = assign.getTarget().toString();
                if (!isSliceRelatedVariable(target)) {
                    return true;
                }
            }
            // 3. 输出语句（如System.out.println）
            if (expr instanceof MethodCallExpr) {
                MethodCallExpr mce = (MethodCallExpr) expr;
                String methodName = mce.getNameAsString();
                if ((methodName.equals("println") || methodName.equals("print")) &&
                        mce.getScope().isPresent() && mce.getScope().get().toString().equals("System.out")) {
                    return true;
                }
            }
        }
        // 4. 独立循环（不包含切片变量）
        if (stmt instanceof ForStmt || stmt instanceof WhileStmt || stmt instanceof ForEachStmt) {
            if (!containsSliceRelatedVariable(stmt)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断语句是否包含切片相关变量
     */
    private boolean containsSliceRelatedVariable(Statement stmt) {
        Set<String> names = new HashSet<>();
        stmt.findAll(NameExpr.class).forEach(nameExpr -> names.add(nameExpr.getNameAsString()));
        for (String name : names) {
            if (isSliceRelatedVariable(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取指定行的内容
     */
    private String getLineContent(String sourceFile, int lineNumber) {
        try {
            String content = Files.readString(Paths.get(sourceFile), StandardCharsets.UTF_8);
            String[] lines = content.split("\n");
            if (lineNumber > 0 && lineNumber <= lines.length) {
                return lines[lineNumber - 1];
            }
            return "";
        } catch (IOException e) {
            log.error("Error reading line content", e);
            return "";
        }
    }

    /**
     * 判断是否为无用代码行
     */
    private boolean isDeadCodeLine(String line) {
        // 无用代码的特征 - 覆盖所有生成的无用代码类型
        return line.startsWith("if (false)") ||
                line.startsWith("for (int i = 0; i < 0;") ||
                line.matches("^int (unusedVar|temp)\\d+.*") ||
                line.matches("^int x = \\d+;") ||
                line.equals("for (int i = 0; i < 0; i++) { }") ||
                line.matches("^int temp\\d+ = \\d+;") ||
                line.matches("^int unusedVar\\d+ = \\d+;") ||
                line.matches("^if \\(false\\) \\{ int x = \\d+; \\}");
    }

    /**
     * 生成语句重排序的变异文件
     * @param baseDir 基础目录
     * @param numFiles 要生成的文件数量
     * @return 生成的原始文件路径列表
     */
    public List<String> generateStatementReorderFiles(String baseDir, int numFiles) {
        List<String> generatedFiles = new ArrayList<>();

        try {
            // 确保目录存在
            Files.createDirectories(Paths.get(MUTATED_DIR));
            Files.createDirectories(Paths.get("reordered"));

            for (int i = 0; i < numFiles; i++) {
                try {
                    // 生成原始代码
                    String originalContent = generateRandomJavaClass();

                    // 创建原始文件
                    String originalFileName = String.format("Example_original_%d.java", i);
                    String originalFilePath = Paths.get(MUTATED_DIR, originalFileName).toString();
                    Files.write(Paths.get(originalFilePath), originalContent.getBytes(StandardCharsets.UTF_8));
                    generatedFiles.add(originalFilePath);

                    // 创建语句重排序文件
                    String reorderedContent = reorderStatements(originalContent);
                    
                    // 检查重排序是否实际上改变了内容
                    if (reorderedContent.equals(originalContent)) {
                        log.warn("重排序后内容与原始内容相同，将尝试再次重排序");
                        // 再次尝试重排序，但加入随机性以尝试产生不同结果
                        reorderedContent = reorderStatements(originalContent);
                    }
                    
                    String reorderedFileName = String.format("Example_reordered_%d.java", i);
                    String reorderedFilePath = Paths.get("reordered", reorderedFileName).toString();
                    Files.write(Paths.get(reorderedFilePath), reorderedContent.getBytes(StandardCharsets.UTF_8));

                    log.info("Generated statement reorder file pair: {} -> {}", originalFilePath, reorderedFilePath);
                    
                    // 确认两个文件的差异
                    if (!reorderedContent.equals(originalContent)) {
                        log.info("成功重排序，两个文件内容不同");
                    } else {
                        log.warn("警告：重排序后的文件与原始文件相同!");
                    }

                } catch (Exception e) {
                    log.error("Error generating statement reorder file {}", i, e);
                }
            }

        } catch (IOException e) {
            log.error("Error creating directories for statement reorder files", e);
        }

        return generatedFiles;
    }

    /**
     * 判断变量名是否为切片相关变量
     */
    private boolean isSliceRelatedVariable(String varName) {
        // 明确排除无关变量
        if (varName.startsWith("unrelated")) {
            return false;
        }
        // 明确识别切片相关变量
        return varName.matches("val\\d+") || varName.matches("temp\\d+") || 
               varName.matches("result\\d+") || varName.equals("temp") ||
               varName.equals("choice"); // choice控制switch语句执行路径，是切片相关变量
    }

    /**
     * 生成控制流等价变换的变异文件
     * @param baseDir 基础目录
     * @param numFiles 要生成的文件数量
     * @return 生成的原始文件路径列表
     */
    public List<String> generateControlFlowFiles(String baseDir, int numFiles) {
        List<String> controlFlowFiles = new ArrayList<>();
        
        try {
            // 确保目录存在
            Files.createDirectories(Paths.get(CONTROLFLOW_DIR));
            
            for (int i = 0; i < numFiles; i++) {
                // 生成原始代码
                String originalContent = generateRandomJavaClass();
                
                // 对原始代码进行控制流变换
                String transformedContent = transformControlFlow(originalContent);
                
                // 保存变换后的代码
                String fileName = String.format("Example_controlflow_%d.java", i);
                String filePath = Paths.get(CONTROLFLOW_DIR, fileName).toString();
                
                Files.write(Paths.get(filePath), transformedContent.getBytes(StandardCharsets.UTF_8));
                controlFlowFiles.add(filePath);
                
                log.info("Generated control flow file: {}", filePath);
            }
            
        } catch (Exception e) {
            log.error("Error generating control flow files", e);
        }
        
        return controlFlowFiles;
    }

    /**
     * 控制流等价变换 - 确保不影响切片点的控制流结构
     */
    public String transformControlFlow(String originalContent) {
        try {
            CompilationUnit cu = javaParser.parse(originalContent).getResult().orElseThrow(() ->
                    new RuntimeException("Failed to parse content for control flow transformation"));

            Optional<MethodDeclaration> mainMethod = cu.findFirst(MethodDeclaration.class, md -> 
                    md.getNameAsString().equals("main"));

            boolean changed = false;
            if (mainMethod.isPresent()) {
                MethodDeclaration method = mainMethod.get();
                BlockStmt body = method.getBody().orElse(null);
                if (body != null) {
                    // 分析切片相关变量和控制依赖关系
                    Set<String> sliceVariables = findSliceRelatedVariables(body);
                    Map<String, Set<String>> controlDependencies = analyzeControlDependencies(body, sliceVariables);
                    
                    log.info("Found slice variables: {}", sliceVariables);
                    log.info("Control dependencies: {}", controlDependencies);
                    
                    // 应用各种控制流变换，但确保不影响切片点
                    changed |= transformIfStatements(body, sliceVariables, controlDependencies);
                    changed |= transformLoopStatements(body, sliceVariables, controlDependencies);
                    changed |= transformSwitchStatements(body, sliceVariables);

                    // 如果没有任何变换，尝试对无关的控制流结构进行变换
                    if (!changed) {
                        changed |= transformUnrelatedControlFlow(body, sliceVariables);
                    }
                    
                    return cu.toString();
                }
            }
            return originalContent;
        } catch (Exception e) {
            log.error("Error in control flow transformation", e);
            return originalContent;
        }
    }

    /**
     * 查找切片相关变量
     */
    private Set<String> findSliceRelatedVariables(BlockStmt body) {
        Set<String> sliceVariables = new HashSet<>();
        
        // 查找所有变量声明和使用
        body.findAll(VariableDeclarator.class).forEach(vd -> {
            String varName = vd.getNameAsString();
            if (isSliceRelatedVariable(varName)) {
                sliceVariables.add(varName);
            }
        });
        
        // 查找所有变量使用
        body.findAll(NameExpr.class).forEach(nameExpr -> {
            String varName = nameExpr.getNameAsString();
            if (isSliceRelatedVariable(varName)) {
                sliceVariables.add(varName);
            }
        });
        
        return sliceVariables;
    }

    /**
     * 分析控制依赖关系
     */
    private Map<String, Set<String>> analyzeControlDependencies(BlockStmt body, Set<String> sliceVariables) {
        Map<String, Set<String>> dependencies = new HashMap<>();
        
        // 分析if语句的控制依赖
        body.findAll(IfStmt.class).forEach(ifStmt -> {
            Set<String> conditionVars = extractVariablesFromExpression(ifStmt.getCondition());
            Set<String> dependentVars = new HashSet<>();
            
            // 检查then分支中的变量
            dependentVars.addAll(extractVariablesFromStatement(ifStmt.getThenStmt()));
            
            // 检查else分支中的变量
            if (ifStmt.getElseStmt().isPresent()) {
                dependentVars.addAll(extractVariablesFromStatement(ifStmt.getElseStmt().get()));
            }
            
            // 如果条件变量或依赖变量包含切片变量，则建立依赖关系
            for (String conditionVar : conditionVars) {
                if (sliceVariables.contains(conditionVar)) {
                    dependencies.computeIfAbsent(conditionVar, k -> new HashSet<>()).addAll(dependentVars);
                }
            }
            
            for (String dependentVar : dependentVars) {
                if (sliceVariables.contains(dependentVar)) {
                    dependencies.computeIfAbsent(dependentVar, k -> new HashSet<>()).addAll(conditionVars);
                }
            }
        });
        
        // 分析循环的控制依赖
        body.findAll(ForStmt.class).forEach(forStmt -> {
            Set<String> loopVars = extractVariablesFromExpression(forStmt.getCompare().orElse(null));
            Set<String> bodyVars = extractVariablesFromStatement(forStmt.getBody());
            
            for (String loopVar : loopVars) {
                if (sliceVariables.contains(loopVar)) {
                    dependencies.computeIfAbsent(loopVar, k -> new HashSet<>()).addAll(bodyVars);
                }
            }
        });
        
        body.findAll(WhileStmt.class).forEach(whileStmt -> {
            Set<String> conditionVars = extractVariablesFromExpression(whileStmt.getCondition());
            Set<String> bodyVars = extractVariablesFromStatement(whileStmt.getBody());
            
            for (String conditionVar : conditionVars) {
                if (sliceVariables.contains(conditionVar)) {
                    dependencies.computeIfAbsent(conditionVar, k -> new HashSet<>()).addAll(bodyVars);
                }
            }
        });
        
        return dependencies;
    }

    /**
     * 从表达式中提取变量名
     */
    private Set<String> extractVariablesFromExpression(Expression expr) {
        Set<String> variables = new HashSet<>();
        if (expr != null) {
            expr.findAll(NameExpr.class).forEach(nameExpr -> 
                variables.add(nameExpr.getNameAsString()));
        }
        return variables;
    }

    /**
     * 从语句中提取变量名
     */
    private Set<String> extractVariablesFromStatement(Statement stmt) {
        Set<String> variables = new HashSet<>();
        if (stmt != null) {
            stmt.findAll(NameExpr.class).forEach(nameExpr -> 
                variables.add(nameExpr.getNameAsString()));
        }
        return variables;
    }

    /**
     * 变换if语句结构 - 确保不影响切片点
     */
    private boolean transformIfStatements(BlockStmt body, Set<String> sliceVariables, 
                                        Map<String, Set<String>> controlDependencies) {
        boolean changed = false;
        List<IfStmt> ifStatements = body.findAll(IfStmt.class);
        
        for (IfStmt ifStmt : ifStatements) {
            // 检查这个if语句是否影响切片点
            if (affectsSlicePoint(ifStmt, sliceVariables, controlDependencies)) {
                log.info("Skipping if statement transformation - affects slice point");
                continue;
            }
            
            // 安全变换：交换if-else分支
            if (ifStmt.getElseStmt().isPresent()) {
                Statement thenStmt = ifStmt.getThenStmt();
                Statement elseStmt = ifStmt.getElseStmt().get();
                
                // 创建取反条件
                Expression negatedCondition = createNegatedCondition(ifStmt.getCondition());
                if (negatedCondition != null) {
                    ifStmt.setCondition(negatedCondition);
                    ifStmt.setThenStmt(elseStmt);
                    ifStmt.setElseStmt(thenStmt);
                    log.info("Transformed if statement: negated condition and swapped branches");
                    changed = true;
                }
            }
        }
        return changed;
    }

    /**
     * 变换循环语句结构 - 确保不影响切片点
     */
    private boolean transformLoopStatements(BlockStmt body, Set<String> sliceVariables,
                                          Map<String, Set<String>> controlDependencies) {
        boolean changed = false;
        
        // 变换for循环
        List<ForStmt> forStatements = body.findAll(ForStmt.class);
        for (ForStmt forStmt : forStatements) {
            if (affectsSlicePoint(forStmt, sliceVariables, controlDependencies)) {
                log.info("Skipping for loop transformation - affects slice point");
                continue;
            }
            
            if (canTransformForToWhile(forStmt)) {
                transformForToWhile(forStmt);
                log.info("Transformed for loop to while loop");
                changed = true;
            }
        }
        
        // 变换while循环
        List<WhileStmt> whileStatements = body.findAll(WhileStmt.class);
        for (WhileStmt whileStmt : whileStatements) {
            if (affectsSlicePoint(whileStmt, sliceVariables, controlDependencies)) {
                log.info("Skipping while loop transformation - affects slice point");
                continue;
            }
            
            if (canTransformWhileToFor(whileStmt)) {
                transformWhileToFor(whileStmt);
                log.info("Transformed while loop to for loop");
                changed = true;
            }
        }
        
        return changed;
    }

    /**
     * 变换switch语句结构 - 确保不影响切片点
     */
    private boolean transformSwitchStatements(BlockStmt body, Set<String> sliceVariables) {
        boolean changed = false;
        List<SwitchStmt> switchStatements = body.findAll(SwitchStmt.class);
        
        for (SwitchStmt switchStmt : switchStatements) {
            // 检查switch语句是否影响切片点
            if (affectsSlicePoint(switchStmt, sliceVariables)) {
                log.info("Skipping switch statement transformation - affects slice point");
                continue;
            }
            
            // 重新排列case顺序
            reorderSwitchCases(switchStmt);
            log.info("Reordered switch cases");
            changed = true;
        }
        return changed;
    }

    /**
     * 检查控制流结构是否影响切片点
     */
    private boolean affectsSlicePoint(Statement stmt, Set<String> sliceVariables) {
        return affectsSlicePoint(stmt, sliceVariables, new HashMap<>());
    }

    /**
     * 检查控制流结构是否影响切片点
     */
    private boolean affectsSlicePoint(Statement stmt, Set<String> sliceVariables, 
                                    Map<String, Set<String>> controlDependencies) {
        Set<String> stmtVariables = extractVariablesFromStatement(stmt);
        
        // 检查语句中是否包含切片变量
        for (String var : stmtVariables) {
            if (sliceVariables.contains(var)) {
                return true;
            }
        }
        
        // 检查控制依赖关系
        for (String sliceVar : sliceVariables) {
            Set<String> dependencies = controlDependencies.get(sliceVar);
            if (dependencies != null) {
                for (String depVar : dependencies) {
                    if (stmtVariables.contains(depVar)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    /**
     * 变换无关的控制流结构
     */
    private boolean transformUnrelatedControlFlow(BlockStmt body, Set<String> sliceVariables) {
        boolean changed = false;
        
        // 查找不包含切片变量的控制流结构
        List<IfStmt> unrelatedIfs = body.findAll(IfStmt.class).stream()
            .filter(ifStmt -> !affectsSlicePoint(ifStmt, sliceVariables))
            .collect(Collectors.toList());
            
        List<ForStmt> unrelatedFors = body.findAll(ForStmt.class).stream()
            .filter(forStmt -> !affectsSlicePoint(forStmt, sliceVariables))
            .collect(Collectors.toList());
            
        List<WhileStmt> unrelatedWhiles = body.findAll(WhileStmt.class).stream()
            .filter(whileStmt -> !affectsSlicePoint(whileStmt, sliceVariables))
            .collect(Collectors.toList());
        
        // 对无关的控制流结构进行变换
        for (IfStmt ifStmt : unrelatedIfs) {
            if (ifStmt.getElseStmt().isPresent()) {
                Expression negatedCondition = createNegatedCondition(ifStmt.getCondition());
                if (negatedCondition != null) {
                    Statement thenStmt = ifStmt.getThenStmt();
                    Statement elseStmt = ifStmt.getElseStmt().get();
                    ifStmt.setCondition(negatedCondition);
                    ifStmt.setThenStmt(elseStmt);
                    ifStmt.setElseStmt(thenStmt);
                    changed = true;
                    log.info("Transformed unrelated if statement");
                }
            }
        }
        
        for (ForStmt forStmt : unrelatedFors) {
            if (canTransformForToWhile(forStmt)) {
                transformForToWhile(forStmt);
                changed = true;
                log.info("Transformed unrelated for loop");
            }
        }
        
        for (WhileStmt whileStmt : unrelatedWhiles) {
            if (canTransformWhileToFor(whileStmt)) {
                transformWhileToFor(whileStmt);
                changed = true;
                log.info("Transformed unrelated while loop");
            }
        }
        
        return changed;
    }

    /**
     * 检查for循环是否可以安全地转换为while循环
     */
    private boolean canTransformForToWhile(ForStmt forStmt) {
        try {
            // 检查是否有基本的for循环结构
            Optional<Expression> condition = forStmt.getCompare();
            List<Expression> initialization = forStmt.getInitialization();
            List<Expression> update = forStmt.getUpdate();
            
            if (!condition.isPresent() || initialization.isEmpty() || update.isEmpty()) {
                return false;
            }
            
            // 检查初始化是否包含变量声明
            boolean hasVariableDeclaration = initialization.stream()
                .anyMatch(expr -> expr instanceof VariableDeclarationExpr);
            
            // 检查更新语句是否简单（如i++）
            boolean hasSimpleUpdate = update.stream()
                .anyMatch(expr -> expr.toString().contains("++") || expr.toString().contains("+="));
            
            return hasVariableDeclaration && hasSimpleUpdate;
        } catch (Exception e) {
            log.error("Error checking if for loop can be transformed", e);
            return false;
        }
    }

    /**
     * 检查while循环是否可以转换为for循环
     */
    private boolean canTransformWhileToFor(WhileStmt whileStmt) {
        try {
            Statement body = whileStmt.getBody();
            if (body instanceof BlockStmt) {
                BlockStmt bodyBlock = (BlockStmt) body;
                List<Statement> statements = bodyBlock.getStatements();
                
                // 检查是否有计数器变量更新
                for (Statement stmt : statements) {
                    if (stmt instanceof ExpressionStmt) {
                        ExpressionStmt exprStmt = (ExpressionStmt) stmt;
                        if (exprStmt.getExpression() instanceof AssignExpr) {
                            AssignExpr assign = (AssignExpr) exprStmt.getExpression();
                            if (assign.getTarget() instanceof NameExpr) {
                                String varName = ((NameExpr) assign.getTarget()).getNameAsString();
                                String value = assign.getValue().toString();
                                // 检查是否是简单的计数器更新
                                if (value.contains(varName + " + 1") || 
                                    value.contains(varName + "++") ||
                                    value.contains(varName + " += 1")) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            log.error("Error checking if while loop can be transformed", e);
            return false;
        }
    }

    /**
     * 将for循环转换为while循环
     */
    private void transformForToWhile(ForStmt forStmt) {
        try {
            // 提取for循环的各个部分
            List<Expression> initialization = forStmt.getInitialization();
            Optional<Expression> condition = forStmt.getCompare();
            List<Expression> update = forStmt.getUpdate();
            Statement body = forStmt.getBody();
            
            if (condition.isPresent() && !initialization.isEmpty() && !update.isEmpty()) {
                // 创建while循环
                WhileStmt whileStmt = new WhileStmt();
                whileStmt.setCondition(condition.get());
                
                // 创建新的语句块，包含初始化、循环体和更新
                BlockStmt newBody = new BlockStmt();
                
                // 添加循环体
                if (body instanceof BlockStmt) {
                    newBody.getStatements().addAll(((BlockStmt) body).getStatements());
                } else {
                    newBody.addStatement(body);
                }
                
                // 添加更新语句
                for (Expression updateExpr : update) {
                    newBody.addStatement(new ExpressionStmt(updateExpr));
                }
                
                whileStmt.setBody(newBody);
                
                // 替换原for循环
                Node parent = forStmt.getParentNode().orElse(null);
                if (parent instanceof BlockStmt) {
                    BlockStmt parentBlock = (BlockStmt) parent;
                    int index = parentBlock.getStatements().indexOf(forStmt);
                    if (index >= 0) {
                        // 先添加初始化语句
                        for (Expression initExpr : initialization) {
                            parentBlock.addStatement(index, new ExpressionStmt(initExpr));
                            index++;
                        }
                        // 替换for循环为while循环
                        parentBlock.setStatement(index, whileStmt);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error transforming for to while loop", e);
        }
    }

    /**
     * 将while循环转换为for循环
     */
    private void transformWhileToFor(WhileStmt whileStmt) {
        try {
            // 简化实现：只处理简单的while循环
            Statement body = whileStmt.getBody();
            if (body instanceof BlockStmt) {
                BlockStmt bodyBlock = (BlockStmt) body;
                List<Statement> statements = bodyBlock.getStatements();
                
                // 查找可能的计数器变量
                for (Statement stmt : statements) {
                    if (stmt instanceof ExpressionStmt) {
                        ExpressionStmt exprStmt = (ExpressionStmt) stmt;
                        if (exprStmt.getExpression() instanceof AssignExpr) {
                            AssignExpr assign = (AssignExpr) exprStmt.getExpression();
                            if (assign.getTarget() instanceof NameExpr) {
                                String varName = ((NameExpr) assign.getTarget()).getNameAsString();
                                // 检查是否是简单的计数器更新（如 i++）
                                if (assign.getValue().toString().contains(varName + " + 1") || 
                                    assign.getValue().toString().contains(varName + "++")) {
                                    // 这里可以实现while到for的转换
                                    log.info("Found potential counter variable: " + varName);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error transforming while to for loop", e);
        }
    }

    /**
     * 检查是否为简单的while循环
     */
    private boolean isSimpleWhileLoop(WhileStmt whileStmt) {
        // 简化检查：只要循环体不为空就认为是简单的
        return whileStmt.getBody() != null;
    }

    /**
     * 创建条件的取反表达式
     */
    private Expression createNegatedCondition(Expression condition) {
        try {
            if (condition instanceof BinaryExpr) {
                BinaryExpr binaryExpr = (BinaryExpr) condition;
                BinaryExpr.Operator operator = binaryExpr.getOperator();
                
                // 简单的取反变换
                switch (operator) {
                    case EQUALS:
                        return new BinaryExpr(binaryExpr.getLeft(), binaryExpr.getRight(), BinaryExpr.Operator.NOT_EQUALS);
                    case NOT_EQUALS:
                        return new BinaryExpr(binaryExpr.getLeft(), binaryExpr.getRight(), BinaryExpr.Operator.EQUALS);
                    case LESS:
                        return new BinaryExpr(binaryExpr.getLeft(), binaryExpr.getRight(), BinaryExpr.Operator.GREATER_EQUALS);
                    case LESS_EQUALS:
                        return new BinaryExpr(binaryExpr.getLeft(), binaryExpr.getRight(), BinaryExpr.Operator.GREATER);
                    case GREATER:
                        return new BinaryExpr(binaryExpr.getLeft(), binaryExpr.getRight(), BinaryExpr.Operator.LESS_EQUALS);
                    case GREATER_EQUALS:
                        return new BinaryExpr(binaryExpr.getLeft(), binaryExpr.getRight(), BinaryExpr.Operator.LESS);
                    default:
                        // 对于其他操作符，使用逻辑非
                        return new UnaryExpr(condition, UnaryExpr.Operator.LOGICAL_COMPLEMENT);
                }
            } else {
                // 对于非二元表达式，使用逻辑非
                return new UnaryExpr(condition, UnaryExpr.Operator.LOGICAL_COMPLEMENT);
            }
        } catch (Exception e) {
            log.error("Error creating negated condition", e);
            return null;
        }
    }

    /**
     * 变换比较操作符
     */
    private BinaryExpr.Operator transformComparisonOperator(BinaryExpr.Operator operator) {
        switch (operator) {
            case LESS:
                return BinaryExpr.Operator.GREATER_EQUALS;
            case LESS_EQUALS:
                return BinaryExpr.Operator.GREATER;
            case GREATER:
                return BinaryExpr.Operator.LESS_EQUALS;
            case GREATER_EQUALS:
                return BinaryExpr.Operator.LESS;
            case EQUALS:
                return BinaryExpr.Operator.NOT_EQUALS;
            case NOT_EQUALS:
                return BinaryExpr.Operator.EQUALS;
            default:
                return null;
        }
    }

    /**
     * 检查循环体是否依赖循环变量
     */
    private boolean loopBodyDependsOnLoopVariable(ForStmt forStmt) {
        // 简化实现：检查循环体中是否使用了循环变量
        Set<String> loopVariables = new HashSet<>();
        forStmt.getInitialization().forEach(expr -> {
            if (expr instanceof VariableDeclarationExpr) {
                ((VariableDeclarationExpr) expr).getVariables().forEach(vd -> 
                    loopVariables.add(vd.getNameAsString()));
            }
        });
        
        Set<String> usedVariables = new HashSet<>();
        forStmt.getBody().findAll(NameExpr.class).forEach(nameExpr -> 
            usedVariables.add(nameExpr.getNameAsString()));
        
        // 如果循环体使用了循环变量，则认为有依赖
        for (String loopVar : loopVariables) {
            if (usedVariables.contains(loopVar)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 重新排列switch的case顺序
     */
    private void reorderSwitchCases(SwitchStmt switchStmt) {
        List<SwitchEntry> entries = new ArrayList<>(switchStmt.getEntries());
        if (entries.size() > 2) { // 至少要有3个case才重排序
            // 保留第一个和最后一个case，重排序中间的case
            if (entries.size() > 3) {
                List<SwitchEntry> middleEntries = entries.subList(1, entries.size() - 1);
                Collections.shuffle(middleEntries);
                
                List<SwitchEntry> newEntries = new ArrayList<>();
                newEntries.add(entries.get(0));
                newEntries.addAll(middleEntries);
                newEntries.add(entries.get(entries.size() - 1));
                
                switchStmt.getEntries().clear();
                switchStmt.getEntries().addAll(newEntries);
            }
        }
    }

    /**
     * 查找指定变量在文件中的正确行号
     * @param sourceFile 源文件路径
     * @param targetVariable 目标变量名
     * @return 变量的行号信息，如果找不到返回null
     */
    public VariableInfo findVariableLineNumber(String sourceFile, String targetVariable) {
        try {
            String content = Files.readString(Paths.get(sourceFile), StandardCharsets.UTF_8);
            CompilationUnit cu = javaParser.parse(content).getResult().orElseThrow(() ->
                    new RuntimeException("Failed to parse Java file"));

            log.info("=== Finding line number for variable '{}' in file: {} ===", targetVariable, sourceFile);

            // 查找变量声明
            List<VariableDeclarator> declarations = cu.findAll(VariableDeclarator.class).stream()
                    .filter(vd -> vd.getNameAsString().equals(targetVariable))
                    .collect(Collectors.toList());

            // 查找变量使用
            List<NameExpr> usages = cu.findAll(NameExpr.class).stream()
                    .filter(nameExpr -> nameExpr.getNameAsString().equals(targetVariable))
                    .collect(Collectors.toList());

            log.info("Found {} declarations and {} usages for variable '{}'", 
                    declarations.size(), usages.size(), targetVariable);

            // 收集所有行号
            List<Integer> allLines = new ArrayList<>();
            
            // 添加声明行号
            for (VariableDeclarator vd : declarations) {
                int line = vd.getBegin().get().line;
                allLines.add(line);
                log.info("Declaration of '{}' at line {}", targetVariable, line);
            }
            
            // 添加使用行号
            for (NameExpr nameExpr : usages) {
                int line = nameExpr.getBegin().get().line;
                allLines.add(line);
                log.info("Usage of '{}' at line {}", targetVariable, line);
            }

            // 排序去重
            allLines = allLines.stream().distinct().sorted().collect(Collectors.toList());
            log.info("All lines for '{}': {}", targetVariable, allLines);

            if (allLines.isEmpty()) {
                log.warn("Variable '{}' not found in file: {}", targetVariable, sourceFile);
                return null;
            }

            // 策略1：优先选择最后一次赋值或使用的行号
            int lastLine = allLines.get(allLines.size() - 1);
            log.info("Selected last occurrence of '{}' at line {}", targetVariable, lastLine);
            
            return new VariableInfo(targetVariable, lastLine);

        } catch (IOException e) {
            log.error("Error finding line number for variable '{}' in file: {}", targetVariable, sourceFile, e);
            return null;
        }
    }

    /**
     * 查找指定变量在文件中的最后一次赋值行号
     * @param sourceFile 源文件路径
     * @param targetVariable 目标变量名
     * @return 变量的最后一次赋值行号，如果找不到返回null
     */
    public VariableInfo findVariableLastAssignment(String sourceFile, String targetVariable) {
        try {
            String content = Files.readString(Paths.get(sourceFile), StandardCharsets.UTF_8);
            CompilationUnit cu = javaParser.parse(content).getResult().orElseThrow(() ->
                    new RuntimeException("Failed to parse Java file"));

            log.info("=== Finding last assignment for variable '{}' in file: {} ===", targetVariable, sourceFile);

            List<Integer> assignmentLinesRaw = new ArrayList<>();

            // 1. 查找所有赋值表达式（如 val1 = ...;）
            cu.findAll(AssignExpr.class).forEach(assign -> {
                Expression target = assign.getTarget();
                if (target instanceof NameExpr) {
                    if (((NameExpr) target).getNameAsString().equals(targetVariable)) {
                        assignmentLinesRaw.add(assign.getBegin().get().line);
                    }
                }
            });

            // 2. 查找所有声明赋值（如 int val1 = ...;）
            cu.findAll(VariableDeclarator.class).forEach(vd -> {
                if (vd.getNameAsString().equals(targetVariable) && vd.getInitializer().isPresent()) {
                    assignmentLinesRaw.add(vd.getBegin().get().line);
                }
            });

            // 3. 查找所有 ExpressionStmt 里的赋值（兼容 switch-case 等）
            cu.findAll(com.github.javaparser.ast.stmt.ExpressionStmt.class).forEach(exprStmt -> {
                Expression expr = exprStmt.getExpression();
                if (expr instanceof AssignExpr) {
                    AssignExpr assign = (AssignExpr) expr;
                    Expression target = assign.getTarget();
                    if (target instanceof NameExpr) {
                        if (((NameExpr) target).getNameAsString().equals(targetVariable)) {
                            assignmentLinesRaw.add(exprStmt.getBegin().get().line);
                        }
                    }
                }
            });

            // 排序去重
            List<Integer> assignmentLines = assignmentLinesRaw.stream().distinct().sorted().collect(Collectors.toList());
            log.info("All assignment lines for '{}': {}", targetVariable, assignmentLines);

            if (!assignmentLines.isEmpty()) {
                // 优先选择有意义的赋值（非默认值）
                // 跳过声明赋值（通常是初始化为0），选择第一个实际赋值
                int selectedLine = assignmentLines.get(0); // 默认选择第一个
                
                // 如果第一个是声明赋值，选择第二个
                if (assignmentLines.size() > 1) {
                    // 检查第一个赋值是否是声明赋值（通常在第20-25行）
                    int firstLine = assignmentLines.get(0);
                    if (firstLine <= 25) { // 假设声明赋值在前25行
                        selectedLine = assignmentLines.get(1);
                        log.info("Skipped declaration assignment at line {}, selected meaningful assignment at line {}", 
                                firstLine, selectedLine);
                    }
                }
                
                log.info("Selected meaningful assignment of '{}' at line {}", targetVariable, selectedLine);
                return new VariableInfo(targetVariable, selectedLine);
            }

            log.warn("No assignments found for variable '{}' in file: {}", targetVariable, sourceFile);
            // 如果没有赋值，返回最后一次使用
            return findVariableLineNumber(sourceFile, targetVariable);
        } catch (IOException e) {
            log.error("Error finding last assignment for variable '{}' in file: {}", targetVariable, sourceFile, e);
            return null;
        }
    }

    /**
     * 查找指定变量在文件中的声明行号
     * @param sourceFile 源文件路径
     * @param targetVariable 目标变量名
     * @return 变量的声明行号信息，如果找不到返回null
     */
    public VariableInfo findVariableDeclaration(String sourceFile, String targetVariable) {
        try {
            String content = Files.readString(Paths.get(sourceFile), StandardCharsets.UTF_8);
            CompilationUnit cu = javaParser.parse(content).getResult().orElseThrow(() ->
                    new RuntimeException("Failed to parse Java file"));

            // 查找所有变量声明
            List<VariableDeclarator> variables = cu.findAll(VariableDeclarator.class);

            for (VariableDeclarator var : variables) {
                if (var.getNameAsString().equals(targetVariable)) {
                    // 获取变量声明的行号
                    if (var.getBegin().isPresent()) {
                        int lineNumber = var.getBegin().get().line;
                        log.info("Found variable '{}' declaration at line {} in file: {}",
                                targetVariable, lineNumber, sourceFile);
                        return new VariableInfo(targetVariable, lineNumber);
                    }
                }
            }

            log.warn("Variable '{}' declaration not found in file: {}", targetVariable, sourceFile);
            return null;

        } catch (IOException e) {
            log.error("Error finding variable declaration for '{}' in file: {}", targetVariable, sourceFile, e);
            return null;
        }
    }

    /**
     * 生成切片命令字符串
     * @param sourceFile 源文件路径
     * @param targetVariable 目标变量名
     * @return 完整的切片命令字符串
     */
    public String generateSliceCommand(String sourceFile, String targetVariable) {
        VariableInfo varInfo = findVariableLastAssignment(sourceFile, targetVariable);
        if (varInfo == null) {
            log.error("Could not find variable '{}' in file: {}", targetVariable, sourceFile);
            return null;
        }
        
        String command = String.format("java -jar src/main/java/sdg-cli-1.3.0-jar-with-dependencies.jar -c %s#%d:%s", 
                sourceFile, varInfo.getLineNumber(), varInfo.getVariableName());
        
        log.info("Generated slice command: {}", command);
        return command;
    }

    /**
     * 生成数据流等价变换的变异文件
     * @param baseDir 基础目录
     * @param numFiles 要生成的文件数量
     * @return 生成的原始文件路径列表
     */
    public List<String> generateDataFlowFiles(String baseDir, int numFiles) {
        List<String> generatedFiles = new ArrayList<>();

        try {
            // 确保目录存在
            Files.createDirectories(Paths.get(MUTATED_DIR));
            Files.createDirectories(Paths.get("dataflow"));

            for (int i = 0; i < numFiles; i++) {
                try {
                    // 生成原始代码
                    String originalContent = generateRandomJavaClass();

                    // 创建原始文件
                    String originalFileName = String.format("Example_original_%d.java", i);
                    String originalFilePath = Paths.get(MUTATED_DIR, originalFileName).toString();
                    Files.write(Paths.get(originalFilePath), originalContent.getBytes(StandardCharsets.UTF_8));
                    generatedFiles.add(originalFilePath);

                    // 创建数据流变换文件
                    String dataFlowContent = transformDataFlow(originalContent);
                    
                    String dataFlowFileName = String.format("Example_dataflow_%d.java", i);
                    String dataFlowFilePath = Paths.get("dataflow", dataFlowFileName).toString();
                    Files.write(Paths.get(dataFlowFilePath), dataFlowContent.getBytes(StandardCharsets.UTF_8));

                    log.info("Generated data flow file pair: {} -> {}", originalFilePath, dataFlowFilePath);
                    
                    // 确认两个文件的差异
                    if (!dataFlowContent.equals(originalContent)) {
                        log.info("Successfully performed data flow transformation, files are different");
                    } else {
                        log.warn("Warning: Data flow transformation resulted in identical files!");
                    }

                } catch (Exception e) {
                    log.error("Error generating data flow file {}", i, e);
                }
            }

        } catch (IOException e) {
            log.error("Error creating directories for data flow files", e);
        }

        return generatedFiles;
    }

    /**
     * 数据流等价变换 - 确保不影响切片点的数据流结构
     */
    public String transformDataFlow(String originalContent) {
        try {
            CompilationUnit cu = javaParser.parse(originalContent).getResult().orElseThrow(() ->
                    new RuntimeException("Failed to parse content for data flow transformation"));

            Optional<MethodDeclaration> mainMethod = cu.findFirst(MethodDeclaration.class, md -> 
                    md.getNameAsString().equals("main"));

            boolean changed = false;
            if (mainMethod.isPresent()) {
                MethodDeclaration method = mainMethod.get();
                BlockStmt body = method.getBody().orElse(null);
                if (body != null) {
                    // 分析切片相关变量和数据依赖关系
                    Set<String> sliceVariables = findSliceRelatedVariables(body);
                    Map<String, Set<String>> dataDependencies = analyzeDataDependencies(body, sliceVariables);
                    
                    log.info("Found slice variables: {}", sliceVariables);
                    log.info("Data dependencies: {}", dataDependencies);
                    
                    // 应用各种数据流变换，但确保不影响切片点
                    changed |= transformUnrelatedVariableAssignments(body, sliceVariables, dataDependencies);
                    changed |= transformUnrelatedExpressions(body, sliceVariables, dataDependencies);
                    changed |= transformUnrelatedCalculations(body, sliceVariables, dataDependencies);

                    // 如果没有任何变换，尝试对无关的数据流结构进行变换
                    if (!changed) {
                        changed |= transformUnrelatedDataFlow(body, sliceVariables);
                    }
                    
                    return cu.toString();
                }
            }
            return originalContent;
        } catch (Exception e) {
            log.error("Error in data flow transformation", e);
            return originalContent;
        }
    }

    /**
     * 分析数据依赖关系
     */
    private Map<String, Set<String>> analyzeDataDependencies(BlockStmt body, Set<String> sliceVariables) {
        Map<String, Set<String>> dependencies = new HashMap<>();
        
        // 分析所有赋值语句的数据依赖
        body.findAll(AssignExpr.class).forEach(assign -> {
            Expression target = assign.getTarget();
            Expression value = assign.getValue();
            
            if (target instanceof NameExpr) {
                String targetVar = ((NameExpr) target).getNameAsString();
                Set<String> usedVars = extractVariablesFromExpression(value);
                
                // 建立数据依赖关系
                dependencies.computeIfAbsent(targetVar, k -> new HashSet<>()).addAll(usedVars);
                
                // 反向依赖：使用该变量的语句依赖于该变量
                for (String usedVar : usedVars) {
                    dependencies.computeIfAbsent(usedVar, k -> new HashSet<>()).add(targetVar);
                }
            }
        });
        
        // 分析变量声明中的数据依赖
        body.findAll(VariableDeclarator.class).forEach(vd -> {
            if (vd.getInitializer().isPresent()) {
                String targetVar = vd.getNameAsString();
                Set<String> usedVars = extractVariablesFromExpression(vd.getInitializer().get());
                
                dependencies.computeIfAbsent(targetVar, k -> new HashSet<>()).addAll(usedVars);
                for (String usedVar : usedVars) {
                    dependencies.computeIfAbsent(usedVar, k -> new HashSet<>()).add(targetVar);
                }
            }
        });
        
        return dependencies;
    }

    /**
     * 变换无关的变量赋值
     */
    private boolean transformUnrelatedVariableAssignments(BlockStmt body, Set<String> sliceVariables, 
                                                         Map<String, Set<String>> dataDependencies) {
        boolean changed = false;
        List<AssignExpr> assignments = body.findAll(AssignExpr.class);
        
        for (AssignExpr assign : assignments) {
            Expression target = assign.getTarget();
            if (target instanceof NameExpr) {
                String targetVar = ((NameExpr) target).getNameAsString();
                
                // 检查这个赋值是否影响切片点
                if (!affectsSlicePoint(assign, sliceVariables, dataDependencies)) {
                    // 安全变换：修改无关变量的赋值
                    Expression newValue = transformUnrelatedExpression(assign.getValue(), sliceVariables);
                    if (newValue != null && !newValue.equals(assign.getValue())) {
                        assign.setValue(newValue);
                        log.info("Transformed unrelated variable assignment: {} = {}", targetVar, newValue);
                        changed = true;
                    }
                }
            }
        }
        
        return changed;
    }

    /**
     * 变换无关的表达式
     */
    private boolean transformUnrelatedExpressions(BlockStmt body, Set<String> sliceVariables, 
                                                 Map<String, Set<String>> dataDependencies) {
        java.util.concurrent.atomic.AtomicBoolean exprChanged = new java.util.concurrent.atomic.AtomicBoolean(false);
        
        // 变换无关的变量声明初始化
        body.findAll(VariableDeclarator.class).forEach(vd -> {
            if (vd.getInitializer().isPresent()) {
                String varName = vd.getNameAsString();
                if (!sliceVariables.contains(varName) && !hasDataDependency(varName, sliceVariables, dataDependencies)) {
                    Expression newValue = transformUnrelatedExpression(vd.getInitializer().get(), sliceVariables);
                    if (newValue != null && !newValue.equals(vd.getInitializer().get())) {
                        vd.setInitializer(newValue);
                        log.info("Transformed unrelated variable initialization: {} = {}", varName, newValue);
                        exprChanged.set(true);
                    }
                }
            }
        });
        
        return exprChanged.get();
    }

    /**
     * 变换无关的计算
     */
    private boolean transformUnrelatedCalculations(BlockStmt body, Set<String> sliceVariables, 
                                                  Map<String, Set<String>> dataDependencies) {
        java.util.concurrent.atomic.AtomicBoolean calcChanged = new java.util.concurrent.atomic.AtomicBoolean(false);
        
        // 查找无关的计算语句并变换
        body.findAll(ExpressionStmt.class).forEach(exprStmt -> {
            Expression expr = exprStmt.getExpression();
            if (expr instanceof AssignExpr) {
                AssignExpr assign = (AssignExpr) expr;
                Expression target = assign.getTarget();
                
                if (target instanceof NameExpr) {
                    String targetVar = ((NameExpr) target).getNameAsString();
                    if (!sliceVariables.contains(targetVar) && !hasDataDependency(targetVar, sliceVariables, dataDependencies)) {
                        Expression newValue = transformUnrelatedExpression(assign.getValue(), sliceVariables);
                        if (newValue != null && !newValue.equals(assign.getValue())) {
                            assign.setValue(newValue);
                            log.info("Transformed unrelated calculation: {} = {}", targetVar, newValue);
                            calcChanged.set(true);
                        }
                    }
                }
            }
        });
        
        return calcChanged.get();
    }

    /**
     * 检查变量是否影响切片点
     */
    private boolean affectsSlicePoint(AssignExpr assign, Set<String> sliceVariables, 
                                    Map<String, Set<String>> dataDependencies) {
        Expression target = assign.getTarget();
        Expression value = assign.getValue();
        
        if (target instanceof NameExpr) {
            String targetVar = ((NameExpr) target).getNameAsString();
            
            // 直接检查：目标变量是否是切片变量
            if (sliceVariables.contains(targetVar)) {
                return true;
            }
            
            // 间接检查：目标变量是否与切片变量有数据依赖
            if (hasDataDependency(targetVar, sliceVariables, dataDependencies)) {
                return true;
            }
            
            // 检查赋值表达式中的变量是否与切片变量相关
            Set<String> usedVars = extractVariablesFromExpression(value);
            for (String usedVar : usedVars) {
                if (sliceVariables.contains(usedVar) || hasDataDependency(usedVar, sliceVariables, dataDependencies)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * 检查变量是否有数据依赖关系
     */
    private boolean hasDataDependency(String varName, Set<String> sliceVariables, 
                                    Map<String, Set<String>> dataDependencies) {
        // 直接依赖
        if (sliceVariables.contains(varName)) {
            return true;
        }
        
        // 间接依赖：通过数据依赖图检查
        Set<String> visited = new HashSet<>();
        return hasDataDependencyRecursive(varName, sliceVariables, dataDependencies, visited);
    }

    /**
     * 递归检查数据依赖关系
     */
    private boolean hasDataDependencyRecursive(String varName, Set<String> sliceVariables, 
                                             Map<String, Set<String>> dataDependencies, Set<String> visited) {
        if (visited.contains(varName)) {
            return false; // 避免循环依赖
        }
        
        visited.add(varName);
        
        Set<String> dependencies = dataDependencies.get(varName);
        if (dependencies != null) {
            for (String depVar : dependencies) {
                if (sliceVariables.contains(depVar)) {
                    return true;
                }
                if (hasDataDependencyRecursive(depVar, sliceVariables, dataDependencies, visited)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * 变换无关的表达式
     */
    private Expression transformUnrelatedExpression(Expression expr, Set<String> sliceVariables) {
        if (expr == null) {
            return null;
        }
        
        // 如果表达式中包含切片变量，不进行变换
        Set<String> exprVars = extractVariablesFromExpression(expr);
        for (String var : exprVars) {
            if (sliceVariables.contains(var)) {
                return null; // 不变换包含切片变量的表达式
            }
        }
        
        // 变换数值表达式
        if (expr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr) expr;
            BinaryExpr.Operator operator = binaryExpr.getOperator();
            
            // 变换操作符
            BinaryExpr.Operator newOperator = transformArithmeticOperator(operator);
            if (newOperator != null && newOperator != operator) {
                binaryExpr.setOperator(newOperator);
                return binaryExpr;
            }
        }
        
        // 变换数值字面量
        if (expr instanceof com.github.javaparser.ast.expr.IntegerLiteralExpr) {
            com.github.javaparser.ast.expr.IntegerLiteralExpr intExpr = 
                (com.github.javaparser.ast.expr.IntegerLiteralExpr) expr;
            int value = intExpr.asInt();
            
            // 生成新的数值，但保持合理的范围
            int newValue = value + random.nextInt(10) - 5; // ±5的随机变化
            if (newValue != value) {
                return new com.github.javaparser.ast.expr.IntegerLiteralExpr(newValue);
            }
        }
        
        return null;
    }

    /**
     * 变换算术操作符
     */
    private BinaryExpr.Operator transformArithmeticOperator(BinaryExpr.Operator operator) {
        switch (operator) {
            case PLUS:
                return random.nextBoolean() ? BinaryExpr.Operator.MINUS : BinaryExpr.Operator.MULTIPLY;
            case MINUS:
                return random.nextBoolean() ? BinaryExpr.Operator.PLUS : BinaryExpr.Operator.DIVIDE;
            case MULTIPLY:
                return random.nextBoolean() ? BinaryExpr.Operator.PLUS : BinaryExpr.Operator.MINUS;
            case DIVIDE:
                return random.nextBoolean() ? BinaryExpr.Operator.MULTIPLY : BinaryExpr.Operator.PLUS;
            default:
                return null;
        }
    }

    /**
     * 变换无关的数据流结构
     */
    private boolean transformUnrelatedDataFlow(BlockStmt body, Set<String> sliceVariables) {
        boolean changed = false;
        
        // 添加无关的计算语句
        List<String> unrelatedCalculations = generateUnrelatedCalculations(sliceVariables);
        for (String calculation : unrelatedCalculations) {
            try {
                Statement calcStmt = javaParser.parseStatement(calculation).getResult().orElse(null);
                if (calcStmt != null) {
                    // 在合适的位置插入无关计算
                    body.addStatement(calcStmt);
                    log.info("Added unrelated calculation: {}", calculation);
                    changed = true;
                }
            } catch (Exception e) {
                log.error("Failed to parse unrelated calculation: {}", calculation, e);
            }
        }
        
        return changed;
    }

    /**
     * 生成无关的计算语句
     */
    private List<String> generateUnrelatedCalculations(Set<String> sliceVariables) {
        List<String> calculations = new ArrayList<>();
        Random random = new Random();
        
        // 生成1-3个无关的计算语句
        int count = random.nextInt(3) + 1;
        
        for (int i = 0; i < count; i++) {
            String varName = generateUnrelatedVariableName(sliceVariables);
            String calculation = generateUnrelatedCalculation(varName);
            if (calculation != null) {
                calculations.add(calculation);
            }
        }
        
        return calculations;
    }

    /**
     * 生成无关的变量名
     */
    private String generateUnrelatedVariableName(Set<String> sliceVariables) {
        String[] unrelatedPrefixes = {"unrelated", "temp", "dummy", "aux", "helper"};
        String[] unrelatedSuffixes = {"Var", "Value", "Data", "Calc", "Result"};
        
        String varName;
        do {
            String prefix = unrelatedPrefixes[random.nextInt(unrelatedPrefixes.length)];
            String suffix = unrelatedSuffixes[random.nextInt(unrelatedSuffixes.length)];
            int number = random.nextInt(100);
            varName = prefix + suffix + number;
        } while (sliceVariables.contains(varName));
        
        return varName;
    }

    /**
     * 生成无关的计算语句
     */
    private String generateUnrelatedCalculation(String varName) {
        String[] operations = {
            varName + " = " + random.nextInt(100) + ";",
            varName + " = " + random.nextInt(50) + " + " + random.nextInt(50) + ";",
            varName + " = " + random.nextInt(20) + " * " + random.nextInt(10) + ";",
            varName + " = " + random.nextInt(100) + " - " + random.nextInt(50) + ";"
        };
        
        return operations[random.nextInt(operations.length)];
    }
}