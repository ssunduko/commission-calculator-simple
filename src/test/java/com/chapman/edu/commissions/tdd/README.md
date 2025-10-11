# Test-Driven Development (TDD) Demonstration

## Overview

This package demonstrates **Test-Driven Development (TDD)**, a software development methodology where tests are written **before** the implementation code. The TDD approach emphasizes the RED-GREEN-REFACTOR cycle, which ensures that code is thoroughly tested, well-designed, and maintainable.

## What is TDD?

**Test-Driven Development (TDD)** is a software development process where you:

1. Write a test for new functionality **before** writing the code
2. Watch the test **fail** (RED phase)
3. Write **minimal** code to make the test **pass** (GREEN phase)
4. **Refactor** the code to improve quality while keeping tests passing (REFACTOR phase)
5. Repeat the cycle for the next piece of functionality

### The TDD Mantra

> **RED → GREEN → REFACTOR**

## The TDD Cycle Explained

### 1. RED Phase: Write a Failing Test

- Write a test that describes the desired behavior
- Run the test and watch it fail
- A failing test proves:
  - The test can detect problems
  - The feature doesn't exist yet
  - The test is actually running

**Example from this implementation:**
```java
@Test
void testBasicCommissionCalculation_RedPhase() {
    BigDecimal actualCommission = commissionService.calculateBasicCommission(
        new BigDecimal("100000"), new BigDecimal("0.10"));
    assertEquals(new BigDecimal("10000.00"), actualCommission.setScale(2));
}
```

When first written, this test fails because `calculateBasicCommission()` doesn't exist yet.

### 2. GREEN Phase: Make It Pass

- Write the **minimum** code needed to make the test pass
- Don't worry about perfection
- Don't add features not covered by tests
- Goal: Get to a passing test as quickly as possible

**Example from this implementation:**
```java
public BigDecimal calculateBasicCommission(BigDecimal dealValue, BigDecimal rate) {
    incrementCalculationCount();
    BigDecimal commission = dealValue.multiply(rate);
    return commission.setScale(2, RoundingMode.HALF_UP);
}
```

Simple multiplication makes the test pass. Nothing more, nothing less.

### 3. REFACTOR Phase: Improve the Code

- Clean up the code without changing behavior
- Remove duplication
- Improve naming and structure
- Optimize performance
- Run tests continuously to ensure nothing breaks

**Example from this implementation:**
```java
// Before refactoring: Magic numbers
if (dealValue.compareTo(new BigDecimal("50000")) <= 0) {
    rate = new BigDecimal("0.08");
}

// After refactoring: Named constants
final BigDecimal TIER_1_MAX = new BigDecimal("50000");
final BigDecimal TIER_1_RATE = new BigDecimal("0.08");
if (dealValue.compareTo(TIER_1_MAX) <= 0) {
    rate = TIER_1_RATE;
}
```

Tests remain green throughout refactoring, proving nothing broke.

## Files in This Package

### 1. `TDDTest.java`

The test class that demonstrates the TDD workflow. Contains:

- **9 test methods** following the RED-GREEN-REFACTOR cycle
- Tests for each TDD cycle:
  - Cycle 1: Basic commission calculation
  - Cycle 2: Deal validation
  - Cycle 3: Tiered commission rates
  - Cycle 4: Bonus calculations
  - Cycle 5: Full pipeline integration
  - Cycle 6: Edge cases and validation
  - Cycle 7: Calculation tracking
  - Cycle 8: Refactoring verification
  - Cycle 9: FIRST principles demonstration

- Tests are ordered to show progression through TDD cycles
- Each test demonstrates a specific TDD concept

### 2. `TDD.java`

The implementation class built using TDD methodology. Contains:

- **Commission calculation methods** developed through TDD cycles
- **Extensive inline documentation** explaining TDD concepts
- **Step-by-step commentary** on RED-GREEN-REFACTOR phases
- **Refactoring examples** showing code improvement
- Methods include:
  - `calculateBasicCommission()` - Simple percentage-based commission
  - `isDealEligibleForCommission()` - Business rule validation
  - `calculateTieredCommission()` - Progressive rate tiers
  - `calculateCommissionWithBonus()` - Bonus application
  - `calculateFullCommission()` - Complete pipeline integration
  - `getCalculationCount()` - Observability feature

### 3. `tdd-cycle.puml`

A PlantUML diagram visualizing:
- The RED-GREEN-REFACTOR cycle
- Class structure and relationships
- TDD workflow steps
- Component interactions

### 4. `README.md` (This File)

Comprehensive documentation explaining:
- What TDD is and why it matters
- The RED-GREEN-REFACTOR cycle
- Benefits and challenges
- How to use this demonstration

### 5. `QUESTIONS.md`

Thought-provoking questions about TDD concepts to reinforce learning.

### 6. `ANSWERS.md`

Detailed answers to the questions with explanations and examples.

## Key TDD Principles

### FIRST Principles

TDD tests should follow **FIRST** principles:

- **F - Fast**: Tests run quickly (milliseconds, not seconds)
- **I - Independent**: Tests don't depend on each other
- **R - Repeatable**: Same results every time
- **S - Self-Validating**: Clear pass/fail, no manual checking
- **T - Timely**: Written at the right time (before implementation)

### The Three Rules of TDD

1. **Don't write production code** until you have a failing test
2. **Don't write more of a test** than is sufficient to fail
3. **Don't write more production code** than necessary to pass the test

## Benefits of TDD

### 1. Better Design
- Forces you to think about interfaces before implementation
- Leads to more modular, loosely-coupled code
- Encourages separation of concerns

### 2. Higher Code Quality
- Bugs are caught early when they're cheapest to fix
- Comprehensive test coverage by default
- Less debugging time

### 3. Living Documentation
- Tests document how code should be used
- Examples of every feature in action
- Always up-to-date (tests must pass)

### 4. Refactoring Confidence
- Tests act as a safety net
- Can improve code without fear
- Regressions are caught immediately

### 5. Reduced Debugging
- Issues found during development, not production
- Clear indication of what broke and when
- Fast feedback loop

### 6. Incremental Development
- Build features one small piece at a time
- Always have working code
- Easy to track progress

## TDD in Practice: This Implementation

This implementation demonstrates TDD using a commission calculation system:

### Cycle 1: Basic Commission
- **RED**: Write test for 10% commission calculation
- **GREEN**: Implement simple multiplication
- **REFACTOR**: Add proper scaling and rounding

### Cycle 2: Validation
- **RED**: Write test requiring deal validation
- **GREEN**: Implement status checking
- **REFACTOR**: Add null handling

### Cycle 3: Tiered Rates
- **RED**: Write test for progressive rate tiers
- **GREEN**: Implement if-else tier logic
- **REFACTOR**: Extract magic numbers to constants

### Cycle 4: Bonuses
- **RED**: Write test for bonus calculation
- **GREEN**: Implement bonus multiplication
- **REFACTOR**: Ensure proper decimal handling

### Cycle 5: Pipeline Integration
- **RED**: Write test combining all features
- **GREEN**: Compose existing methods
- **REFACTOR**: Add comprehensive error handling

### Cycle 6: Edge Cases
- **RED**: Write tests for boundary conditions
- **GREEN**: Add validation and guards
- **REFACTOR**: Improve error messages

### Cycle 7: Observability
- **RED**: Write test for calculation tracking
- **GREEN**: Add counter field and methods
- **REFACTOR**: Extract increment to helper method

## Running the Tests

### Run All TDD Tests

```bash
mvn test -Dtest=TDDTest
```

### Run Specific Test Cycle

```bash
mvn test -Dtest=TDDTest#testBasicCommissionCalculation_RedPhase
```

### Run with Verbose Output

```bash
mvn test -Dtest=TDDTest -Dorg.slf4j.simpleLogger.defaultLogLevel=debug
```

### Expected Output

All tests should pass:
```
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
```

## Understanding the Code Flow

### Step 1: Read the Tests First
Start with `TDDTest.java`. Each test method shows:
- What behavior is expected
- How the API should be used
- What edge cases are handled

### Step 2: Follow the TDD Cycles
Tests are ordered to show progression:
1. Simple functionality first
2. Complexity added incrementally
3. Integration comes last

### Step 3: Examine the Implementation
In `TDD.java`, see how:
- Each test drives implementation
- Code evolves through refactoring
- Comments explain TDD decisions

### Step 4: Review the Refactoring
Notice how code improves while tests stay green:
- Magic numbers → Named constants
- Duplication → Helper methods
- Comments → Better naming

## Comparison: TDD vs Traditional Development

| Aspect | Traditional | TDD |
|--------|-------------|-----|
| **Test Writing** | After implementation | Before implementation |
| **Design** | Code-first | Test-first (drives design) |
| **Bug Detection** | Late (QA/Production) | Early (during development) |
| **Refactoring** | Risky | Safe (protected by tests) |
| **Documentation** | Separate (often outdated) | Tests (always current) |
| **Coverage** | Variable (often incomplete) | High (tests written first) |
| **Confidence** | Lower | Higher |
| **Debug Time** | More | Less |
| **Initial Speed** | Faster | Slower |
| **Long-term Speed** | Slower (more bugs) | Faster (fewer bugs) |

## Common TDD Challenges

### 1. Learning Curve
- TDD feels unnatural at first
- Requires discipline to write tests first
- **Solution**: Practice on small projects, use pair programming

### 2. Initial Slowdown
- Writing tests first seems slower initially
- **Solution**: Long-term benefits outweigh short-term cost

### 3. Test Maintenance
- Tests need maintenance like production code
- **Solution**: Treat tests as first-class citizens, refactor them too

### 4. Over-Testing
- Risk of testing trivial code
- **Solution**: Focus on behavior, not implementation details

### 5. Legacy Code
- Hard to apply TDD to existing code without tests
- **Solution**: Add tests before changing code, refactor gradually

## When to Use TDD

### Ideal For:
- ✅ Business logic and algorithms
- ✅ APIs and interfaces
- ✅ Data transformations
- ✅ Complex calculations
- ✅ Critical system components
- ✅ Libraries and frameworks

### Less Ideal For:
- ❌ UI/UX exploration (requirements unclear)
- ❌ Prototypes and spike solutions
- ❌ Simple CRUD operations
- ❌ Glue code with minimal logic
- ❌ Experiments and throwaway code

## TDD Best Practices

1. **Write the test you wish you had**: Design the API through tests
2. **Start simple**: Begin with the simplest possible test
3. **One test at a time**: Complete RED-GREEN-REFACTOR before next test
4. **Test behavior, not implementation**: Focus on what, not how
5. **Keep tests independent**: Tests shouldn't depend on each other
6. **Refactor continuously**: Don't let technical debt accumulate
7. **Run tests frequently**: Get immediate feedback
8. **Commit on green**: Only commit when tests pass

## Learning Path

### Beginner
1. Read this README thoroughly
2. Run the tests and see them pass
3. Read `TDDTest.java` to understand test structure
4. Read `TDD.java` to see implementation

### Intermediate
5. Answer questions in `QUESTIONS.md`
6. Check your answers against `ANSWERS.md`
7. Try modifying tests and watch implementation change
8. Practice RED-GREEN-REFACTOR on a small feature

### Advanced
9. Add a new commission rule using TDD
10. Refactor existing code using TDD as safety net
11. Apply TDD to another domain in this codebase
12. Teach TDD to someone else

## Additional Resources

### Books
- "Test Driven Development: By Example" by Kent Beck
- "Growing Object-Oriented Software, Guided by Tests" by Freeman & Pryce

### Online Resources
- [Martin Fowler on TDD](https://martinfowler.com/bliki/TestDrivenDevelopment.html)
- [Uncle Bob's Three Rules of TDD](http://butunclebob.com/ArticleS.UncleBob.TheThreeRulesOfTdd)

### Related Concepts in This Codebase
- `/fundamentals/` - JUnit 5 testing fundamentals
- `/patterns/` - Testing patterns and best practices
- `/bdd/` - Behavior-Driven Development examples

## Key Takeaways

1. **TDD is a design methodology**, not just a testing practice
2. **Tests are written first** to drive implementation
3. **RED-GREEN-REFACTOR** is the core cycle
4. **Small steps** lead to robust systems
5. **Refactoring is safe** when protected by tests
6. **Tests are documentation** that never goes stale
7. **Quality is built in**, not tested in
8. **Confidence increases** as test coverage grows

## Conclusion

Test-Driven Development transforms how we write software. By writing tests first, we:
- Design better interfaces
- Catch bugs earlier
- Refactor with confidence
- Document our code
- Deliver higher quality

This implementation demonstrates TDD principles applied to real-world commission calculations. The code is intentionally over-commented to serve as a teaching tool.

Practice the RED-GREEN-REFACTOR cycle, and TDD will become a natural, productive way to develop software.

---

**Remember**: TDD is not about testing. It's about design, quality, and confidence.

**Start simple. Test first. Keep it green. Refactor continuously.**