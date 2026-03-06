package com.karvy;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class StudentManager {
    @SuppressWarnings("FieldMayBeFinal")
    private List<Student> students;
    @SuppressWarnings("FieldMayBeFinal")
    private Scanner scanner;
    private FileSaveThread saveThread;
    private static final String DATA_FILE = "students_data.txt";

    public StudentManager(Scanner scanner) {
        this.students = new ArrayList<>();
        this.scanner = scanner;
        this.saveThread = null;
    }

    public void displayMenu() {
        System.out.println("\n===== STUDENT MANAGEMENT SYSTEM =====");
        System.out.println("1. Add Student");
        System.out.println("2. View All Students");
        System.out.println("3. Search Student");
        System.out.println("4. Edit Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Filter by Marks");
        System.out.println("7. Stream-based Filters");
        System.out.println("8. Save Students to File");
        System.out.println("9. Load Students from File");
        System.out.println("10. Exit");
        System.out.println("=====================================");
        System.out.print("Enter your choice: ");
    }

    public void run() {
        // Load previous data if exists
        loadStudentsFromFile();
        
        // Start background save thread
        startBackgroundSaveThread();
        
        int choice;
        boolean running = true;

        while (running) {
            displayMenu();
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    viewAllStudents();
                    break;
                case 3:
                    searchStudent();
                    break;
                case 4:
                    editStudent();
                    break;
                case 5:
                    deleteStudent();
                    break;
                case 6:
                    filterByMarksMenu();
                    break;
                case 7:
                    streamFilterMenu();
                    break;
                case 8:
                    saveStudentsManually();
                    break;
                case 9:
                    loadStudentsFromFile();
                    break;
                case 10:
                    System.out.println("\nSaving students before exit...");
                    saveStudentsManually();
                    stopBackgroundThread();
                    System.out.println("=== Thank you for using Student Management System ===");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    // Add a new student
    private void addStudent() {
        System.out.println("\n--- Add New Student ---");
        System.out.print("Enter student name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter roll number: ");
        int rollNo = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter marks: ");
        double marks = scanner.nextDouble();
        scanner.nextLine();

        // Check if student with same roll number already exists
        if (isRollNoExists(rollNo)) {
            System.out.println("Error: Student with roll number " + rollNo + " already exists!");
        } else {
            Student student = new Student(name, rollNo, email, marks);
            students.add(student);
            System.out.println("Student added successfully!");
        }
    }

    // View all students
    private void viewAllStudents() {
        if (students.isEmpty()) {
            System.out.println("\n--- No students in the system ---");
        } else {
            System.out.println("\n--- All Students ---");
            for (int i = 0; i < students.size(); i++) {
                System.out.println("\nStudent #" + (i + 1));
                students.get(i).display();
            }
        }
    }

    // Search student by roll number or name
    private void searchStudent() {
        System.out.println("\n--- Search Student ---");
        System.out.println("1. Search by Roll Number");
        System.out.println("2. Search by Name");
        System.out.print("Enter search option (1-2): ");
        int searchOption = scanner.nextInt();
        scanner.nextLine();

        List<Student> searchResults = new ArrayList<>();

        switch (searchOption) {
            case 1:
                System.out.print("Enter roll number: ");
                int rollNo = scanner.nextInt();
                scanner.nextLine();
                for (Student student : students) {
                    if (student.getRollNo() == rollNo) {
                        searchResults.add(student);
                    }
                }   break;
            case 2:
                System.out.print("Enter name (partial or full): ");
                String name = scanner.nextLine().trim().toLowerCase();
                for (Student student : students) {
                    if (student.getName().toLowerCase().contains(name)) {
                        searchResults.add(student);
                    }
                }   break;
            default:
                System.out.println("Invalid search option!");
                return;
        }

        if (searchResults.isEmpty()) {
            System.out.println("No students found matching your criteria.");
        } else {
            System.out.println("\n--- Search Results ---");
            for (int i = 0; i < searchResults.size(); i++) {
                System.out.println("\nResult #" + (i + 1));
                searchResults.get(i).display();

                // Ask if user wants to perform operations on found student
                performOperationOnFound(searchResults.get(i));
            }
        }
    }

    // Perform operations on found student (edit/delete)
    private void performOperationOnFound(Student student) {
        System.out.println("1. Edit this student");
        System.out.println("2. Delete this student");
        System.out.println("3. Back to menu");
        System.out.print("Choose action (1-3): ");
        int action = scanner.nextInt();
        scanner.nextLine();

        switch (action) {
            case 1:
                editSpecificStudent(student);
                break;
            case 2:
                students.remove(student);
                System.out.println("✓ Student deleted successfully!");
                break;
            case 3:
                // Back to menu
                break;
            default:
                System.out.println("Invalid action!");
        }
    }

    // Edit a student
    private void editStudent() {
        System.out.println("\n--- Edit Student ---");
        System.out.print("Enter roll number of student to edit: ");
        int rollNo = scanner.nextInt();
        scanner.nextLine();

        Student student = findStudentByRollNo(rollNo);
        if (student == null) {
            System.out.println("Student not found!");
        } else {
            editSpecificStudent(student);
        }
    }

    // Edit specific student details
    private void editSpecificStudent(Student student) {
        System.out.println("\n--- Edit Student Details ---");
        System.out.println("1. Edit Name (Current: " + student.getName() + ")");
        System.out.println("2. Edit Email (Current: " + student.getEmail() + ")");
        System.out.println("3. Edit Marks (Current: " + student.getMarks() + ")");
        System.out.println("4. Back to menu");
        System.out.print("Choose field to edit (1-4): ");
        int field = scanner.nextInt();
        scanner.nextLine();

        switch (field) {
            case 1:
                System.out.print("Enter new name: ");
                String newName = scanner.nextLine().trim();
                student.setName(newName);
                System.out.println("Name updated successfully!");
                break;
            case 2:
                System.out.print("Enter new email: ");
                String newEmail = scanner.nextLine().trim();
                student.setEmail(newEmail);
                System.out.println("Email updated successfully!");
                break;
            case 3:
                System.out.print("Enter new marks: ");
                double newMarks = scanner.nextDouble();
                scanner.nextLine();
                student.setMarks(newMarks);
                System.out.println("Marks updated successfully!");
                break;
            case 4:
                // Back to menu
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }

    // Filter by Marks Menu
    private void filterByMarksMenu() {
        System.out.println("\n--- Filter by Marks ---");
        System.out.println("1. Greater Than");
        System.out.println("2. Smaller Than");
        System.out.println("3. Greater Than or Equal To");
        System.out.println("4. Smaller Than or Equal To");
        System.out.println("5. Equal To");
        System.out.println("6. Back to Menu");
        System.out.print("Choose filter option (1-6): ");
        int filterOption = scanner.nextInt();
        scanner.nextLine();

        if (filterOption == 6) {
            return;
        }

        if (filterOption < 1 || filterOption > 5) {
            System.out.println("Invalid filter option!");
            return;
        }

        System.out.print("Enter marks value: ");
        double marks = scanner.nextDouble();
        scanner.nextLine();

        List<Student> filteredResults = new ArrayList<>();

        switch (filterOption) {
            case 1:
                // Greater Than
                for (Student student : students) {
                    if (student.getMarks() > marks) {
                        filteredResults.add(student);
                    }
                }
                displayFilterResults(filteredResults, "Greater than " + marks);
                break;
            case 2:
                // Smaller Than
                for (Student student : students) {
                    if (student.getMarks() < marks) {
                        filteredResults.add(student);
                    }
                }
                displayFilterResults(filteredResults, "Smaller than " + marks);
                break;
            case 3:
                // Greater Than or Equal To
                for (Student student : students) {
                    if (student.getMarks() >= marks) {
                        filteredResults.add(student);
                    }
                }
                displayFilterResults(filteredResults, "Greater than or equal to " + marks);
                break;
            case 4:
                // Smaller Than or Equal To
                for (Student student : students) {
                    if (student.getMarks() <= marks) {
                        filteredResults.add(student);
                    }
                }
                displayFilterResults(filteredResults, "Smaller than or equal to " + marks);
                break;
            case 5:
                // Equal To
                for (Student student : students) {
                    if (student.getMarks() == marks) {
                        filteredResults.add(student);
                    }
                }
                displayFilterResults(filteredResults, "Equal to " + marks);
                break;
        }
    }

    // Display filter results
    private void displayFilterResults(List<Student> results, String filterCriteria) {
        if (results.isEmpty()) {
            System.out.println("\nNo students found with marks " + filterCriteria);
        } else {
            System.out.println("\n--- Students with Marks " + filterCriteria + " ---");
            for (int i = 0; i < results.size(); i++) {
                System.out.println("\nResult #" + (i + 1));
                results.get(i).display();
            }
            System.out.println("\nTotal students found: " + results.size());
        }
    }

    // Filter students by minimum marks (Legacy method)
    public void filterByMarks(int minMarks) {
        boolean found = false;
        for(Student s : students) {
            if(s.getMarks() >= minMarks) {
                System.out.println("Name: " + s.getName());
                System.out.println("Roll: " + s.getRollNo());
                System.out.println("Marks: " + s.getMarks());
                System.out.println();
                found = true;
            }
        }
        if(!found) {
            System.out.println("No students found with marks >= " + minMarks);
        }
    }

    // Delete a student
    private void deleteStudent() {
        System.out.println("\n--- Delete Student ---");
        System.out.print("Enter roll number of student to delete: ");
        int rollNo = scanner.nextInt();
        scanner.nextLine();

        Student student = findStudentByRollNo(rollNo);
        if (student == null) {
            System.out.println("Student not found!");
        } else {
            System.out.println("Student Details:");
            student.display();
            System.out.print("Are you sure you want to delete this student? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (confirmation.equals("yes")) {
                students.remove(student);
                System.out.println("Student deleted successfully!");
            } else {
                System.out.println("Deletion cancelled.");
            }
        }
    }

    // Helper method to find student by roll number
    private Student findStudentByRollNo(int rollNo) {
        for (Student student : students) {
            if (student.getRollNo() == rollNo) {
                return student;
            }
        }
        return null;
    }

    // Helper method to check if roll number exists
    private boolean isRollNoExists(int rollNo) {
        return findStudentByRollNo(rollNo) != null;
    }

    public int getStudentCount() {
        return students.size();
    }

    // ============ THREAD AND FILE HANDLING METHODS ============

    // Start background save thread
    private void startBackgroundSaveThread() {
        if (saveThread == null || !saveThread.isAlive()) {
            saveThread = new FileSaveThread(students, DATA_FILE);
            saveThread.start();
            System.out.println("✓ Background save thread started.");
        }
    }

    // Stop background save thread
    private void stopBackgroundThread() {
        if (saveThread != null && saveThread.isAlive()) {
            saveThread.stopThread();
            try {
                saveThread.join(5000); // Wait for thread to finish
                System.out.println("✓ Background save thread stopped.");
            } catch (InterruptedException e) {
                System.out.println("Error waiting for save thread: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    // Save students to file manually
    private void saveStudentsManually() {
        try {
            synchronized (students) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
                    // Write header
                    writer.write("Name,RollNo,Email,Marks");
                    writer.newLine();
                    
                    // Write each student as CSV
                    for (Student student : students) {
                        writer.write(student.getName() + "," + 
                                    student.getRollNo() + "," + 
                                    student.getEmail() + "," + 
                                    student.getMarks());
                        writer.newLine();
                    }
                    System.out.println("✓ Students saved successfully to " + DATA_FILE);
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving students: " + e.getMessage());
        }
    }

    // Load students from file
    private void loadStudentsFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("No previous data found. Starting fresh.");
            return;
        }

        try {
            synchronized (students) {
                students.clear();
                try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
                    String line;
                    int count = 0;
                    
                    // Skip header
                    reader.readLine();
                    
                    // Read student data
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty()) {
                            continue;
                        }
                        
                        String[] parts = line.split(",");
                        if (parts.length == 4) {
                            try {
                                String name = parts[0].trim();
                                int rollNo = Integer.parseInt(parts[1].trim());
                                String email = parts[2].trim();
                                double marks = Double.parseDouble(parts[3].trim());
                                
                                students.add(new Student(name, rollNo, email, marks));
                                count++;
                            } catch (NumberFormatException e) {
                                System.out.println("Warning: Skipping invalid line: " + line);
                            }
                        }
                    }
                    System.out.println("✓ Loaded " + count + " students from file.");
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading students: " + e.getMessage());
        }
    }

    // ============ STREAM API BASED FILTERS ============

    // Stream-based filter menu
    private void streamFilterMenu() {
        System.out.println("\n--- Stream-Based Filters ---");
        System.out.println("1. Search by Name (Stream)");
        System.out.println("2. Filter by Marks Range (Stream)");
        System.out.println("3. Filter by Email Domain (Stream)");
        System.out.println("4. Get Students Above Average (Stream)");
        System.out.println("5. Sort Students by Marks (Stream)");
        System.out.println("6. Back to Menu");
        System.out.print("Choose option (1-6): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                searchByNameStream();
                break;
            case 2:
                filterByMarksRangeStream();
                break;
            case 3:
                filterByEmailDomainStream();
                break;
            case 4:
                getStudentsAboveAverageStream();
                break;
            case 5:
                sortStudentsByMarksStream();
                break;
            case 6:
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }

    // Search by name using Stream API
    private void searchByNameStream() {
        System.out.print("Enter name to search (partial or full): ");
        String searchName = scanner.nextLine().trim().toLowerCase();

        List<Student> results = students.stream()
                .filter(s -> s.getName().toLowerCase().contains(searchName))
                .collect(Collectors.toList());

        if (results.isEmpty()) {
            System.out.println("No students found with name containing: " + searchName);
        } else {
            System.out.println("\n--- Search Results (Stream) ---");
            results.forEach(s -> {
                System.out.println();
                s.display();
            });
            System.out.println("Total found: " + results.size());
        }
    }

    // Filter by marks range using Stream API
    private void filterByMarksRangeStream() {
        System.out.print("Enter minimum marks: ");
        double minMarks = scanner.nextDouble();
        System.out.print("Enter maximum marks: ");
        double maxMarks = scanner.nextDouble();
        scanner.nextLine();

        List<Student> results = students.stream()
                .filter(s -> s.getMarks() >= minMarks && s.getMarks() <= maxMarks)
                .collect(Collectors.toList());

        if (results.isEmpty()) {
            System.out.println("No students found in marks range: " + minMarks + " - " + maxMarks);
        } else {
            System.out.println("\n--- Students in Range " + minMarks + " - " + maxMarks + " (Stream) ---");
            results.forEach(s -> {
                System.out.println();
                s.display();
            });
            System.out.println("Total found: " + results.size());
        }
    }

    // Filter by email domain using Stream API
    private void filterByEmailDomainStream() {
        System.out.print("Enter email domain (e.g., gmail.com): ");
        String domain = scanner.nextLine().trim().toLowerCase();

        List<Student> results = students.stream()
                .filter(s -> s.getEmail().toLowerCase().endsWith("@" + domain))
                .collect(Collectors.toList());

        if (results.isEmpty()) {
            System.out.println("No students found with email domain: @" + domain);
        } else {
            System.out.println("\n--- Students with @" + domain + " email (Stream) ---");
            results.forEach(s -> {
                System.out.println();
                s.display();
            });
            System.out.println("Total found: " + results.size());
        }
    }

    // Get students above average marks using Stream API
    private void getStudentsAboveAverageStream() {
        double averageMarks = students.stream()
                .mapToDouble(Student::getMarks)
                .average()
                .orElse(0.0);

        System.out.println("\n--- Average Marks: " + String.format("%.2f", averageMarks) + " ---");

        List<Student> results = students.stream()
                .filter(s -> s.getMarks() > averageMarks)
                .collect(Collectors.toList());

        if (results.isEmpty()) {
            System.out.println("No students above average marks.");
        } else {
            System.out.println("\n--- Students Above Average (Stream) ---");
            results.forEach(s -> {
                System.out.println();
                s.display();
            });
            System.out.println("Total students above average: " + results.size());
        }
    }

    // Sort and display students by marks using Stream API
    private void sortStudentsByMarksStream() {
        System.out.println("\n--- Students Sorted by Marks (Descending) ---");

        students.stream()
                .sorted((s1, s2) -> Double.compare(s2.getMarks(), s1.getMarks()))
                .forEach(s -> {
                    System.out.println();
                    s.display();
                });
    }
}