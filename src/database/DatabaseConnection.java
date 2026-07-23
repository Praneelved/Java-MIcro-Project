package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * ============================================================
 * DatabaseConnection.java
 * ============================================================
 * Handles database connection and schema initialization.
 * Supports both MySQL (default) and SQLite (easy fallback).
 * ============================================================
 */
public class DatabaseConnection {

    // Set to true for MySQL, false for SQLite
    private static final boolean USE_MYSQL = true;

    // MySQL Database Credentials
    private static final String MYSQL_HOST = "localhost";
    private static final String MYSQL_PORT = "3306";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASS = ""; // Default XAMPP password is empty, change to "password" if needed
    private static final String DB_NAME    = "sports_tournament_db";

    // SQLite Connection URL
    private static final String SQLITE_URL = "jdbc:sqlite:sports_tournament.db";

    /**
     * Get a fresh Connection object.
     */
    public static Connection getConnection() throws SQLException {
        if (USE_MYSQL) {
            try {
                // Ensure MySQL Driver is registered
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = "jdbc:mysql://" + MYSQL_HOST + ":" + MYSQL_PORT + "/" + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true";
                return DriverManager.getConnection(url, MYSQL_USER, MYSQL_PASS);
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL Driver not found! Falling back to SQLite.");
                return getSQLiteConnection();
            } catch (SQLException e) {
                System.err.println("MySQL Connection failed: " + e.getMessage() + ". Falling back to SQLite.");
                return getSQLiteConnection();
            }
        } else {
            return getSQLiteConnection();
        }
    }

    /**
     * Get SQLite Connection fallback.
     */
    private static Connection getSQLiteConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite Driver not found in classpath!", e);
        }
        return DriverManager.getConnection(SQLITE_URL);
    }

    /**
     * Create the database (if MySQL) and initialize all tables.
     */
    public static void initializeDatabase() {
        Connection conn = null;
        Statement stmt = null;
        try {
            if (USE_MYSQL) {
                // First, connect to MySQL server without database to create it
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    String serverUrl = "jdbc:mysql://" + MYSQL_HOST + ":" + MYSQL_PORT + "/?useSSL=false&allowPublicKeyRetrieval=true";
                    conn = DriverManager.getConnection(serverUrl, MYSQL_USER, MYSQL_PASS);
                    stmt = conn.createStatement();
                    stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
                } catch (Exception e) {
                    System.err.println("Could not auto-create MySQL database: " + e.getMessage() + ". Attempting SQLite fallback initialization.");
                    initializeSQLiteSchema();
                    return;
                } finally {
                    try { if (stmt != null) stmt.close(); } catch (Exception e) {}
                    try { if (conn != null) conn.close(); } catch (Exception e) {}
                }
            }

            // Connect to the specific database and create tables
            conn = getConnection();
            stmt = conn.createStatement();

            // Enable Foreign Key support in SQLite
            if (!USE_MYSQL) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }

            // 1. Tournaments Table
            stmt.execute("CREATE TABLE IF NOT EXISTS tournaments (" +
                    "tournament_id INT " + (USE_MYSQL ? "AUTO_INCREMENT" : "") + " PRIMARY KEY, " +
                    "name VARCHAR(100) UNIQUE NOT NULL, " +
                    "sport VARCHAR(50) NOT NULL, " +
                    "start_date VARCHAR(50), " +
                    "end_date VARCHAR(50), " +
                    "format VARCHAR(50) NOT NULL, " +
                    "is_active BOOLEAN DEFAULT 1, " +
                    "current_round INT DEFAULT 1, " +
                    "champion VARCHAR(100) DEFAULT ''" +
                    ")");

            // 2. Teams Table
            stmt.execute("CREATE TABLE IF NOT EXISTS teams (" +
                    "team_id INT " + (USE_MYSQL ? "AUTO_INCREMENT" : "") + " PRIMARY KEY, " +
                    "team_name VARCHAR(100) UNIQUE NOT NULL, " +
                    "sport VARCHAR(50) NOT NULL, " +
                    "coach VARCHAR(100), " +
                    "max_size INT" +
                    ")");

            // 3. Players Table
            stmt.execute("CREATE TABLE IF NOT EXISTS players (" +
                    "player_id INT " + (USE_MYSQL ? "AUTO_INCREMENT" : "") + " PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "age INT, " +
                    "sport VARCHAR(50), " +
                    "team_name VARCHAR(100) DEFAULT 'Unassigned', " +
                    "jersey_number INT, " +
                    "goals_scored INT DEFAULT 0, " +
                    "total_matches INT DEFAULT 0" +
                    ")");

            // 4. Tournament Teams (Link Table)
            stmt.execute("CREATE TABLE IF NOT EXISTS tournament_teams (" +
                    "tournament_id INT, " +
                    "team_id INT, " +
                    "PRIMARY KEY (tournament_id, team_id), " +
                    "FOREIGN KEY (tournament_id) REFERENCES tournaments(tournament_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (team_id) REFERENCES teams(team_id) ON DELETE CASCADE" +
                    ")");

            // 5. Matches Table
            stmt.execute("CREATE TABLE IF NOT EXISTS matches (" +
                    "match_id INT " + (USE_MYSQL ? "AUTO_INCREMENT" : "") + " PRIMARY KEY, " +
                    "tournament_id INT, " +
                    "home_team_id INT, " +
                    "away_team_id INT, " +
                    "home_score INT DEFAULT 0, " +
                    "away_score INT DEFAULT 0, " +
                    "scheduled_date VARCHAR(50), " +
                    "venue VARCHAR(100) DEFAULT 'TBD', " +
                    "status VARCHAR(50) DEFAULT 'SCHEDULED', " +
                    "result VARCHAR(50) DEFAULT 'NOT_PLAYED', " +
                    "round_name VARCHAR(50), " +
                    "FOREIGN KEY (tournament_id) REFERENCES tournaments(tournament_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (home_team_id) REFERENCES teams(team_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (away_team_id) REFERENCES teams(team_id) ON DELETE CASCADE" +
                    ")");

            // 6. Scores (Points Table) Table
            stmt.execute("CREATE TABLE IF NOT EXISTS scores (" +
                    "tournament_id INT, " +
                    "team_id INT, " +
                    "played INT DEFAULT 0, " +
                    "wins INT DEFAULT 0, " +
                    "losses INT DEFAULT 0, " +
                    "draws INT DEFAULT 0, " +
                    "points INT DEFAULT 0, " +
                    "goals_for INT DEFAULT 0, " +
                    "goals_against INT DEFAULT 0, " +
                    "PRIMARY KEY (tournament_id, team_id), " +
                    "FOREIGN KEY (tournament_id) REFERENCES tournaments(tournament_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (team_id) REFERENCES teams(team_id) ON DELETE CASCADE" +
                    ")");

            System.out.println("Database tables initialized successfully.");

        } catch (Exception e) {
            System.err.println("Error initializing database tables: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    /**
     * Fallback setup for SQLite when MySQL is unreachable.
     */
    private static void initializeSQLiteSchema() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getSQLiteConnection();
            stmt = conn.createStatement();
            stmt.execute("PRAGMA foreign_keys = ON;");

            stmt.execute("CREATE TABLE IF NOT EXISTS tournaments (" +
                    "tournament_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT UNIQUE NOT NULL, " +
                    "sport TEXT NOT NULL, " +
                    "start_date TEXT, " +
                    "end_date TEXT, " +
                    "format TEXT NOT NULL, " +
                    "is_active INTEGER DEFAULT 1, " +
                    "current_round INTEGER DEFAULT 1, " +
                    "champion TEXT DEFAULT ''" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS teams (" +
                    "team_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "team_name TEXT UNIQUE NOT NULL, " +
                    "sport TEXT NOT NULL, " +
                    "coach TEXT, " +
                    "max_size INTEGER" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS players (" +
                    "player_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "age INTEGER, " +
                    "sport TEXT, " +
                    "team_name TEXT DEFAULT 'Unassigned', " +
                    "jersey_number INTEGER, " +
                    "goals_scored INTEGER DEFAULT 0, " +
                    "total_matches INTEGER DEFAULT 0" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS tournament_teams (" +
                    "tournament_id INTEGER, " +
                    "team_id INTEGER, " +
                    "PRIMARY KEY (tournament_id, team_id), " +
                    "FOREIGN KEY (tournament_id) REFERENCES tournaments(tournament_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (team_id) REFERENCES teams(team_id) ON DELETE CASCADE" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS matches (" +
                    "match_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "tournament_id INTEGER, " +
                    "home_team_id INTEGER, " +
                    "away_team_id INTEGER, " +
                    "home_score INTEGER DEFAULT 0, " +
                    "away_score INTEGER DEFAULT 0, " +
                    "scheduled_date TEXT, " +
                    "venue TEXT DEFAULT 'TBD', " +
                    "status TEXT DEFAULT 'SCHEDULED', " +
                    "result TEXT DEFAULT 'NOT_PLAYED', " +
                    "round_name TEXT, " +
                    "FOREIGN KEY (tournament_id) REFERENCES tournaments(tournament_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (home_team_id) REFERENCES teams(team_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (away_team_id) REFERENCES teams(team_id) ON DELETE CASCADE" +
                    ")");

            stmt.execute("CREATE TABLE IF NOT EXISTS scores (" +
                    "tournament_id INTEGER, " +
                    "team_id INTEGER, " +
                    "played INTEGER DEFAULT 0, " +
                    "wins INTEGER DEFAULT 0, " +
                    "losses INTEGER DEFAULT 0, " +
                    "draws INTEGER DEFAULT 0, " +
                    "points INTEGER DEFAULT 0, " +
                    "goals_for INTEGER DEFAULT 0, " +
                    "goals_against INTEGER DEFAULT 0, " +
                    "PRIMARY KEY (tournament_id, team_id), " +
                    "FOREIGN KEY (tournament_id) REFERENCES tournaments(tournament_id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (team_id) REFERENCES teams(team_id) ON DELETE CASCADE" +
                    ")");

            System.out.println("SQLite database tables initialized successfully (fallback mode).");
        } catch (Exception ex) {
            System.err.println("Fatal: Could not initialize fallback SQLite database schema: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    /**
     * Completely clear all records in the database.
     */
    public static void resetDatabase() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            
            // Delete all records in correct order to prevent FK violations
            stmt.executeUpdate("DELETE FROM matches");
            stmt.executeUpdate("DELETE FROM tournament_teams");
            stmt.executeUpdate("DELETE FROM scores");
            stmt.executeUpdate("DELETE FROM players");
            stmt.executeUpdate("DELETE FROM teams");
            stmt.executeUpdate("DELETE FROM tournaments");

            // Reset AUTO_INCREMENT in MySQL
            if (USE_MYSQL) {
                try {
                    stmt.executeUpdate("ALTER TABLE matches AUTO_INCREMENT = 1");
                    stmt.executeUpdate("ALTER TABLE players AUTO_INCREMENT = 1");
                    stmt.executeUpdate("ALTER TABLE teams AUTO_INCREMENT = 1");
                    stmt.executeUpdate("ALTER TABLE tournaments AUTO_INCREMENT = 1");
                } catch (Exception e) {
                    // Ignore if MySQL resets differently or auto-resets
                }
            } else {
                // SQLite autoincrement reset
                try {
                    stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name IN ('matches', 'players', 'teams', 'tournaments')");
                } catch (Exception e) {}
            }

            System.out.println("Database reset successful. All records deleted.");
        } catch (SQLException e) {
            System.err.println("Error resetting database: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
}
