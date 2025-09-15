# Adapter Pattern

## Overview
The Adapter Pattern is a structural design pattern that allows objects with incompatible interfaces to collaborate. It acts as a bridge between two incompatible interfaces by wrapping an instance of one class into an adapter class that presents the expected interface.

In this implementation, we demonstrate how to use the Adapter Pattern to make our existing `Deal` and `DealProduct` classes work with different client interfaces without modifying the original classes.

## Components

### 1. Target
The interface that the client expects to work with. In our examples:
- `ReportData` - Interface for generating reports
- `PaymentTransaction` - Interface for processing payments

### 2. Adaptee
The existing class with an incompatible interface. In our examples:
- `Deal` - Our existing model class that needs to be adapted

### 3. Adapter
The class that implements the Target interface and translates calls to the Adaptee. In our examples:
- `DealReportAdapter` - Adapts a Deal to the ReportData interface
- `DealPaymentAdapter` - Adapts a Deal to the PaymentTransaction interface

### 4. Client
The class that interacts with the Target interface. In our examples:
- `ReportGenerator` - Works with the ReportData interface
- `PaymentProcessor` - Works with the PaymentTransaction interface

## Implementation Details

### Structure (AdapterPatternStructure.java)
This file demonstrates the basic structure of the Adapter Pattern, showing how a `Deal` can be adapted to a `ReportData` interface for use with a reporting system.

### Implementation (AdapterPatternImplementation.java)
This file provides a more concrete implementation, showing how a `Deal` can be adapted to a `PaymentTransaction` interface for use with a payment processing system.

### Usage (AdapterPatternUsage.java)
This file demonstrates how to use the adapters in practice, creating a sample `Deal` and using it with both the reporting and payment processing systems.

## Benefits of the Adapter Pattern

1. **Reusability**: Allows reuse of existing classes that lack compatible interfaces.
2. **Flexibility**: Enables classes with incompatible interfaces to work together.
3. **Separation of Concerns**: Keeps the adaptation logic separate from the business logic.
4. **Open/Closed Principle**: Allows adding new adapters without modifying existing code.
5. **Single Responsibility Principle**: Each adapter has a single responsibility - to adapt one interface to another.

## When to Use the Adapter Pattern

- When you want to use an existing class, but its interface doesn't match what you need
- When you want to create a reusable class that cooperates with classes that don't necessarily have compatible interfaces
- When you need to use several existing subclasses but it's impractical to adapt their interface by subclassing each one

## Real-World Analogy

Think of a power adapter that allows you to plug a device with a different plug type into a wall socket. The adapter doesn't change the functionality of the device or the socket, but it allows them to work together despite their incompatible interfaces.

## UML Diagram

A UML diagram of the implementation can be found in the `adapter_pattern.puml` file, which visualizes the relationships between the classes and interfaces in our Adapter Pattern implementation.