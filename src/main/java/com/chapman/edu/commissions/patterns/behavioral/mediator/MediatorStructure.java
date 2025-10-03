package com.chapman.edu.commissions.patterns.behavioral.mediator;

import java.util.ArrayList;
import java.util.List;

/**
 * MEDIATOR PATTERN - STRUCTURAL DEMONSTRATION
 *
 * PURPOSE:
 * The Mediator Pattern defines an object that encapsulates how a set of objects interact.
 * It promotes loose coupling by keeping objects from referring to each other explicitly,
 * and lets you vary their interaction independently.
 *
 * PROBLEM IT SOLVES:
 * - Reduces chaotic dependencies between communicating objects
 * - Prevents tight coupling when objects need to communicate with many other objects
 * - Centralizes complex communications and control logic
 * - Makes it easier to understand and maintain object interactions
 * - Simplifies object protocols by replacing many-to-many relationships with one-to-many
 *
 * WHEN TO USE:
 * - A set of objects communicate in well-defined but complex ways
 * - Reusing an object is difficult because it refers to and communicates with many other objects
 * - Behavior distributed across several classes should be customizable without subclassing
 * - You want to avoid creating tons of interconnected classes
 * - Object interactions should be centralized for easier modification
 *
 * COMPONENTS:
 * 1. Mediator (Interface): Defines interface for communicating with Colleague objects
 * 2. ConcreteMediator: Implements cooperative behavior by coordinating Colleague objects
 * 3. Colleague: Each Colleague class knows its Mediator object and communicates through it
 * 4. ConcreteColleague: Communicates with other Colleagues through the Mediator
 *
 * KEY CONCEPT:
 * Instead of objects communicating directly (many-to-many relationships),
 * they communicate through a mediator (one-to-many relationships).
 *
 * @author Commission Calculator Educational Project
 */
public class MediatorStructure {

    /**
     * MEDIATOR INTERFACE
     *
     * Defines the interface for communication between Colleague objects.
     * The Mediator knows all Colleagues and coordinates their interactions.
     *
     * KEY RESPONSIBILITY:
     * - Provide communication channel for Colleagues
     * - Coordinate complex interactions
     * - Maintain references to all Colleagues
     */
    public interface Mediator {
        /**
         * Notify the mediator that a colleague has changed or needs to communicate.
         *
         * @param sender The colleague sending the notification
         * @param event The event/message being communicated
         */
        void notify(Colleague sender, String event);

        /**
         * Register a colleague with the mediator.
         *
         * @param colleague The colleague to register
         */
        void registerColleague(Colleague colleague);
    }

    /**
     * COLLEAGUE ABSTRACT CLASS
     *
     * Base class for all objects that need to communicate through the mediator.
     * Each colleague knows its mediator but not the other colleagues.
     *
     * KEY PRINCIPLE:
     * Colleagues don't communicate directly with each other - they only know the mediator.
     * This reduces coupling between colleagues.
     */
    public static abstract class Colleague {
        protected Mediator mediator;
        protected String name;

        public Colleague(String name) {
            this.name = name;
        }

        /**
         * Set the mediator for this colleague.
         * Usually called during initialization.
         *
         * @param mediator The mediator to use for communication
         */
        public void setMediator(Mediator mediator) {
            this.mediator = mediator;
        }

        public String getName() {
            return name;
        }

        /**
         * Send a message/event to other colleagues through the mediator.
         *
         * @param event The event/message to send
         */
        protected void send(String event) {
            System.out.println(name + " sends: " + event);
            if (mediator != null) {
                mediator.notify(this, event);
            }
        }

        /**
         * Receive a message/event from another colleague (via mediator).
         *
         * @param message The message received
         */
        public abstract void receive(String message);
    }

    /**
     * CONCRETE MEDIATOR
     *
     * Implements the coordination logic between colleagues.
     * Knows all colleagues and orchestrates their interactions.
     *
     * KEY RESPONSIBILITIES:
     * - Maintain references to all colleagues
     * - Implement the coordination logic
     * - Route messages between appropriate colleagues
     * - Enforce interaction rules
     */
    public static class ConcreteMediator implements Mediator {
        private List<Colleague> colleagues;

        public ConcreteMediator() {
            this.colleagues = new ArrayList<>();
        }

        @Override
        public void registerColleague(Colleague colleague) {
            colleagues.add(colleague);
            colleague.setMediator(this);
            System.out.println("→ Mediator: Registered " + colleague.getName());
        }

        @Override
        public void notify(Colleague sender, String event) {
            System.out.println("→ Mediator: Received '" + event + "' from " + sender.getName());

            // Coordination logic: broadcast to all other colleagues
            for (Colleague colleague : colleagues) {
                if (colleague != sender) {
                    String message = sender.getName() + " says: " + event;
                    colleague.receive(message);
                }
            }
        }
    }

    /**
     * CONCRETE COLLEAGUE A
     *
     * A specific type of colleague that communicates through the mediator.
     */
    public static class ColleagueA extends Colleague {
        public ColleagueA(String name) {
            super(name);
        }

        @Override
        public void receive(String message) {
            System.out.println("  " + name + " (ColleagueA) received: " + message);
        }

        /**
         * Type-specific method that sends a message.
         */
        public void doSomething() {
            System.out.println("\n" + name + " is doing something...");
            send("ColleagueA action completed");
        }
    }

    /**
     * CONCRETE COLLEAGUE B
     *
     * Another type of colleague with different behavior.
     */
    public static class ColleagueB extends Colleague {
        public ColleagueB(String name) {
            super(name);
        }

        @Override
        public void receive(String message) {
            System.out.println("  " + name + " (ColleagueB) received: " + message);

            // ColleagueB might react to certain messages
            if (message.contains("action completed")) {
                reactToAction();
            }
        }

        private void reactToAction() {
            System.out.println("  → " + name + " is reacting to the action");
            send("ColleagueB acknowledged");
        }
    }

    /**
     * CONCRETE COLLEAGUE C
     *
     * Yet another colleague type demonstrating different communication patterns.
     */
    public static class ColleagueC extends Colleague {
        private int messageCount = 0;

        public ColleagueC(String name) {
            super(name);
        }

        @Override
        public void receive(String message) {
            messageCount++;
            System.out.println("  " + name + " (ColleagueC) received: " + message +
                             " [Total messages: " + messageCount + "]");
        }

        public void broadcast() {
            System.out.println("\n" + name + " is broadcasting...");
            send("ColleagueC broadcast to all");
        }
    }

    /**
     * COMPLEX MEDIATOR (Advanced Example)
     *
     * Demonstrates a mediator with more sophisticated coordination logic.
     */
    public static class SmartMediator implements Mediator {
        private List<Colleague> colleagues;
        private boolean broadcastMode = true;

        public SmartMediator() {
            this.colleagues = new ArrayList<>();
        }

        @Override
        public void registerColleague(Colleague colleague) {
            colleagues.add(colleague);
            colleague.setMediator(this);
            System.out.println("→ SmartMediator: Registered " + colleague.getName());
        }

        @Override
        public void notify(Colleague sender, String event) {
            System.out.println("→ SmartMediator: Processing '" + event + "' from " +
                             sender.getName());

            // Smart routing based on event type
            if (event.contains("urgent")) {
                handleUrgentMessage(sender, event);
            } else if (event.contains("private")) {
                handlePrivateMessage(sender, event);
            } else if (broadcastMode) {
                handleBroadcast(sender, event);
            } else {
                handleTargeted(sender, event);
            }
        }

        private void handleUrgentMessage(Colleague sender, String event) {
            System.out.println("  → Urgent message - broadcasting immediately!");
            String message = "[URGENT] " + sender.getName() + ": " + event;
            for (Colleague colleague : colleagues) {
                if (colleague != sender) {
                    colleague.receive(message);
                }
            }
        }

        private void handlePrivateMessage(Colleague sender, String event) {
            System.out.println("  → Private message - not broadcasting");
            // In real scenario, would route to specific recipient
        }

        private void handleBroadcast(Colleague sender, String event) {
            String message = sender.getName() + ": " + event;
            for (Colleague colleague : colleagues) {
                if (colleague != sender) {
                    colleague.receive(message);
                }
            }
        }

        private void handleTargeted(Colleague sender, String event) {
            // Send to first available colleague (example of targeted routing)
            for (Colleague colleague : colleagues) {
                if (colleague != sender) {
                    colleague.receive(sender.getName() + " (targeted): " + event);
                    break;  // Only send to first one
                }
            }
        }

        public void setBroadcastMode(boolean mode) {
            this.broadcastMode = mode;
            System.out.println("→ SmartMediator: Broadcast mode " +
                             (mode ? "ENABLED" : "DISABLED"));
        }
    }

    /**
     * DEMONSTRATION
     *
     * Shows how the Mediator pattern works.
     */
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         MEDIATOR PATTERN - DEMONSTRATION                  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        // SCENARIO 1: Basic Mediator
        System.out.println("SCENARIO 1: Basic Mediator (Broadcast Communication)\n");
        System.out.println("=".repeat(60));

        ConcreteMediator mediator = new ConcreteMediator();

        ColleagueA alice = new ColleagueA("Alice");
        ColleagueB bob = new ColleagueB("Bob");
        ColleagueC charlie = new ColleagueC("Charlie");

        mediator.registerColleague(alice);
        mediator.registerColleague(bob);
        mediator.registerColleague(charlie);

        System.out.println();

        // Alice does something - Bob and Charlie are notified
        alice.doSomething();

        System.out.println();

        // Charlie broadcasts - Alice and Bob receive
        charlie.broadcast();

        System.out.println("\n" + "=".repeat(60) + "\n");

        // SCENARIO 2: Smart Mediator with routing logic
        System.out.println("SCENARIO 2: Smart Mediator (Intelligent Routing)\n");
        System.out.println("=".repeat(60));

        SmartMediator smartMediator = new SmartMediator();

        ColleagueA david = new ColleagueA("David");
        ColleagueB eve = new ColleagueB("Eve");
        ColleagueC frank = new ColleagueC("Frank");

        smartMediator.registerColleague(david);
        smartMediator.registerColleague(eve);
        smartMediator.registerColleague(frank);

        System.out.println();

        // Normal message - broadcast
        david.send("Normal message to all");

        System.out.println();

        // Urgent message - special handling
        eve.send("This is urgent!");

        System.out.println();

        // Switch to targeted mode
        smartMediator.setBroadcastMode(false);
        frank.send("Targeted message");

        System.out.println("\n" + "=".repeat(60) + "\n");

        // Summary
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                      KEY OBSERVATIONS                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("1. LOOSE COUPLING");
        System.out.println("   → Colleagues don't know about each other");
        System.out.println("   → They only know the mediator");
        System.out.println("   → Reduces dependencies between objects");
        System.out.println();
        System.out.println("2. CENTRALIZED CONTROL");
        System.out.println("   → All communication logic in mediator");
        System.out.println("   → Easy to understand and modify interactions");
        System.out.println("   → Single place to add logging, validation, etc.");
        System.out.println();
        System.out.println("3. SIMPLIFIED OBJECTS");
        System.out.println("   → Colleagues focus on their own behavior");
        System.out.println("   → No need to manage multiple references");
        System.out.println("   → Easier to reuse and test individually");
        System.out.println();
        System.out.println("4. FLEXIBLE COORDINATION");
        System.out.println("   → Can change routing logic without affecting colleagues");
        System.out.println("   → SmartMediator shows conditional routing");
        System.out.println("   → Easy to add new coordination rules");
        System.out.println();
        System.out.println("5. SCALABILITY");
        System.out.println("   → Adding new colleague types is easy");
        System.out.println("   → No need to modify existing colleagues");
        System.out.println("   → Mediator handles new interaction patterns");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
    }
}