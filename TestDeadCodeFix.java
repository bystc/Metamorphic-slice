import com.example.generator.JavaCodeGenerator;
import java.util.List;

public class TestDeadCodeFix {
    public static void main(String[] args) {
        try {
            JavaCodeGenerator generator = new JavaCodeGenerator();
            
            // 生成死代码文件
            System.out.println("=== Generating dead code files ===");
            List<String> files = generator.generateDeadCodeFiles("", 1);
            
            if (!files.isEmpty()) {
                System.out.println("Generated files: " + files);
                
                // 检查死代码文件内容
                String deadCodeFile = files.get(0).replace("mutated", "deadcode").replace("_mutated_", "_deadcode_");
                System.out.println("Dead code file: " + deadCodeFile);
                
                // 读取死代码文件内容
                java.nio.file.Path path = java.nio.file.Paths.get(deadCodeFile);
                if (java.nio.file.Files.exists(path)) {
                    String content = java.nio.file.Files.readString(path);
                    System.out.println("Dead code content:");
                    System.out.println(content);
                    
                    // 检查是否包含切片变量
                    if (content.contains("res1") || content.contains("res2") || content.contains("res3")) {
                        System.out.println("✓ Dead code contains slice variables");
                    } else {
                        System.out.println("✗ Dead code does not contain slice variables");
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 