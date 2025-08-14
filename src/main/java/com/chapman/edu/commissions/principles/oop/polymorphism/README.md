# Polymorphism

## Overview

Polymorphism is one of the four fundamental OOP principles. It allows objects of different classes to be treated as objects of a common superclass or interface. The word "polymorphism" means "many forms," and in OOP, it refers to the ability of a single interface or method to work with different types of objects.

## Key Concepts

1. **Method Overriding**: A subclass provides a specific implementation of a method that is already defined in its superclass.
2. **Method Overloading**: Multiple methods with the same name but different parameters in the same class.
3. **Interface Implementation**: Different classes implementing the same interface in different ways.
4. **Runtime Polymorphism**: The JVM determines which method to call at runtime based on the actual object type.
5. **Compile-time Polymorphism**: The compiler determines which method to call based on the method signature.

## Examples in This Directory

### `CommissionCalculator.java` (Interface)

This interface defines the contract for different commission calculator implementations:
- Declares methods for calculating commissions
- Provides a common type for all commission calculators

### `StandardCommissionCalculator.java`

This class implements the `CommissionCalculator` interface for standard commissions:
- Provides a specific implementation for calculating standard commissions
- Demonstrates interface implementation

### `TieredCommissionCalculator.java`

This class implements the `CommissionCalculator` interface for tiered commissions:
- Provides a specific implementation for calculating tiered commissions
- Demonstrates interface implementation with different behavior

### `PerformanceCommissionCalculator.java`

This class implements the `CommissionCalculator` interface for performance-based commissions:
- Provides a specific implementation for calculating performance-based commissions
- Demonstrates interface implementation with different behavior

### `PolymorphismDemo.java`

This class demonstrates how polymorphism works:
- Creates instances of different commission calculator implementations
- Shows how they can be treated as the common interface type
- Demonstrates runtime polymorphism by calling methods on interface references
- Shows how the same method call produces different results based on the actual object type

## Benefits of Polymorphism

1. **Flexibility**: Code can work with objects of different types through a common interface
2. **Extensibility**: New implementations can be added without changing existing code
3. **Simplification**: Complex conditional logic can be replaced with polymorphic method calls
4. **Decoupling**: Code depends on abstractions rather than concrete implementations
5. **Maintainability**: Changes to one implementation don't affect others