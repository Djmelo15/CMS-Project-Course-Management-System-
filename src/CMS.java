import java.sql.*;
import java.util.Scanner;

public class CMS {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3308/cms";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Djxbox360@";
    private static final Scanner scanner = new Scanner(System.in);
    private static int loggedInStudentId = -1;
    // Stores the logged-in student ID, -1 means no one is logged in

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- CMS Main Menu ---");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    registerStudent();  // Call register method
                    break;
                case "2":
                    login();  // Call the login method
                    break;
                case "3":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    // Method to handle user login
    private static void login() {
        System.out.println("\n--- Login ---");
        System.out.println("Choose login type:");
        System.out.println("1. Lecturer");
        System.out.println("2. Student");
        System.out.print("Enter your choice: ");
        String loginChoice = scanner.nextLine();

        switch (loginChoice) {
            case "1":
                loginLecturer(); // Lecturer login logic here
                break;
            case "2":
                loginStudent();  // Proceeds to student login
                break;
            default:
                System.out.println("Invalid choice! Please try again.");
        }
    }

    // Method to handle student login
    private static void loginStudent() {
        System.out.println("\n--- Student Login ---");

        // Prompt for email and password
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        // Check if the email and password match a student in the database
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT student_id, Fname, Lname FROM students WHERE email = ? AND password = ?")) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                loggedInStudentId = rs.getInt("student_id");
                System.out.println("Login successful! Welcome, " + rs.getString("Fname") + " " + rs.getString("Lname"));
                studentMenu();
            } else {
                System.out.println("Invalid email or password. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("Error logging in: " + e.getMessage());
        }
    }

    // Method to handle student dashboard menu
    private static void studentMenu() {
        while (true) {
            System.out.println("\n--- Student Dashboard ---");
            System.out.println("1. View All Courses");
            System.out.println("2. View Subscribed Courses");
            System.out.println("3. Subscribe to a Course");
            System.out.println("4. Unsubscribe from a Course");
            System.out.println("5. View Grades");
            System.out.println("6. View Assignments");
            System.out.println("7. Logout");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewAllCourses();  // Call method to view all courses
                    break;
                case "2":
                    viewSubscribedCourses();  // Call method to view subscribed courses
                    break;
                case "3":
                    subscribeToCourse();  // Call method to subscribe
                    break;
                case "4":
                    unsubscribeFromCourse();  // Call method to unsubscribe
                    break;
                case "5":
                    viewGrades();  // Call method to view grades
                    break;
                case "6":
                    viewAssignments();  // Call method to view assignments
                    break;
                case "7":
                    loggedInStudentId = -1;
                    System.out.println("You have logged out.");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }




    // Lecturer login and menu implementation
    private static void loginLecturer() {
        System.out.println("\n--- Lecturer Login ---");

        // Prompt for email and password
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        // Check if the email and password match a lecturer in the database
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT lecturer_id, first_name, last_name FROM lecturers WHERE email = ? AND password = ?")) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int lecturerId = rs.getInt("lecturer_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");

                System.out.println("Login successful! Welcome, " + firstName + " " + lastName);
                lecturerMenu(lecturerId);
            } else {
                System.out.println("Invalid email or password. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("Error logging in: " + e.getMessage());
        }
    }

    private static void lecturerMenu(int lecturerId) {
        while (true) {
            System.out.println("\n--- Lecturer Dashboard ---");
            System.out.println("1. View Assigned Courses");
            System.out.println("2. View Students in a Course");
            System.out.println("3. Input Grades");
            System.out.println("4. View All Student Grades");
            System.out.println("5. Assign Assignment");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewAssignedCourses(lecturerId);
                    break;
                case "2":
                    viewStudentsInCourse(lecturerId);
                    break;
                case "3":
                    inputGrades(lecturerId);
                    break;
                case "4":
                    viewAllStudentGrades(lecturerId);
                    break;
                case "5":
                    assignAssignment(lecturerId);
                    break;
                case "6":
                    System.out.println("You have logged out.");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void assignAssignment(int lecturerId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Query to fetch all courses taught by this lecturer
            String courseQuery = "SELECT course_id, course_name FROM courses WHERE lecturer_id = ?";
            try (PreparedStatement courseStmt = conn.prepareStatement(courseQuery)) {
                courseStmt.setInt(1, lecturerId);
                ResultSet courseRs = courseStmt.executeQuery();

                if (!courseRs.isBeforeFirst()) {
                    System.out.println("You have no courses assigned.");
                    return;
                }

                System.out.println("\nCourses you teach:");
                while (courseRs.next()) {
                    System.out.println("Course ID: " + courseRs.getInt("course_id") + ", Course Name: " + courseRs.getString("course_name"));
                }
            }

            // Prompt lecturer to enter the Course ID
            System.out.print("\nEnter the Course ID to assign an assignment: ");
            int courseId = Integer.parseInt(scanner.nextLine());

            // Validate that the course belongs to the lecturer
            String validateQuery = "SELECT 1 FROM courses WHERE course_id = ? AND lecturer_id = ?";
            try (PreparedStatement validateStmt = conn.prepareStatement(validateQuery)) {
                validateStmt.setInt(1, courseId);
                validateStmt.setInt(2, lecturerId);
                ResultSet validateRs = validateStmt.executeQuery();

                if (!validateRs.next()) {
                    System.out.println("Invalid Course ID or you do not teach this course.");
                    return;
                }
            }

            // Prompt for assignment details
            System.out.print("Enter Assignment Title: ");
            String assignmentTitle = scanner.nextLine();

            System.out.print("Enter Assignment Description: ");
            String assignmentDescription = scanner.nextLine();

            System.out.print("Enter Assignment File URL (optional, leave blank if none): ");
            String fileUrl = scanner.nextLine();

            System.out.print("Enter Due Date (YYYY-MM-DD): ");
            String dueDate = scanner.nextLine();

            // Insert the assignment into the assignments table
            String insertQuery = "INSERT INTO assignments (course_id, assignment_title, assignment_description, file_url, due_date) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, courseId);
                insertStmt.setString(2, assignmentTitle);
                insertStmt.setString(3, assignmentDescription);
                insertStmt.setString(4, fileUrl.isEmpty() ? null : fileUrl); // Insert null if file URL is empty
                insertStmt.setDate(5, Date.valueOf(dueDate)); // Convert string to SQL Date
                insertStmt.executeUpdate();

                System.out.println("Assignment assigned successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Error assigning assignment: " + e.getMessage());
        }
    }

    private static void viewAssignedCourses(int lecturerId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT course_id, course_name FROM courses WHERE lecturer_id = ?")) {
            stmt.setInt(1, lecturerId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nAssigned Courses:");
            while (rs.next()) {
                System.out.println("Course ID: " + rs.getInt("course_id") + ", Name: " + rs.getString("course_name"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching assigned courses: " + e.getMessage());
        }
    }
    private static void viewStudentsInCourse(int lecturerId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Query to fetch all courses taught by this lecturer
            String courseQuery = "SELECT course_id, course_name FROM courses WHERE lecturer_id = ?";
            try (PreparedStatement courseStmt = conn.prepareStatement(courseQuery)) {
                courseStmt.setInt(1, lecturerId);
                ResultSet courseRs = courseStmt.executeQuery();

                if (!courseRs.isBeforeFirst()) {
                    System.out.println("You have no courses assigned.");
                    return;
                }

                System.out.println("\nCourses you teach:");
                while (courseRs.next()) {
                    System.out.println("Course ID: " + courseRs.getInt("course_id") + ", Course Name: " + courseRs.getString("course_name"));
                }
            }

            // Prompt lecturer to enter the Course ID to view students
            System.out.print("\nEnter the Course ID to view students: ");
            int courseId = Integer.parseInt(scanner.nextLine());

            // Query to fetch students in the selected course
            String studentQuery =
                    "SELECT s.student_id, s.Fname, s.Lname " +
                            "FROM students s " +
                            "JOIN subscriptions sub ON s.student_id = sub.student_id " +
                            "WHERE sub.course_id = ? AND EXISTS (" +
                            "    SELECT 1 FROM courses c WHERE c.course_id = sub.course_id AND c.lecturer_id = ?)";
            try (PreparedStatement studentStmt = conn.prepareStatement(studentQuery)) {
                studentStmt.setInt(1, courseId);
                studentStmt.setInt(2, lecturerId);
                ResultSet studentRs = studentStmt.executeQuery();

                if (!studentRs.isBeforeFirst()) {
                    System.out.println("No students are enrolled in this course.");
                } else {
                    System.out.println("\nStudents enrolled in Course ID " + courseId + ":");
                    while (studentRs.next()) {
                        System.out.println("Student ID: " + studentRs.getInt("student_id") +
                                ", Name: " + studentRs.getString("Fname") + " " + studentRs.getString("Lname"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching students: " + e.getMessage());
        }
    }

    private static void inputGrades(int lecturerId) {
        System.out.print("Enter Course ID: ");
        int courseId = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Student ID: ");
        int studentId = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter Grade: ");
        String grade = scanner.nextLine();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO grades (course_id, student_id, grade) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE grade = ?")) {
            stmt.setInt(1, courseId);
            stmt.setInt(2, studentId);
            stmt.setString(3, grade);
            stmt.setString(4, grade);
            stmt.executeUpdate();

            System.out.println("Grade recorded successfully.");
        } catch (SQLException e) {
            System.out.println("Error recording grade: " + e.getMessage());
        }
    }

    private static void viewAllStudentGrades(int lecturerId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT s.Fname, s.Lname, g.grade, c.course_name FROM grades g " +
                             "JOIN students s ON g.student_id = s.student_id " +
                             "JOIN courses c ON g.course_id = c.course_id " +
                             "WHERE c.lecturer_id = ?")) {
            stmt.setInt(1, lecturerId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nAll Student Grades:");
            while (rs.next()) {
                System.out.println("Student: " + rs.getString("Fname") + " " + rs.getString("Lname") + ", Course: " + rs.getString("course_name") + ", Grade: " + rs.getString("grade"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching grades: " + e.getMessage());
        }
    }


    // Method to register student
    private static void registerStudent() {
        System.out.println("\n--- Student Registration ---");

        // Prompt for student details
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter date of birth (YYYY-MM-DD): ");
        String dob = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        // Insert the student data into the database
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO students (Fname, Lname, dob, email, password) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setDate(3, Date.valueOf(dob)); // Convert String to SQL Date
            stmt.setString(4, email);
            stmt.setString(5, password);
            stmt.executeUpdate();
            System.out.println("Registration successful!");

            // After successful registration, log the student in and go to student dashboard
            try (PreparedStatement loginStmt = conn.prepareStatement(
                    "SELECT student_id FROM students WHERE email = ? AND password = ?")) {
                loginStmt.setString(1, email);
                loginStmt.setString(2, password);
                ResultSet rs = loginStmt.executeQuery();
                if (rs.next()) {
                    loggedInStudentId = rs.getInt("student_id");
                    System.out.println("Welcome, " + firstName + " " + lastName + "!");
                    studentMenu();  // Redirect to the student dashboard
                }
            }
        } catch (SQLException e) {
            System.out.println("Error registering student: " + e.getMessage());
        }
    }

    // Method to view all available courses
    private static void viewAllCourses() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT c.course_name, c.course_description, l.first_name AS lecturer_first_name, l.last_name AS lecturer_last_name " +
                             "FROM courses c " +
                             "JOIN lecturers l ON c.lecturer_id = l.lecturer_id")) {
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) { // If no courses are found
                System.out.println("No courses available.");
            } else {
                System.out.println("\nAvailable Courses:");
                while (rs.next()) {
                    System.out.println("Course: " + rs.getString("course_name"));
                    System.out.println("Lecturer: " + rs.getString("lecturer_first_name") + " " + rs.getString("lecturer_last_name"));
                    System.out.println("Description: " + rs.getString("course_description"));
                    System.out.println(); // Adds a blank line between courses
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching courses: " + e.getMessage());
        }
    }

    // Method to view the courses the student is subscribed to
    private static void viewSubscribedCourses() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT c.course_name, c.course_description, l.first_name AS lecturer_first_name, l.last_name AS lecturer_last_name " +
                             "FROM courses c " +
                             "JOIN lecturers l ON c.lecturer_id = l.lecturer_id " +
                             "JOIN subscriptions s ON c.course_id = s.course_id " +
                             "WHERE s.student_id = ?")) {
            stmt.setInt(1, loggedInStudentId);  // Pass the logged-in student's ID
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) { // If no subscribed courses are found
                System.out.println("You are not subscribed to any courses.");
            } else {
                System.out.println("\nYour Subscribed Courses:");
                while (rs.next()) {
                    System.out.println("Course: " + rs.getString("course_name"));
                    System.out.println("Lecturer: " + rs.getString("lecturer_first_name") + " " + rs.getString("lecturer_last_name"));
                    System.out.println("Description: " + rs.getString("course_description"));
                    System.out.println(); // Adds a blank line between courses
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching subscribed courses: " + e.getMessage());
        }
    }

    // Method to subscribe to a course
    private static void subscribeToCourse() {
        System.out.print("Enter the course name to subscribe: ");
        String courseName = scanner.nextLine();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT course_id FROM courses WHERE course_name = ?")) {
            stmt.setString(1, courseName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int courseId = rs.getInt("course_id");

                try (PreparedStatement subscribeStmt = conn.prepareStatement(
                        "INSERT INTO subscriptions (student_id, course_id) VALUES (?, ?)")) {
                    subscribeStmt.setInt(1, loggedInStudentId);
                    subscribeStmt.setInt(2, courseId);
                    subscribeStmt.executeUpdate();
                    System.out.println("Successfully subscribed to the course: " + courseName);
                }
            } else {
                System.out.println("Course not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error subscribing to the course: " + e.getMessage());
        }
    }

    // Method to unsubscribe from a course
    // Method to unsubscribe from a course
    private static void unsubscribeFromCourse() {
        System.out.print("Enter the course name to unsubscribe: ");
        String courseName = scanner.nextLine();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement courseStmt = conn.prepareStatement(
                     "SELECT course_id FROM courses WHERE course_name = ?")) {
            courseStmt.setString(1, courseName);
            ResultSet rs = courseStmt.executeQuery();

            if (rs.next()) {
                int courseId = rs.getInt("course_id");

                // Step 1: Delete the grade for the course
                try (PreparedStatement deleteGradeStmt = conn.prepareStatement(
                        "DELETE FROM grades WHERE student_id = ? AND course_id = ?")) {
                    deleteGradeStmt.setInt(1, loggedInStudentId);
                    deleteGradeStmt.setInt(2, courseId);
                    deleteGradeStmt.executeUpdate();
                }

                // Step 2: Delete the course subscription
                try (PreparedStatement unsubscribeStmt = conn.prepareStatement(
                        "DELETE FROM subscriptions WHERE student_id = ? AND course_id = ?")) {
                    unsubscribeStmt.setInt(1, loggedInStudentId);
                    unsubscribeStmt.setInt(2, courseId);
                    unsubscribeStmt.executeUpdate();
                    System.out.println("Successfully unsubscribed from the course: " + courseName);
                }
            } else {
                System.out.println("Course not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error unsubscribing from the course: " + e.getMessage());
        }
    }


    // Method to view student's grades
    private static void viewGrades() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT c.course_name, g.grade FROM grades g " +
                             "JOIN courses c ON g.course_id = c.course_id WHERE g.student_id = ?")) {
            stmt.setInt(1, loggedInStudentId);
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("No grades available.");
            } else {
                System.out.println("\nYour Grades:");
                while (rs.next()) {
                    System.out.println(rs.getString("course_name") + ": " + rs.getString("grade"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching grades: " + e.getMessage());
        }
    }

    // Method to view student's assignments
    private static void viewAssignments() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT a.assignment_title, a.due_date FROM assignments a " +
                             "JOIN courses c ON a.course_id = c.course_id " +
                             "JOIN subscriptions s ON s.course_id = c.course_id WHERE s.student_id = ?")) {
            stmt.setInt(1, loggedInStudentId);
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("No assignments available.");
            } else {
                System.out.println("\nYour Assignments:");
                while (rs.next()) {
                    System.out.println(rs.getString("assignment_title") + " - Due Date: " + rs.getDate("due_date"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching assignments: " + e.getMessage());
        }
    }
}