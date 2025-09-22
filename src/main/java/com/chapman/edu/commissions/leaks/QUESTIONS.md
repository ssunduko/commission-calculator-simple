# Questions About Leaky Abstractions

## General Questions

1. What is a leaky abstraction, and why is it considered problematic in software design?

2. How can you identify a leaky abstraction in existing code?

3. What are some common causes of leaky abstractions?

4. How do leaky abstractions affect the maintainability and extensibility of a software system?

5. Can all abstractions be made completely non-leaky? Why or why not?

## Design For Change

6. How does the Strategy pattern help prevent leaky abstractions in the context of design for change?

7. What are some signs that a design is not adequately prepared for future changes?

8. How does the Open/Closed Principle relate to designing for change and preventing leaky abstractions?

9. In the `DesignForChangeExample`, how does the `RigidCommissionCalculator` leak implementation details that the `FlexibleCommissionCalculator` does not?

10. What other design patterns besides Strategy could help in designing for change and preventing leaky abstractions?

## Layered Architecture

11. What are the typical layers in a layered architecture, and what is the responsibility of each layer?

12. How does a properly implemented layered architecture prevent leaky abstractions?

13. What problems can arise when layers are not properly separated?

14. In the `LayeredArchitectureExample`, how does the presentation layer in the leaky implementation violate the principles of layered architecture?

15. How does dependency inversion relate to layered architecture and preventing leaky abstractions?

## Standard Interfaces

16. Why are standard interfaces important for preventing leaky abstractions?

17. What principles should guide the design of a good interface?

18. How does the Liskov Substitution Principle relate to standard interfaces and preventing leaky abstractions?

19. In the `StandardInterfacesExample`, what specific issues make the non-standard interfaces leaky?

20. How does the use of a factory pattern in the standard interfaces example help prevent leaky abstractions?

## Defensive Programming

21. What is defensive programming, and how does it help prevent leaky abstractions?

22. What are some common defensive programming techniques?

23. How does defensive copying help prevent leaky abstractions?

24. In the `DefensiveProgrammingExample`, how does the non-defensive approach leak implementation details?

25. What is the relationship between defensive programming and the principle of encapsulation?

## Documented Expectations

26. Why is documentation important for preventing leaky abstractions?

27. What should be included in good documentation to prevent leaky abstractions?

28. How does the principle of "design by contract" relate to documented expectations?

29. In the `DocumentedExpectationsExample`, how does the poorly documented approach leak implementation details?

30. How can automated tools (like static analysis or documentation generators) help enforce documented expectations?

## Application and Practice

31. How would you refactor an existing codebase with leaky abstractions to improve its design?

32. What trade-offs might you face when trying to eliminate leaky abstractions?

33. How do leaky abstractions relate to technical debt?

34. Can you think of examples of leaky abstractions in popular frameworks or libraries you've used?

35. How can code reviews help identify and prevent leaky abstractions?