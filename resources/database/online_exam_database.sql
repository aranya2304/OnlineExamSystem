-- Online Examination System Database Schema

-- Create Database
CREATE DATABASE IF NOT EXISTS online_exam_system;
USE online_exam_system;

-- Users Table (Admin, Teachers, Students)
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    user_type ENUM('admin', 'teacher', 'student') NOT NULL,
    phone VARCHAR(15),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- Subjects Table
CREATE TABLE subjects (
    subject_id INT PRIMARY KEY AUTO_INCREMENT,
    subject_name VARCHAR(100) NOT NULL,
    subject_code VARCHAR(20) UNIQUE NOT NULL,
    description TEXT,
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- Exams Table
CREATE TABLE exams (
    exam_id INT PRIMARY KEY AUTO_INCREMENT,
    exam_title VARCHAR(200) NOT NULL,
    exam_description TEXT,
    subject_id INT NOT NULL,
    created_by INT NOT NULL,
    total_marks INT NOT NULL DEFAULT 0,
    duration_minutes INT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    instructions TEXT,
    passing_marks INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- Questions Table
CREATE TABLE questions (
    question_id INT PRIMARY KEY AUTO_INCREMENT,
    exam_id INT NOT NULL,
    question_text TEXT NOT NULL,
    question_type ENUM('mcq', 'true_false', 'short_answer') DEFAULT 'mcq',
    marks INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE
);

-- MCQ Options Table
CREATE TABLE mcq_options (
    option_id INT PRIMARY KEY AUTO_INCREMENT,
    question_id INT NOT NULL,
    option_text TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    option_order INT NOT NULL,
    FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE
);

-- Student Exam Registrations
CREATE TABLE exam_registrations (
    registration_id INT PRIMARY KEY AUTO_INCREMENT,
    exam_id INT NOT NULL,
    student_id INT NOT NULL,
    registration_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_completed BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id),
    FOREIGN KEY (student_id) REFERENCES users(user_id),
    UNIQUE KEY unique_registration (exam_id, student_id)
);

-- Student Answers
CREATE TABLE student_answers (
    answer_id INT PRIMARY KEY AUTO_INCREMENT,
    registration_id INT NOT NULL,
    question_id INT NOT NULL,
    selected_option_id INT,
    answer_text TEXT,
    marks_obtained INT DEFAULT 0,
    answered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (registration_id) REFERENCES exam_registrations(registration_id),
    FOREIGN KEY (question_id) REFERENCES questions(question_id),
    FOREIGN KEY (selected_option_id) REFERENCES mcq_options(option_id),
    UNIQUE KEY unique_answer (registration_id, question_id)
);

-- Exam Results
CREATE TABLE exam_results (
    result_id INT PRIMARY KEY AUTO_INCREMENT,
    registration_id INT NOT NULL,
    total_marks INT NOT NULL,
    obtained_marks INT NOT NULL,
    percentage DECIMAL(5,2) NOT NULL,
    result_status ENUM('pass', 'fail') NOT NULL,
    time_taken_minutes INT,
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (registration_id) REFERENCES exam_registrations(registration_id),
    UNIQUE KEY unique_result (registration_id)
);

-- Insert Sample Data
INSERT INTO users (username, password, email, full_name, user_type, phone, address) VALUES
('admin', 'admin123', 'admin@exam.com', 'System Administrator', 'admin', '9876543210', 'Admin Office'),
('teacher1', 'teacher123', 'teacher1@exam.com', 'Dr. John Smith', 'teacher', '9876543211', '123 College Street'),
('teacher2', 'teacher456', 'teacher2@exam.com', 'Prof. Sarah Johnson', 'teacher', '9876543212', '456 University Ave'),
('student1', 'student123', 'student1@exam.com', 'Alice Brown', 'student', '9876543213', '789 Student Dorm'),
('student2', 'student456', 'student2@exam.com', 'Bob Wilson', 'student', '9876543214', '321 Campus Road'),
('student3', 'student789', 'student3@exam.com', 'Charlie Davis', 'student', '9876543215', '654 College Park');

INSERT INTO subjects (subject_name, subject_code, description, created_by) VALUES
('Java Programming', 'CS101', 'Object-oriented programming with Java', 2),
('Database Management Systems', 'CS201', 'Database design and SQL', 2),
('Data Structures', 'CS102', 'Fundamental data structures and algorithms', 3),
('Web Development', 'CS301', 'HTML, CSS, JavaScript and frameworks', 3);

INSERT INTO exams (exam_title, exam_description, subject_id, created_by, total_marks, duration_minutes, start_time, end_time, instructions, passing_marks) VALUES
('Java Basics Test', 'Test covering basic Java concepts', 1, 2, 50, 60, '2024-12-01 10:00:00', '2024-12-01 18:00:00', 'Answer all questions. No negative marking.', 25),
('SQL Fundamentals', 'Database queries and design principles', 2, 2, 40, 45, '2024-12-02 14:00:00', '2024-12-02 20:00:00', 'Choose the best answer for each question.', 20),
('Data Structures Quiz', 'Arrays, linked lists, stacks, and queues', 3, 3, 30, 30, '2024-12-03 09:00:00', '2024-12-03 17:00:00', 'Time limit is strictly enforced.', 15);

INSERT INTO questions (exam_id, question_text, question_type, marks) VALUES
-- Java Basics Test Questions
(1, 'What is the main method signature in Java?', 'mcq', 5),
(1, 'Which keyword is used to create a class in Java?', 'mcq', 5),
(1, 'Java is platform independent. True or False?', 'true_false', 5),
(1, 'What is encapsulation in OOP?', 'short_answer', 10),
(1, 'Which access modifier provides the most restrictive access?', 'mcq', 5),

-- SQL Fundamentals Questions
(2, 'Which SQL statement is used to extract data from a database?', 'mcq', 4),
(2, 'What does ACID stand for in database terminology?', 'short_answer', 8),
(2, 'A primary key can contain NULL values. True or False?', 'true_false', 4),
(2, 'Which clause is used to filter records in SQL?', 'mcq', 4),

-- Data Structures Quiz Questions
(3, 'What is the time complexity of accessing an element in an array?', 'mcq', 6),
(3, 'Stack follows LIFO principle. True or False?', 'true_false', 6),
(3, 'Which data structure is used in BFS traversal?', 'mcq', 6),
(3, 'What is the maximum number of children a binary tree node can have?', 'mcq', 6);

-- MCQ Options for Java Questions
INSERT INTO mcq_options (question_id, option_text, is_correct, option_order) VALUES
-- Question 1: Main method signature
(1, 'public static void main(String[] args)', TRUE, 1),
(1, 'public void main(String[] args)', FALSE, 2),
(1, 'static void main(String[] args)', FALSE, 3),
(1, 'public static main(String[] args)', FALSE, 4),

-- Question 2: Class keyword
(2, 'class', TRUE, 1),
(2, 'Class', FALSE, 2),
(2, 'new', FALSE, 3),
(2, 'object', FALSE, 4),

-- Question 5: Most restrictive access modifier
(5, 'private', TRUE, 1),
(5, 'protected', FALSE, 2),
(5, 'public', FALSE, 3),
(5, 'default', FALSE, 4),

-- SQL Questions Options
-- Question 6: SQL extract statement
(6, 'SELECT', TRUE, 1),
(6, 'EXTRACT', FALSE, 2),
(6, 'GET', FALSE, 3),
(6, 'PULL', FALSE, 4),

-- Question 9: SQL filter clause
(9, 'WHERE', TRUE, 1),
(9, 'FILTER', FALSE, 2),
(9, 'HAVING', FALSE, 3),
(9, 'SELECT', FALSE, 4),

-- Data Structures Questions Options
-- Question 10: Array access time complexity
(10, 'O(1)', TRUE, 1),
(10, 'O(n)', FALSE, 2),
(10, 'O(log n)', FALSE, 3),
(10, 'O(n²)', FALSE, 4),

-- Question 12: BFS data structure
(12, 'Queue', TRUE, 1),
(12, 'Stack', FALSE, 2),
(12, 'Array', FALSE, 3),
(12, 'Tree', FALSE, 4),

-- Question 13: Binary tree children
(13, '2', TRUE, 1),
(13, '1', FALSE, 2),
(13, '3', FALSE, 3),
(13, 'Unlimited', FALSE, 4);

-- Sample Registrations
INSERT INTO exam_registrations (exam_id, student_id) VALUES
(1, 4), (1, 5), (1, 6),
(2, 4), (2, 5),
(3, 5), (3, 6);
