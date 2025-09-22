package com.chapman.edu.commissions.concerns;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.PlanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * This class demonstrates indirect object construction in Java.
 * 
 * Indirect object construction refers to creating objects through intermediary methods or classes
 * rather than directly using constructors. This approach provides several benefits:
 * 
 * 1. Encapsulates complex object creation logic
 * 2. Provides meaningful names for different object configurations
 * 3. Enables reuse of object creation code
 * 4. Allows for object caching and object pooling
 * 5. Supports the creation of immutable objects
 * 
 * Common patterns for indirect object construction include:
 * - Factory Method Pattern
 * - Abstract Factory Pattern
 * - Builder Pattern
 * - Prototype Pattern
 * 
 * This example demonstrates these patterns using the commission calculator domain.
 */
public class IndirectObjectConstructionExample {
    
    /**
     * Main method to demonstrate indirect object construction
     */
    public static void main(String[] args) {
        // Demonstrate Factory Method Pattern
        System.out.println("=== Factory Method Pattern ===");
        DealFactory dealFactory = new DealFactory();
        
        Deal standardDeal = dealFactory.createStandardDeal("Standard Deal", "REP001");
        System.out.println("Standard Deal: " + standardDeal.getTitle() + 
                ", Value: " + standardDeal.getValue());
        
        Deal premiumDeal = dealFactory.createPremiumDeal("Premium Deal", "REP001");
        System.out.println("Premium Deal: " + premiumDeal.getTitle() + 
                ", Value: " + premiumDeal.getValue());
        
        Deal enterpriseDeal = dealFactory.createEnterpriseDeal("Enterprise Deal", "REP001");
        System.out.println("Enterprise Deal: " + enterpriseDeal.getTitle() + 
                ", Value: " + enterpriseDeal.getValue());
        
        // Demonstrate Abstract Factory Pattern
        System.out.println("\n=== Abstract Factory Pattern ===");
        CommissionPlanFactory standardPlanFactory = new StandardCommissionPlanFactory();
        CommissionPlanFactory premiumPlanFactory = new PremiumCommissionPlanFactory();
        
        CommissionPlan standardPlan = standardPlanFactory.createCommissionPlan();
        System.out.println("Standard Plan: " + standardPlan.getName());
        
        CommissionPlan premiumPlan = premiumPlanFactory.createCommissionPlan();
        System.out.println("Premium Plan: " + premiumPlan.getName());
        
        // Demonstrate Builder Pattern
        System.out.println("\n=== Builder Pattern ===");
        CommissionCalculation calculation = new CommissionCalculation.Builder("CALC001")
                .dealId("DEAL001")
                .salesRepId("REP001")
                .calculationDate(LocalDate.now())
                .baseCommission(BigDecimal.valueOf(500))
                .tierAdjustment(BigDecimal.valueOf(100))
                .bonusAmount(BigDecimal.valueOf(50))
                .build();
        
        System.out.println("Commission Calculation: " + calculation);
        
        // Demonstrate Prototype Pattern
        System.out.println("\n=== Prototype Pattern ===");
        DealPrototype templateDeal = new DealPrototype("Template Deal", BigDecimal.valueOf(10000), "REP001");
        templateDeal.addProduct(new DealProduct("PROD001", "Product A", 1, BigDecimal.valueOf(5000)));
        templateDeal.addProduct(new DealProduct("PROD002", "Product B", 1, BigDecimal.valueOf(5000)));
        
        DealPrototype clonedDeal1 = templateDeal.clone();
        clonedDeal1.setTitle("Cloned Deal 1");
        
        DealPrototype clonedDeal2 = templateDeal.clone();
        clonedDeal2.setTitle("Cloned Deal 2");
        clonedDeal2.setValue(BigDecimal.valueOf(15000));
        
        System.out.println("Template Deal: " + templateDeal.getTitle() + 
                ", Value: " + templateDeal.getValue() + 
                ", Products: " + templateDeal.getProducts().size());
        
        System.out.println("Cloned Deal 1: " + clonedDeal1.getTitle() + 
                ", Value: " + clonedDeal1.getValue() + 
                ", Products: " + clonedDeal1.getProducts().size());
        
        System.out.println("Cloned Deal 2: " + clonedDeal2.getTitle() + 
                ", Value: " + clonedDeal2.getValue() + 
                ", Products: " + clonedDeal2.getProducts().size());
    }
    
    /**
     * Factory Method Pattern Example
     * 
     * The Factory Method pattern defines an interface for creating an object,
     * but lets subclasses decide which class to instantiate.
     */
    static class DealFactory {
        /**
         * Create a standard deal with default configuration
         * 
         * @param title the title of the deal
         * @param salesRepId the ID of the sales representative
         * @return a new standard deal
         */
        public Deal createStandardDeal(String title, String salesRepId) {
            Deal deal = new Deal(title, BigDecimal.valueOf(5000), salesRepId);
            deal.addProduct(new DealProduct("PROD001", "Standard Product", 1, BigDecimal.valueOf(5000)));
            return deal;
        }
        
        /**
         * Create a premium deal with higher value and multiple products
         * 
         * @param title the title of the deal
         * @param salesRepId the ID of the sales representative
         * @return a new premium deal
         */
        public Deal createPremiumDeal(String title, String salesRepId) {
            Deal deal = new Deal(title, BigDecimal.valueOf(10000), salesRepId);
            deal.addProduct(new DealProduct("PROD001", "Premium Product A", 1, BigDecimal.valueOf(6000)));
            deal.addProduct(new DealProduct("PROD002", "Premium Product B", 1, BigDecimal.valueOf(4000)));
            return deal;
        }
        
        /**
         * Create an enterprise deal with high value and multiple products
         * 
         * @param title the title of the deal
         * @param salesRepId the ID of the sales representative
         * @return a new enterprise deal
         */
        public Deal createEnterpriseDeal(String title, String salesRepId) {
            Deal deal = new Deal(title, BigDecimal.valueOf(25000), salesRepId);
            deal.addProduct(new DealProduct("PROD001", "Enterprise Product A", 2, BigDecimal.valueOf(8000)));
            deal.addProduct(new DealProduct("PROD002", "Enterprise Product B", 1, BigDecimal.valueOf(5000)));
            deal.addProduct(new DealProduct("PROD003", "Enterprise Product C", 1, BigDecimal.valueOf(4000)));
            return deal;
        }
    }
    
    /**
     * Abstract Factory Pattern Example
     * 
     * The Abstract Factory pattern provides an interface for creating families
     * of related or dependent objects without specifying their concrete classes.
     */
    interface CommissionPlanFactory {
        /**
         * Create a commission plan
         * 
         * @return a new commission plan
         */
        CommissionPlan createCommissionPlan();
    }
    
    /**
     * Concrete implementation of CommissionPlanFactory for standard plans
     */
    static class StandardCommissionPlanFactory implements CommissionPlanFactory {
        @Override
        public CommissionPlan createCommissionPlan() {
            CommissionPlan plan = new CommissionPlan("Standard Commission Plan", null);
            plan.setEffectiveStartDate(LocalDate.now());
            plan.setEffectiveEndDate(LocalDate.now().plusYears(1));
            plan.setStatus(PlanStatus.ACTIVE);
            return plan;
        }
    }
    
    /**
     * Concrete implementation of CommissionPlanFactory for premium plans
     */
    static class PremiumCommissionPlanFactory implements CommissionPlanFactory {
        @Override
        public CommissionPlan createCommissionPlan() {
            CommissionPlan plan = new CommissionPlan("Premium Commission Plan", null);
            plan.setEffectiveStartDate(LocalDate.now());
            plan.setEffectiveEndDate(LocalDate.now().plusYears(1));
            plan.setStatus(PlanStatus.ACTIVE);
            return plan;
        }
    }
    
    /**
     * Builder Pattern Example
     * 
     * The Builder pattern separates the construction of a complex object from its representation,
     * allowing the same construction process to create different representations.
     */
    static class CommissionCalculation {
        private final String id;
        private final String dealId;
        private final String salesRepId;
        private final LocalDate calculationDate;
        private final BigDecimal baseCommission;
        private final BigDecimal tierAdjustment;
        private final BigDecimal bonusAmount;
        
        private CommissionCalculation(Builder builder) {
            this.id = builder.id;
            this.dealId = builder.dealId;
            this.salesRepId = builder.salesRepId;
            this.calculationDate = builder.calculationDate;
            this.baseCommission = builder.baseCommission;
            this.tierAdjustment = builder.tierAdjustment;
            this.bonusAmount = builder.bonusAmount;
        }
        
        /**
         * Builder class for CommissionCalculation
         */
        static class Builder {
            // Required parameters
            private final String id;
            
            // Optional parameters - initialized to default values
            private String dealId = "";
            private String salesRepId = "";
            private LocalDate calculationDate = LocalDate.now();
            private BigDecimal baseCommission = BigDecimal.ZERO;
            private BigDecimal tierAdjustment = BigDecimal.ZERO;
            private BigDecimal bonusAmount = BigDecimal.ZERO;
            
            /**
             * Constructor with required parameters
             * 
             * @param id the ID of the commission calculation
             */
            public Builder(String id) {
                this.id = id;
            }
            
            /**
             * Set the deal ID
             * 
             * @param dealId the ID of the deal
             * @return this builder
             */
            public Builder dealId(String dealId) {
                this.dealId = dealId;
                return this;
            }
            
            /**
             * Set the sales rep ID
             * 
             * @param salesRepId the ID of the sales representative
             * @return this builder
             */
            public Builder salesRepId(String salesRepId) {
                this.salesRepId = salesRepId;
                return this;
            }
            
            /**
             * Set the calculation date
             * 
             * @param calculationDate the date of the calculation
             * @return this builder
             */
            public Builder calculationDate(LocalDate calculationDate) {
                this.calculationDate = calculationDate;
                return this;
            }
            
            /**
             * Set the base commission
             * 
             * @param baseCommission the base commission amount
             * @return this builder
             */
            public Builder baseCommission(BigDecimal baseCommission) {
                this.baseCommission = baseCommission;
                return this;
            }
            
            /**
             * Set the tier adjustment
             * 
             * @param tierAdjustment the tier adjustment amount
             * @return this builder
             */
            public Builder tierAdjustment(BigDecimal tierAdjustment) {
                this.tierAdjustment = tierAdjustment;
                return this;
            }
            
            /**
             * Set the bonus amount
             * 
             * @param bonusAmount the bonus amount
             * @return this builder
             */
            public Builder bonusAmount(BigDecimal bonusAmount) {
                this.bonusAmount = bonusAmount;
                return this;
            }
            
            /**
             * Build the CommissionCalculation object
             * 
             * @return a new CommissionCalculation object
             */
            public CommissionCalculation build() {
                return new CommissionCalculation(this);
            }
        }
        
        @Override
        public String toString() {
            return "CommissionCalculation{" +
                    "id='" + id + '\'' +
                    ", dealId='" + dealId + '\'' +
                    ", salesRepId='" + salesRepId + '\'' +
                    ", calculationDate=" + calculationDate +
                    ", baseCommission=" + baseCommission +
                    ", tierAdjustment=" + tierAdjustment +
                    ", bonusAmount=" + bonusAmount +
                    '}';
        }
    }
    
    /**
     * Prototype Pattern Example
     * 
     * The Prototype pattern creates new objects by copying an existing object,
     * known as the prototype.
     */
    static class DealPrototype implements Cloneable {
        private String title;
        private BigDecimal value;
        private String salesRepId;
        private List<DealProduct> products;
        
        /**
         * Constructor with essential fields
         * 
         * @param title the title of the deal
         * @param value the value of the deal
         * @param salesRepId the ID of the sales representative
         */
        public DealPrototype(String title, BigDecimal value, String salesRepId) {
            this.title = title;
            this.value = value;
            this.salesRepId = salesRepId;
            this.products = new ArrayList<>();
        }
        
        /**
         * Add a product to the deal
         * 
         * @param product the product to add
         */
        public void addProduct(DealProduct product) {
            this.products.add(product);
        }
        
        /**
         * Get the title of the deal
         * 
         * @return the title
         */
        public String getTitle() {
            return title;
        }
        
        /**
         * Set the title of the deal
         * 
         * @param title the new title
         */
        public void setTitle(String title) {
            this.title = title;
        }
        
        /**
         * Get the value of the deal
         * 
         * @return the value
         */
        public BigDecimal getValue() {
            return value;
        }
        
        /**
         * Set the value of the deal
         * 
         * @param value the new value
         */
        public void setValue(BigDecimal value) {
            this.value = value;
        }
        
        /**
         * Get the sales rep ID
         * 
         * @return the sales rep ID
         */
        public String getSalesRepId() {
            return salesRepId;
        }
        
        /**
         * Set the sales rep ID
         * 
         * @param salesRepId the new sales rep ID
         */
        public void setSalesRepId(String salesRepId) {
            this.salesRepId = salesRepId;
        }
        
        /**
         * Get the products in the deal
         * 
         * @return the list of products
         */
        public List<DealProduct> getProducts() {
            return products;
        }
        
        /**
         * Set the products in the deal
         * 
         * @param products the new list of products
         */
        public void setProducts(List<DealProduct> products) {
            this.products = products;
        }
        
        /**
         * Clone the deal
         * 
         * @return a new deal with the same properties
         */
        @Override
        public DealPrototype clone() {
            try {
                DealPrototype cloned = (DealPrototype) super.clone();
                
                // Deep copy of mutable objects
                cloned.products = new ArrayList<>();
                for (DealProduct product : this.products) {
                    DealProduct clonedProduct = new DealProduct(
                            product.getProductId(),
                            product.getProductName(),
                            product.getQuantity(),
                            product.getPrice()
                    );
                    cloned.products.add(clonedProduct);
                }
                
                return cloned;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("Could not clone deal", e);
            }
        }
    }
}