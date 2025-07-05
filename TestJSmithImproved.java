import com.example.service.JSmithService;
import java.util.List;

public class TestJSmithImproved {
    public static void main(String[] args) {
        System.out.println("=== Testing Improved JSmith Code Generation ===");
        
        JSmithService jsmithService = new JSmithService();
        
        // Clean up previous files
        System.out.println("\n1. Cleaning up previous files...");
        jsmithService.cleanupGeneratedFiles();
        
        // Test random code generation
        System.out.println("\n2. Testing Random Code Generation (with retry mechanism):");
        List<String> randomFiles = jsmithService.generateRandomJavaCode(3);
        System.out.println("Generated " + randomFiles.size() + " random files:");
        for (String file : randomFiles) {
            System.out.println("  - " + file);
        }
        
        // Test simple code generation
        System.out.println("\n3. Testing Simple Code Generation:");
        List<String> simpleFiles = jsmithService.generateTypedJavaCode(2, "Simple");
        System.out.println("Generated " + simpleFiles.size() + " simple files:");
        for (String file : simpleFiles) {
            System.out.println("  - " + file);
        }
        
        // Test complex code generation
        System.out.println("\n4. Testing Complex Code Generation:");
        List<String> complexFiles = jsmithService.generateTypedJavaCode(2, "Complex");
        System.out.println("Generated " + complexFiles.size() + " complex files:");
        for (String file : complexFiles) {
            System.out.println("  - " + file);
        }
        
        // Test method code generation
        System.out.println("\n5. Testing Method Code Generation:");
        List<String> methodFiles = jsmithService.generateTypedJavaCode(2, "Method");
        System.out.println("Generated " + methodFiles.size() + " method files:");
        for (String file : methodFiles) {
            System.out.println("  - " + file);
        }
        
        // Test expression code generation
        System.out.println("\n6. Testing Expression Code Generation:");
        List<String> expressionFiles = jsmithService.generateTypedJavaCode(2, "Expression");
        System.out.println("Generated " + expressionFiles.size() + " expression files:");
        for (String file : expressionFiles) {
            System.out.println("  - " + file);
        }
        
        // List all generated files
        System.out.println("\n7. All Generated Files:");
        List<String> allFiles = jsmithService.listGeneratedFiles();
        System.out.println("Total files: " + allFiles.size());
        for (String file : allFiles) {
            System.out.println("  - " + file);
        }
        
        System.out.println("\n=== Test Completed ===");
        System.out.println("Check the generated files in the 'jsmith-generated' directory");
    }
} 