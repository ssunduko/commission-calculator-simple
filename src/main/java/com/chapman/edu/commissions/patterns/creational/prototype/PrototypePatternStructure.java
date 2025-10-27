package com.chapman.edu.commissions.patterns.creational.prototype;

/**
 * PROTOTYPE PATTERN STRUCTURE
 *
 * Defines the core interface for the Prototype design pattern.
 *
 * PATTERN PURPOSE:
 * The Prototype pattern specifies the kinds of objects to create using a prototypical instance,
 * and creates new objects by copying this prototype rather than using constructors.
 *
 * KEY CONCEPT:
 * Instead of using "new" keyword and constructors, client asks an existing object (prototype)
 * to clone itself. This avoids tight coupling to concrete classes and complex initialization.
 *
 * PROBLEM SOLVED:
 * - Avoid expensive object creation (complex initialization, database queries, etc.)
 * - Decouple client code from concrete classes
 * - Create object copies without knowing their concrete types
 * - Reduce subclassing for object configuration variations
 *
 * PATTERN COMPONENTS:
 * - Prototype Interface: Declares clone() method (this file)
 * - Concrete Prototypes: Implement clone() to copy themselves
 * - Client: Creates new objects by cloning prototypes
 * - Registry (Optional): Stores and manages commonly used prototypes
 *
 * @see com.chapman.edu.commissions.patterns.creational.prototype.PrototypePatternImplementation
 * @see com.chapman.edu.commissions.patterns.creational.prototype.PrototypeRegistry
 */
public class PrototypePatternStructure {

    /**
     * PROTOTYPE INTERFACE
     *
     * The Prototype interface declares the cloning method that all prototypes must implement.
     *
     * GENERIC TYPE PARAMETER:
     * <T> allows each concrete prototype to return its own type from clone(),
     * eliminating the need for casting and providing type safety.
     *
     * DESIGN DECISIONS:
     * - Generic interface (Prototype<T>) for type-safe cloning
     * - Single method (clone) keeps interface simple
     * - Return type T ensures subclasses return correct type
     *
     * USAGE PATTERN:
     *   public class MyClass implements Prototype<MyClass> {
     *       public MyClass clone() {
     *           // Create and return copy
     *       }
     *   }
     *
     * BENEFITS:
     * - No casting needed: MyClass copy = original.clone();
     * - Compile-time type safety
     * - IDE support with autocomplete
     * - Clear contract for all prototypes
     *
     * @param <T> The type of the prototype (allows type-safe cloning)
     */
    public interface Prototype<T> {
        /**
         * Creates and returns a copy of this object.
         *
         * CLONING CONTRACT:
         * - Must create a new instance
         * - Should copy all relevant state
         * - Must return same type as implementing class
         * - Original object should remain unchanged
         *
         * IMPLEMENTATION STRATEGIES:
         *
         * 1. SHALLOW COPY:
         *    - Copy primitive fields and references
         *    - Referenced objects are shared between original and clone
         *    - Fast but modifications to referenced objects affect both
         *    - Use when: Referenced objects are immutable or won't change
         *
         * 2. DEEP COPY:
         *    - Copy primitive fields
         *    - Clone all referenced objects recursively
         *    - Complete independence between original and clone
         *    - Slower but safer
         *    - Use when: Need complete independence from original
         *
         * EXAMPLE IMPLEMENTATION:
         * <pre>
         * public MyClass clone() {
         *     MyClass copy = new MyClass();
         *     copy.primitiveField = this.primitiveField;           // Direct copy
         *     copy.immutableField = this.immutableField;           // Safe to share
         *     copy.mutableField = this.mutableField.clone();       // Deep copy
         *     return copy;
         * }
         * </pre>
         *
         * @return A copy of this object with the same type T
         */
        T clone();
    }
}
