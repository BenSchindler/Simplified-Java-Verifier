package ex5.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An object representing a block of code.
 */
public class Scope {

    // Store the scope's variables:
    private Map<String, Variable> variables = new HashMap<>();
    private ScopeType type;
    private Scope parentScope;


    /**
     * Constructor.
     * @param type The scope type.
     * @param parentScope The parent scope.
     */
    public Scope(ScopeType type, Scope parentScope) {
        this.type = type;
        this.parentScope = parentScope;
    }


    /**
     * Copy constructor.
     * @param other The scope to copy.
     */
    public Scope(Scope other) {
        this.variables = new HashMap<>();
        for (String varName: other.variables.keySet()) {
            this.variables.put(varName, new Variable(other.variables.get(varName)));
        }
        this.parentScope = other.parentScope;
        this.type = other.type;
    }





    /**
     * Finds a variable according to a given name.
     * @param varName A variable name.
     * @return The {@code Variable} object that matches the given name.
     * If the variable is not found in the current scope or in any of the parent scopes,
     * return {@code null}.
     */
    public Variable findVariable(String varName) {
        Variable currentVar = variables.get(varName);
        if (type == ScopeType.GLOBAL && currentVar == null) {
            // If the variable is not found in the global scope, then it's over:
            return null;
        }
        if (currentVar != null) {
            return currentVar;
        }
        // If the variable is not found in the current scope, search for it in the parent scope:
        return parentScope.findVariable(varName);

    }


    /**
     *
     * @return The parent scope.
     */
    public Scope getParentScope() {
        return parentScope;
    }


    /**
     * Adds a variable to the scope.
     * @param variable The variable to add.
     */
    public void addToScope(Variable variable) {
        variables.put(variable.getName(), variable);
    }


    /**
     *
     * @return The scope type.
     */
    public ScopeType getType() {
        return type;
    }


    /**
     *
     * @return The variables in the socpe.
     */
    public Map<String, Variable> getVariables() {
        return variables;
    }
}
