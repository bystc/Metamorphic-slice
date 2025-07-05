import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestDataFlowFix {
    public static void main(String[] args) {
        try {
            // Check original file
            String originalContent = Files.readString(Paths.get("mutated/Example_original_0.java"));
            System.out.println("=== Original file content ===");
            System.out.println(originalContent);
            
            // Check dataflow transformed file (if exists)
            if (Files.exists(Paths.get("dataflow/Example_dataflow_0.java"))) {
                String dataflowContent = Files.readString(Paths.get("dataflow/Example_dataflow_0.java"));
                System.out.println("\n=== Dataflow transformed file content ===");
                System.out.println(dataflowContent);
                
                // Check choice value
                if (originalContent.contains("int choice = 0") && dataflowContent.contains("int choice = 0")) {
                    System.out.println("\n[SUCCESS] Fix successful: choice value unchanged");
                } else {
                    System.out.println("\n[FAILED] Fix failed: choice value was modified");
                }
            } else {
                System.out.println("\nDataflow transformed file not generated yet");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 