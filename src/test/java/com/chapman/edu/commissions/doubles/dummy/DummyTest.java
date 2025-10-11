package com.chapman.edu.commissions.doubles.dummy;

import com.chapman.edu.commissions.model.*;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive demonstration of Test Doubles - Dummy Pattern.
 *
 * DUMMY OVERVIEW:
 * A Dummy is the simplest form of test double. It is an object that is passed around but never
 * actually used. Dummies are typically used to fill parameter lists when the test doesn't care
 * about those particular parameters.
 *
 * KEY CHARACTERISTICS:
 * ┌─────────────────────────┬──────────────────────────────────────────┐
 * │ Characteristic          │ Description                              │
 * ├─────────────────────────┼──────────────────────────────────────────┤
 * │ Purpose                 │ Fill required parameters                 │
 * │ Usage                   │ Never actually invoked                   │
 * │ Implementation          │ Minimal or no logic                      │
 * │ Verification            │ No behavior verification needed          │
 * │ Complexity              │ Simplest test double type                │
 * └─────────────────────────┴──────────────────────────────────────────┘
 *
 * TEST DOUBLE HIERARCHY:
 *
 * Dummy ← Simplest (this pattern)
 *   ↓
 * Stub ← Returns pre-programmed values
 *   ↓
 * Spy ← Records information about usage
 *   ↓
 * Mock ← Verifies expectations and behavior
 *   ↓
 * Fake ← Has working implementation (simplified)
 *
 * WHEN TO USE DUMMIES:
 * ✓ Method signature requires parameters you don't care about
 * ✓ Constructor needs dependencies that won't be used in the test
 * ✓ Interface compliance - object needed but not invoked
 * ✓ Satisfying parameter lists in legacy code
 * ✓ Placeholder objects in collections or data structures
 *
 * WHEN NOT TO USE DUMMIES:
 * ✗ The object will actually be used in the test
 * ✗ You need to verify behavior (use Mock instead)
 * ✗ You need specific return values (use Stub instead)
 * ✗ You need to track calls (use Spy instead)
 *
 * KEY CONCEPTS DEMONSTRATED:
 * 1. Basic Dummy Objects - Simple null or minimal instances
 * 2. Constructor Parameter Dummies - Filling required constructor args
 * 3. Method Parameter Dummies - Satisfying method signatures
 * 4. Collection Dummies - Placeholder objects in lists/maps
 * 5. Interface Dummies - Minimal interface implementations
 * 6. Null vs Dummy - When to use each
 * 7. Dummy Builders - Creating reusable dummy factories
 * 8. Real-World Scenarios - Practical dummy usage
 *
 */
@DisplayName("Test Doubles - Dummy Pattern")
class DummyTest {

    // ============================================
    // 1. BASIC DUMMY CONCEPTS
    // ============================================

    /**
     * BASIC DUMMY: The simplest form - an object that exists but is never used.
     *
     * In this test, we create User objects that are required by the method signature
     * but the test logic never actually interacts with them.
     */
    @Test
    @DisplayName("Basic Dummy - Object passed but never used")
    void testBasicDummy() {
        // ARRANGE: Create a dummy user - values don't matter because it won't be used
        User dummyUser = new User();  // Completely empty object

        // Create another dummy with minimal data
        User dummyCreator = new User("dummy", "dummy@test.com", "Dummy", "User");

        // ACT: Create a deal - we're testing the deal creation, not the user
        Deal deal = new Deal("Test Deal", new BigDecimal("50000"), "SALES-001");

        // In real scenarios, dummyCreator might be passed to a method like:
        // dealService.createDeal(deal, dummyCreator);
        // But the service only logs the creator, never calls methods on it

        // ASSERT: Verify deal creation (dummy was never used)
        assertNotNull(deal);
        assertEquals("Test Deal", deal.getTitle());
        assertEquals(new BigDecimal("50000"), deal.getValue());

        // The dummy objects were never invoked - they just filled parameter lists
    }

    /**
     * NULL vs DUMMY: Understanding when to use null vs a dummy object.
     *
     * - Use null when the API allows it (nullable parameters)
     * - Use dummy when the API requires an object (non-null contract)
     */
    @Test
    @DisplayName("Null vs Dummy - Choosing the right approach")
    void testNullVsDummy() {
        // SCENARIO 1: API allows null - use null directly
        Deal dealWithNullDate = new Deal("Test", new BigDecimal("1000"), "USER-1");
        dealWithNullDate.setCloseDate(null);  // Null is acceptable
        assertNull(dealWithNullDate.getCloseDate());

        // SCENARIO 2: API requires non-null object - use dummy
        // Imagine a method: processPayment(Deal deal, User approver, Currency currency)
        // where 'approver' is required but never used in our test scenario

        User dummyApprover = new User();  // Dummy because API doesn't allow null
        Currency dummyCurrency = Currency.getInstance("USD");

        // The method signature requires these objects even if our test doesn't use them
        assertNotNull(dummyApprover);  // Dummy exists
        assertNotNull(dummyCurrency);  // Dummy exists

        // BEST PRACTICE: Use null when allowed, dummy when object is required
    }

    /**
     * DUMMY NAMING CONVENTIONS: Clear naming makes test intent obvious.
     *
     * Good dummy names communicate that the object is not relevant to the test.
     */
    @Test
    @DisplayName("Dummy Naming - Communicate intent through names")
    void testDummyNaming() {
        // GOOD: Prefix with 'dummy' or 'unused' to signal intent
        User dummyUser = new User();
        Deal unusedDeal = new Deal();
        CommissionPlan irrelevantPlan = new CommissionPlan();

        // GOOD: Suffix indicating the object won't be invoked
        User userNotUsed = new User("test", "test@test.com", "Test", "User");

        // BAD: Generic names hide the fact that object is a dummy
        // User user = new User();  // ✗ Unclear if this will be used
        // User u = new User();     // ✗ Even worse

        // ASSERT: Names document intent
        assertNotNull(dummyUser);
        assertNotNull(unusedDeal);
        assertNotNull(irrelevantPlan);
        assertNotNull(userNotUsed);
    }

    // ============================================
    // 2. CONSTRUCTOR PARAMETER DUMMIES
    // ============================================

    /**
     * CONSTRUCTOR DUMMIES: Objects required by constructors but not used in tests.
     *
     * Legacy code often has constructors with many dependencies. Dummies help
     * satisfy these requirements when testing specific behavior.
     */
    @Test
    @DisplayName("Constructor Dummies - Filling required constructor parameters")
    void testConstructorDummies() {
        // SCENARIO: Testing a commission calculation service that requires
        // multiple dependencies in its constructor, but we only test one method

        // Create dummy dependencies
        User dummyAuditor = new User("auditor", "audit@test.com", "Audit", "User");
        CommissionPlan dummyFallbackPlan = new CommissionPlan("Fallback", Currency.getInstance("USD"));

        // In real code, might look like:
        // CommissionService service = new CommissionService(
        //     dummyAuditor,        // Never used in our test
        //     dummyFallbackPlan,   // Never used in our test
        //     emailService,        // Real dependency we care about
        //     calculator           // Real dependency we care about
        // );

        // For this test, we verify the dummies exist but are never invoked
        assertNotNull(dummyAuditor);
        assertNotNull(dummyFallbackPlan);

        // The actual test would call the method that doesn't use these dummies
    }

    /**
     * MINIMAL DUMMY CONSTRUCTION: Creating dummies with minimal setup.
     *
     * Dummies should be constructed with the least effort since they're never used.
     */
    @Test
    @DisplayName("Minimal Dummy Construction - Least effort approach")
    void testMinimalDummyConstruction() {
        // APPROACH 1: Default constructor (best for dummies)
        User dummyUser1 = new User();

        // APPROACH 2: Minimal constructor (when default not available)
        Deal dummyDeal = new Deal("dummy", BigDecimal.ZERO, "dummy");

        // APPROACH 3: Builder with defaults (if available)
        // DealBuilder.dummy()  // Hypothetical factory method

        // ANTI-PATTERN: Over-specifying dummy data
        // User overspecifiedDummy = new User("john.doe", "john.doe@example.com", "John", "Doe");
        // overspecifiedDummy.setDepartment("Sales");
        // overspecifiedDummy.setTerritory("West");
        // overspecifiedDummy.addRole(UserRole.SALES_REP);
        // ✗ Too much setup for an object that's never used!

        // ASSERT: Minimal dummies are created
        assertNotNull(dummyUser1);
        assertNotNull(dummyDeal);
    }

    // ============================================
    // 3. METHOD PARAMETER DUMMIES
    // ============================================

    /**
     * METHOD PARAMETER DUMMIES: Satisfying method signatures with unused objects.
     *
     * Many methods have parameters that are only used in specific code paths.
     * Dummies fill these parameters when testing other paths.
     */
    @Test
    @DisplayName("Method Parameter Dummies - Unused method arguments")
    void testMethodParameterDummies() {
        // SCENARIO: Testing a method that approves deals
        // Signature: approveDeal(Deal deal, User approver, String comments)
        // Our test focuses on the deal, not the approver or comments

        Deal dealToTest = new Deal("Important Deal", new BigDecimal("100000"), "USER-1");
        dealToTest.setStatus(DealStatus.OPEN);

        // Create dummies for unused parameters
        User dummyApprover = new User();  // Required but not used in test
        String dummyComments = "dummy";    // Required but not used in test

        // In real code: dealService.approveDeal(dealToTest, dummyApprover, dummyComments);

        // The test verifies deal status changed, doesn't care about approver
        assertNotNull(dummyApprover);
        assertNotNull(dummyComments);
        assertEquals(DealStatus.OPEN, dealToTest.getStatus());
    }

    /**
     * VARARGS DUMMIES: Filling variable argument lists with dummy objects.
     *
     * Methods with varargs often require at least one argument, even if unused.
     */
    @Test
    @DisplayName("Varargs Dummies - Filling variable argument lists")
    void testVarargsDummies() {
        // SCENARIO: Method signature like: notifyUsers(String message, User... users)
        // We're testing the message, not the users

        String importantMessage = "System maintenance scheduled";

        // Create dummy users to satisfy varargs requirement
        User dummyUser1 = new User();
        User dummyUser2 = new User();
        User dummyUser3 = new User();

        // In real code: notificationService.notifyUsers(importantMessage, dummyUser1, dummyUser2, dummyUser3);

        // Test focuses on message, users are just dummies
        assertNotNull(importantMessage);
        assertNotNull(dummyUser1);
        assertNotNull(dummyUser2);
        assertNotNull(dummyUser3);
    }

    // ============================================
    // 4. COLLECTION DUMMIES
    // ============================================

    /**
     * COLLECTION OF DUMMIES: Placeholder objects in lists, sets, or maps.
     *
     * Tests often need collections of specific sizes but don't care about content.
     */
    @Test
    @DisplayName("Collection Dummies - Dummy objects in collections")
    void testCollectionDummies() {
        // SCENARIO: Testing pagination logic that needs a list of 100 items
        // We don't care about the items, just the count

        List<User> dummyUsers = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            dummyUsers.add(new User());  // 100 dummy users
        }

        // Test pagination logic
        int pageSize = 10;
        int totalPages = (int) Math.ceil((double) dummyUsers.size() / pageSize);

        assertEquals(100, dummyUsers.size());
        assertEquals(10, totalPages);

        // The actual user data doesn't matter, only the count
    }

    /**
     * MAP DUMMIES: Dummy objects as keys or values in maps.
     *
     * Maps often require objects as keys/values even when testing map operations.
     */
    @Test
    @DisplayName("Map Dummies - Dummy keys and values in maps")
    void testMapDummies() {
        // SCENARIO: Testing a cache implementation
        // We care about cache behavior, not the actual data

        Map<String, Deal> dealCache = new HashMap<>();

        // Populate with dummy deals
        dealCache.put("key1", new Deal());  // Dummy deal
        dealCache.put("key2", new Deal());  // Dummy deal
        dealCache.put("key3", new Deal());  // Dummy deal

        // Test cache operations
        assertEquals(3, dealCache.size());
        assertTrue(dealCache.containsKey("key1"));
        assertNotNull(dealCache.get("key2"));

        // The deal objects are dummies - we're testing the map, not the deals
    }

    /**
     * DUMMY COLLECTIONS: Empty or minimal collections as parameters.
     *
     * Methods may require collection parameters that won't be iterated.
     */
    @Test
    @DisplayName("Dummy Collections - Minimal collection parameters")
    void testDummyCollections() {
        // SCENARIO: Method signature requires a list but our test path doesn't use it
        // Signature: validateDeal(Deal deal, List<String> validationRules)

        Deal dealToValidate = new Deal("Test", new BigDecimal("5000"), "USER-1");

        // Create dummy collection - empty is fine since it won't be used
        List<String> dummyValidationRules = new ArrayList<>();  // Empty dummy list

        // Alternative: Collections.emptyList() for immutable dummy
        List<User> dummyNotificationList = Collections.emptyList();

        // In real code: validator.validateDeal(dealToValidate, dummyValidationRules);

        assertNotNull(dummyValidationRules);
        assertNotNull(dummyNotificationList);
        assertTrue(dummyValidationRules.isEmpty());
        assertTrue(dummyNotificationList.isEmpty());
    }

    // ============================================
    // 5. INTERFACE DUMMIES
    // ============================================

    /**
     * INTERFACE DUMMIES: Minimal implementations of interfaces.
     *
     * When a method requires an interface, provide the simplest implementation.
     */
    @Test
    @DisplayName("Interface Dummies - Minimal interface implementations")
    void testInterfaceDummies() {
        // SCENARIO: Method requires a Comparator but we're testing something else

        // Dummy comparator - returns 0 for all comparisons (everything is equal)
        Comparator<Deal> dummyComparator = (d1, d2) -> 0;

        // Dummy runnable that does nothing
        Runnable dummyCallback = () -> {};

        // Use in context where these are required but not invoked
        assertNotNull(dummyComparator);
        assertNotNull(dummyCallback);

        // These satisfy type requirements without real logic
        assertEquals(0, dummyComparator.compare(new Deal(), new Deal()));
        dummyCallback.run();  // Does nothing, which is fine for a dummy
    }

    /**
     * ANONYMOUS DUMMY CLASSES: Creating dummy implementations inline.
     *
     * For complex interfaces, anonymous classes provide minimal implementations.
     */
    @Test
    @DisplayName("Anonymous Dummy Classes - Inline minimal implementations")
    void testAnonymousDummyClasses() {
        // SCENARIO: Testing code that requires an Observer pattern listener
        // We don't care about notifications in this test

        // Dummy observer with no-op methods
        DealObserver dummyObserver = new DealObserver() {
            @Override
            public void onDealCreated(Deal deal) {
                // Dummy - do nothing
            }

            @Override
            public void onDealUpdated(Deal deal) {
                // Dummy - do nothing
            }

            @Override
            public void onDealDeleted(String dealId) {
                // Dummy - do nothing
            }
        };

        // Use dummy in test
        assertNotNull(dummyObserver);

        // Calling methods does nothing, which is expected for dummies
        dummyObserver.onDealCreated(new Deal());
        dummyObserver.onDealUpdated(new Deal());
        dummyObserver.onDealDeleted("DEAL-001");
    }

    // Dummy interface for demonstration
    interface DealObserver {
        void onDealCreated(Deal deal);
        void onDealUpdated(Deal deal);
        void onDealDeleted(String dealId);
    }

    // ============================================
    // 6. DUMMY FACTORIES
    // ============================================

    /**
     * DUMMY FACTORY METHODS: Centralized dummy creation for reusability.
     *
     * Factory methods make dummy creation consistent and easier to maintain.
     */
    @Test
    @DisplayName("Dummy Factories - Reusable dummy creation methods")
    void testDummyFactories() {
        // Use factory methods for consistent dummy creation
        User dummyUser = createDummyUser();
        Deal dummyDeal = createDummyDeal();
        CommissionPlan dummyPlan = createDummyCommissionPlan();

        // Factories ensure dummies are created the same way everywhere
        assertNotNull(dummyUser);
        assertNotNull(dummyDeal);
        assertNotNull(dummyPlan);

        // Additional dummies created consistently
        User anotherDummyUser = createDummyUser();
        assertNotNull(anotherDummyUser);
    }

    // Factory methods for creating dummies
    private static User createDummyUser() {
        return new User();  // Minimal construction
    }

    private static Deal createDummyDeal() {
        return new Deal("dummy", BigDecimal.ZERO, "dummy");
    }

    private static CommissionPlan createDummyCommissionPlan() {
        return new CommissionPlan("dummy", Currency.getInstance("USD"));
    }

    /**
     * DUMMY BUILDER PATTERN: Fluent API for creating dummies with optional customization.
     *
     * Builders provide flexibility while maintaining minimal default dummies.
     */
    @Test
    @DisplayName("Dummy Builders - Fluent dummy creation")
    void testDummyBuilders() {
        // Basic dummy with defaults
        Deal basicDummy = new DealDummyBuilder().build();

        // Customized dummy (still minimal but with specific values if needed)
        Deal customizedDummy = new DealDummyBuilder()
            .withTitle("Custom Dummy")
            .build();

        assertNotNull(basicDummy);
        assertEquals("dummy", basicDummy.getTitle());  // Default

        assertNotNull(customizedDummy);
        assertEquals("Custom Dummy", customizedDummy.getTitle());  // Customized
    }

    // Dummy builder for creating Deal dummies
    static class DealDummyBuilder {
        private String title = "dummy";
        private BigDecimal value = BigDecimal.ZERO;
        private String salesRepId = "dummy";

        DealDummyBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        DealDummyBuilder withValue(BigDecimal value) {
            this.value = value;
            return this;
        }

        Deal build() {
            return new Deal(title, value, salesRepId);
        }
    }

    // ============================================
    // 7. REAL-WORLD SCENARIOS
    // ============================================

    /**
     * SCENARIO 1: Testing business logic that requires audit trail objects.
     *
     * Audit parameters are required but not relevant to the business logic test.
     */
    @Test
    @DisplayName("Real-World - Audit trail dummies in business logic")
    void testAuditTrailDummies() {
        // SCENARIO: Approving a deal requires audit information
        // We're testing approval logic, not audit trail

        Deal dealToApprove = new Deal("Enterprise Sale", new BigDecimal("250000"), "USER-100");
        dealToApprove.setStatus(DealStatus.OPEN);

        // Dummy audit objects
        User dummyAuditor = new User("auditor", "audit@system.com", "System", "Auditor");
        LocalDate dummyAuditDate = LocalDate.now();
        String dummyAuditReason = "dummy audit reason";

        // In real code: dealService.approveDeal(dealToApprove, dummyAuditor, dummyAuditDate, dummyAuditReason);

        // Test focuses on deal state change
        assertNotNull(dealToApprove);
        assertEquals(DealStatus.OPEN, dealToApprove.getStatus());

        // Audit dummies satisfied the API contract but weren't used in our test
        assertNotNull(dummyAuditor);
        assertNotNull(dummyAuditDate);
        assertNotNull(dummyAuditReason);
    }

    /**
     * SCENARIO 2: Testing notification system with recipient dummies.
     *
     * Testing notification formatting, not actual recipient data.
     */
    @Test
    @DisplayName("Real-World - Recipient dummies in notifications")
    void testNotificationRecipientDummies() {
        // SCENARIO: Testing notification message formatting
        // Recipients required but not used in formatting logic

        Deal dealForNotification = new Deal("Big Deal", new BigDecimal("500000"), "USER-200");

        // Create dummy recipients
        List<User> dummyRecipients = Arrays.asList(
            new User(),
            new User(),
            new User()
        );

        // Format notification message (doesn't use recipient details)
        String message = String.format(
            "Deal '%s' worth %s requires approval",
            dealForNotification.getTitle(),
            dealForNotification.getValue()
        );

        // Test message formatting
        assertTrue(message.contains("Big Deal"));
        assertTrue(message.contains("500000"));

        // Recipients were dummies - never used in formatting
        assertEquals(3, dummyRecipients.size());
    }

    /**
     * SCENARIO 3: Testing validation logic with configuration dummies.
     *
     * Configuration objects required but test focuses on validation rules.
     */
    @Test
    @DisplayName("Real-World - Configuration dummies in validation")
    void testValidationConfigurationDummies() {
        // SCENARIO: Testing deal validation rules
        // System configuration required but not used in this validation path

        Deal dealToValidate = new Deal("Test Deal", new BigDecimal("1000"), "USER-1");

        // Dummy configuration objects
        Currency dummySystemCurrency = Currency.getInstance("USD");
        CommissionPlan dummyDefaultPlan = new CommissionPlan("Default", dummySystemCurrency);
        User dummySystemAdmin = new User("admin", "admin@system.com", "System", "Admin");

        // Validation logic (doesn't use configuration in this path)
        boolean isValid = dealToValidate.getValue().compareTo(BigDecimal.ZERO) > 0;

        // Test validation
        assertTrue(isValid);
        assertEquals(new BigDecimal("1000"), dealToValidate.getValue());

        // Configuration dummies satisfied dependencies but weren't invoked
        assertNotNull(dummySystemCurrency);
        assertNotNull(dummyDefaultPlan);
        assertNotNull(dummySystemAdmin);
    }

    /**
     * SCENARIO 4: Testing batch processing with dummy callback handlers.
     *
     * Callbacks required by API but not relevant to processing logic test.
     */
    @Test
    @DisplayName("Real-World - Callback dummies in batch processing")
    void testBatchProcessingCallbackDummies() {
        // SCENARIO: Testing batch deal processing
        // Success/failure callbacks required but we're testing processing count

        List<Deal> dealsToProcess = Arrays.asList(
            new Deal("Deal 1", new BigDecimal("1000"), "USER-1"),
            new Deal("Deal 2", new BigDecimal("2000"), "USER-2"),
            new Deal("Deal 3", new BigDecimal("3000"), "USER-3")
        );

        // Dummy callbacks (not invoked in our test)
        Runnable dummySuccessCallback = () -> {};
        Runnable dummyFailureCallback = () -> {};

        // Process deals (callbacks not used in this test path)
        int processedCount = dealsToProcess.size();

        // Test processing count
        assertEquals(3, processedCount);

        // Callbacks were dummies - satisfied API but never called
        assertNotNull(dummySuccessCallback);
        assertNotNull(dummyFailureCallback);
    }

    // ============================================
    // 8. DUMMY BEST PRACTICES
    // ============================================

    /**
     * BEST PRACTICE 1: Use descriptive names to indicate dummy intent.
     *
     * Clear naming prevents confusion about whether an object will be used.
     */
    @Test
    @DisplayName("Best Practice - Descriptive dummy naming")
    void testDescriptiveDummyNaming() {
        // GOOD: Clear dummy names
        User dummyUser = new User();
        Deal unusedDeal = new Deal();
        CommissionPlan notInvokedPlan = new CommissionPlan();
        List<User> irrelevantUsers = new ArrayList<>();

        // These names communicate that objects are dummies
        assertNotNull(dummyUser);
        assertNotNull(unusedDeal);
        assertNotNull(notInvokedPlan);
        assertNotNull(irrelevantUsers);
    }

    /**
     * BEST PRACTICE 2: Minimize dummy construction effort.
     *
     * Dummies should be created with minimal code since they're never used.
     */
    @Test
    @DisplayName("Best Practice - Minimal dummy construction")
    void testMinimalDummyConstructionBestPractice() {
        // GOOD: Minimal construction
        User dummyUser = new User();
        Deal dummyDeal = createDummyDeal();

        // BAD: Over-specified construction (anti-pattern shown for education)
        // User overDone = new User("specific", "specific@test.com", "First", "Last");
        // overDone.setDepartment("Specific Department");
        // overDone.addRole(UserRole.SALES_REP);
        // ✗ Too much setup for an unused object!

        assertNotNull(dummyUser);
        assertNotNull(dummyDeal);
    }

    /**
     * BEST PRACTICE 3: Use factory methods for consistency.
     *
     * Centralized dummy creation ensures consistency across tests.
     */
    @Test
    @DisplayName("Best Practice - Dummy factory methods")
    void testDummyFactoryBestPractice() {
        // GOOD: Use factories for consistent dummy creation
        User dummy1 = createDummyUser();
        User dummy2 = createDummyUser();
        User dummy3 = createDummyUser();

        // All dummies created the same way
        assertNotNull(dummy1);
        assertNotNull(dummy2);
        assertNotNull(dummy3);
    }

    /**
     * BEST PRACTICE 4: Document why an object is a dummy.
     *
     * Comments explain why the object exists but isn't used.
     */
    @Test
    @DisplayName("Best Practice - Document dummy purpose")
    void testDocumentedDummies() {
        // Dummy user required by API signature but not used in this test path
        User dummyApprover = new User();

        // Dummy currency needed for plan creation but not validated in this test
        Currency dummyCurrency = Currency.getInstance("USD");

        // Documentation makes test intent clear
        assertNotNull(dummyApprover);
        assertNotNull(dummyCurrency);
    }

    /**
     * BEST PRACTICE 5: Prefer null when allowed, dummy when required.
     *
     * Use the simplest approach - null if possible, dummy when necessary.
     */
    @Test
    @DisplayName("Best Practice - Null vs dummy decision")
    void testNullVsDummyBestPractice() {
        Deal deal = new Deal("Test", new BigDecimal("1000"), "USER-1");

        // GOOD: Use null when API allows it
        deal.setCloseDate(null);  // Nullable parameter

        // GOOD: Use dummy when API requires non-null
        User dummyCreator = new User();  // Required parameter

        assertNull(deal.getCloseDate());
        assertNotNull(dummyCreator);
    }

    // ============================================
    // 9. COMMON PITFALLS
    // ============================================

    /**
     * PITFALL 1: Over-specifying dummy data.
     *
     * Adding unnecessary detail to objects that won't be used.
     */
    @Test
    @DisplayName("Pitfall - Over-specifying dummy data")
    void testOverSpecifyingDummyPitfall() {
        // WRONG: Too much detail for a dummy (anti-pattern for education)
        // User overSpecifiedDummy = new User("john.doe", "john.doe@company.com", "John", "Doe");
        // overSpecifiedDummy.setDepartment("Sales");
        // overSpecifiedDummy.setTerritory("West Coast");
        // overSpecifiedDummy.addRole(UserRole.SALES_REP);
        // overSpecifiedDummy.setActive(true);
        // ✗ This object is never used - why all the setup?

        // RIGHT: Minimal dummy
        User properDummy = new User();  // ✓ Simple and clear

        assertNotNull(properDummy);
    }

    /**
     * PITFALL 2: Unclear dummy names.
     *
     * Generic names hide that an object is a dummy.
     */
    @Test
    @DisplayName("Pitfall - Unclear dummy naming")
    void testUnclearDummyNamesPitfall() {
        // WRONG: Unclear names (anti-pattern for education)
        // User user = new User();  // ✗ Will this be used?
        // Deal d = new Deal();     // ✗ Even worse

        // RIGHT: Clear dummy names
        User dummyUser = new User();  // ✓ Obviously a dummy
        Deal unusedDeal = new Deal(); // ✓ Obviously unused

        assertNotNull(dummyUser);
        assertNotNull(unusedDeal);
    }

    /**
     * PITFALL 3: Using dummies when you need stubs or mocks.
     *
     * Dummies are for unused objects - use other test doubles when needed.
     */
    @Test
    @DisplayName("Pitfall - Wrong test double type")
    void testWrongTestDoubleTypePitfall() {
        // SCENARIO: Testing a method that WILL call getFullName()
        User user = new User("test", "test@test.com", "Test", "User");

        // WRONG: Calling this a dummy when it WILL be used
        // User dummyUser = new User();  // ✗ Misleading - this isn't a dummy!

        // RIGHT: This is a real object or stub, not a dummy
        String fullName = user.getFullName();
        assertEquals("Test User", fullName);

        // If you need to verify calls, use a Mock
        // If you need specific return values, use a Stub
        // If you don't use it at all, use a Dummy
    }
}