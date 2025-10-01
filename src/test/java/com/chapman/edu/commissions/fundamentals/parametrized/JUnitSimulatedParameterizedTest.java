package com.chapman.edu.commissions.fundamentals.parametrized;

import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.PlanStatus;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class demonstrates how to implement parameterized tests in JUnit 5.
 * 
 * Parameterized tests allow you to run the same test multiple times with different arguments.
 * This is useful when you want to test a method with various inputs and expected outputs.
 * 
 * While JUnit 5 provides dedicated annotations for parameterized testing (like @ParameterizedTest,
 * @ValueSource, @EnumSource, etc.), this example shows how to achieve similar functionality
 * using standard JUnit features and plain Java.
 * 
 * The key concepts demonstrated are:
 * 1. Using arrays or collections to store test data
 * 2. Iterating through test data to run the same test logic multiple times
 * 3. Organizing test data in a way that's easy to maintain and extend
 */
public class JUnitSimulatedParameterizedTest {

    private User user;
    private Deal deal;
    private CommissionPlan commissionPlan;

    @BeforeEach
    public void setUp() {
        // Initialize a user
        user = new User("jsmith", "john.smith@example.com", "John", "Smith");

        // Initialize a deal
        deal = new Deal("Test Deal", new BigDecimal("1000.00"), "user123");

        // Initialize a commission plan
        commissionPlan = new CommissionPlan("Standard Plan", Currency.getInstance("USD"));
    }

    /**
     * Demonstrates how to parameterize tests with simple values.
     * 
     * This is similar to using @ValueSource in JUnit 5's parameterized tests.
     * We use an array of values and iterate through them to test the same functionality
     * with different inputs.
     * 
     * In this example, we test the User.setUsername method with different username values.
     */
    @Test
    @DisplayName("Test setting different usernames")
    public void testSetUsername() {
        // Define an array of test usernames (our test parameters)
        String[] usernames = {"user1", "john_doe", "test_user_123", "admin"};

        // Iterate through each username and test it
        for (String username : usernames) {
            // Set the username
            user.setUsername(username);

            // Verify the username was set correctly
            assertEquals(username, user.getUsername(), 
                    "Username should be set to the provided value: " + username);
        }
    }

    /**
     * Demonstrates how to parameterize tests with enum values.
     * 
     * This is similar to using @EnumSource in JUnit 5's parameterized tests.
     * We use the enum values directly and iterate through them.
     * 
     * In this example, we test the User.addRole and User.hasRole methods with different UserRole values.
     */
    @Test
    @DisplayName("Test adding different user roles")
    public void testAddRole() {
        // Use all values from the UserRole enum
        UserRole[] roles = UserRole.values();

        // Iterate through each role and test it
        for (UserRole role : roles) {
            // Create a fresh user for each test to avoid role accumulation
            User testUser = new User("username", "email@example.com", "Test", "User");

            // Add the role to the user
            testUser.addRole(role);

            // Verify the role was added correctly
            assertTrue(testUser.hasRole(role), 
                    "User should have the role after adding it: " + role);
        }
    }

    /**
     * Demonstrates how to parameterize tests with filtered enum values.
     * 
     * This is similar to using @EnumSource with filtering in JUnit 5's parameterized tests.
     * We manually filter the enum values we want to test.
     * 
     * In this example, we test only with admin roles (SYSTEM_ADMIN and FINANCE_ADMIN).
     */
    @Test
    @DisplayName("Test admin role checks")
    public void testAdminRoles() {
        // Define an array of admin roles to test
        UserRole[] adminRoles = {UserRole.SYSTEM_ADMIN, UserRole.FINANCE_ADMIN};

        // Iterate through each admin role and test it
        for (UserRole role : adminRoles) {
            // Create a fresh user for each test
            User testUser = new User("username", "email@example.com", "Test", "User");

            // Add the admin role
            testUser.addRole(role);

            // Verify the appropriate admin check method returns true
            if (role == UserRole.SYSTEM_ADMIN) {
                assertTrue(testUser.isSystemAdmin(), "User should be a system admin");
            } else if (role == UserRole.FINANCE_ADMIN) {
                assertTrue(testUser.isFinanceAdmin(), "User should be a finance admin");
            }
        }
    }

    /**
     * Demonstrates how to parameterize tests with multiple related values.
     * 
     * This is similar to using @CsvSource in JUnit 5's parameterized tests.
     * We create a custom class to hold related test data.
     * In this example, we test the User constructor and getFullName method with different first and last names.
     */
    @Test
    @DisplayName("Test full name generation with different names")
    public void testGetFullName() {
        // Define test data with first name, last name, and expected full name
        class NameTestCase {
            String firstName;
            String lastName;
            String expectedFullName;

            NameTestCase(String firstName, String lastName, String expectedFullName) {
                this.firstName = firstName;
                this.lastName = lastName;
                this.expectedFullName = expectedFullName;
            }
        }
        // Create an array of test cases
        NameTestCase[] testCases = {
            new NameTestCase("John", "Smith", "John Smith"),
            new NameTestCase("Jane", "Doe", "Jane Doe"),
            new NameTestCase("Robert", "Johnson", "Robert Johnson"),
            new NameTestCase("Emily", "Williams", "Emily Williams")
        };
        // Iterate through each test case
        for (NameTestCase testCase : testCases) {
            // Create a new user with the provided first and last name
            User testUser = new User("username", "email@example.com", 
                    testCase.firstName, testCase.lastName);
            // Verify the full name is generated correctly
            assertEquals(testCase.expectedFullName, testUser.getFullName(), 
                    "Full name should be correctly generated from first and last name: " + 
                    testCase.firstName + " " + testCase.lastName);
        }
    }

    /**
     * Demonstrates how to parameterize tests with complex objects.
     * 
     * This is similar to using @MethodSource in JUnit 5's parameterized tests.
     * We create a custom class to hold test data and expected results.
     * 
     * In this example, we test the Deal.calculateTotalValue method with different products.
     */
    @Test
    @DisplayName("Test calculating deal total value with different products")
    public void testCalculateTotalValue() {
        // Define a class to hold test data
        class DealTestCase {
            List<DealProduct> products;
            BigDecimal expectedTotal;

            DealTestCase(List<DealProduct> products, BigDecimal expectedTotal) {
                this.products = products;
                this.expectedTotal = expectedTotal;
            }
        }

        // Create test products
        DealProduct product1 = new DealProduct();
        product1.setProductName("Product 1");
        product1.setPrice(new BigDecimal("100.00"));
        product1.setQuantity(2);

        DealProduct product2 = new DealProduct();
        product2.setProductName("Product 2");
        product2.setPrice(new BigDecimal("50.00"));
        product2.setQuantity(3);

        DealProduct product3 = new DealProduct();
        product3.setProductName("Product 3");
        product3.setPrice(new BigDecimal("200.00"));
        product3.setQuantity(1);

        // Create test cases
        DealTestCase[] testCases = {
            // Test case 1: Single product
            new DealTestCase(List.of(product1), new BigDecimal("200.00")),

            // Test case 2: Multiple products
            new DealTestCase(List.of(product1, product2), new BigDecimal("350.00")),

            // Test case 3: More products
            new DealTestCase(List.of(product1, product2, product3), new BigDecimal("550.00")),

            // Test case 4: No products
            new DealTestCase(List.of(), BigDecimal.ZERO)
        };

        // Iterate through each test case
        for (DealTestCase testCase : testCases) {
            // Create a fresh deal for each test
            Deal testDeal = new Deal("Test Deal", new BigDecimal("1000.00"), "user123");

            // Add the products to the deal
            for (DealProduct product : testCase.products) {
                testDeal.addProduct(product);
            }

            // Verify the total value is calculated correctly
            assertEquals(testCase.expectedTotal, testDeal.calculateTotalValue(), 
                    "Deal total value should be calculated correctly for " + 
                    testCase.products.size() + " products");
        }
    }

    /**
     * Demonstrates another example of parameterized testing with complex data.
     * 
     * In this example, we test the CommissionPlan.isActiveOn method with different plan statuses,
     * effective dates, and test dates.
     */
    @Test
    @DisplayName("Test commission plan active status with different dates")
    public void testIsActiveOn() {
        // Define a class to hold test data
        class PlanTestCase {
            PlanStatus status;
            LocalDate startDate;
            LocalDate endDate;
            LocalDate testDate;
            boolean expectedResult;

            PlanTestCase(PlanStatus status, LocalDate startDate, LocalDate endDate, 
                        LocalDate testDate, boolean expectedResult) {
                this.status = status;
                this.startDate = startDate;
                this.endDate = endDate;
                this.testDate = testDate;
                this.expectedResult = expectedResult;
            }
        }

        // Create date variables for test cases
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate tomorrow = today.plusDays(1);
        LocalDate lastMonth = today.minusMonths(1);
        LocalDate nextMonth = today.plusMonths(1);

        // Create test cases
        PlanTestCase[] testCases = {
            // Test case 1: Active plan, within date range
            new PlanTestCase(PlanStatus.ACTIVE, yesterday, tomorrow, today, true),

            // Test case 2: Active plan, before start date
            new PlanTestCase(PlanStatus.ACTIVE, tomorrow, nextMonth, today, false),

            // Test case 3: Active plan, after end date
            new PlanTestCase(PlanStatus.ACTIVE, lastMonth, yesterday, today, false),

            // Test case 4: Draft plan, within date range
            new PlanTestCase(PlanStatus.DRAFT, yesterday, tomorrow, today, false),

            // Test case 5: Active plan, null start date, within end date
            new PlanTestCase(PlanStatus.ACTIVE, null, tomorrow, today, true),

            // Test case 6: Active plan, within start date, null end date
            new PlanTestCase(PlanStatus.ACTIVE, yesterday, null, today, true),

            // Test case 7: Active plan, null start and end dates
            new PlanTestCase(PlanStatus.ACTIVE, null, null, today, true)
        };

        // Iterate through each test case
        for (PlanTestCase testCase : testCases) {
            // Create a fresh commission plan for each test
            CommissionPlan testPlan = new CommissionPlan("Test Plan", Currency.getInstance("USD"));

            // Set up the commission plan
            testPlan.setStatus(testCase.status);
            testPlan.setEffectiveStartDate(testCase.startDate);
            testPlan.setEffectiveEndDate(testCase.endDate);

            // Verify the active status is determined correctly
            assertEquals(testCase.expectedResult, testPlan.isActiveOn(testCase.testDate), 
                    "Commission plan active status should be determined correctly for status=" + 
                    testCase.status + ", startDate=" + testCase.startDate + 
                    ", endDate=" + testCase.endDate + ", testDate=" + testCase.testDate);
        }
    }
}
