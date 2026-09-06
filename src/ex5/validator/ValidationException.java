package ex5.validator;


import ex5.main.CodeException;

/**
 * Validation related exception.
 */
public class ValidationException extends CodeException {
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public ValidationException(String message) {
        super(message);
    }
}
