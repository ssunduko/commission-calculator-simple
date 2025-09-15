# Decorator Pattern Questions

## Conceptual Questions

1. What is the Decorator Pattern, and how does it differ from inheritance for extending functionality?

2. What are the four key components of the Decorator Pattern? Describe the role of each.

3. How does the Decorator Pattern adhere to the Open/Closed Principle of SOLID design principles?

4. What are the advantages and disadvantages of using the Decorator Pattern compared to other design patterns?

5. In what scenarios would you choose to use the Decorator Pattern over other patterns like Strategy or Adapter?

## Implementation Questions

6. In our implementation, we created two separate examples of the Decorator Pattern. What are the key differences between these implementations?

7. Why do we need an abstract Decorator class? Couldn't we just have concrete decorators implement the Component interface directly?

8. How does the Decorator Pattern handle method calls that are not being decorated? What happens when a method is called on a decorated object that doesn't override that method?

9. In the `UrgencyDecorator` class, we modify both the `calculateValue()` and `getTitle()` methods. Is this a common practice in decorators? Why or why not?

10. What would happen if we tried to decorate an object with multiple decorators of the same type (e.g., two `DiscountDecorator`s)? Would this work, and what would be the result?

## Application Questions

11. How could you apply the Decorator Pattern to add validation logic to the Deal class without modifying its code?

12. Could the Decorator Pattern be used to implement a feature that tracks changes to a Deal over time? How would you design such a decorator?

13. In our implementation, we decorated the Deal class. What other classes in the commission calculator model could benefit from the Decorator Pattern?

14. How would you implement a decorator that caches the results of expensive calculations to improve performance?

15. The current implementation focuses on decorating individual objects. How could you extend this to decorate collections of objects (e.g., a list of deals)?

## Advanced Questions

16. How does the Decorator Pattern compare to Aspect-Oriented Programming (AOP) for adding cross-cutting concerns like logging?

17. What challenges might you face when serializing decorated objects? How would you address these challenges?

18. How would you implement a decorator that can be dynamically enabled or disabled at runtime?

19. In a multi-threaded environment, what considerations would you need to take into account when implementing decorators?

20. How could you use the Decorator Pattern in combination with other design patterns like Factory or Builder to create a more flexible system?