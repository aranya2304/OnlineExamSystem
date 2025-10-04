package com.examSystem.model;

import java.sql.Timestamp;
import java.util.List;

/**
 * Question model class representing questions in exams
 */
public class Question {
    private int questionId;
    private int examId;
    private String questionText;
    private QuestionType questionType;
    private int marks;
    private Timestamp createdAt;
    private List<MCQOption> options; // For MCQ questions
    
    // Enum for question types
    public enum QuestionType {
        MCQ("mcq"),
        TRUE_FALSE("true_false"),
        SHORT_ANSWER("short_answer");
        
        private final String value;
        
        QuestionType(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static QuestionType fromString(String text) {
            for (QuestionType type : QuestionType.values()) {
                if (type.value.equalsIgnoreCase(text)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("No enum constant for: " + text);
        }
    }
    
    // Constructors
    public Question() {}
    
    public Question(int examId, String questionText, QuestionType questionType, int marks) {
        this.examId = examId;
        this.questionText = questionText;
        this.questionType = questionType;
        this.marks = marks;
    }
    
    // Getters and Setters
    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }
    
    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }
    
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    
    public QuestionType getQuestionType() { return questionType; }
    public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }
    
    public int getMarks() { return marks; }
    public void setMarks(int marks) { this.marks = marks; }
    
    public Timestamp getCreatedAt() { return createdAt == null ? null : (Timestamp) createdAt.clone(); }

    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt == null ? null : (Timestamp) createdAt.clone(); }
    
    public List<MCQOption> getOptions() {
        return options == null ? null : List.copyOf(options);
    }

    public void setOptions(List<MCQOption> options) {
        this.options = options == null ? null : List.copyOf(options);
    }
    
    @Override
    public String toString() {
        return "Question{" +
                "questionId=" + questionId +
                ", examId=" + examId +
                ", questionType=" + questionType +
                ", marks=" + marks +
                ", questionText='" + (questionText == null ? "" : questionText.substring(0, Math.min(50, questionText.length()))) + "...' }";
    }
}
