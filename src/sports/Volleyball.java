package sports;

/**
 * ============================================================
 * Volleyball.java - Concrete Subclass of Sports
 * ============================================================
 * OOP Concept: INHERITANCE + POLYMORPHISM
 * ============================================================
 */
public class Volleyball extends Sports {

    public Volleyball() {
        super("Volleyball", 6, 6);
    }

    @Override
    public String getSportRules() {
        return "Hit ball over net. Win 3 sets to win match.";
    }

    @Override
    public int getMatchDuration() {
        return 90; // Approximate
    }

    @Override
    public String getScoringSystem() {
        return "Rally point system. First to 25 points (with 2-point lead) wins a set.";
    }

    @Override
    public String toString() {
        return "Volleyball - 6 players per team";
    }
}
