package ex5.main;

import ex5.data.Method;
import ex5.data.Scope;
import ex5.data.ScopeType;
import ex5.data.Variable;
import ex5.validator.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;


/**
 * Veirifes a given file.
 */
public class Verifier {


    private static final String INVALID_SYNTAX_MSG = "Unknown syntax: ";
    private static final String METHOD_INSIDE_METHOD_MSG = "Method cannot be declared inside another method.";
    private static final String MISSING_RETURN_MSG = "Missing return statement.";
    private static final String METHOD_NOT_CLOSED_MSG = "Reached end of file without closing a method.";
    private static final String INVALID_GLOBAL_STATEMENT = "Global scope can only contain declarations.";
    private static final String GLOBAL_RETURN_MSG = "Return statement illegal in global scope";
    private static final String GLOBAL_METHOD_CALL_MSG = "Method call illegal in global scope";
    private static final String UNBALANCED_BRACKETS_MSG = "Unbalanced brackets: extra '}'";


    private List<String> inputLines = new ArrayList<>();
    private Scope globalScope = new Scope(ScopeType.GLOBAL,null);
    private Map<String, Method> globalMethods = new HashMap<>();
    private int currentLineIndex = 0;
    private Scope currentScope = globalScope;


    /**
     * Constructor. Executes first pass.
     * @param reader A buffered reader.
     * @throws IOException In case of IO error.
     * @throws InvalidSyntaxException In case of invalid syntax.
     * @throws ValidationException In case of validation error.
     * @throws FlowException In case of flow error.
     */
    public Verifier(BufferedReader reader) throws IOException, InvalidSyntaxException, ValidationException,
            FlowException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (SyntaxValidator.checkIgnore(line) == null) {
                // If the line is not a whitespace or a comment, add it to the list:
                line = line.trim();
                inputLines.add(line);
            }
        }
    }


    /**
     * Verifies the code in the given input file.
     * @throws InvalidSyntaxException if the syntax is invalid.
     * @throws FlowException if there is a control flow problem.
     * @throws ValidationException if there is an invalid reference.
     * @throws VarException if there is a problem with variable declaration or reference.
     */
    public void verify() throws InvalidSyntaxException, FlowException, ValidationException, VarException {
        firstPass();
        reset();
        runSecondPass();
    }




    /* Passes over a method body and checks basic flow. After running the
     * method, the current line is the closing bracket. */
    private void advanceToMethodEnd() throws FlowException {
        advance(); // Advance over the method declaration.
        int scopeCount = 0;
        while (hasMoreLines() && (scopeCount > 0 || !getCurrentLine().equals("}"))) {
            String currentLine = getCurrentLine();
            if (SyntaxValidator.checkMethodDec(currentLine) != null) {
                // Cannot declare a method inside a method:
                throw new FlowException(METHOD_INSIDE_METHOD_MSG);
            }
            if (SyntaxValidator.checkIfNWhile(currentLine) != null) {
                // Reached an inner scope:
                scopeCount++;
            }
            if (currentLine.equals("}")) {
                // Reached condition end:
                scopeCount--;
            }
            if (scopeCount < 0) {
                throw new FlowException(UNBALANCED_BRACKETS_MSG);
            }
            advance();
        }

        // Ensure the current line is a closing '}':
        if (!hasMoreLines() || (!getCurrentLine().equals("}"))) {
            // Reached end of file without completing a method:
            throw new FlowException(METHOD_NOT_CLOSED_MSG);
        }

        // Check if the method ends with a return statement:
        if (SyntaxValidator.checkReturn(inputLines.get(currentLineIndex - 1)) == null) {
            throw new FlowException(MISSING_RETURN_MSG);
        }


    }


    /* Check if we reached the end of the list. */
    private boolean hasMoreLines() {
        return currentLineIndex < inputLines.size();
    }


    /* Returns the current line in the input. */
    private String getCurrentLine() {
        return inputLines.get(currentLineIndex);
    }

    /* Resets the verifier. */
    private void reset() {
        currentLineIndex = 0;
    }



    /* Advances to the next line in the input. */
    private void advance() {
        currentLineIndex++;
    }


    /* Runs second pass on the file. Checks for all kinds of errors. */
    private void runSecondPass() throws FlowException, ValidationException,
            InvalidSyntaxException {
        Matcher lineMatcher;
        while (hasMoreLines()){
            String line = getCurrentLine();
            if((lineMatcher = SyntaxValidator.checkMethodDec(line)) != null){
                parseMathodDec(lineMatcher);
            }
            else if (line.startsWith("}")){
                parseScopeClose();
            }
            //else if (line.startsWith("if") || line.startsWith("while")) {
            else if (SyntaxValidator.checkIfNWhile(line) != null) {

                parseCondition();
            }
            //else if (line.startsWith("return")){
            else if(SyntaxValidator.checkReturn(line) != null) {
                parseReturn();
            }
            //else if ((!line.startsWith("if")||!line.startsWith("while"))&&line.contains("(")){ //method call
            else if ((lineMatcher = SyntaxValidator.checkMethodCall(line)) != null) {
                parseMethodCall(lineMatcher);
            }
            else if ((lineMatcher = SyntaxValidator.checkVarLine(line)) != null){
                parseVarLine(lineMatcher);
            } else {
                throw new InvalidSyntaxException(INVALID_SYNTAX_MSG + line);
            }
            advance();
        }
    }



    /* Parses a method declaration. */
    private void parseMathodDec(Matcher lineMatcher) throws FlowException, ValidationException {
        if (currentScope != globalScope) {
            throw new FlowException(METHOD_INSIDE_METHOD_MSG);
        }
        // Copy of the global scope:
        Scope methodParent = new Scope(globalScope);
        // The new method scope:
        Scope newScope = new Scope(ScopeType.METHOD, methodParent);
        Method method = MethodValidator.validateMethodDeclaration(lineMatcher, globalMethods);
        for (Variable param: method.getArgs()){
            newScope.addToScope(param);
        }
        currentScope = newScope;
    }



    /* Parses a variable declaration or assignment line. */
    private void parseVarLine(Matcher lineMatcher) throws VarException {
        if(!(currentScope.getType() == ScopeType.GLOBAL && lineMatcher.group(SyntaxValidator.DEC_ID)!= null)){
            // We are only interested in lines that are not declarations in global scope:
            VarValidator.validateVarLine(lineMatcher, currentScope);
        }
    }



    /* Parses a method call. */
    private void parseMethodCall(Matcher lineMatcher) throws FlowException, ValidationException{
        if (currentScope == globalScope) {
            throw new FlowException(GLOBAL_METHOD_CALL_MSG);
        }
        MethodValidator.validateMethodCall(lineMatcher,currentScope,globalMethods);
    }



    /* Parses a return statement. */
    private void parseReturn() throws FlowException {
        if (currentScope == globalScope) {
            throw new FlowException(GLOBAL_RETURN_MSG);
        }
    }


    /* Parses an if\while statement. */
    private void parseCondition() throws FlowException, ValidationException {

        String line = getCurrentLine();
        if (currentScope == globalScope) {
            throw new FlowException(INVALID_GLOBAL_STATEMENT);
        }
        Matcher matcher = SyntaxValidator.checkIfNWhile(line);
        MethodValidator.validateIfNWhile(matcher, currentScope);
        ScopeType type;
        if(line.startsWith("if")){
            type = ScopeType.IF;
        }
        else{
            type = ScopeType.WHILE;
        }
        Scope newScope = new Scope(type, currentScope);
        currentScope = newScope;
    }


    /* Parses a scope closing. Should be called only when the current line is '}'. */
    private void parseScopeClose() throws FlowException {
        if (currentScope == globalScope) {
            throw new FlowException(UNBALANCED_BRACKETS_MSG);
        }

        // Check if we are closing a method:
        boolean closingMethod = (currentScope.getType() == ScopeType.METHOD);

        /*System.out.println("Closing scope. New parent is Global? "
                + (currentScope.getParentScope() == globalScope));*/
        currentScope = currentScope.getParentScope();

        if (closingMethod) {
            // If we reach the end of a method, go up to the global scope:
            currentScope = globalScope;
        }
    }


    /* Scans the input lines for method declarations and global variable definitions.
     * Checks for basic control flow validity. */
    private void firstPass() throws FlowException,
            ValidationException {

        while (hasMoreLines()){
            scanForDeclaration(getCurrentLine());
            advance();
        }
    }

    /* Scans a line for method or global variable declarations. */
    private void scanForDeclaration(String line) throws ValidationException, FlowException {

        // Check variable declaration:
        Matcher varMatcher = SyntaxValidator.checkVarLine(line);
        if (varMatcher != null) {

            // The line is a variable declaration or assignment:
            VarValidator.validateVarLine(varMatcher, globalScope);
            return;
        }
        Matcher methodDecMatcher = SyntaxValidator.checkMethodDec(line);
        if (methodDecMatcher != null) {
            MethodValidator.validateMethodDeclaration(methodDecMatcher, globalMethods);
            // Skip the method body for the first pass:
            advanceToMethodEnd();
            return;
        }

        if (line.equals("}")) {
            throw new FlowException(UNBALANCED_BRACKETS_MSG);
        }
    }
}