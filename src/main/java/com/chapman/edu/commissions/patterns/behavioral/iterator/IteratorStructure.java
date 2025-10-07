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
 */
public class IteratorStructure {

    /**
     * GENERIC ITERATOR INTERFACE
     *
     * Base interface for all commission system iterators.
     * Extends with additional metadata methods useful for reporting.
     */
    public interface CommissionIterator<T> {
        boolean hasNext();
        T next();
        int remaining();  // Additional: How many items left
        void reset();     // Additional: Reset to beginning
    }
}