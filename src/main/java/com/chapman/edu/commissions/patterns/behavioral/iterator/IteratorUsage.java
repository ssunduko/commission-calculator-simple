package com.chapman.edu.commissions.patterns.behavioral.iterator;

import com.chapman.edu.commissions.patterns.behavioral.iterator.IteratorStructure.*;

import com.chapman.edu.commissions.model.*;
import com.chapman.edu.commissions.patterns.behavioral.iterator.IteratorImplementation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * ITERATOR PATTERN - PRACTICAL USAGE EXAMPLES
 *
 * This class demonstrates various real-world scenarios and usage patterns for the
 * Iterator Pattern in commission data traversal and reporting.
 *
 * DEMONSTRATES:
 * 1. Report generation with filtered iteration
 * 2. Pipeline processing with multiple iterators
 * 3. Pagination with iterator state management
 * 4. Concurrent iteration over same collection
 * 5. Chained iteration with transformation
 * 6. Performance optimization with lazy iteration
 *
 * KEY LEARNING POINTS:
 * - Iterator provides uniform interface for traversal
 * - Different iteration strategies for same data
 * - Encapsulation of complex filtering logic
 * - Support for multiple simultaneous traversals
 * - Easy to extend with new iteration types
 *
 */
public class IteratorUsage {

    /**
     * EXAMPLE 1: Report Generation with Filters
     *
     * Demonstrates using different iterators to generate targeted reports.
     */
    public static void exampleReportGeneration() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║     EXAMPLE 1: Report Generation with Filters             ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        System.out.println("Scenario: Generate different sales reports from same dataset\\n");

        DealCollection deals = createLargeDataset();
        System.out.println("Total deals in system: " + deals.size() + "\\n");

        // Report 1: Won Deals Summary
        System.out.println("=== REPORT 1: Won Deals Summary ===");
        CommissionIterator<Deal> wonDeals = deals.createStatusIterator(DealStatus.WON);
        BigDecimal totalWonValue = BigDecimal.ZERO;
        int wonCount = 0;

        while (wonDeals.hasNext()) {
            Deal deal = wonDeals.next();
            totalWonValue = totalWonValue.add(deal.getValue());
            wonCount++;
        }
        System.out.println("Won Deals: " + wonCount);
        System.out.println("Total Value: $" + totalWonValue);
        System.out.println("Average Deal Size: $" +
            (wonCount > 0 ? totalWonValue.divide(new BigDecimal(wonCount), 2,
                java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO));

        // Report 2: High-Value Pipeline
        System.out.println("\\n=== REPORT 2: High-Value Pipeline ($50k+) ===");
        CommissionIterator<Deal> highValue = deals.createValueRangeIterator(
            new BigDecimal("50000"), new BigDecimal("10000000")
        );
        System.out.println("High-value deals:");
        while (highValue.hasNext()) {
            Deal deal = highValue.next();
            System.out.println("  → " + deal.getTitle() + " [$" + deal.getValue() +
                             "] [" + deal.getStatus() + "]");
        }

        // Report 3: Recent Activity
        System.out.println("\\n=== REPORT 3: Recent Activity (Last 30 Days) ===");
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        CommissionIterator<Deal> recentDeals = deals.createDateIterator(thirtyDaysAgo);
        int recentCount = 0;
        while (recentDeals.hasNext()) {
            Deal deal = recentDeals.next();
            System.out.println("  → " + deal.getTitle() + " [" + deal.getCloseDate() + "]");
            recentCount++;
        }
        System.out.println("Recent deals: " + recentCount);

        System.out.println("\\n💡 KEY OBSERVATION:");
        System.out.println("Same dataset, three different reports using different iterators.");
        System.out.println("Each iterator encapsulates its own filtering logic.\\n");
    }

    /**
     * EXAMPLE 2: Pipeline Processing
     *
     * Shows processing deals through multiple stages using iterators.
     */
    public static void examplePipelineProcessing() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║       EXAMPLE 2: Pipeline Processing                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        System.out.println("Scenario: Process won deals through commission calculation pipeline\\n");

        DealCollection deals = createLargeDataset();

        System.out.println("Pipeline: Filter Won → Sort by Value → Calculate Commissions\\n");

        // Stage 1: Filter won deals
        System.out.println("Stage 1: Filtering won deals...");
        CommissionIterator<Deal> wonDeals = deals.createStatusIterator(DealStatus.WON);
        int wonCount = 0;
        while (wonDeals.hasNext()) {
            wonDeals.next();
            wonCount++;
        }
        System.out.println("  ✓ Found " + wonCount + " won deals");

        // Stage 2: Sort by value
        System.out.println("\\nStage 2: Sorting by value...");
        CommissionIterator<Deal> sortedDeals = deals.createValueSortedIterator();
        System.out.println("  ✓ Deals sorted (highest first)");

        // Stage 3: Calculate commissions
        System.out.println("\\nStage 3: Calculating commissions...");
        BigDecimal COMMISSION_RATE = new BigDecimal("0.10"); // 10%
        BigDecimal totalCommissions = BigDecimal.ZERO;
        int processed = 0;

        while (sortedDeals.hasNext()) {
            Deal deal = sortedDeals.next();
            if (deal.getStatus() == DealStatus.WON) {
                BigDecimal commission = deal.getValue().multiply(COMMISSION_RATE);
                totalCommissions = totalCommissions.add(commission);
                processed++;

                if (processed <= 3) {  // Show first 3
                    System.out.println("  → " + deal.getTitle() + ": $" + commission);
                }
            }
        }

        System.out.println("  ... (" + (processed - 3) + " more)");
        System.out.println("\\n✓ Pipeline complete!");
        System.out.println("  Total commissions: $" + totalCommissions);

        System.out.println("\\n💡 KEY OBSERVATION:");
        System.out.println("Each pipeline stage uses different iterator.");
        System.out.println("Iterators can be chained for multi-stage processing.\\n");
    }

    /**
     * EXAMPLE 3: Pagination
     *
     * Demonstrates implementing pagination using iterator state.
     */
    public static void examplePagination() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║          EXAMPLE 3: Pagination                            ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        System.out.println("Scenario: Display deals in pages (5 per page)\\n");

        DealCollection deals = createLargeDataset();
        CommissionIterator<Deal> iterator = deals.createIterator();

        int pageSize = 5;
        int pageNumber = 1;

        while (iterator.hasNext()) {
            System.out.println("=== Page " + pageNumber + " ===");
            int itemsOnPage = 0;

            while (iterator.hasNext() && itemsOnPage < pageSize) {
                Deal deal = iterator.next();
                System.out.println("  " + (itemsOnPage + 1) + ". " + deal.getTitle() +
                                 " [$" + deal.getValue() + "]");
                itemsOnPage++;
            }

            int remaining = iterator.remaining();
            if (remaining > 0) {
                System.out.println("\\n(" + remaining + " more items remaining)");
                System.out.println("[Next Page]\\n");
            }

            pageNumber++;
        }

        System.out.println("\\n💡 KEY OBSERVATION:");
        System.out.println("Iterator maintains position for pagination.");
        System.out.println("remaining() method shows items left for UI feedback.\\n");
    }

    /**
     * EXAMPLE 4: Concurrent Iteration
     *
     * Shows multiple iterators traversing same collection simultaneously.
     */
    public static void exampleConcurrentIteration() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║      EXAMPLE 4: Concurrent Iteration                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        System.out.println("Scenario: Multiple reports running simultaneously\\n");

        DealCollection deals = createLargeDataset();

        // Create three independent iterators
        CommissionIterator<Deal> iter1 = deals.createStatusIterator(DealStatus.WON);
        CommissionIterator<Deal> iter2 = deals.createStatusIterator(DealStatus.OPEN);
        CommissionIterator<Deal> iter3 = deals.createStatusIterator(DealStatus.LOST);

        System.out.println("Starting three concurrent traversals...");
        System.out.println("Iterator 1 (Won):  " + iter1.remaining() + " deals");
        System.out.println("Iterator 2 (Open): " + iter2.remaining() + " deals");
        System.out.println("Iterator 3 (Lost): " + iter3.remaining() + " deals");

        // Interleave iterations
        System.out.println("\\nInterleaved processing:");
        for (int i = 0; i < 3; i++) {
            if (iter1.hasNext()) {
                System.out.println("  Iter1 → " + iter1.next().getTitle());
            }
            if (iter2.hasNext()) {
                System.out.println("  Iter2 → " + iter2.next().getTitle());
            }
            if (iter3.hasNext()) {
                System.out.println("  Iter3 → " + iter3.next().getTitle());
            }
        }

        System.out.println("\\n💡 KEY OBSERVATION:");
        System.out.println("Each iterator maintains independent state.");
        System.out.println("Can process same collection from multiple angles simultaneously.\\n");
    }

    /**
     * EXAMPLE 5: Iterator Reset and Reuse
     *
     * Demonstrates resetting iterators for multiple passes.
     */
    public static void exampleIteratorReset() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║       EXAMPLE 5: Iterator Reset and Reuse                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        System.out.println("Scenario: Two-pass processing with iterator reset\\n");

        DealCollection deals = createLargeDataset();
        CommissionIterator<Deal> wonDeals = deals.createStatusIterator(DealStatus.WON);

        // First pass: Calculate total
        System.out.println("=== Pass 1: Calculate Total ===");
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        while (wonDeals.hasNext()) {
            total = total.add(wonDeals.next().getValue());
            count++;
        }
        System.out.println("Total value: $" + total);
        System.out.println("Count: " + count);
        BigDecimal average = total.divide(new BigDecimal(count), 2,
            java.math.RoundingMode.HALF_UP);
        System.out.println("Average: $" + average);

        // Reset iterator
        System.out.println("\\n[Resetting iterator...]\\n");
        wonDeals.reset();

        // Second pass: Find above-average deals
        System.out.println("=== Pass 2: Find Above-Average Deals ===");
        int aboveAverage = 0;
        while (wonDeals.hasNext()) {
            Deal deal = wonDeals.next();
            if (deal.getValue().compareTo(average) > 0) {
                System.out.println("  → " + deal.getTitle() + " [$" + deal.getValue() + "]");
                aboveAverage++;
            }
        }
        System.out.println("\\nDeals above average: " + aboveAverage + " of " + count);

        System.out.println("\\n💡 KEY OBSERVATION:");
        System.out.println("reset() allows reusing same iterator for multiple passes.");
        System.out.println("Useful for multi-step algorithms on same filtered data.\\n");
    }

    /**
     * EXAMPLE 6: Custom Iteration Strategy
     *
     * Shows implementing custom business logic in iterator.
     */
    public static void exampleCustomStrategy() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║      EXAMPLE 6: Custom Iteration Strategy                ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        System.out.println("Scenario: Iterate deals with custom sales rep filter\\n");

        DealCollection deals = createLargeDataset();

        // Create custom iterator (in real code, would be a proper class)
        System.out.println("Creating custom iterator for high-value won deals...");

        class HighValueWonIterator implements CommissionIterator<Deal> {
            private final CommissionIterator<Deal> wonDeals;
            private final BigDecimal threshold = new BigDecimal("40000");
            private Deal nextDeal = null;
            private boolean nextSet = false;

            public HighValueWonIterator(DealCollection collection) {
                this.wonDeals = collection.createStatusIterator(DealStatus.WON);
            }

            @Override
            public boolean hasNext() {
                if (!nextSet) {
                    nextDeal = findNext();
                    nextSet = true;
                }
                return nextDeal != null;
            }

            @Override
            public Deal next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                Deal result = nextDeal;
                nextDeal = null;
                nextSet = false;
                return result;
            }

            @Override
            public int remaining() {
                int count = 0;
                while (hasNext()) {
                    next();
                    count++;
                }
                wonDeals.reset();
                return count;
            }

            @Override
            public void reset() {
                wonDeals.reset();
                nextDeal = null;
                nextSet = false;
            }

            private Deal findNext() {
                while (wonDeals.hasNext()) {
                    Deal deal = wonDeals.next();
                    if (deal.getValue().compareTo(threshold) >= 0) {
                        return deal;
                    }
                }
                return null;
            }
        }

        CommissionIterator<Deal> customIterator = new HighValueWonIterator(deals);

        System.out.println("High-value won deals ($40k+):\\n");
        while (customIterator.hasNext()) {
            Deal deal = customIterator.next();
            System.out.println("  → " + deal.getTitle() + " [$" + deal.getValue() + "]");
        }

        System.out.println("\\n💡 KEY OBSERVATION:");
        System.out.println("Custom iterators encapsulate complex business logic.");
        System.out.println("Can compose iterators (custom wraps existing won deals iterator).\\n");
    }

    /**
     * MAIN DEMONSTRATION
     *
     * Runs all examples.
     */
    public static void main(String[] args) {
        System.out.println("\\n");
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                                                           ║");
        System.out.println("║       ITERATOR PATTERN - COMPREHENSIVE EXAMPLES           ║");
        System.out.println("║                                                           ║");
        System.out.println("║  Demonstrates practical iterator usage patterns          ║");
        System.out.println("║                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("\\n");

        exampleReportGeneration();
        pause();

        examplePipelineProcessing();
        pause();

        examplePagination();
        pause();

        exampleConcurrentIteration();
        pause();

        exampleIteratorReset();
        pause();

        exampleCustomStrategy();

        // Summary
        System.out.println("\\n");
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                    KEY TAKEAWAYS                          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("1. UNIFORM INTERFACE");
        System.out.println("   → hasNext() and next() work for all iterators");
        System.out.println("   → Client code independent of iteration strategy");
        System.out.println();
        System.out.println("2. ENCAPSULATION");
        System.out.println("   → Iterator hides collection structure");
        System.out.println("   → Filtering logic encapsulated in iterator");
        System.out.println();
        System.out.println("3. MULTIPLE TRAVERSALS");
        System.out.println("   → Multiple iterators over same collection");
        System.out.println("   → Each maintains independent state");
        System.out.println();
        System.out.println("4. FLEXIBLE STRATEGIES");
        System.out.println("   → Filter by status, value, date");
        System.out.println("   → Sort, skip, compose iterators");
        System.out.println();
        System.out.println("5. REUSABILITY");
        System.out.println("   → reset() for multiple passes");
        System.out.println("   → Same iterator, different uses");
        System.out.println();
        System.out.println("6. EXTENSIBILITY");
        System.out.println("   → Easy to add new iterator types");
        System.out.println("   → Custom iterators for specific needs");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
    }

    // Helper methods

    private static DealCollection createLargeDataset() {
        DealCollection deals = new DealCollection();

        deals.addDeal(createDeal("Small Deal 1", "5000", DealStatus.WON));
        deals.addDeal(createDeal("Medium Deal 1", "25000", DealStatus.WON));
        deals.addDeal(createDeal("Large Deal 1", "75000", DealStatus.WON));
        deals.addDeal(createDeal("Pending Small", "8000", DealStatus.OPEN));
        deals.addDeal(createDeal("Pending Medium", "30000", DealStatus.OPEN));
        deals.addDeal(createDeal("Lost Deal 1", "50000", DealStatus.LOST));
        deals.addDeal(createDeal("Mega Deal", "150000", DealStatus.WON));
        deals.addDeal(createDeal("Small Deal 2", "7500", DealStatus.WON));
        deals.addDeal(createDeal("Lost Deal 2", "20000", DealStatus.LOST));
        deals.addDeal(createDeal("Medium Deal 2", "45000", DealStatus.WON));

        return deals;
    }

    private static Deal createDeal(String title, String value, DealStatus status) {
        Deal deal = new Deal(title, new BigDecimal(value), "REP-123");
        deal.setStatus(status);
        deal.setCloseDate(LocalDate.now().minusDays((long)(Math.random() * 90)));
        return deal;
    }

    private static void pause() {
        System.out.println("\\n[Press Enter to continue...]");
        System.out.println("─".repeat(60) + "\\n");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}