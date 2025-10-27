package com.chapman.edu.commissions.patterns.structural.composite;

import java.math.BigDecimal;

/**
 * CompositePatternStructure defines the core interfaces for the Composite Pattern.
 *
 * ## Composite Pattern Overview
 * The Composite Pattern is a structural design pattern that lets you compose objects into
 * tree structures to represent part-whole hierarchies. This pattern allows clients to treat
 * individual objects and compositions of objects uniformly.
 *
 * ### Key Components:
 * - **Component**: Declares the interface for objects in the composition
 * - **Leaf**: Represents leaf objects (no children) in the composition
 * - **Composite**: Stores child components and implements child-related operations
 * - **Client**: Manipulates objects through the Component interface
 *
 * ### Benefits:
 * - Simplifies client code by treating objects uniformly
 * - Makes it easier to add new kinds of components
 * - Provides flexibility in creating complex tree structures
 *
 * ### Application in Commission Calculator:
 * In the commission calculator domain, we use the Composite Pattern to model:
 * - **ProductItem** (Leaf): Individual products in a deal
 * - **SalesDeal** (Composite): A collection of products and/or nested deals
 * - **SalesComponent** (Component): Common interface for both
 *
 * This allows us to calculate commissions on individual products or entire deal hierarchies
 * using the same interface, simplifying the commission calculation logic.
 */
public class CompositePatternStructure {

    /**
     * Component - The base interface that defines operations common to both simple (leaf)
     * and complex (composite) elements of the composition.
     *
     * This is the foundation of the Composite Pattern. It declares the interface that
     * all objects in the composition tree must implement, enabling clients to work with
     * both individual objects and compositions uniformly.
     *
     * **Key Principle:** By programming to this interface rather than concrete classes,
     * clients don't need to know whether they're dealing with a leaf or composite object.
     */
    public interface Component {
        /**
         * Operation that both leaf and composite objects must implement.
         *
         * For leaf objects, this method performs the operation directly.
         * For composite objects, this method typically delegates to all children
         * and combines their results.
         *
         * @return the result of the operation as a double
         */
        double operation();
    }

    /**
     * SalesComponent - The component interface for the commission calculator domain.
     * This interface defines operations common to both individual products (leaves)
     * and deals (composites containing products and/or nested deals).
     *
     * **Domain Application:**
     * - **ProductItem** (Leaf): Calculates value for a single product
     * - **SalesDeal** (Composite): Calculates total value of all contained products/deals
     *
     * This allows the system to calculate commissions uniformly, whether dealing with
     * a single product sale or a complex multi-level deal structure.
     */
    public interface SalesComponent {
        /**
         * Calculate the monetary value of this sales component.
         *
         * **Polymorphic Behavior:**
         * - For ProductItem (Leaf): Returns (price * quantity) - discount
         * - For SalesDeal (Composite): Returns sum of all child component values
         *
         * This demonstrates the power of the Composite Pattern - the same method call
         * works recursively through the entire object tree, automatically handling complexity.
         * @return the calculated value as a BigDecimal
         */
        BigDecimal calculateValue();

        /**
         * Get the name/title of this sales component.
         *
         * Provides a human-readable identifier for reporting and display purposes.
         * Works uniformly for both individual products and deal collections.
         * @return the name as a String
         */
        String getName();
    }
}