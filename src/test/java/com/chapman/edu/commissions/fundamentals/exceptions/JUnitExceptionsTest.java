package com.chapman.edu.commissions.fundamentals.exceptions;

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
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class demonstrates the use of JUnit 5 exception testing with the commission calculator model classes.
 * 
 * Exception testing in JUnit is used to verify that code throws the expected exceptions under specific conditions.
 * This is important for ensuring that error handling works correctly and that the code fails in a predictable way.
 * 
 * This class covers the following exception testing methods:
 * 1. assertThrows() - Verifies that a specific exception is thrown
 * 2. assertDoesNotThrow() - Verifies that no exception is thrown
 * 3. Exception handling with try-catch blocks
 * 4. Testing exception messages
 * 5. Testing custom exceptions
 */
public class JUnitExceptionsTest {

    private User user;
    private Deal deal;
    private CommissionPlan plan;
    private DealProduct product;

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

        // Initialize a product
        product = new DealProduct();
        product.setId("test-product");
        product.setProductName("Test Product");
        product.setPrice(new BigDecimal("1000.00"));
        product.setQuantity(2);

        // Add product to the deal
        deal.addProduct(product);

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
     * Demonstrates the basic use of assertThrows().
     * 
     * assertThrows() verifies that a specific exception is thrown when executing a piece of code.
     * It returns the thrown exception, which can be used for further assertions.
     */
    @Test
    public void testBasicAssertThrows() {
        // Test that NullPointerException is thrown when trying to call a method on a null object
        Deal nullDeal = null;
        assertThrows(NullPointerException.class, () -> {
            nullDeal.getId();
        }, "Calling a method on a null object should throw NullPointerException");

        // Test that NullPointerException is thrown when trying to use a null product
        // Note: The Deal.addProduct method doesn't explicitly check if the product is null,
        // but a NullPointerException will occur when trying to use the null product later
        DealProduct nullProduct = null;
        assertThrows(NullPointerException.class, () -> {
            // This will throw NullPointerException when trying to access a method on the null product
            nullProduct.getId();
        }, "Calling a method on a null product should throw NullPointerException");
    }

    /**
     * Demonstrates how to test for NullPointerException when setting products to null.
     * 
     * The Deal.addProduct method uses Objects.requireNonNull to check that the products list is not null.
     */
    @Test
    public void testNullPointerExceptionWithRequireNonNull() {
        // Set the products list to null
        deal.setProducts(null);

        // Test that NullPointerException is thrown when trying to add a product to a null list
        Exception exception = assertThrows(NullPointerException.class, () -> {
            deal.addProduct(new DealProduct());
        }, "Adding a product to a null products list should throw NullPointerException");

        // Verify the exception message
        assertEquals("Products list cannot be null", exception.getMessage(),
                "Exception message should indicate that products list cannot be null");
    }

    /**
     * Demonstrates how to test for IllegalArgumentException.
     * 
     * This test creates a method that validates if an amount is positive and throws
     * IllegalArgumentException if it's not.
     */
    @Test
    public void testIllegalArgumentException() {
        // Test that IllegalArgumentException is thrown when validating a negative amount
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            validatePositiveAmount(new BigDecimal("-100.00"));
        }, "Validating a negative amount should throw IllegalArgumentException");

        // Verify the exception message
        assertEquals("Amount must be positive", exception.getMessage(),
                "Exception message should indicate that amount must be positive");

        // Test that IllegalArgumentException is thrown when validating a zero amount
        exception = assertThrows(IllegalArgumentException.class, () -> {
            validatePositiveAmount(BigDecimal.ZERO);
        }, "Validating a zero amount should throw IllegalArgumentException");

        // Verify the exception message
        assertEquals("Amount must be positive", exception.getMessage(),
                "Exception message should indicate that amount must be positive");

        // Test that IllegalArgumentException is thrown when validating a null amount
        exception = assertThrows(IllegalArgumentException.class, () -> {
            validatePositiveAmount(null);
        }, "Validating a null amount should throw IllegalArgumentException");

        // Verify the exception message
        assertEquals("Amount must be positive", exception.getMessage(),
                "Exception message should indicate that amount must be positive");
    }

    /**
     * Helper method that validates if an amount is positive.
     * Throws IllegalArgumentException if the amount is negative, zero, or null.
     * 
     * @param amount the amount to validate
     * @throws IllegalArgumentException if the amount is negative, zero, or null
     */
    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    /**
     * Demonstrates how to test for IndexOutOfBoundsException.
     * 
     * This test verifies that IndexOutOfBoundsException is thrown when trying to access
     * an element at an invalid index in a list.
     */
    @Test
    public void testIndexOutOfBoundsException() {
        // Test that IndexOutOfBoundsException is thrown when trying to access an invalid index
        assertThrows(IndexOutOfBoundsException.class, () -> {
            deal.getProducts().get(10); // There's only one product in the list
        }, "Accessing an invalid index should throw IndexOutOfBoundsException");
    }

    /**
     * Demonstrates how to test for ClassCastException.
     * 
     * This test verifies that ClassCastException is thrown when trying to cast an object
     * to an incompatible type.
     */
    @Test
    public void testClassCastException() {
        // Create an object that is not a Deal
        Object notADeal = "This is not a Deal";

        // Test that ClassCastException is thrown when trying to cast to Deal
        assertThrows(ClassCastException.class, () -> {
            Deal castedDeal = (Deal) notADeal;
        }, "Casting a String to Deal should throw ClassCastException");
    }

    /**
     * Demonstrates the use of assertDoesNotThrow().
     * 
     * assertDoesNotThrow() verifies that no exception is thrown when executing a piece of code.
     */
    @Test
    public void testAssertDoesNotThrow() {
        // Test that no exception is thrown when adding a valid product
        assertDoesNotThrow(() -> {
            DealProduct validProduct = new DealProduct();
            validProduct.setId("valid-product");
            deal.addProduct(validProduct);
        }, "Adding a valid product should not throw an exception");

        // Test that no exception is thrown when validating a positive amount
        assertDoesNotThrow(() -> {
            validatePositiveAmount(new BigDecimal("100.00"));
        }, "Validating a positive amount should not throw an exception");
    }

    /**
     * Demonstrates how to test exception handling with try-catch blocks.
     * 
     * This approach is useful when you need more control over the exception handling
     * or when you need to perform additional actions after catching the exception.
     */
    @Test
    public void testExceptionHandlingWithTryCatch() {
        // Set up a scenario that will throw an exception
        deal.setProducts(null);

        // Use try-catch to handle the exception
        try {
            deal.addProduct(new DealProduct());
            fail("Expected NullPointerException was not thrown");
        } catch (NullPointerException e) {
            // Verify the exception message
            assertEquals("Products list cannot be null", e.getMessage(),
                    "Exception message should indicate that products list cannot be null");
        }
    }

    /**
     * Demonstrates how to test for ArithmeticException.
     * 
     * This test verifies that ArithmeticException is thrown when performing an invalid
     * arithmetic operation, such as division by zero.
     */
    @Test
    public void testArithmeticException() {
        // Test that ArithmeticException is thrown when dividing by zero
        assertThrows(ArithmeticException.class, () -> {
            int result = 10 / 0;
        }, "Division by zero should throw ArithmeticException");
    }

    /**
     * Demonstrates how to test for NumberFormatException.
     * 
     * This test verifies that NumberFormatException is thrown when trying to parse
     * an invalid string as a number.
     */
    @Test
    public void testNumberFormatException() {
        // Test that NumberFormatException is thrown when parsing an invalid number
        assertThrows(NumberFormatException.class, () -> {
            int number = Integer.parseInt("not a number");
        }, "Parsing an invalid number should throw NumberFormatException");
    }

    /**
     * Demonstrates how to test for multiple exceptions in the same test.
     * 
     * This test verifies that different exceptions are thrown in different scenarios.
     */
    @Test
    public void testMultipleExceptions() {
        // Test for NullPointerException
        assertThrows(NullPointerException.class, () -> {
            String nullString = null;
            nullString.length();
        }, "Calling a method on a null string should throw NullPointerException");

        // Test for IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            validatePositiveAmount(new BigDecimal("-50.00"));
        }, "Validating a negative amount should throw IllegalArgumentException");

        // Test for IndexOutOfBoundsException
        assertThrows(IndexOutOfBoundsException.class, () -> {
            deal.getProducts().get(5);
        }, "Accessing an invalid index should throw IndexOutOfBoundsException");
    }
}
