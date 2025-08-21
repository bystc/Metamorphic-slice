import java.nio.file.Path;
import java.util.List;

/**
 * Example demonstrating how to use GrammarFileBasedGenerator.
 * This shows the alternative approach to generating Java code with main methods.
 * 
 * @since 0.2
 */
public class GrammarFileBasedExample {
    
    /**
     * Main method demonstrating GrammarFileBasedGenerator usage.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // Create a GrammarFileBasedGenerator instance
        GrammarFileBasedGenerator generator = new GrammarFileBasedGenerator();
        
        // Optionally load a grammar file (if you have one)
        // generator.loadGrammar("src/main/resources/grammars/Java8ReducedParser.g4");
        
        System.out.println("=== GrammarFileBasedGenerator Demo ===\n");
        
        // Example 1: Generate a simple class with main method
        System.out.println("1. Generating simple class with main method:");
        System.out.println("--------------------------------------------");
        String simpleCode = generator.generateMainClassWithMainMethod("SimpleExample");
        System.out.println(simpleCode);
        
        // Example 2: Generate a complex class with control flow
        System.out.println("\n2. Generating complex class with control flow:");
        System.out.println("-----------------------------------------------");
        String complexCode = generator.generateComplexJavaClass("ComplexExample");
        System.out.println(complexCode);
        
        // Example 3: Generate batch files
        System.out.println("\n3. Generating batch files:");
        System.out.println("---------------------------");
        List<Path> files = generator.generateBatchJavaFiles("output", 3, "BatchClass");
        System.out.println("Generated " + files.size() + " files:");
        for (Path file : files) {
            System.out.println("  - " + file);
        }
        
        // Example 4: Generate a single complex file
        System.out.println("\n4. Generating single complex file:");
        System.out.println("-----------------------------------");
        Path singleFile = generator.generateComplexJavaFile("SingleExample", "output/SingleExample.java");
        System.out.println("Generated file: " + singleFile);
        
        // Example 5: Using with specific seed for reproducible results
        System.out.println("\n5. Using specific seed for reproducible results:");
        System.out.println("------------------------------------------------");
        GrammarFileBasedGenerator seededGenerator = new GrammarFileBasedGenerator(12345L);
        String seededCode = seededGenerator.generateMainClassWithMainMethod("SeededExample");
        System.out.println(seededCode);
        
        System.out.println("\n=== Demo Complete ===");
    }
    
    /**
     * Example showing comparison between different generation approaches.
     */
    public static void comparisonExample() {
        System.out.println("=== Comparison Example ===\n");
        
        // Approach 1: Using GrammarFileBasedGenerator
        System.out.println("Approach 1: GrammarFileBasedGenerator");
        System.out.println("--------------------------------------");
        GrammarFileBasedGenerator grammarGenerator = new GrammarFileBasedGenerator();
        String grammarCode = grammarGenerator.generateMainClassWithMainMethod("GrammarExample");
        System.out.println(grammarCode);
        
        // Approach 2: Using enhanced RandomJavaClass (from jsmith)
        System.out.println("\nApproach 2: Enhanced RandomJavaClass (jsmith)");
        System.out.println("-----------------------------------------------");
        try {
            // Note: This requires the jsmith classes to be available
            // com.github.lombrozo.jsmith.RandomJavaClass jsmithClass = 
            //     new com.github.lombrozo.jsmith.RandomJavaClass();
            // String jsmithCode = jsmithClass.generateWithMainMethod();
            // System.out.println(jsmithCode);
            System.out.println("(jsmith approach requires jsmith classes to be available)");
        } catch (Exception e) {
            System.out.println("jsmith classes not available: " + e.getMessage());
        }
    }
} 