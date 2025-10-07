package com.chapman.edu.commissions.patterns.behavioral.strategy;

import com.chapman.edu.commissions.model.Deal;
import java.math.BigDecimal;

/**
 * STRATEGY PATTERN - STRUCTURAL DEMONSTRATION
 *
 * PURPOSE:
 * The Strategy Pattern defines a family of algorithms, encapsulates each one, and makes them
 * interchangeable. Strategy lets the algorithm vary independently from clients that use it.
 *
 * PROBLEM IT SOLVES:
 * - Eliminates conditional statements for selecting different behaviors
 * - Provides an alternative to subclassing for varying behavior
 * - Allows runtime selection of algorithms
 * - Encapsulates algorithm-specific data and logic
 *
 * WHEN TO USE:
 * - Many related classes differ only in their behavior
 * - You need different variants of an algorithm
 * - An algorithm uses data that clients shouldn't know about
 * - A class defines many behaviors that appear as multiple conditional statements
 *
 * COMPONENTS:
 * 1. CommissionStrategy (Interface): Declares common interface for all supported algorithms
 * 2. Concrete Strategies (in StrategyImplementation): Different algorithm implementations
 * 3. CommissionCalculator (Context in StrategyImplementation): Uses a Strategy reference to execute the algorithm
 *
 * KEY DIFFERENCES FROM OTHER PATTERNS:
 * - vs State: Strategy focuses on interchangeable algorithms; State focuses on state-dependent behavior
 * - vs Template Method: Strategy uses composition/delegation; Template Method uses inheritance
 * - vs Command: Strategy encapsulates algorithms; Command encapsulates requests as objects
 */
public class StrategyStructure {

    /**
     * STRATEGY INTERFACE
     * Defines the common interface for all commission calculation algorithms.
     * Each concrete strategy implements this interface with a specific calculation method.
     */
    public interface CommissionStrategy {
        /**
         * Calculate commission for a deal.
         * @param deal The deal for which to calculate commission
         * @return The calculated commission amount
         */
        BigDecimal calculateCommission(Deal deal);

        String getStrategyDescription();
    }
    /**
     * BONUS CALCULATION STRATEGY INTERFACE
     * Defines the interface for bonus calculation algorithms.
     * Bonuses can be calculated based on various criteria.
     */
    public interface BonusStrategy {
        /**
         * Calculate bonus based on the base commission and deal.
         * @param baseCommission The base commission amount
         * @param deal The deal associated with the commission
         * @return The calculated bonus amount
         */
        BigDecimal calculateBonus(BigDecimal baseCommission, Deal deal);
        /**
         * Check if this bonus strategy applies to the given deal.
         * @param deal The deal to check
         * @return true if the strategy applies, false otherwise
         */
        boolean appliesTo(Deal deal);

        /**
         * Get the name of this bonus strategy.
         *
         * @return The strategy name
         */
        String getStrategyName();
    }

    /**
     * PAYMENT TERMS STRATEGY INTERFACE
     *
     * Defines the interface for adjusting commissions based on payment terms.
     * Different payment terms may affect when and how much commission is paid.
     */
    public interface PaymentTermsStrategy {
        /**
         * Adjust commission based on payment terms.
         *
         * @param baseCommission The original commission amount
         * @param paymentTermsInDays The payment terms in days
         * @return The adjusted commission amount
         */
        BigDecimal adjustForPaymentTerms(BigDecimal baseCommission, int paymentTermsInDays);

        /**
         * Get an explanation of how this strategy adjusts commissions.
         *
         * @return A description of the adjustment logic
         */
        String getAdjustmentDescription();
    }
}