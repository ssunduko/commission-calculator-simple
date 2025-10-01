package com.chapman.edu.commissions.fundamentals.parametrized;

import com.chapman.edu.commissions.model.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;


import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit 5 Parameterized Tests for Commission Calculator Model Classes.
 * 
 * This class demonstrates all major parameterized testing techniques in JUnit 5:
 * 
 * 1. @ValueSource - Tests with simple primitive values and strings
 * 2. @CsvSource - Tests with CSV-formatted input data for multiple parameters
 * 3. @MethodSource - Tests with complex objects created by static methods
 * 4. @EnumSource - Tests with enum values automatically provided
 * 5. @NullAndEmptySource - Tests with null and empty values for edge cases
 * 6. @ArgumentsSource - Tests with custom argument providers
 * 
 * Parameterized tests are powerful because they:
 * - Reduce code duplication by testing the same logic with different inputs
 * - Improve test coverage by systematically testing various scenarios
 * - Make tests more maintainable by centralizing test data
 * - Provide clear test names that describe what is being tested
 * - Enable data-driven testing approaches
 */
public class JUnitParametrizedTest {
    /**
     * Tests deal creation with various titles using @ValueSource.
     * 
     * @ValueSource is the simplest parameterized test annotation.
     * It works with primitive types (int, long, double, etc.) and strings.
     * Perfect for testing single-parameter scenarios with simple values.
     * 
     * @param title the deal title to test
     */
    @ParameterizedTest(name = "Creating deal with title: '{0}'")
    @ValueSource(strings = {"Software License", "Hardware Purchase", "Service Contract", "Training Package", "Consulting Deal"})
    void testDealCreationWithTitles(String title) {
        // Arrange & Act
        Deal deal = new Deal(title, new BigDecimal("10000"), "REP001");
        
        // Assert
        assertNotNull(deal, "Deal should not be null");
        assertEquals(title, deal.getTitle(), "Deal title should match input");
        assertEquals(DealStatus.OPEN, deal.getStatus(), "New deals should have OPEN status");
        assertNotNull(deal.getCreatedDate(), "Created date should be set");
    }

    /**
     * Tests deal value validation with numeric inputs using @ValueSource.
     * Demonstrates testing with primitive numeric types.
     * 
     * @param value the deal value to test
     */
    @ParameterizedTest(name = "Deal value validation: ${0}")
    @ValueSource(doubles = {0.01, 100.0, 1000.0, 10000.0, 50000.0, 100000.0, 999999.99})
    void testDealValueValidation(double value) {
        // Arrange & Act
        Deal deal = new Deal("Test Deal", BigDecimal.valueOf(value), "REP001");
        
        // Assert
        assertNotNull(deal.getValue(), "Deal value should not be null");
        assertTrue(deal.getValue().compareTo(BigDecimal.ZERO) > 0, "Deal value should be positive");
        assertEquals(0, deal.getValue().compareTo(BigDecimal.valueOf(value)), "Deal value should match input");
    }

    /**
     * Tests user creation with various usernames using @ValueSource.
     * Shows how to test string validation in different contexts.
     * 
     * @param username the username to test
     */
    @ParameterizedTest(name = "User creation with username: '{0}'")
    @ValueSource(strings = {"john.doe", "sales_rep_1", "manager123", "admin.user", "test.account"})
    void testUserCreationWithUsernames(String username) {
        // Arrange & Act
        User user = new User(username, username + "@company.com", "Test", "User");
        
        // Assert
        assertNotNull(user, "User should not be null");
        assertEquals(username, user.getUsername(), "Username should match input");
        assertTrue(user.isActive(), "New users should be active by default");
        assertNotNull(user.getCreatedDate(), "Created date should be set");
    }

    /**
     * Tests deal status transitions using @EnumSource.
     * 
     * @EnumSource automatically provides all enum constants as test parameters.
     * This is perfect for testing enum-based functionality and ensures
     * all possible enum values are tested.
     * 
     * @param status the deal status to test
     */
    @ParameterizedTest(name = "Testing deal status: {0}")
    @EnumSource(DealStatus.class)
    void testDealStatusHandling(DealStatus status) {
        // Arrange
        Deal deal = new Deal("Test Deal", new BigDecimal("5000"), "REP001");
        // Act
        deal.setStatus(status);
        // Assert
        assertEquals(status, deal.getStatus(), "Deal status should be set correctly");
        assertNotNull(deal.getLastModifiedDate(), "Last modified date should be updated");
        assertNotNull(status.getDisplayName(), "Status should have display name");
        assertFalse(status.getDisplayName().isEmpty(), "Display name should not be empty");
    }

    /**
     * Tests user role functionality using @EnumSource.
     * Demonstrates filtering enum values to test specific subsets.
     * 
     * @param role the user role to test
     */
    @ParameterizedTest(name = "Testing user role: {0}")
    @EnumSource(value = UserRole.class, names = {"SALES_REP", "SALES_MANAGER"})
    void testSalesUserRoles(UserRole role) {
        // Arrange
        User user = new User("testuser", "test@company.com", "Test", "User");
        
        // Act
        user.addRole(role);
        
        // Assert
        assertTrue(user.hasRole(role), "User should have the assigned role");
        assertTrue(user.isSalesRep() || user.isSalesManager(), "User should be in sales");
        assertNotNull(role.getDisplayName(), "Role should have display name");
    }

    /**
     * Tests commission status handling using @EnumSource with exclusions.
     * Shows how to exclude specific enum values from testing.
     * 
     * @param status the commission status to test
     */
    @ParameterizedTest(name = "Testing commission status: {0}")
    @EnumSource(value = CommissionCalculation.CommissionStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"CANCELLED"})
    void testValidCommissionStatuses(CommissionCalculation.CommissionStatus status) {
        // Arrange
        CommissionCalculation calculation = new CommissionCalculation("DEAL001", "REP001", new BigDecimal("1000"));
        
        // Act
        calculation.setStatus(status);
        
        // Assert
        assertEquals(status, calculation.getStatus(), "Commission status should be set correctly");
        assertNotEquals(CommissionCalculation.CommissionStatus.CANCELLED, status, "Should not be cancelled");
        assertNotNull(status.getDisplayName(), "Status should have display name");
    }

    /**
     * Tests deal creation with multiple parameters using @CsvSource.
     * 
     * @CsvSource allows testing multiple parameters in a structured format.
     * It's perfect for testing combinations of inputs and expected outputs.
     * Each line represents one test case with comma-separated values.
     * 
     * @param title the deal title
     * @param value the deal value
     * @param salesRepId the sales representative ID
     * @param expectedValid whether the combination should create a valid deal
     */
    @ParameterizedTest(name = "Deal: {0}, Value: ${1}, Rep: {2}, Valid: {3}")
    @CsvSource({
        "Software License Deal, 1000.00, REP001, true",
        "Hardware Purchase, 5000.50, REP002, true", 
        "Service Contract, 100000.00, REP003, true",
        "Training Package, 0.01, REP001, true",
        "Invalid Deal, 0.00, REP002, false",
        "Large Enterprise Deal, 999999.99, REP004, true"
    })
    void testDealCreationCombinations(String title, double value, String salesRepId, boolean expectedValid) {
        // Arrange
        BigDecimal dealValue = BigDecimal.valueOf(value);
        
        // Act
        Deal deal = new Deal(title, dealValue, salesRepId);
        
        // Assert
        assertNotNull(deal, "Deal should not be null");
        assertEquals(title, deal.getTitle(), "Deal title should match");
        assertEquals(salesRepId, deal.getSalesRepId(), "Sales rep ID should match");
        
        if (expectedValid) {
            assertTrue(deal.getValue().compareTo(BigDecimal.ZERO) > 0, "Valid deals should have positive value");
        } else {
            assertEquals(0, deal.getValue().compareTo(BigDecimal.ZERO), "Invalid deals have zero value");
        }
    }

    /**
     * Tests user creation with various field combinations using @CsvSource.
     * Demonstrates testing object construction with multiple parameters.
     * 
     * @param username the username
     * @param email the email address
     * @param firstName the first name
     * @param lastName the last name
     * @param expectedValid whether the user should be valid
     */
    @ParameterizedTest(name = "User: {0}, Email: {1}, Name: {2} {3}, Valid: {4}")
    @CsvSource({
        "john.doe, john.doe@company.com, John, Doe, true",
        "jane.smith, jane.smith@company.com, Jane, Smith, true",
        "admin, admin@company.com, Admin, User, true",
        "test.user, test@company.com, Test, Account, true",
        "sales.rep, sales@company.com, Sales, Representative, true"
    })
    void testUserCreationCombinations(String username, String email, String firstName, String lastName, boolean expectedValid) {
        // Act
        User user = new User(username, email, firstName, lastName);
        
        // Assert
        assertNotNull(user, "User should not be null");
        assertEquals(username, user.getUsername(), "Username should match");
        assertEquals(email, user.getEmail(), "Email should match");
        assertEquals(firstName, user.getFirstName(), "First name should match");
        assertEquals(lastName, user.getLastName(), "Last name should match");
        
        if (expectedValid) {
            assertNotNull(user.getFullName(), "Valid users should have full name");
            assertTrue(user.isActive(), "Valid users should be active");
        }
    }

    /**
     * Tests commission calculation scenarios using @CsvSource.
     * Shows testing business logic with multiple input parameters.
     * 
     * @param dealId the deal ID
     * @param salesRepId the sales representative ID
     * @param baseCommission the base commission amount
     * @param expectedGross the expected gross commission
     */
    @ParameterizedTest(name = "Commission: Deal={0}, Rep={1}, Base=${2}, Expected=${3}")
    @CsvSource({
        "DEAL001, REP001, 1000.00, 1000.00",
        "DEAL002, REP002, 2500.50, 2500.50",
        "DEAL003, REP001, 5000.00, 5000.00",
        "DEAL004, REP003, 0.01, 0.01",
        "DEAL005, REP002, 15000.00, 15000.00"
    })
    void testCommissionCalculationScenarios(String dealId, String salesRepId, double baseCommission, double expectedGross) {
        // Arrange
        BigDecimal base = BigDecimal.valueOf(baseCommission);
        BigDecimal expected = BigDecimal.valueOf(expectedGross);
        
        // Act
        CommissionCalculation calculation = new CommissionCalculation(dealId, salesRepId, base);
        calculation.recalculate();
        
        // Assert
        assertNotNull(calculation, "Commission calculation should not be null");
        assertEquals(dealId, calculation.getDealId(), "Deal ID should match");
        assertEquals(salesRepId, calculation.getSalesRepId(), "Sales rep ID should match");
        assertEquals(0, base.compareTo(calculation.getBaseCommission()), "Base commission should match");
        assertEquals(0, expected.compareTo(calculation.getGrossCommission()), "Gross commission should match expected");
    }

    /**
     * Tests commission calculation scenarios using @CsvFileSource.
     * Shows testing business logic with multiple input parameters.
     *
     * @param dealId the deal ID
     * @param salesRepId the sales representative ID
     * @param baseCommission the base commission amount
     * @param expectedGross the expected gross commission
     */
    @ParameterizedTest(name = "Commission: Deal={0}, Rep={1}, Base=${2}, Expected=${3}")
    @CsvFileSource(resources = "/deals.csv", numLinesToSkip = 1, useHeadersInDisplayName = true)
    void testCommissionCalculationScenariosFromFile(String dealId, String salesRepId, double baseCommission, double expectedGross) {
        // Arrange
        BigDecimal base = BigDecimal.valueOf(baseCommission);
        BigDecimal expected = BigDecimal.valueOf(expectedGross);

        // Act
        CommissionCalculation calculation = new CommissionCalculation(dealId, salesRepId, base);
        calculation.recalculate();

        // Assert
        assertNotNull(calculation, "Commission calculation should not be null");
        assertEquals(dealId, calculation.getDealId(), "Deal ID should match");
        assertEquals(salesRepId, calculation.getSalesRepId(), "Sales rep ID should match");
        assertEquals(0, base.compareTo(calculation.getBaseCommission()), "Base commission should match");
        assertEquals(0, expected.compareTo(calculation.getGrossCommission()), "Gross commission should match expected");
    }

    /**
     * Tests deal equality and hash code with various ID combinations using @MethodSource.
     * 
     * @MethodSource allows complex object creation and provides maximum flexibility
     * for test data generation. It's perfect when you need to create complex
     * test scenarios that can't be easily expressed with simple annotations.
     * 
     * @param deal1 the first deal to compare
     * @param deal2 the second deal to compare
     * @param shouldBeEqual whether the deals should be equal
     */
    @ParameterizedTest(name = "Deal equality test: {2}")
    @MethodSource("dealEqualityProvider")
    void testDealEquality(Deal deal1, Deal deal2, boolean shouldBeEqual) {
        // Act & Assert
        if (shouldBeEqual) {
            assertEquals(deal1, deal2, "Deals should be equal");
            assertEquals(deal1.hashCode(), deal2.hashCode(), "Hash codes should be equal");
        } else {
            assertNotEquals(deal1, deal2, "Deals should not be equal");
        }
    }

    /**
     * Provides test data for deal equality tests.
     * This method demonstrates how to create complex test scenarios
     * with detailed object construction.
     * 
     * @return Stream of test arguments for deal equality testing
     */
    static Stream<Arguments> dealEqualityProvider() {
        return Stream.of(
            // Same ID - should be equal
            Arguments.of(
                createDealWithId("DEAL001", "First Deal"),
                createDealWithId("DEAL001", "Different Title"),
                true
            ),
            // Different IDs - should not be equal
            Arguments.of(
                createDealWithId("DEAL001", "Deal One"),
                createDealWithId("DEAL002", "Deal Two"),
                false
            ),
            // Both null IDs - should not be equal (per business rule)
            Arguments.of(
                createDealWithId(null, "Deal Without ID"),
                createDealWithId(null, "Another Deal Without ID"),
                false
            ),
            // One null ID - should not be equal
            Arguments.of(
                createDealWithId("DEAL001", "Deal With ID"),
                createDealWithId(null, "Deal Without ID"),
                false
            )
        );
    }

    /**
     * Tests user role combinations using @MethodSource.
     * Demonstrates testing complex object states and behaviors.
     * 
     * @param user the user to test
     * @param expectedSalesRole whether user should have sales role
     * @param expectedAdminRole whether user should have admin role
     */
    @ParameterizedTest(name = "User role test: {0}")
    @MethodSource("userRoleProvider")
    void testUserRoleCombinations(User user, boolean expectedSalesRole, boolean expectedAdminRole) {
        // Assert
        assertEquals(expectedSalesRole, user.isSalesRep() || user.isSalesManager(), 
            "Sales role check should match expected");
        assertEquals(expectedAdminRole, user.isSystemAdmin() || user.isFinanceAdmin(), 
            "Admin role check should match expected");
        
        // Additional role-specific assertions
        if (expectedSalesRole) {
            assertTrue(user.hasRole(UserRole.SALES_REP) || user.hasRole(UserRole.SALES_MANAGER),
                "User should have at least one sales role");
        }
        
        if (expectedAdminRole) {
            assertTrue(user.hasRole(UserRole.SYSTEM_ADMIN) || user.hasRole(UserRole.FINANCE_ADMIN),
                "User should have at least one admin role");
        }
    }

    /**
     * Provides test data for user role combination tests.
     * Shows how to create users with different role configurations.
     * 
     * @return Stream of test arguments for user role testing
     */
    static Stream<Arguments> userRoleProvider() {
        return Stream.of(
            // Sales representative
            Arguments.of(
                createUserWithRoles("sales.rep", UserRole.SALES_REP),
                true,  // has sales role
                false  // no admin role
            ),
            // Sales manager
            Arguments.of(
                createUserWithRoles("sales.manager", UserRole.SALES_MANAGER),
                true,  // has sales role
                false  // no admin role
            ),
            // System administrator
            Arguments.of(
                createUserWithRoles("sys.admin", UserRole.SYSTEM_ADMIN),
                false, // no sales role
                true   // has admin role
            ),
            // Finance administrator
            Arguments.of(
                createUserWithRoles("finance.admin", UserRole.FINANCE_ADMIN),
                false, // no sales role
                true   // has admin role
            ),
            // Multiple roles - sales and admin
            Arguments.of(
                createUserWithRoles("multi.user", UserRole.SALES_MANAGER, UserRole.FINANCE_ADMIN),
                true,  // has sales role
                true   // has admin role
            ),
            // User with no roles
            Arguments.of(
                createUserWithRoles("basic.user"),
                false, // no sales role
                false  // no admin role
            )
        );
    }

    /**
     * Tests commission calculation with bonus scenarios using @MethodSource.
     * Demonstrates testing complex business calculations.
     * 
     * @param calculation the commission calculation to test
     * @param expectedTotal the expected total commission
     */
    @ParameterizedTest(name = "Commission with bonuses: expected ${1}")
    @MethodSource("commissionBonusProvider")
    void testCommissionWithBonuses(CommissionCalculation calculation, BigDecimal expectedTotal) {
        // Act
        BigDecimal actualTotal = calculation.calculateTotalCommission();
        
        // Assert
        assertNotNull(actualTotal, "Total commission should not be null");
        assertEquals(0, expectedTotal.compareTo(actualTotal), 
            "Expected " + expectedTotal + " but got " + actualTotal);
        
        // Verify calculation components
        assertNotNull(calculation.getBaseCommission(), "Base commission should be set");
        assertNotNull(calculation.getBonuses(), "Bonuses list should not be null");
    }

    /**
     * Provides test data for commission bonus calculation tests.
     * Shows how to create complex commission scenarios with bonuses.
     * 
     * @return Stream of test arguments for commission bonus testing
     */
    static Stream<Arguments> commissionBonusProvider() {
        return Stream.of(
            // Base commission only
            Arguments.of(
                createCommissionWithBonuses("DEAL001", "REP001", new BigDecimal("1000")),
                new BigDecimal("1000")
            ),
            // Base commission with single bonus
            Arguments.of(
                createCommissionWithBonuses("DEAL002", "REP002", new BigDecimal("1000"), 
                    new BigDecimal("200")),
                new BigDecimal("1200")
            ),
            // Base commission with multiple bonuses
            Arguments.of(
                createCommissionWithBonuses("DEAL003", "REP003", new BigDecimal("1500"), 
                    new BigDecimal("300"), new BigDecimal("150")),
                new BigDecimal("1950")
            ),
            // High value commission with bonuses
            Arguments.of(
                createCommissionWithBonuses("DEAL004", "REP001", new BigDecimal("5000"), 
                    new BigDecimal("1000"), new BigDecimal("500"), new BigDecimal("250")),
                new BigDecimal("6750")
            )
        );
    }

    /**
     * Tests deal creation with null and empty titles using @NullAndEmptySource.
     * 
     * @NullAndEmptySource automatically provides null and empty string values.
     * This is perfect for testing edge cases and input validation scenarios.
     * It helps ensure your code handles boundary conditions gracefully.
     * 
     * @param title the title to test (null or empty)
     */
    @ParameterizedTest(name = "Deal with invalid title: {0}")
    @NullAndEmptySource
    void testDealWithInvalidTitles(String title) {
        // Act
        Deal deal = new Deal(title, new BigDecimal("1000"), "REP001");
        
        // Assert
        assertEquals(title, deal.getTitle(), "Title should be stored as provided");
        assertNotNull(deal.getValue(), "Value should not be null");
        assertNotNull(deal.getSalesRepId(), "Sales rep ID should not be null");
        assertEquals(DealStatus.OPEN, deal.getStatus(), "Status should be OPEN for new deals");
        
        // Verify deal can still function with invalid title
        assertNotNull(deal.toString(), "toString should work even with invalid title");
    }

    /**
     * Tests user email validation with null and empty values using @NullAndEmptySource.
     * Shows how to test validation logic for required fields.
     * 
     * @param email the email to test (null or empty)
     */
    @ParameterizedTest(name = "User with invalid email: '{0}'")
    @NullAndEmptySource
    void testUserWithInvalidEmail(String email) {
        // Act
        User user = new User("testuser", email, "Test", "User");
        
        // Assert
        assertEquals(email, user.getEmail(), "Email should be stored as provided");
        assertNotNull(user.getUsername(), "Username should not be null");
        assertTrue(user.isActive(), "User should be active by default");
        
        // User should still be functional even with invalid email
        assertNotNull(user.getFullName(), "Full name should be available");
        assertNotNull(user.toString(), "toString should work");
    }

    // ================================
    // Helper Methods for Test Data Creation
    // ================================

    /**
     * Helper method to create a deal with a specific ID.
     * Simplifies test data creation for equality testing.
     * 
     * @param id the deal ID
     * @param title the deal title
     * @return a new deal with the specified ID and title
     */
    private static Deal createDealWithId(String id, String title) {
        Deal deal = new Deal(title, new BigDecimal("1000"), "REP001");
        deal.setId(id);
        return deal;
    }

    /**
     * Helper method to create a user with specific roles.
     * Simplifies test data creation for role testing.
     * 
     * @param username the username
     * @param roles the roles to assign to the user
     * @return a new user with the specified roles
     */
    private static User createUserWithRoles(String username, UserRole... roles) {
        User user = new User(username, username + "@company.com", "Test", "User");
        for (UserRole role : roles) {
            user.addRole(role);
        }
        return user;
    }

    /**
     * Helper method to create a commission calculation with bonuses.
     * Simplifies test data creation for commission testing.
     * 
     * @param dealId the deal ID
     * @param salesRepId the sales rep ID
     * @param baseCommission the base commission amount
     * @param bonusAmounts the bonus amounts to add
     * @return a new commission calculation with the specified bonuses
     */
    private static CommissionCalculation createCommissionWithBonuses(String dealId, String salesRepId, 
                                                                   BigDecimal baseCommission, BigDecimal... bonusAmounts) {
        CommissionCalculation calculation = new CommissionCalculation(dealId, salesRepId, baseCommission);
        
        for (int i = 0; i < bonusAmounts.length; i++) {
            BonusCalculation bonus = new BonusCalculation("BONUS" + (i + 1), "Bonus " + (i + 1), bonusAmounts[i]);
            calculation.addBonus(bonus);
        }
        
        return calculation;
    }
}