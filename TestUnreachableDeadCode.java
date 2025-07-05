import com.example.generator.JavaCodeGenerator;
import java.util.List;

public class TestUnreachableDeadCode {
    public static void main(String[] args) {
        try {
            JavaCodeGenerator generator = new JavaCodeGenerator();
            
            // Test unreachable dead code generation
            System.out.println("Testing unreachable dead code generation...");
            List<String> files = generator.generateDeadCodeFiles("", 3);
            
            System.out.println("Generated " + files.size() + " dead code files");
            
            // Check generated files
            for (String file : files) {
                System.out.println("Original file: " + file);
                String deadCodeFile = file.replace("mutated", "deadcode").replace("_mutated_", "_deadcode_");
                System.out.println("Dead code file: " + deadCodeFile);
                System.out.println("---");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 