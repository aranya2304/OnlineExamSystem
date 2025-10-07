package com.examSystem.gui;

import com.examSystem.dao.ExamDAO;
import com.examSystem.dao.UserDAO;
import com.examSystem.model.User;
import com.examSystem.util.UIUtils;
import javax.swing.*;
import java.awt.*;

/**
 * Admin Dashboard for Online Examination System
 */
public class AdminDashboard extends JFrame {
    private User currentUser;
    private ExamDAO examDAO;
    private UserDAO userDAO;

    public AdminDashboard(User user) {
        this.currentUser = user;
        this.examDAO = new ExamDAO();
        this.userDAO = new UserDAO();

        UIUtils.applyGlobalForegrounds(java.awt.Color.BLACK);

        initializeComponents();
        UIUtils.ensureBlackButtons(this);

        setTitle("Admin Dashboard - " + user.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 25, 112));
        headerPanel.setPreferredSize(new Dimension(800, 80));

        JLabel titleLabel = new JLabel("Admin Dashboard - " + currentUser.getFullName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Main content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // Statistics
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(createStatCard("Total Users", String.valueOf(getTotalUsers()), new Color(70, 130, 180)), gbc);

        gbc.gridx = 1;
        contentPanel.add(
                createStatCard("Total Exams", String.valueOf(examDAO.getTotalExamsCount()), new Color(34, 139, 34)),
                gbc);

        gbc.gridx = 2;
        contentPanel.add(createStatCard("Active Teachers",
                String.valueOf(userDAO.getUserCountByType(User.UserType.TEACHER)), new Color(255, 165, 0)), gbc);

        gbc.gridx = 3;
        contentPanel.add(createStatCard("Active Students",
                String.valueOf(userDAO.getUserCountByType(User.UserType.STUDENT)), new Color(220, 20, 60)), gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton manageUsersBtn = new JButton("Manage Users");
        manageUsersBtn.setPreferredSize(new Dimension(150, 40));
        manageUsersBtn.addActionListener(e -> manageUsers());

        JButton systemReportsBtn = new JButton("System Reports");
        systemReportsBtn.setPreferredSize(new Dimension(150, 40));
        systemReportsBtn.addActionListener(e -> systemReports());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setPreferredSize(new Dimension(150, 40));
        logoutBtn.setBackground(new Color(220, 20, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> logout());

        buttonPanel.add(manageUsersBtn);
        buttonPanel.add(systemReportsBtn);
        buttonPanel.add(logoutBtn);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        contentPanel.add(buttonPanel, gbc);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(200, 120));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setHorizontalAlignment(JLabel.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private int getTotalUsers() {
        return userDAO.getUserCountByType(User.UserType.ADMIN) +
                userDAO.getUserCountByType(User.UserType.TEACHER) +
                userDAO.getUserCountByType(User.UserType.STUDENT);
    }

    private void manageUsers() {
        JOptionPane.showMessageDialog(this,
                "User management feature coming soon!",
                "Feature Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void systemReports() {
        JOptionPane.showMessageDialog(this,
                "System reports feature coming soon!",
                "Feature Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }
}