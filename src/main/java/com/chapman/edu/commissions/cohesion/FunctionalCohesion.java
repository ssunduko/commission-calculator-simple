package com.chapman.edu.commissions.cohesion;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Example of Functional Cohesion.
 * 
 * Functional Cohesion is the strongest form of cohesion, where all elements of a module
 * contribute to a single, well-defined task or purpose. Every part of the module is essential
 * to the performance of a single function, and the module has a clear, singular purpose.
 * 
 * This class demonstrates functional cohesion by focusing solely on the task of calculating
 * the total value of a deal with various discounts. All methods in this class contribute to
 * this single, well-defined purpose.
 */
public class FunctionalCohesion {

    /**
     * Calculates the final value of a deal after applying all applicable discounts.
     * This is the main method that orchestrates the discount calculation process.
     * @param deal the deal to calculate the value for
     * @param earlyPayment whether early payment is selected
     * @return the final value after discounts
     */
    public BigDecimal calculateDealValueWithDiscounts(Deal deal, boolean earlyPayment) {
        if (deal == null) {
            throw new IllegalArgumentException("Deal cannot be null");
        }
        // Calculate the base value of the deal
        BigDecimal baseValue = calculateBaseValue(deal);
        // Calculate the volume discount
        BigDecimal volumeDiscount = calculateVolumeDiscount(baseValue);
        // Calculate the multi-product discount
        BigDecimal multiProductDiscount = calculateMultiProductDiscount(deal.getProducts(), baseValue);
        // Calculate the early payment discount
        BigDecimal earlyPaymentDiscount = calculateEarlyPaymentDiscount(baseValue, earlyPayment);
        // Calculate the total discount
        BigDecimal totalDiscount = calculateTotalDiscount(volumeDiscount, multiProductDiscount, earlyPaymentDiscount);
        // Apply the total discount to the base value
        BigDecimal finalValue = applyDiscount(baseValue, totalDiscount);
        
        return finalValue;
    }

    // Constants for discount calculations
    private static final BigDecimal VOLUME_DISCOUNT_THRESHOLD = new BigDecimal("10000");
    private static final BigDecimal VOLUME_DISCOUNT_RATE = new BigDecimal("0.05");
    private static final int MULTI_PRODUCT_DISCOUNT_THRESHOLD = 3;
    private static final BigDecimal MULTI_PRODUCT_DISCOUNT_RATE = new BigDecimal("0.03");
    private static final BigDecimal EARLY_PAYMENT_DISCOUNT_RATE = new BigDecimal("0.02");
    
    /**
     * Calculates the base value of a deal by summing the values of all its products.
     * 
     * @param deal the deal to calculate the base value for
     * @return the base value of the deal
     */
    private BigDecimal calculateBaseValue(Deal deal) {
        BigDecimal baseValue = BigDecimal.ZERO;
        
        for (DealProduct product : deal.getProducts()) {
            BigDecimal productValue = product.getPrice().multiply(new BigDecimal(product.getQuantity()));
            baseValue = baseValue.add(productValue);
        }
        
        return baseValue;
    }
    
    /**
     * Calculates the volume discount based on the base value of the deal.
     * 
     * @param baseValue the base value of the deal
     * @return the volume discount amount
     */
    private BigDecimal calculateVolumeDiscount(BigDecimal baseValue) {
        if (baseValue.compareTo(VOLUME_DISCOUNT_THRESHOLD) >= 0) {
            return baseValue.multiply(VOLUME_DISCOUNT_RATE);
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Calculates the multi-product discount based on the number of products in the deal.
     * 
     * @param products the products in the deal
     * @param baseValue the base value of the deal
     * @return the multi-product discount amount
     */
    private BigDecimal calculateMultiProductDiscount(List<DealProduct> products, BigDecimal baseValue) {
        if (products.size() >= MULTI_PRODUCT_DISCOUNT_THRESHOLD) {
            return baseValue.multiply(MULTI_PRODUCT_DISCOUNT_RATE);
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Calculates the early payment discount if applicable.
     * 
     * @param baseValue the base value of the deal
     * @param earlyPayment whether early payment is selected
     * @return the early payment discount amount
     */
    private BigDecimal calculateEarlyPaymentDiscount(BigDecimal baseValue, boolean earlyPayment) {
        if (earlyPayment) {
            return baseValue.multiply(EARLY_PAYMENT_DISCOUNT_RATE);
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Calculates the total discount by summing all individual discounts.
     * 
     * @param volumeDiscount the volume discount amount
     * @param multiProductDiscount the multi-product discount amount
     * @param earlyPaymentDiscount the early payment discount amount
     * @return the total discount amount
     */
    private BigDecimal calculateTotalDiscount(BigDecimal volumeDiscount, 
                                             BigDecimal multiProductDiscount, 
                                             BigDecimal earlyPaymentDiscount) {
        return volumeDiscount.add(multiProductDiscount).add(earlyPaymentDiscount);
    }
    
    /**
     * Applies the discount to the base value to get the final value.
     * 
     * @param baseValue the base value of the deal
     * @param totalDiscount the total discount amount
     * @return the final value after applying the discount
     */
    private BigDecimal applyDiscount(BigDecimal baseValue, BigDecimal totalDiscount) {
        BigDecimal finalValue = baseValue.subtract(totalDiscount);
        // Ensure the final value is not negative
        return finalValue.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculates the discount percentage based on the base value and total discount.
     * 
     * @param baseValue the base value of the deal
     * @param totalDiscount the total discount amount
     * @return the discount percentage
     */
    public BigDecimal calculateDiscountPercentage(BigDecimal baseValue, BigDecimal totalDiscount) {
        if (baseValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return totalDiscount.divide(baseValue, 4, RoundingMode.HALF_UP)
                           .multiply(new BigDecimal("100"))
                           .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Determines if a deal is eligible for any discounts.
     * 
     * @param deal the deal to check
     * @return true if the deal is eligible for any discounts, false otherwise
     */
    public boolean isDealEligibleForDiscounts(Deal deal) {
        if (deal == null) {
            return false;
        }
        
        // Check if the deal is in a status that allows discounts
        if (deal.getStatus() != DealStatus.OPEN) {
            return false;
        }
        
        // Check if the deal has products
        if (deal.getProducts() == null || deal.getProducts().isEmpty()) {
            return false;
        }
        
        // Check if the deal has a positive value
        BigDecimal baseValue = calculateBaseValue(deal);
        if (baseValue.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        return true;
    }
}