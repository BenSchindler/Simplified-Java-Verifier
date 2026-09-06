package ex5.validator;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains the Regular Expressions used to identify if a line is a variable declaration, a method signature,
 * a method call, or a control block.
 */
public class SyntaxValidator {


    /** ID for type capture group. */
    public static final String TYPE_ID = "type";

    /** ID for final capture group. */
    public static final String FINAL_ID = "final";

    /** ID for a declaration capture group. */
    public static final String DEC_ID = "dec";




    // Variable name:
    private static final String VAR_NAME_REGEX = "_?[a-zA-Z]+[a-zA-Z0-9_]*";

    // Variable type:
    private static final String VAR_TYPE_REGEX = "int|boolean|String|double|char";

    // Variable declaration of the form "<type> <name>":
    private static final String VAR_DEC_REGEX =
            String.format("(%s)\\s+(%s)", VAR_TYPE_REGEX, VAR_NAME_REGEX);



    // Variable values:

    /** Regex for int value. */
    public static final String INT_VAL_REGEX = "[+-]?0*[0-9]+";

    /** Regex for double value. */
    public static final String DOUBLE_VAL_REGEX = "[+-]?0*(?:[0-9]+\\.[0-9]+|[0-9]+\\.|\\.[0-9]+)";

    /** Regex for String value. */
    public static final String STRING_VAL_REGEX = "\".*\"";

    /** Regex for boolean value. */
    public static final String BOOLEAN_VAL_REGEX = "true|false|%s|%s".formatted(INT_VAL_REGEX,
            DOUBLE_VAL_REGEX);

    /** Regex for char value. */
    public static final String CHAR_VAL_REGEX = "\'.\'";

    // Regex for general value of any type:
    private static final String GENERAL_VAL_REGEX = "(%s)|(%s)|(%s)|(%s)|(%s)|(%s)"
            .formatted(
                    DOUBLE_VAL_REGEX,
                    INT_VAL_REGEX,


                    STRING_VAL_REGEX,

                    BOOLEAN_VAL_REGEX,

                    CHAR_VAL_REGEX,
                    VAR_NAME_REGEX
    );



    // Assignment:
    private static final String ASSIGNMENT_REGEX = "\\s*=\\s*(%s)+\\s*".formatted(GENERAL_VAL_REGEX);


    /* Regex for a single vairable declaration. */
    public static final String VAR_REGEX = "\\s*(%s)(?:%s)?\\s*"
            .formatted(VAR_NAME_REGEX, ASSIGNMENT_REGEX);

    // Regex for full variable declaration or assignment line:
    private static final String FULL_VAR_REGEX =
            "(?<%s>\\s*(?<%s>final\\s+)?(?<%s>%s)\\s*)?(%s)\\s*(?:,\\s*(?:%s))*;"
                    .formatted(DEC_ID, FINAL_ID, TYPE_ID, VAR_TYPE_REGEX, VAR_REGEX, VAR_REGEX);




    public static final String IF_N_WHILE_REGEX =
            "(?:if|while)\\s*\\(\\s*((?:%s|%s)\\s*(?:(?:\\s*(?:\\|\\|)|(?:&&))\\s*(?:%s|%s)\\s*)*\\s*)\\s*\\)\\s*\\{\\s*"
            .formatted(BOOLEAN_VAL_REGEX, VAR_NAME_REGEX, BOOLEAN_VAL_REGEX, VAR_NAME_REGEX);


    private static final String METHOD_NAME_REGEX = "[a-zA-Z]+[a-zA-Z0-9_]*";
    private static final String METHOD_DEC_PARAM = "\\s*(final\\s+)?(?:%s)\\s+(?:%s)".formatted(
            VAR_TYPE_REGEX, VAR_NAME_REGEX);
    private static final String METHOD_CALL_PARAM = "%s|%s".formatted(VAR_NAME_REGEX, GENERAL_VAL_REGEX);

    private static final String METHOD_DEC_REGEX =
            "\\s*void\\s+(%s)\\s*\\((\\s*(?:%s)(\\s*,\\s*(?:%s))*)?\\s*\\)\\s*\\{\\s*"
            .formatted(METHOD_NAME_REGEX, METHOD_DEC_PARAM, METHOD_DEC_PARAM);

    private static final String METHOD_CALL_REGEX =
            "\\s*(%s)\\s*\\(\\s*(\\s*(?:%s)\\s*(?:,\\s*(?:%s)\\s*)*\\s*)?\\s*\\)\\s*;\\s*"
                    .formatted(METHOD_NAME_REGEX, METHOD_CALL_PARAM, METHOD_CALL_PARAM);

    private static final String COMMENT_REGEX = "//.*";




    /** Pattern for variable declaration\assignment. */
    public static final Pattern VAR_LINE_PATTERN = Pattern.compile(FULL_VAR_REGEX);

    /** Pattern for a single variable in a line. */
    public static final Pattern SINGLE_VAR_PATTERN = Pattern.compile(VAR_REGEX);

    /** Pattern for while and if statements. */
    public static final Pattern CONDITION_PATTERN = Pattern.compile(IF_N_WHILE_REGEX);

    /** Pattern for method declaration. */
    public static final Pattern METHOD_DEC_PATTERN = Pattern.compile(METHOD_DEC_REGEX);

    /** Pattern for method calls. */
    public static final Pattern METHOD_CALL_PATTERN = Pattern.compile(METHOD_CALL_REGEX);

    /* Pattern for commants. */
    private static final Pattern IGNORE_PATTERN = Pattern.compile("(?:\\s*)|(?:%s)".formatted(COMMENT_REGEX));

    /* Pattern for return statement. */
    private static final Pattern RETURN_PATTERN = Pattern.compile("\\s*return\\s*;\\s*");




    /**
     * Checks if a given line is a variable declaration.
     * @param line An input line.
     * @return True if the line is a variable declaration line.
     */
    public static Matcher checkVarLine(String line) {

        return generateMatcher(VAR_LINE_PATTERN, line);
    }


    /**
     * Checks if a given line is an if statement.
     * @param line An input line.
     * @return True if the line is an if statement.
     */
    public static Matcher checkIfNWhile(String line) {

        return generateMatcher(CONDITION_PATTERN, line);
    }


    /**
     * Checks if a given line is a method declaration statement.
     * @param line An input line.
     * @return True if the given line is a method declaration.
     */
    public static Matcher checkMethodDec(String line) {
        return generateMatcher(METHOD_DEC_PATTERN, line);
    }


    /**
     * Checks if a given line is a comment.
     * @param line An input line.
     * @return True if the given line is a comment.
     */
    public static Matcher checkIgnore(String line) {

        return generateMatcher(IGNORE_PATTERN, line);
    }

    /**
     * Checks if a given line is a method call.
     * @param line An input line.
     * @return True if the given line is a method call.
     */
    public static Matcher checkMethodCall(String line) {

        return generateMatcher(METHOD_CALL_PATTERN, line);
    }

    /**
     * Checks if a given line is a return statement.
     * @param line An input line.
     * @return True if the given line is a method call.
     */
    public static Matcher checkReturn(String line) {

        return generateMatcher(RETURN_PATTERN, line);
    }


    /* Creates a matcher for a given line. */
    private static Matcher generateMatcher(Pattern pattern, String line) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            return matcher;
        }
        return null;
    }



}
