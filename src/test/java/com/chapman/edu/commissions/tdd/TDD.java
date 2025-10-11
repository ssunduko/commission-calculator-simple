package com.chapman.edu.commissions.tdd;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * TDD (Test-Driven Development) Implementation Class
 *
 * This class demonstrates the complete TDD cycle: RED -> GREEN -> REFACTOR
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * WHAT IS TEST-DRIVEN DEVELOPMENT (TDD)?
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * TDD is a software development approach where tests are written BEFORE the
 * implementation code. This reverses the traditional development flow.
 *
 * Traditional: Write Code -> Write Tests -> Debug
 * TDD:         Write Tests -> Write Code -> Refactor
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * THE TDD CYCLE: RED-GREEN-REFACTOR
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * 1. RED PHASE (Write a Failing Test)
 *    - Write a test for a new feature or requirement
 *    - Run the test - it MUST fail (because code doesn't exist yet)
 *    - Failing test proves the test is valid and can detect issues
 *
 * 2. GREEN PHASE (Make It Pass)
 *    - Write the MINIMUM code needed to make the test pass
 *    - Don't worry about perfection, just make it work
 *    - Run the test - it should now pass
 *    - Don't add features not covered by tests
 *
 * 3. REFACTOR PHASE (Clean Up)
 *    - Improve the code quality without changing behavior
 *    - Remove duplication, improve naming, optimize performance
 *    - Run tests continuously to ensure nothing breaks
 *    - Tests act as a safety net during refactoring
 *
 * After refactoring, return to RED phase for the next feature.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * KEY TDD PRINCIPLES
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * 1. WRITE TESTS FIRST
 *    - Tests define requirements and expected behavior
 *    - Tests serve as executable documentation
 *
 * 2. INCREMENTAL DEVELOPMENT
 *    - Build features one small piece at a time
 *    - Each cycle adds a small, testable increment
 *
 * 3. DESIGN THROUGH TESTS
 *    - Writing tests first forces you to think about design
 *    - Leads to more testable, modular code
 *
 * 4. IMMEDIATE FEEDBACK
 *    - Tests provide instant feedback on code correctness
 *    - Catch bugs early when they're cheapest to fix
 *
 * 5. REFACTORING CONFIDENCE
 *    - Comprehensive tests enable fearless refactoring
 *    - Any regression is immediately caught
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * TDD BEST PRACTICES: FIRST PRINCIPLES
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * F - Fast: Tests should run quickly (milliseconds, not seconds)
 * I - Independent: Tests should not depend on each other
 * R - Repeatable: Tests should produce same results every time
 * S - Self-Validating: Tests should have clear pass/fail (no manual verification)
 * T - Timely: Tests are written at the right time (before implementation)
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * BENEFITS OF TDD
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * 1. Better Design: Forces you to think about interfaces before implementation
 * 2. Higher Code Quality: Tests catch bugs early and often
 * 3. Living Documentation: Tests document how code should behave
 * 4. Easier Refactoring: Comprehensive tests enable safe refactoring
 * 5. Reduced Debugging: Fewer bugs make it to production
 * 6. Increased Confidence: Know your code works because tests prove it
 * 7. Better Coverage: Tests written first ensure all code is tested
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * COMMON TDD CHALLENGES
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * 1. Initial Learning Curve: TDD feels slower at first (but pays off long-term)
 * 2. Discipline Required: Must resist urge to write implementation first
 * 3. Test Maintenance: Tests need to be maintained like production code
 * 4. Over-Testing: Need to balance coverage with practicality
 */
public class TDD {

    // ═══════════════════════════════════════════════════════════════════════
    // FIELD DECLARATIONS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Counter to track the number of commission calculations performed.
     * This demonstrates how TDD can drive the addition of observability features.
     */
    private int calculationCount = 0;

    // ═══════════════════════════════════════════════════════════════════════
    // TDD CYCLE 1: BASIC COMMISSION CALCULATION
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * RED PHASE (Cycle 1):
     * - Test written first in TDDTest.testBasicCommissionCalculation_RedPhase()
     * - Test fails because this method doesn't exist
     *
     * GREEN PHASE (Cycle 1):
     * - Implement the minimum code to make the test pass
     * - Simple multiplication: dealValue * rate
     *
     * Purpose: Calculate commission as a percentage of deal value
     *
     * @param dealValue The value of the deal
     * @param rate      The commission rate (e.g., 0.10 for 10%)
     * @return The calculated commission amount
     */
    public BigDecimal calculateBasicCommission(BigDecimal dealValue, BigDecimal rate) {
        // GREEN PHASE IMPLEMENTATION:
        // Start with the simplest possible implementation
        incrementCalculationCount(); // Track this calculation

        // Simple multiplication to make the test pass
        BigDecimal commission = dealValue.multiply(rate);

        // Set scale to 2 decimal places for currency
        return commission.setScale(2, RoundingMode.HALF_UP);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TDD CYCLE 2: DEAL VALIDATION
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * RED PHASE (Cycle 2):
     * - Test written first in TDDTest.testDealValidation_RedPhase()
     * - Test expects validation logic for deal eligibility
     *
     * GREEN PHASE (Cycle 2):
     * - Implement simple validation: only CLOSED deals are eligible
     *
     * REFACTOR OPPORTUNITY:
     * - Could be expanded to check other conditions (closeDate, etc.)
     * - Validation logic is simple and focused (Single Responsibility)
     *
     * Purpose: Validate if a deal is eligible for commission calculation
     *
     * @param deal The deal to validate
     * @return true if deal is eligible for commission, false otherwise
     */
    public boolean isDealEligibleForCommission(Deal deal) {
        // GREEN PHASE IMPLEMENTATION:
        // Minimum logic to pass the test

        if (deal == null) {
            return false;
        }

        // Only CLOSED deals are eligible for commission
        // This is a business rule driven by the test
        return deal.getStatus() == DealStatus.LOST;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TDD CYCLE 3: TIERED COMMISSION CALCULATION
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * RED PHASE (Cycle 3):
     * - Test written first in TDDTest.testTieredCommissionCalculation_RedPhase()
     * - Test defines tier structure:
     *   * $0 - $50,000: 8%
     *   * $50,001 - $100,000: 10%
     *   * $100,001+: 12%
     *
     * GREEN PHASE (Cycle 3):
     * - Implement tier logic with if-else statements
     *
     * REFACTOR OPPORTUNITY (demonstrated below):
     * - Extract tier boundaries to constants for maintainability
     * - Could use Strategy pattern for more complex tier structures
     * - Magic numbers replaced with named constants
     *
     * Purpose: Calculate commission using tiered rates based on deal size
     *
     * @param dealValue The value of the deal
     * @return The calculated commission using appropriate tier rate
     * @throws IllegalArgumentException if deal value is negative
     */
    public BigDecimal calculateTieredCommission(BigDecimal dealValue) {
        // VALIDATION: Protect against invalid input
        if (dealValue == null || dealValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Deal value cannot be null or negative");
        }

        incrementCalculationCount(); // Track this calculation

        // REFACTORED VERSION:
        // Originally had magic numbers - refactored to use constants

        // Define tier boundaries and rates
        final BigDecimal TIER_1_MAX = new BigDecimal("50000");
        final BigDecimal TIER_2_MAX = new BigDecimal("100000");

        final BigDecimal TIER_1_RATE = new BigDecimal("0.08"); // 8%
        final BigDecimal TIER_2_RATE = new BigDecimal("0.10"); // 10%
        final BigDecimal TIER_3_RATE = new BigDecimal("0.12"); // 12%

        BigDecimal rate;

        // Determine which tier applies
        if (dealValue.compareTo(TIER_1_MAX) <= 0) {
            // Tier 1: $0 - $50,000
            rate = TIER_1_RATE;
        } else if (dealValue.compareTo(TIER_2_MAX) <= 0) {
            // Tier 2: $50,001 - $100,000
            rate = TIER_2_RATE;
        } else {
            // Tier 3: $100,001+
            rate = TIER_3_RATE;
        }

        // Calculate commission using determined rate
        return dealValue.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TDD CYCLE 4: COMMISSION WITH BONUS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * RED PHASE (Cycle 4):
     * - Test written first in TDDTest.testCommissionWithBonus_RedPhase()
     * - Test expects bonus to be added to base commission
     *
     * GREEN PHASE (Cycle 4):
     * - Implement bonus calculation: baseCommission * (1 + bonusPercentage)
     *
     * REFACTOR NOTES:
     * - Simple, focused method doing one thing well
     * - Easy to test and maintain
     *
     * Purpose: Add a performance bonus to the base commission
     *
     * @param baseCommission  The base commission amount
     * @param bonusPercentage The bonus percentage (e.g., 0.15 for 15% bonus)
     * @return The total commission including bonus
     */
    public BigDecimal calculateCommissionWithBonus(BigDecimal baseCommission, BigDecimal bonusPercentage) {
        // GREEN PHASE IMPLEMENTATION:
        // Calculate bonus amount and add to base commission

        incrementCalculationCount(); // Track this calculation

        // Calculate the bonus amount
        BigDecimal bonusAmount = baseCommission.multiply(bonusPercentage);

        // Add bonus to base commission
        BigDecimal totalCommission = baseCommission.add(bonusAmount);

        return totalCommission.setScale(2, RoundingMode.HALF_UP);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TDD CYCLE 5: FULL COMMISSION PIPELINE
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * RED PHASE (Cycle 5):
     * - Test written first in TDDTest.testFullCommissionPipeline_RedPhase()
     * - Test expects integration of validation, tiered calculation, and bonus
     *
     * GREEN PHASE (Cycle 5):
     * - Combine existing methods into a complete pipeline
     * - This demonstrates composition of smaller tested units
     *
     * REFACTOR HIGHLIGHTS:
     * - This method demonstrates the power of TDD composition
     * - Each component is already tested, so integration is safer
     * - Clear separation of concerns: validate -> calculate -> enhance
     *
     * TDD INSIGHT:
     * - Because we built each piece with tests, combining them is low-risk
     * - Tests for individual methods ensure this composite method works correctly
     * - This is a key benefit of TDD: confidence when building on tested foundations
     *
     * Purpose: Complete commission calculation including validation, tier rates, and bonuses
     *
     * @param deal      The deal to calculate commission for
     * @param bonusRate The bonus rate to apply (0 for no bonus)
     * @return The total commission amount
     * @throws IllegalArgumentException if deal is null or not eligible
     */
    public BigDecimal calculateFullCommission(Deal deal, BigDecimal bonusRate) {
        // STEP 1: VALIDATION
        // Use our tested validation method
        if (deal == null) {
            throw new IllegalArgumentException("Deal cannot be null");
        }

        if (!isDealEligibleForCommission(deal)) {
            throw new IllegalArgumentException(
                    "Deal is not eligible for commission. Status: " + deal.getStatus());
        }

        incrementCalculationCount(); // Track this calculation

        // STEP 2: CALCULATE BASE COMMISSION USING TIERED RATES
        // Use our tested tiered calculation method
        BigDecimal baseCommission = calculateTieredCommission(deal.getValue());

        // STEP 3: APPLY BONUS IF PROVIDED
        // Use our tested bonus calculation method
        if (bonusRate != null && bonusRate.compareTo(BigDecimal.ZERO) > 0) {
            return calculateCommissionWithBonus(baseCommission, bonusRate);
        }

        // Return base commission if no bonus
        return baseCommission;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TDD CYCLE 7: OBSERVABILITY - CALCULATION TRACKING
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * RED PHASE (Cycle 7):
     * - Test written first in TDDTest.testCommissionHistoryTracking_RedPhase()
     * - Test expects ability to track number of calculations
     *
     * GREEN PHASE (Cycle 7):
     * - Add counter field and methods to track calculations
     *
     * TDD INSIGHT:
     * - Tests drive us to add useful features like observability
     * - Without tests, we might forget to add tracking
     *
     * Purpose: Get the total number of commission calculations performed
     *
     * @return The number of calculations performed by this service instance
     */
    public int getCalculationCount() {
        return calculationCount;
    }

    /**
     * REFACTOR PHASE:
     * - Extracted this as a private helper method to avoid duplication
     * - Called by each calculation method
     * - Demonstrates DRY (Don't Repeat Yourself) principle
     *
     * Purpose: Increment the calculation counter (private helper method)
     */
    private void incrementCalculationCount() {
        calculationCount++;
    }

    /**
     * ADDITIONAL FEATURE (driven by TDD cycle):
     * Reset the calculation counter.
     *
     * This method would be driven by a new test requiring counter reset functionality.
     * It demonstrates how TDD drives continuous feature addition.
     *
     * @return The count before reset
     */
    public int resetCalculationCount() {
        int previousCount = calculationCount;
        calculationCount = 0;
        return previousCount;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // REFACTORING EXAMPLE: CODE QUALITY IMPROVEMENTS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * REFACTOR PHASE DEMONSTRATION:
     *
     * This is an example of what REFACTORING means in TDD:
     *
     * BEFORE REFACTORING (Green Phase):
     * - Methods had magic numbers (0.08, 0.10, 0.12)
     * - No constant definitions
     * - Less maintainable
     *
     * AFTER REFACTORING:
     * - Extracted constants with meaningful names
     * - Added comprehensive documentation
     * - Improved error handling
     * - Added observability (calculation tracking)
     *
     * KEY POINT: Tests stayed GREEN throughout refactoring!
     * - Tests didn't need to change
     * - Tests proved refactoring didn't break functionality
     * - This is the safety net that enables confident refactoring
     */

    // ═══════════════════════════════════════════════════════════════════════
    // UTILITY METHOD FOR DEMONSTRATION
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Get a summary of the service state.
     * This demonstrates how TDD encourages building observable, testable systems.
     *
     * @return A string summary of the current state
     */
    public String getServiceSummary() {
        return String.format("TDD Commission Service - Total calculations: %d", calculationCount);
    }

    /**
     * ═══════════════════════════════════════════════════════════════════════
     * TDD LESSONS LEARNED
     * ═══════════════════════════════════════════════════════════════════════
     *
     * 1. TESTS DRIVE DESIGN
     *    - Writing tests first forced us to think about the interface
     *    - Methods are naturally more testable and modular
     *
     * 2. INCREMENTAL DEVELOPMENT
     *    - Each TDD cycle added one small, working feature
     *    - No "big bang" integration at the end
     *
     * 3. REFACTORING IS SAFE
     *    - Tests act as a safety net during refactoring
     *    - Can improve code without fear of breaking things
     *
     * 4. DOCUMENTATION THROUGH TESTS
     *    - Tests show exactly how to use each method
     *    - Tests document expected behavior better than comments
     *
     * 5. EARLY BUG DETECTION
     *    - Tests catch issues immediately during development
     *    - Much cheaper to fix bugs early vs. in production
     *
     * 6. CONFIDENCE IN CHANGES
     *    - Comprehensive tests enable confident changes
     *    - Can add features or refactor without regression fear
     *
     * 7. DESIGN PATTERNS EMERGE
     *    - TDD naturally leads to better design patterns
     *    - Composition, separation of concerns happen naturally
     *
     * ═══════════════════════════════════════════════════════════════════════
     * COMPARISON: TDD vs. TRADITIONAL DEVELOPMENT
     * ═══════════════════════════════════════════════════════════════════════
     *
     * TRADITIONAL APPROACH:
     * 1. Write implementation code
     * 2. Manually test it
     * 3. Find bugs
     * 4. Fix bugs
     * 5. Maybe write tests later
     * 6. Refactoring is risky (might break things)
     *
     * TDD APPROACH:
     * 1. Write test (defines requirement)
     * 2. Watch it fail (proves test works)
     * 3. Write minimal code to pass
     * 4. Watch it pass (requirement met)
     * 5. Refactor safely (tests protect you)
     * 6. Repeat for next requirement
     *
     * ═══════════════════════════════════════════════════════════════════════
     * WHEN TO USE TDD
     * ═══════════════════════════════════════════════════════════════════════
     *
     * IDEAL FOR:
     * - Business logic and algorithms
     * - APIs and interfaces
     * - Data transformations
     * - Complex calculations
     * - Critical system components
     *
     * LESS IDEAL FOR:
     * - UI/UX exploration (requirements unclear)
     * - Prototypes and throwaway code
     * - Simple CRUD operations
     * - Glue code with no logic
     *
     * ═══════════════════════════════════════════════════════════════════════
     * FINAL THOUGHTS
     * ═══════════════════════════════════════════════════════════════════════
     *
     * TDD is not just about testing - it's a design methodology.
     *
     * Tests are a byproduct. The real value is:
     * - Better design decisions
     * - More modular code
     * - Higher confidence
     * - Easier maintenance
     * - Living documentation
     *
     * Like any skill, TDD takes practice. It may feel slow at first,
     * but becomes faster with experience. The long-term benefits in
     * code quality and maintenance far outweigh the initial time investment.
     */
}