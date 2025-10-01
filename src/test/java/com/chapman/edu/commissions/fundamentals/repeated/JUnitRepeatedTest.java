package com.chapman.edu.commissions.fundamentals.repeated;

import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.PlanStatus;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class demonstrates the JUnit 5 repeated test functionality.
 * 
 * JUnit 5 provides the ability to repeat a test multiple times using the
 * @RepeatedTest annotation. This is useful for testing functionality that
 * might behave differently on different runs, or for performance testing.
 * 
 * The examples in this class use the model classes from the Commission Calculator
 * application to demonstrate repeated test concepts.
 */
public class JUnitRepeatedTest {

    // Test data
    private User user;
    private Deal deal;
    private CommissionPlan plan;
    private DealProduct product;

    /**
     * This method is executed before each test method.
     * It sets up fresh test data for each test.
     */
    @BeforeEach
    public void setUp() {
        // Initialize user
        user = new User();
        user.setId("test-user");
        user.setUsername("test.user");
        user.setEmail("test.user@example.com");
        user.addRole(UserRole.SALES_REP);

        // Initialize product
        product = new DealProduct();
        product.setId("test-product");
        product.setProductName("Test Product");
        product.setPrice(new BigDecimal("100.00"));
        product.setQuantity(1);

        // Initialize deal
        deal = new Deal();
        deal.setId("test-deal");
        deal.setTitle("Test Deal");
        deal.setStatus(DealStatus.OPEN);
        deal.setCreatedDate(LocalDate.now());
        deal.setSalesRepId(user.getId());
        deal.addProduct(product);

        // Initialize commission plan
        plan = new CommissionPlan();
        plan.setId("test-plan");
        plan.setName("Test Plan");
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setEffectiveStartDate(LocalDate.now());
        plan.setEffectiveEndDate(LocalDate.now().plusMonths(3));
        plan.setCurrency(Currency.getInstance("USD"));
    }

    /**
     * This is a basic repeated test that runs 5 times.
     * 
     * The @RepeatedTest annotation specifies how many times the test should be repeated.
     * Each repetition is reported separately in the test results.
     */
    @RepeatedTest(5)
    public void basicRepeatedTest() {
        // This test will run 5 times
        assertTrue(user.hasRole(UserRole.SALES_REP), "User should have SALES_REP role");
        assertEquals("test-deal", deal.getId(), "Deal ID should match");
        assertEquals(DealStatus.OPEN, deal.getStatus(), "Deal status should be OPEN");
    }

    /**
     * This repeated test uses a custom display name for better readability.
     * 
     * The @RepeatedTest annotation can include a custom display name pattern.
     * {displayName} refers to the method name
     * {currentRepetition} refers to the current repetition (1-based)
     * {totalRepetitions} refers to the total number of repetitions
     */
    @RepeatedTest(value = 3, name = "{displayName} - Repetition {currentRepetition}/{totalRepetitions}")
    @DisplayName("Test with custom name")
    public void repeatedTestWithCustomName() {
        // This test will run 3 times with a custom display name
        assertNotNull(user, "User should not be null");
        assertNotNull(deal, "Deal should not be null");
        assertNotNull(plan, "Plan should not be null");
    }

    /**
     * This repeated test uses the RepetitionInfo parameter to access information
     * about the current repetition.
     * 
     * RepetitionInfo provides:
     * - getCurrentRepetition(): the current repetition (1-based)
     * - getTotalRepetitions(): the total number of repetitions
     */
    @RepeatedTest(4)
    public void repeatedTestWithRepetitionInfo(RepetitionInfo repetitionInfo) {
        // Get the current repetition (1-based)
        int currentRepetition = repetitionInfo.getCurrentRepetition();

        // Get the total number of repetitions
        int totalRepetitions = repetitionInfo.getTotalRepetitions();

        System.out.println("Running repetition " + currentRepetition + " of " + totalRepetitions);

        // Add products based on the current repetition
        for (int i = 1; i < currentRepetition; i++) {
            DealProduct additionalProduct = new DealProduct();
            additionalProduct.setId("product-" + i);
            additionalProduct.setProductName("Product " + i);
            additionalProduct.setPrice(new BigDecimal("50.00"));
            additionalProduct.setQuantity(i);
            deal.addProduct(additionalProduct);
        }

        // Assert that the number of products matches the current repetition
        assertEquals(currentRepetition, deal.getProducts().size(), 
                "Deal should have " + currentRepetition + " products");
    }

    /**
     * This repeated test uses both RepetitionInfo and TestInfo parameters.
     * 
     * TestInfo provides:
     * - getDisplayName(): the display name of the test
     * - getTags(): the tags associated with the test
     * - getTestClass(): the class containing the test
     * - getTestMethod(): the test method
     */
    @RepeatedTest(value = 3, name = "Repetition {currentRepetition} - {displayName}")
    @DisplayName("Test with TestInfo")
    public void repeatedTestWithTestInfo(RepetitionInfo repetitionInfo, TestInfo testInfo) {
        System.out.println("Display name: " + testInfo.getDisplayName());
        System.out.println("Current repetition: " + repetitionInfo.getCurrentRepetition());

        // Modify the commission plan based on the current repetition
        int repetition = repetitionInfo.getCurrentRepetition();

        // Set the effective dates based on the current repetition
        LocalDate startDate = LocalDate.now().minusDays(repetition);
        LocalDate endDate = LocalDate.now().plusMonths(repetition);

        plan.setEffectiveStartDate(startDate);
        plan.setEffectiveEndDate(endDate);

        // Assert that the plan is active on the current date
        assertTrue(plan.isActiveOn(LocalDate.now()), 
                "Plan should be active on the current date");

        // Assert that the effective dates match what we set
        assertEquals(startDate, plan.getEffectiveStartDate(), 
                "Effective start date should match");
        assertEquals(endDate, plan.getEffectiveEndDate(), 
                "Effective end date should match");
    }

    /**
     * This test demonstrates how to simulate a performance test using repeated tests.
     * 
     * In this example, we're measuring how long it takes to add a large number of products
     * to a deal, and asserting that it completes within a reasonable time.
     */
    @RepeatedTest(3)
    public void performanceTest() {
        // Record the start time
        long startTime = System.currentTimeMillis();

        // Perform an operation that might take time
        List<DealProduct> products = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            DealProduct newProduct = new DealProduct();
            newProduct.setId("product-" + i);
            newProduct.setProductName("Product " + i);
            newProduct.setPrice(new BigDecimal("10.00"));
            newProduct.setQuantity(1);
            products.add(newProduct);
        }

        // Add all products to the deal
        for (DealProduct p : products) {
            deal.addProduct(p);
        }

        // Record the end time
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Assert that the operation completed within a reasonable time
        // This is just an example - the actual threshold would depend on your requirements
        assertTrue(duration < 1000, "Operation should complete within 1000ms, but took " + duration + "ms");

        // Assert that all products were added
        assertEquals(1001, deal.getProducts().size(), "Deal should have 1001 products (1000 new + 1 original)");
    }

    /**
     * This test demonstrates how to use repeated tests for testing with different data.
     * 
     * In this example, we're testing the commission calculation with different product quantities.
     */
    @RepeatedTest(5)
    public void repeatedTestWithDifferentData(RepetitionInfo repetitionInfo) {
        // Set the product quantity based on the current repetition
        int quantity = repetitionInfo.getCurrentRepetition() * 10;
        product.setQuantity(quantity);

        // Calculate the expected total price
        BigDecimal expectedTotal = product.getPrice().multiply(new BigDecimal(quantity));

        // Calculate the actual total price
        BigDecimal actualTotal = product.getPrice().multiply(new BigDecimal(product.getQuantity()));

        // Assert that the total price matches the expected value
        assertEquals(expectedTotal, actualTotal, 
                "Total price should be " + expectedTotal + " for quantity " + quantity);
    }
}
