package com.chapman.edu.commissions.doubles.spy;

import com.chapman.edu.commissions.model.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive demonstration of Mockito Spy capabilities.
 *
 * SPY OVERVIEW:
 * A Spy is a special type of test double that wraps a REAL object. Unlike mocks which are
 * empty shells, spies delegate to actual method implementations by default. You can selectively
 * stub specific methods while keeping the rest of the real behavior.
 *
 * KEY DIFFERENCES - MOCK vs SPY:
 * ┌─────────────────┬─────────────────────────┬─────────────────────────┐
 * │ Aspect          │ Mock                    │ Spy                     │
 * ├─────────────────┼─────────────────────────┼─────────────────────────┤
 * │ Base            │ Empty shell             │ Real object             │
 * │ Default         │ Returns null/0/false    │ Calls real methods      │
 * │ Use Case        │ Complete control        │ Partial mocking         │
 * │ Performance     │ Fast (no logic)         │ Slower (real methods)   │
 * │ Creation        │ mock(Class.class)       │ spy(realObject)         │
 * └─────────────────┴─────────────────────────┴─────────────────────────┘
 *
 * KEY CONCEPTS DEMONSTRATED:
 * 1. Basic Spy Creation - Creating spies with @Spy and spy() method
 * 2. Real Method Calls - Understanding default spy behavior
 * 3. Selective Stubbing - Overriding specific methods while keeping others real
 * 4. Stubbing Best Practices - Using doReturn() vs when() for spies
 * 5. Verification - Verifying both stubbed and real method calls
 * 6. Spy vs Mock Comparison - Understanding when to use each
 * 7. Partial Mocking Use Cases - Real-world scenarios for spying
 * 8. Spy Limitations - What you cannot do with spies
 * 9. Spy with Collections - Spying on lists, sets, and maps
 * 10. Integration Testing - Using spies for semi-integration tests
 *
 * WHEN TO USE SPIES:
 * ✓ Testing legacy code with complex dependencies
 * ✓ Verifying calls to real object methods
 * ✓ Partially overriding behavior for testing edge cases
 * ✓ Testing abstract classes or partial implementations
 * ✓ Semi-integration tests where most logic should be real
 *
 * WHEN NOT TO USE SPIES:
 * ✗ When you need complete control (use mocks)
 * ✗ Testing simple objects without dependencies
 * ✗ When real methods have side effects you want to avoid
 * ✗ Pure unit tests (prefer mocks for better isolation)
 *
 * @see <a href="https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#spy">Mockito Spy Documentation</a>
 */
@DisplayName("Mockito Spy Concepts")
class MockitoSpyTest {

    // ============================================
    // 1. BASIC SPY CREATION
    // ============================================

    /**
     * @Spy annotation creates spies of real objects.
     * Unlike @Mock, @Spy requires the object to be instantiated.
     */
    @Spy
    private User spyUser = new User("testuser", "test@example.com", "Test", "User");

    /**
     * AutoCloseable for managing Mockito lifecycle.
     */
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    // ============================================
    // 2. CREATING SPIES
    // ============================================

    /**
     * CREATING SPIES: Three different ways to create spy objects.
     *
     * Method 1: spy(realObject) - Programmatic creation
     * Method 2: @Spy annotation - Field-level annotation
     * Method 3: spy(new Class()) - Inline creation with constructor
     */
    @Test
    @DisplayName("Creating Spies - Different approaches")
    void testCreatingSpies() {
        // METHOD 1: Using spy() method
        Deal realDeal = new Deal("Test Deal", new BigDecimal("50000"), "USER-1");
        Deal spyDeal = spy(realDeal);

        // METHOD 2: Using @Spy annotation (spyUser field defined above)
        assertNotNull(spyUser);

        // METHOD 3: Inline spy creation
        CommissionPlan spyPlan = spy(new CommissionPlan("Test Plan", Currency.getInstance("USD")));

        // All three are spies - they call real methods by default
        assertNotNull(spyDeal.getTitle());
        assertNotNull(spyUser.getUsername());
        assertNotNull(spyPlan.getName());

        assertEquals("Test Deal", spyDeal.getTitle());
        assertEquals("testuser", spyUser.getUsername());
        assertEquals("Test Plan", spyPlan.getName());
    }

    // ============================================
    // 3. REAL METHOD CALLS
    // ============================================

    /**
     * REAL METHOD CALLS: By default, spies call actual methods on the real object.
     * This is the fundamental difference between spies and mocks.
     */
    @Test
    @DisplayName("Real Method Calls - Default spy behavior")
    void testRealMethodCalls() {
        // ARRANGE: Create spy of real User
        User realUser = new User("jdoe", "john.doe@example.com", "John", "Doe");
        User spyUser = spy(realUser);

        // ACT: Call methods - these invoke REAL implementations
        String username = spyUser.getUsername();
        String email = spyUser.getEmail();
        String fullName = spyUser.getFullName();  // Computed from firstName + lastName

        // ASSERT: Real methods were executed
        assertEquals("jdoe", username);
        assertEquals("john.doe@example.com", email);
        assertEquals("John Doe", fullName);  // Real method concatenated the names

        // Modify the real object through spy
        spyUser.setActive(true);
        assertTrue(spyUser.isActive());  // Real setter/getter worked

        // State changes are preserved in the real object
        assertTrue(realUser.isActive());  // Both spy and real object share state
    }

    /**
     * STATE SHARING: Spy wraps a real object and delegates to it.
     */
    @Test
    @DisplayName("State Sharing - Spy delegates to wrapped object")
    void testStateSharing() {
        // ARRANGE: Create real object and spy
        User realUser = new User("original", "original@test.com", "Original", "Name");
        User spyUser = spy(realUser);

        // ACT: Modify through spy's real methods (not stubbed)
        spyUser.setActive(true);

        // ASSERT: State changes made through spy ARE visible in original object
        // because the spy delegates method calls to the wrapped instance
        assertTrue(spyUser.isActive());
        assertTrue(realUser.isActive());  // Same underlying object!

        // Both references maintain consistent state through delegation
        assertEquals(realUser.getUsername(), spyUser.getUsername());
        assertEquals(realUser.getEmail(), spyUser.getEmail());
    }

    // ============================================
    // 4. SELECTIVE STUBBING
    // ============================================

    /**
     * SELECTIVE STUBBING: Override specific methods while keeping others real.
     * This is the primary use case for spies - partial mocking.
     */
    @Test
    @DisplayName("Selective Stubbing - Override specific methods")
    void testSelectiveStubbing() {
        // ARRANGE: Create spy
        User realUser = new User("jsmith", "john.smith@example.com", "John", "Smith");
        User spyUser = spy(realUser);

        // Stub only the email method - all others remain real
        doReturn("fake.email@example.com").when(spyUser).getEmail();

        // ACT & ASSERT: Stubbed method returns mocked value
        assertEquals("fake.email@example.com", spyUser.getEmail());

        // Real methods still work
        assertEquals("jsmith", spyUser.getUsername());
        assertEquals("John Smith", spyUser.getFullName());

        // Can stub more methods selectively
        doReturn(true).when(spyUser).hasRole(any(UserRole.class));
        assertTrue(spyUser.hasRole(UserRole.SALES_REP));
        assertTrue(spyUser.hasRole(UserRole.FINANCE_ADMIN));  // Stubbed for any role
    }

    /**
     * STUBBING VOID METHODS: Use doNothing(), doThrow() for void methods.
     */
    @Test
    @DisplayName("Stubbing Void Methods - Prevent real execution")
    void testStubbingVoidMethods() {
        // ARRANGE: Create spy - using User.setActive as it's a simpler void method
        User realUser = new User("test", "test@test.com", "Test", "User");
        User spyUser = spy(realUser);

        // User constructor sets active = true by default, so set to false first
        spyUser.setActive(false);
        assertFalse(spyUser.isActive());

        // Stub void method to do nothing (prevent real execution)
        doNothing().when(spyUser).setActive(true);

        // ACT: Call the stubbed void method
        spyUser.setActive(true);

        // ASSERT: Method was called but didn't execute real logic
        verify(spyUser, times(2)).setActive(anyBoolean());  // Called twice: once for false, once for true

        // State was NOT actually changed because we stubbed it with doNothing()
        assertFalse(spyUser.isActive());  // Still false - real setter was bypassed
    }

    // ============================================
    // 5. DORETURN VS WHEN
    // ============================================

    /**
     * DORETURN() vs WHEN(): Critical difference for spies!
     *
     * when(spy.method()).thenReturn(value) - Calls real method FIRST, then stubs it
     * doReturn(value).when(spy).method() - Does NOT call real method (PREFERRED for spies)
     *
     * Use doReturn() for spies to avoid unwanted side effects.
     */
    @Test
    @DisplayName("doReturn() vs when() - Stubbing approaches for spies")
    void testDoReturnVsWhen() {
        // ARRANGE: Create spy
        User realUser = new User("testuser", "test@example.com", "Test", "User");
        User spyUser = spy(realUser);

        // APPROACH 1: when().thenReturn() - BAD for spies with side effects
        // This calls getEmail() first, then stubs it
        when(spyUser.getEmail()).thenReturn("when-stubbed@example.com");
        assertEquals("when-stubbed@example.com", spyUser.getEmail());

        // APPROACH 2: doReturn().when() - GOOD for spies (PREFERRED)
        // This does NOT call getEmail(), just stubs it directly
        doReturn("doreturn-stubbed@example.com").when(spyUser).getEmail();
        assertEquals("doreturn-stubbed@example.com", spyUser.getEmail());

        // WHY IT MATTERS: Consider a method with side effects
        CommissionPlan realPlan = new CommissionPlan("Test", Currency.getInstance("USD"));
        CommissionPlan spyPlan = spy(realPlan);

        // Using doReturn - safe, no side effects
        doReturn("Stubbed Name").when(spyPlan).getName();
        assertEquals("Stubbed Name", spyPlan.getName());
    }

    /**
     * SIDE EFFECTS DEMONSTRATION: Why doReturn() is safer for spies.
     */
    @Test
    @DisplayName("Side Effects - Why doReturn() is preferred")
    void testSideEffects() {
        // ARRANGE: Spy on a list (collections have side effects)
        List<String> realList = new ArrayList<>(Arrays.asList("one", "two", "three"));
        List<String> spyList = spy(realList);

        // Using doReturn() - safe, no side effects triggered
        doReturn(5).when(spyList).size();
        assertEquals(5, spyList.size());  // Stubbed

        // Real method still accessible for other operations
        spyList.add("four");

        // The stub is "sticky" - always returns 5 even after adding element
        assertEquals(5, spyList.size());  // Still returns stubbed value!

        // But we can verify the real add happened
        verify(spyList).add("four");
        verify(spyList, times(2)).size();
    }

    // ============================================
    // 6. VERIFICATION WITH SPIES
    // ============================================

    /**
     * VERIFICATION: You can verify both stubbed and real method calls on spies.
     */
    @Test
    @DisplayName("Verification - Verify method calls on spies")
    void testVerification() {
        // ARRANGE: Create spy
        User realUser = new User("verify", "verify@example.com", "Ver", "Ify");
        User spyUser = spy(realUser);

        // Stub one method
        doReturn("stubbed@example.com").when(spyUser).getEmail();

        // ACT: Call both stubbed and real methods
        String email = spyUser.getEmail();  // Stubbed
        String username = spyUser.getUsername();  // Real
        String fullName = spyUser.getFullName();  // Real

        // ASSERT: Verify all method calls (both stubbed and real)
        verify(spyUser).getEmail();
        verify(spyUser).getUsername();
        verify(spyUser).getFullName();

        // Can use verification modes
        verify(spyUser, times(1)).getEmail();
        verify(spyUser, never()).setActive(anyBoolean());
    }

    /**
     * ARGUMENT CAPTORS with Spies: Capture arguments passed to spy methods.
     */
    @Test
    @DisplayName("Argument Captors - Capture arguments on spies")
    void testArgumentCaptors() {
        // ARRANGE: Create spy
        Deal realDeal = new Deal("Test", new BigDecimal("1000"), "USER-1");
        Deal spyDeal = spy(realDeal);

        ArgumentCaptor<BigDecimal> valueCaptor = ArgumentCaptor.forClass(BigDecimal.class);

        // ACT: Call real method (not stubbed)
        spyDeal.setValue(new BigDecimal("50000"));
        spyDeal.setValue(new BigDecimal("75000"));

        // ASSERT: Capture and verify
        verify(spyDeal, times(2)).setValue(valueCaptor.capture());
        List<BigDecimal> capturedValues = valueCaptor.getAllValues();

        assertEquals(2, capturedValues.size());
        assertEquals(new BigDecimal("50000"), capturedValues.get(0));
        assertEquals(new BigDecimal("75000"), capturedValues.get(1));

        // The real setter worked - value is actually set
        assertEquals(new BigDecimal("75000"), spyDeal.getValue());
    }

    // ============================================
    // 7. SPY VS MOCK COMPARISON
    // ============================================

    /**
     * SPY vs MOCK: Side-by-side comparison demonstrating the differences.
     */
    @Test
    @DisplayName("Spy vs Mock - Side-by-side comparison")
    void testSpyVsMock() {
        // Create both spy and mock of the same class
        User realUser = new User("compare", "compare@example.com", "Com", "Pare");
        User spyUser = spy(realUser);
        User mockUser = mock(User.class);

        // DEFAULT BEHAVIOR
        // Spy: Calls real method
        assertEquals("Com Pare", spyUser.getFullName());  // Real method concatenated names

        // Mock: Returns null
        assertNull(mockUser.getFullName());  // No real implementation

        // STUBBING
        // Both can be stubbed
        doReturn("Stubbed Spy Name").when(spyUser).getFullName();
        when(mockUser.getFullName()).thenReturn("Stubbed Mock Name");
        assertEquals("Stubbed Spy Name", spyUser.getFullName());
        assertEquals("Stubbed Mock Name", mockUser.getFullName());

        // STATE CHANGES
        // Spy: Real state changes work
        spyUser.setActive(true);
        assertTrue(spyUser.isActive());  // Real setter worked

        // Mock: Need to stub return value
        mockUser.setActive(true);  // Call happens but does nothing
        assertFalse(mockUser.isActive());  // Returns default false (not stubbed)
        // PERFORMANCE
        // Spy: Slower (executes real logic)
        // Mock: Faster (no real logic execution)
    }

    // ============================================
    // 8. PARTIAL MOCKING USE CASES
    // ============================================

    /**
     * USE CASE 1: Testing protected/package methods by stubbing dependencies.
     * Spy allows testing real logic while controlling specific dependencies.
     */
    @Test
    @DisplayName("Use Case - Testing with controlled dependencies")
    void testControlledDependencies() {
        // ARRANGE: Spy on Deal to test calculation logic
        Deal realDeal = new Deal("Big Deal", new BigDecimal("100000"), "USER-1");
        Deal spyDeal = spy(realDeal);

        // Add products to the deal
        DealProduct product1 = new DealProduct("PROD-1", "Software License", 1, new BigDecimal("60000"));
        DealProduct product2 = new DealProduct("PROD-2", "Support Plan", 1, new BigDecimal("40000"));

        spyDeal.addProduct(product1);
        spyDeal.addProduct(product2);

        // ACT: Call real calculation method
        BigDecimal total = spyDeal.calculateTotalValue();

        // ASSERT: Real calculation worked
        assertEquals(0, total.compareTo(new BigDecimal("100000")));

        // Can verify the real method was called
        verify(spyDeal).calculateTotalValue();
        verify(spyDeal, times(2)).addProduct(any(DealProduct.class));
    }

    /**
     * USE CASE 2: Testing abstract classes or partial implementations.
     * Spies work well with classes that have some concrete and some abstract methods.
     */
    @Test
    @DisplayName("Use Case - Testing concrete methods in complex classes")
    void testConcreteMethodsInComplexClasses() {
        // ARRANGE: Spy on CommissionPlan with real implementation
        CommissionPlan realPlan = new CommissionPlan("Enterprise Plan", Currency.getInstance("USD"));
        CommissionPlan spyPlan = spy(realPlan);

        // Set date range and status (plan must be ACTIVE to return true from isActiveOn)
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now().plusMonths(11);

        spyPlan.setEffectiveStartDate(startDate);
        spyPlan.setEffectiveEndDate(endDate);
        spyPlan.setStatus(PlanStatus.ACTIVE);  // Must be ACTIVE!

        // ACT: Test real isActiveOn() method
        boolean activeToday = spyPlan.isActiveOn(LocalDate.now());
        boolean activePast = spyPlan.isActiveOn(LocalDate.now().minusMonths(2));
        boolean activeFuture = spyPlan.isActiveOn(LocalDate.now().plusMonths(6));  // Within 11 month range

        // ASSERT: Real date logic worked
        assertTrue(activeToday);    // Within range
        assertFalse(activePast);    // Before start
        assertTrue(activeFuture);   // Within range

        // Could stub specific edge cases
        doReturn(false).when(spyPlan).isActiveOn(LocalDate.now());
        assertFalse(spyPlan.isActiveOn(LocalDate.now()));  // Now stubbed
    }

    /**
     * USE CASE 3: Legacy code testing - spy on old code while stubbing problematic parts.
     */
    @Test
    @DisplayName("Use Case - Legacy code with problematic dependencies")
    void testLegacyCode() {
        // ARRANGE: Spy on real object
        User legacyUser = new User("legacy", "legacy@old.com", "Old", "Code");
        User spyLegacyUser = spy(legacyUser);

        // Stub problematic methods that might hit database or external services
        // (In real legacy code, these might be hard to test)
        doReturn(true).when(spyLegacyUser).hasRole(any(UserRole.class));

        // ACT: Test the real business logic
        String fullName = spyLegacyUser.getFullName();

        // Real getFullName() works
        assertEquals("Old Code", fullName);

        // Stubbed hasRole() returns our controlled value
        assertTrue(spyLegacyUser.hasRole(UserRole.SALES_MANAGER));
        assertTrue(spyLegacyUser.hasRole(UserRole.SYSTEM_ADMIN));

        // Verify interactions
        verify(spyLegacyUser).getFullName();
        verify(spyLegacyUser, times(2)).hasRole(any(UserRole.class));
    }

    // ============================================
    // 9. SPY WITH COLLECTIONS
    // ============================================

    /**
     * SPYING ON COLLECTIONS: Useful for testing collection behavior.
     * Be careful with side effects!
     */
    @Test
    @DisplayName("Spy on Collections - Lists, Sets, Maps")
    void testSpyOnCollections() {
        // SPY ON LIST
        List<String> realList = new ArrayList<>(Arrays.asList("A", "B", "C"));
        List<String> spyList = spy(realList);

        // Real methods work
        assertEquals(3, spyList.size());
        assertTrue(spyList.contains("B"));

        // Can stub specific operations
        doReturn(true).when(spyList).isEmpty();
        assertTrue(spyList.isEmpty());  // Stubbed
        assertEquals(3, spyList.size());  // Real method still returns 3

        // Real modifications work
        spyList.add("D");
        verify(spyList).add("D");

        // SPY ON MAP
        Map<String, Integer> realMap = new HashMap<>();
        realMap.put("one", 1);
        realMap.put("two", 2);
        Map<String, Integer> spyMap = spy(realMap);

        // Real methods work
        assertEquals(2, spyMap.size());
        assertEquals(1, spyMap.get("one"));

        // Can stub specific keys
        doReturn(999).when(spyMap).get("one");
        assertEquals(999, spyMap.get("one"));  // Stubbed
        assertEquals(2, spyMap.get("two"));     // Real
    }

    // ============================================
    // 10. SPY LIMITATIONS
    // ============================================

    /**
     * LIMITATIONS: What you CANNOT do with spies.
     */
    @Test
    @DisplayName("Spy Limitations - Understanding constraints")
    void testSpyLimitations() {
        // LIMITATION 1: Cannot spy on final classes (without mockito-inline)
        // String is final, so this would fail:
        // String spyString = spy("test");  // ERROR!

        // LIMITATION 2: Cannot spy on null objects
        // User nullUser = null;
        // User spyNull = spy(nullUser);  // NullPointerException!

        // LIMITATION 3: Must use doReturn() for methods with side effects
        User realUser = new User("limit", "limit@test.com", "Lim", "It");
        User spyUser = spy(realUser);
        // This is safe because getUsername() has no side effects
        when(spyUser.getUsername()).thenReturn("stubbed");
        assertEquals("stubbed", spyUser.getUsername());
        // But for methods with side effects, use doReturn()
        doReturn("safe-stubbed").when(spyUser).getUsername();
        assertEquals("safe-stubbed", spyUser.getUsername());

        // LIMITATION 4: Spies share state with real object
        User sharedUser = new User("shared", "shared@test.com", "Shared", "User");
        User spyShared = spy(sharedUser);

        spyShared.setActive(true);
        assertTrue(sharedUser.isActive());  // Real object modified too!

        // LIMITATION 5: Cannot spy on primitives or interfaces without implementation
        // int spyInt = spy(5);  // ERROR - primitives can't be spied
    }

    // ============================================
    // 11. ADVANCED: CHAINING AND INORDER
    // ============================================

    /**
     * ADVANCED: InOrder verification with spies.
     */
    @Test
    @DisplayName("Advanced - InOrder verification with spies")
    void testInOrderWithSpies() {
        // ARRANGE: Create spy
        Deal realDeal = new Deal("Workflow", new BigDecimal("1000"), "USER-1");
        Deal spyDeal = spy(realDeal);

        // ACT: Simulate workflow
        spyDeal.setStatus(DealStatus.OPEN);
        spyDeal.setValue(new BigDecimal("5000"));
        spyDeal.setStatus(DealStatus.WON);

        // ASSERT: Verify order
        InOrder inOrder = inOrder(spyDeal);
        inOrder.verify(spyDeal).setStatus(DealStatus.OPEN);
        inOrder.verify(spyDeal).setValue(new BigDecimal("5000"));
        inOrder.verify(spyDeal).setStatus(DealStatus.WON);

        // Real methods executed and state changed
        assertEquals(DealStatus.WON, spyDeal.getStatus());
        assertEquals(new BigDecimal("5000"), spyDeal.getValue());
    }

    // ============================================
    // 12. REAL-WORLD SCENARIO
    // ============================================

    /**
     * REAL-WORLD SCENARIO: Commission calculation with partial mocking.
     * Tests real business logic while controlling external dependencies.
     */
    @Test
    @DisplayName("Real-World Scenario - Commission calculation with spies")
    void testRealWorldScenario() {
        // ARRANGE: Set up real objects
        Deal realDeal = new Deal("Enterprise Sale", new BigDecimal("250000"), "USER-100");
        Deal spyDeal = spy(realDeal);

        User realUser = new User("sjohnson", "sarah@example.com", "Sarah", "Johnson");
        User spyUser = spy(realUser);

        CommissionPlan realPlan = new CommissionPlan("2024 Enterprise Plan", Currency.getInstance("USD"));
        CommissionPlan spyPlan = spy(realPlan);

        // Configure real objects
        spyDeal.setStatus(DealStatus.WON);
        spyDeal.setCloseDate(LocalDate.now());

        spyUser.setActive(true);
        spyUser.addRole(UserRole.SALES_REP);

        spyPlan.setStatus(PlanStatus.ACTIVE);
        spyPlan.setEffectiveStartDate(LocalDate.now().minusMonths(6));
        spyPlan.setEffectiveEndDate(LocalDate.now().plusMonths(6));

        // Stub only a hypothetical external rate lookup (simulating complex logic)
        // Note: CommissionPlan doesn't have getCommissionRate(), so we'll use a different approach
        // We'll use a fixed rate for this example
        BigDecimal rate = new BigDecimal("0.12");

        // ACT: Calculate commission using real business logic
        BigDecimal dealValue = spyDeal.getValue();  // Real method
        boolean isPlanActive = spyPlan.isActiveOn(LocalDate.now());  // Real method
        boolean isUserActive = spyUser.isActive();  // Real method

        BigDecimal commission = dealValue.multiply(rate);

        // ASSERT: Verify complex workflow
        assertTrue(isPlanActive);
        assertTrue(isUserActive);
        assertEquals(0, commission.compareTo(new BigDecimal("30000.00")));

        // Verify real methods were called
        verify(spyDeal).getValue();
        verify(spyPlan).isActiveOn(LocalDate.now());
        verify(spyUser).isActive();

        // InOrder verification for workflow
        InOrder inOrder = inOrder(spyDeal, spyUser, spyPlan);
        inOrder.verify(spyDeal).getValue();
        inOrder.verify(spyPlan).isActiveOn(any(LocalDate.class));
        inOrder.verify(spyUser).isActive();
    }

    // ============================================
    // 13. SPY RESET
    // ============================================

    /**
     * SPY RESET: Clearing stubbing but keeping the real object.
     */
    @Test
    @DisplayName("Spy Reset - Clear stubbing")
    void testSpyReset() {
        // ARRANGE: Create and stub spy
        User realUser = new User("reset", "reset@test.com", "Re", "Set");
        User spyUser = spy(realUser);

        doReturn("stubbed@test.com").when(spyUser).getEmail();
        assertEquals("stubbed@test.com", spyUser.getEmail());

        // ACT: Reset the spy
        Mockito.reset(spyUser);

        // ASSERT: Stubbing cleared, back to real behavior
        assertEquals("reset@test.com", spyUser.getEmail());  // Real method again

        // Real object state preserved
        assertEquals("reset", spyUser.getUsername());
        assertEquals("Re Set", spyUser.getFullName());
    }

    // ============================================
    // 14. BEST PRACTICES
    // ============================================

    /**
     * BEST PRACTICES: Guidelines for effective spy usage.
     */
    @Test
    @DisplayName("Best Practices - Effective spy usage")
    void testBestPractices() {
        // PRACTICE 1: Use doReturn() instead of when() for spies
        User user = spy(new User("bp", "bp@test.com", "Best", "Practice"));
        doReturn("safe-stub").when(user).getEmail();  // ✓ GOOD
        // when(user.getEmail()).thenReturn("risky");  // ✗ AVOID

        // PRACTICE 2: Spy on concrete classes, not interfaces
        Deal deal = spy(new Deal("Test", new BigDecimal("1000"), "USER-1"));  // ✓ GOOD
        // Cannot spy on interface without implementation

        // PRACTICE 3: Don't over-use spies - prefer mocks for pure unit tests
        // Use spies when you need REAL behavior with selective overrides

        // PRACTICE 4: Be aware of side effects
        List<String> list = spy(new ArrayList<>());
        doReturn(5).when(list).size();  // ✓ GOOD - use doReturn()
        assertEquals(5, list.size());

        // PRACTICE 5: Verify both stubbed and real methods as needed
        deal.setTitle("Modified");
        verify(deal).setTitle("Modified");
        assertEquals("Modified", deal.getTitle());  // Real setter worked

        // PRACTICE 6: Use spies sparingly in production code
        // Consider if your design could be improved instead
        // Spies are great for legacy code but may indicate design issues
    }
}