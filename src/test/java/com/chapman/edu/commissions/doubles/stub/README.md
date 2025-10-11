# Test Doubles - Stub Pattern

## Overview

This module demonstrates the **Stub** pattern, a test double that provides predetermined responses to method calls. Stubs return hardcoded values without business logic, making them perfect for controlling test inputs and isolating code from dependencies.

## What is a Stub?

A **Stub** is a test double that:

1. **Returns predetermined values** - Hardcoded responses, not calculations
2. **Has no business logic** - Simple return statements
3. **Is stateless** (usually) - Same input → same output
4. **Provides test data** - Controls what the code under test receives
5. **Doesn't verify calls** - Just returns values (use Mock for verification)

Think of a stub as a "canned answer machine" - ask it anything, get the same predetermined response.

## Stub in the Test Double Hierarchy

```
Test Double Types (by complexity):

Dummy    ← Passed but never used
  ↓
Stub     ← Returns predetermined values ← YOU ARE HERE
  ↓
Spy      ← Records calls + delegates to real
  ↓
Mock     ← Verifies expectations
  ↓
Fake     ← Working implementation (simplified)
```

## When to Use Stubs

### ✓ Good Use Cases

**1. Isolating Code from Dependencies**
```java
// Test business logic without real database
StubUserRepository stubRepo = new StubUserRepository(testUser);
UserService service = new UserService(stubRepo);
// Service uses stub, not real database
```

**2. Controlling Test Inputs**
```java
// Always return 10% commission rate
StubCommissionCalculator stubCalc = new StubCommissionCalculator(new BigDecimal("0.10"));
BigDecimal commission = stubCalc.calculateCommission(deal);
assertEquals(expectedValue, commission);
```

**3. Simulating Various Scenarios**
```java
// Test success scenario
StubEmailService successStub = new StubEmailService(true);

// Test failure scenario
StubEmailService failureStub = new StubEmailService(false);
```

**4. Testing Error Handling**
```java
// Stub that throws exception
ExceptionStubRepository errorStub = new ExceptionStubRepository();
assertThrows(RuntimeException.class, () -> errorStub.findById("ERROR"));
```

**5. Date/Time Testing**
```java
// Fixed date for deterministic tests
StubDateProvider stubDate = new StubDateProvider(LocalDate.of(2024, 1, 15));
assertTrue(plan.isActiveOn(stubDate.getCurrentDate()));
```

### ✗ When NOT to Use Stubs

**1. Need to Verify Method Calls**
- Use Mock if you need to verify interactions
- Stubs don't track calls

**2. Need Working Implementation**
- Use Fake if you need real logic
- Stubs only return values

**3. Object Never Used**
- Use Dummy if parameter is ignored
- Stubs are for when you need specific return values

**4. Complex Business Logic**
- Use real object or Fake
- Stubs should be simple

## Core Concepts Demonstrated

### 1. Basic Stub

Returns same hardcoded value regardless of input:

```java
static class StubUserRepository {
    private final User stubUser;

    public StubUserRepository(User stubUser) {
        this.stubUser = stubUser;
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(stubUser);  // Always returns same user
    }
}
```

**Usage:**
```java
User testUser = new User("jdoe", "john@example.com", "John", "Doe");
StubUserRepository stubRepo = new StubUserRepository(testUser);

// ID doesn't matter - always returns same user
Optional<User> result1 = stubRepo.findById("USER-1");
Optional<User> result2 = stubRepo.findById("USER-999");
// Both return testUser
```

### 2. Conditional Stub

Different returns based on input parameters:

```java
static class ConditionalStubDealRepository {
    public Optional<Deal> findById(String id) {
        if ("DEAL-1".equals(id)) {
            return Optional.of(createDeal("Small Deal", "10000"));
        } else if ("DEAL-2".equals(id)) {
            return Optional.of(createDeal("Large Deal", "100000"));
        }
        return Optional.empty();
    }
}
```

**Usage:**
```java
ConditionalStubDealRepository stub = new ConditionalStubDealRepository();

Optional<Deal> small = stub.findById("DEAL-1");
assertEquals("Small Deal", small.get().getTitle());

Optional<Deal> large = stub.findById("DEAL-2");
assertEquals("Large Deal", large.get().getTitle());
```

### 3. Exception Stub

Simulates error conditions:

```java
static class ExceptionStubUserRepository {
    public Optional<User> findById(String id) {
        if ("ERROR".equals(id)) {
            throw new RuntimeException("Database connection failed");
        }
        return Optional.of(new User("test", "test@test.com", "Test", "User"));
    }
}
```

**Usage:**
```java
ExceptionStubUserRepository stub = new ExceptionStubUserRepository();

// Test error handling
RuntimeException ex = assertThrows(RuntimeException.class, () -> {
    stub.findById("ERROR");
});
assertTrue(ex.getMessage().contains("Database connection failed"));
```

### 4. Stateful Stub

Returns different values on successive calls:

```java
static class StatefulStubNotificationService {
    private int callCount = 0;
    private final List<Boolean> returnSequence;

    public StatefulStubNotificationService(List<Boolean> returnSequence) {
        this.returnSequence = returnSequence;
    }

    public boolean sendEmail(String to, String subject, String message) {
        return returnSequence.get(callCount++);
    }
}
```

**Usage:**
```java
// Fails twice, then succeeds
StatefulStubNotificationService stub =
    new StatefulStubNotificationService(List.of(false, false, true));

assertFalse(stub.sendEmail("user@test.com", "Test", "Body"));  // Fail
assertFalse(stub.sendEmail("user@test.com", "Test", "Body"));  // Fail
assertTrue(stub.sendEmail("user@test.com", "Test", "Body"));   // Success
```

### 5. Calculation Stub

Fixed calculation results:

```java
static class StubCommissionCalculator {
    private final BigDecimal fixedRate;

    public StubCommissionCalculator(BigDecimal fixedRate) {
        this.fixedRate = fixedRate;
    }

    public BigDecimal calculateCommission(Deal deal) {
        return deal.getValue().multiply(fixedRate);
    }
}
```

**Usage:**
```java
// 10% commission rate
StubCommissionCalculator stub = new StubCommissionCalculator(new BigDecimal("0.10"));

Deal deal = new Deal("Test", new BigDecimal("100000"), "USER-1");
BigDecimal commission = stub.calculateCommission(deal);

assertEquals(new BigDecimal("10000.00"), commission);
```

### 6. Validation Stub

Predetermined validation results:

```java
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
```

**Usage:**
```java
// Always valid
StubDealValidator validStub = new StubDealValidator(true);
assertTrue(validStub.validate(deal));

// Always invalid with errors
StubDealValidator invalidStub = new StubDealValidator(
    false,
    "Deal value too low",
    "Missing sales rep"
);
assertFalse(invalidStub.validate(deal));
assertEquals(2, invalidStub.getValidationErrors(deal).size());
```

### 7. Date Provider Stub

Fixed dates for testing:

```java
static class StubDateProvider {
    private final LocalDate fixedDate;

    public StubDateProvider(LocalDate fixedDate) {
        this.fixedDate = fixedDate;
    }

    public LocalDate getCurrentDate() {
        return fixedDate;
    }
}
```

**Usage:**
```java
StubDateProvider stubDate = new StubDateProvider(LocalDate.of(2024, 1, 15));

LocalDate current = stubDate.getCurrentDate();
assertEquals(LocalDate.of(2024, 1, 15), current);

// Test date-dependent logic
assertTrue(plan.isActiveOn(stubDate.getCurrentDate()));
```

## Real-World Scenarios

### Scenario 1: Deal Approval Workflow

Testing approval process with stubbed dependencies:

```java
@Test
void testDealApprovalWorkflow() {
    // Arrange stubs
    Deal deal = new Deal("Big Deal", new BigDecimal("150000"), "USER-1");
    StubDealValidator stubValidator = new StubDealValidator(true);
    StubEmailService stubEmailService = new StubEmailService(true);

    // Act
    boolean isValid = stubValidator.validate(deal);
    if (isValid) {
        deal.setStatus(DealStatus.WON);
        stubEmailService.sendEmail("manager@test.com", "Deal Approved", "...");
    }

    // Assert
    assertEquals(DealStatus.WON, deal.getStatus());
}
```

### Scenario 2: Commission Calculation Pipeline

Multiple stubs working together:

```java
@Test
void testCommissionCalculationPipeline() {
    Deal deal = new Deal("Enterprise Deal", new BigDecimal("500000"), "USER-1");

    // Stub dependencies
    StubDealValidator stubValidator = new StubDealValidator(true);
    StubCommissionCalculator stubCalc = new StubCommissionCalculator(new BigDecimal("0.12"));
    StubEmailService stubEmailService = new StubEmailService(true);

    // Run pipeline
    if (stubValidator.validate(deal)) {
        BigDecimal commission = stubCalc.calculateCommission(deal);  // $60,000
        stubEmailService.sendEmail("finance@test.com", "Commission Ready", "...");
    }

    // All steps completed with predictable results
}
```

### Scenario 3: Retry Logic Testing

Using stateful stub to test retry mechanism:

```java
@Test
void testRetryLogicWithStubs() {
    // Fails twice, succeeds on third try
    StatefulStubNotificationService stub =
        new StatefulStubNotificationService(List.of(false, false, true));

    boolean success = false;
    int maxAttempts = 3;

    for (int i = 0; i < maxAttempts; i++) {
        if (stub.sendEmail("user@test.com", "Test", "Body")) {
            success = true;
            break;
        }
    }

    assertTrue(success);  // Eventually succeeded
}
```

### Scenario 4: Date-Sensitive Business Rules

Testing with fixed dates:

```java
@Test
void testDateSensitiveRules() {
    CommissionPlan plan = new CommissionPlan("Q1 2024", Currency.getInstance("USD"));
    plan.setEffectiveStartDate(LocalDate.of(2024, 1, 1));
    plan.setEffectiveEndDate(LocalDate.of(2024, 3, 31));
    plan.setStatus(PlanStatus.ACTIVE);

    // Test with different dates
    StubDateProvider earlyDate = new StubDateProvider(LocalDate.of(2023, 12, 31));
    StubDateProvider validDate = new StubDateProvider(LocalDate.of(2024, 2, 15));
    StubDateProvider lateDate = new StubDateProvider(LocalDate.of(2024, 4, 1));

    assertFalse(plan.isActiveOn(earlyDate.getCurrentDate()));  // Too early
    assertTrue(plan.isActiveOn(validDate.getCurrentDate()));   // Valid
    assertFalse(plan.isActiveOn(lateDate.getCurrentDate()));   // Too late
}
```

## Stub vs Other Test Doubles

| Test Double | Purpose | Returns Values | Has Logic | Verifies Calls | Example |
|-------------|---------|----------------|-----------|----------------|---------|
| **Dummy** | Fill parameters | ❌ Never used | ❌ None | ❌ No | `new User()` passed but ignored |
| **Stub** | Provide data | ✅ Hardcoded | ❌ Minimal | ❌ No | Always returns `true` |
| **Spy** | Partial mock | ✅ Real or stubbed | ✅ Real | ✅ Optional | `spy(realObject)` |
| **Mock** | Verify behavior | ✅ Stubbed | ❌ None | ✅ Yes | `verify(mock).method()` |
| **Fake** | Working impl | ✅ Calculated | ✅ Simplified | ❌ No | HashMap instead of DB |

## Best Practices

### DO:

✅ **Keep stubs simple** - Just return values, no complex logic
```java
// ✓ GOOD - Simple return
static class StubEmailService {
    public boolean sendEmail(...) {
        return true;  // Hardcoded
    }
}
```

✅ **Create scenario-specific stubs** - Different stub for each test case
```java
StubEmailService successStub = new StubEmailService(true);
StubEmailService failureStub = new StubEmailService(false);
```

✅ **Use stubs to control inputs** - Isolate code under test
```java
// Test with 5% commission
testWithRate(new BigDecimal("0.05"));

// Test with 15% commission
testWithRate(new BigDecimal("0.15"));
```

✅ **Make stub data realistic** - Values should match production
```java
User realisticUser = new User("jdoe", "john.doe@company.com", "John", "Doe");
```

✅ **Use for testing error paths** - Exception stubs test error handling
```java
ExceptionStubRepository errorStub = new ExceptionStubRepository();
// Test error recovery logic
```

### DON'T:

❌ **Add complex logic** - That's a Fake, not a Stub
```java
// ✗ WRONG - Too much logic for a stub
public Optional<User> findById(String id) {
    // Complex query logic, filtering, joining...
    // Use a Fake instead
}

// ✓ RIGHT - Simple return
public Optional<User> findById(String id) {
    return Optional.of(predefinedUser);
}
```

❌ **Use stubs to verify calls** - Use Mock for verification
```java
// ✗ WRONG - Stubs don't verify
StubEmailService stub = new StubEmailService(true);
stub.sendEmail(...);
// Can't verify it was called - use Mock instead

// ✓ RIGHT - Just test the return value
boolean sent = stub.sendEmail(...);
assertTrue(sent);
```

❌ **Make stubs stateful unless necessary** - Usually keep them simple
```java
// Only use stateful stubs when testing retry/pagination logic
// Most stubs should be stateless
```

❌ **Return unrealistic data** - Can hide bugs
```java
// ✗ BAD - Inconsistent data
User badStub = new User("test", "production@real.com", "Test", "User");

// ✓ GOOD - Realistic data
User goodStub = new User("jdoe", "john.doe@test.com", "John", "Doe");
```

## Test Structure

### StubTest.java

The test class demonstrates all stub concepts:

1. **Basic Stub Usage** (3 tests)
   - Hardcoded return value
   - Returns empty
   - Returns collection

2. **Conditional Stubs** (2 tests)
   - Varies by input
   - By status

3. **Exception Stubs** (2 tests)
   - Throws exception
   - Error recovery

4. **Stateful Stubs** (2 tests)
   - Changes over time
   - Retry mechanism

5. **Calculation Stubs** (2 tests)
   - Fixed commission rate
   - Multiple scenarios

6. **Validation Stubs** (2 tests)
   - Always valid
   - With errors

7. **Date/Time Stubs** (2 tests)
   - Fixed date
   - Commission plan validity

8. **Real-World Scenarios** (5 tests)
   - Deal approval workflow
   - Commission calculation pipeline
   - Error handling
   - Date-sensitive rules
   - Comprehensive validation

9. **Best Practices** (3 tests)
   - Simple stubs
   - Scenario-specific stubs
   - Stubs don't verify

10. **Common Pitfalls** (3 tests)
    - Overly complex stubs
    - Stub vs Mock confusion
    - Unrealistic stub data

### Stub Implementations Included

1. **StubUserRepository** - Basic repository stub
2. **ConditionalStubDealRepository** - Input-based returns
3. **ExceptionStubUserRepository** - Error simulation
4. **StatefulStubNotificationService** - Sequential returns
5. **StubCommissionCalculator** - Fixed calculation
6. **StubDealValidator** - Validation results
7. **StubEmailService** - Success/failure simulation
8. **StubDateProvider** - Fixed date/time

## Running the Tests

```bash
# Run all stub tests
mvn test -Dtest=StubTest

# Run specific test
mvn test -Dtest=StubTest#testBasicStub

# Run with coverage
mvn clean test jacoco:report
```

## Domain Model Usage

This implementation uses these domain models from `com.chapman.edu.commissions.model`:

- **User** - System users
- **Deal** - Sales deals
- **CommissionPlan** - Commission rules
- **DealStatus** - Deal lifecycle states
- **PlanStatus** - Plan states

## Advantages of Stubs

### 1. Fast Execution
- No I/O operations
- No network calls
- Instant returns
- Deterministic results

### 2. Test Isolation
- No real dependencies
- No database required
- No external services
- Pure unit testing

### 3. Control Test Data
- Exact values you need
- Edge cases easy to test
- Error scenarios simple
- Predictable behavior

### 4. Simple Implementation
- Easy to create
- Easy to understand
- Easy to maintain
- Minimal code

## When Stubs Excel

### Perfect For:
✅ Unit testing with isolated dependencies
✅ Testing various input scenarios
✅ Simulating error conditions
✅ Date/time-dependent logic
✅ Calculation verification
✅ Validation logic testing

### Not Ideal For:
❌ Verifying interactions (use Mock)
❌ Complex business logic (use Fake or real object)
❌ Integration testing (use Fake or real dependencies)
❌ When object is never used (use Dummy)

## Common Patterns

### Pattern 1: Simple Return Stub
```java
static class StubRepository {
    private final Entity entity;

    public Optional<Entity> findById(String id) {
        return Optional.of(entity);
    }
}
```

### Pattern 2: Conditional Stub
```java
static class ConditionalStub {
    public Result process(String input) {
        return switch(input) {
            case "A" -> Result.SUCCESS;
            case "B" -> Result.FAILURE;
            default -> Result.UNKNOWN;
        };
    }
}
```

### Pattern 3: Exception Stub
```java
static class ExceptionStub {
    public void process(String input) {
        if ("ERROR".equals(input)) {
            throw new RuntimeException("Simulated error");
        }
    }
}
```

### Pattern 4: Stateful Stub
```java
static class StatefulStub {
    private final Queue<Result> results;

    public Result next() {
        return results.poll();
    }
}
```

## Stub Limitations

### What Stubs CANNOT Do

1. **Verify method calls** - Use Mock for this
2. **Implement real logic** - Use Fake for this
3. **Track call history** - Use Spy for this
4. **Work in production** - Stubs are for testing only

### What Stubs CAN Do

1. **Return predetermined values** ✓
2. **Simulate errors** ✓
3. **Control test inputs** ✓
4. **Isolate dependencies** ✓

## Additional Resources

- [Martin Fowler - Mocks Aren't Stubs](https://martinfowler.com/articles/mocksArentStubs.html)
- [Martin Fowler - Test Doubles](https://martinfowler.com/bliki/TestDouble.html)
- [xUnit Test Patterns - Stub](https://xunitpatterns.com/Test%20Stub.html)
- [Gerard Meszaros - xUnit Test Patterns](http://xunitpatterns.com/)

## Related Patterns

- **Mock** - Verifies behavior (see `../mock/`)
- **Fake** - Working implementation (see `../fake/`)
- **Spy** - Partial mocking (see `../spy/`)
- **Dummy** - Unused placeholder (see `../dummy/`)

## Key Takeaways

### What Makes a Stub

1. **Returns hardcoded values** - Not calculated
2. **No business logic** - Just return statements
3. **Usually stateless** - Same input → same output
4. **Doesn't verify** - Just provides data
5. **Simple to create** - Minimal code

### When to Use Stub

- Need specific return values for testing
- Want to isolate code from dependencies
- Testing various scenarios (success/failure/edge cases)
- Simulating error conditions
- Date/time-dependent testing

### When NOT to Use Stub

- Need to verify method calls (use Mock)
- Need working implementation (use Fake)
- Object never used (use Dummy)
- Complex logic required (use Fake or real)

## Summary

The Stub pattern provides predetermined responses to method calls, making it perfect for controlling test inputs and isolating code from dependencies. Stubs are simple, focused test doubles that return hardcoded values without business logic. They excel at unit testing scenarios where you need specific, predictable data.

**Key Principle:** Stubs provide answers to questions, but don't verify if the questions were asked.

---

**Total Tests:** 30+ comprehensive tests covering all stub patterns
**Stub Implementations:** 8 reusable stub classes
**Real-World Scenarios:** 5 complete workflow examples