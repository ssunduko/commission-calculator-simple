# Inheritance

## Overview

Inheritance is one of the four fundamental OOP principles. It is the mechanism by which one class (the child or derived class) can inherit the properties and methods of another class (the parent or base class). This establishes an "is-a" relationship between the child and parent classes.

## Key Concepts

1. **Base Class (Parent Class)**: The class whose properties and methods are inherited by another class.
2. **Derived Class (Child Class)**: The class that inherits properties and methods from another class.
3. **Reusability**: Inheritance promotes code reuse by allowing derived classes to use code from base classes.
4. **Method Overriding**: A derived class can provide a specific implementation of a method that is already defined in its base class.
5. **super Keyword**: Used to call the parent class's methods and constructors.

## Examples in This Directory

### `BaseCommission.java`

This class serves as the base class for different types of commission calculations:
- Defines common properties and methods for all commission types
- Provides a basic implementation of commission calculation

### `BonusCommission.java`

This class extends `BaseCommission` to add bonus functionality:
- Inherits all properties and methods from `BaseCommission`
- Adds additional properties specific to bonus commissions
- Overrides methods to provide bonus-specific behavior

### `AcceleratedCommission.java`

This class extends `BaseCommission` to add accelerator functionality:
- Inherits all properties and methods from `BaseCommission`
- Adds additional properties specific to accelerated commissions
- Overrides methods to provide accelerator-specific behavior

### `InheritanceDemo.java`

This class demonstrates how inheritance works:
- Creates instances of the base and derived classes
- Shows how derived classes inherit properties and methods from the base class
- Demonstrates method overriding
- Shows polymorphic behavior (treating derived class objects as base class objects)

## Benefits of Inheritance

1. **Code Reusability**: Reduces code duplication by reusing existing code
2. **Extensibility**: Makes it easy to extend existing code with new functionality
3. **Hierarchical Classification**: Organizes classes into a hierarchical structure
4. **Method Overriding**: Allows specialized implementations in derived classes
5. **Polymorphism**: Enables treating objects of derived classes as objects of their base class