package com.chapman.edu.commissions.coupling;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;

import java.math.BigDecimal;

/**
 * Content Coupling Example
 * 
 * Content coupling occurs when one module directly accesses or modifies the internal data of another module.
 * This is considered the highest level of coupling and should generally be avoided.
 * 
 * In this example, the ContentCoupling class directly accesses the internal products list of the Deal class
 * without using proper accessor methods, creating a tight dependency between the classes.
 */
public class ContentCoupling {

    /**
     * This method demonstrates content coupling by directly accessing and modifying
     * the internal products list of a Deal object.
     * 
     * @param deal The deal whose products will be directly accessed
     */
    public void addSpecialProduct(Deal deal) {
        // Content coupling: Directly accessing the internal products list
        // This creates a tight dependency on the internal implementation of Deal
        deal.getProducts().add(new DealProduct("special-product", "Special Product", 1, new BigDecimal("99.99")));

        System.out.println("Special product added directly to deal's product list");
        System.out.println("Total products: " + deal.getProducts().size());
    }

    /**
     * This method demonstrates content coupling by directly accessing the internal
     * status update counter of the Deal class.
     */
    public void manipulateInternalCounter() {
        // Content coupling: Directly accessing and modifying a private static field
        // This is a comment only as we can't actually do this without reflection
        // due to Java's access modifiers, but in languages with weaker access control,
        // this would be possible and would be an example of content coupling

        System.out.println("In a language with weaker access control, we might directly modify Deal.statusUpdateCounter");
        System.out.println("This would create a strong dependency on Deal's internal implementation");
    }

    public static void main(String[] args) {
        Deal deal = new Deal("Test Deal", new BigDecimal("1000.00"), "sales-rep-1");

        ContentCoupling contentCoupling = new ContentCoupling();
        contentCoupling.addSpecialProduct(deal);
        contentCoupling.manipulateInternalCounter();
    }
}
