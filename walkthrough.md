# Walkthrough — Sports Tournament Management System

## What Was Done

All planned changes have been implemented and the project **compiles with zero errors**.

---

## Changes Made

### 1. Fixed [`LeagueTournament.java`](file:///c:/Users/Praneel%20Ved/Desktop/project%20java/src/tournament/LeagueTournament.java)
Removed the Java Streams API (`.stream().filter().count()`) from `declareWinner()` — replaced with a plain `for` loop. This keeps the code at an appropriate 2nd-year level and easier to explain in a viva.

```java
// BEFORE (too advanced - Java Streams)
long completedCount = matches.stream()
        .filter(m -> m.getStatus() == Match.Status.COMPLETED)
        .count();

// AFTER (simple for loop - beginner-friendly)
int completedCount = 0;
for (Match m : matches) {
    if (m.getStatus() == Match.Status.COMPLETED) {
        completedCount++;
    }
}
```

---

### 2. New: [`MatchException.java`](file:///c:/Users/Praneel%20Ved/Desktop/project%20java/src/exceptions/MatchException.java)
A third custom exception. Thrown when a user tries to update the score of an already-completed match. Demonstrates **checked exceptions** (`extends Exception`) vs. the unchecked `InvalidPlayerException`.

> **Viva Point:** "We have 3 custom exceptions — `InvalidPlayerException` (unchecked), `TournamentException` (checked), and `MatchException` (checked)."

---

### 3. New: [`FileHandler.java`](file:///c:/Users/Praneel%20Ved/Desktop/project%20java/src/filehandler/FileHandler.java)
A new `filehandler` package with a static utility class. Covers all basic file handling concepts:

| Class Used | Purpose |
|---|---|
| `FileWriter` | Write player list and tournament report to `.txt` files |
| `FileReader` | Open a text file for reading |
| `BufferedReader` | Read file line-by-line efficiently |
| `IOException` | Catch file-related errors |

**Files created on disk:**
- `players.txt` — All registered players in CSV format
- `tournament_report.txt` — Full tournament summary with match results and winner

---

### 4. Updated [`Main.java`](file:///c:/Users/Praneel%20Ved/Desktop/project%20java/src/Main.java)
- Added **Menu Option 8: Save / Load Data** to the main menu
- Added `fileMenu()` method with 4 sub-options (save/load players, save/load tournament report)
- Updated `updateMatchScore()` to catch `MatchException` when trying to re-update a completed match — demonstrates **multiple catch blocks**
- Added imports for `FileHandler` and `MatchException`

---

### 5. Updated [`run.bat`](file:///c:/Users/Praneel%20Ved/Desktop/project%20java/run.bat)
Added `src/filehandler/*.java` to the javac compile command.

---

## Final OOP Concepts Coverage

| OOP Concept | Where |
|---|---|
| Classes & Objects | All `.java` files |
| Encapsulation | `Player`, `Team`, `Match` — private fields + getters/setters |
| Inheritance | `Cricket/Football/Volleyball/Badminton → Sports`; `League/Knockout → Tournament` |
| Polymorphism | `sport.displaySportInfo()`, `tournament.generateFixtures()`, `tournament.declareWinner()` |
| Abstraction | `Sports` (abstract class), `Tournament` (abstract class) |
| Interface | `TournamentRules` interface |
| Exception Handling | `InvalidPlayerException`, `TournamentException`, `MatchException`, multiple catch blocks |
| ArrayList | `Team.players`, `Tournament.teams`, `Tournament.matches` |
| HashMap | `TournamentManager.playerMap` (O(1) player lookup) |
| **File Handling** ✅ | `FileHandler.java` — `FileWriter`, `BufferedReader`, `FileReader`, `IOException` |

---

## How to Run

Double-click `run.bat` or run this in the project folder:

```
javac -d bin -sourcepath src src/Main.java ...
java -cp bin Main
```

## Verification

- ✅ Compiled with zero errors/warnings
- ✅ All 10 required OOP concepts present
- ✅ File Handling module complete (`players.txt`, `tournament_report.txt`)
- ✅ 3 custom exceptions (`InvalidPlayerException`, `TournamentException`, `MatchException`)
- ✅ No Streams API, no design patterns, no Spring Boot — genuine 2nd-year level code
