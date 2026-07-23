import admin.TournamentManager;
import database.DatabaseConnection;
import javax.swing.SwingUtilities;

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
 * ║    ✔ JDBC Database Connectivity (MySQL / SQLite)            ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Initializing Database Connection and Schema...");
        
        // Auto-create database & tables if they do not exist
        DatabaseConnection.initializeDatabase();

        // Launch the Swing GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Central manager instance that coordinates all data & DB operations
                TournamentManager manager = new TournamentManager();
                
                // Launch LoginPage as the starting interface
                LoginPage login = new LoginPage(manager);
                login.setVisible(true);
            }
        });
    }
}
