package sports;

/**
 * ============================================================
 * Basketball.java - Concrete Subclass of Sports
 * ============================================================
 * OOP Concept: INHERITANCE + POLYMORPHISM
 * ============================================================
 */
public class Basketball extends Sports {

    public Basketball() {
        // Basketball is played with 5 players per team on court, minimum 3 to avoid forfeit.
        super("Basketball", 5, 3);
    }

    @Override
    public String getSportRules() {
        return "Dribble and shoot the ball into the opponent's hoop. Highest score wins.";
    }

    @Override
    public int getMatchDuration() {
        return 48; // 48 minutes standard NBA match (4 quarters of 12 mins)
    }

    @Override
    public String getScoringSystem() {
        return "2 points for standard field goals, 3 points from beyond the arc, 1 point per free throw.";
    }

    @Override
    public String toString() {
        return "Basketball - 5 players per team (min 3)";
    }
}
