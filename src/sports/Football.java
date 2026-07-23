package sports;

/**
 * ============================================================
 * Football.java - Concrete Subclass of Sports
 * ============================================================
 * OOP Concept: INHERITANCE + POLYMORPHISM
 * ============================================================
 */
public class Football extends Sports {

    public Football() {
        super("Football", 11, 7);
    }

    @Override
    public String getSportRules() {
        return "Kick ball into opponent's goal. Highest goals wins.";
    }

    @Override
    public int getMatchDuration() {
        return 90; // 90 minutes standard match
    }

    @Override
    public String getScoringSystem() {
        return "1 point per goal scored.";
    }

    @Override
    public String toString() {
        return "Football - 11 players per team (min 7)";
    }
}
