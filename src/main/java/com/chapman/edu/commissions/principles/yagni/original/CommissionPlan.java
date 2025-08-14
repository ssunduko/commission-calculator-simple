package com.chapman.edu.commissions.principles.yagni.original;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Objects;

/**
 * Represents a commission plan in the system with YAGNI violations.
 * This class contains many fields and methods that aren't needed for the basic functionality.
 */
public class CommissionPlan {
    private String id;
    private String name;
    private List<CommissionRule> rules;  // YAGNI: Not needed for basic functionality
    private List<CommissionTier> tiers;  // YAGNI: Not needed for basic functionality
    private List<BonusRule> bonuses;  // YAGNI: Not needed for basic functionality
    private Currency currency;  // YAGNI: Not needed for basic functionality
    private String status;  // A simple string would suffice instead of an enum for basic functionality
    private LocalDate effectiveStartDate;  // YAGNI: Not needed for basic functionality
    private LocalDate effectiveEndDate;  // YAGNI: Not needed for basic functionality
    private LocalDate createdDate;  // YAGNI: Not needed for basic functionality
    private LocalDate lastModifiedDate;  // YAGNI: Not needed for basic functionality
    private String createdBy;  // YAGNI: Not needed for basic functionality
    
    /**
     * Default constructor
     */
    public CommissionPlan() {
        this.rules = new ArrayList<>();  // YAGNI: Initializing a field that isn't needed
        this.tiers = new ArrayList<>();  // YAGNI: Initializing a field that isn't needed
        this.bonuses = new ArrayList<>();  // YAGNI: Initializing a field that isn't needed
        this.createdDate = LocalDate.now();  // YAGNI: Setting a field that isn't needed
        this.lastModifiedDate = LocalDate.now();  // YAGNI: Setting a field that isn't needed
        this.status = "DRAFT";  // YAGNI: Setting a status when it might not be needed
    }
    
    /**
     * Constructor with essential fields
     */
    public CommissionPlan(String name, Currency currency) {
        this();
        this.name = name;
        this.currency = currency;  // YAGNI: Setting a field that isn't needed
    }
    
    // Getters and Setters
    
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
    
    // YAGNI: Methods for fields that aren't needed for basic functionality
    
    public List<CommissionRule> getRules() {
        return rules;
    }
    
    public void setRules(List<CommissionRule> rules) {
        this.rules = rules;
    }
    
    public void addRule(CommissionRule rule) {
        this.rules.add(rule);
    }
    
    public List<CommissionTier> getTiers() {
        return tiers;
    }
    
    public void setTiers(List<CommissionTier> tiers) {
        this.tiers = tiers;
    }
    
    public void addTier(CommissionTier tier) {
        this.tiers.add(tier);
    }
    
    public List<BonusRule> getBonuses() {
        return bonuses;
    }
    
    public void setBonuses(List<BonusRule> bonuses) {
        this.bonuses = bonuses;
    }
    
    public void addBonus(BonusRule bonus) {
        this.bonuses.add(bonus);
    }
    
    public Currency getCurrency() {
        return currency;
    }
    
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        this.lastModifiedDate = LocalDate.now();  // YAGNI: Updating a field that isn't needed
    }
    
    public LocalDate getEffectiveStartDate() {
        return effectiveStartDate;
    }
    
    public void setEffectiveStartDate(LocalDate effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }
    
    public LocalDate getEffectiveEndDate() {
        return effectiveEndDate;
    }
    
    public void setEffectiveEndDate(LocalDate effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }
    
    public LocalDate getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDate getLastModifiedDate() {
        return lastModifiedDate;
    }
    
    public void setLastModifiedDate(LocalDate lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    /**
     * YAGNI: Check if the plan is active on a given date
     * This complex date range check isn't needed for basic functionality
     * @param date the date to check
     * @return true if the plan is active on the given date
     */
    public boolean isActiveOn(LocalDate date) {
        if (!"ACTIVE".equals(status)) {
            return false;
        }
        
        boolean afterStart = effectiveStartDate == null || !date.isBefore(effectiveStartDate);
        boolean beforeEnd = effectiveEndDate == null || !date.isAfter(effectiveEndDate);
        
        return afterStart && beforeEnd;
    }
    
    /**
     * YAGNI: Calculate the commission rate for a given sales amount
     * This complex calculation isn't needed for basic functionality
     * @param salesAmount the sales amount
     * @return the commission rate
     */
    public double calculateCommissionRate(double salesAmount) {
        // Simulate a complex calculation based on tiers
        if (tiers.isEmpty()) {
            return 0.05; // Default 5%
        }
        
        // Find the applicable tier
        for (CommissionTier tier : tiers) {
            if (salesAmount >= tier.getMinAmount() && 
                (tier.getMaxAmount() == null || salesAmount <= tier.getMaxAmount())) {
                return tier.getRate();
            }
        }
        
        return 0.05; // Default 5%
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommissionPlan that = (CommissionPlan) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "CommissionPlan{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", effectiveStartDate=" + effectiveStartDate +
                ", effectiveEndDate=" + effectiveEndDate +
                '}';
    }
    
    /**
     * Inner class representing a commission rule
     * YAGNI: This inner class isn't needed for basic functionality
     */
    public static class CommissionRule {
        private String id;
        private String name;
        private String condition;
        private double rate;
        
        public CommissionRule() {
        }
        
        public CommissionRule(String name, String condition, double rate) {
            this.name = name;
            this.condition = condition;
            this.rate = rate;
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
        
        public String getCondition() {
            return condition;
        }
        
        public void setCondition(String condition) {
            this.condition = condition;
        }
        
        public double getRate() {
            return rate;
        }
        
        public void setRate(double rate) {
            this.rate = rate;
        }
    }
    
    /**
     * Inner class representing a commission tier
     * YAGNI: This inner class isn't needed for basic functionality
     */
    public static class CommissionTier {
        private String id;
        private Double minAmount;
        private Double maxAmount;
        private double rate;
        
        public CommissionTier() {
        }
        
        public CommissionTier(Double minAmount, Double maxAmount, double rate) {
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.rate = rate;
        }
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public Double getMinAmount() {
            return minAmount;
        }
        
        public void setMinAmount(Double minAmount) {
            this.minAmount = minAmount;
        }
        
        public Double getMaxAmount() {
            return maxAmount;
        }
        
        public void setMaxAmount(Double maxAmount) {
            this.maxAmount = maxAmount;
        }
        
        public double getRate() {
            return rate;
        }
        
        public void setRate(double rate) {
            this.rate = rate;
        }
    }
    
    /**
     * Inner class representing a bonus rule
     * YAGNI: This inner class isn't needed for basic functionality
     */
    public static class BonusRule {
        private String id;
        private String name;
        private String condition;
        private double amount;
        
        public BonusRule() {
        }
        
        public BonusRule(String name, String condition, double amount) {
            this.name = name;
            this.condition = condition;
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
        
        public String getCondition() {
            return condition;
        }
        
        public void setCondition(String condition) {
            this.condition = condition;
        }
        
        public double getAmount() {
            return amount;
        }
        
        public void setAmount(double amount) {
            this.amount = amount;
        }
    }
}