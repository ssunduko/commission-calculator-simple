# Template Method Pattern Implementation

## Overview

The **Template Method Pattern** is a behavioral design pattern that defines the skeleton of an algorithm in a base class, but lets subclasses override specific steps of the algorithm without changing its overall structure.

This implementation demonstrates the Template Method pattern through **Commission Calculation** workflows, showing how different product types (Software, Hardware, Services, Training) share the same calculation process but have different specific calculation logic.

## Pattern Components

### 1. Abstract Class (`CommissionCalculator`)
- Defines the template method `calculateCommission()` (final - cannot be overridden)
- Contains the algorithm skeleton with concrete, abstract, and hook methods
- Controls the flow of the algorithm

### 2. Template Method (`calculateCommission()`)
- **Final method** that defines the algorithm structure
- Calls a series of steps in a fixed sequence
- Cannot be overridden by subclasses (structure is protected)

### 3. Method Types

| Method Type | Characteristics | Example |
|-------------|----------------|---------|
| **Concrete Methods** | Implemented in base class, same for all subclasses | `validateDeal()`, `finalizeCalculation()` |
| **Abstract Methods** | Must be implemented by subclasses (required variation) | `calculateBaseCommission()`, `applyPerformanceBonuses()` |
| **Hook Methods** | Have default implementation, can be overridden (optional variation) | `applyTimeBasedModifiers()`, `applyCapsAndFloors()` |

### 4. Concrete Subclasses

Four calculator implementations demonstrating different customization approaches:

| Calculator | Base Logic | Hooks Overridden | Unique Features |
|-----------|------------|------------------|-----------------|
| **SoftwareCommissionCalculator** | 15% rate + large deal bonus | None (uses defaults) | 3% bonus for deals > $50k |
| **HardwareCommissionCalculator** | 8% rate + tiered bonuses | Time modifiers disabled, custom caps | Fixed bonuses by tier |
| **ServicesCommissionCalculator** | 20% rate + strategic bonus | Enhanced time modifiers, caps disabled | 10% quarter-end bonus |
| **TrainingCommissionCalculator** | $500 flat rate + volume bonus | Custom caps ($500-$2000) | Flat base + tiered bonuses |

## Problem Solved

### Without Template Method Pattern ❌

```java
public class SoftwareCalculator {
    public BigDecimal calculate(Deal deal) {
        // Validate deal
        if (deal == null) throw new IllegalArgumentException("...");
        if (deal.getStatus() != DealStatus.WON) throw new IllegalStateException("...");

        // Calculate base
        BigDecimal base = deal.getValue().multiply(new BigDecimal("0.15"));

        // Apply modifiers
        if (isQuarterEnd(deal.getCloseDate())) {
            base = base.add(base.multiply(new BigDecimal("0.05")));
        }

        // Apply bonuses
        if (deal.getValue().compareTo(new BigDecimal("50000")) > 0) {
            base = base.add(deal.getValue().multiply(new BigDecimal("0.03")));
        }

        // Apply caps
        BigDecimal max = deal.getValue().multiply(new BigDecimal("0.50"));
        if (base.compareTo(max) > 0) base = max;

        // Log
        log("Software commission: " + base);

        return base;
    }
}

public class HardwareCalculator {
    public BigDecimal calculate(Deal deal) {
        // DUPLICATE validation code
        if (deal == null) throw new IllegalArgumentException("...");
        if (deal.getStatus() != DealStatus.WON) throw new IllegalStateException("...");

        // Different calculation
        BigDecimal base = deal.getValue().multiply(new BigDecimal("0.08"));

        // Different bonuses (no time modifiers for hardware)
        if (deal.getValue().compareTo(new BigDecimal("100000")) > 0) {
            base = base.add(new BigDecimal("2000"));
        }

        // Different caps
        BigDecimal max = deal.getValue().multiply(new BigDecimal("0.30"));
        if (base.compareTo(max) > 0) base = max;

        // DUPLICATE logging code
        log("Hardware commission: " + base);

        return base;
    }
}

// More calculators with MORE duplicate code...
```

**Problems:**
- 🔴 Validation logic duplicated across all calculators
- 🔴 Logging logic duplicated across all calculators
- 🔴 Algorithm structure is implicit and inconsistent
- 🔴 Hard to maintain - changes must be made in multiple places
- 🔴 No guarantee all calculators follow the same process

### With Template Method Pattern ✅

```java
public abstract class CommissionCalculator {
    // Template method - defines algorithm structure
    public final CommissionResult calculateCommission(Deal deal) {
        validateDeal(deal);  // Concrete - shared
        BigDecimal base = calculateBaseCommission(deal);  // Abstract - varies
        BigDecimal modifiers = applyTimeBasedModifiers(deal, base);  // Hook - optional
        BigDecimal bonuses = applyPerformanceBonuses(deal, base);  // Abstract - varies
        BigDecimal final = applyCapsAndFloors(deal, total);  // Hook - optional
        logCalculation(deal, final);  // Concrete - shared
        return result;
    }

    protected void validateDeal(Deal deal) { /* shared code */ }
    protected abstract BigDecimal calculateBaseCommission(Deal deal);
    protected BigDecimal applyTimeBasedModifiers(...) { /* default */ }
    protected abstract BigDecimal applyPerformanceBonuses(...);
    protected BigDecimal applyCapsAndFloors(...) { /* default */ }
    protected void logCalculation(...) { /* shared code */ }
}

public class SoftwareCommissionCalculator extends CommissionCalculator {
    // Only implement what's unique
    protected BigDecimal calculateBaseCommission(Deal deal) {
        return deal.getValue().multiply(new BigDecimal("0.15"));
    }

    protected BigDecimal applyPerformanceBonuses(Deal deal, BigDecimal current) {
        if (deal.getValue().compareTo(new BigDecimal("50000")) > 0) {
            return deal.getValue().multiply(new BigDecimal("0.03"));
        }
        return BigDecimal.ZERO;
    }
    // Inherits validation, logging, default modifiers, default caps
}
```

**Benefits:**
- ✅ Validation and logging written once, used everywhere
- ✅ Algorithm structure is explicit and consistent
- ✅ Easy to add new calculators (just extend and implement abstract methods)
- ✅ Hooks provide flexibility without breaking the template
- ✅ Changes to common logic only need to be made in one place

## Workflow Steps

The template method defines a 6-step workflow:

```
1. Validate Deal (Concrete)
   ↓
2. Calculate Base Commission (Abstract - varies by type)
   ↓
3. Apply Time-Based Modifiers (Hook - optional)
   ↓
4. Apply Performance Bonuses (Abstract - varies by type)
   ↓
5. Apply Caps and Floors (Hook - optional)
   ↓
6. Log Calculation (Concrete)
```

### Step Details

**Step 1: Validate Deal (Concrete)**
- Same for all calculators
- Checks deal is won, has positive value
- Throws exceptions for invalid deals

**Step 2: Calculate Base Commission (Abstract)**
- MUST be implemented by each calculator
- Software: 15% of deal value
- Hardware: 8% of deal value
- Services: 20% of deal value
- Training: $500 flat rate

**Step 3: Apply Time-Based Modifiers (Hook)**
- Default: 5% bonus for quarter-end deals
- Software/Training: Use default
- Hardware: Disabled (returns false from `shouldApplyTimeBasedModifiers()`)
- Services: Enhanced (10% quarter-end + 5% year-end)

**Step 4: Apply Performance Bonuses (Abstract)**
- MUST be implemented by each calculator
- Software: 3% for deals > $50k
- Hardware: Tiered fixed bonuses ($500, $1000, $2000)
- Services: 10% for strategic accounts (> $30k)
- Training: Volume-based bonuses ($100, $300, $600)

**Step 5: Apply Caps and Floors (Hook)**
- Default: Min $100, Max 50% of deal value
- Software/Training: Use custom caps
- Hardware: Custom caps (Min $50, Max 30%)
- Services: Disabled (uncapped earning potential)

**Step 6: Log Calculation (Concrete)**
- Same for all calculators
- Records calculation details
- Creates audit trail

## File Structure

```
template/
├── TemplateStructure.java       # Generic Template Method pattern structure
├── TemplateImplementation.java  # Commission calculator implementation
├── TemplateUsage.java           # Comprehensive usage examples
├── template-pattern.puml        # UML class diagram
└── README.md                    # This file
```

## Running the Examples

### Run Generic Pattern Structure
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.template.TemplateStructure"
```

**Output shows:**
- 3 concrete classes (A, B, C) following the same algorithm
- Different implementations of abstract methods
- Different hook overrides (B overrides hooks, C skips step 3)

### Run Commission Calculator Implementation
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.template.TemplateImplementation"
```

**Output shows:**
- 4 different calculators processing deals
- Same workflow, different results
- Detailed calculation steps

### Run Comprehensive Usage Examples
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.template.TemplateUsage"
```

**Output shows:**
- 6 detailed scenarios
- Time-based modifier comparisons
- Caps and floors behavior
- Polymorphic usage patterns
- Validation and error handling

## Usage Examples

### Example 1: Basic Usage

```java
// Create a deal
Deal deal = new Deal("Enterprise License", new BigDecimal("75000"), "REP-123");
deal.setStatus(DealStatus.WON);
deal.setCloseDate(LocalDate.now());

// Create calculator (decide which type)
CommissionCalculator calculator = new SoftwareCommissionCalculator();

// Calculate commission (template method does the work)
CommissionResult result = calculator.calculateCommission(deal);

// Get the result
System.out.println("Commission: $" + result.getFinalCommission());
result.displayReport();
```

### Example 2: Polymorphic Usage (Factory Pattern)

```java
// Factory to select appropriate calculator
public class CalculatorFactory {
    public static CommissionCalculator getCalculator(String productType) {
        return switch (productType) {
            case "SOFTWARE" -> new SoftwareCommissionCalculator();
            case "HARDWARE" -> new HardwareCommissionCalculator();
            case "SERVICES" -> new ServicesCommissionCalculator();
            case "TRAINING" -> new TrainingCommissionCalculator();
            default -> throw new IllegalArgumentException("Unknown type: " + productType);
        };
    }
}

// Client code doesn't know which concrete calculator it's using
CommissionCalculator calc = CalculatorFactory.getCalculator(deal.getProductType());
CommissionResult result = calc.calculateCommission(deal);
```

### Example 3: Batch Processing

```java
List<Deal> deals = getAllWonDeals();
Map<String, CommissionCalculator> calculators = new HashMap<>();
calculators.put("SOFTWARE", new SoftwareCommissionCalculator());
calculators.put("HARDWARE", new HardwareCommissionCalculator());
// ... etc

for (Deal deal : deals) {
    CommissionCalculator calc = calculators.get(deal.getProductType());
    CommissionResult result = calc.calculateCommission(deal);
    saveToDatabase(result);
}
```

## Design Principles Applied

### Hollywood Principle
> "Don't call us, we'll call you"

The parent class (`CommissionCalculator`) controls the flow and calls the child methods. Subclasses don't control when they're called - the template method does.

```java
// Parent controls the flow
public final CommissionResult calculateCommission(Deal deal) {
    validateDeal(deal);  // Parent calls this
    BigDecimal base = calculateBaseCommission(deal);  // Parent calls child's implementation
    // ... parent continues to orchestrate
}
```

### Single Responsibility Principle (SRP)
- Base class responsibility: Define algorithm structure and common operations
- Subclass responsibility: Implement type-specific calculation logic

### Open/Closed Principle (OCP)
- **Open for extension**: Easy to add new calculator types by extending the base class
- **Closed for modification**: Template method is final, existing calculators don't change

### Don't Repeat Yourself (DRY)
- Validation logic: Written once in base class
- Logging logic: Written once in base class
- Default modifiers/caps: Written once, reused everywhere

### Dependency Inversion Principle (DIP)
- Client code depends on `CommissionCalculator` abstraction
- Actual behavior provided by concrete implementations

## Testing Strategy

### Unit Testing Individual Calculators

```java
@Test
void testSoftwareCalculatorBaseCommission() {
    Deal deal = createWonDeal("Test Deal", new BigDecimal("100000"));
    SoftwareCommissionCalculator calc = new SoftwareCommissionCalculator();

    CommissionResult result = calc.calculateCommission(deal);

    // Software rate is 15%
    // Deal is $100k, so base should be $15k
    // Plus bonus for large deal (3% = $3k)
    // Total should be $18k (before modifiers/caps)
    assertTrue(result.getFinalCommission().compareTo(new BigDecimal("15000")) > 0);
}
```

### Testing Template Method Flow

```java
@Test
void testTemplateMethodExecutesAllSteps() {
    // Create a spy/mock that tracks method calls
    CommissionCalculator spy = spy(new SoftwareCommissionCalculator());
    Deal deal = createWonDeal("Test", new BigDecimal("50000"));

    spy.calculateCommission(deal);

    // Verify all steps were called in order
    InOrder inOrder = inOrder(spy);
    inOrder.verify(spy).validateDeal(deal);
    inOrder.verify(spy).calculateBaseCommission(deal);
    inOrder.verify(spy).applyTimeBasedModifiers(any(), any());
    inOrder.verify(spy).applyPerformanceBonuses(any(), any());
    inOrder.verify(spy).applyCapsAndFloors(any(), any());
    inOrder.verify(spy).logCalculation(any(), any());
}
```

### Testing Hook Behavior

```java
@Test
void testHardwareCalculatorDisablesTimeModifiers() {
    Deal quarterEndDeal = createWonDeal("Test", new BigDecimal("50000"));
    quarterEndDeal.setCloseDate(LocalDate.of(2024, 3, 28)); // Quarter-end

    HardwareCommissionCalculator hardwareCalc = new HardwareCommissionCalculator();
    SoftwareCommissionCalculator softwareCalc = new SoftwareCommissionCalculator();

    CommissionResult hardwareResult = hardwareCalc.calculateCommission(quarterEndDeal);
    CommissionResult softwareResult = softwareCalc.calculateCommission(quarterEndDeal);

    // Hardware should not have time modifiers applied
    // Software should have 5% quarter-end bonus
    assertTrue(softwareResult.getFinalCommission().compareTo(hardwareResult.getFinalCommission()) > 0);
}
```

### Testing Validation

```java
@Test
void testTemplateMethodValidatesInput() {
    CommissionCalculator calc = new SoftwareCommissionCalculator();

    // Should reject null deal
    assertThrows(IllegalArgumentException.class, () -> {
        calc.calculateCommission(null);
    });

    // Should reject non-won deal
    Deal openDeal = createDeal("Test", new BigDecimal("50000"));
    openDeal.setStatus(DealStatus.OPEN);
    assertThrows(IllegalStateException.class, () -> {
        calc.calculateCommission(openDeal);
    });
}
```

## Real-World Applications

The Template Method pattern is useful for:

1. **Document Generation**
   - Template: Open file → Generate header → Generate body → Generate footer → Close file
   - Variations: PDF, Word, HTML (different body generation)

2. **Data Processing Pipelines**
   - Template: Load → Validate → Transform → Enrich → Save
   - Variations: CSV, JSON, XML (different load/transform logic)

3. **Game Character AI**
   - Template: Sense → Think → Act
   - Variations: Aggressive, Defensive, Stealthy (different think/act logic)

4. **Web Request Handling**
   - Template: Parse request → Authenticate → Authorize → Execute → Format response
   - Variations: REST, GraphQL, SOAP (different parse/format logic)

5. **Test Frameworks**
   - Template: Setup → Run test → Teardown
   - Variations: Different test types (unit, integration) with same structure

## Common Pitfalls to Avoid

### ❌ Don't: Make the template method non-final

```java
// BAD - template method can be overridden
public CommissionResult calculateCommission(Deal deal) {
    // ...
}
```

**Why:** Subclasses could bypass the intended algorithm structure.

### ✅ Do: Make it final

```java
// GOOD - algorithm structure is protected
public final CommissionResult calculateCommission(Deal deal) {
    // ...
}
```

### ❌ Don't: Put subclass-specific logic in the base class

```java
// BAD - base class shouldn't know about concrete types
protected BigDecimal calculateBaseCommission(Deal deal) {
    if (this instanceof SoftwareCalculator) {
        return deal.getValue().multiply(new BigDecimal("0.15"));
    } else if (this instanceof HardwareCalculator) {
        return deal.getValue().multiply(new BigDecimal("0.08"));
    }
    // ...
}
```

**Why:** Defeats the purpose of the pattern, creates tight coupling.

### ✅ Do: Use abstract methods

```java
// GOOD - let subclasses provide their own logic
protected abstract BigDecimal calculateBaseCommission(Deal deal);
```

### ❌ Don't: Create too many template methods

```java
// BAD - fragmented algorithm
public final Result calculateCommissionPart1(Deal deal) { /*...*/ }
public final Result calculateCommissionPart2(Deal deal) { /*...*/ }
public final Result calculateCommissionPart3(Deal deal) { /*...*/ }
```

**Why:** Unclear flow, defeats the "single algorithm" purpose.

### ✅ Do: Use one cohesive template method

```java
// GOOD - one clear algorithm
public final CommissionResult calculateCommission(Deal deal) {
    // Complete algorithm in one place
}
```

### ❌ Don't: Make everything abstract

```java
// BAD - no code reuse
protected abstract void validateDeal(Deal deal);
protected abstract BigDecimal calculateBaseCommission(Deal deal);
protected abstract BigDecimal applyModifiers(Deal deal);
// ... everything is abstract
```

**Why:** No benefit over just having separate classes.

### ✅ Do: Balance abstract, hook, and concrete methods

```java
// GOOD - mix of shared (concrete), required (abstract), and optional (hook)
protected void validateDeal(Deal deal) { /* concrete - shared */ }
protected abstract BigDecimal calculateBaseCommission(Deal deal); // abstract - varies
protected BigDecimal applyModifiers(...) { /* hook - optional */ }
```

## When NOT to Use Template Method

Avoid Template Method pattern when:

- 🚫 The algorithm has no invariant steps (nothing is common across implementations)
- 🚫 You only have one implementation (no variation to manage)
- 🚫 The algorithm steps are not sequential (order varies between implementations)
- 🚫 You need runtime composition of algorithm steps (use Strategy or Chain of Responsibility)
- 🚫 The steps are completely independent (just use separate methods)

## Related Patterns

- **Strategy Pattern**: Similar to Template Method but uses composition instead of inheritance. Strategy is for algorithms that can be swapped at runtime; Template Method is for algorithms with fixed structure but varying steps.

- **Factory Method Pattern**: Can be considered a special case of Template Method where the template method creates objects.

- **Builder Pattern**: Both define multi-step processes, but Builder focuses on object construction while Template Method focuses on algorithm execution.

- **Chain of Responsibility**: Template Method defines a fixed sequence; Chain of Responsibility has a dynamic sequence of handlers.

## Further Learning

To deepen understanding:

1. Run all three demo files and compare the output
2. Add a new calculator type (e.g., `ConsultingCommissionCalculator`)
3. Modify the template method to add a new step (e.g., "Apply Regional Adjustments")
4. Experiment with different hook combinations
5. Try using the pattern with composition instead of inheritance (compare pros/cons)
6. Implement a simple document generator using Template Method

## References

- **Design Patterns: Elements of Reusable Object-Oriented Software** - Gang of Four (pages 325-330)
- **Head First Design Patterns** - Freeman & Freeman (Chapter 8: Template Method)
- **Effective Java** - Joshua Bloch (Item 20: Prefer interfaces to abstract classes)
- **Refactoring: Improving the Design of Existing Code** - Martin Fowler (Extract Superclass refactoring)