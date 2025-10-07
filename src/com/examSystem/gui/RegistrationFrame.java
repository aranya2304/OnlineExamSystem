package com.examSystem.gui;

import com.examSystem.dao.UserDAO;
import com.examSystem.model.User;
import com.examSystem.util.UIUtils;
import javax.swing.*;
import java.awt.*;

/**
 * Registration frame for new users
 */
public class RegistrationFrame extends JFrame {
    private UserDAO userDAO;
    private JTextField usernameField, emailField, fullNameField, phoneField;
    private JPasswordField passwordField, confirmPasswordField;
    private JTextArea addressArea;
    private JComboBox<User.UserType> userTypeCombo;

    public RegistrationFrame() {
        UIUtils.applyGlobalForegrounds(java.awt.Color.BLACK);

        userDAO = new UserDAO();
        initializeComponents();
        setTitle("User Registration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        pack();
        UIUtils.ensureBlackButtons(this);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Form fields
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        emailField = new JTextField(20);
        fullNameField = new JTextField(20);
        phoneField = new JTextField(20);
        addressArea = new JTextArea(3, 20);
        userTypeCombo = new JComboBox<>(new User.UserType[] { User.UserType.STUDENT, User.UserType.TEACHER });

        // Add components
        int row = 0;
        addFormField(mainPanel, gbc, "Username:", usernameField, row++);
        addFormField(mainPanel, gbc, "Password:", passwordField, row++);
        addFormField(mainPanel, gbc, "Confirm Password:", confirmPasswordField, row++);
        addFormField(mainPanel, gbc, "Email:", emailField, row++);
        addFormField(mainPanel, gbc, "Full Name:", fullNameField, row++);
        addFormField(mainPanel, gbc, "Phone:", phoneField, row++);
        addFormField(mainPanel, gbc, "User Type:", userTypeCombo, row++);
        addFormField(mainPanel, gbc, "Address:", new JScrollPane(addressArea), row++);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton registerButton = new JButton("Register");
        JButton cancelButton = new JButton("Cancel");

        registerButton.addActionListener(e -> registerUser());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, Component field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);
    }

    private void registerUser() {
        // Basic validation
        if (usernameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create user object
        User user = new User(
                usernameField.getText().trim(),
                password,
                emailField.getText().trim(),
                fullNameField.getText().trim(),
                (User.UserType) userTypeCombo.getSelectedItem());
        user.setPhone(phoneField.getText().trim());
        user.setAddress(addressArea.getText().trim());

        // Try to create user
        if (userDAO.createUser(user)) {
            JOptionPane.showMessageDialog(this, "User registered successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed! Username might already exist.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}