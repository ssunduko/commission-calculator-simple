# Questions About Encapsulation

## General Questions

1. What is encapsulation, and why is it considered one of the fundamental principles of object-oriented programming?

2. How does encapsulation contribute to code maintainability and reusability?

3. What is the difference between encapsulation and information hiding?

4. How does encapsulation support the principle of "programming to an interface, not an implementation"?

5. Can you have encapsulation without using private fields? Explain your answer.

## Data Encapsulation

6. Why is it generally recommended to make fields private and provide public getter and setter methods?

7. What are the potential issues with directly exposing collection fields (like List or Map) even through getter methods?

8. How can immutability be used as a form of encapsulation?

9. What is defensive copying, and when should it be used?

10. How does validation in setter methods enhance encapsulation?

## Method Encapsulation

11. How does method encapsulation differ from data encapsulation?

12. What are the benefits of breaking down complex methods into smaller, private helper methods?

13. How does method encapsulation support the Single Responsibility Principle?

14. In what scenarios might you choose to expose implementation details that would normally be encapsulated?

15. How can method encapsulation improve testability?

## Class Encapsulation

16. What is the purpose of using private inner classes in Java?

17. How do facade classes contribute to encapsulation?

18. What are Data Transfer Objects (DTOs), and how do they relate to encapsulation?

19. How does class encapsulation help manage complexity in large systems?

20. What is the relationship between class encapsulation and the Law of Demeter (principle of least knowledge)?

## Module Encapsulation

21. How does Java's package-private access modifier support module encapsulation?

22. What are the advantages of organizing related classes into packages?

23. How does module encapsulation differ from class encapsulation?

24. What are the limitations of Java's package system for module encapsulation compared to more modern module systems?

25. How can you enforce that clients only interact with a specific entry point to a package?

## Interface Encapsulation

26. How does programming to an interface support encapsulation?

27. What is the Dependency Inversion Principle, and how does it relate to interface encapsulation?

28. What are the trade-offs between interface encapsulation and implementation inheritance?

29. How does interface encapsulation facilitate unit testing through mocking?

30. In what scenarios might interface encapsulation be unnecessary or overly complex?

## Practical Application

31. How would you refactor poorly encapsulated code to improve its design?

32. What tools or techniques can help identify violations of encapsulation in existing code?

33. How do design patterns like Adapter, Facade, and Proxy relate to encapsulation?

34. How does encapsulation apply in functional programming paradigms?

35. How might encapsulation principles differ when applied to microservices architecture versus monolithic applications?