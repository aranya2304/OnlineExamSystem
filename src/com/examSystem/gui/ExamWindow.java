package com.examSystem.gui;

import com.examSystem.dao.ExamDAO;
import com.examSystem.model.User;
import com.examSystem.model.Exam;
import com.examSystem.model.Question;
import com.examSystem.model.MCQOption;
import com.examSystem.util.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Exam taking window for students
 */
public class ExamWindow extends javax.swing.JFrame {
    private User currentUser;
    private int examId;
    private Exam exam;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private Map<Integer, Object> answers; // questionId -> answer

    private ExamDAO examDAO;
    private Timer examTimer;
    private int timeRemaining; // in seconds

    // UI Components
    private JLabel timerLabel;
    private JLabel questionCountLabel;
    private JLabel questionLabel;
    private JPanel answerPanel;
    private JButton previousButton;
    private JButton nextButton;
    private JButton submitButton;
    private ButtonGroup answerGroup;

    public ExamWindow(User user, int examId) {
        this.currentUser = user;
        this.examId = examId;
        this.examDAO = new ExamDAO();
        this.answers = new HashMap<>();

        UIUtils.applyGlobalForegrounds(java.awt.Color.BLACK);

        // load exam data first so UI can use exam metadata (title/duration)
        loadExamData();

        // create components and layout
        initializeComponents();
        setupLayout();
        setupEventHandlers();

        // ensure UI defaults and fix any white-on-white text AFTER components exist
        UIUtils.ensureBlackButtons(this);

        // start timer and show first question
        startTimer();
        loadQuestion(0);

        setTitle("Online Exam - " + exam.getExamTitle());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    private void loadExamData() {
        exam = examDAO.getExamById(examId);
        questions = examDAO.getExamQuestions(examId);
        timeRemaining = exam.getDurationMinutes() * 60; // Convert to seconds
    }

    private void initializeComponents() {
        // Timer display
        timerLabel = new JLabel();
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        timerLabel.setForeground(Color.RED);
        timerLabel.setHorizontalAlignment(JLabel.CENTER);

        // Question counter
        questionCountLabel = new JLabel();
        questionCountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        questionCountLabel.setHorizontalAlignment(JLabel.CENTER);

        // Question display
        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        questionLabel.setVerticalAlignment(JLabel.TOP);

        // Answer panel
        answerPanel = new JPanel();
        answerPanel.setLayout(new BoxLayout(answerPanel, BoxLayout.Y_AXIS));

        // Navigation buttons
        previousButton = new JButton("Previous");
        previousButton.setEnabled(false);
        nextButton = new JButton("Next");
        submitButton = new JButton("Submit Exam");
        submitButton.setBackground(new Color(220, 20, 60));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Arial", Font.BOLD, 14));

        updateTimerDisplay();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header panel with timer and question count
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 25, 112));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftHeader.setBackground(new Color(25, 25, 112));
        JLabel examTitle = new JLabel(exam.getExamTitle());
        examTitle.setForeground(Color.WHITE);
        examTitle.setFont(new Font("Arial", Font.BOLD, 18));
        leftHeader.add(examTitle);

        JPanel centerHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerHeader.setBackground(new Color(25, 25, 112));
        questionCountLabel.setForeground(Color.WHITE);
        centerHeader.add(questionCountLabel);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightHeader.setBackground(new Color(25, 25, 112));
        rightHeader.add(timerLabel);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(centerHeader, BorderLayout.CENTER);
        headerPanel.add(rightHeader, BorderLayout.EAST);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Question panel
        JPanel questionPanel = new JPanel(new BorderLayout());
        questionPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        questionPanel.add(questionLabel, BorderLayout.CENTER);

        // Answer panel with scroll
        JScrollPane answerScrollPane = new JScrollPane(answerPanel);
        answerScrollPane.setPreferredSize(new Dimension(800, 300));
        answerScrollPane.setBorder(BorderFactory.createTitledBorder("Choose your answer:"));

        contentPanel.add(questionPanel, BorderLayout.NORTH);
        contentPanel.add(answerScrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(previousButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(Box.createHorizontalStrut(50));
        buttonPanel.add(submitButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        previousButton.addActionListener(e -> {
            saveCurrentAnswer();
            loadQuestion(currentQuestionIndex - 1);
        });

        nextButton.addActionListener(e -> {
            saveCurrentAnswer();
            loadQuestion(currentQuestionIndex + 1);
        });

        submitButton.addActionListener(e -> submitExam());

        // Prevent window closing without confirmation
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                confirmExitExam();
            }
        });
    }

    private void startTimer() {
        examTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                updateTimerDisplay();

                if (timeRemaining <= 0) {
                    examTimer.stop();
                    JOptionPane.showMessageDialog(ExamWindow.this,
                            "Time is up! Your exam will be submitted automatically.",
                            "Time Up",
                            JOptionPane.WARNING_MESSAGE);
                    submitExam();
                }
            }
        });
        examTimer.start();
    }

    private void updateTimerDisplay() {
        int hours = timeRemaining / 3600;
        int minutes = (timeRemaining % 3600) / 60;
        int seconds = timeRemaining % 60;

        String timeText = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timerLabel.setText("Time Remaining: " + timeText);

        // Change color when time is running low
        if (timeRemaining <= 300) { // 5 minutes
            timerLabel.setForeground(Color.RED);
        } else if (timeRemaining <= 600) { // 10 minutes
            timerLabel.setForeground(Color.ORANGE);
        }
    }

    private void loadQuestion(int index) {
        if (index < 0 || index >= questions.size()) {
            return;
        }

        currentQuestionIndex = index;
        Question question = questions.get(index);

        // Update question counter
        questionCountLabel.setText("Question " + (index + 1) + " of " + questions.size());

        // Update question text
        questionLabel.setText("<html><body style='width: 800px'>" +
                "Q" + (index + 1) + ". " + question.getQuestionText() +
                "<br><br><b>Marks: " + question.getMarks() + "</b></body></html>");

        // Clear previous answers
        answerPanel.removeAll();
        answerGroup = new ButtonGroup();

        // Create answer options based on question type
        createAnswerOptions(question);

        // Update button states
        previousButton.setEnabled(index > 0);
        nextButton.setEnabled(index < questions.size() - 1);

        // Load saved answer if exists
        loadSavedAnswer(question.getQuestionId());

        answerPanel.revalidate();
        answerPanel.repaint();
    }

    private void createAnswerOptions(Question question) {
        switch (question.getQuestionType()) {
            case MCQ:
                for (MCQOption option : question.getOptions()) {
                    JRadioButton radioButton = new JRadioButton(option.getOptionText());
                    radioButton.setFont(new Font("Arial", Font.PLAIN, 14));
                    radioButton.setName(String.valueOf(option.getOptionId()));
                    answerGroup.add(radioButton);
                    answerPanel.add(radioButton);
                    answerPanel.add(Box.createVerticalStrut(5));
                }
                break;

            case TRUE_FALSE:
                JRadioButton trueOption = new JRadioButton("True");
                JRadioButton falseOption = new JRadioButton("False");
                trueOption.setFont(new Font("Arial", Font.PLAIN, 14));
                falseOption.setFont(new Font("Arial", Font.PLAIN, 14));
                trueOption.setName("true");
                falseOption.setName("false");
                answerGroup.add(trueOption);
                answerGroup.add(falseOption);
                answerPanel.add(trueOption);
                answerPanel.add(falseOption);
                break;

            case SHORT_ANSWER:
                JTextArea textArea = new JTextArea(5, 50);
                textArea.setFont(new Font("Arial", Font.PLAIN, 14));
                textArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                JScrollPane scrollPane = new JScrollPane(textArea);
                answerPanel.add(scrollPane);
                break;
        }
    }

    private void saveCurrentAnswer() {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.size()) {
            Question question = questions.get(currentQuestionIndex);
            Object answer = null;

            switch (question.getQuestionType()) {
                case MCQ:
                case TRUE_FALSE:
                    for (AbstractButton button : java.util.Collections.list(answerGroup.getElements())) {
                        if (button.isSelected()) {
                            answer = button.getName();
                            break;
                        }
                    }
                    break;

                case SHORT_ANSWER:
                    Component[] components = answerPanel.getComponents();
                    for (Component comp : components) {
                        if (comp instanceof JScrollPane) {
                            JScrollPane scrollPane = (JScrollPane) comp;
                            JTextArea textArea = (JTextArea) scrollPane.getViewport().getView();
                            answer = textArea.getText().trim();
                            break;
                        }
                    }
                    break;
            }

            if (answer != null && !answer.toString().isEmpty()) {
                answers.put(question.getQuestionId(), answer);
            }
        }
    }

    private void loadSavedAnswer(int questionId) {
        Object savedAnswer = answers.get(questionId);
        if (savedAnswer == null)
            return;

        Question question = questions.get(currentQuestionIndex);

        switch (question.getQuestionType()) {
            case MCQ:
            case TRUE_FALSE:
                String answerName = savedAnswer.toString();
                for (AbstractButton button : java.util.Collections.list(answerGroup.getElements())) {
                    if (answerName.equals(button.getName())) {
                        button.setSelected(true);
                        break;
                    }
                }
                break;

            case SHORT_ANSWER:
                Component[] components = answerPanel.getComponents();
                for (Component comp : components) {
                    if (comp instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) comp;
                        JTextArea textArea = (JTextArea) scrollPane.getViewport().getView();
                        textArea.setText(savedAnswer.toString());
                        break;
                    }
                }
                break;
        }
    }

    private void submitExam() {
        saveCurrentAnswer(); // Save current question answer

        int answeredCount = answers.size();
        int totalQuestions = questions.size();

        String message = "You have answered " + answeredCount + " out of " + totalQuestions + " questions.\n" +
                "Are you sure you want to submit your exam?\n" +
                "This action cannot be undone.";

        int confirm = JOptionPane.showConfirmDialog(this,
                message,
                "Confirm Submission",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (examTimer != null) {
                examTimer.stop();
            }

            // Calculate and show results
            calculateAndShowResults();

            // Return to student dashboard
            dispose();
            SwingUtilities.invokeLater(() -> new StudentDashboard(currentUser).setVisible(true));
        }
    }

    private void calculateAndShowResults() {
        int totalMarks = 0;
        int obtainedMarks = 0;

        for (Question question : questions) {
            totalMarks += question.getMarks();

            Object userAnswer = answers.get(question.getQuestionId());
            if (userAnswer != null) {
                // For this prototype, we'll do basic scoring for MCQ and True/False
                if (question.getQuestionType() == Question.QuestionType.MCQ) {
                    for (MCQOption option : question.getOptions()) {
                        if (option.isCorrect() && userAnswer.equals(String.valueOf(option.getOptionId()))) {
                            obtainedMarks += question.getMarks();
                            break;
                        }
                    }
                }
                // Note: True/False and Short Answer scoring would need additional logic
            }
        }

        double percentage = totalMarks > 0 ? (double) obtainedMarks / totalMarks * 100 : 0;
        String result = percentage >= exam.getPassingMarks() ? "PASS" : "FAIL";

        String resultMessage = "Exam Completed!\n\n" +
                "Total Questions: " + questions.size() + "\n" +
                "Questions Answered: " + answers.size() + "\n" +
                "Total Marks: " + totalMarks + "\n" +
                "Obtained Marks: " + obtainedMarks + "\n" +
                "Percentage: " + String.format("%.2f", percentage) + "%\n" +
                "Result: " + result;

        JOptionPane.showMessageDialog(this,
                resultMessage,
                "Exam Results",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void confirmExitExam() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit the exam?\n" +
                        "Your progress will be lost if you haven't submitted.",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (examTimer != null) {
                examTimer.stop();
            }
            dispose();
            SwingUtilities.invokeLater(() -> new StudentDashboard(currentUser).setVisible(true));
        }
    }
}