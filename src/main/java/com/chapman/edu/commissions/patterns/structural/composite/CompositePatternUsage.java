package com.chapman.edu.commissions.patterns.structural.composite;

import java.math.BigDecimal;

/**
 * CompositePatternUsage demonstrates practical applications of the Composite Pattern
 * in the commission calculator domain.
 *
 * This class shows how the Composite Pattern enables treating individual products
 * and complex deal hierarchies uniformly, simplifying commission calculations and
 * reporting.
 *
 * ### What This Example Demonstrates:
 * 1. Creating leaf objects (individual products)
 * 2. Creating composite objects (deals containing products)
 * 3. Building nested hierarchies (deals containing other deals)
 * 4. Working uniformly with all objects through the component interface
 * 5. Recursive value calculation through the tree structure
 * 6. Dynamic modification of the structure
 *
 * ### Real-World Scenario:
 * Imagine a sales representative closing a large enterprise deal that includes
 * both individual products and pre-packaged bundles (which are themselves deals).
 * The Composite Pattern allows the commission system to calculate the total value
 * seamlessly, regardless of the structure's complexity.
 *
 * @see CompositePatternStructure for pattern interfaces
 * @see CompositePatternImplementation for pattern implementations
 */
public class CompositePatternUsage {

    /**
     * Entry point that demonstrates the Composite Pattern usage.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Using the implementation from CompositePatternImplementation
        useCompositePattern();
    }

    /**
     * Demonstrates how to use the Composite Pattern with the Commission Calculator domain model.
     *
     * This method walks through a complete example showing:
     * - Creating individual products (leaves)
     * - Grouping products into deals (composites)
     * - Nesting deals within other deals (recursive composition)
     * - Generating reports uniformly for any component
     * - Modifying the structure dynamically
     *
     * **Key Observation:** Notice how the same reporting and calculation methods
     * work seamlessly whether applied to a single product, a simple deal, or a complex
     * nested deal structure.
     */
    public static void useCompositePattern() {
        System.out.println("=== Composite Pattern Usage Example ===");

        // ========== STEP 1: Create Individual Products (LEAF nodes) ==========
        // These are the atomic units of our composition - they have no children
        // and represent actual sellable items.

        CompositePatternImplementation.ProductItem laptop =
                new CompositePatternImplementation.ProductItem(
                        "P001",
                        "Business Laptop",
                        2,  // quantity: 2 units
                        new BigDecimal("1200.00"));  // price per unit

        CompositePatternImplementation.ProductItem monitor =
                new CompositePatternImplementation.ProductItem(
                        "P002",
                        "27-inch Monitor",
                        2,  // quantity: 2 units
                        new BigDecimal("350.00"));

        CompositePatternImplementation.ProductItem software =
                new CompositePatternImplementation.ProductItem(
                        "P003",
                        "Office Software Suite",
                        2,  // quantity: 2 units
                        new BigDecimal("200.00"));

        CompositePatternImplementation.ProductItem printer =
                new CompositePatternImplementation.ProductItem(
                        "P004",
                        "Color Laser Printer",
                        1,  // quantity: 1 unit
                        new BigDecimal("450.00"));

        CompositePatternImplementation.ProductItem server =
                new CompositePatternImplementation.ProductItem(
                        "P005",
                        "Enterprise Server",
                        1,  // quantity: 1 unit
                        new BigDecimal("3000.00"));

        // ========== STEP 2: Create Sales Deals (COMPOSITE nodes) ==========
        // Deals are containers that can hold products and/or other deals.
        // They implement the same interface as products (SalesComponent), which
        // is the key to uniform treatment.

        CompositePatternImplementation.SalesDeal workstationDeal =
                new CompositePatternImplementation.SalesDeal(
                        "Workstation Package",
                        "REP001");  // Sales rep ID

        CompositePatternImplementation.SalesDeal enterpriseDeal =
                new CompositePatternImplementation.SalesDeal(
                        "Enterprise Solution",
                        "REP001");  // Same sales rep handling the entire enterprise deal

        // ========== STEP 3: Build the Composite Tree Structure ==========
        // This is where the power of the Composite Pattern becomes evident.
        // We're building a hierarchy that looks like:
        //
        // Enterprise Solution (SalesDeal - COMPOSITE)
        //   ├─ Workstation Package (SalesDeal - COMPOSITE)  <- Nested composite!
        //   │   ├─ Business Laptop (ProductItem - LEAF)
        //   │   ├─ 27-inch Monitor (ProductItem - LEAF)
        //   │   └─ Office Software Suite (ProductItem - LEAF)
        //   ├─ Color Laser Printer (ProductItem - LEAF)
        //   └─ Enterprise Server (ProductItem - LEAF)

        // Add products to the workstation deal (simple composition)
        workstationDeal.addComponent(laptop);
        workstationDeal.addComponent(monitor);
        workstationDeal.addComponent(software);

        // Add products AND the workstation deal to the enterprise deal (recursive composition)
        // IMPORTANT: This line demonstrates the Composite Pattern's power - we're adding
        // a SalesDeal (composite) to another SalesDeal. This creates a tree structure.
        enterpriseDeal.addComponent(workstationDeal);  // Nested composite!
        enterpriseDeal.addComponent(printer);
        enterpriseDeal.addComponent(server);

        // ========== STEP 4: Create a Client (Report Generator) ==========
        // The client works with all components through the SalesComponent interface.
        // It doesn't need to know whether it's dealing with a product or a deal.

        CompositePatternImplementation.SalesReport report =
                new CompositePatternImplementation.SalesReport();

        // ========== STEP 5: Demonstrate Uniform Treatment ==========
        // This is THE KEY BENEFIT of the Composite Pattern.
        // Notice how we call the same generateReport() method for:
        // 1. A single product (leaf)
        // 2. A simple deal containing only products (composite)
        // 3. A complex deal containing nested deals (recursive composite)

        // 1. Report for an individual product (leaf)
        // Expected value: 2 × $1200 = $2400
        System.out.println("\n--- Report for Individual Product ---");
        report.generateReport(laptop);

        // 2. Report for a simple deal (composite with only products)
        // Expected value: (2×$1200) + (2×$350) + (2×$200) = $2400 + $700 + $400 = $3500
        System.out.println("\n--- Report for Workstation Deal ---");
        report.generateReport(workstationDeal);

        // 3. Report for a complex deal (composite with nested composites)
        // Expected value: $3500 (workstation) + $450 (printer) + $3000 (server) = $6950
        // NOTE: The calculation automatically includes the nested workstation deal!
        System.out.println("\n--- Report for Enterprise Deal ---");
        report.generateReport(enterpriseDeal);

        // ========== STEP 6: Demonstrate Uniform Treatment with Direct Calls ==========
        // We can also call calculateValue() directly on any component.
        // The SAME METHOD CALL works for leaves, simple composites, and nested composites.
        System.out.println("\n--- Demonstrating Uniform Treatment ---");

        // All three of these calls use the SAME interface method (calculateValue()),
        // but they execute different implementations:
        // - laptop: calculates directly (leaf behavior)
        // - workstationDeal: sums its children (composite behavior)
        // - enterpriseDeal: sums its children, which includes workstationDeal
        //   that recursively sums its own children (recursive composite behavior)
        System.out.println("Laptop Value: $" + laptop.calculateValue());
        System.out.println("Workstation Deal Value: $" + workstationDeal.calculateValue());
        System.out.println("Enterprise Deal Value: $" + enterpriseDeal.calculateValue());

        // ========== STEP 7: Demonstrate Dynamic Modification and Automatic Propagation ==========
        // The Composite Pattern supports dynamic structure changes, and changes
        // automatically propagate through the tree.

        System.out.println("\n--- After Adding Discount to Laptop ---");
        laptop.setDiscount(new BigDecimal("200.00"));

        // IMPORTANT: Watch what happens here!
        // We modified the laptop object, which is a LEAF node buried deep in the tree:
        //   Enterprise Deal → Workstation Deal → Laptop
        //
        // When we generate the enterprise deal report, the new laptop value
        // automatically propagates up through the hierarchy:
        // 1. Laptop calculates its new value: (2×$1200) - $200 = $2200
        // 2. Workstation Deal recalculates: $2200 + $700 + $400 = $3300
        // 3. Enterprise Deal recalculates: $3300 + $450 + $3000 = $6750
        //
        // This happens WITHOUT any special code - it's the natural result of
        // the recursive delegation in the Composite Pattern!
        report.generateReport(enterpriseDeal);

        // ========== STEP 8: Demonstrate Dynamic Structure Removal ==========
        // We can also remove components from the tree at runtime.

        System.out.println("\n--- After Removing Server from Enterprise Deal ---");
        enterpriseDeal.removeComponent(server);

        // The server ($3000) is now removed from the enterprise deal.
        // New expected value: $3300 (workstation) + $450 (printer) = $3750
        report.generateReport(enterpriseDeal);

        // ========== Summary of Benefits Demonstrated ==========
        // 1. UNIFORM TREATMENT: Same interface for individual objects and compositions
        // 2. RECURSIVE COMPOSITION: Can nest composites to any depth
        // 3. TRANSPARENT COMPLEXITY: Client doesn't need to know the tree structure
        // 4. AUTOMATIC CALCULATION: Values propagate up the tree automatically
        // 5. DYNAMIC MODIFICATION: Can change the structure at runtime
        // 6. SIMPLIFIED CLIENT CODE: No conditional logic based on component type
    }
}