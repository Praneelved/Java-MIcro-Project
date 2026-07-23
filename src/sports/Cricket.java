package sports;

/**
 * ============================================================
 * Cricket.java - Concrete Subclass of Sports
 * ============================================================
 * OOP Concept: INHERITANCE + POLYMORPHISM
 * Cricket inherits from Sports and provides concrete
 * implementations of all abstract methods.
 * ============================================================
 */
public class Cricket extends Sports {

    // Constructor calls super() to initialize parent fields
    public Cricket() {
        super("Cricket", 11, 11);
    }

    // OOP Concept: METHOD OVERRIDING (Polymorphism)
    @Override
    public String getSportRules() {
        return "Bat and ball game. Team with highest runs wins.";
    }

    @Override
    public int getMatchDuration() {
        return 480; // T20 is ~3 hours; ODI ~8 hours; using T20 base for demo
    }

    @Override
    public String getScoringSystem() {
        return "Runs scored by batsmen; wickets taken by bowlers.";
    }

    @Override
    public String toString() {
        return "Cricket - 11 players per team";
    }
}
