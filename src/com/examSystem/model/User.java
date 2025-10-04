package com.examSystem.model;

import java.sql.Timestamp;

/**
 * User model class representing users in the system
 */
public class User {
    private int userId;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private UserType userType;
    private String phone;
    private String address;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean isActive;

    // Enum for user types
    public enum UserType {
        ADMIN("admin"),
        TEACHER("teacher"), 
        STUDENT("student");

        private final String value;

        UserType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static UserType fromString(String text) {
            for (UserType type : UserType.values()) {
                if (type.value.equalsIgnoreCase(text)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("No enum constant for: " + text);
        }
    }

    // Constructors
    public User() {}

    public User(String username, String password, String email, String fullName, UserType userType) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.userType = userType;
        this.isActive = true;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Timestamp getCreatedAt() {
        return createdAt == null ? null : (Timestamp) createdAt.clone();
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt == null ? null : (Timestamp) createdAt.clone();
    }

    public Timestamp getUpdatedAt() {
        return updatedAt == null ? null : (Timestamp) updatedAt.clone();
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt == null ? null : (Timestamp) updatedAt.clone();
    }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", userType=" + userType +
                ", email='" + email + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
