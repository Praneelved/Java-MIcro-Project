import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import admin.TournamentManager;

/**
 * ============================================================
 * LoginPage.java
 * ============================================================
 * Swing-based Administrator Login Screen.
 * Demonstrates:
 *   - GUI Components (JFrame, JPanel, JLabel, JTextField, JPasswordField, JButton)
 *   - Event Handling (ActionListener)
 *   - Layout Managers (GridBagLayout for alignment)
 * ============================================================
 */
public class LoginPage extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private TournamentManager manager;

    public LoginPage(TournamentManager manager) {
        this.manager = manager;

        // Configure Frame
        setTitle("Smart Sports Tournament Management - Admin Login");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);

        // Main Container Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 246, 250));

        // Header Panel (College Microproject look)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185)); // Classic blue
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel lblTitle = new JLabel("Smart Multi-Sports TMS");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Panel (GridBagLayout for professional alignment)
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblUser, gbc);

        // Username Field
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        txtUsername = new JTextField(15);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtUsername, gbc);

        // Password Label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(lblPass, gbc);

        // Password Field
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtPassword, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Action Panel
        JPanel actionPanel = new JPanel();
        actionPanel.setBackground(new Color(245, 246, 250));
        actionPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(46, 204, 113)); // Emerald green
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(120, 35));

        // Event handling - verification of hardcoded administrator credentials
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText().trim();
                String password = new String(txtPassword.getPassword());

                if (username.equals("admin") && password.equals("admin123")) {
                    // Open Dashboard
                    Dashboard dashboard = new Dashboard(manager);
                    dashboard.setVisible(true);
                    // Close login window
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(
                            LoginPage.this,
                            "Invalid Username or Password!\nHint: admin / admin123",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        actionPanel.add(btnLogin);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);

        // Add main container to Frame
        add(mainPanel);
    }
}
