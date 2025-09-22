package com.chapman.edu.commissions.concerns;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * This class demonstrates the concept of immutable objects in Java.
 * 
 * Immutable objects are objects whose state cannot be changed after they are created.
 * Key characteristics of immutable objects:
 * 1. All fields are final
 * 2. The class is declared as final to prevent subclassing
 * 3. No setters or methods that modify state
 * 4. Proper handling of mutable object references (defensive copying)
 * 
 * Benefits of immutable objects:
 * - Thread-safe without synchronization
 * - Can be safely shared between multiple threads
 * - Simplifies concurrent programming
 * - Prevents temporal coupling
 * - Useful as keys in maps or elements in sets
 */
public class ImmutableObjectExample {
    
    /**
     * Main method to demonstrate immutable objects
     */
    public static void main(String[] args) {
        // Create a sample deal
        Deal deal = new Deal("Sample Deal", BigDecimal.valueOf(10000), "REP001");
        deal.setId("DEAL001");
        deal.setCloseDate(LocalDate.now());
        
        // Add products to the deal
        deal.addProduct(new DealProduct("PROD001", "Product A", 2, BigDecimal.valueOf(3000)));
        deal.addProduct(new DealProduct("PROD002", "Product B", 1, BigDecimal.valueOf(4000)));
        
        // Create an immutable commission calculation
        ImmutableCommissionCalculation calculation = new ImmutableCommissionCalculation(
                "CALC001",
                deal.getId(),
                "REP001",
                LocalDate.now(),
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(50),
                createProductCommissions(deal)
        );
        
        // Display the calculation
        System.out.println("Immutable Commission Calculation:");
        System.out.println("ID: " + calculation.getId());
        System.out.println("Deal ID: " + calculation.getDealId());
        System.out.println("Sales Rep ID: " + calculation.getSalesRepId());
        System.out.println("Calculation Date: " + calculation.getCalculationDate());
        System.out.println("Base Commission: " + calculation.getBaseCommission());
        System.out.println("Tier Adjustment: " + calculation.getTierAdjustment());
        System.out.println("Bonus Amount: " + calculation.getBonusAmount());
        System.out.println("Total Commission: " + calculation.getTotalCommission());
        System.out.println("Product Commissions: " + calculation.getProductCommissions());
        
        // Try to modify the immutable object (this would cause compilation errors if uncommented)
        // calculation.setBaseCommission(BigDecimal.valueOf(600)); // No setter methods
        
        // Create a new calculation with a different base commission
        ImmutableCommissionCalculation updatedCalculation = calculation.withBaseCommission(BigDecimal.valueOf(600));
        
        System.out.println("\nUpdated Immutable Commission Calculation:");
        System.out.println("ID: " + updatedCalculation.getId());
        System.out.println("Base Commission: " + updatedCalculation.getBaseCommission());
        System.out.println("Total Commission: " + updatedCalculation.getTotalCommission());
        
        // Original calculation remains unchanged
        System.out.println("\nOriginal calculation (unchanged):");
        System.out.println("Base Commission: " + calculation.getBaseCommission());
        System.out.println("Total Commission: " + calculation.getTotalCommission());
        
        // Demonstrate that the list of product commissions is truly immutable
        try {
            calculation.getProductCommissions().add(
                new ProductCommission("PROD003", BigDecimal.valueOf(100))
            );
            System.out.println("Failed: Product commissions list was modified!");
        } catch (UnsupportedOperationException e) {
            System.out.println("Success: Product commissions list is immutable and cannot be modified.");
        }
    }
    
    /**
     * Helper method to create product commissions
     */
    private static List<ProductCommission> createProductCommissions(Deal deal) {
        List<ProductCommission> productCommissions = new ArrayList<>();
        for (DealProduct product : deal.getProducts()) {
            productCommissions.add(new ProductCommission(
                    product.getProductId(),
                    product.getPrice().multiply(BigDecimal.valueOf(0.05))
            ));
        }
        return productCommissions;
    }
    
    /**
     * Immutable class representing a commission calculation.
     * Note the use of 'final' for the class and all fields.
     */
    public static final class ImmutableCommissionCalculation {
        private final String id;
        private final String dealId;
        private final String salesRepId;
        private final LocalDate calculationDate;
        private final BigDecimal baseCommission;
        private final BigDecimal tierAdjustment;
        private final BigDecimal bonusAmount;
        private final List<ProductCommission> productCommissions;
        
        /**
         * Constructor that initializes all fields.
         * Note that we make defensive copies of mutable objects.
         */
        public ImmutableCommissionCalculation(
                String id,
                String dealId,
                String salesRepId,
                LocalDate calculationDate,
                BigDecimal baseCommission,
                BigDecimal tierAdjustment,
                BigDecimal bonusAmount,
                List<ProductCommission> productCommissions) {
            
            this.id = Objects.requireNonNull(id, "ID cannot be null");
            this.dealId = Objects.requireNonNull(dealId, "Deal ID cannot be null");
            this.salesRepId = Objects.requireNonNull(salesRepId, "Sales Rep ID cannot be null");
            
            // Defensive copy for mutable objects
            this.calculationDate = calculationDate != null ? LocalDate.from(calculationDate) : null;
            this.baseCommission = baseCommission != null ? new BigDecimal(baseCommission.toString()) : BigDecimal.ZERO;
            this.tierAdjustment = tierAdjustment != null ? new BigDecimal(tierAdjustment.toString()) : BigDecimal.ZERO;
            this.bonusAmount = bonusAmount != null ? new BigDecimal(bonusAmount.toString()) : BigDecimal.ZERO;
            
            // Create an unmodifiable copy of the list
            this.productCommissions = productCommissions != null ? 
                    Collections.unmodifiableList(new ArrayList<>(productCommissions)) : 
                    Collections.emptyList();
        }
        
        // Only getters, no setters to maintain immutability
        
        public String getId() {
            return id;
        }
        
        public String getDealId() {
            return dealId;
        }
        
        public String getSalesRepId() {
            return salesRepId;
        }
        
        public LocalDate getCalculationDate() {
            // Return a copy to prevent modification of the internal state
            return calculationDate != null ? LocalDate.from(calculationDate) : null;
        }
        
        public BigDecimal getBaseCommission() {
            // Return a copy to prevent modification of the internal state
            return new BigDecimal(baseCommission.toString());
        }
        
        public BigDecimal getTierAdjustment() {
            // Return a copy to prevent modification of the internal state
            return new BigDecimal(tierAdjustment.toString());
        }
        
        public BigDecimal getBonusAmount() {
            // Return a copy to prevent modification of the internal state
            return new BigDecimal(bonusAmount.toString());
        }
        
        public List<ProductCommission> getProductCommissions() {
            // Already unmodifiable from constructor, so safe to return
            return productCommissions;
        }
        
        /**
         * Calculate the total commission
         * @return the total commission amount
         */
        public BigDecimal getTotalCommission() {
            return baseCommission.add(tierAdjustment).add(bonusAmount);
        }
        
        /**
         * Create a new ImmutableCommissionCalculation with a different base commission.
         * This is how you "modify" immutable objects - by creating new instances.
         * 
         * @param newBaseCommission the new base commission
         * @return a new ImmutableCommissionCalculation with the updated base commission
         */
        public ImmutableCommissionCalculation withBaseCommission(BigDecimal newBaseCommission) {
            return new ImmutableCommissionCalculation(
                    this.id,
                    this.dealId,
                    this.salesRepId,
                    this.calculationDate,
                    newBaseCommission,
                    this.tierAdjustment,
                    this.bonusAmount,
                    this.productCommissions
            );
        }
        
        /**
         * Create a new ImmutableCommissionCalculation with a different tier adjustment.
         * 
         * @param newTierAdjustment the new tier adjustment
         * @return a new ImmutableCommissionCalculation with the updated tier adjustment
         */
        public ImmutableCommissionCalculation withTierAdjustment(BigDecimal newTierAdjustment) {
            return new ImmutableCommissionCalculation(
                    this.id,
                    this.dealId,
                    this.salesRepId,
                    this.calculationDate,
                    this.baseCommission,
                    newTierAdjustment,
                    this.bonusAmount,
                    this.productCommissions
            );
        }
        
        /**
         * Create a new ImmutableCommissionCalculation with a different bonus amount.
         * 
         * @param newBonusAmount the new bonus amount
         * @return a new ImmutableCommissionCalculation with the updated bonus amount
         */
        public ImmutableCommissionCalculation withBonusAmount(BigDecimal newBonusAmount) {
            return new ImmutableCommissionCalculation(
                    this.id,
                    this.dealId,
                    this.salesRepId,
                    this.calculationDate,
                    this.baseCommission,
                    this.tierAdjustment,
                    newBonusAmount,
                    this.productCommissions
            );
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ImmutableCommissionCalculation that = (ImmutableCommissionCalculation) o;
            return Objects.equals(id, that.id) &&
                   Objects.equals(dealId, that.dealId) &&
                   Objects.equals(salesRepId, that.salesRepId) &&
                   Objects.equals(calculationDate, that.calculationDate) &&
                   Objects.equals(baseCommission, that.baseCommission) &&
                   Objects.equals(tierAdjustment, that.tierAdjustment) &&
                   Objects.equals(bonusAmount, that.bonusAmount) &&
                   Objects.equals(productCommissions, that.productCommissions);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(id, dealId, salesRepId, calculationDate, 
                    baseCommission, tierAdjustment, bonusAmount, productCommissions);
        }
        
        @Override
        public String toString() {
            return "ImmutableCommissionCalculation{" +
                    "id='" + id + '\'' +
                    ", dealId='" + dealId + '\'' +
                    ", salesRepId='" + salesRepId + '\'' +
                    ", calculationDate=" + calculationDate +
                    ", baseCommission=" + baseCommission +
                    ", tierAdjustment=" + tierAdjustment +
                    ", bonusAmount=" + bonusAmount +
                    ", productCommissions=" + productCommissions +
                    '}';
        }
    }
    
    /**
     * Immutable class representing a commission for a specific product.
     */
    public static final class ProductCommission {
        private final String productId;
        private final BigDecimal amount;
        
        public ProductCommission(String productId, BigDecimal amount) {
            this.productId = Objects.requireNonNull(productId, "Product ID cannot be null");
            this.amount = amount != null ? new BigDecimal(amount.toString()) : BigDecimal.ZERO;
        }
        
        public String getProductId() {
            return productId;
        }
        
        public BigDecimal getAmount() {
            return new BigDecimal(amount.toString());
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProductCommission that = (ProductCommission) o;
            return Objects.equals(productId, that.productId) &&
                   Objects.equals(amount, that.amount);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(productId, amount);
        }
        
        @Override
        public String toString() {
            return "ProductCommission{" +
                    "productId='" + productId + '\'' +
                    ", amount=" + amount +
                    '}';
        }
    }
}