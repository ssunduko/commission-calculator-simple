package com.chapman.edu.commissions.patterns.fixture;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class demonstrating the use of DealFixture for creating consistent deal test data.
 * 
 * Deals are central to commission calculations, so having reliable and consistent
 * test data is crucial. This class shows how the Fixture pattern helps create
 * various deal scenarios without repetitive setup code.
 * 
 * The fixture pattern benefits shown here:
 * - Simplified creation of complex deal objects with products
 * - Consistent deal configurations across different test scenarios
 * - Easy testing of different deal statuses and their impact
 * - Reduced maintenance when Deal model changes
 */
class DealFixtureTest {

    /**
     * Test that demonstrates creating a basic open deal using fixtures.
     * Open deals represent ongoing sales opportunities that haven't closed yet.
     */
    @Test
    void testCreateOpenDeal() {
        // Arrange: Use fixture to create an open deal
        Deal openDeal = DealFixture.createOpenDeal();
        
        // Act & Assert: Verify open deal properties
        assertNotNull(openDeal, "Open deal should not be null");
        assertEquals("deal-001", openDeal.getId(), "Should have correct deal ID");
        assertEquals(DealStatus.OPEN, openDeal.getStatus(), "Deal should be in OPEN status");
        assertEquals(new BigDecimal("10000.00"), openDeal.getValue(), "Should have correct value");
        assertEquals("user-001", openDeal.getSalesRepId(), "Should have correct sales rep");
        assertNull(openDeal.getCloseDate(), "Open deal should not have a close date");
        assertNotNull(openDeal.getCreatedDate(), "Should have a creation date");
    }

    /**
     * Test that demonstrates creating a won deal using fixtures.
     * Won deals are the primary source of commission calculations.
     */
    @Test
    void testCreateWonDeal() {
        // Arrange: Use fixture to create a won deal
        Deal wonDeal = DealFixture.createWonDeal();
        
        // Act & Assert: Verify won deal properties
        assertNotNull(wonDeal, "Won deal should not be null");
        assertEquals("deal-002", wonDeal.getId(), "Should have correct deal ID");
        assertEquals(DealStatus.WON, wonDeal.getStatus(), "Deal should be in WON status");
        assertEquals(new BigDecimal("50000.00"), wonDeal.getValue(), "Should have correct value");
        assertNotNull(wonDeal.getCloseDate(), "Won deal should have a close date");
        assertTrue(wonDeal.getCloseDate().isBefore(LocalDate.now().plusDays(1)), 
                  "Close date should be in the past or today");
    }

    /**
     * Test that demonstrates creating a lost deal using fixtures.
     * Lost deals should not generate commissions but may affect quotas and metrics.
     */
    @Test
    void testCreateLostDeal() {
        // Arrange: Use fixture to create a lost deal
        Deal lostDeal = DealFixture.createLostDeal();
        
        // Act & Assert: Verify lost deal properties
        assertNotNull(lostDeal, "Lost deal should not be null");
        assertEquals("deal-003", lostDeal.getId(), "Should have correct deal ID");
        assertEquals(DealStatus.LOST, lostDeal.getStatus(), "Deal should be in LOST status");
        assertEquals(new BigDecimal("25000.00"), lostDeal.getValue(), "Should have correct value");
        assertNull(lostDeal.getCloseDate(), "Lost deal typically doesn't have a close date");
    }

    /**
     * Test that demonstrates creating a high-value deal using fixtures.
     * High-value deals often trigger different commission rates or bonus structures.
     */
    @Test
    void testCreateHighValueDeal() {
        // Arrange: Use fixture to create a high-value deal
        Deal highValueDeal = DealFixture.createHighValueDeal();
        
        // Act & Assert: Verify high-value deal properties
        assertNotNull(highValueDeal, "High-value deal should not be null");
        assertEquals(new BigDecimal("100000.00"), highValueDeal.getValue(), "Should have high value");
        assertEquals(DealStatus.WON, highValueDeal.getStatus(), "Should be won to trigger commissions");
        assertTrue(highValueDeal.getValue().compareTo(new BigDecimal("50000.00")) > 0, 
                  "Should be considered a high-value deal");
    }

    /**
     * Test that demonstrates creating a multi-product deal using fixtures.
     * Multi-product deals test complex commission scenarios where different
     * products may have different commission rates.
     */
    @Test
    void testCreateMultiProductDeal() {
        // Arrange: Use fixture to create a deal with multiple products
        Deal multiProductDeal = DealFixture.createMultiProductDeal();
        
        // Act & Assert: Verify multi-product deal structure
        assertNotNull(multiProductDeal, "Multi-product deal should not be null");
        assertEquals("deal-005", multiProductDeal.getId(), "Should have correct deal ID");
        assertEquals(3, multiProductDeal.getProducts().size(), "Should have 3 products");
        
        // Verify individual products are properly configured
        DealProduct softwareProduct = multiProductDeal.getProducts().get(0);
        assertEquals("Software License", softwareProduct.getProductName(), "First product should be software");
        assertEquals(5, softwareProduct.getQuantity(), "Software should have quantity 5");
        
        DealProduct supportProduct = multiProductDeal.getProducts().get(1);
        assertEquals("Support Contract", supportProduct.getProductName(), "Second product should be support");
        assertEquals(1, supportProduct.getQuantity(), "Support should have quantity 1");
        
        DealProduct trainingProduct = multiProductDeal.getProducts().get(2);
        assertEquals("Training Services", trainingProduct.getProductName(), "Third product should be training");
        assertEquals(3, trainingProduct.getQuantity(), "Training should have quantity 3");
    }

    /**
     * Test that demonstrates creating a deal for a specific sales representative.
     * This is useful for testing user-specific commission calculations and quotas.
     */
    @Test
    void testCreateDealForSalesRep() {
        // Arrange: Use fixture to create a deal for a specific sales rep
        String customSalesRepId = "user-999";
        Deal customDeal = DealFixture.createDealForSalesRep(customSalesRepId);
        
        // Act & Assert: Verify sales rep assignment
        assertNotNull(customDeal, "Custom deal should not be null");
        assertEquals(customSalesRepId, customDeal.getSalesRepId(), "Should have correct sales rep ID");
        assertEquals(DealStatus.WON, customDeal.getStatus(), "Should be won for commission calculation");
    }

    /**
     * Test that demonstrates creating a deal with a specific close date.
     * This is important for testing time-based commission rules and reporting periods.
     */
    @Test
    void testCreateDealWithCloseDate() {
        // Arrange: Use fixture to create a deal with a specific close date
        LocalDate customCloseDate = LocalDate.of(2023, 12, 15);
        Deal datedDeal = DealFixture.createDealWithCloseDate(customCloseDate);
        
        // Act & Assert: Verify close date assignment
        assertNotNull(datedDeal, "Dated deal should not be null");
        assertEquals(customCloseDate, datedDeal.getCloseDate(), "Should have correct close date");
        assertEquals(DealStatus.WON, datedDeal.getStatus(), "Should be won");
        assertTrue(datedDeal.getCreatedDate().isBefore(customCloseDate), 
                  "Creation date should be before close date");
    }

    /**
     * Test that demonstrates creating a cancelled deal using fixtures.
     * Cancelled deals represent edge cases that need special handling in commission processing.
     */
    @Test
    void testCreateCancelledDeal() {
        // Arrange: Use fixture to create a cancelled deal
        Deal cancelledDeal = DealFixture.createCancelledDeal();
        
        // Act & Assert: Verify cancelled deal properties
        assertNotNull(cancelledDeal, "Cancelled deal should not be null");
        assertEquals(DealStatus.CANCELLED, cancelledDeal.getStatus(), "Should be cancelled");
        assertNull(cancelledDeal.getCloseDate(), "Cancelled deal should not have close date");
        assertEquals(new BigDecimal("40000.00"), cancelledDeal.getValue(), "Should have correct value");
    }

    /**
     * Test that demonstrates creating a small deal using fixtures.
     * Small deals may not meet minimum thresholds for commission eligibility.
     */
    @Test
    void testCreateSmallDeal() {
        // Arrange: Use fixture to create a small deal
        Deal smallDeal = DealFixture.createSmallDeal();
        
        // Act & Assert: Verify small deal properties
        assertNotNull(smallDeal, "Small deal should not be null");
        assertEquals(new BigDecimal("1000.00"), smallDeal.getValue(), "Should have small value");
        assertTrue(smallDeal.getValue().compareTo(new BigDecimal("5000.00")) < 0, 
                  "Should be considered a small deal");
        assertEquals(DealStatus.WON, smallDeal.getStatus(), "Should be won");
    }

    /**
     * Test that demonstrates creating a discounted deal using fixtures.
     * Discounted deals test how discounts affect commission calculations.
     */
    @Test
    void testCreateDiscountedDeal() {
        // Arrange: Use fixture to create a deal with discounted products
        Deal discountedDeal = DealFixture.createDiscountedDeal();
        
        // Act & Assert: Verify discounted deal structure
        assertNotNull(discountedDeal, "Discounted deal should not be null");
        assertEquals(1, discountedDeal.getProducts().size(), "Should have 1 product");
        
        DealProduct discountedProduct = discountedDeal.getProducts().get(0);
        assertEquals(new BigDecimal("5000.00"), discountedProduct.getDiscount(), 
                    "Product should have correct discount amount");
        assertTrue(discountedProduct.getDiscount().compareTo(BigDecimal.ZERO) > 0, 
                  "Product should have a discount applied");
        
        // Verify discount affects total price calculation
        BigDecimal expectedTotal = discountedProduct.getPrice()
                .multiply(new BigDecimal(discountedProduct.getQuantity()))
                .subtract(discountedProduct.getDiscount());
        assertEquals(expectedTotal, discountedProduct.calculateTotalPrice(), 
                    "Total price should account for discount");
    }

    /**
     * Test that demonstrates the consistency of fixture-created deals.
     * Multiple calls to the same fixture method should create similar deals
     * with consistent base configuration.
     */
    @Test
    void testFixtureConsistency() {
        // Arrange: Create multiple open deals using the same fixture method
        Deal openDeal1 = DealFixture.createOpenDeal();
        Deal openDeal2 = DealFixture.createOpenDeal();
        
        // Act & Assert: Verify consistency while allowing for different instances
        assertNotSame(openDeal1, openDeal2, "Should be different object instances");
        assertEquals(openDeal1.getStatus(), openDeal2.getStatus(), "Should have same status");
        assertEquals(openDeal1.getValue(), openDeal2.getValue(), "Should have same value");
        assertEquals(openDeal1.getSalesRepId(), openDeal2.getSalesRepId(), "Should have same sales rep");
        assertEquals(openDeal1.getTitle(), openDeal2.getTitle(), "Should have same title");
    }

    /**
     * Test that demonstrates using fixtures for deal status-based testing.
     * This shows how fixtures make it easy to test different deal statuses
     * and their impact on business logic.
     */
    @Test
    void testDealStatusScenarios() {
        // Arrange: Create deals with different statuses using fixtures
        Deal openDeal = DealFixture.createOpenDeal();
        Deal wonDeal = DealFixture.createWonDeal();
        Deal lostDeal = DealFixture.createLostDeal();
        Deal cancelledDeal = DealFixture.createCancelledDeal();
        
        // Act & Assert: Verify status-specific behavior
        assertEquals(DealStatus.OPEN, openDeal.getStatus(), "Open deal should have OPEN status");
        assertEquals(DealStatus.WON, wonDeal.getStatus(), "Won deal should have WON status");
        assertEquals(DealStatus.LOST, lostDeal.getStatus(), "Lost deal should have LOST status");
        assertEquals(DealStatus.CANCELLED, cancelledDeal.getStatus(), "Cancelled deal should have CANCELLED status");
        
        // Only won deals should have close dates in our fixture setup
        assertNull(openDeal.getCloseDate(), "Open deal should not have close date");
        assertNotNull(wonDeal.getCloseDate(), "Won deal should have close date");
        assertNull(lostDeal.getCloseDate(), "Lost deal should not have close date");
        assertNull(cancelledDeal.getCloseDate(), "Cancelled deal should not have close date");
    }
}