package com.chapman.edu.commissions.principles.pols.original;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a commission calculation in the system.
 * This class violates the Principle of Least Surprise (POLS) because:
 * 1. The calculateTotalCommission() method calculates the total commission
 *    but doesn't update the grossCommission and netCommission fields, which could be surprising to users.
 * 2. There's a separate recalculate() method that updates these fields, which could be confusing.
 */
public class CommissionCalculation {
    private String id;
    private String dealId;
    private String salesRepId;
    private BigDecimal baseCommission;
    private List<Bonus> bonuses;
    private List<Accelerator> accelerators;
    private BigDecimal grossCommission;
    private BigDecimal netCommission;
    private String status;
    private LocalDate calculationDate;
    private LocalDate payoutDate;
    
    /**
     * Default constructor
     */
    public CommissionCalculation() {
        this.bonuses = new ArrayList<>();
        this.accelerators = new ArrayList<>();
        this.baseCommission = BigDecimal.ZERO;
        this.grossCommission = BigDecimal.ZERO;
        this.netCommission = BigDecimal.ZERO;
        this.calculationDate = LocalDate.now();
        this.status = "CALCULATED";
    }
    
    /**
     * Constructor with essential fields
     */
    public CommissionCalculation(String dealId, String salesRepId, BigDecimal baseCommission) {
        this();
        this.dealId = dealId;
        this.salesRepId = salesRepId;
        this.baseCommission = baseCommission;
        this.grossCommission = baseCommission;
        this.netCommission = baseCommission;
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getDealId() {
        return dealId;
    }
    
    public void setDealId(String dealId) {
        this.dealId = dealId;
    }
    
    public String getSalesRepId() {
        return salesRepId;
    }
    
    public void setSalesRepId(String salesRepId) {
        this.salesRepId = salesRepId;
    }
    
    public BigDecimal getBaseCommission() {
        return baseCommission;
    }
    
    public void setBaseCommission(BigDecimal baseCommission) {
        this.baseCommission = baseCommission;
    }
    
    public List<Bonus> getBonuses() {
        return bonuses;
    }
    
    public void setBonuses(List<Bonus> bonuses) {
        this.bonuses = bonuses;
    }
    
    /**
     * Add a bonus to the commission calculation
     * Note: This doesn't update the grossCommission or netCommission
     */
    public void addBonus(Bonus bonus) {
        this.bonuses.add(bonus);
        // The commission values are not updated here, which could be surprising
    }
    
    public List<Accelerator> getAccelerators() {
        return accelerators;
    }
    
    public void setAccelerators(List<Accelerator> accelerators) {
        this.accelerators = accelerators;
    }
    
    /**
     * Add an accelerator to the commission calculation
     * Note: This doesn't update the grossCommission or netCommission
     */
    public void addAccelerator(Accelerator accelerator) {
        this.accelerators.add(accelerator);
        // The commission values are not updated here, which could be surprising
    }
    
    public BigDecimal getGrossCommission() {
        return grossCommission;
    }
    
    public void setGrossCommission(BigDecimal grossCommission) {
        this.grossCommission = grossCommission;
    }
    
    public BigDecimal getNetCommission() {
        return netCommission;
    }
    
    public void setNetCommission(BigDecimal netCommission) {
        this.netCommission = netCommission;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDate getCalculationDate() {
        return calculationDate;
    }
    
    public void setCalculationDate(LocalDate calculationDate) {
        this.calculationDate = calculationDate;
    }
    
    public LocalDate getPayoutDate() {
        return payoutDate;
    }
    
    public void setPayoutDate(LocalDate payoutDate) {
        this.payoutDate = payoutDate;
    }
    
    /**
     * Calculate the total commission amount including base, bonuses, and accelerators
     * POLS Violation: This method calculates the total commission but doesn't
     * update the grossCommission and netCommission fields, which could be surprising to users.
     * 
     * @return the total commission amount
     */
    public BigDecimal calculateTotalCommission() {
        BigDecimal total = baseCommission;
        
        // Add bonuses
        for (Bonus bonus : bonuses) {
            total = total.add(bonus.getAmount());
        }
        
        // Apply accelerators
        for (Accelerator accelerator : accelerators) {
            total = total.multiply(accelerator.getMultiplier());
        }
        
        return total;
        // The grossCommission and netCommission fields are not updated here, which could be surprising
    }
    
    /**
     * Recalculate the gross and net commission amounts
     * POLS Violation: Having a separate method to update the state after calculation
     * could be confusing to users who might expect calculateTotalCommission() to do this.
     */
    public void recalculate() {
        this.grossCommission = calculateTotalCommission();
        this.netCommission = this.grossCommission; // In a real system, taxes and deductions would be applied here
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommissionCalculation that = (CommissionCalculation) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "CommissionCalculation{" +
                "id='" + id + '\'' +
                ", dealId='" + dealId + '\'' +
                ", salesRepId='" + salesRepId + '\'' +
                ", baseCommission=" + baseCommission +
                ", grossCommission=" + grossCommission +
                ", netCommission=" + netCommission +
                ", status='" + status + '\'' +
                '}';
    }
    
    /**
     * Simple bonus class for demonstration purposes
     */
    public static class Bonus {
        private String id;
        private String name;
        private BigDecimal amount;
        
        public Bonus() {
            this.amount = BigDecimal.ZERO;
        }
        
        public Bonus(String name, BigDecimal amount) {
            this.name = name;
            this.amount = amount;
        }
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public BigDecimal getAmount() {
            return amount;
        }
        
        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }
    
    /**
     * Simple accelerator class for demonstration purposes
     */
    public static class Accelerator {
        private String id;
        private String name;
        private BigDecimal multiplier;
        
        public Accelerator() {
            this.multiplier = BigDecimal.ONE;
        }
        
        public Accelerator(String name, BigDecimal multiplier) {
            this.name = name;
            this.multiplier = multiplier;
        }
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public BigDecimal getMultiplier() {
            return multiplier;
        }
        
        public void setMultiplier(BigDecimal multiplier) {
            this.multiplier = multiplier;
        }
    }
}