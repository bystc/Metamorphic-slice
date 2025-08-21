import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Simple scope management for variable declarations.
 * This class manages variable scopes during code generation.
 * 
 * @since 0.3
 */
public class Scope {
    
    private final Random random;
    private final Map<String, String> variables; // name -> type
    private final Map<String, String> types;     // name -> type
    
    /**
     * Default constructor.
     */
    public Scope() {
        this.random = new Random();
        this.variables = new HashMap<>();
        this.types = new HashMap<>();
    }
    
    /**
     * Constructor with specified seed.
     * @param seed random seed
     */
    public Scope(long seed) {
        this.random = new Random(seed);
        this.variables = new HashMap<>();
        this.types = new HashMap<>();
    }
    
    /**
     * Declare a variable in the current scope.
     * @param name variable name
     * @param type variable type
     */
    public void declareVariable(String name, String type) {
        variables.put(name, type);
        types.put(name, type);
    }
    
    /**
     * Get a variable's type.
     * @param name variable name
     * @return variable type, or null if not found
     */
    public String getVariableType(String name) {
        return types.get(name);
    }
    
    /**
     * Check if a variable is declared in the current scope.
     * @param name variable name
     * @return true if variable is declared
     */
    public boolean isVariableDeclared(String name) {
        return variables.containsKey(name);
    }
    
    /**
     * Get a random declared variable.
     * @return random variable name, or null if none available
     */
    public String getRandomVariable() {
        if (variables.isEmpty()) {
            return null;
        }
        
        String[] names = variables.keySet().toArray(new String[0]);
        return names[random.nextInt(names.length)];
    }
    
    /**
     * Get a random variable of a specific type.
     * @param type desired type
     * @return random variable name of the specified type, or null if none available
     */
    public String getRandomVariableOfType(String type) {
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            if (entry.getValue().equals(type)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    /**
     * Generate a unique variable name.
     * @param baseName base name for the variable
     * @return unique variable name
     */
    public String generateUniqueName(String baseName) {
        String name = baseName;
        int counter = 1;
        
        while (variables.containsKey(name)) {
            name = baseName + counter;
            counter++;
        }
        
        return name;
    }
    
    /**
     * Clear all variables in the current scope.
     */
    public void clear() {
        variables.clear();
        types.clear();
    }
    
    /**
     * Get the number of variables in the current scope.
     * @return number of variables
     */
    public int getVariableCount() {
        return variables.size();
    }
    
    /**
     * Get all variable names in the current scope.
     * @return array of variable names
     */
    public String[] getVariableNames() {
        return variables.keySet().toArray(new String[0]);
    }
} 