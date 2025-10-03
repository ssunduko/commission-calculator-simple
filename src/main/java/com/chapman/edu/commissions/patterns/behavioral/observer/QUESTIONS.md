# Observer Pattern - Knowledge Test Questions

## About This Document

These questions test your understanding of the Observer Pattern as implemented in this package. The implementation is organized into three files:
- **ObserverStructure.java** - Core pattern structure (interfaces and abstractions)
- **ObserverImplementation.java** - Concrete implementations (subject and observers)
- **ObserverUsage.java** - Usage examples, best practices, and common pitfalls

Refer to these files and the accompanying ANSWERS.md for detailed explanations.

---

## Multiple Choice Questions

1. **What is the primary intent of the Observer Pattern?**
   - a) To create a single instance of a class
   - b) To define a one-to-many dependency between objects
   - c) To encapsulate a request as an object
   - d) To provide a way to access elements of a collection sequentially

2. **In the Observer Pattern, what is the role of the Subject?**
   - a) To receive notifications from observers
   - b) To maintain a list of observers and notify them of state changes
   - c) To implement the update logic when notified
   - d) To create concrete observer instances

3. **Which method do observers typically implement to receive notifications?**
   - a) `notify()`
   - b) `observe()`
   - c) `update()` or similar (like `onDealUpdated()`)
   - d) `subscribe()`

4. **What is a key benefit of the Observer Pattern?**
   - a) It reduces memory usage
   - b) It promotes loose coupling between subject and observers
   - c) It guarantees the order of notifications
   - d) It prevents infinite loops

5. **In our implementation, when is the `notifyObservers()` method called?**
   - a) When an observer is attached
   - b) When an observer is detached
   - c) When the subject's state changes
   - d) At regular time intervals

6. **What happens when an observer is detached from a subject?**
   - a) All observers are removed
   - b) The subject stops functioning
   - c) That specific observer stops receiving notifications
   - d) The observer is destroyed

7. **Which design principle does the Observer Pattern best support?**
   - a) Single Responsibility Principle
   - b) Open/Closed Principle
   - c) Liskov Substitution Principle
   - d) Interface Segregation Principle

8. **In the "push" model of the Observer Pattern (used in our implementation), what does the subject do?**
   - a) Waits for observers to request data
   - b) Sends detailed information to observers during notification
   - c) Polls observers for updates
   - d) Only notifies that something changed, without sending data

9. **Why does our `ObservableDealTracker.notifyObservers()` method catch exceptions?**
   - a) To log all observer activities
   - b) To prevent one faulty observer from breaking the notification chain
   - c) To improve performance
   - d) To enforce error handling in observers

10. **Can a single observer instance observe multiple subjects?**
    - a) No, observers can only watch one subject
    - b) Yes, but only if they are the same type of subject
    - c) Yes, observers can be attached to multiple subjects
    - d) Only if using the pull model

## Short Answer Questions

11. **Explain the difference between the "push" model and the "pull" model in the Observer Pattern. Which one is used in our implementation?**

12. **Describe a real-world scenario (outside of software) that exemplifies the Observer Pattern.**

13. **What are the responsibilities of the Subject interface (`DealSubject`) in our implementation?**

14. **How does the Observer Pattern promote the Open/Closed Principle?**

15. **In our implementation, why do we use the interface type (`DealObserver`) instead of concrete types when storing observers in the subject?**

## Code Analysis Questions

16. **Review the `CommissionCalculationObserver` class in ObserverImplementation.java. Why does it only respond to "STATUS_CHANGED" events when the status is `WON`?**

17. **Examine the `ObservableDealTracker.attach()` method. Why does it check if the observer already exists before adding it?**

18. **Look at the three concrete observers (CommissionCalculationObserver, AuditLogObserver, NotificationObserver). How are they different in terms of which events they respond to?**

19. **In `ObserverPatternDemo`, what would happen if we called `createDeal()` before attaching any observers?**

20. **Why does `NotificationObserver` maintain a list of sent notifications? What purpose does this serve?**

## Design Questions

21. **What are potential drawbacks or challenges of using the Observer Pattern?**

22. **How would you modify the implementation to allow observers to specify which types of events they want to receive?**

23. **Compare and contrast the Observer Pattern with the Mediator Pattern. When would you use one over the other?**

24. **In our implementation, observers are notified in the order they were attached. How could you modify the design to support prioritized observers?**

25. **What would be the consequences of having the `notifyObservers()` method call observer updates asynchronously (in separate threads)?**

## Practical Application Questions

26. **Scenario: You need to add a new feature that sends SMS messages when high-value deals are closed. How would you implement this using the Observer Pattern without modifying existing code?**

27. **Scenario: An observer is causing performance issues by taking too long to process updates. How would you identify and address this problem?**

28. **Scenario: You need to ensure that the AuditLogObserver always runs before other observers. How would you modify the implementation to support this requirement?**

29. **Scenario: You want to implement an observer that prevents a deal from being created if certain conditions aren't met. How would you modify the pattern to support validation observers?**

30. **Scenario: How would you test the `ObservableDealTracker` to ensure it properly notifies all observers? Describe a JUnit test approach.**

## Critical Thinking Questions

31. **The Observer Pattern can lead to "cascading updates" where one notification triggers another, potentially creating an infinite loop. How could this happen in our implementation, and how would you prevent it?**

32. **Some developers argue that the Observer Pattern violates the "Law of Demeter" (principle of least knowledge). Do you agree? Why or why not?**

33. **Modern frameworks often use event buses or reactive streams instead of the traditional Observer Pattern. What advantages do these approaches offer?**

34. **In our implementation, the subject stores references to observers. Could this cause memory leaks? If so, how would you prevent them?**

35. **How does the Observer Pattern relate to the Model-View-Controller (MVC) architectural pattern?**