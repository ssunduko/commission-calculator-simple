# BDD (Behavior-Driven Development) with JBehave - Answers

## Fundamental BDD Concepts

### Answer 1: What is BDD?

**Behavior-Driven Development (BDD)** is a software development methodology that extends TDD by emphasizing:

**Core Principle**: Using natural language specifications to drive development and testing.

**Key Differences from TDD:**

| Aspect | TDD | BDD |
|--------|-----|-----|
| **Language** | Technical code | Natural language (Gherkin) |
| **Focus** | Implementation correctness | Business behavior |
| **Audience** | Developers | Everyone (business + tech) |
| **Tests Named** | `testCalculate()` | `Given..When..Then..` |
| **Goal** | Verify code works | Verify it delivers business value |

**Problem BDD Solves:**
TDD ensures code works correctly, but doesn't ensure you're building the RIGHT thing. BDD bridges the communication gap between:
- Business stakeholders (who know what they want)
- Developers (who build it)
- QA (who verify it)

**Example:**

TDD Test:
```java
@Test
void testCalculateCommission() {
    assertEquals(10000, service.calculate(100000, 0.10));
}
```

BDD Scenario:
```gherkin
Given a closed deal worth $100,000
When I calculate the commission at 10% rate
Then the commission amount should be $10,000.00
```

The BDD version is readable by non-developers and describes WHAT should happen, not HOW it's implemented.

### Answer 2: Given-When-Then

**Given-When-Then** is the core structure of BDD scenarios:

**Given** (Context/Setup):
- Sets up the initial state
- Describes preconditions
- Answers: "What's the situation?"
- Example: `Given a closed deal worth $100,000`

**When** (Action):
- Describes the behavior being tested
- The action taken by the user/system
- Answers: "What happens?"
- Example: `When I calculate the commission`

**Then** (Outcome/Verification):
- Describes the expected result
- What should be observed after the action
- Answers: "What should I see?"
- Example: `Then the commission should be $10,000.00`

**Why This Structure Matters:**

1. **Clarity**: Forces explicit thinking about context, action, and outcome
2. **Consistency**: Same format for all scenarios
3. **Readability**: Natural language flow
4. **Testability**: Maps directly to test phases (Arrange-Act-Assert)

**Complete Example:**
```gherkin
Scenario: Apply performance bonus to commission
Given a sales representative named "Alice" with ID "REP-004"
And a deal worth $100,000 for product "Enterprise Solution"
And the deal status is "CLOSED"
And the base commission rate is 10%
When I apply a performance bonus of 15%
Then the base commission should be $10,000.00
And the total commission with bonus should be $11,500.00
```

### Answer 3: Gherkin Language

**Gherkin** is a business-readable, domain-specific language for describing software behavior.

**Key Features:**

1. **Natural Language**: Written in plain English (or other human languages)
2. **Structured**: Uses specific keywords (Given, When, Then, And, But, Scenario, Feature)
3. **Executable**: Can be parsed and executed by BDD frameworks
4. **Business-Focused**: No technical jargon

**Significance of Natural Language:**

**For Business Stakeholders:**
- Can read and understand specifications
- Can verify scenarios match their expectations
- Can contribute to writing scenarios
- No programming knowledge required

**For Developers:**
- Clear requirements before coding
- Reduced ambiguity
- Shared vocabulary with business
- Living documentation

**For QA:**
- Test cases written before development
- Clear acceptance criteria
- Automated test execution

**Example of Significance:**

Business Requirement (Ambiguous):
> "Calculate commission for deals"

Gherkin Scenario (Clear):
```gherkin
Scenario: Calculate commission for closed deal
Given a deal worth $100,000
And the deal status is "CLOSED"
When I calculate the commission at 10% rate
Then the commission amount should be $10,000.00
```

The Gherkin version eliminates ambiguity:
- What deals? CLOSED deals
- How much commission? 10% of deal value
- Example with numbers: $100,000 → $10,000

### Answer 4: Living Documentation

**Living Documentation** means documentation that:
- **Stays Up-to-Date**: Automatically reflects current system behavior
- **Is Executable**: Can be run as tests
- **Is Accurate**: Must pass for code to be deployed
- **Is Readable**: Written in natural language

**How BDD Scenarios Serve as Documentation:**

**Traditional Documentation Problems:**
```
Documentation written → Code changes → Documentation outdated → Nobody trusts docs
```

**BDD Living Documentation:**
```
Scenarios written → Code implements scenarios → Scenarios run as tests → Scenarios must pass
```

If scenarios are out of sync with code, tests fail!

**Example:**

Scenario as Documentation:
```gherkin
Scenario: Tiered commission rates based on deal size
When I process the following deals:
|dealValue|expectedRate|expectedCommission|
|$30,000  |8%          |$2,400.00         |
|$75,000  |10%         |$7,500.00         |
|$150,000 |12%         |$18,000.00        |
Then all commissions should be calculated correctly
```

This scenario:
1. **Documents** the tier structure (8%, 10%, 12%)
2. **Provides examples** of each tier
3. **Is tested** automatically
4. **Must pass** or the build breaks
5. **Never goes stale** because it's executable

**Benefits:**
- Always accurate (or tests fail)
- Business stakeholders can verify behavior
- New team members learn system by reading scenarios
- Regression prevention

### Answer 5: BDD vs TDD - When to Use Each

**Use TDD When:**

✅ **Low-Level Algorithms**
```java
// TDD is better for this
@Test
void testBinarySearch() {
    assertEquals(5, binarySearch(array, target));
}
```

✅ **Technical Utilities**
- String parsers
- Data structures
- Helper methods

✅ **Performance-Critical Code**
- Tests specific implementations
- Benchmarks and optimizations

✅ **Internal APIs**
- Developer-to-developer interfaces
- No business stakeholder involvement

**Use BDD When:**

✅ **Business Features**
```gherkin
# BDD is better for this
Scenario: Calculate tiered commission
Given a deal worth $75,000
When I calculate the commission
Then it should use the 10% tier rate
```

✅ **User-Facing Features**
- Features end users interact with
- Business workflows

✅ **Complex Business Rules**
- Multiple conditions
- Requires stakeholder clarification

✅ **Acceptance Criteria**
- Defining "done"
- User story verification

**Can They Be Used Together?**

**YES! In fact, they complement each other:**

**BDD (Feature Level):**
```gherkin
Scenario: Full commission calculation
Given a deal worth $120,000
When I calculate full commission with 10% bonus
Then the commission should be $15,840.00
```

**TDD (Implementation Level):**
```java
@Test
void testTierRateSelection() {
    assertEquals(TIER_3_RATE, selectTierRate(120000));
}

@Test
void testBonusMultiplier() {
    assertEquals(1.10, calculateMultiplier(0.10));
}
```

**Best Practice: Layered Testing**
```
┌─────────────────────────────┐
│  BDD Scenarios (Features)   │ ← Business-readable
├─────────────────────────────┤
│  Integration Tests          │ ← Components together
├─────────────────────────────┤
│  TDD Unit Tests             │ ← Low-level logic
└─────────────────────────────┘
```

## JBehave Framework

### Answer 6: What is JBehave?

**JBehave** is a BDD framework for Java that enables writing and executing behavior specifications.

**Core Components:**

**1. Story Files (.story)**
```gherkin
Narrative:
As a sales manager
I want to calculate commissions
So that I can compensate sales reps

Scenario: Basic commission calculation
Given a deal worth $100,000
When I calculate commission at 10%
Then commission should be $10,000.00
```

**2. Step Definitions (Java Classes)**
```java
public class CommissionSteps {
    @Given("a deal worth $$value")
    public void givenDeal(BigDecimal value) {
        this.deal = new Deal(value);
    }

    @When("I calculate commission at $rate%")
    public void whenCalculate(BigDecimal rate) {
        this.result = service.calculate(deal, rate);
    }

    @Then("commission should be $$expected")
    public void thenVerify(BigDecimal expected) {
        assertEquals(expected, result);
    }
}
```

**3. Configuration**
```java
@RunWith(JUnit4StoryRunner.class)
public class StoriesTest extends JUnitStory {
    @Override
    public Configuration configuration() {
        return new MostUsefulConfiguration()
            .useStoryLoader(new LoadFromClasspath())
            .useStoryReporterBuilder(new StoryReporterBuilder()
                .withFormats(CONSOLE, HTML));
    }

    @Override
    public InjectableStepsFactory stepsFactory() {
        return new InstanceStepsFactory(configuration(),
            new CommissionSteps());
    }
}
```

**4. Reports**
- HTML reports showing scenario results
- Console output for quick feedback
- XML for CI/CD integration

**How JBehave Enables BDD:**

1. **Reads** story files in natural language
2. **Matches** steps to Java methods via annotations
3. **Executes** scenarios as automated tests
4. **Generates** reports showing results
5. **Integrates** with JUnit for familiar test execution

**Key Features:**
- **Gherkin Support**: Standard BDD syntax
- **Parameter Extraction**: Automatically converts types
- **Tables**: Data-driven testing with ExamplesTable
- **Lifecycle Hooks**: @BeforeScenario, @AfterScenario
- **Meta Tags**: Categorize and filter scenarios
- **Extensible**: Custom reporters, step matchers

### Answer 7: Story Files

**Story Files** (`.story`) are text files containing BDD scenarios written in Gherkin.

**Structure:**

```gherkin
Narrative:                          ← Why this feature exists
As a [role]
I want [feature]
So that [benefit]

Lifecycle:                          ← Setup for all scenarios (optional)
Before:
Given common setup

Meta:                               ← Story-level metadata
@category commission
@version 1.0

Scenario: First scenario name        ← Concrete example
Meta:                               ← Scenario-level metadata
@priority high

Given [context]                     ← Setup
And [more context]
When [action]                       ← What happens
Then [outcome]                      ← Expected result
And [more outcome]

Scenario: Second scenario name       ← Another example
...
```

**Who Should Read and Understand Them?**

**✅ Business Stakeholders**
- Product owners
- Business analysts
- Domain experts
- **Why**: They define what the system should do

**✅ Developers**
- Understand requirements
- Implement features to match scenarios
- **Why**: They build what scenarios describe

**✅ QA Engineers**
- Test scenarios
- Add edge cases
- **Why**: They verify behavior

**✅ New Team Members**
- Learn system behavior
- Understand business rules
- **Why**: Scenarios document the system

**✅ Anyone interested in system behavior**
- Managers checking feature status
- Support staff understanding features
- **Why**: Natural language, no tech knowledge needed

**Key Point**: If business stakeholders can't understand story files, you're not doing BDD correctly. The whole point is shared language.

**Example Good vs Bad:**

❌ **Bad** (Technical):
```gherkin
Given database record with status=2
When POST /api/commission with payload
Then response code 200 and JSON contains commission_amount
```

✅ **Good** (Business Language):
```gherkin
Given a closed deal worth $100,000
When I calculate the commission
Then the commission should be $10,000.00
```

### Answer 8: Step Annotations

**@Given, @When, @Then** annotations map Gherkin steps to Java methods.

**How They Work:**

**In Story File:**
```gherkin
Given a sales representative named "John Doe" with ID "REP-001"
When I calculate the commission at 10% rate
Then the commission amount should be $10,000.00
```

**In Java:**
```java
@Given("a sales representative named \"$name\" with ID \"$id\"")
public void givenSalesRep(String name, String id) {
    this.salesRepName = name;
    this.salesRepId = id;
}

@When("I calculate the commission at $rate% rate")
public void whenCalculate(BigDecimal rate) {
    BigDecimal rateDecimal = rate.divide(new BigDecimal("100"));
    this.result = service.calculateCommission(dealValue, rateDecimal);
}

@Then("the commission amount should be $$expectedAmount")
public void thenVerify(BigDecimal expectedAmount) {
    assertEquals(expectedAmount, result);
}
```

**Mapping Process:**

1. **JBehave reads** story file
2. **Encounters step**: "Given a sales representative named \"John Doe\" with ID \"REP-001\""
3. **Searches** for matching @Given annotation
4. **Finds match**: Pattern matches with $name and $id as variables
5. **Extracts parameters**: name="John Doe", id="REP-001"
6. **Converts types**: Both are Strings (automatic)
7. **Invokes method**: `givenSalesRep("John Doe", "REP-001")`

**Pattern Matching:**

**Literal Text**: Must match exactly
```java
@Given("the system is initialized")
```
Matches: `Given the system is initialized`
Doesn't match: `Given the system is ready`

**Parameters**: $variable extracts value
```java
@Given("a deal worth $$value")
```
Matches: `Given a deal worth $100000`
Extracts: value = 100000

**Type Conversion**: JBehave converts automatically
```java
@When("I calculate at $rate% rate")
public void when(BigDecimal rate) { ... }
```
"10" → BigDecimal(10)

**Quoted Strings**: Use \"$var\" for strings with spaces
```java
@Given("a product named \"$name\"")
```
Matches: `Given a product named "Enterprise Software"`
Extracts: name = "Enterprise Software"

### Answer 9: Parameter Extraction

**In the step:**
```java
@Given("a deal worth $$value for product \"$productName\"")
public void givenDeal(BigDecimal value, String productName) {
    this.deal = new Deal(productName, value);
}
```

**How JBehave Extracts Parameters:**

**Step 1: Pattern Matching**
- JBehave sees `$$value` and `\"$productName\"` as placeholders
- `$$value`: Expects a dollar-prefixed number
- `\"$productName\"`: Expects a quoted string

**Step 2: Text Parsing**
Story file: `Given a deal worth $100,000 for product "Enterprise Software"`
- Matches pattern
- Extracts: `$100,000` and `"Enterprise Software"`

**Step 3: Type Conversion**
- `$100,000` → Remove $ and commas → "100000"
- Convert "100000" to `BigDecimal(100000)`
- `"Enterprise Software"` → Remove quotes → "Enterprise Software"
- Already a String, no conversion needed

**Step 4: Method Invocation**
```java
givenDeal(new BigDecimal("100000"), "Enterprise Software")
```

**Types That Can Be Automatically Converted:**

✅ **Primitive Types**:
- `int`, `long`, `double`, `float`, `boolean`

✅ **Wrapper Classes**:
- `Integer`, `Long`, `Double`, `Float`, `Boolean`

✅ **String**:
- No conversion needed

✅ **BigDecimal**, **BigInteger**:
- From numeric strings

✅ **Enums**:
- From enum constant names

✅ **Date/Time**:
- `LocalDate`, `LocalDateTime` (with proper format)

✅ **Collections**:
- `List`, `Set` (with custom converters)

**Example with Multiple Types:**
```java
@Given("deal ID $id worth $$value created on $date with status $status")
public void given(String id, BigDecimal value, LocalDate date, DealStatus status) {
    // id: String
    // value: BigDecimal
    // date: LocalDate (yyyy-MM-dd format)
    // status: Enum DealStatus
}
```

Story: `Given deal ID "D-001" worth $50000 created on 2024-10-01 with status CLOSED`

Extracts:
- id = "D-001"
- value = BigDecimal(50000)
- date = LocalDate.of(2024, 10, 1)
- status = DealStatus.CLOSED

### Answer 10: Examples Tables

**ExamplesTable** enables data-driven testing in BDD scenarios.

**In Story File:**
```gherkin
Scenario: Tiered commission rates

When I process the following deals:
|dealValue|expectedRate|expectedCommission|
|$30,000  |8%          |$2,400.00         |
|$75,000  |10%         |$7,500.00         |
|$150,000 |12%         |$18,000.00        |
Then all commissions should be calculated correctly
```

**In Java:**
```java
@When("I process the following deals: $dealsTable")
public void whenProcessDeals(ExamplesTable dealsTable) {
    for (Map<String, String> row : dealsTable.getRows()) {
        // Access each column by header name
        String dealValueStr = row.get("dealValue")
            .replace("$", "")
            .replace(",", "");
        BigDecimal dealValue = new BigDecimal(dealValueStr);

        String expectedRateStr = row.get("expectedRate").replace("%", "");
        BigDecimal expectedRate = new BigDecimal(expectedRateStr)
            .divide(new BigDecimal("100"));

        String expectedCommissionStr = row.get("expectedCommission")
            .replace("$", "")
            .replace(",", "");
        BigDecimal expectedCommission = new BigDecimal(expectedCommissionStr);

        // Process each row
        BigDecimal actual = service.calculateTieredCommission(dealValue);

        // Verify
        assertEquals(expectedCommission, actual,
            "Commission for " + dealValue + " should be " + expectedCommission);
    }
}
```

**Why Tables Are Useful:**

**1. Multiple Examples Without Duplication**

Without table:
```gherkin
Scenario: Small deal commission
Given a deal worth $30,000
When I calculate commission
Then it should be $2,400.00

Scenario: Medium deal commission
Given a deal worth $75,000
When I calculate commission
Then it should be $7,500.00

Scenario: Large deal commission
Given a deal worth $150,000
When I calculate commission
Then it should be $18,000.00
```

With table:
```gherkin
Scenario: Tiered commissions
When I process deals:
|value    |commission  |
|$30,000  |$2,400.00   |
|$75,000  |$7,500.00   |
|$150,000 |$18,000.00  |
Then all should calculate correctly
```

**2. Clear Input/Output Relationships**
- Easy to see patterns
- Business stakeholders can verify examples
- Easy to add more test cases

**3. Edge Case Coverage**
```gherkin
When I validate deals closed:
|daysAgo|expectedEligibility|
|30     |eligible           |
|90     |eligible           |
|91     |ineligible         |
|100    |ineligible         |
```

**4. Documentation**
- Table serves as specification
- Shows all covered cases
- Examples are the documentation

## Practical Application

### Answer 46: Scenario for New Feature

**Requirement**: "Deals closed on weekends get a 5% bonus."

**BDD Scenario:**

```gherkin
Narrative:
As a sales manager
I want to reward sales reps who close deals on weekends
So that I can incentivize working outside normal business hours

Scenario: Weekend deal receives bonus
Meta:
@category bonus
@priority medium

Given a sales representative named "Sarah Connor" with ID "REP-007"
And a deal worth $100,000 for product "Enterprise License"
And the deal status is "CLOSED"
And the deal was closed on Saturday
When I calculate the full commission
Then the base commission should be $12,000.00
And the weekend bonus should be $600.00
And the total commission should be $12,600.00

Scenario: Weekday deal does not receive weekend bonus
Given a sales representative named "John Connor" with ID "REP-008"
And a deal worth $100,000 for product "Standard License"
And the deal status is "CLOSED"
And the deal was closed on Monday
When I calculate the full commission
Then the base commission should be $12,000.00
And the weekend bonus should be $0.00
And the total commission should be $12,000.00

Scenario: Sunday deals also receive weekend bonus
Given a sales representative with ID "REP-009"
And a deal worth $50,000 closed on Sunday
When I calculate commission
Then it should include the 5% weekend bonus

Scenario: Both weekend days are eligible
When I process deals closed on:
|dayOfWeek |dealValue|baseCommission|weekendBonus|totalCommission|
|Saturday  |$100,000 |$12,000.00    |$600.00     |$12,600.00     |
|Sunday    |$100,000 |$12,000.00    |$600.00     |$12,600.00     |
|Monday    |$100,000 |$12,000.00    |$0.00       |$12,000.00     |
Then all calculations should match expected values
```

**Key Elements:**
- **Clear business rule**: 5% bonus for weekends
- **Concrete examples**: Specific dollar amounts
- **Positive and negative cases**: Weekend vs weekday
- **Edge cases**: Both Saturday and Sunday
- **Table for multiple cases**: Comprehensive coverage

### Answer 47: Step Definition for Weekend Bonus

**Java Step Definitions:**

```java
@Given("the deal was closed on $dayOfWeek")
public void givenDealClosedOnDay(String dayOfWeek) {
    LocalDate closeDate;

    switch (dayOfWeek.toLowerCase()) {
        case "saturday":
            closeDate = getNextOrCurrentSaturday();
            break;
        case "sunday":
            closeDate = getNextOrCurrentSunday();
            break;
        case "monday":
            closeDate = getNextOrCurrentMonday();
            break;
        // ... other days
        default:
            throw new IllegalArgumentException("Unknown day: " + dayOfWeek);
    }

    if (currentDeal != null) {
        currentDeal.setCloseDate(closeDate);
    }
}

@When("I calculate the full commission")
public void whenCalculateFullCommission() {
    // Calculate base commission
    baseCommission = commissionService.calculateTieredCommission(
        currentDeal.getValue());

    // Check if weekend and apply bonus
    if (isWeekend(currentDeal.getCloseDate())) {
        weekendBonus = baseCommission.multiply(new BigDecimal("0.05"));
        totalCommission = baseCommission.add(weekendBonus);
    } else {
        weekendBonus = BigDecimal.ZERO;
        totalCommission = baseCommission;
    }
}

@Then("the base commission should be $$expectedAmount")
public void thenBaseCommissionShouldBe(BigDecimal expectedAmount) {
    assertNotNull(baseCommission, "Base commission should be calculated");
    assertEquals(expectedAmount, baseCommission,
        "Base commission should match expected value");
}

@Then("the weekend bonus should be $$expectedAmount")
public void thenWeekendBonusShouldBe(BigDecimal expectedAmount) {
    assertNotNull(weekendBonus, "Weekend bonus should be calculated");
    assertEquals(expectedAmount, weekendBonus,
        "Weekend bonus should match expected value");
}

@Then("the total commission should be $$expectedAmount")
public void thenTotalCommissionShouldBe(BigDecimal expectedAmount) {
    assertNotNull(totalCommission, "Total commission should be calculated");
    assertEquals(expectedAmount, totalCommission,
        "Total commission should match expected value");
}

@Then("it should include the 5% weekend bonus")
public void thenShouldIncludeWeekendBonus() {
    assertTrue(weekendBonus.compareTo(BigDecimal.ZERO) > 0,
        "Weekend bonus should be greater than zero");

    BigDecimal expectedBonus = baseCommission.multiply(new BigDecimal("0.05"));
    assertEquals(expectedBonus, weekendBonus,
        "Weekend bonus should be 5% of base commission");
}

// Helper methods
private boolean isWeekend(LocalDate date) {
    DayOfWeek day = date.getDayOfWeek();
    return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
}

private LocalDate getNextOrCurrentSaturday() {
    LocalDate today = LocalDate.now();
    while (today.getDayOfWeek() != DayOfWeek.SATURDAY) {
        today = today.plusDays(1);
    }
    return today;
}

private LocalDate getNextOrCurrentSunday() {
    LocalDate today = LocalDate.now();
    while (today.getDayOfWeek() != DayOfWeek.SUNDAY) {
        today = today.plusDays(1);
    }
    return today;
}

private LocalDate getNextOrCurrentMonday() {
    LocalDate today = LocalDate.now();
    while (today.getDayOfWeek() != DayOfWeek.MONDAY) {
        today = today.plusDays(1);
    }
    return today;
}
```

**Implementation in BDD.java:**

```java
/**
 * BDD SCENARIO: Weekend deals receive bonus
 *
 * Business Rule: Deals closed on Saturday or Sunday receive 5% bonus
 *
 * @param deal The deal to check
 * @return Weekend bonus amount, or zero if not a weekend
 */
public BigDecimal calculateWeekendBonus(Deal deal) {
    if (deal == null || deal.getCloseDate() == null) {
        return BigDecimal.ZERO;
    }

    DayOfWeek dayOfWeek = deal.getCloseDate().getDayOfWeek();
    boolean isWeekend = (dayOfWeek == DayOfWeek.SATURDAY ||
                        dayOfWeek == DayOfWeek.SUNDAY);

    if (!isWeekend) {
        return BigDecimal.ZERO;
    }

    // Calculate base commission first
    BigDecimal baseCommission = calculateTieredCommission(deal.getValue());

    // Apply 5% weekend bonus
    final BigDecimal WEEKEND_BONUS_RATE = new BigDecimal("0.05");
    return baseCommission.multiply(WEEKEND_BONUS_RATE)
        .setScale(2, RoundingMode.HALF_UP);
}

/**
 * Updated full commission calculation including weekend bonus
 */
public BigDecimal calculateFullCommissionWithWeekendBonus(Deal deal, BigDecimal bonusRate) {
    // Existing validation
    if (deal == null || !isDealEligibleForCommission(deal)) {
        throw new IllegalArgumentException("Deal not eligible");
    }

    // Calculate base tiered commission
    BigDecimal baseCommission = calculateTieredCommission(deal.getValue());

    // Add weekend bonus if applicable
    BigDecimal weekendBonus = calculateWeekendBonus(deal);
    BigDecimal commissionWithWeekendBonus = baseCommission.add(weekendBonus);

    // Add performance bonus if provided
    if (bonusRate != null && bonusRate.compareTo(BigDecimal.ZERO) > 0) {
        return calculateCommissionWithBonus(commissionWithWeekendBonus, bonusRate);
    }

    return commissionWithWeekendBonus;
}
```

This demonstrates the complete BDD cycle:
1. **Scenario** describes behavior in business language
2. **Steps** map language to code
3. **Implementation** delivers the feature

---

## Summary

BDD with JBehave transforms software development by:
- Using natural language specifications
- Enabling collaboration between business and tech
- Creating living documentation
- Focusing on business value
- Providing executable examples

The key is **shared understanding** through **concrete examples** written in **natural language** that can be **automatically tested**.

**Remember**: BDD is successful when business stakeholders can read scenarios and say "Yes, that's exactly how it should work!"