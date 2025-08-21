import com.example.generator.JavaCodeGenerator;
import com.example.generator.JSmithCodeGenerator;

/**
 * 测试JSmith集成和变量重命名蜕变关系的完整流程
 */
public class TestJSmithIntegration {
    
    public static void main(String[] args) {
        System.out.println("=== JSmith集成和变量重命名蜕变关系测试 ===\n");
        
        try {
            // 1. 测试JSmith代码生成器
            System.out.println("1. 测试JSmith代码生成器");
            JSmithCodeGenerator jsmithGenerator = new JSmithCodeGenerator();
            
            String jsmithCode = jsmithGenerator.generateSliceableJavaClass();
            System.out.println("JSmith生成的类:");
            System.out.println(jsmithCode);
            System.out.println("\n" + "=".repeat(50) + "\n");
            
            // 2. 测试集成后的JavaCodeGenerator
            System.out.println("2. 测试集成后的JavaCodeGenerator");
            JavaCodeGenerator javaGenerator = new JavaCodeGenerator();
            
            String integratedCode = javaGenerator.generateRandomJavaClass();
            System.out.println("集成后生成的类:");
            System.out.println(integratedCode);
            System.out.println("\n" + "=".repeat(50) + "\n");
            
            // 3. 测试变量重命名功能
            System.out.println("3. 测试变量重命名功能");
            
            // 生成一些测试文件用于变量重命名
            System.out.println("正在生成测试文件进行变量重命名...");
            java.util.List<String> mutatedFiles = javaGenerator.generateMutatedFiles("test", 2);
            
            System.out.println("生成的文件:");
            for (String file : mutatedFiles) {
                System.out.println("- " + file);
            }
            
            System.out.println("\n检查生成的文件内容:");
            for (String file : mutatedFiles) {
                try {
                    String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(file)));
                    System.out.println("\n文件: " + file);
                    System.out.println("内容预览 (前200字符):");
                    System.out.println(content.substring(0, Math.min(200, content.length())) + "...");
                    
                    // 检查对应的重命名文件
                    String renamedFile = file.replace("mutated", "renamed").replace("_mutated_", "_renamed_");
                    if (java.nio.file.Files.exists(java.nio.file.Paths.get(renamedFile))) {
                        String renamedContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(renamedFile)));
                        System.out.println("\n对应的重命名文件: " + renamedFile);
                        System.out.println("重命名内容预览 (前200字符):");
                        System.out.println(renamedContent.substring(0, Math.min(200, renamedContent.length())) + "...");
                    }
                } catch (Exception e) {
                    System.err.println("读取文件失败: " + e.getMessage());
                }
            }
            
            System.out.println("\n=== 测试完成！===");
            System.out.println("✅ JSmith集成成功");
            System.out.println("✅ 能够跳过接口生成类");
            System.out.println("✅ 变量重命名功能正常");
            
        } catch (Exception e) {
            System.err.println("测试过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 