package com.chapman.edu.commissions.patterns.creational.factory.factorymethods;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Factory Method Implementation
 * 
 * This example demonstrates a more complete implementation of the Factory Method pattern
 * using the Deal class from the model package.
 * 
 * IMPLEMENTATION DETAILS:
 * This implementation shows how the Factory Method pattern can be used to create different
 * types of deals (hardware, software, service) with their specific products. It demonstrates
 * several key aspects of the Factory Method pattern:
 * 
 * 1. Template Method Pattern Integration:
 *    - The abstract DealFactory defines a template method (createDealWithProducts)
 *    - This template method calls the factory method (createDeal) and performs additional setup
 *    - It also calls a hook method (addDefaultProducts) that subclasses can override
 * 
 * 2. Product Customization:
 *    - Each concrete factory creates a specific type of deal with its own ID prefix
 *    - Each factory also adds type-specific products to the deal
 * 
 * 3. Extensibility:
 *    - New deal types can be added by creating new subclasses of DealFactory
 *    - No modification to existing code is required (Open/Closed Principle)
 * 
 * PRACTICAL APPLICATION:
 * This pattern is particularly useful in business applications where:
 * - Different product categories have different initialization requirements
 * - Products need to be bundled together in consistent ways
 * - The system needs to be extensible to support new product types
 * 
 * BENEFITS DEMONSTRATED:
 * - Encapsulation of product creation logic in specialized classes
 * - Consistent initialization of products through the template method
 * - Clear separation of common and type-specific initialization code
 * - Easy extension to support new product types
 * 
 * TRADE-OFFS:
 * - More complex than Simple Factory (more classes and relationships)
 * - Requires understanding of both Factory Method and Template Method patterns
 * - May introduce more classes than necessary for simple scenarios
 */
public class FactoryMethodImplementation {

    /**
     * Abstract Creator class that declares the factory method for creating deals
     */
    public static abstract class DealFactory {

        /**
         * Factory method that subclasses must implement to create a specific type of deal
         */
        protected abstract Deal createDeal(String title, BigDecimal value, String salesRepId);

        /**
         * Template method that uses the factory method
         */
        public Deal createDealWithProducts(String title, BigDecimal value, String salesRepId) {
            // Create the deal using the factory method
            Deal deal = createDeal(title, value, salesRepId);

            // Set common properties
            deal.setCreatedDate(LocalDate.now());
            deal.setLastModifiedDate(LocalDate.now());

            // Add default products (this would be customized in a real application)
            addDefaultProducts(deal);

            return deal;
        }

        /**
         * Helper method to add default products to a deal
         */
        protected void addDefaultProducts(Deal deal) {
            // This method can be overridden by subclasses to add specific products
        }
    }

    /**
     * Concrete Creator for hardware deals
     */
    public static class HardwareDealFactory extends DealFactory {
        @Override
        protected Deal createDeal(String title, BigDecimal value, String salesRepId) {
            Deal deal = new Deal(title, value, salesRepId);
            deal.setId("HW-" + System.currentTimeMillis());
            deal.setStatus(DealStatus.OPEN);
            return deal;
        }

        @Override
        protected void addDefaultProducts(Deal deal) {
            // Add hardware products
            DealProduct laptop = new DealProduct();
            laptop.setProductName("Laptop");
            laptop.setProductId("HW-LAPTOP");
            laptop.setPrice(new BigDecimal("1200.00"));
            laptop.setQuantity(1);
            deal.addProduct(laptop);

            DealProduct monitor = new DealProduct();
            monitor.setProductName("Monitor");
            monitor.setProductId("HW-MONITOR");
            monitor.setPrice(new BigDecimal("300.00"));
            monitor.setQuantity(2);
            deal.addProduct(monitor);
        }
    }

    /**
     * Concrete Creator for software deals
     */
    public static class SoftwareDealFactory extends DealFactory {
        @Override
        protected Deal createDeal(String title, BigDecimal value, String salesRepId) {
            Deal deal = new Deal(title, value, salesRepId);
            deal.setId("SW-" + System.currentTimeMillis());
            deal.setStatus(DealStatus.OPEN);
            return deal;
        }

        @Override
        protected void addDefaultProducts(Deal deal) {
            // Add software products
            DealProduct operatingSystem = new DealProduct();
            operatingSystem.setProductName("Operating System");
            operatingSystem.setProductId("SW-OS");
            operatingSystem.setPrice(new BigDecimal("200.00"));
            operatingSystem.setQuantity(5);
            deal.addProduct(operatingSystem);

            DealProduct officePackage = new DealProduct();
            officePackage.setProductName("Office Package");
            officePackage.setProductId("SW-OFFICE");
            officePackage.setPrice(new BigDecimal("150.00"));
            officePackage.setQuantity(5);
            deal.addProduct(officePackage);
        }
    }

    /**
     * Concrete Creator for service deals
     */
    public static class ServiceDealFactory extends DealFactory {
        @Override
        protected Deal createDeal(String title, BigDecimal value, String salesRepId) {
            Deal deal = new Deal(title, value, salesRepId);
            deal.setId("SVC-" + System.currentTimeMillis());
            deal.setStatus(DealStatus.OPEN);
            return deal;
        }

        @Override
        protected void addDefaultProducts(Deal deal) {
            // Add service products
            DealProduct support = new DealProduct();
            support.setProductName("Technical Support");
            support.setProductId("SVC-SUPPORT");
            support.setPrice(new BigDecimal("500.00"));
            support.setQuantity(1);
            deal.addProduct(support);

            DealProduct training = new DealProduct();
            training.setProductName("Training");
            training.setProductId("SVC-TRAINING");
            training.setPrice(new BigDecimal("300.00"));
            training.setQuantity(1);
            deal.addProduct(training);
        }
    }
}
