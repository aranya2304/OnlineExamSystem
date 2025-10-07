package com.examSystem.dao;

import com.examSystem.model.Exam;
import com.examSystem.model.Question;
import com.examSystem.model.MCQOption;
import com.examSystem.util.DatabaseUtil;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Exam operations
 */
public class ExamDAO {

    /**
     * Get all active exams with subject and creator details
     */
    public List<Exam> getAllActiveExams() {
        List<Exam> exams = new ArrayList<>();
        String query = "SELECT e.*, s.subject_name, u.full_name as creator_name " +
                      "FROM exams e " +
                      "JOIN subjects s ON e.subject_id = s.subject_id " +
                      "JOIN users u ON e.created_by = u.user_id " +
                      "WHERE e.is_active = TRUE ORDER BY e.start_time";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                exams.add(mapResultSetToExam(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all exams: " + e.getMessage());
        }
        return exams;
    }

    /**
     * Get exam by ID with details
     */
    public Exam getExamById(int examId) {
        String query = "SELECT e.*, s.subject_name, u.full_name as creator_name " +
                      "FROM exams e " +
                      "JOIN subjects s ON e.subject_id = s.subject_id " +
                      "JOIN users u ON e.created_by = u.user_id " +
                      "WHERE e.exam_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, examId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToExam(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting exam by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Create new exam
     */
    public boolean createExam(Exam exam) {
        String query = "INSERT INTO exams (exam_title, exam_description, subject_id, created_by, " +
                      "total_marks, duration_minutes, start_time, end_time, instructions, passing_marks) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, exam.getExamTitle());
            pstmt.setString(2, exam.getExamDescription());
            pstmt.setInt(3, exam.getSubjectId());
            pstmt.setInt(4, exam.getCreatedBy());
            pstmt.setInt(5, exam.getTotalMarks());
            pstmt.setInt(6, exam.getDurationMinutes());
            pstmt.setTimestamp(7, Timestamp.valueOf(exam.getStartTime()));
            pstmt.setTimestamp(8, Timestamp.valueOf(exam.getEndTime()));
            pstmt.setString(9, exam.getInstructions());
            pstmt.setInt(10, exam.getPassingMarks());

            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error creating exam: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get questions for an exam with options
     */
    public List<Question> getExamQuestions(int examId) {
        List<Question> questions = new ArrayList<>();
        String query = "SELECT * FROM questions WHERE exam_id = ? ORDER BY question_id";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, examId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Question question = mapResultSetToQuestion(rs);

                // Load options for MCQ questions
                if (question.getQuestionType() == Question.QuestionType.MCQ) {
                    question.setOptions(getQuestionOptions(question.getQuestionId()));
                }

                questions.add(question);
            }
        } catch (SQLException e) {
            System.err.println("Error getting exam questions: " + e.getMessage());
        }
        return questions;
    }

    /**
     * Get options for a specific question
     */
    public List<MCQOption> getQuestionOptions(int questionId) {
        List<MCQOption> options = new ArrayList<>();
        String query = "SELECT * FROM mcq_options WHERE question_id = ? ORDER BY option_order";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                options.add(mapResultSetToMCQOption(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting question options: " + e.getMessage());
        }
        return options;
    }

    /**
     * Check if exam is available for taking (within time limits)
     */
    public boolean isExamAvailable(int examId) {
        String query = "SELECT start_time, end_time, is_active FROM exams WHERE exam_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, examId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime endTime = rs.getTimestamp("end_time").toLocalDateTime();
                boolean isActive = rs.getBoolean("is_active");

                return isActive && now.isAfter(startTime) && now.isBefore(endTime);
            }
        } catch (SQLException e) {
            System.err.println("Error checking exam availability: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get total exams count
     */
    public int getTotalExamsCount() {
        String query = "SELECT COUNT(*) FROM exams WHERE is_active = TRUE";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total exams count: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Get exams created by a specific user (creator)
     */
    public List<Exam> getExamsByCreator(int creatorId) {
        List<Exam> exams = new ArrayList<>();
        String query = "SELECT e.*, s.subject_name, u.full_name as creator_name " +
                      "FROM exams e " +
                      "JOIN subjects s ON e.subject_id = s.subject_id " +
                      "JOIN users u ON e.created_by = u.user_id " +
                      "WHERE e.created_by = ? ORDER BY e.start_time";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, creatorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                exams.add(mapResultSetToExam(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting exams by creator: " + e.getMessage());
        }
        return exams;
    }

    /**
     * Map ResultSet to Exam object
     */
    private Exam mapResultSetToExam(ResultSet rs) throws SQLException {
        Exam exam = new Exam();
        exam.setExamId(rs.getInt("exam_id"));
        exam.setExamTitle(rs.getString("exam_title"));
        exam.setExamDescription(rs.getString("exam_description"));
        exam.setSubjectId(rs.getInt("subject_id"));
        exam.setSubjectName(rs.getString("subject_name"));
        exam.setCreatedBy(rs.getInt("created_by"));
        exam.setCreatedByName(rs.getString("creator_name"));
        exam.setTotalMarks(rs.getInt("total_marks"));
        exam.setDurationMinutes(rs.getInt("duration_minutes"));
        exam.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
        exam.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
        exam.setInstructions(rs.getString("instructions"));
        exam.setPassingMarks(rs.getInt("passing_marks"));
        exam.setActive(rs.getBoolean("is_active"));
        exam.setCreatedAt(rs.getTimestamp("created_at"));
        return exam;
    }

    /**
     * Map ResultSet to Question object
     */
    private Question mapResultSetToQuestion(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setQuestionId(rs.getInt("question_id"));
        question.setExamId(rs.getInt("exam_id"));
        question.setQuestionText(rs.getString("question_text"));
        question.setQuestionType(Question.QuestionType.fromString(rs.getString("question_type")));
        question.setMarks(rs.getInt("marks"));
        question.setCreatedAt(rs.getTimestamp("created_at"));
        return question;
    }

    /**
     * Map ResultSet to MCQOption object
     */
    private MCQOption mapResultSetToMCQOption(ResultSet rs) throws SQLException {
        MCQOption option = new MCQOption();
        option.setOptionId(rs.getInt("option_id"));
        option.setQuestionId(rs.getInt("question_id"));
        option.setOptionText(rs.getString("option_text"));
        option.setCorrect(rs.getBoolean("is_correct"));
        option.setOptionOrder(rs.getInt("option_order"));
        return option;
    }
}