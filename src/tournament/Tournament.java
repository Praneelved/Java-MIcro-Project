package tournament;

import team.Team;
import match.Match;
import java.util.ArrayList;

/**
 * ============================================================
 * Tournament.java - Abstract Class
 * ============================================================
 * OOP Concept: ABSTRACTION, INHERITANCE
 * Abstract parent for all tournament types.
 * Cannot be instantiated directly.
 * LeagueTournament and KnockoutTournament extend this class.
 * Also implements TournamentRules interface.
 * ============================================================
 */
public abstract class Tournament implements TournamentRules {

    // Private/Protected fields - Encapsulation
    private static int tournamentIdCounter = 1;
    protected int tournamentId;
    protected String tournamentName;
    protected String sport;
    protected String startDate;
    protected String endDate;
    protected String format; // "League" or "Knockout"
    protected boolean isActive;

    // Collections Framework: ArrayList for teams and matches
    protected ArrayList<Team>  teams;
    protected ArrayList<Match> matches;
    protected PointsTable pointsTable;

    // -------------------------------------------------------
    // Constructor
    // -------------------------------------------------------
    public Tournament(String tournamentName, String sport, String startDate, String endDate, String format) {
        this.tournamentId   = tournamentIdCounter++;
        this.tournamentName = tournamentName;
        this.sport          = sport;
        this.startDate      = startDate;
        this.endDate        = endDate;
        this.format         = format;
        this.isActive       = true;
        this.teams          = new ArrayList<>();
        this.matches        = new ArrayList<>();
        this.pointsTable    = new PointsTable();
    }

    // -------------------------------------------------------
    // Add team to tournament
    // -------------------------------------------------------
    public boolean addTeam(Team team) {
        for (Team t : teams) {
            if (t.getTeamId() == team.getTeamId()) {
                System.out.println("  [ERROR] Team '" + team.getTeamName() + "' already in tournament.");
                return false;
            }
        }
        teams.add(team);
        System.out.println("  [SUCCESS] Team '" + team.getTeamName() + "' added to " + tournamentName);
        return true;
    }

    // -------------------------------------------------------
    // Remove team from tournament
    // -------------------------------------------------------
    public boolean removeTeam(String teamName) {
        for (Team t : teams) {
            if (t.getTeamName().equalsIgnoreCase(teamName)) {
                teams.remove(t);
                System.out.println("  [SUCCESS] Team '" + teamName + "' removed.");
                return true;
            }
        }
        System.out.println("  [ERROR] Team '" + teamName + "' not found.");
        return false;
    }

    // -------------------------------------------------------
    // Find match by ID
    // -------------------------------------------------------
    public Match findMatchById(int id) {
        for (Match m : matches) {
            if (m.getMatchId() == id) return m;
        }
        return null;
    }

    // -------------------------------------------------------
    // Display all upcoming matches
    // -------------------------------------------------------
    public void displayUpcomingMatches() {
        System.out.println("\n  >>> UPCOMING MATCHES <<<");
        boolean found = false;
        for (Match m : matches) {
            if (m.getStatus() == Match.Status.SCHEDULED) {
                m.displayMatch();
                found = true;
            }
        }
        if (!found) System.out.println("  No upcoming matches.");
    }

    // -------------------------------------------------------
    // Display all completed matches
    // -------------------------------------------------------
    public void displayCompletedMatches() {
        System.out.println("\n  >>> COMPLETED MATCHES <<<");
        boolean found = false;
        for (Match m : matches) {
            if (m.getStatus() == Match.Status.COMPLETED) {
                m.displayMatch();
                found = true;
            }
        }
        if (!found) System.out.println("  No completed matches yet.");
    }

    // -------------------------------------------------------
    // Display all teams in tournament
    // -------------------------------------------------------
    public void displayTeams() {
        System.out.println("\n  >>> REGISTERED TEAMS IN: " + tournamentName + " <<<");
        if (teams.isEmpty()) {
            System.out.println("  No teams registered yet.");
            return;
        }
        for (int i = 0; i < teams.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + teams.get(i));
        }
    }

    // -------------------------------------------------------
    // Abstract methods implemented by subclasses (Polymorphism)
    // -------------------------------------------------------
    @Override
    public abstract void generateFixtures();

    @Override
    public abstract void calculateStandings();

    @Override
    public abstract String declareWinner();

    @Override
    public abstract boolean isTournamentOver();

    // Concrete implementation of displayTournamentInfo
    @Override
    public void displayTournamentInfo() {
        System.out.println("===========================================");
        System.out.println("  Tournament  : " + tournamentName);
        System.out.println("  Sport       : " + sport);
        System.out.println("  Format      : " + format);
        System.out.println("  Start Date  : " + startDate);
        System.out.println("  End Date    : " + endDate);
        System.out.println("  Teams       : " + teams.size());
        System.out.println("  Matches     : " + matches.size());
        System.out.println("  Status      : " + (isActive ? "ACTIVE" : "COMPLETED"));
        System.out.println("===========================================");
    }

    // -------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------
    public int getTournamentId()              { return tournamentId; }
    public void setTournamentId(int id)       { this.tournamentId = id; }
    public String getTournamentName()         { return tournamentName; }
    public String getSport()                  { return sport; }
    public String getStartDate()              { return startDate; }
    public String getEndDate()                { return endDate; }
    public String getFormat()                 { return format; }
    public boolean isActive()                 { return isActive; }
    public void setActive(boolean active)     { this.isActive = active; }
    public ArrayList<Team> getTeams()         { return teams; }
    public ArrayList<Match> getMatches()      { return matches; }
    public PointsTable getPointsTable()       { return pointsTable; }

    @Override
    public String toString() {
        return String.format("[ID:%d] %s | %s | %s | Teams: %d",
                tournamentId, tournamentName, sport, format, teams.size());
    }

    public static void resetIdCounter() { tournamentIdCounter = 1; }
}
