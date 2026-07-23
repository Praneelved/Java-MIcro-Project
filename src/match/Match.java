package match;

import team.Team;

/**
 * ============================================================
 * Match.java
 * ============================================================
 * OOP Concept: CLASS, ENCAPSULATION
 * Represents a match between two teams.
 * Tracks scores, result, and match status.
 * ============================================================
 */
public class Match {

    // Enum to represent match status
    public enum Status { SCHEDULED, IN_PROGRESS, COMPLETED }
    public enum Result { HOME_WIN, AWAY_WIN, DRAW, NOT_PLAYED }

    // Private fields - Encapsulation
    private static int matchIdCounter = 1;
    private int matchId;
    private Team homeTeam;
    private Team awayTeam;
    private int homeScore;
    private int awayScore;
    private String scheduledDate;
    private String venue;
    private Status status;
    private Result result;
    private String roundName; // "Group Stage", "Semi-Final", "Final"

    // -------------------------------------------------------
    // Constructor
    // -------------------------------------------------------
    public Match(Team homeTeam, Team awayTeam, String scheduledDate, String venue, String roundName) {
        this.matchId       = matchIdCounter++;
        this.homeTeam      = homeTeam;
        this.awayTeam      = awayTeam;
        this.homeScore     = 0;
        this.awayScore     = 0;
        this.scheduledDate = scheduledDate;
        this.venue         = venue;
        this.status        = Status.SCHEDULED;
        this.result        = Result.NOT_PLAYED;
        this.roundName     = roundName;
    }

    // Overloaded Constructor (without venue)
    public Match(Team homeTeam, Team awayTeam, String scheduledDate, String roundName) {
        this(homeTeam, awayTeam, scheduledDate, "TBD", roundName);
    }

    // -------------------------------------------------------
    // Update Match Score
    // -------------------------------------------------------
    public void updateScore(int homeScore, int awayScore) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.status = Status.COMPLETED;

        // Automatically determine result
        if (homeScore > awayScore) {
            this.result = Result.HOME_WIN;
        } else if (awayScore > homeScore) {
            this.result = Result.AWAY_WIN;
        } else {
            this.result = Result.DRAW;
        }
    }

    // -------------------------------------------------------
    // Get winner team (null if draw or not played)
    // -------------------------------------------------------
    public Team getWinner() {
        if (result == Result.HOME_WIN) return homeTeam;
        if (result == Result.AWAY_WIN) return awayTeam;
        return null;
    }

    // -------------------------------------------------------
    // Get loser team
    // -------------------------------------------------------
    public Team getLoser() {
        if (result == Result.HOME_WIN) return awayTeam;
        if (result == Result.AWAY_WIN) return homeTeam;
        return null;
    }

    // -------------------------------------------------------
    // Display Match Summary
    // -------------------------------------------------------
    public void displayMatch() {
        System.out.printf("  Match #%-3d | %-18s | Round: %-12s%n",
                matchId, scheduledDate, roundName);
        System.out.printf("             | %-15s vs %-15s%n",
                homeTeam.getTeamName(), awayTeam.getTeamName());
        if (status == Status.COMPLETED) {
            System.out.printf("             | Score: %d - %d  |  Result: %s%n",
                    homeScore, awayScore, getResultString());
        } else {
            System.out.printf("             | Status: %-15s | Venue: %s%n",
                    status.toString(), venue);
        }
        System.out.println("             +-----------------------------------------+");
    }

    // Return result as readable string
    public String getResultString() {
        switch (result) {
            case HOME_WIN: return homeTeam.getTeamName() + " WON";
            case AWAY_WIN: return awayTeam.getTeamName() + " WON";
            case DRAW:     return "DRAW";
            default:       return "Not Played";
        }
    }

    // -------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------
    public int getMatchId()              { return matchId; }
    public void setMatchId(int id)       { this.matchId = id; }
    public Team getHomeTeam()            { return homeTeam; }
    public Team getAwayTeam()            { return awayTeam; }
    public int getHomeScore()            { return homeScore; }
    public int getAwayScore()            { return awayScore; }
    public String getScheduledDate()     { return scheduledDate; }
    public void setScheduledDate(String d){ this.scheduledDate = d; }
    public String getVenue()             { return venue; }
    public void setVenue(String venue)   { this.venue = venue; }
    public Status getStatus()            { return status; }
    public void setStatus(Status s)      { this.status = s; }
    public Result getResult()            { return result; }
    public String getRoundName()         { return roundName; }
    public void setRoundName(String r)   { this.roundName = r; }

    @Override
    public String toString() {
        return String.format("Match#%d: %s vs %s on %s [%s]",
                matchId, homeTeam.getTeamName(), awayTeam.getTeamName(),
                scheduledDate, status);
    }

    // Reset counter
    public static void resetIdCounter() { matchIdCounter = 1; }
}
