package com.chapman.edu.commissions.fundamentals.assertions;

import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.PlanStatus;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class demonstrates the use of JUnit 5 assertions with the commission calculator model classes.
 * 
 * Assertions in JUnit are used to verify that the code under test behaves as expected.
 * When an assertion fails, the test fails, indicating that there is a problem with the code.
 * 
 * This class covers the following assertion methods:
 * 1. assertEquals() - Verifies that two values are equal
 * 2. assertNotEquals() - Verifies that two values are not equal
 * 3. assertTrue() - Verifies that a condition is true
 * 4. assertFalse() - Verifies that a condition is false
 * 5. assertNull() - Verifies that an object is null
 * 6. assertNotNull() - Verifies that an object is not null
 * 7. assertSame() - Verifies that two references point to the same object
 * 8. assertNotSame() - Verifies that two references do not point to the same object
 * 9. assertThrows() - Verifies that a specific exception is thrown
 * 10. assertDoesNotThrow() - Verifies that no exception is thrown
 * 11. assertAll() - Groups multiple assertions together
 * 12. assertArrayEquals() - Verifies that two arrays are equal
 * 13. assertIterableEquals() - Verifies that two iterables are deeply equal
 */
public class JUnitAssertionsTest {

    private User user;
    private Deal deal;
    private CommissionPlan plan;
    private DealProduct product1;
    private DealProduct product2;

    /**
     * Set up test data before each test.
     */
    @BeforeEach
    public void setUp() {
        // Initialize a user
        user = new User();
        user.setId("test-user");
        user.setUsername("test.user");
        user.setEmail("test.user@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.addRole(UserRole.SALES_REP);

        // Initialize a deal
        deal = new Deal();
        deal.setId("test-deal");
        deal.setTitle("Test Deal");
        deal.setValue(new BigDecimal("10000.00"));
        deal.setSalesRepId(user.getId());
        deal.setStatus(DealStatus.OPEN);

        // Initialize deal products
        product1 = new DealProduct();
        product1.setId("product1");
        product1.setProductName("Product 1");
        product1.setPrice(new BigDecimal("1000.00"));
        product1.setQuantity(2);

        product2 = new DealProduct();
        product2.setId("product2");
        product2.setProductName("Product 2");
        product2.setPrice(new BigDecimal("500.00"));
        product2.setQuantity(3);

        // Add products to the deal
        deal.addProduct(product1);
        deal.addProduct(product2);

        // Initialize a commission plan
        plan = new CommissionPlan();
        plan.setId("test-plan");
        plan.setName("Test Plan");
        plan.setCurrency(Currency.getInstance("USD"));
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setEffectiveStartDate(LocalDate.now().minusMonths(1));
        plan.setEffectiveEndDate(LocalDate.now().plusMonths(1));
    }

    /**
     * Demonstrates the use of assertEquals().
     * 
     * assertEquals() verifies that two values are equal.
     * For objects, it uses the equals() method for comparison.
     */
    @Test
    public void testAssertEquals() {
        // Test with primitive values
        assertEquals(10000.00, deal.getValue().doubleValue(), 
                "Deal value should be 10000.00");

        // Test with strings
        assertEquals("Test User", user.getFullName(), 
                "User's full name should be 'Test User'");

        // Test with objects
        User sameUser = new User();
        sameUser.setId("test-user");
        assertEquals(user, sameUser, 
                "Users with the same ID should be equal");

        // Test with BigDecimal
        assertEquals(0, new BigDecimal("3500.00").compareTo(deal.calculateTotalValue()), 
                "Deal total value should be 3500.00");
    }

    /**
     * Demonstrates the use of assertNotEquals().
     * 
     * assertNotEquals() verifies that two values are not equal.
     */
    @Test
    public void testAssertNotEquals() {
        // Test with primitive values
        assertNotEquals(5000.00, deal.getValue().doubleValue(), 
                "Deal value should not be 5000.00");

        // Test with strings
        assertNotEquals("John Doe", user.getFullName(), 
                "User's full name should not be 'John Doe'");

        // Test with objects
        User differentUser = new User();
        differentUser.setId("different-user");
        assertNotEquals(user, differentUser, 
                "Users with different IDs should not be equal");
    }

    /**
     * Demonstrates the use of assertTrue().
     * 
     * assertTrue() verifies that a condition is true.
     */
    @Test
    public void testAssertTrue() {
        // Test a boolean condition
        assertTrue(user.hasRole(UserRole.SALES_REP), 
                "User should have SALES_REP role");

        // Test a method that returns boolean
        assertTrue(user.isSalesRep(), 
                "User should be a sales rep");

        // Test a complex condition
        assertTrue(deal.getStatus() == DealStatus.OPEN && deal.getValue().doubleValue() > 5000, 
                "Deal should be open and have a value greater than 5000");

        // Test with plan
        assertTrue(plan.isActiveOn(LocalDate.now()), 
                "Plan should be active today");
    }

    /**
     * Demonstrates the use of assertFalse().
     * 
     * assertFalse() verifies that a condition is false.
     */
    @Test
    public void testAssertFalse() {
        // Test a boolean condition
        assertFalse(user.hasRole(UserRole.SYSTEM_ADMIN), 
                "User should not have SYSTEM_ADMIN role");

        // Test a method that returns boolean
        assertFalse(user.isSystemAdmin(), 
                "User should not be a system admin");

        // Test a complex condition
        assertFalse(deal.getStatus() == DealStatus.WON || deal.getValue().doubleValue() < 1000, 
                "Deal should not be won or have a value less than 1000");

        // Test with plan
        LocalDate futureDate = LocalDate.now().plusYears(1);
        assertFalse(plan.isActiveOn(futureDate), 
                "Plan should not be active in one year");
    }

    /**
     * Demonstrates the use of assertNull() and assertNotNull().
     * 
     * assertNull() verifies that an object is null.
     * assertNotNull() verifies that an object is not null.
     */
    @Test
    public void testAssertNullAndNotNull() {
        // Test assertNull
        User nullUser = null;
        assertNull(nullUser, "User should be null");

        // Test assertNotNull
        assertNotNull(user, "User should not be null");
        assertNotNull(user.getUsername(), "Username should not be null");

        // Test with deal
        assertNotNull(deal.getProducts(), "Products list should not be null");

        // Test with plan
        assertNotNull(plan.getEffectiveStartDate(), "Effective start date should not be null");
    }

    /**
     * Demonstrates the use of assertSame() and assertNotSame().
     * 
     * assertSame() verifies that two references point to the same object.
     * assertNotSame() verifies that two references do not point to the same object.
     */
    @Test
    public void testAssertSameAndNotSame() {
        // Create a reference to the same user object
        User sameUserReference = user;

        // Test assertSame
        assertSame(user, sameUserReference, 
                "Both references should point to the same user object");

        // Create a different user object with the same ID
        User differentUserObject = new User();
        differentUserObject.setId("test-user");

        // Test assertNotSame
        assertNotSame(user, differentUserObject, 
                "References should point to different user objects");

        // Note: Even though the objects are different, they are equal because they have the same ID
        assertEquals(user, differentUserObject, 
                "Users should be equal because they have the same ID");
    }

    /**
     * Demonstrates the use of assertThrows().
     * 
     * assertThrows() verifies that a specific exception is thrown.
     */
    @Test
    public void testAssertThrows() {
        // Test that NullPointerException is thrown when trying to call a method on a null object
        Deal nullDeal = null;
        assertThrows(NullPointerException.class, () -> {
            nullDeal.getId();
        }, "Calling a method on a null object should throw NullPointerException");

        // Test that NullPointerException is thrown when trying to add a product to a null list
        Deal dealWithNullProducts = new Deal();
        dealWithNullProducts.setProducts(null);
        assertThrows(NullPointerException.class, () -> {
            dealWithNullProducts.addProduct(new DealProduct());
        }, "Adding a product to a null products list should throw NullPointerException");

        // Test with a lambda expression that throws an exception
        assertThrows(IllegalArgumentException.class, () -> {
            validatePositiveAmount(new BigDecimal("-100.00"));
        }, "Validating a negative amount should throw IllegalArgumentException");
    }

    /**
     * Helper method that validates if an amount is positive.
     * Throws IllegalArgumentException if the amount is negative or zero.
     * 
     * @param amount the amount to validate
     * @throws IllegalArgumentException if the amount is negative or zero
     */
    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    /**
     * Demonstrates the use of assertDoesNotThrow().
     * 
     * assertDoesNotThrow() verifies that no exception is thrown.
     */
    @Test
    public void testAssertDoesNotThrow() {
        // Test that no exception is thrown when adding a valid product
        assertDoesNotThrow(() -> {
            DealProduct validProduct = new DealProduct();
            validProduct.setId("valid-product");
            deal.addProduct(validProduct);
        }, "Adding a valid product should not throw an exception");

        // Test with a lambda expression that should not throw an exception
        assertDoesNotThrow(() -> {
            user.setEmail("valid.email@example.com");
        }, "Setting a valid email should not throw an exception");
    }

    /**
     * Demonstrates the use of assertAll().
     * 
     * assertAll() groups multiple assertions together.
     * If one assertion fails, the others are still executed.
     */
    @Test
    public void testAssertAll() {
        // Test multiple assertions about the user
        assertAll("User properties",
            () -> assertEquals("test-user", user.getId(), "User ID should match"),
            () -> assertEquals("test.user", user.getUsername(), "Username should match"),
            () -> assertEquals("test.user@example.com", user.getEmail(), "Email should match"),
            () -> assertTrue(user.hasRole(UserRole.SALES_REP), "User should have SALES_REP role")
        );

        // Test multiple assertions about the deal
        assertAll("Deal properties",
            () -> assertEquals("test-deal", deal.getId(), "Deal ID should match"),
            () -> assertEquals("Test Deal", deal.getTitle(), "Deal title should match"),
            () -> assertEquals(DealStatus.OPEN, deal.getStatus(), "Deal status should be OPEN"),
            () -> assertEquals(2, deal.getProducts().size(), "Deal should have 2 products")
        );
    }

    /**
     * Demonstrates the use of assertArrayEquals().
     * 
     * assertArrayEquals() verifies that two arrays are equal.
     */
    @Test
    public void testAssertArrayEquals() {
        // Create arrays of user roles
        UserRole[] expectedRoles = {UserRole.SALES_REP};
        UserRole[] actualRoles = user.getRoles().toArray(new UserRole[0]);

        // Test that the arrays are equal
        assertArrayEquals(expectedRoles, actualRoles, 
                "User roles array should match expected roles");

        // Test with primitive arrays
        int[] expectedQuantities = {2, 3};
        int[] actualQuantities = {
            deal.getProducts().get(0).getQuantity(),
            deal.getProducts().get(1).getQuantity()
        };

        assertArrayEquals(expectedQuantities, actualQuantities, 
                "Product quantities array should match expected quantities");
    }

    /**
     * Demonstrates the use of assertIterableEquals().
     * 
     * assertIterableEquals() verifies that two iterables are deeply equal.
     */
    @Test
    public void testAssertIterableEquals() {
        // Create lists of products
        List<DealProduct> expectedProducts = Arrays.asList(product1, product2);
        List<DealProduct> actualProducts = deal.getProducts();

        // Test that the lists are equal
        assertIterableEquals(expectedProducts, actualProducts, 
                "Products list should match expected products");

        // Test with a different order (this will fail because order matters)
        List<DealProduct> differentOrderProducts = Arrays.asList(product2, product1);
        assertNotEquals(differentOrderProducts, actualProducts, 
                "Products list in different order should not be equal");
    }
}
