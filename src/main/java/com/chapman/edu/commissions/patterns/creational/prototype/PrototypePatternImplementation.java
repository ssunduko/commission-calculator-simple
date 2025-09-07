package com.chapman.edu.commissions.patterns.creational.prototype;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Prototype Pattern for the Commission System.
 * 
 * The Prototype Pattern is a creational design pattern that allows cloning objects,
 * even complex ones, without coupling to their specific classes. It involves creating
 * a prototypical instance first and then cloning it whenever you need a copy.
 * 
 * This pattern is useful when:
 * 1. The cost of creating a new object is expensive or complex
 * 2. You want to hide the complexity of creating new instances from the client
 * 3. You need to create objects that are similar to existing objects
 */
public class PrototypePatternImplementation {

    /**
     * The Prototype interface declares the cloning method.
     */
    public interface Prototype<T> {
        /**
         * Creates a copy of the current object.
         * @return A clone of the current object
         */
        T clone();
    }

    /**
     * Concrete prototype implementation for Deal class.
     * Extends the original Deal class and implements the Prototype interface.
     */
    public static class CloneableDeal extends Deal implements Prototype<CloneableDeal> {
        
        public CloneableDeal() {
            super();
        }
        
        public CloneableDeal(String title, BigDecimal value, String salesRepId) {
            super(title, value, salesRepId);
        }
        
        /**
         * Creates a shallow copy of the Deal object.
         * This copies all primitive fields and references but doesn't clone referenced objects.
         * @return A shallow clone of the Deal object
         */
        @Override
        public CloneableDeal clone() {
            CloneableDeal clonedDeal = new CloneableDeal();
            
            // Copy primitive fields and immutable objects
            clonedDeal.setId(this.getId());
            clonedDeal.setTitle(this.getTitle());
            clonedDeal.setValue(this.getValue());
            clonedDeal.setStatus(this.getStatus());
            clonedDeal.setSalesRepId(this.getSalesRepId());
            clonedDeal.setCloseDate(this.getCloseDate());
            clonedDeal.setCreatedDate(this.getCreatedDate());
            clonedDeal.setLastModifiedDate(this.getLastModifiedDate());
            
            // Shallow copy of products (references only)
            clonedDeal.setProducts(new ArrayList<>(this.getProducts()));
            
            return clonedDeal;
        }
        
        /**
         * Creates a deep copy of the Deal object.
         * This clones all fields including referenced objects.
         * @return A deep clone of the Deal object
         */
        public CloneableDeal deepClone() {
            CloneableDeal clonedDeal = new CloneableDeal();
            
            // Copy primitive fields and immutable objects
            clonedDeal.setId(this.getId());
            clonedDeal.setTitle(this.getTitle());
            clonedDeal.setValue(this.getValue());
            clonedDeal.setStatus(this.getStatus());
            clonedDeal.setSalesRepId(this.getSalesRepId());
            clonedDeal.setCloseDate(this.getCloseDate());
            clonedDeal.setCreatedDate(this.getCreatedDate());
            clonedDeal.setLastModifiedDate(this.getLastModifiedDate());
            
            // Deep copy of products
            List<DealProduct> clonedProducts = new ArrayList<>();
            for (DealProduct product : this.getProducts()) {
                if (product instanceof CloneableDealProduct) {
                    clonedProducts.add(((CloneableDealProduct) product).clone());
                } else {
                    // If the product is not a CloneableDealProduct, create a new one with the same values
                    CloneableDealProduct clonedProduct = new CloneableDealProduct();
                    clonedProduct.setId(product.getId());
                    clonedProduct.setProductId(product.getProductId());
                    clonedProduct.setProductName(product.getProductName());
                    clonedProduct.setQuantity(product.getQuantity());
                    clonedProduct.setPrice(product.getPrice());
                    clonedProduct.setDiscount(product.getDiscount());
                    clonedProduct.setDealId(clonedDeal.getId()); // Set the new deal's ID
                    clonedProducts.add(clonedProduct);
                }
            }
            clonedDeal.setProducts(clonedProducts);
            
            return clonedDeal;
        }
    }

    /**
     * Concrete prototype implementation for DealProduct class.
     * Extends the original DealProduct class and implements the Prototype interface.
     */
    public static class CloneableDealProduct extends DealProduct implements Prototype<CloneableDealProduct> {
        
        public CloneableDealProduct() {
            super();
        }
        
        public CloneableDealProduct(String productId, String productName, int quantity, BigDecimal price) {
            super(productId, productName, quantity, price);
        }
        
        /**
         * Creates a copy of the DealProduct object.
         * @return A clone of the DealProduct object
         */
        @Override
        public CloneableDealProduct clone() {
            CloneableDealProduct clonedProduct = new CloneableDealProduct();
            
            // Copy all fields
            clonedProduct.setId(this.getId());
            clonedProduct.setProductId(this.getProductId());
            clonedProduct.setProductName(this.getProductName());
            clonedProduct.setQuantity(this.getQuantity());
            clonedProduct.setPrice(this.getPrice());
            clonedProduct.setDiscount(this.getDiscount());
            clonedProduct.setDealId(this.getDealId());
            
            return clonedProduct;
        }
    }
}