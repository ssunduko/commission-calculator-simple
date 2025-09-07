package com.chapman.edu.commissions.principles.composition.original;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a bonus rule in the system.
 * This class extends CommissionRule to demonstrate inheritance.
 */
public class BonusRule extends CommissionRule {
    private boolean isPercentage;
    private LocalDate startDate;
    private LocalDate endDate;
    private BonusType bonusType;
    
    /**
     * Default constructor
     */
    public BonusRule() {
        super();
        this.isPercentage = false;
        this.bonusType = BonusType.FIXED;
        setType(RuleType.BONUS); // Set the rule type to BONUS
    }
    
    /**
     * Constructor with essential fields
     */
    public BonusRule(String name, BigDecimal amount, boolean isPercentage, BonusType bonusType) {
        super(name, amount, RuleType.BONUS);
        this.isPercentage = isPercentage;
        this.bonusType = bonusType;
    }
    
    // Getters and Setters
    
    public boolean isPercentage() {
        return isPercentage;
    }
    
    public void setPercentage(boolean percentage) {
        isPercentage = percentage;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public BonusType getBonusType() {
        return bonusType;
    }
    
    public void setBonusType(BonusType bonusType) {
        this.bonusType = bonusType;
    }
    
    /**
     * Check if the bonus is active on a given date
     * @param date the date to check
     * @return true if the bonus is active on the given date
     */
    public boolean isActiveOn(LocalDate date) {
        boolean afterStart = startDate == null || !date.isBefore(startDate);
        boolean beforeEnd = endDate == null || !date.isAfter(endDate);
        
        return afterStart && beforeEnd;
    }
    
    /**
     * Override the calculateCommission method to handle percentage-based bonuses
     * @param dealAmount the deal amount to calculate bonus for
     * @return the bonus amount
     */
    @Override
    public BigDecimal calculateCommission(BigDecimal dealAmount) {
        if (isPercentage) {
            return dealAmount.multiply(getRate().divide(new BigDecimal("100")));
        } else {
            return getRate(); // For fixed bonuses, just return the rate (which is the fixed amount)
        }
    }
    
    /**
     * Enum representing the types of bonuses.
     */
    public enum BonusType {
        FIXED("Fixed"),
        SPIF("SPIF"),
        ACCELERATOR("Accelerator"),
        QUOTA_ACHIEVEMENT("Quota Achievement"),
        TEAM_PERFORMANCE("Team Performance"),
        SPECIAL_INCENTIVE("Special Incentive");
        
        private final String displayName;
        
        BonusType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
}