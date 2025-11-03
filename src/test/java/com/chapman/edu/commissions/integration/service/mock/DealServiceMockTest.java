package com.chapman.edu.commissions.integration.service.mock;

import com.chapman.edu.commissions.integration.repository.H2DealRepository;
import com.chapman.edu.commissions.integration.service.DealService;
import com.chapman.edu.commissions.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * MOCK TESTING - DealService (Business Logic Layer)
 *
 * PURPOSE:
 * Mock testing isolates the SERVICE layer by mocking the REPOSITORY dependency.
 * This allows testing business logic without database I/O.
 *
 * CONCEPTS DEMONSTRATED:
 * 1. SERVICE LAYER MOCKING:
 *    - Mock repository to isolate business logic
 *    - Test service methods without database
 *    - Verify service calls repository correctly
 *    - Test business validation logic
 *
 * 2. MOCKITO PATTERNS FOR SERVICES:
 *    - Mock repository methods (findById, save, delete)
 *    - Stub return values (Optional.of(), List.of())
 *    - Verify method calls with correct parameters
 *    - Capture arguments to verify transformations
 *
 * 3. BUSINESS LOGIC TESTING:
 *    - Test validation logic (title required, products not empty)
 *    - Test state transitions (OPEN → WON)
 *    - Test business rules (can only delete OPEN deals)
 *    - Test orchestration (close deal workflow)
 *
 * 4. DIFFERENCE FROM INTEGRATION TESTS:
 *    - Mock tests: No database, test logic only, fast
 *    - Integration tests: Real database, test logic + persistence
 *
 * LAYER: Service Layer (Business Logic)
 * TEST TYPE: Mock Test (Pure Isolation)
 *
 * WHEN TO USE:
 * - Test business logic without database overhead
 * - Verify service orchestration and validation
 * - Test error handling paths
 * - Fast-running tests for CI/CD
 */
@DisplayName("Mock Tests - DealService (Business Logic)")
class DealServiceMockTest {

    @Mock
    private H2DealRepository mockRepository;

    private DealService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new DealService(mockRepository);
    }

    // ============================================================
    // MOCKING CREATE OPERATIONS
    // ============================================================

    /**
     * TEST: Create deal with validation
     *
     * MOCKING PATTERN:
     * - Mock repository.save() to return deal with ID
     * - Service validates and calls repository
     * - Verify repository was called with correct data
     */
    @Test
    @DisplayName("Mock: Should create deal and delegate to repository")
    void testCreateDeal() {
        // Arrange: Create valid deal
        Deal deal = new Deal();
        deal.setTitle("Test Deal");
        deal.setStatus(DealStatus.OPEN);
        deal.setSalesRepId("USER-123");
        deal.setProducts(Arrays.asList(
            new DealProduct("PROD-1", "Product", 1, new BigDecimal("1000"))
        ));
        deal.setCreatedDate(LocalDate.now());
        deal.setLastModifiedDate(LocalDate.now());

        // Mock: Repository returns deal with ID
        Deal savedDeal = new Deal();
        savedDeal.setId("DEAL-generated-id");
        savedDeal.setTitle(deal.getTitle());
        savedDeal.setStatus(deal.getStatus());
        savedDeal.setSalesRepId(deal.getSalesRepId());
        savedDeal.setProducts(deal.getProducts());
        savedDeal.setCreatedDate(deal.getCreatedDate());
        savedDeal.setLastModifiedDate(deal.getLastModifiedDate());

        when(mockRepository.save(any(Deal.class))).thenReturn(savedDeal);

        // Act: Create through service
        Deal result = service.createDeal(deal);

        // Assert: Result has ID
        assertNotNull(result.getId());
        assertEquals("DEAL-generated-id", result.getId());

        // Verify: Repository was called
        verify(mockRepository).save(any(Deal.class));
    }

    /**
     * TEST: Validation prevents invalid deal creation
     *
     * MOCKING FOCUS:
     * - Service validates BEFORE calling repository
     * - Repository is never called for invalid data
     * - No database interaction occurs
     */
    @Test
    @DisplayName("Mock: Should validate and reject deal without title")
    void testCreateDealWithoutTitle() {
        // Arrange: Invalid deal (no title)
        Deal invalidDeal = new Deal();
        invalidDeal.setStatus(DealStatus.OPEN);
        invalidDeal.setSalesRepId("USER-123");
        invalidDeal.setProducts(Arrays.asList(
            new DealProduct("PROD-1", "Product", 1, new BigDecimal("1000"))
        ));

        // Act & Assert: Service rejects
        assertThrows(IllegalArgumentException.class, () -> {
            service.createDeal(invalidDeal);
        });

        // Verify: Repository never called
        verify(mockRepository, never()).save(any(Deal.class));
    }

    /**
     * TEST: Validation prevents empty products
     */
    @Test
    @DisplayName("Mock: Should reject deal with empty products")
    void testCreateDealWithEmptyProducts() {
        // Arrange: Deal with no products
        Deal deal = new Deal();
        deal.setTitle("Test Deal");
        deal.setStatus(DealStatus.OPEN);
        deal.setSalesRepId("USER-123");
        deal.setProducts(Arrays.asList()); // Empty list

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            service.createDeal(deal);
        });

        verify(mockRepository, never()).save(any(Deal.class));
    }

    // ============================================================
    // MOCKING COMPLEX BUSINESS LOGIC
    // ============================================================

    /**
     * TEST: Close deal as WON workflow
     *
     * MOCKING PATTERN:
     * 1. Mock repository.findById() to return OPEN deal
     * 2. Service changes status and sets close date
     * 3. Mock repository.save() to return updated deal
     * 4. Verify workflow executed correctly
     */
    @Test
    @DisplayName("Mock: Should close OPEN deal as WON")
    void testCloseDealAsWon() {
        // Arrange: Existing OPEN deal
        Deal openDeal = new Deal();
        openDeal.setId("DEAL-123");
        openDeal.setTitle("Open Deal");
        openDeal.setStatus(DealStatus.OPEN);
        openDeal.setSalesRepId("USER-123");
        openDeal.setProducts(Arrays.asList(
            new DealProduct("PROD-1", "Product", 1, new BigDecimal("1000"))
        ));
        openDeal.setCreatedDate(LocalDate.now());
        openDeal.setLastModifiedDate(LocalDate.now());

        // Mock: Repository finds the deal
        when(mockRepository.findById("DEAL-123")).thenReturn(Optional.of(openDeal));

        // Mock: Repository saves updated deal
        Deal wonDeal = new Deal();
        wonDeal.setId("DEAL-123");
        wonDeal.setTitle("Open Deal");
        wonDeal.setStatus(DealStatus.WON);
        wonDeal.setCloseDate(LocalDate.now());
        wonDeal.setSalesRepId("USER-123");
        wonDeal.setProducts(openDeal.getProducts());
        wonDeal.setCreatedDate(openDeal.getCreatedDate());
        wonDeal.setLastModifiedDate(LocalDate.now());

        when(mockRepository.save(any(Deal.class))).thenReturn(wonDeal);

        // Act: Close the deal
        Deal result = service.closeDealAsWon("DEAL-123");

        // Assert: Status changed to WON
        assertEquals(DealStatus.WON, result.getStatus());
        assertNotNull(result.getCloseDate());
        assertEquals(LocalDate.now(), result.getCloseDate());

        // Verify: Repository interactions
        verify(mockRepository).findById("DEAL-123");
        verify(mockRepository).save(argThat(deal ->
            deal.getStatus() == DealStatus.WON &&
            deal.getCloseDate() != null
        ));
    }

    /**
     * TEST: Cannot close already WON deal
     *
     * MOCKING FOCUS:
     * - Repository returns WON deal
     * - Service enforces business rule
     * - Save is never called
     */
    @Test
    @DisplayName("Mock: Should prevent closing already WON deal")
    void testCannotCloseWonDeal() {
        // Arrange: Already WON deal
        Deal wonDeal = new Deal();
        wonDeal.setId("DEAL-123");
        wonDeal.setTitle("Won Deal");
        wonDeal.setStatus(DealStatus.WON);
        wonDeal.setCloseDate(LocalDate.now().minusDays(1));
        wonDeal.setSalesRepId("USER-123");
        wonDeal.setProducts(Arrays.asList(
            new DealProduct("PROD-1", "Product", 1, new BigDecimal("1000"))
        ));

        when(mockRepository.findById("DEAL-123")).thenReturn(Optional.of(wonDeal));

        // Act & Assert: Service prevents closing (service throws IllegalArgumentException)
        assertThrows(IllegalArgumentException.class, () -> {
            service.closeDealAsWon("DEAL-123");
        });

        // Verify: Save never called
        verify(mockRepository, never()).save(any(Deal.class));
    }

    /**
     * TEST: Cannot close non-existent deal
     */
    @Test
    @DisplayName("Mock: Should throw exception when closing non-existent deal")
    void testCloseNonExistentDeal() {
        // Arrange: Deal doesn't exist
        when(mockRepository.findById("DEAL-fake")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            service.closeDealAsWon("DEAL-fake");
        });

        verify(mockRepository, never()).save(any(Deal.class));
    }

    // ============================================================
    // MOCKING DELETE WITH BUSINESS RULES
    // ============================================================

    /**
     * TEST: Delete OPEN deal
     *
     * MOCKING PATTERN:
     * - Mock findById to return OPEN deal
     * - Service validates status is OPEN
     * - Mock deleteById to return true
     * - Verify delete was called
     */
    @Test
    @DisplayName("Mock: Should delete OPEN deal")
    void testDeleteOpenDeal() {
        // Arrange: OPEN deal
        Deal openDeal = new Deal();
        openDeal.setId("DEAL-123");
        openDeal.setStatus(DealStatus.OPEN);
        openDeal.setTitle("Open Deal");
        openDeal.setSalesRepId("USER-123");
        openDeal.setProducts(Arrays.asList(
            new DealProduct("PROD-1", "Product", 1, new BigDecimal("1000"))
        ));

        when(mockRepository.findById("DEAL-123")).thenReturn(Optional.of(openDeal));
        when(mockRepository.deleteById("DEAL-123")).thenReturn(true);

        // Act: Delete through service
        boolean deleted = service.deleteDeal("DEAL-123");

        // Assert: Deletion confirmed
        assertTrue(deleted);

        // Verify: Repository called
        verify(mockRepository).findById("DEAL-123");
        verify(mockRepository).deleteById("DEAL-123");
    }

    /**
     * TEST: Cannot delete WON deal
     *
     * MOCKING FOCUS:
     * - Service checks status before delete
     * - Business rule prevents deletion
     * - Repository delete never called
     */
    @Test
    @DisplayName("Mock: Should prevent deleting WON deal")
    void testCannotDeleteWonDeal() {
        // Arrange: WON deal
        Deal wonDeal = new Deal();
        wonDeal.setId("DEAL-123");
        wonDeal.setStatus(DealStatus.WON);
        wonDeal.setTitle("Won Deal");
        wonDeal.setCloseDate(LocalDate.now());
        wonDeal.setSalesRepId("USER-123");
        wonDeal.setProducts(Arrays.asList(
            new DealProduct("PROD-1", "Product", 1, new BigDecimal("1000"))
        ));

        when(mockRepository.findById("DEAL-123")).thenReturn(Optional.of(wonDeal));

        // Act & Assert: Service prevents deletion (service throws IllegalArgumentException)
        assertThrows(IllegalArgumentException.class, () -> {
            service.deleteDeal("DEAL-123");
        });

        // Verify: Delete never called
        verify(mockRepository, never()).deleteById(anyString());
    }

    // ============================================================
    // MOCKING QUERY OPERATIONS
    // ============================================================

    /**
     * TEST: Get all deals
     *
     * MOCKING PATTERN:
     * - Mock repository.findAll() to return list
     * - Service delegates to repository
     */
    @Test
    @DisplayName("Mock: Should get all deals from repository")
    void testGetAllDeals() {
        // Arrange: Mock repository returns deals
        Deal deal1 = createMockDeal("DEAL-1", "Deal 1");
        Deal deal2 = createMockDeal("DEAL-2", "Deal 2");

        when(mockRepository.findAll()).thenReturn(Arrays.asList(deal1, deal2));

        // Act: Get all through service
        List<Deal> deals = service.getAllDeals();

        // Assert: Returns all deals
        assertEquals(2, deals.size());

        // Verify: Repository called
        verify(mockRepository).findAll();
    }

    /**
     * TEST: Get deal by ID
     */
    @Test
    @DisplayName("Mock: Should get deal by ID from repository")
    void testGetDealById() {
        // Arrange: Mock repository returns deal
        Deal deal = createMockDeal("DEAL-123", "Test Deal");
        when(mockRepository.findById("DEAL-123")).thenReturn(Optional.of(deal));

        // Act: Get by ID
        Optional<Deal> result = service.getDealById("DEAL-123");

        // Assert: Deal found
        assertTrue(result.isPresent());
        assertEquals("DEAL-123", result.get().getId());

        // Verify
        verify(mockRepository).findById("DEAL-123");
    }

    /**
     * TEST: Filter deals by status
     */
    @Test
    @DisplayName("Mock: Should filter deals by status")
    void testFilterDealsByStatus() {
        // Arrange: Mock repository returns all deals
        Deal openDeal = createMockDeal("DEAL-1", "Open Deal");
        openDeal.setStatus(DealStatus.OPEN);

        Deal wonDeal = createMockDeal("DEAL-2", "Won Deal");
        wonDeal.setStatus(DealStatus.WON);

        when(mockRepository.findAll()).thenReturn(Arrays.asList(openDeal, wonDeal));

        // Act: Filter OPEN deals
        List<Deal> openDeals = service.getDealsByStatus(DealStatus.OPEN);

        // Assert: Only OPEN deals returned
        assertEquals(1, openDeals.size());
        assertEquals(DealStatus.OPEN, openDeals.get(0).getStatus());

        // Verify: Repository called
        verify(mockRepository).findAll();
    }

    // ============================================================
    // MOCKING UPDATE OPERATIONS
    // ============================================================

    /**
     * TEST: Update deal
     *
     * MOCKING PATTERN:
     * - Service validates update
     * - Mock repository.findById() returns existing deal
     * - Mock repository.save() returns updated deal
     * - Verify correct data passed to repository
     */
    @Test
    @DisplayName("Mock: Should update deal through repository")
    void testUpdateDeal() {
        // Arrange: Existing deal and update
        Deal existingDeal = createMockDeal("DEAL-123", "Original Title");
        Deal updatedDeal = createMockDeal("DEAL-123", "Updated Title");

        // Mock findById to return existing deal (required for service validation)
        when(mockRepository.findById("DEAL-123")).thenReturn(Optional.of(existingDeal));
        when(mockRepository.save(any(Deal.class))).thenReturn(updatedDeal);

        // Act: Update
        Deal updated = service.updateDeal("DEAL-123", updatedDeal);

        // Assert: Update successful
        assertEquals("Updated Title", updated.getTitle());

        // Verify: Repository methods called
        verify(mockRepository).findById("DEAL-123");
        verify(mockRepository).save(any(Deal.class));
    }

    // ============================================================
    // ARGUMENT CAPTOR TESTS
    // ============================================================

    /**
     * TEST: Verify exact data passed to repository
     *
     * ADVANCED MOCKITO:
     * - Use ArgumentCaptor to inspect save() parameter
     * - Verify service sets fields correctly
     */
    @Test
    @DisplayName("Mock: Should set close date when closing deal")
    void testCloseDealSetsCloseDate() {
        // Arrange: OPEN deal
        Deal openDeal = createMockDeal("DEAL-123", "Deal");
        openDeal.setStatus(DealStatus.OPEN);

        when(mockRepository.findById("DEAL-123")).thenReturn(Optional.of(openDeal));
        when(mockRepository.save(any(Deal.class))).thenAnswer(i -> i.getArgument(0));

        // Act: Close deal
        service.closeDealAsWon("DEAL-123");

        // Capture the argument passed to save()
        ArgumentCaptor<Deal> dealCaptor = ArgumentCaptor.forClass(Deal.class);
        verify(mockRepository).save(dealCaptor.capture());

        // Assert: Verify exact fields set
        Deal savedDeal = dealCaptor.getValue();
        assertEquals(DealStatus.WON, savedDeal.getStatus());
        assertNotNull(savedDeal.getCloseDate());
        assertEquals(LocalDate.now(), savedDeal.getCloseDate());
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
     * KEY TAKEAWAYS - SERVICE MOCK TESTING:
     *
     * WHAT WE TESTED:
     * ✓ Business validation logic (title, products)
     * ✓ Business rules (delete only OPEN, close only OPEN)
     * ✓ State transitions (OPEN → WON)
     * ✓ Service orchestration (close deal workflow)
     * ✓ Repository delegation
     * ✓ Error handling (non-existent deals)
     *
     * MOCKING BENEFITS:
     * - No database overhead
     * - Fast test execution
     * - Isolate business logic
     * - Test error paths easily
     * - Verify exact repository calls
     *
     * BEST PRACTICES:
     * - Mock dependencies, test logic
     * - Verify service doesn't skip validation
     * - Test business rules thoroughly
     * - Use ArgumentCaptor for complex verification
     * - Combine with integration tests for full coverage
     */
}