package player;

/**
 * ============================================================
 * Player.java
 * ============================================================
 * OOP Concept: CLASS, OBJECT, ENCAPSULATION
 * Represents a player with private fields and public methods.
 * Demonstrates:
 *   - Encapsulation (private fields, getters/setters)
 *   - Method Overloading (multiple constructors)
 *   - toString() Override (Polymorphism)
 * ============================================================
 */
public class Player {

    // Private fields - Encapsulation
    private static int idCounter = 1;  // Auto-increment ID counter
    private int playerId;
    private String name;
    private int age;
    private String sport;
    private String teamName;
    private int jerseyNumber;
    private int goalsScored;    // For top scorer tracking
    private int totalMatches;

    // -------------------------------------------------------
    // Method Overloading - Constructor 1 (Full Details)
    // -------------------------------------------------------
    public Player(String name, int age, String sport, String teamName, int jerseyNumber) {
        this.playerId     = idCounter++;
        this.name         = name;
        this.age          = age;
        this.sport        = sport;
        this.teamName     = teamName;
        this.jerseyNumber = jerseyNumber;
        this.goalsScored  = 0;
        this.totalMatches = 0;
    }

    // -------------------------------------------------------
    // Method Overloading - Constructor 2 (Without Team)
    // -------------------------------------------------------
    public Player(String name, int age, String sport) {
        this(name, age, sport, "Unassigned", 0);
    }

    // -------------------------------------------------------
    // Getters and Setters (Encapsulation)
    // -------------------------------------------------------
    public int getPlayerId()             { return playerId; }
    public void setPlayerId(int id)      { this.playerId = id; }
    public String getName()              { return name; }
    public void setName(String name)     { this.name = name; }

    public int getAge()                  { return age; }
    public void setAge(int age)          { this.age = age; }

    public String getSport()             { return sport; }
    public void setSport(String sport)   { this.sport = sport; }

    public String getTeamName()                    { return teamName; }
    public void setTeamName(String teamName)       { this.teamName = teamName; }

    public int getJerseyNumber()                   { return jerseyNumber; }
    public void setJerseyNumber(int jerseyNumber)  { this.jerseyNumber = jerseyNumber; }

    public int getGoalsScored()          { return goalsScored; }
    public void addGoal()                { this.goalsScored++; }
    public void addGoals(int goals)      { this.goalsScored += goals; } // Method Overloading

    public int getTotalMatches()         { return totalMatches; }
    public void incrementMatches()       { this.totalMatches++; }

    // -------------------------------------------------------
    // Display player info in formatted way
    // -------------------------------------------------------
    public void displayPlayer() {
        System.out.println("-------------------------------------------");
        System.out.printf("  Player ID    : %d%n", playerId);
        System.out.printf("  Name         : %s%n", name);
        System.out.printf("  Age          : %d years%n", age);
        System.out.printf("  Sport        : %s%n", sport);
        System.out.printf("  Team         : %s%n", teamName);
        System.out.printf("  Jersey No.   : %d%n", jerseyNumber);
        System.out.printf("  Goals/Runs   : %d%n", goalsScored);
        System.out.printf("  Total Matches: %d%n", totalMatches);
        System.out.println("-------------------------------------------");
    }

    // -------------------------------------------------------
    // toString - Method Overriding (Polymorphism)
    // -------------------------------------------------------
    @Override
    public String toString() {
        return String.format("[ID:%d] %s | Jersey#%d | %s | %s",
                playerId, name, jerseyNumber, sport, teamName);
    }

    // Reset ID counter (for testing)
    public static void resetIdCounter() {
        idCounter = 1;
    }
}
