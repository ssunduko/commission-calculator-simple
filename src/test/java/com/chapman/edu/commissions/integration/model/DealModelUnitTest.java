package com.chapman.edu.commissions.integration.model;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UNIT TESTING - Deal Model (Domain Model Layer)
 *
 * PURPOSE:
 * Unit tests for model classes verify the behavior of domain objects in isolation.
 * Model tests focus on business logic within the model, data validation,
 * and object behavior without external dependencies.
 *
 * CONCEPTS DEMONSTRATED:
 * 1. MODEL UNIT TESTING:
 *    - Test getters and setters
 *    - Test business logic methods (calculateTotalValue)
 *    - Test equals() and hashCode()
 *    - Test toString()
 *    - Test object state and behavior
 *
 * 2. DOMAIN MODEL TESTING:
 *    - Test model invariants
 *    - Test data validation (if present)
 *    - Test calculated fields
 *    - Test object composition
 *    - Test collection handling
 *
 * 3. BUSINESS LOGIC IN MODELS:
 *    - Test calculation methods
 *    - Test state transitions
 *    - Test derived values
 *    - Test business rules
 *
 * 4. DIFFERENCE FROM OTHER TESTS:
 *    - Model tests: Test object behavior only
 *    - Service tests: Test business workflows
 *    - Repository tests: Test persistence
 *
 * LAYER: Model Layer (Domain Objects)
 * TEST TYPE: Unit Test (Pure Model Testing)
 *
 * WHEN TO USE:
 * - Testing model business logic
 * - Verifying calculated fields
 * - Testing equals/hashCode contracts
 * - Validating object state management
 */
@DisplayName("Unit Tests - Deal Model (Domain Object)")
class DealModelUnitTest {

    // ============================================================
    // GETTERS AND SETTERS
    // ============================================================

    /**
     * TEST: Getters and setters work correctly
     *
     * BASIC MODEL TESTING:
     * - Set values using setters
     * - Retrieve values using getters
     * - Verify values match
     */
    @Test
    @DisplayName("Should get and set all Deal properties")
    void testGettersAndSetters() {
        // Arrange & Act
        Deal deal = new Deal();
        deal.setId("DEAL-123");
        deal.setTitle("Test Deal");
        deal.setStatus(DealStatus.OPEN);
        deal.setSalesRepId("USER-456");
        deal.setCloseDate(LocalDate.of(2025, 10, 30));
        deal.setCreatedDate(LocalDate.of(2025, 10, 1));
        deal.setLastModifiedDate(LocalDate.of(2025, 10, 30));

        // Assert
        assertEquals("DEAL-123", deal.getId());
        assertEquals("Test Deal", deal.getTitle());
        assertEquals(DealStatus.OPEN, deal.getStatus());
        assertEquals("USER-456", deal.getSalesRepId());
        assertEquals(LocalDate.of(2025, 10, 30), deal.getCloseDate());
        assertEquals(LocalDate.of(2025, 10, 1), deal.getCreatedDate());
        assertEquals(LocalDate.of(2025, 10, 30), deal.getLastModifiedDate());
    }

    /**
     * TEST: Products list handling
     *
     * MODEL COMPOSITION:
     * - Set products list
     * - Retrieve products
     * - Verify list contents
     */
    @Test
    @DisplayName("Should handle products list")
    void testProductsList() {
        // Arrange
        Deal deal = new Deal();
        List<DealProduct> products = Arrays.asList(
            new DealProduct("PROD-1", "Product 1", 2, new BigDecimal("1000.00")),
            new DealProduct("PROD-2", "Product 2", 3, new BigDecimal("500.00"))
        );

        // Act
        deal.setProducts(products);

        // Assert
        assertNotNull(deal.getProducts());
        assertEquals(2, deal.getProducts().size());
        assertEquals("PROD-1", deal.getProducts().get(0).getProductId());
        assertEquals("Product 2", deal.getProducts().get(1).getProductName());
    }

    // ============================================================
    // BUSINESS LOGIC: CALCULATE TOTAL VALUE
    // ============================================================

    /**
     * TEST: Calculate total value from products
     *
     * BUSINESS LOGIC TESTING:
     * - Deal.calculateTotalValue() sums all products
     * - Formula: sum(quantity * price) for each product
     * - Verifies correct calculation
     */
    @Test
    @DisplayName("Should calculate total value from all products")
    void testCalculateTotalValue() {
        // Arrange
        Deal deal = new Deal();
        deal.setProducts(Arrays.asList(
            new DealProduct("PROD-A", "Product A", 2, new BigDecimal("1000.00")), // 2 * 1000 = 2000
            new DealProduct("PROD-B", "Product B", 3, new BigDecimal("500.00")),   // 3 * 500 = 1500
            new DealProduct("PROD-C", "Product C", 1, new BigDecimal("2500.00"))   // 1 * 2500 = 2500
        ));

        // Act
        BigDecimal totalValue = deal.calculateTotalValue();

        // Assert: 2000 + 1500 + 2500 = 6000
        assertEquals(new BigDecimal("6000.00"), totalValue);
    }

    /**
     * TEST: Total value with single product
     */
    @Test
    @DisplayName("Should calculate total value for single product")
    void testCalculateTotalValueSingleProduct() {
        // Arrange
        Deal deal = new Deal();
        deal.setProducts(Arrays.asList(
            new DealProduct("PROD-X", "Product X", 5, new BigDecimal("1200.50")) // 5 * 1200.50 = 6002.50
        ));

        // Act
        BigDecimal totalValue = deal.calculateTotalValue();

        // Assert
        assertEquals(new BigDecimal("6002.50"), totalValue);
    }

    /**
     * TEST: Total value with no products
     *
     * EDGE CASE:
     * - Empty products list should return 0
     */
    @Test
    @DisplayName("Should return zero for empty products list")
    void testCalculateTotalValueEmptyProducts() {
        // Arrange
        Deal deal = new Deal();
        deal.setProducts(new ArrayList<>());

        // Act
        BigDecimal totalValue = deal.calculateTotalValue();

        // Assert
        assertEquals(BigDecimal.ZERO, totalValue);
    }

    /**
     * TEST: Total value with null products
     *
     * EDGE CASE:
     * - Null products list should return 0
     */
    @Test
    @DisplayName("Should return zero for null products list")
    void testCalculateTotalValueNullProducts() {
        // Arrange
        Deal deal = new Deal();
        deal.setProducts(null);

        // Act
        BigDecimal totalValue = deal.calculateTotalValue();

        // Assert
        assertEquals(BigDecimal.ZERO, totalValue);
    }

    // ============================================================
    // EQUALS AND HASHCODE
    // ============================================================

    /**
     * TEST: Equals method - same ID
     *
     * EQUALS CONTRACT:
     * - Deals with same ID should be equal
     * - Verifies equals() implementation
     */
    @Test
    @DisplayName("Should be equal when IDs match")
    void testEqualsSameId() {
        // Arrange
        Deal deal1 = new Deal();
        deal1.setId("DEAL-same");
        deal1.setTitle("Deal 1");

        Deal deal2 = new Deal();
        deal2.setId("DEAL-same");
        deal2.setTitle("Deal 2"); // Different title but same ID

        // Act & Assert
        assertEquals(deal1, deal2);
        assertEquals(deal1.hashCode(), deal2.hashCode());
    }

    /**
     * TEST: Equals method - different IDs
     */
    @Test
    @DisplayName("Should not be equal when IDs differ")
    void testEqualsDifferentIds() {
        // Arrange
        Deal deal1 = new Deal();
        deal1.setId("DEAL-1");
        deal1.setTitle("Same Title");

        Deal deal2 = new Deal();
        deal2.setId("DEAL-2");
        deal2.setTitle("Same Title");

        // Act & Assert
        assertNotEquals(deal1, deal2);
    }

    /**
     * TEST: Equals with null
     *
     * EQUALS CONTRACT:
     * - Object should not equal null
     */
    @Test
    @DisplayName("Should not equal null")
    void testEqualsNull() {
        // Arrange
        Deal deal = new Deal();
        deal.setId("DEAL-123");

        // Act & Assert
        assertNotEquals(deal, null);
    }

    /**
     * TEST: Equals with same reference
     *
     * EQUALS CONTRACT:
     * - Object should equal itself (reflexive)
     */
    @Test
    @DisplayName("Should equal itself")
    void testEqualsSameReference() {
        // Arrange
        Deal deal = new Deal();
        deal.setId("DEAL-123");

        // Act & Assert
        assertEquals(deal, deal);
    }

    /**
     * TEST: Equals with different class
     */
    @Test
    @DisplayName("Should not equal object of different class")
    void testEqualsDifferentClass() {
        // Arrange
        Deal deal = new Deal();
        deal.setId("DEAL-123");

        String notADeal = "DEAL-123";

        // Act & Assert
        assertNotEquals(deal, notADeal);
    }

    // ============================================================
    // TOSTRING
    // ============================================================

    /**
     * TEST: toString includes key fields
     *
     * TOSTRING TESTING:
     * - Verify toString contains important fields
     * - Useful for debugging and logging
     */
    @Test
    @DisplayName("Should include key fields in toString")
    void testToString() {
        // Arrange
        Deal deal = new Deal();
        deal.setId("DEAL-toString");
        deal.setTitle("ToString Test Deal");
        deal.setStatus(DealStatus.WON);

        // Act
        String result = deal.toString();

        // Assert: toString should contain key identifying information
        assertNotNull(result);
        assertTrue(result.contains("DEAL-toString") || result.contains("ToString Test Deal"),
            "toString should contain ID or title");
    }

    // ============================================================
    // STATE AND LIFECYCLE
    // ============================================================

    /**
     * TEST: Deal status lifecycle
     *
     * BUSINESS STATE:
     * - Verify status can be changed
     * - Test different status values
     */
    @Test
    @DisplayName("Should support status lifecycle transitions")
    void testStatusLifecycle() {
        // Arrange
        Deal deal = new Deal();

        // Act & Assert: OPEN
        deal.setStatus(DealStatus.OPEN);
        assertEquals(DealStatus.OPEN, deal.getStatus());

        // Act & Assert: WON
        deal.setStatus(DealStatus.WON);
        assertEquals(DealStatus.WON, deal.getStatus());

        // Act & Assert: LOST
        deal.setStatus(DealStatus.LOST);
        assertEquals(DealStatus.LOST, deal.getStatus());
    }

    /**
     * TEST: Close date set when status is WON
     *
     * BUSINESS RULE:
     * - WON deals should have close date
     * - OPEN deals may not have close date
     */
    @Test
    @DisplayName("Should allow close date for WON deals")
    void testCloseDateForWonDeal() {
        // Arrange
        Deal deal = new Deal();
        deal.setStatus(DealStatus.WON);
        LocalDate closeDate = LocalDate.now();

        // Act
        deal.setCloseDate(closeDate);

        // Assert
        assertEquals(DealStatus.WON, deal.getStatus());
        assertEquals(closeDate, deal.getCloseDate());
    }

    /**
     * TEST: Null close date for OPEN deal
     */
    @Test
    @DisplayName("Should allow null close date for OPEN deals")
    void testNullCloseDateForOpenDeal() {
        // Arrange
        Deal deal = new Deal();
        deal.setStatus(DealStatus.OPEN);
        deal.setCloseDate(null);

        // Assert
        assertEquals(DealStatus.OPEN, deal.getStatus());
        assertNull(deal.getCloseDate());
    }

    // ============================================================
    // EDGE CASES AND VALIDATION
    // ============================================================

    /**
     * TEST: Deal with large product quantities
     *
     * EDGE CASE:
     * - Verify model handles large quantities correctly
     */
    @Test
    @DisplayName("Should handle large product quantities")
    void testLargeQuantities() {
        // Arrange
        Deal deal = new Deal();
        deal.setProducts(Arrays.asList(
            new DealProduct("PROD-BULK", "Bulk Product", 1000000, new BigDecimal("0.01"))
        ));

        // Act
        BigDecimal totalValue = deal.calculateTotalValue();

        // Assert: 1,000,000 * 0.01 = 10,000
        assertEquals(new BigDecimal("10000.00"), totalValue);
    }

    /**
     * TEST: Deal with decimal prices
     */
    @Test
    @DisplayName("Should handle decimal prices correctly")
    void testDecimalPrices() {
        // Arrange
        Deal deal = new Deal();
        deal.setProducts(Arrays.asList(
            new DealProduct("PROD-DEC", "Decimal Product", 3, new BigDecimal("99.99"))
        ));

        // Act
        BigDecimal totalValue = deal.calculateTotalValue();

        // Assert: 3 * 99.99 = 299.97
        assertEquals(new BigDecimal("299.97"), totalValue);
    }

    /**
     * KEY TAKEAWAYS - MODEL UNIT TESTING:
     *
     * WHAT WE TESTED:
     * ✓ Getters and setters
     * ✓ Business logic (calculateTotalValue)
     * ✓ Equals and hashCode contract
     * ✓ ToString method
     * ✓ State management (status, close date)
     * ✓ Collection handling (products list)
     * ✓ Edge cases (null, empty, large values)
     *
     * MODEL TESTING PRINCIPLES:
     * - Test object behavior, not just accessors
     * - Verify business logic within model
     * - Test equals/hashCode contracts
     * - Test edge cases and boundaries
     * - No external dependencies
     *
     * BUSINESS LOGIC IN MODELS:
     * - calculateTotalValue() demonstrates domain logic
     * - Models can contain calculations
     * - Models enforce business rules
     * - Models maintain state consistency
     *
     * BEST PRACTICES:
     * - Keep models focused on data + behavior
     * - Test calculations thoroughly
     * - Test equals/hashCode together
     * - Test with null and empty values
     * - Verify toString for debugging
     */
}