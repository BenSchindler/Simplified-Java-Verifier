package ex5.validator;


/**
 * Exception for final variables that are given a value after declaration.
 */
public class ModifiedFinalVarException extends VarException {

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public ModifiedFinalVarException(String message) {
        super(message);
    }
}
