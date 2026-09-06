package ex5.data;


import java.util.HashMap;
import java.util.Map;

/**
 * All possible types
 */
public enum VarType {

    /** int */
    INT("int"),
    /** string */
    STRING("String"),
    /** char */
    CHAR("char"),
    /** boolean */
    BOOL("boolean"),
    /** double */
    DOUBLE("double");


    private final String type;
    private static final Map<String, VarType> strToType = new HashMap<>();

    // Populate the hash table when the class is loaded.
    static {
        for (VarType currentType: values()) {
            strToType.put(currentType.type, currentType);
        }
    }

    /**
     * Empty constructor.
     * @param s A variable type.
     */
    private VarType(String s) {
        this.type = s;
    }


    /**
     * Returns the type that matches a given string.
     * @param s A string.
     * @return The type that matches the given string.
     * Assumption: There is a type that matches the given string.
     */
    public static VarType getTypeFromStr(String s) {
        return strToType.get(s);
    }






}
