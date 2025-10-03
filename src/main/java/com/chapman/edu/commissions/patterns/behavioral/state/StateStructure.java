package com.chapman.edu.commissions.patterns.behavioral.state;

/**
 * STATE PATTERN - STRUCTURAL DEMONSTRATION
 *
 * PURPOSE:
 * The State Pattern allows an object to alter its behavior when its internal state changes.
 * The object will appear to change its class. This pattern encapsulates state-specific behavior
 * into separate state classes and delegates behavior to the current state object.
 *
 * PROBLEM IT SOLVES:
 * - Eliminates large conditional statements (if/else, switch) based on object state
 * - Makes state transitions explicit and easier to manage
 * - Encapsulates state-specific behavior, making it easier to add new states
 * - Makes state transition logic more maintainable and testable
 *
 * WHEN TO USE:
 * - An object's behavior depends on its state, and it must change behavior at runtime
 * - Operations have large, multipart conditional statements based on object state
 * - State transitions are complex and need to be made explicit
 * - You want to avoid duplicate code across similar states
 *
 * COMPONENTS:
 * 1. Context: Maintains a reference to the current State and delegates state-specific requests
 * 2. State (Interface): Defines the interface for encapsulating state-specific behavior
 * 3. ConcreteState: Implements behavior associated with a particular state of the Context
 *
 * @author Commission Calculator Educational Project
 */
public class StateStructure {

    /**
     * STATE INTERFACE
     *
     * Defines the common interface for all concrete states.
     * Each method represents an action that can be performed, and the behavior
     * varies depending on the current state.
     *
     * KEY CHARACTERISTICS:
     * - Declares methods for all state-specific behaviors
     * - All concrete states must implement this interface
     * - Methods typically receive the Context as a parameter to allow state transitions
     */
    public interface State {
        /**
         * Handle a request in a state-specific manner.
         * Different states will implement this differently.
         *
         * @param context The context object to allow state transitions
         */
        void handle(Context context);

        /**
         * Get the name of the current state for display/logging purposes.
         *
         * @return The state name
         */
        String getStateName();
    }

    /**
     * CONCRETE STATE A
     *
     * Implements behavior associated with State A.
     * Each concrete state knows which state(s) can follow it.
     *
     * KEY RESPONSIBILITIES:
     * - Implement state-specific behavior
     * - Determine when to transition to another state
     * - Trigger state transitions by calling context.setState()
     */
    public static class ConcreteStateA implements State {
        @Override
        public void handle(Context context) {
            System.out.println("ConcreteStateA: Handling request in State A");
            System.out.println("ConcreteStateA: Transitioning to State B");
            // State transition logic - this state knows it transitions to StateB
            context.setState(new ConcreteStateB());
        }

        @Override
        public String getStateName() {
            return "State A";
        }
    }

    /**
     * CONCRETE STATE B
     *
     * Implements behavior associated with State B.
     * Demonstrates that each state can have completely different behavior
     * and different transition logic.
     */
    public static class ConcreteStateB implements State {
        @Override
        public void handle(Context context) {
            System.out.println("ConcreteStateB: Handling request in State B");
            System.out.println("ConcreteStateB: Transitioning to State C");
            // Different transition logic
            context.setState(new ConcreteStateC());
        }

        @Override
        public String getStateName() {
            return "State B";
        }
    }

    /**
     * CONCRETE STATE C
     *
     * Implements behavior associated with State C.
     * This state transitions back to State A, creating a state cycle.
     */
    public static class ConcreteStateC implements State {
        @Override
        public void handle(Context context) {
            System.out.println("ConcreteStateC: Handling request in State C");
            System.out.println("ConcreteStateC: Transitioning back to State A");
            // Cycle back to State A
            context.setState(new ConcreteStateA());
        }

        @Override
        public String getStateName() {
            return "State C";
        }
    }

    /**
     * CONTEXT
     *
     * Maintains a reference to a ConcreteState instance that defines the current state.
     * The Context delegates state-specific behavior to the current State object.
     *
     * KEY RESPONSIBILITIES:
     * - Maintain a reference to the current state
     * - Provide an interface for clients to request operations
     * - Delegate state-specific requests to the current state object
     * - Allow states to change the current state (via setState method)
     *
     * IMPORTANT: The Context doesn't know which concrete state it has - it only
     * knows it has a State that conforms to the State interface. This is the
     * Dependency Inversion Principle in action.
     */
    public static class Context {
        private State currentState;

        /**
         * Initialize the context with an initial state.
         * Every context must start in some state.
         *
         * @param initialState The starting state
         */
        public Context(State initialState) {
            this.currentState = initialState;
            System.out.println("Context: Initialized with " + currentState.getStateName());
        }

        /**
         * Allow the current state to be changed.
         * This is typically called by State objects to trigger transitions.
         *
         * @param state The new state to transition to
         */
        public void setState(State state) {
            System.out.println("Context: Transitioning from " + currentState.getStateName() +
                             " to " + state.getStateName());
            this.currentState = state;
        }

        /**
         * Client-facing method that delegates to the current state.
         * The behavior of this method changes based on the current state.
         */
        public void request() {
            System.out.println("\nContext: Delegating request to " + currentState.getStateName());
            currentState.handle(this);
        }

        /**
         * Get the current state name (useful for testing and debugging).
         *
         * @return The name of the current state
         */
        public String getCurrentStateName() {
            return currentState.getStateName();
        }
    }

    /**
     * DEMONSTRATION
     *
     * Shows how the State pattern works with a simple state machine.
     * Notice how the Context's behavior changes as it moves through different states.
     */
    public static void main(String[] args) {
        System.out.println("=== STATE PATTERN DEMONSTRATION ===\n");

        // Create context with initial state A
        Context context = new Context(new ConcreteStateA());

        // Make several requests - watch how behavior changes with each state
        System.out.println("\n--- Request 1 (Should be in State A) ---");
        context.request();  // State A -> transitions to State B

        System.out.println("\n--- Request 2 (Should be in State B) ---");
        context.request();  // State B -> transitions to State C

        System.out.println("\n--- Request 3 (Should be in State C) ---");
        context.request();  // State C -> transitions back to State A

        System.out.println("\n--- Request 4 (Back to State A) ---");
        context.request();  // Cycle continues...

        System.out.println("\n=== END DEMONSTRATION ===");
    }
}