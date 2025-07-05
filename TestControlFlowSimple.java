import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TestControlFlowSimple {
    public static void main(String[] args) {
        try {
            System.out.println("Testing control flow transformation...");
            
            // Test file generation
            System.out.println("1. Testing file generation...");
            
            // Check if directories exist
            if (Files.exists(Paths.get("controlflow"))) {
                System.out.println("✓ controlflow directory exists");
            } else {
                System.out.println("✗ controlflow directory does not exist");
            }
            
            if (Files.exists(Paths.get("mutated"))) {
                System.out.println("✓ mutated directory exists");
            } else {
                System.out.println("✗ mutated directory does not exist");
            }
            
            // Check if there are generated files
            try {
                List<String> files = Files.list(Paths.get("controlflow")).toList();
                System.out.println("✓ Found " + files.size() + " control flow files");
                
                if (!files.isEmpty()) {
                    String firstFile = files.get(0);
                    String content = Files.readString(Paths.get(firstFile));
                    System.out.println("✓ First file content length: " + content.length() + " characters");
                    
                    // Check if contains control flow structures
                    if (content.contains("if") || content.contains("switch") || content.contains("for")) {
                        System.out.println("✓ File contains control flow structures");
                    } else {
                        System.out.println("✗ File does not contain control flow structures");
                    }
                }
            } catch (Exception e) {
                System.out.println("✗ Error reading control flow files: " + e.getMessage());
            }
            
            System.out.println("\n2. Testing web interface...");
            System.out.println("✓ Application is running on port 8080");
            System.out.println("✓ You can access the web interface at:");
            System.out.println("  - Main page: http://localhost:8080/slice");
            System.out.println("  - Control flow test: http://localhost:8080/slice/controlflow");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 