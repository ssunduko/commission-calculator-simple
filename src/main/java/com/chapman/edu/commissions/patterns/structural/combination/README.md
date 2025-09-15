# Structural Design Pattern Combinations

This directory contains examples of combined structural design patterns using the Commission Calculator domain model. Each class demonstrates how two different structural patterns can be combined to create more powerful and flexible solutions.

## Pattern Combinations

### 1. Adapter + Facade (`AdapterFacade.java`)

This combination demonstrates how to:
- Use the **Facade pattern** to provide a simplified interface to a complex subsystem (payment processing)
- Use the **Adapter pattern** internally to adapt domain models (Deal) to work with external systems (PaymentTransaction)

Benefits:
- Simplified interface to complex subsystems
- Ability to work with incompatible interfaces
- Decoupling clients from subsystem implementation details
- Easier integration with third-party systems

### 2. Composite + Decorator (`CompositeDecorator.java`)

This combination demonstrates how to:
- Use the **Composite pattern** to create a tree structure of sales components (products and deals)
- Use the **Decorator pattern** to dynamically add behaviors to these components (discounts, urgency premiums, logging)

Benefits:
- Uniform treatment of individual objects and compositions
- Dynamic addition of responsibilities to objects
- Ability to apply decorators to both individual objects and compositions
- Flexible and extensible design with single responsibility classes

### 3. Proxy + Decorator (`ProxyDecorator.java`)

This combination demonstrates how to:
- Use the **Proxy pattern** to control access to deal valuation services
- Use the **Decorator pattern** to add additional behaviors to these services (premium products, seasonal discounts)

Benefits:
- Control access to objects
- Add behaviors to objects dynamically
- Separate concerns: access control vs. additional functionality
- Flexible composition of different proxies and decorators

### 4. Bridge + Abstract Factory (`BridgeAbstractFactory.java`)

This combination demonstrates how to:
- Use the **Bridge pattern** to separate commission processors from calculation strategies
- Use the **Abstract Factory pattern** to create families of related objects (processors and strategies)

Benefits:
- Separation of abstraction from implementation
- Creation of families of related objects
- Ability to switch between different implementations at runtime
- Extensibility: new processors and strategies can be added independently

### 5. Facade + Proxy (`FacadeProxy.java`)

This combination demonstrates how to:
- Use the **Facade pattern** to provide a simplified interface to the commission system
- Use the **Proxy pattern** to control access to the facade and add additional functionality (caching, logging)

Benefits:
- Simplified interface to complex subsystems
- Control access to the facade
- Add additional functionality like caching and logging
- Separation of concerns: business logic vs. cross-cutting concerns

## Implementation Details

Each class contains:
1. A detailed explanation of how the two patterns are combined
2. Concrete implementations using the Commission Calculator domain model
3. A demonstration of how to use the combined patterns
4. A summary of the benefits of combining the patterns

## Domain Model

The implementations use classes from the `com.chapman.edu.commissions.model` package, including:
- `Deal` - Represents a sales deal
- `DealProduct` - Represents a product in a deal
- `User` - Represents a user in the system
- `CommissionPlan` - Represents a commission plan
- `CommissionCalculation` - Represents a commission calculation

## Running the Examples

Each class contains a `main` method that demonstrates how to use the combined patterns. You can run these examples to see the patterns in action.