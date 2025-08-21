/**
 * Example demonstrating the new grammar-driven GrammarFileBasedGenerator.
 * This shows how to use ANTLR grammar files to generate Java code, following JSmith's approach.
 * 
 * @since 0.3
 */
public class GrammarDrivenExample {
    
    /**
     * Main method demonstrating grammar-driven code generation.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        System.out.println("=== Grammar-Driven Code Generation Demo ===\n");
        
        // Create a grammar-driven generator
        GrammarFileBasedGenerator generator = new GrammarFileBasedGenerator(12345L);
        
        // Try to load the custom grammar file
        try {
            generator.loadGrammar("src/main/resources/grammars/JavaWithMain.g4");
            System.out.println("Custom grammar file loaded successfully!");
            System.out.println("Using ANTLR grammar-driven generation...\n");
        } catch (Exception e) {
            System.out.println("Custom grammar file not found, using built-in rules...");
            System.out.println("Using built-in grammar rules for generation...\n");
        }
        
        // Example 1: Generate a simple class using grammar rules
        System.out.println("1. Generating simple class using grammar rules:");
        System.out.println("===============================================");
        String simpleCode = generator.generateMainClassWithMainMethod("GrammarSimpleExample");
        System.out.println(simpleCode);
        
        // Example 2: Generate a complex class using grammar rules
        System.out.println("\n2. Generating complex class using grammar rules:");
        System.out.println("=================================================");
        String complexCode = generator.generateComplexJavaClass("GrammarComplexExample");
        System.out.println(complexCode);
        
        // Example 3: Generate from specific grammar rule
        System.out.println("\n3. Generating from specific grammar rule:");
        System.out.println("=========================================");
        if (generator.hasRule("statement")) {
            String statementCode = generator.generateFromRule("statement");
            System.out.println("Generated statement: " + statementCode);
        }
        
        // Example 4: Generate batch files
        System.out.println("\n4. Generating batch files using grammar rules:");
        System.out.println("===============================================");
        try {
            java.util.List<java.nio.file.Path> files = generator.generateBatchJavaFiles("output", 3, "GrammarBatchClass");
            System.out.println("Generated " + files.size() + " files:");
            for (java.nio.file.Path file : files) {
                System.out.println("  - " + file);
            }
        } catch (Exception e) {
            System.out.println("Error generating batch files: " + e.getMessage());
        }
        
        // Example 5: Demonstrate semantic analysis
        System.out.println("\n5. Semantic Analysis Features:");
        System.out.println("===============================");
        System.out.println("Variable scope management");
        System.out.println("Type checking");
        System.out.println("Unique identifier generation");
        System.out.println("Semantic annotation processing");
        
        // Example 6: Show grammar rule information
        System.out.println("\n6. Available Grammar Rules:");
        System.out.println("============================");
        String[] commonRules = {
            "compilationUnit", "packageDeclaration", "importDeclaration", 
            "classDeclaration", "mainMethod", "statement", "printStatement",
            "variableDeclaration", "forLoop", "ifStatement", "whileLoop"
        };
        
        for (String ruleName : commonRules) {
            if (generator.hasRule(ruleName)) {
                System.out.println("Available: " + ruleName);
            } else {
                System.out.println("Not available: " + ruleName);
            }
        }
        
        System.out.println("\n=== Demo Complete ===");
        System.out.println("\nKey Features of Grammar-Driven Generation:");
        System.out.println("- ANTLR grammar file parsing");
        System.out.println("- Rule-based code generation");
        System.out.println("- Semantic annotation support");
        System.out.println("- Variable scope management");
        System.out.println("- Type-safe generation");
        System.out.println("- Extensible grammar rules");
    }
    
    /**
     * Example showing comparison between different generation approaches.
     */
    public static void comparisonExample() {
        System.out.println("=== Generation Approach Comparison ===\n");
        
        // Approach 1: Grammar-driven (new approach)
        System.out.println("Approach 1: Grammar-Driven Generation");
        System.out.println("--------------------------------------");
        GrammarFileBasedGenerator grammarGenerator = new GrammarFileBasedGenerator();
        try {
            grammarGenerator.loadGrammar("src/main/resources/grammars/JavaWithMain.g4");
            String grammarCode = grammarGenerator.generateMainClassWithMainMethod("GrammarExample");
            System.out.println("Grammar-driven generation successful");
        } catch (Exception e) {
            System.out.println("Grammar-driven generation failed: " + e.getMessage());
        }
        
        // Approach 2: Template-based (old approach)
        System.out.println("\nApproach 2: Template-Based Generation");
        System.out.println("--------------------------------------");
        System.out.println("Template-based approach (deprecated)");
        System.out.println("  - Hard-coded templates");
        System.out.println("  - Limited flexibility");
        System.out.println("  - No semantic analysis");
        
        // Approach 3: JSmith (original approach)
        System.out.println("\nApproach 3: JSmith (Original)");
        System.out.println("-----------------------------");
        System.out.println("Full ANTLR integration");
        System.out.println("Advanced semantic analysis");
        System.out.println("Complex grammar support");
        System.out.println("  - Higher complexity");
        System.out.println("  - Steeper learning curve");
    }
    
    /**
     * Example showing how to extend the grammar with custom rules.
     */
    public static void extensionExample() {
        System.out.println("=== Grammar Extension Example ===\n");
        
        GrammarFileBasedGenerator generator = new GrammarFileBasedGenerator();
        
        // Show how to add custom grammar rules programmatically
        System.out.println("Custom Grammar Rule Addition:");
        System.out.println("-----------------------------");
        System.out.println("1. Define new grammar rules in .g4 file");
        System.out.println("2. Add semantic annotations for behavior control");
        System.out.println("3. Extend SemanticAnalyzer for custom types");
        System.out.println("4. Use $jsmith-* annotations for semantic control");
        
        System.out.println("\nExample semantic annotations:");
        System.out.println("- $jsmith-unique - Generate unique identifiers");
        System.out.println("- $jsmith-var-decl - Variable declaration");
        System.out.println("- $jsmith-var-use - Variable usage");
        System.out.println("- $jsmith-predicate(type) - Type-specific value generation");
        System.out.println("- $jsmith-scope - Scope management");
    }
} 