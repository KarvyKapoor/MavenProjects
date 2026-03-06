package com.karvy;

import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║   Welcome to Student Management System ║");
            System.out.println("╚════════════════════════════════════════╝");
            
            StudentManager manager = new StudentManager(sc);
            manager.run();
        }
    }
}
