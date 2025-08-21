import com.example.generator.JavaCodeGenerator;
import com.example.generator.JSmithCodeGenerator;
import java.util.List;

/**
 * 简单测试JSmith变量重命名功能
 */
public class TestJSmithVariableRenameSimple {
    
    public static void main(String[] args) {
        System.out.println("=== 简单JSmith变量重命名测试 ===\n");
        
        try {
            // 创建生成器实例（不使用Spring依赖注入）
            JSmithCodeGenerator jsmithGenerator = new JSmithCodeGenerator();
            
            System.out.println("1. 测试JSmith代码生成");
            String jsmithCode = jsmithGenerator.generateRandomJavaClass();
            System.out.println("JSmith生成的代码长度: " + jsmithCode.length() + " 字符");
            System.out.println("代码预览:");
            System.out.println(jsmithCode.substring(0, Math.min(500, jsmithCode.length())) + "...\n");
            
            // 分析生成的代码中的变量
            System.out.println("2. 分析生成代码中的变量");
            String[] lines = jsmithCode.split("\n");
            int variableCount = 0;
            for (String line : lines) {
                if (line.trim().matches(".*\\b(boolean|int|long|double|float|String)\\s+\\w+.*")) {
                    variableCount++;
                    if (variableCount <= 5) { // 只显示前5个变量声明
                        System.out.println("  变量声明: " + line.trim());
                    }
                }
            }
            System.out.println("  总共找到约 " + variableCount + " 个变量声明\n");
            
            System.out.println("3. 测试多个JSmith代码生成");
            for (int i = 0; i < 3; i++) {
                String code = jsmithGenerator.generateRandomJavaClass(System.currentTimeMillis() + i * 1000);
                System.out.println("第" + (i + 1) + "个代码长度: " + code.length() + " 字符");
                
                // 提取类名
                String className = extractClassName(code);
                if (className != null) {
                    System.out.println("  类名: " + className);
                }
            }
            
            System.out.println("\n✅ JSmith代码生成测试完成!");
            
        } catch (Exception e) {
            System.err.println("❌ 测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String extractClassName(String code) {
        String[] lines = code.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("class ") || line.startsWith("public class ") || 
                line.startsWith("abstract class ") || line.startsWith("final class ")) {
                String[] parts = line.split("\\s+");
                for (int i = 0; i < parts.length - 1; i++) {
                    if ("class".equals(parts[i])) {
                        String className = parts[i + 1];
                        // 移除可能的大括号
                        if (className.endsWith("{")) {
                            className = className.substring(0, className.length() - 1);
                        }
                        return className;
                    }
                }
            }
        }
        return null;
    }
} 