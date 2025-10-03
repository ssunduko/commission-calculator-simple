# Visitor Pattern Implementation

## Overview

The **Visitor Pattern** is a behavioral design pattern that lets you separate algorithms from the objects on which they operate. It allows you to add new operations to existing object structures without modifying those structures.

This implementation demonstrates the Visitor pattern through **Commission System Operations**, showing how to perform various operations (reporting, validation, statistics, export) on domain objects (Deals, Commission Plans, Users, Disputes) without polluting those domain models with unrelated concerns.

## Pattern Components

### 1. Visitor Interface (`CommissionEntityVisitor`)
Declares a visit method for each concrete element type:
```java
interface CommissionEntityVisitor {
    void visitDeal(CommissionDeal deal);
    void visitCommissionPlan(CommissionPlanEntity plan);
    void visitUser(UserEntity user);
    void visitDispute(DisputeEntity dispute);
}
```

### 2. Concrete Visitors
Four specialized visitors implementing different operations:

| Visitor | Purpose | State Maintained |
|---------|---------|------------------|
| **ReportVisitor** | Generate comprehensive reports | Report content, entity counts, total values |
| **ValidationVisitor** | Validate business rules | Error list, valid entity count |
| **StatisticsVisitor** | Collect aggregated statistics | Counts by type/status, value totals, KPIs |
| **CsvExportVisitor** | Export to CSV format | CSV data buffer |

### 3. Element Interface (`CommissionEntity`)
All visitable objects implement this interface:
```java
interface CommissionEntity {
    void accept(CommissionEntityVisitor visitor);
    String getId();
    String getEntityType();
}
```

### 4. Concrete Elements
Wrapper classes for domain models that enable visitor pattern:
- `CommissionDeal` - wraps `Deal`
- `CommissionPlanEntity` - wraps `CommissionPlan`
- `UserEntity` - wraps `User`
- `DisputeEntity` - wraps `Dispute`

**Note:** We wrap domain models rather than modifying them directly. This preserves the purity of domain objects and allows the pattern to be added non-invasively.

## Problem Solved

### Without Visitor Pattern ❌

```java
// Domain models become bloated with unrelated operations
class Deal {
    // Business logic
    private String title;
    private BigDecimal value;

    // Report operations
    public String generateReport() { /* ... */ }
    public String generateDetailedReport() { /* ... */ }
    public String generateSummaryReport() { /* ... */ }

    // Validation operations
    public boolean validate() { /* ... */ }
    public List<String> validateForExport() { /* ... */ }

    // Export operations
    public String exportToCsv() { /* ... */ }
    public String exportToJson() { /* ... */ }
    public String exportToXml() { /* ... */ }

    // Statistics operations
    public void collectStatistics(StatsCollector collector) { /* ... */ }

    // New operation = MODIFY this class again!
}
```

**Problems:**
- 🔴 Violates Single Responsibility Principle
- 🔴 Violates Open/Closed Principle
- 🔴 Domain models become bloated
- 🔴 Related operations scattered across multiple classes
- 🔴 Hard to add new operations (must modify all classes)
- 🔴 Mixing concerns: business logic + reporting + validation + export

### With Visitor Pattern ✅

```java
// Domain model stays clean
class Deal {
    private String title;
    private BigDecimal value;
    // ... business logic only ...
}

// Wrapper for visitor pattern
class CommissionDeal implements CommissionEntity {
    private final Deal deal;

    public void accept(CommissionEntityVisitor visitor) {
        visitor.visitDeal(this);  // Double dispatch
    }
}

// Operations in separate visitor classes
class ReportVisitor implements CommissionEntityVisitor {
    public void visitDeal(CommissionDeal deal) { /* all report logic */ }
    public void visitUser(UserEntity user) { /* all report logic */ }
    // ...
}

class ValidationVisitor implements CommissionEntityVisitor {
    public void visitDeal(CommissionDeal deal) { /* all validation logic */ }
    public void visitUser(UserEntity user) { /* all validation logic */ }
    // ...
}

// Easy to add new operations!
class NewOperationVisitor implements CommissionEntityVisitor {
    // Add new operation WITHOUT touching existing code
}
```

**Benefits:**
- ✅ Single Responsibility: Each visitor has one operation
- ✅ Open/Closed: Open for extension (new visitors), closed for modification
- ✅ Clean domain models: Only business logic
- ✅ Grouped operations: All report logic together, all validation together
- ✅ Easy to add operations: Just create new visitor class

## Double Dispatch Mechanism

The Visitor pattern uses **double dispatch** to determine behavior based on both the visitor type AND the element type:

```
Client Code:
  element.accept(visitor);
    ↓
First Dispatch (element type determines which accept() is called):
  CommissionDeal.accept(visitor):
    visitor.visitDeal(this);
      ↓
Second Dispatch (visitor type determines which visitDeal() is called):
  ReportVisitor.visitDeal(deal):
    // Report-specific logic for deals
    ↓
Result: Behavior determined by BOTH element type and visitor type
```

This eliminates the need for `instanceof` checks or type casting.

## File Structure

```
visitor/
├── VisitorStructure.java        # Generic Visitor pattern structure
├── VisitorImplementation.java   # Commission system implementation
├── VisitorUsage.java             # Comprehensive usage examples
├── visitor-pattern.puml          # UML class diagram
└── README.md                     # This file
```

## Running the Examples

### Run Generic Pattern Structure
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.visitor.VisitorStructure"
```

**Output shows:**
- 3 different visitors (Display, Validation, Export) operating on 3 element types
- Double dispatch mechanism in action
- State accumulation across visits

### Run Commission System Implementation
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.visitor.VisitorImplementation"
```

**Output shows:**
- 4 visitors operating on commission entities
- Report generation with aggregated data
- Validation with error collection
- Statistics with KPI calculations
- CSV export transformation

### Run Comprehensive Usage Examples
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.visitor.VisitorUsage"
```

**Output shows:**
- 6 detailed scenarios
- Multiple visitors on same data
- Conditional/filtered visiting
- Custom visitor creation
- Data transformation
- With vs without pattern comparison

## Usage Examples

### Example 1: Basic Report Generation

```java
// Create entities
List<CommissionEntity> entities = new ArrayList<>();
entities.add(new CommissionDeal(someDeal));
entities.add(new UserEntity(someUser));

// Create visitor
ReportVisitor visitor = new ReportVisitor();

// Visit all entities
for (CommissionEntity entity : entities) {
    entity.accept(visitor);
}

// Get result
System.out.println(visitor.getReport());
```

### Example 2: Validation Before Processing

```java
List<CommissionEntity> entities = loadEntities();

// Validate first
ValidationVisitor validator = new ValidationVisitor();
for (CommissionEntity entity : entities) {
    entity.accept(validator);
}

if (validator.isValid()) {
    // Proceed with processing
    processEntities(entities);
} else {
    // Handle errors
    System.out.println("Errors: " + validator.getErrors());
}
```

### Example 3: Multiple Operations in Sequence

```java
List<CommissionEntity> entities = loadEntities();

// Operation 1: Validate
ValidationVisitor validator = new ValidationVisitor();
entities.forEach(e -> e.accept(validator));

// Operation 2: Collect statistics (if valid)
if (validator.isValid()) {
    StatisticsVisitor stats = new StatisticsVisitor();
    entities.forEach(e -> e.accept(stats));

    System.out.println("Total value: " + stats.getTotalDealValue());
}

// Operation 3: Export
CsvExportVisitor csv = new CsvExportVisitor();
entities.forEach(e -> e.accept(csv));
saveToFile(csv.getCsvData());
```

### Example 4: Conditional Visiting

```java
List<CommissionEntity> all = loadAllEntities();

// Only visit high-value won deals
StatisticsVisitor stats = new StatisticsVisitor();
for (CommissionEntity entity : all) {
    if (entity instanceof CommissionDeal) {
        CommissionDeal deal = (CommissionDeal) entity;
        if (deal.getStatus() == DealStatus.WON &&
            deal.getValue().compareTo(new BigDecimal("100000")) > 0) {
            entity.accept(stats);
        }
    }
}
```

### Example 5: Custom Visitor

```java
// Create a custom visitor for specific business logic
class QuarterEndCommissionVisitor implements CommissionEntityVisitor {
    private BigDecimal totalCommission = BigDecimal.ZERO;

    @Override
    public void visitDeal(CommissionDeal deal) {
        if (deal.getStatus() == DealStatus.WON &&
            isQuarterEnd(deal.getCloseDate())) {
            // Apply bonus rate for quarter-end deals
            BigDecimal commission = deal.getValue()
                .multiply(new BigDecimal("0.12")); // 12% vs normal 10%
            totalCommission = totalCommission.add(commission);
        }
    }

    // Other visit methods...
}

// Use it
QuarterEndCommissionVisitor qeVisitor = new QuarterEndCommissionVisitor();
deals.forEach(deal -> deal.accept(qeVisitor));
```

## Design Principles Applied

### Single Responsibility Principle (SRP)
- Each visitor has one responsibility (one operation)
- Domain models have one responsibility (business logic)
- No mixing of concerns

### Open/Closed Principle (OCP)
- **Open for extension**: Add new visitors without modifying elements
- **Closed for modification**: Element classes remain unchanged

### Dependency Inversion Principle (DIP)
- Visitors depend on `CommissionEntity` interface
- Elements depend on `CommissionEntityVisitor` interface
- Both depend on abstractions, not concretions

### Interface Segregation Principle (ISP)
- Each visitor implements only what it needs
- Elements expose clean, focused interfaces

### Liskov Substitution Principle (LSP)
- Any visitor can be substituted for another
- Any element can be substituted for another

## When to Use Visitor Pattern

✅ **Use Visitor when:**
- Object structure is stable (classes rarely change)
- You need to perform many distinct operations on objects
- Operations are unrelated to the objects' primary responsibility
- You want to gather related operations in one place
- Operations need to work across a class hierarchy
- You want to avoid polluting domain models with various operations

❌ **Don't use Visitor when:**
- Object structure changes frequently (adding new element types is hard)
- You have only one or two operations
- Operations are core to the objects' responsibility
- Performance is critical (double dispatch has overhead)
- Element classes are simple and few

## Advantages and Disadvantages

### Advantages ✅

1. **Easy to add new operations**
   - Just create a new visitor class
   - No modification to existing code

2. **Grouped related operations**
   - All report logic in one place
   - All validation logic in one place

3. **Clean domain models**
   - Keep domain objects focused on business logic
   - Separate operational concerns

4. **Stateful operations**
   - Visitors can accumulate state across visits
   - Perfect for aggregations, statistics, transformations

5. **Type-safe**
   - No `instanceof` checks needed
   - Compile-time type safety via double dispatch

### Disadvantages ❌

1. **Hard to add new element types**
   - Requires modifying visitor interface
   - All visitor implementations must be updated

2. **Breaks encapsulation**
   - Visitors may need access to element internals
   - Elements must expose more data than ideal

3. **Circular dependency**
   - Elements depend on Visitor
   - Visitors depend on Elements

4. **More complex**
   - Double dispatch is not intuitive
   - More classes to maintain

## Testing Strategy

### Unit Testing Visitors

```java
@Test
void testReportVisitorDealCount() {
    ReportVisitor visitor = new ReportVisitor();

    // Visit multiple deals
    visitor.visitDeal(createTestDeal("Deal 1", new BigDecimal("1000")));
    visitor.visitDeal(createTestDeal("Deal 2", new BigDecimal("2000")));

    String report = visitor.getReport();

    // Verify report contains both deals
    assertTrue(report.contains("Deal 1"));
    assertTrue(report.contains("Deal 2"));
    assertTrue(report.contains("Total Deals: 2"));
}
```

### Testing Validation Logic

```java
@Test
void testValidationVisitorDetectsErrors() {
    ValidationVisitor visitor = new ValidationVisitor();

    // Visit invalid deal (negative value)
    Deal invalidDeal = new Deal("Invalid", new BigDecimal("-1000"), "REP-1");
    CommissionDeal deal = new CommissionDeal(invalidDeal);

    deal.accept(visitor);

    assertFalse(visitor.isValid());
    assertFalse(visitor.getErrors().isEmpty());
}
```

### Integration Testing

```java
@Test
void testMultipleVisitorsOnSameData() {
    List<CommissionEntity> entities = createTestEntities();

    // Apply multiple visitors
    ReportVisitor report = new ReportVisitor();
    ValidationVisitor validation = new ValidationVisitor();
    StatisticsVisitor stats = new StatisticsVisitor();

    for (CommissionEntity entity : entities) {
        entity.accept(report);
        entity.accept(validation);
        entity.accept(stats);
    }

    // All should process same entities
    assertTrue(validation.isValid());
    assertTrue(stats.getTotalDealValue().compareTo(BigDecimal.ZERO) > 0);
    assertFalse(report.getReport().isEmpty());
}
```

## Real-World Applications

The Visitor pattern is useful for:

1. **Compiler Design**
   - Abstract Syntax Tree (AST) traversal
   - Type checking, code generation, optimization
   - Each operation is a visitor

2. **Document Processing**
   - Rendering to different formats (PDF, HTML, Plain Text)
   - Spell checking, word counting, indexing
   - Each operation is a visitor

3. **Financial Systems**
   - Tax calculations on different transaction types
   - Reporting on various financial instruments
   - Compliance checks across diverse entities

4. **Game Development**
   - Rendering different game objects
   - Collision detection across object types
   - AI behavior for different entities

5. **Data Export/Import**
   - Export to various formats (JSON, XML, CSV)
   - Data validation and transformation
   - Schema migration

## Common Pitfalls to Avoid

### ❌ Don't: Use instanceof in visitor methods

```java
// BAD - defeats the purpose of double dispatch
public void visitDeal(CommissionDeal deal) {
    if (deal instanceof SpecialDeal) {
        // Special handling
    }
}
```

### ✅ Do: Use proper visitor methods

```java
// GOOD - create separate visit method
public void visitSpecialDeal(SpecialDeal deal) {
    // Special handling
}
```

### ❌ Don't: Modify element state in visitors

```java
// BAD - visitors should observe, not mutate
public void visitDeal(CommissionDeal deal) {
    deal.getDeal().setValue(newValue);  // Don't do this!
}
```

### ✅ Do: Keep visitors read-only

```java
// GOOD - visitors read and produce output
public void visitDeal(CommissionDeal deal) {
    BigDecimal value = deal.getValue();
    // Process value, generate output
}
```

### ❌ Don't: Put business logic in visitors

```java
// BAD - business logic belongs in domain models
public void visitDeal(CommissionDeal deal) {
    deal.calculateTotalValue();  // This should be in Deal
}
```

### ✅ Do: Put operational logic in visitors

```java
// GOOD - operational concerns in visitor
public void visitDeal(CommissionDeal deal) {
    BigDecimal value = deal.getValue();  // Deal already knows its value
    generateReportLine(value);  // Reporting is visitor's job
}
```

## Visitor vs Other Patterns

### Visitor vs Strategy
- **Strategy**: Encapsulates algorithm, swapped at runtime
- **Visitor**: Separates operation from structure, works across type hierarchy
- **Use Strategy when**: Algorithm varies, object structure is simple
- **Use Visitor when**: Many operations, complex object structure

### Visitor vs Command
- **Command**: Encapsulates request as object
- **Visitor**: Encapsulates operation on object structure
- Both can accumulate state, but different purposes

### Visitor vs Iterator
- **Iterator**: Traverses collection, access elements
- **Visitor**: Performs operations on elements
- Often used together: Iterator for traversal, Visitor for operation

## Related Patterns

- **Composite Pattern**: Visitor often used with Composite to perform operations on tree structures
- **Iterator Pattern**: Can be used together - Iterator for traversal, Visitor for operations
- **Interpreter Pattern**: AST nodes can use Visitor for interpretation

## Further Learning

To deepen understanding:

1. Run all three demo files and study the output
2. Create a new visitor (e.g., `AuditLogVisitor` that tracks all entity access)
3. Add a new element type (e.g., `CommissionAdjustment`) and see what needs to change
4. Implement a visitor that builds a composite data structure
5. Try using Visitor with the Composite pattern (e.g., tree of entities)
6. Compare with Strategy pattern for same use case

## References

- **Design Patterns: Elements of Reusable Object-Oriented Software** - Gang of Four (pages 331-344)
- **Head First Design Patterns** - Freeman & Freeman (Chapter 11: Visitor)
- **Refactoring: Improving the Design of Existing Code** - Martin Fowler (Replace Conditional with Polymorphism)
- **Pattern-Oriented Software Architecture** - Buschmann et al.