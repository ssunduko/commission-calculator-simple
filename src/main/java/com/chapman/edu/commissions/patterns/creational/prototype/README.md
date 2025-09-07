# Prototype Pattern Implementation

## Overview
This directory contains an implementation of the Prototype Design Pattern using the Commission Calculator system's model classes. The Prototype pattern is a creational design pattern that allows cloning objects, even complex ones, without coupling to their specific classes.

## What is the Prototype Pattern?
The Prototype pattern involves creating a prototypical instance first and then cloning it whenever you need a copy. This pattern is particularly useful when:

1. The cost of creating a new object is expensive or complex
2. You want to hide the complexity of creating new instances from the client
3. You need to create objects that are similar to existing objects
4. You want to avoid building a class hierarchy of factories that parallels the class hierarchy of products

## Implementation Details

### Files in this Directory

1. **Prototype.puml**: UML diagram illustrating the structure of the Prototype pattern implementation.
2. **PrototypePatternImplementation.java**: Contains the core implementation of the Prototype pattern, including:
   - The `Prototype<T>` interface with the `clone()` method
   - `CloneableDeal` class that extends `Deal` and implements the Prototype interface
   - `CloneableDealProduct` class that extends `DealProduct` and implements the Prototype interface
3. **PrototypeRegistry.java**: Implements a registry for storing and retrieving prototype objects.
4. **PrototypePatternUsage.java**: Demonstrates how to use the Prototype pattern in practice.

### Key Components

#### Prototype Interface
The `Prototype<T>` interface declares the cloning method that all concrete prototypes must implement:
```java
public interface Prototype<T> {
    T clone();
}
```

#### Concrete Prototypes
- **CloneableDeal**: Extends the `Deal` class and implements the `Prototype` interface, providing both shallow and deep cloning capabilities.
- **CloneableDealProduct**: Extends the `DealProduct` class and implements the `Prototype` interface.

#### Prototype Registry
The `PrototypeRegistry` class provides a way to store and retrieve prototype objects by name, allowing for easy access to commonly used prototypes.

## Shallow vs. Deep Cloning

The implementation demonstrates both shallow and deep cloning:

- **Shallow Cloning**: Copies all primitive fields and references but doesn't clone referenced objects. This means that changes to referenced objects in the original will affect the clone.
- **Deep Cloning**: Copies all fields and creates new instances of referenced objects. This ensures that changes to referenced objects in the original won't affect the clone.

## Usage Examples

The `PrototypePatternUsage.java` file demonstrates three key aspects of the Prototype pattern:

1. **Basic Prototype Usage**: Shows how to create a prototype and clone it to create new instances.
2. **Shallow vs. Deep Cloning**: Illustrates the difference between shallow and deep cloning by showing how modifications to referenced objects affect shallow clones but not deep clones.
3. **Prototype Registry Usage**: Demonstrates how to use a Prototype Registry to store and retrieve prototype objects.

## Benefits of the Prototype Pattern

- **Reduced Subclassing**: You can clone objects without depending on their concrete classes.
- **Reduced Initialization Cost**: You can create new objects by copying existing ones, avoiding expensive initialization.
- **Dynamic Configuration**: You can add and remove prototypes at runtime.
- **Reduced Complexity**: You can create complex objects more easily by cloning existing ones.

## Considerations

- **Circular References**: Deep cloning objects with circular references requires special handling.
- **Clone Method Implementation**: Implementing the `clone()` method can be complex for objects with many fields or complex relationships.
- **Immutable Objects**: For immutable objects, the Prototype pattern may not provide significant benefits.