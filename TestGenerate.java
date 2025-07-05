import com.example.generator.JavaCodeGenerator;

public class TestGenerate {
    public static void main(String[] args) {
        try {
            JavaCodeGenerator generator = new JavaCodeGenerator();
            
            // Test generating a Java class
            System.out.println("=== Testing Java class generation ===");
            String generatedCode = generator.generateRandomJavaClass();
            System.out.println("Generated code:");
            System.out.println(generatedCode);
            
            // Check if contains unrelated statements
            if (generatedCode.contains("unrelated")) {
                System.out.println("Contains unrelated statements");
            } else {
                System.out.println("No unrelated statements found");
            }
            
            // Check if contains switch statement
            if (generatedCode.contains("switch")) {
                System.out.println("Contains switch statement");
            } else {
                System.out.println("No switch statement found");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 