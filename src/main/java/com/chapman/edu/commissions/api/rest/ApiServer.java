package com.chapman.edu.commissions.api.rest;

import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.Dispute;
import com.chapman.edu.commissions.model.User;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

/**
 * Embedded Tomcat server for the Commission Calculator REST API.
 *
 * This class demonstrates several important concepts:
 * - Embedded server pattern: No external web server required
 * - Programmatic servlet configuration: Servlets registered via Java code, not XML
 * - Dependency Injection: Repositories injected into servlets
 * - Single Responsibility: This class only handles server lifecycle
 *
 * The server runs on port 8080 and exposes the following endpoints:
 * - /api/v1/deals - Deal management
 * - /api/v1/users - User management
 * - /api/v1/commission-plans - Commission plan management
 * - /api/v1/disputes - Dispute management
 */
public class ApiServer {

    // Default server port
    private static final int DEFAULT_PORT = 8080;

    // Embedded Tomcat instance
    private final Tomcat tomcat;

    // Repositories for data storage (in-memory for demonstration)
    private final Repository<Deal> dealRepository;
    private final Repository<User> userRepository;
    private final Repository<CommissionPlan> planRepository;
    private final Repository<Dispute> disputeRepository;

    /**
     * Constructor initializes repositories and configures Tomcat.
     */
    public ApiServer() {
        this(DEFAULT_PORT);
    }

    /**
     * Constructor with custom port.
     *
     * @param port The port to run the server on
     */
    public ApiServer(int port) {
        // Initialize in-memory repositories with ID generation strategies
        this.dealRepository = new InMemoryRepository<>(
                "DEAL-",
                Deal::getId,
                Deal::setId
        );

        this.userRepository = new InMemoryRepository<>(
                "USER-",
                User::getId,
                User::setId
        );

        this.planRepository = new InMemoryRepository<>(
                "PLAN-",
                CommissionPlan::getId,
                CommissionPlan::setId
        );

        this.disputeRepository = new InMemoryRepository<>(
                "DISP-",
                Dispute::getId,
                Dispute::setId
        );

        // Initialize Tomcat
        this.tomcat = new Tomcat();
        configureTomcat(port);
    }

    /**
     * Configure the Tomcat server.
     *
     * This method demonstrates:
     * - Programmatic servlet registration
     * - URL pattern mapping
     * - Servlet initialization with dependencies
     *
     * @param port The port to bind to
     */
    private void configureTomcat(int port) {
        // Set Tomcat port
        tomcat.setPort(port);

        // Enable automatic connector creation
        tomcat.getConnector();

        // Create a context for our API
        // The context path is the base URL for all servlets
        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();

        Context context = tomcat.addContext(contextPath, docBase);

        // Register servlets with their URL patterns
        // Pattern: Tomcat.addServlet(context, servletName, servletInstance)
        // Pattern: context.addServletMappingDecoded(urlPattern, servletName)

        // Deal servlet: Handles /api/v1/deals/*
        String dealServletName = "DealServlet";
        Tomcat.addServlet(context, dealServletName, new DealServlet(dealRepository));
        context.addServletMappingDecoded("/api/v1/deals/*", dealServletName);

        // User servlet: Handles /api/v1/users/*
        String userServletName = "UserServlet";
        Tomcat.addServlet(context, userServletName, new UserServlet(userRepository));
        context.addServletMappingDecoded("/api/v1/users/*", userServletName);

        // Commission Plan servlet: Handles /api/v1/commission-plans/*
        String planServletName = "CommissionPlanServlet";
        Tomcat.addServlet(context, planServletName, new CommissionPlanServlet(planRepository));
        context.addServletMappingDecoded("/api/v1/commission-plans/*", planServletName);

        // Dispute servlet: Handles /api/v1/disputes/*
        String disputeServletName = "DisputeServlet";
        Tomcat.addServlet(context, disputeServletName, new DisputeServlet(disputeRepository));
        context.addServletMappingDecoded("/api/v1/disputes/*", disputeServletName);

        System.out.println("✓ Servlets registered successfully");
    }

    /**
     * Start the server.
     *
     * This method starts Tomcat and blocks the current thread to keep the server running.
     *
     * @throws LifecycleException If server fails to start
     */
    public void start() throws LifecycleException {
        System.out.println("Starting Commission Calculator API Server...");
        System.out.println("Port: " + tomcat.getConnector().getPort());

        // Start Tomcat
        tomcat.start();

        System.out.println("✓ Server started successfully!");
        System.out.println("\nAPI Endpoints:");
        System.out.println("  - http://localhost:" + tomcat.getConnector().getPort() + "/api/v1/deals");
        System.out.println("  - http://localhost:" + tomcat.getConnector().getPort() + "/api/v1/users");
        System.out.println("  - http://localhost:" + tomcat.getConnector().getPort() + "/api/v1/commission-plans");
        System.out.println("  - http://localhost:" + tomcat.getConnector().getPort() + "/api/v1/disputes");
        System.out.println("\nPress Ctrl+C to stop the server.");

        // Block the main thread to keep the server running
        tomcat.getServer().await();
    }

    /**
     * Stop the server gracefully.
     *
     * @throws LifecycleException If server fails to stop
     */
    public void stop() throws LifecycleException {
        System.out.println("\nStopping server...");
        tomcat.stop();
        tomcat.destroy();
        System.out.println("✓ Server stopped successfully");
    }

    /**
     * Get the deal repository (useful for testing or seeding data).
     *
     * @return The deal repository
     */
    public Repository<Deal> getDealRepository() {
        return dealRepository;
    }

    /**
     * Get the user repository.
     *
     * @return The user repository
     */
    public Repository<User> getUserRepository() {
        return userRepository;
    }

    /**
     * Get the commission plan repository.
     *
     * @return The commission plan repository
     */
    public Repository<CommissionPlan> getPlanRepository() {
        return planRepository;
    }

    /**
     * Get the dispute repository.
     *
     * @return The dispute repository
     */
    public Repository<Dispute> getDisputeRepository() {
        return disputeRepository;
    }

    /**
     * Main method to run the server.
     *
     * @param args Command line arguments:
     *             - First argument: Port number (optional, default: 8080)
     *             - Additional flags:
     *               --no-sample-data: Skip loading sample data (data is loaded by default)
     */
    public static void main(String[] args) {
        try {
            // Parse command line arguments
            int port = DEFAULT_PORT;
            boolean loadSampleData = true;  // Load by default

            for (String arg : args) {
                // Check for flag to disable sample data
                if ("--no-sample-data".equals(arg)) {
                    loadSampleData = false;
                }
                // Try to parse as port number
                else {
                    try {
                        port = Integer.parseInt(arg);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid argument: " + arg);
                        System.err.println("Usage: java ApiServer [port] [--no-sample-data]");
                        System.err.println("  port: Port number (default: " + DEFAULT_PORT + ")");
                        System.err.println("  --no-sample-data: Start with empty repositories (sample data loaded by default)");
                    }
                }
            }

            // Create server
            ApiServer server = new ApiServer(port);

            // Load sample data by default (unless disabled)
            if (loadSampleData) {
                System.out.println("\n=== Loading Sample Data ===");
                SampleDataLoader dataLoader = new SampleDataLoader(
                        server.getDealRepository(),
                        server.getUserRepository(),
                        server.getPlanRepository(),
                        server.getDisputeRepository()
                );
                dataLoader.loadAllData();
                System.out.println("===========================\n");
            }

            // Add shutdown hook to gracefully stop server
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    server.stop();
                } catch (LifecycleException e) {
                    System.err.println("Error stopping server: " + e.getMessage());
                }
            }));

            // Start server (blocks until server is stopped)
            server.start();

        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}