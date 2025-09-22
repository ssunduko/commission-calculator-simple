# Commission Calculator Concerns Examples

This directory contains examples of various programming concerns and patterns based on the commission calculator model classes. These examples demonstrate important concepts in software design and development.

## Examples Overview

### 1. Cross-Cutting Concerns

**File:** [CrossCuttingConcernsExample.java](CrossCuttingConcernsExample.java)

Cross-cutting concerns are aspects of a program that affect multiple parts of the system and can't be cleanly decomposed from the rest of the system. Examples include:

- Logging
- Security/Authorization
- Transaction management
- Error handling
- Performance monitoring

The example demonstrates how logging and security concerns cut across different business operations in the commission calculator system.

### 2. Access Modifiers

**File:** [AccessModifiersExample.java](AccessModifiersExample.java)

Access modifiers control the visibility and accessibility of classes, methods, and fields. Java provides four access levels:

- `public`: Accessible from any class
- `protected`: Accessible within the same package and subclasses
- Default (no modifier): Accessible only within the same package
- `private`: Accessible only within the same class

The example shows how these modifiers can be used to implement encapsulation and information hiding in a commission calculator system.

### 3. Immutable Objects

**File:** [ImmutableObjectExample.java](ImmutableObjectExample.java)

Immutable objects are objects whose state cannot be changed after they are created. Key characteristics:

- All fields are final
- The class is declared as final to prevent subclassing
- No setters or methods that modify state
- Proper handling of mutable object references (defensive copying)

Benefits of immutable objects:
- Thread-safe without synchronization
- Can be safely shared between multiple threads
- Simplifies concurrent programming
- Prevents temporal coupling
- Useful as keys in maps or elements in sets

The example demonstrates how to create and use immutable commission calculation objects.

### 4. Indirect Object Construction

**File:** [IndirectObjectConstructionExample.java](IndirectObjectConstructionExample.java)

Indirect object construction refers to creating objects through intermediary methods or classes rather than directly using constructors. This approach provides several benefits:

- Encapsulates complex object creation logic
- Provides meaningful names for different object configurations
- Enables reuse of object creation code
- Allows for object caching and object pooling
- Supports the creation of immutable objects

The example demonstrates several patterns for indirect object construction:

- **Factory Method Pattern**: Defines an interface for creating an object, but lets subclasses decide which class to instantiate
- **Abstract Factory Pattern**: Provides an interface for creating families of related or dependent objects
- **Builder Pattern**: Separates the construction of a complex object from its representation
- **Prototype Pattern**: Creates new objects by copying an existing object

## UML Diagram

The [concerns-diagram.puml](concerns-diagram.puml) file contains a PlantUML diagram showing the relationships between the classes in these examples.

## Additional Resources

- [QUESTIONS.md](QUESTIONS.md): Questions about the concepts presented in these examples
- [ANSWERS.md](ANSWERS.md): Answers to the questions in QUESTIONS.md