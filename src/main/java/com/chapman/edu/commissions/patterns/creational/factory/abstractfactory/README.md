# Abstract Factory Pattern

## What Was Done

This directory demonstrates the **Abstract Factory Pattern** implementation in the context of a commission calculation system. The implementation has been consolidated into three well-organized Java files:

### Implementation Files

1. **AbstractFactoryStructure.java** - Contains ONLY abstract elements (pattern structure):
   - Abstract Factory interface: `CommissionSystemFactory`
   - Abstract Product interfaces: `CommissionCalculator`, `DealValidator`, `CommissionPlanCreator`
   - Comprehensive documentation explaining the pattern's intent, benefits, drawbacks, and use cases
   - Design principle annotations (Dependency Inversion, Interface Segregation, Open/Closed)

2. **AbstractFactoryImplementation.java** - Contains ONLY concrete implementations:
   - **Standard Family** (Entry-Level):
     - `StandardCommissionPlanCreator` - Creates plans with 5% commission rate
     - `StandardCommissionCalculator` - Calculates commission using plan rate directly
     - `BasicDealValidator` - Validates deal value > 0
     - `StandardCommissionSystemFactory` - Creates all Standard family products
   - **Premium Family** (High-Performance):
     - `PremiumCommissionPlanCreator` - Creates plans with 8% commission rate
     - `PremiumCommissionCalculator` - Calculates with plan rate + 10% bonus (effective 8.8%)
     - `AdvancedDealValidator` - Validates value > 0 AND has products
     - `PremiumCommissionSystemFactory` - Creates all Premium family products
   - Detailed comments explaining business logic, calculation algorithms, and family consistency

3. **AbstractFactoryUsage.java** - Contains comprehensive usage demonstration with main method:
   - Creates sample deals and sales representatives
   - Demonstrates Standard family workflow (creates plan, validates, calculates commission)
   - Demonstrates Premium family workflow with same client code
   - Shows runtime factory selection based on business logic
   - Compares results: $500 (Standard) vs $880 (Premium) for same $10,000 deal
   - Includes alternative patterns (dependency injection examples)
   - Full executable demonstration showing pattern benefits

### Visual Documentation

**PlantUML Diagrams** - Visual representations showing:
- `abstract-factory.puml` - Complete pattern structure with all relationships
- `basic-abstract-factory.puml` - Simplified structure for learning
- Abstract product interfaces (CommissionCalculator, DealValidator, CommissionPlanCreator)
- Concrete product implementations (Standard and Premium families)
- Abstract factory interface (CommissionSystemFactory)
- Concrete factory implementations with annotations

---

## Pattern Overview

The **Abstract Factory Pattern** is a creational design pattern that provides an interface for creating families of related or dependent objects without specifying their concrete classes. It acts as a "factory of factories" where each factory is responsible for creating a complete family of related products.

**Core Intent:** Ensure that created objects from the same factory are designed to work together and maintain consistency within their product family.

**Key Principle:** Client code depends on abstract interfaces (both for factories and products), never on concrete implementations, enabling seamless switching between entire product families.

---

## Business Domain Application

In the commission calculator domain, the Abstract Factory Pattern is applied to create consistent commission calculation systems with different tiers:

### Product Families

1. **Standard Commission System (Standard Family)**
   - **BasicDealValidator:** Validates that deal value > 0
   - **StandardCommissionCalculator:** Applies 5% commission rate
   - **Use Case:** Small businesses or entry-level sales representatives
   - **Consistency:** Simple validation paired with standard commission rates

2. **Premium Commission System (Premium Family)**
   - **AdvancedDealValidator:** Validates deal value > 0 AND has at least one product
   - **PremiumCommissionCalculator:** Applies 8% commission rate
   - **Use Case:** Enterprise clients or high-performing sales teams
   - **Consistency:** Strict validation paired with higher commission rates

### Real-World Scenario

A sales organization might have different commission tiers:
- **Junior sales reps** use the Standard system with basic validation and standard rates
- **Senior sales reps** use the Premium system with advanced validation and premium rates

When processing a batch of deals, the system selects the appropriate factory based on the sales rep's tier, ensuring all components (validator, calculator) are consistent with that tier.

---

## Structure

### Participants

1. **AbstractFactory (CommissionSystemFactory)**
   - Declares creation methods for each abstract product type
   - Defines the contract that concrete factories must fulfill
   - Methods: `createCommissionCalculator()`, `createDealValidator()`

2. **ConcreteFactory (StandardCommissionSystemFactory, PremiumCommissionSystemFactory)**
   - Implements the abstract factory interface
   - Creates and returns concrete product instances
   - Ensures products from the same family work together
   - Each factory represents one complete product family

3. **AbstractProduct (CommissionCalculator, DealValidator)**
   - Declares interfaces for product types
   - Defines operations that all products of this type must support
   - Client code works with these interfaces

4. **ConcreteProduct (StandardCommissionCalculator, BasicDealValidator, etc.)**
   - Implements the abstract product interface
   - Provides specific behavior for the product
   - Belongs to a specific product family
   - Designed to be compatible with other products from the same family

5. **Client** (Not shown in diagram, but exists in usage code)
   - Uses only interfaces declared by AbstractFactory and AbstractProduct
   - Works with any concrete factory/product through abstract interfaces
   - Receives a factory instance (typically via dependency injection)

### Class Relationships

- **Inheritance:** Concrete factories implement AbstractFactory; concrete products implement AbstractProduct
- **Creation:** Each concrete factory creates specific concrete products
- **Dependency:** Client depends only on abstract interfaces
- **Association:** Factories create products; clients use both factories and products through interfaces

---

## Use Cases

### When to Use

1. **Product Family Consistency Required**
   - System must use related objects together (e.g., validator must match calculator tier)
   - Products from different families should not be mixed
   - Example: Standard validator with premium calculator would be inconsistent

2. **Multiple Product Variants**
   - System needs to support multiple families of products
   - Each family represents a configuration or tier
   - Example: Standard tier, Premium tier, Enterprise tier

3. **Platform or Configuration Independence**
   - System should be configurable with different product families
   - Switching families should be seamless
   - Example: Switching from Standard to Premium tier for a sales rep

4. **Hiding Implementation Details**
   - Client should work with interfaces, not concrete implementations
   - Product creation details should be encapsulated
   - Library provides products but hides how they're constructed

5. **Runtime Family Selection**
   - Product family is determined at runtime
   - Factory selection based on configuration, user type, or business rules
   - Example: Factory chosen based on sales rep's performance tier

### When NOT to Use

1. **Single Product Family**
   - Only one variant of products exists
   - No need for family consistency
   - **Alternative:** Use Simple Factory or Factory Method

2. **Frequent New Product Types**
   - New product types added often to the factory
   - Every addition requires changing all factory interfaces and implementations
   - **Alternative:** Consider Builder or Prototype patterns

3. **Products Don't Need to Work Together**
   - Products are independent and don't need consistency
   - No relationship between different product types
   - **Alternative:** Use separate Factory Methods

4. **Simple Object Creation**
   - Object creation is straightforward
   - No complex initialization or configuration needed
   - **Alternative:** Use constructor or Simple Factory

5. **Small-Scale Applications**
   - Application is small with limited scope
   - Pattern overhead outweighs benefits
   - **Alternative:** Direct instantiation or Simple Factory

---

## Benefits

1. **Product Consistency**
   - Ensures products from the same family work together correctly
   - Prevents mixing incompatible products (e.g., basic validator with premium calculator)
   - Factory enforces that related products are created together

2. **Isolation of Concrete Classes**
   - Client code never instantiates concrete classes directly
   - Works only with abstract interfaces
   - Reduces coupling between client and implementation

3. **Easy Family Switching**
   - Change entire product family by swapping factory instance
   - No changes needed to client code
   - Example: Switch from Standard to Premium by changing factory

4. **Single Responsibility Principle**
   - Product creation logic centralized in factory
   - Each factory responsible for one product family
   - Separates "what to create" from "how to create"

5. **Open/Closed Principle**
   - New product families can be added without modifying existing code
   - Create new concrete factory and products
   - Existing factories and client code remain unchanged

6. **Dependency Inversion Principle**
   - High-level client code depends on abstract interfaces
   - Low-level concrete implementations depend on same abstractions
   - Reduces coupling and increases flexibility

---

## Drawbacks

1. **Increased Complexity**
   - Introduces many interfaces and classes
   - Can be overkill for simple scenarios
   - More code to maintain and understand

2. **Rigid Product Structure**
   - Adding new product types requires changing all factories
   - If you add a new product (e.g., ReportGenerator), must update:
     - AbstractFactory interface
     - All concrete factory implementations
   - Can be time-consuming in large codebases

3. **Commitment to Interface**
   - All factories must implement all product creation methods
   - Even if a factory doesn't need all products
   - Can lead to empty or exception-throwing implementations

4. **Initial Design Overhead**
   - Requires upfront design and planning
   - Must identify product families and their relationships
   - May be premature for evolving requirements

5. **Potential for Over-Engineering**
   - Can add unnecessary abstraction layers
   - May reduce code readability
   - Performance overhead from additional indirection

---

## Key Benefits (Summary)

- **Family Consistency:** Products from the same factory are guaranteed to work together
- **Loose Coupling:** Client code depends on interfaces, not concrete classes
- **Flexibility:** Easy to switch between product families
- **Encapsulation:** Object creation logic is centralized and hidden
- **SOLID Compliance:** Follows Single Responsibility, Open/Closed, and Dependency Inversion principles

---

## Key Takeaways

1. **"Factory of Factories"**
   - Abstract Factory creates families of related objects
   - Each concrete factory is responsible for one complete family
   - Client receives products that are designed to work together

2. **Work with Interfaces**
   - Client code never knows about concrete classes
   - All interaction through abstract factory and product interfaces
   - Enables switching implementations without code changes

3. **Family vs. Individual**
   - Use Abstract Factory when objects must be created together as a family
   - Use Factory Method when creating individual objects
   - Abstract Factory ensures consistency across multiple related products

4. **Trade-offs Matter**
   - Pattern adds complexity in exchange for flexibility
   - Evaluate if product family consistency is worth the additional abstractions
   - Consider simpler patterns for straightforward scenarios

5. **Real-World Analogy**
   - Like a furniture factory: "Modern furniture factory" creates modern chairs, modern tables, modern sofas
   - "Victorian furniture factory" creates Victorian-style furniture
   - Each factory ensures all pieces match the same style (family consistency)

6. **Implementation Pattern**
   ```java
   // Client receives factory (via DI or configuration)
   CommissionSystemFactory factory = getFactory(salesRep.getTier());

   // Client uses factory to create products
   CommissionCalculator calculator = factory.createCommissionCalculator();
   DealValidator validator = factory.createDealValidator();

   // Products from same family work together consistently
   if (validator.validateDeal(deal)) {
       CommissionCalculation result = calculator.calculateCommission(deal, salesRep);
   }
   ```

7. **Design Decision**
   - Choose Abstract Factory when you need to ensure compatibility between multiple related objects
   - The pattern prevents subtle bugs from mixing incompatible products
   - Worth the complexity when product family integrity is critical to the system

---

## Visualization

To view the PlantUML diagram:

1. Open `abstract-factory.puml` in a PlantUML-compatible viewer
2. Use online tools like [PlantText](https://www.planttext.com/) or [PlantUML Online Server](http://www.plantuml.com/plantuml/)
3. Use IDE plugins (IntelliJ IDEA, VS Code with PlantUML extension)

The diagram shows:
- Two product families (Standard and Premium)
- Abstract factory interface and concrete implementations
- Abstract product interfaces and concrete implementations
- Creation relationships between factories and products
- Detailed annotations explaining each component's role

---

## References

- **Source Files:**
  - `AbstractFactoryStructure.java` - Abstract factory and product interfaces (structure only)
  - `AbstractFactoryImplementation.java` - Concrete factories and products (implementations only)
  - `AbstractFactoryUsage.java` - Comprehensive client usage demonstration with main method

- **Related Patterns:**
  - **Factory Method:** Creates one product; Abstract Factory creates families
  - **Builder:** Constructs complex objects step-by-step; Abstract Factory creates families in one call
  - **Prototype:** Creates objects by cloning; Abstract Factory creates by instantiation

- **Design Principles Applied:**
  - Single Responsibility Principle
  - Open/Closed Principle
  - Dependency Inversion Principle
  - Program to an Interface, not an Implementation