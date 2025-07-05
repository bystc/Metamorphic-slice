import com.example.generator.JavaCodeGenerator;
import com.example.generator.JavaCodeGenerator.VariableInfo;

public class TestVariableLineFinder {
    public static void main(String[] args) {
        try {
            JavaCodeGenerator generator = new JavaCodeGenerator();
            
            // Test finding variable line number in control flow transformation file
            String controlFlowFile = "controlflow/Example_controlflow_0.java";
            String targetVariable = "val1";
            
            System.out.println("=== Testing Variable Line Finder ===");
            System.out.println("File: " + controlFlowFile);
            System.out.println("Target variable: " + targetVariable);
            System.out.println();
            
            // Method 1: Find all occurrences of variable
            VariableInfo varInfo1 = generator.findVariableLineNumber(controlFlowFile, targetVariable);
            if (varInfo1 != null) {
                System.out.println("Method 1 - All occurrences:");
                System.out.println("Variable: " + varInfo1.getVariableName());
                System.out.println("Line number: " + varInfo1.getLineNumber());
            } else {
                System.out.println("Method 1 - Variable not found");
            }
            System.out.println();
            
            // Method 2: Find last assignment of variable
            VariableInfo varInfo2 = generator.findVariableLastAssignment(controlFlowFile, targetVariable);
            if (varInfo2 != null) {
                System.out.println("Method 2 - Last assignment:");
                System.out.println("Variable: " + varInfo2.getVariableName());
                System.out.println("Line number: " + varInfo2.getLineNumber());
            } else {
                System.out.println("Method 2 - Variable not found");
            }
            System.out.println();
            
            // Method 3: Generate slice command
            String sliceCommand = generator.generateSliceCommand(controlFlowFile, targetVariable);
            if (sliceCommand != null) {
                System.out.println("Method 3 - Generated slice command:");
                System.out.println(sliceCommand);
            } else {
                System.out.println("Method 3 - Could not generate slice command");
            }
            System.out.println();
            
            // Test original file
            String originalFile = "mutated/Example_original_0.java";
            System.out.println("=== Testing Original File ===");
            System.out.println("File: " + originalFile);
            System.out.println("Target variable: " + targetVariable);
            System.out.println();
            
            VariableInfo originalVarInfo = generator.findVariableLastAssignment(originalFile, targetVariable);
            if (originalVarInfo != null) {
                System.out.println("Original file - Last assignment:");
                System.out.println("Variable: " + originalVarInfo.getVariableName());
                System.out.println("Line number: " + originalVarInfo.getLineNumber());
            } else {
                System.out.println("Original file - Variable not found");
            }
            System.out.println();
            
            String originalSliceCommand = generator.generateSliceCommand(originalFile, targetVariable);
            if (originalSliceCommand != null) {
                System.out.println("Original file - Generated slice command:");
                System.out.println(originalSliceCommand);
            } else {
                System.out.println("Original file - Could not generate slice command");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 