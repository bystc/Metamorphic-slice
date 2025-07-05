import com.example.service.JSmithService;
import java.util.List;

public class TestJSmithQuality {
    public static void main(String[] args) {
        System.out.println("=== Testing Improved JSmith Code Quality ===");
        
        JSmithService jsmithService = new JSmithService();
        
        // Clean up previous files
        System.out.println("\n1. Cleaning up previous files...");
        jsmithService.cleanupGeneratedFiles();
        
        // Test improved random code generation
        System.out.println("\n2. Testing Improved Random Code Generation:");
        List<String> randomFiles = jsmithService.generateRandomJavaCode(5);
        System.out.println("Generated " + randomFiles.size() + " random files:");
        for (String file : randomFiles) {
            System.out.println("  - " + file);
        }
        
        // List all generated files
        System.out.println("\n3. All Generated Files:");
        List<String> allFiles = jsmithService.listGeneratedFiles();
        System.out.println("Total files: " + allFiles.size());
        for (String file : allFiles) {
            System.out.println("  - " + file);
        }
        
        System.out.println("\n=== Test Completed ===");
        System.out.println("Check the generated files in the 'jsmith-generated' directory");
        System.out.println("Files with better quality should have fewer $ symbols and more meaningful code");
    }
} 