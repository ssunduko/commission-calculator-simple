package com.chapman.edu.commissions.doubles.stub;

import com.chapman.edu.commissions.model.*;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive demonstration of Test Doubles - Stub Pattern.
 *
 * STUB OVERVIEW:
 * A Stub is a test double that provides predetermined responses to method calls.
 * Unlike dummies (which are never used) and fakes (which have working implementations),
 * stubs return hardcoded values without any business logic.
 *
 * KEY CHARACTERISTICS:
 * ┌─────────────────────────┬──────────────────────────────────────────┐
 * │ Characteristic          │ Description                              │
 * ├─────────────────────────┼──────────────────────────────────────────┤
 * │ Purpose                 │ Provide controlled test data             │
 * │ Behavior                │ Returns predetermined values             │
 * │ State                   │ Usually stateless (or minimal state)     │
 * │ Implementation          │ Hardcoded return values                  │
 * │ Complexity              │ Simple - just return values              │
 * └─────────────────────────┴──────────────────────────────────────────┘
 *
 * TEST DOUBLE HIERARCHY:
 *
 * Dummy ← Simplest (passed but never used)
 *   ↓
 * Stub  ← Returns predetermined values ← YOU ARE HERE
 *   ↓
 * Spy   ← Records interactions + delegates
 *   ↓
 * Mock  ← Verifies behavior expectations
 *   ↓
 * Fake  ← Working implementation (simplified)
 *
 * STUB vs OTHER TEST DOUBLES:
 *
 * Dummy - Never used, just fills parameters
 * Stub  - Returns hardcoded values (THIS PATTERN)
 * Spy   - Wraps real object, records calls
 * Mock  - Expects specific calls, throws if not met
 * Fake  - Real logic with shortcuts (HashMap vs DB)
 *
 * WHEN TO USE STUBS:
 * ✓ Need specific return values for testing
 * ✓ Isolate code from dependencies
 * ✓ Simulate various scenarios (success, failure, edge cases)
 * ✓ Fast, deterministic tests
 * ✓ Testing error handling paths
 *
 * WHEN NOT TO USE STUBS:
 * ✗ Need to verify method was called (use Mock)
 * ✗ Need working implementation (use Fake)
 * ✗ Parameter never used (use Dummy)
 * ✗ Complex business logic needed (use real object or Fake)
 *
 * KEY CONCEPTS DEMONSTRATED:
 * 1. Basic Stubs - Simple return values
 * 2. Stub Interfaces - Implementing contracts
 * 3. Conditional Stubs - Different returns based on input
 * 4. Exception Stubs - Simulating error conditions
 * 5. Stateful Stubs - Returning different values over time
 * 6. Collection Stubs - Returning lists and maps
 * 7. Builder Pattern Stubs - Fluent stub creation
 * 8. Real-World Scenarios - Practical applications
 *
 */
@DisplayName("Test Doubles - Stub Pattern")
class StubTest {

    // ============================================
    // STUB IMPLEMENTATIONS
    // ============================================

    /**
     * BASIC STUB: Simple repository stub returning hardcoded data.
     *
     * This stub always returns the same predefined user.
     */
    static class StubUserRepository {
        private final User stubUser;

        public StubUserRepository(User stubUser) {
            this.stubUser = stubUser;
        }

        public Optional<User> findById(String id) {
            return Optional.ofNullable(stubUser);
        }

        public List<User> findAll() {
            return stubUser != null ? List.of(stubUser) : List.of();
        }
    }

    /**
     * CONDITIONAL STUB: Returns different values based on input.
     *
     * More sophisticated than basic stub - behavior varies by parameter.
     */
    static class ConditionalStubDealRepository {
        private final Map<String, Deal> deals = new HashMap<>();

        public ConditionalStubDealRepository() {
            // Pre-populate with test data
            deals.put("DEAL-1", createDeal("DEAL-1", "Small Deal", "10000"));
            deals.put("DEAL-2", createDeal("DEAL-2", "Large Deal", "100000"));
        }

        public Optional<Deal> findById(String id) {
            return Optional.ofNullable(deals.get(id));
        }

        public List<Deal> findByStatus(DealStatus status) {
            if (status == DealStatus.WON) {
                return List.of(createDeal("DEAL-1", "Won Deal", "50000"));
            } else if (status == DealStatus.OPEN) {
                return List.of(createDeal("DEAL-2", "Open Deal", "25000"));
            }
            return List.of();
        }

        private Deal createDeal(String id, String title, String value) {
            Deal deal = new Deal(title, new BigDecimal(value), "USER-1");
            deal.setId(id);
            return deal;
        }
    }

    /**
     * EXCEPTION STUB: Simulates error conditions.
     *
     * Used to test error handling without actual failures.
     */
    static class ExceptionStubUserRepository {
        public Optional<User> findById(String id) {
            if ("ERROR".equals(id)) {
                throw new RuntimeException("Database connection failed");
            }
            if ("NOT_FOUND".equals(id)) {
                return Optional.empty();
            }
            return Optional.of(new User("test", "test@test.com", "Test", "User"));
        }
    }

    /**
     * STATEFUL STUB: Returns different values on successive calls.
     *
     * Useful for testing pagination or retry logic.
     */
    static class StatefulStubNotificationService {
        private int callCount = 0;
        private final List<Boolean> returnSequence;

        public StatefulStubNotificationService(List<Boolean> returnSequence) {
            this.returnSequence = returnSequence;
        }

        public boolean sendEmail(String to, String subject, String message) {
            if (callCount < returnSequence.size()) {
                return returnSequence.get(callCount++);
            }
            return false;
        }

        public int getCallCount() {
            return callCount;
        }
    }

    /**
     * COMMISSION CALCULATOR STUB: Returns fixed commission rate.
     */
    static class StubCommissionCalculator {
        private final BigDecimal fixedRate;

        public StubCommissionCalculator(BigDecimal fixedRate) {
            this.fixedRate = fixedRate;
        }

        public BigDecimal calculateCommission(Deal deal) {
            return deal.getValue().multiply(fixedRate);
        }

        public BigDecimal getRate() {
            return fixedRate;
        }
    }

    /**
     * VALIDATION STUB: Always returns same validation result.
     */
    static class StubDealValidator {
        private final boolean isValid;
        private final List<String> errors;

        public StubDealValidator(boolean isValid, String... errors) {
            this.isValid = isValid;
            this.errors = Arrays.asList(errors);
        }

        public boolean validate(Deal deal) {
            return isValid;
        }

        public List<String> getValidationErrors(Deal deal) {
            return new ArrayList<>(errors);
        }
    }

    /**
     * EMAIL SERVICE STUB: Simulates email sending with success/failure.
     */
    static class StubEmailService {
        private final boolean shouldSucceed;

        public StubEmailService(boolean shouldSucceed) {
            this.shouldSucceed = shouldSucceed;
        }

        public boolean sendEmail(String to, String subject, String body) {
            return shouldSucceed;
        }
    }

    /**
     * DATE PROVIDER STUB: Returns fixed date for testing.
     *
     * Crucial for testing date-dependent logic.
     */
    static class StubDateProvider {
        private final LocalDate fixedDate;

        public StubDateProvider(LocalDate fixedDate) {
            this.fixedDate = fixedDate;
        }

        public LocalDate getCurrentDate() {
            return fixedDate;
        }

        public LocalDate addDays(int days) {
            return fixedDate.plusDays(days);
        }
    }

    // ============================================
    // 1. BASIC STUB USAGE
    // ============================================

    /**
     * BASIC STUB: Simplest form - returns hardcoded value.
     *
     * No logic, just predetermined response.
     */
    @Test
    @DisplayName("Basic Stub - Hardcoded return value")
    void testBasicStub() {
        // ARRANGE: Create stub with predetermined data
        User expectedUser = new User("jdoe", "john@example.com", "John", "Doe");
        StubUserRepository stubRepo = new StubUserRepository(expectedUser);

        // ACT: Call stub method
        Optional<User> result = stubRepo.findById("any-id");

        // ASSERT: Stub returns predetermined value
        assertTrue(result.isPresent());
        assertEquals("jdoe", result.get().getUsername());
        assertEquals("john@example.com", result.get().getEmail());

        // Key point: ID doesn't matter - stub always returns same value
        Optional<User> result2 = stubRepo.findById("different-id");
        assertEquals(expectedUser.getUsername(), result2.get().getUsername());
    }

    /**
     * STUB RETURNING EMPTY: Testing "not found" scenarios.
     */
    @Test
    @DisplayName("Basic Stub - Returns empty")
    void testStubReturnsEmpty() {
        // ARRANGE: Stub configured to return empty
        StubUserRepository stubRepo = new StubUserRepository(null);

        // ACT
        Optional<User> result = stubRepo.findById("any-id");

        // ASSERT: Stub returns empty as configured
        assertTrue(result.isEmpty());
    }

    /**
     * STUB RETURNING COLLECTION: Predetermined list of objects.
     */
    @Test
    @DisplayName("Basic Stub - Returns collection")
    void testStubReturnsCollection() {
        // ARRANGE
        User user = new User("test", "test@test.com", "Test", "User");
        StubUserRepository stubRepo = new StubUserRepository(user);

        // ACT
        List<User> results = stubRepo.findAll();

        // ASSERT: Stub returns predetermined list
        assertEquals(1, results.size());
        assertEquals("test", results.get(0).getUsername());
    }

    // ============================================
    // 2. CONDITIONAL STUBS
    // ============================================

    /**
     * CONDITIONAL STUB: Different returns based on input.
     *
     * More sophisticated - behavior varies by parameter.
     */
    @Test
    @DisplayName("Conditional Stub - Varies by input")
    void testConditionalStub() {
        // ARRANGE
        ConditionalStubDealRepository stubRepo = new ConditionalStubDealRepository();

        // ACT & ASSERT: Different inputs produce different outputs
        Optional<Deal> deal1 = stubRepo.findById("DEAL-1");
        assertTrue(deal1.isPresent());
        assertEquals("Small Deal", deal1.get().getTitle());

        Optional<Deal> deal2 = stubRepo.findById("DEAL-2");
        assertTrue(deal2.isPresent());
        assertEquals("Large Deal", deal2.get().getTitle());

        Optional<Deal> notFound = stubRepo.findById("DEAL-999");
        assertTrue(notFound.isEmpty());
    }

    /**
     * CONDITIONAL STUB BY STATUS: Different returns for different statuses.
     */
    @Test
    @DisplayName("Conditional Stub - By status")
    void testConditionalStubByStatus() {
        // ARRANGE
        ConditionalStubDealRepository stubRepo = new ConditionalStubDealRepository();

        // ACT
        List<Deal> wonDeals = stubRepo.findByStatus(DealStatus.WON);
        List<Deal> openDeals = stubRepo.findByStatus(DealStatus.OPEN);
        List<Deal> lostDeals = stubRepo.findByStatus(DealStatus.LOST);

        // ASSERT: Each status returns predetermined data
        assertEquals(1, wonDeals.size());
        assertEquals("Won Deal", wonDeals.get(0).getTitle());

        assertEquals(1, openDeals.size());
        assertEquals("Open Deal", openDeals.get(0).getTitle());

        assertEquals(0, lostDeals.size());
    }

    // ============================================
    // 3. EXCEPTION STUBS
    // ============================================

    /**
     * EXCEPTION STUB: Simulating error conditions.
     *
     * Test error handling without real failures.
     */
    @Test
    @DisplayName("Exception Stub - Throws exception")
    void testExceptionStub() {
        // ARRANGE
        ExceptionStubUserRepository stubRepo = new ExceptionStubUserRepository();

        // ACT & ASSERT: Stub throws exception on specific input
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            stubRepo.findById("ERROR");
        });

        assertTrue(exception.getMessage().contains("Database connection failed"));
    }

    /**
     * EXCEPTION STUB: Testing fallback logic.
     */
    @Test
    @DisplayName("Exception Stub - Testing error recovery")
    void testExceptionStubWithRecovery() {
        // ARRANGE
        ExceptionStubUserRepository stubRepo = new ExceptionStubUserRepository();

        // ACT: Try-catch to test recovery logic
        Optional<User> result;
        try {
            result = stubRepo.findById("ERROR");
        } catch (RuntimeException e) {
            // Recovery logic
            result = Optional.empty();
        }

        // ASSERT: Error was handled gracefully
        assertTrue(result.isEmpty());
    }

    // ============================================
    // 4. STATEFUL STUBS
    // ============================================

    /**
     * STATEFUL STUB: Returns different values on successive calls.
     *
     * Useful for testing retry logic or pagination.
     */
    @Test
    @DisplayName("Stateful Stub - Changes over time")
    void testStatefulStub() {
        // ARRANGE: Stub that fails twice, then succeeds
        StatefulStubNotificationService stubService =
            new StatefulStubNotificationService(List.of(false, false, true));

        // ACT & ASSERT: First two calls fail
        assertFalse(stubService.sendEmail("user@test.com", "Test", "Body"));
        assertFalse(stubService.sendEmail("user@test.com", "Test", "Body"));

        // Third call succeeds
        assertTrue(stubService.sendEmail("user@test.com", "Test", "Body"));
    }

    /**
     * STATEFUL STUB: Testing retry logic.
     */
    @Test
    @DisplayName("Stateful Stub - Retry mechanism")
    void testStatefulStubRetry() {
        // ARRANGE: Fails first 2 attempts, succeeds on 3rd
        StatefulStubNotificationService stubService =
            new StatefulStubNotificationService(List.of(false, false, true));

        // ACT: Retry up to 3 times
        boolean success = false;
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            if (stubService.sendEmail("user@test.com", "Test", "Body")) {
                success = true;
                break;
            }
        }

        // ASSERT: Eventually succeeded
        assertTrue(success);
        assertEquals(3, stubService.getCallCount());
    }

    // ============================================
    // 5. CALCULATION STUBS
    // ============================================

    /**
     * CALCULATION STUB: Fixed calculation results.
     *
     * Isolate testing from complex calculation logic.
     */
    @Test
    @DisplayName("Calculation Stub - Fixed commission rate")
    void testCalculationStub() {
        // ARRANGE: Stub with 10% commission rate
        StubCommissionCalculator stubCalc =
            new StubCommissionCalculator(new BigDecimal("0.10"));

        Deal deal = new Deal("Test Deal", new BigDecimal("100000"), "USER-1");

        // ACT
        BigDecimal commission = stubCalc.calculateCommission(deal);

        // ASSERT: Stub returns predetermined calculation
        assertEquals(new BigDecimal("10000.00"), commission);
    }

    /**
     * CALCULATION STUB: Testing different rates.
     */
    @Test
    @DisplayName("Calculation Stub - Multiple scenarios")
    void testCalculationStubMultipleScenarios() {
        // Test with different rates
        testCommissionScenario("0.05", "50000", "2500.00");  // 5%
        testCommissionScenario("0.10", "50000", "5000.00");  // 10%
        testCommissionScenario("0.15", "50000", "7500.00");  // 15%
    }

    private void testCommissionScenario(String rate, String dealValue, String expectedCommission) {
        StubCommissionCalculator stubCalc = new StubCommissionCalculator(new BigDecimal(rate));
        Deal deal = new Deal("Test", new BigDecimal(dealValue), "USER-1");

        BigDecimal commission = stubCalc.calculateCommission(deal);

        assertEquals(new BigDecimal(expectedCommission), commission);
    }

    // ============================================
    // 6. VALIDATION STUBS
    // ============================================

    /**
     * VALIDATION STUB: Predetermined validation results.
     *
     * Test validation handling without real validation logic.
     */
    @Test
    @DisplayName("Validation Stub - Always valid")
    void testValidationStubValid() {
        // ARRANGE: Stub that always validates successfully
        StubDealValidator stubValidator = new StubDealValidator(true);

        Deal deal = new Deal("Test Deal", new BigDecimal("1000"), "USER-1");

        // ACT
        boolean isValid = stubValidator.validate(deal);
        List<String> errors = stubValidator.getValidationErrors(deal);

        // ASSERT
        assertTrue(isValid);
        assertTrue(errors.isEmpty());
    }

    /**
     * VALIDATION STUB: Testing validation failures.
     */
    @Test
    @DisplayName("Validation Stub - With errors")
    void testValidationStubWithErrors() {
        // ARRANGE: Stub configured with validation errors
        StubDealValidator stubValidator = new StubDealValidator(
            false,
            "Deal value too low",
            "Missing sales rep"
        );

        Deal deal = new Deal("Invalid Deal", new BigDecimal("10"), "USER-1");

        // ACT
        boolean isValid = stubValidator.validate(deal);
        List<String> errors = stubValidator.getValidationErrors(deal);

        // ASSERT
        assertFalse(isValid);
        assertEquals(2, errors.size());
        assertTrue(errors.contains("Deal value too low"));
        assertTrue(errors.contains("Missing sales rep"));
    }

    // ============================================
    // 7. DATE/TIME STUBS
    // ============================================

    /**
     * DATE STUB: Fixed date for deterministic testing.
     *
     * Critical for testing date-dependent logic.
     */
    @Test
    @DisplayName("Date Stub - Fixed date")
    void testDateStub() {
        // ARRANGE: Stub always returns same date
        LocalDate fixedDate = LocalDate.of(2024, 1, 15);
        StubDateProvider stubDateProvider = new StubDateProvider(fixedDate);

        // ACT
        LocalDate currentDate = stubDateProvider.getCurrentDate();
        LocalDate futureDate = stubDateProvider.addDays(30);

        // ASSERT
        assertEquals(LocalDate.of(2024, 1, 15), currentDate);
        assertEquals(LocalDate.of(2024, 2, 14), futureDate);
    }

    /**
     * DATE STUB: Testing commission plan date ranges.
     */
    @Test
    @DisplayName("Date Stub - Commission plan validity")
    void testDateStubCommissionPlan() {
        // ARRANGE
        CommissionPlan plan = new CommissionPlan("2024 Plan", Currency.getInstance("USD"));
        plan.setEffectiveStartDate(LocalDate.of(2024, 1, 1));
        plan.setEffectiveEndDate(LocalDate.of(2024, 12, 31));
        plan.setStatus(PlanStatus.ACTIVE);

        // Test with different dates
        StubDateProvider jan15 = new StubDateProvider(LocalDate.of(2024, 1, 15));
        StubDateProvider dec01 = new StubDateProvider(LocalDate.of(2024, 12, 1));
        StubDateProvider dec31 = new StubDateProvider(LocalDate.of(2024, 12, 31));

        // ACT & ASSERT
        assertTrue(plan.isActiveOn(jan15.getCurrentDate()));
        assertTrue(plan.isActiveOn(dec01.getCurrentDate()));
        assertTrue(plan.isActiveOn(dec31.getCurrentDate()));
    }

    // ============================================
    // 8. REAL-WORLD SCENARIOS
    // ============================================

    /**
     * SCENARIO 1: Testing deal approval workflow with stubs.
     *
     * Stubs isolate business logic from dependencies.
     */
    @Test
    @DisplayName("Real-World - Deal approval with stubs")
    void testDealApprovalWorkflow() {
        // ARRANGE: Set up stubs for all dependencies
        Deal deal = new Deal("Big Deal", new BigDecimal("150000"), "USER-1");

        // Stub validator (always valid)
        StubDealValidator stubValidator = new StubDealValidator(true);

        // Stub email service (always succeeds)
        StubEmailService stubEmailService = new StubEmailService(true);

        // ACT: Test approval workflow
        boolean isValid = stubValidator.validate(deal);
        boolean notificationSent = false;

        if (isValid) {
            deal.setStatus(DealStatus.WON);
            notificationSent = stubEmailService.sendEmail(
                "manager@test.com",
                "Deal Approved",
                "Deal " + deal.getTitle() + " approved"
            );
        }

        // ASSERT: Workflow completed successfully
        assertTrue(isValid);
        assertEquals(DealStatus.WON, deal.getStatus());
        assertTrue(notificationSent);
    }

    /**
     * SCENARIO 2: Testing commission calculation pipeline.
     */
    @Test
    @DisplayName("Real-World - Commission calculation pipeline")
    void testCommissionCalculationPipeline() {
        // ARRANGE: Multiple stubs working together
        Deal deal = new Deal("Enterprise Deal", new BigDecimal("500000"), "USER-1");

        // Stub validator
        StubDealValidator stubValidator = new StubDealValidator(true);

        // Stub calculator (12% commission)
        StubCommissionCalculator stubCalc =
            new StubCommissionCalculator(new BigDecimal("0.12"));

        // Stub notification service
        StubEmailService stubEmailService = new StubEmailService(true);

        // ACT: Run through pipeline
        List<String> pipelineSteps = new ArrayList<>();

        // Step 1: Validate
        if (stubValidator.validate(deal)) {
            pipelineSteps.add("Validation passed");

            // Step 2: Calculate commission
            BigDecimal commission = stubCalc.calculateCommission(deal);
            pipelineSteps.add("Commission calculated: $" + commission);

            // Step 3: Send notification
            if (stubEmailService.sendEmail(
                    "finance@test.com",
                    "Commission Ready",
                    "Commission of $" + commission + " ready for USER-1")) {
                pipelineSteps.add("Notification sent");
            }
        }

        // ASSERT: Pipeline completed all steps
        assertEquals(3, pipelineSteps.size());
        assertTrue(pipelineSteps.contains("Validation passed"));
        assertTrue(pipelineSteps.contains("Commission calculated: $60000.00"));
        assertTrue(pipelineSteps.contains("Notification sent"));
    }

    /**
     * SCENARIO 3: Testing error handling with exception stubs.
     */
    @Test
    @DisplayName("Real-World - Error handling")
    void testErrorHandlingWithStubs() {
        // ARRANGE: Stub that fails, then succeeds
        StatefulStubNotificationService stubService =
            new StatefulStubNotificationService(List.of(false, false, true));

        Deal deal = new Deal("Test Deal", new BigDecimal("10000"), "USER-1");

        // ACT: Implement retry logic with exponential backoff
        boolean success = false;
        int attempt = 0;
        int maxAttempts = 3;

        while (attempt < maxAttempts && !success) {
            attempt++;
            success = stubService.sendEmail(
                "user@test.com",
                "Deal Update",
                "Deal " + deal.getTitle() + " status changed"
            );

            if (!success && attempt < maxAttempts) {
                // Simulate retry delay (in real code would sleep)
                // Thread.sleep(Math.pow(2, attempt) * 1000);
            }
        }

        // ASSERT: Eventually succeeded after retries
        assertTrue(success);
        assertEquals(3, attempt);
    }

    /**
     * SCENARIO 4: Testing date-sensitive business rules.
     */
    @Test
    @DisplayName("Real-World - Date-sensitive rules")
    void testDateSensitiveRules() {
        // ARRANGE: Commission plan with date range
        CommissionPlan plan = new CommissionPlan("Q1 2024 Plan", Currency.getInstance("USD"));
        plan.setEffectiveStartDate(LocalDate.of(2024, 1, 1));
        plan.setEffectiveEndDate(LocalDate.of(2024, 3, 31));
        plan.setStatus(PlanStatus.ACTIVE);

        // Test with different dates using stubs
        StubDateProvider earlyDate = new StubDateProvider(LocalDate.of(2023, 12, 31));
        StubDateProvider validDate = new StubDateProvider(LocalDate.of(2024, 2, 15));
        StubDateProvider lateDate = new StubDateProvider(LocalDate.of(2024, 4, 1));

        // ACT & ASSERT
        assertFalse(plan.isActiveOn(earlyDate.getCurrentDate()), "Too early");
        assertTrue(plan.isActiveOn(validDate.getCurrentDate()), "Within range");
        assertFalse(plan.isActiveOn(lateDate.getCurrentDate()), "Too late");
    }

    /**
     * SCENARIO 5: Testing validation with multiple error scenarios.
     */
    @Test
    @DisplayName("Real-World - Comprehensive validation")
    void testComprehensiveValidation() {
        // Test various validation scenarios
        testValidationScenario(true, new String[]{}, "Valid deal");
        testValidationScenario(false, new String[]{"Value too low"}, "Invalid value");
        testValidationScenario(false, new String[]{"Missing rep", "Invalid status"}, "Multiple errors");
    }

    private void testValidationScenario(boolean expectedValid, String[] errors, String scenario) {
        // ARRANGE
        StubDealValidator stubValidator = new StubDealValidator(expectedValid, errors);
        Deal deal = new Deal("Test", new BigDecimal("1000"), "USER-1");

        // ACT
        boolean isValid = stubValidator.validate(deal);
        List<String> validationErrors = stubValidator.getValidationErrors(deal);

        // ASSERT
        assertEquals(expectedValid, isValid, scenario);
        assertEquals(errors.length, validationErrors.size(), scenario);
    }

    // ============================================
    // 9. STUB BEST PRACTICES
    // ============================================

    /**
     * BEST PRACTICE 1: Stubs should be simple and focused.
     *
     * Don't add complex logic - that's a fake, not a stub.
     */
    @Test
    @DisplayName("Best Practice - Simple stubs")
    void testSimpleStubs() {
        // GOOD: Simple stub with hardcoded return
        StubEmailService stubService = new StubEmailService(true);
        assertTrue(stubService.sendEmail("test@test.com", "Subject", "Body"));

        // BAD (shown for education): Complex logic belongs in Fakes
        // Don't implement business rules in stubs
    }

    /**
     * BEST PRACTICE 2: Use stubs for specific test scenarios.
     *
     * Create focused stubs for each test case.
     */
    @Test
    @DisplayName("Best Practice - Scenario-specific stubs")
    void testScenarioSpecificStubs() {
        // Success scenario
        StubEmailService successStub = new StubEmailService(true);
        assertTrue(successStub.sendEmail("user@test.com", "Test", "Body"));

        // Failure scenario
        StubEmailService failureStub = new StubEmailService(false);
        assertFalse(failureStub.sendEmail("user@test.com", "Test", "Body"));
    }

    /**
     * BEST PRACTICE 3: Stubs don't verify behavior.
     *
     * If you need to verify calls, use a Mock instead.
     */
    @Test
    @DisplayName("Best Practice - Stubs don't verify")
    void testStubsDontVerify() {
        // Stubs just return values, don't verify
        StubUserRepository stubRepo = new StubUserRepository(
            new User("test", "test@test.com", "Test", "User")
        );

        // Call the stub
        stubRepo.findById("any-id");

        // Stubs don't track calls - if you need that, use a Mock
        // No verification here - that's not stub's purpose
    }

    // ============================================
    // 10. COMMON PITFALLS
    // ============================================

    /**
     * PITFALL 1: Adding too much logic to stubs.
     *
     * Stubs should return hardcoded values, not implement logic.
     */
    @Test
    @DisplayName("Pitfall - Overly complex stubs")
    void testOverlyComplexStubPitfall() {
        // GOOD: Simple stub
        StubCommissionCalculator simpleStub =
            new StubCommissionCalculator(new BigDecimal("0.10"));

        Deal deal = new Deal("Test", new BigDecimal("1000"), "USER-1");
        BigDecimal commission = simpleStub.calculateCommission(deal);

        assertEquals(new BigDecimal("100.00"), commission);

        // If stub needs complex logic, use a Fake instead
    }

    /**
     * PITFALL 2: Using stubs when mocks are needed.
     *
     * Stubs don't verify - if you need to check calls, use Mock.
     */
    @Test
    @DisplayName("Pitfall - Stub vs Mock confusion")
    void testStubVsMockConfusion() {
        // If you just need return values, use Stub
        StubEmailService stub = new StubEmailService(true);
        boolean sent = stub.sendEmail("user@test.com", "Test", "Body");
        assertTrue(sent);

        // If you need to verify the email was sent with specific params,
        // use a Mock instead (Mockito, for example)
    }

    /**
     * PITFALL 3: Stubs not matching real behavior.
     *
     * Stubs should return realistic values.
     */
    @Test
    @DisplayName("Pitfall - Unrealistic stub data")
    void testUnrealisticStubData() {
        // GOOD: Realistic data
        User realisticUser = new User("jdoe", "john.doe@example.com", "John", "Doe");
        StubUserRepository goodStub = new StubUserRepository(realisticUser);

        // BAD (shown for education): Unrealistic/inconsistent data can hide bugs
        // Example: Username "test" but email "production@real-company.com"
        // Keep stub data realistic to catch real bugs
    }
}