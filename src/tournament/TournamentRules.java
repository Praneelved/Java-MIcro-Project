package tournament;

/**
 * ============================================================
 * TournamentRules.java - Interface
 * ============================================================
 * OOP Concept: INTERFACE (Abstraction Contract)
 * An interface defines a contract that any implementing class
 * MUST follow. It supports multiple inheritance in Java.
 * Both LeagueTournament and KnockoutTournament implement this.
 * ============================================================
 */
public interface TournamentRules {

    // Interface constants (implicitly public static final)
    int WIN_POINTS    = 3;
    int DRAW_POINTS   = 1;
    int LOSS_POINTS   = 0;

    // Abstract methods that every tournament type must implement
    void generateFixtures();     // Generate match schedule
    void calculateStandings();   // Update points table
    String declareWinner();      // Determine and return winner
    boolean isTournamentOver();  // Check if tournament has ended
    void displayTournamentInfo();// Show tournament details
}
