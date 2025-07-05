import com.example.generator.JavaCodeGenerator;
import java.util.List;

public class TestControlFlowTransformation {
    public static void main(String[] args) {
        try {
            JavaCodeGenerator generator = new JavaCodeGenerator();
            
            System.out.println("Testing improved control flow transformation...");
            
            // Generate original code
            String originalCode = generator.generateRandomJavaClass();
            System.out.println("=== Original Code ===");
            System.out.println(originalCode);
            System.out.println();
            
            // Apply control flow transformation
            String transformedCode = generator.transformControlFlow(originalCode);
            System.out.println("=== Transformed Code ===");
            System.out.println(transformedCode);
            System.out.println();
            
            // Check if there are changes
            if (!originalCode.equals(transformedCode)) {
                System.out.println("SUCCESS: Control flow transformation successful!");
                System.out.println("SUCCESS: The code has been modified.");
                
                // Check if contains loop structures
                if (transformedCode.contains("for") || transformedCode.contains("while")) {
                    System.out.println("SUCCESS: Transformed code contains loop structures.");
                }
            } else {
                System.out.println("FAILED: No transformation applied.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 