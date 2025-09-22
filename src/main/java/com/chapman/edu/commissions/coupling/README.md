# Coupling Examples

This package contains examples of different types of coupling in software design, based on the classes in the `com.chapman.edu.commissions.model` package. Coupling refers to the degree of interdependence between software modules. Lower coupling generally leads to more maintainable and flexible code.

## Types of Coupling (From Highest to Lowest)

### 1. Content Coupling (`ContentCoupling.java`)
Content coupling occurs when one module directly accesses or modifies the internal data of another module. This is considered the highest level of coupling and should generally be avoided.

**Example:** The `ContentCoupling` class directly accesses the internal products list of the `Deal` class without using proper accessor methods, creating a tight dependency between the classes.

### 2. Common Coupling (`CommonCoupling.java`)
Common coupling occurs when multiple modules share global data. This creates dependencies between modules through the shared data.

**Example:** The `GlobalConfig` class contains static fields that are accessed by multiple other classes (`DealManager` and `ProductManager`), creating common coupling.

### 3. Control Coupling (`ControlCoupling.java`)
Control coupling occurs when one module passes a flag, switch, or other control information to another module, influencing its internal logic and behavior.

**Example:** The `DealProcessor` class receives control flags (like `ProcessingMode` enum and boolean flags) that determine how it processes deals, creating control coupling.

### 4. Stamp Coupling (`StampCoupling.java`)
Stamp coupling occurs when modules share a composite data structure and use only parts of it. This creates dependencies between modules through the shared data structure.

**Example:** The `DealAnalyzer` and `DealReporter` classes receive the entire `Deal` object but only use specific parts of it, creating stamp coupling.

### 5. Data Coupling (`DataCoupling.java`)
Data coupling occurs when modules share data through parameters. This is considered a low level of coupling and is generally desirable.

**Example:** The `CommissionCalculator` and `ProductValueCalculator` classes receive only the specific data they need through parameters, rather than entire objects, creating data coupling.

### 6. Message Coupling (`MessageCoupling.java`)
Message coupling occurs when components communicate only through messages or interfaces, without sharing any internal data. This is the lowest form of coupling and is highly desirable.

**Example:** The `DealService` class communicates with `CommissionService` and `NotificationService` through the `DealEventListener` interface, creating message coupling.

## Best Practices

1. **Aim for Lower Coupling**: In general, aim for lower forms of coupling (message, data) rather than higher forms (content, common).

2. **Use Interfaces**: Define clear interfaces between components to reduce coupling.

3. **Dependency Injection**: Use dependency injection to provide dependencies to a class rather than having the class create or find them.

4. **Encapsulation**: Properly encapsulate data within classes and provide controlled access through methods.

5. **Avoid Global State**: Minimize the use of global variables and static fields that can be accessed from anywhere.

By understanding and applying these principles, you can create more maintainable, flexible, and testable code.