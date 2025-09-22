# Autonomy Examples

This directory contains examples demonstrating different types of autonomy in software design, specifically focusing on:

1. **Data Autonomy**: A component's ability to own and manage its data storage
2. **Functional Autonomy**: A component's ability to provide a complete business capability without external dependencies

## Overview

The examples in this directory use the model classes from `com.chapman.edu.commissions.model` to demonstrate how autonomy can be implemented in a commission calculation system. The examples are implemented in plain Java without any external dependencies.

## Data Autonomy Example

The `DataAutonomyExample` class demonstrates data autonomy by:

- Encapsulating all data storage and management for deals and users within the component itself
- Providing well-defined interfaces for data access and manipulation
- Hiding internal data structures from external components
- Taking responsibility for data validation and integrity

Key characteristics of data autonomy shown in this example:

1. **Private data stores**: The component owns its data storage mechanism (HashMap implementations)
2. **Controlled access**: Data access is controlled through well-defined public methods
3. **Encapsulation**: Internal data structures are hidden from external components
4. **Data validation**: The component validates data before storing it

## Functional Autonomy Example

The `FunctionalAutonomyExample` class demonstrates functional autonomy by:

- Implementing a complete commission calculation system that can operate independently
- Encapsulating all the logic needed for its business function
- Providing well-defined interfaces for interaction with other components
- Managing its own data and business rules

Key characteristics of functional autonomy shown in this example:

1. **Complete business capability**: The component provides end-to-end commission calculation functionality
2. **Independent operation**: It can operate without external dependencies
3. **Encapsulated logic**: All business logic is contained within the component
4. **Well-defined interfaces**: It has clear interfaces for interaction

## UML Diagram

The `autonomy_diagram.puml` file contains a PlantUML diagram showing the classes and their relationships. The diagram:

- Shows the classes in the autonomy directory
- Shows their relationships with the model classes
- Does not show the package structure
- Includes notes explaining the pattern components

## Additional Files

- **QUESTIONS.md**: Contains questions about the autonomy concepts presented
- **ANSWERS.md**: Provides answers to the questions in QUESTIONS.md

## Running the Examples

Both examples include a `main` method that demonstrates their usage. You can run them directly to see the examples in action.

## Key Takeaways

1. **Data Autonomy** allows components to manage their own data, reducing dependencies and increasing encapsulation.
2. **Functional Autonomy** allows components to provide complete business capabilities, making them more reusable and maintainable.
3. Both types of autonomy contribute to creating more modular, maintainable, and testable code.