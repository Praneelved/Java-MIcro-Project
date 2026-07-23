import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import admin.TournamentManager;
import player.Player;
import team.Team;
import tournament.Tournament;
import tournament.KnockoutTournament;
import tournament.LeagueTournament;
import tournament.PointsTable;
import match.Match;
import exceptions.InvalidPlayerException;
import exceptions.TournamentException;

/**
 * ============================================================
 * Dashboard.java
 * ============================================================
 * Swing-based Main Application Window.
 * Implements the Admin Dashboard, registration forms, JTables,
 * points table, match scheduling, winner screen, and reset feature.
 * ============================================================
 */
public class Dashboard extends JFrame {

    private TournamentManager manager;
    private Tournament activeTournament = null;

    // Card Layout components
    private CardLayout cardLayout;
    private JPanel cardsPanel;

    // UI Metrics (Home Screen)
    private JLabel lblTotalPlayersVal;
    private JLabel lblTotalTeamsVal;
    private JLabel lblActiveTournamentsVal;
    private JLabel lblSelectedTournamentVal;

    // Tables
    private JTable tblPlayers;
    private JTable tblTeams;
    private JTable tblTournaments;
    private JTable tblMatches;
    private JTable tblPointsTable;

    // Player inputs
    private JTextField txtPlayerName, txtPlayerAge, txtPlayerJersey, txtPlayerGoals;
    private JComboBox<String> cbPlayerSport, cbPlayerTeam;

    // Team inputs
    private JTextField txtTeamName, txtTeamCoach, txtTeamMaxSize;
    private JComboBox<String> cbTeamSport;
    private JComboBox<String> cbTeamAssignTeam, cbTeamAssignPlayer;

    // Tournament inputs
    private JTextField txtTourName, txtTourStart, txtTourEnd;
    private JComboBox<String> cbTourSport, cbTourFormat;
    private JComboBox<String> cbTourAddTeam;

    // Match inputs
    private JTextField txtHomeScore, txtAwayScore;
    private JLabel lblSelectedMatchDesc;
    private Match selectedMatch = null;
    private JButton btnAdvanceRound;

    // Stats inputs
    private JLabel lblWinnerVal, lblTopScorerVal, lblManOfTourVal;

    public Dashboard(TournamentManager manager) {
        this.manager = manager;

        setTitle("Smart Multi-Sports Tournament Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window

        // Main window panel
        JPanel container = new JPanel(new BorderLayout());
        add(container);

        // --- Left Sidebar Panel ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(44, 62, 80)); // Dark navy
        sidebar.setPreferredSize(new Dimension(200, 700));

        // App Logo/Title
        JLabel lblLogo = new JLabel("Smart TMS");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblLogo);

        // Sidebar Navigation Buttons
        sidebar.add(createNavButton("Home / Dashboard", "HOME"));
        sidebar.add(createNavButton("Player Registry", "PLAYERS"));
        sidebar.add(createNavButton("Team Registry", "TEAMS"));
        sidebar.add(createNavButton("Tournaments", "TOURNAMENTS"));
        sidebar.add(createNavButton("Match Schedule", "MATCHES"));
        sidebar.add(createNavButton("Points Table", "POINTS"));
        sidebar.add(createNavButton("Stats & Winner", "STATS"));

        sidebar.add(Box.createVerticalGlue()); // Push reset button to the bottom

        // Reset Button
        JButton btnReset = new JButton("Reset Application");
        btnReset.setBackground(new Color(192, 57, 43)); // Dark Red
        btnReset.setForeground(Color.WHITE);
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReset.setMaximumSize(new Dimension(180, 40));
        btnReset.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReset.setFocusPainted(false);
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        Dashboard.this,
                        "Are you absolutely sure you want to RESET the application?\nThis will delete all players, teams, matches, and tournaments permanently!",
                        "Reset Application",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    manager.resetApplication();
                    activeTournament = null;
                    refreshAllTables();
                    JOptionPane.showMessageDialog(Dashboard.this, "Application has been reset to empty state.");
                }
            }
        });
        sidebar.add(btnReset);
        sidebar.add(Box.createVerticalStrut(20));

        container.add(sidebar, BorderLayout.WEST);

        // --- Center Panels using CardLayout ---
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);

        // Create cards
        cardsPanel.add(createHomeCard(), "HOME");
        cardsPanel.add(createPlayersCard(), "PLAYERS");
        cardsPanel.add(createTeamsCard(), "TEAMS");
        cardsPanel.add(createTournamentsCard(), "TOURNAMENTS");
        cardsPanel.add(createMatchesCard(), "MATCHES");
        cardsPanel.add(createPointsTableCard(), "POINTS");
        cardsPanel.add(createStatsCard(), "STATS");

        container.add(cardsPanel, BorderLayout.CENTER);

        // Initial table load
        refreshAllTables();
    }

    /**
     * Create a standard styled sidebar navigation button.
     */
    private JButton createNavButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(190, 40));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(44, 62, 80));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardsPanel, cardName);
                if (cardName.equals("POINTS") || cardName.equals("STATS")) {
                    refreshAllTables(); // Force update standings and stats on page load
                }
            }
        });
        return btn;
    }

    // =================================================================
    //                         CARDS CREATION
    // =================================================================

    /**
     * 1. Home / Dashboard Card
     */
    private JPanel createHomeCard() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Title
        JLabel lblTitle = new JLabel("College OOP Microproject Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(41, 128, 185));
        panel.add(lblTitle, BorderLayout.NORTH);

        // Info cards panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        statsPanel.setBackground(Color.WHITE);

        statsPanel.add(createInfoCard("Total Registered Players", lblTotalPlayersVal = new JLabel("0"), new Color(52, 152, 219)));
        statsPanel.add(createInfoCard("Total Registered Teams", lblTotalTeamsVal = new JLabel("0"), new Color(46, 204, 113)));
        statsPanel.add(createInfoCard("Total Tournaments", lblActiveTournamentsVal = new JLabel("0"), new Color(155, 89, 182)));

        JPanel centerContainer = new JPanel(new BorderLayout(10, 10));
        centerContainer.setBackground(Color.WHITE);
        centerContainer.add(statsPanel, BorderLayout.NORTH);

        // Display Active Tournament Details
        JPanel activeTourPanel = new JPanel(new BorderLayout());
        activeTourPanel.setBorder(BorderFactory.createTitledBorder("Active Selection"));
        activeTourPanel.setBackground(Color.WHITE);
        
        lblSelectedTournamentVal = new JLabel("Active Tournament: None. Select/Create in Tournaments page.");
        lblSelectedTournamentVal.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSelectedTournamentVal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        activeTourPanel.add(lblSelectedTournamentVal, BorderLayout.CENTER);

        centerContainer.add(activeTourPanel, BorderLayout.CENTER);
        panel.add(centerContainer, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInfoCard(String title, JLabel valLabel, Color bg) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bg);
        card.setPreferredSize(new Dimension(200, 120));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        valLabel.setForeground(Color.WHITE);
        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.CENTER);
        return card;
    }

    /**
     * 2. Players Registry Card
     */
    private JPanel createPlayersCard() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        // Form (Left Column)
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Player Details"));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; txtPlayerName = new JTextField(15);
        form.add(txtPlayerName, gbc);

        // Age
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1; txtPlayerAge = new JTextField();
        form.add(txtPlayerAge, gbc);

        // Sport
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Sport:"), gbc);
        gbc.gridx = 1;
        cbPlayerSport = new JComboBox<>(new String[]{"Cricket", "Football", "Volleyball", "Badminton", "Basketball"});
        form.add(cbPlayerSport, gbc);

        // Team
        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Team:"), gbc);
        gbc.gridx = 1; cbPlayerTeam = new JComboBox<>();
        form.add(cbPlayerTeam, gbc);

        // Jersey
        gbc.gridx = 0; gbc.gridy = 4;
        form.add(new JLabel("Jersey No:"), gbc);
        gbc.gridx = 1; txtPlayerJersey = new JTextField();
        form.add(txtPlayerJersey, gbc);

        // Goals/Runs scored
        gbc.gridx = 0; gbc.gridy = 5;
        form.add(new JLabel("Goals/Runs:"), gbc);
        gbc.gridx = 1; txtPlayerGoals = new JTextField("0");
        form.add(txtPlayerGoals, gbc);

        // Buttons Box
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        btnPanel.setBackground(Color.WHITE);
        JButton btnRegister = new JButton("Register");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");
        btnPanel.add(btnRegister); btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete); btnPanel.add(btnClear);
        form.add(btnPanel, gbc);

        panel.add(form, BorderLayout.WEST);

        // Table (Right Column)
        tblPlayers = new JTable();
        JScrollPane scroll = new JScrollPane(tblPlayers);
        panel.add(scroll, BorderLayout.CENTER);

        // Event handler for table selection
        tblPlayers.getSelectionModel().addListSelectionListener(e -> {
            int row = tblPlayers.getSelectedRow();
            if (row >= 0) {
                txtPlayerName.setText(tblPlayers.getValueAt(row, 1).toString());
                txtPlayerAge.setText(tblPlayers.getValueAt(row, 2).toString());
                cbPlayerSport.setSelectedItem(tblPlayers.getValueAt(row, 3).toString());
                cbPlayerTeam.setSelectedItem(tblPlayers.getValueAt(row, 4).toString());
                txtPlayerJersey.setText(tblPlayers.getValueAt(row, 5).toString());
                txtPlayerGoals.setText(tblPlayers.getValueAt(row, 6).toString());
            }
        });

        // Form actions
        btnRegister.addActionListener(e -> {
            try {
                String name = txtPlayerName.getText().trim();
                int age = Integer.parseInt(txtPlayerAge.getText().trim());
                String sport = cbPlayerSport.getSelectedItem().toString();
                String team = cbPlayerTeam.getSelectedItem() == null ? "Unassigned" : cbPlayerTeam.getSelectedItem().toString();
                int jersey = Integer.parseInt(txtPlayerJersey.getText().trim());

                Player p = manager.registerPlayer(name, age, sport, team, jersey);
                if (p != null && !team.equals("Unassigned")) {
                    // Update stats
                    int goals = Integer.parseInt(txtPlayerGoals.getText().trim());
                    if (goals > 0) {
                        manager.addGoalsToPlayer(p.getPlayerId(), goals);
                    }
                }
                refreshAllTables();
                JOptionPane.showMessageDialog(this, "Player Registered Successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter numeric values for Age, Jersey Number, and Goals/Runs.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (InvalidPlayerException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnUpdate.addActionListener(e -> {
            int row = tblPlayers.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a player from the table to update.");
                return;
            }
            int id = Integer.parseInt(tblPlayers.getValueAt(row, 0).toString());
            try {
                String name = txtPlayerName.getText().trim();
                int age = Integer.parseInt(txtPlayerAge.getText().trim());
                int jersey = Integer.parseInt(txtPlayerJersey.getText().trim());
                int goals = Integer.parseInt(txtPlayerGoals.getText().trim());

                boolean success = manager.updatePlayer(id, name, age, jersey);
                if (success) {
                    // goals update
                    Player p = manager.findPlayerById(id);
                    if (p != null) {
                        int currentGoals = p.getGoalsScored();
                        int diff = goals - currentGoals;
                        if (diff != 0) {
                            manager.addGoalsToPlayer(id, diff);
                        }
                    }
                }
                refreshAllTables();
                JOptionPane.showMessageDialog(this, "Player Updated Successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Numeric validation failed.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            int row = tblPlayers.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a player from the table to delete.");
                return;
            }
            int id = Integer.parseInt(tblPlayers.getValueAt(row, 0).toString());
            manager.removePlayer(id);
            refreshAllTables();
            JOptionPane.showMessageDialog(this, "Player Deleted Successfully!");
        });

        btnClear.addActionListener(e -> {
            txtPlayerName.setText("");
            txtPlayerAge.setText("");
            txtPlayerJersey.setText("");
            txtPlayerGoals.setText("0");
            cbPlayerSport.setSelectedIndex(0);
            cbPlayerTeam.setSelectedIndex(0);
        });

        return panel;
    }

    /**
     * 3. Teams Registry Card
     */
    private JPanel createTeamsCard() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        // Form (Left Column)
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Team Settings"));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Team Name
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Team Name:"), gbc);
        gbc.gridx = 1; txtTeamName = new JTextField(15);
        form.add(txtTeamName, gbc);

        // Sport
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Sport:"), gbc);
        gbc.gridx = 1;
        cbTeamSport = new JComboBox<>(new String[]{"Cricket", "Football", "Volleyball", "Badminton", "Basketball"});
        form.add(cbTeamSport, gbc);

        // Coach
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Coach Name:"), gbc);
        gbc.gridx = 1; txtTeamCoach = new JTextField();
        form.add(txtTeamCoach, gbc);

        // Max Squad Size
        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Max Size:"), gbc);
        gbc.gridx = 1; txtTeamMaxSize = new JTextField("15");
        form.add(txtTeamMaxSize, gbc);

        // Action Buttons
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        btnPanel.setBackground(Color.WHITE);
        JButton btnCreateTeam = new JButton("Create Team");
        JButton btnDeleteTeam = new JButton("Delete Team");
        btnPanel.add(btnCreateTeam); btnPanel.add(btnDeleteTeam);
        form.add(btnPanel, gbc);

        // --- Section: Assign Player to Team ---
        gbc.gridy = 5;
        form.add(new JSeparator(), gbc);

        gbc.gridy = 6;
        JLabel lblSection = new JLabel("Assign Player to Team");
        lblSection.setFont(new Font("Segoe UI", Font.BOLD, 12));
        form.add(lblSection, gbc);

        gbc.gridy = 7; gbc.gridwidth = 1;
        form.add(new JLabel("Team Name:"), gbc);
        gbc.gridx = 1; cbTeamAssignTeam = new JComboBox<>();
        form.add(cbTeamAssignTeam, gbc);

        gbc.gridx = 0; gbc.gridy = 8;
        form.add(new JLabel("Player Name:"), gbc);
        gbc.gridx = 1; cbTeamAssignPlayer = new JComboBox<>();
        form.add(cbTeamAssignPlayer, gbc);

        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2;
        JPanel assignBtnPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        assignBtnPanel.setBackground(Color.WHITE);
        JButton btnAssign = new JButton("Assign");
        JButton btnRemove = new JButton("Release");
        assignBtnPanel.add(btnAssign); assignBtnPanel.add(btnRemove);
        form.add(assignBtnPanel, gbc);

        panel.add(form, BorderLayout.WEST);

        // Table (Right Column)
        tblTeams = new JTable();
        JScrollPane scroll = new JScrollPane(tblTeams);
        panel.add(scroll, BorderLayout.CENTER);

        // Action Logic
        btnCreateTeam.addActionListener(e -> {
            try {
                String name = txtTeamName.getText().trim();
                String sport = cbTeamSport.getSelectedItem().toString();
                String coach = txtTeamCoach.getText().trim();
                int size = Integer.parseInt(txtTeamMaxSize.getText().trim());

                manager.createTeam(name, sport, coach, size);
                refreshAllTables();
                JOptionPane.showMessageDialog(this, "Team Created successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid max size number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (TournamentException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Duplication Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDeleteTeam.addActionListener(e -> {
            int row = tblTeams.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a team from table to delete.");
                return;
            }
            int id = Integer.parseInt(tblTeams.getValueAt(row, 0).toString());
            manager.removeTeam(id);
            refreshAllTables();
            JOptionPane.showMessageDialog(this, "Team deleted and players released.");
        });

        btnAssign.addActionListener(e -> {
            if (cbTeamAssignTeam.getSelectedItem() == null || cbTeamAssignPlayer.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Select valid team and player.");
                return;
            }
            String teamStr = cbTeamAssignTeam.getSelectedItem().toString();
            String playerStr = cbTeamAssignPlayer.getSelectedItem().toString();

            // Extract IDs
            int playerId = Integer.parseInt(playerStr.split(" - ")[0].replace("[ID:", "").replace("]", ""));
            boolean ok = manager.addPlayerToTeam(playerId, teamStr);
            if (ok) {
                refreshAllTables();
                JOptionPane.showMessageDialog(this, "Player assigned successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Roster limit reached or sport mismatch.", "Assignment Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRemove.addActionListener(e -> {
            if (cbTeamAssignTeam.getSelectedItem() == null || cbTeamAssignPlayer.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Select valid team and player.");
                return;
            }
            String teamStr = cbTeamAssignTeam.getSelectedItem().toString();
            String playerStr = cbTeamAssignPlayer.getSelectedItem().toString();

            int playerId = Integer.parseInt(playerStr.split(" - ")[0].replace("[ID:", "").replace("]", ""));
            manager.removePlayerFromTeam(playerId, teamStr);
            refreshAllTables();
            JOptionPane.showMessageDialog(this, "Player released from team.");
        });

        return panel;
    }

    /**
     * 4. Tournament Card
     */
    private JPanel createTournamentsCard() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        // Form (Left Column)
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("New Tournament"));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Tour Name:"), gbc);
        gbc.gridx = 1; txtTourName = new JTextField(15);
        form.add(txtTourName, gbc);

        // Sport
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Sport:"), gbc);
        gbc.gridx = 1;
        cbTourSport = new JComboBox<>(new String[]{"Cricket", "Football", "Volleyball", "Badminton", "Basketball"});
        form.add(cbTourSport, gbc);

        // Format
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Format:"), gbc);
        gbc.gridx = 1;
        cbTourFormat = new JComboBox<>(new String[]{"League", "Knockout"});
        form.add(cbTourFormat, gbc);

        // Start Date
        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1; txtTourStart = new JTextField("01-Aug-2025");
        form.add(txtTourStart, gbc);

        // End Date
        gbc.gridx = 0; gbc.gridy = 4;
        form.add(new JLabel("End Date:"), gbc);
        gbc.gridx = 1; txtTourEnd = new JTextField("15-Aug-2025");
        form.add(txtTourEnd, gbc);

        // Create Button
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JButton btnCreate = new JButton("Create & Set Active");
        form.add(btnCreate, gbc);

        // Set Selected Active Button
        gbc.gridy = 6;
        JButton btnSetActive = new JButton("Set Selected Active");
        form.add(btnSetActive, gbc);

        // --- Roster Selection ---
        gbc.gridy = 7;
        form.add(new JSeparator(), gbc);

        gbc.gridy = 8;
        form.add(new JLabel("Add Team to Active Tournament:"), gbc);

        gbc.gridy = 9; gbc.gridwidth = 1;
        cbTourAddTeam = new JComboBox<>();
        form.add(cbTourAddTeam, gbc);

        gbc.gridx = 1;
        JButton btnAddTeam = new JButton("Add Team");
        form.add(btnAddTeam, gbc);

        panel.add(form, BorderLayout.WEST);

        // Center section: displays all Tournaments JTable
        tblTournaments = new JTable();
        JScrollPane scroll = new JScrollPane(tblTournaments);
        panel.add(scroll, BorderLayout.CENTER);

        // Event actions
        btnCreate.addActionListener(e -> {
            try {
                String name = txtTourName.getText().trim();
                String sport = cbTourSport.getSelectedItem().toString();
                String format = cbTourFormat.getSelectedItem().toString();
                String start = txtTourStart.getText().trim();
                String end = txtTourEnd.getText().trim();

                Tournament t;
                if ("League".equals(format)) {
                    t = manager.createLeagueTournament(name, sport, start, end);
                } else {
                    t = manager.createKnockoutTournament(name, sport, start, end);
                }
                activeTournament = t;
                refreshAllTables();
                JOptionPane.showMessageDialog(this, "Tournament created and set as ACTIVE.");
            } catch (TournamentException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnSetActive.addActionListener(e -> {
            int row = tblTournaments.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a tournament from table first.");
                return;
            }
            String name = tblTournaments.getValueAt(row, 1).toString();
            Tournament t = manager.findTournamentByName(name);
            if (t != null) {
                activeTournament = t;
                refreshAllTables();
                JOptionPane.showMessageDialog(this, "Active tournament set to: " + t.getTournamentName());
            }
        });

        btnAddTeam.addActionListener(e -> {
            if (activeTournament == null) {
                JOptionPane.showMessageDialog(this, "Please set an ACTIVE tournament first.");
                return;
            }
            if (cbTourAddTeam.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "No valid teams available.");
                return;
            }
            String teamName = cbTourAddTeam.getSelectedItem().toString();
            Team team = manager.findTeamByName(teamName);

            if (team != null) {
                boolean ok = manager.addTeamToTournament(activeTournament, team);
                if (ok) {
                    refreshAllTables();
                    JOptionPane.showMessageDialog(this, teamName + " added to " + activeTournament.getTournamentName());
                } else {
                    JOptionPane.showMessageDialog(this, "Team already added or sport mismatch.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    /**
     * 5. Match Schedule and Scores Card
     */
    private JPanel createMatchesCard() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        // Header Panel info
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        headerPanel.setBackground(Color.WHITE);
        JButton btnGenerate = new JButton("Generate Fixtures");
        btnAdvanceRound = new JButton("Advance Knockout Round");
        btnAdvanceRound.setEnabled(false);

        headerPanel.add(btnGenerate);
        headerPanel.add(btnAdvanceRound);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Center Table matches
        tblMatches = new JTable();
        JScrollPane scroll = new JScrollPane(tblMatches);
        panel.add(scroll, BorderLayout.CENTER);

        // Right Score updater panel
        JPanel scorePanel = new JPanel(new GridBagLayout());
        scorePanel.setBorder(BorderFactory.createTitledBorder("Update Scores"));
        scorePanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        lblSelectedMatchDesc = new JLabel("Select a match from table.");
        lblSelectedMatchDesc.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        scorePanel.add(lblSelectedMatchDesc, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        scorePanel.add(new JLabel("Home Score:"), gbc);
        gbc.gridx = 1; txtHomeScore = new JTextField(5);
        scorePanel.add(txtHomeScore, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        scorePanel.add(new JLabel("Away Score:"), gbc);
        gbc.gridx = 1; txtAwayScore = new JTextField(5);
        scorePanel.add(txtAwayScore, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton btnUpdateScore = new JButton("Save Match Result");
        scorePanel.add(btnUpdateScore, gbc);

        panel.add(scorePanel, BorderLayout.EAST);

        // Selection Listener
        tblMatches.getSelectionModel().addListSelectionListener(e -> {
            int row = tblMatches.getSelectedRow();
            if (row >= 0 && activeTournament != null) {
                int id = Integer.parseInt(tblMatches.getValueAt(row, 0).toString());
                selectedMatch = activeTournament.findMatchById(id);
                if (selectedMatch != null) {
                    lblSelectedMatchDesc.setText(selectedMatch.getHomeTeam().getTeamName() + " vs " + selectedMatch.getAwayTeam().getTeamName());
                    txtHomeScore.setText(String.valueOf(selectedMatch.getHomeScore()));
                    txtAwayScore.setText(String.valueOf(selectedMatch.getAwayScore()));
                }
            }
        });

        // Button Actions
        btnGenerate.addActionListener(e -> {
            if (activeTournament == null) {
                JOptionPane.showMessageDialog(this, "Set an ACTIVE tournament first.");
                return;
            }
            boolean success = manager.generateFixtures(activeTournament);
            if (success) {
                refreshAllTables();
                JOptionPane.showMessageDialog(this, "Roster matchups generated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Tournament must have at least 2 teams.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnAdvanceRound.addActionListener(e -> {
            if (activeTournament instanceof KnockoutTournament) {
                manager.advanceKnockoutRound((KnockoutTournament) activeTournament);
                refreshAllTables();
                JOptionPane.showMessageDialog(this, "Knockout round advanced.");
            }
        });

        btnUpdateScore.addActionListener(e -> {
            if (activeTournament == null || selectedMatch == null) {
                JOptionPane.showMessageDialog(this, "Select a match from the table first.");
                return;
            }
            try {
                int home = Integer.parseInt(txtHomeScore.getText().trim());
                int away = Integer.parseInt(txtAwayScore.getText().trim());

                if (home < 0 || away < 0) {
                    JOptionPane.showMessageDialog(this, "Scores cannot be negative.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (selectedMatch.getStatus() == Match.Status.COMPLETED) {
                    int reconfirm = JOptionPane.showConfirmDialog(
                            this,
                            "This match is already completed. Overwrite scores and update team rankings?",
                            "Overwrite Score",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (reconfirm != JOptionPane.YES_OPTION) return;
                }

                manager.updateMatchScore(activeTournament, selectedMatch.getMatchId(), home, away);
                refreshAllTables();
                JOptionPane.showMessageDialog(this, "Match Score updated!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    /**
     * 6. Points Table Card
     */
    private JPanel createPointsTableCard() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Points Standings (Active Tournament)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblTitle, BorderLayout.NORTH);

        tblPointsTable = new JTable();
        JScrollPane scroll = new JScrollPane(tblPointsTable);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 7. Stats and Winner Screen Card
     */
    private JPanel createStatsCard() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblTitle = new JLabel("Tournament Winners & Awards Statistics");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(142, 68, 173));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;

        // Winner Card
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Tournament Champion:"), gbc);
        gbc.gridx = 1; lblWinnerVal = new JLabel("TBD");
        lblWinnerVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblWinnerVal.setForeground(new Color(230, 126, 34)); // Orange
        panel.add(lblWinnerVal, gbc);

        // Top Scorer
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Top Scorer / Run Scorer:"), gbc);
        gbc.gridx = 1; lblTopScorerVal = new JLabel("TBD");
        lblTopScorerVal.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(lblTopScorerVal, gbc);

        // Man of the Tournament
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Man of the Tournament:"), gbc);
        gbc.gridx = 1; lblManOfTourVal = new JLabel("TBD");
        lblManOfTourVal.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(lblManOfTourVal, gbc);

        // Action Declare Button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton btnDeclare = new JButton("Declare Champion & Awards");
        btnDeclare.setBackground(new Color(41, 128, 185));
        btnDeclare.setForeground(Color.WHITE);
        btnDeclare.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnDeclare.setPreferredSize(new Dimension(200, 45));
        panel.add(btnDeclare, gbc);

        btnDeclare.addActionListener(e -> {
            if (activeTournament == null) {
                JOptionPane.showMessageDialog(this, "No active tournament selected.");
                return;
            }
            
            // Call polymorphic winner logic
            String winnerMsg = activeTournament.declareWinner();
            manager.determineTopScorer(activeTournament);

            lblWinnerVal.setText(winnerMsg);
            lblTopScorerVal.setText(manager.getTopScorer());
            lblManOfTourVal.setText(manager.getManOfTheTournament());

            JOptionPane.showMessageDialog(this, "Awards generated:\n" + winnerMsg + "\nTop Scorer: " + manager.getTopScorer());
            
            refreshAllTables();
        });

        return panel;
    }

    // =================================================================
    //                       REFRESH SYSTEM STATE
    // =================================================================

    /**
     * Re-queries database and populates UI elements dynamically.
     * Keeps JTables, JComboBoxes, and Statistics synchronized.
     */
    private void refreshAllTables() {
        // Sync lists from SQLite/MySQL database
        manager.loadDataFromDatabase();

        // 1. Home Dashboard Metrics
        lblTotalPlayersVal.setText(String.valueOf(manager.getAllPlayers().size()));
        lblTotalTeamsVal.setText(String.valueOf(manager.getAllTeams().size()));
        lblActiveTournamentsVal.setText(String.valueOf(manager.getAllTournaments().size()));

        if (activeTournament != null) {
            // Find active tournament in current db list to fetch updated state
            Tournament fresh = null;
            for (Tournament t : manager.getAllTournaments()) {
                if (t.getTournamentId() == activeTournament.getTournamentId()) {
                    fresh = t;
                    break;
                }
            }
            if (fresh != null) {
                activeTournament = fresh;
            }

            lblSelectedTournamentVal.setText("Active Tournament: " + activeTournament.getTournamentName() +
                    " (" + activeTournament.getSport() + " | " + activeTournament.getFormat() + ")");
        } else {
            lblSelectedTournamentVal.setText("Active Tournament: None. Select/Create in Tournaments page.");
        }

        // 2. Players Table
        DefaultTableModel modelPlayers = new DefaultTableModel(
                new Object[]{"ID", "Name", "Age", "Sport", "Team", "Jersey", "Goals/Runs", "Matches"}, 0
        );
        for (Player p : manager.getAllPlayers()) {
            modelPlayers.addRow(new Object[]{
                    p.getPlayerId(), p.getName(), p.getAge(), p.getSport(),
                    p.getTeamName(), p.getJerseyNumber(), p.getGoalsScored(), p.getTotalMatches()
            });
        }
        tblPlayers.setModel(modelPlayers);

        // 3. Teams Table
        DefaultTableModel modelTeams = new DefaultTableModel(
                new Object[]{"ID", "Team Name", "Sport", "Coach", "Max Size", "Roster Size"}, 0
        );
        for (Team t : manager.getAllTeams()) {
            modelTeams.addRow(new Object[]{
                    t.getTeamId(), t.getTeamName(), t.getSport(), t.getCoach(), t.getMaxSize(), t.getPlayerCount()
            });
        }
        tblTeams.setModel(modelTeams);

        // 4. Tournaments Table
        DefaultTableModel modelTournaments = new DefaultTableModel(
                new Object[]{"ID", "Tournament Name", "Sport", "Format", "Start Date", "End Date", "Status", "Winner"}, 0
        );
        for (Tournament t : manager.getAllTournaments()) {
            String winner = "";
            if (!t.isActive()) {
                winner = t.declareWinner();
            } else if (t instanceof KnockoutTournament) {
                winner = ((KnockoutTournament) t).getChampion();
            } else {
                winner = "In Progress";
            }
            modelTournaments.addRow(new Object[]{
                    t.getTournamentId(), t.getTournamentName(), t.getSport(), t.getFormat(),
                    t.getStartDate(), t.getEndDate(), t.isActive() ? "ACTIVE" : "COMPLETED", winner
            });
        }
        tblTournaments.setModel(modelTournaments);

        // 5. Matches Table (Only for active tournament)
        DefaultTableModel modelMatches = new DefaultTableModel(
                new Object[]{"ID", "Round", "Home Team", "Away Team", "Score", "Venue", "Status"}, 0
        );
        if (activeTournament != null) {
            for (Match m : activeTournament.getMatches()) {
                String scoreStr = (m.getStatus() == Match.Status.COMPLETED) ?
                        (m.getHomeScore() + " - " + m.getAwayScore()) : "vs";
                modelMatches.addRow(new Object[]{
                        m.getMatchId(), m.getRoundName(), m.getHomeTeam().getTeamName(),
                        m.getAwayTeam().getTeamName(), scoreStr, m.getVenue(), m.getStatus().name()
                });
            }

            // Enable knockout round advancer if knockout
            if (activeTournament instanceof KnockoutTournament) {
                btnAdvanceRound.setEnabled(activeTournament.isActive());
            } else {
                btnAdvanceRound.setEnabled(false);
            }
        } else {
            btnAdvanceRound.setEnabled(false);
        }
        tblMatches.setModel(modelMatches);

        // 6. Points Table (Only for active tournament)
        DefaultTableModel modelPoints = new DefaultTableModel(
                new Object[]{"Rank", "Team Name", "Played", "Wins", "Losses", "Draws", "GF", "GA", "GD", "Points"}, 0
        );
        if (activeTournament != null) {
            activeTournament.calculateStandings();
            ArrayList<PointsTable.TeamRecord> records = activeTournament.getPointsTable().getRecords();
            for (int i = 0; i < records.size(); i++) {
                PointsTable.TeamRecord r = records.get(i);
                modelPoints.addRow(new Object[]{
                        (i + 1), r.teamName, r.played, r.wins, r.losses, r.draws,
                        r.goalsFor, r.goalsAgainst, r.getGoalDifference(), r.points
                });
            }
        }
        tblPointsTable.setModel(modelPoints);

        // 7. Dynamic ComboBoxes Population
        // cbPlayerTeam (Only show teams playing selected player's sport)
        cbPlayerTeam.removeAllItems();
        cbPlayerTeam.addItem("Unassigned");
        String selPlayerSport = cbPlayerSport.getSelectedItem() == null ? "" : cbPlayerSport.getSelectedItem().toString();
        for (Team t : manager.getAllTeams()) {
            if (t.getSport().equalsIgnoreCase(selPlayerSport)) {
                cbPlayerTeam.addItem(t.getTeamName());
            }
        }

        // cbTeamAssignTeam
        cbTeamAssignTeam.removeAllItems();
        for (Team t : manager.getAllTeams()) {
            cbTeamAssignTeam.addItem(t.getTeamName());
        }

        // cbTeamAssignPlayer (Only show players playing same sport as selected assign-team)
        cbTeamAssignPlayer.removeAllItems();
        String selAssignTeam = cbTeamAssignTeam.getSelectedItem() == null ? "" : cbTeamAssignTeam.getSelectedItem().toString();
        Team tAssign = manager.findTeamByName(selAssignTeam);
        for (Player p : manager.getAllPlayers()) {
            // Player is unassigned, or belongs to this team already, and plays same sport
            if (tAssign != null && p.getSport().equalsIgnoreCase(tAssign.getSport())) {
                if (p.getTeamName().equals("Unassigned") || p.getTeamName().equalsIgnoreCase(selAssignTeam)) {
                    cbTeamAssignPlayer.addItem("[ID:" + p.getPlayerId() + "] " + p.getName());
                }
            }
        }

        // cbTourAddTeam (Only show teams playing active tournament's sport)
        cbTourAddTeam.removeAllItems();
        if (activeTournament != null) {
            for (Team t : manager.getAllTeams()) {
                if (t.getSport().equalsIgnoreCase(activeTournament.getSport())) {
                    cbTourAddTeam.addItem(t.getTeamName());
                }
            }
        }

        // Refresh stats cards
        if (activeTournament != null) {
            if (!activeTournament.isActive() || activeTournament.isTournamentOver()) {
                lblWinnerVal.setText(activeTournament.declareWinner());
            } else {
                lblWinnerVal.setText("In Progress");
            }
            manager.determineTopScorer(activeTournament);
            lblTopScorerVal.setText(manager.getTopScorer());
            lblManOfTourVal.setText(manager.getManOfTheTournament());
        } else {
            lblWinnerVal.setText("No Active Tournament");
            lblTopScorerVal.setText("TBD");
            lblManOfTourVal.setText("TBD");
        }
    }
}
