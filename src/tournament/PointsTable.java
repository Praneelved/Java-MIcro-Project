package tournament;

import team.Team;
import match.Match;
import java.util.ArrayList;

/**
 * ============================================================
 * PointsTable.java
 * ============================================================
 * OOP Concept: CLASS, ENCAPSULATION, COLLECTIONS FRAMEWORK
 * Tracks team statistics: wins, losses, draws, and points.
 * Uses HashMap for O(1) lookup of team records.
 * ============================================================
 */
public class PointsTable {

    /**
     * Inner class to hold a team's record.
     * OOP Concept: Inner Class / Composition
     */
    public static class TeamRecord {
        String teamName;
        int played;
        int wins;
        int losses;
        int draws;
        int points;
        int goalsFor;
        int goalsAgainst;

        public TeamRecord(String teamName) {
            this.teamName = teamName;
            this.played = this.wins = this.losses = this.draws = 0;
            this.points = this.goalsFor = this.goalsAgainst = 0;
        }

        public int getGoalDifference() {
            return goalsFor - goalsAgainst;
        }

        @Override
        public String toString() {
            return String.format("%-20s | P:%-3d W:%-3d L:%-3d D:%-3d | GF:%-3d GA:%-3d GD:%-4d | Pts:%-3d",
                    teamName, played, wins, losses, draws,
                    goalsFor, goalsAgainst, getGoalDifference(), points);
        }
    }

    // Collections Framework: ArrayList to store records (for sorted display)
    private ArrayList<TeamRecord> records;

    // Constructor
    public PointsTable() {
        this.records = new ArrayList<>();
    }

    // -------------------------------------------------------
    // Initialize teams in the table
    // -------------------------------------------------------
    public void initializeTeams(ArrayList<Team> teams) {
        records.clear();
        for (Team t : teams) {
            records.add(new TeamRecord(t.getTeamName()));
        }
    }

    // -------------------------------------------------------
    // Update table from a list of completed matches
    // -------------------------------------------------------
    public void updateFromMatches(ArrayList<Match> matches) {
        // Reset all records first
        for (TeamRecord r : records) {
            r.played = r.wins = r.losses = r.draws = 0;
            r.points = r.goalsFor = r.goalsAgainst = 0;
        }

        // Process each completed match
        for (Match m : matches) {
            if (m.getStatus() != Match.Status.COMPLETED) continue;

            TeamRecord home = getRecord(m.getHomeTeam().getTeamName());
            TeamRecord away = getRecord(m.getAwayTeam().getTeamName());

            if (home == null || away == null) continue;

            home.played++;
            away.played++;
            home.goalsFor      += m.getHomeScore();
            home.goalsAgainst  += m.getAwayScore();
            away.goalsFor      += m.getAwayScore();
            away.goalsAgainst  += m.getHomeScore();

            switch (m.getResult()) {
                case HOME_WIN:
                    home.wins++;   home.points += TournamentRules.WIN_POINTS;
                    away.losses++; away.points += TournamentRules.LOSS_POINTS;
                    break;
                case AWAY_WIN:
                    away.wins++;   away.points += TournamentRules.WIN_POINTS;
                    home.losses++; home.points += TournamentRules.LOSS_POINTS;
                    break;
                case DRAW:
                    home.draws++; home.points += TournamentRules.DRAW_POINTS;
                    away.draws++; away.points += TournamentRules.DRAW_POINTS;
                    break;
                default:
                    break;
            }
        }
        sortByPoints(); // Sort after updating
    }

    // -------------------------------------------------------
    // Find record by team name
    // -------------------------------------------------------
    private TeamRecord getRecord(String teamName) {
        for (TeamRecord r : records) {
            if (r.teamName.equalsIgnoreCase(teamName)) return r;
        }
        return null;
    }

    // -------------------------------------------------------
    // Sort records by points (descending), then goal difference
    // Bubble Sort for clarity (beginner-friendly)
    // -------------------------------------------------------
    private void sortByPoints() {
        int n = records.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                TeamRecord a = records.get(j);
                TeamRecord b = records.get(j + 1);
                boolean shouldSwap = a.points < b.points ||
                        (a.points == b.points && a.getGoalDifference() < b.getGoalDifference());
                if (shouldSwap) {
                    records.set(j, b);
                    records.set(j + 1, a);
                }
            }
        }
    }

    // -------------------------------------------------------
    // Display Points Table
    // -------------------------------------------------------
    public void displayTable() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                         POINTS TABLE                                    ║");
        System.out.println("╠══════════════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ %-3s %-20s | %-3s %-3s %-3s %-3s | %-3s %-3s %-4s | %-4s ║%n",
                "#", "Team", "P", "W", "L", "D", "GF", "GA", "GD", "Pts");
        System.out.println("╠══════════════════════════════════════════════════════════════════════════╣");

        for (int i = 0; i < records.size(); i++) {
            TeamRecord r = records.get(i);
            System.out.printf("║ %-3d %-20s | %-3d %-3d %-3d %-3d | %-3d %-3d %-4d | %-4d ║%n",
                    (i + 1), r.teamName, r.played, r.wins, r.losses, r.draws,
                    r.goalsFor, r.goalsAgainst, r.getGoalDifference(), r.points);
        }
        System.out.println("╚══════════════════════════════════════════════════════════════════════════╝");
    }

    // -------------------------------------------------------
    // Get top-ranked team (leader)
    // -------------------------------------------------------
    public String getLeader() {
        if (records.isEmpty()) return "No teams registered.";
        sortByPoints();
        return records.get(0).teamName;
    }

    // Get all records (for use by winner declaration)
    public ArrayList<TeamRecord> getRecords() {
        return records;
    }
}
