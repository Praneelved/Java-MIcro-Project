package exceptions;

/**
 * ============================================================
 * InvalidPlayerException.java - Custom Exception
 * ============================================================
 * OOP Concept: EXCEPTION HANDLING
 * Custom exception class for invalid player operations.
 * Extends RuntimeException (unchecked exception).
 * ============================================================
 */
public class InvalidPlayerException extends RuntimeException {

    private int errorCode;

    // Constructor 1 - just message
    public InvalidPlayerException(String message) {
        super(message);
        this.errorCode = 0;
    }

    // Constructor 2 - message with error code (Method Overloading)
    public InvalidPlayerException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() { return errorCode; }

    @Override
    public String toString() {
        return "InvalidPlayerException [Code: " + errorCode + "]: " + getMessage();
    }
}
