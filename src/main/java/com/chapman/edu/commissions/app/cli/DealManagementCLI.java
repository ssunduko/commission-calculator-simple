package com.chapman.edu.commissions.app.cli;

import com.chapman.edu.commissions.integration.database.DatabaseManager;
import com.chapman.edu.commissions.integration.repository.H2DealRepository;
import com.chapman.edu.commissions.integration.repository.H2UserRepository;
import com.chapman.edu.commissions.integration.service.DealService;
import com.chapman.edu.commissions.integration.service.UserService;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive Command-Line Interface for Deal Management.
 *
 * Provides a menu-driven interface for managing sales deals without
 * requiring a web browser or HTTP client.
 *
 * <b>FEATURES:</b>
 * - List all deals with filtering options
 * - View detailed information about specific deals
 * - Create new deals with products
 * - Update existing deals
 * - Delete deals
 * - View statistics and dashboard
 * - Interactive menu navigation
 *
 * <b>USAGE:</b>
 * mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.app.cli.DealManagementCLI"
 *
 * Or with arguments:
 * mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.app.cli.DealManagementCLI" -Dexec.args="--cli"
 *
 * @author Commission Calculator Team
 * @version 1.0
 */
public class DealManagementCLI {

    private final DealService dealService;
    private final UserService userService;
    private final Scanner scanner;
    private final DatabaseManager dbManager;

    private static final String DIVIDER = "=".repeat(80);
    private static final String SEPARATOR = "-".repeat(80);

    /**
     * Main entry point for CLI application.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        DealManagementCLI cli = new DealManagementCLI();
        cli.start();
    }

    /**
     * Constructs a new CLI application and initializes services.
     */
    public DealManagementCLI() {
        // Initialize database and repositories
        this.dbManager = DatabaseManager.getInstance();
        H2DealRepository dealRepository = new H2DealRepository(dbManager);
        H2UserRepository userRepository = new H2UserRepository(dbManager);

        // Initialize services
        this.dealService = new DealService(dealRepository);
        this.userService = new UserService(userRepository);

        // Initialize scanner for user input
        this.scanner = new Scanner(System.in);
    }

    /**
     * Starts the CLI application and displays main menu.
     */
    public void start() {
        printWelcomeBanner();

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> listAllDeals();
                case "2" -> listDealsByStatus();
                case "3" -> viewDealDetails();
                case "4" -> createNewDeal();
                case "5" -> closeDeal();
                case "6" -> deleteDeal();
                case "7" -> showDashboard();
                case "8" -> listAllUsers();
                case "9" -> {
                    running = false;
                    shutdown();
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }

            if (running && !choice.equals("9")) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    /**
     * Prints welcome banner.
     */
    private void printWelcomeBanner() {
        clearScreen();
        System.out.println(DIVIDER);
        System.out.println("       DEAL MANAGEMENT SYSTEM - COMMAND LINE INTERFACE");
        System.out.println(DIVIDER);
        System.out.println("Welcome to the Deal Management CLI!");
        System.out.println("This interface allows you to manage sales deals interactively.");
        System.out.println(DIVIDER);
    }

    /**
     * Prints main menu options.
     */
    private void printMainMenu() {
        clearScreen();
        System.out.println(DIVIDER);
        System.out.println("                          MAIN MENU");
        System.out.println(DIVIDER);
        System.out.println("  1. List All Deals");
        System.out.println("  2. List Deals by Status");
        System.out.println("  3. View Deal Details");
        System.out.println("  4. Create New Deal");
        System.out.println("  5. Close Deal");
        System.out.println("  6. Delete Deal");
        System.out.println("  7. Show Dashboard");
        System.out.println("  8. List All Users");
        System.out.println("  9. Exit");
        System.out.println(DIVIDER);
        System.out.print("Enter your choice (1-9): ");
    }

    /**
     * Lists all deals in the system.
     */
    private void listAllDeals() {
        clearScreen();
        System.out.println(DIVIDER);
        System.out.println("                        ALL DEALS");
        System.out.println(DIVIDER);

        try {
            List<Deal> deals = dealService.getAllDeals();

            if (deals.isEmpty()) {
                System.out.println("No deals found.");
                return;
            }

            System.out.printf("Total Deals: %d%n%n", deals.size());

            for (Deal deal : deals) {
                printDealSummary(deal);
            }

        } catch (Exception e) {
            System.err.println("Error loading deals: " + e.getMessage());
        }
    }

    /**
     * Lists deals filtered by status.
     */
    private void listDealsByStatus() {
        clearScreen();
        System.out.println(DIVIDER);
        System.out.println("                    LIST DEALS BY STATUS");
        System.out.println(DIVIDER);
        System.out.println("Available statuses: OPEN, WON, LOST, CANCELLED");
        System.out.print("Enter status: ");

        String statusInput = scanner.nextLine().trim().toUpperCase();

        try {
            DealStatus status = DealStatus.valueOf(statusInput);
            List<Deal> deals = dealService.getDealsByStatus(status);

            System.out.println(SEPARATOR);
            System.out.printf("Deals with status '%s': %d%n%n", status, deals.size());

            if (deals.isEmpty()) {
                System.out.println("No deals found with this status.");
                return;
            }

            for (Deal deal : deals) {
                printDealSummary(deal);
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Invalid status. Please use: OPEN, WON, LOST, or CANCELLED");
        } catch (Exception e) {
            System.err.println("Error loading deals: " + e.getMessage());
        }
    }

    /**
     * Views detailed information about a specific deal.
     */
    private void viewDealDetails() {
        clearScreen();
        System.out.println(DIVIDER);
        System.out.println("                      VIEW DEAL DETAILS");
        System.out.println(DIVIDER);
        System.out.print("Enter Deal ID: ");

        String dealId = scanner.nextLine().trim();

        try {
            Deal deal = dealService.getDealById(dealId)
                    .orElseThrow(() -> new RuntimeException("Deal not found with ID: " + dealId));

            System.out.println(SEPARATOR);
            printDetailedDeal(deal);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Creates a new deal interactively.
     */
    private void createNewDeal() {
        clearScreen();
        System.out.println(DIVIDER);
        System.out.println("                      CREATE NEW DEAL");
        System.out.println(DIVIDER);

        try {
            // Get deal title
            System.out.print("Deal Title: ");
            String title = scanner.nextLine().trim();

            if (title.isEmpty()) {
                System.err.println("Title cannot be empty.");
                return;
            }

            // Get sales rep ID
            System.out.print("Sales Rep ID: ");
            String salesRepId = scanner.nextLine().trim();

            if (salesRepId.isEmpty()) {
                System.err.println("Sales Rep ID cannot be empty.");
                return;
            }

            // Get status
            System.out.println("Status (OPEN, WON, LOST, CANCELLED) [default: OPEN]: ");
            String statusInput = scanner.nextLine().trim().toUpperCase();
            DealStatus status = statusInput.isEmpty() ? DealStatus.OPEN : DealStatus.valueOf(statusInput);

            // Get products
            List<DealProduct> products = new ArrayList<>();
            boolean addingProducts = true;

            System.out.println(SEPARATOR);
            System.out.println("Add Products (at least one required):");

            while (addingProducts) {
                System.out.printf("%nProduct #%d:%n", products.size() + 1);

                System.out.print("  Product ID: ");
                String productId = scanner.nextLine().trim();

                System.out.print("  Product Name: ");
                String productName = scanner.nextLine().trim();

                System.out.print("  Quantity: ");
                int quantity = Integer.parseInt(scanner.nextLine().trim());

                System.out.print("  Unit Price: ");
                BigDecimal unitPrice = new BigDecimal(scanner.nextLine().trim());

                products.add(new DealProduct(productId, productName, quantity, unitPrice));

                System.out.print("\nAdd another product? (y/n): ");
                String response = scanner.nextLine().trim().toLowerCase();
                addingProducts = response.equals("y") || response.equals("yes");
            }

            // Create deal
            Deal deal = new Deal();
            deal.setTitle(title);
            deal.setSalesRepId(salesRepId);
            deal.setStatus(status);
            deal.setProducts(products);

            Deal createdDeal = dealService.createDeal(deal);

            System.out.println(SEPARATOR);
            System.out.println("Deal created successfully!");
            System.out.println(SEPARATOR);
            printDetailedDeal(createdDeal);

        } catch (NumberFormatException e) {
            System.err.println("Invalid number format. Please enter valid numbers for quantity and price.");
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid status. Please use: OPEN, WON, LOST, or CANCELLED");
        } catch (Exception e) {
            System.err.println("Error creating deal: " + e.getMessage());
        }
    }

    /**
     * Closes an existing deal.
     */
    private void closeDeal() {
        clearScreen();
        System.out.println(DIVIDER);
        System.out.println("                        CLOSE DEAL");
        System.out.println(DIVIDER);
        System.out.print("Enter Deal ID to close: ");

        String dealId = scanner.nextLine().trim();

        try {
            Deal closedDeal = dealService.closeDealAsWon(dealId);

            System.out.println(SEPARATOR);
            System.out.println("Deal closed successfully!");
            System.out.println(SEPARATOR);
            printDetailedDeal(closedDeal);

        } catch (Exception e) {
            System.err.println("Error closing deal: " + e.getMessage());
        }
    }

    /**
     * Deletes an existing deal.
     */
    private void deleteDeal() {
        clearScreen();
        System.out.println(DIVIDER);
        System.out.println("                        DELETE DEAL");
        System.out.println(DIVIDER);
        System.out.print("Enter Deal ID to delete: ");

        String dealId = scanner.nextLine().trim();

        System.out.print("Are you sure you want to delete this deal? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (!confirmation.equals("yes")) {
            System.out.println("Delete cancelled.");
            return;
        }

        try {
            dealService.deleteDeal(dealId);
            System.out.println(SEPARATOR);
            System.out.println("Deal deleted successfully!");

        } catch (Exception e) {
            System.err.println("Error deleting deal: " + e.getMessage());
        }
    }

    /**
     * Shows dashboard with statistics.
     */
    private void showDashboard() {
        clearScreen();
        System.out.println(DIVIDER);
        System.out.println("                          DASHBOARD");
        System.out.println(DIVIDER);

        try {
            List<Deal> allDeals = dealService.getAllDeals();

            long totalDeals = allDeals.size();
            long openDeals = allDeals.stream().filter(d -> d.getStatus() == DealStatus.OPEN).count();
            long wonDeals = allDeals.stream().filter(d -> d.getStatus() == DealStatus.WON).count();
            long lostDeals = allDeals.stream().filter(d -> d.getStatus() == DealStatus.LOST).count();
            long cancelledDeals = allDeals.stream().filter(d -> d.getStatus() == DealStatus.CANCELLED).count();

            BigDecimal totalValue = allDeals.stream()
                    .map(Deal::calculateTotalValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal openValue = allDeals.stream()
                    .filter(d -> d.getStatus() == DealStatus.OPEN)
                    .map(Deal::calculateTotalValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal wonValue = allDeals.stream()
                    .filter(d -> d.getStatus() == DealStatus.WON)
                    .map(Deal::calculateTotalValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            System.out.println("DEAL STATISTICS:");
            System.out.println(SEPARATOR);
            System.out.printf("  Total Deals:     %d%n", totalDeals);
            System.out.printf("  Open Deals:      %d%n", openDeals);
            System.out.printf("  Won Deals:       %d%n", wonDeals);
            System.out.printf("  Lost Deals:      %d%n", lostDeals);
            System.out.printf("  Cancelled Deals: %d%n", cancelledDeals);
            System.out.println(SEPARATOR);
            System.out.printf("  Total Value:     $%,.2f%n", totalValue);
            System.out.printf("  Open Value:      $%,.2f%n", openValue);
            System.out.printf("  Won Value:       $%,.2f%n", wonValue);
            System.out.println(SEPARATOR);

            if (!allDeals.isEmpty()) {
                System.out.printf("  Win Rate:        %.1f%%%n",
                        (wonDeals * 100.0) / totalDeals);
                System.out.printf("  Avg Deal Value:  $%,.2f%n",
                        totalValue.divide(BigDecimal.valueOf(totalDeals), 2, BigDecimal.ROUND_HALF_UP));
            }

        } catch (Exception e) {
            System.err.println("Error loading dashboard: " + e.getMessage());
        }
    }

    /**
     * Lists all users in the system.
     */
    private void listAllUsers() {
        clearScreen();
        System.out.println(DIVIDER);
        System.out.println("                        ALL USERS");
        System.out.println(DIVIDER);

        try {
            List<User> users = userService.getAllUsers();

            if (users.isEmpty()) {
                System.out.println("No users found.");
                return;
            }

            System.out.printf("Total Users: %d%n%n", users.size());

            for (User user : users) {
                System.out.printf("ID: %s%n", user.getId());
                System.out.printf("Name: %s %s%n", user.getFirstName(), user.getLastName());
                System.out.printf("Email: %s%n", user.getEmail());
                System.out.printf("Roles: %s%n", user.getRoles());
                System.out.printf("Active: %s%n", user.isActive() ? "Yes" : "No");
                System.out.println(SEPARATOR);
            }

        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    /**
     * Prints a summary of a deal.
     */
    private void printDealSummary(Deal deal) {
        System.out.printf("ID: %s | %s%n", deal.getId(), deal.getTitle());
        System.out.printf("  Status: %s | Sales Rep: %s | Value: $%,.2f%n",
                deal.getStatus(),
                deal.getSalesRepId(),
                deal.calculateTotalValue());
        System.out.printf("  Products: %d | Created: %s%n",
                deal.getProducts() != null ? deal.getProducts().size() : 0,
                deal.getCreatedDate());
        System.out.println(SEPARATOR);
    }

    /**
     * Prints detailed information about a deal.
     */
    private void printDetailedDeal(Deal deal) {
        System.out.printf("Deal ID:         %s%n", deal.getId());
        System.out.printf("Title:           %s%n", deal.getTitle());
        System.out.printf("Status:          %s%n", deal.getStatus());
        System.out.printf("Sales Rep ID:    %s%n", deal.getSalesRepId());
        System.out.printf("Created Date:    %s%n", deal.getCreatedDate());
        System.out.printf("Last Modified:   %s%n", deal.getLastModifiedDate());

        if (deal.getCloseDate() != null) {
            System.out.printf("Close Date:      %s%n", deal.getCloseDate());
        }

        System.out.println(SEPARATOR);

        if (deal.getProducts() != null && !deal.getProducts().isEmpty()) {
            System.out.printf("Products (%d):%n", deal.getProducts().size());
            for (DealProduct product : deal.getProducts()) {
                System.out.printf("  - %s (%s)%n", product.getProductName(), product.getProductId());
                System.out.printf("    Quantity: %d | Unit Price: $%,.2f | Total: $%,.2f%n",
                        product.getQuantity(),
                        product.getPrice(),
                        product.getPrice().multiply(BigDecimal.valueOf(product.getQuantity())));
            }
            System.out.println(SEPARATOR);
        }

        System.out.printf("TOTAL VALUE:     $%,.2f%n", deal.calculateTotalValue());
    }

    /**
     * Clears the screen (works on most terminals).
     */
    private void clearScreen() {
        try {
            // Try ANSI escape code (works on Unix/Linux/Mac and modern Windows terminals)
            System.out.print("\033[H\033[2J");
            System.out.flush();
        } catch (Exception e) {
            // Fallback: print empty lines
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    /**
     * Shuts down the CLI application.
     */
    private void shutdown() {
        System.out.println(DIVIDER);
        System.out.println("Shutting down Deal Management CLI...");

        try {
            scanner.close();
            dbManager.close();
            System.out.println("Thank you for using Deal Management System!");
            System.out.println(DIVIDER);
        } catch (Exception e) {
            System.err.println("Error during shutdown: " + e.getMessage());
        }
    }
}