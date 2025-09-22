package com.chapman.edu.commissions.decomposition;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.PlanStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Data-Driven Decomposition Example for Commission Calculation
 * 
 * This class demonstrates the data-driven decomposition approach to software design.
 * 
 * Data-Driven Decomposition:
 * - Focuses on organizing code around data structures and their transformations
 * - Emphasizes the flow of data through the system
 * - Separates data structures from the operations that manipulate them
 * - Often uses pipelines or transformations to process data
 * - Can leverage functional programming concepts like pure functions and immutability
 * 
 * In this example, we decompose the commission calculation process based on the
 * data structures involved and the transformations applied to them.
 */
public class DataDrivenDecompositionExample {

    /**
     * Main method to demonstrate the data-driven approach
     */
    public static void main(String[] args) {
        // Create a sample deal
        Deal deal = new Deal("Sample Deal", new BigDecimal("75000"), "REP001");
        deal.setCloseDate(LocalDate.now());
        
        // Add products to the deal
        DealProduct product1 = new DealProduct("PROD001", "Product A", 2, new BigDecimal("25000"));
        DealProduct product2 = new DealProduct("PROD002", "Product B", 1, new BigDecimal("25000"));
        deal.addProduct(product1);
        deal.addProduct(product2);
        
        // Create a sample commission plan
        CommissionPlan plan = new CommissionPlan("Standard Plan", null);
        plan.setEffectiveStartDate(LocalDate.now().minusMonths(1));
        plan.setEffectiveEndDate(LocalDate.now().plusMonths(1));
        plan.setStatus(PlanStatus.ACTIVE);
        
        // Create the commission calculator
        CommissionCalculator calculator = new CommissionCalculator();
        
        // Calculate commission
        CommissionData result = calculator.calculateCommission(deal, plan);
        
        // Display results
        System.out.println("Deal: " + deal.getTitle());
        System.out.println("Deal Value: $" + deal.calculateTotalValue());
        System.out.println("Commission Breakdown:");
        System.out.println("  Base Commission: $" + result.getBaseCommission());
        System.out.println("  Tier Adjustments: $" + result.getTierAdjustment());
        System.out.println("  Bonuses: $" + result.getBonusAmount());
        System.out.println("Total Commission: $" + result.getTotalCommission());
        
        // Display product-specific commissions
        System.out.println("\nProduct-Specific Commissions:");
        result.getProductCommissions().forEach((productId, commission) -> {
            System.out.println("  Product " + productId + ": $" + commission);
        });
    }
    
    /**
     * DealData class represents the input data for commission calculation
     * 
     * This class demonstrates how data can be structured to facilitate processing.
     * It extracts and transforms the relevant data from the Deal object.
     */
    public static class DealData {
        private final String id;
        private final String title;
        private final BigDecimal totalValue;
        private final LocalDate closeDate;
        private final String salesRepId;
        private final List<ProductData> products;
        
        /**
         * Constructor extracts data from a Deal object
         * 
         * @param deal The deal from which to extract data
         */
        public DealData(Deal deal) {
            this.id = deal.getId();
            this.title = deal.getTitle();
            this.totalValue = deal.calculateTotalValue();
            this.closeDate = deal.getCloseDate();
            this.salesRepId = deal.getSalesRepId();
            
            // Transform product list
            this.products = deal.getProducts().stream()
                    .map(ProductData::new)
                    .collect(Collectors.toList());
        }
        
        // Getters
        
        public String getId() {
            return id;
        }
        
        public String getTitle() {
            return title;
        }
        
        public BigDecimal getTotalValue() {
            return totalValue;
        }
        
        public LocalDate getCloseDate() {
            return closeDate;
        }
        
        public String getSalesRepId() {
            return salesRepId;
        }
        
        public List<ProductData> getProducts() {
            return products;
        }
        
        /**
         * Check if the deal was closed at the end of a quarter
         * 
         * @return True if the deal was closed at the end of a quarter
         */
        public boolean isQuarterEndDeal() {
            if (closeDate == null) {
                return false;
            }
            
            int month = closeDate.getMonthValue();
            return month == 3 || month == 6 || month == 9 || month == 12;
        }
    }
    
    /**
     * ProductData class represents product data extracted from a DealProduct
     * 
     * This class demonstrates how nested data structures can be transformed
     * to facilitate processing.
     */
    public static class ProductData {
        private final String id;
        private final String productId;
        private final String productName;
        private final int quantity;
        private final BigDecimal price;
        private final BigDecimal totalPrice;
        
        /**
         * Constructor extracts data from a DealProduct object
         * 
         * @param product The product from which to extract data
         */
        public ProductData(DealProduct product) {
            this.id = product.getId();
            this.productId = product.getProductId();
            this.productName = product.getProductName();
            this.quantity = product.getQuantity();
            this.price = product.getPrice();
            this.totalPrice = product.calculateTotalPrice();
        }
        
        // Getters
        
        public String getId() {
            return id;
        }
        
        public String getProductId() {
            return productId;
        }
        
        public String getProductName() {
            return productName;
        }
        
        public int getQuantity() {
            return quantity;
        }
        
        public BigDecimal getPrice() {
            return price;
        }
        
        public BigDecimal getTotalPrice() {
            return totalPrice;
        }
    }
    
    /**
     * PlanData class represents the commission plan data
     * 
     * This class demonstrates how data can be extracted and transformed
     * from complex objects to simplify processing.
     */
    public static class PlanData {
        private final String id;
        private final String name;
        private final boolean isActive;
        private final BigDecimal baseRate;
        private final Map<String, BigDecimal> productRates;
        private final List<TierData> tiers;
        
        /**
         * Constructor extracts data from a CommissionPlan object
         * 
         * @param plan The plan from which to extract data
         */
        public PlanData(CommissionPlan plan) {
            this.id = plan.getId();
            this.name = plan.getName();
            this.isActive = plan.isActiveOn(LocalDate.now());
            
            // For this example, we'll use fixed rates
            this.baseRate = new BigDecimal("0.05"); // 5% base rate
            
            // Product-specific rates (in a real system, these would come from the plan)
            this.productRates = new HashMap<>();
            productRates.put("PROD001", new BigDecimal("0.06")); // 6% for Product A
            productRates.put("PROD002", new BigDecimal("0.04")); // 4% for Product B
            
            // Tiers (in a real system, these would come from the plan)
            this.tiers = new ArrayList<>();
            tiers.add(new TierData(BigDecimal.ZERO, new BigDecimal("50000"), new BigDecimal("0.05")));
            tiers.add(new TierData(new BigDecimal("50000"), new BigDecimal("100000"), new BigDecimal("0.07")));
            tiers.add(new TierData(new BigDecimal("100000"), null, new BigDecimal("0.09")));
        }
        
        // Getters
        
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public boolean isActive() {
            return isActive;
        }
        
        public BigDecimal getBaseRate() {
            return baseRate;
        }
        
        public Map<String, BigDecimal> getProductRates() {
            return productRates;
        }
        
        public List<TierData> getTiers() {
            return tiers;
        }
        
        /**
         * Get the commission rate for a specific product
         * 
         * @param productId The product ID
         * @return The commission rate for the product, or the base rate if not specified
         */
        public BigDecimal getRateForProduct(String productId) {
            return productRates.getOrDefault(productId, baseRate);
        }
        
        /**
         * Get the tier that applies to a given amount
         * 
         * @param amount The amount to check
         * @return The applicable tier
         */
        public TierData getTierForAmount(BigDecimal amount) {
            for (TierData tier : tiers) {
                if (tier.appliesTo(amount)) {
                    return tier;
                }
            }
            return tiers.get(0); // Default to first tier
        }
    }
    
    /**
     * TierData class represents a commission tier
     * 
     * This class demonstrates how specialized data structures can be created
     * to represent specific aspects of the domain.
     */
    public static class TierData {
        private final BigDecimal minAmount;
        private final BigDecimal maxAmount;
        private final BigDecimal rate;
        
        /**
         * Constructor initializes the tier with its range and rate
         * 
         * @param minAmount The minimum amount for this tier (inclusive)
         * @param maxAmount The maximum amount for this tier (exclusive), or null for no upper limit
         * @param rate The commission rate for this tier
         */
        public TierData(BigDecimal minAmount, BigDecimal maxAmount, BigDecimal rate) {
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.rate = rate;
        }
        
        // Getters
        
        public BigDecimal getMinAmount() {
            return minAmount;
        }
        
        public BigDecimal getMaxAmount() {
            return maxAmount;
        }
        
        public BigDecimal getRate() {
            return rate;
        }
        
        /**
         * Check if this tier applies to a given amount
         * 
         * @param amount The amount to check
         * @return True if this tier applies to the amount
         */
        public boolean appliesTo(BigDecimal amount) {
            boolean aboveMin = amount.compareTo(minAmount) >= 0;
            boolean belowMax = maxAmount == null || amount.compareTo(maxAmount) < 0;
            return aboveMin && belowMax;
        }
    }
    
    /**
     * CommissionData class represents the output of the commission calculation
     * 
     * This class demonstrates how output data can be structured to provide
     * a comprehensive view of the calculation results.
     */
    public static class CommissionData {
        private final BigDecimal baseCommission;
        private final BigDecimal tierAdjustment;
        private final BigDecimal bonusAmount;
        private final Map<String, BigDecimal> productCommissions;
        
        /**
         * Constructor initializes the commission data
         * 
         * @param baseCommission The base commission amount
         * @param tierAdjustment The tier adjustment amount
         * @param bonusAmount The bonus amount
         * @param productCommissions Map of product IDs to their commission amounts
         */
        public CommissionData(
                BigDecimal baseCommission,
                BigDecimal tierAdjustment,
                BigDecimal bonusAmount,
                Map<String, BigDecimal> productCommissions) {
            this.baseCommission = baseCommission;
            this.tierAdjustment = tierAdjustment;
            this.bonusAmount = bonusAmount;
            this.productCommissions = productCommissions;
        }
        
        // Getters
        
        public BigDecimal getBaseCommission() {
            return baseCommission;
        }
        
        public BigDecimal getTierAdjustment() {
            return tierAdjustment;
        }
        
        public BigDecimal getBonusAmount() {
            return bonusAmount;
        }
        
        public Map<String, BigDecimal> getProductCommissions() {
            return productCommissions;
        }
        
        /**
         * Calculate the total commission
         * 
         * @return The total commission amount
         */
        public BigDecimal getTotalCommission() {
            return baseCommission.add(tierAdjustment).add(bonusAmount)
                    .setScale(2, RoundingMode.HALF_UP);
        }
    }
    
    /**
     * CommissionCalculator class calculates commissions using a data-driven approach
     * 
     * This class demonstrates how operations can be organized around data transformations.
     */
    public static class CommissionCalculator {
        
        /**
         * Calculate commission for a deal using the specified plan
         * 
         * @param deal The deal for which to calculate commission
         * @param plan The commission plan to apply
         * @return A CommissionData object containing the calculation results
         */
        public CommissionData calculateCommission(Deal deal, CommissionPlan plan) {
            // Transform input data
            DealData dealData = new DealData(deal);
            PlanData planData = new PlanData(plan);
            
            // Validate inputs
            if (!planData.isActive()) {
                return createEmptyResult();
            }
            
            // Calculate product-specific commissions
            Map<String, BigDecimal> productCommissions = calculateProductCommissions(dealData, planData);
            
            // Calculate base commission
            BigDecimal baseCommission = calculateBaseCommission(dealData, planData);
            
            // Calculate tier adjustment
            BigDecimal tierAdjustment = calculateTierAdjustment(dealData, planData, baseCommission);
            
            // Calculate bonuses
            BigDecimal bonusAmount = calculateBonuses(dealData, planData);
            
            // Create and return the result
            return new CommissionData(baseCommission, tierAdjustment, bonusAmount, productCommissions);
        }
        
        /**
         * Create an empty result with zero values
         * 
         * @return An empty CommissionData object
         */
        private CommissionData createEmptyResult() {
            return new CommissionData(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    new HashMap<>()
            );
        }
        
        /**
         * Calculate commissions for each product in the deal
         * 
         * @param dealData The deal data
         * @param planData The plan data
         * @return A map of product IDs to their commission amounts
         */
        private Map<String, BigDecimal> calculateProductCommissions(DealData dealData, PlanData planData) {
            return dealData.getProducts().stream()
                    .collect(Collectors.toMap(
                            ProductData::getProductId,
                            product -> {
                                BigDecimal rate = planData.getRateForProduct(product.getProductId());
                                return product.getTotalPrice().multiply(rate)
                                        .setScale(2, RoundingMode.HALF_UP);
                            }
                    ));
        }
        
        /**
         * Calculate the base commission for the deal
         * 
         * @param dealData The deal data
         * @param planData The plan data
         * @return The base commission amount
         */
        private BigDecimal calculateBaseCommission(DealData dealData, PlanData planData) {
            return dealData.getTotalValue().multiply(planData.getBaseRate())
                    .setScale(2, RoundingMode.HALF_UP);
        }
        
        /**
         * Calculate tier adjustments to the base commission
         * 
         * @param dealData The deal data
         * @param planData The plan data
         * @param baseCommission The base commission amount
         * @return The tier adjustment amount
         */
        private BigDecimal calculateTierAdjustment(DealData dealData, PlanData planData, BigDecimal baseCommission) {
            BigDecimal dealValue = dealData.getTotalValue();
            TierData tier = planData.getTierForAmount(dealValue);
            
            // Calculate what the commission would be at the tier rate
            BigDecimal tieredCommission = dealValue.multiply(tier.getRate())
                    .setScale(2, RoundingMode.HALF_UP);
            
            // The adjustment is the difference between tiered and base commission
            return tieredCommission.subtract(baseCommission);
        }
        
        /**
         * Calculate bonuses for the deal
         * 
         * @param dealData The deal data
         * @param planData The plan data
         * @return The total bonus amount
         */
        private BigDecimal calculateBonuses(DealData dealData, PlanData planData) {
            BigDecimal totalBonus = BigDecimal.ZERO;
            
            // Quarter-end bonus
            if (dealData.isQuarterEndDeal()) {
                totalBonus = totalBonus.add(new BigDecimal("1000"));
            }
            
            // High-value deal bonus
            if (dealData.getTotalValue().compareTo(new BigDecimal("100000")) > 0) {
                totalBonus = totalBonus.add(new BigDecimal("2000"));
            }
            
            return totalBonus;
        }
    }
}