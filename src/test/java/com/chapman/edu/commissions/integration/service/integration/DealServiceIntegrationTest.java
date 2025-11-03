package com.chapman.edu.commissions.integration.service.integration;

import com.chapman.edu.commissions.integration.database.DatabaseManager;
import com.chapman.edu.commissions.integration.repository.H2DealRepository;
import com.chapman.edu.commissions.integration.repository.H2UserRepository;
import com.chapman.edu.commissions.integration.service.DealService;
import com.chapman.edu.commissions.model.*;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * INTEGRATION TESTING - DealService (Service + Repository Layer)
 *
 * PURPOSE:
 * Integration tests verify that the SERVICE layer works correctly with the REPOSITORY layer.
 * This tests the entire business logic flow WITH real database persistence.
 *
 * CONCEPTS DEMONSTRATED:
 * 1. SERVICE-REPOSITORY INTEGRATION:
 *    - Service orchestrates business logic
 *    - Repository handles data persistence
 *    - Test that business rules are enforced AND persisted correctly
 *    - Verify transactions work end-to-end
 *
 * 2. INTEGRATION TEST FOCUS:
 *    - Test service methods with REAL repository (not mocked)
 *    - Verify business validation + database constraints work together
 *    - Test that service orchestration persists correctly
 *    - Validate complex workflows across layers
 *
 * 3. LAYER INTEGRATION PATTERN:
 *    - Service Layer (business logic) → Repository Layer (data access) → Database
 *    - Tests verify the contract between these layers
 *    - Ensures business rules are properly persisted
 *
 * 4. DIFFERENCE FROM UNIT TESTS:
 *    - Unit tests: Mock repository, test business logic only
 *    - Integration tests: Real repository + database, test logic + persistence
 *
 * LAYER: Service Layer + Repository Layer
 * TEST TYPE: Integration Test (Service with real Repository)
 *
 * WHEN TO USE:
 * - Verify service business logic persists correctly
 * - Test complex workflows spanning service and repository
 * - Validate that business rules and database constraints align
 * - Test transaction boundaries and rollback behavior
 */
@DisplayName("Integration Tests - DealService (Service + Repository + Database)")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DealServiceIntegrationTest {

    private static DatabaseManager dbManager;
    private static H2DealRepository dealRepository;
    private static H2UserRepository userRepository;
    private static DealService dealService;
    private static String testUserId;

    @BeforeAll
    static void setUpDatabase() {
        dbManager = DatabaseManager.getInstance();
        dealRepository = new H2DealRepository(dbManager);
        userRepository = new H2UserRepository(dbManager);
        dealService = new DealService(dealRepository);
    }

    @BeforeEach
    void resetDatabase() {
        dbManager.resetDatabase();

        // Create test user for foreign key relationships
        User testUser = new User();
        testUser.setFirstName("Integration");
        testUser.setLastName("Test");
        testUser.setEmail("integration@test.com");
        testUser.setPasswordHash("password");
        testUser.setRoles(new HashSet<>(Arrays.asList(UserRole.SALES_REP)));
        testUser.setActive(true);

        User savedUser = userRepository.save(testUser);
        testUserId = savedUser.getId();
    }

    @AfterAll
    static void tearDown() {
        if (dbManager != null) {
            dbManager.close();
        }
    }

    // ============================================================
    // INTEGRATION TEST: SERVICE + REPOSITORY CREATE OPERATIONS
    // ============================================================

    /**
     * TEST: Create deal through service with validation and persistence
     *
     * INTEGRATION FLOW:
     * 1. Service validates business rules (title not null, products not empty)
     * 2. Service calls repository.save()
     * 3. Repository persists to database with generated ID
     * 4. Service returns persisted deal
     *
     * DEMONSTRATES:
     * - Business validation in service layer
     * - Persistence in repository layer
     * - End-to-end create operation
     */
    @Test
    @Order(1)
    @DisplayName("Integration: Should create deal with validation and persist to database")
    void testCreateDeal() {
        // Arrange: Create valid deal
        Deal deal = new Deal();
        deal.setTitle("Integration Test Deal");
        deal.setStatus(DealStatus.OPEN);
        deal.setSalesRepId(testUserId);
        deal.setProducts(Arrays.asList(
            new DealProduct("PROD-1", "Cloud Platform", 1, new BigDecimal("50000.00"))
        ));
        deal.setCreatedDate(LocalDate.now());
        deal.setLastModifiedDate(LocalDate.now());

        // Act: Create through service
        Deal created = dealService.createDeal(deal);

        // Assert: Validate business logic was applied
        assertNotNull(created.getId(), "ID should be generated");
        assertTrue(created.getId().startsWith("DEAL-"));
        assertEquals("Integration Test Deal", created.getTitle());
        assertEquals(DealStatus.OPEN, created.getStatus());

        // Assert: Verify persistence by re-querying database
        Optional<Deal> retrieved = dealService.getDealById(created.getId());
        assertTrue(retrieved.isPresent(), "Deal should be persisted in database");
        assertEquals(created.getId(), retrieved.get().getId());
    }

    /**
     * TEST: Service validation prevents invalid data from reaching database
     *
     * INTEGRATION FOCUS:
     * - Service validates BEFORE calling repository
     * - Database never sees invalid data
     * - No database transaction is started
     */
    @Test
    @Order(2)
    @DisplayName("Integration: Should reject invalid deal before database access")
    void testCreateDealValidation() {
        // Arrange: Invalid deal (no title)
        Deal invalidDeal = new Deal();
        invalidDeal.setStatus(DealStatus.OPEN);
        invalidDeal.setSalesRepId(testUserId);
        invalidDeal.setProducts(Arrays.asList(
            new DealProduct("PROD-1", "Product", 1, new BigDecimal("1000"))
        ));
        invalidDeal.setCreatedDate(LocalDate.now());
        invalidDeal.setLastModifiedDate(LocalDate.now());

        // Act & Assert: Service rejects before database
        assertThrows(IllegalArgumentException.class, () -> {
            dealService.createDeal(invalidDeal);
        }, "Service should validate before repository");

        // Verify: No deal was created in database
        List<Deal> allDeals = dealService.getAllDeals();
        assertEquals(0, allDeals.size(), "No invalid data should reach database");
    }

    // ============================================================
    // INTEGRATION TEST: COMPLEX BUSINESS LOGIC + PERSISTENCE
    // ============================================================

    /**
     * TEST: Close deal as WON - Complex orchestration
     *
     * INTEGRATION FLOW:
     * 1. Service validates deal exists (repository query)
     * 2. Service validates deal is OPEN (business rule)
     * 3. Service sets status to WON
     * 4. Service sets close date
     * 5. Service persists changes (repository update)
     * 6. Database reflects changes
     *
     * DEMONSTRATES:
     * - Service orchestration across multiple steps
     * - Business rule enforcement
     * - State transitions persist correctly
     */
    @Test
    @Order(3)
    @DisplayName("Integration: Should close deal as WON with business logic and persistence")
    void testCloseDealAsWon() {
        // Arrange: Create an OPEN deal
        Deal openDeal = new Deal();
        openDeal.setTitle("Deal to Close");
        openDeal.setStatus(DealStatus.OPEN);
        openDeal.setSalesRepId(testUserId);
        openDeal.setProducts(Arrays.asList(
            new DealProduct("PROD-1", "Product", 1, new BigDecimal("25000"))
        ));
        openDeal.setCreatedDate(LocalDate.now());
        openDeal.setLastModifiedDate(LocalDate.now());

        Deal created = dealService.createDeal(openDeal);
        String dealId = created.getId();

        // Act: Close the deal through service
        Deal closed = dealService.closeDealAsWon(dealId);

        // Assert: Business rules applied
        assertEquals(DealStatus.WON, closed.getStatus(), "Status should be WON");
        assertNotNull(closed.getCloseDate(), "Close date should be set");
        assertEquals(LocalDate.now(), closed.getCloseDate(), "Close date should be today");

        // Assert: Changes persisted to database
        Optional<Deal> retrieved = dealService.getDealById(dealId);
        assertTrue(retrieved.isPresent());
        assertEquals(DealStatus.WON, retrieved.get().getStatus(), "WON status persisted");
        assertNotNull(retrieved.get().getCloseDate(), "Close date persisted");
    }

    /**
     * TEST: Cannot close already closed deal
     *
     * INTEGRATION FOCUS:
     * - Service checks current state from database
     * - Business rule prevents invalid state transition
     * - Database remains unchanged
     */
    @Test
    @Order(4)
    @DisplayName("Integration: Should prevent closing already WON deal")
    void testCannotCloseWonDeal() {
        // Arrange: Create and close a deal
        Deal deal = new Deal();
        deal.setTitle("Already Won Deal");
        deal.setStatus(DealStatus.OPEN);
        deal.setSalesRepId(testUserId);
        deal.setProducts(Arrays.asList(
            new DealProduct("PROD-1", "Product", 1, new BigDecimal("1000"))
        ));
        deal.setCreatedDate(LocalDate.now());
        deal.setLastModifiedDate(LocalDate.now());

        Deal created = dealService.createDeal(deal);
        dealService.closeDealAsWon(created.getId());

        // Act & Assert: Cannot close again (service throws IllegalArgumentException)
        assertThrows(IllegalArgumentException.class, () -> {
            dealService.closeDealAsWon(created.getId());
        }, "Cannot close already WON deal");
    }

    // ============================================================
    // INTEGRATION TEST: DELETE WITH BUSINESS RULES
    // ============================================================

    /**
     * TEST: Delete only OPEN deals
     *
     * INTEGRATION FLOW:
     * 1. Service retrieves deal from repository
     * 2. Service validates status is OPEN
     * 3. Service calls repository.delete()
     * 4. Database removes record
     *
     * DEMONSTRATES:
     * - Business rule enforcement before delete
     * - Persistent deletion verification
     */
    @Test
    @Order(5)
    @DisplayName("Integration: Should delete OPEN deal with business validation")
    void testDeleteOpenDeal() {
        // Arrange: Create OPEN deal
        Deal deal = new Deal();
        deal.setTitle("To Be Deleted");
        deal.setStatus(DealStatus.OPEN);
        deal.setSalesRepId(testUserId);
        deal.setProducts(Arrays.asList(
            new DealProduct("PROD-1", "Product", 1, new BigDecimal("1000"))
        ));
        deal.setCreatedDate(LocalDate.now());
        deal.setLastModifiedDate(LocalDate.now());

        Deal created = dealService.createDeal(deal);
        String dealId = created.getId();

        // Act: Delete through service
        boolean deleted = dealService.deleteDeal(dealId);

        // Assert: Service confirms deletion
        assertTrue(deleted, "Service should confirm deletion");

        // Assert: Verify deletion in database
        Optional<Deal> retrieved = dealService.getDealById(dealId);
        assertFalse(retrieved.isPresent(), "Deal should be removed from database");
    }

    /**
     * TEST: Cannot delete WON deal
     *
     * INTEGRATION FOCUS:
     * - Service enforces business rule
     * - Repository never called for delete
     * - Database record remains unchanged
     */
    @Test
    @Order(6)
    @DisplayName("Integration: Should prevent deleting WON deal")
    void testCannotDeleteWonDeal() {
        // Arrange: Create and close deal
        Deal deal = new Deal();
        deal.setTitle("Won Deal");
        deal.setStatus(DealStatus.OPEN);
        deal.setSalesRepId(testUserId);
        deal.setProducts(Arrays.asList(
            new DealProduct("PROD-1", "Product", 1, new BigDecimal("1000"))
        ));
        deal.setCreatedDate(LocalDate.now());
        deal.setLastModifiedDate(LocalDate.now());

        Deal created = dealService.createDeal(deal);
        dealService.closeDealAsWon(created.getId());

        // Act & Assert: Service prevents deletion (service throws IllegalArgumentException)
        assertThrows(IllegalArgumentException.class, () -> {
            dealService.deleteDeal(created.getId());
        }, "Cannot delete WON deal");

        // Verify: Deal still exists in database
        Optional<Deal> stillExists = dealService.getDealById(created.getId());
        assertTrue(stillExists.isPresent(), "Deal should still exist");
    }

    // ============================================================
    // INTEGRATION TEST: QUERY OPERATIONS
    // ============================================================

    /**
     * TEST: Get all deals through service
     *
     * INTEGRATION FOCUS:
     * - Service delegates to repository
     * - Repository queries database
     * - Results returned through service layer
     */
    @Test
    @Order(7)
    @DisplayName("Integration: Should retrieve all deals from database")
    void testGetAllDeals() {
        // Arrange: Create multiple deals
        for (int i = 1; i <= 3; i++) {
            Deal deal = new Deal();
            deal.setTitle("Deal " + i);
            deal.setStatus(DealStatus.OPEN);
            deal.setSalesRepId(testUserId);
            deal.setProducts(Arrays.asList(
                new DealProduct("PROD-" + i, "Product " + i, 1, new BigDecimal("1000"))
            ));
            deal.setCreatedDate(LocalDate.now());
            deal.setLastModifiedDate(LocalDate.now());
            dealService.createDeal(deal);
        }

        // Act: Get all through service
        List<Deal> allDeals = dealService.getAllDeals();

        // Assert: All deals retrieved
        assertEquals(3, allDeals.size(), "Should retrieve all deals from database");
    }

    /**
     * TEST: Filter deals by status
     *
     * INTEGRATION FLOW:
     * 1. Service applies filter logic
     * 2. Repository queries database
     * 3. Service returns filtered results
     */
    @Test
    @Order(8)
    @DisplayName("Integration: Should filter deals by status")
    void testFilterDealsByStatus() {
        // Arrange: Create OPEN and WON deals
        Deal openDeal1 = createTestDeal("Open Deal 1", DealStatus.OPEN);
        Deal openDeal2 = createTestDeal("Open Deal 2", DealStatus.OPEN);
        Deal wonDeal = createTestDeal("Won Deal", DealStatus.OPEN);

        dealService.createDeal(openDeal1);
        dealService.createDeal(openDeal2);
        Deal created = dealService.createDeal(wonDeal);
        dealService.closeDealAsWon(created.getId());

        // Act: Filter OPEN deals
        List<Deal> openDeals = dealService.getDealsByStatus(DealStatus.OPEN);

        // Assert: Only OPEN deals returned
        assertEquals(2, openDeals.size(), "Should filter OPEN deals from database");
        assertTrue(openDeals.stream().allMatch(d -> d.getStatus() == DealStatus.OPEN));
    }

    // ============================================================
    // INTEGRATION TEST: UPDATE OPERATIONS
    // ============================================================

    /**
     * TEST: Update deal with validation
     *
     * INTEGRATION FLOW:
     * 1. Service validates update data
     * 2. Service calls repository.save()
     * 3. Repository updates database
     * 4. Changes persist
     */
    @Test
    @Order(9)
    @DisplayName("Integration: Should update deal with validation and persistence")
    void testUpdateDeal() {
        // Arrange: Create deal
        Deal deal = createTestDeal("Original Title", DealStatus.OPEN);
        Deal created = dealService.createDeal(deal);

        // Act: Update through service
        created.setTitle("Updated Title");
        Deal updated = dealService.updateDeal(created.getId(), created);

        // Assert: Update applied
        assertEquals("Updated Title", updated.getTitle());

        // Assert: Update persisted
        Optional<Deal> retrieved = dealService.getDealById(created.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("Updated Title", retrieved.get().getTitle());
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    private Deal createTestDeal(String title, DealStatus status) {
        Deal deal = new Deal();
        deal.setTitle(title);
        deal.setStatus(status);
        deal.setSalesRepId(testUserId);
        deal.setProducts(Arrays.asList(
            new DealProduct("PROD-TEST", "Test Product", 1, new BigDecimal("1000"))
        ));
        deal.setCreatedDate(LocalDate.now());
        deal.setLastModifiedDate(LocalDate.now());
        return deal;
    }

    /**
     * KEY TAKEAWAYS - SERVICE INTEGRATION TESTING:
     *
     * WHAT WE TESTED:
     * ✓ Service business logic WITH real database persistence
     * ✓ Business validation before database access
     * ✓ Complex orchestration (close deal workflow)
     * ✓ State transitions persist correctly
     * ✓ Business rules prevent invalid operations
     * ✓ Query operations through service layer
     * ✓ Delete operations with business constraints
     *
     * INTEGRATION PATTERN:
     * - Service enforces business rules
     * - Repository handles persistence
     * - Database stores state
     * - Tests verify all three work together
     *
     * BEST PRACTICES:
     * - Test business logic + persistence together
     * - Verify validation prevents invalid database state
     * - Test complex workflows end-to-end
     * - Confirm state changes persist correctly
     * - Use real database for integration tests
     */
}