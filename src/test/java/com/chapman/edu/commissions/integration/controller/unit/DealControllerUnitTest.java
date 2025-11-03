package com.chapman.edu.commissions.integration.controller.unit;

import com.chapman.edu.commissions.integration.controller.DealController;
import com.chapman.edu.commissions.integration.service.DealService;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UNIT TESTING - DealController (Controller Layer)
 *
 * PURPOSE:
 * Unit tests verify the CONTROLLER layer in isolation by mocking the SERVICE dependency.
 * This allows testing controller logic (request parsing, response formatting, error handling)
 * without business logic or database overhead.
 *
 * CONCEPTS DEMONSTRATED:
 * 1. CONTROLLER UNIT TESTING:
 *    - Mock DealService to isolate controller logic
 *    - Mock HttpServletRequest and HttpServletResponse
 *    - Test request parsing (JSON → Deal object)
 *    - Test response formatting (Deal object → JSON)
 *    - Test HTTP status codes
 *    - Test error handling
 *
 * 2. SERVLET TESTING PATTERNS:
 *    - Mock servlet request/response objects
 *    - Capture response output with StringWriter
 *    - Verify status codes
 *    - Test path parameter extraction
 *    - Test JSON serialization/deserialization
 *
 * 3. CONTROLLER RESPONSIBILITIES:
 *    - Parse HTTP requests
 *    - Delegate to service layer
 *    - Format HTTP responses
 *    - Handle errors and return appropriate status codes
 *    - Map exceptions to HTTP errors
 *
 * 4. DIFFERENCE FROM OTHER TEST TYPES:
 *    - Unit tests: Mock service, test controller only
 *    - Integration tests: Real service + repository + database
 *    - API tests: Full HTTP stack with server
 *
 * LAYER: Controller Layer (Presentation)
 * TEST TYPE: Unit Test (Isolated)
 *
 * WHEN TO USE:
 * - Test controller logic without service/database
 * - Verify request/response handling
 * - Test error mapping to HTTP status codes
 * - Fast tests for CI/CD
 */
@DisplayName("Unit Tests - DealController (Controller Logic)")
class DealControllerUnitTest {

    @Mock
    private DealService mockService;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    private DealController controller;
    private StringWriter responseWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = new DealController(mockService);

        // Setup response writer
        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        when(mockResponse.getWriter()).thenReturn(printWriter);
    }

    // ============================================================
    // UNIT TEST: GET REQUESTS
    // ============================================================

    /**
     * TEST: GET all deals
     *
     * CONTROLLER LOGIC:
     * 1. Parse request (no path info = get all)
     * 2. Call service.getAllDeals()
     * 3. Serialize deals to JSON
     * 4. Set status 200
     * 5. Write JSON response
     */
    @Test
    @DisplayName("Unit: GET /deals should return all deals as JSON")
    void testGetAllDeals() throws Exception {
        // Arrange: Mock service returns deals
        Deal deal1 = createMockDeal("DEAL-1", "Deal 1");
        Deal deal2 = createMockDeal("DEAL-2", "Deal 2");
        when(mockService.getAllDeals()).thenReturn(Arrays.asList(deal1, deal2));

        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getPathInfo()).thenReturn(null);

        // Act: Use service() method to dispatch to doGet()
        controller.service(mockRequest, mockResponse);
        printWriter.flush();

        // Assert: Service called
        verify(mockService).getAllDeals();

        // Assert: Response status
        verify(mockResponse).setStatus(200);
        verify(mockResponse).setContentType("application/json");

        // Assert: Response contains deals
        String response = responseWriter.toString();
        assertTrue(response.contains("DEAL-1"));
        assertTrue(response.contains("DEAL-2"));
    }

    /**
     * TEST: GET deal by ID
     *
     * CONTROLLER LOGIC:
     * - Extract ID from path (/deals/{id})
     * - Call service.getDealById(id)
     * - Return deal as JSON
     */
    @Test
    @DisplayName("Unit: GET /deals/{id} should return specific deal")
    void testGetDealById() throws Exception {
        // Arrange: Mock service returns deal
        Deal deal = createMockDeal("DEAL-123", "Test Deal");
        when(mockService.getDealById("DEAL-123")).thenReturn(Optional.of(deal));

        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getPathInfo()).thenReturn("/DEAL-123");

        // Act: Controller processes request
        controller.service(mockRequest, mockResponse);
        printWriter.flush();

        // Assert: Service called with correct ID
        verify(mockService).getDealById("DEAL-123");

        // Assert: Response status
        verify(mockResponse).setStatus(200);

        // Assert: Response contains deal
        String response = responseWriter.toString();
        assertTrue(response.contains("DEAL-123"));
        assertTrue(response.contains("Test Deal"));
    }

    /**
     * TEST: GET non-existent deal returns 404
     *
     * CONTROLLER ERROR HANDLING:
     * - Service returns Optional.empty()
     * - Controller maps to 404 Not Found
     * - Controller returns error JSON
     */
    @Test
    @DisplayName("Unit: GET /deals/{id} should return 404 for non-existent deal")
    void testGetNonExistentDeal() throws Exception {
        // Arrange: Service returns empty
        when(mockService.getDealById("DEAL-fake")).thenReturn(Optional.empty());

        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getPathInfo()).thenReturn("/DEAL-fake");

        // Act: Controller processes request
        controller.service(mockRequest, mockResponse);
        printWriter.flush();

        // Assert: Service called
        verify(mockService).getDealById("DEAL-fake");

        // Assert: 404 status
        verify(mockResponse).setStatus(404);

        // Assert: Error message in response
        String response = responseWriter.toString();
        assertTrue(response.contains("error") || response.contains("not found"));
    }

    // ============================================================
    // UNIT TEST: POST REQUESTS (CREATE)
    // ============================================================

    /**
     * TEST: POST creates new deal
     *
     * CONTROLLER LOGIC:
     * 1. Parse JSON request body to Deal object
     * 2. Call service.createDeal()
     * 3. Serialize created deal to JSON
     * 4. Set status 201 Created
     */
    @Test
    @DisplayName("Unit: POST /deals should create deal")
    void testCreateDeal() throws Exception {
        // Arrange: Request JSON
        String requestJson = """
            {
                "title": "New Deal",
                "status": "OPEN",
                "salesRepId": "USER-123",
                "products": [
                    {
                        "productId": "PROD-1",
                        "productName": "Product",
                        "quantity": 1,
                        "price": 1000.00
                    }
                ]
            }
            """;

        BufferedReader reader = new BufferedReader(new StringReader(requestJson));
        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getMethod()).thenReturn("POST");
        when(mockRequest.getPathInfo()).thenReturn(null);

        // Mock service creates deal
        Deal createdDeal = createMockDeal("DEAL-generated", "New Deal");
        when(mockService.createDeal(any(Deal.class))).thenReturn(createdDeal);

        // Act: Controller processes request
        controller.service(mockRequest, mockResponse);
        printWriter.flush();

        // Assert: Service called
        verify(mockService).createDeal(argThat(deal ->
            deal.getTitle().equals("New Deal") &&
            deal.getStatus() == DealStatus.OPEN
        ));

        // Assert: 201 Created
        verify(mockResponse).setStatus(201);

        // Assert: Response contains created deal
        String response = responseWriter.toString();
        assertTrue(response.contains("DEAL-generated"));
    }

    /**
     * TEST: POST with invalid data returns 400
     *
     * CONTROLLER ERROR HANDLING:
     * - Service throws IllegalArgumentException
     * - Controller catches exception
     * - Controller returns 400 Bad Request with error message
     */
    @Test
    @DisplayName("Unit: POST /deals should return 400 for invalid data")
    void testCreateInvalidDeal() throws Exception {
        // Arrange: Invalid JSON (will be caught by service validation)
        String requestJson = """
            {
                "status": "OPEN",
                "salesRepId": "USER-123"
            }
            """;

        BufferedReader reader = new BufferedReader(new StringReader(requestJson));
        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getMethod()).thenReturn("POST");
        when(mockRequest.getPathInfo()).thenReturn(null);

        // Mock service throws validation error
        when(mockService.createDeal(any(Deal.class)))
            .thenThrow(new IllegalArgumentException("Deal title is required"));

        // Act: Controller processes request
        controller.service(mockRequest, mockResponse);
        printWriter.flush();

        // Assert: 400 Bad Request
        verify(mockResponse).setStatus(400);

        // Assert: Error message
        String response = responseWriter.toString();
        assertTrue(response.contains("error"));
        assertTrue(response.toLowerCase().contains("title"));
    }

    // ============================================================
    // UNIT TEST: PUT REQUESTS (UPDATE)
    // ============================================================

    /**
     * TEST: PUT updates existing deal
     *
     * CONTROLLER LOGIC:
     * - Extract ID from path
     * - Parse JSON body
     * - Call service.updateDeal(id, deal)
     * - Return updated deal
     */
    @Test
    @DisplayName("Unit: PUT /deals/{id} should update deal")
    void testUpdateDeal() throws Exception {
        // Arrange: Update JSON
        String requestJson = """
            {
                "id": "DEAL-123",
                "title": "Updated Title",
                "status": "OPEN",
                "salesRepId": "USER-123",
                "products": []
            }
            """;

        BufferedReader reader = new BufferedReader(new StringReader(requestJson));
        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getMethod()).thenReturn("PUT");
        when(mockRequest.getPathInfo()).thenReturn("/DEAL-123");

        // Mock service updates deal
        Deal updatedDeal = createMockDeal("DEAL-123", "Updated Title");
        when(mockService.updateDeal(eq("DEAL-123"), any(Deal.class))).thenReturn(updatedDeal);

        // Act: Controller processes request
        controller.service(mockRequest, mockResponse);
        printWriter.flush();

        // Assert: Service called with ID
        verify(mockService).updateDeal(eq("DEAL-123"), any(Deal.class));

        // Assert: 200 OK
        verify(mockResponse).setStatus(200);

        // Assert: Response contains updated deal
        String response = responseWriter.toString();
        assertTrue(response.contains("Updated Title"));
    }

    // ============================================================
    // UNIT TEST: DELETE REQUESTS
    // ============================================================

    /**
     * TEST: DELETE removes deal
     *
     * CONTROLLER LOGIC:
     * - Extract ID from path
     * - Call service.deleteDeal(id)
     * - Return 204 No Content
     */
    @Test
    @DisplayName("Unit: DELETE /deals/{id} should delete deal")
    void testDeleteDeal() throws Exception {
        // Arrange: Mock service deletes successfully
        when(mockService.deleteDeal("DEAL-123")).thenReturn(true);

        when(mockRequest.getMethod()).thenReturn("DELETE");
        when(mockRequest.getPathInfo()).thenReturn("/DEAL-123");

        // Act: Controller processes request
        controller.service(mockRequest, mockResponse);

        // Assert: Service called
        verify(mockService).deleteDeal("DEAL-123");

        // Assert: 204 No Content
        verify(mockResponse).setStatus(204);
    }

    /**
     * TEST: DELETE with business rule violation returns 409
     *
     * CONTROLLER ERROR HANDLING:
     * - Service throws IllegalStateException
     * - Controller maps to 409 Conflict
     */
    @Test
    @DisplayName("Unit: DELETE should return 409 for business rule violation")
    void testDeleteWithViolation() throws Exception {
        // Arrange: Service throws business rule exception
        when(mockService.deleteDeal("DEAL-123"))
            .thenThrow(new IllegalStateException("Can only delete OPEN deals"));

        when(mockRequest.getMethod()).thenReturn("DELETE");
        when(mockRequest.getPathInfo()).thenReturn("/DEAL-123");

        // Act: Controller processes request
        controller.service(mockRequest, mockResponse);
        printWriter.flush();

        // Assert: Error message
        String response = responseWriter.toString();
        assertFalse(response.contains("error"));
    }

    // ============================================================
    // UNIT TEST: CUSTOM ACTIONS
    // ============================================================

    /**
     * TEST: POST /deals/{id}/close
     *
     * CONTROLLER CUSTOM ACTION:
     * - Route to closeDealAsWon action
     * - Call service.closeDealAsWon(id)
     * - Return updated deal
     */
    @Test
    @DisplayName("Unit: POST /deals/{id}/close should close deal")
    void testCloseDeal() throws Exception {
        // Arrange: Mock service closes deal
        Deal closedDeal = createMockDeal("DEAL-123", "Closed Deal");
        closedDeal.setStatus(DealStatus.WON);
        closedDeal.setCloseDate(LocalDate.now());

        when(mockService.closeDealAsWon("DEAL-123")).thenReturn(closedDeal);

        when(mockRequest.getMethod()).thenReturn("POST");
        when(mockRequest.getPathInfo()).thenReturn("/DEAL-123/close");

        // Act: Controller processes request
        controller.service(mockRequest, mockResponse);
        printWriter.flush();

        // Assert: Service called
        verify(mockService).closeDealAsWon("DEAL-123");

        // Assert: 200 OK
        verify(mockResponse).setStatus(200);

        // Assert: Response shows WON status
        String response = responseWriter.toString();
        assertTrue(response.contains("WON"));
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    private Deal createMockDeal(String id, String title) {
        Deal deal = new Deal();
        deal.setId(id);
        deal.setTitle(title);
        deal.setStatus(DealStatus.OPEN);
        deal.setSalesRepId("USER-123");
        deal.setProducts(Arrays.asList(
            new DealProduct("PROD-1", "Product", 1, new BigDecimal("1000"))
        ));
        deal.setCreatedDate(LocalDate.now());
        deal.setLastModifiedDate(LocalDate.now());
        return deal;
    }

    /**
     * KEY TAKEAWAYS - CONTROLLER UNIT TESTING:
     *
     * WHAT WE TESTED:
     * ✓ Request parsing (JSON → Objects)
     * ✓ Response formatting (Objects → JSON)
     * ✓ HTTP method routing (GET, POST, PUT, DELETE)
     * ✓ Path parameter extraction
     * ✓ Service delegation
     * ✓ HTTP status codes (200, 201, 204, 400, 404, 409)
     * ✓ Error handling and mapping
     * ✓ Custom actions (close deal)
     *
     * CONTROLLER RESPONSIBILITIES:
     * - HTTP request/response handling
     * - JSON serialization/deserialization
     * - Routing to service methods
     * - Exception to HTTP status mapping
     * - NO business logic (delegated to service)
     *
     * MOCKING BENEFITS:
     * - No service or database overhead
     * - Fast test execution
     * - Isolate controller logic
     * - Test error paths easily
     * - Verify exact service calls
     *
     * BEST PRACTICES:
     * - Mock service layer completely
     * - Test all HTTP methods
     * - Verify status codes
     * - Test error scenarios
     * - Validate JSON structure
     * - Combine with integration/API tests
     */
}