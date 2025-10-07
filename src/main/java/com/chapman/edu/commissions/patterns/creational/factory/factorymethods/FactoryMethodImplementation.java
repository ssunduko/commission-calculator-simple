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
     * ABSTRACT CREATOR: Base class that defines the factory method pattern structure
     *
     * PATTERN COMPONENT: This is the "Creator" in the Factory Method pattern
     *
     * PURPOSE:
     * Provides a template for creating Deal objects while delegating the actual
     * instantiation logic to subclasses. This allows each subclass to create
     * different types of deals without changing the overall creation workflow.
     *
     * KEY METHODS:
     * - createDeal(): Abstract factory method (must be implemented by subclasses)
     * - createDealWithProducts(): Template method (provides consistent workflow)
     * - addDefaultProducts(): Hook method (can be overridden for customization)
     *
     * PATTERN INTEGRATION:
     * This class combines Factory Method with Template Method pattern for
     * maximum flexibility and consistency in object creation.
     */
    public static abstract class DealFactory {

        /**
         * FACTORY METHOD: Abstract method that subclasses must implement
         *
         * PATTERN COMPONENT: This is the core "Factory Method"
         *
         * PURPOSE:
         * Declares the interface for creating Deal objects without specifying
         * the concrete class. Each subclass implements this to create its
         * specific type of deal (hardware, software, service).
         *
         * WHY ABSTRACT:
         * By making this abstract, we enforce that subclasses MUST provide
         * their own implementation. This is the key to achieving the
         * Open/Closed Principle - new deal types are added by creating new
         * subclasses, not by modifying this class.
         *
         * SUBCLASS RESPONSIBILITY:
         * Each concrete factory implements this to:
         * 1. Create a Deal instance
         * 2. Set type-specific ID (e.g., "HW-", "SW-", "SVC-")
         * 3. Set initial status
         * 4. Return the configured Deal
         *
         * @param title the title of the deal
         * @param value the monetary value of the deal
         * @param salesRepId the ID of the sales representative
         * @return a new Deal instance (specific type determined by subclass)
         */
        protected abstract Deal createDeal(String title, BigDecimal value, String salesRepId);

        /**
         * TEMPLATE METHOD: Defines the overall workflow for creating deals with products
         *
         * PATTERN COMPONENT: This is the "Template Method" that uses the Factory Method
         *
         * PURPOSE:
         * Provides a consistent, standardized workflow for creating fully
         * configured deals. This method orchestrates the creation process,
         * calling the factory method and hook methods in a specific order.
         *
         * WORKFLOW STEPS:
         * 1. Call createDeal() - Factory Method (implemented by subclass)
         * 2. Set common properties - Shared logic for all deal types
         * 3. Call addDefaultProducts() - Hook Method (can be overridden by subclass)
         * 4. Return fully configured deal
         *
         * BENEFITS:
         * - Ensures consistent initialization across all deal types
         * - Common logic is centralized (DRY principle)
         * - Extensible through factory method and hook method
         * - Clients call one method to get a fully configured product
         *
         * WHY PUBLIC:
         * This is the main entry point for clients. They call this method
         * and receive a fully configured deal without knowing the details
         * of how it was created.
         *
         * @param title the title of the deal
         * @param value the monetary value of the deal
         * @param salesRepId the ID of the sales representative
         * @return a fully configured Deal with products
         */
        public Deal createDealWithProducts(String title, BigDecimal value, String salesRepId) {
            // STEP 1: Create the deal using the FACTORY METHOD
            // This delegates to the subclass-specific implementation
            // The concrete type of deal returned depends on which factory subclass is used
            Deal deal = createDeal(title, value, salesRepId);

            // STEP 2: Apply COMMON CONFIGURATION
            // This logic is shared across all deal types - it's part of the template
            // Set the creation date to now
            deal.setCreatedDate(LocalDate.now());
            // Set the last modified date to now
            deal.setLastModifiedDate(LocalDate.now());

            // STEP 3: Add type-specific products using the HOOK METHOD
            // This calls a method that subclasses can override to add their products
            // Each deal type (hardware, software, service) has different default products
            addDefaultProducts(deal);

            // STEP 4: Return the fully configured deal
            // The deal is now ready to use with all its properties and products set
            return deal;
        }

        /**
         * HOOK METHOD: Optional extension point for subclasses to add default products
         *
         * PATTERN COMPONENT: This is a "Hook Method" in the Template Method pattern
         *
         * PURPOSE:
         * Provides an extension point for subclasses to customize the deal
         * without overriding the entire template method. Subclasses can
         * override this to add their type-specific products.
         *
         * DEFAULT BEHAVIOR:
         * Does nothing - this allows subclasses to choose whether to add products
         *
         * WHY PROTECTED:
         * Only subclasses should call or override this method. Clients should
         * not call it directly - they use the template method instead.
         *
         * SUBCLASS EXAMPLES:
         * - HardwareDealFactory: Adds laptops and monitors
         * - SoftwareDealFactory: Adds operating systems and office packages
         * - ServiceDealFactory: Adds support and training services
         *
         * @param deal the deal to add products to
         */
        protected void addDefaultProducts(Deal deal) {
            // DEFAULT IMPLEMENTATION: Do nothing
            // This is a hook that subclasses CAN override but aren't required to
            // If a deal type doesn't need default products, it can leave this as-is
        }
    }

    /**
     * CONCRETE CREATOR: Factory for creating hardware deals
     *
     * PATTERN COMPONENT: This is a "ConcreteCreator" in the Factory Method pattern
     *
     * PURPOSE:
     * Implements the factory method to create Deal objects configured for
     * hardware sales. Customizes both the deal itself and the products included.
     *
     * RESPONSIBILITIES:
     * 1. Implement createDeal() to create hardware-specific deals
     * 2. Override addDefaultProducts() to add hardware products
     * 3. Ensure hardware deals have "HW-" prefix for easy identification
     *
     * EXTENSIBILITY:
     * New deal types can be added by creating additional subclasses of DealFactory
     * without modifying this class or any existing code (Open/Closed Principle).
     */
    public static class HardwareDealFactory extends DealFactory {
        /**
         * FACTORY METHOD IMPLEMENTATION: Creates a hardware deal
         *
         * PURPOSE:
         * Implements the abstract factory method to create a Deal configured
         * for hardware sales with a hardware-specific ID prefix.
         *
         * CUSTOMIZATION:
         * - Sets ID prefix to "HW-" to distinguish hardware deals
         * - Initializes status to OPEN (ready for sale)
         * - Uses timestamp for unique ID generation
         *
         * @param title the title of the hardware deal
         * @param value the monetary value of the deal
         * @param salesRepId the ID of the sales representative
         * @return a new Deal configured for hardware sales
         */
        @Override
        protected Deal createDeal(String title, BigDecimal value, String salesRepId) {
            // Create the base Deal object
            Deal deal = new Deal(title, value, salesRepId);
            // Set hardware-specific ID prefix for identification and tracking
            deal.setId("HW-" + System.currentTimeMillis());
            // Initialize with OPEN status - ready to be worked
            deal.setStatus(DealStatus.OPEN);
            return deal;
        }

        /**
         * HOOK METHOD IMPLEMENTATION: Adds hardware-specific products to the deal
         *
         * PURPOSE:
         * Overrides the hook method to add default hardware products that are
         * commonly sold together. This ensures hardware deals come pre-configured
         * with relevant products.
         *
         * PRODUCTS ADDED:
         * - Laptop (1 unit @ $1,200)
         * - Monitor (2 units @ $300 each)
         *
         * BUSINESS LOGIC:
         * Hardware deals typically include both computing devices and peripherals.
         * This method encapsulates that business rule.
         *
         * @param deal the hardware deal to add products to
         */
        @Override
        protected void addDefaultProducts(Deal deal) {
            // PRODUCT 1: Laptop - the primary hardware item
            DealProduct laptop = new DealProduct();
            laptop.setProductName("Laptop");
            laptop.setProductId("HW-LAPTOP");
            laptop.setPrice(new BigDecimal("1200.00"));
            laptop.setQuantity(1);  // Typically one laptop per deal
            deal.addProduct(laptop);

            // PRODUCT 2: Monitor - common peripheral for hardware bundles
            DealProduct monitor = new DealProduct();
            monitor.setProductName("Monitor");
            monitor.setProductId("HW-MONITOR");
            monitor.setPrice(new BigDecimal("300.00"));
            monitor.setQuantity(2);  // Dual monitor setup
            deal.addProduct(monitor);
        }
    }

    /**
     * CONCRETE CREATOR: Factory for creating software deals
     *
     * PATTERN COMPONENT: This is a "ConcreteCreator" in the Factory Method pattern
     *
     * PURPOSE:
     * Implements the factory method to create Deal objects configured for
     * software licenses and subscriptions. Software deals often involve
     * multiple licenses for the same product.
     *
     * DIFFERENTIATION FROM HARDWARE:
     * - Uses "SW-" prefix instead of "HW-"
     * - Typically includes multiple quantities (licenses)
     * - Products are intangible (software licenses)
     */
    public static class SoftwareDealFactory extends DealFactory {
        /**
         * FACTORY METHOD IMPLEMENTATION: Creates a software deal
         *
         * PURPOSE:
         * Implements the abstract factory method to create a Deal configured
         * for software sales with a software-specific ID prefix.
         *
         * CUSTOMIZATION:
         * - Sets ID prefix to "SW-" to distinguish software deals
         * - Initializes status to OPEN (ready for license negotiation)
         *
         * @param title the title of the software deal
         * @param value the monetary value of the deal
         * @param salesRepId the ID of the sales representative
         * @return a new Deal configured for software sales
         */
        @Override
        protected Deal createDeal(String title, BigDecimal value, String salesRepId) {
            // Create the base Deal object
            Deal deal = new Deal(title, value, salesRepId);
            // Set software-specific ID prefix
            deal.setId("SW-" + System.currentTimeMillis());
            // Initialize with OPEN status
            deal.setStatus(DealStatus.OPEN);
            return deal;
        }

        /**
         * HOOK METHOD IMPLEMENTATION: Adds software-specific products to the deal
         *
         * PURPOSE:
         * Overrides the hook method to add default software products that are
         * commonly sold together in enterprise software deals.
         *
         * PRODUCTS ADDED:
         * - Operating System (5 licenses @ $200 each)
         * - Office Package (5 licenses @ $150 each)
         *
         * BUSINESS LOGIC:
         * Software deals often include both system software and productivity
         * software. The quantities represent site licenses for multiple users.
         *
         * @param deal the software deal to add products to
         */
        @Override
        protected void addDefaultProducts(Deal deal) {
            // PRODUCT 1: Operating System - foundation software
            DealProduct operatingSystem = new DealProduct();
            operatingSystem.setProductName("Operating System");
            operatingSystem.setProductId("SW-OS");
            operatingSystem.setPrice(new BigDecimal("200.00"));
            operatingSystem.setQuantity(5);  // 5 licenses for 5 workstations
            deal.addProduct(operatingSystem);

            // PRODUCT 2: Office Package - productivity suite
            DealProduct officePackage = new DealProduct();
            officePackage.setProductName("Office Package");
            officePackage.setProductId("SW-OFFICE");
            officePackage.setPrice(new BigDecimal("150.00"));
            officePackage.setQuantity(5);  // 5 licenses to match OS licenses
            deal.addProduct(officePackage);
        }
    }

    /**
     * CONCRETE CREATOR: Factory for creating service deals
     *
     * PATTERN COMPONENT: This is a "ConcreteCreator" in the Factory Method pattern
     *
     * PURPOSE:
     * Implements the factory method to create Deal objects configured for
     * professional services such as support, training, and consulting.
     *
     * DIFFERENTIATION FROM HARDWARE AND SOFTWARE:
     * - Uses "SVC-" prefix for service deals
     * - Products are time-based services rather than physical or licensed goods
     * - Typically sold as packages or retainers
     */
    public static class ServiceDealFactory extends DealFactory {
        /**
         * FACTORY METHOD IMPLEMENTATION: Creates a service deal
         *
         * PURPOSE:
         * Implements the abstract factory method to create a Deal configured
         * for professional services with a service-specific ID prefix.
         *
         * CUSTOMIZATION:
         * - Sets ID prefix to "SVC-" to distinguish service deals
         * - Initializes status to OPEN (ready for service scope definition)
         *
         * @param title the title of the service deal
         * @param value the monetary value of the deal
         * @param salesRepId the ID of the sales representative
         * @return a new Deal configured for service sales
         */
        @Override
        protected Deal createDeal(String title, BigDecimal value, String salesRepId) {
            // Create the base Deal object
            Deal deal = new Deal(title, value, salesRepId);
            // Set service-specific ID prefix
            deal.setId("SVC-" + System.currentTimeMillis());
            // Initialize with OPEN status
            deal.setStatus(DealStatus.OPEN);
            return deal;
        }

        /**
         * HOOK METHOD IMPLEMENTATION: Adds service-specific products to the deal
         *
         * PURPOSE:
         * Overrides the hook method to add default service products that are
         * commonly sold together in professional services engagements.
         *
         * PRODUCTS ADDED:
         * - Technical Support (1 package @ $500)
         * - Training (1 session @ $300)
         *
         * BUSINESS LOGIC:
         * Service deals often bundle support with training to ensure customers
         * can effectively use the products or systems they've purchased.
         * This represents a complete service package.
         *
         * @param deal the service deal to add products to
         */
        @Override
        protected void addDefaultProducts(Deal deal) {
            // PRODUCT 1: Technical Support - ongoing assistance service
            DealProduct support = new DealProduct();
            support.setProductName("Technical Support");
            support.setProductId("SVC-SUPPORT");
            support.setPrice(new BigDecimal("500.00"));
            support.setQuantity(1);  // 1 support package (e.g., annual contract)
            deal.addProduct(support);

            // PRODUCT 2: Training - knowledge transfer service
            DealProduct training = new DealProduct();
            training.setProductName("Training");
            training.setProductId("SVC-TRAINING");
            training.setPrice(new BigDecimal("300.00"));
            training.setQuantity(1);  // 1 training session package
            deal.addProduct(training);
        }
    }
}
