import com.example.generator.JavaCodeGenerator;
import com.example.generator.JavaCodeGenerator.VariableInfo;
import java.util.List;

public class TestDeadCodeVariable {
    public static void main(String[] args) {
        try {
            JavaCodeGenerator generator = new JavaCodeGenerator();
            
            // 生成死代码文件
            System.out.println("=== Generating dead code files ===");
            List<String> files = generator.generateDeadCodeFiles("", 1);
            
            if (!files.isEmpty()) {
                String originalFile = files.get(0);
                System.out.println("Original file: " + originalFile);
                
                // 检查原始文件选择的变量
                VariableInfo originalVarInfo = generator.findVariableForSlicing(originalFile);
                if (originalVarInfo != null) {
                    System.out.println("Selected variable in original: " + originalVarInfo.getVariableName() + " at line " + originalVarInfo.getLineNumber());
                }
                
                // 检查死代码文件
                String deadCodeFile = originalFile.replace("mutated", "deadcode").replace("_mutated_", "_deadcode_");
                System.out.println("Dead code file: " + deadCodeFile);
                
                // 检查死代码文件选择的变量
                VariableInfo deadCodeVarInfo = generator.findVariableForSlicing(deadCodeFile);
                if (deadCodeVarInfo != null) {
                    System.out.println("Selected variable in dead code: " + deadCodeVarInfo.getVariableName() + " at line " + deadCodeVarInfo.getLineNumber());
                }
                
                // 验证是否使用相同的变量
                if (originalVarInfo != null && deadCodeVarInfo != null) {
                    boolean sameVariable = originalVarInfo.getVariableName().equals(deadCodeVarInfo.getVariableName());
                    System.out.println("Using same variable: " + sameVariable);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 