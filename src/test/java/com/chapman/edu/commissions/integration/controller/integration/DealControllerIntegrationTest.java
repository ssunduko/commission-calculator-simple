package com.chapman.edu.commissions.integration.controller.integration;

import com.chapman.edu.commissions.integration.controller.DealController;
import com.chapman.edu.commissions.integration.database.DatabaseManager;
import com.chapman.edu.commissions.integration.repository.H2DealRepository;
import com.chapman.edu.commissions.integration.repository.H2UserRepository;
import com.chapman.edu.commissions.integration.service.DealService;
import com.chapman.edu.commissions.model.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * INTEGRATION TESTING - DealController (Controller + Service + Repository + Database)
 *
 * PURPOSE:
 * Integration tests verify that the CONTROLLER, SERVICE, and REPOSITORY layers
 * work together correctly. This tests the entire request processing flow from
 * controller through to database, without HTTP networking.
 *
 * CONCEPTS DEMONSTRATED:
 * 1. FULL STACK INTEGRATION (minus HTTP):
 *    - Controller receives servlet requests
 *    - Controller delegates to service
 *    - Service executes business logic
 *    - Repository persists to database
 *    - Controller formats responses
 *    - Verify end-to-end data flow
 *
 * 2. INTEGRATION TEST FOCUS:
 *    - Test request → controller → service → repository → database flow
 *    - Verify business logic + persistence + response formatting work together
 *    - Test error propagation through all layers
 *    - Validate real data transformations
 *
 * 3. LAYERED ARCHITECTURE VALIDATION:
 *    - Controller (HTTP concerns)
 *    - Service (business logic)
 *    - Repository (data access)
 *    - Database (persistence)
 *    - All layers integrated and working
 *
 * 4. DIFFERENCE FROM OTHER TEST TYPES:
 *    - Unit tests: Mock service, test controller only
 *    - Mock tests: Mock service, verify interactions
 *    - Integration tests: Real service + repository + database
 *    - API tests: Full HTTP stack with server
 *
 * LAYER: Controller + Service + Repository + Database
 * TEST TYPE: Integration Test (Multi-layer)
 *
 * WHEN TO USE:
 * - Verify full stack without HTTP overhead
 * - Test business logic + persistence + response formatting
 * - Validate error handling across layers
 * - Test complex workflows end-to-end
 */
@DisplayName("Integration Tests - DealController (Controller + Service + Repository + Database)")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DealControllerIntegrationTest {

    private static DatabaseManager dbManager;
    private static H2DealRepository dealRepository;
    private static H2UserRepository userRepository;
    private static DealService dealService;
    private static DealController controller;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    private StringWriter responseWriter;
    private PrintWriter printWriter;
    private static String testUserId;

    @BeforeAll
    static void setUpDatabase() {
        dbManager = DatabaseManager.getInstance();
        dealRepository = new H2DealRepository(dbManager);
        userRepository = new H2UserRepository(dbManager);
        dealService = new DealService(dealRepository);
        controller = new DealController(dealService);
    }

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Reset database with retry logic to handle concurrent access
        try {
            dbManager.resetDatabase();
        } catch (RuntimeException e) {
            // If reset fails, try one more time after a brief wait
            try {
                Thread.sleep(100);
                dbManager.resetDatabase();
            } catch (Exception retryException) {
                throw new RuntimeException("Failed to reset database after retry", retryException);
            }
        }

        // Create test user
        User testUser = new User();
        testUser.setFirstName("Controller");
        testUser.setLastName("Integration");
        testUser.setEmail("controller.integration@test.com");
        testUser.setPasswordHash("password");
        testUser.setRoles(new HashSet<>(Arrays.asList(UserRole.SALES_REP)));
        testUser.setActive(true);
        testUserId = userRepository.save(testUser).getId();

        // Setup response writer
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        when(mockResponse.getWriter()).thenReturn(printWriter);
    }

    @AfterAll
    static void tearDown() {
        if (dbManager != null) {
            dbManager.close();
        }
    }

    // ============================================================
    // INTEGRATION TEST: CREATE THROUGH ALL LAYERS
    // ============================================================

    /**
     * TEST: POST creates deal through all layers
     *
     * INTEGRATION FLOW:
     * 1. Controller receives POST with JSON
     * 2. Controller parses JSON to Deal
     * 3. Controller calls service.createDeal()
     * 4. Service validates business rules
     * 5. Service calls repository.save()
     * 6. Repository persists to database
     * 7. Repository returns deal with generated ID
     * 8. Service returns to controller
     * 9. Controller serializes to JSON
     * 10. Controller writes response
     *
     * DEMONSTRATES:
     * - Full create operation through all layers
     * - JSON → Object → Validation → Persistence → JSON
     */
    @Test
    @Order(1)
    @DisplayName("Integration: POST should create deal and persist to database")
    void testCreateDealFullStack() throws Exception {
        // Arrange: Request JSON
        String requestJson = String.format("""
            {
                "title": "Integration Create Test",
                "status": "OPEN",
                "salesRepId": "%s",
                "products": [
                    {
                        "productId": "PROD-INT",
                        "productName": "Integration Product",
                        "quantity": 2,
                        "price": 5000.00
                    }
                ]
            }
            """, testUserId);

        BufferedReader reader = new BufferedReader(new StringReader(requestJson));
        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getMethod()).thenReturn("POST");
        when(mockRequest.getPathInfo()).thenReturn(null);

        // Act: Process through controller
        controller.service(mockRequest, mockResponse);
        printWriter.flush();

        // Assert: Response status
        verify(mockResponse).setStatus(201);
        verify(mockResponse).setContentType("application/json");

        // Assert: Response contains deal
        String response = responseWriter.toString();
        assertTrue(response.contains("Integration Create Test"));
        assertTrue(response.contains("DEAL-"));

        // Verify: Deal persisted in database
        var allDeals = dealService.getAllDeals();
        assertEquals(1, allDeals.size());
        assertEquals("Integration Create Test", allDeals.get(0).getTitle());
        assertEquals(2, allDeals.get(0).getProducts().get(0).getQuantity());
    }

    /**
     * TEST: Validation error propagates through layers
     *
     * INTEGRATION FLOW:
     * - Controller parses request
     * - Service validation fails
     * - Exception propagates to controller
     * - Controller returns 400
     * - No data persisted to database
     */
    @Test
    @Order(2)
    @DisplayName("Integration: POST should return 400 and not persist invalid data")
    void testCreateInvalidDeal() throws Exception {
        // Arrange: Invalid JSON (no title)
        String requestJson = String.format("""
            {
                "status": "OPEN",
                "salesRepId": "%s",
                "products": [
                    {
                        "productId": "PROD-1",
                        "productName": "Product",
                        "quantity": 1,
                        "price": 1000.00
                    }
                ]
            }
            """, testUserId);

        BufferedReader reader = new BufferedReader(new StringReader(requestJson));
        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getMethod()).thenReturn("POST");
        when(mockRequest.getPathInfo()).thenReturn(null);

        // Act
        controller.service(mockRequest, mockResponse);
        printWriter.flush();

        // Assert: Error response
        verify(mockResponse).setStatus(400);

        // Verify: Nothing persisted
        assertEquals(0, dealService.getAllDeals().size());
    }

    // ============================================================
    // INTEGRATION TEST: READ THROUGH ALL LAYERS
    // ============================================================

    /**
     * TEST: GET retrieves persisted data
     *
     * INTEGRATION FLOW:
     * - Pre-populate database
     * - Controller receives GET request
     * - Service queries repository
     * - Repository queries database
     * - Data flows back through layers
     * - Controller serializes to JSON
     */
    @Test
    @Order(3)
    @DisplayName("Integration: GET should retrieve persisted deals")
    void testGetAllDealsFullStack() throws Exception {
        // Arrange: Create deals in database
        createTestDeal("Deal 1");
        createTestDeal("Deal 2");

        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getPathInfo()).thenReturn(null);

        // Act
        controller.service(mockRequest, mockResponse);
        printWriter.flush();

        // Assert: Response status
        verify(mockResponse).setStatus(200);

        // Assert: Response contains both deals
        String response = responseWriter.toString();
        assertTrue(response.contains("Deal 1"));
        assertTrue(response.contains("Deal 2"));
    }

    /**
     * TEST: GET by ID retrieves specific deal
     */
    @Test
    @Order(4)
    @DisplayName("Integration: GET /{id} should retrieve specific deal from database")
    void testGetDealByIdFullStack() throws Exception {
        // Arrange: Create deal
        Deal created = createTestDeal("Specific Integration Deal");

        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getPathInfo()).thenReturn("/" + created.getId());

        // Act
        controller.service(mockRequest, mockResponse);
        printWriter.flush();

        // Assert: Correct deal returned
        verify(mockResponse).setStatus(200);
        String response = responseWriter.toString();
        assertTrue(response.contains("Specific Integration Deal"));
        assertTrue(response.contains(created.getId()));
    }

    /**
     * TEST: GET non-existent returns 404
     */
    @Test
    @Order(5)
    @DisplayName("Integration: GET /{id} should return 404 for non-existent deal")
    void testGetNonExistentDeal() throws Exception {
        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getPathInfo()).thenReturn("/DEAL-nonexistent");

        controller.service(mockRequest, mockResponse);
        printWriter.flush();

        verify(mockResponse).setStatus(404);
    }

    // ============================================================
    // INTEGRATION TEST: UPDATE THROUGH ALL LAYERS
    // ============================================================

    /**
     * TEST: PUT updates persisted data
     *
     * INTEGRATION FLOW:
     * - Create deal in database
     * - Send PUT with updated data
     * - Controller → Service → Repository → Database
     * - Verify database reflects update
     */
    @Test
    @Order(6)
    @DisplayName("Integration: PUT should update deal in database")
    void testUpdateDealFullStack() throws Exception {
        // Arrange: Create existing deal
        Deal existing = createTestDeal("Original Title");

        // Update JSON
        String updateJson = String.format("""
            {
                "id": "%s",
                "title": "Updated Integration Title",
                "status": "OPEN",
                "salesRepId": "%s",
                "products": [
                    {
                        "productId": "PROD-UPD",
                        "productName": "Updated Product",
                        "quantity": 3,
                        "price": 3000.00
                    }
                ]
            }
            """, existing.getId(), testUserId);

        BufferedReader reader = new BufferedReader(new StringReader(updateJson));
        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getMethod()).thenReturn("PUT");
        when(mockRequest.getPathInfo()).thenReturn("/" + existing.getId());

        // Act
        controller.service(mockRequest, mockResponse);
        printWriter.flush();

        // Assert: Response status
        verify(mockResponse).setStatus(200);

        // Verify: Update persisted in database
        Deal retrieved = dealService.getDealById(existing.getId()).orElseThrow();
        assertEquals("Updated Integration Title", retrieved.getTitle());
        assertEquals("PROD-UPD", retrieved.getProducts().get(0).getProductId());
        assertEquals(3, retrieved.getProducts().get(0).getQuantity());
    }

    // ============================================================
    // INTEGRATION TEST: DELETE WITH BUSINESS RULES
    // ============================================================

    /**
     * TEST: DELETE removes from database
     *
     * INTEGRATION FLOW:
     * - Create OPEN deal
     * - DELETE request
     * - Service validates (OPEN only)
     * - Repository deletes
     * - Verify removal from database
     */
    @Test
    @Order(7)
    @DisplayName("Integration: DELETE should remove OPEN deal from database")
    void testDeleteDealFullStack() throws Exception {
        // Arrange: Create OPEN deal
        Deal deal = createTestDeal("To Be Deleted");
        String dealId = deal.getId();

        when(mockRequest.getMethod()).thenReturn("DELETE");
        when(mockRequest.getPathInfo()).thenReturn("/" + dealId);

        // Act
        controller.service(mockRequest, mockResponse);

        // Assert: 204 No Content
        verify(mockResponse).setStatus(204);

        // Verify: Removed from database
        assertFalse(dealService.getDealById(dealId).isPresent());
    }

    /**
     * TEST: DELETE WON deal returns 409
     *
     * INTEGRATION FLOW:
     * - Create and close deal
     * - DELETE request
     * - Service validates and rejects
     * - Controller returns 409
     * - Deal remains in database
     */
    @Test
    @Order(8)
    @DisplayName("Integration: DELETE should return 409 for WON deal")
    void testDeleteWonDealFullStack() throws Exception {
        // Arrange: Create and close deal
        Deal deal = createTestDeal("Won Deal");
        dealService.closeDealAsWon(deal.getId());

        when(mockRequest.getMethod()).thenReturn("DELETE");
        when(mockRequest.getPathInfo()).thenReturn("/" + deal.getId());

        // Act
        controller.service(mockRequest, mockResponse);
        printWriter.flush();

        // Assert: Conflict status
        verify(mockResponse).setStatus(409);

        // Verify: Still in database
        assertTrue(dealService.getDealById(deal.getId()).isPresent());
    }

    // ============================================================
    // INTEGRATION TEST: COMPLEX WORKFLOWS
    // ============================================================

    /**
     * TEST: COMPLEX WORKFLOWS
     * INTEGRATION FLOW:
     * - Create OPEN deal
     * - POST to /close action
     * - Service orchestrates state change
     * - Repository persists
     * - Verify database reflects WON status
     */
    @Test
    @Order(9)
    @DisplayName("Integration: POST /{id}/close should close deal and persist status")
    void testCloseDealFullStack() throws Exception {
        // Arrange: Create OPEN deal
        Deal deal = createTestDeal("To Close");

        when(mockRequest.getMethod()).thenReturn("POST");
        when(mockRequest.getPathInfo()).thenReturn("/" + deal.getId() + "/close");

        // Act
        controller.service(mockRequest, mockResponse);
        printWriter.flush();

        // Assert: Response shows WON
        verify(mockResponse).setStatus(200);
        String response = responseWriter.toString();
        assertTrue(response.contains("WON"));

        // Verify: Status updated in database
        Deal retrieved = dealService.getDealById(deal.getId()).orElseThrow();
        assertEquals(DealStatus.WON, retrieved.getStatus());
        assertNotNull(retrieved.getCloseDate());
        assertEquals(LocalDate.now(), retrieved.getCloseDate());
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    private Deal createTestDeal(String title) {
        Deal deal = new Deal();
        deal.setTitle(title);
        deal.setStatus(DealStatus.OPEN);
        deal.setSalesRepId(testUserId);
        deal.setProducts(Arrays.asList(
            new DealProduct("PROD-TEST", "Test Product", 1, new BigDecimal("1000"))
        ));
        deal.setCreatedDate(LocalDate.now());
        deal.setLastModifiedDate(LocalDate.now());
        return dealService.createDeal(deal);
    }

    /**
     * KEY TAKEAWAYS - CONTROLLER INTEGRATION TESTING:
     *
     * WHAT WE TESTED:
     * ✓ Full stack: Controller → Service → Repository → Database
     * ✓ Request parsing → Business logic → Persistence → Response
     * ✓ JSON deserialization and serialization
     * ✓ Business validation across layers
     * ✓ Error propagation through layers
     * ✓ Database persistence verification
     * ✓ Complex workflows (close deal)
     * ✓ HTTP status codes for all scenarios
     *
     * INTEGRATION PATTERN:
     * - Controller handles HTTP (without actual HTTP server)
     * - Service handles business logic
     * - Repository handles persistence
     * - Database stores state
     * - Tests verify ALL layers work together
     *
     * DIFFERENCE FROM OTHER TESTS:
     * - Unit: Controller only, mocked service
     * - Mock: Controller only, verify service calls
     * - Integration: Controller + Service + Repository + DB
     * - API: Full HTTP stack with server + networking
     *
     * VALUE OF INTEGRATION TESTS:
     * - Catch integration bugs between layers
     * - Verify end-to-end data flow
     * - Test without HTTP overhead (faster than API tests)
     * - Validate business logic + persistence together
     * - Ensure layers communicate correctly
     *
     * BEST PRACTICES:
     * - Test full request/response cycle
     * - Verify database state after operations
     * - Test error propagation
     * - Test complex workflows
     * - Use real service and repository
     * - Combine with unit, mock, and API tests
     */
}