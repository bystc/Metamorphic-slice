import com.example.generator.JavaCodeGenerator;
import java.util.List;

public class TestReorderFunction {
    public static void main(String[] args) {
        try {
            JavaCodeGenerator generator = new JavaCodeGenerator();
            
            // Test statement reorder file generation
            System.out.println("Testing statement reorder file generation...");
            List<String> files = generator.generateStatementReorderFiles("", 3);
            
            System.out.println("Generated " + files.size() + " original files");
            
            // Check generated files
            for (String file : files) {
                System.out.println("Original file: " + file);
                String reorderedFile = file.replace("mutated", "reordered").replace("_original_", "_reordered_");
                System.out.println("Reordered file: " + reorderedFile);
                System.out.println("---");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 