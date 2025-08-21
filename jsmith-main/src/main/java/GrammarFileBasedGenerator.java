import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Grammar-based Java code generator that uses ANTLR grammar files to generate Java code.
 * This implementation follows JSmith's approach of grammar-driven code generation.
 * 
 * @since 0.3
 */
public class GrammarFileBasedGenerator {
    
    private final Random random;
    private final Map<String, GrammarRule> grammarRules;
    private final SemanticAnalyzer semanticAnalyzer;
    private final Scope scope;
    
    /**
     * Default constructor with random seed.
     */
    public GrammarFileBasedGenerator() {
        this(System.currentTimeMillis());
    }
    
    /**
     * Constructor with specified seed for reproducible results.
     * @param seed random seed
     */
    public GrammarFileBasedGenerator(long seed) {
        this.random = new Random(seed);
        this.grammarRules = new HashMap<>();
        this.semanticAnalyzer = new SemanticAnalyzer(seed);
        this.scope = new Scope(seed);
    }
    
    /**
     * Load and parse ANTLR grammar file.
     * @param grammarFilePath path to the grammar file
     */
    public void loadGrammar(String grammarFilePath) {
        try {
            String grammarContent = new String(Files.readAllBytes(Paths.get(grammarFilePath)));
            parseGrammar(grammarContent);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load grammar file: " + grammarFilePath, e);
        }
    }
    
    /**
     * Parse ANTLR grammar content and extract rules.
     * @param grammarContent the grammar file content
     */
    private void parseGrammar(String grammarContent) {
        grammarRules.clear();
        
        // Parse parser rules
        Pattern rulePattern = Pattern.compile(
            "(\\w+)\\s*:\\s*([^;]+);",
            Pattern.DOTALL
        );
        
        Matcher matcher = rulePattern.matcher(grammarContent);
        
        while (matcher.find()) {
            String ruleName = matcher.group(1);
            String ruleBody = matcher.group(2).trim();
            
            // Extract alternatives (separated by |)
            String[] alternatives = ruleBody.split("\\|");
            List<GrammarAlternative> altList = new ArrayList<>();
            
            for (String alternative : alternatives) {
                altList.add(parseAlternative(alternative.trim()));
            }
            
            grammarRules.put(ruleName, new GrammarRule(ruleName, altList));
        }
    }
    
    /**
     * Parse a single alternative in a grammar rule.
     * @param alternativeText the alternative text
     * @return parsed alternative
     */
    private GrammarAlternative parseAlternative(String alternativeText) {
        List<GrammarElement> elements = new ArrayList<>();
        
        // Parse elements (tokens, rule references, etc.)
        Pattern elementPattern = Pattern.compile(
            "([A-Z][A-Z_]*|\\w+|'[^']*'|\\$\\w+\\-\\w+)",
            Pattern.DOTALL
        );
        
        Matcher matcher = elementPattern.matcher(alternativeText);
        
        while (matcher.find()) {
            String element = matcher.group(1);
            elements.add(parseElement(element));
        }
        
        return new GrammarAlternative(elements);
    }
    
    /**
     * Parse a single element in an alternative.
     * @param elementText the element text
     * @return parsed element
     */
    private GrammarElement parseElement(String elementText) {
        if (elementText.startsWith("'") && elementText.endsWith("'")) {
            // Literal token
            String literal = elementText.substring(1, elementText.length() - 1);
            return new LiteralElement(literal);
        } else if (elementText.startsWith("$")) {
            // Semantic annotation
            return new SemanticElement(elementText);
        } else if (elementText.matches("[A-Z][A-Z_]*")) {
            // Terminal token
            return new TerminalElement(elementText);
        } else if (elementText.matches("\\w+")) {
            // Rule reference
            return new RuleReferenceElement(elementText);
        } else {
            // Default to literal
            return new LiteralElement(elementText);
        }
    }
    
    /**
     * Generate Java code starting from a specific rule.
     * @param startRule the starting rule name
     * @return generated Java code
     */
    public String generateFromRule(String startRule) {
        if (!grammarRules.containsKey(startRule)) {
            throw new IllegalArgumentException("Rule not found: " + startRule);
        }
        
        GrammarRule rule = grammarRules.get(startRule);
        return rule.generate(this);
    }
    
    /**
     * Generate a Java class with main method using grammar-driven approach.
     * @param className name of the generated class
     * @return generated Java source code
     */
    public String generateMainClassWithMainMethod(String className) {
        // Use grammar rules to generate the class structure
        StringBuilder code = new StringBuilder();
        
        // Generate package declaration
        if (hasRule("packageDeclaration")) {
            code.append(generateFromRule("packageDeclaration"));
        } else {
            code.append("package generated;\n");
        }
        code.append("\n");
        
        // Generate import statements
        if (hasRule("importDeclaration")) {
            code.append(generateFromRule("importDeclaration"));
        } else {
            code.append("import java.util.*;\n");
            code.append("import java.io.*;\n");
        }
        code.append("\n");
        
        // Generate class declaration
        code.append("public class ").append(className).append(" {\n");
        
        // Generate main method
        if (hasRule("mainMethod")) {
            code.append(generateFromRule("mainMethod"));
        } else {
            code.append("    public static void main(String[] args) {\n");
            if (hasRule("statementList")) {
                code.append(generateFromRule("statementList"));
            } else {
                code.append("        System.out.println(\"Hello, World!\");\n");
            }
            code.append("    }\n");
        }
        
        code.append("}\n");
        
        return code.toString();
    }
    
    /**
     * Generate a complex Java class using grammar rules.
     * @param className name of the generated class
     * @return generated Java source code
     */
    public String generateComplexJavaClass(String className) {
        StringBuilder code = new StringBuilder();
        
        // Generate package declaration
        if (hasRule("packageDeclaration")) {
            code.append(generateFromRule("packageDeclaration"));
        } else {
            code.append("package generated;\n");
        }
        code.append("\n");
        
        // Generate import statements
        if (hasRule("importDeclaration")) {
            code.append(generateFromRule("importDeclaration"));
        } else {
            code.append("import java.util.*;\n");
            code.append("import java.io.*;\n");
        }
        code.append("\n");
        
        // Generate class declaration
        code.append("public class ").append(className).append(" {\n");
        
        // Generate main method with complex body
        if (hasRule("mainMethodWithComplexBody")) {
            code.append(generateFromRule("mainMethodWithComplexBody"));
        } else {
            code.append("    public static void main(String[] args) {\n");
            if (hasRule("complexStatementList")) {
                code.append(generateFromRule("complexStatementList"));
            } else {
                // Generate some complex statements
                code.append("        System.out.println(\"Complex generated code!\");\n");
                code.append("        int counter = 0;\n");
                code.append("        for (int i = 0; i < 5; i++) {\n");
                code.append("            counter += i;\n");
                code.append("        }\n");
                code.append("        System.out.println(\"Counter: \" + counter);\n");
            }
            code.append("    }\n");
        }
        
        code.append("}\n");
        
        return code.toString();
    }
    
    /**
     * Generate multiple Java files in batch.
     * @param outputDir output directory
     * @param count number of files to generate
     * @param baseName base name for the generated classes
     * @return list of generated file paths
     */
    public List<Path> generateBatchJavaFiles(String outputDir, int count, String baseName) {
        List<Path> files = new ArrayList<>();
        
        for (int i = 1; i <= count; i++) {
            String className = baseName + i;
            String outputPath = outputDir + "/" + className + ".java";
            Path file = generateComplexJavaFile(className, outputPath);
            files.add(file);
        }
        
        return files;
    }
    
    /**
     * Generate a complex Java file and save it.
     * @param className name of the generated class
     * @param outputPath path to save the generated file
     * @return path to the generated file
     */
    public Path generateComplexJavaFile(String className, String outputPath) {
        String code = generateComplexJavaClass(className);
        
        try {
            Path path = Paths.get(outputPath);
            Files.createDirectories(path.getParent());
            Files.write(path, code.getBytes());
            return path;
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file: " + outputPath, e);
        }
    }
    
    /**
     * Get a random number generator.
     * @return random number generator
     */
    public Random getRandom() {
        return random;
    }
    
    /**
     * Get the semantic analyzer.
     * @return semantic analyzer
     */
    public SemanticAnalyzer getSemanticAnalyzer() {
        return semanticAnalyzer;
    }
    
    /**
     * Get the current scope.
     * @return current scope
     */
    public Scope getScope() {
        return scope;
    }
    
    /**
     * Get a grammar rule by name.
     * @param ruleName the rule name
     * @return grammar rule or null if not found
     */
    public GrammarRule getRule(String ruleName) {
        return grammarRules.get(ruleName);
    }
    
    /**
     * Check if a rule exists.
     * @param ruleName the rule name
     * @return true if rule exists
     */
    public boolean hasRule(String ruleName) {
        return grammarRules.containsKey(ruleName);
    }
    
    /**
     * Main method for testing.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        GrammarFileBasedGenerator generator = new GrammarFileBasedGenerator();
        
        // Load grammar file (if available)
        try {
            generator.loadGrammar("src/main/resources/grammars/Java8ReducedParser.g4");
            System.out.println("Grammar loaded successfully!");
        } catch (Exception e) {
            System.out.println("Using built-in grammar rules...");
            // Load built-in rules
            generator.loadBuiltInRules();
        }
        
        // Generate a class using grammar rules
        String code = generator.generateMainClassWithMainMethod("GrammarExample");
        System.out.println("Generated Java class using grammar rules:");
        System.out.println("=========================================");
        System.out.println(code);
        System.out.println("=========================================");
        
        // Generate a complex class
        String complexCode = generator.generateComplexJavaClass("ComplexGrammarExample");
        System.out.println("\nGenerated complex Java class:");
        System.out.println("=============================");
        System.out.println(complexCode);
        System.out.println("=============================");
    }
    
    /**
     * Load built-in grammar rules for basic Java generation.
     */
    private void loadBuiltInRules() {
        // Package declaration rule
        List<GrammarAlternative> packageAlts = new ArrayList<>();
        packageAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("package"),
            new LiteralElement(" "),
            new RuleReferenceElement("packageName"),
            new LiteralElement(";"),
            new LiteralElement("\n")
        )));
        grammarRules.put("packageDeclaration", new GrammarRule("packageDeclaration", packageAlts));
        
        // Import declaration rule
        List<GrammarAlternative> importAlts = new ArrayList<>();
        importAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("import"),
            new LiteralElement(" "),
            new LiteralElement("java.util.*"),
            new LiteralElement(";"),
            new LiteralElement("\n")
        )));
        importAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("import"),
            new LiteralElement(" "),
            new LiteralElement("java.io.*"),
            new LiteralElement(";"),
            new LiteralElement("\n")
        )));
        grammarRules.put("importDeclaration", new GrammarRule("importDeclaration", importAlts));
        
        // Main method rule
        List<GrammarAlternative> mainAlts = new ArrayList<>();
        mainAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("    public static void main(String[] args) {\n"),
            new RuleReferenceElement("statementList"),
            new LiteralElement("    }\n")
        )));
        grammarRules.put("mainMethod", new GrammarRule("mainMethod", mainAlts));
        
        // Statement list rule
        List<GrammarAlternative> stmtListAlts = new ArrayList<>();
        stmtListAlts.add(new GrammarAlternative(List.of(
            new RuleReferenceElement("statement"),
            new LiteralElement("\n"),
            new RuleReferenceElement("statementList")
        )));
        stmtListAlts.add(new GrammarAlternative(List.of(
            new RuleReferenceElement("statement"),
            new LiteralElement("\n")
        )));
        grammarRules.put("statementList", new GrammarRule("statementList", stmtListAlts));
        
        // Statement rule
        List<GrammarAlternative> stmtAlts = new ArrayList<>();
        stmtAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("        "),
            new RuleReferenceElement("printStatement")
        )));
        stmtAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("        "),
            new RuleReferenceElement("variableDeclaration")
        )));
        stmtAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("        "),
            new RuleReferenceElement("forLoop")
        )));
        stmtAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("        "),
            new RuleReferenceElement("ifStatement")
        )));
        grammarRules.put("statement", new GrammarRule("statement", stmtAlts));
        
        // Print statement rule
        List<GrammarAlternative> printAlts = new ArrayList<>();
        printAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("System.out.println(\"Hello, World!\");")
        )));
        printAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("System.out.println(\"Generated code is working!\");")
        )));
        grammarRules.put("printStatement", new GrammarRule("printStatement", printAlts));
        
        // Variable declaration rule
        List<GrammarAlternative> varDeclAlts = new ArrayList<>();
        varDeclAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("int "),
            new SemanticElement("$jsmith-unique"),
            new LiteralElement(" = "),
            new SemanticElement("$jsmith-predicate(int)"),
            new LiteralElement(";")
        )));
        varDeclAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("String "),
            new SemanticElement("$jsmith-unique"),
            new LiteralElement(" = \"Generated String "),
            new SemanticElement("$jsmith-predicate(int)"),
            new LiteralElement("\";")
        )));
        grammarRules.put("variableDeclaration", new GrammarRule("variableDeclaration", varDeclAlts));
        
        // For loop rule
        List<GrammarAlternative> forAlts = new ArrayList<>();
        forAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("for (int "),
            new SemanticElement("$jsmith-unique"),
            new LiteralElement(" = 0; "),
            new SemanticElement("$jsmith-unique"),
            new LiteralElement(" < "),
            new SemanticElement("$jsmith-predicate(int)"),
            new LiteralElement("; "),
            new SemanticElement("$jsmith-unique"),
            new LiteralElement("++) {\n"),
            new LiteralElement("            // Loop body\n"),
            new LiteralElement("        }")
        )));
        grammarRules.put("forLoop", new GrammarRule("forLoop", forAlts));
        
        // If statement rule
        List<GrammarAlternative> ifAlts = new ArrayList<>();
        ifAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("if (System.currentTimeMillis() % 100 > "),
            new SemanticElement("$jsmith-predicate(int)"),
            new LiteralElement(") {\n"),
            new LiteralElement("            System.out.println(\"Condition met!\");\n"),
            new LiteralElement("        }")
        )));
        grammarRules.put("ifStatement", new GrammarRule("ifStatement", ifAlts));
        
        // Main method with complex body rule
        List<GrammarAlternative> complexMainAlts = new ArrayList<>();
        complexMainAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("    public static void main(String[] args) {\n"),
            new RuleReferenceElement("complexStatementList"),
            new LiteralElement("    }\n")
        )));
        grammarRules.put("mainMethodWithComplexBody", new GrammarRule("mainMethodWithComplexBody", complexMainAlts));
        
        // Complex statement list rule
        List<GrammarAlternative> complexStmtListAlts = new ArrayList<>();
        complexStmtListAlts.add(new GrammarAlternative(List.of(
            new RuleReferenceElement("complexStatement"),
            new LiteralElement("\n"),
            new RuleReferenceElement("complexStatementList")
        )));
        complexStmtListAlts.add(new GrammarAlternative(List.of(
            new RuleReferenceElement("complexStatement"),
            new LiteralElement("\n")
        )));
        grammarRules.put("complexStatementList", new GrammarRule("complexStatementList", complexStmtListAlts));
        
        // Complex statement rule
        List<GrammarAlternative> complexStmtAlts = new ArrayList<>();
        complexStmtAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("        "),
            new RuleReferenceElement("printStatement")
        )));
        complexStmtAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("        "),
            new RuleReferenceElement("variableDeclaration")
        )));
        complexStmtAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("        "),
            new RuleReferenceElement("forLoop")
        )));
        complexStmtAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("        "),
            new RuleReferenceElement("ifStatement")
        )));
        complexStmtAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("        "),
            new RuleReferenceElement("whileLoop")
        )));
        complexStmtAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("        "),
            new RuleReferenceElement("switchStatement")
        )));
        grammarRules.put("complexStatement", new GrammarRule("complexStatement", complexStmtAlts));
        
        // While loop rule
        List<GrammarAlternative> whileAlts = new ArrayList<>();
        whileAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("while (System.currentTimeMillis() % 1000 < "),
            new SemanticElement("$jsmith-predicate(int)"),
            new LiteralElement(") {\n"),
            new LiteralElement("            // While loop body\n"),
            new LiteralElement("        }")
        )));
        grammarRules.put("whileLoop", new GrammarRule("whileLoop", whileAlts));
        
        // Switch statement rule
        List<GrammarAlternative> switchAlts = new ArrayList<>();
        switchAlts.add(new GrammarAlternative(List.of(
            new LiteralElement("switch ("),
            new SemanticElement("$jsmith-predicate(int)"),
            new LiteralElement(") {\n"),
            new LiteralElement("            case 0:\n"),
            new LiteralElement("                System.out.println(\"Case 0\");\n"),
            new LiteralElement("                break;\n"),
            new LiteralElement("            default:\n"),
            new LiteralElement("                System.out.println(\"Default case\");\n"),
            new LiteralElement("                break;\n"),
            new LiteralElement("        }")
        )));
        grammarRules.put("switchStatement", new GrammarRule("switchStatement", switchAlts));
    }
}

/**
 * Represents a grammar rule with multiple alternatives.
 */
class GrammarRule {
    private final String name;
    private final List<GrammarAlternative> alternatives;
    
    public GrammarRule(String name, List<GrammarAlternative> alternatives) {
        this.name = name;
        this.alternatives = alternatives;
    }
    
    public String generate(GrammarFileBasedGenerator generator) {
        // Randomly select an alternative
        GrammarAlternative selected = alternatives.get(
            generator.getRandom().nextInt(alternatives.size())
        );
        return selected.generate(generator);
    }
    
    public String getName() {
        return name;
    }
}

/**
 * Represents an alternative in a grammar rule.
 */
class GrammarAlternative {
    private final List<GrammarElement> elements;
    
    public GrammarAlternative(List<GrammarElement> elements) {
        this.elements = elements;
    }
    
    public String generate(GrammarFileBasedGenerator generator) {
        StringBuilder result = new StringBuilder();
        for (GrammarElement element : elements) {
            result.append(element.generate(generator));
        }
        return result.toString();
    }
}

/**
 * Base class for grammar elements.
 */
abstract class GrammarElement {
    public abstract String generate(GrammarFileBasedGenerator generator);
}

/**
 * Represents a literal string in the grammar.
 */
class LiteralElement extends GrammarElement {
    private final String literal;
    
    public LiteralElement(String literal) {
        this.literal = literal;
    }
    
    @Override
    public String generate(GrammarFileBasedGenerator generator) {
        return literal;
    }
}

/**
 * Represents a terminal token in the grammar.
 */
class TerminalElement extends GrammarElement {
    private final String terminal;
    
    public TerminalElement(String terminal) {
        this.terminal = terminal;
    }
    
    @Override
    public String generate(GrammarFileBasedGenerator generator) {
        // Handle different terminal types
        switch (terminal) {
            case "SPACE":
                return " ";
            case "NL":
                return "\n";
            case "Identifier":
                return generator.getSemanticAnalyzer().generateUniqueIdentifier();
            default:
                return terminal;
        }
    }
}

/**
 * Represents a rule reference in the grammar.
 */
class RuleReferenceElement extends GrammarElement {
    private final String ruleName;
    
    public RuleReferenceElement(String ruleName) {
        this.ruleName = ruleName;
    }
    
    @Override
    public String generate(GrammarFileBasedGenerator generator) {
        GrammarRule rule = generator.getRule(ruleName);
        if (rule != null) {
            return rule.generate(generator);
        } else {
            return "// Rule not found: " + ruleName;
        }
    }
}

/**
 * Represents a semantic annotation in the grammar.
 */
class SemanticElement extends GrammarElement {
    private final String annotation;
    
    public SemanticElement(String annotation) {
        this.annotation = annotation;
    }
    
    @Override
    public String generate(GrammarFileBasedGenerator generator) {
        // Handle semantic annotations similar to JSmith
        if (annotation.equals("$jsmith-unique")) {
            return generator.getSemanticAnalyzer().generateUniqueIdentifier();
        } else if (annotation.startsWith("$jsmith-predicate(")) {
            String type = annotation.substring(18, annotation.length() - 1);
            return generator.getSemanticAnalyzer().generateRandomValue(type);
        } else if (annotation.equals("$jsmith-var-decl")) {
            // Variable declaration logic
            return generator.getSemanticAnalyzer().generateVariableDeclaration();
        } else if (annotation.equals("$jsmith-var-use")) {
            // Variable usage logic
            return generator.getSemanticAnalyzer().generateVariableUsage();
        }
        return annotation;
    }
} 