package com.chapman.edu.commissions.patterns.behavioral.template;

/**
 * TEMPLATE METHOD PATTERN - STRUCTURAL DEMONSTRATION
 *
 * PURPOSE:
 * The Template Method Pattern defines the skeleton of an algorithm in a base class,
 * but lets subclasses override specific steps of the algorithm without changing its structure.
 * The template method calls a series of steps, some of which are abstract and must be
 * implemented by subclasses, while others have default implementations.
 *
 * PROBLEM IT SOLVES:
 * - Eliminates code duplication when multiple algorithms share the same structure
 * - Enforces a consistent algorithm structure across related classes
 * - Allows controlled variation points where subclasses can customize behavior
 * - Makes the algorithm's invariant parts explicit (what MUST happen)
 * - Makes the variant parts explicit (what CAN be customized)
 *
 * WHEN TO USE:
 * - Multiple classes implement similar algorithms with the same structure
 * - You want to control which parts of an algorithm can be overridden
 * - You want to avoid code duplication across similar operations
 * - You need to enforce a specific sequence of operations
 * - Common behavior should be localized in a single class
 *
 * COMPONENTS:
 * 1. Abstract Class: Defines the template method and declares abstract/hook methods
 * 2. Template Method: Defines the algorithm skeleton (final to prevent override)
 * 3. Abstract Methods: Steps that MUST be implemented by subclasses (required variation)
 * 4. Hook Methods: Steps that MAY be overridden by subclasses (optional variation)
 * 5. Concrete Methods: Steps with default implementation (invariant behavior)
 * 6. Concrete Subclasses: Implement abstract methods and optionally override hooks
 *
 * KEY PRINCIPLE:
 * "Hollywood Principle" - Don't call us, we'll call you.
 * The parent class calls the subclass methods, not the other way around.
 *
 * @author Commission Calculator Educational Project
 */
public class TemplateStructure {

    /**
     * ABSTRACT CLASS - TEMPLATE
     *
     * Defines the skeleton of the algorithm in the template method.
     * Contains the template method (final) and declares abstract/hook methods.
     *
     * KEY CHARACTERISTICS:
     * - Template method is final to prevent subclasses from changing the algorithm structure
     * - Contains abstract methods that subclasses MUST implement
     * - Contains hook methods that subclasses MAY override
     * - Contains concrete methods with default behavior
     */
    public abstract static class AbstractClass {

        /**
         * TEMPLATE METHOD
         *
         * This is the core of the Template Method Pattern.
         * It defines the skeleton of the algorithm - the sequence of steps.
         *
         * KEY CHARACTERISTICS:
         * - Declared as final so subclasses cannot override it
         * - Calls a series of methods that implement the algorithm steps
         * - Some steps are concrete (defined here)
         * - Some steps are abstract (must be implemented by subclasses)
         * - Some steps are hooks (can be optionally overridden)
         *
         * This method orchestrates the entire algorithm.
         */
        public final void templateMethod() {
            System.out.println("=== TEMPLATE METHOD EXECUTION ===\n");
            System.out.println("Starting algorithm execution...\n");

            // Step 1: Concrete method - same for all subclasses
            stepOne();

            // Step 2: Abstract method - MUST be implemented by subclasses
            stepTwo();

            // Step 3: Hook method - CAN be overridden by subclasses (optional)
            if (shouldExecuteStepThree()) {
                stepThree();
            }

            // Step 4: Abstract method - MUST be implemented by subclasses
            stepFour();

            // Step 5: Hook method with default behavior
            stepFive();

            // Step 6: Concrete method - same for all subclasses
            stepSix();

            System.out.println("\nAlgorithm execution completed!");
            System.out.println("=== END TEMPLATE METHOD ===\n");
        }

        /**
         * CONCRETE METHOD - Invariant Step
         *
         * This step has the same implementation for all subclasses.
         * Subclasses cannot override this (could be final if needed).
         */
        protected void stepOne() {
            System.out.println("[Step 1] Common initialization (concrete method)");
            System.out.println("         → Same for all subclasses");
        }

        /**
         * ABSTRACT METHOD - Required Variation Point
         *
         * Subclasses MUST provide an implementation for this step.
         * This is a variation point where different subclasses provide different behavior.
         */
        protected abstract void stepTwo();

        /**
         * HOOK METHOD - Conditional Execution
         *
         * This hook determines whether step three should be executed.
         * Default returns true, but subclasses can override to customize.
         *
         * KEY: This is a HOOK because it has a default implementation but can be overridden.
         */
        protected boolean shouldExecuteStepThree() {
            return true; // Default: execute step three
        }

        /**
         * HOOK METHOD - Optional Variation Point
         *
         * This step has a default implementation but can be overridden.
         * If a subclass doesn't override it, the default behavior is used.
         *
         * KEY: This is a HOOK because it's optional to override.
         */
        protected void stepThree() {
            System.out.println("[Step 3] Default optional processing (hook method)");
            System.out.println("         → Can be overridden, or skip by returning false from shouldExecuteStepThree()");
        }

        /**
         * ABSTRACT METHOD - Required Variation Point
         *
         * Another step that MUST be implemented by subclasses.
         */
        protected abstract void stepFour();

        /**
         * HOOK METHOD - Optional Variation Point with Default
         *
         * Has a meaningful default implementation, but subclasses can customize.
         */
        protected void stepFive() {
            System.out.println("[Step 5] Default finalization (hook method)");
            System.out.println("         → Has default behavior, can be customized");
        }

        /**
         * CONCRETE METHOD - Invariant Step
         *
         * Final cleanup that's the same for all subclasses.
         */
        protected void stepSix() {
            System.out.println("[Step 6] Common cleanup (concrete method)");
            System.out.println("         → Same for all subclasses");
        }
    }

    /**
     * CONCRETE SUBCLASS A
     *
     * Implements the abstract methods and optionally overrides hook methods.
     * Provides specific behavior for steps 2 and 4.
     */
    public static class ConcreteClassA extends AbstractClass {

        @Override
        protected void stepTwo() {
            System.out.println("[Step 2] ConcreteClassA specific processing (implemented abstract method)");
            System.out.println("         → Implementation A: Processing type A data");
        }

        @Override
        protected void stepFour() {
            System.out.println("[Step 4] ConcreteClassA specific validation (implemented abstract method)");
            System.out.println("         → Implementation A: Validating with rules A");
        }

        // Note: stepThree, stepFive use default implementations (hooks not overridden)
        // Note: shouldExecuteStepThree returns true (default), so stepThree will execute
    }

    /**
     * CONCRETE SUBCLASS B
     *
     * Implements the abstract methods AND overrides some hook methods.
     * Demonstrates customizing optional steps.
     */
    public static class ConcreteClassB extends AbstractClass {

        @Override
        protected void stepTwo() {
            System.out.println("[Step 2] ConcreteClassB specific processing (implemented abstract method)");
            System.out.println("         → Implementation B: Processing type B data");
        }

        @Override
        protected void stepFour() {
            System.out.println("[Step 4] ConcreteClassB specific validation (implemented abstract method)");
            System.out.println("         → Implementation B: Validating with rules B");
        }

        // Override hook method to customize behavior
        @Override
        protected void stepThree() {
            System.out.println("[Step 3] ConcreteClassB CUSTOM optional processing (overridden hook)");
            System.out.println("         → Implementation B: Custom preprocessing");
        }

        // Override hook method to customize behavior
        @Override
        protected void stepFive() {
            System.out.println("[Step 5] ConcreteClassB CUSTOM finalization (overridden hook)");
            System.out.println("         → Implementation B: Custom cleanup");
        }
    }

    /**
     * CONCRETE SUBCLASS C
     *
     * Implements the abstract methods and uses the hook to skip step three.
     * Demonstrates conditional step execution.
     */
    public static class ConcreteClassC extends AbstractClass {

        @Override
        protected void stepTwo() {
            System.out.println("[Step 2] ConcreteClassC specific processing (implemented abstract method)");
            System.out.println("         → Implementation C: Fast-path processing");
        }

        @Override
        protected void stepFour() {
            System.out.println("[Step 4] ConcreteClassC specific validation (implemented abstract method)");
            System.out.println("         → Implementation C: Minimal validation");
        }

        // Override hook to SKIP step three
        @Override
        protected boolean shouldExecuteStepThree() {
            System.out.println("[Hook]   ConcreteClassC skipping step 3 (hook returns false)");
            return false; // Skip step three for this implementation
        }

        // Step three won't be called because shouldExecuteStepThree() returns false
    }

    /**
     * DEMONSTRATION
     *
     * Shows how the Template Method Pattern works with different implementations.
     */
    public static void main(String[] args) {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║     TEMPLATE METHOD PATTERN - STRUCTURE DEMONSTRATION     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        System.out.println("SCENARIO 1: ConcreteClassA (uses default hooks)\n");
        System.out.println("-".repeat(60));
        AbstractClass objectA = new ConcreteClassA();
        objectA.templateMethod();

        System.out.println("\n\nSCENARIO 2: ConcreteClassB (overrides hook methods)\n");
        System.out.println("-".repeat(60));
        AbstractClass objectB = new ConcreteClassB();
        objectB.templateMethod();

        System.out.println("\n\nSCENARIO 3: ConcreteClassC (skips optional step)\n");
        System.out.println("-".repeat(60));
        AbstractClass objectC = new ConcreteClassC();
        objectC.templateMethod();

        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                      KEY OBSERVATIONS                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("1. ALGORITHM STRUCTURE");
        System.out.println("   → All three classes follow the same algorithm sequence");
        System.out.println("   → Steps 1 and 6 are identical across all implementations");
        System.out.println();
        System.out.println("2. REQUIRED CUSTOMIZATION");
        System.out.println("   → Steps 2 and 4 are different for each class (abstract methods)");
        System.out.println("   → Each subclass MUST provide these implementations");
        System.out.println();
        System.out.println("3. OPTIONAL CUSTOMIZATION");
        System.out.println("   → ClassA uses default behavior for steps 3 and 5 (hooks)");
        System.out.println("   → ClassB customizes steps 3 and 5 (overrides hooks)");
        System.out.println("   → ClassC skips step 3 entirely (overrides shouldExecuteStepThree)");
        System.out.println();
        System.out.println("4. INVERSION OF CONTROL");
        System.out.println("   → Parent class (AbstractClass) controls the algorithm flow");
        System.out.println("   → Child classes provide implementations but don't control flow");
        System.out.println("   → \"Don't call us, we'll call you\" (Hollywood Principle)");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
    }
}