package com.examSystem.gui;

import com.examSystem.dao.UserDAO;
import com.examSystem.model.User;
import com.examSystem.util.UIUtils;
import java.awt.EventQueue;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Login window for the Online Examination System
 */
public class LoginFrame extends javax.swing.JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private UserDAO userDAO;

    public LoginFrame() {
        try {
            javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // ignore and continue with default L&F
        }

        // apply global UI defaults before creating components
        UIUtils.applyGlobalForegrounds(java.awt.Color.BLACK);

        // create DAO and build UI (use initializeComponents() which exists)
        userDAO = new UserDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();

        // Ensure button/text color is visible for this window
        UIUtils.ensureBlackButtons(this);

        setTitle("Online Examination System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        pack();
    }

    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register New User");

        // Set button colors and fonts
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));

        registerButton.setBackground(new Color(60, 179, 113));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 25, 112));
        headerPanel.setPreferredSize(new Dimension(400, 80));

        JLabel titleLabel = new JLabel("Online Examination System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Main Panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(passwordField, gbc);

        // Login Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(loginButton, gbc);

        // Register Button
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(registerButton, gbc);

        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegistrationForm();
            }
        });

        // Enter key support
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password!",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Show loading cursor
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            User user = userDAO.authenticateUser(username, password);

            if (user != null) {
                JOptionPane.showMessageDialog(this,
                        "Welcome, " + user.getFullName() + "!",
                        "Login Successful",
                        JOptionPane.INFORMATION_MESSAGE);

                // Open appropriate dashboard based on user type
                openDashboard(user);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password!",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Database connection error: " + ex.getMessage(),
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void openDashboard(User user) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                switch (user.getUserType()) {
                    case ADMIN:
                        new AdminDashboard(user).setVisible(true);
                        break;
                    case TEACHER:
                        new TeacherDashboard(user).setVisible(true);
                        break;
                    case STUDENT:
                        new StudentDashboard(user).setVisible(true);
                        break;
                    default:
                        JOptionPane.showMessageDialog(null,
                                "Unknown user type!",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void openRegistrationForm() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RegistrationFrame().setVisible(true);
            }
        });
    }

    // replace or add the main method with this version
    public static void main(String[] args) {
        System.out.println("LoginFrame: main() starting");
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoginFrame frame = new LoginFrame();
                    frame.setVisible(true);
                    System.out.println("LoginFrame: frame created and setVisible(true)");
                } catch (Throwable t) {
                    System.err.println("LoginFrame: exception during startup");
                    t.printStackTrace();
                }
            }
        });
    }
}