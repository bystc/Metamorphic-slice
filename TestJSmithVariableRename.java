import com.example.generator.JSmithCodeGenerator;
import com.example.generator.JavaCodeGenerator;

/**
 * 测试JSmith集成和变量重命名功能
 */
public class TestJSmithVariableRename {
    
    public static void main(String[] args) {
        System.out.println("=== 测试JSmith集成和变量重命名功能 ===\n");
        
        try {
            // 1. 测试JSmith代码生成器
            System.out.println("1. 测试JSmith代码生成器");
            JSmithCodeGenerator jsmithGenerator = new JSmithCodeGenerator();
            
            String jsmithCode = jsmithGenerator.generateRandomJavaClass();
            System.out.println("JSmith生成的代码:");
            System.out.println(jsmithCode);
            System.out.println("\n" + "=".repeat(50) + "\n");
            
            // 2. 测试集成后的JavaCodeGenerator
            System.out.println("2. 测试集成后的JavaCodeGenerator");
            JavaCodeGenerator javaGenerator = new JavaCodeGenerator();
            
            String integratedCode = javaGenerator.generateRandomJavaClass();
            System.out.println("集成后生成的代码:");
            System.out.println(integratedCode);
            System.out.println("\n" + "=".repeat(50) + "\n");
            
            // 3. 测试变量重命名功能
            System.out.println("3. 测试变量重命名功能");
            
            // 生成一些测试文件
            System.out.println("正在生成测试文件...");
            javaGenerator.generateMutatedFiles("test", 2);
            
            System.out.println("测试完成！请检查生成的文件。");
            
        } catch (Exception e) {
            System.err.println("测试过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 