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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Deal with a 1:3 code-to-test ratio.
 * 
 * In a 1:3 ratio, the amount of test code is approximately three times the amount of production code.
 * This test class provides extremely comprehensive testing with:
 * - Extensive test methods covering all aspects of the class
 * - Multiple assertions per test
 * - Thorough edge case testing
 * - Boundary value testing
 * - Negative testing (testing for expected failures)
 * - Complex test scenarios
 * - Nested test classes for better organization
 * - Detailed documentation
 */
public class DealTest1to3Ratio {
    
    private Deal deal;
    private static final String DEFAULT_TITLE = "Test Deal";
    private static final BigDecimal DEFAULT_VALUE = new BigDecimal("1000.00");
    private static final String DEFAULT_SALES_REP_ID = "REP001";
    private static final LocalDate DEFAULT_DATE = LocalDate.of(2023, 1, 1);
    
    @BeforeEach
    void setUp() {
        // Create a new Deal instance before each test
        deal = new Deal(DEFAULT_TITLE, DEFAULT_VALUE, DEFAULT_SALES_REP_ID);
    }
    
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Default constructor should initialize minimal properties")
        void testDefaultConstructor() {
            Deal emptyDeal = new Deal();
            
            // Test that products list is initialized
            assertNotNull(emptyDeal.getProducts(), "Products list should be initialized");
            assertTrue(emptyDeal.getProducts().isEmpty(), "Products list should be empty");
            
            // Test that dates are initialized
            assertNotNull(emptyDeal.getCreatedDate(), "Created date should be initialized");
            assertNotNull(emptyDeal.getLastModifiedDate(), "Last modified date should be initialized");
            
            // Test that other fields are null
            assertNull(emptyDeal.getId(), "ID should be null");
            assertNull(emptyDeal.getTitle(), "Title should be null");
            assertNull(emptyDeal.getValue(), "Value should be null");
            assertNull(emptyDeal.getSalesRepId(), "Sales rep ID should be null");
            assertNull(emptyDeal.getStatus(), "Status should be null");
            assertNull(emptyDeal.getCloseDate(), "Close date should be null");
            
            // Verify created date and last modified date are the same day
            assertEquals(emptyDeal.getCreatedDate().getYear(), emptyDeal.getLastModifiedDate().getYear(), 
                    "Created and last modified years should match");
            assertEquals(emptyDeal.getCreatedDate().getMonth(), emptyDeal.getLastModifiedDate().getMonth(), 
                    "Created and last modified months should match");
            assertEquals(emptyDeal.getCreatedDate().getDayOfMonth(), emptyDeal.getLastModifiedDate().getDayOfMonth(), 
                    "Created and last modified days should match");
        }
        
        @Test
        @DisplayName("Parameterized constructor should set initial values correctly")
        void testParameterizedConstructor() {
            // Test the constructor sets the expected values
            assertEquals(DEFAULT_TITLE, deal.getTitle(), "Title should match the constructor parameter");
            assertEquals(DEFAULT_VALUE, deal.getValue(), "Value should match the constructor parameter");
            assertEquals(DEFAULT_SALES_REP_ID, deal.getSalesRepId(), "Sales rep ID should match the constructor parameter");
            assertEquals(DealStatus.OPEN, deal.getStatus(), "Status should be set to OPEN");
            
            // Test that products list is initialized
            assertNotNull(deal.getProducts(), "Products list should be initialized");
            assertTrue(deal.getProducts().isEmpty(), "Products list should be empty");
            
            // Test that dates are initialized
            assertNotNull(deal.getCreatedDate(), "Created date should be initialized");
            assertNotNull(deal.getLastModifiedDate(), "Last modified date should be initialized");
            
            // Test that other fields are null
            assertNull(deal.getId(), "ID should be null");
            assertNull(deal.getCloseDate(), "Close date should be null");
            
            // Verify created date and last modified date are the same day
            assertEquals(deal.getCreatedDate().getYear(), deal.getLastModifiedDate().getYear(), 
                    "Created and last modified years should match");
            assertEquals(deal.getCreatedDate().getMonth(), deal.getLastModifiedDate().getMonth(), 
                    "Created and last modified months should match");
            assertEquals(deal.getCreatedDate().getDayOfMonth(), deal.getLastModifiedDate().getDayOfMonth(), 
                    "Created and last modified days should match");
        }
        
        @Test
        @DisplayName("Parameterized constructor should handle null parameters")
        void testParameterizedConstructorWithNullParameters() {
            Deal nullDeal = new Deal(null, null, null);
            
            // Test that products list is initialized
            assertNotNull(nullDeal.getProducts(), "Products list should be initialized even with null parameters");
            assertTrue(nullDeal.getProducts().isEmpty(), "Products list should be empty");
            
            // Test that dates are initialized
            assertNotNull(nullDeal.getCreatedDate(), "Created date should be initialized even with null parameters");
            assertNotNull(nullDeal.getLastModifiedDate(), "Last modified date should be initialized even with null parameters");
            
            // Test that other fields are as expected
            assertNull(nullDeal.getId(), "ID should be null");
            assertNull(nullDeal.getTitle(), "Title should be null when passed as null");
            assertNull(nullDeal.getValue(), "Value should be null when passed as null");
            assertNull(nullDeal.getSalesRepId(), "Sales rep ID should be null when passed as null");
            assertEquals(DealStatus.OPEN, nullDeal.getStatus(), "Status should be set to OPEN even with null parameters");
            assertNull(nullDeal.getCloseDate(), "Close date should be null");
        }
        
        @Test
        @DisplayName("Parameterized constructor should handle empty string parameters")
        void testParameterizedConstructorWithEmptyStringParameters() {
            Deal emptyStringDeal = new Deal("", new BigDecimal("0"), "");
            
            // Test that products list is initialized
            assertNotNull(emptyStringDeal.getProducts(), "Products list should be initialized with empty string parameters");
            assertTrue(emptyStringDeal.getProducts().isEmpty(), "Products list should be empty");
            
            // Test that dates are initialized
            assertNotNull(emptyStringDeal.getCreatedDate(), "Created date should be initialized with empty string parameters");
            assertNotNull(emptyStringDeal.getLastModifiedDate(), "Last modified date should be initialized with empty string parameters");
            
            // Test that other fields are as expected
            assertNull(emptyStringDeal.getId(), "ID should be null");
            assertEquals("", emptyStringDeal.getTitle(), "Title should be empty string when passed as empty");
            assertEquals(new BigDecimal("0"), emptyStringDeal.getValue(), "Value should be zero when passed as zero");
            assertEquals("", emptyStringDeal.getSalesRepId(), "Sales rep ID should be empty string when passed as empty");
            assertEquals(DealStatus.OPEN, emptyStringDeal.getStatus(), "Status should be set to OPEN with empty string parameters");
            assertNull(emptyStringDeal.getCloseDate(), "Close date should be null");
        }
    }
    
    @Nested
    @DisplayName("ID Property Tests")
    class IdPropertyTests {
        
        @Test
        @DisplayName("Setting ID should update the property")
        void testSetAndGetId() {
            assertNull(deal.getId(), "ID should initially be null");
            
            // Test with valid ID
            String validId = "DEAL001";
            deal.setId(validId);
            assertEquals(validId, deal.getId(), "ID should be updated to the valid value");
            
            // Test with empty string
            deal.setId("");
            assertEquals("", deal.getId(), "ID should be updated to empty string");
            
            // Test with null
            deal.setId(null);
            assertNull(deal.getId(), "ID should be updated to null");
            
            // Test with very long ID
            String longId = "DEAL" + "0".repeat(1000);
            deal.setId(longId);
            assertEquals(longId, deal.getId(), "ID should be updated to the long value");
            
            // Test with special characters
            String specialId = "DEAL-123_456!@#";
            deal.setId(specialId);
            assertEquals(specialId, deal.getId(), "ID should be updated to the value with special characters");
        }
        
        @Test
        @DisplayName("Multiple ID updates should work correctly")
        void testMultipleIdUpdates() {
            // Set initial ID
            deal.setId("DEAL001");
            assertEquals("DEAL001", deal.getId(), "ID should be updated to DEAL001");
            
            // Update ID multiple times
            for (int i = 2; i <= 10; i++) {
                String newId = "DEAL00" + i;
                deal.setId(newId);
                assertEquals(newId, deal.getId(), "ID should be updated to " + newId);
            }
        }
    }
    
    @Nested
    @DisplayName("Title Property Tests")
    class TitlePropertyTests {
        
        @Test
        @DisplayName("Setting title should update the property")
        void testSetAndGetTitle() {
            assertEquals(DEFAULT_TITLE, deal.getTitle(), "Title should initially be the default value");
            
            // Test with new valid title
            String newTitle = "New Deal Title";
            deal.setTitle(newTitle);
            assertEquals(newTitle, deal.getTitle(), "Title should be updated to the new value");
            
            // Test with empty string
            deal.setTitle("");
            assertEquals("", deal.getTitle(), "Title should be updated to empty string");
            
            // Test with null
            deal.setTitle(null);
            assertNull(deal.getTitle(), "Title should be updated to null");
            
            // Test with very long title
            String longTitle = "Deal " + "Title ".repeat(100);
            deal.setTitle(longTitle);
            assertEquals(longTitle, deal.getTitle(), "Title should be updated to the long value");
            
            // Test with special characters
            String specialTitle = "Deal Title !@#$%^&*()_+";
            deal.setTitle(specialTitle);
            assertEquals(specialTitle, deal.getTitle(), "Title should be updated to the value with special characters");
        }
        
        @Test
        @DisplayName("Multiple title updates should work correctly")
        void testMultipleTitleUpdates() {
            // Update title multiple times
            for (int i = 1; i <= 10; i++) {
                String newTitle = "Deal Title " + i;
                deal.setTitle(newTitle);
                assertEquals(newTitle, deal.getTitle(), "Title should be updated to " + newTitle);
            }
        }
    }
    
    @Nested
    @DisplayName("Value Property Tests")
    class ValuePropertyTests {
        
        @Test
        @DisplayName("Setting value should update the property")
        void testSetAndGetValue() {
            assertEquals(DEFAULT_VALUE, deal.getValue(), "Value should initially be the default value");
            
            // Test with new valid value
            BigDecimal newValue = new BigDecimal("2000.00");
            deal.setValue(newValue);
            assertEquals(newValue, deal.getValue(), "Value should be updated to the new value");
            assertEquals(0, newValue.compareTo(deal.getValue()), "Value should be exactly equal to the new value");
            
            // Test with zero
            deal.setValue(BigDecimal.ZERO);
            assertEquals(BigDecimal.ZERO, deal.getValue(), "Value should be updated to zero");
            assertEquals(0, BigDecimal.ZERO.compareTo(deal.getValue()), "Value should be exactly equal to zero");
            
            // Test with negative value
            BigDecimal negativeValue = new BigDecimal("-500.00");
            deal.setValue(negativeValue);
            assertEquals(negativeValue, deal.getValue(), "Value should be updated to the negative value");
            assertEquals(0, negativeValue.compareTo(deal.getValue()), "Value should be exactly equal to the negative value");
            
            // Test with null
            deal.setValue(null);
            assertNull(deal.getValue(), "Value should be updated to null");
            
            // Test with very large value
            BigDecimal largeValue = new BigDecimal("9999999999999.99");
            deal.setValue(largeValue);
            assertEquals(largeValue, deal.getValue(), "Value should be updated to the large value");
            assertEquals(0, largeValue.compareTo(deal.getValue()), "Value should be exactly equal to the large value");
            
            // Test with very small value
            BigDecimal smallValue = new BigDecimal("0.01");
            deal.setValue(smallValue);
            assertEquals(smallValue, deal.getValue(), "Value should be updated to the small value");
            assertEquals(0, smallValue.compareTo(deal.getValue()), "Value should be exactly equal to the small value");
            
            // Test with high precision value
            BigDecimal highPrecisionValue = new BigDecimal("123.456789");
            deal.setValue(highPrecisionValue);
            assertEquals(highPrecisionValue, deal.getValue(), "Value should be updated to the high precision value");
            assertEquals(0, highPrecisionValue.compareTo(deal.getValue()), "Value should be exactly equal to the high precision value");
        }
        
        @Test
        @DisplayName("Multiple value updates should work correctly")
        void testMultipleValueUpdates() {
            // Update value multiple times
            for (int i = 1; i <= 10; i++) {
                BigDecimal newValue = new BigDecimal(i * 100);
                deal.setValue(newValue);
                assertEquals(newValue, deal.getValue(), "Value should be updated to " + newValue);
                assertEquals(0, newValue.compareTo(deal.getValue()), "Value should be exactly equal to " + newValue);
            }
        }
    }
    
    @Nested
    @DisplayName("Status Property Tests")
    class StatusPropertyTests {
        
        @Test
        @DisplayName("Setting status should update the property and lastModifiedDate")
        void testSetAndGetStatus() {
            assertEquals(DealStatus.OPEN, deal.getStatus(), "Status should initially be OPEN");
            LocalDate initialModifiedDate = deal.getLastModifiedDate();
            
            // Wait a moment to ensure the timestamp would be different
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            // Test with WON status
            deal.setStatus(DealStatus.WON);
            assertEquals(DealStatus.WON, deal.getStatus(), "Status should be updated to WON");
            assertNotEquals(initialModifiedDate, deal.getLastModifiedDate(), "Last modified date should be updated");
            
            // Store the new modified date
            LocalDate wonModifiedDate = deal.getLastModifiedDate();
            
            // Wait a moment to ensure the timestamp would be different
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            // Test with LOST status
            deal.setStatus(DealStatus.LOST);
            assertEquals(DealStatus.LOST, deal.getStatus(), "Status should be updated to LOST");
            assertNotEquals(wonModifiedDate, deal.getLastModifiedDate(), "Last modified date should be updated again");
            
            // Store the new modified date
            LocalDate lostModifiedDate = deal.getLastModifiedDate();
            
            // Wait a moment to ensure the timestamp would be different
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            // Test with CANCELLED status
            deal.setStatus(DealStatus.CANCELLED);
            assertEquals(DealStatus.CANCELLED, deal.getStatus(), "Status should be updated to CANCELLED");
            assertNotEquals(lostModifiedDate, deal.getLastModifiedDate(), "Last modified date should be updated again");
            
            // Store the new modified date
            LocalDate cancelledModifiedDate = deal.getLastModifiedDate();
            
            // Wait a moment to ensure the timestamp would be different
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            // Test with OPEN status again
            deal.setStatus(DealStatus.OPEN);
            assertEquals(DealStatus.OPEN, deal.getStatus(), "Status should be updated back to OPEN");
            assertNotEquals(cancelledModifiedDate, deal.getLastModifiedDate(), "Last modified date should be updated again");
        }
        
        @Test
        @DisplayName("Setting status to null should be handled")
        void testSetStatusNull() {
            assertEquals(DealStatus.OPEN, deal.getStatus(), "Status should initially be OPEN");
            
            // Test with null status
            deal.setStatus(null);
            assertNull(deal.getStatus(), "Status should be updated to null");
        }
        
        @Test
        @DisplayName("Should handle all possible status values")
        void testAllStatusValues() {
            // Test OPEN status
            deal.setStatus(DealStatus.OPEN);
            assertEquals(DealStatus.OPEN, deal.getStatus(), "Status should be OPEN");
            assertEquals("Open", deal.getStatus().getDisplayName(), "Display name should be 'Open'");
            
            // Test WON status
            deal.setStatus(DealStatus.WON);
            assertEquals(DealStatus.WON, deal.getStatus(), "Status should be WON");
            assertEquals("Won", deal.getStatus().getDisplayName(), "Display name should be 'Won'");
            
            // Test LOST status
            deal.setStatus(DealStatus.LOST);
            assertEquals(DealStatus.LOST, deal.getStatus(), "Status should be LOST");
            assertEquals("Lost", deal.getStatus().getDisplayName(), "Display name should be 'Lost'");
            
            // Test CANCELLED status
            deal.setStatus(DealStatus.CANCELLED);
            assertEquals(DealStatus.CANCELLED, deal.getStatus(), "Status should be CANCELLED");
            assertEquals("Cancelled", deal.getStatus().getDisplayName(), "Display name should be 'Cancelled'");
        }
        
        @Test
        @DisplayName("Multiple status updates should work correctly")
        void testMultipleStatusUpdates() {
            DealStatus[] statuses = {DealStatus.OPEN, DealStatus.WON, DealStatus.LOST, DealStatus.CANCELLED};
            
            // Update status multiple times
            for (int i = 0; i < 10; i++) {
                DealStatus newStatus = statuses[i % statuses.length];
                deal.setStatus(newStatus);
                assertEquals(newStatus, deal.getStatus(), "Status should be updated to " + newStatus);
            }
        }
    }
    
    @Nested
    @DisplayName("Sales Rep ID Property Tests")
    class SalesRepIdPropertyTests {
        
        @Test
        @DisplayName("Setting salesRepId should update the property")
        void testSetAndGetSalesRepId() {
            assertEquals(DEFAULT_SALES_REP_ID, deal.getSalesRepId(), "Sales rep ID should initially be the default value");
            
            // Test with new valid sales rep ID
            String newSalesRepId = "REP002";
            deal.setSalesRepId(newSalesRepId);
            assertEquals(newSalesRepId, deal.getSalesRepId(), "Sales rep ID should be updated to the new value");
            
            // Test with empty string
            deal.setSalesRepId("");
            assertEquals("", deal.getSalesRepId(), "Sales rep ID should be updated to empty string");
            
            // Test with null
            deal.setSalesRepId(null);
            assertNull(deal.getSalesRepId(), "Sales rep ID should be updated to null");
            
            // Test with very long sales rep ID
            String longSalesRepId = "REP" + "0".repeat(1000);
            deal.setSalesRepId(longSalesRepId);
            assertEquals(longSalesRepId, deal.getSalesRepId(), "Sales rep ID should be updated to the long value");
            
            // Test with special characters
            String specialSalesRepId = "REP-123_456!@#";
            deal.setSalesRepId(specialSalesRepId);
            assertEquals(specialSalesRepId, deal.getSalesRepId(), "Sales rep ID should be updated to the value with special characters");
        }
        
        @Test
        @DisplayName("Multiple salesRepId updates should work correctly")
        void testMultipleSalesRepIdUpdates() {
            // Update sales rep ID multiple times
            for (int i = 1; i <= 10; i++) {
                String newSalesRepId = "REP00" + i;
                deal.setSalesRepId(newSalesRepId);
                assertEquals(newSalesRepId, deal.getSalesRepId(), "Sales rep ID should be updated to " + newSalesRepId);
            }
        }
    }
    
    @Nested
    @DisplayName("Products Management Tests")
    class ProductsManagementTests {
        
        @Test
        @DisplayName("Adding a product should update the products list")
        void testAddProduct() {
            assertTrue(deal.getProducts().isEmpty(), "Products list should initially be empty");
            
            // Test adding a valid product
            DealProduct product = new DealProduct("PROD001", "Test Product", 2, new BigDecimal("100.00"));
            deal.addProduct(product);
            
            assertFalse(deal.getProducts().isEmpty(), "Products list should not be empty after adding a product");
            assertEquals(1, deal.getProducts().size(), "Products list should have 1 item");
            assertTrue(deal.getProducts().contains(product), "Products list should contain the added product");
            assertSame(product, deal.getProducts().get(0), "The product in the list should be the same instance that was added");
        }
        
        @Test
        @DisplayName("Adding multiple products should update the products list correctly")
        void testAddMultipleProducts() {
            assertTrue(deal.getProducts().isEmpty(), "Products list should initially be empty");
            
            // Create test products
            DealProduct product1 = new DealProduct("PROD001", "Test Product 1", 2, new BigDecimal("100.00"));
            DealProduct product2 = new DealProduct("PROD002", "Test Product 2", 1, new BigDecimal("50.00"));
            DealProduct product3 = new DealProduct("PROD003", "Test Product 3", 3, new BigDecimal("75.00"));
            
            // Add products one by one
            deal.addProduct(product1);
            assertEquals(1, deal.getProducts().size(), "Products list should have 1 item after adding first product");
            assertTrue(deal.getProducts().contains(product1), "Products list should contain the first product");
            
            deal.addProduct(product2);
            assertEquals(2, deal.getProducts().size(), "Products list should have 2 items after adding second product");
            assertTrue(deal.getProducts().contains(product1), "Products list should still contain the first product");
            assertTrue(deal.getProducts().contains(product2), "Products list should contain the second product");
            
            deal.addProduct(product3);
            assertEquals(3, deal.getProducts().size(), "Products list should have 3 items after adding third product");
            assertTrue(deal.getProducts().contains(product1), "Products list should still contain the first product");
            assertTrue(deal.getProducts().contains(product2), "Products list should still contain the second product");
            assertTrue(deal.getProducts().contains(product3), "Products list should contain the third product");
            
            // Verify the order of products
            assertEquals(product1, deal.getProducts().get(0), "First product should be at index 0");
            assertEquals(product2, deal.getProducts().get(1), "Second product should be at index 1");
            assertEquals(product3, deal.getProducts().get(2), "Third product should be at index 2");
        }
        
        @Test
        @DisplayName("Adding duplicate products should be allowed")
        void testAddDuplicateProducts() {
            // Create a product
            DealProduct product = new DealProduct("PROD001", "Test Product", 2, new BigDecimal("100.00"));
            
            // Add the same product twice
            deal.addProduct(product);
            deal.addProduct(product);
            
            assertEquals(2, deal.getProducts().size(), "Products list should have 2 items after adding the same product twice");
            assertEquals(product, deal.getProducts().get(0), "First product should be the added product");
            assertEquals(product, deal.getProducts().get(1), "Second product should be the same added product");
            assertSame(deal.getProducts().get(0), deal.getProducts().get(1), "Both products should be the same instance");
        }
        
        @Test
        @DisplayName("Adding products with same ID but different instances should be allowed")
        void testAddProductsWithSameId() {
            // Create two products with the same ID but different instances
            DealProduct product1 = new DealProduct("PROD001", "Test Product 1", 2, new BigDecimal("100.00"));
            DealProduct product2 = new DealProduct("PROD001", "Test Product 1", 2, new BigDecimal("100.00"));
            
            // Add both products
            deal.addProduct(product1);
            deal.addProduct(product2);
            
            assertEquals(2, deal.getProducts().size(), "Products list should have 2 items after adding products with same ID");
            assertEquals(product1, deal.getProducts().get(0), "First product should be the first added product");
            assertEquals(product2, deal.getProducts().get(1), "Second product should be the second added product");
            assertNotSame(deal.getProducts().get(0), deal.getProducts().get(1), "Products should be different instances");
        }
        
        @Test
        @DisplayName("Setting products list should replace existing products")
        void testSetProducts() {
            // Add a product first
            DealProduct initialProduct = new DealProduct("PROD001", "Initial Product", 1, new BigDecimal("100.00"));
            deal.addProduct(initialProduct);
            
            assertEquals(1, deal.getProducts().size(), "Products list should have 1 item after adding initial product");
            
            // Create a new list of products
            List<DealProduct> newProducts = new ArrayList<>();
            DealProduct product1 = new DealProduct("PROD002", "New Product 1", 2, new BigDecimal("200.00"));
            DealProduct product2 = new DealProduct("PROD003", "New Product 2", 3, new BigDecimal("300.00"));
            newProducts.add(product1);
            newProducts.add(product2);
            
            // Set the new products list
            deal.setProducts(newProducts);
            
            // Verify the products list was replaced
            assertEquals(2, deal.getProducts().size(), "Products list should have 2 items after setting new list");
            assertFalse(deal.getProducts().contains(initialProduct), "Products list should not contain the initial product");
            assertTrue(deal.getProducts().contains(product1), "Products list should contain the first new product");
            assertTrue(deal.getProducts().contains(product2), "Products list should contain the second new product");
            
            // Verify the order of products
            assertEquals(product1, deal.getProducts().get(0), "First product should be the first new product");
            assertEquals(product2, deal.getProducts().get(1), "Second product should be the second new product");
        }
        
        @Test
        @DisplayName("Setting products to empty list should be handled")
        void testSetProductsEmpty() {
            // Add a product first
            DealProduct product = new DealProduct("PROD001", "Test Product", 1, new BigDecimal("100.00"));
            deal.addProduct(product);
            
            assertEquals(1, deal.getProducts().size(), "Products list should have 1 item after adding product");
            
            // Set products to empty list
            deal.setProducts(new ArrayList<>());
            
            // Verify products is empty
            assertNotNull(deal.getProducts(), "Products list should not be null");
            assertTrue(deal.getProducts().isEmpty(), "Products list should be empty");
        }
        
        @Test
        @DisplayName("Setting products to null should be handled")
        void testSetProductsNull() {
            // Add a product first
            DealProduct product = new DealProduct("PROD001", "Test Product", 1, new BigDecimal("100.00"));
            deal.addProduct(product);
            
            assertEquals(1, deal.getProducts().size(), "Products list should have 1 item after adding product");
            
            // Set products to null
            deal.setProducts(null);
            
            // Verify products is null
            assertNull(deal.getProducts(), "Products list should be null");
            
            // Reset products to non-null for other tests
            deal.setProducts(new ArrayList<>());
        }
        
        @Test
        @DisplayName("Setting products to unmodifiable list should work")
        void testSetProductsUnmodifiableList() {
            // Create an unmodifiable list of products
            List<DealProduct> unmodifiableProducts = Collections.unmodifiableList(
                    Arrays.asList(
                            new DealProduct("PROD001", "Product 1", 1, new BigDecimal("100.00")),
                            new DealProduct("PROD002", "Product 2", 2, new BigDecimal("200.00"))
                    )
            );
            
            // Set the unmodifiable products list
            deal.setProducts(unmodifiableProducts);
            
            // Verify the products list was set correctly
            assertEquals(2, deal.getProducts().size(), "Products list should have 2 items");
            
            // Try to add another product (this should work since Deal makes a copy of the list)
            try {
                deal.addProduct(new DealProduct("PROD003", "Product 3", 3, new BigDecimal("300.00")));
                assertEquals(3, deal.getProducts().size(), "Products list should have 3 items after adding another product");
            } catch (UnsupportedOperationException e) {
                fail("Should be able to add a product after setting an unmodifiable list");
            }
        }
    }
    
    @Nested
    @DisplayName("Total Value Calculation Tests")
    class TotalValueCalculationTests {
        
        @Test
        @DisplayName("Calculate total value with no products should return zero")
        void testCalculateTotalValueNoProducts() {
            assertTrue(deal.getProducts().isEmpty(), "Products list should initially be empty");
            assertEquals(BigDecimal.ZERO, deal.calculateTotalValue(), "Total value should be zero with no products");
            assertEquals(0, BigDecimal.ZERO.compareTo(deal.calculateTotalValue()), "Total value should be exactly zero with no products");
        }
        
        @Test
        @DisplayName("Calculate total value with one product")
        void testCalculateTotalValueOneProduct() {
            // Add one product
            DealProduct product = new DealProduct("PROD001", "Test Product", 2, new BigDecimal("100.00"));
            deal.addProduct(product);
            
            // Expected: 2 * 100.00 = 200.00
            BigDecimal expected = new BigDecimal("200.00");
            assertEquals(expected, deal.calculateTotalValue(), "Total value should be 200.00 with one product");
            assertEquals(0, expected.compareTo(deal.calculateTotalValue()), "Total value should be exactly 200.00 with one product");
        }
        
        @Test
        @DisplayName("Calculate total value with multiple products")
        void testCalculateTotalValueMultipleProducts() {
            // Add multiple products
            DealProduct product1 = new DealProduct("PROD001", "Test Product 1", 2, new BigDecimal("100.00"));
            DealProduct product2 = new DealProduct("PROD002", "Test Product 2", 1, new BigDecimal("50.00"));
            DealProduct product3 = new DealProduct("PROD003", "Test Product 3", 3, new BigDecimal("75.00"));
            
            deal.addProduct(product1);
            deal.addProduct(product2);
            deal.addProduct(product3);
            
            // Expected: (2 * 100.00) + (1 * 50.00) + (3 * 75.00) = 200.00 + 50.00 + 225.00 = 475.00
            BigDecimal expected = new BigDecimal("475.00");
            assertEquals(expected, deal.calculateTotalValue(), "Total value should be 475.00 with multiple products");
            assertEquals(0, expected.compareTo(deal.calculateTotalValue()), "Total value should be exactly 475.00 with multiple products");
        }
        
        @Test
        @DisplayName("Calculate total value with zero quantity product")
        void testCalculateTotalValueZeroQuantity() {
            // Add a product with zero quantity
            DealProduct product = new DealProduct("PROD001", "Test Product", 0, new BigDecimal("100.00"));
            deal.addProduct(product);
            
            // Expected: 0 * 100.00 = 0.00
            assertEquals(BigDecimal.ZERO, deal.calculateTotalValue(), "Total value should be zero with zero quantity product");
            assertEquals(0, BigDecimal.ZERO.compareTo(deal.calculateTotalValue()), "Total value should be exactly zero with zero quantity product");
        }
        
        @Test
        @DisplayName("Calculate total value with negative quantity product")
        void testCalculateTotalValueNegativeQuantity() {
            // Add a product with negative quantity
            DealProduct product = new DealProduct("PROD001", "Test Product", -2, new BigDecimal("100.00"));
            deal.addProduct(product);
            
            // Expected: -2 * 100.00 = -200.00
            BigDecimal expected = new BigDecimal("-200.00");
            assertEquals(expected, deal.calculateTotalValue(), "Total value should be -200.00 with negative quantity product");
            assertEquals(0, expected.compareTo(deal.calculateTotalValue()), "Total value should be exactly -200.00 with negative quantity product");
        }
        
        @Test
        @DisplayName("Calculate total value with zero price product")
        void testCalculateTotalValueZeroPrice() {
            // Add a product with zero price
            DealProduct product = new DealProduct("PROD001", "Test Product", 2, BigDecimal.ZERO);
            deal.addProduct(product);
            
            // Expected: 2 * 0.00 = 0.00
            assertEquals(BigDecimal.ZERO, deal.calculateTotalValue(), "Total value should be zero with zero price product");
            assertEquals(0, BigDecimal.ZERO.compareTo(deal.calculateTotalValue()), "Total value should be exactly zero with zero price product");
        }
        
        @Test
        @DisplayName("Calculate total value with negative price product")
        void testCalculateTotalValueNegativePrice() {
            // Add a product with negative price
            DealProduct product = new DealProduct("PROD001", "Test Product", 2, new BigDecimal("-100.00"));
            deal.addProduct(product);
            
            // Expected: 2 * -100.00 = -200.00
            BigDecimal expected = new BigDecimal("-200.00");
            assertEquals(expected, deal.calculateTotalValue(), "Total value should be -200.00 with negative price product");
            assertEquals(0, expected.compareTo(deal.calculateTotalValue()), "Total value should be exactly -200.00 with negative price product");
        }
        
        @Test
        @DisplayName("Calculate total value with high precision price product")
        void testCalculateTotalValueHighPrecisionPrice() {
            // Add a product with high precision price
            DealProduct product = new DealProduct("PROD001", "Test Product", 2, new BigDecimal("100.123456789"));
            deal.addProduct(product);
            
            // Expected: 2 * 100.123456789 = 200.246913578
            BigDecimal expected = new BigDecimal("200.246913578");
            assertEquals(expected, deal.calculateTotalValue(), "Total value should be 200.246913578 with high precision price product");
            assertEquals(0, expected.compareTo(deal.calculateTotalValue()), "Total value should be exactly 200.246913578 with high precision price product");
        }
        
        @Test
        @DisplayName("Calculate total value with mixed products")
        void testCalculateTotalValueMixedProducts() {
            // Add products with various quantities and prices
            DealProduct product1 = new DealProduct("PROD001", "Test Product 1", 2, new BigDecimal("100.00"));  // 200.00
            DealProduct product2 = new DealProduct("PROD002", "Test Product 2", 0, new BigDecimal("50.00"));   // 0.00
            DealProduct product3 = new DealProduct("PROD003", "Test Product 3", 3, new BigDecimal("0.00"));    // 0.00
            DealProduct product4 = new DealProduct("PROD004", "Test Product 4", -1, new BigDecimal("75.00"));  // -75.00
            DealProduct product5 = new DealProduct("PROD005", "Test Product 5", 2, new BigDecimal("-25.00"));  // -50.00
            
            deal.addProduct(product1);
            deal.addProduct(product2);
            deal.addProduct(product3);
            deal.addProduct(product4);
            deal.addProduct(product5);
            
            // Expected: 200.00 + 0.00 + 0.00 + (-75.00) + (-50.00) = 75.00
            BigDecimal expected = new BigDecimal("75.00");
            assertEquals(expected, deal.calculateTotalValue(), "Total value should be 75.00 with mixed products");
            assertEquals(0, expected.compareTo(deal.calculateTotalValue()), "Total value should be exactly 75.00 with mixed products");
        }
        
        @Test
        @DisplayName("Calculate total value with null products list should throw NullPointerException")
        void testCalculateTotalValueNullProductsList() {
            // Set products to null
            deal.setProducts(null);
            
            // Expect NullPointerException when calculating total value
            assertThrows(NullPointerException.class, () -> deal.calculateTotalValue(), 
                    "Should throw NullPointerException when calculating total value with null products list");
            
            // Reset products to non-null for other tests
            deal.setProducts(new ArrayList<>());
        }
    }
    
    @Nested
    @DisplayName("Date Property Tests")
    class DatePropertyTests {
        
        @Test
        @DisplayName("Setting closeDate should update the property")
        void testSetAndGetCloseDate() {
            assertNull(deal.getCloseDate(), "Close date should initially be null");
            
            // Test with valid date
            LocalDate closeDate = LocalDate.now();
            deal.setCloseDate(closeDate);
            assertEquals(closeDate, deal.getCloseDate(), "Close date should be updated to the valid date");
            
            // Test with past date
            LocalDate pastDate = LocalDate.now().minusYears(1);
            deal.setCloseDate(pastDate);
            assertEquals(pastDate, deal.getCloseDate(), "Close date should be updated to the past date");
            
            // Test with future date
            LocalDate futureDate = LocalDate.now().plusYears(1);
            deal.setCloseDate(futureDate);
            assertEquals(futureDate, deal.getCloseDate(), "Close date should be updated to the future date");
            
            // Test with null
            deal.setCloseDate(null);
            assertNull(deal.getCloseDate(), "Close date should be updated to null");
        }
        
        @Test
        @DisplayName("Setting createdDate should update the property")
        void testSetAndGetCreatedDate() {
            LocalDate initialCreatedDate = deal.getCreatedDate();
            assertNotNull(initialCreatedDate, "Created date should initially be set");
            
            // Test with valid date
            LocalDate newCreatedDate = LocalDate.now().minusDays(5);
            deal.setCreatedDate(newCreatedDate);
            assertEquals(newCreatedDate, deal.getCreatedDate(), "Created date should be updated to the valid date");
            
            // Test with past date
            LocalDate pastDate = LocalDate.now().minusYears(1);
            deal.setCreatedDate(pastDate);
            assertEquals(pastDate, deal.getCreatedDate(), "Created date should be updated to the past date");
            
            // Test with future date
            LocalDate futureDate = LocalDate.now().plusYears(1);
            deal.setCreatedDate(futureDate);
            assertEquals(futureDate, deal.getCreatedDate(), "Created date should be updated to the future date");
            
            // Test with null
            deal.setCreatedDate(null);
            assertNull(deal.getCreatedDate(), "Created date should be updated to null");
            
            // Reset to initial value
            deal.setCreatedDate(initialCreatedDate);
        }
        
        @Test
        @DisplayName("Setting lastModifiedDate should update the property")
        void testSetAndGetLastModifiedDate() {
            LocalDate initialLastModifiedDate = deal.getLastModifiedDate();
            assertNotNull(initialLastModifiedDate, "Last modified date should initially be set");
            
            // Test with valid date
            LocalDate newLastModifiedDate = LocalDate.now().minusDays(2);
            deal.setLastModifiedDate(newLastModifiedDate);
            assertEquals(newLastModifiedDate, deal.getLastModifiedDate(), "Last modified date should be updated to the valid date");
            
            // Test with past date
            LocalDate pastDate = LocalDate.now().minusYears(1);
            deal.setLastModifiedDate(pastDate);
            assertEquals(pastDate, deal.getLastModifiedDate(), "Last modified date should be updated to the past date");
            
            // Test with future date
            LocalDate futureDate = LocalDate.now().plusYears(1);
            deal.setLastModifiedDate(futureDate);
            assertEquals(futureDate, deal.getLastModifiedDate(), "Last modified date should be updated to the future date");
            
            // Test with null
            deal.setLastModifiedDate(null);
            assertNull(deal.getLastModifiedDate(), "Last modified date should be updated to null");
            
            // Reset to initial value
            deal.setLastModifiedDate(initialLastModifiedDate);
        }
        
        @Test
        @DisplayName("Setting status should update lastModifiedDate")
        void testStatusUpdateChangesLastModifiedDate() {
            LocalDate initialLastModifiedDate = deal.getLastModifiedDate();
            assertNotNull(initialLastModifiedDate, "Last modified date should initially be set");
            
            // Wait a moment to ensure the timestamp would be different
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            // Update status
            deal.setStatus(DealStatus.WON);
            
            // Verify lastModifiedDate was updated
            assertNotEquals(initialLastModifiedDate, deal.getLastModifiedDate(), "Last modified date should be updated when status is changed");
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
            assertNotEquals(deal1, deal2, "Deals with null IDs should not be equal");
            
            // Set same ID
            deal1.setId("DEAL001");
            deal2.setId("DEAL001");
            assertEquals(deal1, deal2, "Deals with same ID should be equal");
            
            // Set different IDs
            deal2.setId("DEAL002");
            assertNotEquals(deal1, deal2, "Deals with different IDs should not be equal");
            
            // Test with null
            assertNotEquals(deal1, null, "Deal should not be equal to null");
            
            // Test with different class
            assertNotEquals(deal1, "Not a Deal", "Deal should not be equal to an object of a different class");
            
            // Test with same object
            assertEquals(deal1, deal1, "Deal should be equal to itself");
        }
        
        @Test
        @DisplayName("equals() should only consider ID, not other properties")
        void testEqualsOnlyConsidersId() {
            Deal deal1 = new Deal("Deal 1", new BigDecimal("1000.00"), "REP001");
            Deal deal2 = new Deal("Deal 2", new BigDecimal("2000.00"), "REP002");
            
            // Set same ID
            deal1.setId("DEAL001");
            deal2.setId("DEAL001");
            
            // Despite different properties, they should be equal because they have the same ID
            assertEquals(deal1, deal2, "Deals with same ID should be equal regardless of other properties");
            
            // Change ID of one deal
            deal2.setId("DEAL002");
            
            // Now they should be different
            assertNotEquals(deal1, deal2, "Deals with different IDs should not be equal");
        }
        
        @Test
        @DisplayName("hashCode() should be based on ID")
        void testHashCode() {
            Deal deal1 = new Deal(DEFAULT_TITLE, DEFAULT_VALUE, DEFAULT_SALES_REP_ID);
            Deal deal2 = new Deal(DEFAULT_TITLE, DEFAULT_VALUE, DEFAULT_SALES_REP_ID);
            
            // Both IDs null, should have same hash code
            assertEquals(deal1.hashCode(), deal2.hashCode(), "Deals with null IDs should have same hash code");
            
            // Set same ID
            deal1.setId("DEAL001");
            deal2.setId("DEAL001");
            assertEquals(deal1.hashCode(), deal2.hashCode(), "Deals with same ID should have same hash code");
            
            // Set different IDs
            deal2.setId("DEAL002");
            assertNotEquals(deal1.hashCode(), deal2.hashCode(), "Deals with different IDs should have different hash codes");
        }
        
        @Test
        @DisplayName("toString() should include key properties")
        void testToString() {
            deal.setId("DEAL001");
            deal.setCloseDate(LocalDate.of(2023, 12, 31));
            
            String toString = deal.toString();
            
            // Verify key properties are included in toString
            assertTrue(toString.contains("DEAL001"), "toString should include the ID");
            assertTrue(toString.contains(DEFAULT_TITLE), "toString should include the title");
            assertTrue(toString.contains(DEFAULT_VALUE.toString()), "toString should include the value");
            assertTrue(toString.contains(DealStatus.OPEN.toString()), "toString should include the status");
            assertTrue(toString.contains(DEFAULT_SALES_REP_ID), "toString should include the sales rep ID");
            assertTrue(toString.contains("2023-12-31"), "toString should include the close date");
        }
        
        @Test
        @DisplayName("toString() should handle null properties")
        void testToStringWithNullProperties() {
            Deal nullDeal = new Deal();
            nullDeal.setId(null);
            nullDeal.setTitle(null);
            nullDeal.setValue(null);
            nullDeal.setStatus(null);
            nullDeal.setSalesRepId(null);
            nullDeal.setCloseDate(null);
            
            // Should not throw NullPointerException
            String toString = nullDeal.toString();
            
            // Verify toString contains something
            assertNotNull(toString, "toString should not be null");
            assertTrue(toString.length() > 0, "toString should not be empty");
        }
    }
}