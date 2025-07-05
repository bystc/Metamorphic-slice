import com.example.generator.JavaCodeGenerator;
import java.util.List;

public class TestControlFlowImproved {
    public static void main(String[] args) {
        try {
            JavaCodeGenerator generator = new JavaCodeGenerator();
            
            // Test improved control flow transformation
            System.out.println("Testing improved control flow transformation...");
            
            // Generate test code
            String testCode = generateTestCodeWithSliceVariables();
            System.out.println("Original code:");
            System.out.println(testCode);
            System.out.println("---");
            
            // Apply control flow transformation
            String transformedCode = generator.transformControlFlow(testCode);
            System.out.println("Transformed code:");
            System.out.println(transformedCode);
            System.out.println("---");
            
            // Verify if transformation preserves slice point integrity
            boolean preservesSlicePoint = verifySlicePointPreservation(testCode, transformedCode);
            System.out.println("Slice point preservation: " + (preservesSlicePoint ? "PASS" : "FAIL"));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String generateTestCodeWithSliceVariables() {
        StringBuilder code = new StringBuilder();
        code.append("public class TestControlFlow {\n");
        code.append("    public static void main(String[] args) {\n");
        code.append("        int choice = 1;\n");
        code.append("        String unrelatedStr0 = \"str88\";\n");
        code.append("        int[] unrelatedArr1 = { 4, 3, 6 };\n");
        code.append("\n");
        code.append("        // This if statement doesn't affect slice point, should be transformable\n");
        code.append("        if (choice == 1) {\n");
        code.append("            System.out.println(\"Choice is 1\");\n");
        code.append("        } else {\n");
        code.append("            System.out.println(\"Choice is not 1\");\n");
        code.append("        }\n");
        code.append("\n");
        code.append("        // This loop doesn't affect slice point, should be transformable\n");
        code.append("        for (int i = 0; i < 3; i++) {\n");
        code.append("            System.out.println(\"Loop iteration: \" + i);\n");
        code.append("        }\n");
        code.append("\n");
        code.append("        // Slice-related variables\n");
        code.append("        int val1 = 0, val2 = 0, val3 = 0;\n");
        code.append("        int temp1 = 10, temp2 = 20, temp3 = 30;\n");
        code.append("\n");
        code.append("        // This if statement affects slice point, should NOT be transformed\n");
        code.append("        if (temp1 > 5) {\n");
        code.append("            val1 = temp1 * 2;\n");
        code.append("        } else {\n");
        code.append("            val1 = temp1;\n");
        code.append("        }\n");
        code.append("\n");
        code.append("        // This loop affects slice point, should NOT be transformed\n");
        code.append("        for (int j = 0; j < val1; j++) {\n");
        code.append("            val2 += temp2;\n");
        code.append("        }\n");
        code.append("\n");
        code.append("        switch(choice) {\n");
        code.append("            case 0:\n");
        code.append("                val1 = temp1 * 2;\n");
        code.append("                val2 = temp2 + 5;\n");
        code.append("                val3 = temp3 - 3;\n");
        code.append("                break;\n");
        code.append("            case 1:\n");
        code.append("                val1 = temp1 + temp2;\n");
        code.append("                val2 = temp2 * temp3;\n");
        code.append("                val3 = temp3 / temp1;\n");
        code.append("                break;\n");
        code.append("            case 2:\n");
        code.append("                val1 = temp1 - temp2;\n");
        code.append("                val2 = temp2 / temp3;\n");
        code.append("                val3 = temp3 * temp1;\n");
        code.append("                break;\n");
        code.append("            default:\n");
        code.append("                val1 = temp1;\n");
        code.append("                val2 = temp2;\n");
        code.append("                val3 = temp3;\n");
        code.append("                break;\n");
        code.append("        }\n");
        code.append("\n");
        code.append("        int result1 = val1 + val2;\n");
        code.append("        int result2 = val2 + val3;\n");
        code.append("        int result3 = val1 + val3;\n");
        code.append("\n");
        code.append("        System.out.println(val1);\n");
        code.append("        System.out.println(val2);\n");
        code.append("        System.out.println(val3);\n");
        code.append("        System.out.println(result1);\n");
        code.append("        System.out.println(result2);\n");
        code.append("        System.out.println(result3);\n");
        code.append("    }\n");
        code.append("}\n");
        return code.toString();
    }
    
    private static boolean verifySlicePointPreservation(String originalCode, String transformedCode) {
        // Check if slice-related variables exist in both codes
        String[] sliceVariables = {"val1", "val2", "val3", "temp1", "temp2", "temp3", "result1", "result2", "result3"};
        
        for (String var : sliceVariables) {
            if (!originalCode.contains(var) || !transformedCode.contains(var)) {
                System.out.println("Missing slice variable: " + var);
                return false;
            }
        }
        
        // Check if key computation logic remains unchanged
        String[] keyOperations = {
            "val1 = temp1", "val2 = temp2", "val3 = temp3",
            "result1 = val1 + val2", "result2 = val2 + val3", "result3 = val1 + val3"
        };
        
        for (String operation : keyOperations) {
            if (!transformedCode.contains(operation.replace(" ", ""))) {
                System.out.println("Missing key operation: " + operation);
                return false;
            }
        }
        
        return true;
    }
} 