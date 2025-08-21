import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Semantic analyzer for Java code generation.
 * This class manages variable declarations, types, and semantic correctness.
 * 
 * @since 0.2
 */
public class SemanticAnalyzer {
    
    private final Random random;
    private final Map<String, VariableInfo> variables;
    private final List<String> declaredVariables;
    private final List<String> initializedVariables;
    
    /**
     * Default constructor.
     */
    public SemanticAnalyzer() {
        this.random = new Random();
        this.variables = new HashMap<>();
        this.declaredVariables = new ArrayList<>();
        this.initializedVariables = new ArrayList<>();
    }
    
    /**
     * Constructor with specified seed.
     * @param seed random seed
     */
    public SemanticAnalyzer(long seed) {
        this.random = new Random(seed);
        this.variables = new HashMap<>();
        this.declaredVariables = new ArrayList<>();
        this.initializedVariables = new ArrayList<>();
    }
    
    /**
     * Declare a new variable.
     * @param name variable name
     * @param type variable type
     */
    public void declareVariable(String name, String type) {
        VariableInfo info = new VariableInfo(name, type);
        variables.put(name, info);
        declaredVariables.add(name);
    }
    
    /**
     * Initialize a variable.
     * @param name variable name
     */
    public void initializeVariable(String name) {
        if (variables.containsKey(name)) {
            variables.get(name).setInitialized(true);
            if (!initializedVariables.contains(name)) {
                initializedVariables.add(name);
            }
        }
    }
    
    /**
     * Get a random declared variable.
     * @return random declared variable name, or null if none available
     */
    public String getRandomDeclaredVariable() {
        if (declaredVariables.isEmpty()) {
            return null;
        }
        return declaredVariables.get(random.nextInt(declaredVariables.size()));
    }
    
    /**
     * Get a random initialized variable.
     * @return random initialized variable name, or null if none available
     */
    public String getRandomInitializedVariable() {
        if (initializedVariables.isEmpty()) {
            return null;
        }
        return initializedVariables.get(random.nextInt(initializedVariables.size()));
    }
    
    /**
     * Get a random variable of a specific type.
     * @param type desired variable type
     * @return random variable name of the specified type, or null if none available
     */
    public String getRandomVariableOfType(String type) {
        List<String> typeVariables = new ArrayList<>();
        
        for (Map.Entry<String, VariableInfo> entry : variables.entrySet()) {
            if (entry.getValue().getType().equals(type)) {
                typeVariables.add(entry.getKey());
            }
        }
        
        if (typeVariables.isEmpty()) {
            return null;
        }
        
        return typeVariables.get(random.nextInt(typeVariables.size()));
    }
    
    /**
     * Generate a unique variable name.
     * @param baseName base name for the variable
     * @return unique variable name
     */
    public String generateUniqueVariableName(String baseName) {
        String name = baseName;
        int counter = 1;
        
        while (variables.containsKey(name)) {
            name = baseName + counter;
            counter++;
        }
        
        return name;
    }
    
    /**
     * Check if a variable is declared.
     * @param name variable name
     * @return true if variable is declared, false otherwise
     */
    public boolean isVariableDeclared(String name) {
        return variables.containsKey(name);
    }
    
    /**
     * Check if a variable is initialized.
     * @param name variable name
     * @return true if variable is initialized, false otherwise
     */
    public boolean isVariableInitialized(String name) {
        VariableInfo info = variables.get(name);
        return info != null && info.isInitialized();
    }
    
    /**
     * Get variable type.
     * @param name variable name
     * @return variable type, or null if variable not found
     */
    public String getVariableType(String name) {
        VariableInfo info = variables.get(name);
        return info != null ? info.getType() : null;
    }
    
    /**
     * Generate a random value for a given type.
     * @param type Java type
     * @return random value as string
     */
    public String generateRandomValue(String type) {
        switch (type) {
            case "int":
                return String.valueOf(random.nextInt(1000));
            case "long":
                return random.nextLong() + "L";
            case "boolean":
                return String.valueOf(random.nextBoolean());
            case "String":
                return "\"Generated String " + random.nextInt(100) + "\"";
            case "double":
                return String.valueOf(random.nextDouble());
            case "float":
                return random.nextFloat() + "f";
            default:
                return "null";
        }
    }
    
    /**
     * Clear all variable information.
     */
    public void clear() {
        variables.clear();
        declaredVariables.clear();
        initializedVariables.clear();
    }
    
    /**
     * Get all declared variables.
     * @return list of declared variable names
     */
    public List<String> getDeclaredVariables() {
        return new ArrayList<>(declaredVariables);
    }
    
    /**
     * Get all initialized variables.
     * @return list of initialized variable names
     */
    public List<String> getInitializedVariables() {
        return new ArrayList<>(initializedVariables);
    }
    
    /**
     * Generate a unique identifier.
     * @return unique identifier
     */
    public String generateUniqueIdentifier() {
        return "var" + random.nextInt(10000);
    }
    
    /**
     * Generate a variable declaration.
     * @return variable declaration string
     */
    public String generateVariableDeclaration() {
        String[] types = {"int", "String", "boolean", "long"};
        String type = types[random.nextInt(types.length)];
        String name = generateUniqueIdentifier();
        declareVariable(name, type);
        return type + " " + name;
    }
    
    /**
     * Generate a variable usage.
     * @return variable usage string
     */
    public String generateVariableUsage() {
        String var = getRandomDeclaredVariable();
        if (var != null) {
            return var;
        } else {
            // If no variables available, create a new one
            String[] types = {"int", "String", "boolean", "long"};
            String type = types[random.nextInt(types.length)];
            String name = generateUniqueIdentifier();
            declareVariable(name, type);
            return name;
        }
    }
    
    /**
     * Get variable count.
     * @return number of declared variables
     */
    public int getVariableCount() {
        return variables.size();
    }
    
    /**
     * Inner class to store variable information.
     */
    private static class VariableInfo {
        private final String name;
        private final String type;
        private boolean initialized;
        
        public VariableInfo(String name, String type) {
            this.name = name;
            this.type = type;
            this.initialized = false;
        }
        
        public String getName() {
            return name;
        }
        
        public String getType() {
            return type;
        }
        
        public boolean isInitialized() {
            return initialized;
        }
        
        public void setInitialized(boolean initialized) {
            this.initialized = initialized;
        }
    }
} 