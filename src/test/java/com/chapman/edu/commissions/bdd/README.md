### Behavior-Driven Development (BDD) with JBehave

## Overview

This package demonstrates **Behavior-Driven Development (BDD)** using the **JBehave framework**. BDD is an evolution of TDD that emphasizes collaboration between developers, testers, and non-technical stakeholders through natural language specifications.

## What is BDD?

**Behavior-Driven Development (BDD)** is a software development methodology that extends TDD by focusing on:

- **Shared Understanding**: Using plain language that everyone understands
- **Collaboration**: Bridge between business and technical teams
- **Living Documentation**: Executable specifications that stay up-to-date
- **Business Value**: Testing what matters to users

### BDD vs TDD

| Aspect | TDD | BDD |
|--------|-----|-----|
| **Focus** | Implementation correctness | Business behavior |
| **Language** | Technical test code | Natural language (Gherkin) |
| **Audience** | Developers | Everyone (business + tech) |
| **Tests** | Unit tests | Scenarios/Examples |
| **Example** | `testCalculateCommission()` | `Given a closed deal, When I calculate commission, Then...` |

## Key BDD Concepts

### 1. User Stories

Format: **"As a [role], I want [feature], So that [benefit]"**

Example:
```
As a sales manager
I want to calculate commissions for sales representatives
So that I can accurately compensate them based on their performance
```

### 2. Scenarios

Concrete examples of how a feature should behave, written in **Given-When-Then** format.

### 3. Gherkin Language

Natural language syntax for writing scenarios:

- **Given**: Set up initial context (preconditions)
- **When**: Perform an action (the behavior being tested)
- **Then**: Verify the outcome (expected result)
- **And/But**: Continue previous step type

### 4. JBehave Framework

JBehave is a BDD framework for Java that:
- Reads story files (`.story`) written in Gherkin
- Maps steps to Java methods using annotations (`@Given`, `@When`, `@Then`)
- Executes scenarios and generates reports
- Supports parameterization and table-driven tests

## Files in This Package

### 1. Story Files (`src/test/resources/stories/`)

**commission_calculation.story**
- Basic commission calculation scenarios
- Tiered commission rates
- Performance bonuses
- Full pipeline integration

**deal_validation.story**
- Deal eligibility validation
- Required fields validation
- Close date eligibility
- Boundary testing

**commission_plan.story**
- Commission plan creation
- Plan activation
- Plan applicability by date
- Tier configuration

### 2. BDD.java

The core commission service implementation with extensive comments explaining:
- BDD principles and concepts
- Business rules and validation
- Method-to-scenario mapping
- State management for scenarios

Key methods:
- `calculateBasicCommission()` - Simple percentage-based calculation
- `isDealEligibleForCommission()` - Business rule validation
- `calculateTieredCommission()` - Progressive rate tiers
- `calculateCommissionWithBonus()` - Bonus application
- `calculateFullCommission()` - Complete pipeline
- Commission plan management methods

### 3. CommissionCalculationSteps.java

JBehave step definitions that map Gherkin steps to Java code:

**Given Steps** (Setup):
```java
@Given("a sales representative named \"$name\" with ID \"$id\"")
@Given("a deal worth $$value for product \"$productName\"")
@Given("the deal status is \"$status\"")
```

**When Steps** (Actions):
```java
@When("I calculate the commission at $rate% rate")
@When("I apply a performance bonus of $bonus%")
@When("I process the following deals: $dealsTable")
```

**Then Steps** (Verification):
```java
@Then("the commission amount should be $$expectedAmount")
@Then("the system should reject the calculation")
@Then("all commissions should be calculated correctly")
```

### 4. CommissionCalculationStoriesTest.java

JUnit runner that:
- Configures JBehave
- Locates story files
- Maps steps to Java classes
- Generates reports

## Writing BDD Scenarios

### Example Scenario Structure

```gherkin
Scenario: Calculate basic commission for a closed deal
Meta:
@category calculation
@priority high

Given a sales representative named "John Doe" with ID "REP-001"
And a deal worth $100,000 for product "Enterprise Software"
And the deal status is "CLOSED"
When I calculate the commission at 10% rate
Then the commission amount should be $10,000.00
```

### Scenario Components

**Narrative** (Story level):
```gherkin
Narrative:
As a [role]
I want [feature]
So that [benefit]
```

**Meta Tags** (Scenario level):
```gherkin
Meta:
@category validation
@priority critical
```

**Steps**:
- Use present tense
- Be specific and unambiguous
- Focus on business behavior, not implementation
- Use examples with actual values

### Table-Driven Scenarios

```gherkin
Scenario: Tiered commission rates based on deal size

Given a sales representative named "Bob Wilson" with ID "REP-003"
When I process the following deals:
|dealValue|expectedRate|expectedCommission|
|$30,000  |8%          |$2,400.00         |
|$75,000  |10%         |$7,500.00         |
|$150,000 |12%         |$18,000.00        |
Then all commissions should be calculated correctly
```

## Running BDD Stories

### Method 1: IDE
```
Right-click CommissionCalculationStoriesTest.java
→ Run As → JUnit Test
```

### Method 2: Maven
```bash
# Run specific BDD test
mvn test -Dtest=CommissionCalculationStoriesTest

# Run all tests (including BDD)
mvn test
```

### Method 3: Individual Story
Stories can be run individually by creating separate runner classes.

## Generated Reports

After running, JBehave generates reports in:
```
target/jbehave/
```

**Report Types:**
- **Console**: Immediate feedback during execution
- **HTML**: Visual representation with pass/fail status
- **XML**: Machine-readable for CI/CD integration

**HTML Reports Show:**
- Scenario execution status (passed/failed)
- Step-by-step results
- Actual vs expected values
- Execution time per scenario

## BDD Workflow

### Step 1: Discovery (Collaborative)
- Product owner, developers, and QA discuss feature
- Identify examples and edge cases
- Ask "What if..." questions
- **Output**: Shared understanding

### Step 2: Formulation (Write Scenarios)
- Convert examples to Given-When-Then format
- Write scenarios in .story files
- Review with all stakeholders
- **Output**: Executable specifications

### Step 3: Automation (Implement Steps)
- Map steps to Java code using annotations
- Implement underlying business logic
- Run scenarios to verify behavior
- **Output**: Working feature with tests

### Step 4: Living Documentation
- Scenarios run continuously in CI/CD
- Reports show current system behavior
- Documentation always up-to-date
- **Output**: Confidence in system behavior

## Best Practices

### Do's

✅ **Write scenarios before code**
- Scenarios define requirements

✅ **Use business language**
- Non-technical stakeholders should understand

✅ **Keep scenarios independent**
- Each scenario should stand alone

✅ **Focus on behavior, not implementation**
- Test "what", not "how"

✅ **Use concrete examples**
- Specific values, not abstract concepts

✅ **Involve stakeholders**
- Collaboration is key to BDD

### Don'ts

❌ **Don't use technical jargon in stories**
- Avoid terms like "database", "API", "cache"

❌ **Don't test implementation details**
- Focus on business outcomes

❌ **Don't make scenarios too long**
- Break complex scenarios into multiple smaller ones

❌ **Don't repeat Given steps**
- Use Background or Lifecycle for common setup

❌ **Don't mix multiple behaviors**
- One scenario = one behavior

## Benefits of BDD

### 1. Shared Understanding
Everyone (business, dev, QA) uses the same language and examples.

### 2. Clearer Requirements
Concrete examples eliminate ambiguity better than abstract descriptions.

### 3. Living Documentation
Scenarios serve as always up-to-date documentation that's actually tested.

### 4. Better Collaboration
BDD workshops bring teams together to discover and clarify requirements.

### 5. Reduced Waste
Building the right thing first time, not fixing misunderstandings later.

### 6. Executable Specifications
Requirements that can be automatically verified.

### 7. Confidence
Know that the system does what business needs it to do.

## Common Patterns

### Pattern 1: Basic Scenario
```gherkin
Scenario: <descriptive name>
Given <initial context>
When <action>
Then <expected outcome>
```

### Pattern 2: Multiple Conditions
```gherkin
Scenario: <name>
Given <condition 1>
And <condition 2>
And <condition 3>
When <action>
Then <outcome 1>
And <outcome 2>
```

### Pattern 3: Data Tables
```gherkin
Scenario: <name>
Given <setup>
When I process the following:
|column1|column2|column3|
|value1 |value2 |value3 |
Then <verification>
```

### Pattern 4: Background (Common Setup)
```gherkin
Background:
Given <common setup for all scenarios>

Scenario: <first scenario>
When <action>
Then <outcome>

Scenario: <second scenario>
When <different action>
Then <different outcome>
```

## BDD Anti-Patterns

### Anti-Pattern 1: Technical Scenarios
❌ **Bad**:
```gherkin
Given the database has a record with ID 123
When I call the REST API endpoint /api/commission
Then the JSON response status should be 200
```

✅ **Good**:
```gherkin
Given a closed deal worth $100,000
When I calculate the commission
Then the commission should be $10,000.00
```

### Anti-Pattern 2: Too Many Steps
❌ **Bad**: Scenario with 20+ steps

✅ **Good**: Break into multiple focused scenarios

### Anti-Pattern 3: Testing the UI
❌ **Bad**:
```gherkin
When I click the "Calculate" button
And I see a spinner
Then the result appears in the "Commission" field
```

✅ **Good**:
```gherkin
When I calculate the commission for the deal
Then the commission amount should be $10,000.00
```

## Integration with Development Process

### 1. Sprint Planning
- Discuss user stories
- Write initial scenarios (discovery)

### 2. Development
- Implement step definitions
- Write business logic to make scenarios pass
- Refactor

### 3. Testing
- Scenarios run as automated tests
- QA can add more scenarios for edge cases

### 4. Continuous Integration
- Scenarios run on every commit
- Reports show feature status

### 5. Documentation
- Scenarios serve as living documentation
- Always reflects current system behavior

## Learning Path

### Beginner
1. Read this README thoroughly
2. Review the story files in `src/test/resources/stories/`
3. Read `BDD.java` to see implementation
4. Run `CommissionCalculationStoriesTest` and see scenarios execute

### Intermediate
5. Read `CommissionCalculationSteps.java` to understand step mapping
6. Answer questions in `QUESTIONS.md`
7. Check your answers against `ANSWERS.md`
8. Try modifying a scenario and running it

### Advanced
9. Write a new scenario for a new feature
10. Implement the steps for your scenario
11. Add business logic to make it pass
12. Use BDD in your own projects

## Comparison: TDD vs BDD

### When to Use TDD
- Low-level algorithms
- Internal APIs
- Performance-critical code
- Technical utilities

### When to Use BDD
- Business rules and logic
- User-facing features
- Complex workflows
- When stakeholder involvement is valuable

### Best Approach: Use Both
- **BDD** for feature-level specifications
- **TDD** for implementation-level tests
- BDD scenarios call TDD-tested components

## Key Takeaways

1. **BDD is about communication**, not just testing
2. **Scenarios are examples** that illustrate requirements
3. **Given-When-Then** provides clear structure
4. **Collaboration** between business and tech is essential
5. **Living documentation** evolves with the system
6. **JBehave** bridges natural language and code
7. **Stakeholder readability** is a primary goal

## Additional Resources

### Books
- "Specification by Example" by Gojko Adzic
- "BDD in Action" by John Ferguson Smart
- "The Cucumber Book" by Matt Wynne & Aslak Hellesøy

### Online
- [JBehave Official Documentation](https://jbehave.org/)
- [Gherkin Syntax Reference](https://cucumber.io/docs/gherkin/)
- [BDD on Martin Fowler's Blog](https://martinfowler.com/bliki/GivenWhenThen.html)

### Related Concepts in This Codebase
- `/tdd/` - Test-Driven Development examples
- `/fundamentals/` - JUnit 5 testing fundamentals
- `/patterns/` - Testing patterns

## Conclusion

BDD transforms software development from "building software right" to "building the right software". By using natural language scenarios as executable specifications, BDD ensures that what we build actually delivers the business value it's supposed to.

The real power of BDD comes from the conversations that happen when writing scenarios. These discussions surface assumptions, clarify requirements, and build shared understanding across the team.

**Start with examples. Write scenarios. Build with confidence.**

---

**Remember**: BDD is successful when business stakeholders can read your scenarios and say "Yes, that's exactly how it should work."