package com.chapman.edu.commissions.leaks;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.CommissionPlan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class demonstrates a leaky abstraction related to the "Standard Interfaces" principle.
 * 
 * Standard Interfaces is a principle that suggests using consistent, well-defined interfaces
 * across a system to promote interoperability, reusability, and maintainability.
 * 
 * When interfaces are not standardized, abstractions leak implementation details, making
 * the system harder to understand, use, and maintain.
 */
public class StandardInterfacesExample {

    /**
     * This example demonstrates non-standard interfaces that leak implementation details.
     */
    public static class NonStandardInterfaces {
        
        /**
         * A calculator for standard deals with a non-standard interface.
         * This is a leaky abstraction because it exposes implementation details in its interface.
         */
        public static class StandardDealCalculator {
            /**
             * Calculates commission for a standard deal.
             * The interface leaks implementation details by:
             * 1. Using a specific naming convention tied to implementation
             * 2. Requiring specific deal type knowledge from the caller
             * 3. Having inconsistent parameter ordering compared to other calculators
             */
            public BigDecimal calculateStandardDealCommission(Deal deal, BigDecimal rate) {
                if (deal == null || rate == null) {
                    throw new IllegalArgumentException("Deal and rate cannot be null");
                }
                
                return deal.getValue().multiply(rate);
            }
        }
        
        /**
         * A calculator for premium deals with a different non-standard interface.
         * This is a leaky abstraction because it uses a different interface than StandardDealCalculator.
         */
        public static class PremiumDealCalculator {
            /**
             * Calculates commission for a premium deal.
             * The interface is inconsistent with StandardDealCalculator:
             * 1. Different method name
             * 2. Different parameter order
             * 3. Additional parameters not present in other calculators
             */
            public BigDecimal computePremiumCommission(BigDecimal baseRate, Deal premiumDeal, boolean applyBonus) {
                if (premiumDeal == null || baseRate == null) {
                    throw new IllegalArgumentException("Deal and rate cannot be null");
                }
                
                BigDecimal commission = premiumDeal.getValue().multiply(baseRate);
                
                if (applyBonus) {
                    commission = commission.add(new BigDecimal("100"));
                }
                
                return commission;
            }
        }
        
        /**
         * A calculator for enterprise deals with yet another non-standard interface.
         * This is a leaky abstraction because it uses a completely different approach.
         */
        public static class EnterpriseDealCalculator {
            private BigDecimal enterpriseRate;
            
            /**
             * Constructor that takes a rate.
             * This is inconsistent with other calculators that take the rate as a parameter.
             */
            public EnterpriseDealCalculator(BigDecimal enterpriseRate) {
                this.enterpriseRate = enterpriseRate;
            }
            
            /**
             * Calculates commission for an enterprise deal.
             * The interface is inconsistent with other calculators:
             * 1. Different method name
             * 2. Rate is a class field, not a parameter
             * 3. Returns a different type (String instead of BigDecimal)
             */
            public String getEnterpriseCommissionAsString(Deal enterpriseDeal) {
                if (enterpriseDeal == null) {
                    throw new IllegalArgumentException("Deal cannot be null");
                }
                
                BigDecimal commission = enterpriseDeal.getValue().multiply(enterpriseRate);
                
                // Leaky abstraction: Returns a formatted string instead of a BigDecimal
                return "$" + commission.toString();
            }
        }
        
        /**
         * A service that uses the non-standard calculators.
         * This class has to deal with the inconsistent interfaces of the calculators.
         */
        public static class CommissionService {
            private StandardDealCalculator standardCalculator = new StandardDealCalculator();
            private PremiumDealCalculator premiumCalculator = new PremiumDealCalculator();
            private EnterpriseDealCalculator enterpriseCalculator = new EnterpriseDealCalculator(new BigDecimal("0.10"));
            
            /**
             * Calculates commission for a deal.
             * This method has to handle the inconsistent interfaces of the calculators.
             */
            public String calculateCommission(Deal deal) {
                if (deal == null) {
                    throw new IllegalArgumentException("Deal cannot be null");
                }
                
                // Leaky abstraction: Client code needs to know about different calculator types and interfaces
                if (deal.getTitle().contains("Standard")) {
                    BigDecimal commission = standardCalculator.calculateStandardDealCommission(deal, new BigDecimal("0.05"));
                    return "$" + commission.toString();
                } else if (deal.getTitle().contains("Premium")) {
                    BigDecimal commission = premiumCalculator.computePremiumCommission(new BigDecimal("0.08"), deal, true);
                    return "$" + commission.toString();
                } else if (deal.getTitle().contains("Enterprise")) {
                    // Already returns a string with $ prefix
                    return enterpriseCalculator.getEnterpriseCommissionAsString(deal);
                } else {
                    BigDecimal commission = standardCalculator.calculateStandardDealCommission(deal, new BigDecimal("0.03"));
                    return "$" + commission.toString();
                }
            }
        }
    }
    
    /**
     * This example demonstrates standard interfaces that hide implementation details.
     */
    public static class StandardInterfaces {
        
        /**
         * A standard interface for commission calculators.
         * This provides a consistent contract for all calculator implementations.
         */
        public interface CommissionCalculator {
            /**
             * Calculates commission for a deal.
             * 
             * @param deal The deal to calculate commission for
             * @return The calculated commission amount
             */
            BigDecimal calculateCommission(Deal deal);
        }
        
        /**
         * Implementation of CommissionCalculator for standard deals.
         */
        public static class StandardDealCalculator implements CommissionCalculator {
            private final BigDecimal rate;
            
            public StandardDealCalculator(BigDecimal rate) {
                this.rate = rate;
            }
            
            @Override
            public BigDecimal calculateCommission(Deal deal) {
                return deal.getValue().multiply(rate);
            }
        }
        
        /**
         * Implementation of CommissionCalculator for premium deals.
         */
        public static class PremiumDealCalculator implements CommissionCalculator {
            private final BigDecimal rate;
            private final boolean applyBonus;
            
            public PremiumDealCalculator(BigDecimal rate, boolean applyBonus) {
                this.rate = rate;
                this.applyBonus = applyBonus;
            }
            
            @Override
            public BigDecimal calculateCommission(Deal deal) {
                BigDecimal commission = deal.getValue().multiply(rate);
                
                if (applyBonus) {
                    commission = commission.add(new BigDecimal("100"));
                }
                
                return commission;
            }
        }
        
        /**
         * Implementation of CommissionCalculator for enterprise deals.
         */
        public static class EnterpriseDealCalculator implements CommissionCalculator {
            private final BigDecimal rate;
            
            public EnterpriseDealCalculator(BigDecimal rate) {
                this.rate = rate;
            }
            
            @Override
            public BigDecimal calculateCommission(Deal deal) {
                return deal.getValue().multiply(rate);
            }
        }
        
        /**
         * A factory for creating commission calculators based on deal type.
         */
        public static class CommissionCalculatorFactory {
            /**
             * Creates a commission calculator for a deal.
             */
            public static CommissionCalculator createCalculator(Deal deal) {
                if (deal.getTitle().contains("Standard")) {
                    return new StandardDealCalculator(new BigDecimal("0.05"));
                } else if (deal.getTitle().contains("Premium")) {
                    return new PremiumDealCalculator(new BigDecimal("0.08"), true);
                } else if (deal.getTitle().contains("Enterprise")) {
                    return new EnterpriseDealCalculator(new BigDecimal("0.10"));
                } else {
                    return new StandardDealCalculator(new BigDecimal("0.03"));
                }
            }
        }
        
        /**
         * A service that uses the standard calculators.
         * This class benefits from the consistent interface of all calculators.
         */
        public static class CommissionService {
            /**
             * Calculates commission for a deal.
             * This method uses the standard interface to handle all calculator types uniformly.
             */
            public String calculateCommission(Deal deal) {
                if (deal == null) {
                    throw new IllegalArgumentException("Deal cannot be null");
                }
                
                // Standard interface: Client code doesn't need to know about different calculator types
                CommissionCalculator calculator = CommissionCalculatorFactory.createCalculator(deal);
                BigDecimal commission = calculator.calculateCommission(deal);
                
                return "$" + commission.toString();
            }
        }
    }
    
    /**
     * Main method to demonstrate the non-standard and standard interfaces.
     */
    public static void main(String[] args) {
        // Create sample deals
        Deal standardDeal = new Deal("Standard Deal", new BigDecimal("10000"), "REP001");
        Deal premiumDeal = new Deal("Premium Deal", new BigDecimal("20000"), "REP001");
        Deal enterpriseDeal = new Deal("Enterprise Deal", new BigDecimal("50000"), "REP001");
        Deal customDeal = new Deal("Custom Deal", new BigDecimal("15000"), "REP001");
        
        // Demonstrate non-standard interfaces
        System.out.println("=== Non-Standard Interfaces ===");
        NonStandardInterfaces.CommissionService nonStandardService = new NonStandardInterfaces.CommissionService();
        
        System.out.println("Standard Deal Commission: " + nonStandardService.calculateCommission(standardDeal));
        System.out.println("Premium Deal Commission: " + nonStandardService.calculateCommission(premiumDeal));
        System.out.println("Enterprise Deal Commission: " + nonStandardService.calculateCommission(enterpriseDeal));
        System.out.println("Custom Deal Commission: " + nonStandardService.calculateCommission(customDeal));
        
        // Demonstrate standard interfaces
        System.out.println("\n=== Standard Interfaces ===");
        StandardInterfaces.CommissionService standardService = new StandardInterfaces.CommissionService();
        
        System.out.println("Standard Deal Commission: " + standardService.calculateCommission(standardDeal));
        System.out.println("Premium Deal Commission: " + standardService.calculateCommission(premiumDeal));
        System.out.println("Enterprise Deal Commission: " + standardService.calculateCommission(enterpriseDeal));
        System.out.println("Custom Deal Commission: " + standardService.calculateCommission(customDeal));
    }
}