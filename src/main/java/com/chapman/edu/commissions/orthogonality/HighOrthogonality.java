package com.chapman.edu.commissions.orthogonality;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Example of High Orthogonality.
 * 
 * Orthogonality in software design refers to the ability to change one component
 * without affecting others. It's a measure of independence between components.
 * 
 * This class demonstrates high orthogonality by separating different concerns
 * into independent components that can be modified without affecting each other.
 * Each component has a single responsibility and doesn't depend on the implementation
 * details of other components.
 */
public class HighOrthogonality {

    /**
     * The DealProcessor component handles deal-related operations.
     * It can be modified without affecting the UserProcessor or ReportGenerator.
     */
    public static class DealProcessor {
        /**
         * Calculates the total value of a deal.
         * 
         * @param deal the deal to calculate the value for
         * @return the total value of the deal
         */
        public BigDecimal calculateDealValue(Deal deal) {
            if (deal == null) {
                throw new IllegalArgumentException("Deal cannot be null");
            }
            
            BigDecimal totalValue = BigDecimal.ZERO;
            for (DealProduct product : deal.getProducts()) {
                BigDecimal productValue = product.getPrice().multiply(new BigDecimal(product.getQuantity()));
                totalValue = totalValue.add(productValue);
            }
            
            return totalValue.setScale(2, RoundingMode.HALF_UP);
        }
        
        /**
         * Applies a discount to a deal.
         * 
         * @param deal the deal to apply the discount to
         * @param discountPercentage the discount percentage
         * @return the discounted value
         */
        public BigDecimal applyDiscount(Deal deal, BigDecimal discountPercentage) {
            BigDecimal totalValue = calculateDealValue(deal);
            BigDecimal discountFactor = BigDecimal.ONE.subtract(discountPercentage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            return totalValue.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
        }
    }
    
    /**
     * The UserProcessor component handles user-related operations.
     * It can be modified without affecting the DealProcessor or ReportGenerator.
     */
    public static class UserProcessor {
        /**
         * Gets the full name of a user.
         * 
         * @param user the user to get the full name for
         * @return the full name of the user
         */
        public String getFullName(User user) {
            if (user == null) {
                throw new IllegalArgumentException("User cannot be null");
            }
            
            return user.getFirstName() + " " + user.getLastName();
        }
        
        /**
         * Checks if a user is a sales representative.
         * 
         * @param user the user to check
         * @return true if the user is a sales representative, false otherwise
         */
        public boolean isSalesRep(User user) {
            if (user == null) {
                throw new IllegalArgumentException("User cannot be null");
            }
            
            return user.isSalesRep();
        }
    }
    
    /**
     * The ReportGenerator component handles report generation.
     * It can be modified without affecting the DealProcessor or UserProcessor.
     */
    public static class ReportGenerator {
        private final DealProcessor dealProcessor;
        private final UserProcessor userProcessor;
        
        /**
         * Constructor that takes dependencies as parameters.
         * This is an example of dependency injection, which enhances orthogonality.
         */
        public ReportGenerator(DealProcessor dealProcessor, UserProcessor userProcessor) {
            this.dealProcessor = dealProcessor;
            this.userProcessor = userProcessor;
        }
        
        /**
         * Generates a deal summary report.
         * 
         * @param deal the deal to generate a report for
         * @param user the user who owns the deal
         * @return a summary report
         */
        public String generateDealSummary(Deal deal, User user) {
            if (deal == null || user == null) {
                throw new IllegalArgumentException("Deal and user cannot be null");
            }
            
            BigDecimal dealValue = dealProcessor.calculateDealValue(deal);
            String salesRepName = userProcessor.getFullName(user);
            
            StringBuilder report = new StringBuilder();
            report.append("Deal Summary\n");
            report.append("===========\n");
            report.append("Deal ID: ").append(deal.getId()).append("\n");
            report.append("Title: ").append(deal.getTitle()).append("\n");
            report.append("Value: $").append(dealValue).append("\n");
            report.append("Status: ").append(deal.getStatus()).append("\n");
            report.append("Sales Rep: ").append(salesRepName).append("\n");
            report.append("Products: ").append(deal.getProducts().size()).append("\n");
            
            return report.toString();
        }
    }
    
    /**
     * Demonstrates how the orthogonal components can be used together.
     * Each component can be modified independently without affecting the others.
     */
    public void demonstrateOrthogonality() {
        // Create a deal and a user
        Deal deal = new Deal("Sample Deal", new BigDecimal("10000"), "user123");
        deal.setId("deal123");
        
        DealProduct product1 = new DealProduct("prod1", "Product 1", 2, new BigDecimal("1000"));
        DealProduct product2 = new DealProduct("prod2", "Product 2", 3, new BigDecimal("2000"));
        
        deal.addProduct(product1);
        deal.addProduct(product2);
        
        User user = new User("jdoe", "jdoe@example.com", "John", "Doe");
        user.setId("user123");
        
        // Create the orthogonal components
        DealProcessor dealProcessor = new DealProcessor();
        UserProcessor userProcessor = new UserProcessor();
        ReportGenerator reportGenerator = new ReportGenerator(dealProcessor, userProcessor);
        
        // Use the components
        BigDecimal dealValue = dealProcessor.calculateDealValue(deal);
        BigDecimal discountedValue = dealProcessor.applyDiscount(deal, new BigDecimal("10"));
        String fullName = userProcessor.getFullName(user);
        String report = reportGenerator.generateDealSummary(deal, user);
        
        // The components can be modified independently without affecting each other
        // For example, we could change how the DealProcessor calculates deal values
        // without affecting the UserProcessor or how the ReportGenerator formats reports
    }
}