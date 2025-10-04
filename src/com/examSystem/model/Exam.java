package com.examSystem.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Exam model class representing exams in the system
 */
public class Exam {
    private int examId;
    private String examTitle;
    private String examDescription;
    private int subjectId;
    private String subjectName; // For display purposes
    private int createdBy;
    private String createdByName; // For display purposes
    private int totalMarks;
    private int durationMinutes;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String instructions;
    private int passingMarks;
    private boolean isActive;
    private Timestamp createdAt;
    
    // Constructors
    public Exam() {}
    
    public Exam(String examTitle, String examDescription, int subjectId, int createdBy,
                int totalMarks, int durationMinutes, LocalDateTime startTime, LocalDateTime endTime,
                String instructions, int passingMarks) {
        this.examTitle = examTitle;
        this.examDescription = examDescription;
        this.subjectId = subjectId;
        this.createdBy = createdBy;
        this.totalMarks = totalMarks;
        this.durationMinutes = durationMinutes;
        this.startTime = startTime;
        this.endTime = endTime;
        this.instructions = instructions;
        this.passingMarks = passingMarks;
        this.isActive = true;
    }
    
    // Getters and Setters
    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }
    
    public String getExamTitle() { return examTitle; }
    public void setExamTitle(String examTitle) { this.examTitle = examTitle; }
    
    public String getExamDescription() { return examDescription; }
    public void setExamDescription(String examDescription) { this.examDescription = examDescription; }
    
    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }
    
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    
    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
    
    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
    
    public int getTotalMarks() { return totalMarks; }
    public void setTotalMarks(int totalMarks) { this.totalMarks = totalMarks; }
    
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    
    public int getPassingMarks() { return passingMarks; }
    public void setPassingMarks(int passingMarks) { this.passingMarks = passingMarks; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public Timestamp getCreatedAt() { return createdAt == null ? null : (Timestamp) createdAt.clone(); }

    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt == null ? null : (Timestamp) createdAt.clone(); }
    
    @Override
    public String toString() {
        return "Exam{" +
                "examId=" + examId +
                ", examTitle='" + examTitle + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", totalMarks=" + totalMarks +
                ", durationMinutes=" + durationMinutes +
                ", isActive=" + isActive +
                '}';
    }
}
