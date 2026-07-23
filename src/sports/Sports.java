package sports;

/**
 * ============================================================
 * Sports.java - Abstract Class
 * ============================================================
 * OOP Concept: ABSTRACTION
 * This abstract class defines the blueprint for all sports.
 * It cannot be instantiated directly. Subclasses (Cricket,
 * Football, etc.) must implement the abstract methods.
 * ============================================================
 */
public abstract class Sports {

    // Encapsulation: private fields with public getters/setters
    private String sportName;
    private int maxPlayersPerTeam;
    private int minPlayersPerTeam;

    // Constructor
    public Sports(String sportName, int maxPlayersPerTeam, int minPlayersPerTeam) {
        this.sportName = sportName;
        this.maxPlayersPerTeam = maxPlayersPerTeam;
        this.minPlayersPerTeam = minPlayersPerTeam;
    }

    // Abstract Methods - must be implemented by subclasses
    // OOP Concept: ABSTRACTION (forcing subclasses to define behavior)
    public abstract String getSportRules();
    public abstract int getMatchDuration(); // in minutes
    public abstract String getScoringSystem();

    // Concrete method available to all subclasses
    public void displaySportInfo() {
        System.out.println("===========================================");
        System.out.println("Sport         : " + sportName);
        System.out.println("Max Players   : " + maxPlayersPerTeam);
        System.out.println("Min Players   : " + minPlayersPerTeam);
        System.out.println("Duration      : " + getMatchDuration() + " minutes");
        System.out.println("Scoring System: " + getScoringSystem());
        System.out.println("Rules         : " + getSportRules());
        System.out.println("===========================================");
    }

    // Getters and Setters (Encapsulation)
    public String getSportName() { return sportName; }
    public void setSportName(String sportName) { this.sportName = sportName; }

    public int getMaxPlayersPerTeam() { return maxPlayersPerTeam; }
    public void setMaxPlayersPerTeam(int max) { this.maxPlayersPerTeam = max; }

    public int getMinPlayersPerTeam() { return minPlayersPerTeam; }
    public void setMinPlayersPerTeam(int min) { this.minPlayersPerTeam = min; }

    // toString override - Method Overriding
    @Override
    public String toString() {
        return sportName + " (Max: " + maxPlayersPerTeam + " players)";
    }
}
