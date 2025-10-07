package com.chapman.edu.commissions.patterns.behavioral.visitor;

import com.chapman.edu.commissions.model.*;
import com.chapman.edu.commissions.patterns.behavioral.visitor.VisitorImplementation.*;
import com.chapman.edu.commissions.patterns.behavioral.visitor.VisitorStructure.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;



/**
 * VISITOR PATTERN - PRACTICAL USAGE EXAMPLES
 *
 * This class demonstrates various real-world scenarios and usage patterns for the
 * Visitor Pattern in the context of commission system operations.
 *
 * DEMONSTRATES:
 * 1. Single operation across multiple entity types
 * 2. Multiple operations on the same entity collection
 * 3. Filtering and conditional visiting
 * 4. Combining visitors for complex workflows
 * 5. Visitor chaining and composition
 * 6. Performance considerations with visitors
 *
 * KEY LEARNING POINTS:
 * - Visitors separate operations from data structures
 * - Easy to add new operations without modifying entities
 * - Visitors can accumulate state across visits
 * - Double dispatch enables type-specific behavior
 * - Visitors work well with composite patterns
 *
 */
public class VisitorUsage {

    /**
     * EXAMPLE 1: Basic Single-Purpose Visitor
     *
     * Demonstrates using one visitor for one specific operation.
     */
    public static void exampleBasicUsage() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         EXAMPLE 1: Basic Single-Purpose Visitor           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        // Create a collection of entities
        List<CommissionEntity> entities = new ArrayList<>();
        entities.add(createDeal("Software Deal", new BigDecimal("50000"), DealStatus.WON));
        entities.add(createDeal("Hardware Deal", new BigDecimal("75000"), DealStatus.OPEN));
        entities.add(createUser("Alice Johnson", UserRole.SALES_REP));

        System.out.println("Scenario: Generate a report for selected entities\n");

        // Create and apply visitor
        ReportVisitor visitor = new ReportVisitor();
        for (CommissionEntity entity : entities) {
            entity.accept(visitor);
        }

        System.out.println(visitor.getReport());

        System.out.println("💡 KEY OBSERVATION:");
        System.out.println("Report generation logic is centralized in the visitor.");
        System.out.println("Entity classes don't need to know about reporting.\n");
    }

    /**
     * EXAMPLE 2: Multiple Visitors on Same Data
     *
     * Shows how to apply different operations (visitors) to the same entity collection.
     */
    public static void exampleMultipleVisitors() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║      EXAMPLE 2: Multiple Visitors on Same Data           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        // Create entity collection
        List<CommissionEntity> entities = new ArrayList<>();
        entities.add(createDeal("Deal A", new BigDecimal("100000"), DealStatus.WON));
        entities.add(createDeal("Deal B", new BigDecimal("150000"), DealStatus.WON));
        entities.add(createDeal("Deal C", new BigDecimal("80000"), DealStatus.OPEN));

        System.out.println("Scenario: Validate, then collect statistics, then export\n");

        // Visitor 1: Validation
        System.out.println("Step 1: Validation");
        System.out.println("-".repeat(60));
        ValidationVisitor validationVisitor = new ValidationVisitor();
        for (CommissionEntity entity : entities) {
            entity.accept(validationVisitor);
        }
        System.out.println("Valid entities: " + validationVisitor.getValidEntityCount());
        System.out.println("Validation passed: " + validationVisitor.isValid());

        // Visitor 2: Statistics (only if validation passed)
        if (validationVisitor.isValid()) {
            System.out.println("\nStep 2: Statistics Collection");
            System.out.println("-".repeat(60));
            StatisticsVisitor statsVisitor = new StatisticsVisitor();
            for (CommissionEntity entity : entities) {
                entity.accept(statsVisitor);
            }
            System.out.println("Total deal value: $" + statsVisitor.getTotalDealValue());
            System.out.println("Won deal value: $" + statsVisitor.getWonDealValue());

            // Visitor 3: Export
            System.out.println("\nStep 3: CSV Export");
            System.out.println("-".repeat(60));
            CsvExportVisitor csvVisitor = new CsvExportVisitor();
            for (CommissionEntity entity : entities) {
                entity.accept(csvVisitor);
            }
            System.out.println(csvVisitor.getCsvData());
        }

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Applied 3 different operations to the same data without");
        System.out.println("modifying the entity classes. Each visitor is independent.\n");
    }

    /**
     * EXAMPLE 3: Conditional Visiting
     *
     * Demonstrates selectively visiting entities based on criteria.
     */
    public static void exampleConditionalVisiting() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         EXAMPLE 3: Conditional/Filtered Visiting         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        // Create mixed entity collection
        List<CommissionEntity> allEntities = new ArrayList<>();
        allEntities.add(createDeal("High Value Deal", new BigDecimal("200000"), DealStatus.WON));
        allEntities.add(createDeal("Low Value Deal", new BigDecimal("5000"), DealStatus.WON));
        allEntities.add(createDeal("Medium Deal", new BigDecimal("50000"), DealStatus.OPEN));
        allEntities.add(createUser("Bob Smith", UserRole.SALES_REP));
        allEntities.add(createUser("Carol White", UserRole.SALES_MANAGER));

        System.out.println("Scenario: Only visit high-value won deals\n");

        // Filter and visit
        StatisticsVisitor highValueStats = new StatisticsVisitor();
        int visitedCount = 0;

        for (CommissionEntity entity : allEntities) {
            // Only visit deals
            if (entity instanceof CommissionDeal) {
                CommissionDeal deal = (CommissionDeal) entity;
                // Only high-value won deals
                if (deal.getStatus() == DealStatus.WON &&
                    deal.getValue().compareTo(new BigDecimal("100000")) > 0) {
                    entity.accept(highValueStats);
                    visitedCount++;
                    System.out.println("✓ Visited: " + deal.getTitle() + " ($" + deal.getValue() + ")");
                }
            }
        }

        System.out.println("\nResults:");
        System.out.println("  Total entities in collection: " + allEntities.size());
        System.out.println("  Entities visited: " + visitedCount);
        System.out.println("  High-value won deals total: $" + highValueStats.getWonDealValue());

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Can selectively apply visitors to specific entities or");
        System.out.println("entities meeting certain criteria. Flexible filtering.\n");
    }

    /**
     * EXAMPLE 4: Custom Visitor for Specific Business Logic
     *
     * Shows how to create a specialized visitor for specific business needs.
     */
    public static void exampleCustomVisitor() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║    EXAMPLE 4: Custom Visitor for Commission Calculation  ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        /**
         * COMMISSION CALCULATOR VISITOR
         *
         * Custom visitor that calculates commissions based on won deals.
         * This is a new operation added WITHOUT modifying any existing classes.
         */
        class CommissionCalculatorVisitor implements CommissionEntityVisitor {
            private BigDecimal totalCommission = BigDecimal.ZERO;
            private final BigDecimal COMMISSION_RATE = new BigDecimal("0.10"); // 10%
            private List<String> calculations = new ArrayList<>();

            @Override
            public void visitDeal(CommissionDeal deal) {
                if (deal.getStatus() == DealStatus.WON) {
                    BigDecimal commission = deal.getValue().multiply(COMMISSION_RATE);
                    totalCommission = totalCommission.add(commission);
                    calculations.add(String.format("%s: $%s × 10%% = $%s",
                        deal.getTitle(), deal.getValue(), commission));
                }
            }

            @Override
            public void visitCommissionPlan(CommissionPlanEntity plan) {
                // Not relevant for this calculation
            }

            @Override
            public void visitUser(UserEntity user) {
                // Not relevant for this calculation
            }

            @Override
            public void visitDispute(DisputeEntity dispute) {
                // Could subtract disputed amounts if needed
            }

            public BigDecimal getTotalCommission() {
                return totalCommission;
            }

            public void printReport() {
                System.out.println("COMMISSION CALCULATION REPORT:");
                System.out.println("-".repeat(60));
                calculations.forEach(c -> System.out.println("  " + c));
                System.out.println("-".repeat(60));
                System.out.println("  TOTAL COMMISSION: $" + totalCommission);
            }
        }

        // Create deals
        List<CommissionEntity> deals = new ArrayList<>();
        deals.add(createDeal("Q1 Software License", new BigDecimal("80000"), DealStatus.WON));
        deals.add(createDeal("Q1 Hardware Sale", new BigDecimal("120000"), DealStatus.WON));
        deals.add(createDeal("Q1 Services", new BigDecimal("60000"), DealStatus.OPEN)); // Not won
        deals.add(createDeal("Q1 Training", new BigDecimal("40000"), DealStatus.WON));

        System.out.println("Scenario: Calculate total commissions for won deals\n");

        // Apply custom visitor
        CommissionCalculatorVisitor commissionCalc = new CommissionCalculatorVisitor();
        for (CommissionEntity entity : deals) {
            entity.accept(commissionCalc);
        }

        commissionCalc.printReport();

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Created a new business operation (commission calculation)");
        System.out.println("without touching the Deal class or any existing code!\n");
    }

    /**
     * EXAMPLE 5: Visitor for Data Transformation
     *
     * Shows how visitors can transform data structures.
     */
    public static void exampleDataTransformation() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║      EXAMPLE 5: Visitor for Data Transformation          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        /**
         * JSON EXPORT VISITOR
         *
         * Transforms entities into JSON format.
         */
        class JsonExportVisitor implements CommissionEntityVisitor {
            private List<String> jsonObjects = new ArrayList<>();

            @Override
            public void visitDeal(CommissionDeal deal) {
                String json = String.format(
                    "  {\"type\":\"Deal\",\"id\":\"%s\",\"title\":\"%s\",\"value\":%s,\"status\":\"%s\"}",
                    deal.getId(), deal.getTitle(), deal.getValue(), deal.getStatus()
                );
                jsonObjects.add(json);
            }

            @Override
            public void visitCommissionPlan(CommissionPlanEntity plan) {
                String json = String.format(
                    "  {\"type\":\"Plan\",\"id\":\"%s\",\"name\":\"%s\",\"status\":\"%s\",\"rules\":%d}",
                    plan.getId(), plan.getName(), plan.getStatus(), plan.getRuleCount()
                );
                jsonObjects.add(json);
            }

            @Override
            public void visitUser(UserEntity user) {
                String json = String.format(
                    "  {\"type\":\"User\",\"id\":\"%s\",\"name\":\"%s\",\"roles\":\"%s\"}",
                    user.getId(), user.getName(), user.getRoles()
                );
                jsonObjects.add(json);
            }

            @Override
            public void visitDispute(DisputeEntity dispute) {
                String json = String.format(
                    "  {\"type\":\"Dispute\",\"id\":\"%s\",\"calculationId\":\"%s\",\"status\":\"%s\"}",
                    dispute.getId(), dispute.getCalculationId(), dispute.getStatus()
                );
                jsonObjects.add(json);
            }

            public String getJsonArray() {
                StringBuilder sb = new StringBuilder("[\n");
                for (int i = 0; i < jsonObjects.size(); i++) {
                    sb.append(jsonObjects.get(i));
                    if (i < jsonObjects.size() - 1) {
                        sb.append(",");
                    }
                    sb.append("\n");
                }
                sb.append("]");
                return sb.toString();
            }
        }

        // Create mixed entities
        List<CommissionEntity> entities = new ArrayList<>();
        entities.add(createDeal("API Integration", new BigDecimal("45000"), DealStatus.WON));
        entities.add(createUser("Dave Brown", UserRole.SALES_REP));

        CommissionPlan plan = new CommissionPlan("Monthly Plan", java.util.Currency.getInstance("USD"));
        plan.setId("PLAN-001");
        plan.setStatus(PlanStatus.ACTIVE);
        plan.addRule(new CommissionRule("Base", new BigDecimal("0.10"), CommissionRule.RuleType.STANDARD));
        entities.add(new CommissionPlanEntity(plan));

        System.out.println("Scenario: Export entities to JSON format\n");

        // Apply JSON export visitor
        JsonExportVisitor jsonVisitor = new JsonExportVisitor();
        for (CommissionEntity entity : entities) {
            entity.accept(jsonVisitor);
        }

        System.out.println("JSON Output:");
        System.out.println(jsonVisitor.getJsonArray());

        System.out.println("\n💡 KEY OBSERVATION:");
        System.out.println("Visitor transformed domain objects into JSON representation");
        System.out.println("without adding JSON serialization code to the domain models.\n");
    }

    /**
     * EXAMPLE 6: Visitor Comparison - With vs Without Pattern
     *
     * Demonstrates the difference between using and not using the Visitor pattern.
     */
    public static void examplePatternComparison() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║    EXAMPLE 6: With Visitor vs Without Visitor Pattern    ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        List<CommissionEntity> entities = new ArrayList<>();
        entities.add(createDeal("Comparison Deal", new BigDecimal("100000"), DealStatus.WON));
        entities.add(createUser("Test User", UserRole.SALES_REP));

        System.out.println("WITHOUT VISITOR PATTERN (hypothetical):");
        System.out.println("-".repeat(60));
        System.out.println("// To add a new operation, you'd need to modify each class:");
        System.out.println("class Deal {");
        System.out.println("    // ... existing code ...");
        System.out.println("    public void generateReport() { /* report logic */ }");
        System.out.println("    public void validate() { /* validation logic */ }");
        System.out.println("    public void exportToCsv() { /* export logic */ }");
        System.out.println("    public void exportToJson() { /* export logic */ }");
        System.out.println("    // Every new operation = modify this class!");
        System.out.println("}");
        System.out.println();
        System.out.println("Problems:");
        System.out.println("  ✗ Violates Open/Closed Principle");
        System.out.println("  ✗ Violates Single Responsibility Principle");
        System.out.println("  ✗ Related operations scattered across classes");
        System.out.println("  ✗ Hard to maintain and test");
        System.out.println();

        System.out.println("\nWITH VISITOR PATTERN:");
        System.out.println("-".repeat(60));
        System.out.println("// Domain classes stay clean:");
        System.out.println("class Deal {");
        System.out.println("    // ... business logic only ...");
        System.out.println("    public void accept(Visitor v) { v.visitDeal(this); }");
        System.out.println("}");
        System.out.println();
        System.out.println("// Operations are in separate visitor classes:");
        System.out.println("class ReportVisitor implements Visitor { /* all report logic */ }");
        System.out.println("class ValidationVisitor implements Visitor { /* all validation */ }");
        System.out.println("class CsvExportVisitor implements Visitor { /* all CSV export */ }");
        System.out.println("class JsonExportVisitor implements Visitor { /* all JSON export */ }");
        System.out.println();

        // Demonstrate
        System.out.println("Demo: Add 2 operations without touching Deal/User classes:");
        System.out.println();

        ReportVisitor reportVisitor = new ReportVisitor();
        ValidationVisitor validationVisitor = new ValidationVisitor();

        for (CommissionEntity entity : entities) {
            entity.accept(reportVisitor);
            entity.accept(validationVisitor);
        }

        System.out.println("✓ Generated report");
        System.out.println("✓ Performed validation");
        System.out.println();

        System.out.println("Benefits:");
        System.out.println("  ✓ Follows Open/Closed Principle (open for extension)");
        System.out.println("  ✓ Follows Single Responsibility Principle");
        System.out.println("  ✓ Operations grouped in cohesive visitor classes");
        System.out.println("  ✓ Easy to add new operations");
        System.out.println("  ✓ Easy to test and maintain");
        System.out.println();
    }

    /**
     * MAIN DEMONSTRATION
     *
     * Runs all examples to show different usage patterns.
     */
    public static void main(String[] args) {
        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                                                           ║");
        System.out.println("║      VISITOR PATTERN - COMPREHENSIVE USAGE EXAMPLES       ║");
        System.out.println("║                                                           ║");
        System.out.println("║  Demonstrates real-world commission system operations     ║");
        System.out.println("║                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("\n");

        // Run all examples
        exampleBasicUsage();
        pause();

        exampleMultipleVisitors();
        pause();

        exampleConditionalVisiting();
        pause();

        exampleCustomVisitor();
        pause();

        exampleDataTransformation();
        pause();

        examplePatternComparison();

        // Summary
        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                    KEY TAKEAWAYS                          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("1. SEPARATION OF CONCERNS");
        System.out.println("   → Operations are separated from data structures");
        System.out.println("   → Domain models stay clean and focused");
        System.out.println();
        System.out.println("2. EASY TO EXTEND");
        System.out.println("   → Add new operations by creating new visitors");
        System.out.println("   → No need to modify existing element classes");
        System.out.println();
        System.out.println("3. DOUBLE DISPATCH");
        System.out.println("   → element.accept(visitor) - element knows visitor");
        System.out.println("   → visitor.visitElement(element) - visitor knows element type");
        System.out.println("   → Enables type-specific behavior without instanceof checks");
        System.out.println();
        System.out.println("4. OPERATION GROUPING");
        System.out.println("   → All report logic in ReportVisitor");
        System.out.println("   → All validation logic in ValidationVisitor");
        System.out.println("   → All export logic in ExportVisitor");
        System.out.println();
        System.out.println("5. STATEFUL OPERATIONS");
        System.out.println("   → Visitors can maintain state across visits");
        System.out.println("   → Perfect for aggregations, statistics, transformations");
        System.out.println();
        System.out.println("6. OPEN/CLOSED PRINCIPLE");
        System.out.println("   → Open for extension (add new visitors)");
        System.out.println("   → Closed for modification (elements unchanged)");
        System.out.println();
        System.out.println("7. WHEN TO USE");
        System.out.println("   → Object structure rarely changes");
        System.out.println("   → Need to add many operations");
        System.out.println("   → Want to gather related operations");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
    }

    /**
     * Helper methods to create entities for examples
     */
    private static CommissionDeal createDeal(String title, BigDecimal value, DealStatus status) {
        Deal deal = new Deal(title, value, "REP-" + System.currentTimeMillis());
        deal.setId("DEAL-" + System.currentTimeMillis());
        deal.setStatus(status);
        if (status == DealStatus.WON) {
            deal.setCloseDate(LocalDate.now());
        }
        return new CommissionDeal(deal);
    }

    private static UserEntity createUser(String name, UserRole role) {
        User user = new User();
        user.setId("USER-" + System.currentTimeMillis());
        String[] parts = name.split(" ");
        user.setFirstName(parts[0]);
        user.setLastName(parts.length > 1 ? parts[1] : "");
        user.setEmail(name.replace(" ", ".").toLowerCase() + "@example.com");
        user.addRole(role);
        return new UserEntity(user);
    }

    /**
     * Pause between examples for readability
     */
    private static void pause() {
        System.out.println("\n[Press Enter to continue to next example...]");
        System.out.println("─".repeat(60) + "\n");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}