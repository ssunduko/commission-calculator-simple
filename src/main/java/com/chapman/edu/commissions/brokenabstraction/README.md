# Abstraction Anti-Patterns

This package demonstrates three common abstraction anti-patterns in object-oriented design:

1. **God Object**
2. **Anemic Domain Model**
3. **Feature Envy**

These anti-patterns are implemented using the commission calculator domain model as a basis.

## God Object Anti-Pattern

A God Object (or God Class) is an object that knows too much or does too much. It's a class that has grown too large and has too many responsibilities.

### Characteristics of a God Object:

- It has too many instance variables and methods
- It has low cohesion (methods and data are not strongly related)
- It has high coupling (depends on many other classes)
- It violates the Single Responsibility Principle
- It's difficult to understand, maintain, and test

### Implementation:

The `GodObject` class demonstrates this anti-pattern by combining user management, deal management, commission calculation, reporting, and data persistence into a single class. It has numerous fields and methods that handle different concerns, making it a maintenance nightmare.

### Problems:

- **Maintainability**: Changes to one aspect of the system might affect unrelated parts
- **Testability**: Testing becomes difficult due to the size and complexity
- **Reusability**: The class cannot be reused in different contexts
- **Readability**: Understanding the class requires understanding the entire system

### Better Approach:

Break down the God Object into smaller, focused classes, each with a single responsibility:
- `UserService` for user management
- `DealService` for deal management
- `CommissionCalculator` for commission calculations
- `ReportGenerator` for report generation
- `DatabaseService` for data persistence
- `EmailService` for sending emails

## Anemic Domain Model Anti-Pattern

An Anemic Domain Model is a domain model with very little behavior, consisting mostly of getter and setter methods. It's essentially a collection of data containers with no real domain logic.

### Characteristics of an Anemic Domain Model:

- Domain objects are just data holders with getters and setters
- Business logic is moved to service classes
- Domain objects have no behavior or validation
- Domain objects are not encapsulated
- Business rules are scattered across service classes

### Implementation:

The `AnemicDomainModel` example demonstrates this anti-pattern by creating domain objects (`AnemicUser`, `AnemicDeal`, `AnemicDealProduct`, `AnemicCommissionPlan`, `AnemicCommissionCalculation`) that are just data containers with getters and setters. All business logic is moved to service classes (`UserService`, `DealService`, `CommissionService`).

### Problems:

- **Encapsulation**: Domain objects don't protect their internal state
- **Business Logic**: Business rules are scattered across service classes
- **Object-Oriented Design**: The design doesn't leverage OO principles
- **Maintenance**: Changes to business rules require changes in multiple places

### Better Approach:

Create a rich domain model where objects encapsulate both data and behavior:
- Domain objects should contain business logic related to their data
- Validation should be performed within domain objects
- Domain objects should protect their internal state
- Service classes should coordinate domain objects, not contain domain logic

## Feature Envy Anti-Pattern

Feature Envy occurs when a method in one class seems more interested in the features (methods or attributes) of another class than the ones in its own class. It's a sign that the method might belong in the other class.

### Characteristics of Feature Envy:

- A method accesses the data of another object more than its own data
- A method calls multiple methods on another object to perform its function
- A method is more interested in the state of another class than its own
- A method uses more features of another class than its own class
- A method would be better placed in the class it's envying

### Implementation:

The `FeatureEnvy` example demonstrates this anti-pattern with methods in the `CommissionCalculator`, `ReportGenerator`, and `NotificationService` classes that are more interested in the features of the `Deal` class than their own.

### Problems:

- **Encapsulation**: The method breaks encapsulation by accessing too much of another class's data
- **Cohesion**: The method reduces cohesion in its own class
- **Coupling**: The method increases coupling between classes
- **Maintenance**: Changes to the envied class might require changes in multiple places

### Better Approach:

Move the method to the class it's envying or create a proper domain service:
- Move methods to the class they're most interested in
- Create domain services that don't rely heavily on the internal details of domain objects
- Use the Tell, Don't Ask principle to reduce feature envy

## UML Diagram

The `AbstractionAntiPatterns.puml` file contains a PlantUML diagram showing the relationships between the classes in the three anti-pattern examples. The diagram shows the classes and their relationships without showing the package structure.

## Conclusion

Understanding these anti-patterns helps in designing better object-oriented systems. By recognizing and avoiding these patterns, you can create more maintainable, testable, and reusable code.