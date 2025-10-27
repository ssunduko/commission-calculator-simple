package com.chapman.edu.commissions.patterns.structural.composite;


import com.chapman.edu.commissions.patterns.structural.composite.CompositePatternStructure.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * CompositePatternImplementation provides concrete implementations of the Composite Pattern
 * for the commission calculator domain.
 *
 * This class contains both generic pattern implementations and domain-specific examples
 * that demonstrate how the Composite Pattern solves real-world problems in sales commission
 * calculations.
 *
 * ### Pattern Implementation Structure:
 * 1. **Domain-Specific Implementation**: ProductItem (Leaf) and SalesDeal (Composite)
 * 2. **Generic Implementation**: Leaf and Composite classes
 * 3. **Client Examples**: SalesReport and Client classes
 *
 * @see CompositePatternStructure for interface definitions
 * @see CompositePatternUsage for usage examples
 */
public class CompositePatternImplementation {


    /**
     * ProductItem - LEAF implementation in the Composite Pattern.
     *
     * Represents an individual product in the sales hierarchy. This is a "leaf" node
     * because it has no children - it's the smallest unit in the composition.
     *
     * #### Role in Composite Pattern:
     * - Implements the SalesComponent interface
     * - Defines behavior for primitive objects (no children)
     * - Performs actual work (calculates its own value)
     * - Cannot contain other components
     *
     * #### Domain Context:
     * Analogous to the DealProduct class in the model. Represents a single line item
     * in a sales transaction with its own price, quantity, and discount.
     *
     * #### Key Characteristics of a Leaf:
     * - No add/remove methods (can't have children)
     * - Implements operations directly without delegation
     * - Represents the end nodes in the object tree
     */
    public static class ProductItem implements SalesComponent {
        // Getters and setters
        @Setter
        @Getter
        private String productId;
        private final String productName;
        @Setter
        @Getter
        private int quantity;
        @Setter
        @Getter
        private BigDecimal price;
        @Setter
        @Getter
        private BigDecimal discount;

        /**
         * Constructor for creating a product item without a discount.
         *
         * @param productId   unique identifier for the product
         * @param productName human-readable name of the product
         * @param quantity    number of units being sold
         * @param price       unit price of the product
         */
        public ProductItem(String productId, String productName, int quantity, BigDecimal price) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
            this.discount = BigDecimal.ZERO;
        }

        /**
         * Constructor for creating a product item with a discount.
         *
         * @param productId   unique identifier for the product
         * @param productName human-readable name of the product
         * @param quantity    number of units being sold
         * @param price       unit price of the product
         * @param discount    discount amount to be subtracted from total
         */
        public ProductItem(String productId, String productName, int quantity, BigDecimal price, BigDecimal discount) {
            this(productId, productName, quantity, price);
            this.discount = discount;
        }

        /**
         * Calculate the total value of this product item.
         *
         * **LEAF BEHAVIOR:** This is a leaf node, so it calculates its own value directly
         * without delegating to any children (because it has none).
         *
         * Formula: (price × quantity) - discount
         *
         * **Composite Pattern Note:** Compare this to SalesDeal.calculateValue() which
         * delegates to children. This demonstrates the fundamental difference between
         * leaf and composite implementations.
         *
         * @return the calculated value of this product item
         */
        @Override
        public BigDecimal calculateValue() {
            return price.multiply(new BigDecimal(quantity)).subtract(discount);
        }

        /**
         * Get the name of this product.
         *
         * Part of the SalesComponent interface, enabling uniform access to names
         * for both products and deals.
         *
         * @return the product name
         */
        @Override
        public String getName() {
            return productName;
        }

    }

    /**
     * SalesDeal - COMPOSITE implementation in the Composite Pattern.
     *
     * Represents a collection of products and/or nested deals. This is a "composite" node
     * because it can contain children - both ProductItem (leaves) and other SalesDeal objects
     * (nested composites).
     *
     * #### Role in Composite Pattern:
     * 
         * - Implements the SalesComponent interface (same as ProductItem)
         * - Defines behavior for components having children
         * - Stores child components in a collection
         * - Delegates operations to children and combines results
         * - Provides methods to add/remove children
     * 
     *
     * #### Domain Context:
     * Analogous to the Deal class in the model. Represents a sales transaction that
     * can contain multiple product line items and/or references to other deals (e.g.,
     * a master deal containing sub-deals for different departments).
     *
     * #### Key Characteristics of a Composite:
     * 
         * - Has add/remove methods for managing children
         * - Implements operations by delegating to children
         * - Can contain both leaves (ProductItem) and other composites (SalesDeal)
         * - Enables recursive tree structures of arbitrary depth
     * 
     *
     * #### Recursive Structure Example:
     * <pre>
     * Enterprise Deal (SalesDeal)
     *   ├─ Server (ProductItem) - LEAF
     *   ├─ Workstation Package (SalesDeal) - COMPOSITE
     *   │   ├─ Laptop (ProductItem) - LEAF
     *   │   ├─ Monitor (ProductItem) - LEAF
     *   │   └─ Software (ProductItem) - LEAF
     *   └─ Printer (ProductItem) - LEAF
     * </pre>
     */
    public static class SalesDeal implements SalesComponent {
        // Getters and setters
        @Setter
        @Getter
        private String id;
        private String title;
        /**
         * The collection of child components in this deal.
         *
         * **CRITICAL COMPOSITE PATTERN FEATURE:** This list can contain both
         * ProductItem objects (leaves) and other SalesDeal objects (nested composites).
         * This enables building complex hierarchical structures.
         *
         * The ability to store both types through the SalesComponent interface
         * is what makes the Composite Pattern powerful - clients don't need to
         * distinguish between leaf and composite objects.
         */
        @Getter
        private List<SalesComponent> components = new ArrayList<>();
        @Setter
        @Getter
        private String salesRepId;

        /**
         * Constructor for creating a sales deal.
         *
         * @param title       the title/name of this deal
         * @param salesRepId  the ID of the sales representative
         */
        public SalesDeal(String title, String salesRepId) {
            this.title = title;
            this.salesRepId = salesRepId;
        }

        /**
         * Add a component (product or nested deal) to this deal.
         *
         * **COMPOSITE PATTERN METHOD:** This is a defining feature of composites.
         * Only composites have this method - leaves do not.
         *
         * This method accepts any SalesComponent, which means it can add:
         * 
         * - ProductItem objects (leaves)
         * - Other SalesDeal objects (nested composites)
         * 
         *
         * This enables building arbitrarily complex tree structures.
         *
         * @param component the component to add (can be ProductItem or SalesDeal)
         */
        public void addComponent(SalesComponent component) {
            components.add(component);
        }

        /**
         * Remove a component from this deal.
         *
         * Allows dynamic modification of the composite structure.
         *
         * @param component the component to remove
         */
        public void removeComponent(SalesComponent component) {
            components.remove(component);
        }

        /**
         * Calculate the total value of this deal by summing all contained components.
         *
         * **COMPOSITE BEHAVIOR:** This is the key difference from ProductItem.
         * Instead of calculating directly, this method delegates to all child components
         * and aggregates their results.
         *
         * **How It Works:**
         * 
         * - Iterates through all child components
         * - Calls calculateValue() on each (polymorphic call)
         * - Each child could be:
         *     
         * - A ProductItem that returns its own value
         * - A SalesDeal that recursively calculates its children's values
         *
         * - Sums all returned values
         *
         * **Recursive Magic:** If this deal contains nested deals, those deals
         * will recursively call calculateValue() on their children, automatically
         * handling trees of any depth without the client needing to know the structure.
         *
         * **Example Calculation:**
         * <pre>
         * Enterprise Deal.calculateValue()
         *   → Server.calculateValue() = $3000
         *   → Workstation Package.calculateValue()
         *       → Laptop.calculateValue() = $2400
         *       → Monitor.calculateValue() = $700
         *       → Software.calculateValue() = $400
         *       → Returns $3500
         *   → Printer.calculateValue() = $450
         *   → Returns $3000 + $3500 + $450 = $6950
         * </pre>
         *
         * @return the total value of all components in this deal
         */
        @Override
        public BigDecimal calculateValue() {
            return components.stream()
                    .map(SalesComponent::calculateValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        /**
         * Get the title of this deal.
         *
         * Part of the SalesComponent interface, enabling uniform access to names
         * for both products and deals.
         *
         * @return the deal title
         */
        @Override
        public String getName() {
            return title;
        }

    }

    /**
     * SalesReport - CLIENT class in the Composite Pattern.
     *
     * Demonstrates how client code works with the component interface uniformly,
     * without needing to distinguish between leaf and composite objects.
     *
     * #### Role in Composite Pattern:
     * 
         * - Works exclusively through the SalesComponent interface
         * - Treats ProductItem and SalesDeal objects uniformly
         * - Doesn't need to know the internal structure of composites
     * 
     *
     * #### Key Benefit:
     * The client can call the same method (generateReport) whether it's given
     * a single product or a complex deal with nested sub-deals. The polymorphic
     * behavior handles all complexity automatically.
     */
    public static class SalesReport {
        /**
         * Generate a report for any sales component (product or deal).
         *
         * **UNIFORM TREATMENT:** This method accepts any SalesComponent,
         * demonstrating the core benefit of the Composite Pattern. The same
         * method works for:
         * 
         * - Individual products (ProductItem)
         * - Simple deals with only products
         * - Complex deals with nested sub-deals
         * 
         *
         * The client doesn't need to check the type or write different logic
         * for different scenarios - the polymorphic calculateValue() method
         * handles everything automatically.
         *
         * **Optional Type Checking:** While we can check if a component
         * is a composite (instanceof SalesDeal) to show additional details,
         * this is optional and only for enhanced reporting. The core functionality
         * (getting name and value) works uniformly without type checking.
         *
         * @param component the sales component to generate a report for
         */
        public void generateReport(SalesComponent component) {
            System.out.println("Sales Report for: " + component.getName());
            System.out.println("Total Value: $" + component.calculateValue());

            // Optional: If it's a composite, we can also show details about its components
            // This demonstrates that while the pattern enables uniform treatment,
            // we can still access composite-specific features when needed
            if (component instanceof SalesDeal deal) {
                System.out.println("Components:");
                for (SalesComponent subComponent : deal.getComponents()) {
                    System.out.println("  - " + subComponent.getName() + ": $" + subComponent.calculateValue());
                }
            }
            System.out.println();
        }
    }
    // ========== GENERIC COMPOSITE PATTERN IMPLEMENTATION ==========
    // The following classes demonstrate the basic structure of the Composite Pattern
    // without domain-specific details. Compare these to the domain-specific
    // ProductItem and SalesDeal classes above.

    /**
     * Leaf - Generic LEAF implementation of the Composite Pattern.
     *
     * This is a simplified example showing the bare minimum for a leaf object.
     * Leaves are the building blocks of the composition - they do actual work
     * and have no children.
     *
     * #### Characteristics:
     * 
         * - Implements the Component interface
         * - No child management methods (add/remove)
         * - Performs operations directly without delegation
         * - Represents the end nodes in the tree structure
     * 
     *
     * **Compare to:** ProductItem is a domain-specific version of this pattern
     */
    public static class Leaf implements Component {
        private final double value;

        /**
         * Create a leaf with a specific value.
         *
         * @param value the value this leaf represents
         */
        public Leaf(double value) {
            this.value = value;
        }

        /**
         * Implementation of the operation for a leaf object.
         *
         * **LEAF BEHAVIOR:** Returns the value directly without delegating
         * to any children (because leaves have no children).
         *
         * @return the value of this leaf
         */
        @Override
        public double operation() {
            return value;
        }
    }

    /**
     * Composite - Generic COMPOSITE implementation of the Composite Pattern.
     *
     * This class shows the core structure of a composite object that can contain
     * child components. Composites can contain both Leaf objects and other Composite
     * objects, enabling recursive tree structures.
     *
     * #### Characteristics:
     * 
         * - Implements the Component interface (same as Leaf)
         * - Stores a collection of child components
         * - Provides add/remove methods for managing children
         * - Delegates operations to all children and combines results
         * - Can contain both leaves and other composites
     * 
     *
     * **Key Pattern Feature:** The children list is typed as Component,
     * not Composite or Leaf. This allows it to hold both types, which is essential
     * for building flexible tree structures.
     *
     * **Compare to:** SalesDeal is a domain-specific version of this pattern
     */
    public static class Composite implements Component {
        /**
         * Collection of child components.
         *
         * **IMPORTANT:** This is typed as Component (the interface), not as
         * concrete types. This enables polymorphic storage of both Leaf and Composite
         * objects in the same collection.
         */
        @Getter
        private final List<Component> children = new ArrayList<>();
        private String name;

        /**
         * Create a composite with a given name.
         *
         * @param name identifier for this composite
         */
        public Composite(String name) {
            this.name = name;
        }

        /**
         * Implementation of the operation for a composite object.
         *
         * **COMPOSITE BEHAVIOR:** Delegates the operation to all children
         * and combines their results. This is fundamentally different from Leaf.operation()
         * which computes directly.
         *
         * **Recursive Processing:**
         * 
         * - Iterates through all children
         * - Calls operation() on each child (polymorphic call)
         * - If child is a Leaf: returns its value directly
         * - If child is a Composite: recursively processes its children
         * - Sums all results
         * 
         *
         * This recursive delegation is the "magic" that makes the Composite Pattern work.
         * The client just calls operation() once, and the pattern handles traversing
         * the entire tree automatically.
         *
         * @return the sum of all child operation results
         */
        @Override
        public double operation() {
            double sum = 0;
            for (CompositePatternStructure.Component child : children) {
                sum += child.operation();
            }
            return sum;
        }

        /**
         * Add a child component to this composite.
         *
         * **COMPOSITE-ONLY METHOD:** This method is what distinguishes composites
         * from leaves. Only composites can have children added to them.
         *
         * Accepts any Component, meaning you can add:
         * 
         * - Leaf objects (terminal nodes)
         * - Other Composite objects (creating nested hierarchies)
         * 
         *
         * @param component the component to add (can be Leaf or Composite)
         */
        public void add(Component component) {
            children.add(component);
        }

        /**
         * Remove a child component from this composite.
         *
         * Allows dynamic modification of the tree structure at runtime.
         *
         * @param component the component to remove
         */
        public void remove(Component component) {
            children.remove(component);
        }

    }

    /**
     * Client - Generic CLIENT implementation showing how to use the Composite Pattern.
     *
     * This class demonstrates the primary benefit of the Composite Pattern:
     * the client can work with individual objects and compositions uniformly
     * through the Component interface.
     *
     * #### Key Principle:
     * The client doesn't know (or care) whether it's dealing with a Leaf or Composite.
     * It just calls methods on the Component interface, and polymorphism ensures the
     * correct behavior is executed.
     *
     * #### Benefits for Client Code:
     * 
         * - Simplified code - no need to check types or write conditional logic
         * - Same method works for simple and complex structures
         * - Easy to add new component types without changing client code
     * 
     *
     * **Compare to:** SalesReport is a domain-specific version of this client
     */
    public static class Client {
        /**
         * Work with any component through the unified interface.
         *
         * **UNIFORM TREATMENT:** This method accepts any Component and calls
         * operation() on it. The same code works whether the component is:
         * 
         * - A single Leaf object
         * - A Composite containing multiple Leafs
         * - A Composite containing nested Composites
         * 
         *
         * This is the essence of the Composite Pattern - the client treats
         * individual objects and compositions identically.
         *
         * @param component the component to operate on (Leaf or Composite)
         */
        public void doSomething(Component component) {
            System.out.println("Result: " + component.operation());
        }
    }
}