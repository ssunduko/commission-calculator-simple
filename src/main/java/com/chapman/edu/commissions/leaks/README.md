# Leaky Abstractions Examples

This package contains examples of leaky abstractions in software design, based on the commission calculator model classes. Each example demonstrates a specific principle and shows both a problematic implementation (leaky abstraction) and an improved implementation.

## What are Leaky Abstractions?

A leaky abstraction is an abstraction that exposes details and limitations of its underlying implementation that should ideally be hidden from the user. The term was coined by Joel Spolsky in his article "The Law of Leaky Abstractions" (2002).

When abstractions leak, they can cause several problems:
- They make code harder to understand and use correctly
- They create dependencies on implementation details that should be hidden
- They make the system more difficult to change and maintain
- They can lead to unexpected behavior and bugs

## Examples Included

### 1. Design For Change

**File**: `DesignForChangeExample.java`

This example demonstrates how tightly coupling the commission calculation logic directly to specific deal types creates a leaky abstraction that makes it difficult to add new deal types or change the calculation logic.

The improved implementation uses the Strategy pattern to allow for different commission calculation strategies, making the system more flexible and easier to extend.

### 2. Layered Architecture

**File**: `LayeredArchitectureExample.java`

This example shows how abstractions leak when layers are not properly separated, causing higher layers to depend on implementation details of lower layers.

The improved implementation demonstrates a proper layered architecture with clear separation of concerns between presentation, business logic, and data access layers.

### 3. Standard Interfaces

**File**: `StandardInterfacesExample.java`

This example illustrates how inconsistent interfaces can leak implementation details, making the system harder to understand, use, and maintain.

The improved implementation uses a standard interface for all commission calculators, providing a consistent contract that hides implementation details.

### 4. Defensive Programming

**File**: `DefensiveProgrammingExample.java`

This example shows how the lack of defensive programming techniques can lead to leaky abstractions that expose implementation details and assumptions.

The improved implementation uses defensive programming techniques like input validation, precondition checking, immutability, defensive copying, and exception handling to protect implementation details.

### 5. Documented Expectations

**File**: `DocumentedExpectationsExample.java`

This example demonstrates how poor documentation can lead to leaky abstractions by not clearly communicating expected behavior, inputs, outputs, preconditions, and postconditions.

The improved implementation uses comprehensive documentation, including class and method descriptions, parameter and return value descriptions, preconditions and postconditions, exception documentation, and usage examples.

## How to Use These Examples

Each example file contains a `main` method that demonstrates both the problematic and improved implementations. You can run each file individually to see the examples in action.

The examples are designed to be educational and illustrate the concepts of leaky abstractions. They are not intended for production use.

## Key Takeaways

1. **Design for Change**: Use design patterns like Strategy, Factory, and Template Method to make your code more flexible and easier to change.

2. **Layered Architecture**: Maintain clear separation between layers, with each layer only depending on the layer directly below it.

3. **Standard Interfaces**: Define consistent interfaces that hide implementation details and provide a clear contract for clients.

4. **Defensive Programming**: Use techniques like input validation, precondition checking, immutability, defensive copying, and exception handling to protect your implementation details.

5. **Documented Expectations**: Clearly document the expected behavior, inputs, outputs, preconditions, postconditions, and invariants of your code.

By following these principles, you can create more robust, maintainable, and flexible software systems with fewer leaky abstractions.