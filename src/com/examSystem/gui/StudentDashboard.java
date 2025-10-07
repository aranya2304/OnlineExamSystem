package com.examSystem.gui;

import com.examSystem.dao.ExamDAO;
import com.examSystem.dao.UserDAO;
import com.examSystem.model.User;
import com.examSystem.model.Exam;
import com.examSystem.util.UIUtils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Student Dashboard for Online Examination System
 */
public class StudentDashboard extends JFrame {
    private User currentUser;
    private ExamDAO examDAO;
    private UserDAO userDAO;

    private JLabel welcomeLabel;
    private JTable examTable;
    private DefaultTableModel tableModel;
    private JButton takeExamButton;
    private JButton viewResultsButton;
    private JButton logoutButton;
    private JButton refreshButton;

    public StudentDashboard(User user) {
        this.currentUser = user;
        this.examDAO = new ExamDAO();
        this.userDAO = new UserDAO();

        UIUtils.applyGlobalForegrounds(java.awt.Color.BLACK);

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadAvailableExams();

        setTitle("Student Dashboard - " + user.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(new Color(25, 25, 112));

        // Table for available exams
        String[] columnNames = { "Exam ID", "Exam Title", "Subject", "Duration (min)", "Total Marks", "Start Time",
                "End Time", "Status" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        examTable = new JTable(tableModel);
        examTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        examTable.setRowHeight(25);
        examTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        examTable.getTableHeader().setBackground(new Color(70, 130, 180));
        examTable.getTableHeader().setForeground(Color.WHITE);

        // Buttons
        takeExamButton = new JButton("Take Selected Exam");
        takeExamButton.setBackground(new Color(34, 139, 34));
        takeExamButton.setForeground(Color.WHITE);
        takeExamButton.setFont(new Font("Arial", Font.BOLD, 14));

        viewResultsButton = new JButton("View My Results");
        viewResultsButton.setBackground(new Color(70, 130, 180));
        viewResultsButton.setForeground(Color.WHITE);
        viewResultsButton.setFont(new Font("Arial", Font.BOLD, 14));

        refreshButton = new JButton("Refresh Exams");
        refreshButton.setBackground(new Color(255, 165, 0));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));

        logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 20, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(Color.WHITE);
        logoutPanel.add(logoutButton);
        headerPanel.add(logoutPanel, BorderLayout.EAST);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // Exams section
        JPanel examSection = new JPanel(new BorderLayout());
        examSection.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                "Available Exams",
                0, 0,
                new Font("Arial", Font.BOLD, 16),
                new Color(70, 130, 180)));

        JScrollPane scrollPane = new JScrollPane(examTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        examSection.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(takeExamButton);
        buttonPanel.add(viewResultsButton);
        buttonPanel.add(refreshButton);
        examSection.add(buttonPanel, BorderLayout.SOUTH);

        contentPanel.add(examSection, BorderLayout.CENTER);

        // Statistics panel
        JPanel statsPanel = createStatsPanel();
        contentPanel.add(statsPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        statsPanel.setPreferredSize(new Dimension(250, 400));
        statsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                "Quick Stats",
                0, 0,
                new Font("Arial", Font.BOLD, 16),
                new Color(70, 130, 180)));

        // Create stat cards
        JPanel totalExamsCard = createStatCard("Total Exams", String.valueOf(examDAO.getTotalExamsCount()),
                new Color(70, 130, 180));
        JPanel availableExamsCard = createStatCard("Available Now", "0", new Color(34, 139, 34));
        JPanel completedExamsCard = createStatCard("Completed", "0", new Color(255, 165, 0));
        JPanel upcomingExamsCard = createStatCard("Upcoming", "0", new Color(220, 20, 60));

        statsPanel.add(totalExamsCard);
        statsPanel.add(availableExamsCard);
        statsPanel.add(completedExamsCard);
        statsPanel.add(upcomingExamsCard);

        return statsPanel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setHorizontalAlignment(JLabel.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void setupEventHandlers() {
        takeExamButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                takeSelectedExam();
            }
        });

        viewResultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewResults();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAvailableExams();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        // Double-click to take exam
        examTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    takeSelectedExam();
                }
            }
        });
    }

    private void loadAvailableExams() {
        tableModel.setRowCount(0);

        try {
            List<Exam> exams = examDAO.getAllActiveExams();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (Exam exam : exams) {
                String status = examDAO.isExamAvailable(exam.getExamId()) ? "Available" : "Not Available";

                Object[] row = {
                        exam.getExamId(),
                        exam.getExamTitle(),
                        exam.getSubjectName(),
                        exam.getDurationMinutes(),
                        exam.getTotalMarks(),
                        exam.getStartTime().format(formatter),
                        exam.getEndTime().format(formatter),
                        status
                };
                tableModel.addRow(row);
            }

            // Enable/disable take exam button based on selection
            takeExamButton.setEnabled(examTable.getRowCount() > 0);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading exams: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void takeSelectedExam() {
        int selectedRow = examTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an exam to take!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int examId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String examTitle = (String) tableModel.getValueAt(selectedRow, 1);
        String status = (String) tableModel.getValueAt(selectedRow, 7);

        if (!"Available".equals(status)) {
            JOptionPane.showMessageDialog(this,
                    "This exam is not currently available for taking!",
                    "Exam Unavailable",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to start the exam: " + examTitle + "?\n" +
                        "Once started, the timer will begin counting down.",
                "Confirm Exam Start",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Open exam window
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new ExamWindow(currentUser, examId).setVisible(true);
                    dispose(); // Close dashboard when exam starts
                }
            });
        }
    }

    private void viewResults() {
        JOptionPane.showMessageDialog(this,
                "Results viewing feature will be implemented in the next version.",
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
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new LoginFrame().setVisible(true);
                }
            });
        }
    }
}