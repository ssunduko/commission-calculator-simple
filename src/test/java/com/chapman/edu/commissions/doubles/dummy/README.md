# Test Doubles - Dummy Pattern

## Overview

This module demonstrates the **Dummy** pattern, the simplest form of test double. Dummies are objects that are passed around but never actually used in tests. They exist solely to fill parameter lists when the test doesn't care about those particular parameters.

## What is a Dummy?

A **Dummy** is a placeholder object that:

1. **Exists but is never invoked** - Passed to methods but never used
2. **Has minimal or no implementation** - Simplest possible construction
3. **Fills required parameters** - Satisfies API contracts without providing real data
4. **Requires no verification** - We don't check if it was called (because it shouldn't be)

Think of a dummy as the programming equivalent of "Lorem Ipsum" text - it's there to fill space, not to be read.

## Dummy in the Test Double Hierarchy

```
Test Double Types (by complexity):

Dummy    ← Simplest (never used) ← YOU ARE HERE
  ↓
Stub     ← Returns pre-programmed values
  ↓
Spy      ← Records calls and delegates to real object
  ↓
Mock     ← Verifies expectations and behavior
  ↓
Fake     ← Working implementation (simplified)
```

## When to Use Dummies

### ✓ Good Use Cases

**1. Required Method Parameters**
```java
// Testing deal creation logic, don't care about creator
dealService.createDeal(deal, dummyCreator);
```

**2. Constructor Dependencies**
```java
// Legacy constructor requires many dependencies
CommissionService service = new CommissionService(
    dummyAuditor,      // Required but not used in our test
    dummyCache,        // Required but not used in our test
    realCalculator     // The dependency we're testing
);
```

**3. Collection Placeholders**
```java
// Need 100 items for pagination test, don't care about content
List<User> dummyUsers = new ArrayList<>();
for (int i = 0; i < 100; i++) {
    dummyUsers.add(new User());
}
```

**4. Interface Compliance**
```java
// Method requires a callback that won't be invoked in this test path
Runnable dummyCallback = () -> {};
processor.process(data, dummyCallback);
```

### ✗ When NOT to Use Dummies

**1. Object Will Be Used**
- If methods will be called on the object, use a Stub or real object
- Dummies are ONLY for unused objects

**2. Need to Verify Behavior**
- Use a Mock instead if you need to verify method calls
- Dummies are not tracked or verified

**3. Need Specific Return Values**
- Use a Stub if you need controlled return values
- Dummies don't return meaningful data

**4. Null is Acceptable**
- If the API allows null, just use null
- Dummies are for when objects are required but not used

## Core Concepts Demonstrated

### 1. Basic Dummy Objects

The simplest form - objects that exist but are never invoked:

```java
@Test
void testBasicDummy() {
    // Dummy user - completely empty, never used
    User dummyUser = new User();

    // Create deal (test focus is here)
    Deal deal = new Deal("Test Deal", new BigDecimal("50000"), "SALES-001");

    // dummyUser might be passed to a method but never invoked
    // dealService.createDeal(deal, dummyUser);
}
```

### 2. Null vs Dummy

Understanding when to use null vs a dummy object:

```java
// Use null when API allows it
deal.setCloseDate(null);  // ✓ Nullable parameter

// Use dummy when API requires non-null
User dummyApprover = new User();  // ✓ Required parameter
processApproval(deal, dummyApprover);
```

### 3. Dummy Naming Conventions

Clear naming communicates intent:

```java
// GOOD: Clear dummy names
User dummyUser = new User();
Deal unusedDeal = new Deal();
CommissionPlan irrelevantPlan = new CommissionPlan();

// BAD: Generic names hide intent
User user = new User();  // ✗ Will this be used?
```

### 4. Constructor Parameter Dummies

Satisfying constructor requirements in legacy code:

```java
// Constructor requires many dependencies
CommissionService service = new CommissionService(
    dummyAuditor,        // Never used in our test
    dummyFallbackPlan,   // Never used in our test
    realEmailService,    // Real dependency we care about
    realCalculator       // Real dependency we care about
);
```

### 5. Method Parameter Dummies

Filling method signatures with unused objects:

```java
// Method: approveDeal(Deal deal, User approver, String comments)
// Testing deal logic, not approver or comments

Deal dealToTest = new Deal("Important Deal", new BigDecimal("100000"), "USER-1");
User dummyApprover = new User();  // Required but not used
String dummyComments = "dummy";    // Required but not used

dealService.approveDeal(dealToTest, dummyApprover, dummyComments);
```

### 6. Collection Dummies

Placeholder objects in lists, sets, or maps:

```java
// Need 100 items for pagination test
List<User> dummyUsers = new ArrayList<>();
for (int i = 0; i < 100; i++) {
    dummyUsers.add(new User());  // Content doesn't matter
}

// Test pagination logic
int pageSize = 10;
int totalPages = (int) Math.ceil((double) dummyUsers.size() / pageSize);
assertEquals(10, totalPages);
```

### 7. Interface Dummies

Minimal implementations of interfaces:

```java
// Dummy comparator - always returns 0
Comparator<Deal> dummyComparator = (d1, d2) -> 0;

// Dummy callback - does nothing
Runnable dummyCallback = () -> {};

// Use where required but not invoked
sorter.sort(deals, dummyComparator);
processor.process(data, dummyCallback);
```

### 8. Dummy Factories

Centralized dummy creation for consistency:

```java
// Factory methods for reusable dummies
private static User createDummyUser() {
    return new User();  // Minimal construction
}

private static Deal createDummyDeal() {
    return new Deal("dummy", BigDecimal.ZERO, "dummy");
}

// Usage in tests
User dummy1 = createDummyUser();
User dummy2 = createDummyUser();
```

### 9. Dummy Builders

Fluent API for creating dummies with optional customization:

```java
// Basic dummy with defaults
Deal basicDummy = new DealDummyBuilder().build();

// Customized dummy if specific values needed
Deal customizedDummy = new DealDummyBuilder()
    .withTitle("Custom Dummy")
    .build();
```

### 10. Real-World Scenarios

**Scenario 1: Audit Trail Dummies**
```java
// Approving a deal requires audit info, but we're testing approval logic
Deal dealToApprove = new Deal("Enterprise Sale", new BigDecimal("250000"), "USER-100");

// Dummy audit objects
User dummyAuditor = new User("auditor", "audit@system.com", "System", "Auditor");
LocalDate dummyAuditDate = LocalDate.now();
String dummyAuditReason = "dummy audit reason";

dealService.approveDeal(dealToApprove, dummyAuditor, dummyAuditDate, dummyAuditReason);
```

**Scenario 2: Notification Recipients**
```java
// Testing notification formatting, not recipient data
List<User> dummyRecipients = Arrays.asList(
    new User(),
    new User(),
    new User()
);

String message = String.format(
    "Deal '%s' worth %s requires approval",
    deal.getTitle(),
    deal.getValue()
);

notificationService.send(message, dummyRecipients);
```

**Scenario 3: Configuration Dummies**
```java
// Testing validation logic, configuration required but not used
Currency dummySystemCurrency = Currency.getInstance("USD");
CommissionPlan dummyDefaultPlan = new CommissionPlan("Default", dummySystemCurrency);
User dummySystemAdmin = new User("admin", "admin@system.com", "System", "Admin");

boolean isValid = validator.validate(dealToValidate,
    dummySystemCurrency, dummyDefaultPlan, dummySystemAdmin);
```

**Scenario 4: Callback Dummies**
```java
// Testing batch processing, callbacks required but not used in test
Runnable dummySuccessCallback = () -> {};
Runnable dummyFailureCallback = () -> {};

batchProcessor.process(deals, dummySuccessCallback, dummyFailureCallback);
```

## Best Practices

### DO:

✅ **Use descriptive names** - Prefix with "dummy", "unused", "irrelevant"
```java
User dummyUser = new User();
Deal unusedDeal = new Deal();
```

✅ **Minimize construction** - Use default constructors or factory methods
```java
User dummy = new User();  // ✓ Simple
User dummy = createDummyUser();  // ✓ Factory
```

✅ **Use factory methods** - Centralize dummy creation for consistency
```java
private static User createDummyUser() {
    return new User();
}
```

✅ **Document purpose** - Comment why object is a dummy
```java
// Dummy user required by API signature but not used in this test path
User dummyApprover = new User();
```

✅ **Prefer null when allowed** - Use null if API accepts it, dummy when required
```java
deal.setCloseDate(null);  // ✓ Nullable
User dummyCreator = new User();  // ✓ Required
```

### DON'T:

❌ **Over-specify dummy data** - Don't add unnecessary detail
```java
// ✗ WRONG - Too much setup for unused object
User dummy = new User("john.doe", "john.doe@company.com", "John", "Doe");
dummy.setDepartment("Sales");
dummy.addRole(UserRole.SALES_REP);

// ✓ RIGHT - Minimal setup
User dummy = new User();
```

❌ **Use unclear names** - Don't hide that object is a dummy
```java
User user = new User();  // ✗ Will this be used?
User dummyUser = new User();  // ✓ Clear intent
```

❌ **Use wrong test double** - Dummy is only for unused objects
```java
// If object WILL be used, don't call it a dummy
User user = new User("test", "test@test.com", "Test", "User");
String fullName = user.getFullName();  // Used - not a dummy!
```

❌ **Verify dummy behavior** - Dummies aren't verified (they shouldn't be called)
```java
// ✗ WRONG - If you're verifying, it's not a dummy
verify(dummyUser).getEmail();  // Should use Mock instead
```

## Common Pitfalls

### Pitfall 1: Over-Specifying Dummy Data

Adding unnecessary detail to objects that won't be used:

```java
// ✗ ANTI-PATTERN
User overSpecified = new User("john.doe", "john.doe@company.com", "John", "Doe");
overSpecified.setDepartment("Sales");
overSpecified.setTerritory("West Coast");
overSpecified.addRole(UserRole.SALES_REP);
// This object is never used - why all the setup?

// ✓ CORRECT
User dummy = new User();  // Simple and clear
```

### Pitfall 2: Unclear Dummy Names

Generic names hide that an object is a dummy:

```java
// ✗ ANTI-PATTERN
User user = new User();  // Will this be used?
Deal d = new Deal();     // Even worse

// ✓ CORRECT
User dummyUser = new User();  // Obviously a dummy
Deal unusedDeal = new Deal(); // Obviously unused
```

### Pitfall 3: Using Dummy When You Need Stub/Mock

Dummies are for unused objects - use other test doubles when needed:

```java
// ✗ WRONG - Object WILL be used, shouldn't call it dummy
User dummyUser = new User();
String name = dummyUser.getFullName();  // Used! Not a dummy!

// ✓ RIGHT - Use appropriate test double
User user = new User("test", "test@test.com", "Test", "User");  // Real object
String name = user.getFullName();  // Used appropriately

// Or use a Stub if you need controlled values
// Or use a Mock if you need to verify calls
```

## Test Structure

### DummyTest.java

The test class demonstrates all dummy concepts:

1. **Basic Dummy Concepts** - Simple unused objects
2. **Null vs Dummy** - Choosing the right approach
3. **Dummy Naming** - Clear communication through names
4. **Constructor Dummies** - Filling required constructor parameters
5. **Method Parameter Dummies** - Unused method arguments
6. **Collection Dummies** - Placeholder objects in collections
7. **Interface Dummies** - Minimal interface implementations
8. **Dummy Factories** - Reusable dummy creation
9. **Real-World Scenarios** - Practical applications
10. **Best Practices** - Guidelines for effective usage
11. **Common Pitfalls** - What to avoid

## Running the Tests

```bash
# Run all dummy tests
mvn test -Dtest=DummyTest

# Run specific test
mvn test -Dtest=DummyTest#testBasicDummy

# Run with coverage
mvn clean test jacoco:report
```

## Domain Model Usage

This implementation uses these domain models from `com.chapman.edu.commissions.model`:

- **User** - System users with roles and authentication
- **Deal** - Sales deals with products and calculations
- **CommissionPlan** - Commission rules and tiers
- **DealProduct** - Products within deals
- **DealStatus** - Enum for deal lifecycle states
- **UserRole** - Enum for user permissions
- **PlanStatus** - Enum for plan states

## Comparison with Other Test Doubles

| Test Double | Purpose | When to Use | Verification |
|-------------|---------|-------------|--------------|
| **Dummy** | Fill parameters | Object required but not used | No verification |
| **Stub** | Provide values | Need specific return values | No verification |
| **Spy** | Track calls | Partial mocking of real objects | Optional verification |
| **Mock** | Verify behavior | Need to verify method calls | Mandatory verification |
| **Fake** | Simplified impl | Need working but simpler version | No verification |

## Key Takeaways

### What Makes a Dummy

1. **Never invoked** - Object exists but methods are never called
2. **Minimal setup** - Constructed with least effort
3. **Clear naming** - Named to indicate it's unused
4. **No verification** - We don't check if it was called

### When to Use Dummy

- Method signature requires parameters you don't care about
- Constructor needs dependencies that won't be used
- Collections need placeholder objects
- API requires non-null objects even when unused

### When NOT to Use Dummy

- Object will actually be used (use real object or stub)
- Need to verify calls (use mock)
- Need specific return values (use stub)
- Null is acceptable (just use null)

## Additional Resources

- [Martin Fowler - Test Doubles](https://martinfowler.com/bliki/TestDouble.html)
- [xUnit Test Patterns - Dummy](https://xunitpatterns.com/Dummy%20Object.html)
- [Test Double Patterns](https://blog.cleancoder.com/uncle-bob/2014/05/14/TheLittleMocker.html)

## Related Patterns

- **Stub** - Next level: provides return values (see `../stub/`)
- **Mock** - Verifies behavior (see `../mock/`)
- **Spy** - Partial mocking (see `../spy/`)
- **Fake** - Working implementation (see `../fake/`)

## Summary

The Dummy pattern is the simplest test double - an object that exists solely to fill parameter lists. Dummies are never invoked, require minimal setup, and need no verification. They're perfect for satisfying API contracts when you don't care about specific parameters in your test.

Key principle: **If it's used, it's not a dummy.**