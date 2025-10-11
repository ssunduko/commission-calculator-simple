package com.chapman.edu.commissions.doubles.mock;

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
 * Comprehensive demonstration of Mockito mocking capabilities.
 *
 * MOCKITO OVERVIEW:
 * Mockito is a mocking framework that allows you to create mock objects for testing.
 * Mock objects simulate the behavior of real objects in controlled ways, enabling
 * isolated unit testing without dependencies on external systems or complex setup.
 *
 * KEY CONCEPTS DEMONSTRATED:
 * 1. Basic Mock Creation - Creating mock objects using @Mock annotation and mock() method
 * 2. Stubbing - Defining return values for method calls on mocks
 * 3. Verification - Verifying that methods were called with expected arguments
 * 4. Argument Matchers - Using flexible argument matching in stubbing and verification
 * 5. Argument Captors - Capturing arguments passed to methods for detailed assertions
 * 6. Spy Objects - Partial mocking of real objects
 * 7. Mock Behavior - doThrow, doNothing, doReturn, doAnswer
 * 8. Verification Modes - times(), never(), atLeast(), atMost()
 * 9. InOrder Verification - Verifying method call order
 * 10. BDD Style - Behavior-Driven Development style testing with Mockito
 *
 * @see <a href="https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html">Mockito Documentation</a>
 */
@DisplayName("Mockito Mocking Concepts")
class MockitoMockTest {

    // ============================================
    // 1. BASIC MOCK CREATION
    // ============================================

    /**
     * @Mock annotation automatically creates mock objects.
     * Must be initialized with MockitoAnnotations.openMocks(this) or @ExtendWith(MockitoExtension.class)
     */
    @Mock
    private Deal mockDeal;

    @Mock
    private User mockUser;

    @Mock
    private CommissionPlan mockCommissionPlan;

    /**
     * AutoCloseable resource for managing Mockito mocks lifecycle.
     * Ensures proper cleanup after each test.
     */
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        // Initialize mocks annotated with @Mock
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        // Close the AutoCloseable to release resources
        closeable.close();
    }

    // ============================================
    // 2. BASIC STUBBING
    // ============================================

    /**
     * STUBBING: Defining what a mock should return when methods are called.
     * Syntax: when(mock.method()).thenReturn(value)
     *
     * Use Case: Control the behavior of dependencies to test specific scenarios.
     */
    @Test
    @DisplayName("Basic Stubbing - Define return values for mock methods")
    void testBasicStubbing() {
        // ARRANGE: Define what the mock should return when methods are called
        when(mockDeal.getId()).thenReturn("DEAL-001");
        when(mockDeal.getTitle()).thenReturn("Enterprise Software License");
        when(mockDeal.getValue()).thenReturn(new BigDecimal("100000.00"));
        when(mockDeal.getStatus()).thenReturn(DealStatus.WON);

        // ACT: Call the methods on the mock
        String id = mockDeal.getId();
        String title = mockDeal.getTitle();
        BigDecimal value = mockDeal.getValue();
        DealStatus status = mockDeal.getStatus();

        // ASSERT: Verify the stubbed values are returned
        assertEquals("DEAL-001", id);
        assertEquals("Enterprise Software License", title);
        assertEquals(new BigDecimal("100000.00"), value);
        assertEquals(DealStatus.WON, status);
    }

    // ============================================
    // 3. VERIFICATION
    // ============================================

    /**
     * VERIFICATION: Checking that specific methods were called on mocks.
     * Syntax: verify(mock).method()
     *
     * Use Case: Ensure that your code interacts with dependencies correctly.
     */
    @Test
    @DisplayName("Verification - Ensure methods were called")
    void testMethodVerification() {
        // ACT: Interact with the mock
        mockDeal.setTitle("Cloud Migration Project");
        mockDeal.setValue(new BigDecimal("250000.00"));
        mockDeal.setStatus(DealStatus.OPEN);

        // ASSERT: Verify the methods were called
        verify(mockDeal).setTitle("Cloud Migration Project");
        verify(mockDeal).setValue(new BigDecimal("250000.00"));
        verify(mockDeal).setStatus(DealStatus.OPEN);
    }

    /**
     * VERIFICATION WITH TIMES: Verify exact number of method calls.
     * Syntax: verify(mock, times(n)).method()
     */
    @Test
    @DisplayName("Verification with times() - Verify exact call count")
    void testVerificationWithTimes() {
        // ACT: Call methods multiple times
        mockUser.getFullName();
        mockUser.getFullName();
        mockUser.getFullName();

        // ASSERT: Verify the method was called exactly 3 times
        verify(mockUser, times(3)).getFullName();
    }

    /**
     * VERIFICATION WITH NEVER: Ensure a method was never called.
     * Syntax: verify(mock, never()).method()
     */
    @Test
    @DisplayName("Verification with never() - Ensure method was not called")
    void testVerificationNever() {
        // ACT: Call some methods but not others
        mockUser.getEmail();

        // ASSERT: Verify specific method was never called
        verify(mockUser, never()).setPasswordHash(anyString());
        verify(mockUser, never()).setActive(false);
    }

    /**
     * VERIFICATION WITH AT LEAST/AT MOST: Flexible verification.
     */
    @Test
    @DisplayName("Verification with atLeast() and atMost() - Flexible call count")
    void testVerificationAtLeastAtMost() {
        // ACT: Call methods multiple times
        mockCommissionPlan.getName();
        mockCommissionPlan.getName();
        mockCommissionPlan.getName();
        mockCommissionPlan.getName();

        // ASSERT: Verify with flexible boundaries
        verify(mockCommissionPlan, atLeast(2)).getName();
        verify(mockCommissionPlan, atMost(5)).getName();
    }

    // ============================================
    // 4. ARGUMENT MATCHERS
    // ============================================

    /**
     * ARGUMENT MATCHERS: Use flexible argument matching in stubbing/verification.
     * Common matchers: any(), anyString(), anyInt(), eq(), isNull(), isNotNull()
     *
     * Use Case: When you don't care about exact argument values or want to match patterns.
     */
    @Test
    @DisplayName("Argument Matchers - Flexible argument matching")
    void testArgumentMatchers() {
        // ARRANGE: Stub with argument matchers
        when(mockUser.hasRole(any(UserRole.class))).thenReturn(true);

        // ACT & ASSERT: Any UserRole argument will match
        assertTrue(mockUser.hasRole(UserRole.SALES_REP));
        assertTrue(mockUser.hasRole(UserRole.SALES_MANAGER));
        assertTrue(mockUser.hasRole(UserRole.FINANCE_ADMIN));

        // Verify with matchers
        verify(mockUser, times(3)).hasRole(any(UserRole.class));
    }

    /**
     * MIXING MATCHERS AND EXACT VALUES:
     * Important: If you use matchers, ALL arguments must use matchers.
     * Use eq() to specify exact values when mixing with matchers.
     */
    @Test
    @DisplayName("Mixing Matchers - Using eq() with other matchers")
    void testMixingMatchers() {
        // Create a mock list for demonstration
        @SuppressWarnings("unchecked")
        List<Deal> mockDealList = mock(List.class);
        Deal deal = new Deal();

        // Stub: Add any Deal - List.add(E) returns boolean
        when(mockDealList.add(any(Deal.class))).thenReturn(true);

        // ACT
        boolean added = mockDealList.add(deal);

        // ASSERT
        assertTrue(added);
        verify(mockDealList).add(any(Deal.class));
    }

    // ============================================
    // 5. ARGUMENT CAPTORS
    // ============================================

    /**
     * ARGUMENT CAPTORS: Capture arguments passed to methods for detailed assertions.
     * Syntax: @Captor annotation or ArgumentCaptor.forClass()
     *
     * Use Case: When you need to verify the exact state or values of complex arguments.
     */
    @Test
    @DisplayName("Argument Captors - Capture and inspect arguments")
    void testArgumentCaptors() {
        // ARRANGE: Create argument captor
        ArgumentCaptor<BigDecimal> valueCaptor = ArgumentCaptor.forClass(BigDecimal.class);

        // ACT: Call method with argument
        mockDeal.setValue(new BigDecimal("75000.00"));

        // ASSERT: Capture and verify the argument
        verify(mockDeal).setValue(valueCaptor.capture());
        BigDecimal capturedValue = valueCaptor.getValue();
        assertEquals(new BigDecimal("75000.00"), capturedValue);
        assertTrue(capturedValue.compareTo(new BigDecimal("50000.00")) > 0);
    }

    /**
     * CAPTURING MULTIPLE ARGUMENTS:
     * Use getAllValues() when a method is called multiple times.
     */
    @Test
    @DisplayName("Argument Captors - Capture multiple invocations")
    void testArgumentCaptorsMultipleInvocations() {
        // ARRANGE
        ArgumentCaptor<DealStatus> statusCaptor = ArgumentCaptor.forClass(DealStatus.class);

        // ACT: Call method multiple times
        mockDeal.setStatus(DealStatus.OPEN);
        mockDeal.setStatus(DealStatus.WON);
        mockDeal.setStatus(DealStatus.LOST);

        // ASSERT: Capture all invocations
        verify(mockDeal, times(3)).setStatus(statusCaptor.capture());
        List<DealStatus> allStatuses = statusCaptor.getAllValues();

        assertEquals(3, allStatuses.size());
        assertEquals(DealStatus.OPEN, allStatuses.get(0));
        assertEquals(DealStatus.WON, allStatuses.get(1));
        assertEquals(DealStatus.LOST, allStatuses.get(2));
    }

    // ============================================
    // 6. SPY OBJECTS
    // ============================================

    /**
     * SPY: Partial mocking - wraps a real object and allows selective stubbing.
     * By default, spy calls real methods unless explicitly stubbed.
     *
     * Use Case: When you want to test a real object but override specific methods.
     */
    @Test
    @DisplayName("Spy Objects - Partial mocking of real objects")
    void testSpyObjects() {
        // ARRANGE: Create a spy from a real object
        User realUser = new User("jsmith", "john.smith@example.com", "John", "Smith");
        User spyUser = spy(realUser);

        // By default, spy calls real methods
        assertEquals("John Smith", spyUser.getFullName());

        // We can stub specific methods
        when(spyUser.getEmail()).thenReturn("john.smith.spy@example.com");

        // ACT & ASSERT: Stubbed method returns mocked value
        assertEquals("john.smith.spy@example.com", spyUser.getEmail());

        // Real method is still called for non-stubbed methods
        assertEquals("jsmith", spyUser.getUsername());
        assertEquals("John Smith", spyUser.getFullName());
    }

    /**
     * SPY WITH VOID METHODS:
     * Use doNothing(), doThrow() for void methods on spies.
     */
    @Test
    @DisplayName("Spy Objects - Stubbing void methods")
    void testSpyVoidMethods() {
        // ARRANGE: Create spy
        CommissionPlan realPlan = new CommissionPlan("Standard Plan", Currency.getInstance("USD"));
        CommissionPlan spyPlan = spy(realPlan);

        // Stub void method to do nothing (prevent real behavior)
        doNothing().when(spyPlan).setStatus(any(PlanStatus.class));

        // ACT: Call void method
        spyPlan.setStatus(PlanStatus.ACTIVE);

        // ASSERT: Verify called but real behavior was bypassed
        verify(spyPlan).setStatus(PlanStatus.ACTIVE);
        // Status remains DRAFT because we stubbed setStatus to do nothing
        assertEquals(PlanStatus.DRAFT, spyPlan.getStatus());
    }

    // ============================================
    // 7. MOCK BEHAVIOR - EXCEPTIONS
    // ============================================

    /**
     * THROWING EXCEPTIONS: Mock methods to throw exceptions.
     * Syntax: when(mock.method()).thenThrow(exception)
     *         or doThrow(exception).when(mock).voidMethod()
     *
     * Use Case: Test error handling and exception scenarios.
     */
    @Test
    @DisplayName("Throwing Exceptions - Test exception handling")
    void testThrowingExceptions() {
        // ARRANGE: Stub method to throw exception
        when(mockDeal.calculateTotalValue())
            .thenThrow(new IllegalStateException("Products list is corrupted"));

        // ACT & ASSERT: Verify exception is thrown
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> mockDeal.calculateTotalValue()
        );

        assertEquals("Products list is corrupted", exception.getMessage());
    }

    /**
     * THROWING EXCEPTIONS ON VOID METHODS:
     * Use doThrow() for void methods.
     */
    @Test
    @DisplayName("Throwing Exceptions on Void Methods")
    void testThrowingExceptionsVoidMethod() {
        // ARRANGE: Stub void method to throw exception
        doThrow(new IllegalArgumentException("Invalid status transition"))
            .when(mockDeal).setStatus(DealStatus.WON);

        // ACT & ASSERT: Verify exception is thrown
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> mockDeal.setStatus(DealStatus.WON)
        );

        assertEquals("Invalid status transition", exception.getMessage());
    }

    // ============================================
    // 8. MOCK BEHAVIOR - ANSWERS
    // ============================================

    /**
     * CUSTOM ANSWERS: Implement custom logic for method calls.
     * Syntax: when(mock.method()).thenAnswer(invocation -> { ... })
     *
     * Use Case: When you need complex, dynamic behavior based on arguments.
     */
    @Test
    @DisplayName("Custom Answers - Dynamic behavior based on arguments")
    void testCustomAnswers() {
        // ARRANGE: Create a mock with custom answer
        Deal dealMock = mock(Deal.class);

        // Custom answer that calculates based on input
        when(dealMock.calculateTotalValue()).thenAnswer(invocation -> {
            // Simulate complex calculation
            return new BigDecimal("50000.00");
        });

        // ACT
        BigDecimal result = dealMock.calculateTotalValue();

        // ASSERT
        assertEquals(new BigDecimal("50000.00"), result);
    }

    /**
     * ANSWER WITH ARGUMENTS:
     * Access method arguments in custom answers.
     */
    @Test
    @DisplayName("Custom Answers - Using method arguments")
    void testCustomAnswersWithArguments() {
        // ARRANGE: Mock with answer that uses arguments
        User userMock = mock(User.class);

        when(userMock.hasRole(any(UserRole.class))).thenAnswer(invocation -> {
            UserRole role = invocation.getArgument(0);
            // Custom logic based on argument
            return role == UserRole.SALES_REP || role == UserRole.SALES_MANAGER;
        });

        // ACT & ASSERT
        assertTrue(userMock.hasRole(UserRole.SALES_REP));
        assertTrue(userMock.hasRole(UserRole.SALES_MANAGER));
        assertFalse(userMock.hasRole(UserRole.SYSTEM_ADMIN));
    }

    // ============================================
    // 9. INORDER VERIFICATION
    // ============================================

    /**
     * INORDER VERIFICATION: Verify methods were called in specific order.
     * Syntax: InOrder inOrder = inOrder(mock); inOrder.verify(mock).method1(); inOrder.verify(mock).method2();
     *
     * Use Case: When the order of operations matters (e.g., state machines, workflows).
     */
    @Test
    @DisplayName("InOrder Verification - Verify method call sequence")
    void testInOrderVerification() {
        // ARRANGE
        Deal dealMock = mock(Deal.class);

        // ACT: Simulate a deal lifecycle
        dealMock.setStatus(DealStatus.OPEN);
        dealMock.setStatus(DealStatus.WON);
        dealMock.setStatus(DealStatus.LOST);
        dealMock.setStatus(DealStatus.CANCELLED);

        // ASSERT: Verify the order of status changes
        InOrder inOrder = inOrder(dealMock);
        inOrder.verify(dealMock).setStatus(DealStatus.OPEN);
        inOrder.verify(dealMock).setStatus(DealStatus.WON);
        inOrder.verify(dealMock).setStatus(DealStatus.LOST);
        inOrder.verify(dealMock).setStatus(DealStatus.CANCELLED);
    }

    /**
     * INORDER VERIFICATION WITH MULTIPLE MOCKS:
     * Verify order across multiple mock objects.
     */
    @Test
    @DisplayName("InOrder Verification - Multiple mocks")
    void testInOrderMultipleMocks() {
        // ARRANGE
        Deal dealMock = mock(Deal.class);
        User userMock = mock(User.class);
        CommissionPlan planMock = mock(CommissionPlan.class);

        // ACT: Simulate a workflow across multiple objects
        dealMock.setStatus(DealStatus.OPEN);
        userMock.setLastLogin(LocalDateTime.now());
        planMock.setStatus(PlanStatus.ACTIVE);
        dealMock.setStatus(DealStatus.WON);

        // ASSERT: Verify the order across multiple mocks
        InOrder inOrder = inOrder(dealMock, userMock, planMock);
        inOrder.verify(dealMock).setStatus(DealStatus.OPEN);
        inOrder.verify(userMock).setLastLogin(any(LocalDateTime.class));
        inOrder.verify(planMock).setStatus(PlanStatus.ACTIVE);
        inOrder.verify(dealMock).setStatus(DealStatus.WON);
    }

    // ============================================
    // 10. BDD STYLE (BEHAVIOR-DRIVEN DEVELOPMENT)
    // ============================================

    /**
     * BDD STYLE: Use given-when-then naming for better readability.
     * Syntax: given(mock.method()).willReturn(value)
     *         then(mock).should().method()
     *
     * Use Case: Makes tests read more like specifications.
     */
    @Test
    @DisplayName("BDD Style - Behavior-Driven Development syntax")
    void testBDDStyle() {
        // GIVEN (arrange): Set up the test scenario
        BDDMockito.given(mockUser.getFullName()).willReturn("Jane Doe");
        BDDMockito.given(mockUser.isActive()).willReturn(true);
        BDDMockito.given(mockUser.hasRole(UserRole.SALES_REP)).willReturn(true);

        // WHEN (act): Execute the behavior being tested
        String fullName = mockUser.getFullName();
        boolean isActive = mockUser.isActive();
        boolean isSalesRep = mockUser.hasRole(UserRole.SALES_REP);

        // THEN (assert): Verify the expected outcomes
        BDDMockito.then(mockUser).should().getFullName();
        BDDMockito.then(mockUser).should().isActive();
        BDDMockito.then(mockUser).should().hasRole(UserRole.SALES_REP);

        assertEquals("Jane Doe", fullName);
        assertTrue(isActive);
        assertTrue(isSalesRep);
    }

    // ============================================
    // 11. COMPLEX SCENARIO - INTEGRATION
    // ============================================

    /**
     * COMPLEX SCENARIO: Combining multiple Mockito features.
     * Demonstrates real-world testing scenario using commission calculation.
     */
    @Test
    @DisplayName("Complex Scenario - Commission calculation with multiple mocks")
    void testComplexScenario() {
        // ARRANGE: Set up mocks for a commission calculation scenario
        Deal deal = mock(Deal.class);
        User salesRep = mock(User.class);
        CommissionPlan plan = mock(CommissionPlan.class);

        // Stub deal behavior
        when(deal.getId()).thenReturn("DEAL-5000");
        when(deal.getValue()).thenReturn(new BigDecimal("150000.00"));
        when(deal.getStatus()).thenReturn(DealStatus.WON);
        when(deal.getSalesRepId()).thenReturn("USER-100");
        when(deal.getCloseDate()).thenReturn(LocalDate.now());

        // Stub user behavior
        when(salesRep.getId()).thenReturn("USER-100");
        when(salesRep.getFullName()).thenReturn("Sarah Johnson");
        when(salesRep.hasRole(UserRole.SALES_REP)).thenReturn(true);
        when(salesRep.isActive()).thenReturn(true);

        // Stub commission plan behavior
        when(plan.getId()).thenReturn("PLAN-001");
        when(plan.getName()).thenReturn("Enterprise Sales Plan");
        when(plan.isActiveOn(any(LocalDate.class))).thenReturn(true);
        when(plan.getStatus()).thenReturn(PlanStatus.ACTIVE);

        // ACT: Simulate commission calculation workflow
        if (deal.getStatus() == DealStatus.WON && salesRep.isActive()) {
            String repId = deal.getSalesRepId();
            LocalDate closeDate = deal.getCloseDate();
            boolean planActive = plan.isActiveOn(closeDate);

            if (planActive && repId.equals(salesRep.getId())) {
                // Commission would be calculated here
                BigDecimal dealValue = deal.getValue();
                BigDecimal commissionRate = new BigDecimal("0.10");
                BigDecimal commission = dealValue.multiply(commissionRate);

                // ASSERT: Verify complex interactions (use compareTo for BigDecimal)
                assertEquals(0, commission.compareTo(new BigDecimal("15000.00")),
                    "Commission should be 15000.00");
            }
        }

        // ASSERT: Verify all interactions occurred
        verify(deal, atLeastOnce()).getStatus();
        verify(salesRep, atLeastOnce()).isActive();
        verify(deal).getSalesRepId();
        verify(deal).getCloseDate();
        verify(plan).isActiveOn(any(LocalDate.class));
        verify(salesRep).getId();
        verify(deal, atLeastOnce()).getValue();

        // Verify order of critical operations
        InOrder inOrder = inOrder(deal, salesRep, plan);
        inOrder.verify(deal).getStatus();
        inOrder.verify(salesRep).isActive();
        inOrder.verify(plan).isActiveOn(any(LocalDate.class));
    }

    // ============================================
    // 12. MOCK RESET
    // ============================================

    /**
     * MOCK RESET: Clear all interactions and stubbing on a mock.
     * Syntax: Mockito.reset(mock)
     *
     * Use Case: Rarely needed, but useful when reusing mocks across tests.
     */
    @Test
    @DisplayName("Mock Reset - Clear mock state")
    void testMockReset() {
        // ARRANGE: Stub and interact with mock
        when(mockDeal.getId()).thenReturn("DEAL-001");
        String id = mockDeal.getId();
        assertEquals("DEAL-001", id);

        // ACT: Reset the mock
        Mockito.reset(mockDeal);

        // ASSERT: Mock is clean - no interactions recorded
        // Previous stubbing is cleared, returns null now
        assertNull(mockDeal.getId()); // Returns null (default for reference types)

        // After reset, only the call AFTER reset is tracked
        verify(mockDeal, times(1)).getId();
    }

    // ============================================
    // 13. DEFAULT RETURN VALUES
    // ============================================

    /**
     * DEFAULT RETURN VALUES: Understanding what mocks return by default.
     * - Reference types: null
     * - Primitives: 0, false, etc.
     * - Collections: null (by default in Mockito)
     *
     * NOTE: Mockito behavior can vary based on configuration. Some versions
     * return empty collections by default.
     */
    @Test
    @DisplayName("Default Return Values - Understanding mock defaults")
    void testDefaultReturnValues() {
        // ACT: Call methods without stubbing
        String id = mockDeal.getId();
        BigDecimal value = mockDeal.getValue();
        DealStatus status = mockDeal.getStatus();
        List<DealProduct> products = mockDeal.getProducts();

        // ASSERT: Check default return values
        assertNull(id, "Unstubbed reference types return null");
        assertNull(value, "Unstubbed BigDecimal returns null");
        assertNull(status, "Unstubbed enum returns null");
        // Note: In some Mockito configurations, collections return empty collections
        // instead of null - this is configurable behavior
        assertNotNull(products, "Collections may return empty list by default");
        assertTrue(products.isEmpty(), "Default collection should be empty");
    }

    // ============================================
    // 14. VERIFICATION TIMEOUT
    // ============================================

    /**
     * VERIFICATION WITH TIMEOUT: Useful for asynchronous code.
     * Syntax: verify(mock, timeout(millis)).method()
     *
     * Use Case: Testing asynchronous or multi-threaded code.
     */
    @Test
    @DisplayName("Verification Timeout - For asynchronous testing")
    void testVerificationTimeout() throws InterruptedException {
        // ARRANGE: Create a mock
        Deal asyncDeal = mock(Deal.class);

        // ACT: Simulate async call in separate thread
        new Thread(() -> {
            try {
                Thread.sleep(100); // Simulate delay
                asyncDeal.setStatus(DealStatus.WON);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        // ASSERT: Verify with timeout (waits up to 500ms for the call)
        verify(asyncDeal, timeout(500)).setStatus(DealStatus.WON);
    }
}