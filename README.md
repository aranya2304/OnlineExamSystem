# Online Examination System

A Java Swing-based desktop application for managing online examinations securely and efficiently. This project allows administrators to create exams and questions, and students to participate in timed online tests with automatic result evaluation.

---

## Features

- **User Login:** Secure authentication for students and admins.
- **Role-Based Access:** Different interfaces and actions for admins and students.
- **Exam and Question Management:** Admins can add, update, and delete exams and questions.
- **Timed Online Exams:** Students can take exams within a specified time limit.
- **Automatic Grading:** Instant result calculation upon exam submission.
- **Simple GUI:** Java Swing user interface for smooth user experience.
- **Persistence:** Data handling through database integration (MySQL recommended).

---

## Getting Started

### Prerequisites

- Java Development Kit (JDK 8 or later)
- MySQL (if using database functionality)
- Git (for source code management)

### Clone the Repository
```bash
git clone https://github.com/aranya2304/OnlineExamSystem.git
cd OnlineExamSystem
```

### Compilation & Running

#### Using Command Line

1. **Compile:**
    ```
    javac -d bin -cp ".;lib/*" src\com\examSystem\gui\*.java
    ```

2. **Run:**
    ```
    java -cp "bin;lib/*" com.examSystem.gui.LoginFrame
    ```

#### Using Provided Batch File

A `run.bat` file is included for Windows. Double-click `run.bat` to launch the application.

---

## Directory Structure
```
.
├───.github
├───.vscode
├───lib
├───out
│   └───com
│       └───examSystem
│           ├───dao
│           ├───gui
│           ├───model
│           └───util
├───resources
│   └───database
└───src
    ├───com
    │   └───examSystem
    │       ├───dao
    │       ├───gui
    │       ├───model
    │       └───util
    └───out
```

---

## Screenshots

<img width="395" height="303" alt="image" src="https://github.com/user-attachments/assets/5216a9c6-1327-41e8-a675-5bfdb093e72e" />

---

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

---

## License

This project is open-source and available under the [MIT License](LICENSE).

---

## Acknowledgements

- Java Swing for GUI
- MySQL for backend data storage

---

For questions, contact [repo owner](https://github.com/aranya2304/OnlineExamSystem).

