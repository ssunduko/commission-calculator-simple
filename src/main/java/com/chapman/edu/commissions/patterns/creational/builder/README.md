# Builder Pattern

## Overview

The **Builder Pattern** is a creational design pattern that separates the construction of a complex object from its representation, allowing the same construction process to create different representations. It provides a step-by-step approach to constructing objects, using a fluent interface that improves code readability and maintainability.

### Pattern Intent

Define a clear, readable way to construct complex objects with many optional parameters, avoiding:
- Telescoping constructors (multiple constructors with different parameter combinations)
- Objects in invalid intermediate states (from using setters)
- Parameter confusion (when multiple parameters have the same type)

### Core Concept

Instead of directly calling a constructor with many parameters:
```java
Deal deal = new Deal("title", value, "salesRep", "id", status, products, closeDate, createdDate, modifiedDate);
// Which parameter is which? Easy to mix up!
```

Use a builder with a fluent interface:
```java
Deal deal = new DealBuilder("title", value, "salesRep")
    .withId("DEAL-001")
    .withStatus(DealStatus.OPEN)
    .withProducts(productList)
    .withCloseDate(LocalDate.now().plusDays(30))
    .build();
// Clear, readable, self-documenting!
```

## Business Domain Application

### Commission Calculator Context

In the commission calculator domain, **Deal** objects are complex entities with:
- **3 required fields**: title, value, salesRepId
- **6 optional fields**: id, status, products, closeDate, createdDate, lastModifiedDate
- **Default values**: status (OPEN), dates (LocalDate.now()), products (empty list)
- **Collections**: List of DealProduct objects
- **Business rules**: Different deal types (standard, premium, won, lost)

### Problem Without Builder

**Option 1: Telescoping Constructors (Anti-pattern)**
```java
public Deal(String title, BigDecimal value, String salesRepId) { ... }
public Deal(String title, BigDecimal value, String salesRepId, String id) { ... }
public Deal(String title, BigDecimal value, String salesRepId, String id, DealStatus status) { ... }
public Deal(String title, BigDecimal value, String salesRepId, String id, DealStatus status, List<DealProduct> products) { ... }
// ... 20+ more constructors for all combinations!
```
**Problems**: Explosion of constructors, hard to maintain, confusing to use

**Option 2: Mutable Object with Setters**
```java
Deal deal = new Deal("title", value, "salesRep");
deal.setId("DEAL-001");
deal.setStatus(DealStatus.OPEN);
deal.setProducts(products);
// Object is in invalid state between constructor and last setter!
```
**Problems**: Invalid intermediate states, not thread-safe, verbose

### Solution With Builder

```java
// Clear, readable, type-safe construction
Deal deal = new DealBuilder("Enterprise Deal", new BigDecimal("50000"), "SALES-001")
    .withId("DEAL-001")
    .withStatus(DealStatus.OPEN)
    .addProduct(product1)
    .addProduct(product2)
    .withCloseDate(LocalDate.now().plusDays(30))
    .build();
```

**Benefits**:
- Readable and self-documenting
- Only required fields in constructor
- Optional fields clearly named
- Type-safe
- Immutable result
- Supports method chaining

## Structure

### Class Diagram

See [`basic-builder.puml`](basic-builder.puml) for the generic Builder pattern structure.

See [`commission-builder.puml`](commission-builder.puml) for the Deal-specific implementation.

### Pattern Participants

#### 1. Product (Deal)
The complex object being constructed.

**Characteristics:**
- Multiple fields (required and optional)
- May have complex initialization logic
- Final result of the building process

**In our implementation:**
- `Deal` class from the domain model
- 9 total attributes
- Represents a sales opportunity

#### 2. Builder (DealBuilder)
Provides interface for constructing Product step-by-step.

**Characteristics:**
- Static nested class (in our implementation)
- Mirrors Product fields
- Provides fluent methods (withXxx, addXxx)
- Returns `this` for method chaining
- Has `build()` method to create Product

**Key Methods:**
- **Constructor**: Accepts required parameters
- **withXxx()**: Sets optional parameters, returns `this`
- **addXxx()**: Incrementally builds collections, returns `this`
- **build()**: Creates and returns configured Product

#### 3. Director (DealDirector) - Optional
Encapsulates common construction sequences.

**Characteristics:**
- Works with Builder interface
- Defines reusable construction templates
- Hides construction complexity
- Provides semantic method names

**Key Methods:**
- **constructStandardDeal()**: 30-day cycle, OPEN status
- **constructPremiumDeal()**: 15-day cycle, premium products
- **constructWonDeal()**: WON status, historical data
- **constructLostDeal()**: LOST status, historical data

## Use Cases

### 1. Test Data Creation
**Scenario**: Creating Deal objects for unit tests

**Without Builder:**
```java
Deal deal = new Deal("Test Deal", new BigDecimal("1000"), "SALES-001");
deal.setStatus(DealStatus.OPEN);
deal.setCloseDate(LocalDate.now().plusDays(30));
// Verbose, requires knowledge of Deal internals
```

**With Builder:**
```java
Deal deal = new DealBuilder("Test Deal", new BigDecimal("1000"), "SALES-001")
    .withStatus(DealStatus.OPEN)
    .withCloseDate(LocalDate.now().plusDays(30))
    .build();
// Clear, concise, readable
```

### 2. Different Deal Configurations
**Scenario**: Creating minimal vs. complete Deal objects

**Minimal Deal** (only required fields):
```java
Deal minimalDeal = new DealBuilder("Quick Deal", new BigDecimal("5000"), "SALES-001")
    .build();
// Defaults: OPEN status, empty products, current dates
```

**Complete Deal** (all fields):
```java
Deal completeDeal = new DealBuilder("Enterprise Deal", new BigDecimal("100000"), "SALES-002")
    .withId("DEAL-002")
    .withStatus(DealStatus.WON)
    .withProducts(productList)
    .withCloseDate(LocalDate.now())
    .withCreatedDate(LocalDate.now().minusDays(60))
    .withLastModifiedDate(LocalDate.now())
    .build();
```

### 3. Incremental Object Construction
**Scenario**: Building products list one at a time

```java
DealBuilder builder = new DealBuilder("Growing Deal", new BigDecimal("0"), "SALES-003");

// Add products incrementally as they're identified
builder.addProduct(new DealProduct("PROD-001", "Software", 1, new BigDecimal("5000")));
builder.addProduct(new DealProduct("PROD-002", "Support", 1, new BigDecimal("2000")));
builder.addProduct(new DealProduct("PROD-003", "Training", 1, new BigDecimal("3000")));

Deal deal = builder.build();
```

### 4. Director for Standard Configurations
**Scenario**: Multiple clients need same Deal types

**Without Director** (duplication):
```java
// Client A
Deal deal1 = new DealBuilder("Deal A", value, "REP-1")
    .withStatus(DealStatus.OPEN)
    .withCloseDate(LocalDate.now().plusDays(30))
    .build();

// Client B (same code, duplicated)
Deal deal2 = new DealBuilder("Deal B", value, "REP-2")
    .withStatus(DealStatus.OPEN)
    .withCloseDate(LocalDate.now().plusDays(30))
    .build();
```

**With Director** (reusable):
```java
DealBuilder builder1 = new DealBuilder("Deal A", value, "REP-1");
DealBuilder builder2 = new DealBuilder("Deal B", value, "REP-2");

DealDirector director = new DealDirector(builder1);
Deal deal1 = director.constructStandardDeal();

director.changeBuilder(builder2);
Deal deal2 = director.constructStandardDeal();
```

## When to Use

###  Use Builder Pattern When:

1. **Object has many parameters** (especially 4+)
   - Deal has 9 parameters total
   - Constructors become unwieldy

2. **Many optional parameters**
   - Deal has 6 optional fields
   - Not all combinations needed

3. **Need for immutability**
   - Want to avoid setters on domain objects
   - Want fully-constructed objects

4. **Creating test data**
   - Builder makes test data creation very readable
   - Easy to create variations

5. **Object construction is complex**
   - Multi-step initialization
   - Validation needed across fields
   - Default values required

6. **Multiple representations**
   - Standard deals, premium deals, historical deals
   - Same construction process, different configurations

7. **Parameters of same type**
   - Easy to mix up: `Deal(String, String, String)`
   - Builder makes purpose clear: `.withId(x).withTitle(y)`

## When NOT to Use

### L Avoid Builder Pattern When:

1. **Simple objects**
   - Objects with 1-3 fields
   - All fields are required
   - Example: `Point(int x, int y)` - constructor is fine

2. **Rarely used classes**
   - Not worth the extra code
   - Simple constructor suffices

3. **Fields rarely change**
   - If object always constructed the same way
   - Builder adds unnecessary complexity

4. **Performance critical**
   - Builder adds object creation overhead
   - Extra method calls for fluent interface
   - (Usually negligible, but consider for high-frequency scenarios)

5. **Team unfamiliarity**
   - If team doesn't understand pattern
   - Can add confusion instead of clarity

## Benefits

### Code Quality Benefits

1. **Readability**
   - Self-documenting code
   - Clear what each parameter represents
   - Fluent interface reads like natural language

2. **Maintainability**
   - Easy to add new optional parameters
   - No need to modify existing constructors
   - Follows Open/Closed Principle

3. **Type Safety**
   - Compile-time checking
   - IDE autocomplete support
   - Can't mix up parameter order

4. **Immutability**
   - Build once, then immutable
   - Thread-safe
   - No invalid intermediate states

5. **Flexibility**
   - Same builder, different configurations
   - Director enables reusable templates
   - Easy to create variations

6. **Testability**
   - Clean test data creation
   - Easy to create edge cases
   - Readable test assertions

### Design Benefits

1. **Separation of Concerns**
   - Construction logic separate from Product class
   - Product focused on behavior, Builder on construction

2. **Single Responsibility Principle**
   - Product doesn't handle its own construction
   - Builder solely responsible for construction

3. **DRY (Don't Repeat Yourself)**
   - Director eliminates construction duplication
   - Common sequences centralized

## Drawbacks

### Disadvantages to Consider

1. **Code Verbosity**
   - More classes to maintain
   - Builder class mirrors Product fields
   - More lines of code overall

2. **Complexity**
   - Additional abstraction layer
   - May be overkill for simple objects
   - Learning curve for team

3. **Memory Overhead**
   - Builder object created for each Product
   - Temporary object that's discarded after build()
   - (Usually negligible)

4. **Maintenance Burden**
   - Adding field to Product requires updating Builder
   - Must keep Builder and Product in sync
   - Risk of forgetting to update Builder

5. **No Compile-Time Enforcement**
   - Can't enforce that required fields are set (without constructor parameters)
   - Runtime validation needed if using parameterless constructor
   - Our implementation mitigates this with constructor parameters

## Key Benefits in Commission Calculator

### Specific Advantages for This Project

1. **Test Data Creation**
   - JUnit tests extensively use DealBuilder
   - Easy to create various Deal configurations
   - Readable test setup code

2. **Deal Variations**
   - Standard deals, premium deals, historical deals
   - Director provides semantic constructors
   - Business logic encapsulated

3. **Default Values**
   - OPEN status by default
   - Current dates by default
   - Empty product list by default

4. **Product List Building**
   - `addProduct()` for incremental building
   - `withProducts()` for batch setting
   - Both support fluent interface

5. **Historical Data**
   - Easy to create past-dated deals
   - `constructWonDeal()` and `constructLostDeal()` templates
   - Useful for reporting and analytics tests

## Key Takeaways

### Essential Concepts to Remember

1. **Builder separates construction from representation**
   - HOW to build (Builder) vs. WHAT to build (Product)
   - Enables flexible object creation

2. **Fluent interface improves readability**
   - Method chaining creates readable code
   - Each method returns `this`
   - Terminal `build()` method creates Product

3. **Constructor enforces required fields**
   - Required parameters in Builder constructor
   - Optional parameters via fluent methods
   - Compile-time safety for essential fields

4. **Director is optional but powerful**
   - Encapsulates common construction sequences
   - Provides reusable templates
   - Reduces code duplication

5. **Pattern solves specific problems**
   - Telescoping constructors
   - Many optional parameters
   - Complex object initialization
   - Need for immutability

6. **Trade-offs exist**
   - More code to maintain
   - Builder and Product must stay in sync
   - Worth it for complex objects, not simple ones

### Implementation Guidelines

**Do:**
-  Use Builder for objects with 4+ parameters
-  Make Builder a static nested class
-  Use fluent method names (withXxx, addXxx)
-  Return `this` from builder methods
-  Put required fields in constructor
-  Use Director for common patterns
-  Create defensive copies of collections
-  Provide default values where appropriate

**Don't:**
- L Use Builder for simple 1-3 field objects
- L Forget to return `this` from builder methods
- L Skip required field validation
- L Make Builder too complex
- L Couple Builder to specific Product implementation details unnecessarily

## Code Examples

### Files in This Directory

1. **[BuilderPatternImplementation.java](BuilderPatternImplementation.java)**
   - DealBuilder class implementation
   - Fluent interface methods
   - Build method with default value handling
   - Comprehensive comments explaining concepts

2. **[BuilderPatternUsage.java](BuilderPatternUsage.java)**
   - Usage examples of DealBuilder
   - Minimal, complete, and custom Deal creation
   - Demonstrates flexibility of pattern
   - Runnable main method with examples

3. **[DirectorPatternWithBuilder.java](DirectorPatternWithBuilder.java)**
   - DealDirector class implementation
   - Construction templates (standard, premium, won, lost)
   - Runtime builder switching
   - Demonstrates Director pattern benefits

4. **[basic-builder.puml](basic-builder.puml)**
   - Generic Builder pattern structure
   - Class diagram with relationships
   - Pattern components explained
   - Notes on responsibilities

5. **[commission-builder.puml](commission-builder.puml)**
   - Deal-specific implementation diagram
   - Shows actual classes used
   - Business logic documentation
   - Usage examples in notes

### Running the Examples

**Build Pattern Usage Examples:**
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.creational.builder.BuilderPatternUsage"
```

**Director Pattern Examples:**
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.creational.builder.DirectorPatternWithBuilder"
```

### Quick Reference

**Creating a minimal Deal:**
```java
Deal deal = new DealBuilder("Title", new BigDecimal("10000"), "SALES-001")
    .build();
```

**Creating a complete Deal:**
```java
Deal deal = new DealBuilder("Title", new BigDecimal("10000"), "SALES-001")
    .withId("DEAL-001")
    .withStatus(DealStatus.OPEN)
    .addProduct(product1)
    .addProduct(product2)
    .withCloseDate(LocalDate.now().plusDays(30))
    .build();
```

**Using Director for standard configurations:**
```java
DealBuilder builder = new DealBuilder("Title", new BigDecimal("10000"), "SALES-001");
DealDirector director = new DealDirector(builder);
Deal standardDeal = director.constructStandardDeal();
Deal premiumDeal = director.constructPremiumDeal();
```

## Related Patterns

- **Abstract Factory**: Creates families of related objects; Builder focuses on constructing a single complex object step-by-step
- **Prototype**: Clones existing objects; Builder constructs new objects from scratch
- **Factory Method**: Returns instance of different classes; Builder always returns same class with different configurations
- **Fluent Interface**: Builder uses fluent interface for method chaining and readability

## References

- Gang of Four (GoF) Design Patterns: "Separate the construction of a complex object from its representation"
- Effective Java by Joshua Bloch: Item 2 - "Consider a builder when faced with many constructor parameters"
- This implementation: `com.chapman.edu.commissions.patterns.creational.builder`

---

**Pattern Type**: Creational
**Complexity**: Medium
**Frequency of Use**: High (especially in Java development)
**Implementation Status**: Complete with examples and documentation