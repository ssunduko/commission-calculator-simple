# Software Design Principles

## Overview
This directory contains examples of various software design principles that are fundamental to writing clean, maintainable, and efficient code. Each subdirectory focuses on a specific principle, providing examples of code that violates the principle and how to fix it.

## Motivation
Software design principles are guidelines that help developers create code that is:
- Easier to understand and maintain
- More flexible and adaptable to change
- Less prone to bugs and errors
- More reusable and modular
- Better organized and structured

By following these principles, developers can create software that is more robust, scalable, and sustainable over time.

## Principles in this Directory

### DRY (Don't Repeat Yourself)
**Motivation**: Code duplication leads to maintenance nightmares. When the same code exists in multiple places, changes must be made in all those places, increasing the risk of inconsistencies and bugs. DRY encourages creating a single, authoritative source for each piece of knowledge in a system.

### KISS (Keep It Simple, Stupid)
**Motivation**: Complexity is the enemy of maintainability. Simple solutions are easier to understand, debug, and extend. KISS encourages developers to avoid over-engineering and to choose the simplest solution that meets the requirements.

### OOP (Object-Oriented Programming)
**Motivation**: Object-oriented programming provides a structured approach to organizing code by modeling real-world entities as objects with properties and behaviors. OOP principles help create more modular, reusable, and maintainable code.

Includes:
- **Abstraction**: Hiding complex implementation details and exposing only necessary functionality
- **Encapsulation**: Bundling data and methods that operate on that data within a single unit
- **Inheritance**: Creating new classes that inherit properties and behaviors from existing classes
- **Polymorphism**: Allowing objects of different types to be treated as objects of a common type

### POLS (Principle of Least Surprise)
**Motivation**: Code should behave in ways that users expect. When code behaves surprisingly, it leads to bugs and confusion. POLS encourages creating interfaces and behaviors that align with users' expectations, making code more intuitive and less error-prone.

### SOC (Separation of Concerns)
**Motivation**: When different aspects of a program are mixed together, the code becomes difficult to understand and modify. SOC encourages dividing a program into distinct sections, each addressing a separate concern, making the code more modular and maintainable.

### SOLID Principles
**Motivation**: As software grows in complexity, it becomes harder to maintain without a solid foundation. The SOLID principles provide guidelines for creating more maintainable, flexible, and scalable object-oriented designs.

Includes:
- **Single Responsibility Principle (SRP)**: A class should have only one reason to change
- **Open/Closed Principle (OCP)**: Software entities should be open for extension but closed for modification
- **Liskov Substitution Principle (LSP)**: Objects of a superclass should be replaceable with objects of a subclass without affecting correctness
- **Interface Segregation Principle (ISP)**: No client should be forced to depend on methods it does not use
- **Dependency Inversion Principle (DIP)**: High-level modules should not depend on low-level modules; both should depend on abstractions

### YAGNI (You Aren't Gonna Need It)
**Motivation**: Adding features or code that might be needed in the future often leads to unnecessary complexity and wasted effort. YAGNI encourages developers to implement only what is needed now, avoiding speculative generality and focusing on current requirements.

## Directory Structure
Each principle has its own subdirectory containing:
- A README.md file explaining the principle in detail
- An "original" directory with code examples that violate the principle
- A "fixed" directory with refactored versions that adhere to the principle

## Learning Path
For those new to software design principles, we recommend studying them in the following order:
1. KISS - Start with simplicity as a foundation
2. DRY - Learn to eliminate duplication
3. OOP - Understand object-oriented fundamentals
4. SOLID - Build on OOP with more advanced principles
5. POLS - Focus on creating intuitive interfaces
6. SOC - Learn to separate different aspects of your program
7. YAGNI - Develop discipline in feature implementation