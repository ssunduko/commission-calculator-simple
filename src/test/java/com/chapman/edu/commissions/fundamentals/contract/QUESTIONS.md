# Questions about Design by Contract

The following questions are designed to help you think about and understand the Design by Contract (DbC) concept and how it can be applied to software development.

## Conceptual Questions

1. What is the main difference between pre-conditions and post-conditions in Design by Contract?

2. How do class invariants differ from pre-conditions and post-conditions?

3. In Design by Contract, who is responsible for ensuring that pre-conditions are met? The caller or the method being called?

4. What happens if a pre-condition is violated? What about a post-condition?

5. How does Design by Contract relate to defensive programming? Are they complementary or contradictory approaches?

## Implementation Questions

6. How would you implement Design by Contract in a language that doesn't have built-in support for it (like Java)?

7. What are some common patterns or techniques for enforcing contracts in code?

8. How would you handle contract violations in a production environment? Should they throw exceptions, log errors, or something else?

9. How do you test that your contract implementations are working correctly?

10. Can Design by Contract be applied to interfaces and abstract classes? If so, how?

## Application Questions

11. How would you apply Design by Contract to the `CommissionCalculation` class in our model? What pre-conditions, post-conditions, and invariants would you define?

12. Consider the `Dispute` class in our model. What contracts would be appropriate for its methods?

13. How would Design by Contract help prevent bugs in the commission calculation process?

14. How might Design by Contract impact the performance of our application? Are there ways to minimize any negative impact?

15. How would you document the contracts in your code? Should they be in comments, separate documentation, or enforced in the code itself?

## Advanced Questions

16. How does Design by Contract relate to other software design principles like SOLID or DRY?

17. Can Design by Contract be used in conjunction with Test-Driven Development (TDD)? If so, how?

18. How would you handle contract inheritance in object-oriented programming? For example, if a subclass overrides a method, what happens to the contracts?

19. How might Design by Contract be applied in a microservices architecture where different services are developed by different teams?

20. What are the limitations or potential drawbacks of Design by Contract? When might it not be the best approach?