# Builder Pattern Implementation

This directory contains examples of the Builder pattern implemented using the classes from the `com.chapman.edu.commissions.model` package.

## Overview

The Builder pattern is a creational design pattern that separates the construction of a complex object from its representation. It allows the same construction process to create different representations of an object.

## Files in this Directory

1. **BuilderPatternStructure.java**
   - Demonstrates the general structure of the Builder pattern
   - Shows the components: Product, Builder, ConcreteBuilder, and Director
   - Includes detailed comments explaining the pattern's benefits and a real-world analogy

2. **BuilderPatternImplementation.java**
   - Provides a concrete implementation of the Builder pattern using the `Deal` class
   - Implements a `DealBuilder` that creates `Deal` objects with various properties
   - Shows how to handle required and optional parameters

3. **BuilderPatternUsage.java**
   - Demonstrates how to use the Builder pattern in practice
   - Shows different ways to create `Deal` objects using the `DealBuilder`
   - Illustrates the benefits of the pattern from a client's perspective

4. **DirectorPatternWithBuilder.java**
   - Shows how the Director pattern works with the Builder pattern
   - Implements a `DealDirector` that encapsulates the construction process
   - Demonstrates how to create different types of deals using the same builder

## Key Concepts Demonstrated

### Builder Pattern

- **Separation of Concerns**: The construction process is separated from the representation
- **Step-by-Step Construction**: Objects are built step by step, with each step returning the builder
- **Method Chaining**: Builder methods return the builder itself to allow method chaining
- **Immutability Support**: Helps create immutable objects without complex constructors
- **Readability**: Makes code more readable by clearly showing what each parameter represents

### Director Pattern

- **Encapsulation**: Complex construction logic is encapsulated in one place
- **Reusability**: Construction processes can be reused across the application
- **Isolation**: Client code is isolated from construction details
- **Flexibility**: Different construction processes can use the same builder

## When to Use the Builder Pattern

- When the algorithm for creating a complex object should be independent of the parts that make up the object
- When the construction process must allow different representations for the object that's constructed
- When you need to construct objects that contain a lot of parameters, some optional
- When you want to avoid "telescoping constructor" anti-pattern (multiple constructors with different parameter combinations)

## Implementation Details

The implementation uses the `Deal` class from the `com.chapman.edu.commissions.model` package as the product being built. The `DealBuilder` provides methods for setting each property of the `Deal`, and the `build()` method returns the final `Deal` object.

The `DealDirector` encapsulates the construction process for different types of deals, such as standard deals, premium deals, won deals, and lost deals. It works with any builder that follows the `DealBuilder` interface.