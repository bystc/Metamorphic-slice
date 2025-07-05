import com.example.generator.JavaCodeGenerator;
import com.example.generator.JavaCodeGenerator.VariableInfo;
import com.example.SliceExecutor;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Test data dependency metamorphic relation
 * Verify: Modifying variable assignments or data operations unrelated to slice points 
 * should not change slice results
 */
public class TestDataFlow {
    
    public static void main(String[] args) {
        System.out.println("=== Testing Data Dependency Metamorphic Relation ===");
        
        JavaCodeGenerator generator = new JavaCodeGenerator();
        SliceExecutor sliceExecutor = new SliceExecutor();
        
        try {
            // Generate a test file
            String originalContent = generator.generateRandomJavaClass();
            System.out.println("Generated original code:");
            System.out.println(originalContent);
            System.out.println();
            
            // Perform data flow transformation
            String dataFlowContent = generator.transformDataFlow(originalContent);
            System.out.println("Data flow transformed code:");
            System.out.println(dataFlowContent);
            System.out.println();
            
            // Check if files are different
            if (!dataFlowContent.equals(originalContent)) {
                System.out.println("OK: Data flow transformation successful, file content changed");
            } else {
                System.out.println("WARNING: Data flow transformation resulted in identical files");
            }
            
            // Save files for slicing test
            java.nio.file.Files.write(java.nio.file.Paths.get("TestDataFlow_original.java"), 
                                    originalContent.getBytes());
            java.nio.file.Files.write(java.nio.file.Paths.get("TestDataFlow_dataflow.java"), 
                                    dataFlowContent.getBytes());
            
            // Slice original file
            VariableInfo originalVarInfo = generator.findVariableForSlicing("TestDataFlow_original.java");
            if (originalVarInfo != null) {
                System.out.println("Original file slice variable: " + originalVarInfo.getVariableName() + 
                                 " at line " + originalVarInfo.getLineNumber());
                
                String originalSlice = sliceExecutor.executeSliceWithVariable(
                    "TestDataFlow_original.java", 
                    originalVarInfo.getVariableName(), 
                    originalVarInfo.getLineNumber()
                );
                System.out.println("Original file slice result:");
                System.out.println(originalSlice);
                System.out.println();
                
                // Slice transformed file
                VariableInfo dataFlowVarInfo = generator.findVariableLastAssignment(
                    "TestDataFlow_dataflow.java", 
                    originalVarInfo.getVariableName()
                );
                if (dataFlowVarInfo != null) {
                    System.out.println("Data flow file slice variable: " + dataFlowVarInfo.getVariableName() + 
                                     " at line " + dataFlowVarInfo.getLineNumber());
                    
                    String dataFlowSlice = sliceExecutor.executeSliceWithVariable(
                        "TestDataFlow_dataflow.java", 
                        dataFlowVarInfo.getVariableName(), 
                        dataFlowVarInfo.getLineNumber()
                    );
                    System.out.println("Data flow file slice result:");
                    System.out.println(dataFlowSlice);
                    System.out.println();
                    
                    // Compare slice results
                    boolean isEquivalent = compareSlices(originalSlice, dataFlowSlice);
                    if (isEquivalent) {
                        System.out.println("OK: Slice results are equivalent - Data dependency metamorphic relation verified!");
                    } else {
                        System.out.println("ERROR: Slice results are not equivalent - Data dependency metamorphic relation failed!");
                    }
                }
            }
            
            // Test batch generation
            System.out.println("\n=== Testing Batch Generation of Data Flow Files ===");
            List<String> originalFiles = generator.generateDataFlowFiles("", 3);
            System.out.println("Generated " + originalFiles.size() + " original files");
            
            for (String originalFile : originalFiles) {
                String dataFlowFile = originalFile.replace("mutated", "dataflow").replace("_original_", "_dataflow_");
                System.out.println("Original file: " + originalFile);
                System.out.println("Data flow file: " + dataFlowFile);
                
                // Check if files exist and content is different
                if (java.nio.file.Files.exists(java.nio.file.Paths.get(dataFlowFile))) {
                    String originalContent2 = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(originalFile)));
                    String dataFlowContent2 = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(dataFlowFile)));
                    
                    if (!originalContent2.equals(dataFlowContent2)) {
                        System.out.println("OK: File pair generated successfully, content different");
                    } else {
                        System.out.println("WARNING: File pair generated successfully, but content identical");
                    }
                } else {
                    System.out.println("ERROR: Data flow file does not exist");
                }
                System.out.println();
            }
            
        } catch (Exception e) {
            System.err.println("Error during testing: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Compare if two slice results are equivalent
     */
    private static boolean compareSlices(String slice1, String slice2) {
        if (slice1 == null || slice2 == null) {
            return slice1 == slice2;
        }
        
        // Simple string comparison, in practice may need more complex semantic comparison
        return slice1.trim().equals(slice2.trim());
    }
} 