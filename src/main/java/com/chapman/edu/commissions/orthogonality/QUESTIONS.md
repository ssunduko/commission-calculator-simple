# Questions about Orthogonality

## Conceptual Questions

1. What is orthogonality in software design, and why is it important?

2. How does orthogonality differ from cohesion and coupling in software design?

3. What are the key benefits of high orthogonality in a software system?

4. How does orthogonality relate to the Single Responsibility Principle?

5. What is the relationship between orthogonality and testability?

6. How does orthogonality contribute to the maintainability of a software system?

7. What is the mathematical origin of the term "orthogonality," and how does it relate to software design?

## Applied Questions

8. In the `HighOrthogonality` class, what specific characteristics make it an example of high orthogonality?

9. Compare and contrast the `HighOrthogonality` and `LowOrthogonality` classes. What are the key differences in their design?

10. How does the `ReportGenerator` class in `HighOrthogonality` demonstrate dependency injection, and why does this enhance orthogonality?

11. What problems might arise when maintaining or extending the `LowOrthogonality` class? Provide specific examples.

12. How does the `OrthogonalityPrinciples` class demonstrate the concept of Separation of Concerns?

13. What would be required to refactor the `LowOrthogonality` class to achieve high orthogonality?

14. How does the Command-Query Separation principle demonstrated in `OrthogonalityPrinciples` contribute to orthogonality?

## Design Considerations

15. What are the trade-offs between high orthogonality and other design goals like performance or simplicity?

16. In what scenarios might lower orthogonality be acceptable or even preferable?

17. How can you identify areas of low orthogonality in an existing codebase?

18. How does the size and complexity of a system affect the importance of orthogonality?

19. How can design patterns help achieve higher levels of orthogonality?

20. How does orthogonality apply to different architectural styles (e.g., microservices, monolithic, event-driven)?

21. What refactoring techniques can be used to improve orthogonality in an existing codebase?

## Implementation Questions

22. How do pure functions, as demonstrated in `OrthogonalityPrinciples.PureFunctions`, contribute to orthogonality?

23. What role does immutability play in achieving orthogonality, and how is this demonstrated in the `ImmutableDealSummary` class?

24. How does interface segregation, as shown in `OrthogonalityPrinciples.InterfaceSegregation`, enhance orthogonality?

25. What would happen if you added a method to the `HighOrthogonality.DealProcessor` class that also processed user data? How would this affect the orthogonality of the design?

26. How could you measure or quantify the level of orthogonality in a software module?

27. In the context of the commission calculator system, how might orthogonality principles be applied to the calculation of commissions based on different rules or tiers?

28. How does the use of dependency injection in `OrthogonalityPrinciples.DependencyInjection` make the code more orthogonal and testable?