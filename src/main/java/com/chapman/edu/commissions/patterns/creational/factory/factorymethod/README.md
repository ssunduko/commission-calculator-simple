# Factory Method Pattern

## What Was Done

This directory demonstrates the **Factory Method Pattern** implementation in the context of a commission calculation and deal creation system. The implementation includes:

1. **PlantUML Diagram** (`factory-method.puml`) - Visual representation of the pattern structure showing:
   - Abstract creator class (DealFactory) with factory method
   - Concrete creator implementations (HardwareDealFactory, SoftwareDealFactory, ServiceDealFactory)
   - Product class (Deal) created by the factories
   - Template method and hook method integration
   - Relationships and component annotations

2. **Java Implementation** - Complete working code demonstrating:
   - Abstract creator defining the factory method interface
   - Template method pattern integration for consistent workflow
   - Multiple concrete creators for different deal types
   - Hook methods for optional customization
   - Client usage examples

---

## Pattern Overview

The **Factory Method Pattern** is a creational design pattern that defines an interface for creating objects but lets subclasses decide which class to instantiate. It delegates object creation to subclasses, promoting loose coupling and adherence to the Open/Closed Principle.

**Core Intent:** Allow a class to defer instantiation to subclasses. The pattern lets subclasses decide what specific objects to create while maintaining a common creation interface.

**Key Principle:** "Program to an interface, not an implementation." Client code works with the abstract creator and product interfaces, never knowing the concrete types being instantiated.

**Distinguishing Feature:** Unlike Simple Factory (one factory class with conditional logic), Factory Method uses inheritance and polymorphism. Each product variant gets its own factory subclass.

---

## Business Domain Application

In the commission calculator domain, the Factory Method Pattern is applied to create different types of deals with type-specific configurations and products:

### Product Types

1. **Hardware Deals (HardwareDealFactory)**
   - **ID Prefix:** "HW-" for easy identification
   - **Default Products:**
     - Laptop (1 unit @ $1,200)
     - Monitor (2 units @ $300 each)
   - **Use Case:** Physical equipment sales like computers, servers, networking hardware
   - **Business Rule:** Hardware deals require tangible product inventory

2. **Software Deals (SoftwareDealFactory)**
   - **ID Prefix:** "SW-" for licensing tracking
   - **Default Products:**
     - Operating System (5 licenses @ $200 each)
     - Office Package (5 licenses @ $150 each)
   - **Use Case:** Software licenses and subscriptions for enterprise customers
   - **Business Rule:** Software deals involve license counts and seat management

3. **Service Deals (ServiceDealFactory)**
   - **ID Prefix:** "SVC-" for service tracking
   - **Default Products:**
     - Technical Support (1 package @ $500)
     - Training (1 session @ $300)
   - **Use Case:** Professional services like consulting, support, and training
   - **Business Rule:** Service deals are time-based engagements with deliverables

### Real-World Scenario

A sales organization processes different types of deals:
- **Sales rep closes a hardware deal:** System uses HardwareDealFactory to create a deal with "HW-" prefix and pre-populates it with common hardware products
- **Sales rep closes a software deal:** System uses SoftwareDealFactory to create a deal with "SW-" prefix and includes standard software licenses
- **Sales rep closes a service contract:** System uses ServiceDealFactory to create a deal with "SVC-" prefix and bundles support with training

The factory method ensures each deal type is initialized correctly with appropriate products, IDs, and configurations without the client code needing to know the details.

---

## Structure

### Participants

1. **Creator (DealFactory)**
   - Abstract class that declares the factory method
   - Defines template method that uses the factory method
   - May provide hook methods for optional customization
   - Methods:
     - `createDeal()`: Abstract factory method (must be implemented by subclasses)
     - `createDealWithProducts()`: Template method (provides consistent workflow)
     - `addDefaultProducts()`: Hook method (can be overridden for customization)

2. **ConcreteCreator (HardwareDealFactory, SoftwareDealFactory, ServiceDealFactory)**
   - Extends the Creator class
   - Implements the factory method to create specific product variants
   - Overrides hook methods to customize product initialization
   - Each represents one product variant or category
   - Responsibilities:
     - Define how to create the specific product type
     - Configure product with type-specific attributes
     - Add type-specific default products

3. **Product (Deal)**
   - The object created by the factory method
   - Defines the interface of objects the factory method creates
   - Same class used by all factories (configured differently)
   - Contains common properties (id, title, value, status, products)

4. **Client** (Not shown in diagram, but exists in usage code)
   - Uses Creator abstraction to work with factories
   - Calls template method to create products
   - Never instantiates products directly
   - Decoupled from concrete creator and product creation logic

### Class Relationships

- **Inheritance:** ConcreteCreators extend Creator (abstract class)
- **Creation:** Each concrete creator creates Product instances
- **Dependency:** Client depends only on Creator abstraction and Product class
- **Template Method:** Creator's template method orchestrates creation using factory method and hook methods
- **Polymorphism:** Same factory method call produces different product configurations

---

## Use Cases

### When to Use

1. **Unknown Product Types at Design Time**
   - System cannot anticipate all product types it will need
   - Product types determined at runtime based on configuration or user input
   - Example: Deal type selected by user from dropdown menu

2. **Subclass-Specific Object Creation**
   - Class wants subclasses to specify which objects to create
   - Different subclasses need different product configurations
   - Example: Different deal factories for hardware, software, and services

3. **Framework Extension Points**
   - Framework provides hooks for users to customize object creation
   - Library wants to allow extension without modifying core code
   - Example: Plugin system where plugins register custom factories

4. **Parallel Class Hierarchies**
   - One hierarchy for creators, parallel hierarchy for products
   - Each creator responsible for creating corresponding product
   - Example: DealFactory hierarchy creates Deal variants

5. **Localize Knowledge of Product Creation**
   - Encapsulate product creation logic in specialized classes
   - Separate creation logic from business logic
   - Example: Deal creation rules separate from deal processing logic

6. **Replace Conditional Logic**
   - Eliminate switch/if-else statements for object creation
   - Each condition becomes a factory subclass
   - Example: Replace `if (type == "hardware")` with HardwareDealFactory

### When NOT to Use

1. **Simple Object Creation**
   - Object creation is straightforward with no special logic
   - No variations in how objects are created
   - **Alternative:** Use constructor directly

2. **Single Product Variant**
   - Only one way to create the product
   - No need for different factory implementations
   - **Alternative:** Use Simple Factory or direct instantiation

3. **No Future Extensions Expected**
   - Product types are fixed and unlikely to change
   - No need for extensibility
   - **Alternative:** Use Simple Factory with conditional logic

4. **Excessive Subclass Proliferation**
   - Would result in too many factory subclasses
   - Maintenance overhead outweighs benefits
   - **Alternative:** Use parameterized factory or configuration

5. **Complex Product Families**
   - Need to create families of related objects together
   - Products must be compatible with each other
   - **Alternative:** Use Abstract Factory Pattern

6. **Step-by-Step Construction**
   - Product construction is complex with many optional parameters
   - Need to build product incrementally
   - **Alternative:** Use Builder Pattern

---

## Benefits

1. **Open/Closed Principle**
   - Open for extension: New product types added via new subclasses
   - Closed for modification: Existing code unchanged when adding new types
   - Example: Add CloudDealFactory without modifying existing factories

2. **Loose Coupling**
   - Client depends on abstract creator and product interfaces
   - Decouples client from concrete product creation
   - Easy to substitute different factory implementations

3. **Single Responsibility Principle**
   - Product creation logic separated from business logic
   - Each factory responsible for one product type
   - Clear separation of concerns

4. **Customization Hooks**
   - Template method provides consistent workflow
   - Hook methods allow subclass customization
   - Balance between consistency and flexibility

5. **Eliminates Conditional Logic**
   - Replaces switch/if-else with polymorphism
   - Each condition becomes a class
   - More maintainable and testable

6. **Parallel Class Hierarchies**
   - Links creator hierarchy with product hierarchy
   - Each creator knows how to create its product
   - Clear correspondence between factories and products

7. **Encapsulation of Creation Logic**
   - Complex initialization hidden in factory
   - Client gets fully configured products
   - Creation details can change without affecting clients

---

## Drawbacks

1. **Increased Complexity**
   - Introduces additional classes and inheritance
   - Can be overkill for simple creation scenarios
   - More code to understand and maintain

2. **Subclass Requirement**
   - Must create a new subclass for each product variant
   - Can lead to class proliferation
   - May be excessive if variants are simple

3. **Inheritance Constraints**
   - Tied to inheritance hierarchy
   - Less flexible than composition-based approaches
   - Subclasses must extend specific creator

4. **Single Product Type**
   - Each factory method creates one product type
   - Not suitable for creating families of related products
   - Use Abstract Factory for product families

5. **Learning Curve**
   - Requires understanding of template method pattern
   - More abstract than direct instantiation
   - Can be confusing for junior developers

6. **Indirection Overhead**
   - Additional layer of abstraction
   - May impact performance in high-throughput scenarios
   - Slightly more complex debugging

---

## Key Benefits (Summary)

- **Extensibility:** New product types added without modifying existing code
- **Loose Coupling:** Client decoupled from concrete product creation
- **Encapsulation:** Creation logic centralized in factory classes
- **Polymorphism:** Same interface, different implementations
- **Template Method:** Consistent creation workflow with customization points
- **SOLID Compliance:** Follows Open/Closed and Single Responsibility principles

---

## Key Takeaways

1. **"Let Subclasses Decide"**
   - Factory Method defers instantiation to subclasses
   - Each subclass implements the factory method differently
   - Client works with abstraction, unaware of concrete types

2. **Template Method Integration**
   - Abstract creator often defines template method
   - Template method calls factory method at appropriate time
   - Provides consistent workflow while allowing customization
   - Example: `createDealWithProducts()` ensures deals are fully initialized

3. **Factory Method vs Simple Factory**
   - **Simple Factory:** One class with conditional logic (if/switch)
   - **Factory Method:** Multiple classes with inheritance and polymorphism
   - **When to use Factory Method:** Need extensibility and Open/Closed compliance
   - **When to use Simple Factory:** Few product types, unlikely to change

4. **Factory Method vs Abstract Factory**
   - **Factory Method:** Creates single products, one factory method
   - **Abstract Factory:** Creates families of related products, multiple factory methods
   - **When to use Factory Method:** Creating individual products
   - **When to use Abstract Factory:** Creating product families that must work together

5. **Trade-offs Matter**
   - Adds complexity in exchange for extensibility
   - Worth it when product types likely to grow
   - Overkill for simple, stable scenarios

6. **Real-World Analogy**
   - Like a manufacturing plant: "Vehicle Factory" defines how to build vehicles
   - "Car Factory" builds cars, "Truck Factory" builds trucks
   - Each factory knows the specific steps for its product type
   - All follow the same overall process (template method)

7. **Implementation Pattern**
   ```java
   // Abstract Creator
   abstract class DealFactory {
       // Factory Method - subclasses implement
       protected abstract Deal createDeal(String title, BigDecimal value, String salesRepId);

       // Template Method - uses factory method
       public Deal createDealWithProducts(String title, BigDecimal value, String salesRepId) {
           Deal deal = createDeal(title, value, salesRepId);  // Factory Method call
           deal.setCreatedDate(LocalDate.now());              // Common setup
           addDefaultProducts(deal);                          // Hook method
           return deal;
       }

       // Hook Method - subclasses can override
       protected void addDefaultProducts(Deal deal) { }
   }

   // Concrete Creator
   class HardwareDealFactory extends DealFactory {
       @Override
       protected Deal createDeal(String title, BigDecimal value, String salesRepId) {
           Deal deal = new Deal(title, value, salesRepId);
           deal.setId("HW-" + System.currentTimeMillis());
           return deal;
       }

       @Override
       protected void addDefaultProducts(Deal deal) {
           // Add hardware-specific products
       }
   }

   // Client
   DealFactory factory = new HardwareDealFactory();
   Deal deal = factory.createDealWithProducts("Laptop Sale", new BigDecimal("5000"), "SALES-001");
   ```

8. **Hook Methods**
   - Provide optional extension points in template method
   - Have default implementation (often empty)
   - Subclasses can override to add behavior
   - More flexible than requiring implementation (like abstract methods)

9. **When Inheritance Makes Sense**
   - Factory Method uses inheritance (is-a relationship)
   - Makes sense when creators share common behavior
   - Template method provides shared workflow
   - Each subclass specializes the creation step

10. **Design Decision**
    - Choose Factory Method when:
      - Need to extend product types over time
      - Want to eliminate conditional creation logic
      - Creators have common workflow with variant steps
    - Avoid when:
      - Product types are fixed and unlikely to change
      - Object creation is trivial
      - Would result in excessive subclasses

---

## Visualization

To view the PlantUML diagram:

1. Open `factory-method.puml` in a PlantUML-compatible viewer
2. Use online tools like [PlantText](https://www.planttext.com/) or [PlantUML Online Server](http://www.plantuml.com/plantuml/)
3. Use IDE plugins (IntelliJ IDEA, VS Code with PlantUML extension)

The diagram shows:
- Abstract creator (DealFactory) with factory method, template method, and hook method
- Three concrete creators (Hardware, Software, Service factories)
- Product class (Deal) created by factories
- Creation relationships showing how each factory creates configured Deal objects
- Detailed annotations explaining each component's role and responsibilities

---

## References

- **Source Files:**
  - `FactoryMethodStructure.java` - Basic structure with commission calculator example
  - `FactoryMethodImplementation.java` - Complete implementation with deal creation
  - `FactoryMethodUsage.java` - Client usage examples and demonstration

- **Related Patterns:**
  - **Abstract Factory:** Creates families of products; Factory Method creates single products
  - **Template Method:** Often used within Factory Method for consistent workflow
  - **Prototype:** Alternative creational pattern using cloning instead of subclassing
  - **Simple Factory:** Simpler alternative without inheritance

- **Design Principles Applied:**
  - Open/Closed Principle: Open for extension, closed for modification
  - Single Responsibility Principle: Each factory creates one product type
  - Dependency Inversion Principle: Depend on abstractions, not concrete classes
  - Program to an Interface: Work with abstract creator and product interfaces

- **Pattern Comparison:**
  | Aspect | Simple Factory | Factory Method | Abstract Factory |
  |--------|---------------|----------------|------------------|
  | Structure | Single class | Class hierarchy | Interface + implementations |
  | Product Types | Single | Single | Multiple (families) |
  | Extensibility | Modify existing class | Add subclass | Add factory implementation |
  | Complexity | Low | Medium | High |
  | Use Case | Few fixed types | Extensible types | Product families |