# BDD (Behavior-Driven Development) with JBehave - Questions

## Fundamental BDD Concepts

### Question 1: What is BDD?
What is Behavior-Driven Development, and how does it differ from Test-Driven Development (TDD)? What problem does BDD solve that TDD doesn't?

### Question 2: Given-When-Then
Explain the Given-When-Then format used in BDD. What is the purpose of each keyword, and why is this structure important?

### Question 3: Gherkin Language
What is Gherkin? Why is it significant that BDD scenarios are written in natural language rather than code?

### Question 4: Living Documentation
What does "living documentation" mean in the context of BDD? How do BDD scenarios serve as documentation?

### Question 5: BDD vs TDD
When should you use BDD versus TDD? Can they be used together? Give examples of scenarios where each is most appropriate.

## JBehave Framework

### Question 6: What is JBehave?
What is JBehave, and how does it enable BDD in Java projects? What are its core components?

### Question 7: Story Files
What are `.story` files in JBehave? What is their structure, and who should be able to read and understand them?

### Question 8: Step Annotations
Explain the @Given, @When, and @Then annotations in JBehave. How do they map natural language steps to Java code?

### Question 9: Parameter Extraction
In the step definition `@Given("a deal worth $$value for product \"$productName\"")`, how does JBehave extract the `value` and `productName` parameters? What types can be automatically converted?

### Question 10: Examples Tables
How do ExamplesTable work in JBehave? Why are they useful for data-driven testing in BDD scenarios?

## Writing BDD Scenarios

### Question 11: User Stories
What is the standard format for a user story in BDD? Why is this format effective for capturing requirements?

### Question 12: Scenario Structure
What makes a good BDD scenario? What should be included, and what should be avoided?

### Question 13: Scenario Independence
Why is it important that BDD scenarios are independent of each other? How does this independence impact test design?

### Question 14: Business Language
Why should BDD scenarios use business language rather than technical terminology? Give examples of business language vs technical language.

### Question 15: Concrete Examples
Why does BDD emphasize concrete examples with specific values rather than abstract descriptions? How does this improve clarity?

## BDD in Practice

### Question 16: Collaboration
BDD is often described as a collaborative practice. Who should be involved in writing BDD scenarios, and what role does each person play?

### Question 17: Discovery Workshops
What happens during a BDD discovery workshop (sometimes called "Three Amigos")? What is the output of such a session?

### Question 18: Scenario Lifecycle
Walk through the lifecycle of a BDD scenario from initial idea to automated test. What are the key stages?

### Question 19: Failed Scenarios
When a BDD scenario fails, what should happen? How is failure handled differently in BDD compared to traditional testing?

### Question 20: Refactoring Scenarios
When and why should BDD scenarios be refactored? What are signs that a scenario needs improvement?

## Technical Implementation

### Question 21: Step State Management
In `CommissionCalculationSteps.java`, how is state shared between Given, When, and Then steps? Why is this necessary?

### Question 22: @BeforeScenario Annotation
What is the purpose of the `@BeforeScenario` annotation in JBehave? Why is scenario isolation important?

### Question 23: Step Reusability
How can the same step definition be reused across multiple scenarios? Why is step reusability beneficial?

### Question 24: Regular Expressions in Steps
JBehave supports regular expressions in step definitions. When would you use regex instead of simple parameter extraction?

### Question 25: Pending Steps
What happens when a step in a story file doesn't have a matching step definition in Java? How does JBehave handle this?

## Design and Architecture

### Question 26: Ubiquitous Language
What is "ubiquitous language" in BDD? How does the `BDD.java` implementation demonstrate this concept?

### Question 27: Business Rules
Looking at `isDealEligibleForCommission()` in `BDD.java`, how are business rules expressed? Why is this approach better than embedding rules in complex conditions?

### Question 28: Separation of Concerns
How does BDD promote separation between specification (scenarios) and implementation (step definitions and business logic)?

### Question 29: Testing Pyramid
Where does BDD fit in the testing pyramid (unit tests, integration tests, end-to-end tests)? What level of testing do BDD scenarios typically cover?

### Question 30: Page Object vs Step Definitions
In UI testing, there's a Page Object pattern. How do JBehave step definitions relate to or differ from Page Objects?

## Advanced BDD Concepts

### Question 31: Background in Stories
What is a `Background` section in a JBehave story? When should you use it versus repeating Given steps in each scenario?

### Question 32: Meta Tags
What are `@category` and `@priority` meta tags in story files? How can they be used to organize and filter scenarios?

### Question 33: Narrative Sections
What is the purpose of the `Narrative` section at the top of story files? Who benefits from reading it?

### Question 34: Lifecycle Hooks
Besides `@BeforeScenario`, what other lifecycle hooks does JBehave provide? When would you use `Lifecycle` in a story file?

### Question 35: Scenario Outlines
While not shown in our examples, what are Scenario Outlines (or Scenario Templates) in Gherkin? How do they differ from using Examples Tables?

## Benefits and Challenges

### Question 36: Stakeholder Communication
How does BDD improve communication between developers, testers, and business stakeholders? Give a concrete example.

### Question 37: Requirements Clarification
How do BDD scenarios help clarify ambiguous requirements? Why are examples more effective than prose descriptions?

### Question 38: BDD Overhead
Writing scenarios before code might seem like extra work. What are the long-term benefits that justify this upfront investment?

### Question 39: Maintaining Scenarios
As the system evolves, BDD scenarios need maintenance. What are strategies for keeping scenarios up-to-date and valuable?

### Question 40: BDD Anti-Patterns
What are common BDD anti-patterns or mistakes? Give examples of what NOT to do when writing scenarios.

## Comparison and Context

### Question 41: BDD vs Acceptance Testing
How does BDD relate to traditional acceptance testing? Are they the same thing?

### Question 42: BDD vs Specification by Example
What is the relationship between BDD and Specification by Example? Are these different names for the same concept?

### Question 43: Cucumber vs JBehave
JBehave and Cucumber are both BDD frameworks. What are the key differences? When would you choose one over the other?

### Question 44: BDD for APIs vs UI
Can BDD be used for both API testing and UI testing? How would scenarios differ between the two?

### Question 45: BDD in Agile
How does BDD fit into Agile development methodologies like Scrum or Kanban? When in the sprint should scenarios be written?

## Practical Application

### Question 46: Scenario for New Feature
Suppose the business wants a new feature: "Deals closed on weekends get a 5% bonus." Write a BDD scenario for this feature in Given-When-Then format.

### Question 47: Step Definition for Weekend Bonus
For the scenario in Question 46, write the Java step definitions (@Given, @When, @Then) that would implement this feature.

### Question 48: Complex Business Rule
How would you write a scenario for this complex rule: "Commission plans can only be activated if they have at least one rule OR one tier, and the effective start date is in the future"?

### Question 49: Error Scenarios
Write a BDD scenario that tests the error case where a user tries to calculate commission for an OPEN (not CLOSED) deal.

### Question 50: Table-Driven Testing
Write a scenario using an Examples Table to test that commission eligibility correctly handles deals closed at 30, 60, 90, and 91 days ago.

---

## Integration and Workflow

### Question 51: BDD in CI/CD
How should BDD scenarios be integrated into a Continuous Integration/Continuous Deployment pipeline? What should happen if scenarios fail?

### Question 52: BDD Reports
What information should BDD test reports contain? Who is the audience for these reports?

### Question 53: Exploratory Testing
How does BDD relate to exploratory testing? Does BDD replace the need for manual exploratory testing?

### Question 54: BDD for Bug Fixes
When a bug is discovered, how can BDD be used? Should you write a scenario before fixing the bug?

### Question 55: Scenario Review Process
What should a scenario review process look like? Who should review scenarios, and what should they check for?

## Philosophical and Strategic

### Question 56: The Three Amigos
Who are the "Three Amigos" in BDD, and why is their collaboration critical?

### Question 57: BDD Mindset
What mindset shift is required to adopt BDD successfully? How is thinking in scenarios different from thinking in tests?

### Question 58: Example Mapping
What is Example Mapping? How does it relate to writing BDD scenarios?

### Question 59: BDD at Scale
How do you scale BDD in large projects with hundreds or thousands of scenarios? What challenges arise?

### Question 60: When NOT to Use BDD
Are there situations where BDD is NOT appropriate? When should you skip BDD and use alternative approaches?

---

**Note**: These questions are designed to deepen your understanding of BDD concepts and JBehave framework. Try to answer them based on the implementation in this package and your own reasoning before looking at ANSWERS.md. Discussing these with peers can lead to valuable insights about how BDD can improve your development process.