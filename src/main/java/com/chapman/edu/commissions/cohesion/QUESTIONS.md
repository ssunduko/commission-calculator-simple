# Questions about Cohesion

## Conceptual Questions

1. What is cohesion in software design, and why is it important?

2. How does cohesion differ from coupling in software design?

3. What are the seven types of cohesion, and how are they ranked from weakest to strongest?

4. Why is functional cohesion considered the strongest form of cohesion?

5. How does coincidental cohesion impact code maintainability and why is it considered the weakest form?

6. What is the relationship between cohesion and the Single Responsibility Principle (SRP)?

7. How can you identify the type of cohesion in an existing codebase?

## Applied Questions

8. In the `CoincidentalCohesion` class, what specific characteristics make it an example of coincidental cohesion?

9. Compare and contrast logical cohesion and functional cohesion using the examples provided in this package.

10. How does the `TemporalCohesion` class demonstrate the concept of operations being executed at the same time?

11. In the `ProceduralCohesion` class, what is the relationship between the methods, and how does this demonstrate procedural cohesion?

12. How does the `CommunicationalCohesion` class differ from the `SequentialCohesion` class in terms of how they handle data?

13. What would be required to refactor the `LogicalCohesion` class to achieve functional cohesion instead?

14. How might you refactor a class with coincidental cohesion to improve its design? Provide a specific example using the `CoincidentalCohesion` class.

## Design Considerations

15. What are the trade-offs between high cohesion and low cohesion in software design?

16. How does the level of cohesion in a module affect its testability?

17. In what scenarios might a lower form of cohesion (like logical or temporal) be acceptable or even preferable?

18. How does the size of a module (class, method) relate to its level of cohesion?

19. How can design patterns help achieve higher levels of cohesion?

20. How does the concept of cohesion apply to microservices architecture?

21. What refactoring techniques can be used to improve cohesion in an existing codebase?

## Implementation Questions

22. In the `FunctionalCohesion` class, how do the private methods contribute to the single responsibility of the class?

23. How does the `SequentialCohesion` class ensure that the output of one step becomes the input to the next step?

24. What would happen if you added a method to the `FunctionalCohesion` class that was not related to calculating deal values with discounts?

25. How could you measure or quantify the level of cohesion in a software module?