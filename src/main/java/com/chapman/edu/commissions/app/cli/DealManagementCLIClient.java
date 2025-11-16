package com.chapman.edu.commissions.app.cli;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.User;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

/**
 * REST API Client-Based CLI for Deal Management.
 *
 * This CLI demonstrates proper client-server architecture by communicating
 * with the backend exclusively through the REST API (HTTP), rather than
 * directly accessing services or repositories.
 *
 * **ARCHITECTURE COMPARISON:**
 *
 * **OLD APPROACH (Direct Service Access):**
 * ```
 * CLI -> DealService -> H2DealRepository -> Database
 * ```
 * Problem: Tight coupling, no network layer, monolithic
 *
 * **NEW APPROACH (REST API Client):**
 * ```
 * CLI -> HTTP Client -> REST API -> DealController -> DealService -> Repository -> Database
 * ```
 * Benefits: Loose coupling, network-ready, true client-server architecture
 *
 * **KEY DIFFERENCES:**
 *
 * 1. **COMMUNICATION:**
 *    - Old: Direct method calls (dealService.createDeal())
 *    - New: HTTP requests (POST http://localhost:8080/api/v1/integration/deals)
 *
 * 2. **DEPENDENCY:**
 *    - Old: Depends on Service, Repository, Database layers
 *    - New: Depends only on HTTP Client and JSON serialization
 *
 * 3. **DEPLOYMENT:**
 *    - Old: Must run in same JVM as server
 *    - New: Can run on different machine, different network
 *
 * 4. **DATA FORMAT:**
 *    - Old: Java objects passed directly
 *    - New: JSON over HTTP
 *
 * **HTTP CLIENT FEATURES:**
 * - Uses Java 11+ HttpClient (modern, non-blocking capable)
 * - JSON serialization with Gson
 * - Proper error handling for network failures
 * - RESTful communication (GET, POST, DELETE)
 *
 * **PREREQUISITES:**
 * The DealManagementApp server must be running on localhost:8080
 *
 * **USAGE:**
 * ```bash
 * # Start server first
 * mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.app.DealManagementApp"
 *
 * # Then start CLI client (in separate terminal)
 * mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.app.cli.DealManagementCLIClient"
 * ```
 *
 * @author Sergey L. Sundukovskiy
 * @version 2.0
 */
public class DealManagementCLIClient {

    private final HttpClient httpClient;
    private final Gson gson;
    private final Scanner scanner;
    private final String authHeaderValue;

    // API Configuration
    private static final String API_BASE_URL = "http://localhost:8080";
    private static final String DEALS_ENDPOINT = "/api/v1/integration/deals";
    private static final String USERS_ENDPOINT = "/api/v1/integration/users";

    // Default authentication credentials (from IntegrationApplication sample data)
    private static final String DEFAULT_EMAIL = "john.doe@example.com";
    private static final String DEFAULT_PASSWORD = "password";

    private static final String DIVIDER = "=".repeat(80);
    private static final String SEPARATOR = "-".repeat(80);

    /**
     * Main entry point for CLI client application.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        DealManagementCLIClient cli = new DealManagementCLIClient();
        cli.start();
    }

    /**
     * Constructs a new CLI client application.
     * Initializes HTTP client instead of direct service access.
     */
    public DealManagementCLIClient() {
        // HTTP Client for REST API communication
        this.httpClient = HttpClient.newHttpClient();

        // Gson for JSON serialization/deserialization with LocalDate/LocalDateTime support
        // Custom TypeAdapters needed for Java 9+ module restrictions
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class,
                    (JsonDeserializer<LocalDate>) (json, type, context) ->
                        LocalDate.parse(json.getAsString()))
                .registerTypeAdapter(LocalDate.class,
                    (JsonSerializer<LocalDate>) (date, type, context) ->
                        context.serialize(date.toString()))
                .registerTypeAdapter(LocalDateTime.class,
                    (JsonDeserializer<LocalDateTime>) (json, type, context) ->
                        LocalDateTime.parse(json.getAsString()))
                .registerTypeAdapter(LocalDateTime.class,
                    (JsonSerializer<LocalDateTime>) (dateTime, type, context) ->
                        context.serialize(dateTime.toString()))
                .create();

        // Scanner for user input
        this.scanner = new Scanner(System.in);

        // Prepare HTTP Basic Authentication header
        // Format: "Authorization: Basic base64(email:password)"
        this.authHeaderValue = createBasicAuthHeader(DEFAULT_EMAIL, DEFAULT_PASSWORD);
    }

    /**
     * Creates a Basic Authentication header value.
     *
     * **HTTP Basic Auth format:**
     * ```
     * Authorization: Basic base64(email:password)
     * ```
     *
     * @param email The user's email
     * @param password The user's password
     * @return The complete Authorization header value
     */
    private String createBasicAuthHeader(String email, String password) {
        String credentials = email + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encodedCredentials;
    }

    /**
     * Starts the CLI application and displays main menu.
     */
    public void start() {
        printWelcomeBanner();

        // Check server connectivity before proceeding
        if (!checkServerConnection()) {
            System.err.println("ERROR: Cannot connect to server at " + API_BASE_URL);
            System.err.println("Please ensure DealManagementApp is running:");
            System.err.println("  mvn exec:java -Dexec.mainClass=\"com.chapman.edu.commissions.app.DealManagementApp\"");
            return;
        }

        System.out.println("Successfully connected to server!");
        System.out.println();

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
     * Checks if the server is reachable.
     *
     * @return true if server responds, false otherwise
     */
    private boolean checkServerConnection() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + DEALS_ENDPOINT))
                    .header("Authorization", authHeaderValue)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Prints welcome banner.
     */
    private void printWelcomeBanner() {
        clearScreen();
        System.out.println(DIVIDER);
        System.out.println("    DEAL MANAGEMENT SYSTEM - REST API CLIENT (CLI)");
        System.out.println(DIVIDER);
        System.out.println("Welcome to the Deal Management CLI Client!");
        System.out.println("This client communicates with the server via REST API.");
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
     * Lists all deals via `GET /api/v1/integration/deals`
     */
    private void listAllDeals() {
        clearScreen();
        System.out.println(DIVIDER);
        System.out.println("                        ALL DEALS");
        System.out.println(DIVIDER);

        try {
            // Send HTTP GET request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + DEALS_ENDPOINT))
                    .header("Authorization", authHeaderValue)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Deserialize JSON to List<Deal>
                List<Deal> deals = gson.fromJson(response.body(), new TypeToken<List<Deal>>(){}.getType());

                if (deals.isEmpty()) {
                    System.out.println("No deals found.");
                    return;
                }

                System.out.printf("Total Deals: %d%n%n", deals.size());

                for (Deal deal : deals) {
                    printDealSummary(deal);
                }
            } else {
                System.err.println("Error: HTTP " + response.statusCode());
                System.err.println(response.body());
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Network error: " + e.getMessage());
        }
    }

    /**
     * Lists deals filtered by status via `GET /api/v1/integration/deals?status=STATUS`
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
            DealStatus.valueOf(statusInput); // Validate status

            // Send HTTP GET request with query parameter
            String url = API_BASE_URL + DEALS_ENDPOINT + "?status=" + statusInput;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", authHeaderValue)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                List<Deal> deals = gson.fromJson(response.body(), new TypeToken<List<Deal>>(){}.getType());

                System.out.println(SEPARATOR);
                System.out.printf("Deals with status '%s': %d%n%n", statusInput, deals.size());

                if (deals.isEmpty()) {
                    System.out.println("No deals found with this status.");
                    return;
                }

                for (Deal deal : deals) {
                    printDealSummary(deal);
                }
            } else {
                System.err.println("Error: HTTP " + response.statusCode());
            }

        } catch (IllegalArgumentException e) {
            System.err.println("Invalid status. Please use: OPEN, WON, LOST, or CANCELLED");
        } catch (IOException | InterruptedException e) {
            System.err.println("Network error: " + e.getMessage());
        }
    }

    /**
     * Views deal details via `GET /api/v1/integration/deals/{id}`
     */
    private void viewDealDetails() {
        clearScreen();
        System.out.println(DIVIDER);
        System.out.println("                      VIEW DEAL DETAILS");
        System.out.println(DIVIDER);
        System.out.print("Enter Deal ID: ");

        String dealId = scanner.nextLine().trim();

        try {
            // Send HTTP GET request
            String url = API_BASE_URL + DEALS_ENDPOINT + "/" + dealId;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", authHeaderValue)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Deal deal = gson.fromJson(response.body(), Deal.class);
                System.out.println(SEPARATOR);
                printDetailedDeal(deal);
            } else if (response.statusCode() == 404) {
                System.err.println("Deal not found with ID: " + dealId);
            } else {
                System.err.println("Error: HTTP " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Network error: " + e.getMessage());
        }
    }

    /**
     * Creates a new deal via `POST /api/v1/integration/deals`
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

                System.out.print("  Price: ");
                BigDecimal price = new BigDecimal(scanner.nextLine().trim());

                products.add(new DealProduct(productId, productName, quantity, price));

                System.out.print("\nAdd another product? (y/n): ");
                String response = scanner.nextLine().trim().toLowerCase();
                addingProducts = response.equals("y") || response.equals("yes");
            }

            // Create deal object
            Deal deal = new Deal();
            deal.setTitle(title);
            deal.setSalesRepId(salesRepId);
            deal.setStatus(status);
            deal.setProducts(products);

            // Serialize to JSON
            String jsonBody = gson.toJson(deal);

            // Send HTTP POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + DEALS_ENDPOINT))
                    .header("Content-Type", "application/json")
                    .header("Authorization", authHeaderValue)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() == 201 || httpResponse.statusCode() == 200) {
                Deal createdDeal = gson.fromJson(httpResponse.body(), Deal.class);
                System.out.println(SEPARATOR);
                System.out.println("Deal created successfully!");
                System.out.println(SEPARATOR);
                printDetailedDeal(createdDeal);
            } else {
                System.err.println("Error creating deal: HTTP " + httpResponse.statusCode());
                System.err.println(httpResponse.body());
            }

        } catch (NumberFormatException e) {
            System.err.println("Invalid number format. Please enter valid numbers for quantity and price.");
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid status. Please use: OPEN, WON, LOST, or CANCELLED");
        } catch (IOException | InterruptedException e) {
            System.err.println("Network error: " + e.getMessage());
        }
    }

    /**
     * Closes a deal via `POST /api/v1/integration/deals/{id}/close`
     */
    private void closeDeal() {
        clearScreen();
        System.out.println(DIVIDER);
        System.out.println("                        CLOSE DEAL");
        System.out.println(DIVIDER);
        System.out.print("Enter Deal ID to close: ");

        String dealId = scanner.nextLine().trim();

        try {
            // Send HTTP POST request
            String url = API_BASE_URL + DEALS_ENDPOINT + "/" + dealId + "/close";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", authHeaderValue)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Deal closedDeal = gson.fromJson(response.body(), Deal.class);
                System.out.println(SEPARATOR);
                System.out.println("Deal closed successfully!");
                System.out.println(SEPARATOR);
                printDetailedDeal(closedDeal);
            } else {
                System.err.println("Error closing deal: HTTP " + response.statusCode());
                System.err.println(response.body());
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Network error: " + e.getMessage());
        }
    }

    /**
     * Deletes a deal via `DELETE /api/v1/integration/deals/{id}`
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
            // Send HTTP DELETE request
            String url = API_BASE_URL + DEALS_ENDPOINT + "/" + dealId;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", authHeaderValue)
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 204 || response.statusCode() == 200) {
                System.out.println(SEPARATOR);
                System.out.println("Deal deleted successfully!");
            } else {
                System.err.println("Error deleting deal: HTTP " + response.statusCode());
                System.err.println(response.body());
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Network error: " + e.getMessage());
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
            // Fetch all deals via HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + DEALS_ENDPOINT))
                    .header("Authorization", authHeaderValue)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                List<Deal> allDeals = gson.fromJson(response.body(), new TypeToken<List<Deal>>(){}.getType());

                // Calculate statistics
                long totalDeals = allDeals.size();
                long openDeals = allDeals.stream().filter(d -> d.getStatus() == DealStatus.OPEN).count();
                long wonDeals = allDeals.stream().filter(d -> d.getStatus() == DealStatus.WON).count();
                long lostDeals = allDeals.stream().filter(d -> d.getStatus() == DealStatus.LOST).count();
                long cancelledDeals = allDeals.stream().filter(d -> d.getStatus() == DealStatus.CANCELLED).count();

                BigDecimal totalValue = allDeals.stream()
                        .map(Deal::calculateTotalValue)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal wonValue = allDeals.stream()
                        .filter(d -> d.getStatus() == DealStatus.WON)
                        .map(Deal::calculateTotalValue)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                double winRate = (totalDeals > 0 && (wonDeals + lostDeals) > 0)
                        ? (wonDeals * 100.0 / (wonDeals + lostDeals))
                        : 0.0;

                System.out.println("DEAL STATISTICS:");
                System.out.println(SEPARATOR);
                System.out.printf("  Total Deals:     %d%n", totalDeals);
                System.out.printf("  Open Deals:      %d%n", openDeals);
                System.out.printf("  Won Deals:       %d%n", wonDeals);
                System.out.printf("  Lost Deals:      %d%n", lostDeals);
                System.out.printf("  Cancelled Deals: %d%n", cancelledDeals);
                System.out.println(SEPARATOR);
                System.out.printf("  Total Value:     $%,.2f%n", totalValue);
                System.out.printf("  Won Value:       $%,.2f%n", wonValue);
                System.out.println(SEPARATOR);

                if (totalDeals > 0) {
                    System.out.printf("  Win Rate:        %.1f%%%n", winRate);
                    System.out.printf("  Avg Deal Value:  $%,.2f%n",
                            totalValue.divide(BigDecimal.valueOf(totalDeals), 2, BigDecimal.ROUND_HALF_UP));
                }
            } else {
                System.err.println("Error loading dashboard: HTTP " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Network error: " + e.getMessage());
        }
    }

    /**
     * Lists all users via `GET /api/v1/integration/users`
     */
    private void listAllUsers() {
        clearScreen();
        System.out.println(DIVIDER);
        System.out.println("                        ALL USERS");
        System.out.println(DIVIDER);

        try {
            // Send HTTP GET request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + USERS_ENDPOINT))
                    .header("Authorization", authHeaderValue)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                List<User> users = gson.fromJson(response.body(), new TypeToken<List<User>>(){}.getType());

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
            } else {
                System.err.println("Error loading users: HTTP " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Network error: " + e.getMessage());
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
                System.out.printf("    Quantity: %d | Price: $%,.2f | Total: $%,.2f%n",
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
            System.out.print("\033[H\033[2J");
            System.out.flush();
        } catch (Exception e) {
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
        System.out.println("Shutting down Deal Management CLI Client...");
        System.out.println("Thank you for using Deal Management System!");
        System.out.println(DIVIDER);
        scanner.close();
    }
}