package com.chapman.edu.commissions.patterns.structural.composite;

/**
 * This class demonstrates the structure of the Composite Pattern.
 * 
 * The Composite Pattern is a structural design pattern that lets you compose objects
 * into tree structures to represent part-whole hierarchies. It lets clients treat
 * individual objects and compositions of objects uniformly.
 */
public class CompositePatternStructure {

    /**
     * Component - The interface that defines operations common to both simple and complex elements
     * of the composition.
     */
    public interface Component {
        /**
         * Operation that both leaf and composite objects must implement.
         */
        double operation();
    }
    /**
     * Leaf - Represents individual objects in the composition that have no children.
     */
    public static class Leaf implements Component {
        private double value;
        public Leaf(double value) {
            this.value = value;
        }
        /**
         * Implementation of the operation for a leaf object.
         */
        @Override
        public double operation() {
            return value;
        }
    }
    /**
     * Composite - Represents complex objects that may have children. Composites store
     * child components and delegate operations to them.
     */
    public static class Composite implements Component {
        private java.util.List<Component> children = new java.util.ArrayList<>();
        private String name;
        /**
         * Implementation of the operation for a composite object.
         * Typically, this delegates the operation to all children and combines the results.
         */
        @Override
        public double operation() {
            double sum = 0;
            for (Component child : children) {
                sum += child.operation();
            }
            return sum;
        }

        public Composite(String name) {
            this.name = name;
        }

        /**
         * Add a child component to this composite.
         */
        public void add(Component component) {
            children.add(component);
        }

        /**
         * Remove a child component from this composite.
         */
        public void remove(Component component) {
            children.remove(component);
        }

        /**
         * Get all child components.
         */
        public java.util.List<Component> getChildren() {
            return children;
        }
    }

    /**
     * Client - Uses the component interface to interact with objects in the composition.
     */
    public static class Client {
        /**
         * The client works with all components through the component interface.
         */
        public void doSomething(Component component) {
            System.out.println("Result: " + component.operation());
        }
    }

    /**
     * Example of how the Composite Pattern structure works.
     */
    public static void main(String[] args) {
        // Create leaf objects
        Leaf leaf1 = new Leaf(5);
        Leaf leaf2 = new Leaf(10);
        Leaf leaf3 = new Leaf(15);

        // Create composite objects
        Composite composite1 = new Composite("Composite 1");
        Composite composite2 = new Composite("Composite 2");
        Composite root = new Composite("Root");

        // Build the tree structure
        composite1.add(leaf1);
        composite1.add(leaf2);
        
        composite2.add(leaf3);
        
        root.add(composite1);
        root.add(composite2);

        // Client uses components
        Client client = new Client();
        
        // Client can work with leaf objects
        System.out.println("Client working with leaf:");
        client.doSomething(leaf1);
        
        // Client can also work with composite objects the same way
        System.out.println("Client working with composite:");
        client.doSomething(composite1);
        
        // Client can work with the entire tree
        System.out.println("Client working with the entire tree:");
        client.doSomething(root);
    }
}