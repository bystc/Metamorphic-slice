import com.example.generator.JavaCodeGenerator;
import com.example.generator.JSmithCodeGenerator;
import java.util.List;

/**
 * 测试JSmith集成的变量重命名蜕变关系完整流程
 */
public class TestJSmithVariableRenameIntegration {
    
    public static void main(String[] args) {
        System.out.println("=== JSmith变量重命名蜕变关系集成测试 ===\n");
        
        try {
            // 1. 创建生成器实例
            JSmithCodeGenerator jsmithGenerator = new JSmithCodeGenerator();
            JavaCodeGenerator javaGenerator = new JavaCodeGenerator();
            
            System.out.println("1. 测试JSmith代码生成");
            String jsmithCode = jsmithGenerator.generateRandomJavaClass();
            System.out.println("JSmith生成的代码长度: " + jsmithCode.length() + " 字符");
            System.out.println("代码预览:");
            System.out.println(jsmithCode.substring(0, Math.min(300, jsmithCode.length())) + "...\n");
            
            // 2. 测试JSmith变量重命名文件对生成
            System.out.println("2. 测试JSmith变量重命名文件对生成");
            List<String> generatedFiles = javaGenerator.generateJSmithVariableRenameTestFiles(2);
            System.out.println("生成的文件数量: " + generatedFiles.size());
            
            for (String file : generatedFiles) {
                System.out.println("生成的文件: " + file);
                
                // 读取并显示文件内容预览
                try {
                    String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(file)));
                    System.out.println("文件大小: " + content.length() + " 字符");
                    System.out.println("内容预览:");
                    System.out.println(content.substring(0, Math.min(200, content.length())) + "...");
                    System.out.println();
                } catch (Exception e) {
                    System.out.println("读取文件失败: " + e.getMessage());
                }
            }
            
            // 3. 验证文件对的对应关系
            System.out.println("3. 验证文件对的对应关系");
            List<String> mutatedFiles = generatedFiles.stream()
                .filter(file -> file.contains("mutated") && file.contains("JSmith"))
                .collect(java.util.stream.Collectors.toList());
                
            for (String mutatedFile : mutatedFiles) {
                String renamedFile = mutatedFile.replace("mutated", "renamed").replace("JSmith_mutated_", "JSmith_renamed_");
                
                if (java.nio.file.Files.exists(java.nio.file.Paths.get(renamedFile))) {
                    System.out.println("✅ 文件对匹配:");
                    System.out.println("  原始文件: " + mutatedFile);
                    System.out.println("  重命名文件: " + renamedFile);
                    
                    // 比较文件内容差异
                    try {
                        String originalContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(mutatedFile)));
                        String renamedContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(renamedFile)));
                        
                        System.out.println("  原始文件大小: " + originalContent.length() + " 字符");
                        System.out.println("  重命名文件大小: " + renamedContent.length() + " 字符");
                        
                        // 简单检查是否有变量被重命名
                        boolean hasChanges = !originalContent.equals(renamedContent);
                        System.out.println("  文件内容是否有变化: " + (hasChanges ? "是" : "否"));
                        
                    } catch (Exception e) {
                        System.out.println("  ❌ 读取文件内容失败: " + e.getMessage());
                    }
                } else {
                    System.out.println("❌ 缺少对应的重命名文件: " + renamedFile);
                }
                System.out.println();
            }
            
            // 4. 测试变量映射功能
            System.out.println("4. 测试变量映射功能");
            for (String mutatedFile : mutatedFiles) {
                String fileName = java.nio.file.Paths.get(mutatedFile).getFileName().toString();
                try {
                    java.util.Map<String, String> variableMapping = javaGenerator.getVariableMapping(fileName);
                    if (variableMapping != null && !variableMapping.isEmpty()) {
                        System.out.println("文件 " + fileName + " 的变量映射:");
                        for (java.util.Map.Entry<String, String> entry : variableMapping.entrySet()) {
                            System.out.println("  " + entry.getKey() + " -> " + entry.getValue());
                        }
                    } else {
                        System.out.println("文件 " + fileName + " 没有变量映射信息");
                    }
                } catch (Exception e) {
                    System.out.println("获取变量映射失败: " + e.getMessage());
                }
                System.out.println();
            }
            
            System.out.println("✅ JSmith变量重命名蜕变关系集成测试完成!");
            
        } catch (Exception e) {
            System.err.println("❌ 测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 