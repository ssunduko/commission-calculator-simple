package com.chapman.edu.commissions.patterns.creational.prototype;

import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.patterns.creational.prototype.PrototypePatternImplementation.CloneableDeal;
import com.chapman.edu.commissions.patterns.creational.prototype.PrototypePatternImplementation.CloneableDealProduct;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * PROTOTYPE PATTERN USAGE EXAMPLES
 *
 * Demonstrates practical usage of the Prototype pattern for cloning Deal objects.
 *
 * DEMONSTRATIONS PROVIDED:
 * 1. Basic Prototype Usage - Simple cloning and customization
 * 2. Shallow vs Deep Cloning - Critical differences and implications
 * 3. Prototype Registry Usage - Template catalog management
 *
 * LEARNING OBJECTIVES:
 * - Understand how to clone objects using the Prototype pattern
 * - See the difference between shallow and deep cloning in practice
 * - Learn when each cloning strategy is appropriate
 * - Master the Registry pattern for managing prototype templates
 *
 * KEY CONCEPTS DEMONSTRATED:
 * - Creating prototypes with full configuration
 * - Cloning to create new instances
 * - Customizing clones for specific needs
 * - Shallow clone behavior with shared references
 * - Deep clone behavior with complete independence
 * - Registry-based template retrieval and cloning
 *
 * REAL-WORLD SCENARIOS:
 * - Deal templates for sales teams
 * - Duplicating successful deals
 * - Creating proposal variations
 * - Test data generation
 *
 * RUN THIS CLASS:
 * mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.creational.prototype.PrototypePatternUsage"
 *
 * @see PrototypePatternStructure.Prototype
 * @see PrototypePatternImplementation.CloneableDeal
 * @see PrototypeRegistry
 */
public class PrototypePatternUsage {

    /**
     * Main method demonstrating Prototype pattern usage.
     *
     * Runs three comprehensive demonstrations:
     * 1. Basic prototype cloning
     * 2. Shallow vs. deep cloning comparison
     * 3. Prototype registry with template catalog
     */
    public static void main(String[] args) {
        // Demonstrate basic prototype usage
        demonstrateBasicPrototypeUsage();
        
        System.out.println("\n-----------------------------------\n");
        
        // Demonstrate shallow vs deep cloning
        demonstrateShallowVsDeepCloning();
        
        System.out.println("\n-----------------------------------\n");
        
        // Demonstrate prototype registry usage
        demonstratePrototypeRegistryUsage();
    }
    
    /**
     * DEMONSTRATION 1: Basic Prototype Usage
     *
     * Shows the fundamental Prototype pattern workflow:
     * 1. Create a fully-configured prototype object
     * 2. Clone it to create a new instance
     * 3. Customize the clone for specific needs
     *
     * KEY CONCEPTS:
     * - Prototype is created once with full configuration
     * - Cloning is faster and simpler than reconstruction
     * - Clone can be customized independently
     * - Original prototype remains unchanged
     *
     * BUSINESS SCENARIO:
     * Sales rep has a standard deal template and needs to create
     * a new deal for a different customer with slightly different value.
     */
    private static void demonstrateBasicPrototypeUsage() {
        System.out.println("BASIC PROTOTYPE USAGE DEMONSTRATION");
        System.out.println("----------------------------------");

        // STEP 1: Create and configure a prototype deal
        // This represents a standard software deal template
        CloneableDeal prototypeDeal = new CloneableDeal("Standard Software Deal", new BigDecimal("10000.00"), "REP001");
        prototypeDeal.setId("DEAL-PROTOTYPE-1");
        prototypeDeal.setStatus(DealStatus.OPEN);
        prototypeDeal.setCloseDate(LocalDate.now().plusMonths(1));

        // Add standard products to the prototype
        CloneableDealProduct product1 = new CloneableDealProduct("PROD-001", "Basic Software License", 1, new BigDecimal("5000.00"));
        CloneableDealProduct product2 = new CloneableDealProduct("PROD-002", "Premium Support", 1, new BigDecimal("5000.00"));
        prototypeDeal.addProduct(product1);
        prototypeDeal.addProduct(product2);
        
        // Display the prototype configuration
        System.out.println("Original Prototype Deal:");
        System.out.println("ID: " + prototypeDeal.getId());
        System.out.println("Title: " + prototypeDeal.getTitle());
        System.out.println("Value: " + prototypeDeal.getValue());
        System.out.println("Status: " + prototypeDeal.getStatus());
        System.out.println("Products: " + prototypeDeal.getProducts().size());

        // STEP 2: Clone the prototype to create a new deal
        // This is much simpler than recreating all the configuration
        CloneableDeal clonedDeal = prototypeDeal.clone();

        // STEP 3: Customize the clone for this specific customer
        // Only change what's different - ID, title, and value
        clonedDeal.setId("DEAL-CLONE-1");
        clonedDeal.setTitle("Enterprise Software Deal");
        clonedDeal.setValue(new BigDecimal("15000.00"));
        // Status, products, and dates are inherited from prototype

        // Display the customized clone
        System.out.println("\nCloned Deal (after modifications):");
        System.out.println("ID: " + clonedDeal.getId());
        System.out.println("Title: " + clonedDeal.getTitle());
        System.out.println("Value: " + clonedDeal.getValue());
        System.out.println("Status: " + clonedDeal.getStatus());
        System.out.println("Products: " + clonedDeal.getProducts().size());

        // STEP 4: Verify prototype is unchanged
        // This demonstrates prototype independence
        System.out.println("\nOriginal Prototype Deal (unchanged):");
        System.out.println("ID: " + prototypeDeal.getId());
        System.out.println("Title: " + prototypeDeal.getTitle());
        System.out.println("Value: " + prototypeDeal.getValue());
    }

    /**
     * DEMONSTRATION 2: Shallow vs Deep Cloning
     *
     * Shows the CRITICAL difference between shallow and deep cloning.
     *
     * SHALLOW CLONE:
     * - Creates new Deal object
     * - Creates new products ArrayList
     * - But products list contains SAME product object references
     * - Modifying a product affects both original and shallow clone
     *
     * DEEP CLONE:
     * - Creates new Deal object
     * - Creates new products ArrayList
     * - Creates NEW cloned product objects
     * - Complete independence - modifications don't affect each other
     *
     * WHEN TO USE EACH:
     * - Shallow: Fast, use when products won't be modified
     * - Deep: Safe, use when you need to modify products
     *
     * THIS DEMONSTRATION PROVES:
     * Modifying original's product affects shallow clone but not deep clone.
     */
    private static void demonstrateShallowVsDeepCloning() {
        System.out.println("SHALLOW VS DEEP CLONING DEMONSTRATION");
        System.out.println("------------------------------------");

        // STEP 1: Create prototype with a product
        CloneableDeal prototypeDeal = new CloneableDeal("Software Deal", new BigDecimal("10000.00"), "REP001");
        prototypeDeal.setId("DEAL-PROTOTYPE-2");

        CloneableDealProduct product = new CloneableDealProduct("PROD-001", "Software License", 1, new BigDecimal("10000.00"));
        product.setId("PRODUCT-1");
        prototypeDeal.addProduct(product);

        // STEP 2: Create SHALLOW clone
        // Creates new Deal and new ArrayList, but shares product objects
        CloneableDeal shallowClone = prototypeDeal.clone();
        shallowClone.setId("DEAL-SHALLOW-CLONE");

        // STEP 3: Create DEEP clone
        // Creates new Deal, new ArrayList, AND new product objects
        CloneableDeal deepClone = prototypeDeal.deepClone();
        deepClone.setId("DEAL-DEEP-CLONE");
        
        // STEP 4: Display initial state - all three have same product name
        System.out.println("Initial State:");
        System.out.println("Original Product Name: " + prototypeDeal.getProducts().get(0).getProductName());
        System.out.println("Shallow Clone Product Name: " + shallowClone.getProducts().get(0).getProductName());
        System.out.println("Deep Clone Product Name: " + deepClone.getProducts().get(0).getProductName());

        // STEP 5: THE CRITICAL TEST - Modify the product in the ORIGINAL deal
        // This will reveal the difference between shallow and deep cloning
        ((CloneableDealProduct)prototypeDeal.getProducts().get(0)).setProductName("Modified Software License");

        // STEP 6: Observe the results
        // Original: Changed (we modified it)
        // Shallow Clone: ALSO CHANGED (shares the same product object!)
        // Deep Clone: UNCHANGED (has its own independent product object)
        System.out.println("\nAfter Modifying Original Product:");
        System.out.println("Original Product Name: " + prototypeDeal.getProducts().get(0).getProductName());
        System.out.println("Shallow Clone Product Name: " + shallowClone.getProducts().get(0).getProductName() + " (changed because it's a shallow copy)");
        System.out.println("Deep Clone Product Name: " + deepClone.getProducts().get(0).getProductName() + " (unchanged because it's a deep copy)");
    }

    /**
     * DEMONSTRATION 3: Prototype Registry Usage
     *
     * Shows how to use a Registry to manage a catalog of prototype templates.
     *
     * REGISTRY BENEFITS:
     * - Centralized template storage
     * - Consistent configurations across application
     * - Easy retrieval by meaningful names
     * - No need to recreate standard prototypes
     *
     * WORKFLOW:
     * 1. Create standard prototype configurations (standard, premium, enterprise)
     * 2. Register each with a meaningful key
     * 3. Retrieve by key and clone to create new instances
     * 4. Customize clones for specific needs
     *
     * BUSINESS SCENARIO:
     * Company has three standard deal tiers. Sales reps select the tier
     * and the system retrieves that template, clones it, and customizes
     * it for the specific customer.
     *
     * KEY PRACTICE:
     * Always use deepClone() when retrieving from registry to ensure
     * complete independence from the stored prototype.
     */
    private static void demonstratePrototypeRegistryUsage() {
        System.out.println("PROTOTYPE REGISTRY USAGE DEMONSTRATION");
        System.out.println("-------------------------------------");

        // STEP 1: Create a prototype registry (catalog)
        PrototypeRegistry registry = new PrototypeRegistry();

        // STEP 2: Create standard deal tier prototypes

        // Standard Tier - Basic deal configuration
        CloneableDeal standardDeal = new CloneableDeal("Standard Deal", new BigDecimal("10000.00"), "REP001");
        standardDeal.setStatus(DealStatus.OPEN);

        // Premium Tier - Mid-level with 2 products
        CloneableDeal premiumDeal = new CloneableDeal("Premium Deal", new BigDecimal("50000.00"), "REP001");
        premiumDeal.setStatus(DealStatus.OPEN);
        premiumDeal.addProduct(new CloneableDealProduct("PROD-001", "Premium License", 1, new BigDecimal("30000.00")));
        premiumDeal.addProduct(new CloneableDealProduct("PROD-002", "Premium Support", 1, new BigDecimal("20000.00")));

        // Enterprise Tier - Full package with 3 products including training
        CloneableDeal enterpriseDeal = new CloneableDeal("Enterprise Deal", new BigDecimal("100000.00"), "REP001");
        enterpriseDeal.setStatus(DealStatus.OPEN);
        enterpriseDeal.addProduct(new CloneableDealProduct("PROD-001", "Enterprise License", 1, new BigDecimal("60000.00")));
        enterpriseDeal.addProduct(new CloneableDealProduct("PROD-002", "Enterprise Support", 1, new BigDecimal("30000.00")));
        enterpriseDeal.addProduct(new CloneableDealProduct("PROD-003", "Training", 1, new BigDecimal("10000.00")));

        // STEP 3: Register prototypes with semantic keys
        registry.addPrototype("standard", standardDeal);
        registry.addPrototype("premium", premiumDeal);
        registry.addPrototype("enterprise", enterpriseDeal);

        System.out.println("Registered Prototypes:");
        System.out.println("- standard");
        System.out.println("- premium");
        System.out.println("- enterprise");

        // STEP 4: Retrieve and clone "premium" template for a new customer
        // IMPORTANT: Use deepClone() for complete independence
        CloneableDeal newDeal = registry.getPrototype("premium").deepClone();

        // STEP 5: Customize for specific customer
        newDeal.setId("DEAL-NEW-1");
        newDeal.setTitle("Custom Premium Deal");
        newDeal.setSalesRepId("REP002");  // Different sales rep
        // Products and value inherited from premium template

        System.out.println("\nCreated a new deal from 'premium' prototype:");
        System.out.println("ID: " + newDeal.getId());
        System.out.println("Title: " + newDeal.getTitle());
        System.out.println("Value: " + newDeal.getValue());
        System.out.println("Sales Rep: " + newDeal.getSalesRepId());
        System.out.println("Products: " + newDeal.getProducts().size());

        // STEP 6: Create another deal from different template
        CloneableDeal anotherDeal = registry.getPrototype("enterprise").deepClone();
        anotherDeal.setId("DEAL-NEW-2");
        anotherDeal.setTitle("Custom Enterprise Deal");

        System.out.println("\nCreated another deal from 'enterprise' prototype:");
        System.out.println("ID: " + anotherDeal.getId());
        System.out.println("Title: " + anotherDeal.getTitle());
        System.out.println("Value: " + anotherDeal.getValue());
        System.out.println("Products: " + anotherDeal.getProducts().size());

        // KEY TAKEAWAY:
        // Registry provides centralized template management.
        // Sales reps just pick a tier, system handles the rest.
        // Consistent configurations, fast deal creation.
    }
}