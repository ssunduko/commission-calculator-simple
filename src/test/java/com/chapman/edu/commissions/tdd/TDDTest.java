package com.chapman.edu.commissions.tdd;

import com.chapman.edu.commissions.model.*;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD (Test-Driven Development) Demonstration Test Class
 *
 * This test class demonstrates the RED-GREEN-REFACTOR cycle of Test-Driven Development.
 * In TDD, we write tests BEFORE implementing the code. The workflow is:
 *
 * 1. RED: Write a failing test that defines a desired improvement or new function
 * 2. GREEN: Write the minimal code to make the test pass
 * 3. REFACTOR: Clean up the code while keeping tests green
 *
 * This class uses the commission calculator domain to demonstrate TDD principles.
 */
@DisplayName("TDD Demonstration - Commission Calculation Service")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TDDTest {

    private TDD commissionService;
    private Deal testDeal;
    private CommissionPlan testPlan;

    /**
     * STEP 1 - RED PHASE: Write a failing test first
     * This test will fail because TDD class doesn't exist yet
     */
    @BeforeEach
    void setUp() {
        // Initialize the service (will fail initially - RED phase)
        commissionService = new TDD();

        // Set up test data
        testDeal = new Deal("Enterprise Software License", new BigDecimal("100000"), "REP-001");
        testDeal.setId("DEAL-001");
        testDeal.setStatus(DealStatus.LOST);
        testDeal.setCloseDate(LocalDate.now());

        testPlan = new CommissionPlan("Standard Plan", Currency.getInstance("USD"));
        testPlan.setId("PLAN-001");
        testPlan.setStatus(PlanStatus.ACTIVE);
    }

    /**
     * CYCLE 1: Basic Commission Calculation
     * RED: Test for basic 10% commission calculation
     */
    @Test
    @Order(1)
    @DisplayName("RED Phase - Test basic commission calculation at 10% rate")
    void testBasicCommissionCalculation_RedPhase() {
        // ARRANGE: Set up test data
        BigDecimal dealValue = new BigDecimal("100000");
        BigDecimal expectedRate = new BigDecimal("0.10");
        BigDecimal expectedCommission = new BigDecimal("10000.00");

        // ACT: Call the method (will fail - method doesn't exist yet)
        BigDecimal actualCommission = commissionService.calculateBasicCommission(dealValue, expectedRate);

        // ASSERT: Verify the result
        assertEquals(expectedCommission, actualCommission.setScale(2));
    }

    /**
     * CYCLE 2: Deal Validation
     * RED: Test that only CLOSED deals are eligible for commission
     */
    @Test
    @Order(2)
    @DisplayName("RED Phase - Test deal validation for commission eligibility")
    void testDealValidation_RedPhase() {
        // ARRANGE: Create deals with different statuses
        Deal closedDeal = new Deal("Closed Deal", new BigDecimal("50000"), "REP-001");
        closedDeal.setStatus(DealStatus.LOST);

        Deal openDeal = new Deal("Open Deal", new BigDecimal("50000"), "REP-001");
        openDeal.setStatus(DealStatus.OPEN);

        // ACT & ASSERT: Closed deal should be eligible
        assertTrue(commissionService.isDealEligibleForCommission(closedDeal),
                "Closed deals should be eligible for commission");

        // ACT & ASSERT: Open deal should NOT be eligible
        assertFalse(commissionService.isDealEligibleForCommission(openDeal),
                "Open deals should not be eligible for commission");
    }

    /**
     * CYCLE 3: Tiered Commission Calculation
     * RED: Test tiered commission rates based on deal size
     *
     * Tier Structure:
     * - $0 - $50,000: 8%
     * - $50,001 - $100,000: 10%
     * - $100,001+: 12%
     */
    @Test
    @Order(3)
    @DisplayName("RED Phase - Test tiered commission calculation")
    void testTieredCommissionCalculation_RedPhase() {
        // ARRANGE: Test deals at different tiers
        BigDecimal smallDealValue = new BigDecimal("30000");  // 8% tier
        BigDecimal mediumDealValue = new BigDecimal("75000"); // 10% tier
        BigDecimal largeDealValue = new BigDecimal("150000"); // 12% tier
        BigDecimal largerDealValue = new BigDecimal("170000"); // 12% tier

        // ACT: Calculate commissions
        BigDecimal smallCommission = commissionService.calculateTieredCommission(smallDealValue);
        BigDecimal mediumCommission = commissionService.calculateTieredCommission(mediumDealValue);
        BigDecimal largeCommission = commissionService.calculateTieredCommission(largeDealValue);
        BigDecimal largerCommission = commissionService.calculateTieredCommission(largerDealValue);

        // ASSERT: Verify correct tier rates applied
        assertEquals(new BigDecimal("2400.00"), smallCommission.setScale(2),
                "Small deal should use 8% rate: 30000 * 0.08 = 2400");
        assertEquals(new BigDecimal("7500.00"), mediumCommission.setScale(2),
                "Medium deal should use 10% rate: 75000 * 0.10 = 7500");
        assertEquals(new BigDecimal("18000.00"), largeCommission.setScale(2),
                "Large deal should use 12% rate: 150000 * 0.12 = 18000");
        assertEquals(new BigDecimal("20400.00"), largerCommission.setScale(2),
                "Even Larger deal should use 12% rate: 170000 * 0.12 = 20400");
    }

    /**
     * CYCLE 4: Commission with Bonus
     * RED: Test commission calculation with performance bonus
     */
    @Test
    @Order(4)
    @DisplayName("RED Phase - Test commission with bonus calculation")
    void testCommissionWithBonus_RedPhase() {
        // ARRANGE: Base commission and bonus percentage
        BigDecimal baseCommission = new BigDecimal("10000");
        BigDecimal bonusPercentage = new BigDecimal("0.15"); // 15% bonus
        BigDecimal expectedTotal = new BigDecimal("11500.00"); // 10000 + (10000 * 0.15)

        // ACT: Calculate total with bonus
        BigDecimal actualTotal = commissionService.calculateCommissionWithBonus(baseCommission, bonusPercentage);

        // ASSERT: Verify bonus is applied correctly
        assertEquals(expectedTotal, actualTotal.setScale(2),
                "Commission with 15% bonus should be 11500");
    }

    /**
     * CYCLE 5: Full Commission Pipeline
     * RED: Test complete commission calculation pipeline
     * This test integrates validation, tiered calculation, and bonus
     */
    @Test
    @Order(5)
    @DisplayName("RED Phase - Test full commission calculation pipeline")
    void testFullCommissionPipeline_RedPhase() {
        // ARRANGE: Set up a complete scenario
        Deal qualifiedDeal = new Deal("Major Enterprise Deal", new BigDecimal("120000"), "REP-001");
        qualifiedDeal.setStatus(DealStatus.LOST);
        qualifiedDeal.setCloseDate(LocalDate.now().minusDays(5)); // Closed 5 days ago

        BigDecimal bonusRate = new BigDecimal("0.10"); // 10% bonus for early close

        // ACT: Calculate using the full pipeline
        BigDecimal totalCommission = commissionService.calculateFullCommission(qualifiedDeal, bonusRate);

        // ASSERT: Verify the complete calculation
        // Expected: 120000 * 0.12 (tier rate) = 14400
        //          14400 * 1.10 (with bonus) = 15840
        BigDecimal expectedCommission = new BigDecimal("15840.00");
        assertEquals(expectedCommission, totalCommission.setScale(2),
                "Full commission should include tier rate and bonus");
    }

    /**
     * CYCLE 6: Edge Cases and Validation
     * RED: Test boundary conditions and error handling
     */
    @Test
    @Order(6)
    @DisplayName("RED Phase - Test edge cases and validation")
    void testEdgeCasesAndValidation_RedPhase() {
        // Test 1: Null deal should throw exception
        assertThrows(IllegalArgumentException.class,
                () -> commissionService.calculateFullCommission(null, BigDecimal.ZERO),
                "Null deal should throw IllegalArgumentException");

        // Test 2: Negative deal value should throw exception
        Deal negativeDeal = new Deal("Invalid Deal", new BigDecimal("-1000"), "REP-001");
        assertThrows(IllegalArgumentException.class,
                () -> commissionService.calculateTieredCommission(negativeDeal.getValue()),
                "Negative deal value should throw IllegalArgumentException");

        // Test 3: Zero commission rate should return zero
        BigDecimal zeroCommission = commissionService.calculateBasicCommission(
                new BigDecimal("100000"), BigDecimal.ZERO);
        assertEquals(new BigDecimal("0.00"), zeroCommission.setScale(2),
                "Zero rate should result in zero commission");

        // Test 4: Boundary value at tier threshold
        BigDecimal boundaryValue = new BigDecimal("50000"); // Exactly at tier boundary
        BigDecimal boundaryCommission = commissionService.calculateTieredCommission(boundaryValue);
        assertEquals(new BigDecimal("4000.00"), boundaryCommission.setScale(2),
                "Boundary value 50000 should use 8% rate");
    }

    /**
     * CYCLE 7: Commission History Tracking
     * RED: Test that we can track multiple commission calculations
     */
    @Test
    @Order(7)
    @DisplayName("RED Phase - Test commission calculation history tracking")
    void testCommissionHistoryTracking_RedPhase() {
        // ARRANGE: Calculate multiple commissions
        BigDecimal commission1 = commissionService.calculateBasicCommission(
                new BigDecimal("50000"), new BigDecimal("0.10"));
        BigDecimal commission2 = commissionService.calculateBasicCommission(
                new BigDecimal("75000"), new BigDecimal("0.10"));

        // ACT: Get calculation count
        int calculationCount = commissionService.getCalculationCount();

        // ASSERT: Should track both calculations
        assertTrue(calculationCount >= 2,
                "Service should track at least 2 commission calculations");
    }

    /**
     * REFACTOR Phase Test: Verify refactored code maintains correctness
     * This test ensures that refactoring doesn't break existing functionality
     */
    @Test
    @Order(8)
    @DisplayName("REFACTOR Phase - Verify refactored methods maintain correctness")
    void testRefactoredMethodsMaintainCorrectness() {
        // This test verifies that after refactoring, all previous functionality still works

        // Test 1: Basic calculation still works
        BigDecimal basic = commissionService.calculateBasicCommission(
                new BigDecimal("100000"), new BigDecimal("0.10"));
        assertEquals(new BigDecimal("10000.00"), basic.setScale(2));

        // Test 2: Tiered calculation still works
        BigDecimal tiered = commissionService.calculateTieredCommission(new BigDecimal("75000"));
        assertEquals(new BigDecimal("7500.00"), tiered.setScale(2));

        // Test 3: Pipeline still works
        Deal testDeal = new Deal("Test", new BigDecimal("100000"), "REP-001");
        testDeal.setStatus(DealStatus.LOST);
        BigDecimal pipeline = commissionService.calculateFullCommission(testDeal, new BigDecimal("0.05"));
        assertNotNull(pipeline, "Pipeline calculation should return a result");
    }

    /**
     * TDD Best Practices Test
     * This test demonstrates TDD best practices in action
     */
    @Test
    @Order(9)
    @DisplayName("TDD Best Practices - Test demonstrates FIRST principles")
    void testFirstPrinciples() {
        // F - Fast: This test runs quickly
        long startTime = System.currentTimeMillis();

        BigDecimal result = commissionService.calculateBasicCommission(
                new BigDecimal("100000"), new BigDecimal("0.10"));

        long endTime = System.currentTimeMillis();
        assertTrue((endTime - startTime) < 100, "Test should complete in less than 100ms");

        // I - Independent: This test doesn't depend on other tests
        assertNotNull(result, "Test produces independent result");

        // R - Repeatable: Running this test multiple times produces same result
        BigDecimal result2 = commissionService.calculateBasicCommission(
                new BigDecimal("100000"), new BigDecimal("0.10"));
        assertEquals(result, result2, "Test should be repeatable");

        // S - Self-validating: Test has clear pass/fail with assertions
        assertTrue(result.compareTo(BigDecimal.ZERO) > 0, "Result should be positive");

        // T - Timely: Test written before implementation (in true TDD fashion)
    }

    /**
     * Teardown method to clean up after tests
     */
    @AfterEach
    void tearDown() {
        // Clean up resources if needed
        commissionService = null;
        testDeal = null;
        testPlan = null;
    }
}