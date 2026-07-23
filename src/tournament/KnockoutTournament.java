package tournament;

import team.Team;
import match.Match;
import java.util.ArrayList;

/**
 * ============================================================
 * KnockoutTournament.java
 * ============================================================
 * OOP Concept: INHERITANCE, POLYMORPHISM
 * Extends Tournament (inherits all fields and methods).
 * Implements Knockout (Single-Elimination) logic:
 *   - Losing team is eliminated
 *   - Winners advance to next round
 *   - Final match winner is the tournament champion
 * ============================================================
 */
public class KnockoutTournament extends Tournament {

    private int currentRound;
    private ArrayList<Team> remainingTeams; // Teams still in competition
    private String champion;               // Final winner

    // Constructor
    public KnockoutTournament(String name, String sport, String startDate, String endDate) {
        super(name, sport, startDate, endDate, "Knockout");
        this.currentRound    = 1;
        this.remainingTeams  = new ArrayList<>();
        this.champion        = "";
    }

    /**
     * OOP Concept: METHOD OVERRIDING
     * Generate knockout brackets for the current round.
     */
    @Override
    public void generateFixtures() {
        if (teams.size() < 2) {
            System.out.println("  [ERROR] Need at least 2 teams to generate brackets.");
            return;
        }

        // Power of 2 check (for ideal knockout bracket)
        if ((teams.size() & (teams.size() - 1)) != 0) {
            System.out.println("  [WARNING] Knockout works best with 2, 4, 8, or 16 teams.");
        }

        remainingTeams = new ArrayList<>(teams); // All teams start active
        matches.clear();
        generateRoundFixtures(remainingTeams, getRoundLabel(teams.size()));
        System.out.println("  [SUCCESS] Generated Round 1 with " + matches.size() + " matches.");
    }

    /**
     * Generate fixtures for a specific list of teams in one round.
     */
    private void generateRoundFixtures(ArrayList<Team> roundTeams, String roundName) {
        for (int i = 0; i < roundTeams.size() - 1; i += 2) {
            Team home = roundTeams.get(i);
            Team away = roundTeams.get(i + 1);
            Match m = new Match(home, away, "Round " + currentRound, roundName);
            matches.add(m);
        }
    }

    /**
     * Advance to next knockout round based on winners.
     */
    public void advanceToNextRound() {
        ArrayList<Team> winners = new ArrayList<>();

        // Collect winners of current round
        for (Match m : matches) {
            if (m.getStatus() == Match.Status.COMPLETED && m.getResult() != Match.Result.DRAW) {
                Team winner = m.getWinner();
                if (winner != null && !winners.contains(winner)) {
                    winners.add(winner);
                    System.out.println("  >> " + winner.getTeamName() + " advances to next round!");
                }
            }
        }

        if (winners.size() == 1) {
            champion = winners.get(0).getTeamName();
            System.out.println("\n  🏆 CHAMPION: " + champion);
            setActive(false);
            return;
        }

        if (winners.size() < 2) {
            System.out.println("  [INFO] Complete all current round matches first.");
            return;
        }

        currentRound++;
        remainingTeams = winners;

        // Generate next round fixtures
        String nextRoundLabel = getRoundLabel(winners.size());
        generateRoundFixtures(winners, nextRoundLabel);
        System.out.println("  [SUCCESS] " + nextRoundLabel + " fixtures generated!");
    }

    /**
     * Get round name based on number of remaining teams.
     */
    private String getRoundLabel(int teamCount) {
        if (teamCount <= 2)  return "FINAL";
        if (teamCount <= 4)  return "SEMI-FINAL";
        if (teamCount <= 8)  return "QUARTER-FINAL";
        return "ROUND OF " + teamCount;
    }

    /**
     * OOP Concept: METHOD OVERRIDING
     * Knockout format: no traditional points, just wins/losses.
     */
    @Override
    public void calculateStandings() {
        pointsTable.initializeTeams(teams);
        pointsTable.updateFromMatches(matches);
    }

    /**
     * OOP Concept: METHOD OVERRIDING
     */
    @Override
    public String declareWinner() {
        if (!champion.isEmpty()) {
            return "🏆 KNOCKOUT CHAMPION: " + champion;
        }
        // If only 1 remaining after logic
        if (remainingTeams.size() == 1) {
            champion = remainingTeams.get(0).getTeamName();
            return "🏆 KNOCKOUT CHAMPION: " + champion;
        }
        return "Tournament is still in progress. Advance rounds to find the winner!";
    }

    /**
     * OOP Concept: METHOD OVERRIDING
     */
    @Override
    public boolean isTournamentOver() {
        return !champion.isEmpty();
    }

    /**
     * OOP Concept: METHOD OVERRIDING
     */
    @Override
    public void displayTournamentInfo() {
        super.displayTournamentInfo(); // Call parent's method
        System.out.println("  [KNOCKOUT] Single elimination format.");
        System.out.println("  [KNOCKOUT] Current Round: " + currentRound);
        System.out.println("  [KNOCKOUT] Teams Remaining: " + remainingTeams.size());
        System.out.println("  [KNOCKOUT] Champion: " + (champion.isEmpty() ? "TBD" : champion));
    }

    // Getters
    public int getCurrentRound()             { return currentRound; }
    public String getChampion()              { return champion; }
    public ArrayList<Team> getRemainingTeams(){ return remainingTeams; }
}
