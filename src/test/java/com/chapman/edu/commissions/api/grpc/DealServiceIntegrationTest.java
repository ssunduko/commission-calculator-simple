package com.chapman.edu.commissions.api.grpc;

import com.chapman.edu.commissions.api.grpc.proto.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for DealService gRPC API.
 *
 * These tests verify the complete end-to-end behavior of the Deal service,
 * including:
 * - Creating deals
 * - Retrieving deals
 * - Listing deals with filters
 * - Updating deals
 * - Deleting deals
 * - Error handling (NOT_FOUND, INVALID_ARGUMENT)
 *
 * Test Strategy:
 * - Use real gRPC client/server communication
 * - Verify both success and error cases
 * - Test request validation
 * - Test filtering and querying
 * - Verify protobuf message serialization/deserialization
 *
 * Educational Aspects:
 * - Demonstrates JUnit 5 testing patterns
 * - Shows gRPC error handling in tests
 * - Illustrates integration test best practices
 * - Examples of AAA pattern (Arrange, Act, Assert)
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("DealService Integration Tests")
public class DealServiceIntegrationTest extends GrpcServerTestBase {

    /**
     * Test: Create a new deal successfully.
     *
     * Verifies:
     * - Deal is created with auto-generated ID
     * - Request fields are properly saved
     * - Response contains the created deal
     */
    @Test
    @Order(1)
    @DisplayName("Should create a new deal successfully")
    void testCreateDeal() {
        // Arrange: Prepare test data
        String title = "Test Enterprise Deal";
        BigDecimal value = new BigDecimal("50000.00");
        String salesRepId = "USER-TEST-1";

        // Act: Call the API
        Deal createdDeal = client.createDeal(title, value, salesRepId);

        // Assert: Verify the results
        assertNotNull(createdDeal, "Created deal should not be null");
        assertNotNull(createdDeal.getId(), "Deal ID should be auto-generated");
        assertTrue(createdDeal.getId().startsWith("DEAL-"), "Deal ID should have correct prefix");
        assertEquals(title, createdDeal.getTitle(), "Title should match");
        assertEquals(value.toPlainString(), createdDeal.getValue().getValue(), "Value should match");
        assertEquals(salesRepId, createdDeal.getSalesRepId(), "Sales rep ID should match");
        assertEquals(DealStatus.OPEN, createdDeal.getStatus(), "New deals should be OPEN by default");
    }

    /**
     * Test: Retrieve a deal by ID.
     *
     * Verifies:
     * - Deal can be retrieved after creation
     * - Retrieved data matches created data
     */
    @Test
    @Order(2)
    @DisplayName("Should retrieve a deal by ID")
    void testGetDeal() {
        // Arrange: Create a deal first
        String title = "Deal for Retrieval Test";
        Deal createdDeal = client.createDeal(title, new BigDecimal("25000.00"), "USER-TEST-2");
        String dealId = createdDeal.getId();

        // Act: Retrieve the deal
        Deal retrievedDeal = client.getDeal(dealId);

        // Assert: Verify retrieved deal matches created deal
        assertNotNull(retrievedDeal, "Retrieved deal should not be null");
        assertEquals(dealId, retrievedDeal.getId(), "Deal IDs should match");
        assertEquals(title, retrievedDeal.getTitle(), "Titles should match");
        assertEquals(createdDeal.getValue().getValue(), retrievedDeal.getValue().getValue(),
                "Values should match");
    }

    /**
     * Test: Get non-existent deal returns NOT_FOUND error.
     *
     * Verifies:
     * - Proper error handling for missing resources
     * - gRPC status code is NOT_FOUND
     * - Error message is descriptive
     */
    @Test
    @Order(3)
    @DisplayName("Should return NOT_FOUND for non-existent deal")
    void testGetDealNotFound() {
        // Arrange: Use an ID that doesn't exist
        String nonExistentId = "DEAL-NONEXISTENT";

        // Act & Assert: Verify exception is thrown with correct status
        StatusRuntimeException exception = assertThrows(
                StatusRuntimeException.class,
                () -> client.getDeal(nonExistentId),
                "Should throw StatusRuntimeException for non-existent deal"
        );

        assertEquals(Status.Code.NOT_FOUND, exception.getStatus().getCode(),
                "Status code should be NOT_FOUND");
        assertTrue(exception.getStatus().getDescription().contains(nonExistentId),
                "Error message should contain the deal ID");
    }

    /**
     * Test: List all deals.
     *
     * Verifies:
     * - Multiple deals can be listed
     * - Total count is accurate
     * - All created deals are included
     */
    @Test
    @Order(4)
    @DisplayName("Should list all deals")
    void testListDeals() {
        // Arrange: Create multiple deals
        client.createDeal("Deal 1", new BigDecimal("10000.00"), "USER-TEST-3");
        client.createDeal("Deal 2", new BigDecimal("20000.00"), "USER-TEST-3");
        client.createDeal("Deal 3", new BigDecimal("30000.00"), "USER-TEST-3");

        // Act: List all deals
        ListDealsResponse response = client.listDeals();

        // Assert: Verify response
        assertNotNull(response, "Response should not be null");
        assertTrue(response.getTotalCount() >= 3, "Should have at least 3 deals");
        assertEquals(response.getTotalCount(), response.getDealsList().size(),
                "Total count should match list size");
    }

    /**
     * Test: List deals filtered by status.
     *
     * Verifies:
     * - Server-side filtering works correctly
     * - Only matching deals are returned
     * - Filter parameter is properly handled
     */
    @Test
    @Order(5)
    @DisplayName("Should filter deals by status")
    void testListDealsFilteredByStatus() {
        // Arrange: Create deals with different statuses
        Deal openDeal = client.createDeal("Open Deal", new BigDecimal("5000.00"), "USER-TEST-4");

        Deal wonDeal = client.createDeal("Won Deal", new BigDecimal("15000.00"), "USER-TEST-4");
        Deal updatedWonDeal = wonDeal.toBuilder().setStatus(DealStatus.WON).build();
        client.updateDeal(updatedWonDeal);

        // Act: Filter by WON status
        ListDealsResponse response = client.listDeals("WON", null);

        // Assert: Verify only WON deals are returned
        assertNotNull(response, "Response should not be null");
        assertTrue(response.getTotalCount() >= 1, "Should have at least 1 won deal");

        for (Deal deal : response.getDealsList()) {
            assertEquals(DealStatus.WON, deal.getStatus(),
                    "All returned deals should have WON status");
        }
    }

    /**
     * Test: List deals filtered by sales rep.
     *
     * Verifies:
     * - Filtering by sales rep ID works
     * - Only deals for specified rep are returned
     */
    @Test
    @Order(6)
    @DisplayName("Should filter deals by sales rep")
    void testListDealsFilteredBySalesRep() {
        // Arrange: Create deals for different reps
        String targetRepId = "USER-TEST-5";
        client.createDeal("Rep 5 Deal 1", new BigDecimal("10000.00"), targetRepId);
        client.createDeal("Rep 5 Deal 2", new BigDecimal("20000.00"), targetRepId);
        client.createDeal("Other Rep Deal", new BigDecimal("30000.00"), "USER-OTHER");

        // Act: Filter by target rep
        ListDealsResponse response = client.listDeals(null, targetRepId);

        // Assert: Verify only target rep's deals are returned
        assertNotNull(response, "Response should not be null");
        assertTrue(response.getTotalCount() >= 2, "Should have at least 2 deals for target rep");

        for (Deal deal : response.getDealsList()) {
            assertEquals(targetRepId, deal.getSalesRepId(),
                    "All returned deals should belong to target rep");
        }
    }

    /**
     * Test: Update a deal.
     *
     * Verifies:
     * - Deal fields can be updated
     * - Updated values are persisted
     * - ID remains unchanged
     */
    @Test
    @Order(7)
    @DisplayName("Should update a deal successfully")
    void testUpdateDeal() {
        // Arrange: Create a deal to update
        Deal originalDeal = client.createDeal("Original Title",
                new BigDecimal("10000.00"), "USER-TEST-6");

        // Modify the deal
        String updatedTitle = "Updated Title";
        DealStatus updatedStatus = DealStatus.WON;
        Deal dealToUpdate = originalDeal.toBuilder()
                .setTitle(updatedTitle)
                .setStatus(updatedStatus)
                .build();

        // Act: Update the deal
        Deal updatedDeal = client.updateDeal(dealToUpdate);

        // Assert: Verify updates
        assertNotNull(updatedDeal, "Updated deal should not be null");
        assertEquals(originalDeal.getId(), updatedDeal.getId(), "ID should not change");
        assertEquals(updatedTitle, updatedDeal.getTitle(), "Title should be updated");
        assertEquals(updatedStatus, updatedDeal.getStatus(), "Status should be updated");
    }

    /**
     * Test: Update non-existent deal returns NOT_FOUND.
     *
     * Verifies:
     * - Cannot update a deal that doesn't exist
     * - Proper error status is returned
     */
    @Test
    @Order(8)
    @DisplayName("Should return NOT_FOUND when updating non-existent deal")
    void testUpdateDealNotFound() {
        // Arrange: Create a deal with non-existent ID
        Deal nonExistentDeal = Deal.newBuilder()
                .setId("DEAL-NONEXISTENT")
                .setTitle("Non-existent")
                .setValue(Decimal.newBuilder().setValue("1000.00").build())
                .setSalesRepId("USER-TEST")
                .build();

        // Act & Assert: Verify exception
        StatusRuntimeException exception = assertThrows(
                StatusRuntimeException.class,
                () -> client.updateDeal(nonExistentDeal),
                "Should throw StatusRuntimeException for non-existent deal"
        );

        assertEquals(Status.Code.NOT_FOUND, exception.getStatus().getCode(),
                "Status code should be NOT_FOUND");
    }

    /**
     * Test: Delete a deal.
     *
     * Verifies:
     * - Deal can be deleted
     * - Deleted deal cannot be retrieved
     * - Delete operation returns success
     */
    @Test
    @Order(9)
    @DisplayName("Should delete a deal successfully")
    void testDeleteDeal() {
        // Arrange: Create a deal to delete
        Deal dealToDelete = client.createDeal("Deal to Delete",
                new BigDecimal("5000.00"), "USER-TEST-7");
        String dealId = dealToDelete.getId();

        // Act: Delete the deal
        boolean deleted = client.deleteDeal(dealId);

        // Assert: Verify deletion
        assertTrue(deleted, "Deletion should return true");

        // Verify deal is gone
        StatusRuntimeException exception = assertThrows(
                StatusRuntimeException.class,
                () -> client.getDeal(dealId),
                "Deleted deal should not be retrievable"
        );

        assertEquals(Status.Code.NOT_FOUND, exception.getStatus().getCode(),
                "Should return NOT_FOUND for deleted deal");
    }

    /**
     * Test: Delete non-existent deal.
     *
     * Verifies:
     * - Deleting non-existent deal returns false
     * - No exception is thrown (graceful handling)
     */
    @Test
    @Order(10)
    @DisplayName("Should return false when deleting non-existent deal")
    void testDeleteDealNotFound() {
        // Arrange: Use non-existent ID
        String nonExistentId = "DEAL-NONEXISTENT-DELETE";

        // Act: Attempt to delete
        boolean deleted = client.deleteDeal(nonExistentId);

        // Assert: Verify deletion failed gracefully
        assertFalse(deleted, "Deletion should return false for non-existent deal");
    }

    /**
     * Test: Create deal with products.
     *
     * Verifies:
     * - Nested messages (products) are properly handled
     * - Products list is saved and retrieved
     * - Complex object graphs work correctly
     */
    @Test
    @Order(11)
    @DisplayName("Should create deal with products")
    void testCreateDealWithProducts() {
        // Arrange: Create deal with products
        DealProduct product1 = DealProduct.newBuilder()
                .setProductId("PROD-1")
                .setProductName("Software License")
                .setQuantity(10)
                .setPrice(Decimal.newBuilder().setValue("1000.00").build())
                .setDiscount(Decimal.newBuilder().setValue("100.00").build())
                .build();

        DealProduct product2 = DealProduct.newBuilder()
                .setProductId("PROD-2")
                .setProductName("Support Package")
                .setQuantity(1)
                .setPrice(Decimal.newBuilder().setValue("5000.00").build())
                .setDiscount(Decimal.newBuilder().setValue("0.00").build())
                .build();

        // Build request with products
        CreateDealRequest request = CreateDealRequest.newBuilder()
                .setTitle("Deal with Products")
                .setValue(Decimal.newBuilder().setValue("14900.00").build())
                .setSalesRepId("USER-TEST-8")
                .addProducts(product1)
                .addProducts(product2)
                .build();

        // Use dealService directly for this test
        CreateDealResponse response = GrpcServerTestBase.server.getDealRepository()
                .findAll().size() >= 0 ?
                        createDealViaStub(request) : null;

        // Assert
        assertNotNull(response, "Response should not be null");
        Deal createdDeal = response.getDeal();
        assertNotNull(createdDeal, "Created deal should not be null");
        assertEquals(2, createdDeal.getProductsCount(), "Should have 2 products");

        // Verify first product
        DealProduct savedProduct1 = createdDeal.getProducts(0);
        assertEquals("PROD-1", savedProduct1.getProductId(), "Product ID should match");
        assertEquals(10, savedProduct1.getQuantity(), "Quantity should match");
    }

    /**
     * Helper method to create deal via stub.
     * This bypasses the client wrapper to test CreateDealRequest directly.
     */
    private CreateDealResponse createDealViaStub(CreateDealRequest request) {
        DealServiceGrpc.DealServiceBlockingStub stub =
                DealServiceGrpc.newBlockingStub(
                        io.grpc.ManagedChannelBuilder.forAddress("localhost", 50052)
                                .usePlaintext()
                                .build()
                );
        return stub.createDeal(request);
    }

    /**
     * Nested test class for testing error conditions.
     *
     * Demonstrates JUnit 5 @Nested annotation for organizing related tests.
     */
    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle invalid status filter gracefully")
        void testInvalidStatusFilter() {
            // Act & Assert: Invalid status should cause error
            StatusRuntimeException exception = assertThrows(
                    StatusRuntimeException.class,
                    () -> client.listDeals("INVALID_STATUS", null),
                    "Should throw exception for invalid status"
            );

            assertEquals(Status.Code.INVALID_ARGUMENT, exception.getStatus().getCode(),
                    "Status code should be INVALID_ARGUMENT");
        }
    }
}