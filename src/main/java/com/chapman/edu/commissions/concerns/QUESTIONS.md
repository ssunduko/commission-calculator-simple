# Questions About Programming Concerns

This document contains questions about the programming concerns and patterns demonstrated in the examples.

## Cross-Cutting Concerns

1. What are cross-cutting concerns, and why are they challenging to handle in traditional object-oriented programming?

2. How does Aspect-Oriented Programming (AOP) address cross-cutting concerns? What are some frameworks that support AOP in Java?

3. In our example, we implemented logging and security as cross-cutting concerns. What other cross-cutting concerns might be relevant in a commission calculator system?

4. What are the drawbacks of the approach used in our example compared to using a dedicated AOP framework?

5. How would you refactor the cross-cutting concerns example to make it more maintainable and scalable?

## Access Modifiers

1. What is the principle of encapsulation, and how do access modifiers help implement it?

2. When should you use `protected` access instead of `private` or package-private (default) access?

3. What are the security implications of using different access modifiers?

4. In our example, we used nested classes with different access modifiers. What are the rules for accessing members of outer classes from nested classes and vice versa?

5. How do access modifiers affect inheritance and polymorphism?

## Immutable Objects

1. What makes an object truly immutable in Java? Is it sufficient to just make all fields final?

2. What are the performance implications of using immutable objects, especially when "modifications" require creating new objects?

3. How does immutability help with thread safety? Are there any scenarios where immutable objects might still cause concurrency issues?

4. In our example, we used defensive copying for mutable objects. Why is this necessary, and what would happen if we didn't do it?

5. What built-in Java classes are immutable? Why were they designed to be immutable?

## Indirect Object Construction

1. What are the main differences between the Factory Method pattern and the Abstract Factory pattern?

2. When would you choose the Builder pattern over constructors with multiple parameters?

3. How does the Prototype pattern differ from simply using the `clone()` method? What are the challenges of implementing a proper `clone()` method?

4. What are the trade-offs between using these indirect construction patterns versus direct construction with constructors?

5. How do these patterns support the SOLID principles, particularly the Single Responsibility Principle and the Open/Closed Principle?

## General Questions

1. How do these concerns and patterns interact with each other? For example, how might immutability affect the implementation of cross-cutting concerns?

2. Which of these concerns or patterns do you think is most important for maintaining a large-scale enterprise application, and why?

3. How would you apply these concepts in a microservices architecture versus a monolithic application?

4. What testing strategies would you use to ensure that these patterns are correctly implemented?

5. How do modern programming languages and frameworks address these concerns differently than traditional Java?