package filehandler;

import player.Player;
import team.Team;
import tournament.Tournament;
import match.Match;

import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * ============================================================
 * FileHandler.java
 * ============================================================
 * OOP Concept: FILE HANDLING (Basic Java I/O)
 * This class handles saving and loading data to/from text files.
 *
 * File Handling concepts used:
 *   - FileWriter    : Write text to a file
 *   - BufferedReader: Read text from a file efficiently
 *   - FileReader    : Open a file for reading
 *   - IOException   : Exception for file errors
 *
 * Methods are static so we don't need to create an object
 * of FileHandler every time we want to use it.
 * ============================================================
 */
public class FileHandler {

    // File names where data will be stored
    private static final String PLAYERS_FILE     = "players.txt";
    private static final String TOURNAMENT_FILE  = "tournament_report.txt";

    // -------------------------------------------------------
    // Save all players to a text file
    // -------------------------------------------------------
    public static void savePlayers(ArrayList<Player> players) {
        // FileWriter writes to the file; IOException must be caught
        try {
            FileWriter fw = new FileWriter(PLAYERS_FILE);

            // Write header
            fw.write("===================================================\n");
            fw.write("          REGISTERED PLAYERS - PLAYER LIST\n");
            fw.write("===================================================\n");

            if (players.isEmpty()) {
                fw.write("No players registered.\n");
            } else {
                // Write each player's details in a simple CSV-like format
                fw.write("ID,Name,Age,Sport,Team,Jersey,Goals\n");
                fw.write("---------------------------------------------------\n");

                for (Player p : players) {
                    // We manually build the line - simple and easy to understand
                    String line = p.getPlayerId() + ","
                            + p.getName() + ","
                            + p.getAge() + ","
                            + p.getSport() + ","
                            + p.getTeamName() + ","
                            + p.getJerseyNumber() + ","
                            + p.getGoalsScored() + "\n";
                    fw.write(line);
                }
                fw.write("---------------------------------------------------\n");
                fw.write("Total Players: " + players.size() + "\n");
            }

            fw.close(); // Always close the file after writing
            System.out.println("  [SUCCESS] Players saved to '" + PLAYERS_FILE + "'");

        } catch (IOException e) {
            // IOException is thrown when file cannot be created or written
            System.out.println("  [ERROR] Could not save players: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Load players from file and display them
    // (In this project, we just read and print - 
    //  full reload is complex and out of scope for 2nd year)
    // -------------------------------------------------------
    public static void loadAndDisplayPlayers() {
        try {
            // Open the file for reading using FileReader + BufferedReader
            FileReader fr = new FileReader(PLAYERS_FILE);
            BufferedReader br = new BufferedReader(fr);

            System.out.println("\n  Reading player data from '" + PLAYERS_FILE + "':");
            System.out.println("  ------------------------------------------------");

            String line;
            // Read line by line using readLine() method
            while ((line = br.readLine()) != null) {
                System.out.println("  " + line);
            }

            br.close(); // Close BufferedReader after reading
            System.out.println("  ------------------------------------------------");

        } catch (IOException e) {
            // File might not exist if save was never called
            System.out.println("  [ERROR] Could not read file: " + e.getMessage());
            System.out.println("  [HINT] Make sure to save players first (Option 1 in File Menu).");
        }
    }

    // -------------------------------------------------------
    // Save the tournament report to a text file
    // -------------------------------------------------------
    public static void saveTournamentReport(Tournament t) {
        try {
            FileWriter fw = new FileWriter(TOURNAMENT_FILE);

            // Write tournament details
            fw.write("===================================================\n");
            fw.write("          TOURNAMENT REPORT\n");
            fw.write("===================================================\n");
            fw.write("Tournament Name : " + t.getTournamentName() + "\n");
            fw.write("Sport           : " + t.getSport() + "\n");
            fw.write("Format          : " + t.getFormat() + "\n");
            fw.write("Start Date      : " + t.getStartDate() + "\n");
            fw.write("End Date        : " + t.getEndDate() + "\n");
            fw.write("Total Teams     : " + t.getTeams().size() + "\n");
            fw.write("Total Matches   : " + t.getMatches().size() + "\n");
            fw.write("\n");

            // Write list of registered teams
            fw.write("--- REGISTERED TEAMS ---\n");
            ArrayList<Team> teams = t.getTeams();
            if (teams.isEmpty()) {
                fw.write("No teams registered.\n");
            } else {
                for (int i = 0; i < teams.size(); i++) {
                    fw.write((i + 1) + ". " + teams.get(i).getTeamName()
                            + " | Coach: " + teams.get(i).getCoach()
                            + " | Players: " + teams.get(i).getPlayerCount() + "\n");
                }
            }
            fw.write("\n");

            // Write match results
            fw.write("--- MATCH RESULTS ---\n");
            ArrayList<Match> matches = t.getMatches();
            if (matches.isEmpty()) {
                fw.write("No matches played yet.\n");
            } else {
                for (Match m : matches) {
                    if (m.getStatus() == Match.Status.COMPLETED) {
                        fw.write("Match #" + m.getMatchId() + " | "
                                + m.getHomeTeam().getTeamName() + " vs "
                                + m.getAwayTeam().getTeamName() + " | Score: "
                                + m.getHomeScore() + " - " + m.getAwayScore()
                                + " | " + m.getResultString() + "\n");
                    } else {
                        fw.write("Match #" + m.getMatchId() + " | "
                                + m.getHomeTeam().getTeamName() + " vs "
                                + m.getAwayTeam().getTeamName()
                                + " | [SCHEDULED]\n");
                    }
                }
            }
            fw.write("\n");

            // Write current leader
            fw.write("--- STANDINGS ---\n");
            fw.write("Current Leader: " + t.getPointsTable().getLeader() + "\n");
            fw.write("Winner: " + t.declareWinner() + "\n");

            fw.write("\n===================================================\n");
            fw.write("Report generated by Smart Sports TMS\n");
            fw.write("===================================================\n");

            fw.close(); // Always close after writing
            System.out.println("  [SUCCESS] Tournament report saved to '" + TOURNAMENT_FILE + "'");

        } catch (IOException e) {
            System.out.println("  [ERROR] Could not save tournament report: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Load and display tournament report from file
    // -------------------------------------------------------
    public static void loadAndDisplayTournamentReport() {
        try {
            FileReader fr     = new FileReader(TOURNAMENT_FILE);
            BufferedReader br = new BufferedReader(fr);

            System.out.println("\n  Reading tournament report from '" + TOURNAMENT_FILE + "':");
            System.out.println("  ------------------------------------------------");

            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("  " + line);
            }

            br.close();
            System.out.println("  ------------------------------------------------");

        } catch (IOException e) {
            System.out.println("  [ERROR] Could not read file: " + e.getMessage());
            System.out.println("  [HINT] Save a tournament report first (Option 3 in File Menu).");
        }
    }
}
