package ex5.validator;

import ex5.data.Scope;
import ex5.data.VarType;
import ex5.data.Variable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Validates variable declaration and usage.
 */
public class VarValidator {


    private static final String INT_GROUP = "intVal";
    private static final String DOUBLE_GROUP = "doubleVal";
    private static final String STRING_GROUP = "stringVal";
    private static final String BOOL_GROUP = "boolVal";
    private static final String CHAR_GROUP = "charVal";
    private static final int ASSIGNMENT_GROUP = 2;
    private static final int VAR_NAME_GROUP = 1;

    // Regex for extracting types.
    // TODO explain in README why we need both this and the one defined in SyntaxValidator.
    private static final String TYPE_EXTRACT_REGEX  = "(?<%s>%s)|(?<%s>%s)|(?<%s>%s)|(?<%s>%s)|(?<%s>%s)"
            .formatted(
                    DOUBLE_GROUP,
                    SyntaxValidator.DOUBLE_VAL_REGEX,
                    INT_GROUP,
                    SyntaxValidator.INT_VAL_REGEX,
                    STRING_GROUP,
                    SyntaxValidator.STRING_VAL_REGEX,
                    BOOL_GROUP,
                    SyntaxValidator.BOOLEAN_VAL_REGEX,
                    CHAR_GROUP,
                    SyntaxValidator.CHAR_VAL_REGEX
            );


    /** Uninitialized reference error message. */
    public static final String UNINITIALIZED_REFERENCE_MSG = "Referenced variable is not initialized.";

    private static final Pattern TYPE_PATTERN = Pattern.compile(TYPE_EXTRACT_REGEX);

    private static final String VAR_DUPLICATE_MSG = "Variable already defined inside the current scope.";
    private static final String UNINITIALIZED_FINAL_MSG = "Final variables must be initialized.";
    private static final String INCOMPATIBLE_TYPE_MSG = "Incompatible type.";
    private static final String INVALID_VALUE_MSG = "Variable is not assigned to a valid value.";

    private static final String MODIFIED_FINAL_MSG = "Final variable cannot be modified.";
    private static final String UNDEFINED_VALUE_MSG_TEMPLATE =
            "Variable \"%s\" is not defined in this scope.";





    /**
     * Validates a full variable line, and adds the variables to the scope.
     * @param lineMatcher A var declaration line matcher.
     * @param scope The scope in which the line appears.
     * @throws VarException for value error
     */
    public static void validateVarLine(Matcher lineMatcher, Scope scope) throws VarException {
        VarType varType = null;
        String fullLine = lineMatcher.group(0);
        Matcher individualVarMatcher = SyntaxValidator.SINGLE_VAR_PATTERN.matcher(lineMatcher.group(0));
        // Check if the line is a declaration line:
        boolean isDec = lineMatcher.group(SyntaxValidator.DEC_ID) != null;
        if (isDec) {
            // Define the indexes to start searching variables from:
            int startIndex = lineMatcher.end(SyntaxValidator.TYPE_ID);
            int endIndex = fullLine.indexOf(';');
            individualVarMatcher.region(startIndex, endIndex);
        }

        // Check if the variable declaration is final:
        //boolean isFinal = individualVarMatcher.group(SyntaxValidator.FINAL_ID) != null;
        if (isDec) {
            // If the line is a variable declaration, extract the type from the declaration:
            varType = VarType.getTypeFromStr(lineMatcher.group(SyntaxValidator.TYPE_ID));
        }
        while (individualVarMatcher.find()) {

            // Validate the individual variable section:
            Variable currentVar = scope.findVariable(individualVarMatcher.group(VAR_NAME_GROUP));
            if (!isDec && currentVar == null) {
                // If a variable is assigned without declaration, it must be declared in a parent scope:
                throw new InvalidReferenceException(UNINITIALIZED_REFERENCE_MSG);
            } else if (!isDec) {
                varType = currentVar.getType();
            }
            // Continue validation:
            boolean isFinal = lineMatcher.group(SyntaxValidator.FINAL_ID) != null;
            if (!isDec) {
                // If the line is not a declaration line, we need to check the "final" thing differently:
                isFinal = currentVar.isFinal();
            }
            validateIndividualVar(individualVarMatcher, isDec, isFinal, scope, varType);
            if (isDec) {
                String name = individualVarMatcher.group(VAR_NAME_GROUP);
                boolean isInit = individualVarMatcher.group(ASSIGNMENT_GROUP) != null;
                scope.addToScope(new Variable(name, varType, isFinal, true, isInit));
            } else {
                boolean isInit = individualVarMatcher.group(ASSIGNMENT_GROUP) != null;
                if (!isInit) {
                    // A non-declared variable must be initialized:
                    throw new InvalidReferenceException(UNINITIALIZED_REFERENCE_MSG);
                }

                currentVar.setInitialized(true);
            }
        }

        
    }


    /* Validates an individual variable declaration. */
    private static void validateIndividualVar(Matcher varMatcher, boolean isDec, boolean isFinal, Scope scope,
                                              VarType currentType) throws VarException {
        String varName = varMatcher.group(VAR_NAME_GROUP); // Extract the var name.
        if (isDec && scope.getVariables().containsKey(varName)) {
            // If a variable is in the scope, it can't be declared again:
            throw new DuplicateVarException(VAR_DUPLICATE_MSG);
        }

        if (isDec && isFinal && varMatcher.group(ASSIGNMENT_GROUP) == null) {
            // final variable must be initialized at declaration:
            throw new UninitializedFinalVarException(UNINITIALIZED_FINAL_MSG);
        }
        if (!isDec && isFinal && varMatcher.group(ASSIGNMENT_GROUP) != null) {
            // Final variable cannot be assigned after declaration:
            throw new ModifiedFinalVarException(MODIFIED_FINAL_MSG);
        }

        if (varMatcher.group(ASSIGNMENT_GROUP) != null) {
            // Check if the assignment is valid:
            validateVarAssignment(varMatcher, scope, currentType);
        }
    }


    /* Validates a variable assignment.
     * Assumption: There is an assignment in the line somewhere.
     */
    private static void validateVarAssignment(Matcher varMatcher, Scope scope, VarType currentType) throws
            VarException{
        String assignedVal = varMatcher.group(ASSIGNMENT_GROUP);
        Matcher typeMatcher = TYPE_PATTERN.matcher(assignedVal);
        VarType valType = checkType(typeMatcher);
        Variable rightSideVar = scope.findVariable(assignedVal);
        if (valType == null && rightSideVar == null) {
            // The assigned value is not a constant and not an identifier,
            // so it's not a valid assignment:
            throw new InvalidValueException(INVALID_VALUE_MSG);
        } else if (rightSideVar != null) {
            // The value is an identifier, so check if it is initialized:
            if (!rightSideVar.isInitialized()) {
                throw new InvalidReferenceException(UNINITIALIZED_REFERENCE_MSG);
            }
            valType = rightSideVar.getType();
        }
        if (!checkCompatibility(currentType, valType)) {
            throw new IncompatibleTypeException(INCOMPATIBLE_TYPE_MSG);
        }

    }




    /** Check what type is the value, then return it
     * @param val The value that we want to get the type of
     * @param scope The scope in which the value appears.
     * @return the type of the value
     * @throws InvalidReferenceException in case of uninitialized reference
     * */
    public static VarType getTypeOfVal(String val, Scope scope) throws InvalidReferenceException {
        Matcher matcher = TYPE_PATTERN.matcher(val);
        // Check if the variable is a literal:
        if (matcher.matches()) {
            VarType type = checkType(matcher);
            if (type != null) {
                return type;
            }
        }

        // If the value is not a constant, then it is an identifier:
        Variable variable = scope.findVariable(val);
        if (variable == null) {
            // Not found in the scope:
            throw new InvalidReferenceException(UNDEFINED_VALUE_MSG_TEMPLATE.formatted(val));
        }
        if (!variable.isInitialized()) {
            // Variable not initialized:
            throw new InvalidReferenceException(UNINITIALIZED_REFERENCE_MSG);
        }
        return variable.getType();
    }



    /* Returns the type of a given value. */
    private static VarType checkType(Matcher valMatcher) {
        if (!valMatcher.matches()) {
            return null;
        }
        if (valMatcher.group(INT_GROUP) != null) {
            return VarType.INT;
        }
        if (valMatcher.group(DOUBLE_GROUP) != null) {
            return VarType.DOUBLE;
        }
        if (valMatcher.group(BOOL_GROUP) != null) {
            return VarType.BOOL;
        }
        if (valMatcher.group(CHAR_GROUP) != null) {
            return VarType.CHAR;
        }
        if (valMatcher.group(STRING_GROUP) != null) {
            return VarType.STRING;
        }
        return null;
    }


    /** Check if the other variable type can be assigned to the current variable type.
     *  @param assignedVarType The type of the variable we assigned
     *  @param otherVarType The type we compared the assignVarType to
     * @return true if the values are compatible, else false
     *  */
    public static boolean checkCompatibility(VarType assignedVarType, VarType otherVarType) {

        if (assignedVarType == VarType.DOUBLE && otherVarType == VarType.INT) {
            // Int can be assigned to a double.
            return true;
        }
        if (assignedVarType == VarType.BOOL &&
                (otherVarType == VarType.DOUBLE || otherVarType == VarType.INT)) {
            // Int and double can be assgined to bool:
            return true;
        }
        // In any other case, check if the variables have exacly the same type:
        return assignedVarType == otherVarType;
    }



}
