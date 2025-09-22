package com.chapman.edu.commissions.fundamentals.ratio;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Deal with a 1:1 code-to-test ratio.
 * 
 * In a 1:1 ratio, the amount of test code is approximately equal to the amount of production code.
 * This test class focuses on basic functionality testing with minimal setup and assertions.
 * 
 * This test class does NOT include:
 * - Extensive test methods covering all aspects of the class
 * - Multiple assertions per test
 * - Thorough edge case testing
 * - Boundary value testing
 * - Negative testing (testing for expected failures)
 * - Complex test scenarios
 * - Nested test classes for better organization
 */
public class DealTest1to1Ratio {

    private Deal deal;

    @BeforeEach
    void setUp() {
        // Create a new Deal instance before each test
        deal = new Deal("Test Deal", new BigDecimal("1000.00"), "REP001");
    }

    /**
     * Test the constructor and basic getters
     */
    @Test
    void testConstructorAndGetters() {
        assertEquals("Test Deal", deal.getTitle());
        assertEquals(new BigDecimal("1000.00"), deal.getValue());
        assertEquals("REP001", deal.getSalesRepId());
        assertEquals(DealStatus.OPEN, deal.getStatus());
    }

    /**
     * Test setting and getting the deal ID
     */
    @Test
    void testSetAndGetId() {
        deal.setId("DEAL001");
        assertEquals("DEAL001", deal.getId());
    }

    /**
     * Test adding a product to the deal
     */
    @Test
    void testAddProduct() {
        DealProduct product = new DealProduct("PROD001", "Test Product", 2, new BigDecimal("100.00"));
        deal.addProduct(product);

        assertTrue(deal.getProducts().contains(product));
        assertEquals(1, deal.getProducts().size());
    }

    /**
     * Test calculating the total value of the deal
     */
    @Test
    void testCalculateTotalValue() {
        DealProduct product1 = new DealProduct("PROD001", "Test Product 1", 2, new BigDecimal("100.00"));
        DealProduct product2 = new DealProduct("PROD002", "Test Product 2", 1, new BigDecimal("50.00"));

        deal.addProduct(product1);
        deal.addProduct(product2);

        // Expected: (2 * 100.00) + (1 * 50.00) = 250.00
        assertEquals(new BigDecimal("250.00"), deal.calculateTotalValue());
    }

    /**
     * Test setting and getting the deal status
     */
    @Test
    void testSetAndGetStatus() {
        deal.setStatus(DealStatus.WON);
        assertEquals(DealStatus.WON, deal.getStatus());
    }
}
