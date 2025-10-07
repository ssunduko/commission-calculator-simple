# Strategy Pattern - Review Questions

## Conceptual Understanding

### 1. Pattern Definition
**Q1.1:** What is the Strategy Pattern and what problem does it solve?

**Q1.2:** What are the three main components of the Strategy Pattern?

**Q1.3:** How does the Strategy Pattern embody the Open/Closed Principle?

### 2. Pattern vs. Other Patterns
**Q2.1:** What is the key difference between the Strategy Pattern and the State Pattern?

**Q2.2:** How does the Strategy Pattern differ from the Template Method Pattern in terms of implementation approach?

**Q2.3:** When would you choose Strategy Pattern over simple conditional statements (if/else or switch)?

## Implementation Analysis

### 3. Commission Calculator Architecture
**Q3.1:** In the `CommissionCalculator` class, what would happen if you tried to call `calculateTotalCommission()` without setting a `CommissionStrategy`?

**Q3.2:** Why does the `BonusStrategy` interface include an `appliesTo(Deal deal)` method, while the `CommissionStrategy` interface does not?

**Q3.3:** Explain the calculation flow in `CommissionCalculator.calculateTotalCommission()`. In what order are the strategies applied?

### 4. Strategy Implementations
**Q4.1:** Compare `StandardRateStrategy` and `TieredRateStrategy`. When would each be more appropriate?

**Q4.2:** How does `GrossProfitStrategy` differ from other commission strategies in terms of what it incentivizes?

**Q4.3:** In `DeferredPaymentStrategy`, why is the commission reduced based on payment terms?

## Design Decisions

### 5. Multiple Strategy Types
**Q5.1:** Why are there three separate strategy interfaces (`CommissionStrategy`, `BonusStrategy`, `PaymentTermsStrategy`) instead of a single unified interface?

**Q5.2:** What are the advantages of allowing `CommissionCalculator` to use multiple strategies simultaneously?

**Q5.3:** Could you implement a system where strategies are chained together instead of using three separate types? What would be the pros and cons?

### 6. Strategy Selection
**Q6.1:** In `StrategyUsage.demonstrateDynamicStrategySelection()`, how are strategies selected? What criteria are used?

**Q6.2:** What would be a good approach to externalize strategy selection rules (e.g., using configuration files)?

**Q6.3:** How could you implement a strategy registry that maps strategy names to strategy instances?

## Practical Application

### 7. Real-World Scenarios
**Q7.1:** A company wants to offer a "Product Launch Bonus" that gives extra commission for selling a specific product during the first quarter. Which strategy type would you extend, and how would you implement it?

**Q7.2:** How would you modify the system to support region-specific commission rates (e.g., different rates for US vs. EU)?

**Q7.3:** If a sales rep can be assigned to multiple commission plans, how would you modify the `CommissionCalculator` to support this?

### 8. Testing Strategies
**Q8.1:** What are the benefits of the Strategy Pattern for unit testing commission calculations?

**Q8.2:** How would you test that the correct strategy is selected based on deal characteristics?

**Q8.3:** What edge cases should you test for in the `TieredRateStrategy`?

## Advanced Concepts

### 9. Performance and Optimization
**Q9.1:** If commission calculations are performed frequently, what performance optimizations could you apply?

**Q9.2:** Would it make sense to make strategy objects singletons? Why or why not?

**Q9.3:** How could you implement caching for commission calculations while still maintaining strategy flexibility?

### 10. Extension and Evolution
**Q10.1:** How would you add support for "negative commissions" or "commission clawbacks" when deals are cancelled?

**Q10.2:** If you needed to implement a strategy that considers historical performance (e.g., "accelerators" for reps who exceeded quota last quarter), what additional information would the strategies need access to?

**Q10.3:** How would you implement a "combination strategy" that blends multiple commission strategies (e.g., 70% tiered + 30% flat fee)?

## Design Principles

### 11. SOLID Principles
**Q11.1:** Which SOLID principles are demonstrated in this Strategy Pattern implementation? Provide specific examples.

**Q11.2:** Does the current implementation violate any SOLID principles? If so, how could it be improved?

**Q11.3:** How does composition (as used in the Strategy Pattern) support the Dependency Inversion Principle?

### 12. Code Quality
**Q12.1:** What are the benefits of having `getStrategyDescription()` in each strategy implementation?

**Q12.2:** Why is `BigDecimal` used instead of `double` for monetary calculations?

**Q12.3:** How does the Strategy Pattern help maintain the Single Responsibility Principle?

## Integration and Refactoring

### 13. Refactoring Legacy Code
**Q13.1:** You have a legacy `calculateCommission()` method with 15 nested if/else statements. What steps would you take to refactor it using the Strategy Pattern?

**Q13.2:** How would you maintain backward compatibility while migrating to a strategy-based system?

**Q13.3:** What indicators in existing code suggest that the Strategy Pattern would be beneficial?

### 14. System Integration
**Q14.1:** How could you integrate this strategy system with a database to persist commission calculations?

**Q14.2:** If strategies need to access external services (e.g., quota tracking, customer history), how would you provide this access without violating encapsulation?

**Q14.3:** How would you implement an audit trail that logs which strategies were applied and why?

## Critical Thinking

### 15. Trade-offs and Limitations
**Q15.1:** What are the disadvantages of using the Strategy Pattern? When might it add unnecessary complexity?

**Q15.2:** How does the Strategy Pattern affect code navigation and understanding for new developers?

**Q15.3:** What is the overhead of using the Strategy Pattern compared to simple conditional logic?

### 16. Alternative Approaches
**Q16.1:** Could you implement similar functionality using functional interfaces and lambdas in Java? What would be the advantages and disadvantages?

**Q16.2:** How would you implement this using a rules engine instead of the Strategy Pattern?

**Q16.3:** In what scenarios would a configuration-based approach (e.g., using JSON/YAML) be preferable to code-based strategies?