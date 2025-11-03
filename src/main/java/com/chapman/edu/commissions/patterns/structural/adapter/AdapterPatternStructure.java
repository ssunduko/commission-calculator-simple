package com.chapman.edu.commissions.patterns.structural.adapter;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;

import java.math.BigDecimal;
import java.util.List;

/**
 * This class demonstrates the structure of the Adapter Pattern.
 * 
 * The Adapter Pattern is a structural design pattern that allows objects with incompatible interfaces
 * to collaborate. It acts as a bridge between two incompatible interfaces by wrapping an instance
 * of one class into an adapter class that presents the expected interface.
 * 
 * Key components of the Adapter Pattern:
 * 1. Target - The interface that the client expects to work with
 * 2. Adaptee - The existing class with incompatible interface
 * 3. Adapter - The class that implements the Target interface and translates calls to the Adaptee
 * 4. Client - The class that interacts with the Target interface
 * 
 * When to use the Adapter Pattern:
 * - When you want to use an existing class, but its interface doesn't match what you need
 * - When you want to create a reusable class that cooperates with classes that don't necessarily
 *   have compatible interfaces
 * - When you need to use several existing subclasses but it's impractical to adapt their interface
 *   by subclassing each one
 */
public class AdapterPatternStructure {

    /**
     * Target Interface
     * This is the interface that the client expects to work with.
     */
    public interface ReportData {
        String getReportTitle();
        BigDecimal getReportValue();
        String getOwnerName();
        List<String> getItemDescriptions();
        BigDecimal getTotalAmount();
    }

    /**
     * Target Interface
     * This is the interface that the third-party payment system expects.
     */
    public interface PaymentTransaction {
        String getTransactionId();
        String getCustomerId();
        double getAmount();
        String getCurrency();
        String getTransactionDate();
        List<PaymentItem> getItems();
        String getStatus();
    }

    /**
     * Another interface required by the Target
     */
    public interface PaymentItem {
        String getItemId();
        String getDescription();
        int getQuantity();
        double getUnitPrice();
        double getTotalPrice();
    }
    

}