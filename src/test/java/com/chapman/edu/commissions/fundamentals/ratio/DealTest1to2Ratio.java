package com.chapman.edu.commissions.fundamentals.ratio;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Deal with a 1:2 code-to-test ratio.
 * 
 * In a 1:2 ratio, the amount of test code is approximately twice the amount of production code.
 * This test class provides more comprehensive testing with:
 * - Extensive test methods covering all aspects of the class
 * - Multiple assertions per test
 * - Thorough edge case testing
 * - Boundary value testing
 * - Negative testing (testing for expected failures)
 * - Complex test scenarios
 * - Nested test classes for better organization
 */
public class DealTest1to2Ratio {

    private Deal deal;
    private static final String DEFAULT_TITLE = "Test Deal";
    private static final BigDecimal DEFAULT_VALUE = new BigDecimal("1000.00");
    private static final String DEFAULT_SALES_REP_ID = "REP001";

    @BeforeEach
    void setUp() {
        // Create a new Deal instance before each test
        deal = new Deal(DEFAULT_TITLE, DEFAULT_VALUE, DEFAULT_SALES_REP_ID);
    }

    @Nested
    @DisplayName("Constructor and Basic Properties Tests")
    class ConstructorAndBasicPropertiesTests {

        @Test
        @DisplayName("Constructor should set initial values correctly")
        void testConstructor() {
            // Test the constructor sets the expected values
            assertEquals(DEFAULT_TITLE, deal.getTitle());
            assertEquals(DEFAULT_VALUE, deal.getValue());
            assertEquals(DEFAULT_SALES_REP_ID, deal.getSalesRepId());
            assertEquals(DealStatus.OPEN, deal.getStatus());

            // Test that products list is initialized
            assertNotNull(deal.getProducts());
            assertTrue(deal.getProducts().isEmpty());

            // Test that dates are initialized
            assertNotNull(deal.getCreatedDate());
            assertNotNull(deal.getLastModifiedDate());
            assertEquals(deal.getCreatedDate(), deal.getLastModifiedDate());

            // Test that other fields are null
            assertNull(deal.getId());
            assertNull(deal.getCloseDate());
        }

        @Test
        @DisplayName("Default constructor should initialize minimal properties")
        void testDefaultConstructor() {
            Deal emptyDeal = new Deal();

            // Test that products list is initialized
            assertNotNull(emptyDeal.getProducts());
            assertTrue(emptyDeal.getProducts().isEmpty());

            // Test that dates are initialized
            assertNotNull(emptyDeal.getCreatedDate());
            assertNotNull(emptyDeal.getLastModifiedDate());

            // Test that other fields are null
            assertNull(emptyDeal.getId());
            assertNull(emptyDeal.getTitle());
            assertNull(emptyDeal.getValue());
            assertNull(emptyDeal.getSalesRepId());
            assertNull(emptyDeal.getStatus());
            assertNull(emptyDeal.getCloseDate());
        }
    }

    @Nested
    @DisplayName("ID Property Tests")
    class IdPropertyTests {

        @Test
        @DisplayName("Setting ID should update the property")
        void testSetAndGetId() {
            assertNull(deal.getId());

            deal.setId("DEAL001");
            assertEquals("DEAL001", deal.getId());

            // Test with empty string
            deal.setId("");
            assertEquals("", deal.getId());

            // Test with null
            deal.setId(null);
            assertNull(deal.getId());
        }
    }

    @Nested
    @DisplayName("Title Property Tests")
    class TitlePropertyTests {

        @Test
        @DisplayName("Setting title should update the property")
        void testSetAndGetTitle() {
            assertEquals(DEFAULT_TITLE, deal.getTitle());

            deal.setTitle("New Title");
            assertEquals("New Title", deal.getTitle());

            // Test with empty string
            deal.setTitle("");
            assertEquals("", deal.getTitle());

            // Test with null
            deal.setTitle(null);
            assertNull(deal.getTitle());
        }
    }

    @Nested
    @DisplayName("Value Property Tests")
    class ValuePropertyTests {

        @Test
        @DisplayName("Setting value should update the property")
        void testSetAndGetValue() {
            assertEquals(DEFAULT_VALUE, deal.getValue());

            BigDecimal newValue = new BigDecimal("2000.00");
            deal.setValue(newValue);
            assertEquals(newValue, deal.getValue());

            // Test with zero
            deal.setValue(BigDecimal.ZERO);
            assertEquals(BigDecimal.ZERO, deal.getValue());

            // Test with negative value
            BigDecimal negativeValue = new BigDecimal("-500.00");
            deal.setValue(negativeValue);
            assertEquals(negativeValue, deal.getValue());

            // Test with null
            deal.setValue(null);
            assertNull(deal.getValue());
        }
    }

    @Nested
    @DisplayName("Status Property Tests")
    class StatusPropertyTests {

        @Test
        @DisplayName("Setting status should update the property and lastModifiedDate")
        void testSetAndGetStatus() {
            assertEquals(DealStatus.OPEN, deal.getStatus());
            LocalDate initialModifiedDate = deal.getLastModifiedDate();

            // Wait a moment to ensure the timestamp would be different
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            deal.setStatus(DealStatus.WON);
            assertEquals(DealStatus.WON, deal.getStatus());

            // Verify lastModifiedDate was updated
            assertNotEquals(initialModifiedDate, deal.getLastModifiedDate());
        }

        @Test
        @DisplayName("Should handle OPEN status value")
        void testOpenStatusValue() {
            deal.setStatus(DealStatus.OPEN);
            assertEquals(DealStatus.OPEN, deal.getStatus());
        }

        @Test
        @DisplayName("Should handle WON status value")
        void testWonStatusValue() {
            deal.setStatus(DealStatus.WON);
            assertEquals(DealStatus.WON, deal.getStatus());
        }

        @Test
        @DisplayName("Should handle LOST status value")
        void testLostStatusValue() {
            deal.setStatus(DealStatus.LOST);
            assertEquals(DealStatus.LOST, deal.getStatus());
        }

        @Test
        @DisplayName("Should handle CANCELLED status value")
        void testCancelledStatusValue() {
            deal.setStatus(DealStatus.CANCELLED);
            assertEquals(DealStatus.CANCELLED, deal.getStatus());
        }
    }

    @Nested
    @DisplayName("Products Management Tests")
    class ProductsManagementTests {

        @Test
        @DisplayName("Adding a product should update the products list")
        void testAddProduct() {
            assertTrue(deal.getProducts().isEmpty());

            DealProduct product = new DealProduct("PROD001", "Test Product", 2, new BigDecimal("100.00"));
            deal.addProduct(product);

            assertFalse(deal.getProducts().isEmpty());
            assertEquals(1, deal.getProducts().size());
            assertTrue(deal.getProducts().contains(product));
        }

        @Test
        @DisplayName("Adding multiple products should update the products list correctly")
        void testAddMultipleProducts() {
            DealProduct product1 = new DealProduct("PROD001", "Test Product 1", 2, new BigDecimal("100.00"));
            DealProduct product2 = new DealProduct("PROD002", "Test Product 2", 1, new BigDecimal("50.00"));
            DealProduct product3 = new DealProduct("PROD003", "Test Product 3", 3, new BigDecimal("75.00"));

            deal.addProduct(product1);
            deal.addProduct(product2);
            deal.addProduct(product3);

            assertEquals(3, deal.getProducts().size());
            assertTrue(deal.getProducts().contains(product1));
            assertTrue(deal.getProducts().contains(product2));
            assertTrue(deal.getProducts().contains(product3));
        }

        @Test
        @DisplayName("Setting products list should replace existing products")
        void testSetProducts() {
            // Add a product first
            DealProduct initialProduct = new DealProduct("PROD001", "Initial Product", 1, new BigDecimal("100.00"));
            deal.addProduct(initialProduct);

            // Create a new list of products
            List<DealProduct> newProducts = new ArrayList<>();
            DealProduct product1 = new DealProduct("PROD002", "New Product 1", 2, new BigDecimal("200.00"));
            DealProduct product2 = new DealProduct("PROD003", "New Product 2", 3, new BigDecimal("300.00"));
            newProducts.add(product1);
            newProducts.add(product2);

            // Set the new products list
            deal.setProducts(newProducts);

            // Verify the products list was replaced
            assertEquals(2, deal.getProducts().size());
            assertFalse(deal.getProducts().contains(initialProduct));
            assertTrue(deal.getProducts().contains(product1));
            assertTrue(deal.getProducts().contains(product2));
        }

        @Test
        @DisplayName("Setting products to null should be handled")
        void testSetProductsNull() {
            // Add a product first
            DealProduct product = new DealProduct("PROD001", "Test Product", 1, new BigDecimal("100.00"));
            deal.addProduct(product);

            // Set products to null
            deal.setProducts(null);

            // Verify products is null
            assertNull(deal.getProducts());

            // Reset products to non-null for other tests
            deal.setProducts(new ArrayList<>());
        }
    }

    @Nested
    @DisplayName("Total Value Calculation Tests")
    class TotalValueCalculationTests {

        @Test
        @DisplayName("Calculate total value with no products should return zero")
        void testCalculateTotalValueNoProducts() {
            assertEquals(BigDecimal.ZERO, deal.calculateTotalValue());
        }

        @Test
        @DisplayName("Calculate total value with one product")
        void testCalculateTotalValueOneProduct() {
            DealProduct product = new DealProduct("PROD001", "Test Product", 2, new BigDecimal("100.00"));
            deal.addProduct(product);

            // Expected: 2 * 100.00 = 200.00
            assertEquals(new BigDecimal("200.00"), deal.calculateTotalValue());
        }

        @Test
        @DisplayName("Calculate total value with multiple products")
        void testCalculateTotalValueMultipleProducts() {
            DealProduct product1 = new DealProduct("PROD001", "Test Product 1", 2, new BigDecimal("100.00"));
            DealProduct product2 = new DealProduct("PROD002", "Test Product 2", 1, new BigDecimal("50.00"));
            DealProduct product3 = new DealProduct("PROD003", "Test Product 3", 3, new BigDecimal("75.00"));

            deal.addProduct(product1);
            deal.addProduct(product2);
            deal.addProduct(product3);

            // Expected: (2 * 100.00) + (1 * 50.00) + (3 * 75.00) = 200.00 + 50.00 + 225.00 = 475.00
            assertEquals(new BigDecimal("475.00"), deal.calculateTotalValue());
        }

        @Test
        @DisplayName("Calculate total value with zero quantity product")
        void testCalculateTotalValueZeroQuantity() {
            DealProduct product = new DealProduct("PROD001", "Test Product", 0, new BigDecimal("100.00"));
            deal.addProduct(product);

            // Expected: 0 * 100.00 = 0.00
            assertEquals(BigDecimal.ZERO, deal.calculateTotalValue());
        }
    }

    @Nested
    @DisplayName("Date Property Tests")
    class DatePropertyTests {

        @Test
        @DisplayName("Setting closeDate should update the property")
        void testSetAndGetCloseDate() {
            assertNull(deal.getCloseDate());

            LocalDate closeDate = LocalDate.now();
            deal.setCloseDate(closeDate);
            assertEquals(closeDate, deal.getCloseDate());

            // Test with null
            deal.setCloseDate(null);
            assertNull(deal.getCloseDate());
        }

        @Test
        @DisplayName("Setting createdDate should update the property")
        void testSetAndGetCreatedDate() {
            LocalDate initialCreatedDate = deal.getCreatedDate();
            assertNotNull(initialCreatedDate);

            LocalDate newCreatedDate = LocalDate.now().minusDays(5);
            deal.setCreatedDate(newCreatedDate);
            assertEquals(newCreatedDate, deal.getCreatedDate());

            // Test with null
            deal.setCreatedDate(null);
            assertNull(deal.getCreatedDate());

            // Reset to initial value
            deal.setCreatedDate(initialCreatedDate);
        }

        @Test
        @DisplayName("Setting lastModifiedDate should update the property")
        void testSetAndGetLastModifiedDate() {
            LocalDate initialLastModifiedDate = deal.getLastModifiedDate();
            assertNotNull(initialLastModifiedDate);

            LocalDate newLastModifiedDate = LocalDate.now().minusDays(2);
            deal.setLastModifiedDate(newLastModifiedDate);
            assertEquals(newLastModifiedDate, deal.getLastModifiedDate());

            // Test with null
            deal.setLastModifiedDate(null);
            assertNull(deal.getLastModifiedDate());

            // Reset to initial value
            deal.setLastModifiedDate(initialLastModifiedDate);
        }
    }

    @Nested
    @DisplayName("Object Method Tests")
    class ObjectMethodTests {

        @Test
        @DisplayName("equals() should compare by ID")
        void testEquals() {
            Deal deal1 = new Deal(DEFAULT_TITLE, DEFAULT_VALUE, DEFAULT_SALES_REP_ID);
            Deal deal2 = new Deal(DEFAULT_TITLE, DEFAULT_VALUE, DEFAULT_SALES_REP_ID);

            // Both IDs null, should be different objects
            assertNotEquals(deal1, deal2);

            // Set same ID
            deal1.setId("DEAL001");
            deal2.setId("DEAL001");
            assertEquals(deal1, deal2);

            // Set different IDs
            deal2.setId("DEAL002");
            assertNotEquals(deal1, deal2);

            // Test with null
            assertNotEquals(deal1, null);

            // Test with different class
            assertNotEquals(deal1, "Not a Deal");

            // Test with same object
            assertEquals(deal1, deal1);
        }

        @Test
        @DisplayName("hashCode() should be based on ID")
        void testHashCode() {
            Deal deal1 = new Deal(DEFAULT_TITLE, DEFAULT_VALUE, DEFAULT_SALES_REP_ID);
            Deal deal2 = new Deal(DEFAULT_TITLE, DEFAULT_VALUE, DEFAULT_SALES_REP_ID);

            // Set same ID
            deal1.setId("DEAL001");
            deal2.setId("DEAL001");
            assertEquals(deal1.hashCode(), deal2.hashCode());

            // Set different IDs
            deal2.setId("DEAL002");
            assertNotEquals(deal1.hashCode(), deal2.hashCode());
        }

        @Test
        @DisplayName("toString() should include key properties")
        void testToString() {
            deal.setId("DEAL001");
            deal.setCloseDate(LocalDate.of(2023, 12, 31));

            String toString = deal.toString();

            // Verify key properties are included in toString
            assertTrue(toString.contains("DEAL001"));
            assertTrue(toString.contains(DEFAULT_TITLE));
            assertTrue(toString.contains(DEFAULT_VALUE.toString()));
            assertTrue(toString.contains(DealStatus.OPEN.toString()));
            assertTrue(toString.contains(DEFAULT_SALES_REP_ID));
            assertTrue(toString.contains("2023-12-31"));
        }
    }
}
