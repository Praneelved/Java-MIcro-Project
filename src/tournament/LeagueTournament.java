package tournament;

import team.Team;
import match.Match;

/**
 * ============================================================
 * LeagueTournament.java
 * ============================================================
 * OOP Concept: INHERITANCE, POLYMORPHISM
 * Extends Tournament (inherits all fields and methods).
 * Implements League-specific logic:
 *   - Every team plays every other team twice (home & away)
 *   - Points decide the final standings
 *   - No elimination; team with most points wins
 * ============================================================
 */
public class LeagueTournament extends Tournament {

    private int roundNumber; // Track current round

    // Constructor - calls super() to initialize parent
    public LeagueTournament(String name, String sport, String startDate, String endDate) {
        super(name, sport, startDate, endDate, "League");
        this.roundNumber = 1;
    }

    /**
     * OOP Concept: METHOD OVERRIDING
     * Generate round-robin fixtures (every team vs every other team)
     */
    @Override
    public void generateFixtures() {
        if (teams.size() < 2) {
            System.out.println("  [ERROR] Need at least 2 teams to generate fixtures.");
            return;
        }

        matches.clear(); // Clear old fixtures
        int matchDay = 1;

        System.out.println("\n  Generating League Fixtures (Round Robin)...");

        // Round-robin: each team plays against every other team
        for (int i = 0; i < teams.size(); i++) {
            for (int j = i + 1; j < teams.size(); j++) {
                Team home = teams.get(i);
                Team away = teams.get(j);

                // Create match
                String date = "Matchday " + matchDay;
                Match m = new Match(home, away, date, "Stadium " + matchDay, "League Stage");
                matches.add(m);
                matchDay++;

                // Optional: Return fixture (home & away for each pair)
                // Uncomment for full double round-robin:
                // Match m2 = new Match(away, home, "Matchday " + matchDay, "Stadium " + matchDay, "League Stage");
                // matches.add(m2);
                // matchDay++;
            }
        }
        System.out.println("  [SUCCESS] Generated " + matches.size() + " fixtures.");
    }

    /**
     * OOP Concept: METHOD OVERRIDING
     * Calculate points from all completed matches.
     */
    @Override
    public void calculateStandings() {
        pointsTable.initializeTeams(teams);
        pointsTable.updateFromMatches(matches);
    }

    /**
     * OOP Concept: METHOD OVERRIDING
     * Declare winner as team with most points.
     */
    @Override
    public String declareWinner() {
        calculateStandings();

        // Check how many matches are completed (simple for loop - no Streams needed)
        int completedCount = 0;
        for (Match m : matches) {
            if (m.getStatus() == Match.Status.COMPLETED) {
                completedCount++;
            }
        }

        if (completedCount == 0) {
            return "Tournament has not started yet.";
        }

        String leader = pointsTable.getLeader();
        if (leader != null && !leader.isEmpty()) {
            return "🏆 LEAGUE WINNER: " + leader;
        }
        return "Winner not determined yet.";
    }

    /**
     * OOP Concept: METHOD OVERRIDING
     * Check if all matches are completed.
     */
    @Override
    public boolean isTournamentOver() {
        if (matches.isEmpty()) return false;
        for (Match m : matches) {
            if (m.getStatus() != Match.Status.COMPLETED) return false;
        }
        return true;
    }

    /**
     * OOP Concept: METHOD OVERRIDING
     * Display league-specific tournament info.
     */
    @Override
    public void displayTournamentInfo() {
        super.displayTournamentInfo(); // Call parent method (Polymorphism)
        System.out.println("  [LEAGUE] Every team plays every other team once.");
        System.out.println("  [LEAGUE] Winner = Team with most points.");
        System.out.println("  Round Number: " + roundNumber);
    }

    public int getRoundNumber() { return roundNumber; }
    public void nextRound()     { roundNumber++; }
}
