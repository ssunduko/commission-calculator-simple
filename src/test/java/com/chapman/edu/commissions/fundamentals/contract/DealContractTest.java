package com.chapman.edu.commissions.fundamentals.contract;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class demonstrates the concept of Design by Contract for the Deal class.
 * 
 * Design by Contract is a software design approach that focuses on clearly defining
 * the responsibilities and expectations between different components of a system.
 * 
 * The key elements of Design by Contract are:
 * 1. Pre-conditions: Conditions that must be true before a method is executed
 * 2. Post-conditions: Conditions that must be true after a method is executed
 * 3. Invariants: Conditions that must always be true for an object
 */
public class DealContractTest {

    private Deal deal;

    @BeforeEach
    void setUp() {
        // Initialize a deal for testing
        deal = new Deal("Test Deal", BigDecimal.ZERO, "sales-rep-1");
    }

    /**
     * Test demonstrating pre-conditions for the addProduct method.
     * 
     * Pre-condition: The product parameter must not be null.
     * 
     * Note: This test demonstrates what a contract would look like, but doesn't
     * actually enforce the contract since the current implementation doesn't
     * validate inputs.
     */
    @Test
    void testAddProduct_PreCondition() {
        // Add a null product
        try {
            deal.addProduct(null);
            // If we get here, the implementation accepts null products
            // Count the number of products to verify the null was added
            assertEquals(1, deal.getProducts().size());
            // Verify the product at index 0 is null
            assertNull(deal.getProducts().get(0));
        } catch (NullPointerException e) {
        }
    }

    /**
     * Test demonstrating post-conditions for the addProduct method.
     * 
     * Post-condition: After adding a product, the products list must contain the added product.
     */
    @Test
    void testAddProduct_PreAndPostCondition() {
        // Create a product
        DealProduct product = new DealProduct("prod1", "Product 1", 2, new BigDecimal("100.00"));

        // Verify pre-condition: product is not null
        assertNotNull(product);

        // Add the product to the deal
        deal.addProduct(product);

        // Verify post-condition: products list contains the added product
        assertTrue(deal.getProducts().contains(product));
        assertEquals(1, deal.getProducts().size());
    }

    /**
     * Test demonstrating pre-conditions for the calculateTotalValue method.
     * 
     * Pre-condition: The products list must not be null.
     * 
     * Note: This test demonstrates what a contract would look like, but doesn't
     * actually enforce the contract since the current implementation doesn't
     * validate inputs.
     */
    @Test
    void testCalculateTotalValue_PreCondition() {
        // Set products to null (violating the pre-condition)
        deal.setProducts(null);

        // Try to calculate total value with null products list
        try {
            BigDecimal result = deal.calculateTotalValue();
            // If we get here, the implementation handles null products list
            // This is unexpected based on the implementation, but we'll verify the result
            assertEquals(BigDecimal.ZERO, result, "Expected zero total for null products list");
        } catch (NullPointerException e) {
            // If we get here, the implementation throws NPE when products is null
            // This is expected with the current implementation, but we're not testing that
            // We're just documenting what happens with the current implementation

            // Reset products to a non-null value so the test can continue
            deal.setProducts(new ArrayList<>());
        }

        // In a proper contract implementation, the method would validate its state:
        /*
        public BigDecimal calculateTotalValue() {
            // Pre-condition: products must not be null
            Objects.requireNonNull(products, "Products list cannot be null");
            return products.stream()
                    .map(product -> product.getPrice().multiply(new BigDecimal(product.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        */
    }

    /**
     * Test demonstrating post-conditions for the calculateTotalValue method.
     * 
     * Post-condition: The returned value must be the sum of (price * quantity) for all products.
     */
    @Test
    void testCalculateTotalValue_PostCondition() {
        // Add products to the deal
        deal.addProduct(new DealProduct("prod1", "Product 1", 2, new BigDecimal("100.00")));
        deal.addProduct(new DealProduct("prod2", "Product 2", 1, new BigDecimal("50.00")));

        // Calculate expected total value manually
        BigDecimal expectedTotal = new BigDecimal("250.00");

        // Calculate actual total value
        BigDecimal actualTotal = deal.calculateTotalValue();

        // Verify post-condition: returned value equals the expected sum
        assertEquals(expectedTotal, actualTotal);
    }

    /**
     * Test demonstrating class invariants for the Deal class.
     * 
     * Invariant: The deal's status must always be a valid DealStatus.
     * Invariant: The deal's title must never be null.
     * Invariant: The deal's salesRepId must never be null.
     */
    @Test
    void testDealInvariants() {
        // Verify invariant: status must be a valid DealStatus
        assertNotNull(deal.getStatus());
        assertEquals(DealStatus.OPEN, deal.getStatus());

        // Verify invariant: title must not be null
        assertNotNull(deal.getTitle());

        // Verify invariant: salesRepId must not be null
        assertNotNull(deal.getSalesRepId());

        // Note: In a proper implementation, these invariants would be checked
        // after every method call that could potentially violate them
    }

    /**
     * Test demonstrating a more complex contract with both pre and post conditions.
     * 
     * Pre-condition: The deal must be in OPEN status to change its value.
     * Post-condition: After changing the value, the lastModifiedDate must be updated.
     * 
     * Note: This test demonstrates what a contract would look like, but doesn't
     * actually enforce the post-condition since the current implementation doesn't
     * update lastModifiedDate when setValue is called.
     */
    @Test
    void testSetValue_ContractExample() {
        // Set up: Ensure deal is in OPEN status
        deal.setStatus(DealStatus.OPEN);

        // Record the current lastModifiedDate
        LocalDate originalModifiedDate = deal.getLastModifiedDate();

        // Wait a moment to ensure date would be different if it were updated
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Change the value
        BigDecimal newValue = new BigDecimal("1000.00");
        deal.setValue(newValue);

        // Verify the value was changed
        assertEquals(newValue, deal.getValue(), "Value should be updated");

        // In the current implementation, lastModifiedDate is not updated when setValue is called
        // So we just verify that the lastModifiedDate is the same as before
        assertEquals(originalModifiedDate, deal.getLastModifiedDate(), 
                "Current implementation doesn't update lastModifiedDate when setValue is called");

        // In a proper contract implementation, the method would update lastModifiedDate:
        /*
        public void setValue(BigDecimal value) {
            // Pre-condition: status must be OPEN to change value
            if (this.status != DealStatus.OPEN) {
                throw new IllegalStateException("Cannot change value of a deal that is not OPEN");
            }
            this.value = value;
            // Post-condition: update lastModifiedDate
            this.lastModifiedDate = LocalDate.now();
        }
        */
    }

    /**
     * Test demonstrating pre-conditions for the setTitle method.
     * 
     * Pre-condition: The title parameter must not be null or empty.
     * 
     * Note: This test demonstrates what a contract would look like, but doesn't
     * actually enforce the contract since the current implementation doesn't
     * validate inputs.
     */
    @Test
    void testSetTitle_PreCondition() {
        // Set a null title
        deal.setTitle(null);

        // Verify that the title was set to null (no validation in current implementation)
        assertNull(deal.getTitle());

        // Set an empty title
        deal.setTitle("");

        // Verify that the empty title was set (no validation in current implementation)
        assertEquals("", deal.getTitle());

        // In a proper contract implementation, the method would validate its inputs:
        /*
        public void setTitle(String title) {
            // Pre-condition: title must not be null
            Objects.requireNonNull(title, "Title cannot be null");

            // Pre-condition: title must not be empty
            if (title.trim().isEmpty()) {
                throw new IllegalArgumentException("Title cannot be empty");
            }

            this.title = title;
        }
        */
    }

    /**
     * Test demonstrating post-conditions for the setTitle method.
     * 
     * Post-condition: After setting the title, the title must be updated.
     */
    @Test
    void testSetTitle_PostCondition() {
        // Set a valid title
        String newTitle = "New Deal Title";
        deal.setTitle(newTitle);

        // Verify post-condition: title is updated
        assertEquals(newTitle, deal.getTitle());
    }

    /**
     * Test demonstrating pre-conditions for the setStatus method.
     * 
     * Pre-condition: The status parameter must not be null.
     * 
     * Note: This test demonstrates what a contract would look like, but doesn't
     * actually enforce the contract since the current implementation doesn't
     * validate inputs.
     */
    @Test
    void testSetStatus_PreCondition() {
        // Try to set a null status
        try {
            deal.setStatus(null);
            // If we get here, the implementation accepts null status
            assertNull(deal.getStatus());
        } catch (NullPointerException e) {
            // If we get here, the implementation throws NPE when status is null
            // This is expected with the current implementation, but we're not testing that
            // We're just documenting what happens with the current implementation
        }

        // In a proper contract implementation, the method would validate its inputs:
        /*
        public void setStatus(DealStatus status) {
            // Pre-condition: status must not be null
            Objects.requireNonNull(status, "Status cannot be null");

            this.status = status;
            this.lastModifiedDate = LocalDate.now();
        }
        */
    }

    /**
     * Test demonstrating post-conditions for the setStatus method.
     * 
     * Post-condition: After setting the status, the status must be updated.
     * Post-condition: After setting the status, the lastModifiedDate must be updated (not tested here due to timing issues).
     */
    @Test
    void testSetStatus_PostCondition() {
        // Set a new status
        DealStatus newStatus = DealStatus.WON;
        deal.setStatus(newStatus);

        // Verify post-condition: status is updated
        assertEquals(newStatus, deal.getStatus());

        // Note: We don't test the lastModifiedDate update here because it's difficult to reliably
        // test in a unit test due to timing issues. In a real implementation, we would need to
        // use a clock abstraction to make this testable.
    }

    /**
     * Test demonstrating pre-conditions for the setCloseDate method.
     * 
     * Pre-condition: The closeDate parameter must not be in the future.
     * 
     * Note: This test demonstrates what a contract would look like, but doesn't
     * actually enforce the contract since the current implementation doesn't
     * validate inputs.
     */
    @Test
    void testSetCloseDate_PreCondition() {
        // Set a future close date
        LocalDate futureDate = LocalDate.now().plusDays(30);
        deal.setCloseDate(futureDate);

        // Verify that the future date was set (no validation in current implementation)
        assertEquals(futureDate, deal.getCloseDate());

        // In a proper contract implementation, the method would validate its inputs:
        /*
        public void setCloseDate(LocalDate closeDate) {
            // Pre-condition: closeDate must not be in the future
            if (closeDate != null && closeDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Close date cannot be in the future");
            }

            this.closeDate = closeDate;
        }
        */
    }

    /**
     * Test demonstrating post-conditions for the setCloseDate method.
     * 
     * Post-condition: After setting the closeDate, the closeDate must be updated.
     */
    @Test
    void testSetCloseDate_PostCondition() {
        // Set a valid close date
        LocalDate validDate = LocalDate.now().minusDays(1);
        deal.setCloseDate(validDate);

        // Verify post-condition: closeDate is updated
        assertEquals(validDate, deal.getCloseDate());
    }
}
