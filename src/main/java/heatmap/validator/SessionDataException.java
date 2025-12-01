package heatmap.validator;

/**
 * Base exception for session data-related errors.
 */
public class SessionDataException extends Exception {
    public SessionDataException(String message) {
        super(message);
    }

    public SessionDataException(String message, Throwable cause) {
        super(message, cause);
    }
}