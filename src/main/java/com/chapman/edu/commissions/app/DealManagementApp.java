package com.chapman.edu.commissions.app;

import com.chapman.edu.commissions.app.jsp.JSPController;
import com.chapman.edu.commissions.app.listener.JSPFactoryInitializer;
import com.chapman.edu.commissions.app.servlet.WebUIController;
import com.chapman.edu.commissions.integration.controller.DealController;
import com.chapman.edu.commissions.integration.controller.UserController;
import com.chapman.edu.commissions.integration.security.AuthenticationFilter;
import com.chapman.edu.commissions.integration.servlet.SwaggerServlet;
import com.chapman.edu.commissions.integration.database.DatabaseManager;
import com.chapman.edu.commissions.integration.repository.H2DealRepository;
import com.chapman.edu.commissions.integration.repository.H2UserRepository;
import com.chapman.edu.commissions.integration.service.DealService;
import com.chapman.edu.commissions.integration.service.UserService;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.servlets.DefaultServlet;
import org.h2.server.web.JakartaWebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DealManagementApp - Standalone Deal Management Application.
 *
 * This application demonstrates a complete RESTful web application architecture
 * for managing sales deals using a layered architecture pattern with embedded Tomcat.
 *
 * <b>ARCHITECTURAL PATTERNS DEMONSTRATED:</b>
 *
 * 1. LAYERED ARCHITECTURE (Separation of Concerns):
 *    The application is organized into distinct layers, each with specific responsibilities:
 *
 *    a) <b>Presentation Layer</b> (Controller):
 *       - DealController: Handles HTTP requests and responses
 *       - Responsibilities: Request parsing, response formatting, HTTP status codes
 *       - Technology: Jakarta Servlets, JSON serialization
 *       - Principle: Thin controller - no business logic
 *
 *    b) <b>Business Logic Layer</b> (Service):
 *       - DealService: Encapsulates business rules and validation
 *       - Responsibilities: Business validation, complex queries, transaction coordination
 *       - Principle: Single source of truth for business rules
 *       - Examples: "Can only delete OPEN deals", "Deal value must be positive"
 *
 *    c) <b>Data Access Layer</b> (Repository):
 *       - H2DealRepository: Manages data persistence
 *       - Responsibilities: CRUD operations, SQL queries, ORM mapping
 *       - Principle: Abstracts database implementation details
 *       - Pattern: Repository Pattern (interface-based)
 *
 *    d) <b>Database Layer</b>:
 *       - DatabaseManager: Connection pooling and schema management
 *       - Technology: H2 embedded database with JDBC
 *
 * 2. DEPENDENCY INJECTION (Manual):
 *    Each layer receives its dependencies via constructor injection:
 *    - Controller depends on Service (not Repository directly)
 *    - Service depends on Repository interface (not concrete implementation)
 *    - Benefits: Loose coupling, testability, flexibility
 *    - Example: DealController(DealService) -> DealService(Repository<Deal>)
 *
 * 3. REPOSITORY PATTERN:
 *    Data access is abstracted behind Repository<T> interface:
 *    - Provides collection-like interface for domain objects
 *    - Encapsulates queries and data mapping logic
 *    - Allows swapping implementations (H2 -> MySQL, PostgreSQL, MongoDB, etc.)
 *    - Interface: findAll(), findById(), save(), deleteById()
 *
 * 4. MVC PATTERN (Modified for REST):
 *    - Model: Deal, DealProduct, DealStatus (domain objects)
 *    - View: JSON responses (not traditional HTML views)
 *    - Controller: DealController (request routing and response formatting)
 *
 * 5. FACADE PATTERN:
 *    DealManagementApp acts as a facade that:
 *    - Simplifies complex subsystem initialization
 *    - Coordinates multiple components
 *    - Provides unified interface to start/stop application
 *
 * <b>RESTFUL API DESIGN:</b>
 *
 * The application exposes RESTful endpoints following HTTP conventions:
 *
 * Resource-Based URLs:
 *   - Base URL: /api/v1/integration/deals
 *   - Collection: GET /deals (list all)
 *   - Individual: GET /deals/{id} (get specific)
 *   - Create: POST /deals
 *   - Update: PUT /deals/{id}
 *   - Delete: DELETE /deals/{id}
 *   - Action: POST /deals/{id}/close (business operation)
 *
 * HTTP Methods Mapping:
 *   - GET: Retrieve resources (safe, idempotent)
 *   - POST: Create new resources or trigger actions
 *   - PUT: Update existing resources (idempotent)
 *   - DELETE: Remove resources (idempotent)
 *
 * Status Codes:
 *   - 200 OK: Successful GET, PUT
 *   - 201 Created: Successful POST (resource created)
 *   - 204 No Content: Successful DELETE
 *   - 400 Bad Request: Validation error
 *   - 404 Not Found: Resource doesn't exist
 *   - 409 Conflict: Business rule violation
 *   - 500 Internal Server Error: Unexpected error
 *
 * Query Parameters:
 *   - Filtering: GET /deals?status=OPEN
 *   - Filtering: GET /deals?salesRepId=USER-123
 *
 * <b>DATA FLOW EXAMPLE:</b>
 *
 * Creating a new deal:
 * 1. Client sends: POST /api/v1/integration/deals
 *    Body: {"title": "New Deal", "salesRepId": "USER-123", ...}
 *
 * 2. DealController.doPost():
 *    - Parses JSON request body into Deal object
 *    - Calls dealService.createDeal(deal)
 *    - Returns 201 Created with saved Deal as JSON
 *
 * 3. DealService.createDeal():
 *    - Validates business rules (title required, value positive, etc.)
 *    - Sets default status to OPEN if not specified
 *    - Calls dealRepository.save(deal)
 *    - Logs the operation
 *    - Returns saved Deal with generated ID
 *
 * 4. H2DealRepository.save():
 *    - Generates unique ID if new (DEAL-{UUID})
 *    - Executes SQL INSERT with PreparedStatement
 *    - Serializes products list to JSON for storage
 *    - Returns Deal with all database-generated values
 *
 * 5. Response flows back through layers:
 *    Repository -> Service -> Controller -> HTTP Response
 *
 * <b>KEY DESIGN PRINCIPLES:</b>
 *
 * - Single Responsibility Principle (SRP):
 *   Each class has one reason to change (Controller: HTTP, Service: business, Repository: data)
 *
 * - Open/Closed Principle (OCP):
 *   Can extend with new implementations without modifying existing code
 *
 * - Dependency Inversion Principle (DIP):
 *   High-level modules (Service) depend on abstractions (Repository interface), not concrete classes
 *
 * - Separation of Concerns (SoC):
 *   HTTP handling, business logic, and data access are completely separated
 *
 * - Don't Repeat Yourself (DRY):
 *   Business validation exists only in DealService, not duplicated in controller or repository
 *
 * <b>TECHNOLOGY STACK:</b>
 *
 * - Embedded Web Server: Apache Tomcat 10.1
 * - Database: H2 (embedded, file-based)
 * - Data Access: JDBC with PreparedStatements
 * - JSON Processing: Gson
 * - Logging: SLF4J with Logback
 * - Build Tool: Maven
 *
 * <b>RUNNING THE APPLICATION:</b>
 *
 * Command line:
 *   mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.app.DealManagementApp"
 *
 * Available endpoints:
 *   - API Base: http://localhost:8080/api/v1/integration/deals
 *   - H2 Console: http://localhost:8080/h2-console
 *   - Database: jdbc:h2:./data/commissions (user: sa, password: empty)
 *
 * <b>EDUCATIONAL VALUE:</b>
 *
 * This application serves as a comprehensive example of:
 * - Enterprise application architecture
 * - RESTful API design and implementation
 * - Layered architecture pattern
 * - Repository pattern
 * - Dependency injection
 * - SOLID principles in practice
 * - HTTP servlet programming
 * - JDBC and relational database access
 * - JSON serialization/deserialization
 * - Error handling and validation
 * - Logging and debugging
 * - Application lifecycle management
 *
 * @author Commission Calculator Team
 * @version 1.0
 * @see DealController
 * @see DealService
 * @see H2DealRepository
 */
public class DealManagementApp {

    private static final Logger logger = LoggerFactory.getLogger(DealManagementApp.class);

    /**
     * HTTP port for the embedded Tomcat server.
     * Standard development port to avoid conflicts with production systems.
     */
    private static final int PORT = 8080;

    /**
     * Tomcat embedded server instance.
     * Provides servlet container for hosting REST API.
     */
    private Tomcat tomcat;

    /**
     * Database manager for connection pooling and schema initialization.
     * Singleton pattern ensures single database connection pool.
     */
    private DatabaseManager dbManager;

    /**
     * Application entry point.
     *
     * Demonstrates:
     * - Exception handling at application level
     * - Graceful error reporting
     * - Proper exit codes for process management
     *
     * @param args Command line arguments (not used in this application)
     */
    public static void main(String[] args) {
        // Create application instance
        DealManagementApp app = new DealManagementApp();

        try {
            // Start the application
            app.start();
        } catch (Exception e) {
            // Log fatal errors and exit with error code
            logger.error("Failed to start Deal Management Application", e);
            System.exit(1);
        }
    }

    /**
     * Starts the Deal Management Application.
     *
     * This method demonstrates the complete application bootstrap process:
     * 1. Database initialization
     * 2. Dependency injection (manual)
     * 3. Web server configuration
     * 4. Servlet registration
     * 5. Sample data loading
     * 6. Server startup
     *
     * <b>Layered Construction Process:</b>
     *
     * The layers are constructed bottom-up:
     * Database -> Repository -> Service -> Controller -> Server
     *
     * This ensures dependencies are available when needed (Dependency Injection).
     *
     * @throws LifecycleException if server fails to start
     */
    public void start() throws LifecycleException {
        logger.info("=".repeat(80));
        logger.info("Starting Deal Management Application");
        logger.info("=".repeat(80));

        // ========================================================================
        // STEP 1: Initialize Database Layer
        // ========================================================================
        logger.info("[1/7] Initializing database...");

        // DatabaseManager is a singleton that manages:
        // - Database connection pool
        // - Schema creation and migration
        // - Connection lifecycle
        dbManager = DatabaseManager.getInstance();
        logger.info("      Database initialized: H2 embedded database");
        logger.info("      Database location: ./data/commissions.mv.db");

        // ========================================================================
        // STEP 2: Create Data Access Layer (Repository)
        // ========================================================================
        logger.info("[2/7] Creating repositories (Data Access Layer)...");

        // Repository Pattern: Abstracts data access behind interface
        // Benefits:
        // - Hides SQL and JDBC complexity from business layer
        // - Allows swapping database implementations
        // - Provides collection-like interface for domain objects
        H2DealRepository dealRepository = new H2DealRepository(dbManager);
        H2UserRepository userRepository = new H2UserRepository(dbManager);
        logger.info("      Created: H2DealRepository, H2UserRepository");

        // ========================================================================
        // STEP 3: Create Business Logic Layer (Service)
        // ========================================================================
        logger.info("[3/7] Creating services (Business Logic Layer)...");

        // Service Layer: Encapsulates business logic
        // Key responsibilities:
        // - Business validation (e.g., "deal value must be positive")
        // - Business rules (e.g., "can only delete OPEN deals")
        // - Complex queries and filtering
        // - Transaction coordination
        //
        // Note: Service depends on Repository interface, not concrete implementation
        // This is Dependency Inversion Principle in action
        DealService dealService = new DealService(dealRepository);
        UserService userService = new UserService(userRepository);
        logger.info("      Created: DealService, UserService");

        // ========================================================================
        // STEP 4: Create Presentation Layer (Controller)
        // ========================================================================
        logger.info("[4/7] Creating controllers (Presentation Layer)...");

        // Controller Layer: Handles HTTP communication
        // Responsibilities:
        // - Parse HTTP requests
        // - Validate request format (not business rules)
        // - Delegate to service layer
        // - Format HTTP responses
        // - Map exceptions to HTTP status codes
        //
        // Note: Controller has NO business logic, only HTTP handling
        DealController dealController = new DealController(dealService);
        UserController userController = new UserController(userService);
        WebUIController webUIController = new WebUIController(dealService, userService);
        JSPController jspController = new JSPController(dealService, userService);
        logger.info("      Created: DealController, UserController, WebUIController, JSPController");

        // ========================================================================
        // STEP 5: Configure Embedded Web Server
        // ========================================================================
        logger.info("[5/7] Configuring embedded Tomcat server...");

        // Tomcat Configuration:
        // - Embedded mode (no separate server installation needed)
        // - Programmatic configuration (no web.xml required)
        // - Development-friendly (quick start/stop)
        tomcat = new Tomcat();
        tomcat.setPort(PORT);
        tomcat.getConnector(); // Initialize default HTTP connector

        // Create servlet context
        // Context path: "" means root context (http://localhost:8080/)
        // Doc base: Base directory for web application files
        String contextPath = "";
        String docBase = new File("src/main/webapp").getAbsolutePath();
        Context context = tomcat.addContext(contextPath, docBase);

        // Register JSP Factory Initializer Listener
        // This ensures JSP factory is initialized when the servlet context starts
        context.addApplicationListener(JSPFactoryInitializer.class.getName());
        logger.info("      Registered JSP Factory Initializer listener");

        // Register Authentication Filter (SECURITY)
        // This filter secures Swagger UI, H2 Console, and API endpoints
        // Uses HTTP Basic Authentication
        AuthenticationFilter authFilter = new AuthenticationFilter(userService);

        org.apache.tomcat.util.descriptor.web.FilterDef filterDef = new org.apache.tomcat.util.descriptor.web.FilterDef();
        filterDef.setFilterName("authenticationFilter");
        filterDef.setFilter(authFilter);
        context.addFilterDef(filterDef);

        org.apache.tomcat.util.descriptor.web.FilterMap filterMap = new org.apache.tomcat.util.descriptor.web.FilterMap();
        filterMap.setFilterName("authenticationFilter");
        filterMap.addURLPattern("/*");
        context.addFilterMap(filterMap);

        logger.info("      Registered Authentication Filter for all endpoints");
        logger.info("      SECURED: /swagger-ui/*, /h2-console/*, /api/v1/integration/*");
        logger.info("      PUBLIC: /, /index.html, /dashboard.html, /ui/*, /jsp/*");

        // Register Deal Controller Servlet
        // URL pattern: /api/v1/integration/deals/*
        // This makes the controller handle all URLs starting with this pattern
        Tomcat.addServlet(context, "dealController", dealController);
        context.addServletMappingDecoded("/api/v1/integration/deals/*", "dealController");
        logger.info("      Registered servlet: DealController at /api/v1/integration/deals/*");

        // Register User Controller Servlet
        // URL pattern: /api/v1/integration/users/*
        // Provides user list for web UI dropdown
        Tomcat.addServlet(context, "userController", userController);
        context.addServletMappingDecoded("/api/v1/integration/users/*", "userController");
        logger.info("      Registered servlet: UserController at /api/v1/integration/users/*");

        // Register Web UI Controller Servlet (Server-Side Rendering)
        // URL pattern: /ui/*
        // Generates HTML dynamically using PrintWriter (alternative to JavaScript UI)
        Tomcat.addServlet(context, "webUIController", webUIController);
        context.addServletMappingDecoded("/ui/*", "webUIController");
        logger.info("      Registered servlet: WebUIController at /ui/* (PrintWriter-based UI)");

        // Register JSP Controller Servlet (JSP-based Server-Side Rendering)
        // URL pattern: /jsp/*
        // Forwards requests to JSP pages with data populated in request attributes
        Tomcat.addServlet(context, "jspController", jspController);
        context.addServletMappingDecoded("/jsp/*", "jspController");
        logger.info("      Registered servlet: JSPController at /jsp/* (JSP-based UI)");

        // Register JSP Servlet (required for compiling and executing .jsp files)
        // This servlet processes .jsp files and compiles them into servlets
        org.apache.jasper.servlet.JspServlet jspServlet = new org.apache.jasper.servlet.JspServlet();
        org.apache.catalina.Wrapper jspWrapper = Tomcat.addServlet(context, "jsp", jspServlet);

        // Configure JSP servlet parameters
        jspWrapper.addInitParameter("development", "true");
        jspWrapper.addInitParameter("compilerSourceVM", "21");
        jspWrapper.addInitParameter("compilerTargetVM", "21");

        // Set scratch directory for JSP compilation
        File jspScratchDir = new File(System.getProperty("java.io.tmpdir"), "jsp");
        jspScratchDir.mkdirs();
        jspWrapper.addInitParameter("scratchdir", jspScratchDir.getAbsolutePath());
        
        // Additional JSP configuration for embedded Tomcat
        jspWrapper.addInitParameter("classpath", System.getProperty("java.class.path"));
        jspWrapper.addInitParameter("keepgenerated", "true");
        
        // Set context work directory for JSP compilation
        context.setDocBase(new File("src/main/webapp").getAbsolutePath());

        jspWrapper.setLoadOnStartup(3);
        context.addServletMappingDecoded("*.jsp", "jsp");
        logger.info("      Registered JSP servlet for .jsp file processing");
        logger.info("      JSP scratch directory: {}", jspScratchDir.getAbsolutePath());

        // Add lifecycle listener for JSP initialization
        context.addLifecycleListener(new org.apache.catalina.core.JreMemoryLeakPreventionListener());

        // Register H2 Database Console
        // Provides web-based SQL interface for database inspection
        // Useful for debugging and development
        JakartaWebServlet h2Servlet = new JakartaWebServlet();
        Tomcat.addServlet(context, "h2Console", h2Servlet);
        context.addServletMappingDecoded("/h2-console/*", "h2Console");
        logger.info("      Registered servlet: H2 Console at /h2-console/*");

        // Register Static File Servlet for Web UI
        // Serves HTML, CSS, and JavaScript files from src/main/resources/webapp
        // This enables the web-based user interface
        DefaultServlet staticServlet = new DefaultServlet();
        Tomcat.addServlet(context, "staticFiles", staticServlet);

        // Configure static file paths
        context.addServletMappingDecoded("/index.html", "staticFiles");
        context.addServletMappingDecoded("/dashboard.html", "staticFiles");
        context.addServletMappingDecoded("/styles.css", "staticFiles");
        context.addServletMappingDecoded("/app.js", "staticFiles");
        context.addServletMappingDecoded("/", "staticFiles"); // Redirect root to index.html

        // Register Swagger UI servlet
        // Uses CDN-hosted Swagger UI files for simplicity
        SwaggerServlet swaggerServlet = new SwaggerServlet();
        Tomcat.addServlet(context, "swagger", swaggerServlet);
        context.addServletMappingDecoded("/swagger-ui/*", "swagger");
        context.addServletMappingDecoded("/api-docs", "swagger");

        // Set resource base to webapp directory
        File webappDir = new File("src/main/resources/webapp");
        if (webappDir.exists()) {
            context.setResources(new org.apache.catalina.webresources.StandardRoot(context));
            context.getResources().createWebResourceSet(
                    org.apache.catalina.WebResourceRoot.ResourceSetType.RESOURCE_JAR,
                    "/",
                    webappDir.getAbsolutePath(),
                    null,
                    "/"
            );
            logger.info("      Registered static file servlet for Web UI");
        } else {
            logger.warn("      Web UI directory not found: {}", webappDir.getAbsolutePath());
        }

        // Add JSP directory to resources
        File jspDir = new File("src/main/webapp");
        if (jspDir.exists()) {
            if (context.getResources() == null) {
                context.setResources(new org.apache.catalina.webresources.StandardRoot(context));
            }
            context.getResources().createWebResourceSet(
                    org.apache.catalina.WebResourceRoot.ResourceSetType.RESOURCE_JAR,
                    "/",
                    jspDir.getAbsolutePath(),
                    null,
                    "/"
            );
            logger.info("      Registered JSP directory: {}", jspDir.getAbsolutePath());
        } else {
            logger.warn("      JSP directory not found: {}", jspDir.getAbsolutePath());
        }

        // ========================================================================
        // STEP 6: Load Sample Data
        // ========================================================================
        logger.info("[6/7] Loading sample data...");

        // Sample data helps with:
        // - Testing the API without manual data entry
        // - Demonstrating application features
        // - Providing realistic examples
        loadSampleData(userService, dealService);
        logger.info("      Sample data loaded successfully");

        // ========================================================================
        // STEP 7: Start Web Server
        // ========================================================================
        logger.info("[7/7] Starting Tomcat server...");

        // Set system properties for JSP compilation in embedded Tomcat
        System.setProperty("org.apache.jasper.compiler.disablejsr199", "true");
        System.setProperty("tomcat.util.scan.StandardJarScanFilter.jarsToSkip", "");
        System.setProperty("org.apache.jasper.compiler.Parser.STRICT_QUOTE_ESCAPING", "false");

        // Initialize JSP Factory BEFORE Tomcat starts
        // This prevents NullPointerException when JSP pages are accessed immediately after startup
        try {
            // Force JSP factory initialization
            jakarta.servlet.jsp.JspFactory.setDefaultFactory(new org.apache.jasper.runtime.JspFactoryImpl());

            // Initialize JSP runtime context
            Class.forName("org.apache.jasper.compiler.JspRuntimeContext");

            logger.info("      JSP factory initialized successfully before Tomcat startup");
        } catch (Exception e) {
            logger.error("      Failed to initialize JSP factory before startup", e);
            throw new RuntimeException("JSP factory initialization failed", e);
        }

        tomcat.start();

        // ========================================================================
        // Application Started Successfully
        // ========================================================================
        logger.info("=".repeat(80));
        logger.info("Deal Management Application started successfully!");
        logger.info("=".repeat(80));
        logger.info("");
        logger.info("Server Information:");
        logger.info("  Server:           Apache Tomcat 10.1");
        logger.info("  Port:             {}", PORT);
        logger.info("  Base URL:         http://localhost:{}", PORT);
        logger.info("");
        logger.info("API Endpoints:");
        logger.info("  List all deals:   GET    http://localhost:{}/api/v1/integration/deals", PORT);
        logger.info("  Get deal by ID:   GET    http://localhost:{}/api/v1/integration/deals/{{id}}", PORT);
        logger.info("  Create deal:      POST   http://localhost:{}/api/v1/integration/deals", PORT);
        logger.info("  Update deal:      PUT    http://localhost:{}/api/v1/integration/deals/{{id}}", PORT);
        logger.info("  Delete deal:      DELETE http://localhost:{}/api/v1/integration/deals/{{id}}", PORT);
        logger.info("  Close deal:       POST   http://localhost:{}/api/v1/integration/deals/{{id}}/close", PORT);
        logger.info("");
        logger.info("Query Parameters:");
        logger.info("  Filter by status: GET    http://localhost:{}/api/v1/integration/deals?status=OPEN", PORT);
        logger.info("  Filter by rep:    GET    http://localhost:{}/api/v1/integration/deals?salesRepId=USER-123", PORT);
        logger.info("");
        logger.info("Web User Interfaces:");
        logger.info("  JavaScript UI:    http://localhost:{}/index.html", PORT);
        logger.info("  PrintWriter UI:   http://localhost:{}/ui", PORT);
        logger.info("  JSP UI:           http://localhost:{}/jsp/deals", PORT);
        logger.info("  Dashboard:        http://localhost:{}/dashboard.html", PORT);
        logger.info("  CLI Mode:         mvn exec:java -Dexec.mainClass=\"com.chapman.edu.commissions.app.cli.DealManagementCLI\"");
        logger.info("");
        logger.info("Development Tools:");
        logger.info("  H2 Console:       http://localhost:{}/h2-console", PORT);
        logger.info("  Swagger UI:       http://localhost:{}/swagger-ui/", PORT);
        logger.info("");
        logger.info("Database Connection:");
        logger.info("  JDBC URL:         jdbc:h2:./data/commissions");
        logger.info("  Username:         sa");
        logger.info("  Password:         (empty)");
        logger.info("");
        logger.info("Sample Data:");
        logger.info("  Users:            3 users created (1 admin, 2 sales reps)");
        logger.info("  Deals:            3 sample deals created");
        logger.info("  Admin user:       admin@store.com / password");
        logger.info("  Test users:       john.doe@example.com / password, jane.smith@example.com / password");
        logger.info("");
        logger.info("Example cURL Commands:");
        logger.info("  # Get all deals");
        logger.info("  curl http://localhost:{}/api/v1/integration/deals", PORT);
        logger.info("");
        logger.info("  # Get deals by status");
        logger.info("  curl http://localhost:{}/api/v1/integration/deals?status=OPEN", PORT);
        logger.info("");
        logger.info("  # Create new deal");
        logger.info("  curl -X POST http://localhost:{}/api/v1/integration/deals \\", PORT);
        logger.info("       -H 'Content-Type: application/json' \\");
        logger.info("       -d '{{\"title\":\"New Deal\",\"salesRepId\":\"USER-123\",...}}'");
        logger.info("=".repeat(80));

        // Register shutdown hook for graceful cleanup
        // Ensures database connections and server resources are properly released
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        // Block main thread and keep server running
        // Server will run until process is killed or shutdown() is called
        tomcat.getServer().await();
    }

    /**
     * Loads sample data into the database for testing and demonstration.
     *
     * This method demonstrates:
     * - Using service layer for data creation (not repository directly)
     * - Proper model object construction
     * - Handling potential duplicate data errors gracefully
     *
     * Sample Data Created:
     * - 2 Users (sales representatives)
     * - 3 Deals (various statuses: OPEN, WON)
     *
     * @param userService Service for user operations
     * @param dealService Service for deal operations
     */
    private void loadSampleData(UserService userService, DealService dealService) {
        try {
            // ================================================================
            // Create Sample Users
            // ================================================================

            // Admin User: Administrator with all roles
            User adminUser = new User();
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setEmail("admin@store.com");
            adminUser.setPasswordHash("password"); // In production: use bcrypt/argon2
            adminUser.setRoles(java.util.Set.of(UserRole.SYSTEM_ADMIN, UserRole.SALES_MANAGER, UserRole.SALES_REP));
            adminUser.setActive(true);

            // User 1: Basic sales representative
            User user1 = new User();
            user1.setFirstName("John");
            user1.setLastName("Doe");
            user1.setEmail("john.doe@example.com");
            user1.setPasswordHash("password"); // In production: use bcrypt/argon2
            user1.setRoles(java.util.Set.of(UserRole.SALES_REP));
            user1.setActive(true);

            // User 2: Sales representative with manager role
            User user2 = new User();
            user2.setFirstName("Jane");
            user2.setLastName("Smith");
            user2.setEmail("jane.smith@example.com");
            user2.setPasswordHash("password"); // In production: use bcrypt/argon2
            user2.setRoles(java.util.Set.of(UserRole.SALES_REP, UserRole.SALES_MANAGER));
            user2.setActive(true);

            // Save users and capture generated IDs
            User savedAdmin = userService.createUser(adminUser);
            User savedUser1 = userService.createUser(user1);
            User savedUser2 = userService.createUser(user2);

            logger.info("      Created users: {} ({}), {} ({}), {} ({})",
                    savedAdmin.getEmail(), savedAdmin.getId(),
                    savedUser1.getEmail(), savedUser1.getId(),
                    savedUser2.getEmail(), savedUser2.getId());

            // ================================================================
            // Create Sample Deals
            // ================================================================

            // Deal 1: Open enterprise software deal
            Deal deal1 = new Deal();
            deal1.setTitle("Enterprise Software License - Acme Corporation");
            deal1.setStatus(DealStatus.OPEN);
            deal1.setSalesRepId(savedUser1.getId());
            deal1.setProducts(List.of(
                    new DealProduct(
                            "PROD-SW-001",
                            "Enterprise Software License",
                            1,
                            new BigDecimal("50000.00")
                    ),
                    new DealProduct(
                            "PROD-TRN-001",
                            "Professional Training Package",
                            1,
                            new BigDecimal("10000.00")
                    )
            ));
            // Total value: $60,000

            // Deal 2: Open cloud services deal
            Deal deal2 = new Deal();
            deal2.setTitle("Cloud Services Contract - TechStart Inc");
            deal2.setStatus(DealStatus.OPEN);
            deal2.setSalesRepId(savedUser2.getId());
            deal2.setProducts(List.of(
                    new DealProduct(
                            "PROD-CLOUD-001",
                            "Cloud Hosting - Annual Subscription",
                            12,
                            new BigDecimal("25000.00")
                    )
            ));
            // Total value: $300,000

            // Deal 3: Won consulting deal (closed deal example)
            Deal deal3 = new Deal();
            deal3.setTitle("Consulting Services - Global Industries");
            deal3.setStatus(DealStatus.WON);
            deal3.setSalesRepId(savedUser1.getId());
            deal3.setCloseDate(LocalDate.now().minusDays(3)); // Closed 3 days ago
            deal3.setProducts(List.of(
                    new DealProduct(
                            "PROD-CONSULT-001",
                            "Senior Consultant Hours",
                            100,
                            new BigDecimal("150.00")
                    )
            ));
            // Total value: $15,000

            // Save all deals
            Deal savedDeal1 = dealService.createDeal(deal1);
            Deal savedDeal2 = dealService.createDeal(deal2);
            Deal savedDeal3 = dealService.createDeal(deal3);

            logger.info("      Created deals:");
            logger.info("        - {} ({}): ${}",
                    savedDeal1.getTitle(), savedDeal1.getId(), savedDeal1.calculateTotalValue());
            logger.info("        - {} ({}): ${}",
                    savedDeal2.getTitle(), savedDeal2.getId(), savedDeal2.calculateTotalValue());
            logger.info("        - {} ({}): ${}",
                    savedDeal3.getTitle(), savedDeal3.getId(), savedDeal3.calculateTotalValue());

        } catch (Exception e) {
            // Sample data loading is not critical for application startup
            // Log warning if data already exists or other error occurs
            logger.warn("      Sample data may already exist or error occurred: {}", e.getMessage());
        }
    }

    /**
     * Gracefully shuts down the application.
     *
     * This method demonstrates proper resource cleanup:
     * 1. Stop web server (release port)
     * 2. Close database connections (release file locks)
     * 3. Log shutdown status
     *
     * Called by:
     * - Shutdown hook (SIGTERM, Ctrl+C)
     * - Application.stop() method
     *
     * Ensures:
     * - No resource leaks
     * - Database is properly closed (no corruption)
     * - Port is released for other applications
     */
    private void shutdown() {
        logger.info("=".repeat(80));
        logger.info("Shutting down Deal Management Application...");
        logger.info("=".repeat(80));

        try {
            // Stop Tomcat server
            if (tomcat != null) {
                logger.info("  Stopping Tomcat server...");
                tomcat.stop();
                tomcat.destroy();
                logger.info("  Tomcat server stopped");
            }

            // Close database connections
            if (dbManager != null) {
                logger.info("  Closing database connections...");
                dbManager.close();
                logger.info("  Database connections closed");
            }

            logger.info("=".repeat(80));
            logger.info("Deal Management Application shut down successfully");
            logger.info("=".repeat(80));

        } catch (Exception e) {
            logger.error("Error during application shutdown", e);
        }
    }
}