# Prototype Pattern

## Overview

The **Prototype Pattern** is a creational design pattern that belongs to the Gang of Four's collection of fundamental software design patterns. Rather than creating new objects from scratch using constructors, the Prototype pattern enables objects to create copies of themselves. This simple yet powerful concept—"clone yourself instead of construct new"—fundamentally changes how we approach object creation in scenarios where initialization is complex or expensive.

### The Pattern's Place in Software Design

The Prototype pattern sits alongside other creational patterns like Factory, Builder, Singleton, and Abstract Factory. While these patterns all address object creation, Prototype takes a unique approach: instead of defining how to construct objects, it defines how to copy them. This distinction becomes crucial when dealing with complex object graphs, expensive initialization routines, or situations where you need to create many similar objects with only slight variations.

### What Problem Does It Solve?

Imagine you're building a commission calculator where sales representatives need to create dozens of deal proposals daily. Each Deal object is complex—it has products, pricing tiers, dates, status information, and relationships to other objects. Creating each deal from scratch means:

1. **Expensive Object Creation**: If Deal objects require database queries to load product catalogs, pricing rules, or commission structures, creating each one from scratch becomes a performance bottleneck. Network calls, complex calculations, and resource allocation all add up.

2. **Complex Initialization**: Setting up a Deal properly might require ten or more steps: creating the deal, setting its status, adding products, calculating totals, applying discounts, setting dates, linking to customer records, and more. This complexity makes it error-prone and tedious.

3. **Dynamic Configuration**: Sales reps work with standard deal templates—"Standard," "Premium," and "Enterprise." The specific template needed is only known at runtime based on the customer's selection. Traditional factory patterns would require conditional logic or a proliferation of classes.

4. **Configuration Explosion**: Without Prototype, you might be tempted to create subclasses like `StandardDeal`, `PremiumDeal`, and `EnterpriseDeal` just to handle different configurations. This leads to class explosion—a maintenance nightmare where each new configuration requires a new class.

5. **State Duplication**: Sometimes you need to duplicate an existing deal's exact state—perhaps to create a similar proposal for a different customer, or to generate multiple pricing variations. Manually copying each field is fragile and breaks when the object structure changes.

### How the Pattern Solves These Problems

The Prototype pattern addresses these challenges through several elegant mechanisms:

**Cloning Instead of Construction**: Rather than calling constructors and setters repeatedly, you create a fully-configured prototype once, then clone it whenever you need a new instance. This shifts the burden from repeated initialization to one-time setup plus fast copying.

**Prototype Registry**: The pattern introduces the concept of a registry—essentially a catalog of pre-configured prototypes indexed by meaningful names like "standard," "premium," or "enterprise." Need a premium deal? Simply retrieve the premium prototype from the registry and clone it. This eliminates conditional logic and makes adding new templates trivial.

**Shallow vs. Deep Cloning**: The pattern acknowledges that copying has nuances. A shallow clone copies object references (fast but shared), while a deep clone recursively copies all nested objects (slower but independent). You choose the strategy that matches your needs—shallow for read-only scenarios, deep when modifications are needed.

**Polymorphic Cloning**: Through the Prototype interface, you can clone objects without knowing their concrete types. Your code works with the `Prototype` interface, calling `clone()` on whatever object you have. This reduces coupling and increases flexibility.

**Runtime Flexibility**: Unlike factory classes compiled into your application, a prototype registry can be modified at runtime. Add new templates, remove deprecated ones, or update existing prototypes—all without changing code or redeploying.

### A Real-World Analogy

Think about a **cookie cutter** or a **photocopier**:

When you bake cookies, you don't hand-craft each cookie individually. Instead, you roll out the dough (your complex object state) and use a cookie cutter (the prototype) to quickly stamp out identical shapes. If you want star-shaped cookies, you grab the star cutter. For hearts, you switch to the heart cutter. Each cutter is like a prototype template, and the stamping process is like cloning.

Similarly, a photocopier doesn't require you to manually redraw each copy of a document. You place the original (prototype) in the machine and press copy (clone). The photocopier handles all the complexity of duplicating the content. If you need 100 copies, you don't spend time creating each one from scratch—you simply clone the original.

In this analogy:
- The **cookie dough** represents your complex object state
- The **cookie cutter** is your prototype (defines the shape/structure)
- **Stamping out cookies** is the cloning process (much faster than hand-crafting)
- **Different cookie cutters** are different prototype templates
- Your **cookie recipe book** is the Prototype Registry (a catalog of available shapes)

Just as it's dramatically faster to use a cookie cutter than to hand-shape each cookie identically, it's faster and more reliable to clone a pre-configured object than to reconstruct it from scratch each time. The pattern transforms object creation from a complex, error-prone process into a simple, fast operation.

### Pattern Classification

- **Type**: Creational Design Pattern
- **Complexity**: Medium (requires understanding shallow vs. deep copying)
- **Frequency**: Medium-High in enterprise applications with complex object graphs
- **Alternative Names**: Clone Pattern

## Business Domain Application

- **Context**: Commission calculator needs to create similar Deal objects repeatedly
- **Problem**: Deal creation involves complex setup (products, status, dates, relationships)
- **Solution**: Create template deals once, clone them to make new deals
- **Real-World Usage**:
  - Standard deal templates per product tier (standard/premium/enterprise)
  - Regional deal templates (US/EU/APAC)
  - Industry-specific templates (Healthcare/Finance/Tech)
  - Test data generation from real deal configurations

## Structure

### Pattern Participants

**1. Prototype Interface (`Prototype<T>`)**
- Declares `clone()` method
- Generic type parameter for type-safe cloning
- Contract for all cloneable objects

**2. Concrete Prototypes**
- `CloneableDeal`: Extends Deal, implements cloning
- `CloneableDealProduct`: Extends DealProduct, implements cloning
- Provide both shallow and deep clone methods

**3. Prototype Registry (Optional)**
- `PrototypeRegistry`: Centralized catalog of prototypes
- Stores pre-configured templates by name
- Provides quick access to common prototypes

**4. Client**
- Retrieves prototype from registry
- Clones it to create new instance
- Customizes clone for specific needs

### Class Relationships

- `CloneableDeal` extends `Deal` and implements `Prototype<CloneableDeal>`
- `CloneableDealProduct` extends `DealProduct` and implements `Prototype<CloneableDealProduct>`
- `PrototypeRegistry` stores and manages `CloneableDeal` instances
- Clients use registry to retrieve and clone prototypes

## Use Cases

**1. Deal Template System**
- Create standard deal templates for different tiers
- Sales reps clone template and customize for their customer
- Ensures consistency across deals of same type

**2. Deal Duplication**
- Clone existing deal to create similar opportunity
- Modify specific fields (customer, value, ID)
- Reuse products, settings, and structure

**3. Test Data Generation**
- Create one fully-configured test deal
- Clone it multiple times with variations
- Fast, consistent test data creation

**4. Historical Deal Recreation**
- Clone past successful deals
- Use as starting point for new opportunities
- Leverage proven configurations

**5. Proposal Variations**
- Create base deal configuration
- Clone to generate multiple proposal options
- Each clone represents different pricing/product mix

## When to Use

✅ **Use Prototype Pattern When:**

- Object creation is expensive (database queries, complex calculations)
- Need many similar objects with slight variations
- Want to avoid coupling to specific classes
- Object initialization is complex
- Need to duplicate existing object state
- Creating subclasses just for configuration is impractical
- Runtime object configuration required
- Performance benefit from cloning vs. creation

## When NOT to Use

❌ **Avoid Prototype Pattern When:**

- Objects are simple and cheap to create
- Each object is fundamentally unique
- Deep cloning is complex and error-prone
- Objects have circular references
- Cloning semantics are unclear for domain
- Team unfamiliar with cloning concepts
- Standard constructors are sufficient

## Benefits

**Code Quality**
- **Reduces coupling**: Clients don't need to know concrete classes
- **Simplifies creation**: Clone instead of complex initialization
- **Improves performance**: Cloning often faster than creation
- **Enables flexibility**: Add/remove prototypes at runtime

**Design Benefits**
- **Hides complexity**: Complex initialization encapsulated in prototype
- **Reduces subclassing**: Avoid creating subclasses just for configuration
- **Supports composition**: Prototypes can be composed from other prototypes
- **Runtime flexibility**: Can change available prototypes dynamically

**Business Benefits**
- **Standardization**: Templates ensure consistency
- **Reusability**: Configure once, clone many times
- **Productivity**: Faster deal creation for sales reps
- **Maintainability**: Update template, all future clones use new config

## Drawbacks

**Technical Challenges**
- **Shallow vs Deep cloning**: Must understand and choose correctly
- **Circular references**: Can cause infinite loops in deep cloning
- **Implementation complexity**: Clone method must handle all fields correctly
- **Maintenance burden**: Must keep clone() in sync with class fields

**Design Trade-offs**
- **Memory overhead**: Registry stores prototype instances
- **Cloning semantics**: Not always clear what "copy" means for domain objects
- **Mutable prototypes**: Modifying stored prototype affects all future clones
- **Type safety**: Generic approach required for type-safe cloning

**Conceptual Complexity**
- **Learning curve**: Team must understand shallow vs. deep cloning
- **Bug potential**: Forgetting to clone before modifying
- **Testing complexity**: Must test both cloning strategies

## Key Benefits in Commission Calculator

- **Deal Templates**: Pre-configured deals for standard/premium/enterprise tiers
- **Shallow Clone**: Fast creation when products don't change (read-only scenarios)
- **Deep Clone**: Independent copies when modifications needed
- **Registry**: Centralized catalog of deal templates by name
- **Flexibility**: Add new templates without code changes
- **Test Data**: Easy generation of varied test deals from prototypes

## Key Takeaways

**Essential Concepts**
- **Clone vs. Create**: Prototype clones existing objects instead of creating new
- **Shallow Copy**: Copies references; changes to referenced objects affect both
- **Deep Copy**: Clones all referenced objects; complete independence
- **Registry Pattern**: Often used with Prototype to manage template catalog

**Implementation Guidelines**
- Always clone() before modifying retrieved prototype
- Use shallow clone for performance when safe (immutable references)
- Use deep clone for complete independence (mutable references)
- Store fully-configured prototypes in registry
- Use meaningful keys ("standard", "premium") not generic ("proto1", "proto2")

**Pattern Combinations**
- **With Builder**: Build complex prototype once, clone many times
- **With Factory**: Factory returns clones of stored prototypes
- **With Registry**: Manage catalog of commonly used prototypes
- **With Singleton**: Registry itself often implemented as singleton

**Shallow vs. Deep Decision Tree**
- Immutable referenced objects → Shallow clone sufficient
- Mutable referenced objects NOT modified → Shallow clone acceptable
- Mutable referenced objects will be modified → Deep clone required
- Complex object graph → Consider deep clone or serialization

**Common Pitfalls to Avoid**
- Modifying retrieved prototype directly (always clone first)
- Using shallow clone when deep clone needed
- Forgetting to implement clone() for nested objects
- Not handling null fields in clone()
- Circular references in deep clone (causes stack overflow)

---

**Files**: [`PrototypePatternStructure.java`](PrototypePatternStructure.java) | [`PrototypePatternImplementation.java`](PrototypePatternImplementation.java) | [`PrototypeRegistry.java`](PrototypeRegistry.java) | [`PrototypePatternUsage.java`](PrototypePatternUsage.java)

**Diagrams**: [`basic-prototype.puml`](basic-prototype.puml) | [`commission-prototype.puml`](commission-prototype.puml)

**Run Examples**: `mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.creational.prototype.PrototypePatternUsage"`