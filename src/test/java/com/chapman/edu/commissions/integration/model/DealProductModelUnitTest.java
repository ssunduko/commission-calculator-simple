package com.chapman.edu.commissions.integration.model;

import com.chapman.edu.commissions.model.DealProduct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UNIT TESTING - DealProduct Model
 *
 * PURPOSE:
 * Test the DealProduct domain model in isolation, verifying getters, setters,
 * calculated fields, and business logic.
 */
@DisplayName("Unit Tests - DealProduct Model")
class DealProductModelUnitTest {

    @Test
    @DisplayName("Should create DealProduct with all fields")
    void testConstructorAndGetters() {
        // Arrange & Act
        DealProduct product = new DealProduct(
            "PROD-123",
            "Test Product",
            5,
            new BigDecimal("1000.00")
        );

        // Assert
        assertEquals("PROD-123", product.getProductId());
        assertEquals("Test Product", product.getProductName());
        assertEquals(5, product.getQuantity());
        assertEquals(new BigDecimal("1000.00"), product.getPrice());
    }

    @Test
    @DisplayName("Should calculate total price correctly")
    void testCalculateTotalPrice() {
        // Arrange
        DealProduct product = new DealProduct(
            "PROD-LINE",
            "Line Total Test",
            3,
            new BigDecimal("250.50")
        );

        // Act
        BigDecimal totalPrice = product.calculateTotalPrice();

        // Assert: 3 * 250.50 = 751.50
        assertEquals(new BigDecimal("751.50"), totalPrice);
    }

    @Test
    @DisplayName("Should handle zero quantity")
    void testZeroQuantity() {
        // Arrange
        DealProduct product = new DealProduct(
            "PROD-ZERO",
            "Zero Quantity",
            0,
            new BigDecimal("1000.00")
        );

        // Act
        BigDecimal totalPrice = product.calculateTotalPrice();

        // Assert
        assertEquals(0, totalPrice.compareTo(BigDecimal.ZERO), "Total price should be zero");
    }

    @Test
    @DisplayName("Should handle zero price")
    void testZeroPrice() {
        // Arrange
        DealProduct product = new DealProduct(
            "PROD-FREE",
            "Free Product",
            10,
            BigDecimal.ZERO
        );

        // Act
        BigDecimal totalPrice = product.calculateTotalPrice();

        // Assert
        assertEquals(0, totalPrice.compareTo(BigDecimal.ZERO), "Total price should be zero");
    }

    @Test
    @DisplayName("Should use setters to update values")
    void testSetters() {
        // Arrange
        DealProduct product = new DealProduct(
            "PROD-1",
            "Original",
            1,
            new BigDecimal("100")
        );

        // Act
        product.setProductId("PROD-2");
        product.setProductName("Updated");
        product.setQuantity(5);
        product.setPrice(new BigDecimal("200"));

        // Assert
        assertEquals("PROD-2", product.getProductId());
        assertEquals("Updated", product.getProductName());
        assertEquals(5, product.getQuantity());
        assertEquals(new BigDecimal("200"), product.getPrice());
        assertEquals(new BigDecimal("1000"), product.calculateTotalPrice());
    }

    @Test
    @DisplayName("Should handle large quantities and prices")
    void testLargeValues() {
        // Arrange
        DealProduct product = new DealProduct(
            "PROD-LARGE",
            "Large Product",
            1000000,
            new BigDecimal("9999.99")
        );

        // Act
        BigDecimal totalPrice = product.calculateTotalPrice();

        // Assert: 1,000,000 * 9999.99
        assertEquals(new BigDecimal("9999990000.00"), totalPrice);
    }

    @Test
    @DisplayName("Should handle decimal precision")
    void testDecimalPrecision() {
        // Arrange
        DealProduct product = new DealProduct(
            "PROD-DEC",
            "Decimal Test",
            7,
            new BigDecimal("12.345")
        );

        // Act
        BigDecimal totalPrice = product.calculateTotalPrice();

        // Assert: 7 * 12.345 = 86.415
        assertEquals(new BigDecimal("86.415"), totalPrice);
    }
}