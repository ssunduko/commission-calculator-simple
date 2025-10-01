package com.chapman.edu.commissions.fundamentals.tagged;

import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.PlanStatus;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class demonstrates the use of JUnit 5 tags with the commission calculator model classes.
 * 
 * JUnit tags are used to categorize tests and selectively run them. Tags can be used to:
 * 1. Group related tests together
 * 2. Run only specific categories of tests
 * 3. Exclude certain categories of tests from execution
 * 4. Configure different test execution settings based on tags
 * 
 * This class covers the following tag-related concepts:
 * 1. Tagging individual test methods
 * 2. Tagging test classes
 * 3. Using multiple tags
 * 4. Filtering tests by tags
 */
@Tag("model")
public class JUnitTaggedTest {

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
     * A test method tagged as "user" that verifies user properties.
     * 
     * This test can be run selectively by including the "user" tag.
     */
    @Test
    @Tag("user")
    public void testUserProperties() {
        assertEquals("test-user", user.getId(), "User ID should match");
        assertEquals("test.user", user.getUsername(), "Username should match");
        assertEquals("test.user@example.com", user.getEmail(), "Email should match");
        assertTrue(user.hasRole(UserRole.SALES_REP), "User should have SALES_REP role");
    }

    /**
     * A test method tagged as "user" and "role" that verifies user role functionality.
     * 
     * This test can be run selectively by including either the "user" or "role" tag.
     */
    @Test
    @Tag("user")
    @Tag("role")
    public void testUserRoles() {
        // Add another role to the user
        user.addRole(UserRole.SALES_MANAGER);

        // Verify the user has both roles
        assertTrue(user.hasRole(UserRole.SALES_REP), "User should have SALES_REP role");
        assertTrue(user.hasRole(UserRole.SALES_MANAGER), "User should have SALES_MANAGER role");

        // Test role checking methods
        assertTrue(user.isSalesRep(), "User should be a sales rep");
        assertTrue(user.isSalesManager(), "User should be a sales manager");
    }

    /**
     * A test method tagged as "deal" that verifies deal properties.
     * 
     * This test can be run selectively by including the "deal" tag.
     */
    @Test
    @Tag("deal")
    public void testDealProperties() {
        assertEquals("test-deal", deal.getId(), "Deal ID should match");
        assertEquals("Test Deal", deal.getTitle(), "Deal title should match");
        assertEquals(new BigDecimal("10000.00"), deal.getValue(), "Deal value should match");
        assertEquals(user.getId(), deal.getSalesRepId(), "Deal sales rep ID should match");
        assertEquals(DealStatus.OPEN, deal.getStatus(), "Deal status should be OPEN");
    }

    /**
     * A test method tagged as "deal" and "product" that verifies deal product functionality.
     * 
     * This test can be run selectively by including either the "deal" or "product" tag.
     */
    @Test
    @Tag("deal")
    @Tag("product")
    public void testDealProducts() {
        // Verify the product was added to the deal
        assertEquals(1, deal.getProducts().size(), "Deal should have one product");
        assertEquals(product, deal.getProducts().get(0), "Deal product should match");

        // Add another product to the deal
        DealProduct anotherProduct = new DealProduct();
        anotherProduct.setId("another-product");
        anotherProduct.setProductName("Another Product");
        anotherProduct.setPrice(new BigDecimal("2000.00"));
        anotherProduct.setQuantity(1);
        deal.addProduct(anotherProduct);

        // Verify both products are in the deal
        assertEquals(2, deal.getProducts().size(), "Deal should have two products");
        assertTrue(deal.getProducts().contains(product), "Deal should contain the first product");
        assertTrue(deal.getProducts().contains(anotherProduct), "Deal should contain the second product");
    }

    /**
     * A test method tagged as "plan" that verifies commission plan properties.
     * 
     * This test can be run selectively by including the "plan" tag.
     */
    @Test
    @Tag("plan")
    public void testCommissionPlanProperties() {
        assertEquals("test-plan", plan.getId(), "Plan ID should match");
        assertEquals("Test Plan", plan.getName(), "Plan name should match");
        assertEquals(Currency.getInstance("USD"), plan.getCurrency(), "Plan currency should match");
        assertEquals(PlanStatus.ACTIVE, plan.getStatus(), "Plan status should be ACTIVE");
        assertTrue(plan.getStatus() == PlanStatus.ACTIVE, "Plan should be active");
    }

    /**
     * A test method tagged as "plan" and "date" that verifies commission plan date functionality.
     * 
     * This test can be run selectively by including either the "plan" or "date" tag.
     */
    @Test
    @Tag("plan")
    @Tag("date")
    public void testCommissionPlanDates() {
        // Verify the plan is active for the current date
        LocalDate today = LocalDate.now();
        assertTrue(plan.isActiveOn(today), "Plan should be active today");

        // Verify the plan is not active before the start date
        LocalDate beforeStart = plan.getEffectiveStartDate().minusDays(1);
        assertFalse(plan.isActiveOn(beforeStart), "Plan should not be active before start date");

        // Verify the plan is not active after the end date
        LocalDate afterEnd = plan.getEffectiveEndDate().plusDays(1);
        assertFalse(plan.isActiveOn(afterEnd), "Plan should not be active after end date");
    }

    /**
     * A test method tagged as "integration" that verifies the integration between user, deal, and plan.
     * 
     * This test can be run selectively by including the "integration" tag.
     */
    @Test
    @Tag("integration")
    public void testUserDealPlanIntegration() {
        // Create a new deal for the user
        Deal userDeal = new Deal();
        userDeal.setId("user-deal");
        userDeal.setTitle("User Deal");
        userDeal.setValue(new BigDecimal("5000.00"));
        userDeal.setSalesRepId(user.getId());
        userDeal.setStatus(DealStatus.OPEN);

        // Verify the deal is associated with the user
        assertEquals(user.getId(), userDeal.getSalesRepId(), "Deal should be associated with the user");

        // Verify the plan is active for the deal
        LocalDate dealDate = LocalDate.now();
        assertTrue(plan.isActiveOn(dealDate), "Plan should be active on the deal date");
    }

    /**
     * A test method tagged as "performance" that verifies the performance of deal operations.
     * 
     * This test can be run selectively by including the "performance" tag.
     * In a real-world scenario, performance tests might be excluded from regular test runs.
     */
    @Test
    @Tag("performance")
    public void testDealPerformance() {
        // Simulate adding many products to a deal
        Deal largeDeal = new Deal();
        largeDeal.setId("large-deal");
        largeDeal.setTitle("Large Deal");
        largeDeal.setValue(new BigDecimal("100000.00"));
        largeDeal.setSalesRepId(user.getId());
        largeDeal.setStatus(DealStatus.OPEN);

        // Add 100 products to the deal
        for (int i = 0; i < 100; i++) {
            DealProduct p = new DealProduct();
            p.setId("product-" + i);
            p.setProductName("Product " + i);
            p.setPrice(new BigDecimal("100.00"));
            p.setQuantity(1);
            largeDeal.addProduct(p);
        }

        // Verify all products were added
        assertEquals(100, largeDeal.getProducts().size(), "Large deal should have 100 products");
    }
}
