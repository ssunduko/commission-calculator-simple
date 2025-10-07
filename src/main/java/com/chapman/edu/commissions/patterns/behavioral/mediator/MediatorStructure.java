package com.chapman.edu.commissions.patterns.behavioral.mediator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 */
public class MediatorStructure {

    /**
     * COMMISSION SYSTEM MEDIATOR INTERFACE
     *
     * Defines the interface for coordinating commission system components.
     */
    public interface CommissionSystemMediator {
        void registerComponent(SystemComponent component);
        void notify(SystemComponent sender, CommissionEvent event);
        void unregisterComponent(SystemComponent component);
    }

    /**
     * SYSTEM COMPONENT (Colleague Base Class)
     *
     * Base class for all components in the commission system.
     * Each component knows the mediator but not other components.
     */
    public static abstract class SystemComponent {
        protected CommissionSystemMediator mediator;
        protected String componentName;
        protected boolean enabled = true;

        public SystemComponent(String componentName) {
            this.componentName = componentName;
        }

        public void setMediator(CommissionSystemMediator mediator) {
            this.mediator = mediator;
        }

        public String getComponentName() {
            return componentName;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * Send an event through the mediator.
         */
        protected void sendEvent(CommissionEvent event) {
            if (mediator != null && enabled) {
                mediator.notify(this, event);
            }
        }

        /**
         * Handle an event received from the mediator.
         */
        public abstract void handleEvent(CommissionEvent event);

        /**
         * Check if this component is interested in the given event type.
         */
        public abstract boolean isInterestedIn(String eventType);
    }

    /**
     * COMMISSION EVENT
     *
     * Represents events that occur in the commission system.
     * Events are passed through the mediator to interested components.
     */
    public static class CommissionEvent {
        private final String eventType;
        private final String sourceComponent;
        private final Map<String, Object> data;
        private final LocalDateTime timestamp;

        public CommissionEvent(String eventType, String sourceComponent) {
            this.eventType = eventType;
            this.sourceComponent = sourceComponent;
            this.data = new HashMap<>();
            this.timestamp = LocalDateTime.now();
        }

        public CommissionEvent addData(String key, Object value) {
            data.put(key, value);
            return this;
        }

        public String getEventType() {
            return eventType;
        }

        public String getSourceComponent() {
            return sourceComponent;
        }

        public Object getData(String key) {
            return data.get(key);
        }

        public Map<String, Object> getAllData() {
            return new HashMap<>(data);
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return "CommissionEvent{" +
                    "type='" + eventType + '\'' +
                    ", source='" + sourceComponent + '\'' +
                    ", data=" + data +
                    '}';
        }
    }
}