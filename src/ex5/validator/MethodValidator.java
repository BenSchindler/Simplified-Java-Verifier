package ex5.validator;

import ex5.data.*;
import ex5.data.Scope;

import java.util.Map;
import java.util.regex.Matcher;


/**
 * Validates methods.
 */
public class MethodValidator {


    private static final String DUPLICATE_ARG_MSG = "Duplicate arguments.";


    /**
     * Validates a method call.
     * @param lineMatcher A line matcher.
     * @param currentScope The current scope.
     * @param globalMethods A map of global methods.
     * @throws MethodException In case of a method validation error.
     * @throws IncompatibleTypeException In case of type compatibility error.
     * @throws InvalidReferenceException In case of reference error.
     */
    public static void validateMethodCall(Matcher lineMatcher, Scope currentScope,
                                          Map<String, Method> globalMethods)
            throws MethodException, IncompatibleTypeException, InvalidReferenceException {



        String methodName = lineMatcher.group(1);
        String argsString = lineMatcher.group(2);
        String[] arguments;

        //explicitly make sure that no spaces are written and this "protects" the arguments
        if (argsString == null) {
            arguments = new String[0];
        } else {
            arguments = argsString.split(",");
        }
        if(!(globalMethods.containsKey(methodName))){
            throw new MethodException("Method " + methodName + " not found");
        }
        Method calledMethod = globalMethods.get(methodName);
        int amountOfArgs = arguments.length;
        if (amountOfArgs!=calledMethod.getNumArgs()) {
            throw new MethodException("Method " + methodName + " has wrong number of arguments");}
        for (int i = 0; i < amountOfArgs; i++) {
            String argValue = arguments[i].trim(); //remove spaces

                // We have a constant:
                VarType actualType = VarValidator.getTypeOfVal(argValue, currentScope);
                if (!calledMethod.orderCorrectness(i, actualType, VarValidator::checkCompatibility)) {
                    throw new IncompatibleTypeException("Type mismatch for argument " + (i+1));
                }
        }

        if(!globalMethods.containsKey(methodName)){
            throw new InvalidReferenceException("Method " + methodName + " not found");
        }

    }


    /**
     * Validates a method declaration.
     * @param lineMatcher A matcher.
     * @param methodMap A map of methods.
     * @return The method which is declared.
     * @throws ValidationException In case where the method call is invalid.
     */
    public static Method validateMethodDeclaration(Matcher lineMatcher, Map<String, Method> methodMap)
            throws ValidationException {
        /*String fixedLine = line.trim();
        Matcher matcher = SyntaxValidator.METHOD_DEC_PATTERN.matcher(fixedLine);
        if (!matcher.matches()) {
            throw new Exception("Invalid method declaration");
        }*/
        String methodName = lineMatcher.group(1).trim(); //clean spaces
        String argsString = lineMatcher.group(2); //clean spaces
        Method method = new Method(methodName); //create the method to be returned
        if (argsString == null) {
            String[] arguments = new String[0]; //no need for further action since no args to go over
        }
        else {
            String[] arguments = argsString.trim().split(","); //split every arg based on ","
            for(String arg : arguments){
                arg = arg.trim();
                String[] parts =  arg.split("\\s+"); //differentiate between type to name, using space
                boolean isFinal = parts.length == 3;
                String type = null;
                String name = null;
                if (!isFinal) {
                    type = parts[0];
                    name = parts[1];
                } else {
                    type = parts[1];
                    name = parts[2];
                }
                VarType varType = VarType.getTypeFromStr(type); //type of "type"
                if (method.hasArg(name)) {
                    // Cannot have duplicate arg names:
                    throw new DuplicateVarException(DUPLICATE_ARG_MSG);
                }

                //var is confirmed, and can be added to the method's vars
                Variable var = new Variable(name,varType,isFinal,true,true);
                method.addArg(var);
            }
        }
        methodMap.put(methodName, method);
        return method;
    }


    /**
     * Validates a condition statement.
     * @param lineMatcher A metcher.
     * @param currentScope The current scope.
     * @throws IncompatibleTypeException In case when one of the variables is not boolean.
     * @throws InvalidReferenceException In case of an invalid reference.
     */
    public static void validateIfNWhile(Matcher lineMatcher,Scope currentScope) throws
            IncompatibleTypeException, InvalidReferenceException {

        //matcher matches
        String conditionString = lineMatcher.group(1).trim();
        String[] subConditions = conditionString.split("\\|\\||&&");
        for(String word : subConditions){
            word = word.trim();
            VarType actualType = VarValidator.getTypeOfVal(word, currentScope);
            if (!VarValidator.checkCompatibility(VarType.BOOL, actualType)) {
                throw new IncompatibleTypeException("Condition '" + word + "' is not a boolean value.");
            }
        }
    }
}