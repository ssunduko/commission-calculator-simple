# Singleton Pattern Implementations

This directory contains various implementations of the Singleton design pattern based on the `CommissionCalculation` class.

## Overview

The Singleton pattern ensures a class has only one instance and provides a global point of access to it. This is useful when exactly one object is needed to coordinate actions across the system.

## Implementations

Six different implementations of the Singleton pattern are provided:

1. **Basic Singleton** (`BasicSingleton.java`)
   - The simplest form using a private constructor and a static method
   - Not thread-safe

2. **Thread-Safe Singleton - Synchronized Method** (`ThreadSafeSingletonSynchronizedMethod.java`)
   - Thread-safe implementation using a synchronized getInstance() method
   - Performance overhead due to synchronization on every call

3. **Thread-Safe Singleton - Double-Checked Locking** (`ThreadSafeSingletonDoubleCheckedLocking.java`)
   - Thread-safe implementation that minimizes synchronization overhead
   - Uses volatile keyword and synchronizes only during initialization

4. **Thread-Safe Singleton - Eager Initialization** (`ThreadSafeSingletonEagerInitialization.java`)
   - Thread-safe implementation that initializes the instance at class loading time
   - No synchronization needed but instance is created even if never used

5. **Thread-Safe Singleton - Initialization-on-Demand Holder** (`ThreadSafeSingletonInitializationOnDemand.java`)
   - Thread-safe implementation using a static inner class
   - Combines lazy initialization with thread safety without synchronization

6. **Enum Singleton** (`EnumSingleton.java`)
   - Implementation using Java's enum type
   - Considered the simplest and most effective way in Java
   - Provides serialization and thread safety by default

## Demo

The `SingletonDemo.java` class demonstrates how to use each of these implementations. For each implementation, it:

1. Gets the singleton instance
2. Retrieves the commission calculation from the singleton
3. Calculates a new commission
4. Verifies that we get the same instance when calling getInstance() again

## Advantages and Disadvantages

Each implementation has its own advantages and disadvantages, which are documented as inline comments in the respective files. Some common considerations:

### Advantages
- Controlled access to sole instance
- Reduced namespace pollution
- Permits refinement of operations and representation
- Can be extended through subclassing
- More flexible than class operations

### Disadvantages
- Singleton Thread Safety Problem (for non-thread-safe implementations)
- Violates Single Responsibility Principle
- Difficult to unit test (global state)
- Hidden dependencies
- Tight coupling
- Problems with inheritance
- Serialization issues

## Best Practices

When using the Singleton pattern, consider the following best practices:

- Use only when truly needed (one instance requirement)
- Consider thread safety requirements
- Make the class final to prevent inheritance issues
- Implement Serializable carefully
- Consider using dependency injection frameworks
- Document the singleton nature clearly

## Usage

To run the demo:

```java
public static void main(String[] args) {
    // Get the singleton instance
    BasicSingleton singleton = BasicSingleton.getInstance();
    
    // Get the commission calculation
    CommissionCalculation calculation = singleton.getCommissionCalculation();
    
    // Calculate a commission
    CommissionCalculation newCalculation = singleton.calculateCommission(
            "DEAL-001", "SALES-001", new BigDecimal("1000.00"));
    
    // Verify that we get the same instance
    BasicSingleton anotherReference = BasicSingleton.getInstance();
    System.out.println("Same instance: " + (singleton == anotherReference));
}
```