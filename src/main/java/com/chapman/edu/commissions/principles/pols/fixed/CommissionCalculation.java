package com.chapman.edu.commissions.principles.pols.fixed;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a commission calculation in the system.
 * This class follows the Principle of Least Surprise (POLS) by:
 * 1. The calculateTotalCommission() method calculates the total commission
 *    AND updates the grossCommission and netCommission fields, which is what users would expect.
 * 2. The addBonus() and addAccelerator() methods update the commission values
 *    when a bonus or accelerator is added, which is also what users would expect.
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
        // Update commission values when base commission changes
        calculateTotalCommission();
    }
    
    public List<Bonus> getBonuses() {
        return bonuses;
    }
    
    public void setBonuses(List<Bonus> bonuses) {
        this.bonuses = bonuses;
        // Update commission values when bonuses are set
        calculateTotalCommission();
    }
    
    /**
     * Add a bonus to the commission calculation
     * POLS Fix: This method now updates the grossCommission and netCommission
     * when a bonus is added, which is what users would expect.
     */
    public void addBonus(Bonus bonus) {
        this.bonuses.add(bonus);
        // Update commission values when a bonus is added
        calculateTotalCommission();
    }
    
    public List<Accelerator> getAccelerators() {
        return accelerators;
    }
    
    public void setAccelerators(List<Accelerator> accelerators) {
        this.accelerators = accelerators;
        // Update commission values when accelerators are set
        calculateTotalCommission();
    }
    
    /**
     * Add an accelerator to the commission calculation
     * POLS Fix: This method now updates the grossCommission and netCommission
     * when an accelerator is added, which is what users would expect.
     */
    public void addAccelerator(Accelerator accelerator) {
        this.accelerators.add(accelerator);
        // Update commission values when an accelerator is added
        calculateTotalCommission();
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
     * POLS Fix: This method now updates the grossCommission and netCommission fields
     * after calculation, which is what users would expect.
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
        
        // Update the grossCommission and netCommission fields with the calculated total
        this.grossCommission = total;
        this.netCommission = total; // In a real system, taxes and deductions would be applied here
        
        return total;
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