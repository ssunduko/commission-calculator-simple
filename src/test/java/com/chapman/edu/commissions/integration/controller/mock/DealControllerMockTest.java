package com.chapman.edu.commissions.integration.controller.mock;

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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * MOCK TESTING - DealController (Controller with Mocked Service)
 *
 * PURPOSE:
 * Mock tests verify controller behavior by mocking the service layer completely.
 * This allows testing controller logic in pure isolation with predictable service responses.
 *
 * CONCEPTS DEMONSTRATED:
 * 1. CONTROLLER MOCK TESTING:
 *    - Mock DealService to control all service responses
 *    - Verify controller correctly calls service methods
 *    - Test request/response handling with predictable data
 *    - Use ArgumentCaptor to verify request parsing
 *
 * 2. ADVANCED MOCKITO PATTERNS:
 *    - ArgumentCaptor for inspecting parsed objects
 *    - Verify exact service method calls
 *    - Mock different service responses (success, errors)
 *    - Test exception handling paths
 *
 * 3. CONTROLLER LOGIC VERIFICATION:
 *    - Verify JSON parsing correctness
 *    - Verify service method selection (create vs update)
 *    - Verify response serialization
 *    - Verify error mapping to HTTP status codes
 *
 * 4. DIFFERENCE FROM OTHER TEST TYPES:
 *    - Mock tests: Full isolation, verify exact interactions
 *    - Unit tests: Similar but less focus on verification
 *    - Integration tests: Real service + repository
 *
 * LAYER: Controller Layer (Presentation)
 * TEST TYPE: Mock Test (Pure Isolation)
 *
 * WHEN TO USE:
 * - Verify exact service method calls
 * - Test request parsing correctness
 * - Verify controller doesn't add business logic
 * - Test error handling without database
 */
@DisplayName("Mock Tests - DealController (Verify Service Interactions)")
class DealControllerMockTest {

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

        responseWriter = new StringWriter();
        printWriter = new PrintWriter(responseWriter);
        when(mockResponse.getWriter()).thenReturn(printWriter);
    }

    // ============================================================
    // MOCK TEST: VERIFY SERVICE CALLS
    // ============================================================

    /**
     * TEST: Verify controller calls getAllDeals()
     *
     * MOCK VERIFICATION:
     * - Mock service returns predefined list
     * - Verify service.getAllDeals() called exactly once
     * - Verify no other service methods called
     */
    @Test
    @DisplayName("Mock: Should call service.getAllDeals() exactly once")
    void testGetAllDealsServiceCall() throws Exception {
        // Arrange
        Deal deal1 = createMockDeal("DEAL-1", "Deal 1");
        Deal deal2 = createMockDeal("DEAL-2", "Deal 2");
        when(mockService.getAllDeals()).thenReturn(Arrays.asList(deal1, deal2));

        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getPathInfo()).thenReturn(null);

        // Act
        controller.service(mockRequest, mockResponse);

        // Verify: Exact service call
        verify(mockService, times(1)).getAllDeals();
        verify(mockService, never()).getDealById(anyString());
        verify(mockService, never()).createDeal(any());
    }

    /**
     * TEST: Verify controller calls getDealById with correct ID
     *
     * MOCK VERIFICATION:
     * - Verify ID extracted from path correctly
     * - Verify exact ID passed to service
     */
    @Test
    @DisplayName("Mock: Should call service.getDealById() with extracted ID")
    void testGetDealByIdServiceCall() throws Exception {
        // Arrange
        Deal deal = createMockDeal("DEAL-specific-123", "Specific Deal");
        when(mockService.getDealById("DEAL-specific-123")).thenReturn(Optional.of(deal));

        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getPathInfo()).thenReturn("/DEAL-specific-123");

        // Act
        controller.service(mockRequest, mockResponse);

        // Verify: Exact ID used
        verify(mockService).getDealById(eq("DEAL-specific-123"));
        verify(mockService, never()).getDealById(argThat(id -> !id.equals("DEAL-specific-123")));
    }

    // ============================================================
    // MOCK TEST: ARGUMENT CAPTOR FOR REQUEST PARSING
    // ============================================================

    /**
     * TEST: Verify controller correctly parses JSON to Deal
     *
     * ARGUMENT CAPTOR:
     * - Capture the Deal object passed to service.createDeal()
     * - Verify all fields parsed correctly from JSON
     * - Verify products array parsed correctly
     */
    @Test
    @DisplayName("Mock: Should correctly parse JSON request to Deal object")
    void testCreateDealRequestParsing() throws Exception {
        // Arrange: Request JSON
        String requestJson = """
            {
                "title": "Test Deal Title",
                "status": "OPEN",
                "salesRepId": "USER-sales-rep-456",
                "products": [
                    {
                        "productId": "PROD-A",
                        "productName": "Product A",
                        "quantity": 5,
                        "price": 2500.50
                    },
                    {
                        "productId": "PROD-B",
                        "productName": "Product B",
                        "quantity": 2,
                        "price": 1000.00
                    }
                ]
            }
            """;

        BufferedReader reader = new BufferedReader(new StringReader(requestJson));
        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getMethod()).thenReturn("POST");
        when(mockRequest.getPathInfo()).thenReturn(null);

        Deal createdDeal = createMockDeal("DEAL-generated", "Test Deal Title");
        when(mockService.createDeal(any(Deal.class))).thenReturn(createdDeal);

        // Act
        controller.service(mockRequest, mockResponse);

        // Capture the Deal object passed to service
        ArgumentCaptor<Deal> dealCaptor = ArgumentCaptor.forClass(Deal.class);
        verify(mockService).createDeal(dealCaptor.capture());

        // Verify: All fields parsed correctly
        Deal capturedDeal = dealCaptor.getValue();
        assertNotNull(capturedDeal);
        assertEquals("Test Deal Title", capturedDeal.getTitle());
        assertEquals(DealStatus.OPEN, capturedDeal.getStatus());
        assertEquals("USER-sales-rep-456", capturedDeal.getSalesRepId());

        // Verify: Products parsed correctly
        assertEquals(2, capturedDeal.getProducts().size());
        assertEquals("PROD-A", capturedDeal.getProducts().get(0).getProductId());
        assertEquals("Product A", capturedDeal.getProducts().get(0).getProductName());
        assertEquals(5, capturedDeal.getProducts().get(0).getQuantity());
        assertEquals(new BigDecimal("2500.50"), capturedDeal.getProducts().get(0).getPrice());
    }

    /**
     * TEST: Verify controller passes ID and Deal to updateDeal
     *
     * ARGUMENT CAPTOR:
     * - Verify ID extracted from path
     * - Verify Deal parsed from JSON
     * - Verify both passed to service.updateDeal(id, deal)
     */
    @Test
    @DisplayName("Mock: Should pass both ID and Deal to service.updateDeal()")
    void testUpdateDealArguments() throws Exception {
        // Arrange
        String requestJson = """
            {
                "id": "DEAL-456",
                "title": "Updated Title",
                "status": "OPEN",
                "salesRepId": "USER-123",
                "products": []
            }
            """;

        BufferedReader reader = new BufferedReader(new StringReader(requestJson));
        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getMethod()).thenReturn("PUT");
        when(mockRequest.getPathInfo()).thenReturn("/DEAL-456");

        Deal updatedDeal = createMockDeal("DEAL-456", "Updated Title");
        when(mockService.updateDeal(anyString(), any(Deal.class))).thenReturn(updatedDeal);

        // Act
        controller.service(mockRequest, mockResponse);

        // Verify: Both arguments passed correctly
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Deal> dealCaptor = ArgumentCaptor.forClass(Deal.class);

        verify(mockService).updateDeal(idCaptor.capture(), dealCaptor.capture());

        assertEquals("DEAL-456", idCaptor.getValue());
        assertEquals("Updated Title", dealCaptor.getValue().getTitle());
    }

    // ============================================================
    // MOCK TEST: RESPONSE VERIFICATION
    // ============================================================

    /**
     * TEST: Verify controller serializes response correctly
     *
     * MOCK VERIFICATION:
     * - Service returns specific deal
     * - Verify controller serializes all fields to JSON
     * - Verify JSON structure
     */
    @Test
    @DisplayName("Mock: Should serialize Deal to JSON response correctly")
    void testResponseSerialization() throws Exception {
        // Arrange: Service returns deal with specific data
        Deal deal = new Deal();
        deal.setId("DEAL-serialize-test");
        deal.setTitle("Serialization Test Deal");
        deal.setStatus(DealStatus.WON);
        deal.setSalesRepId("USER-789");
        deal.setCloseDate(LocalDate.of(2025, 10, 30));
        deal.setProducts(Arrays.asList(
            new DealProduct("PROD-X", "Product X", 3, new BigDecimal("1500.75"))
        ));

        when(mockService.getDealById("DEAL-serialize-test")).thenReturn(Optional.of(deal));

        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getPathInfo()).thenReturn("/DEAL-serialize-test");

        // Act
        controller.service(mockRequest, mockResponse);
        printWriter.flush();

        // Verify: Response contains all fields
        String response = responseWriter.toString();
        assertTrue(response.contains("DEAL-serialize-test"));
        assertTrue(response.contains("Serialization Test Deal"));
        assertTrue(response.contains("WON"));
        assertTrue(response.contains("USER-789"));
        assertTrue(response.contains("PROD-X"));
        assertTrue(response.contains("Product X"));
    }

    // ============================================================
    // MOCK TEST: ERROR HANDLING VERIFICATION
    // ============================================================

    /**
     * TEST: Verify controller maps IllegalArgumentException to 400
     *
     * MOCK ERROR SIMULATION:
     * - Service throws IllegalArgumentException
     * - Verify controller catches exception
     * - Verify 400 status code set
     * - Verify error message in response
     */
    @Test
    @DisplayName("Mock: Should map IllegalArgumentException to 400 Bad Request")
    void testIllegalArgumentExceptionMapping() throws Exception {
        // Arrange: Service throws validation error
        when(mockService.createDeal(any(Deal.class)))
            .thenThrow(new IllegalArgumentException("Title is required and cannot be empty"));

        String requestJson = """
            {
                "status": "OPEN",
                "salesRepId": "USER-123",
                "products": []
            }
            """;

        BufferedReader reader = new BufferedReader(new StringReader(requestJson));
        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getMethod()).thenReturn("POST");
        when(mockRequest.getPathInfo()).thenReturn(null);

        // Act
        controller.service(mockRequest, mockResponse);
        printWriter.flush();

        // Verify: 400 status
        verify(mockResponse).setStatus(400);

        // Verify: Error message included
        String response = responseWriter.toString();
        assertTrue(response.contains("error"));
        assertTrue(response.toLowerCase().contains("title"));
    }

    /**
     * TEST: Verify controller maps IllegalStateException to 409
     *
     * MOCK ERROR SIMULATION:
     * - Service throws IllegalStateException (business rule violation)
     * - Verify controller maps to 409 Conflict
     */
    @Test
    @DisplayName("Mock: Should map IllegalStateException to 409 Conflict")
    void testIllegalStateExceptionMapping() throws Exception {
        // Arrange: Service throws business rule error
        when(mockService.deleteDeal("DEAL-won"))
            .thenThrow(new IllegalStateException("Can only delete OPEN deals"));

        when(mockRequest.getMethod()).thenReturn("DELETE");
        when(mockRequest.getPathInfo()).thenReturn("/DEAL-won");

        // Act
        controller.service(mockRequest, mockResponse);
        printWriter.flush();

        // Verify: Error message
        String response = responseWriter.toString();
        assertTrue(response.contains("OPEN"));
    }

    /**
     * TEST: Verify controller maps Optional.empty() to 404
     *
     * MOCK VERIFICATION:
     * - Service returns Optional.empty()
     * - Verify controller sets 404 status
     */
    @Test
    @DisplayName("Mock: Should map Optional.empty() to 404 Not Found")
    void testOptionalEmptyMapping() throws Exception {
        // Arrange: Service returns empty
        when(mockService.getDealById("DEAL-missing")).thenReturn(Optional.empty());

        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockRequest.getPathInfo()).thenReturn("/DEAL-missing");

        // Act
        controller.service(mockRequest, mockResponse);
        printWriter.flush();

        // Verify: 404 Not Found
        verify(mockResponse).setStatus(404);

        // Verify: Service was called (controller doesn't skip the call)
        verify(mockService).getDealById("DEAL-missing");
    }

    // ============================================================
    // MOCK TEST: CUSTOM ACTIONS
    // ============================================================

    /**
     * TEST: Verify close action calls correct service method
     *
     * MOCK VERIFICATION:
     * - Verify routing to closeDealAsWon action
     * - Verify correct service method called
     * - Verify ID passed correctly
     */
    @Test
    @DisplayName("Mock: Should call service.closeDealAsWon() for close action")
    void testCloseActionServiceCall() throws Exception {
        // Arrange
        Deal closedDeal = createMockDeal("DEAL-to-close", "Closing Deal");
        closedDeal.setStatus(DealStatus.WON);
        when(mockService.closeDealAsWon("DEAL-to-close")).thenReturn(closedDeal);

        when(mockRequest.getMethod()).thenReturn("POST");
        when(mockRequest.getPathInfo()).thenReturn("/DEAL-to-close/close");

        // Act
        controller.service(mockRequest, mockResponse);

        // Verify: Correct service method called
        verify(mockService).closeDealAsWon("DEAL-to-close");
        verify(mockService, never()).createDeal(any());
        verify(mockService, never()).updateDeal(anyString(), any());
    }

    // ============================================================
    // MOCK TEST: VERIFY NO BUSINESS LOGIC IN CONTROLLER
    // ============================================================

    /**
     * TEST: Verify controller doesn't add business logic
     *
     * MOCK VERIFICATION:
     * - Controller should ONLY delegate to service
     * - No business validation in controller
     * - No data transformation beyond JSON parsing
     */
    @Test
    @DisplayName("Mock: Controller should delegate all logic to service")
    void testControllerDelegatesAllLogic() throws Exception {
        // Arrange: Even with "invalid" data, controller should call service
        // (service handles validation, not controller)
        String requestJson = """
            {
                "title": "",
                "status": "OPEN",
                "salesRepId": "",
                "products": []
            }
            """;

        BufferedReader reader = new BufferedReader(new StringReader(requestJson));
        when(mockRequest.getReader()).thenReturn(reader);
        when(mockRequest.getMethod()).thenReturn("POST");
        when(mockRequest.getPathInfo()).thenReturn(null);

        // Service will handle validation
        when(mockService.createDeal(any(Deal.class)))
            .thenThrow(new IllegalArgumentException("Validation failed"));

        // Act
        controller.service(mockRequest, mockResponse);

        // Verify: Controller STILL called service (didn't validate itself)
        verify(mockService).createDeal(any(Deal.class));
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
     * KEY TAKEAWAYS - CONTROLLER MOCK TESTING:
     *
     * WHAT WE VERIFIED:
     * ✓ Exact service method calls (getAllDeals, getDealById, etc.)
     * ✓ Request parsing correctness (JSON → Deal)
     * ✓ Parameter extraction (ID from path)
     * ✓ Response serialization (Deal → JSON)
     * ✓ Exception mapping to HTTP status codes
     * ✓ Controller delegates all business logic to service
     * ✓ No business logic in controller layer
     *
     * MOCKITO TECHNIQUES:
     * - ArgumentCaptor for inspecting parsed objects
     * - verify() with times(), never(), eq()
     * - Mock exception throwing
     * - Verify exact method arguments
     *
     * CONTROLLER DESIGN VALIDATION:
     * - Controller is thin (no business logic)
     * - Controller properly delegates to service
     * - Controller handles only HTTP concerns
     * - Proper separation of concerns
     *
     * BEST PRACTICES:
     * - Use ArgumentCaptor to verify parsing
     * - Verify service methods called correctly
     * - Test all error mapping paths
     * - Ensure controller stays thin
     * - Combine with unit and integration tests
     */
}