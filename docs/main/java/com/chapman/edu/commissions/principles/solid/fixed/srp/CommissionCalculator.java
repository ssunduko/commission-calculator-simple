package com.chapman.edu.commissions.principles.solid.fixed.srp;

import com.chapman.edu.commissions.model.AcceleratorCalculation;
import com.chapman.edu.commissions.model.BonusCalculation;
import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.logging.Logger;

/**
 * This class is responsible only for calculating commissions.
 * It follows the Single Responsibility Principle by focusing on just one responsibility.
 */
public class CommissionCalculator {
    private static final Logger LOGGER = Logger.getLogger(CommissionCalculator.class.getName());
    private final DatabaseService databaseService;

    public CommissionCalculator(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * Calculates commission for a deal using a specific plan.
     */
    public CommissionCalculation calculateCommission(String dealId, String planId) {
        LOGGER.info("Calculating commission for deal: " + dealId + " with plan: " + planId);

        // Retrieve data from database
        Deal deal = databaseService.getDealFromDatabase(dealId);
        if (deal == null) {
            LOGGER.severe("Deal not found: " + dealId);
            throw new IllegalArgumentException("Deal not found: " + dealId);
        }

        CommissionPlan plan = databaseService.getPlanFromDatabase(planId);
        if (plan == null) {
            LOGGER.warning("Plan not found: " + planId + ". Using default plan.");
            plan = databaseService.getDefaultPlan();
        }

        User salesRep = databaseService.getUserFromDatabase(deal.getSalesRepId());
        if (salesRep == null) {
            LOGGER.severe("Sales rep not found: " + deal.getSalesRepId());
            throw new IllegalArgumentException("Sales rep not found: " + deal.getSalesRepId());
        }

        // Calculate commission
        CommissionCalculation calculation = new CommissionCalculation();
        calculation.setId(generateId());
        calculation.setDealId(dealId);
        calculation.setSalesRepId(deal.getSalesRepId());
        calculation.setPlanId(plan.getId());
        calculation.setCalculationDate(LocalDate.now());

        // Calculate base commission
        BigDecimal baseCommission = calculateBaseCommission(deal, plan);
        calculation.setBaseCommission(baseCommission);

        // Apply bonuses and accelerators
        applyBonuses(calculation, deal, plan);
        applyAccelerators(calculation, deal, plan);

        // Calculate total commission
        BigDecimal totalCommission = calculation.calculateTotalCommission();
        calculation.setGrossCommission(totalCommission);
        calculation.setNetCommission(totalCommission); // Simplified, no deductions

        return calculation;
    }

    private String generateId() {
        return "CALC-" + System.currentTimeMillis();
    }

    private BigDecimal calculateBaseCommission(Deal deal, CommissionPlan plan) {
        // Simplified calculation logic
        // In a real implementation, we would use the plan's rules and tiers
        // For simplicity, we'll use a fixed rate of 5%
        return deal.getValue().multiply(new BigDecimal("0.05"));
    }

    private void applyBonuses(CommissionCalculation calculation, Deal deal, CommissionPlan plan) {
        // Simplified bonus logic
        if (deal.getValue().compareTo(new BigDecimal("10000")) > 0) {
            BonusCalculation bonus = new BonusCalculation("BONUS-LARGE-DEAL", "Large Deal Bonus", new BigDecimal("500"));
            calculation.addBonus(bonus);
        }
    }

    private void applyAccelerators(CommissionCalculation calculation, Deal deal, CommissionPlan plan) {
        // Simplified accelerator logic
        if (deal.getValue().compareTo(new BigDecimal("50000")) > 0) {
            AcceleratorCalculation accelerator = new AcceleratorCalculation("ACCEL-PREMIUM", "Premium Deal Accelerator", new BigDecimal("1.02"));
            calculation.addAccelerator(accelerator);
        }
    }
}
