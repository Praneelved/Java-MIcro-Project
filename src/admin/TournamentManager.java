package admin;

import player.Player;
import team.Team;
import tournament.Tournament;
import tournament.LeagueTournament;
import tournament.KnockoutTournament;
import match.Match;
import exceptions.InvalidPlayerException;
import exceptions.TournamentException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ============================================================
 * TournamentManager.java
 * ============================================================
 * OOP Concept: CLASS, COLLECTIONS FRAMEWORK (ArrayList + HashMap)
 * Central controller class that manages all data in the system.
 * Acts as the bridge between Main.java (UI) and the data layer.
 *
 * Collections Used:
 *   - ArrayList<Player>     : All registered players
 *   - ArrayList<Team>       : All registered teams
 *   - ArrayList<Tournament> : All tournaments
 *   - HashMap<Integer, Player> : Fast player lookup by ID
 * ============================================================
 */
public class TournamentManager {

    // Collections Framework: HashMap for O(1) player lookup
    private HashMap<Integer, Player> playerMap;       // playerId -> Player
    private ArrayList<Player>        players;          // All players list
    private ArrayList<Team>          teams;            // All teams list
    private ArrayList<Tournament>    tournaments;      // All tournaments list

    // Man of the Tournament and Top Scorer (bonus feature)
    private String manOfTheTournament;
    private String topScorer;

    // -------------------------------------------------------
    // Constructor - Initialize all collections
    // -------------------------------------------------------
    public TournamentManager() {
        playerMap           = new HashMap<>();
        players             = new ArrayList<>();
        teams               = new ArrayList<>();
        tournaments         = new ArrayList<>();
        manOfTheTournament  = "Not declared yet";
        topScorer           = "Not declared yet";
    }

    // ===========================================================
    //                    PLAYER MANAGEMENT
    // ===========================================================

    /**
     * Register a new player with full validation.
     * Exception Handling: Throws InvalidPlayerException for bad data.
     */
    public Player registerPlayer(String name, int age, String sport,
                                  String teamName, int jerseyNumber)
            throws InvalidPlayerException {

        // Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidPlayerException("Player name cannot be empty!", 101);
        }
        // Validate age
        if (age < 10 || age > 60) {
            throw new InvalidPlayerException("Invalid age: " + age + ". Must be between 10 and 60.", 102);
        }
        // Validate jersey number
        if (jerseyNumber < 1 || jerseyNumber > 999) {
            throw new InvalidPlayerException("Invalid jersey number: " + jerseyNumber, 103);
        }

        Player player = new Player(name.trim(), age, sport, teamName, jerseyNumber);
        players.add(player);
        playerMap.put(player.getPlayerId(), player); // HashMap put operation
        return player;
    }

    /**
     * Overloaded method - register without team (Method Overloading)
     */
    public Player registerPlayer(String name, int age, String sport)
            throws InvalidPlayerException {
        return registerPlayer(name, age, sport, "Unassigned", 0);
    }

    /**
     * Update player details.
     */
    public boolean updatePlayer(int playerId, String newName, int newAge, int newJersey) {
        Player p = playerMap.get(playerId); // HashMap get operation - O(1)
        if (p == null) {
            System.out.println("  [ERROR] Player ID " + playerId + " not found.");
            return false;
        }
        if (newName != null && !newName.trim().isEmpty()) p.setName(newName.trim());
        if (newAge > 0)     p.setAge(newAge);
        if (newJersey > 0)  p.setJerseyNumber(newJersey);
        System.out.println("  [SUCCESS] Player updated: " + p.getName());
        return true;
    }

    /**
     * Remove a player from the system.
     */
    public boolean removePlayer(int playerId) {
        Player p = playerMap.get(playerId);
        if (p == null) {
            System.out.println("  [ERROR] Player ID " + playerId + " not found.");
            return false;
        }
        // Remove from their team too
        for (Team t : teams) {
            t.removePlayer(playerId);
        }
        players.remove(p);
        playerMap.remove(playerId);
        System.out.println("  [SUCCESS] Player '" + p.getName() + "' removed from system.");
        return true;
    }

    /**
     * Display all players.
     */
    public void displayAllPlayers() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║       ALL REGISTERED PLAYERS         ║");
        System.out.println("╚══════════════════════════════════════╝");
        if (players.isEmpty()) {
            System.out.println("  No players registered yet.");
            return;
        }
        for (Player p : players) {
            p.displayPlayer();
        }
    }

    /**
     * Find player by ID using HashMap.
     */
    public Player findPlayerById(int id) {
        return playerMap.get(id); // O(1) HashMap lookup
    }

    /**
     * Find player by name (linear search).
     */
    public Player findPlayerByName(String name) {
        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(name)) return p;
        }
        return null;
    }

    // ===========================================================
    //                    TEAM MANAGEMENT
    // ===========================================================

    /**
     * Create a new team.
     */
    public Team createTeam(String teamName, String sport, String coach, int maxSize)
            throws TournamentException {

        // Check duplicate team name
        for (Team t : teams) {
            if (t.getTeamName().equalsIgnoreCase(teamName)) {
                throw new TournamentException("Team '" + teamName + "' already exists!");
            }
        }

        Team team = new Team(teamName, sport, coach, maxSize);
        teams.add(team);
        System.out.println("  [SUCCESS] Team '" + teamName + "' created successfully!");
        return team;
    }

    /**
     * Add a player to a team.
     */
    public boolean addPlayerToTeam(int playerId, String teamName) {
        Player p = playerMap.get(playerId);
        if (p == null) {
            System.out.println("  [ERROR] Player ID " + playerId + " not found.");
            return false;
        }
        Team team = findTeamByName(teamName);
        if (team == null) {
            System.out.println("  [ERROR] Team '" + teamName + "' not found.");
            return false;
        }
        return team.addPlayer(p);
    }

    /**
     * Remove player from team.
     */
    public boolean removePlayerFromTeam(int playerId, String teamName) {
        Team team = findTeamByName(teamName);
        if (team == null) {
            System.out.println("  [ERROR] Team '" + teamName + "' not found.");
            return false;
        }
        return team.removePlayer(playerId);
    }

    /**
     * Display all teams.
     */
    public void displayAllTeams() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║         ALL REGISTERED TEAMS         ║");
        System.out.println("╚══════════════════════════════════════╝");
        if (teams.isEmpty()) {
            System.out.println("  No teams registered yet.");
            return;
        }
        for (Team t : teams) {
            t.displayTeam();
        }
    }

    /**
     * Find team by name (case-insensitive).
     */
    public Team findTeamByName(String name) {
        for (Team t : teams) {
            if (t.getTeamName().equalsIgnoreCase(name)) return t;
        }
        return null;
    }

    // ===========================================================
    //                  TOURNAMENT MANAGEMENT
    // ===========================================================

    /**
     * Create a League tournament.
     */
    public Tournament createLeagueTournament(String name, String sport,
                                              String start, String end)
            throws TournamentException {
        validateTournamentName(name);
        LeagueTournament t = new LeagueTournament(name, sport, start, end);
        tournaments.add(t);
        System.out.println("  [SUCCESS] League Tournament '" + name + "' created!");
        return t;
    }

    /**
     * Create a Knockout tournament.
     */
    public Tournament createKnockoutTournament(String name, String sport,
                                                String start, String end)
            throws TournamentException {
        validateTournamentName(name);
        KnockoutTournament t = new KnockoutTournament(name, sport, start, end);
        tournaments.add(t);
        System.out.println("  [SUCCESS] Knockout Tournament '" + name + "' created!");
        return t;
    }

    /**
     * Validate tournament name.
     */
    private void validateTournamentName(String name) throws TournamentException {
        if (name == null || name.trim().isEmpty()) {
            throw new TournamentException("Tournament name cannot be empty!");
        }
        for (Tournament t : tournaments) {
            if (t.getTournamentName().equalsIgnoreCase(name)) {
                throw new TournamentException("Tournament '" + name + "' already exists!");
            }
        }
    }

    /**
     * Display all tournaments.
     */
    public void displayAllTournaments() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║         ALL TOURNAMENTS                  ║");
        System.out.println("╚══════════════════════════════════════════╝");
        if (tournaments.isEmpty()) {
            System.out.println("  No tournaments created yet.");
            return;
        }
        for (int i = 0; i < tournaments.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + tournaments.get(i));
        }
    }

    /**
     * Find tournament by name.
     */
    public Tournament findTournamentByName(String name) {
        for (Tournament t : tournaments) {
            if (t.getTournamentName().equalsIgnoreCase(name)) return t;
        }
        return null;
    }

    // ===========================================================
    //                   MATCH MANAGEMENT
    // ===========================================================

    /**
     * Update match score in a tournament.
     */
    public boolean updateMatchScore(Tournament tournament, int matchId,
                                    int homeScore, int awayScore) {
        Match m = tournament.findMatchById(matchId);
        if (m == null) {
            System.out.println("  [ERROR] Match ID " + matchId + " not found.");
            return false;
        }
        if (homeScore < 0 || awayScore < 0) {
            System.out.println("  [ERROR] Scores cannot be negative.");
            return false;
        }
        m.updateScore(homeScore, awayScore);

        // Update player match counts
        for (player.Player p : m.getHomeTeam().getPlayers()) p.incrementMatches();
        for (player.Player p : m.getAwayTeam().getPlayers()) p.incrementMatches();

        System.out.println("  [SUCCESS] Score updated: " + m.getHomeTeam().getTeamName()
                + " " + homeScore + " - " + awayScore + " " + m.getAwayTeam().getTeamName());
        return true;
    }

    // ===========================================================
    //               BONUS: WINNER DECLARATION
    // ===========================================================

    /**
     * Determine and display Man of the Tournament (player with most goals).
     */
    public void determineTopScorer(Tournament tournament) {
        Player best = null;
        int maxGoals = -1;

        for (Team t : tournament.getTeams()) {
            for (Player p : t.getPlayers()) {
                if (p.getGoalsScored() > maxGoals) {
                    maxGoals = p.getGoalsScored();
                    best = p;
                }
            }
        }

        if (best != null) {
            topScorer          = best.getName() + " (" + maxGoals + " goals/runs)";
            manOfTheTournament = best.getName();
            System.out.println("  ⭐ TOP SCORER: " + topScorer);
            System.out.println("  🏅 MAN OF TOURNAMENT: " + manOfTheTournament);
        } else {
            System.out.println("  No scoring data available.");
        }
    }

    // ===========================================================
    //                    GETTERS
    // ===========================================================
    public ArrayList<Player>     getAllPlayers()     { return players; }
    public ArrayList<Team>       getAllTeams()        { return teams; }
    public ArrayList<Tournament> getAllTournaments()  { return tournaments; }
    public String getManOfTheTournament()            { return manOfTheTournament; }
    public String getTopScorer()                     { return topScorer; }
}
