package com.chapman.edu.commissions.integration.service.unit;

import com.chapman.edu.commissions.api.rest.Repository;
import com.chapman.edu.commissions.integration.service.DealService;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import com.chapman.edu.commissions.model.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UNIT TESTING - DealService (Business Logic Layer)
 *
 * PURPOSE:
 * Unit testing the Service layer focuses on testing business logic in isolation
 * from external dependencies. We mock the Repository layer to ensure we're ONLY
 * testing the service's business rules, validation, and coordination logic.
 *
 * CONCEPTS DEMONSTRATED:
 * 1. SERVICE LAYER TESTING:
 *    - Test business validation logic
 *    - Test business rules enforcement
 *    - Test service coordination (multiple method calls)
 *    - Test error handling and exception throwing
 *    - Test filtering and aggregation logic
 *
 * 2. DEPENDENCY INJECTION TESTING:
 *    - Mock the Repository dependency
 *    - Service depends on interface (Repository<Deal>), not implementation
 *    - Demonstrates Dependency Inversion Principle
 *
 * 3. BEHAVIORAL TESTING:
 *    - Verify service calls repository methods correctly
 *    - Verify service applies business rules before persistence
 *    - Verify service throws exceptions for invalid inputs
 *
 * 4. MOCKING PATTERNS:
 *    - when().thenReturn() - Stub repository responses
 *    - verify() - Ensure repository methods are called
 *    - ArgumentCaptor - Inspect arguments passed to mocks
 *
 * LAYER: Service Layer (Business Logic)
 * TEST TYPE: Unit Test (Isolated with Mocks)
 *
 * TESTING PYRAMID:
 * In a well-designed application:
 * - Business logic lives in the Service layer
 * - Unit tests for Service layer are FAST and ISOLATED
 * - Service tests don't need database, network, or external resources
 * - Integration tests verify Service + Repository work together
 */
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@DisplayName("Unit Tests - DealService (Business Logic)")
class DealServiceUnitTest {

    /**
     * MOCK REPOSITORY:
     * We mock the Repository to isolate the service layer.
     * This allows us to test business logic without database I/O.
     */
    @Mock
    private Repository<Deal> mockRepository;

    // System Under Test (SUT)
    private DealService service;

    /**
     * SETUP: Create service with mocked repository
     */
    @BeforeEach
    void setUp() {
        service = new DealService(mockRepository);
    }

    // ============================================================
    // BUSINESS VALIDATION TESTS
    // ============================================================

    /**
     * TEST: Create deal with valid data
     *
     * BUSINESS LOGIC TESTED:
     * - Service accepts valid deal
     * - Service calls repository.save()
     * - Service returns the saved deal
     */
    @Test
    @DisplayName("Should create deal with valid data")
    void testCreateValidDeal() {
        // Arrange: Create valid deal
        Deal deal = createTestDeal(null, "Valid Deal");
        Deal savedDeal = createTestDeal("DEAL-123", "Valid Deal");

        // Mock repository behavior
        when(mockRepository.save(any(Deal.class))).thenReturn(savedDeal);

        // Act: Call service
        Deal result = service.createDeal(deal);

        // Assert: Verify result
        assertNotNull(result);
        assertEquals("DEAL-123", result.getId());
        assertEquals("Valid Deal", result.getTitle());

        // Verify: Repository was called
        verify(mockRepository, times(1)).save(any(Deal.class));
    }

    /**
     * TEST: Create deal without title fails validation
     *
     * BUSINESS RULE: Deal title is required
     *
     * This tests that the service enforces business rules BEFORE
     * attempting to persist data.
     */
    @Test
    @DisplayName("Should reject deal without title (Business Validation)")
    void testCreateDealWithoutTitle() {
        // Arrange: Invalid deal (no title)
        Deal deal = createTestDeal(null, null);

        // Act & Assert: Should throw exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.createDeal(deal);
        });

        assertEquals("Deal title is required", exception.getMessage());

        // Verify: Repository was NEVER called (validation failed first)
        verify(mockRepository, never()).save(any(Deal.class));
    }

    /**
     * TEST: Create deal without products fails validation
     *
     * BUSINESS RULE: Deal must have at least one product
     */
    @Test
    @DisplayName("Should reject deal without products (Business Validation)")
    void testCreateDealWithoutProducts() {
        // Arrange: Deal with no products
        Deal deal = createTestDeal(null, "No Products Deal");
        deal.setProducts(Arrays.asList()); // Empty list

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.createDeal(deal);
        });

        assertEquals("Deal must have at least one product", exception.getMessage());

        // Verify: Repository never called
        verify(mockRepository, never()).save(any(Deal.class));
    }

    /**
     * TEST: Create deal without sales rep fails validation
     *
     * BUSINESS RULE: Sales rep ID is required
     */
    @Test
    @DisplayName("Should reject deal without sales rep (Business Validation)")
    void testCreateDealWithoutSalesRep() {
        // Arrange: Deal without sales rep
        Deal deal = createTestDeal(null, "No Rep Deal");
        deal.setSalesRepId(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.createDeal(deal);
        });

        assertEquals("Sales rep ID is required", exception.getMessage());
        verify(mockRepository, never()).save(any(Deal.class));
    }

    /**
     * TEST: Create deal with zero value fails validation
     *
     * BUSINESS RULE: Deal total value must be positive
     */
    @Test
    @DisplayName("Should reject deal with zero value (Business Validation)")
    void testCreateDealWithZeroValue() {
        // Arrange: Deal with zero-value product
        Deal deal = createTestDeal(null, "Zero Value Deal");
        deal.setProducts(Arrays.asList(new DealProduct("PROD-FREE", "Free Item", 1, BigDecimal.ZERO)));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.createDeal(deal);
        });

        assertEquals("Deal total value must be positive", exception.getMessage());
        verify(mockRepository, never()).save(any(Deal.class));
    }

    // ============================================================
    // BUSINESS RULES ENFORCEMENT TESTS
    // ============================================================

    /**
     * TEST: Cannot modify cancelled deals
     *
     * BUSINESS RULE: Cancelled deals are immutable
     *
     * This tests that the service prevents invalid business operations.
     */
    @Test
    @DisplayName("Should prevent updates to cancelled deals (Business Rule)")
    void testCannotUpdateCancelledDeal() {
        // Arrange: Existing cancelled deal
        Deal existingDeal = createTestDeal("DEAL-123", "Cancelled Deal");
        existingDeal.setStatus(DealStatus.CANCELLED);

        when(mockRepository.findById("DEAL-123")).thenReturn(Optional.of(existingDeal));

        Deal updatedDeal = createTestDeal("DEAL-123", "Trying to Update");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.updateDeal("DEAL-123", updatedDeal);
        });

        assertEquals("Cannot modify cancelled deals", exception.getMessage());

        // Verify: findById was called, but save was not
        verify(mockRepository).findById("DEAL-123");
        verify(mockRepository, never()).save(any(Deal.class));
    }

    /**
     * TEST: Cannot delete non-OPEN deals
     *
     * BUSINESS RULE: Only OPEN deals can be deleted
     */
    @Test
    @DisplayName("Should prevent deletion of WON deals (Business Rule)")
    void testCannotDeleteWonDeal() {
        // Arrange: WON deal
        Deal wonDeal = createTestDeal("DEAL-456", "Won Deal");
        wonDeal.setStatus(DealStatus.WON);

        when(mockRepository.findById("DEAL-456")).thenReturn(Optional.of(wonDeal));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.deleteDeal("DEAL-456");
        });

        assertTrue(exception.getMessage().contains("Can only delete OPEN deals"));

        // Verify: deleteById was never called
        verify(mockRepository, never()).deleteById(anyString());
    }

    /**
     * TEST: Can only close OPEN deals
     *
     * BUSINESS RULE: Status transition validation
     */
    @Test
    @DisplayName("Should prevent closing already closed deals (Business Rule)")
    void testCannotCloseAlreadyWonDeal() {
        // Arrange: Already WON deal
        Deal wonDeal = createTestDeal("DEAL-789", "Already Won");
        wonDeal.setStatus(DealStatus.WON);

        when(mockRepository.findById("DEAL-789")).thenReturn(Optional.of(wonDeal));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.closeDealAsWon("DEAL-789");
        });

        assertEquals("Can only close OPEN deals", exception.getMessage());
        verify(mockRepository, never()).save(any(Deal.class));
    }

    // ============================================================
    // SERVICE COORDINATION TESTS
    // ============================================================

    /**
     * TEST: Close deal sets status and close date
     *
     * SERVICE COORDINATION:
     * - Retrieve deal
     * - Validate business rule
     * - Modify deal state
     * - Persist changes
     *
     * This tests the service's ability to coordinate multiple operations.
     */
    @Test
    @DisplayName("Should set status and close date when closing deal")
    void testCloseDealSetsStatusAndDate() {
        // Arrange: OPEN deal
        Deal openDeal = createTestDeal("DEAL-CLOSE", "To Be Closed");
        openDeal.setStatus(DealStatus.OPEN);
        openDeal.setCloseDate(null);

        when(mockRepository.findById("DEAL-CLOSE")).thenReturn(Optional.of(openDeal));
        when(mockRepository.save(any(Deal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act: Close the deal
        Deal closedDeal = service.closeDealAsWon("DEAL-CLOSE");

        // Assert: Verify state changes
        assertEquals(DealStatus.WON, closedDeal.getStatus());
        assertNotNull(closedDeal.getCloseDate());
        assertEquals(LocalDate.now(), closedDeal.getCloseDate());

        // Verify: Both findById and save were called
        verify(mockRepository).findById("DEAL-CLOSE");
        verify(mockRepository).save(openDeal);
    }

    // ============================================================
    // FILTERING AND QUERY LOGIC TESTS
    // ============================================================

    /**
     * TEST: Filter deals by status
     *
     * SERVICE LOGIC:
     * - Retrieve all deals from repository
     * - Apply filtering logic in service
     * - Return filtered results
     *
     * This tests business logic that operates on collections.
     */
    @Test
    @DisplayName("Should filter deals by status")
    void testGetDealsByStatus() {
        // Arrange: Mock multiple deals with different statuses
        Deal openDeal1 = createTestDeal("DEAL-1", "Open 1");
        openDeal1.setStatus(DealStatus.OPEN);

        Deal openDeal2 = createTestDeal("DEAL-2", "Open 2");
        openDeal2.setStatus(DealStatus.OPEN);

        Deal wonDeal = createTestDeal("DEAL-3", "Won");
        wonDeal.setStatus(DealStatus.WON);

        List<Deal> allDeals = Arrays.asList(openDeal1, openDeal2, wonDeal);
        when(mockRepository.findAll()).thenReturn(allDeals);

        // Act: Get only OPEN deals
        List<Deal> openDeals = service.getDealsByStatus(DealStatus.OPEN);

        // Assert: Only OPEN deals returned
        assertEquals(2, openDeals.size());
        assertTrue(openDeals.stream().allMatch(d -> d.getStatus() == DealStatus.OPEN));

        // Verify: Repository was called
        verify(mockRepository).findAll();
    }

    /**
     * TEST: Filter deals by sales rep
     *
     * SERVICE LOGIC: Filtering by sales rep ID
     */
    @Test
    @DisplayName("Should filter deals by sales rep")
    void testGetDealsBySalesRep() {
        // Arrange: Deals for different reps
        Deal deal1 = createTestDeal("DEAL-1", "Rep1 Deal1");
        deal1.setSalesRepId("USER-REP1");

        Deal deal2 = createTestDeal("DEAL-2", "Rep1 Deal2");
        deal2.setSalesRepId("USER-REP1");

        Deal deal3 = createTestDeal("DEAL-3", "Rep2 Deal");
        deal3.setSalesRepId("USER-REP2");

        when(mockRepository.findAll()).thenReturn(Arrays.asList(deal1, deal2, deal3));

        // Act: Get deals for REP1
        List<Deal> rep1Deals = service.getDealsBySalesRep("USER-REP1");

        // Assert: Only REP1's deals
        assertEquals(2, rep1Deals.size());
        assertTrue(rep1Deals.stream().allMatch(d -> d.getSalesRepId().equals("USER-REP1")));
    }

    // ============================================================
    // AGGREGATION LOGIC TESTS
    // ============================================================

    /**
     * TEST: Calculate pipeline value
     *
     * BUSINESS LOGIC:
     * - Get all deals for sales rep
     * - Filter for OPEN deals only
     * - Sum total values
     *
     * This tests complex business logic with multiple operations.
     */
    @Test
    @DisplayName("Should calculate pipeline value for sales rep")
    void testCalculatePipelineValue() {
        // Arrange: Mix of OPEN and closed deals for a rep
        Deal openDeal1 = createTestDeal("DEAL-1", "Open 10K");
        openDeal1.setSalesRepId("USER-REP1");
        openDeal1.setStatus(DealStatus.OPEN);
        openDeal1.setProducts(Arrays.asList(new DealProduct("PROD-1", "Product", 1, new BigDecimal("10000"))));

        Deal openDeal2 = createTestDeal("DEAL-2", "Open 5K");
        openDeal2.setSalesRepId("USER-REP1");
        openDeal2.setStatus(DealStatus.OPEN);
        openDeal2.setProducts(Arrays.asList(new DealProduct("PROD-2", "Product", 1, new BigDecimal("5000"))));

        Deal wonDeal = createTestDeal("DEAL-3", "Won 20K");
        wonDeal.setSalesRepId("USER-REP1");
        wonDeal.setStatus(DealStatus.WON);
        wonDeal.setProducts(Arrays.asList(new DealProduct("PROD-3", "Product", 1, new BigDecimal("20000"))));

        when(mockRepository.findAll()).thenReturn(Arrays.asList(openDeal1, openDeal2, wonDeal));

        // Act: Calculate pipeline (OPEN deals only)
        BigDecimal pipelineValue = service.calculatePipelineValue("USER-REP1");

        // Assert: Only OPEN deals counted (10K + 5K = 15K)
        assertEquals(new BigDecimal("15000"), pipelineValue);
    }

    // ============================================================
    // ERROR HANDLING TESTS
    // ============================================================

    /**
     * TEST: Update non-existent deal
     *
     * ERROR HANDLING: Service validates existence before update
     */
    @Test
    @DisplayName("Should throw exception when updating non-existent deal")
    void testUpdateNonExistentDeal() {
        // Arrange: Mock repository returns empty
        when(mockRepository.findById("DEAL-fake")).thenReturn(Optional.empty());

        Deal updatedDeal = createTestDeal("DEAL-fake", "Doesn't Exist");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.updateDeal("DEAL-fake", updatedDeal);
        });

        assertTrue(exception.getMessage().contains("Deal not found"));
    }

    /**
     * TEST: Delete non-existent deal returns false
     *
     * ERROR HANDLING: Service handles missing data gracefully
     */
    @Test
    @DisplayName("Should return false when deleting non-existent deal")
    void testDeleteNonExistentDeal() {
        // Arrange: Mock repository returns empty
        when(mockRepository.findById("DEAL-fake")).thenReturn(Optional.empty());

        // Act: Attempt delete
        boolean deleted = service.deleteDeal("DEAL-fake");

        // Assert: Returns false, doesn't throw
        assertFalse(deleted);

        // Verify: deleteById was never called
        verify(mockRepository, never()).deleteById(anyString());
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    private Deal createTestDeal(String id, String title) {
        Deal deal = new Deal();
        deal.setId(id);
        deal.setTitle(title);
        deal.setStatus(DealStatus.OPEN);
        deal.setSalesRepId("USER-123");
        deal.setProducts(Arrays.asList(new DealProduct("PROD-TEST", "Test Product", 1, new BigDecimal("1000"))));
        deal.setCreatedDate(LocalDate.now());
        deal.setLastModifiedDate(LocalDate.now());
        return deal;
    }

    /**
     * KEY TAKEAWAYS - SERVICE LAYER UNIT TESTING:
     *
     * WHAT WE TESTED:
     * ✓ Business validation rules (required fields, positive values)
     * ✓ Business logic enforcement (state transitions, immutability rules)
     * ✓ Service coordination (multi-step operations)
     * ✓ Filtering and querying logic
     * ✓ Aggregation and calculation logic
     * ✓ Error handling and exception throwing
     *
     * WHY MOCK THE REPOSITORY:
     * - Tests run FAST (no database I/O)
     * - Tests are ISOLATED (no external dependencies)
     * - Tests are PREDICTABLE (controlled mock data)
     * - Tests focus on BUSINESS LOGIC only
     *
     * TESTING STRATEGY:
     * - Unit tests (this file): Business logic with mocks
     * - Integration tests: Service + Repository with real database
     * - Both are necessary for comprehensive coverage
     *
     * BEST PRACTICES:
     * - Test one business rule per test method
     * - Use descriptive test names that explain the business rule
     * - Verify mocks to ensure service calls repository correctly
     * - Test both success paths and validation failures
     * - Test edge cases (null, empty, zero values)
     */
}