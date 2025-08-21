package com.github.lombrozo.jsmith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 生成语法正确的Java代码示例
 */
public class ValidCodeExample {
    public static void main(String... args) {
        try {
            // 使用固定种子确保可重现的结果
            RandomJavaClass clazz = new RandomJavaClass(12345L);
            String rawCode = clazz.src();

            // 格式化代码
            String code = CodeFormatter.format(rawCode);

            // 输出到控制台
            System.out.println("Generated Valid Java code:");
            System.out.println("==========================");
            System.out.println(code);
            System.out.println("==========================");
            
            // 验证代码语法
            boolean isValid = validateJavaSyntax(code);
            System.out.println("Syntax validation: " + (isValid ? "PASSED" : "FAILED"));
            
            // 提取类名
            String className = extractClassName(code);
            if (className == null) {
                className = "ValidGeneratedClass";
            }
            
            // 创建输出目录
            Path outputDir = Paths.get("generated");
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            
            // 生成文件名
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = className + "_valid_" + timestamp + ".java";
            Path filePath = outputDir.resolve(fileName);
            
            // 写入文件
            Files.write(filePath, code.getBytes());
            
            System.out.println("Valid code saved to: " + filePath.toAbsolutePath());
            System.out.println("File size: " + Files.size(filePath) + " bytes");
            
            // 尝试编译验证
            if (isValid) {
                System.out.println("Code appears to be syntactically correct!");
            } else {
                System.out.println("Warning: Generated code may have syntax issues.");
            }
            
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error generating code: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 简单的Java语法验证
     */
    private static boolean validateJavaSyntax(String code) {
        // 基本语法检查
        if (!code.contains("class ")) {
            return false;
        }
        
        if (!code.contains("public static void main")) {
            return false;
        }
        
        // 检查括号匹配
        int braceCount = 0;
        int parenCount = 0;
        
        for (char c : code.toCharArray()) {
            switch (c) {
                case '{': braceCount++; break;
                case '}': braceCount--; break;
                case '(': parenCount++; break;
                case ')': parenCount--; break;
            }
            
            if (braceCount < 0 || parenCount < 0) {
                return false;
            }
        }
        
        return braceCount == 0 && parenCount == 0;
    }
    
    /**
     * 从生成的Java代码中提取类名
     */
    private static String extractClassName(String code) {
        // 匹配 class 关键字后的类名
        Pattern pattern = Pattern.compile("(?:abstract\\s+|final\\s+|strictfp\\s+)*class\\s+([a-zA-Z_$][a-zA-Z0-9_$]*)");
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
