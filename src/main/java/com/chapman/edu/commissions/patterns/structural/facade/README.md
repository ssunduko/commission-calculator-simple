# Facade Pattern Implementation

## Overview
This directory contains an implementation of the Facade Pattern using the Commission Calculator domain model. The Facade Pattern is a structural design pattern that provides a simplified interface to a complex subsystem of classes, making it easier to use.

## What is the Facade Pattern?
The Facade Pattern defines a higher-level interface that makes a subsystem easier to use by reducing complexity and hiding the implementation details. It acts as a "facade" or front-facing interface that shields clients from complex subsystem components.

## Key Components
1. **Facade**: Provides a simplified interface to a complex subsystem
2. **Subsystem Classes**: The complex classes that the facade simplifies
3. **Client**: Uses the facade instead of working directly with the subsystem

## Implementation Details
In this implementation, we've created:

### 1. FacadePatternStructure.java
This class demonstrates the structure of the Facade Pattern, showing the key components and their relationships. It includes:
- A `CommissionSystemFacade` class that provides a simplified interface to the complex subsystem
- Several subsystem components (`DealManager`, `UserManager`, `CommissionCalculator`)
- A `Client` class that uses the facade

### 2. FacadePatternImplementation.java
This class provides a concrete implementation of the Facade Pattern using the Commission Calculator domain model. It includes:
- A `CommissionFacade` class that provides methods for creating deals, closing deals, and calculating commissions
- Several subsystem components (`DealService`, `UserService`, `CommissionService`, `ReportService`)
- Implementation details for each component

### 3. FacadePatternUsage.java
This class demonstrates how to use the Facade Pattern implementation. It includes:
- Examples of creating deals
- Examples of closing deals as won
- Examples of getting deals by sales rep
- Examples of calculating total commission

### 4. facade_pattern.puml
This file contains a UML diagram of the Facade Pattern implementation, showing the relationships between the classes and the components of the pattern.

## Benefits of the Facade Pattern
1. **Simplifies Interface**: Provides a simple interface to a complex subsystem
2. **Decouples Subsystems**: Decouples the client from the subsystem, allowing the subsystem to change without affecting the client
3. **Promotes Loose Coupling**: Reduces dependencies between clients and the implementation classes
4. **Layering**: Helps in layering subsystems and providing entry points to each layer

## When to Use the Facade Pattern
- When you need to provide a simple interface to a complex subsystem
- When there are many dependencies between clients and the implementation classes
- When you want to layer your subsystems and use a facade as an entry point to each layer
- When you want to decouple your client code from the subsystem

## Real-World Analogy
Think of a restaurant: as a customer (client), you interact with the waiter (facade) who takes your order and brings your food. You don't need to interact directly with the chef, the kitchen staff, or the suppliers (subsystem components). The waiter provides a simplified interface to the complex restaurant system.