package team;

import player.Player;
import java.util.ArrayList;

/**
 * ============================================================
 * Team.java
 * ============================================================
 * OOP Concept: CLASS, ENCAPSULATION, COLLECTIONS FRAMEWORK
 * Represents a team with a roster of players.
 * Uses ArrayList (Collections Framework) to manage players.
 * ============================================================
 */
public class Team {

    // Private fields - Encapsulation
    private static int teamIdCounter = 1;
    private int teamId;
    private String teamName;
    private String sport;
    private String coach;
    private ArrayList<Player> players; // Collections Framework: ArrayList
    private int maxSize;

    // -------------------------------------------------------
    // Constructor
    // -------------------------------------------------------
    public Team(String teamName, String sport, String coach, int maxSize) {
        this.teamId   = teamIdCounter++;
        this.teamName = teamName;
        this.sport    = sport;
        this.coach    = coach;
        this.maxSize  = maxSize;
        this.players  = new ArrayList<>(); // Initialize the ArrayList
    }

    // Overloaded Constructor (without coach info)
    public Team(String teamName, String sport) {
        this(teamName, sport, "TBD", 15);
    }

    // -------------------------------------------------------
    // Add Player to Team
    // -------------------------------------------------------
    public boolean addPlayer(Player player) {
        // Exception Handling via validation
        if (players.size() >= maxSize) {
            System.out.println("  [ERROR] Team " + teamName + " is full! Max size: " + maxSize);
            return false;
        }
        // Check for duplicate jersey number
        for (Player p : players) {
            if (p.getJerseyNumber() == player.getJerseyNumber()) {
                System.out.println("  [ERROR] Jersey number " + player.getJerseyNumber() + " already taken!");
                return false;
            }
        }
        player.setTeamName(teamName);
        players.add(player);
        System.out.println("  [SUCCESS] " + player.getName() + " added to " + teamName);
        return true;
    }

    // -------------------------------------------------------
    // Remove Player from Team
    // -------------------------------------------------------
    public boolean removePlayer(int playerId) {
        for (Player p : players) {
            if (p.getPlayerId() == playerId) {
                players.remove(p);
                System.out.println("  [SUCCESS] Player " + p.getName() + " removed from " + teamName);
                return true;
            }
        }
        System.out.println("  [ERROR] Player ID " + playerId + " not found in " + teamName);
        return false;
    }

    // -------------------------------------------------------
    // Find player by ID
    // -------------------------------------------------------
    public Player findPlayerById(int id) {
        for (Player p : players) {
            if (p.getPlayerId() == id) return p;
        }
        return null;
    }

    // -------------------------------------------------------
    // Display Team Details
    // -------------------------------------------------------
    public void displayTeam() {
        System.out.println("===========================================");
        System.out.println("  Team ID    : " + teamId);
        System.out.println("  Team Name  : " + teamName);
        System.out.println("  Sport      : " + sport);
        System.out.println("  Coach      : " + coach);
        System.out.println("  Players    : " + players.size() + "/" + maxSize);
        System.out.println("-------------------------------------------");
        if (players.isEmpty()) {
            System.out.println("  No players registered yet.");
        } else {
            for (Player p : players) {
                System.out.println("  " + p);
            }
        }
        System.out.println("===========================================");
    }

    // -------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------
    public int getTeamId()               { return teamId; }
    public void setTeamId(int id)        { this.teamId = id; }
    public String getTeamName()          { return teamName; }
    public void setTeamName(String n)    { this.teamName = n; }
    public String getSport()             { return sport; }
    public String getCoach()             { return coach; }
    public void setCoach(String coach)   { this.coach = coach; }
    public ArrayList<Player> getPlayers(){ return players; }
    public int getMaxSize()              { return maxSize; }
    public int getPlayerCount()          { return players.size(); }

    @Override
    public String toString() {
        return String.format("[TeamID:%d] %s (%s) - %d players", teamId, teamName, sport, players.size());
    }

    // Reset counter
    public static void resetIdCounter() { teamIdCounter = 1; }
}
