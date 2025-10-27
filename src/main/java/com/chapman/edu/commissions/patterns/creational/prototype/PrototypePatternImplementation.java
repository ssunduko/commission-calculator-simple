package com.chapman.edu.commissions.patterns.creational.prototype;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * PROTOTYPE PATTERN IMPLEMENTATION
 *
 * Concrete implementations of the Prototype pattern for Deal and DealProduct classes.
 *
 * IMPLEMENTATION STRATEGY:
 * Rather than modifying the original domain models (Deal, DealProduct), we create
 * cloneable subclasses that add prototype capabilities. This preserves the domain
 * model integrity while enabling the Prototype pattern.
 *
 * CLASSES PROVIDED:
 * 1. CloneableDeal - Adds cloning capability to Deal domain model
 * 2. CloneableDealProduct - Adds cloning capability to DealProduct domain model
 *
 * TWO CLONING APPROACHES:
 * 1. SHALLOW CLONING (clone() method):
 *    - Faster performance
 *    - Shares referenced objects (products list references same objects)
 *    - Use when: Referenced objects are immutable or won't be modified
 *
 * 2. DEEP CLONING (deepClone() method):
 *    - Complete independence
 *    - Clones all referenced objects recursively
 *    - Use when: Need to modify clone without affecting original
 *
 * BUSINESS USE CASES:
 * - Create deal templates for different regions/product lines
 * - Duplicate existing deals with minor modifications
 * - Generate test data based on real deal configurations
 * - Create deal variations for proposal scenarios
 *
 * @see PrototypePatternStructure.Prototype
 * @see PrototypeRegistry
 */
public class PrototypePatternImplementation {

    /**
     * CONCRETE PROTOTYPE: CloneableDeal
     *
     * Extends Deal domain model and implements Prototype interface to enable cloning.
     *
     * DESIGN APPROACH:
     * - Inheritance: Extends Deal to reuse all Deal functionality
     * - Interface: Implements Prototype<CloneableDeal> for type-safe cloning
     * - Two strategies: Provides both shallow (clone) and deep (deepClone) copying
     *
     * WHY EXTEND DEAL:
     * - Preserves original Deal class (Open/Closed Principle)
     * - Adds cloning capability without modifying domain model
     * - Can be used wherever Deal is expected (Liskov Substitution)
     *
     * CLONING CONSIDERATIONS:
     * - Primitives & Immutables: BigDecimal, String, LocalDate (safe to share)
     * - Enums: DealStatus (safe to share, immutable)
     * - Collections: Products list (needs special handling)
     *
     * SHALLOW VS DEEP:
     * - clone(): New Deal, new ArrayList, same product object references
     * - deepClone(): New Deal, new ArrayList, new cloned product objects
     */
    public static class CloneableDeal extends Deal implements PrototypePatternStructure.Prototype<CloneableDeal> {
        
        /**
         * Default constructor - delegates to superclass Deal().
         * Required for creating new instances during cloning.
         */
        public CloneableDeal() {
            super();
        }

        /**
         * Constructor with required Deal fields.
         * Delegates to Deal(title, value, salesRepId) constructor.
         *
         * @param title the deal title
         * @param value the deal value
         * @param salesRepId the sales representative ID
         */
        public CloneableDeal(String title, BigDecimal value, String salesRepId) {
            super(title, value, salesRepId);
        }

        /**
         * SHALLOW CLONE METHOD
         *
         * Creates a shallow copy of this Deal object.
         *
         * WHAT IS COPIED:
         * - All primitive fields (copied by value)
         * - All immutable object references (String, BigDecimal, LocalDate, DealStatus)
         * - Products list structure (new ArrayList created)
         *
         * WHAT IS SHARED:
         * - Product objects themselves (same object references in both lists)
         *
         * IMPLICATIONS:
         * - Modifying clone's primitive fields: Does NOT affect original ✓
         * - Modifying clone's product list (add/remove): Does NOT affect original ✓
         * - Modifying a product object in clone's list: DOES affect original ✗
         *
         * WHEN TO USE:
         * - Need quick copy for read-only purposes
         * - Products won't be modified
         * - Performance is critical
         * - Creating many clones from one prototype
         *
         * EXAMPLE:
         * <pre>
         * CloneableDeal original = new CloneableDeal("Deal", value, "REP-001");
         * original.addProduct(product);
         *
         * CloneableDeal copy = original.clone();
         * copy.setId("NEW-ID");                     // Safe - doesn't affect original
         * copy.getProducts().get(0).setPrice(...);  // DANGER - affects original!
         * </pre>
         *
         * @return A shallow clone of this Deal object
         */
        @Override
        public CloneableDeal clone() {
            // Create new empty CloneableDeal instance
            CloneableDeal clonedDeal = new CloneableDeal();

            // Copy all primitive and immutable fields
            // These are safe to share because they're either copied by value or immutable
            clonedDeal.setId(this.getId());                              // String (immutable)
            clonedDeal.setTitle(this.getTitle());                        // String (immutable)
            clonedDeal.setValue(this.getValue());                        // BigDecimal (immutable)
            clonedDeal.setStatus(this.getStatus());                      // DealStatus enum (immutable)
            clonedDeal.setSalesRepId(this.getSalesRepId());              // String (immutable)
            clonedDeal.setCloseDate(this.getCloseDate());                // LocalDate (immutable)
            clonedDeal.setCreatedDate(this.getCreatedDate());            // LocalDate (immutable)
            clonedDeal.setLastModifiedDate(this.getLastModifiedDate());  // LocalDate (immutable)

            // SHALLOW COPY: Create new ArrayList with same product references
            // The list is new, but the DealProduct objects inside are shared
            clonedDeal.setProducts(new ArrayList<>(this.getProducts()));

            return clonedDeal;
        }

        /**
         * DEEP CLONE METHOD
         *
         * Creates a deep copy of this Deal object with complete independence.
         *
         * WHAT IS COPIED:
         * - All primitive fields (copied by value)
         * - All immutable object references (safe to share)
         * - Products list structure (new ArrayList)
         * - Each product object (individually cloned)
         *
         * WHAT IS SHARED:
         * - Nothing! Complete independence from original
         *
         * IMPLICATIONS:
         * - Modifying ANY part of clone: Does NOT affect original ✓
         * - Complete independence between original and clone ✓
         * - More expensive (time and memory) than shallow clone
         *
         * WHEN TO USE:
         * - Need to modify products in the clone
         * - Complete independence required
         * - Creating deal variations with different product configurations
         * - Correctness more important than performance
         *
         * DEEP CLONE PROCESS:
         * 1. Create new CloneableDeal instance
         * 2. Copy all primitive and immutable fields
         * 3. For each product in products list:
         *    a. Clone the product (if CloneableDealProduct)
         *    b. Or manually copy fields (if regular DealProduct)
         * 4. Set cloned products list on new deal
         *
         * EXAMPLE:
         * <pre>
         * CloneableDeal original = new CloneableDeal("Deal", value, "REP-001");
         * original.addProduct(product);
         *
         * CloneableDeal copy = original.deepClone();
         * copy.setId("NEW-ID");                     // Safe - doesn't affect original
         * copy.getProducts().get(0).setPrice(...);  // Safe - doesn't affect original
         * </pre>
         *
         * @return A deep clone of this Deal object with complete independence
         */
        public CloneableDeal deepClone() {
            // Create new empty CloneableDeal instance
            CloneableDeal clonedDeal = new CloneableDeal();

            // Copy all primitive and immutable fields (same as shallow clone)
            clonedDeal.setId(this.getId());
            clonedDeal.setTitle(this.getTitle());
            clonedDeal.setValue(this.getValue());
            clonedDeal.setStatus(this.getStatus());
            clonedDeal.setSalesRepId(this.getSalesRepId());
            clonedDeal.setCloseDate(this.getCloseDate());
            clonedDeal.setCreatedDate(this.getCreatedDate());
            clonedDeal.setLastModifiedDate(this.getLastModifiedDate());

            // DEEP COPY: Clone each product individually
            List<DealProduct> clonedProducts = new ArrayList<>();
            for (DealProduct product : this.getProducts()) {
                if (product instanceof CloneableDealProduct) {
                    // Product is cloneable - use its clone() method
                    clonedProducts.add(((CloneableDealProduct) product).clone());
                } else {
                    // Product is not cloneable - manually copy fields
                    // This handles cases where regular DealProduct objects are used
                    CloneableDealProduct clonedProduct = new CloneableDealProduct();
                    clonedProduct.setId(product.getId());
                    clonedProduct.setProductId(product.getProductId());
                    clonedProduct.setProductName(product.getProductName());
                    clonedProduct.setQuantity(product.getQuantity());
                    clonedProduct.setPrice(product.getPrice());
                    clonedProduct.setDiscount(product.getDiscount());
                    clonedProduct.setDealId(clonedDeal.getId()); // Link to cloned deal
                    clonedProducts.add(clonedProduct);
                }
            }
            clonedDeal.setProducts(clonedProducts);

            return clonedDeal;
        }
    }

    /**
     * CONCRETE PROTOTYPE: CloneableDealProduct
     *
     * Extends DealProduct domain model and implements Prototype interface for cloning.
     *
     * PURPOSE:
     * Enables deep cloning of CloneableDeal objects by providing cloneable product objects.
     * Without this, deepClone() would have to manually copy each product field.
     *
     * WHY NEEDED:
     * - DealProduct is not naturally cloneable
     * - Deep cloning requires cloning all referenced objects
     * - Provides type-safe, reusable cloning for products
     *
     * CLONING STRATEGY:
     * - Simple deep clone (all DealProduct fields are primitives or immutable)
     * - No nested objects to worry about
     * - Single clone() method sufficient (no need for deepClone())
     *
     * FIELDS CLONED:
     * - id: String (immutable)
     * - productId: String (immutable)
     * - productName: String (immutable)
     * - quantity: int (primitive)
     * - price: BigDecimal (immutable)
     * - discount: BigDecimal (immutable)
     * - dealId: String (immutable)
     */
    public static class CloneableDealProduct extends DealProduct implements PrototypePatternStructure.Prototype<CloneableDealProduct> {

        /**
         * Default constructor - delegates to superclass DealProduct().
         * Required for creating new instances during cloning.
         */
        public CloneableDealProduct() {
            super();
        }

        /**
         * Constructor with product fields.
         * Delegates to DealProduct constructor.
         *
         * @param productId the product identifier
         * @param productName the product name
         * @param quantity the quantity
         * @param price the unit price
         */
        public CloneableDealProduct(String productId, String productName, int quantity, BigDecimal price) {
            super(productId, productName, quantity, price);
        }

        /**
         * CLONE METHOD
         *
         * Creates a complete copy of this DealProduct object.
         *
         * IMPLEMENTATION NOTE:
         * Since DealProduct only contains primitives and immutable objects,
         * this is effectively a deep clone. No need for separate deepClone() method.
         *
         * WHAT IS COPIED:
         * - All 7 fields (id, productId, productName, quantity, price, discount, dealId)
         * - All are either primitives or immutable (String, BigDecimal)
         *
         * COMPLETE INDEPENDENCE:
         * The cloned product is completely independent from the original.
         * Modifications to either won't affect the other.
         *
         * USE IN DEEP CLONING:
         * This method is called by CloneableDeal.deepClone() to clone
         * each product in the products list.
         *
         * @return A complete copy of this DealProduct object
         */
        @Override
        public CloneableDealProduct clone() {
            // Create new empty CloneableDealProduct instance
            CloneableDealProduct clonedProduct = new CloneableDealProduct();

            // Copy all 7 fields
            clonedProduct.setId(this.getId());                      // String (immutable)
            clonedProduct.setProductId(this.getProductId());        // String (immutable)
            clonedProduct.setProductName(this.getProductName());    // String (immutable)
            clonedProduct.setQuantity(this.getQuantity());          // int (primitive)
            clonedProduct.setPrice(this.getPrice());                // BigDecimal (immutable)
            clonedProduct.setDiscount(this.getDiscount());          // BigDecimal (immutable)
            clonedProduct.setDealId(this.getDealId());              // String (immutable)

            return clonedProduct;
        }
    }
}