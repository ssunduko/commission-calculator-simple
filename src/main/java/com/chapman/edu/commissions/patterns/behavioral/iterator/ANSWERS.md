# Iterator Pattern - Answers to Concept Questions

## Basic Understanding

### Answer 1: Pattern Purpose
**Purpose**: The Iterator pattern provides a way to access elements of an aggregate object sequentially without exposing its underlying representation.

**Problem it solves**:
- **Encapsulation**: Clients need to traverse collections without knowing internal structure (array, list, tree, etc.)
- **Multiple Traversals**: Need to support multiple simultaneous traversals of same collection
- **Uniform Interface**: Want consistent way to traverse different collection types
- **Separation of Concerns**: Collection stores data, iterator handles traversal

**Example**: Instead of exposing `List<Deal> deals` and letting clients access via index, DealCollection provides iterators that hide whether deals are stored in a List, Array, or custom structure.

### Answer 2: Core Components
**1. Iterator (Interface)**:
   - Defines traversal interface: `hasNext()`, `next()`, `reset()`
   - Provides uniform access method for all collection types
   - Example: `CommissionIterator<T>`

**2. ConcreteIterator (Implementation)**:
   - Implements specific traversal strategy
   - Maintains current position in traversal
   - Encapsulates filtering/sorting logic
   - Example: `StatusFilterIterator`, `ValueRangeIterator`

**3. Aggregate (Interface/Abstract)**:
   - Defines interface for creating iterators
   - Factory method pattern for iterator creation
   - Example: Collections with `createIterator()` methods

**4. ConcreteAggregate**:
   - Implements aggregate interface
   - Stores actual collection data
   - Returns appropriate concrete iterators
   - Example: `DealCollection`, `CommissionPlanRepository`

### Answer 3: Encapsulation
**How Iterator preserves encapsulation**:
- Collection's internal structure is **completely hidden**
- Iterator provides **controlled access** through standard interface
- Can change internal structure (List → Array → Tree) **without affecting clients**
- Only iterator knows how to traverse internal structure

**Without iterators, what's exposed**:
```java
// BAD - exposes internals
public List<Deal> getDeals() {
    return deals;  // Clients know it's a List!
}

// Client code becomes coupled
List<Deal> dealList = collection.getDeals();
for (int i = 0; i < dealList.size(); i++) {
    // Coupled to List interface
}
```

**With iterators**:
```java
// GOOD - internals hidden
public CommissionIterator<Deal> createIterator() {
    return new AllDealsIterator();  // Returns iterator
}

// Client doesn't know or care about internal structure
CommissionIterator<Deal> it = collection.createIterator();
while (it.hasNext()) {
    Deal deal = it.next();  // Uniform interface
}
```

## Pattern Mechanics

### Answer 4: Iterator Interface
**Why minimal interface**:
- **Simplicity**: Easy to understand and implement
- **Universality**: Works for all collection types (even ones without random access)
- **Focus**: Single responsibility - sequential traversal only
- **Flexibility**: Can be extended with optional methods (remaining, reset) as needed

**Calling next() without hasNext()**:
```java
CommissionIterator<Deal> it = deals.createIterator();
Deal d1 = it.next();  // OK if elements exist
Deal d2 = it.next();  // May throw NoSuchElementException!
Deal d3 = it.next();  // ERROR if only 2 elements
```

**Throws `NoSuchElementException`** - This is intentional:
- **Fail-fast**: Immediately indicates programming error
- **Clear contract**: Always check `hasNext()` first
- **Defensive programming**: Better than returning null

**Correct usage**:
```java
while (it.hasNext()) {  // Always check first
    Deal deal = it.next();  // Safe to call
}
```

### Answer 5: Multiple Traversals
**How it supports multiple traversals**:
- Each iterator maintains **independent state** (separate position)
- Iterators don't interfere with each other
- Can create multiple iterators from same collection

**Example from commission system**:
```java
DealCollection deals = new DealCollection();
// ... add deals ...

// Three independent iterators
CommissionIterator<Deal> wonDeals = deals.createStatusIterator(DealStatus.WON);
CommissionIterator<Deal> openDeals = deals.createStatusIterator(DealStatus.OPEN);
CommissionIterator<Deal> allDeals = deals.createIterator();

// Each maintains separate position
wonDeals.next();   // Advances only wonDeals iterator
wonDeals.next();

openDeals.next();  // Advances only openDeals iterator

// allDeals is still at beginning
Deal first = allDeals.next();  // Gets first deal overall
```

**Use case**: Generating multiple reports simultaneously without conflicts.

### Answer 6: Iterator State
**State an iterator maintains**:
1. **Current Position**: Index or reference to current element
2. **Collection Reference**: Reference to collection being traversed
3. **Cached Next Element** (for filtered iterators): Pre-fetched next matching element
4. **Cache Flag**: Whether cached element is valid

**Example from StatusFilterIterator**:
```java
private int currentIndex = 0;           // Position
private final DealStatus targetStatus;  // Filter criteria
private Deal nextDeal = null;           // Cached next
private boolean nextSet = false;        // Cache validity
```

**Why important**:
- **Independent State**: Each iterator has its own state → multiple simultaneous traversals
- **Encapsulation**: State is private to iterator → collection structure hidden
- **Efficiency**: Caching avoids redundant searches
- **Correctness**: Cache flag prevents returning same element twice

**Where stored**: Inside iterator object (not in collection), ensuring independence.

## Implementation Details

### Answer 7: Filtered Iterators
**How StatusFilterIterator works**:

```java
private class StatusFilterIterator implements CommissionIterator<Deal> {
    private int currentIndex = 0;        // Current search position
    private Deal nextDeal = null;        // Cached next matching deal
    private boolean nextSet = false;     // Is cache valid?

    @Override
    public boolean hasNext() {
        if (!nextSet) {              // Cache miss
            nextDeal = findNext();   // Search for next match
            nextSet = true;          // Mark cache valid
        }
        return nextDeal != null;     // Return cache validity
    }

    @Override
    public Deal next() {
        if (!hasNext()) throw new NoSuchElementException();
        Deal result = nextDeal;      // Return cached element
        nextDeal = null;             // Invalidate cache
        nextSet = false;
        return result;
    }

    private Deal findNext() {
        while (currentIndex < deals.size()) {
            Deal deal = deals.get(currentIndex++);
            if (deal.getStatus() == targetStatus) {
                return deal;  // Found match
            }
        }
        return null;  // No more matches
    }
}
```

**Why nextDeal and nextSet are needed**:

**nextDeal**:
- Caches the next matching element
- Avoids re-searching when client calls `hasNext()` multiple times
- Stores result of expensive `findNext()` operation

**nextSet**:
- Indicates whether cached value is valid
- Prevents returning same element twice
- Distinguishes between "not searched yet" and "searched, no match found"

**Purpose of findNext()**:
- Encapsulates search logic (single responsibility)
- Scans collection from `currentIndex` until match found
- Returns `null` when no more matches (signals end)
- Advances `currentIndex` as it searches

**Flow**:
1. Client calls `hasNext()`
2. If cache invalid (`!nextSet`), call `findNext()`
3. `findNext()` scans until match found or end reached
4. Cache result and mark valid
5. Return whether match found
6. Client calls `next()` → return cached element and invalidate cache

### Answer 8: Lazy Evaluation
**Lazy Evaluation** means computing next element **on demand** rather than up front.

**In iterator context**:
- Element found only when `hasNext()` or `next()` called
- Don't pre-compute entire filtered list
- Process one element at a time

**Example**:
```java
// Lazy (Iterator approach)
CommissionIterator<Deal> won = deals.createStatusIterator(DealStatus.WON);
while (won.hasNext()) {
    Deal deal = won.next();  // Find next match NOW
    if (someCondition(deal)) break;  // May exit early
}

// Eager (Pre-filtered copy)
List<Deal> wonDeals = new ArrayList<>();
for (Deal d : allDeals) {
    if (d.getStatus() == DealStatus.WON) {
        wonDeals.add(d);  // Build entire list up front
    }
}
for (Deal deal : wonDeals) {
    if (someCondition(deal)) break;  // May exit early
}
```

**Performance Comparison**:

| Aspect | Lazy (Iterator) | Eager (Filtered Copy) |
|--------|----------------|----------------------|
| **Initial Cost** | None (O(1)) | Full scan (O(n)) |
| **Memory** | O(1) - just state | O(k) - all matches |
| **Early Exit** | Only processes needed | Processes all |
| **Best For** | Large collections, early exit | Small collections, multiple passes |

**When to use each**:
- **Lazy**: Large datasets, may not need all elements, single pass
- **Eager**: Small datasets, need multiple passes, need count before iteration

### Answer 9: Reset Functionality
**Why you want reset()**:
- **Multiple Passes**: Process same filtered data multiple times
- **Reusability**: Reuse same iterator without creating new one
- **Algorithm Needs**: Some algorithms require multiple passes (e.g., two-pass statistics)

**Use case from commission system**:

**Scenario**: Calculate average commission, then find deals above average.

```java
CommissionIterator<Deal> wonDeals = deals.createStatusIterator(DealStatus.WON);

// Pass 1: Calculate average
BigDecimal total = BigDecimal.ZERO;
int count = 0;
while (wonDeals.hasNext()) {
    total = total.add(wonDeals.next().getValue());
    count++;
}
BigDecimal average = total.divide(new BigDecimal(count));

// Reset iterator
wonDeals.reset();  // Back to beginning

// Pass 2: Find above-average deals
while (wonDeals.hasNext()) {
    Deal deal = wonDeals.next();
    if (deal.getValue().compareTo(average) > 0) {
        processAboveAverage(deal);
    }
}
```

**Benefits**:
- Don't need to create new iterator
- Same filtering logic applied
- More efficient than rebuilding iterator
- Clearer intent (same dataset, different pass)

## Design Decisions

### Answer 10: Snapshot vs. Live Iteration
**Snapshot Iteration**:
- Iterator works on **copy** of collection
- Modifications to original don't affect iteration
- Safe from concurrent modification
- Higher memory usage

**Live Iteration**:
- Iterator works on **original** collection
- Modifications can break iteration
- Lower memory usage
- Faster initialization

**ValueSortedIterator uses Snapshot**:
```java
private class ValueSortedIterator implements CommissionIterator<Deal> {
    private final List<Deal> sortedDeals;  // Copy!

    public ValueSortedIterator() {
        this.sortedDeals = new ArrayList<>(deals);  // Create copy
        this.sortedDeals.sort(...);  // Sort the copy
    }
}
```

**Why snapshot for sorted iterator**:
1. **Sorting requires copy** anyway (don't want to modify original)
2. **Deterministic order** (won't change during iteration)
3. **Safe concurrent access** (original can be modified)
4. **Clear semantics** (sorted view is independent)

**Trade-off**:
- ✅ Safe from modifications
- ✅ Stable sort order
- ❌ Extra memory (O(n))
- ❌ Slower initialization

**When to use each**:
- **Snapshot**: When sorting, when modifications expected, when need stable view
- **Live**: Simple traversals, large collections, single-threaded

### Answer 11: Iterator Factories
**Why multiple factory methods**:

**Approach in code**:
```java
public class DealCollection {
    // Multiple factories
    public CommissionIterator<Deal> createIterator();
    public CommissionIterator<Deal> createStatusIterator(DealStatus status);
    public CommissionIterator<Deal> createValueRangeIterator(BigDecimal min, BigDecimal max);
    public CommissionIterator<Deal> createDateIterator(LocalDate afterDate);
}
```

**Alternative (not used)**:
```java
// Single factory with parameters
public CommissionIterator<Deal> createIterator(
    FilterType type,
    Object... params
) {
    // Complex branching logic
}
```

**Advantages of multiple factories**:

1. **Type Safety**:
   ```java
   // Compile-time type checking
   createStatusIterator(DealStatus.WON);  // Type-safe

   // vs.
   createIterator(FilterType.STATUS, "WON");  // String! Type-unsafe
   ```

2. **Clear Intent**:
   ```java
   createStatusIterator(DealStatus.WON);  // Clear what it does

   // vs.
   createIterator(FilterType.STATUS, DealStatus.WON);  // More verbose
   ```

3. **Discoverability**:
   - IDE auto-complete shows all iterator types
   - Self-documenting API

4. **Flexibility**:
   - Each factory has appropriate parameters
   - Can add new factories without changing existing ones (OCP)

5. **Single Responsibility**:
   - Each factory method creates one iterator type
   - No complex branching logic

**Factory Method Pattern**: Each factory method is essentially a Factory Method, creating specific iterator types.

### Answer 12: Remove Operation
**Why remove() might be intentionally excluded**:

1. **Simplicity**:
   - Minimal interface is easier to implement
   - Not all iterators support removal
   - Optional operation adds complexity

2. **Collection Integrity**:
   - Removing during iteration can be dangerous
   - Modifying collection while iterating causes concurrent modification issues
   - Some collections don't support modification

3. **Filtered Iterators**:
   - What does "remove" mean for filtered iterator?
   - Should it remove from original collection or just skip?
   - Ambiguous semantics

4. **Alternative Pattern**:
   ```java
   // Better: collect items to remove, then remove after iteration
   List<Deal> toRemove = new ArrayList<>();
   while (iterator.hasNext()) {
       Deal deal = iterator.next();
       if (shouldRemove(deal)) {
           toRemove.add(deal);
       }
   }
   toRemove.forEach(collection::removeDeal);
   ```

**Challenges of implementing remove()**:

1. **State Management**:
   ```java
   public void remove() {
       if (lastReturnedIndex == -1) {
           throw new IllegalStateException("next() not called");
       }
       deals.remove(lastReturnedIndex);
       currentIndex--;  // Adjust position
       lastReturnedIndex = -1;  // Mark as removed
   }
   ```

2. **Multiple Calls**:
   - Can remove() be called twice for same element?
   - Must track whether current element already removed

3. **Filtered Iterators**:
   - Removing from sorted/filtered view affects underlying collection
   - Index management becomes complex

4. **Concurrent Modification**:
   - Iterator becomes invalid if collection modified externally
   - Need fail-fast checking

**When to include remove()**:
- Collection supports modification
- Clear semantics for filtered iterators
- Willing to accept added complexity
- Common use case in your application

## Advanced Concepts

### Answer 13: Composite Iterator
**How CompositeIterator works**:

```java
public class CompositeIterator<T> implements CommissionIterator<T> {
    private final List<CommissionIterator<T>> iterators;
    private int currentIteratorIndex = 0;

    public boolean hasNext() {
        // Skip exhausted iterators
        while (currentIteratorIndex < iterators.size()) {
            if (iterators.get(currentIteratorIndex).hasNext()) {
                return true;  // Found iterator with elements
            }
            currentIteratorIndex++;  // Move to next iterator
        }
        return false;  // All exhausted
    }

    public T next() {
        if (!hasNext()) throw new NoSuchElementException();
        return iterators.get(currentIteratorIndex).next();
    }
}
```

**Problem it solves**:
- **Unified Traversal**: Treat multiple collections as single collection
- **Data Federation**: Combine data from different sources
- **Seamless Transition**: Automatically move to next collection when current exhausted

**Example**:
```java
// Three separate collections
DealCollection q1Deals = getQ1Deals();
DealCollection q2Deals = getQ2Deals();
DealCollection q3Deals = getQ3Deals();

// Composite iterator
CompositeIterator<Deal> allYearDeals = new CompositeIterator<>(Arrays.asList(
    q1Deals.createIterator(),
    q2Deals.createIterator(),
    q3Deals.createIterator()
));

// Iterate as if single collection
while (allYearDeals.hasNext()) {
    Deal deal = allYearDeals.next();
    // Process deal (don't know which collection it came from)
}
```

**State Management**:
- `currentIteratorIndex`: Which iterator is active
- Automatically advances to next iterator when current exhausted
- Each child iterator maintains its own state

**Use Cases**:
- Annual reports across quarterly data
- Combining historical and current data
- Aggregating data from multiple databases/sources

### Answer 14: Iterator vs. Direct Access
**When to prefer Iterator**:

1. **Unknown Collection Type**:
   ```java
   // Works for any collection
   void processDeals(DealCollection deals) {
       CommissionIterator<Deal> it = deals.createIterator();
       while (it.hasNext()) {
           process(it.next());
       }
   }
   ```

2. **Filtered Traversal**:
   ```java
   // Filtering logic encapsulated
   CommissionIterator<Deal> won = deals.createStatusIterator(DealStatus.WON);
   ```

3. **Multiple Simultaneous Traversals**:
   ```java
   Iterator<Deal> iter1 = deals.createIterator();
   Iterator<Deal> iter2 = deals.createIterator();
   // Both can traverse independently
   ```

4. **Non-Sequential Collections** (trees, graphs, custom structures):
   ```java
   // Tree traversal hidden in iterator
   Iterator<Node> it = tree.createInOrderIterator();
   ```

**When direct access is more appropriate**:

1. **Random Access Needed**:
   ```java
   // Need specific indices
   Deal middle = list.get(list.size() / 2);
   Deal last = list.get(list.size() - 1);
   ```

2. **Index-Based Logic**:
   ```java
   // Processing depends on position
   for (int i = 0; i < deals.size(); i++) {
       if (i % 2 == 0) {  // Even indices
           processEven(deals.get(i));
       }
   }
   ```

3. **Simple, Known Collection**:
   ```java
   // Simple array, no abstraction needed
   int[] numbers = {1, 2, 3, 4, 5};
   for (int n : numbers) {
       System.out.println(n);
   }
   ```

4. **Performance Critical**:
   ```java
   // Direct access faster (no method calls, no object creation)
   for (int i = 0; i < array.length; i++) {
       array[i] = process(array[i]);
   }
   ```

**Summary**:
- **Iterator**: Abstraction, encapsulation, flexibility, unknown types
- **Direct Access**: Performance, index-based logic, simple cases

### Answer 15: Concurrent Modification
**Concurrent Modification Problem**:
- Collection modified **during iteration**
- Iterator state becomes invalid
- Can cause incorrect results or infinite loops

**Example**:
```java
// PROBLEM
CommissionIterator<Deal> it = deals.createIterator();
while (it.hasNext()) {
    Deal deal = it.next();
    if (shouldRemove(deal)) {
        deals.removeDeal(deal);  // MODIFIES collection during iteration!
    }
}
// Iterator now in invalid state (indices off, may skip elements)
```

**Detection Strategy (Java's approach)**:
```java
public class FailFastIterator implements Iterator<Deal> {
    private final int expectedModCount;  // Collection's modification count at iterator creation

    public FailFastIterator() {
        this.expectedModCount = collection.getModCount();
    }

    public Deal next() {
        if (collection.getModCount() != expectedModCount) {
            throw new ConcurrentModificationException();
        }
        // ... proceed with next()
    }
}
```

**Our Implementation**:
- Does **NOT** detect concurrent modification
- Iterators are "snapshot-like" for filtered views
- Assumes single-threaded usage

**Prevention Strategies**:

1. **Collect-Then-Modify**:
   ```java
   List<Deal> toRemove = new ArrayList<>();
   while (it.hasNext()) {
       Deal deal = it.next();
       if (shouldRemove(deal)) toRemove.add(deal);
   }
   toRemove.forEach(deals::removeDeal);  // Modify after iteration
   ```

2. **Iterator Remove**:
   ```java
   Iterator<Deal> it = deals.iterator();
   while (it.hasNext()) {
       Deal deal = it.next();
       if (shouldRemove(deal)) {
           it.remove();  // Safe removal through iterator
       }
   }
   ```

3. **Synchronized Access** (multi-threaded):
   ```java
   synchronized (deals) {
       Iterator<Deal> it = deals.createIterator();
       while (it.hasNext()) {
           process(it.next());
       }
   }
   ```

4. **Copy-On-Write** (snapshot):
   ```java
   // Iterator works on copy
   List<Deal> snapshot = new ArrayList<>(deals);
   // Original can be modified safely
   ```

## Design Principles

### Answer 16: Single Responsibility Principle
**How Iterator supports SRP**:

**Separate Responsibilities**:

1. **Collection's Responsibility**: Store data
   ```java
   class DealCollection {
       private List<Deal> deals;  // Storage only
       public void addDeal(Deal d) { deals.add(d); }
   }
   ```

2. **Iterator's Responsibility**: Traverse data
   ```java
   class AllDealsIterator {
       private int currentIndex = 0;  // Traversal only
       public boolean hasNext() { ... }
       public Deal next() { ... }
   }
   ```

3. **Client's Responsibility**: Process data
   ```java
   while (iterator.hasNext()) {
       Deal deal = iterator.next();
       processCommission(deal);  // Business logic
   }
   ```

**Without Iterator (violates SRP)**:
```java
class DealCollection {
    private List<Deal> deals;
    private int currentIndex = 0;  // Traversal state mixed with storage!

    public boolean hasNext() { ... }  // Traversal responsibility
    public Deal next() { ... }        // Mixed into collection class
}
// Can't have multiple simultaneous traversals!
// Collection does too much!
```

**Benefits**:
- Each class has one reason to change
- Traversal logic can evolve independently
- Testing is simpler (test collection and iterator separately)

### Answer 17: Open/Closed Principle
**How Iterator adheres to OCP**:

**Open for Extension** (can add new iterator types):
```java
// Add new iterator without modifying existing code
public class SalesRepFilterIterator implements CommissionIterator<Deal> {
    private final String repId;

    // New filtering logic
    private Deal findNext() {
        while (currentIndex < deals.size()) {
            Deal deal = deals.get(currentIndex++);
            if (deal.getSalesRepId().equals(repId)) {
                return deal;
            }
        }
        return null;
    }
}

// Add factory method to collection
public CommissionIterator<Deal> createRepIterator(String repId) {
    return new SalesRepFilterIterator(this.deals, repId);
}
```

**Closed for Modification** (existing iterators unchanged):
- `AllDealsIterator` - unchanged
- `StatusFilterIterator` - unchanged
- `ValueRangeIterator` - unchanged
- `DateFilterIterator` - unchanged

**Client code unchanged**:
```java
// Existing code still works
CommissionIterator<Deal> it = deals.createStatusIterator(DealStatus.WON);

// New code uses new iterator
CommissionIterator<Deal> repIt = deals.createRepIterator("REP-123");
```

**Contrast with non-OCP approach**:
```java
// BAD - violates OCP
public Iterator<Deal> createIterator(FilterType type, Object param) {
    switch (type) {  // Must modify this switch for new types!
        case STATUS: return new StatusFilterIterator(...);
        case VALUE: return new ValueRangeIterator(...);
        case REP: return new SalesRepFilterIterator(...);  // Added case
    }
}
```

**Benefits**:
- New iterator types added without breaking existing code
- Each iterator type is independent module
- Existing iterators remain stable (no regression risk)

### Answer 18: Dependency Inversion
**How Iterator applies DIP**:

**High-level policy depends on abstraction**:
```java
// High-level module (report generator)
public class CommissionReportGenerator {
    public void generateReport(DealCollection deals) {
        // Depends on abstraction (Iterator interface), not concrete implementation
        CommissionIterator<Deal> iterator = deals.createIterator();

        while (iterator.hasNext()) {  // Uses interface methods
            Deal deal = iterator.next();
            addToReport(deal);
        }
    }
}
```

**Concrete iterators depend on same abstraction**:
```java
// Low-level module 1
class StatusFilterIterator implements CommissionIterator<Deal> {
    // Implements abstract interface
}

// Low-level module 2
class ValueRangeIterator implements CommissionIterator<Deal> {
    // Implements abstract interface
}
```

**Dependency Diagram**:
```
     ┌─────────────────────────┐
     │ CommissionIterator<T>   │ ← Abstraction
     │ (interface)             │
     └─────────────────────────┘
              △         △
              │         │
     ┌────────┘         └────────┐
     │                           │
┌────────────────┐    ┌──────────────────┐
│ Report         │    │ StatusFilter     │
│ Generator      │    │ Iterator         │
│ (high-level)   │    │ (low-level)      │
└────────────────┘    └──────────────────┘
```

**Both depend on abstraction**, not each other.

**Benefits**:
- High-level code (report generator) independent of implementation details
- Can swap iterator implementations without changing client
- Both stable (changes to one don't affect the other)
- Testing easier (can mock iterator interface)

**Contrast with violation**:
```java
// BAD - violates DIP
public class CommissionReportGenerator {
    public void generateReport(DealCollection deals) {
        // Depends on concrete implementation!
        StatusFilterIterator iterator = new StatusFilterIterator(...);

        while (iterator.hasNext()) {  // Coupled to concrete class
            Deal deal = iterator.next();
            addToReport(deal);
        }
    }
}
```

## Practical Applications

### Answer 19: Pagination
**Pagination Implementation**:

```java
public class PaginatedIterator<T> implements CommissionIterator<T> {
    private final CommissionIterator<T> sourceIterator;
    private final int pageSize;
    private int currentPage = 0;

    public PaginatedIterator(CommissionIterator<T> source, int pageSize) {
        this.sourceIterator = source;
        this.pageSize = pageSize;
    }

    // Get specific page
    public List<T> getPage(int pageNumber) {
        if (pageNumber < currentPage) {
            sourceIterator.reset();  // Reset if going backwards
            currentPage = 0;
        }

        // Skip to desired page
        while (currentPage < pageNumber) {
            skipPage();
            currentPage++;
        }

        // Collect page items
        List<T> page = new ArrayList<>();
        for (int i = 0; i < pageSize && sourceIterator.hasNext(); i++) {
            page.add(sourceIterator.next());
        }
        currentPage++;
        return page;
    }

    private void skipPage() {
        for (int i = 0; i < pageSize && sourceIterator.hasNext(); i++) {
            sourceIterator.next();
        }
    }

    public boolean hasNextPage() {
        return sourceIterator.hasNext();
    }

    public int getTotalPages() {
        int saved = currentPage;
        int total = currentPage;

        while (hasNextPage()) {
            skipPage();
            total++;
        }

        // Restore position
        sourceIterator.reset();
        while (currentPage < saved) {
            skipPage();
            currentPage++;
        }

        return total;
    }

    @Override
    public boolean hasNext() {
        return sourceIterator.hasNext();
    }

    @Override
    public T next() {
        return sourceIterator.next();
    }

    @Override
    public int remaining() {
        return sourceIterator.remaining();
    }

    @Override
    public void reset() {
        sourceIterator.reset();
        currentPage = 0;
    }
}
```

**Usage**:
```java
// Create paginated iterator
CommissionIterator<Deal> won = deals.createStatusIterator(DealStatus.WON);
PaginatedIterator<Deal> paginated = new PaginatedIterator<>(won, 10);

// Display page 1
List<Deal> page1 = paginated.getPage(1);
displayPage(page1, 1);

// Display page 2
List<Deal> page2 = paginated.getPage(2);
displayPage(page2, 2);

// Check if more pages
if (paginated.hasNextPage()) {
    System.out.println("More pages available");
}
```

**Additional State Needed**:
- `currentPage`: Track which page we're on
- `pageSize`: Number of items per page
- `sourceIterator`: Underlying iterator

**Methods Added**:
- `getPage(int pageNumber)`: Get specific page
- `hasNextPage()`: Check if more pages exist
- `getTotalPages()`: Calculate total pages (requires full traversal)

### Answer 20: Pipeline Processing
**Iterator Pipeline**:

Pipeline processing chains multiple filtering/transformation steps.

**Implementation**:
```java
// Stage 1: Filter won deals
CommissionIterator<Deal> wonDeals = deals.createStatusIterator(DealStatus.WON);

// Stage 2: Filter high value (wrap existing iterator)
class HighValueIterator implements CommissionIterator<Deal> {
    private final CommissionIterator<Deal> source;
    private final BigDecimal threshold;

    public HighValueIterator(CommissionIterator<Deal> source, BigDecimal threshold) {
        this.source = source;
        this.threshold = threshold;
    }

    public boolean hasNext() {
        while (source.hasNext()) {
            Deal deal = source.next();
            if (deal.getValue().compareTo(threshold) > 0) {
                cache = deal;
                return true;
            }
        }
        return false;
    }
}

// Stage 3: Transform to commission amounts
class CommissionAmountIterator implements CommissionIterator<BigDecimal> {
    private final CommissionIterator<Deal> source;
    private final BigDecimal rate;

    public CommissionAmountIterator(CommissionIterator<Deal> source, BigDecimal rate) {
        this.source = source;
        this.rate = rate;
    }

    public boolean hasNext() {
        return source.hasNext();
    }

    public BigDecimal next() {
        Deal deal = source.next();
        return deal.getValue().multiply(rate);  // Transform
    }
}

// Build pipeline
CommissionIterator<Deal> stage1 = deals.createStatusIterator(DealStatus.WON);
CommissionIterator<Deal> stage2 = new HighValueIterator(stage1, new BigDecimal("50000"));
CommissionIterator<BigDecimal> stage3 = new CommissionAmountIterator(stage2, new BigDecimal("0.10"));

// Execute pipeline
BigDecimal totalCommissions = BigDecimal.ZERO;
while (stage3.hasNext()) {
    totalCommissions = totalCommissions.add(stage3.next());
}
```

**Benefits**:
- Each stage independent
- Lazy evaluation (only processes what's needed)
- Composable (can rearrange stages)
- Reusable stages

**Similar to Java Streams**:
```java
BigDecimal total = deals.stream()
    .filter(d -> d.getStatus() == DealStatus.WON)
    .filter(d -> d.getValue().compareTo(new BigDecimal("50000")) > 0)
    .map(d -> d.getValue().multiply(new BigDecimal("0.10")))
    .reduce(BigDecimal.ZERO, BigDecimal::add);
```

### Answer 21: Performance Optimization
**Performance Implications**:

**Overhead Sources**:
1. **Object Creation**: Iterator object created
2. **Method Calls**: `hasNext()` and `next()` are method calls (vs. array access)
3. **Filtering Logic**: Filtered iterators must check each element
4. **Caching**: Maintaining cache state

**When Iterators Are Slower**:
```java
// Iterator approach
CommissionIterator<Deal> it = deals.createIterator();
while (it.hasNext()) {
    Deal deal = it.next();  // 2 method calls per element
    process(deal);
}

// Direct access (faster)
for (int i = 0; i < deals.size(); i++) {
    Deal deal = deals.get(i);  // Direct array access
    process(deal);
}
```

**Benchmark (rough):**
- Direct access: ~5ns per element
- Iterator: ~15ns per element (3x slower)
- But: difference usually negligible compared to processing time

**Optimization Techniques**:

1. **Batch Processing**:
   ```java
   // Process in batches to amortize iterator overhead
   public List<Deal> nextBatch(int batchSize) {
       List<Deal> batch = new ArrayList<>(batchSize);
       for (int i = 0; i < batchSize && hasNext(); i++) {
           batch.add(next());
       }
       return batch;
   }
   ```

2. **Cache Remaining Count**:
   ```java
   // Avoid recalculating remaining on each call
   private int cachedRemaining = -1;

   public int remaining() {
       if (cachedRemaining == -1) {
           cachedRemaining = calculateRemaining();
       }
       return cachedRemaining;
   }
   ```

3. **Eliminate Redundant Checks**:
   ```java
   // Don't call hasNext() if you just called next() successfully
   while (true) {
       try {
           Deal deal = iterator.next();
           process(deal);
       } catch (NoSuchElementException e) {
           break;
       }
   }
   ```

4. **Pre-compute Filtered Results**:
   ```java
   // If iterating multiple times, pre-filter once
   List<Deal> wonDeals = new ArrayList<>();
   CommissionIterator<Deal> it = deals.createStatusIterator(DealStatus.WON);
   while (it.hasNext()) {
       wonDeals.add(it.next());
   }

   // Now iterate cached results multiple times (fast)
   for (Deal d : wonDeals) { ... }
   ```

5. **Avoid Reset If Possible**:
   - Creating new iterator may be faster than resetting
   - Reset may need to reconstruct state

**When Performance Matters**:
- Processing millions of elements
- Real-time systems with strict latency requirements
- Inner loops of performance-critical algorithms

**When Iterators Are Fine**:
- Most business applications (overhead negligible)
- Benefits (encapsulation, flexibility) outweigh cost
- Processing time dominates iteration time

## Pattern Comparisons

### Answer 22: Iterator vs. Stream API
**Similarities**:
- Both provide sequential access to elements
- Both support filtering and transformation
- Both support lazy evaluation
- Both hide collection structure

**Differences**:

| Aspect | Iterator | Stream API |
|--------|----------|-----------|
| **Style** | Imperative (while loop) | Declarative (fluent API) |
| **State** | External (hasNext/next) | Internal (forEach, collect) |
| **Reusability** | Can reset and reuse | Single-use only |
| **Operations** | Manual filtering | Built-in filter/map/reduce |
| **Parallel** | Manual threading | Built-in parallel support |
| **Lazy** | Partially lazy | Fully lazy (until terminal op) |

**Examples**:

**Iterator**:
```java
CommissionIterator<Deal> it = deals.createStatusIterator(DealStatus.WON);
BigDecimal total = BigDecimal.ZERO;
while (it.hasNext()) {
    Deal deal = it.next();
    if (deal.getValue().compareTo(new BigDecimal("10000")) > 0) {
        total = total.add(deal.getValue());
    }
}
```

**Stream**:
```java
BigDecimal total = deals.stream()
    .filter(d -> d.getStatus() == DealStatus.WON)
    .filter(d -> d.getValue().compareTo(new BigDecimal("10000")) > 0)
    .map(Deal::getValue)
    .reduce(BigDecimal.ZERO, BigDecimal::add);
```

**When to choose Iterator**:
- Need manual control over iteration
- Need to reset and reuse
- Need to maintain explicit state
- Working with older Java versions
- Need custom iteration logic not expressible in streams

**When to choose Stream**:
- Want concise, declarative code
- Need parallel processing
- Have complex transformation pipeline
- Working with Java 8+
- Want built-in operations (filter, map, reduce)

**Can Convert Between**:
```java
// Iterator → Stream
Iterator<Deal> it = deals.createIterator();
Spliterator<Deal> spliterator = Spliterators.spliteratorUnknownSize(
    it, Spliterator.ORDERED
);
Stream<Deal> stream = StreamSupport.stream(spliterator, false);

// Stream → Iterator
Iterator<Deal> it = deals.stream().iterator();
```

### Answer 23: Iterator vs. Visitor
**Key Differences**:

| Aspect | Iterator | Visitor |
|--------|----------|---------|
| **Purpose** | Traverse structure | Perform operations |
| **Focus** | Access elements | Process elements |
| **What it does** | Returns elements one by one | Operates on each element |
| **Client role** | Calls next(), processes element | Just starts traversal |
| **Operations** | Traversal only | Business operations |

**Iterator Example**:
```java
// Client controls iteration and processing
CommissionIterator<Deal> it = deals.createIterator();
while (it.hasNext()) {
    Deal deal = it.next();
    // Client does processing
    System.out.println(deal.getTitle());
}
```

**Visitor Example**:
```java
// Visitor contains processing logic
class ReportVisitor implements DealVisitor {
    public void visit(Deal deal) {
        System.out.println(deal.getTitle());  // Visitor does processing
    }
}

// Client just starts traversal
deals.accept(new ReportVisitor());
```

**Used Together**:
```java
// Iterator for traversal, Visitor for operations
CommissionIterator<Deal> it = deals.createIterator();
DealVisitor visitor = new ReportVisitor();

while (it.hasNext()) {
    Deal deal = it.next();
    deal.accept(visitor);  // Visitor processes each element
}
```

**When to use each**:
- **Iterator**: When client needs control over traversal order and processing
- **Visitor**: When you have multiple operations on same structure
- **Both**: Complex traversal with multiple operations

### Answer 24: Iterator vs. For-Each Loop
**Java's for-each loop**:
```java
for (Deal deal : deals) {
    process(deal);
}
```

**Internally uses iterator**:
```java
// Compiler converts for-each to:
Iterator<Deal> it = deals.iterator();
while (it.hasNext()) {
    Deal deal = it.next();
    process(deal);
}
```

**Advantages of for-each**:
1. **Concise**: Less boilerplate code
2. **Readable**: Clear intent
3. **Safe**: Can't forget to call `hasNext()`
4. **Familiar**: Standard Java idiom

**Limitations of for-each**:
1. **No index access**:
   ```java
   // Can't do this with for-each
   for (int i = 0; i < deals.size(); i++) {
       System.out.println(i + ": " + deals.get(i));
   }
   ```

2. **Can't remove during iteration**:
   ```java
   // Doesn't work (ConcurrentModificationException)
   for (Deal deal : deals) {
       if (shouldRemove(deal)) {
           deals.remove(deal);  // ERROR
       }
   }

   // Must use explicit iterator
   Iterator<Deal> it = deals.iterator();
   while (it.hasNext()) {
       if (shouldRemove(it.next())) {
           it.remove();  // OK
       }
   }
   ```

3. **Can't iterate multiple collections simultaneously**:
   ```java
   // Can't do this with for-each
   Iterator<Deal> it1 = collection1.iterator();
   Iterator<Deal> it2 = collection2.iterator();
   while (it1.hasNext() && it2.hasNext()) {
       process(it1.next(), it2.next());
   }
   ```

4. **No control over iteration**:
   - Can't skip elements
   - Can't iterate backwards
   - Can't reset

**When to use for-each**:
- Simple forward iteration
- Don't need index
- Don't need to remove elements
- Want clean, readable code

**When to use explicit iterator**:
- Need to remove during iteration
- Need manual control (skip, reset, etc.)
- Iterating multiple collections
- Need access to iterator methods

## Real-World Scenarios

### Answer 25: Report Generation
**Three Reports from Same Data**:

**Traditional Approach (without iterators)**:
```java
// Report 1: Won Deals Summary
BigDecimal total = BigDecimal.ZERO;
int count = 0;
for (int i = 0; i < deals.size(); i++) {
    Deal deal = deals.get(i);
    if (deal.getStatus() == DealStatus.WON) {  // Filtering logic here
        total = total.add(deal.getValue());
        count++;
    }
}
generateWonDealsReport(total, count);

// Report 2: High-Value Pipeline
List<Deal> highValue = new ArrayList<>();
for (int i = 0; i < deals.size(); i++) {
    Deal deal = deals.get(i);
    if (deal.getValue().compareTo(new BigDecimal("50000")) > 0) {  // Duplicate filtering
        highValue.add(deal);
    }
}
generateHighValueReport(highValue);

// Report 3: Recent Activity
List<Deal> recent = new ArrayList<>();
LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
for (int i = 0; i < deals.size(); i++) {
    Deal deal = deals.get(i);
    if (deal.getCloseDate().isAfter(thirtyDaysAgo)) {  // More filtering logic
        recent.add(deal);
    }
}
generateRecentActivityReport(recent);
```

**Problems**:
- Filtering logic scattered and duplicated
- Tight coupling to collection structure
- Hard to test filtering logic separately
- Can't reuse filtering for other purposes

**Iterator Approach**:
```java
// Report 1: Won Deals Summary
CommissionIterator<Deal> wonDeals = deals.createStatusIterator(DealStatus.WON);
BigDecimal total = BigDecimal.ZERO;
int count = 0;
while (wonDeals.hasNext()) {
    Deal deal = wonDeals.next();
    total = total.add(deal.getValue());
    count++;
}
generateWonDealsReport(total, count);

// Report 2: High-Value Pipeline
CommissionIterator<Deal> highValue = deals.createValueRangeIterator(
    new BigDecimal("50000"),
    new BigDecimal("1000000000")
);
generateHighValueReport(highValue);  // Can pass iterator directly

// Report 3: Recent Activity
LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
CommissionIterator<Deal> recent = deals.createDateIterator(thirtyDaysAgo);
generateRecentActivityReport(recent);
```

**Benefits**:
- ✅ Filtering logic encapsulated in iterators
- ✅ Reusable (same iterators for other purposes)
- ✅ Testable (can test iterator logic separately)
- ✅ Flexible (easy to add new filter types)
- ✅ Clean report generation code (focused on business logic)
- ✅ No duplicate filtering code

**Further Improvement**:
```java
// Can compose iterators for complex filters
CommissionIterator<Deal> wonHighValue =
    new HighValueIterator(
        deals.createStatusIterator(DealStatus.WON),
        new BigDecimal("50000")
    );
// Won deals above $50k in one iterator!
```

### Answer 26: Data Transformation
**Transformation Iterator**:

```java
/**
 * Iterator that transforms deals to commission amounts
 */
public class CommissionAmountIterator implements CommissionIterator<BigDecimal> {
    private final CommissionIterator<Deal> dealIterator;
    private final BigDecimal commissionRate;

    public CommissionAmountIterator(
            CommissionIterator<Deal> dealIterator,
            BigDecimal commissionRate) {
        this.dealIterator = dealIterator;
        this.commissionRate = commissionRate;
    }

    @Override
    public boolean hasNext() {
        return dealIterator.hasNext();
    }

    @Override
    public BigDecimal next() {
        Deal deal = dealIterator.next();
        return deal.getValue().multiply(commissionRate);  // Transform
    }

    @Override
    public int remaining() {
        return dealIterator.remaining();
    }

    @Override
    public void reset() {
        dealIterator.reset();
    }
}
```

**Usage**:
```java
// Create transformation iterator
CommissionIterator<Deal> wonDeals = deals.createStatusIterator(DealStatus.WON);
CommissionIterator<BigDecimal> commissions =
    new CommissionAmountIterator(wonDeals, new BigDecimal("0.10"));

// Iterate over commission amounts (not deals)
BigDecimal total = BigDecimal.ZERO;
while (commissions.hasNext()) {
    BigDecimal commission = commissions.next();
    total = total.add(commission);
    System.out.println("Commission: $" + commission);
}
```

**Benefits**:

1. **Separation of Concerns**:
   - Deal iteration separate from transformation
   - Can change transformation logic without affecting iteration

2. **Lazy Transformation**:
   - Values transformed on-demand
   - Don't need to create intermediate collection

3. **Composability**:
   ```java
   // Chain transformations
   CommissionIterator<Deal> deals = ...;
   CommissionIterator<BigDecimal> commissions =
       new CommissionAmountIterator(deals, rate);
   CommissionIterator<BigDecimal> afterTax =
       new TaxedCommissionIterator(commissions, taxRate);
   ```

4. **Reusability**:
   - Same transformation iterator for different deal sources
   - Can apply to filtered, sorted, or any deal iterator

5. **Memory Efficiency**:
   ```java
   // Without transformation iterator (needs intermediate collection)
   List<BigDecimal> commissions = new ArrayList<>();
   while (deals.hasNext()) {
       Deal deal = deals.next();
       commissions.add(deal.getValue().multiply(rate));
   }
   // Process commissions...

   // With transformation iterator (no intermediate collection)
   CommissionIterator<BigDecimal> commissions =
       new CommissionAmountIterator(deals, rate);
   // Process directly
   ```

**More Examples**:
```java
// Transform to report rows
class DealReportRowIterator implements CommissionIterator<ReportRow> {
    public ReportRow next() {
        Deal deal = dealIterator.next();
        return new ReportRow(
            deal.getTitle(),
            deal.getValue(),
            deal.getStatus().toString()
        );
    }
}

// Transform to DTOs
class DealDtoIterator implements CommissionIterator<DealDto> {
    public DealDto next() {
        Deal deal = dealIterator.next();
        return DealDto.fromDeal(deal);
    }
}
```

### Answer 27: Custom Business Logic
**Requirement**: Process only deals where:
- value > $50k
- status = WON
- closeDate within last 90 days

**Implementation**:
```java
public class ComplexFilterIterator implements CommissionIterator<Deal> {
    private final CommissionIterator<Deal> sourceIterator;
    private final BigDecimal minValue;
    private final LocalDate afterDate;
    private Deal nextDeal = null;
    private boolean nextSet = false;

    public ComplexFilterIterator(CommissionIterator<Deal> source) {
        this.sourceIterator = source;
        this.minValue = new BigDecimal("50000");
        this.afterDate = LocalDate.now().minusDays(90);
    }

    @Override
    public boolean hasNext() {
        if (!nextSet) {
            nextDeal = findNext();
            nextSet = true;
        }
        return nextDeal != null;
    }

    @Override
    public Deal next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Deal result = nextDeal;
        nextDeal = null;
        nextSet = false;
        return result;
    }

    private Deal findNext() {
        while (sourceIterator.hasNext()) {
            Deal deal = sourceIterator.next();

            // Apply all three conditions
            boolean highValue = deal.getValue().compareTo(minValue) > 0;
            boolean won = deal.getStatus() == DealStatus.WON;
            boolean recent = deal.getCloseDate() != null &&
                           deal.getCloseDate().isAfter(afterDate);

            if (highValue && won && recent) {
                return deal;  // All conditions met
            }
        }
        return null;  // No more matches
    }

    @Override
    public int remaining() {
        int count = 0;
        while (hasNext()) {
            next();
            count++;
        }
        sourceIterator.reset();
        return count;
    }

    @Override
    public void reset() {
        sourceIterator.reset();
        nextDeal = null;
        nextSet = false;
    }
}
```

**Usage**:
```java
// Create complex filter
DealCollection deals = getDealCollection();
CommissionIterator<Deal> complexFilter =
    new ComplexFilterIterator(deals.createIterator());

// Process matching deals
while (complexFilter.hasNext()) {
    Deal deal = complexFilter.next();
    processHighValueRecentWonDeal(deal);
}
```

**Alternative: Compose Simple Iterators**:
```java
// Build from existing iterators
CommissionIterator<Deal> step1 = deals.createStatusIterator(DealStatus.WON);
CommissionIterator<Deal> step2 = new HighValueIterator(step1, new BigDecimal("50000"));
CommissionIterator<Deal> step3 = new RecentDateIterator(step2, LocalDate.now().minusDays(90));

// Final composed iterator
while (step3.hasNext()) {
    Deal deal = step3.next();
    processHighValueRecentWonDeal(deal);
}
```

**Benefits of Iterator Approach**:
1. **Encapsulation**: Complex logic hidden in iterator
2. **Reusability**: Can reuse for other purposes
3. **Testability**: Can test filtering logic independently
4. **Clarity**: Business logic clear and declarative
5. **Maintainability**: Easy to modify criteria in one place

**Add Factory Method**:
```java
public class DealCollection {
    public CommissionIterator<Deal> createHighValueRecentWonIterator() {
        return new ComplexFilterIterator(createIterator());
    }
}

// Clean usage
CommissionIterator<Deal> it = deals.createHighValueRecentWonIterator();
```

## Testing and Quality

### Answer 28: Testing Strategies
**Key Test Cases**:

1. **Basic Iteration**:
   ```java
   @Test
   void testBasicIteration() {
       DealCollection deals = createTestDeals(5);
       CommissionIterator<Deal> it = deals.createIterator();

       int count = 0;
       while (it.hasNext()) {
           assertNotNull(it.next());
           count++;
       }
       assertEquals(5, count);
   }
   ```

2. **Empty Collection**:
   ```java
   @Test
   void testEmptyCollection() {
       DealCollection deals = new DealCollection();
       CommissionIterator<Deal> it = deals.createIterator();

       assertFalse(it.hasNext());
       assertThrows(NoSuchElementException.class, () -> it.next());
   }
   ```

3. **Single Element**:
   ```java
   @Test
   void testSingleElement() {
       DealCollection deals = createTestDeals(1);
       CommissionIterator<Deal> it = deals.createIterator();

       assertTrue(it.hasNext());
       assertNotNull(it.next());
       assertFalse(it.hasNext());
   }
   ```

4. **Filtered Iteration Correctness**:
   ```java
   @Test
   void testStatusFilter() {
       DealCollection deals = new DealCollection();
       deals.addDeal(createDeal(DealStatus.WON));
       deals.addDeal(createDeal(DealStatus.LOST));
       deals.addDeal(createDeal(DealStatus.WON));

       CommissionIterator<Deal> won = deals.createStatusIterator(DealStatus.WON);

       int count = 0;
       while (won.hasNext()) {
           Deal deal = won.next();
           assertEquals(DealStatus.WON, deal.getStatus());
           count++;
       }
       assertEquals(2, count);
   }
   ```

5. **hasNext() Idempotence**:
   ```java
   @Test
   void testHasNextIdempotent() {
       DealCollection deals = createTestDeals(3);
       CommissionIterator<Deal> it = deals.createIterator();

       // Multiple hasNext() calls shouldn't affect state
       assertTrue(it.hasNext());
       assertTrue(it.hasNext());
       assertTrue(it.hasNext());

       // Still get first element
       Deal first = it.next();
       assertNotNull(first);
   }
   ```

6. **Multiple Simultaneous Iterators**:
   ```java
   @Test
   void testMultipleIterators() {
       DealCollection deals = createTestDeals(5);

       CommissionIterator<Deal> it1 = deals.createIterator();
       CommissionIterator<Deal> it2 = deals.createIterator();

       // Advance it1
       it1.next();
       it1.next();

       // it2 should still be at beginning
       Deal it2First = it2.next();
       Deal it1Restart = deals.createIterator().next();

       assertEquals(it1Restart.getId(), it2First.getId());
   }
   ```

7. **Reset Functionality**:
   ```java
   @Test
   void testReset() {
       DealCollection deals = createTestDeals(3);
       CommissionIterator<Deal> it = deals.createIterator();

       // First pass
       List<String> firstPass = new ArrayList<>();
       while (it.hasNext()) {
           firstPass.add(it.next().getId());
       }

       // Reset
       it.reset();

       // Second pass should be identical
       List<String> secondPass = new ArrayList<>();
       while (it.hasNext()) {
           secondPass.add(it.next().getId());
       }

       assertEquals(firstPass, secondPass);
   }
   ```

8. **Boundary Conditions**:
   ```java
   @Test
   void testValueRangeBoundaries() {
       DealCollection deals = new DealCollection();
       deals.addDeal(createDeal(new BigDecimal("49999")));
       deals.addDeal(createDeal(new BigDecimal("50000")));  // Min boundary
       deals.addDeal(createDeal(new BigDecimal("100000"))); // Max boundary
       deals.addDeal(createDeal(new BigDecimal("100001")));

       CommissionIterator<Deal> range = deals.createValueRangeIterator(
           new BigDecimal("50000"),
           new BigDecimal("100000")
       );

       int count = 0;
       while (range.hasNext()) {
           count++;
           range.next();
       }
       assertEquals(2, count);  // Only middle two
   }
   ```

**Edge Cases to Test**:
- Empty collection
- Single element
- All elements filtered out
- Null values (if allowed)
- Very large collections (performance)
- Concurrent modification (if detection implemented)
- Reset on empty iterator
- calling next() after exhaustion

### Answer 29: Iterator Invariants
**Invariants** are conditions that must always be true for iterator to be in valid state.

**Key Invariants**:

1. **Position Invariant**:
   ```
   0 <= currentIndex <= collection.size()
   ```
   - Current index within valid range
   - Never negative, never beyond collection

2. **hasNext() Consistency**:
   ```
   hasNext() returns true ⟹ next() will succeed
   hasNext() returns false ⟹ next() will throw NoSuchElementException
   ```

3. **next() Progress**:
   ```
   After next() called, position advances
   next() can't return same element twice (unless reset)
   ```

4. **Cache Consistency** (for filtered iterators):
   ```
   If nextSet == true, then nextDeal is valid
   If nextSet == false, nextDeal may be invalid
   After next() called, nextSet becomes false
   ```

5. **Collection Reference**:
   ```
   Iterator maintains reference to collection (or its data)
   Collection reference is never null
   ```

6. **Filter Invariant** (for filtered iterators):
   ```
   All elements returned by next() match filter criteria
   No element matching criteria is skipped
   ```

**Verifying Invariants in Code**:
```java
public class InvariantCheckingIterator implements CommissionIterator<Deal> {
    private int currentIndex = 0;
    private final List<Deal> deals;

    private void checkInvariants() {
        assert currentIndex >= 0 : "Index negative";
        assert currentIndex <= deals.size() : "Index beyond collection";
        assert deals != null : "Collection null";
    }

    @Override
    public boolean hasNext() {
        checkInvariants();
        return currentIndex < deals.size();
    }

    @Override
    public Deal next() {
        checkInvariants();
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Deal result = deals.get(currentIndex++);
        checkInvariants();  // Check after modification
        return result;
    }
}
```

**Testing Invariants**:
```java
@Test
void testPositionInvariant() {
    DealCollection deals = createTestDeals(5);
    CommissionIterator<Deal> it = deals.createIterator();

    // Initially at position 0
    // After each next(), position advances by 1
    for (int i = 0; i < 5; i++) {
        assertTrue(it.hasNext());
        it.next();
        assertEquals(5 - i - 1, it.remaining());
    }

    // Finally at position 5 (end)
    assertFalse(it.hasNext());
}
```

### Answer 30: Error Handling
**Exceptions Iterators Should Throw**:

1. **NoSuchElementException**:
   ```java
   @Override
   public Deal next() {
       if (!hasNext()) {
           throw new NoSuchElementException("No more deals");
       }
       return deals.get(currentIndex++);
   }
   ```
   - **When**: `next()` called when no more elements
   - **Why**: Standard Java convention (Iterator interface)
   - **Helps**: Fail-fast - indicates programming error

2. **IllegalStateException**:
   ```java
   @Override
   public void remove() {
       if (lastReturnedIndex == -1) {
           throw new IllegalStateException("next() not yet called");
       }
       deals.remove(lastReturnedIndex);
       lastReturnedIndex = -1;
   }
   ```
   - **When**: Operation called in invalid state (e.g., remove before next)
   - **Why**: Indicates incorrect API usage
   - **Helps**: Guides correct usage

3. **ConcurrentModificationException** (optional):
   ```java
   @Override
   public Deal next() {
       if (modCount != expectedModCount) {
           throw new ConcurrentModificationException();
       }
       // ...
   }
   ```
   - **When**: Collection modified during iteration
   - **Why**: Iteration state is invalid
   - **Helps**: Fail-fast detection of concurrent modification

4. **UnsupportedOperationException**:
   ```java
   @Override
   public void remove() {
       throw new UnsupportedOperationException("remove not supported");
   }
   ```
   - **When**: Optional operation not implemented
   - **Why**: Some iterators don't support all operations
   - **Helps**: Clear indication of capabilities

**How This Helps with API Design**:

1. **Clear Contract**:
   - Exceptions document expected behavior
   - Client knows what to expect

2. **Fail-Fast**:
   - Errors detected immediately
   - Easier to debug

3. **Standard Conventions**:
   - Follows Java Iterator interface conventions
   - Familiar to developers

4. **Defensive Programming**:
   ```java
   // Client code
   try {
       while (true) {
           Deal deal = iterator.next();
           process(deal);
       }
   } catch (NoSuchElementException e) {
       // Expected end of iteration
   }

   // Better: check hasNext()
   while (iterator.hasNext()) {
       Deal deal = iterator.next();
       process(deal);
   }
   ```

**Documentation**:
```java
/**
 * Returns the next element in the iteration.
 *
 * @return the next Deal
 * @throws NoSuchElementException if iteration has no more elements
 */
@Override
public Deal next();
```

## Architecture and Best Practices

### Answer 31: Collection Modification
**Should a collection be modified during iteration?**

**General Answer: NO** (usually).

**Why Not**:
```java
// DANGEROUS
CommissionIterator<Deal> it = deals.createIterator();
while (it.hasNext()) {
    Deal deal = it.next();
    if (shouldRemove(deal)) {
        deals.removeDeal(deal);  // Modifies collection during iteration!
    }
}
// Iterator state now invalid - may skip elements, get duplicates, or crash
```

**Problems**:
- Iterator position becomes invalid
- May skip elements or see duplicates
- Index-based iterators break
- Undefined behavior

**Strategies to Prevent**:

1. **Collect-Then-Modify** (safest):
   ```java
   // Collect items to modify
   List<Deal> toRemove = new ArrayList<>();
   while (it.hasNext()) {
       Deal deal = it.next();
       if (shouldRemove(deal)) {
           toRemove.add(deal);
       }
   }

   // Modify after iteration complete
   toRemove.forEach(deals::removeDeal);
   ```

2. **Iterator.remove()** (if supported):
   ```java
   Iterator<Deal> it = deals.iterator();
   while (it.hasNext()) {
       Deal deal = it.next();
       if (shouldRemove(deal)) {
           it.remove();  // Safe - iterator handles it
       }
   }
   ```

3. **Fail-Fast Detection**:
   ```java
   public class FailFastIterator implements Iterator<Deal> {
       private final int expectedModCount;

       public FailFastIterator() {
           this.expectedModCount = collection.getModCount();
       }

       public Deal next() {
           if (collection.getModCount() != expectedModCount) {
               throw new ConcurrentModificationException();
           }
           // ... proceed
       }
   }
   ```

4. **Copy-On-Write** (snapshot):
   ```java
   public class SnapshotIterator implements Iterator<Deal> {
       private final List<Deal> snapshot;

       public SnapshotIterator(List<Deal> original) {
           this.snapshot = new ArrayList<>(original);  // Copy
       }

       // Iterate over snapshot - original can be modified
   }
   ```

5. **Synchronized Access** (multi-threaded):
   ```java
   synchronized (deals) {
       Iterator<Deal> it = deals.createIterator();
       while (it.hasNext()) {
           process(it.next());
       }
   }
   ```

6. **Immutable Collections**:
   ```java
   // Collection can't be modified at all
   List<Deal> immutableDeals = Collections.unmodifiableList(deals);
   // Safe to iterate - can't be modified
   ```

**Best Practice**:
- Default: collect items, modify after iteration
- If available: use iterator.remove()
- If necessary: use copy-on-write or fail-fast detection
- Document: clearly state whether modification during iteration is allowed

### Answer 32: Iterator Lifetime
**How long should an iterator live?**

**General Guideline**: **Short-lived, single-use**.

**Typical Pattern**:
```java
// Create, use, discard
CommissionIterator<Deal> it = deals.createIterator();
while (it.hasNext()) {
    process(it.next());
}
// Iterator discarded (garbage collected)
```

**Should Iterators Be Reused?**

**Generally NO**, but with `reset()`, can be reused:

**Single-Use (common)**:
```java
// Create new iterator for each traversal
for (int pass = 0; pass < 3; pass++) {
    CommissionIterator<Deal> it = deals.createIterator();
    while (it.hasNext()) {
        process(it.next());
    }
}
```

**Reuse with Reset (if supported)**:
```java
CommissionIterator<Deal> it = deals.createIterator();

// First pass
while (it.hasNext()) {
    process(it.next());
}

it.reset();  // Reset for reuse

// Second pass
while (it.hasNext()) {
    process(it.next());
}
```

**When to Reuse**:
- Iterator creation is expensive (e.g., sorted iterator)
- Multiple passes needed over same filtered data
- Iterator has significant initialization cost

**When NOT to Reuse**:
- Simple iterators (cheap to create)
- Only need single pass
- Risk of forgetting to reset
- State management complexity

**Anti-Pattern - Long-Lived Iterator**:
```java
// BAD - iterator held too long
public class DealProcessor {
    private CommissionIterator<Deal> iterator;  // Field!

    public DealProcessor(DealCollection deals) {
        this.iterator = deals.createIterator();  // Held in object
    }

    public void processNext() {
        if (iterator.hasNext()) {
            process(iterator.next());
        }
    }
}
```

**Problems**:
- Iterator state can become stale
- Collection may have changed
- Ties object lifetime to iterator
- Hard to reason about state

**Better - Short-Lived**:
```java
public class DealProcessor {
    private final DealCollection deals;  // Hold collection, not iterator

    public void processAll() {
        CommissionIterator<Deal> it = deals.createIterator();  // Create when needed
        while (it.hasNext()) {
            process(it.next());
        }
        // Iterator discarded
    }
}
```

**Exception - Stateful Iteration**:
```java
// Legitimate long-lived iterator for pagination
public class PagedDealIterator {
    private final CommissionIterator<Deal> iterator;
    private final int pageSize;

    public List<Deal> nextPage() {
        List<Deal> page = new ArrayList<>();
        for (int i = 0; i < pageSize && iterator.hasNext(); i++) {
            page.add(iterator.next());
        }
        return page;
    }
}
```

**Best Practices**:
- Create iterators when needed, discard after use
- Don't store iterators as fields (unless specific reason)
- If reusing, always reset first
- Document iterator lifetime expectations

### Answer 33: Iterator Composition
**Can iterators be composed?**

**YES** - One iterator wrapping another is common and powerful.

**Examples from Commission System**:

**1. High-Value Won Deals**:
```java
// Compose: Status filter → Value filter
CommissionIterator<Deal> wonDeals = deals.createStatusIterator(DealStatus.WON);
CommissionIterator<Deal> highValueWon = new HighValueIterator(wonDeals, new BigDecimal("50000"));

// Now have iterator for high-value won deals
while (highValueWon.hasNext()) {
    Deal deal = highValueWon.next();
    // Process high-value won deal
}
```

**2. Transform then Filter**:
```java
// Compose: All deals → Commission amounts → High commissions only
CommissionIterator<Deal> allDeals = deals.createIterator();
CommissionIterator<BigDecimal> commissions = new CommissionAmountIterator(allDeals, rate);
CommissionIterator<BigDecimal> highCommissions = new ThresholdIterator(commissions, new BigDecimal("5000"));

// Final result: commission amounts above $5000
while (highCommissions.hasNext()) {
    BigDecimal commission = highCommissions.next();
    processHighCommission(commission);
}
```

**3. Multiple Collections**:
```java
// Compose: Multiple collections → Single view
CommissionIterator<Deal> q1 = q1Deals.createIterator();
CommissionIterator<Deal> q2 = q2Deals.createIterator();
CompositeIterator<Deal> halfYear = new CompositeIterator<>(Arrays.asList(q1, q2));

// Iterate across both quarters
while (halfYear.hasNext()) {
    Deal deal = halfYear.next();
    processHalfYearDeal(deal);
}
```

**4. Pipeline**:
```java
// Build processing pipeline
CommissionIterator<Deal> step1 = deals.createStatusIterator(DealStatus.WON);
CommissionIterator<Deal> step2 = new HighValueIterator(step1, threshold);
CommissionIterator<Deal> step3 = new RecentDateIterator(step2, cutoffDate);
CommissionIterator<String> step4 = new ToStringIterator(step3);

// Final composed iterator
while (step4.hasNext()) {
    String report = step4.next();
    System.out.println(report);
}
```

**Benefits of Composition**:

1. **Modularity**:
   - Each iterator does one thing
   - Easy to understand and test

2. **Reusability**:
   - Can reuse existing iterators
   - Build complex behavior from simple parts

3. **Flexibility**:
   - Can rearrange pipeline
   - Add/remove stages easily

4. **Lazy Evaluation**:
   - Elements processed on-demand
   - Efficient for large datasets

**Implementation Pattern**:
```java
public class ComposingIterator implements CommissionIterator<T> {
    private final CommissionIterator<T> source;  // Wrap another iterator

    public ComposingIterator(CommissionIterator<T> source) {
        this.source = source;
    }

    public boolean hasNext() {
        // Delegate to source, possibly with additional logic
        return source.hasNext();
    }

    public T next() {
        T element = source.next();
        // Transform/filter element
        return processElement(element);
    }
}
```

**Similar to Decorator Pattern**:
- Iterator composition is essentially Decorator pattern applied to iterators
- Each wrapper adds behavior (filtering, transformation, etc.)

**Real-World Use Cases**:
- Java 8 Stream API is built on iterator composition
- Database query iterators (filter → sort → limit)
- File processing pipelines (read → parse → validate → transform)

## Anti-Patterns and Pitfalls

### Answer 34: Iterator Misuse
**Common Mistakes**:

**1. Not Checking hasNext()**:
```java
// BAD
CommissionIterator<Deal> it = deals.createIterator();
Deal d1 = it.next();  // OK if at least 1 element
Deal d2 = it.next();  // May throw NoSuchElementException!
Deal d3 = it.next();  // Likely throws exception

// GOOD
while (it.hasNext()) {
    Deal deal = it.next();
    process(deal);
}
```

**2. Modifying Collection During Iteration**:
```java
// BAD
CommissionIterator<Deal> it = deals.createIterator();
while (it.hasNext()) {
    Deal deal = it.next();
    deals.removeDeal(deal);  // Breaks iterator!
}

// GOOD
List<Deal> toRemove = new ArrayList<>();
while (it.hasNext()) {
    Deal deal = it.next();
    if (shouldRemove(deal)) {
        toRemove.add(deal);
    }
}
toRemove.forEach(deals::removeDeal);
```

**3. Calling hasNext() Multiple Times Expecting Different Results**:
```java
// MISUNDERSTANDING
if (it.hasNext()) {
    Deal d1 = it.next();
}
if (it.hasNext()) {  // Different element now!
    Deal d2 = it.next();
}
// Expects d1 and d2 to be same, but they're different
```

**4. Sharing Iterator Between Threads**:
```java
// BAD - race condition
CommissionIterator<Deal> it = deals.createIterator();

Thread t1 = new Thread(() -> {
    while (it.hasNext()) {
        Deal deal = it.next();  // Race!
        process(deal);
    }
});

Thread t2 = new Thread(() -> {
    while (it.hasNext()) {
        Deal deal = it.next();  // Race!
        process(deal);
    }
});
// Both threads compete for same iterator - undefined behavior

// GOOD - separate iterators
Thread t1 = new Thread(() -> {
    CommissionIterator<Deal> it1 = deals.createIterator();
    while (it1.hasNext()) {
        process(it1.next());
    }
});

Thread t2 = new Thread(() -> {
    CommissionIterator<Deal> it2 = deals.createIterator();
    while (it2.hasNext()) {
        process(it2.next());
    }
});
```

**5. Expecting Iterator to Reflect Changes**:
```java
// MISUNDERSTANDING
CommissionIterator<Deal> it = deals.createIterator();

// Add element after iterator created
deals.addDeal(newDeal);

// Expects iterator to see new element, but may not
while (it.hasNext()) {
    Deal deal = it.next();
    // newDeal may or may not appear
}
```

**6. Forgetting to Reset**:
```java
// BAD
CommissionIterator<Deal> it = deals.createIterator();

// First pass
while (it.hasNext()) {
    process(it.next());
}

// Second pass - forgot to reset!
while (it.hasNext()) {  // Already exhausted!
    process(it.next());  // Never executes
}

// GOOD
it.reset();  // Reset before second pass
while (it.hasNext()) {
    process(it.next());
}
```

**7. Assuming Order**:
```java
// BAD - assumes order
CommissionIterator<Deal> it = deals.createIterator();
Deal first = it.next();
Deal second = it.next();
// Assumes first < second, but order may not be guaranteed
```

**8. Using Iterator After Collection Cleared**:
```java
// BAD
CommissionIterator<Deal> it = deals.createIterator();
deals.clear();  // Collection now empty
it.next();  // What happens? Depends on implementation
```

**Why Problematic**:
- **Not checking hasNext()**: Crashes with NoSuchElementException
- **Modifying during iteration**: Iterator state becomes invalid
- **hasNext() confusion**: Doesn't understand iteration semantics
- **Shared iterator**: Race conditions, unpredictable results
- **Expecting changes**: Iterator may be snapshot or live - depends on implementation
- **Forgetting reset**: Silent failure - no elements processed
- **Assuming order**: May get unexpected results
- **After clear**: Undefined behavior

### Answer 35: Over-Engineering
**When Iterator pattern is over-engineering**:

**1. Simple Array Iteration**:
```java
// OVER-ENGINEERED
int[] numbers = {1, 2, 3, 4, 5};
Iterator<Integer> it = new ArrayIterator(numbers);
while (it.hasNext()) {
    System.out.println(it.next());
}

// SIMPLER
for (int n : numbers) {
    System.out.println(n);
}
```

**2. Single Traversal of Known Collection**:
```java
// OVER-ENGINEERED
List<Deal> deals = getDeals();
Iterator<Deal> it = deals.iterator();
while (it.hasNext()) {
    process(it.next());
}

// SIMPLER
for (Deal deal : deals) {
    process(deal);
}
```

**3. Trivial Filtering**:
```java
// OVER-ENGINEERED
class EvenNumberIterator implements Iterator<Integer> {
    // 50 lines of iterator implementation
}

// SIMPLER
for (int n : numbers) {
    if (n % 2 == 0) {
        process(n);
    }
}

// OR (Java 8+)
numbers.stream()
    .filter(n -> n % 2 == 0)
    .forEach(this::process);
```

**4. No Abstraction Needed**:
```java
// OVER-ENGINEERED
// Creating iterator interface for single concrete type
interface DealIterator {
    boolean hasNext();
    Deal next();
}

class ConcreteDealIterator implements DealIterator {
    // Only implementation, no alternatives
}

// SIMPLER
// Just use for-each or Stream API
```

**When Simpler Alternatives Are Better**:

| Scenario | Instead of Iterator | Use This |
|----------|-------------------|----------|
| Array iteration | Custom iterator | for-each loop |
| List iteration | Custom iterator | for-each loop |
| Simple filtering | Filter iterator | if-statement inside loop |
| Transformation | Transform iterator | Stream API map() |
| Single use | Custom iterator | Direct access |
| Known type | Abstract iterator | Concrete collection methods |

**Simpler Alternatives**:

**For-each loop**:
```java
for (Deal deal : deals) {
    process(deal);
}
```

**Stream API** (Java 8+):
```java
deals.stream()
    .filter(d -> d.getStatus() == DealStatus.WON)
    .map(Deal::getValue)
    .forEach(this::process);
```

**Direct Collection Methods**:
```java
deals.forEach(this::process);
```

**When Iterator IS Appropriate**:
- Need abstraction over multiple collection types
- Complex filtering logic worth encapsulating
- Multiple traversal strategies needed
- Need to expose traversal without exposing structure
- Building reusable library/API
- Need manual control over iteration

**Guideline**:
- **Start simple** (for-loop, for-each, streams)
- **Add iterator** when you need abstraction, encapsulation, or multiple strategies
- **Don't create iterators** just because you can

## Extension Questions

### Answer 36: Bidirectional Iterator
**Bidirectional Iterator** supports both forward and backward traversal.

**Extended Interface**:
```java
public interface BidirectionalIterator<T> extends CommissionIterator<T> {
    // Existing methods
    boolean hasNext();
    T next();

    // New methods for backward traversal
    boolean hasPrevious();
    T previous();

    // Additional helpful methods
    int currentIndex();
    void jumpTo(int index);
}
```

**Implementation**:
```java
public class BidirectionalDealIterator implements BidirectionalIterator<Deal> {
    private final List<Deal> deals;
    private int currentIndex;

    public BidirectionalDealIterator(List<Deal> deals) {
        this.deals = deals;
        this.currentIndex = 0;  // Start at beginning
    }

    @Override
    public boolean hasNext() {
        return currentIndex < deals.size();
    }

    @Override
    public Deal next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements");
        }
        return deals.get(currentIndex++);  // Advance forward
    }

    @Override
    public boolean hasPrevious() {
        return currentIndex > 0;
    }

    @Override
    public Deal previous() {
        if (!hasPrevious()) {
            throw new NoSuchElementException("No previous elements");
        }
        return deals.get(--currentIndex);  // Move backward
    }

    @Override
    public int currentIndex() {
        return currentIndex;
    }

    @Override
    public void jumpTo(int index) {
        if (index < 0 || index > deals.size()) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        this.currentIndex = index;
    }

    @Override
    public int remaining() {
        return deals.size() - currentIndex;
    }

    @Override
    public void reset() {
        currentIndex = 0;
    }
}
```

**Usage**:
```java
BidirectionalIterator<Deal> it = new BidirectionalDealIterator(deals);

// Forward
while (it.hasNext()) {
    Deal deal = it.next();
    System.out.println("Forward: " + deal.getTitle());
}

// Backward
while (it.hasPrevious()) {
    Deal deal = it.previous();
    System.out.println("Backward: " + deal.getTitle());
}

// Jump around
it.jumpTo(5);
Deal middle = it.next();

it.jumpTo(0);  // Back to start
```

**Use Cases**:
- Text editors (cursor movement)
- History navigation (browser back/forward)
- Undo/redo with position tracking
- Scrolling through data with ability to go back

**Similar to**: Java's `ListIterator` interface.

### Answer 37: Parallel Iterator
**Parallel Iterator** processes elements concurrently.

**Challenges**:
1. **Thread Safety**: Multiple threads accessing same collection
2. **Work Distribution**: How to split work among threads
3. **Order**: Parallel processing may not preserve order
4. **Synchronization**: Overhead of coordinating threads
5. **State Management**: Each thread needs independent state

**Approach 1: Work Splitting**:
```java
public class ParallelIterator<T> {
    private final List<T> elements;
    private final int threadCount;

    public void forEachParallel(Consumer<T> processor) {
        int chunkSize = elements.size() / threadCount;

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            int start = i * chunkSize;
            int end = (i == threadCount - 1) ? elements.size() : (i + 1) * chunkSize;

            Thread thread = new Thread(() -> {
                for (int j = start; j < end; j++) {
                    processor.accept(elements.get(j));
                }
            });
            threads.add(thread);
            thread.start();
        }

        // Wait for completion
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
```

**Approach 2: Thread-Safe Queue**:
```java
public class ConcurrentIterator<T> {
    private final BlockingQueue<T> queue;
    private final AtomicBoolean exhausted = new AtomicBoolean(false);

    public ConcurrentIterator(List<T> elements) {
        this.queue = new LinkedBlockingQueue<>(elements);
    }

    public T next() throws InterruptedException {
        T element = queue.poll(1, TimeUnit.SECONDS);
        if (element == null) {
            exhausted.set(true);
            throw new NoSuchElementException();
        }
        return element;
    }

    public boolean hasNext() {
        return !queue.isEmpty() || !exhausted.get();
    }
}

// Usage with thread pool
ExecutorService executor = Executors.newFixedThreadPool(4);
ConcurrentIterator<Deal> it = new ConcurrentIterator<>(deals);

for (int i = 0; i < 4; i++) {
    executor.submit(() -> {
        while (it.hasNext()) {
            try {
                Deal deal = it.next();
                process(deal);  // Process in parallel
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    });
}

executor.shutdown();
```

**Better: Use Java 8 Parallel Streams**:
```java
// Much simpler!
deals.parallelStream()
    .forEach(this::process);
```

**When to use parallel iteration**:
- Large datasets
- CPU-intensive processing per element
- Order doesn't matter
- Thread-safe processing logic

### Answer 38: Persistent Iterator
**Persistent Iterator** saves state for resuming later (e.g., across HTTP requests).

**State to Save**:
- Current position/index
- Filter criteria
- Sort order
- Collection identifier

**Implementation**:
```java
public class PersistentIteratorState implements Serializable {
    private final String collectionId;
    private final int currentIndex;
    private final Map<String, Object> filterCriteria;
    private final LocalDateTime createdAt;

    // Getters, constructors...
}

public class PersistentIterator<T> implements CommissionIterator<T> {
    private final String collectionId;
    private int currentIndex;
    private final Map<String, Object> filterCriteria;

    // Save state
    public PersistentIteratorState saveState() {
        return new PersistentIteratorState(
            collectionId,
            currentIndex,
            filterCriteria,
            LocalDateTime.now()
        );
    }

    // Restore state
    public static <T> PersistentIterator<T> fromState(
            PersistentIteratorState state,
            DealCollection collection) {
        PersistentIterator<T> iterator = new PersistentIterator<>(collection);
        iterator.currentIndex = state.getCurrentIndex();
        iterator.filterCriteria.putAll(state.getFilterCriteria());
        return iterator;
    }
}
```

**Web Application Usage**:
```java
// Request 1: Initial page
@GetMapping("/deals")
public Page<Deal> getDealsPage(@RequestParam int page) {
    DealCollection deals = dealService.getAllDeals();
    PersistentIterator<Deal> iterator = new PersistentIterator<>(deals);

    // Get page
    List<Deal> pageDeals = iterator.nextPage(20);

    // Save state in session
    PersistentIteratorState state = iterator.saveState();
    session.setAttribute("dealIteratorState", state);

    return new Page<>(pageDeals, page, iterator.hasNext());
}

// Request 2: Next page
@GetMapping("/deals/next")
public Page<Deal> getNextPage() {
    // Restore state
    PersistentIteratorState state =
        (PersistentIteratorState) session.getAttribute("dealIteratorState");

    DealCollection deals = dealService.getAllDeals();
    PersistentIterator<Deal> iterator =
        PersistentIterator.fromState(state, deals);

    // Continue from where we left off
    List<Deal> pageDeals = iterator.nextPage(20);

    // Update state
    session.setAttribute("dealIteratorState", iterator.saveState());

    return new Page<>(pageDeals, iterator.currentIndex() / 20, iterator.hasNext());
}
```

**Challenges**:
- Collection may have changed between requests
- State must be serializable
- Security (don't expose sensitive filter criteria)
- Expiration (old states should timeout)

**Alternative: Cursor-Based Pagination**:
```java
// Instead of saving iterator state, use cursor
@GetMapping("/deals")
public Page<Deal> getDealsPage(@RequestParam(required=false) String cursor) {
    // Cursor is last seen ID
    List<Deal> deals = dealService.getDealsAfter(cursor, 20);
    String nextCursor = deals.isEmpty() ? null : deals.get(deals.size() - 1).getId();

    return new Page<>(deals, nextCursor);
}
```

## Integration

### Answer 39: Iterator with Other Patterns
**Iterator + Strategy**:
```java
// Strategy for filtering
interface FilterStrategy<T> {
    boolean matches(T element);
}

// Iterator uses strategy
class StrategyBasedIterator<T> implements CommissionIterator<T> {
    private final CommissionIterator<T> source;
    private final FilterStrategy<T> strategy;

    public StrategyBasedIterator(CommissionIterator<T> source, FilterStrategy<T> strategy) {
        this.source = source;
        this.strategy = strategy;
    }

    private T findNext() {
        while (source.hasNext()) {
            T element = source.next();
            if (strategy.matches(element)) {  // Use strategy
                return element;
            }
        }
        return null;
    }
}

// Usage
FilterStrategy<Deal> wonDealsStrategy = deal -> deal.getStatus() == DealStatus.WON;
CommissionIterator<Deal> won = new StrategyBasedIterator<>(allDeals, wonDealsStrategy);
```

**Iterator + Composite**:
```java
// Iterate through composite tree structure
interface Component {
    String getName();
    List<Component> getChildren();
}

class TreeIterator implements Iterator<Component> {
    private final Stack<Iterator<Component>> stack = new Stack<>();

    public TreeIterator(Component root) {
        List<Component> rootList = Collections.singletonList(root);
        stack.push(rootList.iterator());
    }

    public boolean hasNext() {
        while (!stack.isEmpty()) {
            if (stack.peek().hasNext()) {
                return true;
            }
            stack.pop();
        }
        return false;
    }

    public Component next() {
        if (!hasNext()) throw new NoSuchElementException();

        Component next = stack.peek().next();

        // Push children onto stack for traversal
        if (!next.getChildren().isEmpty()) {
            stack.push(next.getChildren().iterator());
        }

        return next;
    }
}
```

**Iterator + Memento**:
```java
// Save/restore iterator state
class IteratorMemento {
    private final int savedIndex;

    public IteratorMemento(int index) {
        this.savedIndex = index;
    }

    public int getSavedIndex() {
        return savedIndex;
    }
}

class StatefulIterator<T> implements CommissionIterator<T> {
    private int currentIndex;

    // Save state
    public IteratorMemento saveState() {
        return new IteratorMemento(currentIndex);
    }

    // Restore state
    public void restoreState(IteratorMemento memento) {
        this.currentIndex = memento.getSavedIndex();
    }
}

// Usage: bookmark position
StatefulIterator<Deal> it = new StatefulIterator<>(deals);

it.next();
it.next();

IteratorMemento bookmark = it.saveState();  // Save position

it.next();
it.next();

it.restoreState(bookmark);  // Return to bookmarked position
```

**Iterator + Factory Method**:
```java
// Collection uses factory methods to create iterators
abstract class DealCollection {
    protected List<Deal> deals;

    // Factory method
    public abstract CommissionIterator<Deal> createIterator();

    // Can be overridden by subclasses
}

class SimpleDealCollection extends DealCollection {
    public CommissionIterator<Deal> createIterator() {
        return new SimpleIterator(deals);
    }
}

class FilteredDealCollection extends DealCollection {
    public CommissionIterator<Deal> createIterator() {
        return new FilteredIterator(deals, filter);
    }
}
```

### Answer 40: Framework Integration
**Integration with Java Collections Framework**:

**1. Implement `Iterable<T>` Interface**:
```java
public class DealCollection implements Iterable<Deal> {
    private final List<Deal> deals;

    @Override
    public Iterator<Deal> iterator() {
        return deals.iterator();  // Delegate to internal list
    }

    // Now supports for-each!
    // for (Deal deal : dealCollection) { ... }
}
```

**2. Provide Standard Iterator**:
```java
public class DealIterator implements Iterator<Deal> {
    private final List<Deal> deals;
    private int currentIndex = 0;

    @Override
    public boolean hasNext() {
        return currentIndex < deals.size();
    }

    @Override
    public Deal next() {
        if (!hasNext()) throw new NoSuchElementException();
        return deals.get(currentIndex++);
    }

    @Override
    public void remove() {
        // Optional operation
        throw new UnsupportedOperationException();
    }
}
```

**3. Follow Conventions**:
- Use `java.util.Iterator` interface
- Throw `NoSuchElementException` from `next()` when exhausted
- Throw `IllegalStateException` from `remove()` if not supported or called at wrong time
- Throw `ConcurrentModificationException` if collection modified during iteration

**4. Provide Multiple Iterator Types**:
```java
public class DealCollection implements Iterable<Deal> {
    // Default iterator
    @Override
    public Iterator<Deal> iterator() {
        return deals.iterator();
    }

    // Custom iterators
    public Iterator<Deal> wonDealsIterator() {
        return new FilteredIterator(deals, d -> d.getStatus() == DealStatus.WON);
    }

    public Iterator<Deal> descendingIterator() {
        return new ReverseIterator(deals);
    }
}
```

**5. Stream Support**:
```java
public class DealCollection implements Iterable<Deal> {
    // Enable Stream API
    public Stream<Deal> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    public Stream<Deal> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }

    // Custom spliterator for better performance
    @Override
    public Spliterator<Deal> spliterator() {
        return Spliterators.spliterator(
            deals,
            Spliterator.ORDERED | Spliterator.SIZED
        );
    }
}

// Usage
dealCollection.stream()
    .filter(d -> d.getStatus() == DealStatus.WON)
    .forEach(this::process);
```

**6. Fail-Fast Behavior**:
```java
public class FailFastDealIterator implements Iterator<Deal> {
    private final int expectedModCount;

    public FailFastDealIterator() {
        this.expectedModCount = collection.modCount;
    }

    @Override
    public Deal next() {
        checkForComodification();
        // ...
    }

    private void checkForComodification() {
        if (collection.modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }
}
```

**Best Practices for Framework Integration**:
- Implement `Iterable<T>` for for-each support
- Use `java.util.Iterator<T>` interface
- Follow exception conventions
- Provide spliterator for Stream support
- Consider fail-fast behavior
- Document iterator characteristics (fail-fast, weakly consistent, etc.)
- Provide both mutable and immutable views if applicable