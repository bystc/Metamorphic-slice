import com.example.generator.JavaCodeGenerator;
import java.util.List;

public class TestControlFlow {
    public static void main(String[] args) {
        try {
            JavaCodeGenerator generator = new JavaCodeGenerator();
            
            // Test control flow transformation file generation
            System.out.println("Testing control flow transformation file generation...");
            List<String> files = generator.generateControlFlowFiles("", 3);
            
            System.out.println("Generated " + files.size() + " original files");
            
            // Check generated files
            for (String file : files) {
                System.out.println("Original file: " + file);
                String controlFlowFile = file.replace("mutated", "controlflow").replace("_original_", "_controlflow_");
                System.out.println("Control flow file: " + controlFlowFile);
                System.out.println("---");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 