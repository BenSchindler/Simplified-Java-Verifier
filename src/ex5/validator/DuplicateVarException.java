package ex5.validator;

/**
 * A duplicate variable exception.
 */
public class DuplicateVarException extends VarDecException {
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public DuplicateVarException(String message) {
        super(message);
    }
}
