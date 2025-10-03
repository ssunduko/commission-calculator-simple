package com.chapman.edu.commissions.patterns.behavioral.iterator;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * ITERATOR PATTERN - STRUCTURAL DEMONSTRATION
 *
 * PURPOSE:
 * The Iterator Pattern provides a way to access elements of an aggregate object
 * sequentially without exposing its underlying representation.
 *
 * PROBLEM IT SOLVES:
 * - Need to traverse different data structures uniformly
 * - Want to hide internal structure of collections
 * - Support multiple simultaneous traversals
 * - Provide multiple ways to traverse same collection
 * - Decouple collection algorithms from collection structure
 *
 * WHEN TO USE:
 * - Need to access contents of aggregate without exposing internal structure
 * - Support multiple traversals of aggregate objects
 * - Provide uniform interface for traversing different aggregate structures
 * - Want to implement filtering, transformation, or complex traversal logic
 * - Need concurrent iterators over same collection
 *
 * COMPONENTS:
 * 1. Iterator (Interface): Defines interface for accessing and traversing elements
 * 2. ConcreteIterator: Implements Iterator interface, keeps track of current position
 * 3. Aggregate (Interface): Defines interface for creating Iterator
 * 4. ConcreteAggregate: Implements Aggregate interface, returns ConcreteIterator
 *
 * KEY CONCEPT:
 * The Iterator pattern decouples the traversal algorithm from the collection structure,
 * allowing you to iterate over different collections using a uniform interface.
 *
 * @author Commission Calculator Educational Project
 */
public class IteratorStructure {

    /**
     * ITERATOR INTERFACE
     *
     * Defines the interface for traversing elements.
     *
     * KEY METHODS:
     * - hasNext(): Check if more elements exist
     * - next(): Get next element and advance
     * - remove(): Optional - remove current element
     *
     * DESIGN NOTES:
     * - Minimal interface for basic iteration
     * - Can be extended with additional methods
     * - Should not expose collection internals
     */
    public interface Iterator<T> {
        /**
         * Check if there are more elements to iterate.
         *
         * @return true if more elements exist, false otherwise
         */
        boolean hasNext();

        /**
         * Get the next element and advance the iterator.
         *
         * @return The next element
         * @throws NoSuchElementException if no more elements
         */
        T next();

        /**
         * Remove the current element (optional operation).
         * May not be supported by all iterators.
         */
        default void remove() {
            throw new UnsupportedOperationException("remove not supported");
        }
    }

    /**
     * AGGREGATE INTERFACE
     *
     * Defines interface for creating iterators.
     * Collections implement this to provide their iterators.
     *
     * KEY RESPONSIBILITY:
     * - Factory method for creating iterators
     * - May provide multiple iterator types
     */
    public interface Aggregate<T> {
        /**
         * Create an iterator for this aggregate.
         *
         * @return Iterator for traversing elements
         */
        Iterator<T> createIterator();
    }

    /**
     * CONCRETE AGGREGATE: Simple Collection
     *
     * Example collection that provides an iterator.
     * Stores items in internal array.
     *
     * IMPLEMENTATION DETAILS:
     * - Hides internal storage structure
     * - Can change implementation without affecting clients
     * - Provides iterator factory method
     */
    public static class SimpleCollection<T> implements Aggregate<T> {
        private final List<T> items;

        public SimpleCollection() {
            this.items = new ArrayList<>();
        }

        public void add(T item) {
            items.add(item);
            System.out.println("  Added: " + item);
        }

        public int size() {
            return items.size();
        }

        @Override
        public Iterator<T> createIterator() {
            return new SimpleIterator();
        }

        /**
         * CONCRETE ITERATOR: Simple Iterator
         *
         * Implements iteration over SimpleCollection.
         * Maintains current position in traversal.
         *
         * KEY RESPONSIBILITIES:
         * - Track current position
         * - Provide access to elements
         * - Handle bounds checking
         */
        private class SimpleIterator implements Iterator<T> {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < items.size();
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more elements");
                }
                return items.get(currentIndex++);
            }

            @Override
            public void remove() {
                if (currentIndex <= 0) {
                    throw new IllegalStateException("next() not yet called");
                }
                items.remove(--currentIndex);
                System.out.println("  Removed element at index " + currentIndex);
            }
        }
    }

    /**
     * ADVANCED EXAMPLE: Reverse Iterator
     *
     * Demonstrates different traversal strategy on same collection.
     */
    public static class ReverseIterator<T> implements Iterator<T> {
        private final List<T> items;
        private int currentIndex;

        public ReverseIterator(List<T> items) {
            this.items = items;
            this.currentIndex = items.size() - 1;
        }

        @Override
        public boolean hasNext() {
            return currentIndex >= 0;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }
            return items.get(currentIndex--);
        }
    }

    /**
     * ADVANCED EXAMPLE: Filtered Iterator
     *
     * Demonstrates iterator with filtering logic.
     * Only returns elements that match a condition.
     */
    public static class FilteredIterator<T> implements Iterator<T> {
        private final List<T> items;
        private final FilterCondition<T> condition;
        private int currentIndex = 0;
        private T nextItem = null;
        private boolean nextItemSet = false;

        public interface FilterCondition<T> {
            boolean matches(T item);
        }

        public FilteredIterator(List<T> items, FilterCondition<T> condition) {
            this.items = items;
            this.condition = condition;
        }

        @Override
        public boolean hasNext() {
            if (!nextItemSet) {
                nextItem = findNext();
                nextItemSet = true;
            }
            return nextItem != null;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }
            T result = nextItem;
            nextItem = null;
            nextItemSet = false;
            return result;
        }

        private T findNext() {
            while (currentIndex < items.size()) {
                T item = items.get(currentIndex++);
                if (condition.matches(item)) {
                    return item;
                }
            }
            return null;
        }
    }

    /**
     * ADVANCED EXAMPLE: Skip Iterator
     *
     * Demonstrates iterator that skips elements (e.g., every other element).
     */
    public static class SkipIterator<T> implements Iterator<T> {
        private final List<T> items;
        private final int skipCount;
        private int currentIndex = 0;

        public SkipIterator(List<T> items, int skipCount) {
            this.items = items;
            this.skipCount = skipCount;
        }

        @Override
        public boolean hasNext() {
            return currentIndex < items.size();
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }
            T result = items.get(currentIndex);
            currentIndex += skipCount + 1;  // Skip N elements
            return result;
        }
    }

    /**
     * ADVANCED EXAMPLE: Composite Collection
     *
     * Demonstrates aggregate that can provide multiple iterator types.
     */
    public static class AdvancedCollection<T> implements Aggregate<T> {
        private final List<T> items;

        public AdvancedCollection() {
            this.items = new ArrayList<>();
        }

        public void add(T item) {
            items.add(item);
        }

        @Override
        public Iterator<T> createIterator() {
            return new ForwardIterator();
        }

        public Iterator<T> createReverseIterator() {
            return new ReverseIterator<>(items);
        }

        public Iterator<T> createFilteredIterator(FilteredIterator.FilterCondition<T> condition) {
            return new FilteredIterator<>(items, condition);
        }

        public Iterator<T> createSkipIterator(int skipCount) {
            return new SkipIterator<>(items, skipCount);
        }

        private class ForwardIterator implements Iterator<T> {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < items.size();
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more elements");
                }
                return items.get(currentIndex++);
            }
        }

        public int size() {
            return items.size();
        }
    }

    /**
     * DEMONSTRATION
     *
     * Shows how the Iterator pattern works.
     */
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         ITERATOR PATTERN - DEMONSTRATION                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        // SCENARIO 1: Basic Iterator Usage
        System.out.println("SCENARIO 1: Basic Iterator\n");
        System.out.println("=".repeat(60));

        SimpleCollection<String> collection = new SimpleCollection<>();
        collection.add("Alpha");
        collection.add("Beta");
        collection.add("Gamma");
        collection.add("Delta");

        System.out.println("\nIterating forward:");
        Iterator<String> iterator = collection.createIterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            System.out.println("  → " + item);
        }

        System.out.println("\n" + "=".repeat(60) + "\n");

        // SCENARIO 2: Multiple Simultaneous Iterators
        System.out.println("SCENARIO 2: Multiple Simultaneous Iterators\n");
        System.out.println("=".repeat(60));

        SimpleCollection<Integer> numbers = new SimpleCollection<>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
        numbers.add(4);
        numbers.add(5);

        Iterator<Integer> iter1 = numbers.createIterator();
        Iterator<Integer> iter2 = numbers.createIterator();

        System.out.println("Iterator 1 - First two elements:");
        System.out.println("  → " + iter1.next());
        System.out.println("  → " + iter1.next());

        System.out.println("\nIterator 2 - First three elements:");
        System.out.println("  → " + iter2.next());
        System.out.println("  → " + iter2.next());
        System.out.println("  → " + iter2.next());

        System.out.println("\nIterator 1 - Continue:");
        while (iter1.hasNext()) {
            System.out.println("  → " + iter1.next());
        }

        System.out.println("\n" + "=".repeat(60) + "\n");

        // SCENARIO 3: Different Iterator Types
        System.out.println("SCENARIO 3: Different Iterator Types\n");
        System.out.println("=".repeat(60));

        AdvancedCollection<String> advanced = new AdvancedCollection<>();
        advanced.add("One");
        advanced.add("Two");
        advanced.add("Three");
        advanced.add("Four");
        advanced.add("Five");

        System.out.println("Forward iteration:");
        Iterator<String> forward = advanced.createIterator();
        while (forward.hasNext()) {
            System.out.println("  → " + forward.next());
        }

        System.out.println("\nReverse iteration:");
        Iterator<String> reverse = advanced.createReverseIterator();
        while (reverse.hasNext()) {
            System.out.println("  ← " + reverse.next());
        }

        System.out.println("\nSkip iteration (every other):");
        Iterator<String> skip = advanced.createSkipIterator(1);
        while (skip.hasNext()) {
            System.out.println("  → " + skip.next());
        }

        System.out.println("\n" + "=".repeat(60) + "\n");

        // SCENARIO 4: Filtered Iterator
        System.out.println("SCENARIO 4: Filtered Iterator\n");
        System.out.println("=".repeat(60));

        AdvancedCollection<Integer> moreNumbers = new AdvancedCollection<>();
        for (int i = 1; i <= 10; i++) {
            moreNumbers.add(i);
        }

        System.out.println("All numbers: 1-10");
        System.out.println("\nFiltered - Even numbers only:");
        Iterator<Integer> evenIterator = moreNumbers.createFilteredIterator(
            n -> n % 2 == 0
        );
        while (evenIterator.hasNext()) {
            System.out.println("  → " + evenIterator.next());
        }

        System.out.println("\nFiltered - Greater than 5:");
        Iterator<Integer> greaterThan5 = moreNumbers.createFilteredIterator(
            n -> n > 5
        );
        while (greaterThan5.hasNext()) {
            System.out.println("  → " + greaterThan5.next());
        }

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Summary
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                      KEY OBSERVATIONS                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("1. UNIFORM INTERFACE");
        System.out.println("   → All iterators use same hasNext()/next() interface");
        System.out.println("   → Different collections can be traversed uniformly");
        System.out.println("   → Client code doesn't know internal structure");
        System.out.println();
        System.out.println("2. ENCAPSULATION");
        System.out.println("   → Collection's internal structure is hidden");
        System.out.println("   → Can change implementation without affecting clients");
        System.out.println("   → Iterator handles traversal logic");
        System.out.println();
        System.out.println("3. MULTIPLE TRAVERSALS");
        System.out.println("   → Multiple iterators can traverse same collection");
        System.out.println("   → Each iterator maintains its own position");
        System.out.println("   → Concurrent iteration is safe");
        System.out.println();
        System.out.println("4. DIFFERENT ITERATION STRATEGIES");
        System.out.println("   → Forward, reverse, skip, filtered");
        System.out.println("   → Same collection, different traversal algorithms");
        System.out.println("   → Easy to add new iteration strategies");
        System.out.println();
        System.out.println("5. SEPARATION OF CONCERNS");
        System.out.println("   → Collection: Stores data");
        System.out.println("   → Iterator: Handles traversal");
        System.out.println("   → Clear, focused responsibilities");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
    }
}