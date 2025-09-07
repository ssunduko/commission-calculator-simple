# Composition Over Inheritance

## Overview
The "Composition Over Inheritance" principle is a fundamental design guideline in object-oriented programming that suggests favoring object composition over class inheritance when designing software. This principle encourages developers to build complex functionality by combining simpler objects rather than creating deep inheritance hierarchies.

## Explanation
Composition over inheritance encourages developers to:

1. Design small, focused classes with a single responsibility
2. Combine these classes through composition to create more complex behavior
3. Avoid deep inheritance hierarchies that can lead to fragile code
4. Use interfaces to define contracts that composed objects must fulfill
5. Prefer "has-a" relationships over "is-a" relationships when appropriate

## Examples in this Directory

This directory contains examples of inheritance-based design and their composition-based alternatives:

- **Original**: Contains code examples that use inheritance to share code and behavior between classes.
- **Fixed**: Contains refactored versions of the same code, using composition to achieve the same functionality with greater flexibility.

## Common Inheritance Problems

1. **Fragile Base Class Problem**: Changes to a base class can unexpectedly break subclasses
2. **Rigid Hierarchies**: Inheritance creates tight coupling that makes code difficult to modify
3. **Inheritance Explosion**: As variations increase, inheritance hierarchies become unwieldy
4. **Method Name Conflicts**: When inheriting from multiple sources (in languages that support it), method name conflicts can occur
5. **Implementation Inheritance**: Inheriting implementation details rather than just interfaces leads to tight coupling

## Benefits of Composition

1. **Flexibility**: Composed objects can be swapped at runtime
2. **Testability**: Components can be tested in isolation
3. **Maintainability**: Changes to one component are less likely to affect others
4. **Reusability**: Small, focused components are more reusable
5. **Simplicity**: Composition often leads to simpler, more understandable code

## When to Use Inheritance

While composition is often preferred, inheritance is still appropriate in some cases:

1. When there is a clear "is-a" relationship that is unlikely to change
2. For framework extension points specifically designed for inheritance
3. When using well-designed, stable base classes with clear extension points
4. For implementing interfaces or abstract classes that define contracts