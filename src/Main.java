import admin.TournamentManager;
import player.Player;
import team.Team;
import tournament.Tournament;
import tournament.KnockoutTournament;
import match.Match;
import exceptions.InvalidPlayerException;
import exceptions.TournamentException;
import exceptions.MatchException;
import filehandler.FileHandler;
import sports.*;

import java.util.Scanner;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║        SMART SPORTS TOURNAMENT MANAGEMENT SYSTEM            ║
 * ║                     Main.java                               ║
 * ╠══════════════════════════════════════════════════════════════╣
 * ║  College: [Your College Name]                               ║
 * ║  Subject: Object Oriented Programming with Java             ║
 * ║  Class  : Second Year Engineering                           ║
 * ║                                                             ║
 * ║  OOP Concepts Demonstrated:                                 ║
 * ║    ✔ Classes & Objects                                      ║
 * ║    ✔ Encapsulation (private fields + getters/setters)       ║
 * ║    ✔ Inheritance (Sports -> Cricket/Football...)            ║
 * ║    ✔ Polymorphism (method overriding + overloading)         ║
 * ║    ✔ Abstraction (abstract classes + interfaces)            ║
 * ║    ✔ Interface (TournamentRules)                            ║
 * ║    ✔ Exception Handling (custom + standard exceptions)      ║
 * ║    ✔ Collections Framework (ArrayList + HashMap)            ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
public class Main {

    // Static scanner for reading user input throughout the program
    static Scanner sc = new Scanner(System.in);

    // Central manager - single instance managing all data
    static TournamentManager manager = new TournamentManager();

    // Currently active tournament (selected by user)
    static Tournament activeTournament = null;

    // ================================================================
    //                          MAIN METHOD
    // ================================================================
    public static void main(String[] args) {
        printBanner();

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1: playerMenu();      break;
                case 2: teamMenu();        break;
                case 3: tournamentMenu();  break;
                case 4: matchMenu();       break;
                case 5: pointsTableMenu(); break;
                case 6: winnerMenu();      break;
                case 7: sportInfoMenu();   break;
                case 8: fileMenu();        break;
                case 0:
                    System.out.println("\n  Thank you for using Smart Sports TMS! Goodbye!\n");
                    running = false;
                    break;
                default:
                    System.out.println("  [!] Invalid choice. Please try again.");
            }
        }
        sc.close();
    }

    // ================================================================
    //                        MAIN MENU
    // ================================================================
    static void printMainMenu() {
        System.out.println();
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║        MAIN MENU                           ║");
        System.out.println("╠════════════════════════════════════════════╣");
        System.out.println("║  1. Player Management                      ║");
        System.out.println("║  2. Team Management                        ║");
        System.out.println("║  3. Tournament Management                  ║");
        System.out.println("║  4. Match Management                       ║");
        System.out.println("║  5. Points Table                           ║");
        System.out.println("║  6. Winner Declaration                     ║");
        System.out.println("║  7. Sport Info                             ║");
        System.out.println("║  8. Save / Load Data (File Handling)       ║");
        System.out.println("║  0. Exit                                   ║");
        System.out.println("╚════════════════════════════════════════════╝");

        if (activeTournament != null) {
            System.out.println("  [Active Tournament: " + activeTournament.getTournamentName()
                    + " | " + activeTournament.getFormat() + "]");
        }
    }

    // ================================================================
    //                    1. PLAYER MENU
    // ================================================================
    static void playerMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n╔══════════════════════════════════╗");
            System.out.println("║       PLAYER MANAGEMENT          ║");
            System.out.println("╠══════════════════════════════════╣");
            System.out.println("║  1. Register New Player          ║");
            System.out.println("║  2. Display All Players          ║");
            System.out.println("║  3. Find Player by ID            ║");
            System.out.println("║  4. Update Player Info           ║");
            System.out.println("║  5. Remove Player                ║");
            System.out.println("║  6. Add Goals/Runs to Player     ║");
            System.out.println("║  0. Back to Main Menu            ║");
            System.out.println("╚══════════════════════════════════╝");

            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1: registerPlayer();    break;
                case 2: manager.displayAllPlayers(); break;
                case 3: findPlayer();        break;
                case 4: updatePlayer();      break;
                case 5: removePlayer();      break;
                case 6: addGoalsToPlayer();  break;
                case 0: back = true;         break;
                default: System.out.println("  [!] Invalid option.");
            }
        }
    }

    static void registerPlayer() {
        System.out.println("\n  --- Register New Player ---");
        System.out.print("  Enter Name        : ");
        String name = sc.nextLine().trim();

        int age = readInt("  Enter Age         : ");

        System.out.println("  Sports: 1.Cricket  2.Football  3.Volleyball  4.Badminton");
        int sportChoice = readInt("  Choose Sport      : ");
        String sport = getSportName(sportChoice);

        System.out.print("  Enter Team Name   : ");
        String teamName = sc.nextLine().trim();

        int jersey = readInt("  Enter Jersey No.  : ");

        // Exception Handling - try-catch for custom exception
        try {
            Player p = manager.registerPlayer(name, age, sport, teamName, jersey);
            System.out.println("  [SUCCESS] Player registered!");
            p.displayPlayer();
        } catch (InvalidPlayerException e) {
            // Catching our custom exception
            System.out.println("  [ERROR] " + e.getMessage() + " (Code: " + e.getErrorCode() + ")");
        }
    }

    static void findPlayer() {
        int id = readInt("  Enter Player ID: ");
        Player p = manager.findPlayerById(id);
        if (p != null) {
            p.displayPlayer();
        } else {
            System.out.println("  [!] Player not found.");
        }
    }

    static void updatePlayer() {
        int id = readInt("  Enter Player ID to update: ");
        System.out.print("  New Name (press Enter to skip): ");
        String name = sc.nextLine().trim();
        int age    = readInt("  New Age  (0 to skip): ");
        int jersey = readInt("  New Jersey No. (0 to skip): ");
        manager.updatePlayer(id, name.isEmpty() ? null : name, age, jersey);
    }

    static void removePlayer() {
        int id = readInt("  Enter Player ID to remove: ");
        manager.removePlayer(id);
    }

    static void addGoalsToPlayer() {
        int id    = readInt("  Enter Player ID: ");
        int goals = readInt("  Goals/Runs to add: ");
        Player p  = manager.findPlayerById(id);
        if (p != null) {
            p.addGoals(goals); // Overloaded method
            System.out.println("  [SUCCESS] " + goals + " goals/runs added to " + p.getName()
                    + ". Total: " + p.getGoalsScored());
        } else {
            System.out.println("  [!] Player not found.");
        }
    }

    // ================================================================
    //                    2. TEAM MENU
    // ================================================================
    static void teamMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n╔══════════════════════════════════╗");
            System.out.println("║        TEAM MANAGEMENT           ║");
            System.out.println("╠══════════════════════════════════╣");
            System.out.println("║  1. Create New Team              ║");
            System.out.println("║  2. Display All Teams            ║");
            System.out.println("║  3. Add Player to Team           ║");
            System.out.println("║  4. Remove Player from Team      ║");
            System.out.println("║  5. Display Team Details         ║");
            System.out.println("║  0. Back to Main Menu            ║");
            System.out.println("╚══════════════════════════════════╝");

            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1: createTeam();             break;
                case 2: manager.displayAllTeams();break;
                case 3: addPlayerToTeam();        break;
                case 4: removePlayerFromTeam();   break;
                case 5: displayTeamDetails();     break;
                case 0: back = true;              break;
                default: System.out.println("  [!] Invalid option.");
            }
        }
    }

    static void createTeam() {
        System.out.println("\n  --- Create New Team ---");
        System.out.print("  Team Name  : ");
        String name = sc.nextLine().trim();

        System.out.println("  Sports: 1.Cricket  2.Football  3.Volleyball  4.Badminton");
        int sportChoice = readInt("  Choose Sport: ");
        String sport = getSportName(sportChoice);

        System.out.print("  Coach Name : ");
        String coach = sc.nextLine().trim();

        int maxSize = readInt("  Max Squad Size (e.g. 15): ");

        // Exception Handling - checked exception (TournamentException)
        try {
            manager.createTeam(name, sport, coach, maxSize);
        } catch (TournamentException e) {
            System.out.println("  [ERROR] " + e.getMessage());
        }
    }

    static void addPlayerToTeam() {
        int id = readInt("  Enter Player ID: ");
        System.out.print("  Enter Team Name: ");
        String teamName = sc.nextLine().trim();
        manager.addPlayerToTeam(id, teamName);
    }

    static void removePlayerFromTeam() {
        int id = readInt("  Enter Player ID: ");
        System.out.print("  Enter Team Name: ");
        String teamName = sc.nextLine().trim();
        manager.removePlayerFromTeam(id, teamName);
    }

    static void displayTeamDetails() {
        System.out.print("  Enter Team Name: ");
        String name = sc.nextLine().trim();
        Team t = manager.findTeamByName(name);
        if (t != null) {
            t.displayTeam();
        } else {
            System.out.println("  [!] Team not found.");
        }
    }

    // ================================================================
    //                 3. TOURNAMENT MENU
    // ================================================================
    static void tournamentMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║      TOURNAMENT MANAGEMENT           ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║  1. Create League Tournament         ║");
            System.out.println("║  2. Create Knockout Tournament       ║");
            System.out.println("║  3. View All Tournaments             ║");
            System.out.println("║  4. Select Active Tournament         ║");
            System.out.println("║  5. Add Team to Tournament           ║");
            System.out.println("║  6. View Tournament Info             ║");
            System.out.println("║  7. Generate Fixtures                ║");
            System.out.println("║  8. Advance Knockout Round           ║");
            System.out.println("║  0. Back to Main Menu                ║");
            System.out.println("╚══════════════════════════════════════╝");

            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1: createLeagueTournament();   break;
                case 2: createKnockoutTournament(); break;
                case 3: manager.displayAllTournaments(); break;
                case 4: selectActiveTournament();   break;
                case 5: addTeamToTournament();      break;
                case 6: viewTournamentInfo();       break;
                case 7: generateFixtures();         break;
                case 8: advanceKnockoutRound();     break;
                case 0: back = true;                break;
                default: System.out.println("  [!] Invalid option.");
            }
        }
    }

    static void createLeagueTournament() {
        System.out.println("\n  --- Create League Tournament ---");
        System.out.print("  Tournament Name: ");
        String name = sc.nextLine().trim();

        System.out.println("  Sports: 1.Cricket  2.Football  3.Volleyball  4.Badminton");
        int sportChoice = readInt("  Choose Sport: ");
        String sport = getSportName(sportChoice);

        System.out.print("  Start Date (e.g. 01-Aug-2025): ");
        String start = sc.nextLine().trim();
        System.out.print("  End Date   (e.g. 30-Aug-2025): ");
        String end = sc.nextLine().trim();

        try {
            Tournament t = manager.createLeagueTournament(name, sport, start, end);
            activeTournament = t;
            System.out.println("  [INFO] '" + name + "' set as active tournament.");
        } catch (TournamentException e) {
            System.out.println("  [ERROR] " + e.getMessage());
        }
    }

    static void createKnockoutTournament() {
        System.out.println("\n  --- Create Knockout Tournament ---");
        System.out.print("  Tournament Name: ");
        String name = sc.nextLine().trim();

        System.out.println("  Sports: 1.Cricket  2.Football  3.Volleyball  4.Badminton");
        int sportChoice = readInt("  Choose Sport: ");
        String sport = getSportName(sportChoice);

        System.out.print("  Start Date (e.g. 01-Sep-2025): ");
        String start = sc.nextLine().trim();
        System.out.print("  End Date   (e.g. 15-Sep-2025): ");
        String end = sc.nextLine().trim();

        try {
            Tournament t = manager.createKnockoutTournament(name, sport, start, end);
            activeTournament = t;
            System.out.println("  [INFO] '" + name + "' set as active tournament.");
        } catch (TournamentException e) {
            System.out.println("  [ERROR] " + e.getMessage());
        }
    }

    static void selectActiveTournament() {
        manager.displayAllTournaments();
        System.out.print("  Enter Tournament Name to select: ");
        String name = sc.nextLine().trim();
        Tournament t = manager.findTournamentByName(name);
        if (t != null) {
            activeTournament = t;
            System.out.println("  [SUCCESS] Active tournament set to: " + t.getTournamentName());
        } else {
            System.out.println("  [ERROR] Tournament not found.");
        }
    }

    static void addTeamToTournament() {
        if (activeTournament == null) {
            System.out.println("  [!] No active tournament selected. Create or select one first.");
            return;
        }
        manager.displayAllTeams();
        System.out.print("  Enter Team Name to add: ");
        String teamName = sc.nextLine().trim();
        Team team = manager.findTeamByName(teamName);
        if (team != null) {
            activeTournament.addTeam(team);
        } else {
            System.out.println("  [ERROR] Team '" + teamName + "' not found.");
        }
    }

    static void viewTournamentInfo() {
        if (activeTournament == null) {
            System.out.println("  [!] No active tournament selected.");
            return;
        }
        activeTournament.displayTournamentInfo(); // Polymorphic call
        activeTournament.displayTeams();
    }

    static void generateFixtures() {
        if (activeTournament == null) {
            System.out.println("  [!] No active tournament selected.");
            return;
        }
        activeTournament.generateFixtures(); // Polymorphic call
        activeTournament.displayUpcomingMatches();
    }

    static void advanceKnockoutRound() {
        if (activeTournament == null) {
            System.out.println("  [!] No active tournament selected.");
            return;
        }
        if (!(activeTournament instanceof KnockoutTournament)) {
            System.out.println("  [!] This option is only for Knockout tournaments.");
            return;
        }
        KnockoutTournament kt = (KnockoutTournament) activeTournament;
        kt.advanceToNextRound();
        activeTournament.displayUpcomingMatches();
    }

    // ================================================================
    //                    4. MATCH MENU
    // ================================================================
    static void matchMenu() {
        if (activeTournament == null) {
            System.out.println("  [!] No active tournament. Please create/select one.");
            return;
        }

        boolean back = false;
        while (!back) {
            System.out.println("\n╔══════════════════════════════════╗");
            System.out.println("║        MATCH MANAGEMENT          ║");
            System.out.println("╠══════════════════════════════════╣");
            System.out.println("║  1. View Upcoming Matches        ║");
            System.out.println("║  2. View Completed Matches       ║");
            System.out.println("║  3. Update Match Score           ║");
            System.out.println("║  0. Back to Main Menu            ║");
            System.out.println("╚══════════════════════════════════╝");
            System.out.println("  [Tournament: " + activeTournament.getTournamentName() + "]");

            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1: activeTournament.displayUpcomingMatches();  break;
                case 2: activeTournament.displayCompletedMatches(); break;
                case 3: updateMatchScore();                          break;
                case 0: back = true;                                 break;
                default: System.out.println("  [!] Invalid option.");
            }
        }
    }

    static void updateMatchScore() {
        activeTournament.displayUpcomingMatches();
        int matchId   = readInt("  Enter Match ID to update: ");
        int homeScore = readInt("  Enter Home Team Score   : ");
        int awayScore = readInt("  Enter Away Team Score   : ");

        // Exception Handling: try-catch for both standard and custom exceptions
        try {
            // Standard exception for negative scores
            if (homeScore < 0 || awayScore < 0) {
                throw new IllegalArgumentException("Scores cannot be negative!");
            }

            // Check if match is already completed - use our custom MatchException
            Match m = activeTournament.findMatchById(matchId);
            if (m != null && m.getStatus() == Match.Status.COMPLETED) {
                // Throwing our custom checked exception
                throw new MatchException(
                    "Match #" + matchId + " is already completed! Cannot update score again.",
                    matchId
                );
            }

            boolean success = manager.updateMatchScore(activeTournament, matchId, homeScore, awayScore);
            if (success) {
                // Recalculate standings after each match update
                activeTournament.calculateStandings();
            }
        } catch (IllegalArgumentException e) {
            System.out.println("  [ERROR] " + e.getMessage());
        } catch (MatchException e) {
            // Catching our custom MatchException
            System.out.println("  [ERROR] " + e.getMessage());
        }
    }

    // ================================================================
    //                  5. POINTS TABLE MENU
    // ================================================================
    static void pointsTableMenu() {
        if (activeTournament == null) {
            System.out.println("  [!] No active tournament selected.");
            return;
        }

        System.out.println("\n  Calculating standings for: " + activeTournament.getTournamentName());
        activeTournament.calculateStandings(); // Polymorphic call
        activeTournament.getPointsTable().displayTable();

        System.out.println("\n  Current Leader: " + activeTournament.getPointsTable().getLeader());
    }

    // ================================================================
    //                  6. WINNER DECLARATION MENU
    // ================================================================
    static void winnerMenu() {
        if (activeTournament == null) {
            System.out.println("  [!] No active tournament selected.");
            return;
        }

        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║            WINNER DECLARATION                    ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println("  Tournament: " + activeTournament.getTournamentName());
        System.out.println();

        // Declare winner - Polymorphic call
        String winner = activeTournament.declareWinner();
        System.out.println("  " + winner);

        // Bonus: Top Scorer and Man of the Tournament
        System.out.println("\n  --- Bonus Awards ---");
        manager.determineTopScorer(activeTournament);
        System.out.println("  🏅 Man of Tournament : " + manager.getManOfTheTournament());
        System.out.println("  🥇 Top Scorer        : " + manager.getTopScorer());

        if (activeTournament.isTournamentOver()) {
            System.out.println("\n  *** TOURNAMENT COMPLETED ***");
        }
    }

    // ================================================================
    //                  8. FILE HANDLING MENU
    // ================================================================
    static void fileMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║       SAVE / LOAD DATA               ║");
            System.out.println("║       (File Handling Module)         ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║  1. Save All Players to File         ║");
            System.out.println("║  2. Load & Display Players from File ║");
            System.out.println("║  3. Save Tournament Report to File   ║");
            System.out.println("║  4. Load & Display Tournament Report ║");
            System.out.println("║  0. Back to Main Menu                ║");
            System.out.println("╚══════════════════════════════════════╝");

            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1:
                    // Save player list using FileWriter
                    FileHandler.savePlayers(manager.getAllPlayers());
                    break;
                case 2:
                    // Load and display players using BufferedReader
                    FileHandler.loadAndDisplayPlayers();
                    break;
                case 3:
                    // Save tournament report
                    if (activeTournament == null) {
                        System.out.println("  [!] No active tournament selected.");
                    } else {
                        FileHandler.saveTournamentReport(activeTournament);
                    }
                    break;
                case 4:
                    // Load tournament report from file
                    FileHandler.loadAndDisplayTournamentReport();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("  [!] Invalid option.");
            }
        }
    }

    // ================================================================
    //                  9. SPORT INFO MENU
    // ================================================================
    static void sportInfoMenu() {
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║       SPORT INFORMATION          ║");
        System.out.println("╠══════════════════════════════════╣");
        System.out.println("║  1. Cricket                      ║");
        System.out.println("║  2. Football                     ║");
        System.out.println("║  3. Volleyball                   ║");
        System.out.println("║  4. Badminton                    ║");
        System.out.println("╚══════════════════════════════════╝");

        int choice = readInt("  Choose a sport: ");

        // OOP Concept: Polymorphism (runtime binding via abstract class reference)
        Sports sport = null;
        switch (choice) {
            case 1: sport = new Cricket();    break;
            case 2: sport = new Football();   break;
            case 3: sport = new Volleyball(); break;
            case 4: sport = new Badminton();  break;
            default: System.out.println("  [!] Invalid choice."); return;
        }

        // Polymorphic method call - actual method depends on subclass
        sport.displaySportInfo();
    }

    // ================================================================
    //                    HELPER METHODS
    // ================================================================

    /**
     * Read an integer safely with exception handling.
     * Demonstrates: Exception Handling (NumberFormatException)
     */
    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = sc.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                // Catching standard Java exception
                System.out.println("  [!] Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Map sport number to sport name string.
     */
    static String getSportName(int choice) {
        switch (choice) {
            case 1: return "Cricket";
            case 2: return "Football";
            case 3: return "Volleyball";
            case 4: return "Badminton";
            default: return "General";
        }
    }

    /**
     * Print the application banner.
     */
    static void printBanner() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                              ║");
        System.out.println("║    ███████╗███╗   ███╗ █████╗ ██████╗ ████████╗             ║");
        System.out.println("║    ██╔════╝████╗ ████║██╔══██╗██╔══██╗╚══██╔══╝             ║");
        System.out.println("║    ███████╗██╔████╔██║███████║██████╔╝   ██║                ║");
        System.out.println("║    ╚════██║██║╚██╔╝██║██╔══██║██╔══██╗   ██║                ║");
        System.out.println("║    ███████║██║ ╚═╝ ██║██║  ██║██║  ██║   ██║                ║");
        System.out.println("║    ╚══════╝╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝  ╚═╝                ║");
        System.out.println("║                                                              ║");
        System.out.println("║         SPORTS TOURNAMENT MANAGEMENT SYSTEM                 ║");
        System.out.println("║             Second Year Engineering | OOP Java               ║");
        System.out.println("║                                                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }
}
