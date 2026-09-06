package ex5.data;
import java.util.*;
import java.util.function.BiPredicate;


/**
 * Stores data about methods.
 */
public class Method {
    private String funcName; // The method name.
    private List<Variable> args = new ArrayList<>(); // The arguments.



    /**
     * Constructor.
     * @param funcName The method name.
     */
    public Method(String funcName) {
        this.funcName = funcName;
    }


    /**
     *
     * @return The arguments.
     */
    public List<Variable> getArgs() {
        return args;
    }


    /**
     * Adds an argument to the method.
     * @param arg The argument to add.
     */
    public void addArg(Variable arg) {
        args.add(arg);
    }


    /**
     *
     * @return The number of arguments.
     */
    public int getNumArgs() {
        return args.size();
    }


    /**
     * Checks if the current method has an argument with the given name.
     * @param argName A given argument name.
     * @return True if an argument with {@code argName} already exists.
     */
    public boolean hasArg(String argName) {
        for (Variable variable : args) {
            if (variable.getName().equals(argName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Checks if the argument at the given index is the desired type.
     *
     * @param index                An index in the args list.
     * @param type                 The desired type.
     * @param copmatibilityChecker Returns true if the variables are compatible.
     * @return True if the argument at the given index is the desired type.
     */
    public boolean orderCorrectness(int index, VarType type, BiPredicate<VarType, VarType> copmatibilityChecker) {
        return copmatibilityChecker.test(args.get(index).getType(), type);
    }
}
