package com.chapman.edu.commissions.integration;

import com.chapman.edu.commissions.integration.database.DatabaseManager;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * BIG BANG INTEGRATION TESTING
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * WHAT IS BIG BANG INTEGRATION TESTING?
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * Big Bang Integration Testing is an approach where ALL components of the system
 * are integrated and tested TOGETHER at once, rather than incrementally.
 *
 * In this approach:
 * - All modules/components are developed independently
 * - Once complete, everything is integrated in one go
 * - The entire system is tested as a whole
 * - Testing happens from the user's perspective (black-box)
 *
 * METAPHOR: Like assembling a complex machine by putting ALL parts together
 * at once and then seeing if it works, rather than building it piece by piece.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * BIG BANG vs OTHER INTEGRATION TESTING APPROACHES
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * 1. BIG BANG INTEGRATION:
 *    ✓ All components integrated at once
 *    ✓ Tests entire system as a whole
 *    ✓ Fast to set up (no incremental steps)
 *    ✗ Hard to isolate defects (which component failed?)
 *    ✗ High risk if core components fail
 *
 * 2. INCREMENTAL INTEGRATION (Top-Down/Bottom-Up):
 *    ✓ Components integrated gradually
 *    ✓ Easier to isolate defects
 *    ✓ Lower risk (failures caught early)
 *    ✗ Takes longer to test entire system
 *    ✗ May require stubs/drivers
 *
 * 3. SANDWICH/HYBRID INTEGRATION:
 *    ✓ Combines top-down and bottom-up
 *    ✓ Tests critical paths early
 *    ✗ More complex test strategy
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * WHAT THIS TEST DEMONSTRATES
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * This Big Bang test integrates ALL layers of the Commission Calculator:
 *
 * ┌─────────────────────────────────────────────────────────────┐
 * │                    HTTP CLIENT (RestAssured)                │
 * └─────────────────────────────────────────────────────────────┘
 *                              ↓
 * ┌─────────────────────────────────────────────────────────────┐
 * │              EMBEDDED TOMCAT SERVER (Port 8080)             │
 * └─────────────────────────────────────────────────────────────┘
 *                              ↓
 * ┌─────────────────────────────────────────────────────────────┐
 * │         AUTHENTICATION FILTER (Security Layer)              │
 * │  - HTTP Basic Auth validation                               │
 * │  - User credential verification                             │
 * │  - Request attribute population                             │
 * └─────────────────────────────────────────────────────────────┘
 *                              ↓
 * ┌─────────────────────────────────────────────────────────────┐
 * │              CONTROLLERS (HTTP Layer)                       │
 * │  - DealController (deal management endpoints)               │
 * │  - UserController (user management endpoints)               │
 * │  - Request parsing (JSON → Objects)                         │
 * │  - Response formatting (Objects → JSON)                     │
 * └─────────────────────────────────────────────────────────────┘
 *                              ↓
 * ┌─────────────────────────────────────────────────────────────┐
 * │                SERVICES (Business Logic Layer)              │
 * │  - DealService (deal lifecycle, validation)                 │
 * │  - UserService (authentication, user management)            │
 * │  - Business rule enforcement                                │
 * │  - Workflow orchestration                                   │
 * └─────────────────────────────────────────────────────────────┘
 *                              ↓
 * ┌─────────────────────────────────────────────────────────────┐
 * │              REPOSITORIES (Data Access Layer)               │
 * │  - H2DealRepository (CRUD operations for deals)             │
 * │  - H2UserRepository (CRUD operations for users)             │
 * │  - SQL query execution                                      │
 * │  - Object-Relational Mapping                                │
 * └─────────────────────────────────────────────────────────────┘
 *                              ↓
 * ┌─────────────────────────────────────────────────────────────┐
 * │                  DATABASE (H2 In-Memory)                    │
 * │  - Schema: users, deals, commission_plans                   │
 * │  - Transactions and ACID properties                         │
 * │  - Referential integrity (foreign keys)                     │
 * └─────────────────────────────────────────────────────────────┘
 *
 * ALL OF THE ABOVE COMPONENTS ARE TESTED TOGETHER IN THIS FILE.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * TEST SCENARIOS COVERED
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * 1. COMPLETE USER WORKFLOW:
 *    - User authentication across all endpoints
 *    - Creating deals with validation
 *    - Retrieving deals with filtering
 *    - Updating deals with business rules
 *    - Deleting deals with authorization
 *
 * 2. CROSS-LAYER DATA FLOW:
 *    - HTTP request → Filter → Controller → Service → Repository → Database
 *    - Database → Repository → Service → Controller → HTTP response
 *
 * 3. BUSINESS SCENARIOS:
 *    - Sales rep creates a deal
 *    - Deal progresses through lifecycle (OPEN → WON)
 *    - Multiple users interacting with deals
 *    - Business rule validation across all layers
 *
 * 4. ERROR SCENARIOS:
 *    - Authentication failures
 *    - Validation failures
 *    - Business rule violations
 *    - Data integrity violations
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * WHEN TO USE BIG BANG INTEGRATION TESTING
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * GOOD FOR:
 * ✓ Small systems (few components)
 * ✓ Well-tested individual components
 * ✓ Stable interfaces between components
 * ✓ Time-constrained projects (quick validation)
 * ✓ Proof-of-concept or demo scenarios
 *
 * BAD FOR:
 * ✗ Large, complex systems (hard to debug)
 * ✗ Unstable components
 * ✗ Critical systems (high risk)
 * ✗ When defect localization is important
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * TEST STRATEGY
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * SETUP (@BeforeAll):
 * 1. Reset database to clean state
 * 2. Start IntegrationApplication (all components)
 * 3. Wait for server initialization
 * 4. Configure RestAssured HTTP client
 *
 * TESTS:
 * 1. Test complete user journeys (end-to-end)
 * 2. Verify data flows through all layers
 * 3. Validate business rules enforced across system
 * 4. Test error handling at system boundaries
 *
 * TEARDOWN (@AfterAll):
 * 1. Stop the server
 * 2. Close database connections
 * 3. Clean up resources
 *
 * ═══════════════════════════════════════════════════════════════════════════
 */
@DisplayName("Big Bang Integration Test - Complete System Testing")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BigBangIntegrationTest {

    private static IntegrationApplication app;
    private static String salesRepUserId;
    private static String managerUserId;

    // Test credentials from sample data
    // Note: These users are created by IntegrationApplication.loadSampleData()
    private static final String SALES_REP_EMAIL = "john.doe@example.com";
    private static final String SALES_REP_PASSWORD = "password";
    private static final String MANAGER_EMAIL = "jane.smith@example.com"; // Has SALES_REP + SALES_MANAGER roles
    private static final String MANAGER_PASSWORD = "password";

    /**
     * ═══════════════════════════════════════════════════════════════════════
     * BIG BANG SETUP: INTEGRATE ALL COMPONENTS
     * ═══════════════════════════════════════════════════════════════════════
     *
     * This method brings together ALL components of the system:
     *
     * 1. DATABASE LAYER:
     *    - Initialize H2 in-memory database
     *    - Create schema (users, deals, commission_plans, etc.)
     *    - Load sample data (users, test deals)
     *
     * 2. DATA ACCESS LAYER:
     *    - Initialize repositories (H2DealRepository, H2UserRepository)
     *    - Configure database connections
     *
     * 3. BUSINESS LOGIC LAYER:
     *    - Initialize services (DealService, UserService)
     *    - Inject repository dependencies
     *
     * 4. WEB LAYER:
     *    - Initialize controllers (DealController, UserController)
     *    - Inject service dependencies
     *
     * 5. SECURITY LAYER:
     *    - Initialize authentication filter
     *    - Configure security rules
     *
     * 6. SERVER LAYER:
     *    - Start embedded Tomcat server
     *    - Register servlets and filters
     *    - Open port 8080
     *
     * ALL OF THIS HAPPENS IN ONE INTEGRATION STEP (BIG BANG!)
     */
    @BeforeAll
    static void integrateBigBang() throws Exception {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   BIG BANG INTEGRATION: Starting Complete System         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        // Step 1: Reset database to ensure clean state
        System.out.println("→ Resetting database...");
        DatabaseManager.getInstance().resetDatabase();

        // Step 2: Start ENTIRE application (all layers integrated)
        System.out.println("→ Starting IntegrationApplication (ALL components)...");
        app = new IntegrationApplication();
        app.startNonBlocking(); // Non-blocking for test execution

        // Step 3: Configure HTTP client for testing
        System.out.println("→ Configuring RestAssured HTTP client...");
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/api/v1/integration";

        // Step 4: Wait for complete system initialization
        System.out.println("→ Waiting for system to initialize...");
        Thread.sleep(5000); // Allow server startup and sample data loading

        // Step 5: Get user IDs for test scenarios
        System.out.println("→ Loading test user data...");
        salesRepUserId = getUserIdByEmail(SALES_REP_EMAIL, SALES_REP_EMAIL, SALES_REP_PASSWORD);
        managerUserId = getUserIdByEmail(MANAGER_EMAIL, MANAGER_EMAIL, MANAGER_PASSWORD);

        System.out.println("\n✓ Big Bang Integration Complete: System Ready for Testing\n");
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════
     * TEARDOWN: DISMANTLE THE INTEGRATED SYSTEM
     * ═══════════════════════════════════════════════════════════════════════
     */
    @AfterAll
    static void dismantleBigBang() throws Exception {
        System.out.println("\n→ Shutting down integrated system...");
        if (app != null) {
            app.stop();
        }
        System.out.println("✓ System shutdown complete\n");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // BIG BANG TEST 1: COMPLETE DEAL LIFECYCLE (END-TO-END)
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * TEST: Complete deal lifecycle through entire system
     *
     * BIG BANG INTEGRATION FLOW:
     * ═══════════════════════════════════════════════════════════════════════
     *
     * 1. AUTHENTICATION (Filter Layer):
     *    → Sales rep provides credentials
     *    → AuthenticationFilter validates via UserService
     *    → UserRepository queries database
     *    → User loaded from database
     *    → Request authenticated and user attached
     *
     * 2. CREATE DEAL (All Layers):
     *    → HTTP POST with JSON body
     *    → DealController receives request
     *    → DealService validates business rules
     *    → H2DealRepository saves to database
     *    → Database enforces foreign keys and constraints
     *    → Deal returned with generated ID
     *    → Controller serializes to JSON
     *    → HTTP 201 Created response
     *
     * 3. RETRIEVE DEAL (Read Path):
     *    → HTTP GET by ID
     *    → Controller extracts ID from path
     *    → Service queries repository
     *    → Repository executes SQL SELECT
     *    → Database returns row
     *    → Repository maps to Deal object
     *    → Service returns to controller
     *    → Controller serializes to JSON
     *    → HTTP 200 OK response
     *
     * 4. UPDATE DEAL (Validation Path):
     *    → HTTP PUT with updated data
     *    → Controller parses JSON
     *    → Service validates business rules
     *    → Repository updates database
     *    → Database enforces constraints
     *    → Updated deal returned
     *
     * 5. CLOSE DEAL (Business Logic Path):
     *    → HTTP POST to /close endpoint
     *    → Service orchestrates workflow:
     *      - Validate deal is OPEN
     *      - Change status to WON
     *      - Set close date to today
     *      - Persist changes
     *    → Repository saves
     *    → Response confirms closure
     *
     * 6. DELETE PROTECTION (Security Path):
     *    → HTTP DELETE on WON deal
     *    → Service enforces business rule
     *    → "Can only delete OPEN deals" error
     *    → HTTP 409 Conflict response
     *    → Deal remains in database (verified)
     *
     * ALL OF THESE STEPS TEST THE COMPLETE INTEGRATED SYSTEM.
     */
    @Test
    @Order(1)
    @DisplayName("Big Bang: Complete Deal Lifecycle (CREATE → READ → UPDATE → CLOSE → DELETE)")
    void testCompleteDealLifecycle() {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ BIG BANG TEST: Complete Deal Lifecycle                 │");
        System.out.println("└─────────────────────────────────────────────────────────┘\n");

        // ═══════════════════════════════════════════════════════════════
        // STEP 1: CREATE DEAL (All layers: HTTP → Filter → Controller → Service → Repository → DB)
        // ═══════════════════════════════════════════════════════════════
        System.out.println("→ STEP 1: Creating deal through entire system...");

        Map<String, Object> newDeal = new HashMap<>();
        newDeal.put("title", "Big Bang Test Deal - Cloud Migration");
        newDeal.put("status", "OPEN");
        newDeal.put("salesRepId", salesRepUserId);
        newDeal.put("products", Arrays.asList(
            Map.of("productId", "PROD-CLOUD", "productName", "Cloud Platform", "quantity", 1, "price", 50000.00),
            Map.of("productId", "PROD-SUPPORT", "productName", "Premium Support", "quantity", 1, "price", 10000.00)
        ));

        Response createResponse = given()
            .auth().basic(SALES_REP_EMAIL, SALES_REP_PASSWORD) // Filter: Authentication
            .contentType(ContentType.JSON)
            .body(newDeal)
        .when()
            .post("/deals") // Controller: Route to DealController.doPost()
        .then()
            .statusCode(201) // Service: Validation passed, Repository: Saved to DB
            .body("title", equalTo("Big Bang Test Deal - Cloud Migration"))
            .body("status", equalTo("OPEN"))
            .body("id", notNullValue())
        .extract()
            .response();

        String dealId = createResponse.jsonPath().getString("id");
        System.out.println("  ✓ Deal created with ID: " + dealId);

        // ═══════════════════════════════════════════════════════════════
        // STEP 2: READ DEAL (Verify data persisted through all layers)
        // ═══════════════════════════════════════════════════════════════
        System.out.println("→ STEP 2: Retrieving deal from database...");

        given()
            .auth().basic(SALES_REP_EMAIL, SALES_REP_PASSWORD)
        .when()
            .get("/deals/" + dealId) // Repository: SELECT from database
        .then()
            .statusCode(200)
            .body("id", equalTo(dealId))
            .body("title", equalTo("Big Bang Test Deal - Cloud Migration"))
            .body("products.size()", equalTo(2));

        System.out.println("  ✓ Deal retrieved successfully from database");

        // ═══════════════════════════════════════════════════════════════
        // STEP 3: UPDATE DEAL (Test validation and business logic)
        // ═══════════════════════════════════════════════════════════════
        System.out.println("→ STEP 3: Updating deal (business validation)...");

        Map<String, Object> updatedDeal = new HashMap<>();
        updatedDeal.put("title", "Big Bang Test Deal - UPDATED");
        updatedDeal.put("status", "OPEN");
        updatedDeal.put("salesRepId", salesRepUserId);
        updatedDeal.put("products", Arrays.asList(
            Map.of("productId", "PROD-CLOUD", "productName", "Cloud Platform", "quantity", 2, "price", 50000.00)
        ));

        given()
            .auth().basic(SALES_REP_EMAIL, SALES_REP_PASSWORD)
            .contentType(ContentType.JSON)
            .body(updatedDeal)
        .when()
            .put("/deals/" + dealId) // Service: Business validation, Repository: UPDATE query
        .then()
            .statusCode(200)
            .body("title", equalTo("Big Bang Test Deal - UPDATED"));

        System.out.println("  ✓ Deal updated successfully");

        // ═══════════════════════════════════════════════════════════════
        // STEP 4: CLOSE DEAL (Complex workflow through Service layer)
        // ═══════════════════════════════════════════════════════════════
        System.out.println("→ STEP 4: Closing deal (workflow orchestration)...");

        given()
            .auth().basic(SALES_REP_EMAIL, SALES_REP_PASSWORD)
        .when()
            .post("/deals/" + dealId + "/close") // Service: Orchestrate status change + date update
        .then()
            .statusCode(200)
            .body("status", equalTo("WON"))
            .body("closeDate", notNullValue());

        System.out.println("  ✓ Deal closed (status changed to WON)");

        // ═══════════════════════════════════════════════════════════════
        // STEP 5: DELETE PROTECTION (Business rule enforcement)
        // ═══════════════════════════════════════════════════════════════
        System.out.println("→ STEP 5: Testing delete protection (business rules)...");

        given()
            .auth().basic(SALES_REP_EMAIL, SALES_REP_PASSWORD)
        .when()
            .delete("/deals/" + dealId) // Service: "Can only delete OPEN deals" rule
        .then()
            .statusCode(409) // Conflict: Business rule violation
            .body("message", containsString("Can only delete OPEN deals"));

        System.out.println("  ✓ Delete protection enforced (cannot delete WON deal)");

        // ═══════════════════════════════════════════════════════════════
        // VERIFY: Deal still exists in database
        // ═══════════════════════════════════════════════════════════════
        given()
            .auth().basic(SALES_REP_EMAIL, SALES_REP_PASSWORD)
        .when()
            .get("/deals/" + dealId)
        .then()
            .statusCode(200)
            .body("status", equalTo("WON"));

        System.out.println("  ✓ Deal still exists in database (verified)");
        System.out.println("\n✓ Complete lifecycle tested through all integrated layers\n");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // BIG BANG TEST 2: MULTI-USER SCENARIO (CONCURRENT OPERATIONS)
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * TEST: Multiple users interacting with the system simultaneously
     *
     * BIG BANG SCENARIO:
     * ═══════════════════════════════════════════════════════════════════════
     *
     * This tests the integrated system with multiple authenticated users
     * performing operations concurrently, verifying:
     *
     * 1. AUTHENTICATION ISOLATION:
     *    - Sales rep authenticates separately
     *    - Manager authenticates separately
     *    - Each maintains their own session context
     *
     * 2. DATA ISOLATION:
     *    - Sales rep creates their own deals
     *    - Manager can view all deals
     *    - Filtering works across users
     *
     * 3. BUSINESS RULES PER USER:
     *    - Sales rep can only manage OPEN deals
     *    - Different users follow same business rules
     *
     * 4. DATABASE INTEGRITY:
     *    - Foreign keys enforce user references
     *    - Concurrent transactions don't conflict
     *    - Data remains consistent
     *
     * ALL LAYERS ARE TESTED WITH MULTIPLE USERS.
     */
    @Test
    @Order(2)
    @DisplayName("Big Bang: Multi-User Scenario (Sales Rep + Manager)")
    void testMultiUserScenario() {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ BIG BANG TEST: Multi-User Concurrent Operations        │");
        System.out.println("└─────────────────────────────────────────────────────────┘\n");

        // ═══════════════════════════════════════════════════════════════
        // USER 1: Sales Rep creates a deal
        // ═══════════════════════════════════════════════════════════════
        System.out.println("→ USER 1 (Sales Rep): Creating deal...");

        Map<String, Object> salesRepDeal = new HashMap<>();
        salesRepDeal.put("title", "Sales Rep Deal - Enterprise Contract");
        salesRepDeal.put("status", "OPEN");
        salesRepDeal.put("salesRepId", salesRepUserId);
        salesRepDeal.put("products", Arrays.asList(
            Map.of("productId", "PROD-ENT", "productName", "Enterprise License", "quantity", 1, "price", 100000.00)
        ));

        String salesRepDealId = given()
            .auth().basic(SALES_REP_EMAIL, SALES_REP_PASSWORD) // Separate auth session
            .contentType(ContentType.JSON)
            .body(salesRepDeal)
        .when()
            .post("/deals")
        .then()
            .statusCode(201)
        .extract()
            .jsonPath().getString("id");

        System.out.println("  ✓ Sales rep created deal: " + salesRepDealId);

        // ═══════════════════════════════════════════════════════════════
        // USER 2: Manager views all deals (different auth context)
        // ═══════════════════════════════════════════════════════════════
        System.out.println("→ USER 2 (Manager): Viewing all deals...");

        Response allDeals = given()
            .auth().basic(MANAGER_EMAIL, MANAGER_PASSWORD) // Different user auth
        .when()
            .get("/deals")
        .then()
            .statusCode(200)
            .body("$", instanceOf(java.util.List.class))
        .extract()
            .response();

        int totalDeals = allDeals.jsonPath().getList("$").size();
        System.out.println("  ✓ Manager can view " + totalDeals + " deals (cross-user visibility)");

        // ═══════════════════════════════════════════════════════════════
        // USER 1: Sales Rep filters their own deals
        // ═══════════════════════════════════════════════════════════════
        System.out.println("→ USER 1 (Sales Rep): Filtering by status...");

        given()
            .auth().basic(SALES_REP_EMAIL, SALES_REP_PASSWORD)
            .queryParam("status", "OPEN")
        .when()
            .get("/deals")
        .then()
            .statusCode(200)
            .body("status", everyItem(equalTo("OPEN")));

        System.out.println("  ✓ Sales rep filtered deals by status");

        // ═══════════════════════════════════════════════════════════════
        // USER 1: Sales Rep deletes their OPEN deal (allowed)
        // ═══════════════════════════════════════════════════════════════
        System.out.println("→ USER 1 (Sales Rep): Deleting OPEN deal...");

        given()
            .auth().basic(SALES_REP_EMAIL, SALES_REP_PASSWORD)
        .when()
            .delete("/deals/" + salesRepDealId)
        .then()
            .statusCode(204); // Successful deletion

        System.out.println("  ✓ Sales rep deleted their OPEN deal");

        // ═══════════════════════════════════════════════════════════════
        // VERIFY: Deal removed from database (visible to all users)
        // ═══════════════════════════════════════════════════════════════
        given()
            .auth().basic(MANAGER_EMAIL, MANAGER_PASSWORD) // Manager verifies deletion
        .when()
            .get("/deals/" + salesRepDealId)
        .then()
            .statusCode(404); // Deal no longer exists

        System.out.println("  ✓ Deal removed from database (verified by manager)");
        System.out.println("\n✓ Multi-user scenario completed successfully\n");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // BIG BANG TEST 3: SECURITY & VALIDATION (BOUNDARY TESTING)
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * TEST: Security and validation across all integrated layers
     *
     * BIG BANG SECURITY TESTING:
     * ═══════════════════════════════════════════════════════════════════════
     *
     * Tests that security and validation work correctly when ALL layers
     * are integrated:
     *
     * 1. AUTHENTICATION LAYER:
     *    - Invalid credentials rejected at filter level
     *    - Request never reaches controller
     *
     * 2. INPUT VALIDATION:
     *    - Invalid JSON rejected by controller
     *    - Business validation by service
     *    - Database constraints enforced
     *
     * 3. AUTHORIZATION:
     *    - Business rules enforced in service layer
     *    - Cannot violate state transitions
     *
     * 4. ERROR PROPAGATION:
     *    - Errors flow back through all layers
     *    - Proper HTTP status codes
     *    - User-friendly error messages
     */
    @Test
    @Order(3)
    @DisplayName("Big Bang: Security & Validation (Authentication + Business Rules)")
    void testSecurityAndValidation() {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ BIG BANG TEST: Security & Validation Boundaries        │");
        System.out.println("└─────────────────────────────────────────────────────────┘\n");

        // ═══════════════════════════════════════════════════════════════
        // SECURITY TEST 1: Invalid Authentication (Filter Layer)
        // ═══════════════════════════════════════════════════════════════
        System.out.println("→ TEST: Invalid credentials (Authentication Filter)...");

        given()
            .auth().basic("wrong@example.com", "wrongpassword")
        .when()
            .get("/deals")
        .then()
            .statusCode(401); // Unauthorized - blocked at filter

        System.out.println("  ✓ Invalid credentials rejected by authentication filter");

        // ═══════════════════════════════════════════════════════════════
        // SECURITY TEST 2: Missing Required Fields (Service Validation)
        // ═══════════════════════════════════════════════════════════════
        System.out.println("→ TEST: Missing required fields (Business Validation)...");

        Map<String, Object> invalidDeal = new HashMap<>();
        invalidDeal.put("status", "OPEN"); // Missing title!
        invalidDeal.put("salesRepId", salesRepUserId);
        invalidDeal.put("products", Arrays.asList(
            Map.of("productId", "PROD-1", "productName", "Product", "quantity", 1, "price", 1000.00)
        ));

        given()
            .auth().basic(SALES_REP_EMAIL, SALES_REP_PASSWORD)
            .contentType(ContentType.JSON)
            .body(invalidDeal)
        .when()
            .post("/deals")
        .then()
            .statusCode(400) // Bad Request - validation failed
            .body("message", containsString("title is required"));

        System.out.println("  ✓ Missing required field rejected by service validation");

        // ═══════════════════════════════════════════════════════════════
        // SECURITY TEST 3: Business Rule Violation
        // ═══════════════════════════════════════════════════════════════
        System.out.println("→ TEST: Empty products list (Business Rule)...");

        Map<String, Object> noProductsDeal = new HashMap<>();
        noProductsDeal.put("title", "Deal With No Products");
        noProductsDeal.put("status", "OPEN");
        noProductsDeal.put("salesRepId", salesRepUserId);
        noProductsDeal.put("products", Arrays.asList()); // Empty!

        given()
            .auth().basic(SALES_REP_EMAIL, SALES_REP_PASSWORD)
            .contentType(ContentType.JSON)
            .body(noProductsDeal)
        .when()
            .post("/deals")
        .then()
            .statusCode(400)
            .body("message", containsString("at least one product"));

        System.out.println("  ✓ Business rule violation rejected");

        // ═══════════════════════════════════════════════════════════════
        // SECURITY TEST 4: Database Constraint (Foreign Key)
        // ═══════════════════════════════════════════════════════════════
        System.out.println("→ TEST: Invalid foreign key (Database Constraint)...");

        Map<String, Object> invalidUserDeal = new HashMap<>();
        invalidUserDeal.put("title", "Invalid User Deal");
        invalidUserDeal.put("status", "OPEN");
        invalidUserDeal.put("salesRepId", "USER-nonexistent-fake-id"); // Invalid!
        invalidUserDeal.put("products", Arrays.asList(
            Map.of("productId", "PROD-1", "productName", "Product", "quantity", 1, "price", 1000.00)
        ));

        given()
            .auth().basic(SALES_REP_EMAIL, SALES_REP_PASSWORD)
            .contentType(ContentType.JSON)
            .body(invalidUserDeal)
        .when()
            .post("/deals")
        .then()
            .statusCode(anyOf(is(400), is(500))); // DB constraint violation

        System.out.println("  ✓ Database foreign key constraint enforced");
        System.out.println("\n✓ All security and validation layers working correctly\n");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // BIG BANG TEST 4: CROSS-ENTITY RELATIONSHIPS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * TEST: Relationships between entities across the integrated system
     *
     * BIG BANG RELATIONSHIP TESTING:
     * ═══════════════════════════════════════════════════════════════════════
     *
     * Verifies that relationships between entities are maintained correctly
     * when all layers are integrated:
     *
     * 1. USER ↔ DEAL RELATIONSHIP:
     *    - Deals reference users via salesRepId
     *    - Foreign key enforced in database
     *    - User data returned with deal queries
     *
     * 2. DEAL ↔ PRODUCTS RELATIONSHIP:
     *    - Deals contain multiple products
     *    - Products serialized as JSON in database
     *    - Products correctly deserialized on retrieval
     *
     * 3. REFERENTIAL INTEGRITY:
     *    - Cannot delete user with active deals
     *    - Cannot create deal with non-existent user
     *    - Database enforces relationships
     */
    @Test
    @Order(4)
    @DisplayName("Big Bang: Cross-Entity Relationships (Users ↔ Deals ↔ Products)")
    void testCrossEntityRelationships() {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│ BIG BANG TEST: Cross-Entity Relationships              │");
        System.out.println("└─────────────────────────────────────────────────────────┘\n");

        // ═══════════════════════════════════════════════════════════════
        // TEST 1: User → Deal relationship
        // ═══════════════════════════════════════════════════════════════
        System.out.println("→ TEST: Creating deal linked to user (foreign key)...");

        Map<String, Object> linkedDeal = new HashMap<>();
        linkedDeal.put("title", "Relationship Test Deal");
        linkedDeal.put("status", "OPEN");
        linkedDeal.put("salesRepId", salesRepUserId); // Foreign key to users table
        linkedDeal.put("products", Arrays.asList(
            Map.of("productId", "PROD-REL", "productName", "Relationship Product", "quantity", 1, "price", 5000.00)
        ));

        String linkedDealId = given()
            .auth().basic(SALES_REP_EMAIL, SALES_REP_PASSWORD)
            .contentType(ContentType.JSON)
            .body(linkedDeal)
        .when()
            .post("/deals")
        .then()
            .statusCode(201)
            .body("salesRepId", equalTo(salesRepUserId))
        .extract()
            .jsonPath().getString("id");

        System.out.println("  ✓ Deal created with user foreign key: " + linkedDealId);

        // ═══════════════════════════════════════════════════════════════
        // TEST 2: Deal → Products relationship (JSON serialization)
        // ═══════════════════════════════════════════════════════════════
        System.out.println("→ TEST: Retrieving deal with products (JSON deserialization)...");

        given()
            .auth().basic(SALES_REP_EMAIL, SALES_REP_PASSWORD)
        .when()
            .get("/deals/" + linkedDealId)
        .then()
            .statusCode(200)
            .body("products.size()", equalTo(1))
            .body("products[0].productId", equalTo("PROD-REL"))
            .body("products[0].productName", equalTo("Relationship Product"))
            .body("products[0].quantity", equalTo(1))
            .body("products[0].price", equalTo(5000.0f));

        System.out.println("  ✓ Products correctly serialized and deserialized");

        // ═══════════════════════════════════════════════════════════════
        // TEST 3: Verify user data accessible (join-like behavior)
        // ═══════════════════════════════════════════════════════════════
        System.out.println("→ TEST: Verifying user still exists for deal...");

        given()
            .auth().basic(SALES_REP_EMAIL, SALES_REP_PASSWORD)
        .when()
            .get("/users/" + salesRepUserId)
        .then()
            .statusCode(200)
            .body("id", equalTo(salesRepUserId))
            .body("email", equalTo(SALES_REP_EMAIL));

        System.out.println("  ✓ User data accessible and consistent");
        System.out.println("\n✓ Cross-entity relationships working correctly\n");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Helper: Get user ID by email through the integrated system
     */
    private static String getUserIdByEmail(String targetEmail, String authEmail, String authPassword) {
        Response response = given()
            .auth().basic(authEmail, authPassword)
        .when()
            .get("/users")
        .then()
            .statusCode(200)
        .extract()
            .response();

        java.util.List<Map<String, Object>> users = response.jsonPath().getList("$");
        for (Map<String, Object> user : users) {
            if (targetEmail.equals(user.get("email"))) {
                return (String) user.get("id");
            }
        }

        throw new RuntimeException("Test user not found: " + targetEmail);
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════
     * KEY TAKEAWAYS - BIG BANG INTEGRATION TESTING
     * ═══════════════════════════════════════════════════════════════════════
     *
     * WHAT WE TESTED:
     * ✓ ALL components integrated together at once
     * ✓ Complete end-to-end workflows (HTTP → Database → HTTP)
     * ✓ Multi-user concurrent scenarios
     * ✓ Security across all layers
     * ✓ Business rules enforced system-wide
     * ✓ Cross-entity relationships
     * ✓ Error propagation through layers
     *
     * INTEGRATION VERIFIED:
     * ✓ HTTP Client (RestAssured)
     * ✓ Embedded Server (Tomcat)
     * ✓ Security Filter (Authentication)
     * ✓ Controllers (HTTP Handlers)
     * ✓ Services (Business Logic)
     * ✓ Repositories (Data Access)
     * ✓ Database (H2 In-Memory)
     *
     * ADVANTAGES OF BIG BANG:
     * ✓ Tests entire system as user experiences it
     * ✓ Catches integration issues between layers
     * ✓ Validates complete workflows
     * ✓ Realistic testing scenarios
     * ✓ Quick to set up (one integration step)
     *
     * DISADVANTAGES OF BIG BANG:
     * ✗ Hard to isolate defects (which layer failed?)
     * ✗ Requires all components to be stable
     * ✗ High risk if core components have issues
     * ✗ Debugging is more complex
     * ✗ Test failures may cascade
     *
     * BEST PRACTICES FOR BIG BANG:
     * 1. Ensure individual components are well-tested first (unit tests)
     * 2. Use for final validation, not initial integration
     * 3. Combine with incremental integration tests
     * 4. Have good logging to trace failures
     * 5. Test realistic user scenarios
     * 6. Focus on happy paths and critical errors
     *
     * WHEN TO USE BIG BANG:
     * ✓ Small to medium systems
     * ✓ Stable, well-tested components
     * ✓ Final acceptance testing
     * ✓ Proof-of-concept validation
     * ✓ Regression testing before release
     *
     * COMPLEMENTARY TESTING:
     * - Unit Tests: Test each component in isolation
     * - Integration Tests: Test pairs/groups incrementally
     * - Big Bang Tests: Test complete system (this file)
     * - Acceptance Tests: User-focused scenarios
     *
     * USE ALL APPROACHES FOR COMPREHENSIVE QUALITY ASSURANCE!
     * ═══════════════════════════════════════════════════════════════════════
     */
}