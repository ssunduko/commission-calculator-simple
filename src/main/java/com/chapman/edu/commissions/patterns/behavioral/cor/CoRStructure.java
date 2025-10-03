package com.chapman.edu.commissions.patterns.behavioral.cor;

/**
 * CHAIN OF RESPONSIBILITY PATTERN - STRUCTURAL DEMONSTRATION
 *
 * PURPOSE:
 * The Chain of Responsibility Pattern lets you pass requests along a chain of handlers.
 * Upon receiving a request, each handler decides either to process the request or to pass
 * it to the next handler in the chain. This decouples the sender of a request from its receivers.
 *
 * PROBLEM IT SOLVES:
 * - Avoid coupling the sender of a request to its receiver
 * - Allow more than one object to handle a request
 * - Let the set of objects that can handle a request be specified dynamically
 * - Eliminate complex conditional logic for routing requests
 * - Make it easy to add or remove handlers without affecting the client
 *
 * WHEN TO USE:
 * - More than one object may handle a request, and the handler isn't known a priori
 * - You want to issue a request to one of several objects without specifying the receiver explicitly
 * - The set of objects that can handle a request should be specified dynamically
 * - You want to avoid explicit if-else chains for handling different request types
 * - Processing steps need to be executed in a specific order
 *
 * COMPONENTS:
 * 1. Handler (Interface/Abstract): Defines interface for handling requests and optionally
 *    implements the successor link
 * 2. ConcreteHandler: Handles requests it is responsible for; can access its successor
 * 3. Client: Initiates the request to a Handler object in the chain
 *
 * KEY CONCEPTS:
 * - Handlers are chained together (each has reference to next handler)
 * - Request flows through the chain until a handler processes it
 * - Handler can: (1) Process and stop, (2) Process and pass to next, (3) Pass to next
 * - The chain can be assembled at runtime
 *
 * @author Commission Calculator Educational Project
 */
public class CoRStructure {

    /**
     * REQUEST CLASS
     *
     * Represents a request that needs to be processed by the chain.
     * Contains data and metadata about what needs to be handled.
     */
    public static class Request {
        private final String type;
        private final int priority;
        private final String data;
        private boolean handled = false;

        public Request(String type, int priority, String data) {
            this.type = type;
            this.priority = priority;
            this.data = data;
        }

        public String getType() {
            return type;
        }

        public int getPriority() {
            return priority;
        }

        public String getData() {
            return data;
        }

        public boolean isHandled() {
            return handled;
        }

        public void markAsHandled() {
            this.handled = true;
        }

        @Override
        public String toString() {
            return "Request{type='" + type + "', priority=" + priority +
                   ", data='" + data + "', handled=" + handled + "}";
        }
    }

    /**
     * HANDLER INTERFACE
     *
     * Defines the interface for handling requests.
     * Can be an interface or abstract class.
     *
     * KEY RESPONSIBILITIES:
     * - Define method for handling requests
     * - Maintain reference to next handler in chain
     * - Provide mechanism to set the next handler
     */
    public interface Handler {
        /**
         * Set the next handler in the chain.
         *
         * @param handler The next handler
         * @return The next handler (for fluent chain building)
         */
        Handler setNext(Handler handler);

        /**
         * Handle the request.
         * Each handler decides whether to process the request, pass it along, or both.
         *
         * @param request The request to handle
         */
        void handle(Request request);
    }

    /**
     * BASE HANDLER (Abstract Implementation)
     *
     * Provides default implementation of chain behavior.
     * Concrete handlers extend this to inherit chain management logic.
     *
     * KEY BENEFIT: Handlers only need to implement their specific logic,
     * not the chain management code.
     */
    public static abstract class AbstractHandler implements Handler {
        private Handler nextHandler;

        @Override
        public Handler setNext(Handler handler) {
            this.nextHandler = handler;
            return handler;  // Allows fluent chaining: h1.setNext(h2).setNext(h3)
        }

        @Override
        public void handle(Request request) {
            if (canHandle(request)) {
                // This handler can process the request
                processRequest(request);

                // Optionally continue chain even after processing
                if (shouldContinueChain(request)) {
                    passToNext(request);
                }
            } else {
                // This handler cannot process, pass to next
                passToNext(request);
            }
        }

        /**
         * Pass the request to the next handler in the chain.
         * If there's no next handler, the request ends here.
         *
         * @param request The request to pass
         */
        protected void passToNext(Request request) {
            if (nextHandler != null) {
                nextHandler.handle(request);
            } else {
                // End of chain - request not handled
                if (!request.isHandled()) {
                    System.out.println("⚠️  End of chain reached - Request not handled: " + request);
                }
            }
        }

        /**
         * Determine if this handler can process the request.
         * Subclasses override this to define their handling criteria.
         *
         * @param request The request to check
         * @return true if this handler can process the request
         */
        protected abstract boolean canHandle(Request request);

        /**
         * Process the request.
         * Subclasses override this to define their specific processing logic.
         *
         * @param request The request to process
         */
        protected abstract void processRequest(Request request);

        /**
         * Determine if the chain should continue after this handler processes the request.
         * Default is false (stop after handling), but subclasses can override.
         *
         * @param request The request being processed
         * @return true if the chain should continue
         */
        protected boolean shouldContinueChain(Request request) {
            return false;  // Default: stop chain after handling
        }
    }

    /**
     * CONCRETE HANDLER 1 - Low Priority Handler
     *
     * Handles requests with priority 1-3.
     */
    public static class LowPriorityHandler extends AbstractHandler {
        @Override
        protected boolean canHandle(Request request) {
            return request.getPriority() >= 1 && request.getPriority() <= 3;
        }

        @Override
        protected void processRequest(Request request) {
            System.out.println("✓ LowPriorityHandler: Processing " + request.getType() +
                             " (Priority: " + request.getPriority() + ")");
            System.out.println("  → Standard processing: " + request.getData());
            request.markAsHandled();
        }
    }

    /**
     * CONCRETE HANDLER 2 - Medium Priority Handler
     *
     * Handles requests with priority 4-6.
     */
    public static class MediumPriorityHandler extends AbstractHandler {
        @Override
        protected boolean canHandle(Request request) {
            return request.getPriority() >= 4 && request.getPriority() <= 6;
        }

        @Override
        protected void processRequest(Request request) {
            System.out.println("✓ MediumPriorityHandler: Processing " + request.getType() +
                             " (Priority: " + request.getPriority() + ")");
            System.out.println("  → Enhanced processing: " + request.getData());
            request.markAsHandled();
        }
    }

    /**
     * CONCRETE HANDLER 3 - High Priority Handler
     *
     * Handles requests with priority 7-10.
     */
    public static class HighPriorityHandler extends AbstractHandler {
        @Override
        protected boolean canHandle(Request request) {
            return request.getPriority() >= 7 && request.getPriority() <= 10;
        }

        @Override
        protected void processRequest(Request request) {
            System.out.println("✓ HighPriorityHandler: Processing " + request.getType() +
                             " (Priority: " + request.getPriority() + ")");
            System.out.println("  → URGENT processing: " + request.getData());
            request.markAsHandled();
        }
    }

    /**
     * CONCRETE HANDLER 4 - Logging Handler (Decorator-style)
     *
     * Demonstrates a handler that processes AND continues the chain.
     * Acts like a decorator/interceptor in the chain.
     */
    public static class LoggingHandler extends AbstractHandler {
        @Override
        protected boolean canHandle(Request request) {
            return true;  // Log all requests
        }

        @Override
        protected void processRequest(Request request) {
            System.out.println("📝 LoggingHandler: Logging request - " + request);
            // Don't mark as handled - this is just logging
        }

        @Override
        protected boolean shouldContinueChain(Request request) {
            return true;  // Always continue - this is a cross-cutting concern
        }
    }

    /**
     * CONCRETE HANDLER 5 - Validation Handler
     *
     * Another decorator-style handler that validates and continues.
     */
    public static class ValidationHandler extends AbstractHandler {
        @Override
        protected boolean canHandle(Request request) {
            return true;  // Validate all requests
        }

        @Override
        protected void processRequest(Request request) {
            System.out.println("🔍 ValidationHandler: Validating request...");

            // Perform validation
            if (request.getData() == null || request.getData().isEmpty()) {
                System.out.println("  ✗ Validation failed: Empty data");
                request.markAsHandled();  // Stop chain on validation failure
                return;
            }

            System.out.println("  ✓ Validation passed");
            // Don't mark as handled - let other handlers process
        }

        @Override
        protected boolean shouldContinueChain(Request request) {
            // Continue only if validation passed (not marked as handled)
            return !request.isHandled();
        }
    }

    /**
     * DEMONSTRATION
     *
     * Shows how the Chain of Responsibility pattern works.
     */
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║   CHAIN OF RESPONSIBILITY PATTERN - DEMONSTRATION         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        System.out.println("SCENARIO 1: Simple Chain (Priority-based routing)\n");
        System.out.println("Chain: Low → Medium → High\n");
        System.out.println("=".repeat(60));

        // Build the chain
        Handler lowHandler = new LowPriorityHandler();
        Handler mediumHandler = new MediumPriorityHandler();
        Handler highHandler = new HighPriorityHandler();

        lowHandler.setNext(mediumHandler).setNext(highHandler);

        // Send requests through the chain
        Request req1 = new Request("Task A", 2, "Low priority task");
        Request req2 = new Request("Task B", 5, "Medium priority task");
        Request req3 = new Request("Task C", 9, "High priority task");

        System.out.println("\n→ Sending low priority request:");
        lowHandler.handle(req1);

        System.out.println("\n→ Sending medium priority request:");
        lowHandler.handle(req2);

        System.out.println("\n→ Sending high priority request:");
        lowHandler.handle(req3);

        // Request that no handler can process
        System.out.println("\n→ Sending invalid priority request:");
        Request req4 = new Request("Task D", 15, "Out of range priority");
        lowHandler.handle(req4);

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Scenario 2: Chain with interceptors
        System.out.println("SCENARIO 2: Chain with Interceptors (Cross-cutting concerns)\n");
        System.out.println("Chain: Logging → Validation → Low → Medium → High\n");
        System.out.println("=".repeat(60));

        // Build chain with interceptors
        Handler loggingHandler = new LoggingHandler();
        Handler validationHandler = new ValidationHandler();
        Handler lowHandler2 = new LowPriorityHandler();
        Handler mediumHandler2 = new MediumPriorityHandler();
        Handler highHandler2 = new HighPriorityHandler();

        loggingHandler.setNext(validationHandler)
                     .setNext(lowHandler2)
                     .setNext(mediumHandler2)
                     .setNext(highHandler2);

        // Send requests
        System.out.println("\n→ Sending valid request:");
        Request req5 = new Request("Task E", 3, "Valid low priority task");
        loggingHandler.handle(req5);

        System.out.println("\n→ Sending invalid request (empty data):");
        Request req6 = new Request("Task F", 5, "");
        loggingHandler.handle(req6);

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Summary
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                      KEY OBSERVATIONS                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("1. DECOUPLING");
        System.out.println("   → Client doesn't know which handler will process request");
        System.out.println("   → Handlers don't know about each other, only their successor");
        System.out.println();
        System.out.println("2. DYNAMIC CHAIN");
        System.out.println("   → Chain can be assembled at runtime");
        System.out.println("   → Easy to add/remove handlers without affecting client");
        System.out.println();
        System.out.println("3. FLEXIBLE PROCESSING");
        System.out.println("   → Handler can: process and stop, process and continue, or just pass");
        System.out.println("   → Enables interceptors and cross-cutting concerns");
        System.out.println();
        System.out.println("4. SINGLE RESPONSIBILITY");
        System.out.println("   → Each handler has one responsibility");
        System.out.println("   → No complex if-else chains in client code");
        System.out.println();
        System.out.println("5. OPEN/CLOSED PRINCIPLE");
        System.out.println("   → Easy to add new handler types");
        System.out.println("   → Existing handlers don't need modification");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
    }
}