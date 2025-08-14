# Abstraction

## Overview

Abstraction is one of the four fundamental OOP principles. It focuses on hiding complex implementation details and showing only the necessary features of an object. Abstraction allows you to focus on what an object does rather than how it does it.

## Key Concepts

1. **Abstract Classes**: Classes that cannot be instantiated and may contain abstract methods (methods without implementation).
2. **Abstract Methods**: Methods declared without an implementation, which must be implemented by concrete subclasses.
3. **Interfaces**: A completely abstract type that defines a contract for implementing classes.
4. **Implementation Hiding**: Hiding the internal details of how an object works.
5. **Simplified Interaction**: Providing a simple interface for interacting with complex systems.

## Examples in This Directory

### `ReportGenerator.java` (Abstract Class)

This abstract class defines the framework for generating reports:
- Declares abstract methods that must be implemented by subclasses
- Provides common functionality in concrete methods
- Implements the Template Method pattern

### `CommissionReportGenerator.java`

This class extends `ReportGenerator` for commission reports:
- Implements the abstract methods from the parent class
- Provides specific implementation for generating commission reports

### `SalesReportGenerator.java`

This class extends `ReportGenerator` for sales reports:
- Implements the abstract methods from the parent class
- Provides specific implementation for generating sales reports

### `PerformanceReportGenerator.java`

This class extends `ReportGenerator` for performance reports:
- Implements the abstract methods from the parent class
- Provides specific implementation for generating performance reports

### `AbstractionDemo.java`

This class demonstrates how abstraction works:
- Creates instances of concrete report generator classes
- Shows how abstract classes define a common structure
- Demonstrates how the Template Method pattern works
- Shows how abstraction simplifies interaction with complex systems

## Benefits of Abstraction

1. **Reduced Complexity**: Users of the class don't need to understand the internal details
2. **Enhanced Security**: Implementation details are hidden from users
3. **Easy Maintenance**: Changes to implementation don't affect the abstraction's users
4. **Focused Design**: Focus on essential features without getting lost in details
5. **Code Reusability**: Abstract classes can provide common functionality for subclasses