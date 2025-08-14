package com.chapman.edu.commissions.principles.oop.inheritance;

import java.math.BigDecimal;

/**
 * AcceleratedCommission class that extends BaseCommission.
 * This class demonstrates inheritance by:
 * 1. Extending the BaseCommission class
 * 2. Adding additional properties specific to accelerated commissions
 * 3. Overriding methods to provide accelerator-specific behavior
 */
public class AcceleratedCommission extends BaseCommission {
    // Additional properties specific to accelerated commissions
    private BigDecimal multiplier;
    private String acceleratorType;
    private String acceleratorReason;
    
    /**
     * Default constructor
     */
    public AcceleratedCommission() {
        super(); // Call the parent class constructor
        this.multiplier = BigDecimal.ONE; // Default multiplier is 1.0 (no acceleration)
    }
    
    /**
     * Constructor with essential fields
     */
    public AcceleratedCommission(String dealId, String salesRepId, BigDecimal baseAmount, BigDecimal multiplier) {
        super(dealId, salesRepId, baseAmount); // Call the parent class constructor
        this.multiplier = multiplier;
    }
    
    /**
     * Constructor with all fields
     */
    public AcceleratedCommission(String dealId, String salesRepId, BigDecimal baseAmount, 
                                BigDecimal multiplier, String acceleratorType, String acceleratorReason) {
        super(dealId, salesRepId, baseAmount); // Call the parent class constructor
        this.multiplier = multiplier;
        this.acceleratorType = acceleratorType;
        this.acceleratorReason = acceleratorReason;
    }
    
    // Getters and Setters for the additional properties
    
    public BigDecimal getMultiplier() {
        return multiplier;
    }
    
    public void setMultiplier(BigDecimal multiplier) {
        this.multiplier = multiplier;
    }
    
    public String getAcceleratorType() {
        return acceleratorType;
    }
    
    public void setAcceleratorType(String acceleratorType) {
        this.acceleratorType = acceleratorType;
    }
    
    public String getAcceleratorReason() {
        return acceleratorReason;
    }
    
    public void setAcceleratorReason(String acceleratorReason) {
        this.acceleratorReason = acceleratorReason;
    }
    
    /**
     * Override the calculateCommission method to apply the multiplier.
     * This demonstrates method overriding in inheritance.
     * @return the calculated commission amount with the multiplier applied
     */
    @Override
    public BigDecimal calculateCommission() {
        // Apply the multiplier to the base amount
        return super.calculateCommission().multiply(multiplier);
    }
    
    /**
     * Override the getSummary method to include accelerator information.
     * This demonstrates method overriding in inheritance.
     * @return a string summary of the commission including accelerator information
     */
    @Override
    public String getSummary() {
        return "Accelerated Commission: Base=" + amount + ", Multiplier=" + multiplier + 
               ", Total=" + calculateCommission() + " for Deal: " + dealId +
               (acceleratorReason != null ? " (Reason: " + acceleratorReason + ")" : "");
    }
    
    /**
     * Get the acceleration percentage (multiplier as a percentage).
     * This is a new method specific to AcceleratedCommission.
     * @return the acceleration percentage
     */
    public BigDecimal getAccelerationPercentage() {
        return multiplier.subtract(BigDecimal.ONE).multiply(new BigDecimal("100"));
    }
    
    /**
     * Check if this commission has acceleration applied.
     * This is a new method specific to AcceleratedCommission.
     * @return true if the multiplier is greater than 1.0
     */
    public boolean hasAcceleration() {
        return multiplier.compareTo(BigDecimal.ONE) > 0;
    }
    
    @Override
    public String toString() {
        return "AcceleratedCommission{" +
                "id='" + id + '\'' +
                ", dealId='" + dealId + '\'' +
                ", salesRepId='" + salesRepId + '\'' +
                ", baseAmount=" + amount +
                ", multiplier=" + multiplier +
                ", acceleratorType='" + acceleratorType + '\'' +
                ", acceleratorReason='" + acceleratorReason + '\'' +
                ", totalAmount=" + calculateCommission() +
                ", calculationDate=" + calculationDate +
                '}';
    }
}