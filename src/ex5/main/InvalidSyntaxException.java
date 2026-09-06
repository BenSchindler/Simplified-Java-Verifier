package ex5.main;

/**
 * Exception for syntax errors.
 */
public class InvalidSyntaxException extends CodeException {
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public InvalidSyntaxException(String message) {
        super(message);
    }
}
