# Factory Patterns - Commission Calculator Implementation

## Overview

This directory contains comprehensive implementations of three factory pattern variations used in the commission calculator domain:

1. **Simple Factory** - A straightforward approach using static factory methods
2. **Factory Method** - An extensible pattern using abstract creators and concrete implementations
3. **Abstract Factory** - A sophisticated pattern for creating families of related objects

All three patterns solve the same fundamental problem: **encapsulating object creation logic to promote loose coupling and improve maintainability**. However, each pattern offers different trade-offs in terms of complexity, extensibility, and use cases.

## Definition

### What is the Factory Pattern?

The Factory Pattern is a creational design pattern that provides an interface for creating objects in a superclass, but allows subclasses or methods to alter the type of objects that will be created. Instead of calling constructors directly (`new SomeClass()`), client code calls a factory method that returns instances of various classes based on parameters or configuration.

### Three Variants Defined

**Simple Factory (Static Factory Method):**
A single class with a static method that returns instances of different classes based on input parameters. Not a formal GoF pattern but widely used.

**Factory Method Pattern:**
Defines an interface for creating an object, but lets subclasses decide which class to instantiate. It lets a class defer instantiation to subclasses.

**Abstract Factory Pattern:**
Provides an interface for creating families of related or dependent objects without specifying their concrete classes.

## Why It Matters

### Business Domain Context

In commission calculation systems, object creation is complex and context-dependent:

- **Different commission types** require different calculation objects (standard, bonus, accelerated)
- **Different deal types** need different initialization logic (hardware, software, services)
- **Different commission structures** require compatible sets of objects (standard vs. premium plans with matching calculators)

Factory patterns matter because they:

1. **Reduce coupling**: Client code doesn't depend on concrete classes, only abstractions
2. **Centralize complexity**: Object creation logic is in one place, not scattered throughout the codebase
3. **Improve testability**: Factories can be mocked or stubbed for testing
4. **Enable flexibility**: New product types can be added without changing client code
5. **Ensure consistency**: Related objects are created together correctly (especially in Abstract Factory)
6. **Promote SOLID principles**: Particularly the Open/Closed Principle and Dependency Inversion Principle

### Real-World Impact

Without factory patterns, commission calculation code would be littered with:
- Complex conditional logic for object creation
- Tight coupling to concrete classes
- Duplicate initialization code
- Inconsistent object configurations
- Difficulty testing and mocking

With factory patterns, the code is:
- More maintainable and readable
- Easier to extend with new types
- More testable with dependency injection
- Less prone to configuration errors

## Use Cases

### When to Use Simple Factory

✅ **Use when:**
- You have a limited number of product types that are unlikely to change frequently
- Object creation logic is straightforward but needs centralization
- You want a quick, pragmatic solution without extensive infrastructure
- The products share a common interface but differ in configuration

❌ **Avoid when:**
- You need to frequently add new product types (violates Open/Closed Principle)
- The factory class would become very large with many product types
- You need extensibility without modifying existing code

**Example from this codebase:**
`SimpleFactory.createCommissionCalculation()` creates three types of commission calculations (standard, bonus, accelerated) based on a string parameter.

### When to Use Factory Method

✅ **Use when:**
- You have a single product type with multiple variants
- You want to extend the system with new product types without modifying existing code
- Subclasses need to customize object creation
- You're implementing a framework where creation logic should be overridden

❌ **Avoid when:**
- You only have one product type with no variants
- The creation logic is too simple to justify additional classes
- You need to create families of related objects (use Abstract Factory instead)

**Example from this codebase:**
`DealFactory` hierarchy creates different types of deals (hardware, software, service) with type-specific products and initialization.

### When to Use Abstract Factory

✅ **Use when:**
- You need to create families of related objects
- Products must be used together and compatibility is critical
- You want to enforce constraints across multiple product types
- Your system should be independent of how products are created and composed

❌ **Avoid when:**
- You only need to create single objects, not families
- The added complexity isn't justified by the need for product family consistency
- Products don't have strong interdependencies

**Example from this codebase:**
`CommissionFactory` creates families of commission objects (plan creator + calculator) where standard and premium families must not be mixed.

## Pattern Overview

### Simple Factory Structure

```
Client --> Factory : requests product
Factory --> ConcreteProductA : creates
Factory --> ConcreteProductB : creates
Factory --> ConcreteProductC : creates
```

**Key characteristic**: One factory class with conditional logic to create different product types.

### Factory Method Structure

```
Client --> AbstractCreator
AbstractCreator <|-- ConcreteCreatorA : implements
AbstractCreator <|-- ConcreteCreatorB : implements
ConcreteCreatorA --> ConcreteProductA : creates
ConcreteCreatorB --> ConcreteProductB : creates
```

**Key characteristic**: Abstract creator with factory method; subclasses implement creation logic.

### Abstract Factory Structure

```
Client --> AbstractFactory
AbstractFactory <|-- ConcreteFactory1
AbstractFactory <|-- ConcreteFactory2
ConcreteFactory1 --> ProductA1 : creates
ConcreteFactory1 --> ProductB1 : creates
ConcreteFactory2 --> ProductA2 : creates
ConcreteFactory2 --> ProductB2 : creates
```

**Key characteristic**: Multiple related products created by each concrete factory, ensuring family consistency.

## Pattern Intent

### Simple Factory Intent
**"Centralize object creation logic in a single class to hide instantiation details from client code."**

This pattern aims to:
- Provide a single point of control for object creation
- Give meaningful names to creation operations
- Reduce code duplication in object instantiation
- Decouple clients from concrete class names

### Factory Method Intent
**"Define an interface for creating an object, but let subclasses decide which class to instantiate."**

This pattern aims to:
- Defer instantiation to subclasses
- Support the Open/Closed Principle (open for extension, closed for modification)
- Enable framework design where creation hooks are provided
- Allow parallel class hierarchies (creators and products)

### Abstract Factory Intent
**"Provide an interface for creating families of related or dependent objects without specifying their concrete classes."**

This pattern aims to:
- Create entire product families with guaranteed compatibility
- Isolate concrete classes from client code
- Make exchanging product families easy
- Enforce consistency among products designed to work together

## Key Benefits

### Benefits Across All Factory Patterns

1. **Encapsulation of Creation Logic**
   - Object creation details are hidden from client code
   - Complex initialization is centralized and reusable

2. **Reduced Coupling**
   - Clients depend on interfaces/abstractions, not concrete classes
   - Easier to change implementations without affecting clients

3. **Improved Testability**
   - Factories can be mocked or stubbed
   - Test doubles can be injected easily

4. **Meaningful Naming**
   - Factory methods have descriptive names (`createHardwareDeal()` vs. `new Deal()`)
   - Intent is clearer from method names

5. **Consistency**
   - Object initialization follows consistent patterns
   - Reduces errors from manual construction

### Simple Factory Specific Benefits

- **Simplicity**: Easiest to understand and implement
- **Centralization**: All creation logic in one place
- **Quick Implementation**: Minimal infrastructure required

### Factory Method Specific Benefits

- **Extensibility**: New product types via new subclasses (Open/Closed Principle)
- **Template Method Integration**: Can combine with template methods for standardized workflows
- **Framework Design**: Allows frameworks to define hooks for client customization

### Abstract Factory Specific Benefits

- **Product Family Consistency**: Guarantees related products work together
- **Easy Family Swapping**: Change entire product families by changing factory
- **Product Isolation**: Concrete product classes are isolated from client code
- **Constraint Enforcement**: Ensures incompatible products aren't mixed

## Files in This Directory

### Implementation Files

#### Simple Factory
- **`simplefactory/SimpleFactory.java`**
  - Static factory method for creating commission calculations
  - Creates three types: standard, bonus, accelerated
  - Demonstrates centralized creation with conditional logic

- **`simplefactory/SimpleFactoryUsage.java`**
  - Example client code showing how to use the simple factory
  - Demonstrates the simplicity of client interaction

#### Factory Method
- **`factorymethods/FactoryMethodStructure.java`**
  - Abstract structure showing creator hierarchy
  - Demonstrates pure Factory Method pattern structure

- **`factorymethods/FactoryMethodImplementation.java`**
  - Complete implementation with Deal creation
  - Creates hardware, software, and service deals
  - Integrates Template Method pattern
  - Each factory adds type-specific products

- **`factorymethods/FactoryMethodUsage.java`**
  - Example usage showing extensibility and polymorphism

- **`factorymethods/FactoryMethodClasses.java`**
  - Supporting classes for factory method demonstration

#### Abstract Factory
- **`abstractfactory/AbstractInterfaces.java`**
  - Defines abstract factory and product interfaces
  - Shows the complete abstraction layer

- **`abstractfactory/AbstractFactoryStructure.java`**
  - Generic structure of Abstract Factory pattern
  - Educational example showing pattern mechanics

- **`abstractfactory/AbstractFactoryImplementation.java`**
  - Complete commission system implementation
  - Creates standard and premium commission families
  - Each family includes plan creator + calculator
  - Demonstrates product family consistency

- **`abstractfactory/ConcreteImplementation.java`**
  - Concrete implementations of factory products
  - Shows how related products work together

- **`abstractfactory/AbstractFactoryUsage.java`**
  - Client code demonstrating factory usage
  - Shows how families ensure compatibility

- **`abstractfactory/AbstractFactoryCallingCode.java`**
  - Additional usage examples and scenarios

### Documentation Files

- **`README.md`** (this file)
  - Comprehensive guide to factory patterns
  - Comparison, use cases, and best practices

- **`QUESTIONS.md`**
  - Educational questions about factory patterns
  - Helps reinforce learning

- **`ANSWERS.md`**
  - Answers to educational questions
  - Provides deeper insights

### Diagram Files

- **`factory-pattern.puml`**
  - Complete UML diagram showing all three factory pattern variants
  - Includes Simple Factory, Factory Method, and Abstract Factory
  - Annotated with detailed notes explaining each component
  - Shows relationships and interactions
  - Based on actual implementation in this codebase

- **`basic-factory.puml`**
  - Generic factory pattern structure diagram
  - Educational diagram showing core factory pattern concepts
  - Useful for understanding factory pattern fundamentals
  - Language and framework agnostic

- **`simplefactory/SimpleFactory.puml`**
  - Diagram specific to Simple Factory implementation

- **`factorymethods/FactoryMethod.puml`**
  - Diagram showing Factory Method pattern structure

- **`abstractfactory/AbstractFactory.puml`**
  - Diagram illustrating Abstract Factory pattern

## Pattern Participants

### Simple Factory Participants

1. **Factory (SimpleFactory)**
   - Declares static creation method
   - Contains conditional logic to determine product type
   - Instantiates and returns concrete products
   - Example: `SimpleFactory.createCommissionCalculation()`

2. **Product (CommissionCalculation)**
   - Defines interface for objects the factory creates
   - All products share this common interface
   - Example: `CommissionCalculation` class

3. **Client**
   - Calls factory method to obtain products
   - Works with products through their interface
   - Unaware of concrete product classes

### Factory Method Participants

1. **Creator (DealFactory)**
   - Declares abstract factory method
   - May provide default implementation
   - Can include template methods that use the factory method
   - Example: `DealFactory` with `createDeal()` factory method

2. **ConcreteCreator (HardwareDealFactory, SoftwareDealFactory, ServiceDealFactory)**
   - Implements factory method to create specific products
   - Overrides hook methods for customization
   - Each creates a specific product variant
   - Example: `HardwareDealFactory.createDeal()`

3. **Product (Deal)**
   - Defines interface for objects the factory method creates
   - Example: `Deal` class

4. **ConcreteProduct (Deal instances with type-specific configuration)**
   - Specific implementation created by concrete creator
   - Example: Deal with "HW-" prefix and hardware products

### Abstract Factory Participants

1. **AbstractFactory (CommissionFactory)**
   - Declares interface for creating abstract product families
   - Contains multiple factory methods, one per product type
   - Example: `CommissionFactory` interface with `createPlanCreator()` and `createCalculator()`

2. **ConcreteFactory (StandardCommissionFactory, PremiumCommissionFactory)**
   - Implements creation methods for product families
   - Each factory creates a complete, compatible product family
   - Example: `StandardCommissionFactory` creates standard plan creator + standard calculator

3. **AbstractProduct (CommissionPlanCreator, CommissionCalculator)**
   - Declares interface for a type of product
   - Example: `CommissionPlanCreator` interface

4. **ConcreteProduct (StandardCommissionPlanCreator, PremiumCommissionPlanCreator, etc.)**
   - Implements abstract product interface
   - Created by corresponding concrete factory
   - Designed to work with other products from the same family
   - Example: `StandardCommissionPlanCreator` creates plans with 5% rate

5. **Client**
   - Uses abstract factory to obtain product families
   - Works only with interfaces (abstract factory and products)
   - Can switch product families by changing concrete factory
   - Example: Code that uses `CommissionFactory` without knowing if it's standard or premium

## Common Pitfalls to Avoid

### Simple Factory Pitfalls

1. **❌ Violating Open/Closed Principle**
   - **Problem**: Adding new product types requires modifying the factory class
   - **Impact**: Increases risk of bugs, harder to maintain
   - **Solution**: Consider Factory Method if types change frequently

2. **❌ Overly Complex Conditional Logic**
   - **Problem**: Large switch/if statements become hard to read and maintain
   - **Impact**: Error-prone, difficult to test all branches
   - **Solution**: Limit the number of product types or use Factory Method

3. **❌ Mixing Creation with Business Logic**
   - **Problem**: Factory contains business logic beyond object creation
   - **Impact**: Violates Single Responsibility Principle
   - **Solution**: Keep factories focused on creation only

### Factory Method Pitfalls

1. **❌ Over-Engineering Simple Cases**
   - **Problem**: Using Factory Method for simple object creation
   - **Impact**: Unnecessary complexity, more classes to maintain
   - **Solution**: Use Simple Factory for straightforward scenarios

2. **❌ Forgetting Template Methods**
   - **Problem**: Not leveraging template method integration
   - **Impact**: Misses opportunity for consistent initialization
   - **Solution**: Use template methods in creator for common setup steps

3. **❌ Creating Too Many Factory Hierarchies**
   - **Problem**: Factory hierarchy for every minor variation
   - **Impact**: Class explosion, hard to navigate
   - **Solution**: Consider configuration over subclassing

4. **❌ Unclear Responsibility Division**
   - **Problem**: Confusion about what goes in creator vs. product
   - **Impact**: Inconsistent design, hard to extend
   - **Solution**: Creator handles creation, product handles behavior

### Abstract Factory Pitfalls

1. **❌ Using When Not Needed**
   - **Problem**: Implementing Abstract Factory for single products
   - **Impact**: Excessive complexity without benefit
   - **Solution**: Use Factory Method if you're not creating product families

2. **❌ Incomplete Product Families**
   - **Problem**: Not all concrete factories implement all product creation methods
   - **Impact**: Runtime errors, broken abstractions
   - **Solution**: Ensure every factory creates all products in the family

3. **❌ Mixing Products from Different Families**
   - **Problem**: Manually creating products from different factories
   - **Impact**: Defeats the purpose of family consistency
   - **Solution**: Always use factory methods, never instantiate products directly

4. **❌ Not Planning for Extension**
   - **Problem**: Hard-coding factory selection logic
   - **Impact**: Difficult to add new product families
   - **Solution**: Use configuration, dependency injection, or registry patterns

5. **❌ Forgetting About Factory Selection**
   - **Problem**: No clear strategy for which factory to use
   - **Impact**: Client code becomes complex with factory selection logic
   - **Solution**: Use strategy pattern, configuration, or environment-based selection

### General Factory Pattern Pitfalls

1. **❌ Exposing Concrete Classes**
   - **Problem**: Returning concrete types instead of interfaces
   - **Impact**: Defeats the purpose of the pattern
   - **Solution**: Always return abstract types/interfaces

2. **❌ Not Using Dependency Injection**
   - **Problem**: Hard-coding factory dependencies
   - **Impact**: Tight coupling, hard to test
   - **Solution**: Inject factories as dependencies

3. **❌ Overuse of Static Factories**
   - **Problem**: Making all factories static
   - **Impact**: Hard to mock, test, or replace
   - **Solution**: Use instance methods when testing and flexibility matter

4. **❌ Poor Naming**
   - **Problem**: Generic names like `createObject()`
   - **Impact**: Intent is unclear
   - **Solution**: Use descriptive names: `createHardwareDeal()`, `createPremiumCommissionPlan()`

5. **❌ Ignoring Error Handling**
   - **Problem**: No validation of factory parameters
   - **Impact**: Runtime errors, unclear failure modes
   - **Solution**: Validate inputs, provide clear error messages

## How It Works

### Simple Factory Flow

1. **Client Requests Product**
   ```java
   CommissionCalculation calc = SimpleFactory.createCommissionCalculation(
       "bonus", "DEAL-123", "REP-456", new BigDecimal("1000")
   );
   ```

2. **Factory Examines Type**
   ```java
   switch (type.toLowerCase()) {
       case "standard": /* create standard */
       case "bonus": /* create with bonus */
       case "accelerated": /* create accelerated */
   }
   ```

3. **Factory Creates and Configures Product**
   ```java
   CommissionCalculation calculation = new CommissionCalculation(...);
   calculation.setId("COMMISSION-" + type.toUpperCase());
   calculation.setCalculatedBy("SimpleFactory-" + type);
   // Type-specific configuration
   ```

4. **Factory Returns Product**
   - Returns product as interface/common type
   - Client uses product without knowing concrete class

### Factory Method Flow

1. **Client Holds Creator Reference**
   ```java
   DealFactory factory = new HardwareDealFactory();
   ```

2. **Client Calls Template Method**
   ```java
   Deal deal = factory.createDealWithProducts("Big Sale", value, salesRepId);
   ```

3. **Template Method Calls Factory Method**
   ```java
   public Deal createDealWithProducts(...) {
       Deal deal = createDeal(...);  // Factory method (overridden by subclass)
       deal.setCreatedDate(LocalDate.now());
       addDefaultProducts(deal);  // Hook method (overridden by subclass)
       return deal;
   }
   ```

4. **Concrete Creator Creates Product**
   ```java
   protected Deal createDeal(...) {
       Deal deal = new Deal(...);
       deal.setId("HW-" + System.currentTimeMillis());  // HW-specific
       deal.setStatus(DealStatus.OPEN);
       return deal;
   }
   ```

5. **Concrete Creator Customizes Product**
   ```java
   protected void addDefaultProducts(Deal deal) {
       // Add hardware-specific products
       deal.addProduct(laptop);
       deal.addProduct(monitor);
   }
   ```

### Abstract Factory Flow

1. **Client Obtains Factory**
   ```java
   CommissionFactory factory = new PremiumCommissionFactory();
   ```

2. **Client Creates Product Family**
   ```java
   CommissionPlanCreator planCreator = factory.createPlanCreator();
   CommissionCalculator calculator = factory.createCalculator();
   ```

3. **Client Uses Products Together**
   ```java
   CommissionPlan plan = planCreator.createCommissionPlan("Premium Plan", "8% rate");
   CommissionCalculation calc = calculator.calculateCommission(deal, salesRep, plan);
   ```

4. **Products Work Together Consistently**
   - `PremiumCommissionPlanCreator` creates plan with 8% rate
   - `PremiumCommissionCalculator` applies premium algorithm with 10% bonus
   - Both designed to work together as a family

5. **Easy to Switch Families**
   ```java
   // Simply change factory instance
   CommissionFactory factory = new StandardCommissionFactory();
   // All subsequent products will be from standard family
   ```

### Pattern Collaboration

The three factory patterns can work together:

```java
// Abstract Factory creates factory families
CommissionFactory systemFactory = factoryProvider.getFactory(tier);

// Factory Method within each product creator
CommissionPlanCreator planCreator = systemFactory.createPlanCreator();
CommissionPlan plan = planCreator.createCommissionPlan(...);  // Factory method

// Simple Factory for quick utilities
CommissionCalculation quickCalc = SimpleFactory.createCommissionCalculation(...);
```

## Conclusion

Factory patterns are essential tools for managing object creation complexity in software systems. This implementation demonstrates three progressive levels of sophistication:

### Pattern Selection Summary

- **Choose Simple Factory when**: You need quick, centralized object creation with limited types
- **Choose Factory Method when**: You need extensibility for single product types with multiple variants
- **Choose Abstract Factory when**: You need to create families of related objects with guaranteed compatibility

### Key Takeaways

1. **All factory patterns reduce coupling** between client code and concrete classes
2. **Factory Method and Abstract Factory support extensibility** better than Simple Factory
3. **Abstract Factory ensures consistency** among related objects
4. **Choose the simplest pattern** that meets your needs—don't over-engineer
5. **Factory patterns work well with other patterns** (Strategy, Dependency Injection, etc.)

### Learning Path

1. **Start with Simple Factory** to understand the core concept
2. **Progress to Factory Method** when you need Open/Closed Principle compliance
3. **Graduate to Abstract Factory** when you have product families to manage

### Practical Application

In the commission calculator domain, factory patterns:
- Simplify creation of commission calculations with different configurations
- Enable extension with new deal types without modifying existing code
- Ensure commission plans and calculators are compatible
- Make testing easier through dependency injection
- Improve code maintainability and readability

By mastering these patterns, you'll be equipped to design flexible, maintainable object creation systems in any domain.

---

**For Questions and Further Learning**: See `QUESTIONS.md` and `ANSWERS.md` in this directory.

**For Visual Reference**: See PlantUML diagrams:
- `factory-pattern.puml` - Complete implementation diagram
- `basic-factory.puml` - Core pattern structure diagram
