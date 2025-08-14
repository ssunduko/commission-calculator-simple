package com.chapman.edu.commissions.principles.solid.original;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * This class violates the Open/Closed Principle (OCP).
 * 
 * OCP states that software entities should be open for extension but closed for modification.
 * This means that the behavior of a module can be extended without modifying its source code.
 * 
 * This class violates OCP because:
 * 1. It uses conditional logic (if-else statements) to handle different product types
 * 2. Adding a new product type requires modifying the existing code
 * 3. The calculateDiscount method would need to be modified for each new discount type
 */
public class OCPViolation {

    /**
     * Calculates the discount for a deal based on product types.
     * This method violates OCP because adding a new product type requires modifying this method.
     */
    public BigDecimal calculateDiscount(Deal deal) {
        BigDecimal totalDiscount = BigDecimal.ZERO;
        List<DealProduct> products = deal.getProducts();

        for (DealProduct product : products) {
            String productType = product.getProductId(); // Using productId as the type identifier
            BigDecimal productPrice = product.getPrice();
            int quantity = product.getQuantity();

            // Violates OCP: Hard-coded conditional logic for different product types
            if ("SOFTWARE".equals(productType)) {
                // 10% discount for software products
                BigDecimal discount = productPrice.multiply(BigDecimal.valueOf(0.10))
                        .multiply(BigDecimal.valueOf(quantity));
                totalDiscount = totalDiscount.add(discount);
            } else if ("HARDWARE".equals(productType)) {
                // 5% discount for hardware products
                BigDecimal discount = productPrice.multiply(BigDecimal.valueOf(0.05))
                        .multiply(BigDecimal.valueOf(quantity));
                totalDiscount = totalDiscount.add(discount);
            } else if ("SERVICE".equals(productType)) {
                // 15% discount for service products
                BigDecimal discount = productPrice.multiply(BigDecimal.valueOf(0.15))
                        .multiply(BigDecimal.valueOf(quantity));
                totalDiscount = totalDiscount.add(discount);
            } else if ("TRAINING".equals(productType)) {
                // 20% discount for training products
                BigDecimal discount = productPrice.multiply(BigDecimal.valueOf(0.20))
                        .multiply(BigDecimal.valueOf(quantity));
                totalDiscount = totalDiscount.add(discount);
            }
            // If a new product type is added, this method must be modified!
        }

        return totalDiscount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the commission for a deal based on product types.
     * This method also violates OCP for the same reason.
     */
    public BigDecimal calculateCommission(Deal deal) {
        BigDecimal totalCommission = BigDecimal.ZERO;
        List<DealProduct> products = deal.getProducts();

        for (DealProduct product : products) {
            String productType = product.getProductId(); // Using productId as the type identifier
            BigDecimal productPrice = product.getPrice();
            int quantity = product.getQuantity();
            BigDecimal productTotal = productPrice.multiply(BigDecimal.valueOf(quantity));

            // Violates OCP: Hard-coded conditional logic for different product types
            if ("SOFTWARE".equals(productType)) {
                // 8% commission for software products
                BigDecimal commission = productTotal.multiply(BigDecimal.valueOf(0.08));
                totalCommission = totalCommission.add(commission);
            } else if ("HARDWARE".equals(productType)) {
                // 5% commission for hardware products
                BigDecimal commission = productTotal.multiply(BigDecimal.valueOf(0.05));
                totalCommission = totalCommission.add(commission);
            } else if ("SERVICE".equals(productType)) {
                // 10% commission for service products
                BigDecimal commission = productTotal.multiply(BigDecimal.valueOf(0.10));
                totalCommission = totalCommission.add(commission);
            } else if ("TRAINING".equals(productType)) {
                // 12% commission for training products
                BigDecimal commission = productTotal.multiply(BigDecimal.valueOf(0.12));
                totalCommission = totalCommission.add(commission);
            }
            // If a new product type is added, this method must be modified!
        }

        return totalCommission.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the tax for a deal based on product types.
     * This method also violates OCP for the same reason.
     */
    public BigDecimal calculateTax(Deal deal, String countryCode) {
        BigDecimal totalTax = BigDecimal.ZERO;
        List<DealProduct> products = deal.getProducts();

        for (DealProduct product : products) {
            BigDecimal productPrice = product.getPrice();
            int quantity = product.getQuantity();
            BigDecimal productTotal = productPrice.multiply(BigDecimal.valueOf(quantity));

            // Violates OCP: Hard-coded conditional logic for different country codes
            if ("US".equals(countryCode)) {
                // 8.5% tax for US
                BigDecimal tax = productTotal.multiply(BigDecimal.valueOf(0.085));
                totalTax = totalTax.add(tax);
            } else if ("UK".equals(countryCode)) {
                // 20% VAT for UK
                BigDecimal tax = productTotal.multiply(BigDecimal.valueOf(0.20));
                totalTax = totalTax.add(tax);
            } else if ("CA".equals(countryCode)) {
                // 13% tax for Canada
                BigDecimal tax = productTotal.multiply(BigDecimal.valueOf(0.13));
                totalTax = totalTax.add(tax);
            } else if ("AU".equals(countryCode)) {
                // 10% GST for Australia
                BigDecimal tax = productTotal.multiply(BigDecimal.valueOf(0.10));
                totalTax = totalTax.add(tax);
            } else {
                // Default 5% tax for other countries
                BigDecimal tax = productTotal.multiply(BigDecimal.valueOf(0.05));
                totalTax = totalTax.add(tax);
            }
            // If a new country is added, this method must be modified!
        }

        return totalTax.setScale(2, RoundingMode.HALF_UP);
    }
}
