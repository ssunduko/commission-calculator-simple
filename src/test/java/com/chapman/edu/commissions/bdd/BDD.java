package com.chapman.edu.commissions.bdd;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.PlanStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * ═══════════════════════════════════════════════════════════════════════════
 * BDD (Behavior-Driven Development) Implementation Class
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * This class demonstrates Behavior-Driven Development (BDD) concepts using
 * the JBehave framework. BDD extends TDD by emphasizing collaboration between
 * developers, QA, and non-technical stakeholders through natural language
 * specifications.
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * WHAT IS BEHAVIOR-DRIVEN DEVELOPMENT (BDD)?
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * BDD is an evolution of Test-Driven Development that focuses on:
 * - Describing behavior in plain language (Given-When-Then)
 * - Collaboration between technical and non-technical team members
 * - Shared understanding of requirements
 * - Living documentation that anyone can read
 *
 * Traditional TDD: Write test → Write code → Refactor
 * BDD:            Write scenario → Write steps → Implement behavior → Refactor
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * KEY BDD CONCEPTS
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * 1. USER STORIES
 *    Format: "As a [role], I want [feature], So that [benefit]"
 *    Example: "As a sales manager, I want to calculate commissions,
 *              So that I can compensate sales reps accurately"
 *
 * 2. SCENARIOS
 *    Concrete examples of how a feature should behave
 *    Written in Given-When-Then format
 *
 * 3. GHERKIN LANGUAGE
 *    Natural language syntax for writing scenarios
 *    Keywords: Given, When, Then, And, But
 *
 * 4. GIVEN-WHEN-THEN
 *    Given: Set up initial context (preconditions)
 *    When:  Perform an action (the behavior being tested)
 *    Then:  Verify the outcome (expected result)
 *
 * 5. LIVING DOCUMENTATION
 *    Scenarios serve as always up-to-date documentation
 *    Business stakeholders can read and verify scenarios
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * BDD vs TDD
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * TDD:
 * - Developer-focused
 * - Technical test code
 * - Tests implementation details
 * - Example: testCalculateCommission()
 *
 * BDD:
 * - Stakeholder-focused
 * - Natural language scenarios
 * - Tests business behavior
 * - Example: "Given a closed deal, When I calculate commission, Then..."
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * JBEHAVE FRAMEWORK
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * JBehave is a BDD framework for Java that:
 * - Reads story files (*.story) written in Gherkin
 * - Maps steps to Java methods using annotations
 * - Executes scenarios and generates reports
 * - Supports parameterization and table-driven tests
 *
 * Components:
 * 1. Story Files (.story): Gherkin scenarios
 * 2. Steps Classes: Java methods with @Given, @When, @Then annotations
 * 3. Configuration: How to run stories and find steps
 * 4. Reports: HTML reports showing scenario results
 *
 * ═══════════════════════════════════════════════════════════════════════════
 * BENEFITS OF BDD
 * ═══════════════════════════════════════════════════════════════════════════
 *
 * 1. Shared Understanding: Everyone uses the same language
 * 2. Clearer Requirements: Examples clarify expectations
 * 3. Living Documentation: Scenarios document current behavior
 * 4. Better Collaboration: Bridge between business and development
 * 5. Reduced Ambiguity: Concrete examples eliminate confusion
 * 6. Executable Specifications: Requirements that can be tested
 * 7. Focus on Value: Tests what matters to users
 *
 * ═══════════════════════════════════════════════════════════════════════════
 */
public class BDD {

    // ═══════════════════════════════════════════════════════════════════════
    // STATE MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Tracking calculation count for observability.
     * In BDD, we track state that's relevant to business scenarios.
     */
    private int calculationCount = 0;

    /**
     * Store for commission plans.
     * BDD often involves testing business entities and their lifecycle.
     */
    private Map<String, CommissionPlan> commissionPlans = new HashMap<>();

    /**
     * Commission eligibility window in days.
     * BDD: Business rules are made explicit and testable.
     */
    private static final int COMMISSION_ELIGIBILITY_DAYS = 90;

    // ═══════════════════════════════════════════════════════════════════════
    // COMMISSION CALCULATION - Basic
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * BDD SCENARIO: Calculate basic commission for a closed deal
     *
     * Story Mapping:
     * Given: A deal with value and rate (setup context)
     * When:  I calculate the commission (action)
     * Then:  Commission is calculated correctly (verification)
     *
     * Business Value:
     * Sales reps need to know their commission for each deal.
     * Simple percentage-based calculation provides transparency.
     *
     * @param dealValue The monetary value of the deal
     * @param rate      The commission rate (e.g., 0.10 for 10%)
     * @return Calculated commission amount
     */
    public BigDecimal calculateBasicCommission(BigDecimal dealValue, BigDecimal rate) {
        // BDD PRINCIPLE: Validate business rules
        if (dealValue == null || rate == null) {
            throw new IllegalArgumentException("Deal value and rate are required for commission calculation");
        }

        incrementCalculationCount();

        // Simple business rule: commission = dealValue * rate
        BigDecimal commission = dealValue.multiply(rate);
        return commission.setScale(2, RoundingMode.HALF_UP);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // DEAL VALIDATION - Business Rules
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * BDD SCENARIO: Commission calculation requires closed deal status
     *
     * Story Mapping:
     * Given: A deal with a specific status (context)
     * When:  I check if deal is eligible (action)
     * Then:  System validates based on business rules (verification)
     *
     * Business Rules:
     * 1. Only CLOSED deals are eligible for commission
     * 2. Deal must have a close date
     * 3. Close date must be within last 90 days
     *
     * Why This Matters:
     * Prevents paying commissions on deals that aren't finalized
     * or are too old to qualify.
     *
     * @param deal The deal to validate
     * @return true if eligible, false otherwise
     */
    public boolean isDealEligibleForCommission(Deal deal) {
        // BDD: Each validation represents a business rule

        if (deal == null) {
            return false;
        }

        // Business Rule 0: Deal value must be positive
        if (deal.getValue() == null || deal.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        // Business Rule 1: Must be WON (closed/won in business language)
        if (deal.getStatus() != DealStatus.WON) {
            return false;
        }

        // Business Rule 2: Must have close date
        if (deal.getCloseDate() == null) {
            return false;
        }

        // Business Rule 3: Must be within eligibility window
        LocalDate cutoffDate = LocalDate.now().minusDays(COMMISSION_ELIGIBILITY_DAYS);
        if (deal.getCloseDate().isBefore(cutoffDate)) {
            return false;
        }

        return true;
    }

    /**
     * Get the rejection reason for an ineligible deal.
     *
     * BDD PRINCIPLE: Provide clear feedback
     * Users need to understand WHY a deal is ineligible.
     *
     * @param deal The deal to check
     * @return Human-readable reason for rejection
     */
    public String getIneligibilityReason(Deal deal) {
        if (deal == null) {
            return "Deal is null";
        }

        if (deal.getValue() == null || deal.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            return "Deal value must be positive";
        }

        if (deal.getStatus() != DealStatus.WON) {
            return "Deal must be WON (current status: " + deal.getStatus() + ")";
        }

        if (deal.getCloseDate() == null) {
            return "Deal does not have a close date";
        }

        LocalDate cutoffDate = LocalDate.now().minusDays(COMMISSION_ELIGIBILITY_DAYS);
        if (deal.getCloseDate().isBefore(cutoffDate)) {
            return "Deal closed more than " + COMMISSION_ELIGIBILITY_DAYS + " days ago";
        }

        return "Deal is eligible";
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TIERED COMMISSION - Business Logic
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * BDD SCENARIO: Tiered commission rates based on deal size
     *
     * Story Mapping:
     * Given: Different deal sizes (examples table)
     * When:  I calculate commission (action)
     * Then:  Appropriate tier rate is applied (verification)
     *
     * Business Rules:
     * - Tier 1 ($0-$50K): 8% commission
     * - Tier 2 ($50K-$100K): 10% commission
     * - Tier 3 ($100K+): 12% commission
     *
     * Business Value:
     * Incentivizes larger deals by offering higher commission rates.
     *
     * @param dealValue The value of the deal
     * @return Commission calculated using tiered rates
     */
    public BigDecimal calculateTieredCommission(BigDecimal dealValue) {
        // BDD: Validate business constraints
        if (dealValue == null || dealValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Deal value must be positive");
        }

        incrementCalculationCount();

        // Business Rule: Tier definitions
        final BigDecimal TIER_1_MAX = new BigDecimal("50000");
        final BigDecimal TIER_2_MAX = new BigDecimal("100000");

        final BigDecimal TIER_1_RATE = new BigDecimal("0.08");  // 8%
        final BigDecimal TIER_2_RATE = new BigDecimal("0.10");  // 10%
        final BigDecimal TIER_3_RATE = new BigDecimal("0.12");  // 12%

        BigDecimal rate;

        // Apply appropriate tier
        if (dealValue.compareTo(TIER_1_MAX) <= 0) {
            rate = TIER_1_RATE;
        } else if (dealValue.compareTo(TIER_2_MAX) <= 0) {
            rate = TIER_2_RATE;
        } else {
            rate = TIER_3_RATE;
        }

        return dealValue.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // BONUS APPLICATION - Business Feature
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * BDD SCENARIO: Apply performance bonus to commission
     *
     * Story Mapping:
     * Given: Base commission and bonus percentage (context)
     * When:  I apply the bonus (action)
     * Then:  Total includes both base and bonus (verification)
     *
     * Business Value:
     * Rewards top performers with additional compensation.
     *
     * @param baseCommission  The base commission amount
     * @param bonusPercentage The bonus rate to apply
     * @return Total commission with bonus
     */
    public BigDecimal calculateCommissionWithBonus(BigDecimal baseCommission, BigDecimal bonusPercentage) {
        if (baseCommission == null || bonusPercentage == null) {
            throw new IllegalArgumentException("Base commission and bonus percentage are required");
        }

        incrementCalculationCount();

        // Business Logic: Add bonus to base
        BigDecimal bonusAmount = baseCommission.multiply(bonusPercentage);
        BigDecimal total = baseCommission.add(bonusAmount);

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // FULL COMMISSION PIPELINE - Integration Scenario
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * BDD SCENARIO: Full commission pipeline with validation
     *
     * Story Mapping:
     * Given: A complete deal with all details (complex context)
     * When:  I process the full commission calculation (integrated action)
     * Then:  Validation, calculation, and bonus all work together (end-to-end verification)
     *
     * Business Process:
     * 1. Validate deal eligibility
     * 2. Calculate tiered commission
     * 3. Apply performance bonus if applicable
     * 4. Track calculation
     *
     * This represents the actual business workflow.
     *
     * @param deal      The deal to process
     * @param bonusRate Bonus rate to apply (0 for no bonus)
     * @return Final commission amount
     * @throws IllegalArgumentException if deal is ineligible
     */
    public BigDecimal calculateFullCommission(Deal deal, BigDecimal bonusRate) {
        // Step 1: Validation (business rules)
        if (deal == null) {
            throw new IllegalArgumentException("Deal cannot be null");
        }

        if (!isDealEligibleForCommission(deal)) {
            String reason = getIneligibilityReason(deal);
            throw new IllegalArgumentException("Deal is not eligible for commission: " + reason);
        }

        incrementCalculationCount();

        // Step 2: Calculate base commission using tiers
        BigDecimal baseCommission = calculateTieredCommission(deal.getValue());

        // Step 3: Apply bonus if provided
        if (bonusRate != null && bonusRate.compareTo(BigDecimal.ZERO) > 0) {
            return calculateCommissionWithBonus(baseCommission, bonusRate);
        }

        return baseCommission;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // COMMISSION PLAN MANAGEMENT - Business Entity Lifecycle
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * BDD SCENARIO: Create a new commission plan
     *
     * Story Mapping:
     * Given: I want to create a commission plan (intent)
     * When:  I create the plan with details (action)
     * Then:  Plan is stored with DRAFT status (verification)
     *
     * Business Value:
     * Allows configuration of different commission structures
     * for different time periods or products.
     *
     * @param plan The commission plan to store
     */
    public void storeCommissionPlan(CommissionPlan plan) {
        if (plan == null || plan.getName() == null) {
            throw new IllegalArgumentException("Valid commission plan with name is required");
        }

        // Business Rule: New plans start as DRAFT
        if (plan.getStatus() == null) {
            plan.setStatus(PlanStatus.DRAFT);
        }

        commissionPlans.put(plan.getName(), plan);
    }

    /**
     * BDD SCENARIO: Activate a commission plan
     *
     * Story Mapping:
     * Given: A plan in DRAFT status (context)
     * When:  I activate the plan (action)
     * Then:  Status changes to ACTIVE (verification)
     *
     * @param planName Name of the plan to activate
     */
    public void activateCommissionPlan(String planName) {
        CommissionPlan plan = commissionPlans.get(planName);

        if (plan == null) {
            throw new IllegalArgumentException("Commission plan not found: " + planName);
        }

        // Business Rule: Can only activate plans with rules
        if (plan.getRules().isEmpty() && plan.getTiers().isEmpty()) {
            throw new IllegalArgumentException("Cannot activate plan without rules or tiers");
        }

        plan.setStatus(PlanStatus.ACTIVE);
        plan.setLastModifiedDate(LocalDate.now());
    }

    /**
     * BDD SCENARIO: Check plan applicability for a specific date
     *
     * Story Mapping:
     * Given: A plan with effective dates (context)
     * When:  I check if it applies on a date (action)
     * Then:  Plan applicability is determined (verification)
     *
     * @param planName Name of the plan
     * @param date     Date to check
     * @return true if plan is applicable on that date
     */
    public boolean isPlanApplicableOn(String planName, LocalDate date) {
        CommissionPlan plan = commissionPlans.get(planName);

        if (plan == null) {
            return false;
        }

        return plan.isActiveOn(date);
    }

    /**
     * Retrieve a commission plan by name.
     *
     * @param planName Name of the plan
     * @return The commission plan, or null if not found
     */
    public CommissionPlan getCommissionPlan(String planName) {
        return commissionPlans.get(planName);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // OBSERVABILITY - System Monitoring
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * BDD SCENARIO: Track commission calculations
     *
     * Business Value:
     * Monitoring helps understand system usage and performance.
     *
     * @return Number of calculations performed
     */
    public int getCalculationCount() {
        return calculationCount;
    }

    /**
     * Increment the calculation counter.
     */
    private void incrementCalculationCount() {
        calculationCount++;
    }

    /**
     * Reset the calculation counter.
     * Useful for scenario isolation in BDD tests.
     */
    public void resetCalculationCount() {
        calculationCount = 0;
    }

    /**
     * Reset all state (for BDD scenario isolation).
     *
     * BDD PRINCIPLE: Each scenario should be independent.
     * This method ensures clean state between scenarios.
     */
    public void reset() {
        calculationCount = 0;
        commissionPlans.clear();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // BDD LESSONS LEARNED
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * KEY BDD PRINCIPLES DEMONSTRATED:
     *
     * 1. UBIQUITOUS LANGUAGE
     *    - Methods use business terminology
     *    - Names match story file language
     *    - Example: "eligibility" not "validation"
     *
     * 2. BUSINESS VALUE FOCUS
     *    - Every method solves a business problem
     *    - Comments explain WHY, not just WHAT
     *    - Example: Tiered rates "incentivize larger deals"
     *
     * 3. CLEAR FEEDBACK
     *    - Methods provide human-readable messages
     *    - Errors explain business rules
     *    - Example: getIneligibilityReason()
     *
     * 4. TESTABLE BUSINESS RULES
     *    - Rules are explicit and checkable
     *    - Example: 90-day eligibility window
     *
     * 5. SCENARIO INDEPENDENCE
     *    - Reset methods for clean state
     *    - No shared mutable state between scenarios
     *
     * 6. LIVING DOCUMENTATION
     *    - Code documents business behavior
     *    - Story files provide examples
     *    - Together they explain the system
     */

    /**
     * ═══════════════════════════════════════════════════════════════════════
     * BDD WORKFLOW SUMMARY
     * ═══════════════════════════════════════════════════════════════════════
     *
     * Step 1: DISCOVERY (Collaborative)
     * - Product owner, developers, QA discuss feature
     * - Identify examples and edge cases
     * - Result: Shared understanding
     *
     * Step 2: FORMULATION (Write Scenarios)
     * - Convert examples to Given-When-Then format
     * - Write in story files using Gherkin
     * - Review with stakeholders
     * - Result: Executable specifications
     *
     * Step 3: AUTOMATION (Implement Steps)
     * - Map steps to code using @Given, @When, @Then
     * - Implement underlying business logic
     * - Run scenarios to verify
     * - Result: Working feature with tests
     *
     * Step 4: LIVING DOCUMENTATION
     * - Scenarios run continuously (CI/CD)
     * - Reports show current system behavior
     * - Documentation always up-to-date
     * - Result: Confidence in system behavior
     *
     * ═══════════════════════════════════════════════════════════════════════
     * FINAL THOUGHTS ON BDD
     * ═══════════════════════════════════════════════════════════════════════
     *
     * BDD is not just about testing—it's about communication and collaboration.
     *
     * The real value comes from:
     * - Shared language between business and tech
     * - Clear examples that eliminate ambiguity
     * - Documentation that evolves with the code
     * - Confidence that we're building the right thing
     *
     * Use BDD when:
     * - Complex business rules need clarification
     * - Multiple stakeholders need to understand behavior
     * - Requirements are unclear or ambiguous
     * - Living documentation is valuable
     *
     * BDD complements TDD:
     * - TDD ensures code correctness
     * - BDD ensures business value
     * - Use both for maximum benefit
     */
}