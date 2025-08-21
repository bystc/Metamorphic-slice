import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for ANTLR grammar files (.g4 files).
 * This class extracts grammar rules and alternatives from ANTLR grammar files.
 * 
 * @since 0.2
 */
public class GrammarParser {
    
    private Map<String, String[]> rules;
    private String grammarContent;
    
    /**
     * Default constructor.
     */
    public GrammarParser() {
        this.rules = new HashMap<>();
    }
    
    /**
     * Load and parse an ANTLR grammar file.
     * @param grammarFilePath path to the grammar file
     * @throws IOException if the file cannot be read
     */
    public void loadGrammar(String grammarFilePath) throws IOException {
        this.grammarContent = new String(Files.readAllBytes(Paths.get(grammarFilePath)));
        parseGrammar();
    }
    
    /**
     * Parse the loaded grammar content and extract rules.
     */
    private void parseGrammar() {
        rules.clear();
        
        // Pattern to match ANTLR parser rules
        Pattern rulePattern = Pattern.compile(
            "(\\w+)\\s*:\\s*([^;]+);",
            Pattern.DOTALL
        );
        
        Matcher matcher = rulePattern.matcher(grammarContent);
        
        while (matcher.find()) {
            String ruleName = matcher.group(1);
            String ruleBody = matcher.group(2).trim();
            
            // Split alternatives (separated by |)
            String[] alternatives = ruleBody.split("\\|");
            String[] cleanAlternatives = new String[alternatives.length];
            
            for (int i = 0; i < alternatives.length; i++) {
                cleanAlternatives[i] = alternatives[i].trim();
            }
            
            rules.put(ruleName, cleanAlternatives);
        }
    }
    
    /**
     * Get all parsed rules.
     * @return map of rule names to their alternatives
     */
    public Map<String, String[]> getRules() {
        return new HashMap<>(rules);
    }
    
    /**
     * Get alternatives for a specific rule.
     * @param ruleName name of the rule
     * @return array of alternatives for the rule, or null if rule not found
     */
    public String[] getRuleAlternatives(String ruleName) {
        return rules.get(ruleName);
    }
    
    /**
     * Check if a rule exists.
     * @param ruleName name of the rule
     * @return true if the rule exists, false otherwise
     */
    public boolean hasRule(String ruleName) {
        return rules.containsKey(ruleName);
    }
    
    /**
     * Get the number of rules in the grammar.
     * @return number of rules
     */
    public int getRuleCount() {
        return rules.size();
    }
    
    /**
     * Get all rule names.
     * @return array of rule names
     */
    public String[] getRuleNames() {
        return rules.keySet().toArray(new String[0]);
    }
    
    /**
     * Get the raw grammar content.
     * @return the grammar file content as string
     */
    public String getGrammarContent() {
        return grammarContent;
    }
    
    /**
     * Clear all loaded rules.
     */
    public void clear() {
        rules.clear();
        grammarContent = null;
    }
} 