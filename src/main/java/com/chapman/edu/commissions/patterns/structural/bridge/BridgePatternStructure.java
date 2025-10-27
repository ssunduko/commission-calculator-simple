package com.chapman.edu.commissions.patterns.structural.bridge;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.CommissionCalculation;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * BridgePatternStructure defines the core interfaces and abstract classes for the Bridge Pattern.
 *
 * ## Bridge Pattern Overview
 * The Bridge Pattern is a structural design pattern that decouples an abstraction from its
 * implementation so that the two can vary independently. It uses composition over inheritance
 * to separate the interface from the implementation.
 *
 * ### Key Components:
 * - **Implementor**: Defines the interface for implementation classes
 * - **Concrete Implementor**: Provides specific implementations of the Implementor interface
 * - **Abstraction**: Defines the abstract interface and maintains a reference to an Implementor
 * - **Refined Abstraction**: Extends the abstraction and provides specialized operations
 * - **Client**: Works with objects through the Abstraction interface
 *
 * ### Benefits:
 * - Decouples abstraction from implementation
 * - Improves extensibility - can extend abstractions and implementations independently
 * - Hides implementation details from clients
 * - Allows runtime selection of implementation
 * - Avoids class explosion from inheritance hierarchies
 *
 * ### When to Use:
 * - When you want to avoid permanent binding between abstraction and implementation
 * - When both abstractions and implementations should be extensible via subclassing
 * - When changes in implementation shouldn't affect clients
 * - When you want to share an implementation among multiple objects
 * - When you have a proliferation of classes from a coupled interface and implementations
 *
 * ### Application in Commission Calculator:
 * In the commission calculator domain, we use the Bridge Pattern to separate:
 * - **Implementor** (CommissionCalculationStrategy): Different algorithms for calculating commissions
 * - **Abstraction** (CommissionProcessor): Different ways of processing commission calculations
 *
 * This allows us to mix and match calculation strategies with processing approaches independently.
 * For example, we can use the same tiered calculation strategy with both sales rep processors
 * and manager override processors.
 */
public class BridgePatternStructure {

    /**
     * IMPLEMENTOR - Defines the interface for implementation classes.
     *
     * This is the "implementation" side of the bridge. It declares operations that
     * all concrete implementations must support.
     *
     * **Key Principle:** The Implementor interface doesn't need to match the Abstraction's
     * interface. In fact, the two can be quite different. The Abstraction provides a
     * high-level control interface, while the Implementor provides primitive operations.
     *
     * **In Commission Calculator:** This interface defines how commissions are calculated,
     * allowing different calculation algorithms (flat rate, tiered, product-based, etc.)
     * to be swapped in and out.
     */
    public interface CommissionCalculationStrategy {
        /**
         * Calculate commission for a deal.
         *
         * This is the core operation that all concrete implementations must provide.
         * Different implementations can use different algorithms (flat rate, tiered,
         * product-based, etc.) to calculate the commission.
         *
         * **Bridge Pattern Note:** This method signature is independent of how the
         * abstraction (CommissionProcessor) uses it. The abstraction might add
         * additional logic, validation, or workflows around this calculation.
         *
         * @param deal The deal for which to calculate commission
         * @param user The sales representative who owns the deal
         * @param plan The commission plan to apply
         * @return The calculated commission amount
         */
        BigDecimal calculateCommission(Deal deal, User user, CommissionPlan plan);
    }

    /**
     * ABSTRACTION - Defines the abstract interface and maintains a reference to the Implementor.
     *
     * This is the "abstraction" side of the bridge. It defines the high-level interface
     * that clients use and delegates the actual work to the Implementor.
     *
     * **Key Characteristics:**
     * - Contains a reference to an Implementor object (composition, not inheritance)
     * - Defines higher-level operations in terms of Implementor operations
     * - Can be extended through subclassing (Refined Abstractions)
     * - Provides the interface that clients interact with
     *
     * **Bridge Pattern Core:** The "bridge" is the relationship between this abstraction
     * and the Implementor it holds. This composition allows them to vary independently.
     *
     * **In Commission Calculator:** This class represents different ways of processing
     * commissions (sales rep processing, manager overrides, etc.) while delegating the
     * actual calculation to a strategy.
     */
    public static abstract class CommissionProcessor {
        /**
         * Reference to the Implementor.
         *
         * **CRITICAL BRIDGE PATTERN FEATURE:** This composition (rather than inheritance)
         * is what creates the "bridge". The abstraction delegates work to the implementor
         * through this reference.
         *
         * This reference can be changed at runtime, allowing the same abstraction to
         * use different implementations dynamically.
         * -- SETTER --
         *  Change the implementation strategy at runtime.
         *  **Dynamic Bridge:** This demonstrates one of the key benefits of the Bridge Pattern -
         *  the ability to change implementations at runtime. The abstraction can switch to a
         *  different algorithm without changing its own code or requiring client changes.
         *
         * @param strategy The new calculation strategy to use

         */
        @Setter
        protected CommissionCalculationStrategy strategy;

        /**
         * Service dependencies for the abstraction.
         *
         * These are additional dependencies that the abstraction needs to perform
         * its work. They're separate from the Implementor and represent the abstraction's
         * own responsibilities.
         */
        protected DealService dealService;
        protected UserService userService;

        /**
         * Constructor that establishes the bridge to the implementation.
         *
         * **Bridge Pattern:** The abstraction is initialized with an implementor,
         * establishing the bridge. This can be a different implementor at runtime,
         * allowing flexible configuration.
         *
         * @param strategy The commission calculation strategy (Implementor)
         * @param dealService Service for retrieving deal information
         * @param userService Service for retrieving user information
         */
        public CommissionProcessor(CommissionCalculationStrategy strategy,
                                   DealService dealService,
                                   UserService userService) {
            this.strategy = strategy;
            this.dealService = dealService;
            this.userService = userService;
        }

        /**
         * Process commission for a deal.
         *
         * **Abstract Method:** Refined abstractions (subclasses) implement this method
         * to define their specific processing logic while using the strategy for calculations.
         *
         * This demonstrates how the abstraction provides high-level operations while
         * delegating primitive operations to the implementor.
         *
         * @param dealId The ID of the deal to process
         * @return The commission calculation result
         */
        public abstract CommissionCalculation processCommission(String dealId);
    }

    /**
     * Helper service interfaces for the abstraction.
     *
     * These interfaces define dependencies needed by the abstraction to do its work.
     * They're separate from the Implementor and represent the abstraction's domain services.
     */
    public interface DealService {
        /**
         * Retrieve a deal by its ID.
         *
         * @param dealId The unique identifier of the deal
         * @return The deal object
         */
        Deal getDealById(String dealId);

        /**
         * Get all deals for a specific sales representative.
         *
         * @param salesRepId The ID of the sales representative
         * @return List of deals owned by the sales rep
         */
        List<Deal> getDealsBySalesRep(String salesRepId);
    }

    public interface UserService {
        /**
         * Retrieve a user by their ID.
         *
         * @param userId The unique identifier of the user
         * @return The user object
         */
        User getUserById(String userId);

        /**
         * Get the commission plan for a user.
         *
         * @param userId The ID of the user
         * @return The commission plan assigned to the user
         */
        CommissionPlan getCommissionPlan(String userId);

        /**
         * Get the manager for a given user.
         *
         * @param userId The ID of the user
         * @return The user's manager
         */
        User getManagerForUser(String userId);
    }
}