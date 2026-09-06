package ex5.main;


/**
 * Exception for control flow errors.
 */
public class FlowException extends CodeException {
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public FlowException(String message) {
        super(message);
    }
}
