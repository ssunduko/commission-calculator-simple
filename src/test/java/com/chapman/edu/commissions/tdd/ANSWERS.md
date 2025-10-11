# TDD (Test-Driven Development) - Answers

## Fundamental Concepts

### Answer 1: What is TDD?

**Test-Driven Development (TDD)** is a software development methodology where you write tests **before** writing the implementation code.

**Traditional Approach:**
```
1. Write implementation code
2. Write tests
3. Debug
```

**TDD Approach:**
```
1. Write test (defines requirement)
2. Watch it fail (RED)
3. Write minimal implementation (GREEN)
4. Refactor (improve quality)
5. Repeat
```

**Key Differences:**
- Tests drive the design, not verify it after the fact
- Tests are written with the developer's full attention (not as an afterthought)
- Implementation is guided by clear, testable requirements
- Every line of code has a test because tests came first

### Answer 2: The TDD Cycle

The three phases of TDD:

**RED Phase - Write a Failing Test**
- Purpose: Define what you want to build
- Action: Write a test for new functionality
- Result: Test fails (because implementation doesn't exist)
- Why: Proves the test works and can detect problems

**GREEN Phase - Make It Pass**
- Purpose: Implement the feature
- Action: Write minimal code to make the test pass
- Result: Test passes
- Why: Focus on making it work, not making it perfect

**REFACTOR Phase - Improve the Code**
- Purpose: Enhance code quality without changing behavior
- Action: Clean up, remove duplication, improve naming
- Result: Tests still pass
- Why: Improve maintainability with tests as safety net

The cycle repeats for each new feature or requirement.

### Answer 3: Why Test First?

Writing tests first provides several critical benefits:

**1. Tests Drive Design**
- Forces you to think about how code will be used before writing it
- Leads to better interfaces and APIs
- Ensures code is testable (you can't test it if you can't design a test)

**2. Clear Requirements**
- Test explicitly states what the code should do
- No ambiguity about expected behavior
- Serves as executable specification

**3. Prevents Over-Engineering**
- You only write code that has a test
- No speculative features "just in case"
- YAGNI (You Ain't Gonna Need It) principle enforced

**4. Complete Coverage**
- All code has tests because tests came first
- No untested "gaps" in functionality

**5. Immediate Feedback**
- Know instantly when implementation is complete
- Test passing means requirement is met

**Example from TDD.java:**
The test for `calculateBasicCommission()` defined the API before the method existed, ensuring a clean, usable interface.

### Answer 4: What Does "Failing Test" Prove?

Watching a test fail in the RED phase is critical because it proves:

**1. The Test Actually Runs**
- A test that never fails might not be running at all
- Verifies the test framework is configured correctly

**2. The Test Can Detect Problems**
- If it passes before implementation, something is wrong
- The test might be trivial or incorrectly written

**3. The Feature Doesn't Exist Yet**
- Confirms you're about to add new functionality
- Prevents duplicate implementation

**4. The Test is Valid**
- A test that passes without implementation is a false positive
- False positives are worse than useless—they give false confidence

**If a test passes before implementation:**
- The test might be testing the wrong thing
- The feature might already exist
- The test might have no assertions
- The test setup might be creating the expected result

**Example:**
```java
@Test
void testNewFeature() {
    // If this passes before implementation, investigate!
    assertEquals(expected, service.newFeature());
}
```

### Answer 5: Minimal Implementation

Writing minimal code in the GREEN phase is important for several reasons:

**1. Focus on Making It Work**
- Don't worry about perfection yet
- Get to green as quickly as possible
- Refactoring comes next

**2. Prevents Over-Engineering**
- Don't add features not covered by tests
- Keep scope focused on current requirement
- YAGNI principle

**3. Faster Feedback**
- Quicker to get to passing test
- Shorter RED-GREEN cycle
- More frequent validation

**4. Refactoring Opportunity**
- Deliberately leave room for improvement
- Refactor phase improves the simple code
- Tests protect during refactoring

**5. Learning and Discovery**
- Simple implementation might reveal insights
- May discover the "minimal" solution is actually the best
- Avoid premature optimization

**Example from TDD.java:**
```java
// GREEN Phase: Minimal implementation
public BigDecimal calculateBasicCommission(BigDecimal dealValue, BigDecimal rate) {
    return dealValue.multiply(rate).setScale(2, RoundingMode.HALF_UP);
}
```

This is minimal but complete. It doesn't include validation, logging, or caching—those would be added if tests require them.

## TDD Principles and Best Practices

### Answer 6: FIRST Principles

**FIRST** is an acronym for test qualities in TDD:

**F - Fast**
- Tests should run in milliseconds, not seconds
- Fast tests encourage frequent running
- Slow tests discourage TDD practice
- Example: `TDDTest.testFirstPrinciples()` verifies execution time < 100ms

**I - Independent**
- Tests should not depend on each other
- Can run in any order
- One test's failure doesn't cascade
- Each test has its own setup/teardown

**R - Repeatable**
- Tests produce same results every time
- No dependence on environment, time, or randomness
- Can run on any machine with same results
- Example: Commission calculations are deterministic

**S - Self-Validating**
- Tests have clear pass/fail (no manual verification)
- Use assertions, not print statements
- Automated result checking
- Example: All tests use `assertEquals`, `assertTrue`, etc.

**T - Timely**
- Tests written at the right time
- In TDD: Tests written BEFORE implementation
- Don't delay testing until later
- Immediate test writing prevents technical debt

### Answer 7: Three Rules of TDD

Uncle Bob's Three Rules of TDD:

**Rule 1: Don't Write Production Code Until You Have a Failing Test**
- Always start with RED phase
- Test defines what to build
- Ensures all code is driven by tests

**Rule 2: Don't Write More of a Test Than Is Sufficient to Fail**
- Write minimal test that fails
- Don't write multiple failing tests
- One requirement at a time

**Rule 3: Don't Write More Production Code Than Necessary to Pass the Test**
- Write minimal implementation
- Only make current test pass
- Don't add untested features

**Why They Matter:**
- Enforce discipline of TDD cycle
- Prevent over-engineering
- Ensure complete test coverage
- Keep development incremental and focused

**Example Violation:**
```java
// WRONG: Writing implementation before test
public BigDecimal calculate(...) {
    // Implementation here
}

// CORRECT: Write test first
@Test
void testCalculate() {
    // This fails first, then implement
}
```

### Answer 8: Test Independence

Tests must be independent for several critical reasons:

**Problems with Dependent Tests:**

**1. Order Dependency**
```java
// BAD: Test depends on previous test
@Test
void test1() {
    service.initialize(); // Sets up state
}

@Test
void test2() {
    // Assumes test1 ran first
    assertEquals(expected, service.getValue()); // Fails if run alone
}
```

**2. Cascading Failures**
- One failing test causes others to fail
- Difficult to identify root cause
- Debugging becomes much harder

**3. Fragile Test Suite**
- Tests break when run in different order
- Parallel execution impossible
- Difficult to run individual tests

**4. Maintenance Nightmare**
- Changing one test affects others
- Coupling between tests
- Hard to refactor

**Best Practice: Independent Tests**
```java
@BeforeEach
void setUp() {
    // Each test gets fresh service
    commissionService = new TDD();
    // Each test sets up own data
    testDeal = new Deal(...);
}
```

**Benefits:**
- Run tests in any order
- Run tests in parallel
- Debug single test in isolation
- Refactor one test without affecting others

### Answer 9: Refactoring Safety

Tests provide a safety net during refactoring:

**How Tests Enable Safe Refactoring:**

**1. Regression Detection**
- Any breaking change immediately caught
- Tests fail if behavior changes
- Fast feedback prevents bugs

**2. Confidence to Change**
```java
// Can refactor this:
if (dealValue.compareTo(new BigDecimal("50000")) <= 0) {
    rate = new BigDecimal("0.08");
}

// Into this:
final BigDecimal TIER_1_MAX = new BigDecimal("50000");
final BigDecimal TIER_1_RATE = new BigDecimal("0.08");
if (dealValue.compareTo(TIER_1_MAX) <= 0) {
    rate = TIER_1_RATE;
}

// Tests prove behavior unchanged
```

**3. Continuous Validation**
- Run tests after each small change
- Catch issues immediately
- Know exactly what change broke what

**4. Fearless Improvement**
- Extract methods confidently
- Rename variables safely
- Restructure code without fear
- Optimize performance with validation

**Example from TDD.java:**
The refactoring from magic numbers to constants in `calculateTieredCommission()` was safe because `testTieredCommissionCalculation_RedPhase()` kept passing.

**Without Tests:**
- Refactoring is risky
- Might introduce bugs
- Manual testing required
- Changes are expensive and scary

### Answer 10: One Test at a Time

Completing full RED-GREEN-REFACTOR cycle before moving to next test is crucial:

**Why One Test at a Time:**

**1. Focus**
- Work on one requirement at a time
- Clear objective: make this test pass
- Avoid context switching

**2. Incremental Progress**
- Each cycle adds working functionality
- Always have working code
- Can stop at any green point

**3. Clear Debugging**
- If test fails, you know exactly what broke
- Recent changes are the culprit
- Fast problem identification

**4. Psychological Benefits**
- Frequent success (passing tests)
- Motivation from progress
- Reduces overwhelm

**Problems with Multiple Failing Tests:**

```java
// BAD: Multiple RED tests
@Test void testFeature1() { ... } // RED
@Test void testFeature2() { ... } // RED
@Test void testFeature3() { ... } // RED

// Which one to implement first?
// Unclear what "done" means
// Overwhelming
```

**Correct Approach:**
```java
// Test 1: RED -> GREEN -> REFACTOR
@Test void testFeature1() { ... } // Complete this cycle

// Then Test 2: RED -> GREEN -> REFACTOR
@Test void testFeature2() { ... } // Then complete this cycle
```

**Benefits:**
- Clear next step
- One test fails at a time (usually)
- Easy to track what's complete

## TDD in Practice

### Answer 11: Basic Commission Example

The `calculateBasicCommission()` method is intentionally simple:

```java
public BigDecimal calculateBasicCommission(BigDecimal dealValue, BigDecimal rate) {
    incrementCalculationCount();
    BigDecimal commission = dealValue.multiply(rate);
    return commission.setScale(2, RoundingMode.HALF_UP);
}
```

**Why This is Acceptable in TDD:**

**1. Test Defines Requirements**
- Test only requires: `dealValue * rate`
- No test requires additional complexity
- Don't add features without tests

**2. YAGNI Principle**
- You Ain't Gonna Need It
- Don't add complexity speculatively
- Wait for tests to demand features

**3. Single Responsibility**
- Method does one thing: basic calculation
- Validation is separate method
- Tiered calculation is separate method

**4. Easy to Test**
- Simple code is easy to test
- Clear, predictable behavior
- No hidden complexity

**5. Easy to Maintain**
- Anyone can understand it
- Low risk of bugs
- Easy to modify if needed

**Principle Demonstrated: KISS**
- Keep It Simple, Stupid
- Simplest solution that passes tests
- Complexity added only when tests require it

### Answer 12: Tiered Commission Refactoring

The refactoring of `calculateTieredCommission()` demonstrates test safety:

**Before Refactoring (GREEN Phase):**
```java
if (dealValue.compareTo(new BigDecimal("50000")) <= 0) {
    rate = new BigDecimal("0.08");
} else if (dealValue.compareTo(new BigDecimal("100000")) <= 0) {
    rate = new BigDecimal("0.10");
} else {
    rate = new BigDecimal("0.12");
}
```

**After Refactoring (REFACTOR Phase):**
```java
final BigDecimal TIER_1_MAX = new BigDecimal("50000");
final BigDecimal TIER_2_MAX = new BigDecimal("100000");
final BigDecimal TIER_1_RATE = new BigDecimal("0.08");
final BigDecimal TIER_2_RATE = new BigDecimal("0.10");
final BigDecimal TIER_3_RATE = new BigDecimal("0.12");

if (dealValue.compareTo(TIER_1_MAX) <= 0) {
    rate = TIER_1_RATE;
} // ... etc
```

**How Tests Ensure Safety:**

**1. Same Behavior, Different Structure**
- Logic unchanged, only structure improved
- Tests verify same outputs for same inputs
- `testTieredCommissionCalculation_RedPhase()` still passes

**2. All Cases Covered**
- Tests cover all three tiers
- Tests cover boundary conditions
- Any regression immediately detected

**3. Immediate Feedback**
- Run tests after refactoring
- Green = safe, Red = broke something
- Can refactor incrementally

**4. Confidence**
- Know refactoring didn't break anything
- Can continue refactoring
- Tests act as specification

### Answer 13: Pipeline Composition

The `calculateFullCommission()` method demonstrates safe composition through TDD:

```java
public BigDecimal calculateFullCommission(Deal deal, BigDecimal bonusRate) {
    // STEP 1: Validation
    if (!isDealEligibleForCommission(deal)) {
        throw new IllegalArgumentException(...);
    }

    // STEP 2: Base calculation
    BigDecimal baseCommission = calculateTieredCommission(deal.getValue());

    // STEP 3: Apply bonus
    if (bonusRate != null && bonusRate.compareTo(BigDecimal.ZERO) > 0) {
        return calculateCommissionWithBonus(baseCommission, bonusRate);
    }

    return baseCommission;
}
```

**How TDD Makes Composition Safer:**

**1. Each Component Tested**
- `isDealEligibleForCommission()` has tests
- `calculateTieredCommission()` has tests
- `calculateCommissionWithBonus()` has tests
- Confidence in each building block

**2. Integration Test**
- `testFullCommissionPipeline_RedPhase()` tests integration
- Verifies components work together
- Tests the orchestration logic

**3. Known Behavior**
- Each component has known, tested behavior
- Can reason about composition
- Predictable interactions

**4. Easy Debugging**
- If integration fails, test individual components
- Isolate the problem quickly
- Components can be tested in isolation

**5. Flexible Refactoring**
- Can change component implementation
- As long as tests pass, integration works
- Can optimize individual components

**TDD Insight:**
Building complex features from tested components is safer and faster than building monolithic untested features.

### Answer 14: Edge Cases

The edge case test cycle (`testEdgeCasesAndValidation_RedPhase()`) is critical:

**Why Edge Cases Cannot Be Omitted:**

**1. Real-World Usage**
```java
// Edge cases happen in production:
assertThrows(IllegalArgumentException.class,
    () -> commissionService.calculateFullCommission(null, BigDecimal.ZERO));
```
- Users pass null
- Systems generate negative numbers
- Boundary conditions occur frequently

**2. Robustness**
- Happy path tests show it works when used correctly
- Edge case tests show it fails gracefully when used incorrectly
- Production code must handle both

**3. Error Handling**
- Edge cases test validation logic
- Test error messages are helpful
- Test recovery mechanisms

**4. Boundary Conditions**
```java
// What happens exactly at tier boundary?
BigDecimal boundaryValue = new BigDecimal("50000");
BigDecimal boundaryCommission = commissionService.calculateTieredCommission(boundaryValue);
assertEquals(new BigDecimal("4000.00"), boundaryCommission.setScale(2));
```
- Boundary conditions are common sources of off-by-one errors
- Must explicitly test them

**5. Security**
- Invalid input might be malicious
- Must validate and reject properly
- Prevent injection attacks, etc.

**Common Edge Cases to Test:**
- Null inputs
- Empty collections
- Zero values
- Negative values
- Boundary values (exactly at thresholds)
- Maximum/minimum values
- Invalid states

### Answer 15: Calculation Tracking

The `calculationCount` feature demonstrates test-driven observability:

**How TDD Drove This Feature:**

**1. Test Defined the Need**
```java
@Test
void testCommissionHistoryTracking_RedPhase() {
    BigDecimal commission1 = commissionService.calculateBasicCommission(...);
    BigDecimal commission2 = commissionService.calculateBasicCommission(...);

    int calculationCount = commissionService.getCalculationCount();

    assertTrue(calculationCount >= 2);
}
```

**2. Test Required API**
- Test needed `getCalculationCount()` method
- Drove addition of counter field
- Drove implementation of tracking

**3. Incremental Addition**
- Added `calculationCount` field
- Added `incrementCalculationCount()` helper
- Added getter method
- Each step driven by test

**4. Refactoring Opportunity**
- Initially might increment in each method
- Refactored to helper method (DRY)
- Tests ensure tracking still works

**Benefits of Test-Driven Observability:**
- Features like metrics, logging, tracing are testable
- Observability built in, not added as afterthought
- Verified to work correctly

**Without TDD:**
- Might forget to add observability
- Might add it incorrectly
- No tests to verify it works

## Design and Architecture

### Answer 16: TDD and Design

TDD profoundly influences code design:

**Design Qualities from TDD:**

**1. Modular Code**
- Small, focused methods (easy to test)
- Single Responsibility Principle naturally followed
- Example: Separate methods for validation, calculation, bonus

**2. Loose Coupling**
- Methods have minimal dependencies
- Can test each method independently
- Changes don't ripple through codebase

**3. Clear Interfaces**
- Tests define how code is called
- APIs designed from caller's perspective
- Intent-revealing names

**4. Dependency Injection**
- Dependencies passed in (testable)
- Not hardcoded (untestable)
- Can mock dependencies in tests

**5. Separation of Concerns**
- Validation separate from calculation
- Calculation separate from persistence
- Each concern tested independently

**Example from TDD.java:**
```java
// These are separate, focused methods:
isDealEligibleForCommission(deal)  // Validation
calculateTieredCommission(value)    // Calculation
calculateCommissionWithBonus(...)   // Enhancement
```

**Design Patterns that Emerge from TDD:**
- Strategy (different calculation algorithms)
- Builder (complex object construction)
- Factory (object creation)
- Decorator (adding features)

### Answer 17: Testability

**Testable Code** means code that can be easily tested:

**Characteristics of Testable Code:**

**1. Deterministic**
- Same inputs produce same outputs
- No hidden randomness
- No hidden state

**2. Isolated**
- Doesn't depend on external systems
- Dependencies can be mocked/stubbed
- No global state

**3. Small Units**
- Methods do one thing
- Functions have few parameters
- Classes have clear responsibilities

**4. Observable**
- Results can be inspected
- State changes are visible
- Side effects are testable

**5. Controllable**
- Inputs can be controlled
- Environment can be set up
- Preconditions can be established

**How TDD Leads to Testable Code:**

Writing tests first forces you to:
- Design APIs that can be called in tests
- Avoid untestable patterns (global state, singletons)
- Keep methods small and focused
- Make behavior observable

**Example - Testable:**
```java
public BigDecimal calculateCommission(BigDecimal value, BigDecimal rate) {
    return value.multiply(rate);
}
```
- Pure function
- No side effects
- Easy to test

**Example - Less Testable:**
```java
public void processCommission() {
    BigDecimal value = database.getValue(); // External dependency
    BigDecimal rate = Config.RATE;          // Global state
    BigDecimal result = value.multiply(rate);
    database.save(result);                  // Side effect
    sendEmail();                            // External system
}
```
- Multiple concerns
- External dependencies
- Hard to test in isolation

### Answer 18: Single Responsibility

TDD encourages Single Responsibility Principle (SRP):

**Single Responsibility Principle:**
A class or method should have one, and only one, reason to change.

**How TDD Enforces SRP:**

**1. Testing Complexity**
- Methods doing multiple things are hard to test
- Tests become complex and fragile
- TDD pain signals need for refactoring

**2. One Assert per Concept**
- Tests should verify one concept
- Methods doing multiple things require multiple unrelated asserts
- Signals method should be split

**3. Test Naming**
- Can't name test if method does multiple things
- "testCalculateAndValidateAndSave" is a smell
- Should be three separate methods with three tests

**Examples from TDD.java:**

**Separate Responsibilities:**
```java
// Validation responsibility
public boolean isDealEligibleForCommission(Deal deal)

// Calculation responsibility
public BigDecimal calculateTieredCommission(BigDecimal dealValue)

// Enhancement responsibility
public BigDecimal calculateCommissionWithBonus(BigDecimal base, BigDecimal bonus)
```

**Each Method:**
- Has one clear purpose
- One reason to change
- Easy to test
- Easy to name
- Easy to understand

**Violation Example:**
```java
// BAD: Multiple responsibilities
public BigDecimal calculateAndSaveCommission(Deal deal) {
    // Validation
    if (!isValid(deal)) throw exception;

    // Calculation
    BigDecimal result = deal.getValue() * rate;

    // Persistence
    database.save(result);

    // Notification
    email.send(result);

    return result;
}
```

This method has 4 responsibilities and would require complex mocking to test.

### Answer 19: Dependencies and Coupling

TDD encourages loose coupling:

**Coupling Types:**

**1. Tight Coupling (Bad)**
```java
public class CommissionService {
    private MySQLDatabase db = new MySQLDatabase(); // Hardcoded dependency

    public void save(BigDecimal commission) {
        db.connect("localhost");  // Implementation details
        db.save(commission);
    }
}
```
- Cannot test without real database
- Cannot swap implementations
- Hard to change

**2. Loose Coupling (Good)**
```java
public class CommissionService {
    private Database db; // Dependency abstraction

    public CommissionService(Database db) {  // Injected
        this.db = db;
    }

    public void save(BigDecimal commission) {
        db.save(commission);  // No implementation details
    }
}
```
- Can inject mock database for testing
- Can swap implementations
- Easy to change

**How TDD Encourages Loose Coupling:**

**1. Testing Pressure**
- Tightly coupled code is hard to test
- Tests require real dependencies
- Pain motivates decoupling

**2. Dependency Injection**
- Pass dependencies in constructor/parameters
- Makes dependencies explicit
- Can inject mocks in tests

**3. Interface-Based Design**
- Depend on abstractions, not concretions
- Can provide test doubles
- Flexibility for future changes

**Examples from TDD.java:**

The methods have minimal dependencies:
```java
public BigDecimal calculateBasicCommission(BigDecimal dealValue, BigDecimal rate) {
    // Only depends on inputs, not external systems
    // Easy to test with any values
    return dealValue.multiply(rate).setScale(2, RoundingMode.HALF_UP);
}
```

No hidden dependencies, no global state, no tight coupling.

### Answer 20: Interface Design

Writing tests first improves interface design:

**Test-First Interface Design:**

**1. User Perspective**
```java
// Test defines how you WANT to use the API
@Test
void testCommission() {
    BigDecimal result = service.calculateCommission(
        new BigDecimal("100000"),
        new BigDecimal("0.10")
    );
    assertEquals(new BigDecimal("10000.00"), result.setScale(2));
}
```
- API designed from caller's perspective
- Natural, intuitive usage
- Clear parameter order and types

**2. Minimal Parameters**
- Tests reveal when methods have too many parameters
- Painful to set up → signals need for refactoring
- TDD encourages focused methods

**3. Intent-Revealing Names**
```java
// Test makes unclear naming obvious
service.calculate(...)      // Calculate what?
service.calculateCommission(...)  // Clear!
```
- Test name influences method name
- Clear test names lead to clear method names

**4. Return Types**
- Tests clarify what should be returned
- Explicit about success/failure modes
- Exception vs return value decisions

**5. Consistency**
- Multiple tests reveal inconsistent APIs
- TDD encourages consistent patterns across methods

**Benefits of Test-Driven Interface Design:**

- **More Usable**: Designed from user perspective
- **Better Documentation**: Tests show usage examples
- **Easier to Learn**: Clear, intuitive APIs
- **Fewer Parameters**: Methods stay focused
- **Better Names**: Intent-revealing names
- **Type Safety**: Correct types from the start

## Benefits and Challenges

### Answer 21: Documentation Through Tests

Tests serve as excellent documentation:

**Tests as Documentation:**

**1. Executable Examples**
```java
@Test
@DisplayName("Calculate 10% commission on $100,000 deal")
void testBasicCommissionCalculation() {
    // This test IS the documentation
    BigDecimal result = service.calculateBasicCommission(
        new BigDecimal("100000"),
        new BigDecimal("0.10")
    );
    assertEquals(new BigDecimal("10000.00"), result.setScale(2));
}
```
- Shows exactly how to use the method
- Provides concrete example with values
- Always up-to-date (tests must pass)

**2. Use Cases**
- Each test represents a use case
- Shows different scenarios
- Documents edge cases

**3. Expected Behavior**
- Assertions document expected results
- No ambiguity
- Verifiable

**4. Always Current**
```
Traditional Documentation: Written once, becomes outdated
Test Documentation: Must pass, so always correct
```

**5. Comprehensive**
- Tests cover all features
- Show error handling
- Document constraints

**Advantages Over Traditional Documentation:**

| Traditional Docs | Test Documentation |
|------------------|-------------------|
| Can become outdated | Must be current |
| No verification | Executable verification |
| Separate from code | Lives with code |
| Might be incomplete | Coverage metrics show completeness |
| Abstract descriptions | Concrete examples |
| Manual maintenance | Automated validation |

**Example:**
The 9 tests in `TDDTest.java` document:
- How to calculate basic commissions
- How to validate deals
- Tiered rate structure
- Bonus application
- Error handling
- Edge cases

### Answer 22: Bug Detection

TDD catches bugs at the earliest, cheapest point:

**Bug Detection Timeline:**

**Traditional Development:**
```
Write Code → Manual Testing → QA → Staging → Production
             ↑ Find bugs here (hours/days later)
```

**TDD:**
```
Write Test → Write Code → Test Fails ← Bug found (seconds later)
```

**When TDD Catches Bugs:**

**1. Immediate (During Development)**
```java
@Test
void test() {
    // Write implementation
    // Run test
    // FAIL - bug found immediately
}
```
- Seconds after writing buggy code
- Context still fresh in mind
- Easy to fix

**2. During Refactoring**
```java
// Refactor code
// Run tests
// FAIL - refactoring broke something
// Fix immediately
```

**3. Before Commit**
- All tests run before committing
- Bugs caught before sharing code
- Never commit broken code

**Cost of Fixing Bugs:**

| Stage | Time to Find | Cost to Fix | Relative Cost |
|-------|-------------|-------------|---------------|
| TDD (immediate) | Seconds | Minutes | 1x |
| Unit Testing | Hours | Hours | 10x |
| Integration Testing | Days | Days | 100x |
| QA | Weeks | Weeks | 1000x |
| Production | Months | Very High | 10000x |

**Types of Bugs TDD Catches:**

1. **Logic Errors**: Wrong calculation
2. **Edge Cases**: Boundary conditions
3. **Null Handling**: Missing null checks
4. **Type Errors**: Wrong types
5. **Regressions**: Previously working code breaks

### Answer 23: Code Coverage

TDD naturally achieves high code coverage:

**Why TDD Has High Coverage:**

**1. Tests Written First**
```
TDD: Test → Implementation
Result: Every line has a test because tests came first

TAD (Test-After): Implementation → Test
Result: Some lines might not get tested
```

**2. No Untested Code**
- Can't write code without a test
- Rule: Don't write production code without failing test
- Enforces 100% coverage discipline

**3. Coverage is Meaningful**
- Lines covered because tests REQUIRE them
- Not just covered, but meaningfully tested
- Tests actually verify behavior

**4. Edge Cases Included**
- TDD cycles include edge case tests
- Boundary conditions have tests
- Error paths have tests

**Comparison:**

**TDD Coverage:**
```java
// Test written first
@Test void testCalculate() { ... }  ← Drives implementation

public BigDecimal calculate() {
    // Every line needed to pass test
    return value.multiply(rate);  ← Covered by test
}
```
Coverage: 100%, meaningful

**Test-After Coverage:**
```java
// Implementation written first
public BigDecimal calculate() {
    log("Calculating");  ← Might not be tested
    return value.multiply(rate);  ← Gets tested
}

@Test void testCalculate() {
    // Test written after, might miss logging
}
```
Coverage: Maybe 80%, less meaningful

**Coverage Metrics:**

TDD projects typically achieve:
- 90-100% line coverage
- 80-95% branch coverage
- High mutation test scores

### Answer 24: Debugging Time

TDD dramatically reduces debugging time:

**How TDD Reduces Debugging:**

**1. Fewer Bugs**
- Bugs caught during development
- Never make it to debugging phase
- Prevention vs detection

**2. Immediate Detection**
```
Write buggy code → Run test → FAIL immediately
vs.
Write buggy code → Later find bug → Debug to find cause
```

**3. Known Working State**
- Always know last time tests passed
- Recent changes are culprit
- Small search space

**4. Isolation**
- Tests isolate specific functionality
- Can run individual test
- Pinpoint exact location

**5. Regression Safety**
- Tests prevent reintroducing bugs
- Fix once, stays fixed
- No "Whac-A-Mole" debugging

**Debugging Scenarios:**

**Without TDD:**
```
1. User reports bug
2. Can't reproduce
3. Add logging
4. Try to reproduce
5. Analyze logs
6. Form hypothesis
7. Test hypothesis
8. Fix bug
9. Manual testing
10. Deploy
Time: Hours to days
```

**With TDD:**
```
1. Test fails
2. Recent change is cause
3. Fix bug
4. Test passes
Time: Minutes
```

**Example:**
In TDD.java, if `calculateTieredCommission()` has a bug, the test immediately shows:
- Which test failed: `testTieredCommissionCalculation_RedPhase()`
- What was expected: `2400.00`
- What was actual: `2500.00`
- Where the bug is: In tier rate calculation

No debugging session needed.

### Answer 25: Initial Slowdown

TDD feels slower initially but becomes faster long-term:

**Why TDD Feels Slower at First:**

**1. Learning Curve**
- Learning to write tests first
- Unfamiliar workflow
- New tools and practices

**2. Discipline Required**
- Resist urge to write implementation first
- Follow RED-GREEN-REFACTOR strictly
- Feels unnatural initially

**3. More Code**
- Writing tests in addition to implementation
- Feels like extra work
- More lines of code total

**4. Different Thinking**
- Think about tests before implementation
- Design from outside-in
- Mental shift required

**Why It's Faster Long-Term:**

**1. Less Debugging**
```
Traditional: 20% coding + 80% debugging = 100% time
TDD: 40% coding + 10% debugging = 50% time
```

**2. Fewer Production Bugs**
- Less emergency fixes
- Less customer escalations
- Less firefighting

**3. Confident Refactoring**
- Can improve code quickly
- No fear of breaking things
- Technical debt stays low

**4. Better Design**
- Less rework
- Fewer major refactorings
- Code scales better

**5. Easier Maintenance**
- Tests document code
- Can understand code faster
- Changes are safer

**Time Investment:**

```
Month 1:    TDD slower (learning)
Month 2-3:  TDD breaks even
Month 4+:   TDD significantly faster
```

**Long-term Velocity:**

| Approach | Sprint 1 | Sprint 5 | Sprint 10 |
|----------|----------|----------|-----------|
| No Tests | Fast | Slowing | Very Slow (debt) |
| TDD | Slower | Steady | Fast (low debt) |

## Advanced TDD Concepts

### Answer 26: Test Granularity

Determining test size is an art:

**Guidelines for Test Granularity:**

**1. One Concept Per Test**
```java
// GOOD: Tests one concept
@Test
void dealMustBeClosedToBeEligible() {
    Deal closedDeal = new Deal();
    closedDeal.setStatus(DealStatus.CLOSED);
    assertTrue(service.isDealEligibleForCommission(closedDeal));
}

// BAD: Tests multiple concepts
@Test
void testEverything() {
    // Tests validation AND calculation AND bonus
    // Too large, unclear what's being tested
}
```

**2. Small, Focused Tests**
- Test one behavior
- 3-10 lines of test code
- Clear arrange-act-assert
- Easy to understand

**3. Test Method, Not Implementation**
```java
// Right size: Tests one method's behavior
@Test
void calculateBasicCommissionUsesRate() {
    BigDecimal result = service.calculateBasicCommission(
        new BigDecimal("1000"),
        new BigDecimal("0.10")
    );
    assertEquals(new BigDecimal("100.00"), result.setScale(2));
}
```

**4. Multiple Tests for Complex Methods**
```java
// Complex method needs multiple tests:
@Test void tierOneRate() { ... }
@Test void tierTwoRate() { ... }
@Test void tierThreeRate() { ... }
@Test void boundaryConditions() { ... }
@Test void negativeInput() { ... }
```

**Signs Test is Too Large:**
- Hard to name
- Tests multiple behaviors
- Multiple unrelated asserts
- Long setup (more than 5-10 lines)

**Signs Test is Too Small:**
- Trivial assertion
- Tests getter/setter only
- No real logic tested

**Rule of Thumb:**
One test per logical branch or scenario.

### Answer 27: Test Naming

Clear test names are crucial:

**Why Test Naming Matters:**

**1. Documentation**
```java
@Test
@DisplayName("Calculate 10% commission on $100,000 deal")
void testBasicCommissionCalculation_RedPhase() {
    // Name explains what's being tested
}
```
- Name documents intent
- Explains what should happen
- No need to read test code to understand

**2. Failure Messages**
```
FAIL: testBasicCommissionCalculation_RedPhase
```
Immediately know what failed

**3. Test Organization**
- Names group related tests
- Easy to find specific test
- Clear test suite structure

**4. Requirements Traceability**
- Name maps to requirement
- Can verify requirement is tested
- Living specification

**Good Naming Patterns:**

**Pattern 1: Method + Scenario + Expected**
```java
void calculateCommission_WhenDealIsClosed_ReturnsCommissionAmount()
void calculateCommission_WhenDealIsOpen_ThrowsException()
```

**Pattern 2: Should + Behavior**
```java
void shouldCalculateCommissionForClosedDeals()
void shouldThrowExceptionForNegativeValues()
```

**Pattern 3: Given-When-Then (BDD Style)**
```java
void givenClosedDeal_whenCalculatingCommission_thenReturnsAmount()
```

**Pattern 4: Descriptive with DisplayName**
```java
@DisplayName("Tiered commission uses 8% rate for deals under $50k")
void testTieredCommission_SmallDeal()
```

**Bad Test Names:**
```java
void test1()           // No information
void testCommission()  // Too vague
void testStuff()       // Meaningless
```

### Answer 28: When Tests Fail

A previously passing test failing is significant:

**What a Failing Test Means:**

**1. Regression**
- Code that worked now doesn't
- Recent change broke something
- Need to fix immediately

**2. Find the Change**
```bash
# What changed since tests passed?
git diff HEAD~1

# Recent commits
git log --oneline -5
```

**3. Isolate the Problem**
- Run single failing test
- Check recent code changes
- Revert if necessary

**4. Fix Immediately**
- Don't continue with failing tests
- Fix before adding new features
- Maintain green test suite

**Response Process:**

**Step 1: Reproduce**
```bash
mvn test -Dtest=TDDTest#testBasicCommission
```
Verify test actually fails

**Step 2: Understand**
- Read failure message
- Check expected vs actual
- Review test code

**Step 3: Identify Cause**
- Recent code changes
- Changed dependencies
- Environment changes

**Step 4: Fix**
- Fix code (not test)
- Or update test if requirements changed
- Get back to green

**Step 5: Prevent**
- Add test for similar cases
- Improve test coverage
- Learn from mistake

**Common Causes:**

1. **Code Change**: Recent implementation broke something
2. **Refactoring**: Behavior accidentally changed
3. **Dependency Update**: Library behavior changed
4. **Environment**: Database, file system, etc.
5. **Flaky Test**: Race condition, timing issue

**Red Flags:**

- Ignoring failing tests
- Deleting failing tests
- Only running subset of tests
- Long periods with red tests

### Answer 29: Over-Testing

Yes, you can write too many tests:

**Signs of Over-Testing:**

**1. Testing Implementation Details**
```java
// BAD: Testing private method implementation
@Test
void testPrivateHelperMethod() {
    // Don't test internals
}
```
Test behavior, not implementation

**2. Testing Framework Code**
```java
// BAD: Testing that BigDecimal.multiply() works
@Test
void testMultiplication() {
    BigDecimal result = new BigDecimal("2").multiply(new BigDecimal("3"));
    assertEquals(new BigDecimal("6"), result);
}
```
Trust the framework

**3. Trivial Tests**
```java
// BAD: Testing simple getter
@Test
void testGetValue() {
    deal.setValue(BigDecimal.TEN);
    assertEquals(BigDecimal.TEN, deal.getValue());
}
```
No logic = no test needed

**4. Duplicate Tests**
```java
// Testing same thing multiple ways
@Test void testCommissionMethod1() { ... }
@Test void testCommissionMethod2() { ... } // Same test, different setup
```

**5. Testing Dependencies**
```java
// Testing that library works
@Test void testJavaStringContains() { ... }
```

**Balance Testing:**

**DO Test:**
- Business logic
- Algorithms
- Edge cases
- Error handling
- Integration points
- Complex calculations

**DON'T Test:**
- Simple getters/setters
- Framework code
- Third-party libraries
- Private implementation details
- Auto-generated code

**Guidelines:**

**1. Test Behavior, Not Implementation**
```java
// GOOD: Tests public API behavior
service.calculate(...)

// BAD: Tests internal helper
service.privateHelper(...)
```

**2. Focus on Value**
- Test what matters to users
- Test business requirements
- Test failure modes

**3. Pragmatism**
- 100% coverage is not always the goal
- 80-90% coverage of meaningful code is excellent
- Focus on critical paths

**Test ROI:**

```
High ROI Tests:
- Complex business logic
- Edge cases that cause bugs
- Integration points
- Security-critical code

Low ROI Tests:
- Simple getters/setters
- Framework functionality
- Obviously correct code
```

### Answer 30: TDD for Bug Fixes

Using TDD for bug fixes:

**TDD Bug Fix Process:**

**Step 1: RED - Write Failing Test**
```java
@Test
void bugFix_NegativeCommissionForLargeDeals() {
    // Reproduce the bug in a test
    BigDecimal result = service.calculateCommission(
        new BigDecimal("1000000")
    );

    // This should pass but currently fails (bug)
    assertTrue(result.compareTo(BigDecimal.ZERO) > 0);
}
```
Test should fail, reproducing the bug

**Step 2: GREEN - Fix the Bug**
```java
public BigDecimal calculateCommission(BigDecimal value) {
    // Fix the bug
    if (value.compareTo(MAX_VALUE) > 0) {
        return MAX_VALUE.multiply(rate);  // Cap at max
    }
    return value.multiply(rate);
}
```
Test now passes

**Step 3: REFACTOR - Clean Up**
- Extract constants
- Improve error messages
- Add related tests

**Benefits of TDD for Bug Fixes:**

**1. Regression Prevention**
- Bug can never return
- Test ensures fix stays fixed
- Automated verification

**2. Reproduces Bug**
- Test documents the bug
- Shows exact failure condition
- Can be shared with team

**3. Verifies Fix**
- Proves fix works
- No manual testing needed
- Confidence in fix

**4. Documentation**
- Test explains what bug was
- Shows why fix was needed
- Historical record

**Example Process:**

```
1. Bug Report: "Commission negative for $1M deals"

2. Write Test:
   @Test
   void commissionShouldBePositiveForLargeDeals() {
       BigDecimal result = service.calculate(
           new BigDecimal("1000000")
       );
       assertTrue(result.compareTo(BigDecimal.ZERO) > 0,
           "Commission should always be positive");
   }

   Status: RED (test fails, bug reproduced)

3. Fix Bug:
   // Add validation
   if (result.compareTo(BigDecimal.ZERO) < 0) {
       throw new IllegalStateException("Negative commission");
   }

   Status: GREEN (test passes, bug fixed)

4. Refactor:
   // Extract validation method
   private void validateCommission(BigDecimal commission) { ... }

   Status: GREEN (tests still pass)

5. Deploy with confidence!
```

## Comparison and Tradeoffs

### Answer 31: TDD vs. TAD

Comparing Test-Driven Development (TDD) and Test-After Development (TAD):

**TDD (Test-Driven Development):**
```
Test → Implementation → Refactor
```

**TAD (Test-After Development):**
```
Implementation → Test
```

**Comparison Table:**

| Aspect | TDD | TAD |
|--------|-----|-----|
| **When Tests Written** | Before implementation | After implementation |
| **Design Influence** | Tests drive design | Tests verify design |
| **Coverage** | Naturally high (90-100%) | Often lower (60-80%) |
| **Bugs Found** | During development | During testing phase |
| **Refactoring** | Safe (protected by tests) | Risky (tests may not catch issues) |
| **API Design** | User-focused (test perspective) | Implementation-focused |
| **Initial Speed** | Slower | Faster |
| **Long-term Speed** | Faster (fewer bugs) | Slower (more debugging) |
| **Test Quality** | Higher (intentional) | Lower (afterthought) |
| **Completeness** | All code has tests | Some code untested |

**TDD Advantages:**

1. **Better Design**: Tests drive API design
2. **Higher Coverage**: All code tested by definition
3. **Fewer Bugs**: Caught during development
4. **Living Documentation**: Tests show usage
5. **Refactoring Safety**: Comprehensive tests protect
6. **Clear Requirements**: Tests define behavior

**TDD Disadvantages:**

1. **Learning Curve**: Unfamiliar workflow
2. **Initial Slowdown**: Takes time to write tests first
3. **Discipline Required**: Easy to skip test-first
4. **More Code**: Tests add to codebase size

**TAD Advantages:**

1. **Familiar**: Traditional approach
2. **Initially Faster**: No tests to write first
3. **Flexible**: Can skip tests if needed
4. **Lower Barrier**: Easier for beginners

**TAD Disadvantages:**

1. **Lower Coverage**: Easy to skip tests
2. **Worse Design**: Implementation-first design
3. **More Bugs**: Found later (expensive)
4. **Risky Refactoring**: No safety net
5. **Incomplete Tests**: Some code never tested
6. **Testing Debt**: "We'll add tests later" (never happens)

**When to Use Each:**

**Use TDD:**
- Production code
- Business logic
- Complex algorithms
- Critical features
- Long-term projects
- Team projects

**Use TAD:**
- Prototypes
- Spike solutions
- Throwaway code
- Very simple code
- Uncertain requirements

**Hybrid Approach:**
Some teams use TDD for core logic and TAD for simple code.

### Answer 32: When Not to Use TDD

TDD is not always appropriate:

**Situations Where TDD is Less Beneficial:**

**1. Prototyping / Spike Solutions**
```
Exploring: "Will this approach work?"
Requirements: Unclear
Goal: Learn and discard
TDD: Slows down exploration
Alternative: Prototype without tests, then TDD for real implementation
```

**2. Throwaway Code**
- One-time scripts
- Data migration scripts
- Temporary workarounds
- Code that won't be maintained

**3. UI/UX Exploration**
```
Visual Design: Requires manual validation
User Experience: Subjective
Rapid Iteration: Design changes frequently
TDD: Tests become obsolete quickly
Alternative: Manual testing, user feedback
```

**4. Very Simple Code**
```java
// Too simple for TDD
public String getFullName() {
    return firstName + " " + lastName;
}
```
Cost of test exceeds benefit

**5. Highly Experimental Features**
- Unclear requirements
- Rapid design changes
- Learning new technology
- Proof of concept

**6. Integration with Unstable External Systems**
- Third-party API in development
- Frequently changing interfaces
- Difficult to mock
- Tests break often

**7. Performance Optimization**
```
Premature Optimization: Don't know what to optimize yet
Need Profiling: Must measure first
TDD: Tests define behavior, not performance
Alternative: TDD for correctness, then profile and optimize
```

**8. Legacy Code Without Tests**
```
Challenge: Hard to add tests to untestable code
Reality: Must refactor to make testable
Approach: Incremental testing, not strict TDD
```

**Better Approaches for These Cases:**

1. **Prototyping**: Spike without tests, then TDD for production
2. **UI**: Manual testing, user testing, visual regression tests
3. **Legacy**: Add characterization tests, refactor gradually
4. **Exploration**: Learn first, then apply TDD to implementation
5. **Simple Code**: Common sense - if test is longer than code, skip it

**Key Principle:**
Use TDD where it provides value. Don't be dogmatic.

### Answer 33: TDD and Legacy Code

Applying TDD to legacy code is challenging but possible:

**Challenges with Legacy Code:**

**1. No Existing Tests**
- No safety net for changes
- Can't refactor safely
- Don't know if changes break anything

**2. Untestable Design**
```java
// Legacy code often has:
class LegacyService {
    private static Database db = new Database(); // Hardcoded

    public void process() {
        db.connect();  // Can't test without real DB
        // Complex logic mixed with I/O
    }
}
```

**3. High Coupling**
- Classes depend on many others
- Can't test in isolation
- Difficult to mock

**4. Large Methods**
- 100+ line methods
- Multiple responsibilities
- Hard to understand

**Strategy for Legacy Code:**

**Step 1: Characterization Tests**
```java
@Test
void documentCurrentBehavior() {
    // Don't know if behavior is correct
    // Just document what it does now
    LegacyService service = new LegacyService();
    Result result = service.process(input);

    // Document current output
    assertEquals(currentOutput, result);
}
```
Purpose: Understand current behavior, prevent regressions

**Step 2: Identify Change Point**
- Find where you need to make changes
- Understand dependencies
- Plan minimal change

**Step 3: Write Test for New Behavior (RED)**
```java
@Test
void newFeature_shouldDoX() {
    // Test for new behavior
    // Will fail because feature doesn't exist
}
```

**Step 4: Refactor to Make Testable**
```java
// Before: Untestable
public void process() {
    Database db = new Database();
    db.connect();
    // ...
}

// After: Testable
public void process(Database db) {  // Inject dependency
    db.connect();
    // ...
}
```

**Step 5: Implement New Feature (GREEN)**
- Add new feature
- Keep existing tests passing
- Make new test pass

**Step 6: Refactor (REFACTOR)**
- Improve code quality
- Tests protect against regressions
- Incremental improvement

**Modified TDD for Legacy:**

```
Traditional TDD: Test → Code → Refactor
Legacy TDD: Characterization Tests → Small Refactoring → Test → Code → Refactor
```

**Techniques:**

**1. Sprout Method**
```java
// Legacy method too complex to test
public void complexLegacy() {
    // 100 lines of legacy code

    newFeature();  // Sprout new tested method

    // More legacy code
}

// Test the sprout in isolation
public void newFeature() {
    // New, tested code
}
```

**2. Wrap Method**
```java
// Wrap legacy method with tested wrapper
public void legacyProcessWithNewFeature() {
    preProcess();  // New, tested
    legacyProcess();  // Leave legacy alone
    postProcess();  // New, tested
}
```

**3. Strangler Pattern**
- Gradually replace legacy code
- New code uses TDD
- Old code slowly removed

**Example Process:**

```
1. Legacy Code:
   void processCommission() {
       // 200 lines of untestable code
   }

2. Add Characterization Test:
   @Test
   void currentBehavior() {
       // Document what it does now
   }

3. Extract Testable Part:
   void processCommission() {
       BigDecimal amount = calculateAmount();  // Extracted!
       // Rest of legacy code
   }

4. TDD for New Method:
   @Test  // RED
   void testCalculateAmount() { ... }

   BigDecimal calculateAmount() {  // GREEN
       // Tested implementation
   }

5. Gradually Improve:
   - More extractions
   - More tests
   - Less legacy code
```

### Answer 34: TDD and Prototyping

TDD during prototyping requires judgment:

**Prototyping Characteristics:**

1. **Uncertain Requirements**
   - Don't know what to build yet
   - Exploring possibilities
   - Requirements will change

2. **Rapid Iteration**
   - Frequent large changes
   - Throw away and start over
   - Speed over quality

3. **Learning Goals**
   - Learn new technology
   - Prove feasibility
   - Explore design options

**Why TDD Can Be Challenging for Prototypes:**

**1. Wasted Effort**
```
Write Test → Write Code → Delete Everything
Why test code that will be thrown away?
```

**2. Slow Exploration**
- Tests slow down experimentation
- TDD discipline interrupts flow
- Need freedom to try things quickly

**3. Changing Requirements**
- Tests become obsolete quickly
- Frequent test rewrites
- Test maintenance overhead

**4. Unknown Unknowns**
- Don't know what to test
- Learning as you go
- Tests based on wrong assumptions

**When to Use TDD in Prototyping:**

**Use TDD for Prototypes When:**

1. **Core Algorithm Exploration**
```java
// Testing algorithm correctness while exploring
@Test
void prototypeAlgorithm() {
    // Even in prototype, algorithm should be correct
}
```

2. **Comparing Approaches**
```java
// Test helps compare options objectively
@Test void approachA_performance() { ... }
@Test void approachB_performance() { ... }
```

3. **Preserving Insights**
- Tests document what you learned
- Keep when prototype becomes product
- Knowledge transfer to team

**Skip TDD for Prototypes When:**

1. **Pure UI Exploration**
   - Visual design
   - User experience
   - Requires human judgment

2. **Feasibility Checks**
   - "Can this library do X?"
   - Quick experiments
   - Answer yes/no questions

3. **Time-Boxed Spikes**
   - Limited exploration time
   - Disposable code
   - Just learning

**Best Practice: Two-Phase Approach**

**Phase 1: Prototype Without TDD**
```
Goal: Learn and explore
Speed: Fast
Quality: Low
Tests: Minimal or none
Outcome: Knowledge, decisions
```

**Phase 2: Implement With TDD**
```
Goal: Build production code
Speed: Steady
Quality: High
Tests: Comprehensive
Outcome: Tested, maintainable code
```

**Example:**

```
Week 1: Prototype
- Try 3 different commission algorithms
- No tests (exploring)
- Messy code
- Result: Algorithm C is best

Week 2: Implement with TDD
- RED: Test for Algorithm C
- GREEN: Implement Algorithm C properly
- REFACTOR: Clean code
- Result: Production-ready, tested code
```

**Compromise Approach:**

```java
// Prototype with minimal smoke tests
@Test
void prototype_doesntCrash() {
    // Very basic test
    assertDoesNotThrow(() -> prototype.run());
}

// If prototype becomes product, add proper TDD tests
@Test
void productionTest() {
    // Proper TDD
}
```

### Answer 35: TDD in Different Languages

TDD principles are universal, but implementation varies:

**Core TDD Concepts (Language-Independent):**
1. RED-GREEN-REFACTOR cycle
2. Test first
3. Minimal implementation
4. Refactoring

**Language-Specific Considerations:**

**Java (This Implementation):**
```java
@Test
void test() {
    assertEquals(expected, actual);
}
```
- **Frameworks**: JUnit, TestNG
- **Mocking**: Mockito, EasyMock
- **IDE Support**: Excellent (IntelliJ, Eclipse)
- **Compilation**: Compile-time type safety helps
- **Verbosity**: More boilerplate

**Python:**
```python
def test_commission():
    assert calculate(100, 0.1) == 10
```
- **Frameworks**: pytest, unittest
- **Advantages**: Less boilerplate, faster to write
- **Duck Typing**: Need more runtime tests
- **REPL**: Easy to test interactively

**JavaScript/TypeScript:**
```javascript
test('calculate commission', () => {
    expect(calculate(100, 0.1)).toBe(10);
});
```
- **Frameworks**: Jest, Mocha, Jasmine
- **Async**: Must handle promises, callbacks
- **Browser/Node**: Different environments
- **TypeScript**: Type safety helps like Java

**C#:**
```csharp
[Test]
public void TestCommission() {
    Assert.AreEqual(10, Calculate(100, 0.1));
}
```
- **Frameworks**: NUnit, xUnit, MSTest
- **Similar to Java**: Same principles
- **LINQ**: Powerful for test data setup

**Go:**
```go
func TestCommission(t *testing.T) {
    if Calculate(100, 0.1) != 10 {
        t.Error("Expected 10")
    }
}
```
- **Built-in**: testing package in standard library
- **Table Tests**: Idiomatic pattern
- **Interfaces**: Enable easy mocking

**Ruby:**
```ruby
it 'calculates commission' do
    expect(calculate(100, 0.1)).to eq(10)
end
```
- **Frameworks**: RSpec, Minitest
- **BDD Style**: Very readable tests
- **Duck Typing**: Similar to Python

**Common Patterns Across Languages:**

**1. Arrange-Act-Assert** (Universal)
```
Setup → Execute → Verify
```

**2. Test Fixtures** (All languages support)
```
Before/After hooks
Setup/Teardown
```

**3. Mocking** (Available everywhere)
```
Test doubles, stubs, mocks
```

**Language-Specific Challenges:**

**Statically Typed (Java, C#, TypeScript):**
- ✅ Compiler catches errors
- ✅ Better refactoring tools
- ❌ More boilerplate
- ❌ Interface requirements

**Dynamically Typed (Python, JavaScript, Ruby):**
- ✅ Faster to write
- ✅ Less boilerplate
- ❌ Need more tests
- ❌ Runtime errors

**Functional Languages:**
- Pure functions easier to test
- Immutability helps
- Less mocking needed

**Bottom Line:**
TDD works in all languages. Choose frameworks and tools for your language, but principles remain the same.

## Practical Application

### Answer 36: Next Feature

Adding weekend bonus using TDD:

**Requirement:** "Deals closed on weekends get an extra 5% bonus."

**TDD Cycle:**

**RED Phase - Write Failing Test:**

```java
@Test
@DisplayName("Deals closed on weekend receive 5% bonus")
void dealClosedOnWeekend_receives5PercentBonus() {
    // ARRANGE
    Deal saturdayDeal = new Deal("Weekend Deal",
        new BigDecimal("100000"), "REP-001");
    saturdayDeal.setStatus(DealStatus.CLOSED);
    saturdayDeal.setCloseDate(LocalDate.of(2024, 10, 5)); // Saturday

    Deal weekdayDeal = new Deal("Weekday Deal",
        new BigDecimal("100000"), "REP-001");
    weekdayDeal.setStatus(DealStatus.CLOSED);
    weekdayDeal.setCloseDate(LocalDate.of(2024, 10, 7)); // Monday

    // ACT
    BigDecimal weekendCommission = commissionService
        .calculateFullCommission(saturdayDeal, BigDecimal.ZERO);
    BigDecimal weekdayCommission = commissionService
        .calculateFullCommission(weekdayDeal, BigDecimal.ZERO);

    // ASSERT
    // Base: 100000 * 0.12 (tier 3) = 12000
    // Weekend: 12000 * 1.05 = 12600
    assertEquals(new BigDecimal("12600.00"),
        weekendCommission.setScale(2));
    assertEquals(new BigDecimal("12000.00"),
        weekdayCommission.setScale(2));
}

@Test
@DisplayName("Sunday deals also receive weekend bonus")
void sundayDeals_receiveWeekendBonus() {
    Deal sundayDeal = new Deal("Sunday Deal",
        new BigDecimal("100000"), "REP-001");
    sundayDeal.setStatus(DealStatus.CLOSED);
    sundayDeal.setCloseDate(LocalDate.of(2024, 10, 6)); // Sunday

    BigDecimal commission = commissionService
        .calculateFullCommission(sundayDeal, BigDecimal.ZERO);

    assertEquals(new BigDecimal("12600.00"), commission.setScale(2));
}
```

**Status: ❌ RED** (Tests fail - feature doesn't exist)

**GREEN Phase - Make Tests Pass:**

```java
/**
 * Helper method to check if a date falls on weekend
 */
private boolean isWeekend(LocalDate date) {
    DayOfWeek day = date.getDayOfWeek();
    return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
}

/**
 * Apply weekend bonus if deal closed on weekend
 */
private BigDecimal applyWeekendBonus(Deal deal, BigDecimal commission) {
    if (deal.getCloseDate() != null && isWeekend(deal.getCloseDate())) {
        BigDecimal weekendBonus = new BigDecimal("0.05"); // 5%
        return commission.add(commission.multiply(weekendBonus))
            .setScale(2, RoundingMode.HALF_UP);
    }
    return commission;
}

/**
 * Update calculateFullCommission to include weekend bonus
 */
public BigDecimal calculateFullCommission(Deal deal, BigDecimal bonusRate) {
    // Existing validation
    if (deal == null) {
        throw new IllegalArgumentException("Deal cannot be null");
    }

    if (!isDealEligibleForCommission(deal)) {
        throw new IllegalArgumentException(
            "Deal is not eligible for commission. Status: " + deal.getStatus());
    }

    incrementCalculationCount();

    // Existing tiered calculation
    BigDecimal baseCommission = calculateTieredCommission(deal.getValue());

    // NEW: Apply weekend bonus
    baseCommission = applyWeekendBonus(deal, baseCommission);

    // Existing bonus calculation
    if (bonusRate != null && bonusRate.compareTo(BigDecimal.ZERO) > 0) {
        return calculateCommissionWithBonus(baseCommission, bonusRate);
    }

    return baseCommission;
}
```

**Status: ✅ GREEN** (Tests pass!)

**REFACTOR Phase - Improve Code:**

```java
// Extract constant
private static final BigDecimal WEEKEND_BONUS_RATE = new BigDecimal("0.05");

private BigDecimal applyWeekendBonus(Deal deal, BigDecimal commission) {
    if (isWeekendDeal(deal)) {
        return applyBonusRate(commission, WEEKEND_BONUS_RATE);
    }
    return commission;
}

// Extract well-named methods
private boolean isWeekendDeal(Deal deal) {
    return deal.getCloseDate() != null &&
           isWeekend(deal.getCloseDate());
}

// Reuse existing bonus logic
private BigDecimal applyBonusRate(BigDecimal amount, BigDecimal rate) {
    return amount.add(amount.multiply(rate))
        .setScale(2, RoundingMode.HALF_UP);
}

private boolean isWeekend(LocalDate date) {
    DayOfWeek day = date.getDayOfWeek();
    return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
}
```

**Status: ✅ GREEN** (Tests still pass after refactoring!)

**Summary:**
1. ❌ RED: Tests defined weekend bonus behavior
2. ✅ GREEN: Minimal implementation added
3. ✅ REFACTOR: Extracted constants, improved names, reused logic

### Answer 37: Validation Expansion

Adding 90-day close date validation using TDD:

**Requirement:** "Deals must have a closeDate within the last 90 days."

**RED Phase - Write Failing Test:**

```java
@Test
@DisplayName("Deal closed within 90 days is eligible")
void dealClosedWithin90Days_isEligible() {
    // ARRANGE
    Deal recentDeal = new Deal("Recent Deal",
        new BigDecimal("100000"), "REP-001");
    recentDeal.setStatus(DealStatus.CLOSED);
    recentDeal.setCloseDate(LocalDate.now().minusDays(30)); // 30 days ago

    // ACT
    boolean eligible = commissionService.isDealEligibleForCommission(recentDeal);

    // ASSERT
    assertTrue(eligible, "Deal closed 30 days ago should be eligible");
}

@Test
@DisplayName("Deal closed more than 90 days ago is not eligible")
void dealClosedOver90DaysAgo_isNotEligible() {
    // ARRANGE
    Deal oldDeal = new Deal("Old Deal",
        new BigDecimal("100000"), "REP-001");
    oldDeal.setStatus(DealStatus.CLOSED);
    oldDeal.setCloseDate(LocalDate.now().minusDays(91)); // 91 days ago

    // ACT
    boolean eligible = commissionService.isDealEligibleForCommission(oldDeal);

    // ASSERT
    assertFalse(eligible, "Deal closed 91 days ago should not be eligible");
}

@Test
@DisplayName("Deal closed exactly 90 days ago is eligible (boundary)")
void dealClosedExactly90DaysAgo_isEligible() {
    // ARRANGE
    Deal boundaryDeal = new Deal("Boundary Deal",
        new BigDecimal("100000"), "REP-001");
    boundaryDeal.setStatus(DealStatus.CLOSED);
    boundaryDeal.setCloseDate(LocalDate.now().minusDays(90)); // Exactly 90 days

    // ACT
    boolean eligible = commissionService.isDealEligibleForCommission(boundaryDeal);

    // ASSERT
    assertTrue(eligible, "Deal closed exactly 90 days ago should be eligible");
}

@Test
@DisplayName("Deal without close date is not eligible")
void dealWithoutCloseDate_isNotEligible() {
    // ARRANGE
    Deal noDateDeal = new Deal("No Date Deal",
        new BigDecimal("100000"), "REP-001");
    noDateDeal.setStatus(DealStatus.CLOSED);
    // No close date set

    // ACT
    boolean eligible = commissionService.isDealEligibleForCommission(noDateDeal);

    // ASSERT
    assertFalse(eligible, "Deal without close date should not be eligible");
}
```

**Status: ❌ RED** (Tests fail - validation doesn't exist)

**GREEN Phase - Make Tests Pass:**

```java
public boolean isDealEligibleForCommission(Deal deal) {
    // Existing validation
    if (deal == null) {
        return false;
    }

    // Must be CLOSED
    if (deal.getStatus() != DealStatus.CLOSED) {
        return false;
    }

    // NEW: Must have a close date
    if (deal.getCloseDate() == null) {
        return false;
    }

    // NEW: Close date must be within last 90 days
    LocalDate closeDate = deal.getCloseDate();
    LocalDate cutoffDate = LocalDate.now().minusDays(90);

    if (closeDate.isBefore(cutoffDate)) {
        return false;  // Too old
    }

    return true;
}
```

**Status: ✅ GREEN** (Tests pass!)

**REFACTOR Phase - Improve Code:**

```java
// Extract constant
private static final int COMMISSION_ELIGIBILITY_DAYS = 90;

public boolean isDealEligibleForCommission(Deal deal) {
    if (deal == null) {
        return false;
    }

    // Use extracted methods for clarity
    return isClosedStatus(deal) &&
           hasValidCloseDate(deal) &&
           isWithinEligibilityWindow(deal);
}

private boolean isClosedStatus(Deal deal) {
    return deal.getStatus() == DealStatus.CLOSED;
}

private boolean hasValidCloseDate(Deal deal) {
    return deal.getCloseDate() != null;
}

private boolean isWithinEligibilityWindow(Deal deal) {
    LocalDate closeDate = deal.getCloseDate();
    if (closeDate == null) {
        return false;
    }

    LocalDate cutoffDate = LocalDate.now()
        .minusDays(COMMISSION_ELIGIBILITY_DAYS);

    return !closeDate.isBefore(cutoffDate);
}
```

**Status: ✅ GREEN** (Tests still pass after refactoring!)

**Refactoring Benefits:**
- Clear method names explain intent
- Each validation is separate and testable
- Easy to add more validation rules
- Constant makes eligibility window configurable

### Answer 38: Refactoring Exercise

Refactoring tier configuration while keeping tests green:

**Current Implementation:**
```java
public BigDecimal calculateTieredCommission(BigDecimal dealValue) {
    // Hardcoded tiers inside method
    final BigDecimal TIER_1_MAX = new BigDecimal("50000");
    final BigDecimal TIER_2_MAX = new BigDecimal("100000");
    final BigDecimal TIER_1_RATE = new BigDecimal("0.08");
    final BigDecimal TIER_2_RATE = new BigDecimal("0.10");
    final BigDecimal TIER_3_RATE = new BigDecimal("0.12");

    // Logic...
}
```

**Refactoring Goal:**
Make tier configuration flexible and externalized.

**Refactoring Steps (Keeping Tests Green):**

**Step 1: Extract Class for Tier Configuration**

```java
public class CommissionTier {
    private final BigDecimal maxValue;  // null for unlimited
    private final BigDecimal rate;

    public CommissionTier(BigDecimal maxValue, BigDecimal rate) {
        this.maxValue = maxValue;
        this.rate = rate;
    }

    public boolean applies(BigDecimal value) {
        return maxValue == null || value.compareTo(maxValue) <= 0;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
```

**Run Tests: ✅ GREEN** (No behavior change yet)

**Step 2: Add Tier List to TDD Class**

```java
public class TDD {
    private final List<CommissionTier> tiers;

    public TDD() {
        // Initialize with same values as before
        this.tiers = initializeDefaultTiers();
    }

    private List<CommissionTier> initializeDefaultTiers() {
        List<CommissionTier> defaultTiers = new ArrayList<>();
        defaultTiers.add(new CommissionTier(
            new BigDecimal("50000"), new BigDecimal("0.08")));
        defaultTiers.add(new CommissionTier(
            new BigDecimal("100000"), new BigDecimal("0.10")));
        defaultTiers.add(new CommissionTier(
            null, new BigDecimal("0.12"))); // Unlimited
        return defaultTiers;
    }
}
```

**Run Tests: ✅ GREEN** (Still same behavior)

**Step 3: Refactor calculateTieredCommission to Use Tiers**

```java
public BigDecimal calculateTieredCommission(BigDecimal dealValue) {
    if (dealValue == null || dealValue.compareTo(BigDecimal.ZERO) < 0) {
        throw new IllegalArgumentException("Deal value cannot be null or negative");
    }

    incrementCalculationCount();

    // Find applicable tier
    BigDecimal rate = findApplicableTierRate(dealValue);

    return dealValue.multiply(rate).setScale(2, RoundingMode.HALF_UP);
}

private BigDecimal findApplicableTierRate(BigDecimal dealValue) {
    for (CommissionTier tier : tiers) {
        if (tier.applies(dealValue)) {
            return tier.getRate();
        }
    }

    // Fallback (should never reach here with proper config)
    throw new IllegalStateException("No applicable tier found for value: " + dealValue);
}
```

**Run Tests: ✅ GREEN** (Behavior unchanged, structure improved)

**Step 4: Add Constructor for Custom Tiers (Flexible)**

```java
public TDD() {
    this(null);  // Use default tiers
}

public TDD(List<CommissionTier> customTiers) {
    this.tiers = (customTiers != null)
        ? new ArrayList<>(customTiers)
        : initializeDefaultTiers();
}
```

**Run Tests: ✅ GREEN** (Default behavior unchanged)

**Step 5: Add Test for Custom Tiers**

```java
@Test
@DisplayName("Custom tier configuration works correctly")
void customTiers_calculateCorrectly() {
    // ARRANGE: Create custom tiers
    List<CommissionTier> customTiers = Arrays.asList(
        new CommissionTier(new BigDecimal("25000"), new BigDecimal("0.05")),  // 5%
        new CommissionTier(new BigDecimal("75000"), new BigDecimal("0.08")),  // 8%
        new CommissionTier(null, new BigDecimal("0.10"))  // 10%
    );

    TDD customService = new TDD(customTiers);

    // ACT & ASSERT
    assertEquals(new BigDecimal("1250.00"),  // 25000 * 0.05
        customService.calculateTieredCommission(new BigDecimal("25000")).setScale(2));

    assertEquals(new BigDecimal("4000.00"),  // 50000 * 0.08
        customService.calculateTieredCommission(new BigDecimal("50000")).setScale(2));

    assertEquals(new BigDecimal("10000.00"),  // 100000 * 0.10
        customService.calculateTieredCommission(new BigDecimal("100000")).setScale(2));
}
```

**Run Tests: ✅ GREEN** (All tests pass!)

**Benefits of This Refactoring:**
1. **Flexibility**: Can configure different tier structures
2. **Testability**: Can test with custom tiers
3. **Maintainability**: Tier logic separated
4. **Open/Closed**: Open for extension (custom tiers), closed for modification
5. **Safety**: All tests stayed green throughout

**All existing tests still pass** because default behavior is preserved!

### Answer 39: Error Messages

Improving error messages using TDD:

**Current State:**
```java
throw new IllegalArgumentException("Deal value cannot be null or negative");
```

**TDD Approach to Better Error Messages:**

**RED Phase - Test for Helpful Error Messages:**

```java
@Test
@DisplayName("Null deal value provides specific error message")
void nullDealValue_providesSpecificErrorMessage() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> commissionService.calculateTieredCommission(null)
    );

    assertThat(exception.getMessage())
        .contains("Deal value")
        .contains("null")
        .contains("cannot be");
}

@Test
@DisplayName("Negative deal value provides actionable error message")
void negativeDealValue_providesActionableErrorMessage() {
    BigDecimal negativeValue = new BigDecimal("-5000");

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> commissionService.calculateTieredCommission(negativeValue)
    );

    assertThat(exception.getMessage())
        .contains("Deal value")
        .contains("-5000")  // Shows actual value
        .contains("cannot be negative")
        .contains("must be greater than or equal to zero");  // Actionable
}

@Test
@DisplayName("Ineligible deal provides reason in error message")
void ineligibleDeal_providesReasonInErrorMessage() {
    Deal openDeal = new Deal("Test", BigDecimal.TEN, "REP-001");
    openDeal.setStatus(DealStatus.OPEN);  // Not closed

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> commissionService.calculateFullCommission(openDeal, BigDecimal.ZERO)
    );

    assertThat(exception.getMessage())
        .contains("not eligible")
        .contains("OPEN")  // Shows actual status
        .contains("must be CLOSED");  // Explains requirement
}
```

**Status: ❌ RED** (Current messages don't meet requirements)

**GREEN Phase - Improve Error Messages:**

```java
public BigDecimal calculateTieredCommission(BigDecimal dealValue) {
    // Improved validation with specific messages
    if (dealValue == null) {
        throw new IllegalArgumentException(
            "Deal value cannot be null. Please provide a valid deal value.");
    }

    if (dealValue.compareTo(BigDecimal.ZERO) < 0) {
        throw new IllegalArgumentException(
            String.format(
                "Deal value cannot be negative. Provided value: %s. " +
                "Deal value must be greater than or equal to zero.",
                dealValue
            )
        );
    }

    incrementCalculationCount();
    BigDecimal rate = findApplicableTierRate(dealValue);
    return dealValue.multiply(rate).setScale(2, RoundingMode.HALF_UP);
}

public BigDecimal calculateFullCommission(Deal deal, BigDecimal bonusRate) {
    if (deal == null) {
        throw new IllegalArgumentException(
            "Deal cannot be null. Please provide a valid deal for commission calculation.");
    }

    if (!isDealEligibleForCommission(deal)) {
        // Build detailed message explaining why
        String reason = buildIneligibilityReason(deal);
        throw new IllegalArgumentException(
            String.format(
                "Deal '%s' (ID: %s) is not eligible for commission. %s",
                deal.getTitle(),
                deal.getId(),
                reason
            )
        );
    }

    // Rest of implementation...
}

private String buildIneligibilityReason(Deal deal) {
    if (deal.getStatus() != DealStatus.CLOSED) {
        return String.format(
            "Deal status is %s but must be CLOSED to be eligible for commission.",
            deal.getStatus()
        );
    }

    if (deal.getCloseDate() == null) {
        return "Deal does not have a close date. Please set a close date.";
    }

    LocalDate cutoff = LocalDate.now().minusDays(COMMISSION_ELIGIBILITY_DAYS);
    if (deal.getCloseDate().isBefore(cutoff)) {
        return String.format(
            "Deal was closed on %s, which is more than %d days ago. " +
            "Only deals closed within the last %d days are eligible.",
            deal.getCloseDate(),
            COMMISSION_ELIGIBILITY_DAYS,
            COMMISSION_ELIGIBILITY_DAYS
        );
    }

    return "Unknown eligibility issue.";
}
```

**Status: ✅ GREEN** (Tests pass with helpful messages!)

**Error Message Best Practices (Demonstrated):**

1. **What went wrong**: "Deal value cannot be negative"
2. **Actual value**: "Provided value: -5000"
3. **What's expected**: "must be greater than or equal to zero"
4. **Actionable**: "Please provide a valid deal value"
5. **Context**: "Deal 'Enterprise Sale' (ID: DEAL-001)"

**Benefits:**
- Faster debugging for developers
- Better user experience
- Self-documenting validation rules
- TDD ensures messages are tested

### Answer 40: Performance Testing

Adding performance requirements using TDD:

**Requirement:** "Commission calculation must complete in under 10ms."

**RED Phase - Write Performance Test:**

```java
@Test
@DisplayName("Commission calculation completes within 10ms")
void commissionCalculation_completesWithin10ms() {
    // ARRANGE
    Deal deal = new Deal("Performance Test",
        new BigDecimal("100000"), "REP-001");
    deal.setStatus(DealStatus.CLOSED);
    deal.setCloseDate(LocalDate.now());

    // Warm up (JVM optimization)
    for (int i = 0; i < 100; i++) {
        commissionService.calculateFullCommission(deal, new BigDecimal("0.05"));
    }

    // ACT - Measure performance
    long startTime = System.nanoTime();

    BigDecimal result = commissionService.calculateFullCommission(
        deal, new BigDecimal("0.05"));

    long endTime = System.nanoTime();
    long durationMs = (endTime - startTime) / 1_000_000;

    // ASSERT
    assertNotNull(result, "Calculation should return a result");
    assertTrue(durationMs < 10,
        String.format("Calculation took %dms, must be under 10ms", durationMs));
}

@Test
@DisplayName("Bulk calculation performance is acceptable")
void bulkCalculation_meetsPerformanceTarget() {
    // ARRANGE
    List<Deal> deals = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
        Deal deal = new Deal("Deal " + i,
            new BigDecimal("100000"), "REP-001");
        deal.setStatus(DealStatus.CLOSED);
        deal.setCloseDate(LocalDate.now());
        deals.add(deal);
    }

    // ACT
    long startTime = System.currentTimeMillis();

    for (Deal deal : deals) {
        commissionService.calculateFullCommission(deal, BigDecimal.ZERO);
    }

    long endTime = System.currentTimeMillis();
    long totalDuration = endTime - startTime;

    // ASSERT
    // 1000 calculations should complete in under 1 second
    assertTrue(totalDuration < 1000,
        String.format("1000 calculations took %dms, should be under 1000ms",
            totalDuration));
}

@Test
@DisplayName("Performance doesn't degrade with calculation count")
void performance_doesntDegradeWithCount() {
    Deal deal = new Deal("Test", new BigDecimal("100000"), "REP-001");
    deal.setStatus(DealStatus.CLOSED);
    deal.setCloseDate(LocalDate.now());

    // Measure first 100 calculations
    long start1 = System.nanoTime();
    for (int i = 0; i < 100; i++) {
        commissionService.calculateFullCommission(deal, BigDecimal.ZERO);
    }
    long duration1 = System.nanoTime() - start1;

    // Measure next 100 calculations
    long start2 = System.nanoTime();
    for (int i = 0; i < 100; i++) {
        commissionService.calculateFullCommission(deal, BigDecimal.ZERO);
    }
    long duration2 = System.nanoTime() - start2;

    // ASSERT: Second batch shouldn't be significantly slower
    assertTrue(duration2 < duration1 * 1.5,
        "Performance should not degrade significantly");
}
```

**Status**: Depends on implementation performance

**GREEN Phase - Optimize if Needed:**

If tests fail, optimize:

```java
// Cache tier rate lookups
private final Map<BigDecimal, BigDecimal> tierCache = new HashMap<>();

private BigDecimal findApplicableTierRate(BigDecimal dealValue) {
    // Check cache first
    BigDecimal cached = tierCache.get(dealValue);
    if (cached != null) {
        return cached;
    }

    // Find tier
    for (CommissionTier tier : tiers) {
        if (tier.applies(dealValue)) {
            BigDecimal rate = tier.getRate();
            tierCache.put(dealValue, rate);  // Cache for next time
            return rate;
        }
    }

    throw new IllegalStateException("No applicable tier found");
}
```

**REFACTOR Phase - Balance Performance and Readability:**

```java
// Add performance monitoring
public BigDecimal calculateFullCommission(Deal deal, BigDecimal bonusRate) {
    long startTime = System.nanoTime();

    try {
        // Existing implementation
        // ...
        return result;
    } finally {
        long duration = System.nanoTime() - startTime;
        if (duration > 10_000_000) {  // 10ms in nanoseconds
            logger.warn("Slow calculation: {}ms for deal {}",
                duration / 1_000_000, deal.getId());
        }
    }
}
```

**Performance Testing Best Practices:**

1. **Warm-up JVM**: Run code multiple times before measuring
2. **Realistic Data**: Use production-like data volumes
3. **Statistical Significance**: Multiple runs, average results
4. **Environment Control**: Consistent test environment
5. **Regression Detection**: Track performance over time
6. **Profiling**: Use tools when optimizing

---

## Conclusion

These answers demonstrate comprehensive understanding of TDD concepts, from fundamental principles to advanced applications. The key takeaway is that TDD is not just a testing practice—it's a design methodology that leads to better, more maintainable software.

Practice these concepts, and TDD will become a natural and productive way to develop software.