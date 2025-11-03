package com.chapman.edu.commissions.integration.repository.unit;

import com.chapman.edu.commissions.integration.database.DatabaseManager;
import com.chapman.edu.commissions.integration.repository.H2DealRepository;
import com.chapman.edu.commissions.integration.repository.H2UserRepository;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UNIT TESTING - H2DealRepository
 *
 * PURPOSE:
 * Unit tests focus on testing a single component in isolation without external dependencies.
 * However, since this is a Repository layer that directly interacts with the database,
 * this demonstrates "repository unit testing" where we test the repository's contract
 * implementation with a real (but test-isolated) database.
 *
 * CONCEPTS DEMONSTRATED:
 * 1. UNIT TESTING PRINCIPLES:
 *    - Test one component at a time (H2DealRepository)
 *    - Each test method validates one specific behavior
 *    - Tests are independent and can run in any order
 *    - Arrange-Act-Assert (AAA) pattern
 *
 * 2. TEST FIXTURES:
 *    - @BeforeAll: One-time setup (database initialization)
 *    - @BeforeEach: Per-test setup (clean database state)
 *    - @AfterAll: Cleanup resources
 *
 * 3. JUNIT 5 FEATURES:
 *    - @DisplayName: Human-readable test descriptions
 *    - @Test: Mark test methods
 *    - Assertions: assertEquals, assertTrue, assertFalse, assertNotNull, assertThrows
 *
 * 4. REPOSITORY PATTERN TESTING:
 *    - CRUD operations (Create, Read, Update, Delete)
 *    - Data persistence verification
 *    - Query methods (findAll, findById)
 *    - ID generation
 *
 * LAYER: Data Access Layer (Repository)
 * TEST TYPE: Unit Test (with database dependency)
 *
 * NOTE: Pure unit tests would mock the database, but repository tests typically
 * use a real database to verify SQL operations. See Mock tests for pure isolation.
 */
@DisplayName("Unit Tests - H2DealRepository")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class H2DealRepositoryUnitTest {

    private static DatabaseManager dbManager;
    private static H2DealRepository repository;
    private static H2UserRepository userRepository;
    private static String testUserId;

    /**
     * ONE-TIME SETUP - Runs once before all tests
     *
     * Initializes the database connection and schema.
     * This is shared across all tests in this class.
     */
    @BeforeAll
    static void setUpDatabase() {
        // Arrange: Initialize database manager (singleton pattern)
        dbManager = DatabaseManager.getInstance();
        // Schema is initialized automatically in getInstance()

        // Create repository instance with database manager dependency
        repository = new H2DealRepository(dbManager);
        userRepository = new H2UserRepository(dbManager);
    }

    /**
     * PER-TEST SETUP - Runs before each test method
     *
     * Resets the database to ensure test isolation.
     * Each test starts with a clean slate (no residual data).
     */
    @BeforeEach
    void resetDatabase() {
        dbManager.resetDatabase();

        // Create a test user for foreign key relationships
        User testUser = new User();
        testUser.setFirstName("Test");
        testUser.setLastName("SalesRep");
        testUser.setEmail("salesrep@test.com");
        testUser.setPasswordHash("password123");
        testUser.setRoles(new HashSet<>(Arrays.asList(UserRole.SALES_REP)));
        testUser.setActive(true);

        User savedUser = userRepository.save(testUser);
        testUserId = savedUser.getId();
    }

    /**
     * CLEANUP - Runs once after all tests
     *
     * Releases database resources and closes connections.
     */
    @AfterAll
    static void tearDown() {
        if (dbManager != null) {
            dbManager.close();
        }
    }

    // ============================================================
    // CREATE OPERATIONS (INSERT)
    // ============================================================

    /**
     * TEST: Save new deal with auto-generated ID
     *
     * ARRANGE-ACT-ASSERT PATTERN:
     * - Arrange: Create a Deal object without ID
     * - Act: Call repository.save()
     * - Assert: Verify deal was saved with generated ID
     */
    @Test
    @Order(1)
    @DisplayName("Should save new deal with generated ID")
    void testSaveNewDeal() {
        // Arrange: Create test data
        Deal deal = createTestDeal(null, "New Cloud Migration Deal");

        // Act: Save to database
        Deal savedDeal = repository.save(deal);

        // Assert: Verify results
        assertNotNull(savedDeal.getId(), "Deal ID should be generated");
        assertTrue(savedDeal.getId().startsWith("DEAL-"), "ID should follow DEAL-UUID format");
        assertEquals("New Cloud Migration Deal", savedDeal.getTitle());
        assertEquals(DealStatus.OPEN, savedDeal.getStatus());
    }

    /**
     * TEST: Generated IDs are unique
     *
     * Validates the generateId() method produces unique identifiers.
     */
    @Test
    @Order(2)
    @DisplayName("Should generate unique IDs for multiple deals")
    void testGenerateUniqueIds() {
        // Arrange & Act: Generate multiple IDs
        String id1 = repository.generateId();
        String id2 = repository.generateId();
        String id3 = repository.generateId();

        // Assert: All IDs are unique
        assertNotEquals(id1, id2, "IDs should be unique");
        assertNotEquals(id2, id3, "IDs should be unique");
        assertNotEquals(id1, id3, "IDs should be unique");

        // All follow the same format
        assertTrue(id1.startsWith("DEAL-"));
        assertTrue(id2.startsWith("DEAL-"));
        assertTrue(id3.startsWith("DEAL-"));
    }

    /**
     * TEST: Save deal with complex product list
     *
     * Validates JSON serialization of nested DealProduct objects.
     */
    @Test
    @Order(3)
    @DisplayName("Should save deal with multiple products (JSON serialization)")
    void testSaveDealWithProducts() {
        // Arrange: Create deal with multiple products
        Deal deal = createTestDeal(null, "Multi-Product Deal");
        deal.getProducts().add(new DealProduct("PROD-LIC", "Software License", 1, new BigDecimal("5000.00")));
        deal.getProducts().add(new DealProduct("PROD-SUPP", "Support Services", 1, new BigDecimal("2000.00")));

        // Act: Save
        Deal savedDeal = repository.save(deal);

        // Assert: Verify products were persisted
        // Base product: 1 × 10000 = 10000
        // Added product: 1 × 5000 = 5000
        // Added product: 1 × 2000 = 2000
        // Total: 17000
        assertEquals(3, savedDeal.getProducts().size(), "Should have 3 products");
        assertEquals(new BigDecimal("17000.00"), savedDeal.calculateTotalValue());
    }

    // ============================================================
    // READ OPERATIONS (SELECT)
    // ============================================================

    /**
     * TEST: Find deal by ID
     *
     * Validates retrieval of a single entity by primary key.
     */
    @Test
    @Order(4)
    @DisplayName("Should find deal by ID")
    void testFindById() {
        // Arrange: Save a deal first
        Deal deal = repository.save(createTestDeal(null, "Test Deal"));
        String dealId = deal.getId();

        // Act: Retrieve by ID
        Optional<Deal> found = repository.findById(dealId);

        // Assert: Verify found
        assertTrue(found.isPresent(), "Deal should be found");
        assertEquals(dealId, found.get().getId());
        assertEquals("Test Deal", found.get().getTitle());
    }

    /**
     * TEST: Find by ID returns empty for non-existent ID
     *
     * Validates Optional pattern for missing data.
     */
    @Test
    @Order(5)
    @DisplayName("Should return empty Optional for non-existent ID")
    void testFindByIdNotFound() {
        // Act: Search for non-existent ID
        Optional<Deal> found = repository.findById("DEAL-nonexistent-id");

        // Assert: Should be empty
        assertFalse(found.isPresent(), "Should return empty Optional");
        assertTrue(found.isEmpty(), "Optional.isEmpty() should be true");
    }

    /**
     * TEST: Find all deals
     *
     * Validates batch retrieval with ordering.
     */
    @Test
    @Order(6)
    @DisplayName("Should find all deals ordered by created_date DESC")
    void testFindAll() {
        // Arrange: Save multiple deals with different created dates
        Deal deal1 = createTestDeal(null, "Deal 1");
        deal1.setCreatedDate(LocalDate.now().minusDays(2));
        repository.save(deal1);

        Deal deal2 = createTestDeal(null, "Deal 2");
        deal2.setCreatedDate(LocalDate.now().minusDays(1));
        repository.save(deal2);

        Deal deal3 = createTestDeal(null, "Deal 3");
        deal3.setCreatedDate(LocalDate.now());
        repository.save(deal3);

        // Act: Retrieve all
        List<Deal> allDeals = repository.findAll();

        // Assert: Verify count and order
        assertEquals(3, allDeals.size(), "Should have 3 deals");
        // Most recent first (DESC order)
        assertEquals("Deal 3", allDeals.get(0).getTitle());
        assertEquals("Deal 1", allDeals.get(2).getTitle());
    }

    /**
     * TEST: Find all returns empty list when no data
     *
     * Validates behavior with empty database.
     */
    @Test
    @Order(7)
    @DisplayName("Should return empty list when no deals exist")
    void testFindAllEmpty() {
        // Act: Query empty database
        List<Deal> deals = repository.findAll();

        // Assert: Empty list
        assertNotNull(deals, "Should return non-null list");
        assertTrue(deals.isEmpty(), "List should be empty");
        assertEquals(0, deals.size());
    }

    // ============================================================
    // UPDATE OPERATIONS
    // ============================================================

    /**
     * TEST: Update existing deal
     *
     * Validates modification of persisted entities.
     */
    @Test
    @Order(8)
    @DisplayName("Should update existing deal")
    void testUpdateDeal() {
        // Arrange: Save initial deal
        Deal deal = repository.save(createTestDeal(null, "Original Title"));
        String dealId = deal.getId();

        // Act: Modify and save
        deal.setTitle("Updated Title");
        deal.setStatus(DealStatus.WON);
        deal.setCloseDate(LocalDate.now());
        Deal updated = repository.save(deal);

        // Assert: Verify changes persisted
        assertEquals(dealId, updated.getId(), "ID should remain the same");
        assertEquals("Updated Title", updated.getTitle());
        assertEquals(DealStatus.WON, updated.getStatus());
        assertNotNull(updated.getCloseDate());

        // Verify by re-querying
        Optional<Deal> reQueried = repository.findById(dealId);
        assertTrue(reQueried.isPresent());
        assertEquals("Updated Title", reQueried.get().getTitle());
    }

    /**
     * TEST: Update non-existent deal throws exception
     *
     * Validates error handling for invalid updates.
     */
    @Test
    @Order(9)
    @DisplayName("Should throw exception when updating non-existent deal")
    void testUpdateNonExistentDeal() {
        // Arrange: Create deal with fake ID
        Deal deal = createTestDeal("DEAL-fake-id", "Non-existent");

        // Act & Assert: Should throw RuntimeException
        assertThrows(RuntimeException.class, () -> {
            repository.save(deal);
        }, "Should throw exception for non-existent deal");
    }

    // ============================================================
    // DELETE OPERATIONS
    // ============================================================

    /**
     * TEST: Delete deal by ID
     *
     * Validates removal of entities.
     */
    @Test
    @Order(10)
    @DisplayName("Should delete deal by ID")
    void testDeleteById() {
        // Arrange: Save a deal
        Deal deal = repository.save(createTestDeal(null, "To Be Deleted"));
        String dealId = deal.getId();

        // Act: Delete
        boolean deleted = repository.deleteById(dealId);

        // Assert: Verify deletion
        assertTrue(deleted, "Delete operation should return true");

        // Verify it's gone
        Optional<Deal> found = repository.findById(dealId);
        assertFalse(found.isPresent(), "Deal should no longer exist");
    }

    /**
     * TEST: Delete non-existent ID returns false
     *
     * Validates behavior when deleting missing data.
     */
    @Test
    @Order(11)
    @DisplayName("Should return false when deleting non-existent ID")
    void testDeleteNonExistentId() {
        // Act: Try to delete non-existent ID
        boolean deleted = repository.deleteById("DEAL-nonexistent");

        // Assert: Should return false
        assertFalse(deleted, "Should return false for non-existent ID");
    }

    // ============================================================
    // DATA INTEGRITY AND EDGE CASES
    // ============================================================

    /**
     * TEST: Deal with null close date
     *
     * Validates handling of nullable fields.
     */
    @Test
    @Order(12)
    @DisplayName("Should handle deals with null close date")
    void testNullCloseDate() {
        // Arrange: Deal with no close date
        Deal deal = createTestDeal(null, "Open Deal");
        deal.setCloseDate(null);

        // Act: Save and retrieve
        Deal saved = repository.save(deal);
        Optional<Deal> retrieved = repository.findById(saved.getId());

        // Assert: Null is preserved
        assertTrue(retrieved.isPresent());
        assertNull(retrieved.get().getCloseDate(), "Close date should be null");
    }

    /**
     * TEST: Deal value calculation
     *
     * Validates that total value is computed from products.
     */
    @Test
    @Order(13)
    @DisplayName("Should calculate deal value from products")
    void testDealValueCalculation() {
        // Arrange: Deal with products
        Deal deal = createTestDeal(null, "Value Test");
        deal.getProducts().clear();
        deal.getProducts().add(new DealProduct("PROD-A", "Product A", 2, new BigDecimal("1000.00")));
        deal.getProducts().add(new DealProduct("PROD-B", "Product B", 3, new BigDecimal("500.00")));

        // Act: Save
        Deal saved = repository.save(deal);

        // Assert: Total = (1000*2) + (500*3) = 3500
        assertEquals(new BigDecimal("3500.00"), saved.calculateTotalValue());
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    /**
     * Helper method to create test Deal objects.
     * Demonstrates the Test Data Builder pattern.
     *
     * @param id Deal ID (null for new deals)
     * @param title Deal title
     * @return Configured Deal object
     */
    private Deal createTestDeal(String id, String title) {
        Deal deal = new Deal();
        deal.setId(id);
        deal.setTitle(title);
        deal.setStatus(DealStatus.OPEN);
        deal.setSalesRepId(testUserId); // Use valid test user ID
        deal.setCreatedDate(LocalDate.now());
        deal.setLastModifiedDate(LocalDate.now());

        // Add default product
        DealProduct product = new DealProduct("PROD-CLOUD", "Cloud Services", 1, new BigDecimal("10000.00"));
        deal.setProducts(Arrays.asList(product));

        return deal;
    }
}