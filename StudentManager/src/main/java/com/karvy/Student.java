package com.karvy;

import java.io.Serializable;

public class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private int rollNo;
    private String email;
    private double marks;

    public Student(String name, int rollNo) {
        this.name = name;
        this.rollNo = rollNo;
        this.email = "";
        this.marks = 0.0;
    }

    public Student(String name, int rollNo, String email, double marks) {
        this.name = name;
        this.rollNo = rollNo;
        this.email = email;
        this.marks = marks;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getRollNo() {
        return rollNo;
    }

    public String getEmail() {
        return email;
    }

    public double getMarks() {
        return marks;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setRollNo(int rollNo) {
        this.rollNo = rollNo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMarks(double marks) {
        this.marks = marks;
    }

    public void display() {
        System.out.println("\n==== Student Details ====");
        System.out.println("Name: " + name);
        System.out.println("Roll No: " + rollNo);
        System.out.println("Email: " + email);
        System.out.println("Marks: " + marks);
        System.out.println("=========================");
    }
}
