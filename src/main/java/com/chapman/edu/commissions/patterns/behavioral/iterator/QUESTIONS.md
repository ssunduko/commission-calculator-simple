# Iterator Pattern - Concept Questions

## Basic Understanding

### Question 1: Pattern Purpose
What is the primary purpose of the Iterator pattern, and what problem does it solve in software design?

### Question 2: Core Components
What are the four main components of the Iterator pattern? Describe the role of each component.

### Question 3: Encapsulation
How does the Iterator pattern preserve encapsulation? What would be exposed without using iterators?

## Pattern Mechanics

### Question 4: Iterator Interface
Why do iterators typically have a minimal interface (hasNext() and next())? What happens if you call next() without checking hasNext()?

### Question 5: Multiple Traversals
How does the Iterator pattern support multiple simultaneous traversals of the same collection? Provide a concrete example from the commission system.

### Question 6: Iterator State
What state does an iterator need to maintain? Where is this state stored, and why is this important?

## Implementation Details

### Question 7: Filtered Iterators
Explain how the StatusFilterIterator works. Why does it need both nextDeal and nextSet fields? What is the purpose of the findNext() method?

### Question 8: Lazy Evaluation
What is lazy evaluation in the context of iterators? Compare the performance implications of lazy evaluation vs. creating a filtered copy of the collection.

### Question 9: Reset Functionality
Why might you want a reset() method on an iterator? Provide a use case from the commission system where reset() is beneficial.

## Design Decisions

### Question 10: Snapshot vs. Live Iteration
What is the difference between snapshot iteration and live iteration? Which approach does ValueSortedIterator use, and why?

### Question 11: Iterator Factories
Why does DealCollection have multiple factory methods (createStatusIterator, createValueRangeIterator, etc.) instead of a single createIterator() method that takes parameters?

### Question 12: Remove Operation
The Iterator interface in our implementation doesn't include a remove() method. Why might this be intentional? What challenges does implementing remove() introduce?

## Advanced Concepts

### Question 13: Composite Iterator
Explain how the CompositeIterator works. What problem does it solve? How does it maintain state across multiple iterators?

### Question 14: Iterator vs. Direct Access
When would you prefer using an iterator over direct collection access (e.g., for loop with index)? When is direct access more appropriate?

### Question 15: Concurrent Modification
What is the "concurrent modification" problem with iterators? How can it be detected or prevented? Does our implementation address this?

## Design Principles

### Question 16: Single Responsibility Principle
How does the Iterator pattern support the Single Responsibility Principle? What are the separate responsibilities?

### Question 17: Open/Closed Principle
Demonstrate how the Iterator pattern adheres to the Open/Closed Principle. How would you add a new type of iterator without modifying existing code?

### Question 18: Dependency Inversion
How does the Iterator pattern apply the Dependency Inversion Principle? What depends on abstractions rather than concrete implementations?

## Practical Applications

### Question 19: Pagination
How would you implement pagination using iterators? What additional methods or state would you need? Provide pseudocode.

### Question 20: Pipeline Processing
Explain how iterators can be used for pipeline processing in the commission system. How would you chain multiple filtering steps?

### Question 21: Performance Optimization
What are the performance implications of iterators? When might iterators be slower than direct collection access? How can you optimize iterator performance?

## Pattern Comparisons

### Question 22: Iterator vs. Stream API
How does the Iterator pattern compare to Java's Stream API? What are the similarities and differences? When would you choose one over the other?

### Question 23: Iterator vs. Visitor
Both Iterator and Visitor patterns work with collections. What are the key differences in their purposes and usage?

### Question 24: Iterator vs. For-Each Loop
Java's for-each loop uses iterators internally. What are the advantages and limitations of for-each compared to explicit iterator usage?

## Real-World Scenarios

### Question 25: Report Generation
You need to generate three different reports from the same DealCollection: won deals summary, high-value pipeline, and recent activity. How would iterators simplify this task compared to traditional approaches?

### Question 26: Data Transformation
How would you implement an iterator that transforms elements during iteration (e.g., converting deals to commission amounts)? What would be the benefits?

### Question 27: Custom Business Logic
A business requirement states: "Process only deals where value > $50k AND status = WON AND closeDate is within last 90 days." How would you implement this using iterators?

## Testing and Quality

### Question 28: Testing Strategies
What are the key test cases for an iterator implementation? What edge cases should be tested?

### Question 29: Iterator Invariants
What invariants should an iterator maintain? What conditions must always be true for an iterator to be in a valid state?

### Question 30: Error Handling
What exceptions should an iterator throw? When should each exception be thrown? How does this help with API design?

## Architecture and Best Practices

### Question 31: Collection Modification
Should a collection be modified while being iterated? What strategies can prevent problems with concurrent modification?

### Question 32: Iterator Lifetime
How long should an iterator live? Should iterators be reused, or should new ones be created for each traversal?

### Question 33: Iterator Composition
Can iterators be composed (one iterator wrapping another)? Provide an example from the commission system where iterator composition would be useful.

## Anti-Patterns and Pitfalls

### Question 34: Iterator Misuse
What are common mistakes when using iterators? Provide examples of incorrect iterator usage and explain why they're problematic.

### Question 35: Over-Engineering
When might using the Iterator pattern be over-engineering? What are simpler alternatives for basic traversal needs?

## Extension Questions

### Question 36: Bidirectional Iterator
How would you extend the Iterator pattern to support bidirectional traversal (forward and backward)? What methods would you add?

### Question 37: Parallel Iteration
How could you implement a parallel iterator that processes elements concurrently? What challenges would you face?

### Question 38: Persistent Iterator
How would you implement an iterator that persists its state (e.g., for paginating through database results across HTTP requests)? What information would need to be saved?

## Integration

### Question 39: Iterator with Other Patterns
How can the Iterator pattern be combined with other patterns like Strategy, Composite, or Memento? Provide specific examples.

### Question 40: Framework Integration
How does the Iterator pattern integrate with Java Collections Framework? What interfaces and conventions should be followed for seamless integration?