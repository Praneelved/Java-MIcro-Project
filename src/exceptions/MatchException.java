package exceptions;

/**
 * ============================================================
 * MatchException.java - Custom Exception
 * ============================================================
 * OOP Concept: EXCEPTION HANDLING
 * This is a custom checked exception for invalid match
 * operations. For example: trying to update a score for
 * a match that is already completed.
 *
 * It extends Exception (checked), so the caller MUST
 * handle it using try-catch or declare it with throws.
 * ============================================================
 */
public class MatchException extends Exception {

    // Extra field to store what kind of error happened
    private int matchId;

    // Constructor 1 - just message
    public MatchException(String message) {
        super(message);
        this.matchId = -1; // -1 means no specific match
    }

    // Constructor 2 - message + match ID (Method Overloading)
    public MatchException(String message, int matchId) {
        super(message);
        this.matchId = matchId;
    }

    // Getter
    public int getMatchId() {
        return matchId;
    }

    // Override toString for better error display
    @Override
    public String toString() {
        if (matchId == -1) {
            return "MatchException: " + getMessage();
        }
        return "MatchException [Match ID: " + matchId + "]: " + getMessage();
    }
}
