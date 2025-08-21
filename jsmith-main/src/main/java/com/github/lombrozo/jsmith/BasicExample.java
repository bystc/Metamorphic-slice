package com.github.lombrozo.jsmith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasicExample {
    public static void main(String... args) {
        try {
            // 生成随机Java代码
            RandomJavaClass clazz = new RandomJavaClass();
            String rawCode = clazz.src();

            // 格式化代码
            String code = CodeFormatter.format(rawCode);

            // 输出到控制台
            System.out.println("Generated Java code:");
            System.out.println("===================");
            System.out.println(code);
            System.out.println("===================");

            // 提取类名
            String className = extractClassName(code);
            if (className == null) {
                className = "GeneratedClass";
            }

            // 创建输出目录
            Path outputDir = Paths.get("generated");
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }

            // 生成文件名（包含时间戳避免冲突）
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = className + "_" + timestamp + ".java";
            Path filePath = outputDir.resolve(fileName);

            // 写入文件
            Files.write(filePath, code.getBytes());

            System.out.println("Code saved to: " + filePath.toAbsolutePath());
            System.out.println("File size: " + Files.size(filePath) + " bytes");

        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error generating code: " + e.getMessage());
            e.printStackTrace();
        }
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