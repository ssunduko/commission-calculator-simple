package com.chapman.edu.commissions.integration;

import com.chapman.edu.commissions.integration.database.DatabaseManager;
import com.chapman.edu.commissions.integration.repository.H2DealRepository;
import com.chapman.edu.commissions.integration.repository.H2UserRepository;
import com.chapman.edu.commissions.integration.service.DealService;
import com.chapman.edu.commissions.integration.service.UserService;
import com.chapman.edu.commissions.model.*;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * IntegrationApplicationTest - Integration tests for the Commission Calculator application.
 *
 * This test class demonstrates:
 * - Integration testing across all layers (Controller -> Service -> Repository -> Database)
 * - H2 in-memory database for testing
 * - Test lifecycle management (@BeforeAll, @AfterAll, @BeforeEach)
 * - Business logic validation testing
 * - CRUD operation testing
 * - Transaction testing
 *
 * Testing Pyramid:
 * - Unit Tests: Test individual components in isolation (service methods, validation)
 * - Integration Tests: Test component interactions (this class)
 * - End-to-End Tests: Test complete user workflows via HTTP
 *
 * @see DealService
 * @see UserService
 * @see H2DealRepository
 * @see H2UserRepository
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationApplicationTest {

    private static DatabaseManager dbManager;
    private static DealService dealService;
    private static UserService userService;
    private static User testUser;

    /**
     * Set up test environment once before all tests.
     * Initializes database and service layers.
     */
    @BeforeAll
    static void setUp() {
        // Initialize database (will use test database)
        dbManager = DatabaseManager.getInstance();

        // Create repositories
        H2DealRepository dealRepository = new H2DealRepository(dbManager);
        H2UserRepository userRepository = new H2UserRepository(dbManager);

        // Create services
        dealService = new DealService(dealRepository);
        userService = new UserService(userRepository);
    }

    /**
     * Clean database before each test to ensure isolation.
     */
    @BeforeEach
    void cleanDatabase() {
        dbManager.resetDatabase();

        // Create a test user for use in deal tests
        testUser = new User();
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("password");
        testUser.setRoles(java.util.Set.of(UserRole.SALES_REP));
        testUser.setActive(true);
        testUser = userService.createUser(testUser);
    }

    /**
     * Clean up after all tests.
     */
    @AfterAll
    static void tearDown() {
        if (dbManager != null) {
            dbManager.close();
        }
    }

    // ========== USER SERVICE TESTS ==========

    @Test
    @Order(1)
    @DisplayName("Test user creation with validation")
    void testCreateUser() {
        // Arrange
        User user = new User();
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("jane@example.com");
        user.setPasswordHash("securepass");
        user.setRoles(java.util.Set.of(UserRole.SALES_MANAGER));

        // Act
        User created = userService.createUser(user);

        // Assert
        assertNotNull(created.getId(), "User ID should be generated");
        assertEquals("Jane", created.getFirstName());
        assertEquals("jane@example.com", created.getEmail());
        assertTrue(created.isActive(), "User should be active by default");
        assertTrue(created.getRoles().contains(UserRole.SALES_MANAGER));
    }

    @Test
    @Order(2)
    @DisplayName("Test user creation fails with duplicate email")
    void testCreateUserDuplicateEmail() {
        // Arrange
        User user = new User();
        user.setFirstName("Another");
        user.setLastName("User");
        user.setEmail("test@example.com"); // Same as testUser
        user.setPasswordHash("password");

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(user);
        });

        assertTrue(exception.getMessage().contains("Email already exists"));
    }

    @Test
    @Order(3)
    @DisplayName("Test user authentication with valid credentials")
    void testAuthenticateUser() {
        // Act
        Optional<User> authenticated = userService.authenticate("test@example.com", "password");

        // Assert
        assertTrue(authenticated.isPresent(), "User should be authenticated");
        assertEquals(testUser.getId(), authenticated.get().getId());
    }

    @Test
    @Order(4)
    @DisplayName("Test user authentication fails with invalid password")
    void testAuthenticateInvalidPassword() {
        // Act
        Optional<User> authenticated = userService.authenticate("test@example.com", "wrongpassword");

        // Assert
        assertTrue(authenticated.isEmpty(), "Authentication should fail");
    }

    // ========== DEAL SERVICE TESTS ==========

    @Test
    @Order(10)
    @DisplayName("Test deal creation with valid data")
    void testCreateDeal() {
        // Arrange
        Deal deal = createValidDeal();

        // Act
        Deal created = dealService.createDeal(deal);

        // Assert
        assertNotNull(created.getId(), "Deal ID should be generated");
        assertEquals("Test Deal", created.getTitle());
        assertEquals(DealStatus.OPEN, created.getStatus());
        assertEquals(testUser.getId(), created.getSalesRepId());
        assertEquals(2, created.getProducts().size());
    }

    @Test
    @Order(11)
    @DisplayName("Test deal creation fails without title")
    void testCreateDealMissingTitle() {
        // Arrange
        Deal deal = createValidDeal();
        deal.setTitle(null);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            dealService.createDeal(deal);
        });

        assertTrue(exception.getMessage().contains("title is required"));
    }

    @Test
    @Order(12)
    @DisplayName("Test deal creation fails without products")
    void testCreateDealMissingProducts() {
        // Arrange
        Deal deal = createValidDeal();
        deal.setProducts(List.of());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            dealService.createDeal(deal);
        });

        assertTrue(exception.getMessage().contains("at least one product"));
    }

    @Test
    @Order(13)
    @DisplayName("Test get all deals")
    void testGetAllDeals() {
        // Arrange
        dealService.createDeal(createValidDeal());
        dealService.createDeal(createValidDeal());

        // Act
        List<Deal> deals = dealService.getAllDeals();

        // Assert
        assertEquals(2, deals.size());
    }

    @Test
    @Order(14)
    @DisplayName("Test get deal by ID")
    void testGetDealById() {
        // Arrange
        Deal created = dealService.createDeal(createValidDeal());

        // Act
        Optional<Deal> found = dealService.getDealById(created.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
    }

    @Test
    @Order(15)
    @DisplayName("Test get deals by status")
    void testGetDealsByStatus() {
        // Arrange
        Deal deal1 = createValidDeal();
        deal1.setStatus(DealStatus.OPEN);
        dealService.createDeal(deal1);

        Deal deal2 = createValidDeal();
        deal2.setStatus(DealStatus.WON);
        dealService.createDeal(deal2);

        // Act
        List<Deal> openDeals = dealService.getDealsByStatus(DealStatus.OPEN);
        List<Deal> wonDeals = dealService.getDealsByStatus(DealStatus.WON);

        // Assert
        assertEquals(1, openDeals.size());
        assertEquals(1, wonDeals.size());
    }

    @Test
    @Order(16)
    @DisplayName("Test update deal")
    void testUpdateDeal() {
        // Arrange
        Deal created = dealService.createDeal(createValidDeal());
        created.setTitle("Updated Title");

        // Act
        Deal updated = dealService.updateDeal(created.getId(), created);

        // Assert
        assertEquals("Updated Title", updated.getTitle());
    }

    @Test
    @Order(17)
    @DisplayName("Test delete OPEN deal")
    void testDeleteOpenDeal() {
        // Arrange
        Deal created = dealService.createDeal(createValidDeal());

        // Act
        boolean deleted = dealService.deleteDeal(created.getId());

        // Assert
        assertTrue(deleted);
        assertTrue(dealService.getDealById(created.getId()).isEmpty());
    }

    @Test
    @Order(18)
    @DisplayName("Test delete non-OPEN deal fails")
    void testDeleteClosedDealFails() {
        // Arrange
        Deal deal = createValidDeal();
        deal.setStatus(DealStatus.WON);
        Deal created = dealService.createDeal(deal);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            dealService.deleteDeal(created.getId());
        });

        assertTrue(exception.getMessage().contains("Can only delete OPEN deals"));
    }

    @Test
    @Order(19)
    @DisplayName("Test close deal as WON")
    void testCloseDealAsWon() {
        // Arrange
        Deal created = dealService.createDeal(createValidDeal());

        // Act
        Deal closed = dealService.closeDealAsWon(created.getId());

        // Assert
        assertEquals(DealStatus.WON, closed.getStatus());
        assertNotNull(closed.getCloseDate());
    }

    @Test
    @Order(20)
    @DisplayName("Test calculate pipeline value")
    void testCalculatePipelineValue() {
        // Arrange
        Deal deal1 = createValidDeal();
        deal1.setStatus(DealStatus.OPEN);
        dealService.createDeal(deal1);

        Deal deal2 = createValidDeal();
        deal2.setStatus(DealStatus.OPEN);
        dealService.createDeal(deal2);

        Deal deal3 = createValidDeal();
        deal3.setStatus(DealStatus.WON); // Should not be counted
        dealService.createDeal(deal3);

        // Act
        BigDecimal pipelineValue = dealService.calculatePipelineValue(testUser.getId());

        // Assert
        // Each valid deal has products totaling 15000 (10000 + 5000)
        // Two OPEN deals = 30000
        assertEquals(new BigDecimal("30000.00"), pipelineValue);
    }

    // ========== HELPER METHODS ==========

    /**
     * Creates a valid deal for testing.
     */
    private Deal createValidDeal() {
        Deal deal = new Deal();
        deal.setTitle("Test Deal");
        deal.setStatus(DealStatus.OPEN);
        deal.setSalesRepId(testUser.getId());
        deal.setProducts(List.of(
                new DealProduct("PROD-Product A", "Product A", 1, new BigDecimal("10000.00")),
                new DealProduct("PROD-Product B", "Product B", 1, new BigDecimal("5000.00"))
        ));
        return deal;
    }
}