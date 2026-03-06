package com.karvy;

import java.io.*;
import java.util.List;

public class FileSaveThread extends Thread {
    @SuppressWarnings("FieldMayBeFinal")
    private List<Student> students;
    @SuppressWarnings("FieldMayBeFinal")
    private String filename;
    private boolean isRunning = true;

    public FileSaveThread(List<Student> students, String filename) {
        this.students = students;
        this.filename = filename;
        this.setName("StudentFileSaveThread");
        this.setDaemon(true); // Set as daemon thread
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                synchronized (students) {
                    saveStudentsToFile();
                }
                // Auto-save every 30 seconds
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                if (isRunning) {
                    System.out.println("[Background Thread] Interrupted while sleeping: " + e.getMessage());
                }
                Thread.currentThread().interrupt();
                break;
            } catch (IOException e) {
                System.out.println("[Background Thread] Error saving file: " + e.getMessage());
            }
        }
        System.out.println("[Background Thread] File save thread stopped.");
    }

    private void saveStudentsToFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
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
            System.out.println("[Background Thread] ✓ Students auto-saved to " + filename);
        }
    }

    public void stopThread() {
        isRunning = false;
        this.interrupt();
    }
}
