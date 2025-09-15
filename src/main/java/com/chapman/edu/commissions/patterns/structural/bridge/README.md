# Bridge Pattern Implementation

## Overview
This directory contains an implementation of the Bridge Pattern using the Commission Calculator domain model. The Bridge Pattern is a structural design pattern that separates an abstraction from its implementation so that the two can vary independently.

## What is the Bridge Pattern?
The Bridge Pattern is a design pattern that decouples an abstraction from its implementation so that the two can vary independently. It involves an interface which acts as a bridge between the abstraction class and implementation classes.

## Key Components
1. **Abstraction**: Defines the abstract interface and maintains a reference to the implementor
2. **Refined Abstraction**: Extends the abstraction and provides more specialized operations
3. **Implementor**: Defines the interface for implementation classes
4. **Concrete Implementor**: Implements the Implementor interface

## Implementation Details
In this implementation, we've created:

### 1. BridgePatternStructure.java
This class demonstrates the structure of the Bridge Pattern, showing the key components and their relationships. It includes:
- A `CommissionCalculator` interface (Implementor) that defines the operations for calculating commissions
- Two concrete implementations of the `CommissionCalculator` interface: `StandardCommissionCalculator` and `TieredCommissionCalculator`
- An abstract `CommissionService` class (Abstraction) that maintains a reference to the `CommissionCalculator`
- Two refined abstractions: `SalesCommissionService` and `PartnerCommissionService`

### 2. BridgePatternImplementation.java
This class provides a concrete implementation of the Bridge Pattern using the Commission Calculator domain model. It includes:
- A `CommissionCalculationStrategy` interface (Implementor) that defines the operations for calculating commissions
- Three concrete implementations of the `CommissionCalculationStrategy` interface: `FlatRateStrategy`, `TieredValueStrategy`, and `ProductBasedStrategy`
- An abstract `CommissionProcessor` class (Abstraction) that maintains a reference to the `CommissionCalculationStrategy`
- Two refined abstractions: `SalesRepCommissionProcessor` and `ManagerCommissionProcessor`
- Supporting services: `DealService` and `UserService`

### 3. BridgePatternUsage.java
This class demonstrates how to use the Bridge Pattern implementation. It includes:
- Examples of creating different commission calculation strategies
- Examples of creating different commission processors
- Examples of processing commissions for different deals
- Examples of changing strategies at runtime
- Examples of comparing different strategies for the same deal

### 4. bridge_pattern.puml
This file contains a UML diagram of the Bridge Pattern implementation, showing the relationships between the classes and the components of the pattern.

## Benefits of the Bridge Pattern
1. **Decoupling Interface from Implementation**: Separates the interface from the implementation, allowing them to vary independently
2. **Improved Extensibility**: Both the abstraction and implementation can be extended independently
3. **Hiding Implementation Details**: Implementation details are hidden from the client
4. **Runtime Flexibility**: The implementation can be changed at runtime

## When to Use the Bridge Pattern
- When you want to avoid a permanent binding between an abstraction and its implementation
- When both the abstractions and their implementations should be extensible through subclasses
- When changes in the implementation should not impact the client code
- When you have a proliferation of classes resulting from a coupled interface and numerous implementations

## Real-World Analogy
Think of a remote control (abstraction) and a device (implementation). The remote control can work with different devices (TV, DVD player, sound system), and each device can have different implementations (different brands, models). The remote control doesn't need to know the specific details of how each device works, it just needs to send the appropriate commands through the interface.

## Bridge Pattern vs. Adapter Pattern
While both patterns involve an abstraction and an implementation, they serve different purposes:
- The Bridge Pattern is designed to separate an abstraction from its implementation so that both can vary independently.
- The Adapter Pattern is designed to make incompatible interfaces compatible, allowing classes to work together that couldn't otherwise.

## Bridge Pattern vs. Strategy Pattern
The Bridge Pattern and Strategy Pattern are similar in that they both involve composition and delegation:
- The Bridge Pattern focuses on separating an abstraction from its implementation.
- The Strategy Pattern focuses on defining a family of algorithms, encapsulating each one, and making them interchangeable.

In our implementation, we've combined aspects of both patterns, using the Bridge Pattern to separate the commission processing abstraction from the commission calculation implementation, and using the Strategy Pattern to make the calculation strategies interchangeable at runtime.