package sports;

/**
 * ============================================================
 * Badminton.java - Concrete Subclass of Sports
 * ============================================================
 * OOP Concept: INHERITANCE + POLYMORPHISM
 * ============================================================
 */
public class Badminton extends Sports {

    public Badminton() {
        super("Badminton", 2, 1); // Singles or doubles
    }

    @Override
    public String getSportRules() {
        return "Hit shuttlecock over net. Best of 3 games wins.";
    }

    @Override
    public int getMatchDuration() {
        return 60; // Approximately 1 hour
    }

    @Override
    public String getScoringSystem() {
        return "First to 21 points wins a game (2-point lead required).";
    }

    @Override
    public String toString() {
        return "Badminton - 1 or 2 players per side";
    }
}
