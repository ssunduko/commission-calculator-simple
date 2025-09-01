package com.chapman.edu.commissions.principles.oop.inheritance;

import java.math.BigDecimal;

/**
 * BonusCommission class that extends BaseCommission.
 * This class demonstrates inheritance by:
 * 1. Extending the BaseCommission class
 * 2. Adding additional properties specific to bonus commissions
 * 3. Overriding methods to provide bonus-specific behavior
 */
public class BonusCommission extends BaseCommission {
    // Additional properties specific to bonus commissions
    private String bonusType;
    private String bonusReason;
    private BigDecimal bonusAmount;
    
    /**
     * Default constructor
     */
    public BonusCommission() {
        super(); // Call the parent class constructor
        this.bonusAmount = BigDecimal.ZERO;
    }
    
    /**
     * Constructor with essential fields
     */
    public BonusCommission(String dealId, String salesRepId, BigDecimal baseAmount, BigDecimal bonusAmount) {
        super(dealId, salesRepId, baseAmount); // Call the parent class constructor
        this.bonusAmount = bonusAmount;
    }
    
    /**
     * Constructor with all fields
     */
    public BonusCommission(String dealId, String salesRepId, BigDecimal baseAmount, 
                          String bonusType, String bonusReason, BigDecimal bonusAmount) {
        super(dealId, salesRepId, baseAmount); // Call the parent class constructor
        this.bonusType = bonusType;
        this.bonusReason = bonusReason;
        this.bonusAmount = bonusAmount;
    }
    
    // Getters and Setters for the additional properties
    
    public String getBonusType() {
        return bonusType;
    }
    
    public void setBonusType(String bonusType) {
        this.bonusType = bonusType;
    }
    
    public String getBonusReason() {
        return bonusReason;
    }
    
    public void setBonusReason(String bonusReason) {
        this.bonusReason = bonusReason;
    }
    
    public BigDecimal getBonusAmount() {
        return bonusAmount;
    }
    
    public void setBonusAmount(BigDecimal bonusAmount) {
        this.bonusAmount = bonusAmount;
    }
    
    /**
     * Override the calculateCommission method to include the bonus amount.
     * This demonstrates method overriding in inheritance.
     * @return the calculated commission amount including the bonus
     */
    @Override
    public BigDecimal calculateCommission() {
        // Add the bonus amount to the base amount
        return super.calculateCommission().add(bonusAmount);
    }
    
    /**
     * Override the getSummary method to include bonus information.
     * This demonstrates method overriding in inheritance.
     * @return a string summary of the commission including bonus information
     */
    @Override
    public String getSummary() {
        return "Bonus Commission: Base=" + amount + ", Bonus=" + bonusAmount + 
               ", Total=" + calculateCommission() + " for Deal: " + dealId +
               (bonusReason != null ? " (Reason: " + bonusReason + ")" : "");
    }
    
    /**
     * Get the bonus percentage relative to the base amount.
     * This is a new method specific to BonusCommission.
     * @return the bonus percentage
     */
    public BigDecimal getBonusPercentage() {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return bonusAmount.divide(amount, 2, BigDecimal.ROUND_HALF_UP)
                         .multiply(new BigDecimal("100"));
    }
    
    @Override
    public String toString() {
        return "BonusCommission{" +
                "id='" + id + '\'' +
                ", dealId='" + dealId + '\'' +
                ", salesRepId='" + salesRepId + '\'' +
                ", baseAmount=" + amount +
                ", bonusType='" + bonusType + '\'' +
                ", bonusReason='" + bonusReason + '\'' +
                ", bonusAmount=" + bonusAmount +
                ", totalAmount=" + calculateCommission() +
                ", calculationDate=" + calculationDate +
                '}';
    }
}