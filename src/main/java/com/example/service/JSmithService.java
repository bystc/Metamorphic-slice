package com.example.service;

import com.github.lombrozo.jsmith.RandomJavaClass;
import com.github.lombrozo.jsmith.RandomScript;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.cactoos.io.InputOf;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class JSmithService {
    
    private static final String JSMITH_DIR = "jsmith-generated";
    private final Random random;
    
    // JSmith 生成配置
    private static final int MAX_ATTEMPTS = 5;  // 最大尝试次数
    private static final int MIN_CODE_LENGTH = 200;  // 最小代码长度
    private static final double MAX_DOLLAR_RATIO = 0.03;  // 最大$符号比例 (3%)
    private static final int MIN_MEANINGFUL_LINES = 5;  // 最少有意义行数
    private static final int MAX_STRANGE_LITERALS = 2;  // 最大奇怪字面量数量
    
    public JSmithService() {
        this.random = new Random();
        
        // 确保目录存在
        try {
            Files.createDirectories(Paths.get(JSMITH_DIR));
            log.info("Created jsmith directory: {}", JSMITH_DIR);
        } catch (IOException e) {
            log.error("Failed to create jsmith directory", e);
        }
    }
    
    /**
     * 使用JSmith生成随机Java代码
     */
    public List<String> generateRandomJavaCode(int numFiles) {
        List<String> generatedFiles = new ArrayList<>();
        
        try {
            for (int i = 0; i < numFiles; i++) {
                String fileName = String.format("JSmithRandom_%d.java", i);
                String filePath = Paths.get(JSMITH_DIR, fileName).toString();
                
                // 使用JSmith生成随机Java代码，带重试机制
                String generatedCode = generateWithJSmithRetry(i);
                
                // 保存到文件
                Files.write(Paths.get(filePath), generatedCode.getBytes(StandardCharsets.UTF_8));
                
                generatedFiles.add(filePath);
                log.info("Generated JSmith random file: {}", filePath);
            }
            
        } catch (Exception e) {
            log.error("Error generating JSmith random files", e);
        }
        
        return generatedFiles;
    }
    
    /**
     * 使用JSmith生成特定类型的Java代码
     */
    public List<String> generateTypedJavaCode(int numFiles, String type) {
        List<String> generatedFiles = new ArrayList<>();
        
        try {
            for (int i = 0; i < numFiles; i++) {
                String fileName = String.format("JSmith%s_%d.java", type, i);
                String filePath = Paths.get(JSMITH_DIR, fileName).toString();
                
                // 根据类型生成不同的代码，带重试机制
                String generatedCode = generateTypedCodeWithJSmithRetry(i, type);
                
                // 保存到文件
                Files.write(Paths.get(filePath), generatedCode.getBytes(StandardCharsets.UTF_8));
                
                generatedFiles.add(filePath);
                log.info("Generated JSmith {} file: {}", type, filePath);
            }
            
        } catch (Exception e) {
            log.error("Error generating JSmith {} files", type, e);
        }
        
        return generatedFiles;
    }
    
    /**
     * 使用JSmith生成代码，带重试机制
     */
    private String generateWithJSmithRetry(int index) {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                log.debug("JSmith generation attempt {} for index {}", attempt, index);
                
                // 创建JSmith实例并生成随机Java类
                RandomJavaClass randomClass = new RandomJavaClass();
                String generatedCode = randomClass.src();
                
                // 检查生成的代码质量
                if (isCodeQualityGood(generatedCode)) {
                    log.info("JSmith generated good quality code on attempt {}", attempt);
                    return generatedCode;
                } else {
                    log.debug("JSmith generated poor quality code on attempt {}, retrying...", attempt);
                }
                
            } catch (Exception e) {
                log.warn("JSmith generation failed on attempt {}: {}", attempt, e.getMessage());
            }
        }
        
        // 如果所有尝试都失败，使用备用生成器
        log.warn("All JSmith attempts failed for index {}, using fallback generator", index);
        return generateFallbackCode(index);
    }
    
    /**
     * 根据类型生成特定的JSmith代码，带重试机制
     */
    private String generateTypedCodeWithJSmithRetry(int index, String type) {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                log.debug("JSmith typed generation attempt {} for type {} index {}", attempt, type, index);
                
                // 根据类型调整生成策略
                RandomJavaClass randomClass = new RandomJavaClass();
                String generatedCode = randomClass.src();
                
                // 检查生成的代码质量
                if (isCodeQualityGood(generatedCode)) {
                    log.info("JSmith generated good quality code for type {} on attempt {}", type, attempt);
                    return generatedCode;
                } else {
                    log.debug("JSmith generated poor quality code for type {} on attempt {}, retrying...", type, attempt);
                }
                
            } catch (Exception e) {
                log.warn("JSmith typed generation failed for type {} on attempt {}: {}", type, attempt, e.getMessage());
            }
        }
        
        // 如果所有尝试都失败，使用备用生成器
        log.warn("All JSmith attempts failed for type {} index {}, using fallback generator", type, index);
        return generateTypedFallbackCode(index, type);
    }
    
    /**
     * 检查代码质量 - 更严格的标准
     */
    private boolean isCodeQualityGood(String code) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        
        // 检查代码长度
        if (code.length() < MIN_CODE_LENGTH) {
            log.debug("Code too short: {} chars", code.length());
            return false;
        }
        
        // 检查是否包含太多$符号（JSmith生成的问题代码特征）
        int dollarCount = 0;
        for (char c : code.toCharArray()) {
            if (c == '$') {
                dollarCount++;
            }
        }
        
        // 如果$符号超过代码长度的3%，认为质量不好
        double dollarRatio = (double) dollarCount / code.length();
        if (dollarRatio > MAX_DOLLAR_RATIO) {
            log.debug("Too many $ symbols: {}% ({} out of {} chars)", 
                     String.format("%.2f", dollarRatio * 100), dollarCount, code.length());
            return false;
        }
        
        // 检查是否包含基本的Java结构
        if (!code.contains("class") && !code.contains("interface")) {
            log.debug("No class or interface found");
            return false;
        }
        
        // 检查是否包含方法
        if (!code.contains("public") && !code.contains("private") && !code.contains("protected")) {
            log.debug("No access modifiers found");
            return false;
        }
        
        // 检查是否包含有意义的变量名（不全是$符号）
        String[] lines = code.split("\n");
        int meaningfulLines = 0;
        int strangeLiterals = 0;
        
        for (String line : lines) {
            line = line.trim();
            if (line.length() > 10 && !line.contains("$$$") && !line.contains("$")) {
                meaningfulLines++;
            }
            
            // 检查奇怪的字面量
            if (line.matches(".*0_[0-9]+.*") || line.matches(".*0b[01]+.*") || 
                line.matches(".*0x[a-fA-F0-9]+.*") || line.matches(".*0B[01]+.*")) {
                strangeLiterals++;
            }
        }
        
        if (meaningfulLines < MIN_MEANINGFUL_LINES) {
            log.debug("Not enough meaningful lines: {}", meaningfulLines);
            return false;
        }
        
        if (strangeLiterals > MAX_STRANGE_LITERALS) {
            log.debug("Too many strange literals: {}", strangeLiterals);
            return false;
        }
        
        // 检查是否包含基本的逻辑结构
        if (!code.contains("if") && !code.contains("for") && !code.contains("while") && 
            !code.contains("return") && !code.contains("=")) {
            log.debug("No basic logic structures found");
            return false;
        }
        
        return true;
    }
    
    /**
     * 生成备用代码（当JSmith失败时）
     */
    private String generateFallbackCode(int index) {
        StringBuilder code = new StringBuilder();
        code.append("public class JSmithRandom").append(index).append(" {\n");
        code.append("    public static void main(String[] args) {\n");
        
        // 生成一些变量
        int numVars = random.nextInt(4) + 2;
        for (int i = 0; i < numVars; i++) {
            String varName = "var" + i;
            int value = random.nextInt(100);
            code.append("        int ").append(varName).append(" = ").append(value).append(";\n");
        }
        
        // 生成一些操作
        for (int i = 0; i < numVars - 1; i++) {
            String var1 = "var" + i;
            String var2 = "var" + (i + 1);
            String operation = random.nextBoolean() ? "+" : "*";
            code.append("        ").append(var1).append(" = ").append(var1).append(" ").append(operation).append(" ").append(var2).append(";\n");
        }
        
        code.append("        System.out.println(\"Result: \" + var0);\n");
        code.append("    }\n");
        code.append("}\n");
        
        return code.toString();
    }
    
    /**
     * 根据类型生成备用代码
     */
    private String generateTypedFallbackCode(int index, String type) {
        switch (type.toLowerCase()) {
            case "complex":
                return generateComplexFallbackCode(index);
            case "simple":
                return generateSimpleFallbackCode(index);
            case "method":
                return generateMethodFallbackCode(index);
            case "expression":
                return generateExpressionFallbackCode(index);
            default:
                return generateFallbackCode(index);
        }
    }
    
    private String generateComplexFallbackCode(int index) {
        StringBuilder code = new StringBuilder();
        code.append("public class JSmithComplex").append(index).append(" {\n");
        
        // 生成字段
        code.append("    private int value;\n");
        code.append("    private String name;\n\n");
        
        // 生成构造函数
        code.append("    public JSmithComplex").append(index).append("(int value, String name) {\n");
        code.append("        this.value = value;\n");
        code.append("        this.name = name;\n");
        code.append("    }\n\n");
        
        // 生成方法
        code.append("    public int calculate(int x, int y) {\n");
        code.append("        int result = x + y;\n");
        code.append("        if (result > 100) {\n");
        code.append("            result = result * 2;\n");
        code.append("        } else {\n");
        code.append("            result = result / 2;\n");
        code.append("        }\n");
        code.append("        return result;\n");
        code.append("    }\n\n");
        
        code.append("    public static void main(String[] args) {\n");
        code.append("        JSmithComplex").append(index).append(" obj = new JSmithComplex").append(index).append("(50, \"test\");\n");
        code.append("        int result = obj.calculate(30, 40);\n");
        code.append("        System.out.println(\"Complex result: \" + result);\n");
        code.append("    }\n");
        code.append("}\n");
        
        return code.toString();
    }
    
    private String generateSimpleFallbackCode(int index) {
        StringBuilder code = new StringBuilder();
        code.append("public class JSmithSimple").append(index).append(" {\n");
        code.append("    public static void main(String[] args) {\n");
        code.append("        int x = 10;\n");
        code.append("        int y = 20;\n");
        code.append("        int sum = x + y;\n");
        code.append("        System.out.println(\"Simple sum: \" + sum);\n");
        code.append("    }\n");
        code.append("}\n");
        
        return code.toString();
    }
    
    private String generateMethodFallbackCode(int index) {
        StringBuilder code = new StringBuilder();
        code.append("public class JSmithMethod").append(index).append(" {\n");
        
        // 生成多个方法
        for (int i = 0; i < 3; i++) {
            code.append("    public int method").append(i).append("(int a, int b) {\n");
            code.append("        return a + b;\n");
            code.append("    }\n\n");
        }
        
        code.append("    public static void main(String[] args) {\n");
        code.append("        JSmithMethod").append(index).append(" obj = new JSmithMethod").append(index).append("();\n");
        code.append("        int result = obj.method0(5, 10);\n");
        code.append("        System.out.println(\"Method result: \" + result);\n");
        code.append("    }\n");
        code.append("}\n");
        
        return code.toString();
    }
    
    private String generateExpressionFallbackCode(int index) {
        StringBuilder code = new StringBuilder();
        code.append("public class JSmithExpression").append(index).append(" {\n");
        code.append("    public static void main(String[] args) {\n");
        
        // 生成复杂表达式
        code.append("        int a = 5, b = 10, c = 15;\n");
        code.append("        int result = (a + b) * c / 3;\n");
        code.append("        result = result + (b - a) * 2;\n");
        code.append("        System.out.println(\"Expression result: \" + result);\n");
        
        code.append("    }\n");
        code.append("}\n");
        
        return code.toString();
    }
    
    /**
     * 清理生成的文件
     */
    public void cleanupGeneratedFiles() {
        try {
            Path jsmithPath = Paths.get(JSMITH_DIR);
            if (Files.exists(jsmithPath)) {
                Files.walk(jsmithPath)
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".java"))
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                                log.info("Deleted file: {}", path);
                            } catch (IOException e) {
                                log.error("Failed to delete file: {}", path, e);
                            }
                        });
            }
            log.info("Cleaned up JSmith generated files");
        } catch (IOException e) {
            log.error("Error cleaning up JSmith files", e);
        }
    }
    
    /**
     * 获取生成目录的路径
     */
    public String getGeneratedDirectory() {
        return JSMITH_DIR;
    }
    
    /**
     * 列出所有生成的文件
     */
    public List<String> listGeneratedFiles() {
        List<String> files = new ArrayList<>();
        try {
            Path jsmithPath = Paths.get(JSMITH_DIR);
            if (Files.exists(jsmithPath)) {
                Files.walk(jsmithPath)
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".java"))
                        .forEach(path -> files.add(path.toString()));
            }
        } catch (IOException e) {
            log.error("Error listing JSmith files", e);
        }
        return files;
    }
    
    /**
     * 使用自定义ANTLR语法生成复杂Java代码
     */
    public String generateComplexWithCustomGrammar() {
        try {
            String grammarFile = "ComplexJava.g4";
            RandomScript script = new RandomScript(new InputOf(grammarFile));
            return script.generate("compilationUnit").output();
        } catch (Exception e) {
            log.error("Custom grammar generation failed", e);
            return null;
        }
    }
} 