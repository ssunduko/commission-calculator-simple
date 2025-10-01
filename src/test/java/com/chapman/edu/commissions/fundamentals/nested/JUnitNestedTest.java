package com.chapman.edu.commissions.fundamentals.nested;

import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.PlanStatus;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class demonstrates the use of JUnit 5 nested tests with the commission calculator model classes.
 * 
 * Nested tests in JUnit 5 allow you to express the relationship between groups of tests.
 * They provide several benefits:
 * 1. Better organization - Tests can be grouped by functionality or test scenarios
 * 2. Shared setup - Inner classes can share setup code from outer classes
 * 3. Readability - Test reports show the hierarchical structure of tests
 * 4. Context - Tests can be written in a context-specific way
 * 
 * This class demonstrates nested tests for User, Deal, and CommissionPlan classes.
 */
@DisplayName("JUnit Nested Tests Demo")
public class JUnitNestedTest {

    /**
     * The outer class can contain setup methods that are shared by all nested classes.
     * This is useful for common setup code.
     */
    @BeforeEach
    void setUp() {
        // Common setup code if needed
        System.out.println("Setting up the test environment");
    }

    /**
     * A simple test in the outer class.
     */
    @Test
    @DisplayName("Simple test in outer class")
    void testInOuterClass() {
        assertTrue(true, "This test should always pass");
    }

    /**
     * Nested class for User tests.
     * This demonstrates how to group tests related to the User class.
     */
    @Nested
    @DisplayName("User Tests")
    class UserTests {
        private User user;

        /**
         * Setup method specific to User tests.
         * This will be executed before each test in this nested class.
         */
        @BeforeEach
        void setUpUser() {
            user = new User();
            user.setId("test-user");
            user.setUsername("test.user");
            user.setEmail("test.user@example.com");
            user.setFirstName("Test");
            user.setLastName("User");
        }

        /**
         * Test basic user properties.
         */
        @Test
        @DisplayName("Test user properties")
        void testUserProperties() {
            assertEquals("test-user", user.getId(), "User ID should match");
            assertEquals("test.user", user.getUsername(), "Username should match");
            assertEquals("test.user@example.com", user.getEmail(), "Email should match");
            assertEquals("Test", user.getFirstName(), "First name should match");
            assertEquals("User", user.getLastName(), "Last name should match");
        }

        /**
         * Further nested class for role-specific tests.
         * This demonstrates how to create deeper levels of nesting.
         */
        @Nested
        @DisplayName("User Role Tests")
        class UserRoleTests {

            /**
             * Setup method specific to role tests.
             * This will be executed after the outer setup methods.
             */
            @BeforeEach
            void setUpRoles() {
                // Add roles to the user
                user.addRole(UserRole.SALES_REP);
            }

            /**
             * Test that the user has the SALES_REP role.
             */
            @Test
            @DisplayName("Test user has SALES_REP role")
            void testUserHasSalesRepRole() {
                assertTrue(user.hasRole(UserRole.SALES_REP), "User should have SALES_REP role");
                assertTrue(user.isSalesRep(), "User should be a sales rep");
            }

            /**
             * Test that the user does not have other roles.
             */
            @Test
            @DisplayName("Test user does not have other roles")
            void testUserDoesNotHaveOtherRoles() {
                assertFalse(user.hasRole(UserRole.SYSTEM_ADMIN), "User should not have SYSTEM_ADMIN role");
                assertFalse(user.isSalesManager(), "User should not be a sales manager");
                assertFalse(user.isFinanceAdmin(), "User should not be a finance admin");
                assertFalse(user.isSystemAdmin(), "User should not be a system admin");
            }
        }
    }

    /**
     * Nested class for Deal tests.
     * This demonstrates how to group tests related to the Deal class.
     */
    @Nested
    @DisplayName("Deal Tests")
    class DealTests {
        private Deal deal;
        private User salesRep;

        /**
         * Test basic deal properties.
         */
        @Test
        @DisplayName("Test deal properties")
        void testDealProperties() {
            assertEquals("test-deal", deal.getId(), "Deal ID should match");
            assertEquals("Test Deal", deal.getTitle(), "Deal title should match");
            assertEquals(0, new BigDecimal("10000.00").compareTo(deal.getValue()), "Deal value should match");
            assertEquals("sales-rep", deal.getSalesRepId(), "Sales rep ID should match");
            assertEquals(DealStatus.OPEN, deal.getStatus(), "Deal status should be OPEN");
        }

        /**
         * Nested class for deal product tests.
         */
        @Nested
        @DisplayName("Deal Product Tests")
        class DealProductTests {
            private DealProduct product1;
            private DealProduct product2;

            /**
             * Setup method specific to product tests.
             */
            @BeforeEach
            void setUpProducts() {
                // Create products
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
            }

            /**
             * Test that products were added to the deal.
             */
            @Test
            @DisplayName("Test products were added to deal")
            void testProductsAddedToDeal() {
                assertEquals(2, deal.getProducts().size(), "Deal should have 2 products");
                assertTrue(deal.getProducts().contains(product1), "Deal should contain product1");
                assertTrue(deal.getProducts().contains(product2), "Deal should contain product2");
            }

            /**
             * Test the calculation of total deal value.
             */
            @Test
            @DisplayName("Test deal total value calculation")
            void testDealTotalValueCalculation() {
                // Expected: (1000 * 2) + (500 * 3) = 2000 + 1500 = 3500
                BigDecimal expectedTotal = new BigDecimal("3500.00");
                assertEquals(0, expectedTotal.compareTo(deal.calculateTotalValue()), 
                        "Deal total value should be 3500.00");
            }
        }

        /**
         * Nested class for deal status tests.
         */
        @Nested
        @DisplayName("Deal Status Tests")
        class DealStatusTests {

            /**
             * Test changing the deal status.
             */
            @Test
            @DisplayName("Test changing deal status")
            void testChangingDealStatus() {
                // Initially the deal is OPEN
                assertEquals(DealStatus.OPEN, deal.getStatus(), "Deal should initially be OPEN");

                // Change to WON
                deal.setStatus(DealStatus.WON);
                assertEquals(DealStatus.WON, deal.getStatus(), "Deal status should be changed to WON");

                // Change to LOST
                deal.setStatus(DealStatus.LOST);
                assertEquals(DealStatus.LOST, deal.getStatus(), "Deal status should be changed to LOST");
            }
        }
        /**
         * Setup method specific to Deal tests.
         */
        @BeforeEach
        void setUpDeal() {
            // Create a sales rep
            salesRep = new User();
            salesRep.setId("sales-rep");
            salesRep.addRole(UserRole.SALES_REP);

            // Create a deal
            deal = new Deal();
            deal.setId("test-deal");
            deal.setTitle("Test Deal");
            deal.setValue(new BigDecimal("10000.00"));
            deal.setSalesRepId(salesRep.getId());
            deal.setStatus(DealStatus.OPEN);
        }
    }

    /**
     * Nested class for CommissionPlan tests.
     */
    @Nested
    @DisplayName("Commission Plan Tests")
    class CommissionPlanTests {
        private CommissionPlan plan;

        /**
         * Setup method specific to CommissionPlan tests.
         */
        @BeforeEach
        void setUpPlan() {
            plan = new CommissionPlan();
            plan.setId("test-plan");
            plan.setName("Test Plan");
            plan.setCurrency(Currency.getInstance("USD"));
            plan.setStatus(PlanStatus.DRAFT);
        }

        /**
         * Test basic plan properties.
         */
        @Test
        @DisplayName("Test plan properties")
        void testPlanProperties() {
            assertEquals("test-plan", plan.getId(), "Plan ID should match");
            assertEquals("Test Plan", plan.getName(), "Plan name should match");
            assertEquals(Currency.getInstance("USD"), plan.getCurrency(), "Currency should match");
            assertEquals(PlanStatus.DRAFT, plan.getStatus(), "Plan status should be DRAFT");
        }

        /**
         * Nested class for plan status tests.
         */
        @Nested
        @DisplayName("Plan Status Tests")
        class PlanStatusTests {

            /**
             * Test changing the plan status.
             */
            @Test
            @DisplayName("Test changing plan status")
            void testChangingPlanStatus() {
                // Initially the plan is DRAFT
                assertEquals(PlanStatus.DRAFT, plan.getStatus(), "Plan should initially be DRAFT");

                // Change to ACTIVE
                plan.setStatus(PlanStatus.ACTIVE);
                assertEquals(PlanStatus.ACTIVE, plan.getStatus(), "Plan status should be changed to ACTIVE");

                // Change to INACTIVE
                plan.setStatus(PlanStatus.INACTIVE);
                assertEquals(PlanStatus.INACTIVE, plan.getStatus(), "Plan status should be changed to INACTIVE");
            }
        }

        /**
         * Nested class for plan activation tests.
         */
        @Nested
        @DisplayName("Plan Activation Tests")
        class PlanActivationTests {

            /**
             * Setup method specific to activation tests.
             */
            @BeforeEach
            void setUpActivation() {
                plan.setStatus(PlanStatus.ACTIVE);
                plan.setEffectiveStartDate(LocalDate.now().minusMonths(1));
                plan.setEffectiveEndDate(LocalDate.now().plusMonths(1));
            }

            /**
             * Test that the plan is active on the current date.
             */
            @Test
            @DisplayName("Test plan is active on current date")
            void testPlanIsActiveOnCurrentDate() {
                assertTrue(plan.isActiveOn(LocalDate.now()), "Plan should be active today");
            }

            /**
             * Test that the plan is not active before the start date.
             */
            @Test
            @DisplayName("Test plan is not active before start date")
            void testPlanIsNotActiveBeforeStartDate() {
                LocalDate beforeStartDate = plan.getEffectiveStartDate().minusDays(1);
                assertFalse(plan.isActiveOn(beforeStartDate), "Plan should not be active before start date");
            }

            /**
             * Test that the plan is not active after the end date.
             */
            @Test
            @DisplayName("Test plan is not active after end date")
            void testPlanIsNotActiveAfterEndDate() {
                LocalDate afterEndDate = plan.getEffectiveEndDate().plusDays(1);
                assertFalse(plan.isActiveOn(afterEndDate), "Plan should not be active after end date");
            }
        }
    }
}
