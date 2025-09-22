# Orthogonality Examples

This package contains examples of orthogonality in software design, implemented using the commission calculator model classes.

## What is Orthogonality?

Orthogonality in software design refers to the ability to change one component without affecting others. It's a measure of independence between components. The term comes from mathematics, where orthogonal vectors are perpendicular to each other, meaning a change in one dimension doesn't affect the other dimensions.

In software, high orthogonality means that components are independent and can be modified, replaced, or reused without affecting other parts of the system. Low orthogonality means that components are tightly coupled, making changes more difficult and risky.

## Examples in this Package

### 1. High Orthogonality

**Example**: [HighOrthogonality.java](HighOrthogonality.java)

This class demonstrates high orthogonality by separating different concerns into independent components:

- `DealProcessor`: Handles deal-related operations
- `UserProcessor`: Handles user-related operations
- `ReportGenerator`: Handles report generation

Each component has a single responsibility and doesn't depend on the implementation details of other components. They can be modified independently without affecting each other.

### 2. Low Orthogonality

**Example**: [LowOrthogonality.java](LowOrthogonality.java)

This class demonstrates low orthogonality by tightly coupling different concerns in a single class with interdependent methods that share state:

- Methods depend on shared state being properly set
- Methods have side effects, modifying shared state
- The order of method calls matters
- Changes to one aspect affect other operations
- Testing is difficult because of the shared state and dependencies

### 3. Orthogonality Principles

**Example**: [OrthogonalityPrinciples.java](OrthogonalityPrinciples.java)

This class demonstrates key principles that help achieve orthogonality in software design:

1. **Separation of Concerns**: Separate different aspects of the program into distinct sections
2. **Single Responsibility Principle**: Each class should have only one reason to change
3. **Dependency Injection**: Inject dependencies from outside instead of creating them inside a class
4. **Interface Segregation**: Create specific interfaces rather than general-purpose ones
5. **Pure Functions**: Functions that always produce the same output for the same input and have no side effects
6. **Immutability**: Objects that cannot be changed after creation
7. **Command-Query Separation**: Methods should either change state or return values, but not both

## Benefits of Orthogonality

High orthogonality in software design provides several benefits:

1. **Maintainability**: Changes are localized to specific components, making maintenance easier
2. **Testability**: Components can be tested in isolation
3. **Reusability**: Independent components can be reused in different contexts
4. **Flexibility**: Components can be replaced or modified without affecting the rest of the system
5. **Scalability**: The system can grow by adding new orthogonal components
6. **Reduced Complexity**: Interactions between components are minimized and well-defined

## Achieving Orthogonality

To achieve high orthogonality in your code:

1. **Follow the Single Responsibility Principle**: Each class should have only one reason to change
2. **Use Dependency Injection**: Inject dependencies rather than creating them inside a class
3. **Avoid Shared State**: Minimize the use of global variables and shared mutable state
4. **Use Immutable Objects**: Make objects immutable when possible
5. **Write Pure Functions**: Functions that don't have side effects and always return the same output for the same input
6. **Use Interfaces**: Define clear interfaces between components
7. **Apply Command-Query Separation**: Methods should either change state or return values, but not both

## UML Diagram

The [orthogonality-examples.puml](orthogonality-examples.puml) file contains a UML diagram showing the relationships between the classes in this package. The diagram illustrates the differences between high and low orthogonality and shows how the orthogonality principles are applied.

## Relationship to Other Design Principles

Orthogonality is closely related to other design principles:

- **Cohesion**: High cohesion (elements within a module are strongly related) complements orthogonality
- **Coupling**: Low coupling (modules are relatively independent) is a prerequisite for orthogonality
- **SOLID Principles**: All SOLID principles contribute to orthogonality
- **Separation of Concerns**: A key aspect of achieving orthogonality
- **Modularity**: Orthogonal systems are inherently modular

By applying these principles together, you can create software that is easier to understand, maintain, test, and extend.