package com.chapman.edu.commissions.principles.composition.fixed;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a bonus rule in the system.
 * This class uses composition instead of inheritance by containing a CommissionRule.
 */
public class BonusRule {
    private CommissionRule commissionRule; // Composition instead of inheritance
    private boolean isPercentage;
    private LocalDate startDate;
    private LocalDate endDate;
    private BonusType bonusType;
    
    /**
     * Default constructor
     */
    public BonusRule() {
        this.commissionRule = new CommissionRule();
        this.commissionRule.setType(CommissionRule.RuleType.BONUS);
        this.isPercentage = false;
        this.bonusType = BonusType.FIXED;
    }
    
    /**
     * Constructor with essential fields
     */
    public BonusRule(String name, BigDecimal amount, boolean isPercentage, BonusType bonusType) {
        this.commissionRule = new CommissionRule(name, amount, CommissionRule.RuleType.BONUS);
        this.isPercentage = isPercentage;
        this.bonusType = bonusType;
    }
    
    // Delegate methods to the contained CommissionRule
    
    public String getId() {
        return commissionRule.getId();
    }
    
    public void setId(String id) {
        commissionRule.setId(id);
    }
    
    public String getName() {
        return commissionRule.getName();
    }
    
    public void setName(String name) {
        commissionRule.setName(name);
    }
    
    public String getDescription() {
        return commissionRule.getDescription();
    }
    
    public void setDescription(String description) {
        commissionRule.setDescription(description);
    }
    
    public void addCondition(RuleCondition condition) {
        commissionRule.addCondition(condition);
    }
    
    public BigDecimal getRate() {
        return commissionRule.getRate();
    }
    
    public void setRate(BigDecimal rate) {
        commissionRule.setRate(rate);
    }
    
    public CommissionRule.RuleType getType() {
        return commissionRule.getType();
    }
    
    public int getPriority() {
        return commissionRule.getPriority();
    }
    
    public void setPriority(int priority) {
        commissionRule.setPriority(priority);
    }
    
    public String getPlanId() {
        return commissionRule.getPlanId();
    }
    
    public void setPlanId(String planId) {
        commissionRule.setPlanId(planId);
    }
    
    // BonusRule specific methods
    
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
     * Calculate bonus amount for a given deal amount
     * @param dealAmount the deal amount to calculate bonus for
     * @return the bonus amount
     */
    public BigDecimal calculateBonus(BigDecimal dealAmount) {
        if (isPercentage) {
            return dealAmount.multiply(getRate().divide(new BigDecimal("100")));
        } else {
            return getRate(); // For fixed bonuses, just return the rate (which is the fixed amount)
        }
    }
    
    /**
     * Get the underlying CommissionRule
     * @return the CommissionRule
     */
    public CommissionRule getCommissionRule() {
        return commissionRule;
    }
    
    @Override
    public String toString() {
        return "BonusRule{" +
                "commissionRule=" + commissionRule +
                ", isPercentage=" + isPercentage +
                ", bonusType=" + bonusType +
                '}';
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