package ex5.data;

/**
 * Represents a variable.
 */
public class Variable {

    private String name;
    private VarType type;
    private boolean isFinal;
    private boolean isDeclared;
    private boolean isInitialized;


    /**
     * Constructor.
     * @param name The var name.
     * @param type The var type.
     * @param isFinal Is the var final?
     * @param isDeclared Is the var declared?
     * @param isInitialized Is the var initialized?
     */
    public Variable(String name, VarType type, boolean isFinal, boolean isDeclared, boolean isInitialized) {
        this.name = name;
        this.type = type;
        this.isFinal = isFinal;
        this.isDeclared = isDeclared;
        this.isInitialized = isInitialized;
    }


    /**
     * Copy constructor.
     * @param other The variable to copy.
     */
    public Variable(Variable other) {
        this.name = other.name;
        this.type = other.type;
        this.isFinal = other.isFinal;
        this.isDeclared = other.isDeclared;
        this.isInitialized = other.isInitialized;
    }


    /**
     *
     * @return The var name.
     */
    public String getName() {
        return name;
    }


    /**
     *
     * @return The variable type.
     */
    public VarType getType() {
        return type;
    }


    /**
     *
     * @return Whether the variable is final.
     */
    public boolean isFinal() {
        return isFinal;
    }




    /**
     *
     * @return Whether the var is initialized.
     */
    public boolean isInitialized() {
        return isInitialized;
    }



    /**
     * Sets the initialized state.
     * @param initialized The new state.
     */
    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

}
