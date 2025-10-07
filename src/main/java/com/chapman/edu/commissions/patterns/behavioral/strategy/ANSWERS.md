# Strategy Pattern - Answers to Review Questions

## Conceptual Understanding

### 1. Pattern Definition

**A1.1:** The Strategy Pattern defines a family of algorithms, encapsulates each one, and makes them interchangeable. It solves the problem of:
- Eliminating complex conditional statements for selecting behaviors
- Allowing algorithm selection at runtime
- Making it easy to add new algorithms without modifying existing code
- Providing alternatives to subclassing for varying behavior

**A1.2:** The three main components are:
1. **Strategy Interface** - Declares the common interface for all supported algorithms
2. **Concrete Strategies** - Implement specific algorithms using the Strategy interface
3. **Context** - Maintains a reference to a Strategy object and delegates algorithm execution to it

**A1.3:** The Strategy Pattern embodies the Open/Closed Principle by:
- Being **open for extension** - New strategies can be added by creating new classes that implement the strategy interface
- Being **closed for modification** - Adding new strategies doesn't require changing the Context or existing strategies
- Example: Adding a new `QuarterlyIncentiveStrategy` requires creating one new class without touching `CommissionCalculator`

### 2. Pattern vs. Other Patterns

**A2.1:** Key differences between Strategy and State:
- **Strategy**: Focuses on interchangeable algorithms; client typically sets the strategy explicitly
- **State**: Focuses on state-dependent behavior; state transitions happen automatically based on context
- **Strategy**: Strategies are independent and don't know about each other
- **State**: States know about and can transition to other states
- Example: Commission strategies don't transition to each other; dispute states do transition (Submitted → UnderReview)

**A2.2:** Strategy vs. Template Method:
- **Strategy**: Uses composition/delegation; algorithms are encapsulated in separate classes
- **Template Method**: Uses inheritance; algorithm steps are defined in subclasses
- **Strategy**: Algorithms can be swapped at runtime
- **Template Method**: Algorithm structure is fixed at compile time
- **Strategy**: More flexible but requires more classes
- **Template Method**: Simpler but less flexible

**A2.3:** Choose Strategy Pattern when:
- You have 3+ algorithms that might grow over time
- Algorithms need to be selected at runtime based on data
- Algorithm logic is complex and should be isolated for testing
- Different algorithms share minimal code (otherwise Template Method might be better)
- You want to avoid code duplication across similar conditional branches

## Implementation Analysis

### 3. Commission Calculator Architecture

**A3.1:** If `calculateTotalCommission()` is called without a `CommissionStrategy`, it throws an `IllegalStateException` with the message "Commission strategy must be set". This is a fail-fast approach that prevents invalid calculations and makes the required dependency explicit.

**A3.2:** `BonusStrategy.appliesTo()` exists because:
- Bonuses are conditional - they only apply to certain deals
- Commission strategies always calculate something for every deal
- `appliesTo()` allows strategies to self-determine eligibility without the calculator needing to know the rules
- Example: `QuotaAchievementBonusStrategy` only applies when `deal.getValue() > quotaThreshold`

**A3.3:** Calculation flow in `calculateTotalCommission()`:
1. **Base Commission** - `commissionStrategy.calculateCommission(deal)` - always executed
2. **Bonus Addition** - If `bonusStrategy != null && bonusStrategy.appliesTo(deal)`, calculate and add bonus
3. **Payment Adjustment** - If `paymentTermsStrategy != null`, adjust the total (base + bonus) for payment terms
4. **Return** - Final adjusted amount

The order matters: bonuses are based on base commission, and payment adjustments affect the total.

### 4. Strategy Implementations

**A4.1:** `StandardRateStrategy` vs. `TieredRateStrategy`:

**StandardRateStrategy:**
- Fixed percentage regardless of deal size
- Simpler, more predictable
- Appropriate for: Consistent compensation, smaller organizations, specific product lines
- Example: 10% on all deals

**TieredRateStrategy:**
- Percentage increases with deal size
- Incentivizes larger deals
- Appropriate for: Scaling organizations, encouraging upsells, enterprise sales
- Example: 8% (<$10k), 10% ($10k-$50k), 12% ($50k-$100k), 15% (≥$100k)

**A4.2:** `GrossProfitStrategy` differs by:
- Calculating commission on **profit** rather than **revenue**
- Incentivizing **profitable** deals, not just large ones
- Encouraging reps to negotiate better terms and control costs
- Requiring knowledge of cost structure (`costPercentage` parameter)
- More aligned with company financial goals
- Example: A $100k deal at 40% cost = $60k profit; 20% commission on profit = $12k (vs. $10k at 10% revenue rate)

**A4.3:** `DeferredPaymentStrategy` reduces commission because:
- **Collection Risk** - Customer may not pay or pay late
- **Time Value of Money** - Payment delay has opportunity cost
- **Incentive Alignment** - Encourages reps to negotiate better payment terms
- Implementation: Reduces commission by `discountRatePerMonth` for each month of payment terms
- Example: $10k commission, Net 90 (3 months), 2% per month = 6% reduction = $9,400

## Design Decisions

### 5. Multiple Strategy Types

**A5.1:** Three separate interfaces exist because:
- **Separation of Concerns** - Each strategy type has a distinct purpose
- **Single Responsibility** - Each interface has one reason to change
- **Flexibility** - Can mix and match strategies independently
- **Clarity** - Clear what each strategy does
- If unified, you'd need complex logic to determine which calculation to perform and strategies would be less focused

**A5.2:** Advantages of multiple simultaneous strategies:
- **Composability** - Complex calculations from simple components
- **Reusability** - Same base strategy can be combined with different bonuses
- **Flexibility** - Enable/disable bonuses without changing base calculation
- **Business Alignment** - Mirrors how compensation is actually structured (base + bonuses + adjustments)
- **Testing** - Each component can be tested independently

**A5.3:** Chain of strategies approach:

**Pros:**
- More extensible - can have unlimited strategies
- Uniform interface - all strategies treated the same
- Pipeline pattern - clear data flow

**Cons:**
- Less type-safe - harder to ensure required strategies are present
- Order matters - must maintain correct sequence
- Less clear - what does each strategy do?
- Harder to access specific strategies (e.g., "what's my base commission?")

**Current approach is better** for this domain because the three calculation types have distinct roles and requirements.

### 6. Strategy Selection

**A6.1:** In `demonstrateDynamicStrategySelection()`, strategies are selected based on:

**Commission Strategy:**
- Deal value ≥ $100k → `TieredRateStrategy` (large deals get better rates)
- Deal value < $10k → `FlatFeeStrategy` (small deals get flat fee)
- Otherwise → `StandardRateStrategy` (medium deals get 10%)

**Bonus Strategy:**
- Title contains "new" → `NewCustomerBonusStrategy`
- Value ≥ $100k → `QuotaAchievementBonusStrategy`

**Payment Strategy:**
- Value ≥ $100k → `SplitPaymentStrategy` (60/40 split)
- Otherwise → `ImmediatePaymentStrategy`

**A6.2:** Externalizing strategy selection rules:

```java
// Configuration file (YAML example)
strategyRules:
  - condition: "deal.value >= 100000"
    commissionStrategy: "TieredRateStrategy"
    bonusStrategy: "QuotaAchievementBonusStrategy"
    paymentStrategy: "SplitPaymentStrategy"
  - condition: "deal.value < 10000"
    commissionStrategy: "FlatFeeStrategy"
    paymentStrategy: "ImmediatePaymentStrategy"
  - condition: "deal.title.contains('new')"
    bonusStrategy: "NewCustomerBonusStrategy"

// Implementation
class StrategySelector {
    private RuleEngine ruleEngine;
    private StrategyFactory strategyFactory;

    public void configureCalculator(CommissionCalculator calc, Deal deal) {
        String commStrategy = ruleEngine.evaluate("commissionStrategy", deal);
        calc.setCommissionStrategy(strategyFactory.create(commStrategy));
        // ... similar for other strategies
    }
}
```

**A6.3:** Strategy registry implementation:

```java
public class StrategyRegistry {
    private static final Map<String, Supplier<CommissionStrategy>> COMMISSION_STRATEGIES = new HashMap<>();

    static {
        COMMISSION_STRATEGIES.put("standard-10", () -> new StandardRateStrategy(new BigDecimal("10")));
        COMMISSION_STRATEGIES.put("tiered", TieredRateStrategy::new);
        COMMISSION_STRATEGIES.put("flat-500", () -> new FlatFeeStrategy(new BigDecimal("500")));
    }

    public static CommissionStrategy getCommissionStrategy(String name) {
        Supplier<CommissionStrategy> supplier = COMMISSION_STRATEGIES.get(name);
        if (supplier == null) {
            throw new IllegalArgumentException("Unknown strategy: " + name);
        }
        return supplier.get();
    }

    public static void register(String name, Supplier<CommissionStrategy> supplier) {
        COMMISSION_STRATEGIES.put(name, supplier);
    }
}

// Usage
calculator.setCommissionStrategy(StrategyRegistry.getCommissionStrategy("tiered"));
```

## Practical Application

### 7. Real-World Scenarios

**A7.1:** Product Launch Bonus implementation:

Extend `BonusStrategy`:

```java
public class ProductLaunchBonusStrategy implements BonusStrategy {
    private final String targetProductId;
    private final LocalDate launchStartDate;
    private final LocalDate launchEndDate;
    private final BigDecimal bonusPercentage;

    @Override
    public BigDecimal calculateBonus(BigDecimal baseCommission, Deal deal) {
        if (!appliesTo(deal)) return BigDecimal.ZERO;
        return baseCommission.multiply(bonusPercentage)
            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean appliesTo(Deal deal) {
        // Check if deal contains target product and is in launch period
        boolean hasProduct = deal.getProducts().stream()
            .anyMatch(p -> p.getProductId().equals(targetProductId));
        boolean inPeriod = deal.getCloseDate() != null &&
            !deal.getCloseDate().isBefore(launchStartDate) &&
            !deal.getCloseDate().isAfter(launchEndDate);
        return hasProduct && inPeriod;
    }

    @Override
    public String getStrategyName() {
        return "Product Launch Bonus (" + bonusPercentage + "% for " +
               targetProductId + " during Q1)";
    }
}
```

**A7.2:** Region-specific commission rates:

**Approach 1: Enhanced strategies with region awareness**
```java
public class RegionalStandardRateStrategy implements CommissionStrategy {
    private final Map<String, BigDecimal> ratesByRegion;

    @Override
    public BigDecimal calculateCommission(Deal deal) {
        String region = getRegionForDeal(deal); // lookup from deal or sales rep
        BigDecimal rate = ratesByRegion.getOrDefault(region, DEFAULT_RATE);
        return deal.getValue().multiply(rate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }
}
```

**Approach 2: Strategy factory by region**
```java
public class RegionalStrategyFactory {
    public CommissionStrategy createForRegion(String region) {
        return switch(region) {
            case "US" -> new StandardRateStrategy(new BigDecimal("10"));
            case "EU" -> new StandardRateStrategy(new BigDecimal("8"));
            case "APAC" -> new TieredRateStrategy(); // different structure
            default -> new StandardRateStrategy(new BigDecimal("10"));
        };
    }
}
```

**A7.3:** Multiple commission plans per sales rep:

```java
public class MultiPlanCommissionCalculator {
    private final List<PlanConfiguration> plans;

    public static class PlanConfiguration {
        private final String planId;
        private final CommissionCalculator calculator;
        private final BigDecimal weight; // For weighted average or sum

        // constructor, getters
    }

    public BigDecimal calculateTotalCommission(Deal deal, int paymentTerms) {
        return plans.stream()
            .map(plan -> {
                BigDecimal planCommission = plan.getCalculator()
                    .calculateTotalCommission(deal, paymentTerms);
                return planCommission.multiply(plan.getWeight());
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

### 8. Testing Strategies

**A8.1:** Strategy Pattern benefits for testing:
- **Isolation** - Each strategy can be tested independently
- **Mock-free** - No need to mock algorithm logic, just strategy interface
- **Focused Tests** - Test one algorithm variant at a time
- **Easy Edge Cases** - Test boundary conditions in specific strategies
- **Regression Safety** - Adding new strategies doesn't break existing tests

Example:
```java
@Test
public void testTieredRateStrategy_SmallDeal() {
    TieredRateStrategy strategy = new TieredRateStrategy();
    Deal deal = new Deal("Test", new BigDecimal("5000"), "REP-001");

    BigDecimal commission = strategy.calculateCommission(deal);

    assertEquals(new BigDecimal("400.00"), commission); // 8% of $5000
}
```

**A8.2:** Testing strategy selection:

```java
@Test
public void testStrategySelection_LargeDeal() {
    Deal largeDeal = new Deal("Enterprise", new BigDecimal("150000"), "REP-001");
    StrategySelector selector = new StrategySelector();

    CommissionCalculator calculator = selector.configureCalculator(largeDeal);

    // Verify correct strategy selected
    assertTrue(calculator.getCommissionStrategy() instanceof TieredRateStrategy);
    assertTrue(calculator.getBonusStrategy() instanceof QuotaAchievementBonusStrategy);
}

@ParameterizedTest
@CsvSource({
    "5000, StandardRateStrategy",
    "50000, StandardRateStrategy",
    "150000, TieredRateStrategy"
})
public void testStrategySelectionByValue(BigDecimal value, String expectedStrategy) {
    // Test strategy selection logic
}
```

**A8.3:** Edge cases for `TieredRateStrategy`:

1. **Boundary Values:**
   - $9,999.99 (just below $10k threshold) → 8%
   - $10,000.00 (exactly at threshold) → 10%
   - $10,000.01 (just above threshold) → 10%

2. **Tier Boundaries:**
   - Test each tier threshold ($10k, $50k, $100k)
   - Test values just below, at, and above each threshold

3. **Edge Inputs:**
   - Zero value → $0 commission
   - Null value → $0 commission (or exception)
   - Negative value → should it be allowed?

4. **Precision:**
   - Verify rounding (e.g., $10,000.005 → $800.00 or $800.01?)
   - Verify scale (2 decimal places)

5. **Large Values:**
   - $1,000,000,000 → verify no overflow
   - Ensure BigDecimal precision maintained

```java
@ParameterizedTest
@CsvSource({
    "9999.99, 799.99",    // 8% tier
    "10000.00, 1000.00",  // 10% tier boundary
    "10000.01, 1000.00",  // 10% tier
    "49999.99, 4999.99",  // 10% tier
    "50000.00, 6000.00",  // 12% tier boundary
    "99999.99, 11999.99", // 12% tier
    "100000.00, 15000.00" // 15% tier boundary
})
public void testTieredRateBoundaries(BigDecimal dealValue, BigDecimal expectedCommission) {
    TieredRateStrategy strategy = new TieredRateStrategy();
    Deal deal = new Deal("Test", dealValue, "REP-001");
    assertEquals(expectedCommission, strategy.calculateCommission(deal));
}
```

## Advanced Concepts

### 9. Performance and Optimization

**A9.1:** Performance optimizations for frequent calculations:

1. **Strategy Reuse:**
   ```java
   // Bad: Creates new strategy each time
   calculator.setCommissionStrategy(new StandardRateStrategy(rate));

   // Good: Reuse strategy instances
   private static final CommissionStrategy STANDARD_10 =
       new StandardRateStrategy(new BigDecimal("10"));
   calculator.setCommissionStrategy(STANDARD_10);
   ```

2. **Caching Results:**
   ```java
   public class CachedCommissionCalculator {
       private final Map<CacheKey, BigDecimal> cache = new ConcurrentHashMap<>();

       public BigDecimal calculateTotalCommission(Deal deal, int paymentTerms) {
           CacheKey key = new CacheKey(deal.getId(), deal.getValue(),
               commissionStrategy.getClass(), paymentTerms);
           return cache.computeIfAbsent(key, k ->
               super.calculateTotalCommission(deal, paymentTerms));
       }
   }
   ```

3. **Lazy Calculation:**
   ```java
   public class LazyCommission {
       private final Supplier<BigDecimal> calculator;
       private BigDecimal cached;

       public BigDecimal get() {
           if (cached == null) {
               cached = calculator.get();
           }
           return cached;
       }
   }
   ```

4. **Parallel Calculation** (if calculating for many deals):
   ```java
   deals.parallelStream()
       .map(deal -> calculator.calculateTotalCommission(deal, 30))
       .collect(Collectors.toList());
   ```

**A9.2:** Strategy objects as singletons:

**When to use singletons:**
- Strategies are **stateless** (like `TieredRateStrategy`)
- Strategies are **immutable**
- Strategies are **expensive to create**
- Memory savings are important

**When NOT to use singletons:**
- Strategies have **state** (e.g., counters, accumulations)
- Strategies are **configurable** with different parameters (like `StandardRateStrategy(rate)`)
- Need **thread-specific** strategy instances

**Current implementation:** Most strategies take parameters (rate, threshold, etc.), so singletons wouldn't work. Could use **flyweight pattern** for parameterized strategies:

```java
public class StrategyFactory {
    private static final Map<StrategyKey, CommissionStrategy> cache = new ConcurrentHashMap<>();

    public static CommissionStrategy getStandardRate(BigDecimal rate) {
        return cache.computeIfAbsent(
            new StrategyKey("standard", rate),
            k -> new StandardRateStrategy(rate)
        );
    }
}
```

**A9.3:** Caching with strategy flexibility:

```java
public class CachingCommissionCalculator {
    private final CommissionCalculator delegate;
    private final Cache<CommissionCacheKey, BigDecimal> cache;

    public BigDecimal calculateTotalCommission(Deal deal, int paymentTerms) {
        // Create cache key that includes strategy identities
        CommissionCacheKey key = new CommissionCacheKey(
            deal.getId(),
            deal.getValue(),
            deal.getLastModifiedDate(),
            delegate.getCommissionStrategy().getClass().getName(),
            delegate.getBonusStrategy() != null ?
                delegate.getBonusStrategy().getClass().getName() : null,
            delegate.getPaymentTermsStrategy() != null ?
                delegate.getPaymentTermsStrategy().getClass().getName() : null,
            paymentTerms
        );

        return cache.get(key, () ->
            delegate.calculateTotalCommission(deal, paymentTerms)
        );
    }

    // Invalidate cache when strategies change
    public void setCommissionStrategy(CommissionStrategy strategy) {
        cache.invalidateAll();
        delegate.setCommissionStrategy(strategy);
    }
}
```

### 10. Extension and Evolution

**A10.1:** Commission clawbacks implementation:

```java
// New strategy type for post-calculation adjustments
public interface CommissionAdjustmentStrategy {
    BigDecimal adjustCommission(BigDecimal originalCommission, Deal deal,
                                DealStatus newStatus);
    boolean appliesTo(Deal deal, DealStatus newStatus);
}

public class CancellationClawbackStrategy implements CommissionAdjustmentStrategy {
    private final BigDecimal clawbackPercentage;
    private final int daysThreshold;

    @Override
    public BigDecimal adjustCommission(BigDecimal originalCommission,
                                       Deal deal, DealStatus newStatus) {
        if (!appliesTo(deal, newStatus)) return originalCommission;

        long daysSinceClose = ChronoUnit.DAYS.between(
            deal.getCloseDate(), LocalDate.now());

        if (daysSinceClose <= daysThreshold) {
            // Full clawback if cancelled within threshold
            return originalCommission.negate(); // negative commission
        } else {
            // Partial clawback
            return originalCommission.multiply(clawbackPercentage)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
                .negate();
        }
    }

    @Override
    public boolean appliesTo(Deal deal, DealStatus newStatus) {
        return deal.getStatus() == DealStatus.CLOSED_WON &&
               newStatus == DealStatus.CANCELLED;
    }
}
```

**A10.2:** Historical performance-based strategies:

Strategies need access to additional context:

```java
public interface HistoricalCommissionStrategy {
    BigDecimal calculateCommission(Deal deal, PerformanceHistory history);
}

public class PerformanceHistory {
    private final String salesRepId;
    private final BigDecimal quarterlyQuota;
    private final BigDecimal quarterlyActual;
    private final List<Deal> previousDeals;

    public boolean exceededQuotaLastQuarter() {
        return quarterlyActual.compareTo(quarterlyQuota) > 0;
    }

    public BigDecimal getAttainmentPercentage() {
        return quarterlyActual.divide(quarterlyQuota, 4, RoundingMode.HALF_UP)
            .multiply(new BigDecimal("100"));
    }
}

public class AcceleratorStrategy implements HistoricalCommissionStrategy {
    private final BigDecimal baseRate;
    private final BigDecimal acceleratedRate;

    @Override
    public BigDecimal calculateCommission(Deal deal, PerformanceHistory history) {
        BigDecimal rate = history.exceededQuotaLastQuarter() ?
            acceleratedRate : baseRate;
        return deal.getValue().multiply(rate)
            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }
}

// Modified calculator
public class EnhancedCommissionCalculator {
    private HistoricalCommissionStrategy strategy;
    private PerformanceHistoryService historyService;

    public BigDecimal calculateTotalCommission(Deal deal) {
        PerformanceHistory history = historyService.getHistory(deal.getSalesRepId());
        return strategy.calculateCommission(deal, history);
    }
}
```

**A10.3:** Combination/blended strategy:

```java
public class BlendedCommissionStrategy implements CommissionStrategy {
    private final List<WeightedStrategy> strategies;

    public static class WeightedStrategy {
        private final CommissionStrategy strategy;
        private final BigDecimal weight; // percentage (e.g., 70 for 70%)

        public WeightedStrategy(CommissionStrategy strategy, BigDecimal weight) {
            this.strategy = strategy;
            this.weight = weight;
        }
    }

    public BlendedCommissionStrategy(List<WeightedStrategy> strategies) {
        // Validate weights sum to 100
        BigDecimal totalWeight = strategies.stream()
            .map(ws -> ws.weight)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalWeight.compareTo(new BigDecimal("100")) != 0) {
            throw new IllegalArgumentException("Weights must sum to 100%");
        }
        this.strategies = strategies;
    }

    @Override
    public BigDecimal calculateCommission(Deal deal) {
        return strategies.stream()
            .map(ws -> {
                BigDecimal strategyCommission = ws.strategy.calculateCommission(deal);
                return strategyCommission.multiply(ws.weight)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public String getStrategyDescription() {
        return "Blended: " + strategies.stream()
            .map(ws -> ws.weight + "% " + ws.strategy.getStrategyDescription())
            .collect(Collectors.joining(", "));
    }
}

// Usage
CommissionStrategy blended = new BlendedCommissionStrategy(Arrays.asList(
    new WeightedStrategy(new TieredRateStrategy(), new BigDecimal("70")),
    new WeightedStrategy(new FlatFeeStrategy(new BigDecimal("500")), new BigDecimal("30"))
));
// Result: 70% of tiered rate + 30% of $500
```

## Design Principles

### 11. SOLID Principles

**A11.1:** SOLID principles demonstrated:

**Single Responsibility Principle (SRP):**
- Each strategy has ONE reason to change: its calculation algorithm
- `TieredRateStrategy` only changes if tier thresholds/rates change
- `CommissionCalculator` only orchestrates; doesn't do calculations

**Open/Closed Principle (OCP):**
- Open for extension: Add new strategies without modifying existing code
- Closed for modification: Can add `SeasonalBonusStrategy` without touching `CommissionCalculator`
- Example in code: Line StrategyStructure.java:14-35 defines interface; new strategies just implement it

**Liskov Substitution Principle (LSP):**
- Any `CommissionStrategy` can replace another without breaking `CommissionCalculator`
- All strategies follow the contract: take `Deal`, return `BigDecimal`
- Preconditions not strengthened, postconditions not weakened

**Interface Segregation Principle (ISP):**
- Three separate strategy interfaces instead of one large interface
- Clients only depend on methods they use
- `CommissionStrategy` doesn't force strategies to implement bonus logic

**Dependency Inversion Principle (DIP):**
- `CommissionCalculator` depends on abstractions (`CommissionStrategy` interface), not concrete implementations
- High-level module (calculator) doesn't depend on low-level modules (concrete strategies)
- Both depend on abstraction (strategy interface)

**A11.2:** Potential SOLID violations and improvements:

**Current Implementation Issues:**

1. **DIP Potential Violation:**
   - Line StrategyImplementation.java:328 - `CommissionCalculator` returns `String` summary with concrete strategy details
   - Could be improved by having strategies provide metadata objects

2. **ISP Consideration:**
   - `BonusStrategy` has `appliesTo()`, `calculateBonus()`, and `getStrategyName()` - might be too coupled
   - Could split into `BonusCalculation` and `BonusEligibility` interfaces

**Improvements:**

```java
// Better separation of concerns
public interface BonusCalculation {
    BigDecimal calculateBonus(BigDecimal baseCommission, Deal deal);
}

public interface BonusEligibility {
    boolean appliesTo(Deal deal);
}

public interface NamedStrategy {
    String getStrategyName();
}

// Strategies implement only what they need
public class QuotaBonus implements BonusCalculation, BonusEligibility, NamedStrategy {
    // ...
}
```

**A11.3:** Composition supporting Dependency Inversion Principle:

**How it works:**
1. **High-level policy** (`CommissionCalculator`) defines what should happen: "calculate commission using a strategy"
2. **Abstraction** (`CommissionStrategy` interface) defines the contract
3. **Low-level details** (concrete strategies) implement the abstraction
4. **Dependency direction**: High-level → Abstraction ← Low-level (both depend on abstraction)

**Without composition (inheritance):**
```java
// High-level depends on low-level - violates DIP
public class TieredCommissionCalculator extends CommissionCalculator {
    @Override
    public BigDecimal calculate(Deal deal) {
        // Tiered logic here
    }
}
```

**With composition (Strategy Pattern):**
```java
// Both depend on abstraction - follows DIP
public class CommissionCalculator {
    private CommissionStrategy strategy; // depends on abstraction

    public BigDecimal calculate(Deal deal) {
        return strategy.calculateCommission(deal); // delegates to abstraction
    }
}

public class TieredRateStrategy implements CommissionStrategy { // depends on abstraction
    // Implementation
}
```

Benefits:
- Calculator doesn't know about concrete strategies
- Can swap strategies at runtime
- Easy to test with mock strategies
- Strategies can be developed independently

### 12. Code Quality

**A12.1:** Benefits of `getStrategyDescription()`:

1. **Debugging/Logging:**
   - Understand which strategy was applied
   - Audit trail for commission calculations
   - Example: "Base commission: $5,000 (Standard Rate: 10% of deal value)"

2. **User Interface:**
   - Display active commission plan to users
   - Show why commission was calculated a certain way
   - Transparency in compensation

3. **Testing:**
   - Verify correct strategy is configured
   - Assert on strategy descriptions in tests

4. **Documentation:**
   - Self-documenting code
   - Strategy describes itself without external docs

5. **Troubleshooting:**
   ```java
   System.out.println(calculator.getStrategySummary());
   // Shows all configured strategies and their parameters
   ```

**A12.2:** Why `BigDecimal` instead of `double`:

**Problems with `double`:**
```java
double result = 0.1 + 0.2;
// result = 0.30000000000000004 (not 0.3!)

double commission = 100000.00 * 0.10;
// Might be 10000.000000000002
```

**Benefits of `BigDecimal`:**
1. **Exact precision** - No floating-point errors
2. **Controlled rounding** - Explicit rounding mode (HALF_UP, HALF_DOWN, etc.)
3. **Scale control** - Exact decimal places (e.g., 2 for currency)
4. **Audit compliance** - Financial calculations must be exact
5. **Legal requirements** - Money calculations must be precise

**Example:**
```java
// Double - WRONG for money
double commission = 100000.00 * 0.10; // might be 10000.000000000002
double rounded = Math.round(commission * 100.0) / 100.0; // still imprecise

// BigDecimal - CORRECT for money
BigDecimal dealValue = new BigDecimal("100000.00");
BigDecimal rate = new BigDecimal("0.10");
BigDecimal commission = dealValue.multiply(rate)
    .setScale(2, RoundingMode.HALF_UP); // exactly 10000.00
```

**A12.3:** Strategy Pattern supporting Single Responsibility:

**Without Strategy Pattern:**
```java
public class CommissionCalculator {
    public BigDecimal calculate(Deal deal) {
        if (deal.getValue().compareTo(new BigDecimal("100000")) >= 0) {
            // Tiered rate logic - RESPONSIBILITY 1
            if (deal.getValue().compareTo(new BigDecimal("100000")) >= 0) {
                return deal.getValue().multiply(new BigDecimal("0.15"));
            } else if (deal.getValue().compareTo(new BigDecimal("50000")) >= 0) {
                return deal.getValue().multiply(new BigDecimal("0.12"));
            }
            // ...
        } else if (deal.getType().equals("PROFIT_SHARE")) {
            // Profit logic - RESPONSIBILITY 2
            BigDecimal costs = calculateCosts(deal);
            BigDecimal profit = deal.getValue().subtract(costs);
            return profit.multiply(new BigDecimal("0.20"));
        }
        // More conditions - MORE RESPONSIBILITIES
    }

    // VIOLATION: Multiple reasons to change:
    // 1. Tiered rate thresholds change
    // 2. Profit calculation logic changes
    // 3. New commission types added
    // 4. Rate percentages change
}
```

**With Strategy Pattern:**
```java
// Calculator has ONE responsibility: orchestrate strategies
public class CommissionCalculator {
    private CommissionStrategy strategy;

    public BigDecimal calculate(Deal deal) {
        return strategy.calculateCommission(deal);
    }
    // Changes only if orchestration logic changes
}

// Each strategy has ONE responsibility: its algorithm
public class TieredRateStrategy implements CommissionStrategy {
    public BigDecimal calculateCommission(Deal deal) {
        // Tiered logic
    }
    // Changes only if tiered rate logic changes
}

public class GrossProfitStrategy implements CommissionStrategy {
    public BigDecimal calculateCommission(Deal deal) {
        // Profit logic
    }
    // Changes only if profit calculation changes
}
```

**Result:**
- Each class has **one reason to change**
- Changes are **localized** to the relevant strategy
- **No cascade effects** - changing one strategy doesn't affect others
- **Easier testing** - test each responsibility in isolation

## Integration and Refactoring

### 13. Refactoring Legacy Code

**A13.1:** Steps to refactor legacy code with 15 nested if/else:

**Step 1: Identify the algorithms**
```java
// Legacy code
public BigDecimal calculateCommission(Deal deal) {
    if (deal.getType().equals("STANDARD")) {
        if (deal.getValue() > 100000) {
            return deal.getValue() * 0.15;
        } else if (deal.getValue() > 50000) {
            return deal.getValue() * 0.12;
        } else {
            return deal.getValue() * 0.10;
        }
    } else if (deal.getType().equals("PROFIT")) {
        // ...
    } else if (deal.getType().equals("FLAT")) {
        // ...
    }
    // 12 more conditions...
}

// Analysis: 3+ distinct algorithms identified
// 1. Tiered rate (based on value)
// 2. Profit-based
// 3. Flat fee
```

**Step 2: Extract methods (preparatory refactoring)**
```java
public BigDecimal calculateCommission(Deal deal) {
    if (deal.getType().equals("STANDARD")) {
        return calculateStandardCommission(deal);
    } else if (deal.getType().equals("PROFIT")) {
        return calculateProfitCommission(deal);
    } else if (deal.getType().equals("FLAT")) {
        return calculateFlatCommission(deal);
    }
    // ...
}

private BigDecimal calculateStandardCommission(Deal deal) {
    if (deal.getValue() > 100000) {
        return deal.getValue() * 0.15;
    } else if (deal.getValue() > 50000) {
        return deal.getValue() * 0.12;
    } else {
        return deal.getValue() * 0.10;
    }
}
```

**Step 3: Create strategy interface**
```java
public interface CommissionStrategy {
    BigDecimal calculateCommission(Deal deal);
}
```

**Step 4: Convert extracted methods to strategies**
```java
public class StandardCommissionStrategy implements CommissionStrategy {
    @Override
    public BigDecimal calculateCommission(Deal deal) {
        if (deal.getValue().compareTo(new BigDecimal("100000")) > 0) {
            return deal.getValue().multiply(new BigDecimal("0.15"));
        } else if (deal.getValue().compareTo(new BigDecimal("50000")) > 0) {
            return deal.getValue().multiply(new BigDecimal("0.12"));
        } else {
            return deal.getValue().multiply(new BigDecimal("0.10"));
        }
    }
}

// Repeat for other strategies...
```

**Step 5: Replace conditional with strategy selection**
```java
// Before
public BigDecimal calculateCommission(Deal deal) {
    if (deal.getType().equals("STANDARD")) {
        return calculateStandardCommission(deal);
    }
    // ...
}

// After
private final Map<String, CommissionStrategy> strategies = Map.of(
    "STANDARD", new StandardCommissionStrategy(),
    "PROFIT", new ProfitCommissionStrategy(),
    "FLAT", new FlatCommissionStrategy()
);

public BigDecimal calculateCommission(Deal deal) {
    CommissionStrategy strategy = strategies.get(deal.getType());
    if (strategy == null) {
        throw new IllegalArgumentException("Unknown deal type: " + deal.getType());
    }
    return strategy.calculateCommission(deal);
}
```

**Step 6: Add tests for each strategy**
```java
@Test
public void testStandardStrategy_LargeValue() {
    StandardCommissionStrategy strategy = new StandardCommissionStrategy();
    Deal deal = new Deal("Test", new BigDecimal("150000"), "REP-001");

    BigDecimal result = strategy.calculateCommission(deal);

    assertEquals(new BigDecimal("22500.00"), result);
}
```

**Step 7: Refactor to use dependency injection**
```java
public class CommissionCalculator {
    private final Map<String, CommissionStrategy> strategies;

    public CommissionCalculator(Map<String, CommissionStrategy> strategies) {
        this.strategies = strategies;
    }

    public BigDecimal calculateCommission(Deal deal) {
        CommissionStrategy strategy = strategies.get(deal.getType());
        return strategy.calculateCommission(deal);
    }
}
```

**A13.2:** Maintaining backward compatibility:

**Approach 1: Facade/Adapter**
```java
// Old interface - must maintain
public class LegacyCommissionService {
    private final CommissionCalculator newCalculator;

    @Deprecated
    public double calculateCommission(DealDTO dealDto) {
        // Convert DTO to domain model
        Deal deal = convertToDeal(dealDto);

        // Use new strategy-based calculator
        BigDecimal result = newCalculator.calculateCommission(deal);

        // Convert back to legacy format
        return result.doubleValue();
    }
}
```

**Approach 2: Strangler Fig Pattern**
```java
public class CommissionService {
    private final LegacyCalculator legacyCalculator;
    private final CommissionCalculator newCalculator;
    private final FeatureToggle toggle;

    public BigDecimal calculate(Deal deal) {
        if (toggle.isEnabled("new-commission-strategy")) {
            return newCalculator.calculateCommission(deal);
        } else {
            return legacyCalculator.calculate(deal);
        }
    }
}
```

**Approach 3: Parallel Run**
```java
public BigDecimal calculate(Deal deal) {
    BigDecimal legacyResult = legacyCalculator.calculate(deal);
    BigDecimal newResult = newCalculator.calculateCommission(deal);

    // Log differences
    if (!legacyResult.equals(newResult)) {
        logger.warn("Calculation mismatch for deal {}: legacy={}, new={}",
            deal.getId(), legacyResult, newResult);
    }

    // Return legacy result until confident in new implementation
    return legacyResult;
}
```

**Migration steps:**
1. Add new strategy-based system alongside legacy
2. Run both in parallel, compare results
3. Gradually enable new system with feature toggle
4. Monitor for issues
5. Deprecate legacy system
6. Remove legacy code

**A13.3:** Indicators that suggest Strategy Pattern:

**Code Smells:**
1. **Long Method with Conditionals:**
   ```java
   // RED FLAG: Method with 10+ conditional branches
   public void process(Deal deal) {
       if (condition1) {
           // 20 lines
       } else if (condition2) {
           // 20 lines
       } else if (condition3) {
           // 20 lines
       }
       // ... 7 more conditions
   }
   ```

2. **Type Codes / Enums with Behavior:**
   ```java
   // RED FLAG: Enum values imply different algorithms
   public enum CommissionType {
       STANDARD, TIERED, PROFIT_BASED, FLAT_FEE, CUSTOM
   }

   // And then...
   if (type == CommissionType.STANDARD) { ... }
   else if (type == CommissionType.TIERED) { ... }
   ```

3. **Repeated Similar Conditionals:**
   ```java
   // RED FLAG: Same condition appears in multiple methods
   public BigDecimal calculate(Deal deal) {
       if (deal.getType().equals("TIERED")) { ... }
   }

   public String getDescription(Deal deal) {
       if (deal.getType().equals("TIERED")) { ... }
   }

   public boolean validate(Deal deal) {
       if (deal.getType().equals("TIERED")) { ... }
   }
   ```

4. **Switch Statements on Type:**
   ```java
   // RED FLAG: Switch on type to select behavior
   switch (deal.getCommissionPlanType()) {
       case STANDARD:
           return calculateStandard(deal);
       case TIERED:
           return calculateTiered(deal);
       case PROFIT:
           return calculateProfit(deal);
       // Adding new type requires modifying this switch
   }
   ```

5. **Parallel Class Hierarchies:**
   ```java
   // RED FLAG: Subclasses that only differ in one algorithm
   class StandardDealProcessor extends DealProcessor {
       protected BigDecimal calculateCommission() { /* standard logic */ }
   }
   class TieredDealProcessor extends DealProcessor {
       protected BigDecimal calculateCommission() { /* tiered logic */ }
   }
   ```

**Business Indicators:**
1. **Multiple Variations** - "We have 5 different ways to calculate commissions"
2. **Frequent Changes** - "Marketing wants to try a new commission structure next quarter"
3. **Runtime Selection** - "Commission type depends on the sales rep's plan"
4. **A/B Testing** - "We want to test two commission strategies simultaneously"
5. **Plugin Architecture** - "Third-party integrations need to provide their own calculation"

**Metrics:**
- **Cyclomatic Complexity** > 10 in calculation methods
- **Number of conditional branches** > 5
- **Lines of code** > 100 in a single method
- **Duplication** - Similar logic in multiple places

### 14. System Integration

**A14.1:** Database integration for persisting calculations:

```java
// Entity for persisting calculation results
@Entity
public class CommissionCalculationRecord {
    @Id
    private String id;

    private String dealId;
    private String salesRepId;
    private BigDecimal baseCommission;
    private BigDecimal bonus;
    private BigDecimal finalCommission;

    // Strategy information
    private String commissionStrategyClass;
    private String commissionStrategyConfig; // JSON
    private String bonusStrategyClass;
    private String bonusStrategyConfig;

    private LocalDateTime calculatedAt;
    private String calculatedBy;

    // Getters/setters
}

// Service layer
@Service
public class CommissionPersistenceService {
    private final CommissionCalculator calculator;
    private final CommissionRepository repository;

    @Transactional
    public CommissionCalculationRecord calculateAndPersist(Deal deal, int paymentTerms) {
        // Calculate
        BigDecimal baseCommission = calculator.getCommissionStrategy()
            .calculateCommission(deal);
        BigDecimal bonus = calculator.getBonusStrategy() != null ?
            calculator.getBonusStrategy().calculateBonus(baseCommission, deal) :
            BigDecimal.ZERO;
        BigDecimal finalCommission = calculator.calculateTotalCommission(deal, paymentTerms);

        // Create record
        CommissionCalculationRecord record = new CommissionCalculationRecord();
        record.setId(UUID.randomUUID().toString());
        record.setDealId(deal.getId());
        record.setSalesRepId(deal.getSalesRepId());
        record.setBaseCommission(baseCommission);
        record.setBonus(bonus);
        record.setFinalCommission(finalCommission);

        // Capture strategy information for audit
        record.setCommissionStrategyClass(
            calculator.getCommissionStrategy().getClass().getName());
        record.setCommissionStrategyConfig(
            serializeStrategy(calculator.getCommissionStrategy()));

        record.setCalculatedAt(LocalDateTime.now());

        // Persist
        return repository.save(record);
    }

    private String serializeStrategy(CommissionStrategy strategy) {
        // Serialize strategy configuration to JSON
        // e.g., {"type": "StandardRate", "rate": "10"}
        return objectMapper.writeValueAsString(strategy);
    }
}

// Retrieval
@Service
public class CommissionQueryService {
    private final CommissionRepository repository;

    public List<CommissionCalculationRecord> getCalculationsForDeal(String dealId) {
        return repository.findByDealId(dealId);
    }

    public BigDecimal getTotalCommissionsForRep(String repId, LocalDate start, LocalDate end) {
        return repository.findByRepIdAndDateRange(repId, start, end)
            .stream()
            .map(CommissionCalculationRecord::getFinalCommission)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

**A14.2:** Providing strategies access to external services:

**Problem:** Strategies need data from external systems without creating tight coupling.

**Solution 1: Context Object Pattern**
```java
// Enhanced context with external data
public class CommissionContext {
    private final Deal deal;
    private final QuotaService quotaService;
    private final CustomerHistoryService customerHistoryService;
    private final ExchangeRateService exchangeRateService;

    public CommissionContext(Deal deal,
                            QuotaService quotaService,
                            CustomerHistoryService customerHistoryService,
                            ExchangeRateService exchangeRateService) {
        this.deal = deal;
        this.quotaService = quotaService;
        this.customerHistoryService = customerHistoryService;
        this.exchangeRateService = exchangeRateService;
    }

    // Getters for deal and services
    public Deal getDeal() { return deal; }
    public QuotaService getQuotaService() { return quotaService; }
    // ...
}

// Strategy uses context
public class QuotaAwareBonusStrategy implements BonusStrategy {
    @Override
    public BigDecimal calculateBonus(BigDecimal baseCommission, CommissionContext context) {
        // Access quota service through context
        Quota quota = context.getQuotaService()
            .getQuotaForRep(context.getDeal().getSalesRepId());

        if (context.getDeal().getValue().compareTo(quota.getAmount()) > 0) {
            return baseCommission.multiply(new BigDecimal("0.5"));
        }
        return BigDecimal.ZERO;
    }
}
```

**Solution 2: Strategy Factory with Dependency Injection**
```java
@Component
public class StrategyFactory {
    private final QuotaService quotaService;
    private final CustomerHistoryService customerHistoryService;

    @Autowired
    public StrategyFactory(QuotaService quotaService,
                          CustomerHistoryService customerHistoryService) {
        this.quotaService = quotaService;
        this.customerHistoryService = customerHistoryService;
    }

    public CommissionStrategy createStrategy(String type) {
        return switch(type) {
            case "quota-aware" -> new QuotaAwareBonusStrategy(quotaService);
            case "customer-history" -> new CustomerHistoryStrategy(customerHistoryService);
            default -> new StandardRateStrategy(new BigDecimal("10"));
        };
    }
}

// Strategy with injected dependency
public class QuotaAwareBonusStrategy implements BonusStrategy {
    private final QuotaService quotaService;

    public QuotaAwareBonusStrategy(QuotaService quotaService) {
        this.quotaService = quotaService;
    }

    @Override
    public BigDecimal calculateBonus(BigDecimal baseCommission, Deal deal) {
        Quota quota = quotaService.getQuotaForRep(deal.getSalesRepId());
        // ...
    }
}
```

**Solution 3: Event-Driven with Data Enrichment**
```java
// Enrich deal with external data before calculation
@Service
public class DealEnrichmentService {
    private final QuotaService quotaService;
    private final CustomerHistoryService customerHistoryService;

    public EnrichedDeal enrich(Deal deal) {
        EnrichedDeal enriched = new EnrichedDeal(deal);
        enriched.setQuota(quotaService.getQuotaForRep(deal.getSalesRepId()));
        enriched.setCustomerHistory(customerHistoryService.getHistory(deal.getCustomerId()));
        return enriched;
    }
}

// Strategy operates on enriched data
public class QuotaAwareBonusStrategy implements BonusStrategy {
    @Override
    public BigDecimal calculateBonus(BigDecimal baseCommission, EnrichedDeal deal) {
        // Data already available, no service calls needed
        if (deal.getQuota() != null &&
            deal.getValue().compareTo(deal.getQuota().getAmount()) > 0) {
            return baseCommission.multiply(new BigDecimal("0.5"));
        }
        return BigDecimal.ZERO;
    }
}
```

**Best Practice:** Use **Context Object** or **Data Enrichment** to keep strategies pure and testable.

**A14.3:** Implementing audit trail:

```java
// Audit event
public class CommissionCalculationEvent {
    private final String calculationId;
    private final String dealId;
    private final LocalDateTime timestamp;
    private final List<StrategyApplication> strategiesApplied;
    private final BigDecimal finalAmount;

    public static class StrategyApplication {
        private final String strategyType;
        private final String strategyClass;
        private final String strategyDescription;
        private final BigDecimal inputAmount;
        private final BigDecimal outputAmount;
        private final String reasoning;
    }
}

// Auditable calculator
public class AuditableCommissionCalculator {
    private final CommissionCalculator delegate;
    private final AuditService auditService;

    public BigDecimal calculateTotalCommission(Deal deal, int paymentTerms) {
        String calculationId = UUID.randomUUID().toString();
        List<StrategyApplication> applications = new ArrayList<>();

        // Track base commission
        CommissionStrategy commStrategy = delegate.getCommissionStrategy();
        BigDecimal baseCommission = commStrategy.calculateCommission(deal);
        applications.add(new StrategyApplication(
            "COMMISSION",
            commStrategy.getClass().getSimpleName(),
            commStrategy.getStrategyDescription(),
            deal.getValue(),
            baseCommission,
            "Base commission calculation"
        ));

        // Track bonus if applicable
        BonusStrategy bonusStrategy = delegate.getBonusStrategy();
        BigDecimal totalBeforePayment = baseCommission;
        if (bonusStrategy != null && bonusStrategy.appliesTo(deal)) {
            BigDecimal bonus = bonusStrategy.calculateBonus(baseCommission, deal);
            applications.add(new StrategyApplication(
                "BONUS",
                bonusStrategy.getClass().getSimpleName(),
                bonusStrategy.getStrategyName(),
                baseCommission,
                bonus,
                "Bonus applied: " + (bonusStrategy.appliesTo(deal) ? "eligible" : "not eligible")
            ));
            totalBeforePayment = baseCommission.add(bonus);
        }

        // Track payment adjustment
        PaymentTermsStrategy paymentStrategy = delegate.getPaymentTermsStrategy();
        BigDecimal finalAmount = totalBeforePayment;
        if (paymentStrategy != null) {
            finalAmount = paymentStrategy.adjustForPaymentTerms(totalBeforePayment, paymentTerms);
            applications.add(new StrategyApplication(
                "PAYMENT_ADJUSTMENT",
                paymentStrategy.getClass().getSimpleName(),
                paymentStrategy.getAdjustmentDescription(),
                totalBeforePayment,
                finalAmount,
                "Adjusted for " + paymentTerms + " day payment terms"
            ));
        }

        // Create and publish audit event
        CommissionCalculationEvent event = new CommissionCalculationEvent(
            calculationId,
            deal.getId(),
            LocalDateTime.now(),
            applications,
            finalAmount
        );

        auditService.recordCalculation(event);

        return finalAmount;
    }
}

// Audit service
@Service
public class AuditService {
    private final AuditRepository repository;
    private final EventPublisher eventPublisher;

    public void recordCalculation(CommissionCalculationEvent event) {
        // Persist to database
        repository.save(toEntity(event));

        // Publish for real-time monitoring
        eventPublisher.publish("commission.calculated", event);

        // Log for debugging
        logger.info("Commission calculation {}: {} strategies applied, final amount: {}",
            event.getCalculationId(),
            event.getStrategiesApplied().size(),
            event.getFinalAmount());
    }

    public List<CommissionCalculationEvent> getAuditTrail(String dealId) {
        return repository.findByDealId(dealId).stream()
            .map(this::fromEntity)
            .collect(Collectors.toList());
    }
}

// Query audit trail
@RestController
public class AuditController {
    private final AuditService auditService;

    @GetMapping("/api/deals/{dealId}/commission-audit")
    public ResponseEntity<AuditTrailResponse> getAuditTrail(@PathVariable String dealId) {
        List<CommissionCalculationEvent> events = auditService.getAuditTrail(dealId);
        return ResponseEntity.ok(new AuditTrailResponse(events));
    }
}
```

## Critical Thinking

### 15. Trade-offs and Limitations

**A15.1:** Disadvantages of Strategy Pattern and when it adds unnecessary complexity:

**Disadvantages:**

1. **Increased Number of Classes:**
   - Each algorithm requires its own class
   - Can lead to "class explosion" with many strategies
   - Example: 10 commission types = 10+ strategy classes

2. **Overhead for Simple Cases:**
   - If you only have 2-3 simple algorithms that rarely change
   - Example: `if (isPremium) return value * 0.15; else return value * 0.10;`
   - Strategy Pattern is overkill here

3. **Client Awareness:**
   - Client must know about different strategies to select them
   - Increases coupling between client and strategy types
   - Alternatively, need a factory or registry layer

4. **Communication Overhead:**
   - Strategy and Context must share data
   - May require passing large context objects
   - All data needed by any strategy must be available

5. **Performance:**
   - Method call indirection (minimal in modern JVMs)
   - Cannot inline strategy code at compile time
   - Extra object creation if strategies are not reused

**When It's Unnecessary:**

1. **Stable Algorithms:**
   - Only 1-2 variations that never change
   - Example: Always 10% commission, never varies
   - Simple constant or calculation suffices

2. **Simple Conditionals:**
   - 2-3 line if/else that's clear and stable
   ```java
   // This is fine, don't over-engineer:
   BigDecimal commission = isPremium ?
       value.multiply(new BigDecimal("0.15")) :
       value.multiply(new BigDecimal("0.10"));
   ```

3. **Tightly Coupled Algorithms:**
   - Algorithms share 90% of code, only differ slightly
   - Template Method might be better
   - Example: Same calculation but different rounding rules

4. **One-Time Use:**
   - Algorithm used in exactly one place
   - No need for abstraction or reuse
   - Inline it

5. **Functional Approach Suffices:**
   - Simple lambda can replace strategy
   ```java
   // Instead of strategy classes:
   calculator.setCalculator(deal -> deal.getValue().multiply(rate));
   ```

**Guideline:** Use Strategy Pattern when you have:
- 3+ distinct algorithms
- Algorithms likely to change or expand
- Need runtime selection
- Algorithms are complex (>10 lines)
- Need to test algorithms independently

**A15.2:** Impact on code navigation and understanding:

**Challenges for New Developers:**

1. **Indirection:**
   ```java
   // Not obvious what calculation happens
   BigDecimal commission = calculator.calculateTotalCommission(deal, 30);

   // Must trace to find:
   // 1. What strategy is set?
   // 2. Where is strategy configured?
   // 3. Which concrete class is used?
   ```

2. **Scattered Logic:**
   - Calculation logic distributed across multiple classes
   - Can't see all options in one place
   - Must navigate through multiple files

3. **Runtime Binding:**
   - IDE "Find Usages" shows all strategy implementations
   - Can't determine which one runs without runtime context
   - Debugging requires breakpoints in strategy selection logic

4. **Steeper Learning Curve:**
   - Must understand pattern before understanding code
   - Need to grasp interface → implementation relationship
   - Context → Strategy delegation flow

**Mitigation Strategies:**

1. **Clear Documentation:**
   ```java
   /**
    * Commission Calculator using Strategy Pattern.
    *
    * Available Strategies:
    * - StandardRateStrategy: Fixed percentage
    * - TieredRateStrategy: Rates based on deal size
    * - GrossProfitStrategy: Commission on profit margin
    *
    * See: StrategyRegistry for all registered strategies
    * See: StrategySelector for selection logic
    */
   public class CommissionCalculator { ... }
   ```

2. **Registry/Catalog:**
   ```java
   public class StrategyRegistry {
       public static List<StrategyInfo> getAllStrategies() {
           return List.of(
               new StrategyInfo("standard", StandardRateStrategy.class,
                   "Fixed percentage rate"),
               new StrategyInfo("tiered", TieredRateStrategy.class,
                   "Rates based on deal size tiers"),
               // ...
           );
       }
   }
   ```

3. **Logging:**
   ```java
   public BigDecimal calculate(Deal deal) {
       logger.debug("Using strategy: {}", strategy.getClass().getSimpleName());
       logger.debug("Strategy description: {}", strategy.getStrategyDescription());
       BigDecimal result = strategy.calculateCommission(deal);
       logger.debug("Result: {}", result);
       return result;
   }
   ```

4. **IDE Support:**
   - Use clear naming: `TieredRateCommissionStrategy` (not `Strategy1`)
   - Add JavaDoc with examples
   - Include package-info.java with overview

5. **Visual Documentation:**
   - UML diagrams showing relationships
   - Flow charts for strategy selection
   - Decision trees for which strategy applies

**A15.3:** Overhead compared to simple conditional logic:

**Runtime Overhead:**

1. **Memory:**
   - Strategy objects consume memory
   - Calculator holds references to 1-3 strategies
   - Minimal if strategies are stateless and reused
   - Example: 3 strategy references ≈ 24 bytes (on 64-bit JVM)

2. **CPU:**
   - Virtual method call (interface dispatch)
   - Modern JVMs optimize this (JIT compilation, inlining)
   - Typically <1 nanosecond overhead
   - Negligible unless in tight loop with millions of iterations

3. **Object Creation:**
   - If creating new strategy instances each time: wasteful
   - If reusing instances: minimal overhead
   ```java
   // Bad: creates object every call
   calculator.setStrategy(new StandardRateStrategy(rate));

   // Good: reuse
   private static final CommissionStrategy STANDARD_10 =
       new StandardRateStrategy(new BigDecimal("10"));
   calculator.setStrategy(STANDARD_10);
   ```

**Development Overhead:**

1. **Initial Setup:**
   - Define interface
   - Create concrete classes
   - Set up configuration/selection logic
   - More code than simple if/else

2. **Maintenance:**
   - More files to navigate
   - More complex project structure
   - But: easier to add new algorithms

**Comparison:**

```java
// Simple conditional: ~10 lines, 1 method, 1 class
public BigDecimal calculateCommission(Deal deal) {
    if (deal.getValue().compareTo(new BigDecimal("100000")) >= 0) {
        return deal.getValue().multiply(new BigDecimal("0.15"));
    } else if (deal.getValue().compareTo(new BigDecimal("50000")) >= 0) {
        return deal.getValue().multiply(new BigDecimal("0.12"));
    } else {
        return deal.getValue().multiply(new BigDecimal("0.10"));
    }
}

// Strategy Pattern: ~100 lines, 3 methods, 4 classes
// + Interface (10 lines)
// + TieredRateStrategy (30 lines)
// + StandardRateStrategy (20 lines)
// + Calculator (40 lines)
```

**When Overhead is Worth It:**
- Algorithms are complex (>20 lines each)
- Need to test algorithms independently
- Algorithms change frequently
- Need runtime selection based on data
- Multiple algorithms (3+)

**When It's Not Worth It:**
- Algorithms are simple (1-2 lines)
- Only 1-2 variations
- Never changes
- Performance-critical tight loop

**Rule of Thumb:**
- For 1-2 simple, stable algorithms: use conditional
- For 3+ or complex algorithms: use Strategy Pattern
- For frequently changing logic: use Strategy Pattern

### 16. Alternative Approaches

**A16.1:** Using functional interfaces and lambdas:

**Strategy Pattern with Lambdas:**

```java
// Functional interface (Java 8+)
@FunctionalInterface
public interface CommissionCalculator {
    BigDecimal calculate(Deal deal);
}

// Usage with lambdas
public class Calculator {
    private CommissionCalculator strategy;

    public void setStrategy(CommissionCalculator strategy) {
        this.strategy = strategy;
    }

    public BigDecimal calculate(Deal deal) {
        return strategy.calculate(deal);
    }
}

// Client code
Calculator calc = new Calculator();

// Lambda strategies - no classes needed!
calc.setStrategy(deal ->
    deal.getValue().multiply(new BigDecimal("0.10")));

calc.setStrategy(deal -> {
    if (deal.getValue().compareTo(new BigDecimal("100000")) >= 0) {
        return deal.getValue().multiply(new BigDecimal("0.15"));
    } else {
        return deal.getValue().multiply(new BigDecimal("0.10"));
    }
});

// Method reference
calc.setStrategy(this::calculateTiered);

// Using built-in Function interface
Function<Deal, BigDecimal> strategy = deal -> deal.getValue().multiply(rate);
```

**Advantages:**
1. **Less Boilerplate** - No strategy classes needed
2. **Concise** - Define strategy inline where used
3. **Flexible** - Easy to create one-off strategies
4. **Modern Java** - Leverages Java 8+ features
5. **Composable** - Can combine with streams, optionals, etc.

```java
// Compose strategies
CommissionCalculator base = deal -> deal.getValue().multiply(new BigDecimal("0.10"));
CommissionCalculator bonus = deal -> base.calculate(deal).multiply(new BigDecimal("1.5"));
```

**Disadvantages:**
1. **No State** - Lambdas can't easily hold configuration
   ```java
   // How do you configure this?
   calc.setStrategy(deal -> deal.getValue().multiply(WHAT_RATE?));

   // Must capture from enclosing scope
   BigDecimal rate = new BigDecimal("0.10");
   calc.setStrategy(deal -> deal.getValue().multiply(rate));
   ```

2. **No Metadata** - Can't get description, name, etc.
   ```java
   // Class-based strategy
   strategy.getStrategyDescription(); // "Standard Rate: 10%"

   // Lambda strategy
   strategy.??? // No way to get description
   ```

3. **Complex Logic** - Lambdas get messy for multi-line algorithms
   ```java
   calc.setStrategy(deal -> {
       // 50 lines of logic here gets ugly
       // Better as a class
   });
   ```

4. **Reusability** - Harder to register and reuse
   ```java
   // How to register lambdas in a registry?
   Map<String, CommissionCalculator> strategies = new HashMap<>();
   strategies.put("standard", ???); // Each time creates new lambda
   ```

5. **Testing** - Can't test lambda in isolation
   ```java
   // Class-based
   @Test
   public void testTieredStrategy() {
       TieredRateStrategy strategy = new TieredRateStrategy();
       // test it
   }

   // Lambda - must test through calculator
   ```

**Best Approach: Hybrid**
```java
public class CommissionStrategies {
    // Complex strategies as classes
    public static class TieredRateStrategy implements CommissionCalculator {
        // Full implementation with state, metadata, etc.
    }

    // Simple strategies as lambdas
    public static final CommissionCalculator FLAT_500 =
        deal -> new BigDecimal("500");

    public static final CommissionCalculator STANDARD_10 =
        deal -> deal.getValue().multiply(new BigDecimal("0.10"));

    // Parameterized lambda factory
    public static CommissionCalculator standardRate(BigDecimal rate) {
        return deal -> deal.getValue().multiply(rate)
            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }
}

// Usage
calculator.setStrategy(new TieredRateStrategy()); // Complex
calculator.setStrategy(CommissionStrategies.FLAT_500); // Simple
calculator.setStrategy(CommissionStrategies.standardRate(new BigDecimal("12"))); // Parameterized
```

**When to Use Each:**
- **Lambda**: Simple, stateless, one-off calculations
- **Class**: Complex logic, needs state/configuration, metadata, testing

**A16.2:** Rules Engine approach:

**Rules Engine Implementation:**

```json
// Rules configuration (JSON/YAML)
{
  "commissionRules": [
    {
      "id": "tiered-large",
      "priority": 1,
      "condition": "deal.value >= 100000",
      "action": {
        "type": "percentage",
        "value": 15
      }
    },
    {
      "id": "tiered-medium",
      "priority": 2,
      "condition": "deal.value >= 50000 && deal.value < 100000",
      "action": {
        "type": "percentage",
        "value": 12
      }
    },
    {
      "id": "tiered-small",
      "priority": 3,
      "condition": "deal.value < 50000",
      "action": {
        "type": "percentage",
        "value": 10
      }
    },
    {
      "id": "new-customer-bonus",
      "priority": 10,
      "condition": "deal.title.contains('New')",
      "action": {
        "type": "bonus-percentage",
        "value": 25
      }
    }
  ]
}
```

```java
// Rules engine
public class CommissionRulesEngine {
    private final List<Rule> rules;

    public static class Rule {
        private String id;
        private int priority;
        private String condition; // Expression to evaluate
        private Action action;
    }

    public static class Action {
        private String type; // "percentage", "flat", "bonus-percentage"
        private BigDecimal value;
    }

    public BigDecimal calculateCommission(Deal deal) {
        BigDecimal baseCommission = BigDecimal.ZERO;
        BigDecimal bonus = BigDecimal.ZERO;

        // Sort by priority
        List<Rule> sortedRules = rules.stream()
            .sorted(Comparator.comparing(Rule::getPriority))
            .collect(Collectors.toList());

        // Evaluate rules
        for (Rule rule : sortedRules) {
            if (evaluateCondition(rule.getCondition(), deal)) {
                switch (rule.getAction().getType()) {
                    case "percentage":
                        baseCommission = deal.getValue()
                            .multiply(rule.getAction().getValue())
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                        break;
                    case "bonus-percentage":
                        bonus = bonus.add(baseCommission
                            .multiply(rule.getAction().getValue())
                            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
                        break;
                    // ... other action types
                }
            }
        }

        return baseCommission.add(bonus);
    }

    private boolean evaluateCondition(String condition, Deal deal) {
        // Use expression evaluator (e.g., Spring EL, MVEL, JEval)
        ExpressionParser parser = new SpelExpressionParser();
        Expression expr = parser.parseExpression(condition);

        EvaluationContext context = new StandardEvaluationContext(deal);
        return Boolean.TRUE.equals(expr.getValue(context, Boolean.class));
    }
}
```

**Advantages:**
1. **No Code Changes** - Add/modify rules without recompiling
2. **Business User Friendly** - Non-developers can modify rules
3. **Centralized** - All rules in one place
4. **Runtime Changes** - Reload rules without restart
5. **Audit Trail** - Easy to log which rules fired
6. **Declarative** - What, not how

**Disadvantages:**
1. **Limited Expressiveness** - Complex logic is hard to express
2. **Type Safety** - Lost compile-time checking
3. **Performance** - Expression evaluation overhead
4. **Debugging** - Harder to debug than code
5. **Version Control** - JSON/YAML changes less visible than code
6. **Testing** - Can't unit test rules as easily
7. **Security** - Must sanitize user-provided expressions

**When to Use Rules Engine:**
- Business users need to modify rules
- Rules change very frequently
- Need runtime configuration
- Regulatory/compliance requirements (audit trail)
- Centralized rule management across systems

**When to Use Strategy Pattern:**
- Complex algorithms better expressed in code
- Need compile-time safety
- Developers manage the logic
- Performance critical
- Want IDE support, refactoring, etc.

**A16.3:** Configuration-based approach (JSON/YAML):

**Configuration-Based Strategy:**

```yaml
# commission-config.yaml
commissionPlans:
  - id: standard-sales-rep
    name: Standard Sales Rep Plan
    strategies:
      commission:
        type: standardRate
        parameters:
          rate: 10
      bonus:
        type: quotaAchievement
        parameters:
          threshold: 100000
          bonusPercentage: 50
      paymentTerms:
        type: immediate

  - id: enterprise-account-exec
    name: Enterprise Account Executive Plan
    strategies:
      commission:
        type: tiered
        parameters:
          tiers:
            - threshold: 0
              rate: 8
            - threshold: 10000
              rate: 10
            - threshold: 50000
              rate: 12
            - threshold: 100000
              rate: 15
      bonus:
        type: composite
        parameters:
          bonuses:
            - type: quotaAchievement
              threshold: 100000
              bonusPercentage: 50
            - type: newCustomer
              bonusPercentage: 25
      paymentTerms:
        type: split
        parameters:
          immediatePercentage: 60

  - id: channel-partner
    name: Channel Partner Plan
    strategies:
      commission:
        type: grossProfit
        parameters:
          rate: 20
          costPercentage: 40
      paymentTerms:
        type: deferred
        parameters:
          discountRatePerMonth: 2
```

```java
// Configuration loader and strategy factory
@Component
public class ConfigurationBasedStrategyFactory {

    @Value("classpath:commission-config.yaml")
    private Resource configFile;

    private Map<String, PlanConfiguration> plans;

    @PostConstruct
    public void loadConfiguration() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        CommissionConfiguration config = mapper.readValue(
            configFile.getInputStream(),
            CommissionConfiguration.class
        );
        this.plans = config.getPlans().stream()
            .collect(Collectors.toMap(PlanConfiguration::getId, Function.identity()));
    }

    public CommissionCalculator createCalculator(String planId) {
        PlanConfiguration plan = plans.get(planId);
        if (plan == null) {
            throw new IllegalArgumentException("Unknown plan: " + planId);
        }

        CommissionCalculator calculator = new CommissionCalculator();

        // Build commission strategy from config
        calculator.setCommissionStrategy(
            buildCommissionStrategy(plan.getStrategies().getCommission())
        );

        // Build bonus strategy if configured
        if (plan.getStrategies().getBonus() != null) {
            calculator.setBonusStrategy(
                buildBonusStrategy(plan.getStrategies().getBonus())
            );
        }

        // Build payment terms strategy if configured
        if (plan.getStrategies().getPaymentTerms() != null) {
            calculator.setPaymentTermsStrategy(
                buildPaymentTermsStrategy(plan.getStrategies().getPaymentTerms())
            );
        }

        return calculator;
    }

    private CommissionStrategy buildCommissionStrategy(StrategyConfig config) {
        return switch(config.getType()) {
            case "standardRate" -> new StandardRateStrategy(
                new BigDecimal(config.getParameters().get("rate").toString())
            );
            case "tiered" -> buildTieredStrategy(config);
            case "grossProfit" -> new GrossProfitStrategy(
                new BigDecimal(config.getParameters().get("rate").toString()),
                new BigDecimal(config.getParameters().get("costPercentage").toString())
            );
            default -> throw new IllegalArgumentException("Unknown strategy type: " + config.getType());
        };
    }

    // Similar for bonus and payment terms strategies...
}

// Usage
@Service
public class CommissionService {
    private final ConfigurationBasedStrategyFactory factory;

    public BigDecimal calculateCommission(Deal deal, String planId) {
        CommissionCalculator calculator = factory.createCalculator(planId);
        return calculator.calculateTotalCommission(deal, 30);
    }
}
```

**Advantages:**

1. **Flexibility:**
   - Change plans without code changes
   - Deploy new plans via configuration
   - A/B test different plans easily

2. **Separation of Concerns:**
   - Business logic (code) vs. business rules (config)
   - Developers write code, business analysts configure

3. **Multi-tenancy:**
   - Different configurations for different customers
   - Easy to customize per client

4. **Version Control:**
   - Track plan changes in git
   - Rollback to previous configurations

5. **Dynamic Loading:**
   ```java
   @Scheduled(fixedRate = 60000)
   public void reloadConfiguration() {
       loadConfiguration();
       logger.info("Commission plans reloaded");
   }
   ```

**Disadvantages:**

1. **Validation:**
   - Must validate configuration at runtime
   - Errors not caught at compile time
   ```java
   // Typo in config file won't be caught until runtime
   strategies:
     commission:
       type: "standradRate" # TYPO!
   ```

2. **Limited Complexity:**
   - Complex logic hard to express in YAML/JSON
   - May need escape hatches to code

3. **Discoverability:**
   - IDE doesn't help with configuration
   - Need documentation for available options

4. **Testing:**
   - Must test configuration parsing
   - Integration tests more important

**When to Use Configuration:**
- Plans change frequently
- Multiple environments (dev/staging/prod with different plans)
- Multi-tenant (different plans per customer)
- Business users manage plans
- Need runtime changes without deployment

**When to Use Code:**
- Complex algorithms
- Want type safety and IDE support
- Algorithms rarely change
- Need compile-time guarantees

**Best Practice: Hybrid Approach**
```java
// Code defines available strategy types
public interface CommissionStrategy { ... }
public class StandardRateStrategy implements CommissionStrategy { ... }
public class TieredRateStrategy implements CommissionStrategy { ... }

// Configuration composes strategies
// YAML says: "Use StandardRateStrategy with rate=10"
// Factory builds it: new StandardRateStrategy(new BigDecimal("10"))
```

This gives you:
- **Type safety** from code
- **Flexibility** from configuration
- **Best of both worlds**