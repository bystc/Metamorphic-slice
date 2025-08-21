import com.example.generator.JSmithCodeGenerator;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 使用JSmith批量生成复杂的Java文件
 */
public class TestJSmithBatch {
    
    public static void main(String[] args) {
        System.out.println("=== JSmith批量生成复杂Java文件 ===\n");
        
        int count = 5; // 生成5个文件
        String outputDir = "jsmith-generated";
        
        try {
            // 创建输出目录
            Files.createDirectories(Paths.get(outputDir));
            
            JSmithCodeGenerator generator = new JSmithCodeGenerator();
            
            System.out.println("正在生成 " + count + " 个复杂的Java文件...\n");
            
            for (int i = 0; i < count; i++) {
                try {
                    // 使用不同的种子生成不同的代码
                    long seed = System.currentTimeMillis() + i * 1000;
                    String code = generator.generateRandomJavaClass(seed);
                    
                    // 提取类名或使用默认名称
                    String className = extractClassName(code);
                    if (className == null) {
                        className = "GeneratedClass" + (i + 1);
                    }
                    
                    // 生成文件名
                    String fileName = className + "_" + String.format("%03d", i + 1) + ".java";
                    String filePath = outputDir + "/" + fileName;
                    
                    // 写入文件
                    try (FileWriter writer = new FileWriter(filePath)) {
                        writer.write(code);
                    }
                    
                    long fileSize = Files.size(Paths.get(filePath));
                    System.out.printf("[%d/%d] 生成: %s (%d 字节)%n", 
                        i + 1, count, fileName, fileSize);
                    
                    // 显示代码预览
                    System.out.println("代码预览:");
                    String preview = code.length() > 300 ? code.substring(0, 300) + "..." : code;
                    System.out.println(preview);
                    System.out.println("\n" + "=".repeat(50) + "\n");
                    
                    // 短暂延迟
                    Thread.sleep(100);
                    
                } catch (Exception e) {
                    System.err.printf("[%d/%d] 生成失败: %s%n", 
                        i + 1, count, e.getMessage());
                }
            }
            
            System.out.println("✅ 批量生成完成！");
            System.out.println("输出目录: " + Paths.get(outputDir).toAbsolutePath());
            
            // 列出生成的文件
            System.out.println("\n生成的文件:");
            Files.list(Paths.get(outputDir))
                .filter(path -> path.toString().endsWith(".java"))
                .forEach(path -> {
                    try {
                        long size = Files.size(path);
                        System.out.println("- " + path.getFileName() + " (" + size + " 字节)");
                    } catch (IOException e) {
                        System.out.println("- " + path.getFileName() + " (无法获取大小)");
                    }
                });
                
        } catch (Exception e) {
            System.err.println("批量生成过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 从生成的Java代码中提取类名
     */
    private static String extractClassName(String code) {
        // 匹配 class 关键字后的类名
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
            "(?:abstract\\s+|final\\s+|strictfp\\s+)*class\\s+([a-zA-Z_$][a-zA-Z0-9_$]*)"
        );
        java.util.regex.Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
} 