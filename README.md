# Smart Multi-Sports Tournament Management System

A comprehensive Java Swing based desktop application designed for managing multi-sports tournaments. This project was developed as a Second-Year Engineering Object-Oriented Programming (OOP) Java Microproject.

## Features

- **Modern Swing GUI**: A user-friendly desktop interface with multiple screens (Login, Dashboard) and a tabbed navigation system.
- **Multi-Sport Support**: Supports managing tournaments for Cricket, Football, Badminton, Volleyball, and Basketball.
- **Database Persistence**: Uses JDBC to connect to a database for persistent storage of players, teams, matches, and tournaments.
  - Defaults to **MySQL**.
  - Automatically falls back to **SQLite** if MySQL is unavailable, ensuring the application always runs without complex setup.
- **Player & Team Registration**: Register players and assign them to teams.
- **Tournament Management**: Create tournaments, schedule matches, and record scores.
- **Points Table**: Automatically generates and displays real-time points tables based on match results.

## Tech Stack

- **Frontend**: Java Swing (JFrame, JPanel, JTable, CardLayout, etc.)
- **Backend**: Core Java (OOP Principles: Encapsulation, Inheritance, Polymorphism, Abstraction)
- **Database**: MySQL (Primary) / SQLite (Fallback)
- **Connectivity**: JDBC (Java Database Connectivity)

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- MySQL Server (Optional, if you want to use the primary database mode)
- JDBC Drivers (Included in the `lib` folder)

## Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/Praneelved/Java-MIcro-Project.git
cd Java-MIcro-Project
```

### 2. Database Setup (Optional - for MySQL)
By default, the application will attempt to connect to MySQL. If it fails, it will automatically create and use a local SQLite database (`sports_tournament.db`). 

If you want to use MySQL:
1. Ensure MySQL Server is running.
2. Update the credentials in `src/database/DatabaseConnection.java` if your local MySQL uses a different username/password than the default (`root` / `root`).
3. The application will automatically create the `sports_tournament` database and the required tables on the first run.

### 3. Compile and Run (Windows)
A batch script is provided for easy compilation and execution on Windows. Double-click the script or run it from the command line:

```cmd
run.bat
```

This script will:
1. Compile all `.java` files from the `src` directory into the `bin` directory.
2. Include the JDBC drivers from the `lib` directory in the classpath.
3. Launch the Java Swing application.

## Project Structure

```
Java-MIcro-Project/
├── src/                # Java source code files
│   ├── admin/          # Admin and Tournament management logic
│   ├── database/       # Database connection and schema initialization
│   ├── match/          # Match scheduling and scoring logic
│   ├── player/         # Player entities
│   ├── sports/         # Sport-specific logic
│   ├── team/           # Team entities
│   ├── tournament/     # Tournament and Points Table logic
│   ├── Dashboard.java  # Main Swing GUI Dashboard
│   ├── LoginPage.java  # Swing GUI Login Screen
│   └── Main.java       # Application entry point
├── lib/                # Third-party libraries (JDBC drivers)
│   ├── mysql-connector-j-9.0.0.jar
│   └── sqlite-jdbc-3.46.0.1.jar
├── bin/                # Compiled .class files (generated automatically)
├── run.bat             # Windows compile and run script
└── .gitignore          # Git ignore file
```

## Contributing

Feel free to fork the repository and submit pull requests for any enhancements or bug fixes.

## License

This project is open-source and available under the MIT License.
