package exceptions;

/**
 * ============================================================
 * TournamentException.java - Custom Exception
 * ============================================================
 * OOP Concept: EXCEPTION HANDLING
 * Custom exception for invalid tournament operations.
 * ============================================================
 */
public class TournamentException extends Exception {

    public TournamentException(String message) {
        super(message);
    }

    public TournamentException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String toString() {
        return "TournamentException: " + getMessage();
    }
}
