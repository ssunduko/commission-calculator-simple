package com.chapman.edu.commissions.fundamentals.aaa;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DealTest {
    @Test
    void shouldCalculateTotalValueFromProducts() {
        // Arrange
        Deal deal = new Deal("Test Deal", BigDecimal.ZERO, "sales-rep-1");
        deal.addProduct(new DealProduct("prod1", "Product 1", 2,
                new BigDecimal("100.00")));
        deal.addProduct(new DealProduct("prod2", "Product 2", 1,
                new BigDecimal("50.00")));
        // Act
        BigDecimal totalValue = deal.calculateTotalValue();

        // Assert
        assertEquals(new BigDecimal("250.00"), totalValue);
    }
}