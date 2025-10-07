package com.examSystem.gui;

import com.examSystem.dao.ExamDAO;
import com.examSystem.dao.UserDAO;
import com.examSystem.model.User;
import com.examSystem.model.Exam;
import com.examSystem.util.UIUtils;
import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Teacher Dashboard for Online Examination System
 */
public class TeacherDashboard extends javax.swing.JFrame {
    private User currentUser;
    private ExamDAO examDAO;
    private UserDAO userDAO;

    private JLabel welcomeLabel;
    private JTable examTable;
    private DefaultTableModel tableModel;
    private JButton createExamButton;
    private JButton editExamButton;
    private JButton viewResultsButton;
    private JButton logoutButton;
    private JButton refreshButton;

    public TeacherDashboard(User user) {
        this.currentUser = user;
        this.examDAO = new ExamDAO();
        this.userDAO = new UserDAO();

        // apply global defaults early
        UIUtils.applyGlobalForegrounds(java.awt.Color.BLACK);

        // Build the UI
        initComponents();

        // Fix any white-on-white text in this window
        UIUtils.ensureBlackButtons(this);

        loadTeacherExams();

        setTitle("Teacher Dashboard - " + user.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(new Color(25, 25, 112));

        // Table for teacher's exams
        String[] columnNames = { "Exam ID", "Exam Title", "Subject", "Duration", "Total Marks", "Start Time", "Status",
                "Students" };
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
        createExamButton = new JButton("Create New Exam");
        createExamButton.setBackground(new Color(34, 139, 34));
        createExamButton.setForeground(Color.WHITE);
        createExamButton.setFont(new Font("Arial", Font.BOLD, 14));

        editExamButton = new JButton("Edit Selected Exam");
        editExamButton.setBackground(new Color(255, 165, 0));
        editExamButton.setForeground(Color.WHITE);
        editExamButton.setFont(new Font("Arial", Font.BOLD, 14));

        viewResultsButton = new JButton("View Results");
        viewResultsButton.setBackground(new Color(70, 130, 180));
        viewResultsButton.setForeground(Color.WHITE);
        viewResultsButton.setFont(new Font("Arial", Font.BOLD, 14));

        refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(128, 128, 128));
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
                "My Exams",
                0, 0,
                new Font("Arial", Font.BOLD, 16),
                new Color(70, 130, 180)));

        JScrollPane scrollPane = new JScrollPane(examTable);
        scrollPane.setPreferredSize(new Dimension(900, 400));
        examSection.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(createExamButton);
        buttonPanel.add(editExamButton);
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

        // Get teacher's exam count
        List<Exam> teacherExams = examDAO.getExamsByCreator(currentUser.getUserId());
        int totalExams = teacherExams.size();
        int activeExams = (int) teacherExams.stream().filter(Exam::isActive).count();

        // Create stat cards
        JPanel totalExamsCard = createStatCard("Total Exams", String.valueOf(totalExams), new Color(70, 130, 180));
        JPanel activeExamsCard = createStatCard("Active Exams", String.valueOf(activeExams), new Color(34, 139, 34));
        JPanel totalStudentsCard = createStatCard("Total Students",
                String.valueOf(userDAO.getUserCountByType(User.UserType.STUDENT)), new Color(255, 165, 0));
        JPanel systemExamsCard = createStatCard("System Exams", String.valueOf(examDAO.getTotalExamsCount()),
                new Color(128, 128, 128));

        statsPanel.add(totalExamsCard);
        statsPanel.add(activeExamsCard);
        statsPanel.add(totalStudentsCard);
        statsPanel.add(systemExamsCard);

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
        createExamButton.addActionListener(e -> createNewExam());

        editExamButton.addActionListener(e -> editSelectedExam());

        viewResultsButton.addActionListener(e -> viewExamResults());

        refreshButton.addActionListener(e -> loadTeacherExams());

        logoutButton.addActionListener(e -> logout());

        // Double-click to edit exam
        examTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editSelectedExam();
                }
            }
        });
    }

    private void loadTeacherExams() {
        tableModel.setRowCount(0);

        try {
            List<Exam> exams = examDAO.getExamsByCreator(currentUser.getUserId());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (Exam exam : exams) {
                String status = exam.isActive() ? "Active" : "Inactive";

                Object[] row = {
                        exam.getExamId(),
                        exam.getExamTitle(),
                        exam.getSubjectName(),
                        exam.getDurationMinutes() + " min",
                        exam.getTotalMarks(),
                        exam.getStartTime().format(formatter),
                        status,
                        "0" // Placeholder for student count
                };
                tableModel.addRow(row);
            }

            // Enable/disable buttons based on selection
            editExamButton.setEnabled(examTable.getRowCount() > 0);
            viewResultsButton.setEnabled(examTable.getRowCount() > 0);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading exams: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createNewExam() {
        JOptionPane.showMessageDialog(this,
                "Exam creation feature will be implemented in the next version.\n" +
                        "This will include:\n" +
                        "- Subject selection\n" +
                        "- Question management\n" +
                        "- Scheduling options\n" +
                        "- Student assignment",
                "Feature Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void editSelectedExam() {
        int selectedRow = examTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an exam to edit!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int examId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String examTitle = (String) tableModel.getValueAt(selectedRow, 1);

        JOptionPane.showMessageDialog(this,
                "Edit exam feature will be implemented in the next version.\n" +
                        "Selected exam: " + examTitle + " (ID: " + examId + ")",
                "Feature Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewExamResults() {
        int selectedRow = examTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an exam to view results!",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int examId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String examTitle = (String) tableModel.getValueAt(selectedRow, 1);

        JOptionPane.showMessageDialog(this,
                "Results viewing feature will be implemented in the next version.\n" +
                        "Selected exam: " + examTitle + " (ID: " + examId + ")",
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