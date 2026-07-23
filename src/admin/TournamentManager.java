package admin;

import player.Player;
import team.Team;
import tournament.Tournament;
import tournament.LeagueTournament;
import tournament.KnockoutTournament;
import tournament.PointsTable;
import match.Match;
import exceptions.InvalidPlayerException;
import exceptions.TournamentException;
import database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * ============================================================
 * TournamentManager.java (Database-driven)
 * ============================================================
 * OOP Concept: CLASS, COLLECTIONS FRAMEWORK, JDBC DATABASE INTEGRATION
 * Now acts as the central database controller using JDBC.
 * It manages CRUD operations directly in MySQL/SQLite and reloads
 * data into memory arrays to keep the UI in sync.
 * ============================================================
 */
public class TournamentManager {

    private HashMap<Integer, Player> playerMap;       // playerId -> Player
    private ArrayList<Player>        players;          // All players list
    private ArrayList<Team>          teams;            // All teams list
    private ArrayList<Tournament>    tournaments;      // All tournaments list

    private String manOfTheTournament;
    private String topScorer;

    public TournamentManager() {
        playerMap           = new HashMap<>();
        players             = new ArrayList<>();
        teams               = new ArrayList<>();
        tournaments         = new ArrayList<>();
        manOfTheTournament  = "Not declared yet";
        topScorer           = "Not declared yet";
        
        // Load data on startup
        loadDataFromDatabase();
    }

    /**
     * Load all data from MySQL/SQLite database into memory collections.
     * This acts as a synchronizer between DB and UI.
     */
    public void loadDataFromDatabase() {
        players.clear();
        playerMap.clear();
        teams.clear();
        tournaments.clear();

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            stmt = conn.createStatement();

            // 1. Load Players
            rs = stmt.executeQuery("SELECT * FROM players");
            while (rs.next()) {
                int id = rs.getInt("player_id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String sport = rs.getString("sport");
                String teamName = rs.getString("team_name");
                int jersey = rs.getInt("jersey_number");
                int goals = rs.getInt("goals_scored");
                int matches = rs.getInt("total_matches");

                Player p = new Player(name, age, sport, teamName, jersey);
                p.setPlayerId(id);
                p.addGoals(goals);
                for(int i=0; i<matches; i++) p.incrementMatches();

                players.add(p);
                playerMap.put(id, p);
            }
            rs.close();

            // 2. Load Teams
            rs = stmt.executeQuery("SELECT * FROM teams");
            while (rs.next()) {
                int id = rs.getInt("team_id");
                String name = rs.getString("team_name");
                String sport = rs.getString("sport");
                String coach = rs.getString("coach");
                int maxSize = rs.getInt("max_size");

                Team t = new Team(name, sport, coach, maxSize);
                t.setTeamId(id);

                // Add players belonging to this team from players list
                for (Player p : players) {
                    if (p.getTeamName().equalsIgnoreCase(name)) {
                        t.getPlayers().add(p);
                    }
                }
                teams.add(t);
            }
            rs.close();

            // 3. Load Tournaments
            rs = stmt.executeQuery("SELECT * FROM tournaments");
            ArrayList<TempTournamentInfo> tempTournaments = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("tournament_id");
                String name = rs.getString("name");
                String sport = rs.getString("sport");
                String start = rs.getString("start_date");
                String end = rs.getString("end_date");
                String format = rs.getString("format");
                boolean isActive = rs.getBoolean("is_active");
                int currentRound = rs.getInt("current_round");
                String champion = rs.getString("champion");

                Tournament t;
                if ("Knockout".equalsIgnoreCase(format)) {
                    t = new KnockoutTournament(name, sport, start, end);
                    // Use reflection/setters to populate knockout specific fields
                    KnockoutTournament kt = (KnockoutTournament) t;
                    // We will set knockout champion and round below
                } else {
                    t = new LeagueTournament(name, sport, start, end);
                }
                t.setTournamentId(id);
                t.setActive(isActive);

                tempTournaments.add(new TempTournamentInfo(t, id, currentRound, champion));
            }
            rs.close();

            // 4. Load teams in each tournament and their matches
            for (TempTournamentInfo temp : tempTournaments) {
                Tournament t = temp.tournament;

                // Load tournament teams
                PreparedStatement psTeams = conn.prepareStatement(
                        "SELECT team_id FROM tournament_teams WHERE tournament_id = ?");
                psTeams.setInt(1, temp.id);
                ResultSet rsTeams = psTeams.executeQuery();
                while (rsTeams.next()) {
                    int teamId = rsTeams.getInt("team_id");
                    Team team = findTeamByIdInMemory(teamId);
                    if (team != null) {
                        t.getTeams().add(team);
                    }
                }
                rsTeams.close();
                psTeams.close();

                // Load matches
                PreparedStatement psMatches = conn.prepareStatement(
                        "SELECT * FROM matches WHERE tournament_id = ?");
                psMatches.setInt(1, temp.id);
                ResultSet rsMatches = psMatches.executeQuery();
                while (rsMatches.next()) {
                    int mId = rsMatches.getInt("match_id");
                    int homeId = rsMatches.getInt("home_team_id");
                    int awayId = rsMatches.getInt("away_team_id");
                    int homeScore = rsMatches.getInt("home_score");
                    int awayScore = rsMatches.getInt("away_score");
                    String date = rsMatches.getString("scheduled_date");
                    String venue = rsMatches.getString("venue");
                    String statusStr = rsMatches.getString("status");
                    String resultStr = rsMatches.getString("result");
                    String roundName = rsMatches.getString("round_name");

                    Team home = findTeamByIdInMemory(homeId);
                    Team away = findTeamByIdInMemory(awayId);

                    if (home != null && away != null) {
                        Match m = new Match(home, away, date, venue, roundName);
                        m.setMatchId(mId);
                        m.setStatus(Match.Status.valueOf(statusStr));
                        m.updateScore(homeScore, awayScore);
                        // Force state values back from database just in case result was customized
                        // m.updateScore sets Status to COMPLETED, let's keep status database-driven
                        m.setStatus(Match.Status.valueOf(statusStr));
                        t.getMatches().add(m);
                    }
                }
                rsMatches.close();
                psMatches.close();

                // Set remaining details for Knockout
                if (t instanceof KnockoutTournament) {
                    KnockoutTournament kt = (KnockoutTournament) t;
                    // Remaining teams in knockout are the ones that haven't lost, or if tournament is in progress, we can calculate it
                    // Let's initialize remaining teams list with remaining active teams
                    kt.getRemainingTeams().clear();
                    // By default, start with all tournament teams, then remove losers of completed matches
                    ArrayList<Team> rem = new ArrayList<>(t.getTeams());
                    for (Match m : t.getMatches()) {
                        if (m.getStatus() == Match.Status.COMPLETED) {
                            Team loser = m.getLoser();
                            if (loser != null) {
                                rem.remove(loser);
                            }
                        }
                    }
                    kt.getRemainingTeams().addAll(rem);
                }

                // Recalculate standings for Points Table
                t.calculateStandings();
                tournaments.add(t);
            }

        } catch (SQLException e) {
            System.err.println("Error loading data from database: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    private Team findTeamByIdInMemory(int id) {
        for (Team t : teams) {
            if (t.getTeamId() == id) return t;
        }
        return null;
    }

    // Helper holder class for parsing tournaments
    private static class TempTournamentInfo {
        Tournament tournament;
        int id;
        int currentRound;
        String champion;

        TempTournamentInfo(Tournament t, int id, int currentRound, String champion) {
            this.tournament = t;
            this.id = id;
            this.currentRound = currentRound;
            this.champion = champion;
        }
    }

    // ===========================================================
    //                    PLAYER CRUD OPERATIONS
    // ===========================================================

    public Player registerPlayer(String name, int age, String sport,
                                  String teamName, int jerseyNumber)
            throws InvalidPlayerException {

        if (name == null || name.trim().isEmpty()) {
            throw new InvalidPlayerException("Player name cannot be empty!", 101);
        }
        if (age < 10 || age > 60) {
            throw new InvalidPlayerException("Invalid age: " + age + ". Must be between 10 and 60.", 102);
        }
        if (jerseyNumber < 1 || jerseyNumber > 999) {
            throw new InvalidPlayerException("Invalid jersey number: " + jerseyNumber, 103);
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO players (name, age, sport, team_name, jersey_number, goals_scored, total_matches) VALUES (?, ?, ?, ?, ?, 0, 0)";
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, name.trim());
            pstmt.setInt(2, age);
            pstmt.setString(3, sport);
            pstmt.setString(4, teamName == null ? "Unassigned" : teamName.trim());
            pstmt.setInt(5, jerseyNumber);

            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            int newId = -1;
            if (rs.next()) {
                newId = rs.getInt(1);
            }

            // Sync with local memory
            loadDataFromDatabase();
            return findPlayerById(newId);

        } catch (SQLException e) {
            System.err.println("JDBC Error registering player: " + e.getMessage());
            throw new InvalidPlayerException("Database error: " + e.getMessage(), 104);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    public Player registerPlayer(String name, int age, String sport)
            throws InvalidPlayerException {
        return registerPlayer(name, age, sport, "Unassigned", 0);
    }

    public boolean updatePlayer(int playerId, String newName, int newAge, int newJersey) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            StringBuilder sql = new StringBuilder("UPDATE players SET ");
            ArrayList<Object> params = new ArrayList<>();
            boolean first = true;

            if (newName != null && !newName.trim().isEmpty()) {
                sql.append("name = ?");
                params.add(newName.trim());
                first = false;
            }
            if (newAge > 0) {
                if (!first) sql.append(", ");
                sql.append("age = ?");
                params.add(newAge);
                first = false;
            }
            if (newJersey > 0) {
                if (!first) sql.append(", ");
                sql.append("jersey_number = ?");
                params.add(newJersey);
            }

            sql.append(" WHERE player_id = ?");
            params.add(playerId);

            pstmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            int rows = pstmt.executeUpdate();
            loadDataFromDatabase(); // Reload memory
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating player: " + e.getMessage());
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    public boolean removePlayer(int playerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement("DELETE FROM players WHERE player_id = ?");
            pstmt.setInt(1, playerId);
            int rows = pstmt.executeUpdate();
            loadDataFromDatabase(); // Reload memory
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting player: " + e.getMessage());
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    public Player findPlayerById(int id) {
        return playerMap.get(id);
    }

    public Player findPlayerByName(String name) {
        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(name)) return p;
        }
        return null;
    }

    public void displayAllPlayers() {
        loadDataFromDatabase();
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

    public void addGoalsToPlayer(int id, int goals) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement("UPDATE players SET goals_scored = goals_scored + ? WHERE player_id = ?");
            pstmt.setInt(1, goals);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            loadDataFromDatabase();
        } catch (SQLException e) {
            System.err.println("Error adding goals: " + e.getMessage());
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    // ===========================================================
    //                    TEAM CRUD OPERATIONS
    // ===========================================================

    public Team createTeam(String teamName, String sport, String coach, int maxSize)
            throws TournamentException {

        // Validate duplicates
        for (Team t : teams) {
            if (t.getTeamName().equalsIgnoreCase(teamName)) {
                throw new TournamentException("Team '" + teamName + "' already exists!");
            }
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement("INSERT INTO teams (team_name, sport, coach, max_size) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, teamName.trim());
            pstmt.setString(2, sport);
            pstmt.setString(3, coach == null ? "TBD" : coach.trim());
            pstmt.setInt(4, maxSize);

            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            int newId = -1;
            if (rs.next()) {
                newId = rs.getInt(1);
            }

            loadDataFromDatabase();
            return findTeamByIdInMemory(newId);

        } catch (SQLException e) {
            throw new TournamentException("Database error creating team: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    public boolean removeTeam(int teamId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            
            // Get team name first to release its players
            Team t = findTeamByIdInMemory(teamId);
            if (t != null) {
                PreparedStatement psP = conn.prepareStatement("UPDATE players SET team_name = 'Unassigned' WHERE team_name = ?");
                psP.setString(1, t.getTeamName());
                psP.executeUpdate();
                psP.close();
            }

            pstmt = conn.prepareStatement("DELETE FROM teams WHERE team_id = ?");
            pstmt.setInt(1, teamId);
            int rows = pstmt.executeUpdate();
            loadDataFromDatabase();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error removing team: " + e.getMessage());
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    public boolean addPlayerToTeam(int playerId, String teamName) {
        Player p = findPlayerById(playerId);
        if (p == null) return false;
        Team team = findTeamByName(teamName);
        if (team == null) return false;

        // Check size limit
        if (team.getPlayerCount() >= team.getMaxSize()) {
            return false;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement("UPDATE players SET team_name = ? WHERE player_id = ?");
            pstmt.setString(1, teamName);
            pstmt.setInt(2, playerId);
            int rows = pstmt.executeUpdate();
            loadDataFromDatabase();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error adding player to team: " + e.getMessage());
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    public boolean removePlayerFromTeam(int playerId, String teamName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement("UPDATE players SET team_name = 'Unassigned' WHERE player_id = ? AND team_name = ?");
            pstmt.setInt(1, playerId);
            pstmt.setString(2, teamName);
            int rows = pstmt.executeUpdate();
            loadDataFromDatabase();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error removing player from team: " + e.getMessage());
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    public Team findTeamByName(String name) {
        for (Team t : teams) {
            if (t.getTeamName().equalsIgnoreCase(name)) return t;
        }
        return null;
    }

    public void displayAllTeams() {
        loadDataFromDatabase();
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

    // ===========================================================
    //                 TOURNAMENT CRUD OPERATIONS
    // ===========================================================

    public Tournament createLeagueTournament(String name, String sport,
                                              String start, String end)
            throws TournamentException {
        return saveTournament(name, sport, start, end, "League");
    }

    public Tournament createKnockoutTournament(String name, String sport,
                                                String start, String end)
            throws TournamentException {
        return saveTournament(name, sport, start, end, "Knockout");
    }

    private Tournament saveTournament(String name, String sport, String start, String end, String format)
            throws TournamentException {
        // Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new TournamentException("Tournament name cannot be empty!");
        }
        for (Tournament t : tournaments) {
            if (t.getTournamentName().equalsIgnoreCase(name)) {
                throw new TournamentException("Tournament '" + name + "' already exists!");
            }
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(
                    "INSERT INTO tournaments (name, sport, start_date, end_date, format, is_active, current_round, champion) " +
                    "VALUES (?, ?, ?, ?, ?, 1, 1, '')", Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, name.trim());
            pstmt.setString(2, sport);
            pstmt.setString(3, start);
            pstmt.setString(4, end);
            pstmt.setString(5, format);

            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            int newId = -1;
            if (rs.next()) {
                newId = rs.getInt(1);
            }

            loadDataFromDatabase();
            return findTournamentByIdInMemory(newId);

        } catch (SQLException e) {
            throw new TournamentException("Database error creating tournament: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    private Tournament findTournamentByIdInMemory(int id) {
        for (Tournament t : tournaments) {
            if (t.getTournamentId() == id) return t;
        }
        return null;
    }

    public boolean addTeamToTournament(Tournament tournament, Team team) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            
            // Check if already in tournament
            PreparedStatement psCheck = conn.prepareStatement("SELECT * FROM tournament_teams WHERE tournament_id = ? AND team_id = ?");
            psCheck.setInt(1, tournament.getTournamentId());
            psCheck.setInt(2, team.getTeamId());
            ResultSet rs = psCheck.executeQuery();
            boolean exists = rs.next();
            rs.close();
            psCheck.close();

            if (exists) {
                return false;
            }

            pstmt = conn.prepareStatement("INSERT INTO tournament_teams (tournament_id, team_id) VALUES (?, ?)");
            pstmt.setInt(1, tournament.getTournamentId());
            pstmt.setInt(2, team.getTeamId());
            pstmt.executeUpdate();
            pstmt.close();

            // Insert initial record in scores table too
            pstmt = conn.prepareStatement(
                    "INSERT INTO scores (tournament_id, team_id, played, wins, losses, draws, points, goals_for, goals_against) " +
                    "VALUES (?, ?, 0, 0, 0, 0, 0, 0, 0)");
            pstmt.setInt(1, tournament.getTournamentId());
            pstmt.setInt(2, team.getTeamId());
            pstmt.executeUpdate();

            loadDataFromDatabase();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding team to tournament: " + e.getMessage());
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    public boolean generateFixtures(Tournament tournament) {
        if (tournament.getTeams().size() < 2) {
            return false;
        }

        // Generate in memory first
        tournament.generateFixtures();
        ArrayList<Match> matches = tournament.getMatches();

        // Save generated matches to DB
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            
            // Delete existing scheduled matches for this tournament
            PreparedStatement psDel = conn.prepareStatement("DELETE FROM matches WHERE tournament_id = ?");
            psDel.setInt(1, tournament.getTournamentId());
            psDel.executeUpdate();
            psDel.close();

            // Save new fixtures
            pstmt = conn.prepareStatement(
                    "INSERT INTO matches (tournament_id, home_team_id, away_team_id, scheduled_date, venue, status, result, round_name) " +
                    "VALUES (?, ?, ?, ?, ?, 'SCHEDULED', 'NOT_PLAYED', ?)");

            for (Match m : matches) {
                pstmt.setInt(1, tournament.getTournamentId());
                pstmt.setInt(2, m.getHomeTeam().getTeamId());
                pstmt.setInt(3, m.getAwayTeam().getTeamId());
                pstmt.setString(4, m.getScheduledDate());
                pstmt.setString(5, m.getVenue());
                pstmt.setString(6, m.getRoundName());
                pstmt.executeUpdate();
            }

            loadDataFromDatabase(); // Reload structures with database generated IDs
            return true;
        } catch (SQLException e) {
            System.err.println("Error saving fixtures to database: " + e.getMessage());
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    public boolean updateMatchScore(Tournament tournament, int matchId,
                                    int homeScore, int awayScore) {
        Match m = tournament.findMatchById(matchId);
        if (m == null) return false;

        m.updateScore(homeScore, awayScore);

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(
                    "UPDATE matches SET home_score = ?, away_score = ?, status = ?, result = ? WHERE match_id = ?");
            pstmt.setInt(1, homeScore);
            pstmt.setInt(2, awayScore);
            pstmt.setString(3, m.getStatus().name());
            pstmt.setString(4, m.getResult().name());
            pstmt.setInt(5, matchId);

            int rows = pstmt.executeUpdate();
            pstmt.close();

            // Increment match counts in DB
            PreparedStatement psP = conn.prepareStatement("UPDATE players SET total_matches = total_matches + 1 WHERE team_name = ?");
            psP.setString(1, m.getHomeTeam().getTeamName());
            psP.executeUpdate();
            psP.setString(1, m.getAwayTeam().getTeamName());
            psP.executeUpdate();
            psP.close();

            // Sync scores points table in database
            tournament.calculateStandings();
            savePointsTableToDatabase(tournament);

            loadDataFromDatabase();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating match score: " + e.getMessage());
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    private void savePointsTableToDatabase(Tournament t) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            pstmt = conn.prepareStatement(
                    "UPDATE scores SET played = ?, wins = ?, losses = ?, draws = ?, points = ?, goals_for = ?, goals_against = ? " +
                    "WHERE tournament_id = ? AND team_id = ?");

            for (PointsTable.TeamRecord r : t.getPointsTable().getRecords()) {
                Team team = findTeamByName(r.teamName);
                if (team != null) {
                    pstmt.setInt(1, r.played);
                    pstmt.setInt(2, r.wins);
                    pstmt.setInt(3, r.losses);
                    pstmt.setInt(4, r.draws);
                    pstmt.setInt(5, r.points);
                    pstmt.setInt(6, r.goalsFor);
                    pstmt.setInt(7, r.goalsAgainst);
                    pstmt.setInt(8, t.getTournamentId());
                    pstmt.setInt(9, team.getTeamId());
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving points table to database: " + e.getMessage());
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    public void advanceKnockoutRound(KnockoutTournament kt) {
        kt.advanceToNextRound();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            
            // If champion is declared, update tournament status
            if (!kt.getChampion().isEmpty()) {
                pstmt = conn.prepareStatement("UPDATE tournaments SET is_active = 0, champion = ? WHERE tournament_id = ?");
                pstmt.setString(1, kt.getChampion());
                pstmt.setInt(2, kt.getTournamentId());
                pstmt.executeUpdate();
                pstmt.close();
            } else {
                // Otherwise save the new round matches generated
                pstmt = conn.prepareStatement(
                        "INSERT INTO matches (tournament_id, home_team_id, away_team_id, scheduled_date, venue, status, result, round_name) " +
                        "VALUES (?, ?, ?, ?, ?, 'SCHEDULED', 'NOT_PLAYED', ?)");
                
                // Only insert matches of the new round (the last ones in matches list)
                int matchesInNewRound = kt.getRemainingTeams().size() / 2;
                ArrayList<Match> allMatches = kt.getMatches();
                for (int i = allMatches.size() - matchesInNewRound; i < allMatches.size(); i++) {
                    Match m = allMatches.get(i);
                    pstmt.setInt(1, kt.getTournamentId());
                    pstmt.setInt(2, m.getHomeTeam().getTeamId());
                    pstmt.setInt(3, m.getAwayTeam().getTeamId());
                    pstmt.setString(4, m.getScheduledDate());
                    pstmt.setString(5, m.getVenue());
                    pstmt.setString(6, m.getRoundName());
                    pstmt.executeUpdate();
                }
                pstmt.close();
                
                // Update current round number
                pstmt = conn.prepareStatement("UPDATE tournaments SET current_round = ? WHERE tournament_id = ?");
                pstmt.setInt(1, kt.getCurrentRound());
                pstmt.setInt(2, kt.getTournamentId());
                pstmt.executeUpdate();
            }
            
            loadDataFromDatabase();
        } catch (SQLException e) {
            System.err.println("Error advancing knockout round: " + e.getMessage());
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    public Tournament findTournamentByName(String name) {
        for (Tournament t : tournaments) {
            if (t.getTournamentName().equalsIgnoreCase(name)) return t;
        }
        return null;
    }

    public void displayAllTournaments() {
        loadDataFromDatabase();
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

    // ===========================================================
    //               BONUS: WINNER DECLARATION
    // ===========================================================

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
        } else {
            topScorer = "No scoring data available";
            manOfTheTournament = "Not declared yet";
        }
    }

    public void resetApplication() {
        DatabaseConnection.resetDatabase();
        loadDataFromDatabase();
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
