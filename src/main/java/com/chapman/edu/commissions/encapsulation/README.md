# Encapsulation Examples

This directory contains examples of different types of encapsulation in Java. Encapsulation is one of the four fundamental OOP concepts and refers to the bundling of data with the methods that operate on that data, or the restricting of direct access to some of an object's components.

## Types of Encapsulation Demonstrated

### 1. Data Encapsulation
**File:** `DataEncapsulationExample.java`

Data encapsulation is the most common form of encapsulation in Java. It involves:
- Making fields private
- Providing public getter and setter methods
- Controlling access to the internal state of an object
- Validating data before it's modified

The example demonstrates:
- Private fields with public accessors
- Validation in setter methods
- Defensive copying of collections
- Immutable objects

### 2. Method Encapsulation
**File:** `MethodEncapsulationExample.java`

Method encapsulation involves hiding the implementation details of methods:
- Public methods expose what the class does, not how it does it
- Implementation details are hidden in private helper methods
- Complex algorithms are broken down into simpler, encapsulated steps
- Changes to implementation don't affect client code

The example demonstrates:
- Complex commission calculation logic hidden behind simple public methods
- Private helper methods for implementation details
- Clear separation between public API and internal implementation

### 3. Class Encapsulation
**File:** `ClassEncapsulationExample.java`

Class encapsulation involves hiding internal classes and structures:
- Internal implementation classes are hidden from clients
- Only necessary interfaces are exposed
- Implementation details can change without affecting client code
- Reduces complexity for clients

The example demonstrates:
- Public facade class (CommissionPlanManager) that clients interact with
- Private internal classes (CommissionPlan, CommissionRule, CommissionCalculator)
- Data Transfer Objects (DTOs) to expose only necessary information

### 4. Module Encapsulation
**File:** `module/` directory and `ModuleEncapsulationExample.java`

Module encapsulation involves using package-level access control:
- Package-private classes are only accessible within their package
- Public classes provide a controlled interface to the package
- Implementation details are hidden within the package
- Creates clear boundaries between different parts of the system

The example demonstrates:
- Package-private classes (User, UserRepository, UserValidator)
- Public facade class (UserManager) that clients interact with
- Data Transfer Objects (DTOs) to expose only necessary information
- Clear separation between public API and internal implementation

### 5. Interface Encapsulation
**File:** `InterfaceEncapsulationExample.java`

Interface encapsulation involves abstracting implementation behind contracts:
- Interfaces define what a class does, not how it does it
- Multiple implementations can be provided for the same interface
- Clients depend on interfaces, not concrete implementations
- Supports the Dependency Inversion Principle

The example demonstrates:
- PaymentProcessor interface that defines a contract
- Multiple implementations (CreditCardProcessor, PayPalProcessor)
- Client code (PaymentService) that depends only on the interface
- Loose coupling between components

## Key Benefits of Encapsulation

1. **Information Hiding**: Hides the internal state and implementation details
2. **Modularity**: Creates clear boundaries between different parts of the system
3. **Flexibility**: Allows implementation to change without affecting client code
4. **Maintainability**: Makes code easier to understand, maintain, and extend
5. **Reusability**: Promotes code reuse through well-defined interfaces
6. **Testability**: Makes code easier to test through clear interfaces and dependencies

## How to Run the Examples

Each example has a `main` method that demonstrates the concepts. You can run them individually to see the encapsulation principles in action.