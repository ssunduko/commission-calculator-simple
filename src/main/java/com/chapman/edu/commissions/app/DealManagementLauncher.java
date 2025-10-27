package com.chapman.edu.commissions.app;

import com.chapman.edu.commissions.app.cli.DealManagementCLI;

import java.util.Scanner;

/**
 * Launcher for the Deal Management Application.
 *
 * This class provides a unified entry point that allows users to choose
 * between different modes of running the application:
 * - Web Mode: Starts the full web application with REST API and Web UI
 * - CLI Mode: Starts the interactive command-line interface
 *
 * <b>USAGE:</b>
 *
 * Interactive mode (prompts for choice):
 *   mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.app.DealManagementLauncher"
 *
 * Direct web mode:
 *   mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.app.DealManagementLauncher" -Dexec.args="web"
 *
 * Direct CLI mode:
 *   mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.app.DealManagementLauncher" -Dexec.args="cli"
 *
 * @author Commission Calculator Team
 * @version 1.0
 */
public class DealManagementLauncher {

    private static final String BANNER = """
            ================================================================================

                       ____             _   __  __                                                _
                      |  _ \\  ___  __ _| | |  \\/  | __ _ _ __   __ _  __ _  ___ _ __ ___   ___ _ __ | |_
                      | | | |/ _ \\/ _` | | | |\\/| |/ _` | '_ \\ / _` |/ _` |/ _ \\ '_ ` _ \\ / _ \\ '_ \\| __|
                      | |_| |  __/ (_| | | | |  | | (_| | | | | (_| | (_| |  __/ | | | | |  __/ | | | |_
                      |____/ \\___|\\__,_|_| |_|  |_|\\__,_|_| |_|\\__,_|\\__, |\\___|_| |_| |_|\\___|_| |_|\\__|
                                                                      |___/

                                     Sales Commission Calculator
                                  Enterprise Deal Management System

            ================================================================================
            """;

    /**
     * Main entry point for the launcher.
     *
     * Accepts command-line arguments to determine which mode to run:
     * - "web" or "1": Start web application
     * - "cli" or "2": Start CLI application
     * - No argument: Show interactive menu
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println(BANNER);

        // Check if mode was specified via command-line argument
        if (args.length > 0) {
            String mode = args[0].toLowerCase();
            launchMode(mode);
        } else {
            // Interactive mode - show menu
            showInteractiveMenu();
        }
    }

    /**
     * Shows an interactive menu for mode selection.
     */
    private static void showInteractiveMenu() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please select how you would like to run the application:");
        System.out.println();
        System.out.println("  1. Web Mode (REST API + Web UI + Dashboard)");
        System.out.println("     - Full-featured web application with browser interface");
        System.out.println("     - Access at http://localhost:8080/index.html");
        System.out.println("     - Includes REST API, H2 Console, and interactive dashboard");
        System.out.println();
        System.out.println("  2. CLI Mode (Interactive Command Line)");
        System.out.println("     - Menu-driven terminal interface");
        System.out.println("     - No browser required");
        System.out.println("     - Ideal for remote servers or headless environments");
        System.out.println();
        System.out.println("  3. Exit");
        System.out.println();
        System.out.println("================================================================================");
        System.out.print("Enter your choice (1-3): ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1", "web" -> launchWebMode();
            case "2", "cli" -> launchCliMode();
            case "3", "exit" -> {
                System.out.println("Goodbye!");
                System.exit(0);
            }
            default -> {
                System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                System.out.println();
                showInteractiveMenu(); // Recursive call to show menu again
            }
        }

        scanner.close();
    }

    /**
     * Launches the application in the specified mode.
     *
     * @param mode The mode to launch ("web", "cli", "1", or "2")
     */
    private static void launchMode(String mode) {
        switch (mode) {
            case "web", "1" -> launchWebMode();
            case "cli", "2" -> launchCliMode();
            default -> {
                System.err.println("Invalid mode: " + mode);
                System.err.println("Valid options: web, cli, 1, or 2");
                System.exit(1);
            }
        }
    }

    /**
     * Launches the web application mode.
     */
    private static void launchWebMode() {
        System.out.println("================================================================================");
        System.out.println("Starting Deal Management Application in WEB MODE...");
        System.out.println("================================================================================");
        System.out.println();

        try {
            DealManagementApp.main(new String[]{});
        } catch (Exception e) {
            System.err.println("Failed to start web application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Launches the CLI application mode.
     */
    private static void launchCliMode() {
        System.out.println("================================================================================");
        System.out.println("Starting Deal Management Application in CLI MODE...");
        System.out.println("================================================================================");
        System.out.println();

        try {
            DealManagementCLI.main(new String[]{});
        } catch (Exception e) {
            System.err.println("Failed to start CLI application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}