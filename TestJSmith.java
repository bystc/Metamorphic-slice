import com.example.service.JSmithService;
import java.util.List;

public class TestJSmith {
    public static void main(String[] args) {
        System.out.println("Testing JSmith Code Generation...");
        
        JSmithService jsmithService = new JSmithService();
        
        try {
            // Test random code generation
            System.out.println("\n1. Testing Random Code Generation...");
            List<String> randomFiles = jsmithService.generateRandomJavaCode(3);
            System.out.println("Generated " + randomFiles.size() + " random files:");
            randomFiles.forEach(file -> System.out.println("  - " + file));
            
            // Test complex code generation
            System.out.println("\n2. Testing Complex Code Generation...");
            List<String> complexFiles = jsmithService.generateTypedJavaCode(2, "complex");
            System.out.println("Generated " + complexFiles.size() + " complex files:");
            complexFiles.forEach(file -> System.out.println("  - " + file));
            
            // Test simple code generation
            System.out.println("\n3. Testing Simple Code Generation...");
            List<String> simpleFiles = jsmithService.generateTypedJavaCode(2, "simple");
            System.out.println("Generated " + simpleFiles.size() + " simple files:");
            simpleFiles.forEach(file -> System.out.println("  - " + file));
            
            // Test method code generation
            System.out.println("\n4. Testing Method Code Generation...");
            List<String> methodFiles = jsmithService.generateTypedJavaCode(2, "method");
            System.out.println("Generated " + methodFiles.size() + " method files:");
            methodFiles.forEach(file -> System.out.println("  - " + file));
            
            // Test expression code generation
            System.out.println("\n5. Testing Expression Code Generation...");
            List<String> expressionFiles = jsmithService.generateTypedJavaCode(2, "expression");
            System.out.println("Generated " + expressionFiles.size() + " expression files:");
            expressionFiles.forEach(file -> System.out.println("  - " + file));
            
            // List all generated files
            System.out.println("\n6. Listing All Generated Files...");
            List<String> allFiles = jsmithService.listGeneratedFiles();
            System.out.println("Total files: " + allFiles.size());
            allFiles.forEach(file -> System.out.println("  - " + file));
            
            // Get generated directory
            System.out.println("\n7. Generated Directory: " + jsmithService.getGeneratedDirectory());
            
            System.out.println("\nSUCCESS: JSmith testing completed successfully!");
            
        } catch (Exception e) {
            System.err.println("ERROR: Error during JSmith testing: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 