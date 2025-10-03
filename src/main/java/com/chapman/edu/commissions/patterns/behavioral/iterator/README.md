# Iterator Pattern Implementation

## Overview

The **Iterator Pattern** is a behavioral design pattern that provides a way to access elements of an aggregate object sequentially without exposing its underlying representation. It decouples the traversal algorithm from the collection structure, enabling uniform access to different data structures.

This implementation demonstrates the Iterator pattern through **Commission Data Traversal**, showing how to implement specialized iterators for filtering, sorting, and processing commission-related data without exposing internal collection structures.

## Pattern Components

### 1. Iterator Interface (`CommissionIterator<T>`)
Defines the interface for traversing elements:
```java
interface CommissionIterator<T> {
    boolean hasNext();        // Check if more elements exist
    T next();                 // Get next element
    int remaining();          // How many items left
    void reset();             // Reset to beginning
}
```

### 2. Concrete Iterators
Implement specific traversal strategies:

| Iterator | Purpose | Filtering Logic |
|----------|---------|----------------|
| **AllDealsIterator** | Forward traversal | None (all deals) |
| **StatusFilterIterator** | Filter by deal status | `deal.getStatus() == targetStatus` |
| **ValueRangeIterator** | Filter by value range | `value >= min && value <= max` |
| **DateFilterIterator** | Filter by date | `closeDate.isAfter(targetDate)` |
| **ValueSortedIterator** | Sorted by value | Creates sorted copy, descending |
| **ActivePlansIterator** | Active plans only | `plan.getStatus() == ACTIVE` |
| **EffectiveDateIterator** | Effective on date | `plan.isActiveOn(date)` |

### 3. Aggregate (`DealCollection`, `CommissionPlanRepository`)
Collections that provide iterator factory methods:
- Stores the actual data
- Provides methods to create different iterator types
- Hides internal structure from clients

### 4. Client Code
Uses iterators without knowing collection internals:
```java
CommissionIterator<Deal> iterator = deals.createStatusIterator(DealStatus.WON);
while (iterator.hasNext()) {
    Deal deal = iterator.next();
    // Process deal
}
```

## Problem Solved

### Without Iterator ❌

```java
// Tight coupling to collection structure
public class DealProcessor {
    public void processWonDeals(DealCollection deals) {
        // Client must know internal structure!
        List<Deal> internalList = deals.getInternalList();

        for (int i = 0; i < internalList.size(); i++) {
            Deal deal = internalList.get(i);
            if (deal.getStatus() == DealStatus.WON) {
                process(deal);
            }
        }
    }

    public void processHighValueDeals(DealCollection deals) {
        // Duplicate filtering logic everywhere!
        List<Deal> internalList = deals.getInternalList();

        for (int i = 0; i < internalList.size(); i++) {
            Deal deal = internalList.get(i);
            if (deal.getValue().compareTo(new BigDecimal("50000")) > 0) {
                process(deal);
            }
        }
    }
}

// Can't change collection implementation without breaking clients
// Multiple simultaneous traversals are difficult
// Filtering logic scattered across codebase
```

**Problems:**
- 🔴 Exposes collection internals (violates encapsulation)
- 🔴 Tight coupling to specific collection type
- 🔴 Filtering logic duplicated everywhere
- 🔴 Hard to change collection implementation
- 🔴 Difficult to support multiple simultaneous traversals
- 🔴 Client code cluttered with traversal logic

### With Iterator ✅

```java
// Encapsulated traversal
public class DealProcessor {
    public void processWonDeals(DealCollection deals) {
        // Clean, encapsulated iteration
        CommissionIterator<Deal> iterator =
            deals.createStatusIterator(DealStatus.WON);

        while (iterator.hasNext()) {
            process(iterator.next());
        }
    }

    public void processHighValueDeals(DealCollection deals) {
        // Filtering logic in iterator
        CommissionIterator<Deal> iterator =
            deals.createValueRangeIterator(
                new BigDecimal("50000"),
                new BigDecimal("1000000")
            );

        while (iterator.hasNext()) {
            process(iterator.next());
        }
    }
}

// Collection structure completely hidden
// Can change implementation freely
// Multiple iterators work simultaneously
// Filtering logic centralized in iterators
```

**Benefits:**
- ✅ Encapsulation preserved (internals hidden)
- ✅ Loose coupling (uniform interface)
- ✅ Centralized filtering logic
- ✅ Easy to change collection implementation
- ✅ Multiple simultaneous traversals supported
- ✅ Clean, focused client code

## Traversal Strategies Comparison

```
Without Iterator (Direct Access):
┌─────────────┐
│   Client    │
└──────┬──────┘
       │ Knows internal structure
       │ Direct array/list access
       ▼
┌─────────────────────────┐
│   DealCollection        │
│  - List<Deal> deals ←───┼─── Exposed!
│  + getInternalList()    │
└─────────────────────────┘

Problems:
• Client coupled to List implementation
• Can't change to array, tree, etc.
• Filtering logic in client
• Hard to add new traversal types

With Iterator (Encapsulated):
┌─────────────┐
│   Client    │
└──────┬──────┘
       │ Uses uniform interface
       │ hasNext() / next()
       ▼
┌─────────────────────────┐
│  CommissionIterator<T>  │ ← Interface
└─────────────────────────┘
       △
       │ Implements
       │
┌──────┴──────────┬─────────────┬──────────────┐
│  StatusFilter   │ ValueRange  │ DateFilter   │
│   Iterator      │  Iterator   │  Iterator    │
└─────────────────┴─────────────┴──────────────┘
       │                │              │
       └────────────────┴──────────────┘
                        │
                        ▼
                ┌─────────────────┐
                │ DealCollection  │
                │ - deals (hidden)│
                └─────────────────┘

Benefits:
• Client independent of structure
• Easy to add new iterator types
• Filtering logic in iterators
• Multiple traversals supported
```

## File Structure

```
iterator/
├── IteratorStructure.java           # Generic Iterator pattern structure
├── IteratorImplementation.java      # Commission data iterators
├── IteratorUsage.java               # Comprehensive usage examples
├── iterator-pattern.puml            # UML class diagram
├── README.md                        # This file
├── QUESTIONS.md                     # Concept questions
└── ANSWERS.md                       # Question answers
```

## Running the Examples

### Run Generic Pattern Structure
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.iterator.IteratorStructure"
```

**Output shows:**
- Basic forward iteration
- Multiple simultaneous iterators
- Different iterator types (forward, reverse, skip)
- Filtered iteration with conditions

### Run Commission Data Implementation
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.iterator.IteratorImplementation"
```

**Output shows:**
- Deal collection with multiple iterator types
- Status, value range, date filtering
- Sorted iteration
- Commission plan iterators

### Run Comprehensive Usage Examples
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.behavioral.iterator.IteratorUsage"
```

**Output shows:**
- 6 detailed scenarios
- Report generation with filters
- Pipeline processing
- Pagination
- Concurrent iteration
- Iterator reset and reuse
- Custom iteration strategies

## Usage Examples

### Example 1: Basic Iteration

```java
DealCollection deals = new DealCollection();
deals.addDeal(deal1);
deals.addDeal(deal2);
deals.addDeal(deal3);

// Create iterator
CommissionIterator<Deal> iterator = deals.createIterator();

// Traverse
while (iterator.hasNext()) {
    Deal deal = iterator.next();
    System.out.println(deal.getTitle());
}
```

### Example 2: Filtered Iteration

```java
// Only iterate won deals
CommissionIterator<Deal> wonDeals =
    deals.createStatusIterator(DealStatus.WON);

while (wonDeals.hasNext()) {
    Deal deal = wonDeals.next();
    processWonDeal(deal);
}

// Only iterate high-value deals
CommissionIterator<Deal> highValue =
    deals.createValueRangeIterator(
        new BigDecimal("50000"),
        new BigDecimal("1000000")
    );

while (highValue.hasNext()) {
    Deal deal = highValue.next();
    processHighValueDeal(deal);
}
```

### Example 3: Multiple Simultaneous Iterators

```java
// Create multiple independent iterators
CommissionIterator<Deal> iter1 = deals.createStatusIterator(DealStatus.WON);
CommissionIterator<Deal> iter2 = deals.createStatusIterator(DealStatus.OPEN);
CommissionIterator<Deal> iter3 = deals.createStatusIterator(DealStatus.LOST);

// Each maintains independent position
while (iter1.hasNext() || iter2.hasNext() || iter3.hasNext()) {
    if (iter1.hasNext()) processWon(iter1.next());
    if (iter2.hasNext()) processOpen(iter2.next());
    if (iter3.hasNext()) processLost(iter3.next());
}
```

### Example 4: Iterator with Reset

```java
CommissionIterator<Deal> iterator = deals.createIterator();

// First pass: calculate total
BigDecimal total = BigDecimal.ZERO;
while (iterator.hasNext()) {
    total = total.add(iterator.next().getValue());
}

// Reset iterator
iterator.reset();

// Second pass: process each deal
while (iterator.hasNext()) {
    Deal deal = iterator.next();
    processDeal(deal, total);
}
```

### Example 5: Pagination

```java
CommissionIterator<Deal> iterator = deals.createIterator();
int pageSize = 10;
int page = 1;

while (iterator.hasNext()) {
    System.out.println("=== Page " + page + " ===");

    for (int i = 0; i < pageSize && iterator.hasNext(); i++) {
        Deal deal = iterator.next();
        displayDeal(deal);
    }

    System.out.println("Remaining: " + iterator.remaining());
    page++;
}
```

### Example 6: Composite Iterator

```java
// Combine multiple collections
DealCollection collection1 = getCollection1();
DealCollection collection2 = getCollection2();

List<CommissionIterator<Deal>> iterators = Arrays.asList(
    collection1.createIterator(),
    collection2.createIterator()
);

CompositeIterator<Deal> composite = new CompositeIterator<>(iterators);

// Iterate across all collections seamlessly
while (composite.hasNext()) {
    Deal deal = composite.next();
    processDeal(deal);
}
```

## Design Principles Applied

### Single Responsibility Principle (SRP)
- **Collection**: Stores data
- **Iterator**: Handles traversal
- **Filter logic**: Encapsulated in specific iterator

### Open/Closed Principle (OCP)
- **Open for extension**: Add new iterator types without modifying collection
- **Closed for modification**: Existing iterators unchanged

### Dependency Inversion Principle (DIP)
- Client depends on Iterator interface
- Concrete iterators are pluggable implementations

### Interface Segregation Principle (ISP)
- Minimal iterator interface (hasNext, next)
- Additional methods (remaining, reset) as needed

### Encapsulation
- Collection internal structure hidden
- Iterator provides controlled access
- Filtering logic not exposed to client

## When to Use Iterator

✅ **Use Iterator when:**
- Need to traverse collection without exposing structure
- Support multiple traversal strategies
- Want uniform interface for different collections
- Need multiple simultaneous traversals
- Implementing filtering or transformation logic
- Building data processing pipelines
- Implementing pagination or lazy loading

❌ **Don't use Iterator when:**
- Simple array loop is sufficient
- Collection has only one traversal method
- Direct index access is required
- Performance is critical (iterator has small overhead)
- Collection is very simple (e.g., single element)

## Advantages and Disadvantages

### Advantages ✅

1. **Encapsulation**
   - Collection structure hidden
   - Can change implementation freely
   - Internal details not exposed

2. **Uniform Interface**
   - All collections traversed same way
   - Polymorphic iteration
   - Client code independent of collection type

3. **Multiple Traversals**
   - Multiple iterators simultaneously
   - Each maintains independent position
   - No interference between iterators

4. **Separation of Concerns**
   - Collection: stores data
   - Iterator: handles traversal
   - Client: processes data

5. **Flexible Strategies**
   - Different iteration orders
   - Filtering during iteration
   - Transformation pipelines

6. **Easy Extension**
   - Add new iterator types easily
   - No changes to existing code
   - Compose iterators

### Disadvantages ❌

1. **Overhead**
   - Extra objects created
   - Method call overhead
   - Slightly slower than direct access

2. **Complexity**
   - More classes to maintain
   - Interface to implement
   - Factory methods needed

3. **State Management**
   - Iterator tracks position
   - Must handle concurrent modification
   - Reset logic may be complex

4. **Limited Operations**
   - Only forward traversal (usually)
   - No random access
   - Remove operation optional

## Testing Strategy

### Unit Testing Iterators

```java
@Test
void testBasicIteration() {
    DealCollection deals = createTestDeals();
    CommissionIterator<Deal> iterator = deals.createIterator();

    int count = 0;
    while (iterator.hasNext()) {
        Deal deal = iterator.next();
        assertNotNull(deal);
        count++;
    }

    assertEquals(deals.size(), count);
}

@Test
void testFilteredIteration() {
    DealCollection deals = createTestDeals();
    CommissionIterator<Deal> wonDeals =
        deals.createStatusIterator(DealStatus.WON);

    while (wonDeals.hasNext()) {
        Deal deal = wonDeals.next();
        assertEquals(DealStatus.WON, deal.getStatus());
    }
}

@Test
void testIteratorReset() {
    DealCollection deals = createTestDeals();
    CommissionIterator<Deal> iterator = deals.createIterator();

    // First pass
    int count1 = 0;
    while (iterator.hasNext()) {
        iterator.next();
        count1++;
    }

    // Reset and second pass
    iterator.reset();
    int count2 = 0;
    while (iterator.hasNext()) {
        iterator.next();
        count2++;
    }

    assertEquals(count1, count2);
}

@Test
void testMultipleSimultaneousIterators() {
    DealCollection deals = createTestDeals();

    CommissionIterator<Deal> iter1 = deals.createIterator();
    CommissionIterator<Deal> iter2 = deals.createIterator();

    // Advance iter1
    iter1.next();
    iter1.next();

    // iter2 should still be at beginning
    Deal first = iter2.next();
    Deal dealFromCollection = deals.createIterator().next();

    assertEquals(dealFromCollection.getId(), first.getId());
}

@Test
void testEmptyIteration() {
    DealCollection empty = new DealCollection();
    CommissionIterator<Deal> iterator = empty.createIterator();

    assertFalse(iterator.hasNext());
    assertThrows(NoSuchElementException.class, () -> iterator.next());
}
```

## Real-World Applications

The Iterator pattern is useful for:

1. **Database Result Sets**
   - JDBC ResultSet is an iterator
   - Traverse query results
   - Lazy loading of data

2. **File System Traversal**
   - Directory iterators
   - File filtering
   - Recursive traversal

3. **Collections Framework**
   - Java's Iterator interface
   - For-each loops
   - Stream API

4. **UI Components**
   - Tree node traversal
   - Table row iteration
   - Menu item navigation

5. **Report Generation**
   - Data filtering
   - Aggregation
   - Multi-pass processing

6. **Batch Processing**
   - Process subsets of data
   - Pagination
   - Chunked processing

## Common Pitfalls to Avoid

### ❌ Don't: Modify Collection During Iteration

```java
// BAD - concurrent modification
CommissionIterator<Deal> iterator = deals.createIterator();
while (iterator.hasNext()) {
    Deal deal = iterator.next();
    if (shouldRemove(deal)) {
        deals.removeDeal(deal);  // Breaks iterator!
    }
}
```

### ✅ Do: Use Iterator Remove or Collect Items

```java
// GOOD - collect items to remove
CommissionIterator<Deal> iterator = deals.createIterator();
List<Deal> toRemove = new ArrayList<>();

while (iterator.hasNext()) {
    Deal deal = iterator.next();
    if (shouldRemove(deal)) {
        toRemove.add(deal);
    }
}

// Remove after iteration
toRemove.forEach(deals::removeDeal);
```

### ❌ Don't: Call next() Without Checking hasNext()

```java
// BAD - may throw exception
CommissionIterator<Deal> iterator = deals.createIterator();
Deal deal1 = iterator.next();
Deal deal2 = iterator.next();  // What if only 1 deal?
```

### ✅ Do: Always Check hasNext()

```java
// GOOD - safe iteration
CommissionIterator<Deal> iterator = deals.createIterator();
if (iterator.hasNext()) {
    Deal deal1 = iterator.next();
}
if (iterator.hasNext()) {
    Deal deal2 = iterator.next();
}
```

### ❌ Don't: Expose Collection in Iterator

```java
// BAD - breaks encapsulation
public class BadIterator implements CommissionIterator<Deal> {
    private List<Deal> deals;  // Reference to original!

    public List<Deal> getDeals() {  // Exposes collection!
        return deals;
    }
}
```

### ✅ Do: Keep Collection Reference Private

```java
// GOOD - encapsulated
public class GoodIterator implements CommissionIterator<Deal> {
    private final List<Deal> deals;  // Private
    private int currentIndex = 0;

    // No getter for deals - only iterator methods
}
```

## Iterator vs Other Patterns

### Iterator vs Visitor
- **Iterator**: Traverses collection
- **Visitor**: Performs operations on elements
- Often used together: Iterator for traversal, Visitor for operations

### Iterator vs Composite
- **Composite**: Tree structure
- **Iterator**: Traverses composite structure
- Iterator can traverse composite trees uniformly

### Iterator vs Strategy
- **Strategy**: Encapsulates algorithms
- **Iterator**: Encapsulates traversal
- Similar structure, different purpose

## Related Patterns

- **Composite Pattern**: Iterator can traverse composite structures
- **Factory Method**: Iterator factory methods in collections
- **Memento Pattern**: Can use iterator to capture collection state

## Performance Considerations

### 1. Lazy Evaluation
Filtered iterators compute next element on demand:
```java
// Only finds next match when hasNext() called
private Deal findNext() {
    while (currentIndex < deals.size()) {
        Deal deal = deals.get(currentIndex++);
        if (matches(deal)) return deal;
    }
    return null;
}
```

### 2. Snapshot vs Live Iteration
- **Snapshot**: Iterator works on copy (safe from modifications)
- **Live**: Iterator works on original (faster but risky)

### 3. Caching
Cache expensive operations in iterator:
```java
public class CachingIterator implements CommissionIterator<Deal> {
    private Deal nextDeal = null;
    private boolean nextSet = false;  // Cache flag

    public boolean hasNext() {
        if (!nextSet) {
            nextDeal = findNext();  // Expensive
            nextSet = true;          // Cache result
        }
        return nextDeal != null;
    }
}
```

## Further Learning

To deepen understanding:

1. Run all three demo files and study output
2. Implement bidirectional iterator (hasNext, hasPrevious, next, previous)
3. Add iterator with remove() support
4. Create iterator that skips every Nth element
5. Implement iterator composition (chain multiple iterators)
6. Add snapshot iterator (works on copy of collection)
7. Implement parallel iterator for concurrent processing
8. Create iterator with transformation (map operation)

## References

- **Design Patterns: Elements of Reusable Object-Oriented Software** - Gang of Four (pages 257-271)
- **Head First Design Patterns** - Freeman & Freeman
- **Effective Java** - Joshua Bloch (Item 58: Prefer for-each loops to traditional for loops)
- **Java Collections Framework** - Oracle Documentation