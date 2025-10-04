package com.examSystem.model;

/**
 * MCQ Option model class representing options for multiple choice questions
 */
public class MCQOption {
    private int optionId;
    private int questionId;
    private String optionText;
    private boolean isCorrect;
    private int optionOrder;
    
    // Constructors
    public MCQOption() {}
    
    public MCQOption(int questionId, String optionText, boolean isCorrect, int optionOrder) {
        this.questionId = questionId;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
        this.optionOrder = optionOrder;
    }
    
    // Getters and Setters
    public int getOptionId() { return optionId; }
    public void setOptionId(int optionId) { this.optionId = optionId; }
    
    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }
    
    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }
    
    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }
    
    public int getOptionOrder() { return optionOrder; }
    public void setOptionOrder(int optionOrder) { this.optionOrder = optionOrder; }
    
    @Override
    public String toString() {
        return "MCQOption{" +
                "optionId=" + optionId +
                ", optionText='" + optionText + '\'' +
                ", isCorrect=" + isCorrect +
                ", optionOrder=" + optionOrder +
                '}';
    }
}
