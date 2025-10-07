# Strategy Pattern - Commission Calculator Implementation

## Pattern Overview

The **Strategy Pattern** is a behavioral design pattern that enables selecting an algorithm's behavior at runtime. It defines a family of algorithms, encapsulates each one in a separate class, and makes them interchangeable. The pattern lets the algorithm vary independently from the clients that use it, promoting flexibility and adherence to the Open/Closed Principle.

This implementation demonstrates the Strategy Pattern applied to commission calculations in a sales system, showcasing how different commission calculation algorithms can be swapped dynamically based on business rules without modifying the client code.

## Definition

**Strategy Pattern**: Define a family of algorithms, encapsulate each one, and make them interchangeable. Strategy lets the algorithm vary independently from clients that use it.

### Core Components

1. **Strategy** - An interface common to all supported algorithms
2. **ConcreteStrategy** - Implements the algorithm using the Strategy interface
3. **Context** - Maintains a reference to a Strategy object and uses it to execute the algorithm
4. **Client** - Creates and configures the Context with a specific ConcreteStrategy

## Pattern Structure

### Components

1. **Strategy Interfaces** (`StrategyStructure.java`)
   - `CommissionStrategy` - Base commission calculation algorithms
   - `BonusStrategy` - Bonus calculation algorithms
   - `PaymentTermsStrategy` - Payment terms adjustment algorithms

2. **Concrete Strategies** (`StrategyImplementation.java`)
   - **Commission Strategies:**
     - `StandardRateStrategy` - Fixed percentage rate (e.g., 10%)
     - `TieredRateStrategy` - Different rates based on deal size
     - `FlatFeeStrategy` - Fixed amount per deal
     - `GrossProfitStrategy` - Commission based on profit margin

   - **Bonus Strategies:**
     - `QuotaAchievementBonusStrategy` - Bonus for exceeding quota
     - `EarlyCloseBonusStrategy` - Bonus for early quarter closings
     - `NewCustomerBonusStrategy` - Bonus for new customer acquisition

   - **Payment Terms Strategies:**
     - `ImmediatePaymentStrategy` - Full commission paid immediately
     - `DeferredPaymentStrategy` - Commission with discount for payment risk
     - `SplitPaymentStrategy` - Partial payment immediately, rest on collection

3. **Context** (`StrategyImplementation.java`)
   - `CommissionCalculator` - Uses strategies to calculate total commission
   - Maintains references to strategy objects
   - Delegates algorithm execution to strategies

4. **Usage Examples** (`StrategyUsage.java`)
   - Demonstrations of all strategy types
   - Runtime strategy selection
   - Strategy composition

## Why It Matters

### Business Domain Significance

In real-world sales organizations, commission structures are:

1. **Complex and Variable** - Different products, deal types, and business objectives require different commission calculations
2. **Frequently Changing** - Commission plans evolve with market conditions and business strategy
3. **Rule-Driven** - Multiple factors (deal size, customer type, timing, payment terms) influence calculations
4. **Critical for Motivation** - Proper commission structures drive desired sales behaviors
5. **Compliance-Sensitive** - Must be accurately tracked for financial reporting and legal compliance

### Technical Challenges Without Strategy Pattern

Without the Strategy Pattern, commission calculation systems suffer from:

- **Monolithic if/else chains** that are hard to understand and maintain
- **Tight coupling** between calculation logic and business rules
- **Difficult testing** due to intertwined conditional logic
- **Risk of regression** when adding new commission types
- **Code duplication** across similar calculation methods
- **Violation of Open/Closed Principle** requiring modifications to existing code for new strategies

### How Strategy Pattern Solves These Problems

The Strategy Pattern addresses these challenges by:

- **Encapsulating** each algorithm in its own class
- **Enabling runtime selection** of appropriate strategies
- **Promoting extensibility** through new strategy classes
- **Improving testability** with isolated, focused strategy classes
- **Reducing coupling** between client code and algorithms
- **Supporting composition** of multiple strategies for complex calculations

## Pattern Intent

The intent of the Strategy Pattern is to:

1. **Define a family of algorithms** - Create a set of related algorithms with a common interface
2. **Encapsulate each algorithm** - Isolate algorithm implementation details within separate classes
3. **Make algorithms interchangeable** - Allow clients to use different algorithms through the same interface
4. **Enable runtime algorithm selection** - Choose the appropriate algorithm dynamically based on context
5. **Eliminate conditional statements** - Replace complex if/else chains with polymorphic strategy objects
6. **Support the Open/Closed Principle** - Open for extension (new strategies) but closed for modification (no changes to existing code)

In the commission calculator domain, this means:
- Commission calculation methods are strategies that implement `CommissionStrategy`
- Bonus calculations are strategies implementing `BonusStrategy`
- Payment term adjustments are strategies implementing `PaymentTermsStrategy`
- The `CommissionCalculator` (context) uses these strategies without knowing their specific implementations
- New commission structures can be added by creating new strategy classes without modifying the calculator

## Use Cases

### When to Use Strategy Pattern

✅ **Use Strategy Pattern when:**

1. **Multiple algorithms exist** for a specific task with different trade-offs
2. **Algorithm selection** needs to happen at runtime based on context
3. **Complex conditional logic** can be replaced with polymorphism
4. **Algorithms need isolation** for independent testing and maintenance
5. **You want to hide** algorithm implementation details from clients
6. **Similar classes** differ only in behavior
7. **You need to avoid** exposing algorithm-specific data structures

### Specific Use Cases in This Implementation

**Commission Calculation Strategies:**
- Standard rate for predictable, simple commissions
- Tiered rates to incentivize larger deals
- Flat fees for small or standard transactions
- Gross profit-based for encouraging profitable sales

**Bonus Calculation Strategies:**
- Quota achievement bonuses to drive revenue targets
- Early close bonuses to encourage timely quarter closings
- New customer bonuses to promote customer acquisition

**Payment Terms Strategies:**
- Immediate payment for standard terms
- Deferred payment accounting for collection risk
- Split payment balancing cash flow and risk

### When NOT to Use Strategy Pattern

❌ **Avoid Strategy Pattern when:**

1. **Only one algorithm** exists with no foreseeable variations
2. **Algorithms are simple** and don't justify separate classes
3. **Algorithms rarely change** and don't need runtime selection
4. **Performance is critical** and strategy overhead is unacceptable
5. **Algorithm selection is trivial** and doesn't warrant the pattern complexity

## Key Benefits

### Primary Advantages

1. **Runtime Algorithm Selection**
   - Strategies can be swapped at runtime without changing client code
   - Enables dynamic behavior based on business rules
   - Example: Choose commission strategy based on deal size or product type

2. **Eliminates Conditional Logic**
   - Replaces large if/else or switch statements with polymorphism
   - Makes code more readable and maintainable
   - Reduces cyclomatic complexity

3. **Open/Closed Principle Adherence**
   - New strategies can be added without modifying existing code
   - Reduces risk of regression bugs
   - Supports evolutionary design

4. **Independent Strategy Development**
   - Each strategy can be developed, tested, and maintained independently
   - Teams can work on different strategies in parallel
   - Strategies can be versioned separately

5. **Improved Testability**
   - Each strategy is a focused, cohesive unit that's easy to test
   - Strategies can be tested in isolation
   - Mock strategies can be used for testing context

6. **Code Reusability**
   - Strategies can be reused across different contexts
   - Common strategy interfaces enable strategy sharing
   - Reduces code duplication

7. **Strategy Composition**
   - Multiple strategies can be combined for complex behavior
   - Example: Commission strategy + bonus strategy + payment terms strategy
   - Provides flexibility without combinatorial explosion of classes

8. **Separation of Concerns**
   - Algorithm implementation is separated from algorithm usage
   - Context (calculator) doesn't need to know algorithm details
   - Promotes single responsibility principle

### Business Benefits

- **Faster Time to Market** - New commission structures can be added quickly
- **Reduced Risk** - Changes are isolated to specific strategy classes
- **Better Compliance** - Commission rules are clearly defined and auditable
- **Improved Agility** - Easy to experiment with different commission structures
- **Cost Savings** - Reduced maintenance effort and fewer bugs

## Downsides

### Potential Drawbacks

1. **Increased Number of Classes**
   - **Issue**: Each strategy requires a separate class
   - **Impact**: More files to manage and navigate
   - **Mitigation**: Group related strategies in packages; use clear naming conventions
   - **When problematic**: Simple scenarios with few algorithms

2. **Client Must Be Aware of Strategies**
   - **Issue**: Clients need to know which strategy to use and when
   - **Impact**: Clients must understand strategy differences
   - **Mitigation**: Provide factory methods or strategy selectors; document strategy usage
   - **When problematic**: Complex strategy selection logic

3. **Communication Overhead**
   - **Issue**: Context must pass data to strategies
   - **Impact**: May require passing more data than some strategies need
   - **Mitigation**: Use parameter objects; provide context access methods
   - **When problematic**: Strategies have vastly different data needs

4. **Potential Performance Overhead**
   - **Issue**: Strategy object creation and delegation
   - **Impact**: Slight performance cost compared to inline code
   - **Mitigation**: Cache strategy instances; use flyweight pattern if needed
   - **When problematic**: High-frequency, performance-critical code paths

5. **Strategy Selection Complexity**
   - **Issue**: Determining which strategy to use can become complex
   - **Impact**: Strategy selection logic may become a maintenance burden
   - **Mitigation**: Encapsulate selection logic; use configuration or rules engine
   - **When problematic**: Many strategies with complex selection criteria

6. **Potential for Overuse**
   - **Issue**: Applying the pattern where simple if/else would suffice
   - **Impact**: Unnecessary complexity and cognitive overhead
   - **Mitigation**: Apply pragmatically; start simple and refactor to strategy when needed
   - **When problematic**: Premature optimization or over-engineering

### Trade-offs in This Implementation

**Benefits Realized:**
- Easy to add new commission types (e.g., residual commissions, team bonuses)
- Commission logic is testable in isolation
- Multiple strategies can be composed for complex scenarios
- Changes to one strategy don't affect others

**Costs Accepted:**
- Multiple strategy classes for commission, bonus, and payment terms
- Clients must understand strategy types (mitigated by clear naming and documentation)
- Slight overhead from strategy delegation (negligible in this business domain)

### When the Downsides Outweigh Benefits

Avoid Strategy Pattern if:
- You have only 2-3 simple algorithms that rarely change
- Performance is absolutely critical (e.g., high-frequency trading)
- The team lacks experience with design patterns
- Algorithm selection is trivial and obvious

## How It Works

### Basic Flow

```java
// 1. Create the context
CommissionCalculator calculator = new CommissionCalculator();

// 2. Configure strategies
calculator.setCommissionStrategy(new TieredRateStrategy());
calculator.setBonusStrategy(new QuotaAchievementBonusStrategy(...));
calculator.setPaymentTermsStrategy(new SplitPaymentStrategy(...));

// 3. Calculate commission
BigDecimal total = calculator.calculateTotalCommission(deal, paymentTerms);
```

### Strategy Selection

Strategies can be selected dynamically based on business rules:

```java
// Select strategy based on deal size
if (deal.getValue().compareTo(new BigDecimal("100000")) >= 0) {
    calculator.setCommissionStrategy(new TieredRateStrategy());
} else {
    calculator.setCommissionStrategy(new StandardRateStrategy(new BigDecimal("10")));
}

// Apply bonus for new customers
if (isNewCustomer(deal)) {
    calculator.setBonusStrategy(new NewCustomerBonusStrategy(new BigDecimal("25")));
}
```

### Calculation Process

The `CommissionCalculator` orchestrates the calculation:

1. **Base Commission** - Calculated using `CommissionStrategy`
2. **Bonus** - Added if `BonusStrategy` is configured and `appliesTo(deal)` returns true
3. **Payment Adjustment** - Applied using `PaymentTermsStrategy` to adjust for payment terms

## Code Examples

### Example 1: Standard Rate

```java
Deal deal = new Deal("Software License", new BigDecimal("50000"), "REP-001");

CommissionCalculator calculator = new CommissionCalculator();
calculator.setCommissionStrategy(new StandardRateStrategy(new BigDecimal("10")));

BigDecimal commission = calculator.calculateTotalCommission(deal, 30);
// Result: $5,000 (10% of $50,000)
```

### Example 2: Tiered Rate with Bonus

```java
Deal deal = new Deal("Enterprise Deal", new BigDecimal("150000"), "REP-002");

CommissionCalculator calculator = new CommissionCalculator();
calculator.setCommissionStrategy(new TieredRateStrategy());
calculator.setBonusStrategy(new QuotaAchievementBonusStrategy(
    new BigDecimal("100000"), new BigDecimal("50")));

BigDecimal commission = calculator.calculateTotalCommission(deal, 30);
// Base: $22,500 (15% tier for deals >= $100k)
// Bonus: $11,250 (50% of base for exceeding $100k quota)
// Total: $33,750
```

### Example 3: Full Strategy Composition

```java
Deal deal = new Deal("New Strategic Deal", new BigDecimal("200000"), "REP-003");

CommissionCalculator calculator = new CommissionCalculator();
calculator.setCommissionStrategy(new TieredRateStrategy());
calculator.setBonusStrategy(new NewCustomerBonusStrategy(new BigDecimal("25")));
calculator.setPaymentTermsStrategy(new SplitPaymentStrategy(new BigDecimal("60")));

BigDecimal commission = calculator.calculateTotalCommission(deal, 45);
// Base: $30,000 (15% of $200k)
// Bonus: $7,500 (25% new customer bonus)
// Subtotal: $37,500
// Immediate payment: $22,500 (60% of $37,500)
```

## Running the Examples

Execute the main class to see all demonstrations:

```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.strategy.StrategyUsage"
```

Or run via Maven test:

```bash
mvn test -Dtest=StrategyUsageTest
```

## Files in This Directory

### Implementation Files

- **`StrategyStructure.java`**
  - Defines the core strategy interfaces
  - Contains `CommissionStrategy`, `BonusStrategy`, and `PaymentTermsStrategy` interfaces
  - Provides interface documentation and method contracts
  - Demonstrates the Strategy pattern structure in its purest form

- **`StrategyImplementation.java`**
  - Implements all concrete strategy classes
  - **Commission Strategies**: `StandardRateStrategy`, `TieredRateStrategy`, `FlatFeeStrategy`, `GrossProfitStrategy`
  - **Bonus Strategies**: `QuotaAchievementBonusStrategy`, `EarlyCloseBonusStrategy`, `NewCustomerBonusStrategy`
  - **Payment Terms Strategies**: `ImmediatePaymentStrategy`, `DeferredPaymentStrategy`, `SplitPaymentStrategy`
  - Contains the `CommissionCalculator` context class
  - Includes detailed comments explaining each strategy's purpose and algorithm

- **`StrategyUsage.java`**
  - Demonstrates practical usage of all strategy types
  - Shows runtime strategy selection
  - Examples of strategy composition
  - Runnable main class for testing strategies

### Documentation Files

- **`README.md`** (this file)
  - Comprehensive guide to the Strategy Pattern implementation
  - Business context and use cases
  - Code examples and best practices

- **`QUESTIONS.md`**
  - Review questions to test understanding
  - Conceptual and practical questions
  - Helps reinforce learning

- **`ANSWERS.md`**
  - Detailed answers to review questions
  - Additional insights and explanations

### Diagram Files

- **`strategy-pattern.puml`**
  - PlantUML diagram of the complete implementation
  - Shows strategy interfaces, concrete strategies, and context
  - Includes pattern component annotations
  - Demonstrates strategy composition flow

- **`basic-strategy.puml`**
  - Generic strategy pattern structure diagram
  - Educational diagram showing core pattern concepts
  - Language and framework agnostic

## Pattern Participants

### 1. Strategy Interfaces (Abstract Strategies)

**`CommissionStrategy`**
- **Role**: Defines the interface for commission calculation algorithms
- **Responsibilities**: Declare `calculateCommission(Deal)` method
- **Implementations**: StandardRateStrategy, TieredRateStrategy, FlatFeeStrategy, GrossProfitStrategy
- **Location**: `StrategyStructure.java:14-20`

**`BonusStrategy`**
- **Role**: Defines the interface for bonus calculation algorithms
- **Responsibilities**: Declare `calculateBonus(BigDecimal, Deal)` and `appliesTo(Deal)` methods
- **Implementations**: QuotaAchievementBonusStrategy, EarlyCloseBonusStrategy, NewCustomerBonusStrategy
- **Location**: `StrategyStructure.java:22-29`

**`PaymentTermsStrategy`**
- **Role**: Defines the interface for payment term adjustment algorithms
- **Responsibilities**: Declare `adjustForPaymentTerms(BigDecimal, int)` method
- **Implementations**: ImmediatePaymentStrategy, DeferredPaymentStrategy, SplitPaymentStrategy
- **Location**: `StrategyStructure.java:31-37`

### 2. Concrete Strategies

**Commission Strategies:**

1. **`StandardRateStrategy`**
   - Applies fixed percentage rate to deal value
   - Configurable rate (e.g., 10%)
   - Most common commission structure
   - Location: `StrategyImplementation.java:44-65`

2. **`TieredRateStrategy`**
   - Applies different rates based on deal size
   - Tiers: 8% (<$10k), 10% ($10k-$50k), 12% ($50k-$100k), 15% (≥$100k)
   - Incentivizes larger deals
   - Location: `StrategyImplementation.java:72-101`

3. **`FlatFeeStrategy`**
   - Pays fixed amount regardless of deal size
   - Useful for small deals or specific product types
   - Location: `StrategyImplementation.java:109-125`

4. **`GrossProfitStrategy`**
   - Calculates commission based on profit margin
   - Encourages profitable sales
   - Location: `StrategyImplementation.java:133-162`

**Bonus Strategies:**

1. **`QuotaAchievementBonusStrategy`**
   - Bonus when sales exceed quota threshold
   - Configurable threshold and bonus percentage
   - Location: `StrategyImplementation.java:171-200`

2. **`EarlyCloseBonusStrategy`**
   - Rewards deals closed before quarter end
   - Fixed bonus amount
   - Location: `StrategyImplementation.java:207-237`

3. **`NewCustomerBonusStrategy`**
   - Bonus for deals with new customers
   - Percentage of base commission
   - Location: `StrategyImplementation.java:244-272`

**Payment Terms Strategies:**

1. **`ImmediatePaymentStrategy`**
   - Full commission paid immediately
   - No adjustment
   - Location: `StrategyImplementation.java:281-292`

2. **`DeferredPaymentStrategy`**
   - Reduces commission based on payment terms
   - Accounts for collection risk
   - Location: `StrategyImplementation.java:300-327`

3. **`SplitPaymentStrategy`**
   - Splits commission between immediate and deferred
   - Configurable immediate percentage
   - Location: `StrategyImplementation.java:334-354`

### 3. Context

**`CommissionCalculator`**
- **Role**: Uses strategies to calculate total commission
- **Responsibilities**:
  - Maintain references to strategy objects
  - Delegate algorithm execution to strategies
  - Orchestrate multi-step calculations (base + bonus + adjustment)
  - Provide strategy configuration methods
- **Methods**:
  - `setCommissionStrategy(CommissionStrategy)`
  - `setBonusStrategy(BonusStrategy)`
  - `setPaymentTermsStrategy(PaymentTermsStrategy)`
  - `calculateTotalCommission(Deal, int)`
  - `getStrategySummary()`
- **Location**: `StrategyImplementation.java:364-455`

### 4. Client

**Usage Code in `StrategyUsage.java`**
- **Role**: Creates and configures strategies
- **Responsibilities**:
  - Select appropriate strategies based on business rules
  - Configure the context (CommissionCalculator) with strategies
  - Invoke calculation through the context
- **Examples**: Runtime strategy selection, strategy composition

## Common Pitfalls to Avoid

### 1. Coupling Context to Concrete Strategies

**❌ Problem:**
```java
CommissionCalculator calculator = new CommissionCalculator();
if (deal.getValue().compareTo(BigDecimal("100000")) >= 0) {
    calculator.commissionStrategy = new TieredRateStrategy(); // Direct field access
}
```

**✅ Solution:**
```java
CommissionCalculator calculator = new CommissionCalculator();
if (deal.getValue().compareTo(new BigDecimal("100000")) >= 0) {
    calculator.setCommissionStrategy(new TieredRateStrategy()); // Use setter
}
```

**Why it matters**: Direct access couples code to implementation details and prevents future changes like validation or logging.

### 2. Forgetting to Set Required Strategies

**❌ Problem:**
```java
CommissionCalculator calculator = new CommissionCalculator();
// Forgot to set commission strategy!
BigDecimal commission = calculator.calculateTotalCommission(deal, 30); // Throws exception
```

**✅ Solution:**
```java
CommissionCalculator calculator = new CommissionCalculator();
calculator.setCommissionStrategy(new StandardRateStrategy(new BigDecimal("10")));
BigDecimal commission = calculator.calculateTotalCommission(deal, 30);
```

**Why it matters**: The implementation throws `IllegalStateException` if commission strategy is not set. Always validate strategy configuration.

### 3. Creating Strategy Instances Repeatedly

**❌ Problem:**
```java
for (Deal deal : deals) {
    calculator.setCommissionStrategy(new StandardRateStrategy(new BigDecimal("10"))); // Creates new instance each time
    BigDecimal commission = calculator.calculateTotalCommission(deal, 30);
}
```

**✅ Solution:**
```java
CommissionStrategy strategy = new StandardRateStrategy(new BigDecimal("10")); // Create once
for (Deal deal : deals) {
    calculator.setCommissionStrategy(strategy); // Reuse instance
    BigDecimal commission = calculator.calculateTotalCommission(deal, 30);
}
```

**Why it matters**: Strategies are stateless and can be reused. Creating new instances wastes memory and CPU.

### 4. Mixing Algorithm Logic in Context

**❌ Problem:**
```java
public BigDecimal calculateTotalCommission(Deal deal, int paymentTerms) {
    BigDecimal base = commissionStrategy.calculateCommission(deal);

    // Don't put algorithm logic in context!
    if (deal.getTitle().contains("New")) {
        base = base.multiply(new BigDecimal("1.25")); // 25% new customer bonus
    }

    return base;
}
```

**✅ Solution:**
```java
public BigDecimal calculateTotalCommission(Deal deal, int paymentTerms) {
    BigDecimal base = commissionStrategy.calculateCommission(deal);

    if (bonusStrategy != null && bonusStrategy.appliesTo(deal)) {
        base = base.add(bonusStrategy.calculateBonus(base, deal));
    }

    return base;
}
```

**Why it matters**: Algorithm logic belongs in strategies, not in the context. The context only orchestrates.

### 5. Not Considering Strategy Composition

**❌ Problem:**
```java
// Creating a new strategy for every combination
public class TieredRateWithNewCustomerBonusStrategy implements CommissionStrategy { ... }
public class TieredRateWithQuotaBonusStrategy implements CommissionStrategy { ... }
public class StandardRateWithNewCustomerBonusStrategy implements CommissionStrategy { ... }
// Combinatorial explosion!
```

**✅ Solution:**
```java
// Compose strategies instead
calculator.setCommissionStrategy(new TieredRateStrategy());
calculator.setBonusStrategy(new NewCustomerBonusStrategy(new BigDecimal("25")));
// Or
calculator.setCommissionStrategy(new StandardRateStrategy(new BigDecimal("10")));
calculator.setBonusStrategy(new QuotaAchievementBonusStrategy(...));
```

**Why it matters**: Composition avoids class explosion and provides flexibility to mix and match strategies.

### 6. Overusing Strategy for Simple Cases

**❌ Problem:**
```java
// Overkill for a simple boolean check
public interface ValidationStrategy {
    boolean isValid(String value);
}

public class NotNullStrategy implements ValidationStrategy {
    public boolean isValid(String value) { return value != null; }
}
```

**✅ Solution:**
```java
// Just use a simple method
public boolean isValid(String value) {
    return value != null;
}
```

**Why it matters**: Strategy Pattern adds complexity. Use it when you have multiple algorithms that need to be interchangeable, not for trivial logic.

### 7. Tight Coupling Through Shared State

**❌ Problem:**
```java
public class StatefulStrategy implements CommissionStrategy {
    private BigDecimal lastCalculation; // Shared state!

    public BigDecimal calculateCommission(Deal deal) {
        lastCalculation = deal.getValue().multiply(rate);
        return lastCalculation;
    }
}
```

**✅ Solution:**
```java
public class StatelessStrategy implements CommissionStrategy {
    private final BigDecimal rate; // Immutable configuration only

    public BigDecimal calculateCommission(Deal deal) {
        return deal.getValue().multiply(rate); // No shared state
    }
}
```

**Why it matters**: Stateful strategies can't be safely reused and create concurrency issues. Keep strategies stateless.

### 8. Exposing Strategy Implementation Details

**❌ Problem:**
```java
public class TieredRateStrategy implements CommissionStrategy {
    public BigDecimal getHighTierRate() { return new BigDecimal("15"); } // Leaking implementation
    // ...
}
```

**✅ Solution:**
```java
public class TieredRateStrategy implements CommissionStrategy {
    private BigDecimal getHighTierRate() { return new BigDecimal("15"); } // Private implementation detail
    // Only expose interface methods
}
```

**Why it matters**: Exposing implementation details defeats the purpose of encapsulation and makes refactoring harder.

## UML Diagram

The `strategy-pattern.puml` file contains a PlantUML diagram showing:
- Strategy interfaces and concrete implementations
- Context (CommissionCalculator) relationships
- Pattern component annotations
- Strategy composition flow

The `basic-strategy.puml` file contains a generic diagram showing:
- Core strategy pattern structure
- Pattern participants and relationships
- Educational annotations

## Design Principles

This implementation demonstrates:

1. **Open/Closed Principle** - Open for extension (new strategies), closed for modification
2. **Single Responsibility** - Each strategy has one reason to change
3. **Dependency Inversion** - Context depends on strategy abstractions, not concrete implementations
4. **Composition over Inheritance** - Behaviors are composed rather than inherited

## Comparison with Related Patterns

| Pattern | Purpose | Key Difference |
|---------|---------|---------------|
| **Strategy** | Encapsulate interchangeable algorithms | Focuses on algorithm variation |
| **State** | Vary behavior based on internal state | Focuses on state-dependent behavior |
| **Template Method** | Define algorithm skeleton with varying steps | Uses inheritance instead of composition |
| **Command** | Encapsulate requests as objects | Focuses on requests, not algorithms |

## Extensions and Variations

### Potential Enhancements

1. **Strategy Registry** - Centralized registry for strategy lookup by name
2. **Chain of Strategies** - Multiple strategies executed in sequence
3. **Composite Strategies** - Strategies that combine other strategies
4. **Strategy Validation** - Validate strategy combinations at configuration time
5. **Strategy Persistence** - Save/load strategy configurations

### Advanced Scenarios

- **A/B Testing** - Different strategies for different user segments
- **Time-based Strategies** - Different strategies based on date/time
- **Role-based Strategies** - Different strategies for different user roles
- **Performance Optimization** - Cache strategy calculations
- **Audit Trail** - Log which strategies were applied and why

## Learning Outcomes

After studying this implementation, you should understand:

1. How to define a family of algorithms with a common interface
2. How to make algorithms interchangeable at runtime
3. How to eliminate complex conditional logic using strategies
4. How to compose multiple strategies for complex behavior
5. How to apply the Open/Closed Principle in practice
6. When to use Strategy Pattern vs. other behavioral patterns

## Conclusion

The Strategy Pattern is a powerful tool for managing algorithmic variation in software systems. This commission calculator implementation demonstrates how the pattern transforms complex, conditional commission logic into a flexible, maintainable, and extensible system.

### Key Takeaways

1. **Encapsulation is Powerful**
   - Each strategy encapsulates a specific algorithm
   - Implementation details are hidden behind simple interfaces
   - Changes to one strategy don't ripple through the system

2. **Runtime Flexibility is Valuable**
   - Strategies can be selected dynamically based on business rules
   - No recompilation needed to change behavior
   - Enables configuration-driven systems

3. **Composition Beats Inheritance**
   - Multiple strategies can be combined for complex behavior
   - Avoids class explosion from inheritance hierarchies
   - Provides maximum flexibility with minimal classes

4. **Open/Closed Principle in Action**
   - System is open for extension (new strategies)
   - System is closed for modification (existing code unchanged)
   - New commission structures don't break existing functionality

5. **Testability is Enhanced**
   - Each strategy is independently testable
   - Mock strategies simplify context testing
   - Isolation reduces test complexity

### Practical Application

In the commission calculator domain, the Strategy Pattern delivers tangible benefits:

- **Business Agility**: New commission structures can be deployed quickly to respond to market changes
- **Reduced Risk**: Changes are isolated, reducing the risk of introducing bugs
- **Clear Audit Trail**: Commission rules are explicit and traceable
- **Easier Compliance**: Commission calculations are transparent and verifiable
- **Cost Efficiency**: Reduced development and maintenance costs

### When to Apply This Pattern

Use the Strategy Pattern when you find yourself:
- Writing complex if/else chains to select algorithms
- Duplicating similar code with minor variations
- Needing to change behavior at runtime
- Wanting to add new algorithms without modifying existing code
- Struggling to test algorithm variations in isolation

### When to Consider Alternatives

Avoid the Strategy Pattern when:
- You have only one or two simple algorithms
- Algorithms never change
- Performance overhead is unacceptable
- The team is unfamiliar with design patterns

### Next Steps

To deepen your understanding:

1. **Experiment**: Modify the code to add new strategies (e.g., accelerator bonuses, team commissions)
2. **Extend**: Implement a strategy registry for dynamic strategy lookup
3. **Optimize**: Add strategy caching for performance
4. **Integrate**: Combine with other patterns (Factory for strategy creation, Decorator for strategy enhancement)
5. **Practice**: Apply the pattern to other domains with algorithmic variation

### Final Thoughts

The Strategy Pattern exemplifies good object-oriented design. It promotes:
- **Separation of concerns** - algorithms separate from usage
- **Single responsibility** - each class has one job
- **Open/Closed Principle** - extensible without modification
- **Dependency inversion** - depend on abstractions, not concretions

By mastering this pattern, you gain a powerful tool for managing complexity, promoting flexibility, and building maintainable software systems. The commission calculator implementation provides a solid foundation for understanding the pattern's mechanics and benefits.

**Remember**: Patterns are tools, not rules. Apply them judiciously when they solve real problems, not as an academic exercise. The best code is simple, clear, and solves the problem at hand—sometimes that means using a pattern, sometimes it doesn't.

---

## References

- **Design Patterns: Elements of Reusable Object-Oriented Software** (Gang of Four)
  - Original Strategy Pattern definition and examples
  - Chapter on Behavioral Patterns

- **Head First Design Patterns** (Freeman & Freeman)
  - Accessible introduction to Strategy Pattern
  - Practical examples and visual explanations

- **Refactoring: Improving the Design of Existing Code** (Fowler)
  - Refactoring to patterns including Strategy
  - When and how to introduce patterns

- **Clean Code** (Martin)
  - Principles of good design that complement pattern usage
  - Avoiding over-engineering

- **Design Patterns Explained** (Shalloway & Trott)
  - Pattern concepts and relationships
  - Practical application guidance

## Questions?

See `QUESTIONS.md` for concept review questions and `ANSWERS.md` for detailed answers.