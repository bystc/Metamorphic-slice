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
 * 批量生成随机Java类文件
 */
public class BatchGenerator {
    
    private static final String DEFAULT_OUTPUT_DIR = "generated";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    public static void main(String... args) {
        int count = 5; // 默认生成5个文件
        String outputDir = DEFAULT_OUTPUT_DIR;
        long seed = System.currentTimeMillis();
        
        // 解析命令行参数
        if (args.length > 0) {
            try {
                count = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid count: " + args[0] + ", using default: " + count);
            }
        }
        
        if (args.length > 1) {
            outputDir = args[1];
        }
        
        if (args.length > 2) {
            try {
                seed = Long.parseLong(args[2]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid seed: " + args[2] + ", using default: " + seed);
            }
        }
        
        System.out.println("Batch Java Code Generator");
        System.out.println("========================");
        System.out.println("Count: " + count);
        System.out.println("Output directory: " + outputDir);
        System.out.println("Base seed: " + seed);
        System.out.println();
        
        try {
            generateBatch(count, outputDir, seed);
        } catch (Exception e) {
            System.err.println("Error during batch generation: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 批量生成Java文件
     */
    public static void generateBatch(int count, String outputDir, long baseSeed) throws IOException {
        // 创建输出目录
        Path outputPath = Paths.get(outputDir);
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
            System.out.println("Created output directory: " + outputPath.toAbsolutePath());
        }
        
        int successCount = 0;
        int failCount = 0;
        long totalSize = 0;
        
        for (int i = 0; i < count; i++) {
            try {
                // 使用不同的种子生成不同的代码
                long currentSeed = baseSeed + i;
                RandomJavaClass clazz = new RandomJavaClass(currentSeed);
                String rawCode = clazz.src();

                // 格式化代码
                String code = CodeFormatter.format(rawCode);
                
                // 提取类名
                String className = extractClassName(code);
                if (className == null) {
                    className = "GeneratedClass" + (i + 1);
                }
                
                // 生成文件名
                String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
                String fileName = className + "_" + timestamp + "_" + String.format("%03d", i + 1) + ".java";
                Path filePath = outputPath.resolve(fileName);
                
                // 写入文件
                Files.write(filePath, code.getBytes());
                long fileSize = Files.size(filePath);
                totalSize += fileSize;
                
                System.out.printf("[%d/%d] Generated: %s (%d bytes)%n", 
                    i + 1, count, fileName, fileSize);
                
                successCount++;
                
                // 短暂延迟以确保时间戳不同
                Thread.sleep(10);
                
            } catch (Exception e) {
                System.err.printf("[%d/%d] Failed to generate file: %s%n", 
                    i + 1, count, e.getMessage());
                failCount++;
            }
        }
        
        // 输出统计信息
        System.out.println();
        System.out.println("Generation Summary:");
        System.out.println("==================");
        System.out.println("Total files requested: " + count);
        System.out.println("Successfully generated: " + successCount);
        System.out.println("Failed: " + failCount);
        System.out.println("Total size: " + totalSize + " bytes");
        System.out.println("Average size: " + (successCount > 0 ? totalSize / successCount : 0) + " bytes");
        System.out.println("Output directory: " + outputPath.toAbsolutePath());
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
