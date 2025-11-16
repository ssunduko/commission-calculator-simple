package com.chapman.edu.commissions.integration;

import com.chapman.edu.commissions.integration.controller.DealController;
import com.chapman.edu.commissions.integration.controller.UserController;
import com.chapman.edu.commissions.integration.database.DatabaseManager;
import com.chapman.edu.commissions.integration.repository.H2DealRepository;
import com.chapman.edu.commissions.integration.repository.H2UserRepository;
import com.chapman.edu.commissions.integration.security.AuthenticationFilter;
import com.chapman.edu.commissions.integration.service.DealService;
import com.chapman.edu.commissions.integration.service.UserService;
import com.chapman.edu.commissions.integration.servlet.SwaggerServlet;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.h2.server.web.JakartaWebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

/**
 * IntegrationApplication - Main entry point for the Commission Calculator Integration App.
 *
 * This class demonstrates:
 * - Application bootstrapping and dependency injection (manual DI)
 * - Embedded Tomcat server configuration
 * - Servlet registration and URL mapping
 * - Filter configuration for authentication
 * - H2 database console setup
 * - Swagger UI integration
 * - Sample data initialization
 *
 * Architecture:
 * This application follows a Layered MVC architecture:
 *
 * 1. Presentation Layer (Controller/View):
 *    - DealController (Servlet) - Handles HTTP requests/responses
 *    - JSON serialization/deserialization for API responses
 *
 * 2. Business Logic Layer (Service):
 *    - DealService - Business rules, validation, complex operations
 *    - UserService - User management and authentication logic
 *
 * 3. Data Access Layer (Repository):
 *    - H2DealRepository - JDBC-based persistence for deals
 *    - H2UserRepository - JDBC-based persistence for users
 *
 * 4. Database Layer:
 *    - H2 embedded database with file persistence
 *    - DatabaseManager - Connection management and schema initialization
 *
 * 5. Security Layer:
 *    - AuthenticationFilter - HTTP Basic Auth implementation
 *
 * Features:
 * - RESTful API endpoints for Deal and User management
 * - H2 Database Console at http://localhost:8080/h2-console
 * - Swagger UI at http://localhost:8080/swagger-ui/
 * - HTTP Basic Authentication for API endpoints
 * - Sample data for testing
 *
 * @author Sergey L. Sundukovskiy
 * @version 1.0
 */
public class IntegrationApplication {

    private static final Logger logger = LoggerFactory.getLogger(IntegrationApplication.class);
    private static final int PORT = 8080;

    private Tomcat tomcat;
    private DatabaseManager dbManager;

    /**
     * Main method - application entry point.
     */
    public static void main(String[] args) {
        IntegrationApplication app = new IntegrationApplication();
        try {
            app.start();
        } catch (Exception e) {
            logger.error("Failed to start application", e);
            System.exit(1);
        }
    }

    /**
     * Starts the application.
     * Demonstrates the complete application lifecycle.
     * Blocks until server shutdown (suitable for main application).
     */
    public void start() throws LifecycleException {
        startServer(true); // blocking mode
    }

    /**
     * Starts the application without blocking.
     * Suitable for testing where you need the server running but want to continue execution.
     */
    public void startNonBlocking() throws LifecycleException {
        startServer(false); // non-blocking mode
    }

    /**
     * Internal method to start the server with optional blocking.
     *
     * @param block if true, blocks until server shutdown; if false, returns immediately
     */
    private void startServer(boolean block) throws LifecycleException {
        logger.info("Starting Commission Calculator Integration Application...");

        // Step 1: Initialize database
        logger.info("Initializing database...");
        dbManager = DatabaseManager.getInstance();

        // Step 2: Create repositories (Data Access Layer)
        logger.info("Creating repositories...");
        H2DealRepository dealRepository = new H2DealRepository(dbManager);
        H2UserRepository userRepository = new H2UserRepository(dbManager);

        // Step 3: Create services (Business Logic Layer)
        logger.info("Creating services...");
        DealService dealService = new DealService(dealRepository);
        UserService userService = new UserService(userRepository);

        // Step 4: Create controllers (Presentation Layer)
        logger.info("Creating controllers...");
        DealController dealController = new DealController(dealService);
        UserController userController = new UserController(userService);

        // Step 5: Create security filter
        AuthenticationFilter authFilter = new AuthenticationFilter(userService);

        // Step 6: Configure and start Tomcat
        logger.info("Configuring Tomcat server...");
        tomcat = new Tomcat();
        tomcat.setPort(PORT);
        tomcat.getConnector(); // Initialize default connector

        // Create context
        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();
        Context context = tomcat.addContext(contextPath, docBase);

        // Register authentication filter FIRST before servlets
        // This filter protects: API endpoints, H2 Console, and Swagger UI
        // Public endpoints (/, /index.html, static resources) are still accessible
        logger.info("Registering authentication filter...");

        // Use Tomcat-specific API for filter registration
        org.apache.tomcat.util.descriptor.web.FilterDef filterDef = new org.apache.tomcat.util.descriptor.web.FilterDef();
        filterDef.setFilterName("authenticationFilter");
        filterDef.setFilter(authFilter);
        context.addFilterDef(filterDef);

        org.apache.tomcat.util.descriptor.web.FilterMap filterMap = new org.apache.tomcat.util.descriptor.web.FilterMap();
        filterMap.setFilterName("authenticationFilter");
        filterMap.addURLPattern("/*");
        filterMap.setDispatcher(DispatcherType.REQUEST.name());
        filterMap.setDispatcher(DispatcherType.FORWARD.name());
        context.addFilterMap(filterMap);

        logger.info("Authentication filter registered for all endpoints");

        // Register Deal Controller servlet
        Tomcat.addServlet(context, "dealController", dealController);
        context.addServletMappingDecoded("/api/v1/integration/deals/*", "dealController");

        // Register User Controller servlet
        Tomcat.addServlet(context, "userController", userController);
        context.addServletMappingDecoded("/api/v1/integration/users/*", "userController");

        // Register H2 Console servlet (web interface for database)
        // Accessible at: http://localhost:8080/h2-console
        // JDBC URL: jdbc:h2:./data/commissions
        // User: sa, Password: (empty)
        JakartaWebServlet h2Servlet = new JakartaWebServlet();
        Tomcat.addServlet(context, "h2Console", h2Servlet);
        context.addServletMappingDecoded("/h2-console/*", "h2Console");

        // Register Swagger UI servlet
        // Uses CDN-hosted Swagger UI files for simplicity
        SwaggerServlet swaggerServlet = new SwaggerServlet();
        Tomcat.addServlet(context, "swagger", swaggerServlet);
        context.addServletMappingDecoded("/swagger-ui/*", "swagger");
        context.addServletMappingDecoded("/api-docs", "swagger");

        // Step 7: Load sample data
        logger.info("Loading sample data...");
        loadSampleData(userService, dealService);

        // Step 8: Start Tomcat
        tomcat.start();

        logger.info("=".repeat(80));
        logger.info("Commission Calculator Integration Application started successfully!");
        logger.info("=".repeat(80));
        logger.info("Server running on: http://localhost:{}", PORT);
        logger.info("");
        logger.info("Available endpoints:");
        logger.info("  - API:            http://localhost:{}/api/v1/integration/deals", PORT);
        logger.info("  - H2 Console:     http://localhost:{}/h2-console", PORT);
        logger.info("  - Swagger UI:     http://localhost:{}/swagger-ui/", PORT);
        logger.info("");
        logger.info("H2 Database connection:");
        logger.info("  - JDBC URL:       jdbc:h2:./data/commissions");
        logger.info("  - Username:       sa");
        logger.info("  - Password:       (empty)");
        logger.info("");
        logger.info("Test credentials:");
        logger.info("  - Email:          john.doe@example.com");
        logger.info("  - Password:       password");
        logger.info("=".repeat(80));

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        // Keep the server running (only if blocking mode)
        if (block) {
            tomcat.getServer().await();
        }
    }

    /**
     * Stops the server.
     * Useful for testing scenarios where you need to explicitly stop the server.
     */
    public void stop() throws LifecycleException {
        if (tomcat != null) {
            tomcat.stop();
        }
    }

    /**
     * Loads sample data into the database for testing.
     * Creates sample users and deals.
     */
    private void loadSampleData(UserService userService, DealService dealService) {
        try {
            // Create sample users
            User user1 = new User();
            user1.setFirstName("John");
            user1.setLastName("Doe");
            user1.setEmail("john.doe@example.com");
            user1.setPasswordHash("password");
            user1.setRoles(java.util.Set.of(UserRole.SALES_REP));
            user1.setActive(true);

            User user2 = new User();
            user2.setFirstName("Jane");
            user2.setLastName("Smith");
            user2.setEmail("jane.smith@example.com");
            user2.setPasswordHash("password");
            user2.setRoles(java.util.Set.of(UserRole.SALES_REP, UserRole.SALES_MANAGER));
            user2.setActive(true);

            User savedUser1 = userService.createUser(user1);
            User savedUser2 = userService.createUser(user2);

            logger.info("Created sample users: {} and {}", savedUser1.getEmail(), savedUser2.getEmail());

            // Create sample deals
            Deal deal1 = new Deal();
            deal1.setTitle("Enterprise Software License - Acme Corporation");
            deal1.setStatus(DealStatus.OPEN);
            deal1.setSalesRepId(savedUser1.getId());
            deal1.setProducts(List.of(
                    new DealProduct("PROD-Software License", "Software License", 1, new BigDecimal("50000.00")),
                    new DealProduct("PROD-Training Package", "Training Package", 1, new BigDecimal("10000.00"))
            ));

            Deal deal2 = new Deal();
            deal2.setTitle("Cloud Services Contract - TechStart Inc");
            deal2.setStatus(DealStatus.OPEN);
            deal2.setSalesRepId(savedUser2.getId());
            deal2.setProducts(List.of(
                    new DealProduct("PROD-Cloud Hosting", "Cloud Hosting", 12, new BigDecimal("25000.00"))
            ));

            Deal deal3 = new Deal();
            deal3.setTitle("Consulting Services - Global Industries");
            deal3.setStatus(DealStatus.WON);
            deal3.setSalesRepId(savedUser1.getId());
            deal3.setCloseDate(LocalDate.now().minusDays(3));
            deal3.setProducts(List.of(
                    new DealProduct("PROD-Consulting Hours", "Consulting Hours", 1, new BigDecimal("15000.00"))
            ));

            dealService.createDeal(deal1);
            dealService.createDeal(deal2);
            dealService.createDeal(deal3);

            logger.info("Created {} sample deals", 3);

        } catch (Exception e) {
            logger.warn("Sample data may already exist or error occurred: {}", e.getMessage());
        }
    }

    /**
     * Shuts down the application gracefully.
     *
     * NOTE: We do NOT close the DatabaseManager here because it's a singleton
     * shared across the application (and test classes). Closing it would break
     * other tests or application components. DatabaseManager has its own shutdown
     * hook to close when the JVM exits.
     */
    private void shutdown() {
        logger.info("Shutting down Commission Calculator Integration Application...");

        try {
            if (tomcat != null) {
                tomcat.stop();
                tomcat.destroy();
            }

            // Do NOT close dbManager - it's a singleton with its own lifecycle
            // if (dbManager != null) {
            //     dbManager.close();
            // }

            logger.info("Application shut down successfully");
        } catch (Exception e) {
            logger.error("Error during shutdown", e);
        }
    }
}
