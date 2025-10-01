package com.chapman.edu.commissions.fundamentals.conditional;

import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.PlanStatus;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class demonstrates the use of JUnit 5 conditional tests with the commission calculator model classes.
 * 
 * Conditional tests in JUnit 5 allow you to execute tests only if certain conditions are met.
 * This is useful for:
 * 1. Running tests only on specific operating systems
 * 2. Running tests only on specific Java versions
 * 3. Running tests only when certain system properties or environment variables are set
 * 4. Running tests based on custom conditions
 * 
 * JUnit 5 provides several annotations for conditional test execution:
 * - @EnabledOnOs / @DisabledOnOs
 * - @EnabledOnJre / @DisabledOnJre
 * - @EnabledIfSystemProperty / @DisabledIfSystemProperty
 * - @EnabledIfEnvironmentVariable / @DisabledIfEnvironmentVariable
 * - @EnabledIf / @DisabledIf
 */
@DisplayName("JUnit Conditional Tests Demo")
public class JUnitConditionalTest {

    private User user;
    private Deal deal;
    private CommissionPlan plan;

    /**
     * Set up test data before each test
     */
    @BeforeEach
    void setUp() {
        // Set up User
        user = new User();
        user.setId("test-user");
        user.setUsername("test.user");
        user.setEmail("test.user@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.addRole(UserRole.SALES_REP);

        // Set up Deal
        deal = new Deal();
        deal.setId("test-deal");
        deal.setTitle("Test Deal");
        deal.setValue(new BigDecimal("10000.00"));
        deal.setSalesRepId(user.getId());
        deal.setStatus(DealStatus.OPEN);

        // Add products to deal
        DealProduct product1 = new DealProduct();
        product1.setProductName("Product 1");
        product1.setPrice(new BigDecimal("5000.00"));
        product1.setQuantity(1);
        deal.addProduct(product1);

        DealProduct product2 = new DealProduct();
        product2.setProductName("Product 2");
        product2.setPrice(new BigDecimal("2500.00"));
        product2.setQuantity(2);
        deal.addProduct(product2);

        // Set up CommissionPlan
        plan = new CommissionPlan();
        plan.setId("test-plan");
        plan.setName("Test Plan");
        plan.setCurrency(Currency.getInstance("USD"));
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setEffectiveStartDate(LocalDate.now().minusDays(30));
        plan.setEffectiveEndDate(LocalDate.now().plusDays(30));
    }

    /**
     * This section demonstrates OS-specific tests using @EnabledOnOs and @DisabledOnOs
     * 
     * These annotations allow you to run tests only on specific operating systems.
     * This is useful for testing platform-specific features or behaviors.
     */

    @Test
    @EnabledOnOs(OS.WINDOWS)
    @DisplayName("This test runs only on Windows")
    void testEnabledOnWindows() {
        // This test will only run on Windows
        System.out.println("Running test on Windows");
        assertTrue(user.hasRole(UserRole.SALES_REP), "User should have SALES_REP role");
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    @DisplayName("This test runs only on Linux or Mac")
    void testEnabledOnLinuxOrMac() {
        // This test will only run on Linux or Mac
        System.out.println("Running test on Linux or Mac");
        assertTrue(user.hasRole(UserRole.SALES_REP), "User should have SALES_REP role");
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    @DisplayName("This test is disabled on Windows")
    void testDisabledOnWindows() {
        // This test will not run on Windows
        System.out.println("Running test on non-Windows OS");
        assertTrue(user.hasRole(UserRole.SALES_REP), "User should have SALES_REP role");
    }

    /**
     * This section demonstrates JRE-specific tests using @EnabledOnJre and @DisabledOnJre
     * 
     * These annotations allow you to run tests only on specific Java Runtime Environment versions.
     * This is useful for testing features that are only available in certain Java versions.
     */

    @Test
    @EnabledOnJre(JRE.JAVA_8)
    @DisplayName("This test runs only on Java 8")
    void testEnabledOnJava8() {
        // This test will only run on Java 8
        System.out.println("Running test on Java 8");
        assertEquals("Test Deal", deal.getTitle(), "Deal title should match");
    }

    @Test
    @EnabledOnJre({JRE.JAVA_11, JRE.JAVA_21})
    @DisplayName("This test runs only on Java 11 or Java 21")
    void testEnabledOnJava11Or17() {
        // This test will only run on Java 11 or Java 21
        System.out.println("Running test on Java 11 or Java 21");
        assertEquals("Test Deal", deal.getTitle(), "Deal title should match");
    }

    @Test
    @DisabledOnJre(JRE.JAVA_10)
    @DisplayName("This test is disabled on Java 10")
    void testDisabledOnJava10() {
        // This test will not run on Java 10
        System.out.println("Running test on Java version other than 10");
        assertEquals("Test Deal", deal.getTitle(), "Deal title should match");
    }

    /**
     * This section demonstrates system property conditions using @EnabledIfSystemProperty and @DisabledIfSystemProperty
     * 
     * These annotations allow you to run tests only when certain system properties have specific values.
     * This is useful for testing features that depend on system configuration.
     * 
     * To run these tests, you need to set the system properties:
     * -Denv=test or -Denv=prod
     */

    @Test
    @EnabledIfSystemProperty(named = "env", matches = "test")
    @DisplayName("This test runs only in test environment")
    void testEnabledInTestEnvironment() {
        // This test will only run if -Denv=test
        System.out.println("Running test in test environment");
        assertTrue(plan.isActiveOn(LocalDate.now()), "Plan should be active on current date");
    }

    @Test
    @DisabledIfSystemProperty(named = "env", matches = "prod")
    @DisplayName("This test is disabled in production environment")
    void testDisabledInProductionEnvironment() {
        // This test will not run if -Denv=prod
        System.out.println("Running test in non-production environment");
        assertTrue(plan.isActiveOn(LocalDate.now()), "Plan should be active on current date");
    }

    /**
     * This section demonstrates environment variable conditions using @EnabledIfEnvironmentVariable and @DisabledIfEnvironmentVariable
     * 
     * These annotations allow you to run tests only when certain environment variables have specific values.
     * This is useful for testing features that depend on environment configuration.
     * 
     * To run these tests, you need to set the environment variables:
     * TEST_MODE=enabled or TEST_MODE=disabled
     */

    @Test
    @EnabledIfEnvironmentVariable(named = "TEST_MODE", matches = "enabled")
    @DisplayName("This test runs only when TEST_MODE is enabled")
    void testEnabledWhenTestModeEnabled() {
        // This test will only run if TEST_MODE=enabled
        System.out.println("Running test with TEST_MODE enabled");
        assertEquals(new BigDecimal("10000.00"), deal.calculateTotalValue(), "Deal total value should match");
    }

    @Test
    @DisabledIfEnvironmentVariable(named = "TEST_MODE", matches = "disabled")
    @DisplayName("This test is disabled when TEST_MODE is disabled")
    void testDisabledWhenTestModeDisabled() {
        // This test will not run if TEST_MODE=disabled
        System.out.println("Running test with TEST_MODE not disabled");
        assertEquals(new BigDecimal("10000.00"), deal.calculateTotalValue(), "Deal total value should match");
    }

    /**
     * This section demonstrates custom conditions using @EnabledIf and @DisabledIf
     * 
     * These annotations allow you to run tests based on custom conditions defined in static methods.
     * This is useful for complex conditions that can't be expressed with the other annotations.
     */

    @Test
    @EnabledIf("isUserASalesRep")
    @DisplayName("This test runs only if the user is a sales rep")
    void testEnabledIfUserIsSalesRep() {
        // This test will only run if the user has the SALES_REP role
        System.out.println("Running test for sales rep user");
        assertTrue(user.isSalesRep(), "User should be a sales rep");
    }

    @Test
    @DisabledIf("isDealWon")
    @DisplayName("This test is disabled if the deal is won")
    void testDisabledIfDealIsWon() {
        // This test will not run if the deal status is WON
        System.out.println("Running test for non-won deal");
        assertNotEquals(DealStatus.WON, deal.getStatus(), "Deal should not be won");
    }

    /**
     * Custom condition method to check if the user is a sales rep
     * Used by @EnabledIf annotation
     */
    static boolean isUserASalesRep() {
        // In a real scenario, this would check the actual user
        // For demonstration, we'll return true
        return true;
    }

    /**
     * Custom condition method to check if the deal is won
     * Used by @DisabledIf annotation
     */
    static boolean isDealWon() {
        // In a real scenario, this would check the actual deal
        // For demonstration, we'll return false
        return false;
    }

    /**
     * This section demonstrates combining multiple conditions
     * 
     * You can apply multiple conditional annotations to a test method.
     * The test will only run if all conditions are satisfied.
     */

    @Test
    @EnabledOnOs(OS.WINDOWS)
    @EnabledIfSystemProperty(named = "env", matches = "test")
    @DisplayName("This test runs only on Windows in test environment")
    void testEnabledOnWindowsInTestEnvironment() {
        // This test will only run on Windows with -Denv=test
        System.out.println("Running test on Windows in test environment");
        assertTrue(plan.isActiveOn(LocalDate.now()), "Plan should be active on current date");
    }

    /**
     * This section demonstrates practical use cases with the commission calculator model
     */

    @Test
    @EnabledIf("isPlanActive")
    @DisplayName("Calculate commission only if plan is active")
    void testCalculateCommissionIfPlanActive() {
        // This test will only run if the plan is active
        System.out.println("Calculating commission for active plan");
        assertTrue(plan.isActiveOn(LocalDate.now()), "Plan should be active on current date");
        // In a real scenario, this would calculate the commission
    }

    @Test
    @DisabledIf("isDealValueTooLow")
    @DisplayName("Skip commission calculation if deal value is too low")
    void testSkipCommissionCalculationIfDealValueTooLow() {
        // This test will not run if the deal value is too low
        System.out.println("Calculating commission for high-value deal");
        assertTrue(deal.calculateTotalValue().compareTo(new BigDecimal("1000.00")) > 0, 
                "Deal value should be greater than 1000.00");
        // In a real scenario, this would calculate the commission
    }

    /**
     * Custom condition method to check if the plan is active
     * Used by @EnabledIf annotation
     */
    static boolean isPlanActive() {
        // In a real scenario, this would check the actual plan
        // For demonstration, we'll return true
        return true;
    }

    /**
     * Custom condition method to check if the deal value is too low
     * Used by @DisabledIf annotation
     */
    static boolean isDealValueTooLow() {
        // In a real scenario, this would check the actual deal
        // For demonstration, we'll return false
        return false;
    }
}
