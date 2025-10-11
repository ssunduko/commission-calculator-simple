# TDD (Test-Driven Development) - Questions

## Fundamental Concepts

### Question 1: What is TDD?
What does Test-Driven Development mean, and how does it differ from traditional development approaches where tests are written after implementation?

### Question 2: The TDD Cycle
Explain the three phases of the TDD cycle: RED, GREEN, and REFACTOR. What is the purpose of each phase?

### Question 3: Why Test First?
Why is it important to write the test before writing the implementation code? What benefits does this order provide?

### Question 4: What Does "Failing Test" Prove?
In the RED phase, we deliberately watch a test fail. Why is this important? What would it mean if the test passed before we wrote any implementation?

### Question 5: Minimal Implementation
In the GREEN phase, we write the "minimal code to make the test pass." Why not write perfect, production-ready code right away?

## TDD Principles and Best Practices

### Question 6: FIRST Principles
What do the FIRST principles stand for in TDD? Explain each letter and why it's important for effective test-driven development.

### Question 7: Three Rules of TDD
Uncle Bob (Robert C. Martin) defined three rules of TDD. What are they, and why do they matter?

### Question 8: Test Independence
Why is it crucial that tests be independent of each other? What problems can arise when tests depend on each other?

### Question 9: Refactoring Safety
How do tests provide safety during the refactoring phase? Why can we refactor confidently when we have comprehensive tests?

### Question 10: One Test at a Time
Why should you complete the full RED-GREEN-REFACTOR cycle for one test before moving to the next test? What happens if you write multiple failing tests at once?

## TDD in Practice

### Question 11: Basic Commission Example
In the implementation, `calculateBasicCommission()` simply multiplies `dealValue * rate`. This seems almost too simple. Why is this acceptable in TDD? What principle does this demonstrate?

### Question 12: Tiered Commission Refactoring
The `calculateTieredCommission()` method was refactored to replace magic numbers with named constants. How do the tests ensure this refactoring didn't break functionality?

### Question 13: Pipeline Composition
The `calculateFullCommission()` method combines several smaller methods (validation, tiered calculation, bonus). How does TDD make this composition safer and easier?

### Question 14: Edge Cases
Why does `TDDTest` include a test cycle specifically for edge cases and validation? Could these be omitted if the "happy path" tests pass?

### Question 15: Calculation Tracking
The implementation includes `calculationCount` and related methods for tracking calculations. How did TDD drive the addition of this observability feature?

## Design and Architecture

### Question 16: TDD and Design
How does writing tests first influence the design of your code? What qualities does TDD-driven code tend to have?

### Question 17: Testability
What does it mean for code to be "testable"? How does TDD naturally lead to more testable code?

### Question 18: Single Responsibility
How does TDD encourage adherence to the Single Responsibility Principle? Look at the methods in `TDD.java` as examples.

### Question 19: Dependencies and Coupling
In this implementation, methods have minimal dependencies. How does TDD encourage loose coupling?

### Question 20: Interface Design
When writing tests first, you define how you want to call methods before those methods exist. How does this affect the quality of your APIs?

## Benefits and Challenges

### Question 21: Documentation Through Tests
How do tests serve as documentation? What advantages do they have over traditional documentation?

### Question 22: Bug Detection
At what point in the development lifecycle does TDD catch bugs? How does this compare to traditional testing?

### Question 23: Code Coverage
Projects using TDD typically have high code coverage. Why? How is this different from writing tests after implementation?

### Question 24: Debugging Time
How does TDD affect the amount of time spent debugging? Why?

### Question 25: Initial Slowdown
Many developers find TDD feels slower at first. Why does it feel slower, and why do experienced TDD practitioners find it faster in the long run?

## Advanced TDD Concepts

### Question 26: Test Granularity
How do you decide how small or large each test should be? Should one test cover multiple features?

### Question 27: Test Naming
The tests in `TDDTest.java` have descriptive names like `testBasicCommissionCalculation_RedPhase()`. Why is clear test naming important?

### Question 28: When Tests Fail
If a test starts failing after previously passing, what does this tell you? How should you respond?

### Question 29: Over-Testing
Is it possible to write too many tests? How do you balance thorough testing with practical development speed?

### Question 30: TDD for Bug Fixes
How would you use TDD when fixing a bug in existing code? Describe the process.

## Comparison and Tradeoffs

### Question 31: TDD vs. TAD (Test-After Development)
Compare TDD (test-first) with TAD (test-after). What are the advantages and disadvantages of each approach?

### Question 32: When Not to Use TDD
Are there situations where TDD is not appropriate or not the best choice? Give examples.

### Question 33: TDD and Legacy Code
How do you apply TDD principles when working with legacy code that has no tests? Is it different from TDD on a greenfield project?

### Question 34: TDD and Prototyping
When exploring new ideas or creating prototypes with unclear requirements, is TDD still beneficial? Why or why not?

### Question 35: TDD in Different Languages
Does TDD work the same way in all programming languages? Are there language-specific considerations?

## Practical Application

### Question 36: Next Feature
Suppose you need to add a new commission rule: "Deals closed on weekends get an extra 5% bonus." How would you approach this using TDD? Describe the RED-GREEN-REFACTOR cycle.

### Question 37: Validation Expansion
Currently, only `DealStatus.CLOSED` deals are eligible for commission. Suppose the business adds a rule: "Deals must also have a `closeDate` within the last 90 days." How would you add this using TDD?

### Question 38: Refactoring Exercise
The tier rate boundaries (50000, 100000) and rates (0.08, 0.10, 0.12) are defined inside the method. How could you refactor this to make it more flexible while keeping tests green?

### Question 39: Error Messages
The implementation throws `IllegalArgumentException` for invalid inputs. How could you use TDD to improve the error messages to be more helpful to callers?

### Question 40: Performance Testing
The current tests verify correctness. How would you add performance requirements (e.g., "calculation must complete in under 10ms") using TDD principles?

## Reflection Questions

### Question 41: TDD Discipline
What is the hardest part about practicing TDD? Why do developers sometimes skip the "test first" step?

### Question 42: Team Adoption
If you wanted to introduce TDD to a team that has never used it, how would you approach it? What challenges might you face?

### Question 43: Test Maintenance
Tests are code too and require maintenance. How do you keep tests maintainable as the system evolves?

### Question 44: Confidence Level
After completing a feature using TDD with full test coverage, how confident are you that the code works correctly? Compare this to confidence when tests are written after implementation (or not at all).

### Question 45: TDD Mindset
How does TDD change the way you think about software development? What mental shift is required to adopt TDD successfully?

## Integration with Other Practices

### Question 46: TDD and Continuous Integration
How does TDD complement Continuous Integration (CI) practices? Why do they work well together?

### Question 47: TDD and Pair Programming
How does TDD work with pair programming? Can the RED-GREEN-REFACTOR cycle be divided between pair partners?

### Question 48: TDD and Code Reviews
How do comprehensive tests (from TDD) affect the code review process? What should reviewers focus on?

### Question 49: TDD and Design Patterns
Can you see any design patterns emerging from the TDD implementation in `TDD.java`? How does TDD relate to design patterns?

### Question 50: TDD and Agile
TDD is often associated with Agile methodologies. Why? How do TDD principles align with Agile values?

---

## Bonus Challenge Questions

### Challenge 1: TDD for Complex Scenarios
How would you use TDD to implement a complex commission split feature where multiple sales reps share commission on a single deal?

### Challenge 2: TDD with External Dependencies
If commission calculation required calling an external tax calculation API, how would you structure your TDD approach to handle this dependency?

### Challenge 3: TDD for Concurrent Code
How would you apply TDD to code that must be thread-safe and handle concurrent access to commission calculations?

### Challenge 4: TDD Metrics
What metrics could you use to measure the effectiveness of TDD on a project? How would you know if TDD is providing value?

### Challenge 5: TDD Evolution
Imagine this commission system needs to support multiple calculation strategies that can be selected at runtime (Strategy pattern). How would you evolve the current TDD implementation to support this? What tests would you write first?

---

**Note**: These questions are designed to deepen your understanding of TDD concepts. Try to answer them based on the code in this package and your own reasoning before looking at ANSWERS.md. Discussing these questions with peers can lead to valuable insights.