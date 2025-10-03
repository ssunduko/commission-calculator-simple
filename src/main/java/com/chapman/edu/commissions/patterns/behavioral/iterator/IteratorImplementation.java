package com.chapman.edu.commissions.patterns.behavioral.iterator;

import com.chapman.edu.commissions.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * ITERATOR PATTERN - COMMISSION SYSTEM IMPLEMENTATION
 *
 * This implementation demonstrates the Iterator Pattern through specialized iterators
 * for traversing commission data with filtering, sorting, and transformation capabilities.
 *
 * REAL-WORLD SCENARIOS:
 * 1. Deal Pipeline Traversal: Iterate through deals by status, value range, date
 * 2. Commission Plan Analysis: Traverse active plans, expired plans, specific criteria
 * 3. Commission Tier Navigation: Iterate through tiers in order
 * 4. Report Generation: Filtered iteration for specific data subsets
 * 5. Batch Processing: Process subsets of data with custom iteration logic
 *
 * KEY BENEFITS:
 * - Uniform interface for different traversal strategies
 * - Hide complex filtering/sorting logic from clients
 * - Support multiple simultaneous traversals
 * - Easy to add new iteration strategies
 * - Decouple traversal from data structure
 *
 * @author Commission Calculator Educational Project
 */
public class IteratorImplementation {

    /**
     * GENERIC ITERATOR INTERFACE
     *
     * Base interface for all commission system iterators.
     * Extends with additional metadata methods useful for reporting.
     */
    public interface CommissionIterator<T> {
        boolean hasNext();
        T next();
        int remaining();  // Additional: How many items left
        void reset();     // Additional: Reset to beginning
    }

    /**
     * AGGREGATE: Deal Collection
     *
     * Collection of deals with multiple iterator types for different traversal needs.
     *
     * PROVIDES ITERATORS FOR:
     * - All deals (insertion order)
     * - Deals by status
     * - Deals by value range
     * - Deals by date range
     * - Active deals only
     */
    public static class DealCollection {
        private final List<Deal> deals;

        public DealCollection() {
            this.deals = new ArrayList<>();
        }

        public void addDeal(Deal deal) {
            deals.add(deal);
            System.out.println("  Added deal: " + deal.getTitle() +
                             " [$" + deal.getValue() + "]");
        }

        public int size() {
            return deals.size();
        }

        // Iterator factory methods

        /**
         * Create iterator for all deals.
         */
        public CommissionIterator<Deal> createIterator() {
            return new AllDealsIterator();
        }

        /**
         * Create iterator for deals with specific status.
         */
        public CommissionIterator<Deal> createStatusIterator(DealStatus status) {
            return new StatusFilterIterator(status);
        }

        /**
         * Create iterator for deals within value range.
         */
        public CommissionIterator<Deal> createValueRangeIterator(
                BigDecimal minValue, BigDecimal maxValue) {
            return new ValueRangeIterator(minValue, maxValue);
        }

        /**
         * Create iterator for deals closed after a date.
         */
        public CommissionIterator<Deal> createDateIterator(LocalDate afterDate) {
            return new DateFilterIterator(afterDate);
        }

        /**
         * Create iterator sorted by value (descending).
         */
        public CommissionIterator<Deal> createValueSortedIterator() {
            return new ValueSortedIterator();
        }

        /**
         * CONCRETE ITERATOR: All Deals
         *
         * Simple forward iteration through all deals.
         */
        private class AllDealsIterator implements CommissionIterator<Deal> {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < deals.size();
            }

            @Override
            public Deal next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more deals");
                }
                return deals.get(currentIndex++);
            }

            @Override
            public int remaining() {
                return deals.size() - currentIndex;
            }

            @Override
            public void reset() {
                currentIndex = 0;
            }
        }

        /**
         * CONCRETE ITERATOR: Status Filter
         *
         * Iterates only through deals with specific status.
         */
        private class StatusFilterIterator implements CommissionIterator<Deal> {
            private final DealStatus targetStatus;
            private int currentIndex = 0;
            private Deal nextDeal = null;
            private boolean nextSet = false;

            public StatusFilterIterator(DealStatus status) {
                this.targetStatus = status;
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
                    throw new NoSuchElementException("No more deals with status " + targetStatus);
                }
                Deal result = nextDeal;
                nextDeal = null;
                nextSet = false;
                return result;
            }

            @Override
            public int remaining() {
                int count = 0;
                int savedIndex = currentIndex;
                while (findNext() != null) {
                    count++;
                }
                currentIndex = savedIndex;
                return count;
            }

            @Override
            public void reset() {
                currentIndex = 0;
                nextDeal = null;
                nextSet = false;
            }

            private Deal findNext() {
                while (currentIndex < deals.size()) {
                    Deal deal = deals.get(currentIndex++);
                    if (deal.getStatus() == targetStatus) {
                        return deal;
                    }
                }
                return null;
            }
        }

        /**
         * CONCRETE ITERATOR: Value Range
         *
         * Iterates through deals within a value range.
         */
        private class ValueRangeIterator implements CommissionIterator<Deal> {
            private final BigDecimal minValue;
            private final BigDecimal maxValue;
            private int currentIndex = 0;
            private Deal nextDeal = null;
            private boolean nextSet = false;

            public ValueRangeIterator(BigDecimal minValue, BigDecimal maxValue) {
                this.minValue = minValue;
                this.maxValue = maxValue;
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
                    throw new NoSuchElementException("No more deals in range");
                }
                Deal result = nextDeal;
                nextDeal = null;
                nextSet = false;
                return result;
            }

            @Override
            public int remaining() {
                int count = 0;
                int savedIndex = currentIndex;
                while (findNext() != null) {
                    count++;
                }
                currentIndex = savedIndex;
                return count;
            }

            @Override
            public void reset() {
                currentIndex = 0;
                nextDeal = null;
                nextSet = false;
            }

            private Deal findNext() {
                while (currentIndex < deals.size()) {
                    Deal deal = deals.get(currentIndex++);
                    if (deal.getValue().compareTo(minValue) >= 0 &&
                        deal.getValue().compareTo(maxValue) <= 0) {
                        return deal;
                    }
                }
                return null;
            }
        }

        /**
         * CONCRETE ITERATOR: Date Filter
         *
         * Iterates through deals closed after a specific date.
         */
        private class DateFilterIterator implements CommissionIterator<Deal> {
            private final LocalDate afterDate;
            private int currentIndex = 0;
            private Deal nextDeal = null;
            private boolean nextSet = false;

            public DateFilterIterator(LocalDate afterDate) {
                this.afterDate = afterDate;
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
                    throw new NoSuchElementException("No more recent deals");
                }
                Deal result = nextDeal;
                nextDeal = null;
                nextSet = false;
                return result;
            }

            @Override
            public int remaining() {
                int count = 0;
                int savedIndex = currentIndex;
                while (findNext() != null) {
                    count++;
                }
                currentIndex = savedIndex;
                return count;
            }

            @Override
            public void reset() {
                currentIndex = 0;
                nextDeal = null;
                nextSet = false;
            }

            private Deal findNext() {
                while (currentIndex < deals.size()) {
                    Deal deal = deals.get(currentIndex++);
                    if (deal.getCloseDate() != null &&
                        deal.getCloseDate().isAfter(afterDate)) {
                        return deal;
                    }
                }
                return null;
            }
        }

        /**
         * CONCRETE ITERATOR: Value Sorted
         *
         * Iterates through deals sorted by value (descending).
         * Creates sorted copy for iteration.
         */
        private class ValueSortedIterator implements CommissionIterator<Deal> {
            private final List<Deal> sortedDeals;
            private int currentIndex = 0;

            public ValueSortedIterator() {
                // Create sorted copy
                this.sortedDeals = new ArrayList<>(deals);
                this.sortedDeals.sort((d1, d2) ->
                    d2.getValue().compareTo(d1.getValue())  // Descending
                );
            }

            @Override
            public boolean hasNext() {
                return currentIndex < sortedDeals.size();
            }

            @Override
            public Deal next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more deals");
                }
                return sortedDeals.get(currentIndex++);
            }

            @Override
            public int remaining() {
                return sortedDeals.size() - currentIndex;
            }

            @Override
            public void reset() {
                currentIndex = 0;
            }
        }
    }

    /**
     * AGGREGATE: Commission Plan Repository
     *
     * Collection of commission plans with specialized iterators.
     */
    public static class CommissionPlanRepository {
        private final List<CommissionPlan> plans;

        public CommissionPlanRepository() {
            this.plans = new ArrayList<>();
        }

        public void addPlan(CommissionPlan plan) {
            plans.add(plan);
        }

        public int size() {
            return plans.size();
        }

        /**
         * Iterate through active plans only.
         */
        public CommissionIterator<CommissionPlan> createActiveIterator() {
            return new ActivePlansIterator();
        }

        /**
         * Iterate through plans effective on a specific date.
         */
        public CommissionIterator<CommissionPlan> createEffectiveOnIterator(LocalDate date) {
            return new EffectiveDateIterator(date);
        }

        /**
         * CONCRETE ITERATOR: Active Plans
         */
        private class ActivePlansIterator implements CommissionIterator<CommissionPlan> {
            private int currentIndex = 0;
            private CommissionPlan nextPlan = null;
            private boolean nextSet = false;

            @Override
            public boolean hasNext() {
                if (!nextSet) {
                    nextPlan = findNext();
                    nextSet = true;
                }
                return nextPlan != null;
            }

            @Override
            public CommissionPlan next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more active plans");
                }
                CommissionPlan result = nextPlan;
                nextPlan = null;
                nextSet = false;
                return result;
            }

            @Override
            public int remaining() {
                return (int) plans.stream()
                    .filter(p -> p.getStatus() == PlanStatus.ACTIVE)
                    .count();
            }

            @Override
            public void reset() {
                currentIndex = 0;
                nextPlan = null;
                nextSet = false;
            }

            private CommissionPlan findNext() {
                while (currentIndex < plans.size()) {
                    CommissionPlan plan = plans.get(currentIndex++);
                    if (plan.getStatus() == PlanStatus.ACTIVE) {
                        return plan;
                    }
                }
                return null;
            }
        }

        /**
         * CONCRETE ITERATOR: Effective Date
         */
        private class EffectiveDateIterator implements CommissionIterator<CommissionPlan> {
            private final LocalDate targetDate;
            private int currentIndex = 0;
            private CommissionPlan nextPlan = null;
            private boolean nextSet = false;

            public EffectiveDateIterator(LocalDate date) {
                this.targetDate = date;
            }

            @Override
            public boolean hasNext() {
                if (!nextSet) {
                    nextPlan = findNext();
                    nextSet = true;
                }
                return nextPlan != null;
            }

            @Override
            public CommissionPlan next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more effective plans");
                }
                CommissionPlan result = nextPlan;
                nextPlan = null;
                nextSet = false;
                return result;
            }

            @Override
            public int remaining() {
                return (int) plans.stream()
                    .filter(p -> p.isActiveOn(targetDate))
                    .count();
            }

            @Override
            public void reset() {
                currentIndex = 0;
                nextPlan = null;
                nextSet = false;
            }

            private CommissionPlan findNext() {
                while (currentIndex < plans.size()) {
                    CommissionPlan plan = plans.get(currentIndex++);
                    if (plan.isActiveOn(targetDate)) {
                        return plan;
                    }
                }
                return null;
            }
        }
    }

    /**
     * BONUS: Composite Iterator
     *
     * Iterates through multiple collections sequentially.
     * Useful for traversing related data across different sources.
     */
    public static class CompositeIterator<T> implements CommissionIterator<T> {
        private final List<CommissionIterator<T>> iterators;
        private int currentIteratorIndex = 0;

        public CompositeIterator(List<CommissionIterator<T>> iterators) {
            this.iterators = new ArrayList<>(iterators);
        }

        @Override
        public boolean hasNext() {
            // Find next iterator with elements
            while (currentIteratorIndex < iterators.size()) {
                if (iterators.get(currentIteratorIndex).hasNext()) {
                    return true;
                }
                currentIteratorIndex++;
            }
            return false;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }
            return iterators.get(currentIteratorIndex).next();
        }

        @Override
        public int remaining() {
            int total = 0;
            for (int i = currentIteratorIndex; i < iterators.size(); i++) {
                total += iterators.get(i).remaining();
            }
            return total;
        }

        @Override
        public void reset() {
            currentIteratorIndex = 0;
            for (CommissionIterator<T> iterator : iterators) {
                iterator.reset();
            }
        }
    }

    /**
     * DEMONSTRATION
     *
     * Shows the Iterator pattern in action with commission data.
     */
    public static void main(String[] args) {
        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                                                           ║");
        System.out.println("║   ITERATOR PATTERN - COMMISSION SYSTEM IMPLEMENTATION     ║");
        System.out.println("║                                                           ║");
        System.out.println("║  Demonstrates specialized iterators for commission data  ║");
        System.out.println("║                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("\n");

        demonstrateDealIterators();
        demonstratePlanIterators();
        demonstrateCompositeIterator();

        System.out.println("\n");
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                    PATTERN SUMMARY                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("ITERATOR PATTERN COMPONENTS:");
        System.out.println("  • Iterator: CommissionIterator<T> interface");
        System.out.println("  • ConcreteIterators: Status, Value, Date filters");
        System.out.println("  • Aggregate: DealCollection, PlanRepository");
        System.out.println();
        System.out.println("KEY BENEFITS:");
        System.out.println("  ✓ Uniform traversal interface");
        System.out.println("  ✓ Multiple iteration strategies");
        System.out.println("  ✓ Encapsulated traversal logic");
        System.out.println("  ✓ Concurrent iterations supported");
        System.out.println("  ✓ Easy to add new iterators");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
    }

    private static void demonstrateDealIterators() {
        System.out.println("EXAMPLE 1: Deal Collection Iterators\n");
        System.out.println("=".repeat(60));

        DealCollection deals = createSampleDeals();

        // All deals
        System.out.println("\n--- All Deals ---");
        CommissionIterator<Deal> allDeals = deals.createIterator();
        System.out.println("Total: " + allDeals.remaining());
        while (allDeals.hasNext()) {
            Deal deal = allDeals.next();
            System.out.println("  → " + deal.getTitle() + " [$" + deal.getValue() +
                             "] [" + deal.getStatus() + "]");
        }

        // Won deals only
        System.out.println("\n--- Won Deals Only ---");
        CommissionIterator<Deal> wonDeals = deals.createStatusIterator(DealStatus.WON);
        System.out.println("Won deals: " + wonDeals.remaining());
        while (wonDeals.hasNext()) {
            Deal deal = wonDeals.next();
            System.out.println("  → " + deal.getTitle() + " [$" + deal.getValue() + "]");
        }

        // High value deals
        System.out.println("\n--- High Value Deals ($50k+) ---");
        CommissionIterator<Deal> highValue = deals.createValueRangeIterator(
            new BigDecimal("50000"), new BigDecimal("1000000")
        );
        while (highValue.hasNext()) {
            Deal deal = highValue.next();
            System.out.println("  → " + deal.getTitle() + " [$" + deal.getValue() + "]");
        }

        // Sorted by value
        System.out.println("\n--- Deals Sorted by Value (Highest First) ---");
        CommissionIterator<Deal> sorted = deals.createValueSortedIterator();
        while (sorted.hasNext()) {
            Deal deal = sorted.next();
            System.out.println("  → " + deal.getTitle() + " [$" + deal.getValue() + "]");
        }

        System.out.println("\n" + "=".repeat(60) + "\n");
    }

    private static void demonstratePlanIterators() {
        System.out.println("EXAMPLE 2: Commission Plan Iterators\n");
        System.out.println("=".repeat(60));

        CommissionPlanRepository repo = createSamplePlans();

        System.out.println("\n--- Active Plans Only ---");
        CommissionIterator<CommissionPlan> activePlans = repo.createActiveIterator();
        System.out.println("Active plans: " + activePlans.remaining());
        while (activePlans.hasNext()) {
            CommissionPlan plan = activePlans.next();
            System.out.println("  → " + plan.getName() + " [" + plan.getStatus() + "]");
        }

        System.out.println("\n--- Plans Effective on 2024-02-15 ---");
        LocalDate targetDate = LocalDate.of(2024, 2, 15);
        CommissionIterator<CommissionPlan> effectivePlans =
            repo.createEffectiveOnIterator(targetDate);
        while (effectivePlans.hasNext()) {
            CommissionPlan plan = effectivePlans.next();
            System.out.println("  → " + plan.getName() +
                             " [" + plan.getEffectiveStartDate() + " to " + plan.getEffectiveEndDate() + "]");
        }

        System.out.println("\n" + "=".repeat(60) + "\n");
    }

    private static void demonstrateCompositeIterator() {
        System.out.println("EXAMPLE 3: Composite Iterator\n");
        System.out.println("=".repeat(60));

        DealCollection collection1 = new DealCollection();
        collection1.addDeal(createDeal("Deal A", "10000", DealStatus.WON));
        collection1.addDeal(createDeal("Deal B", "20000", DealStatus.WON));

        DealCollection collection2 = new DealCollection();
        collection2.addDeal(createDeal("Deal C", "30000", DealStatus.WON));
        collection2.addDeal(createDeal("Deal D", "40000", DealStatus.WON));

        System.out.println("\n--- Composite Iterator (Both Collections) ---");
        List<CommissionIterator<Deal>> iterators = Arrays.asList(
            collection1.createIterator(),
            collection2.createIterator()
        );

        CompositeIterator<Deal> composite = new CompositeIterator<>(iterators);
        System.out.println("Total deals across collections: " + composite.remaining());
        while (composite.hasNext()) {
            Deal deal = composite.next();
            System.out.println("  → " + deal.getTitle() + " [$" + deal.getValue() + "]");
        }

        System.out.println("\n" + "=".repeat(60) + "\n");
    }

    // Helper methods

    private static DealCollection createSampleDeals() {
        DealCollection deals = new DealCollection();

        deals.addDeal(createDeal("Small Sale", "5000", DealStatus.WON));
        deals.addDeal(createDeal("Medium Deal", "25000", DealStatus.WON));
        deals.addDeal(createDeal("Large Enterprise", "75000", DealStatus.WON));
        deals.addDeal(createDeal("Pending Deal", "30000", DealStatus.OPEN));
        deals.addDeal(createDeal("Lost Opportunity", "50000", DealStatus.LOST));
        deals.addDeal(createDeal("Mega Deal", "150000", DealStatus.WON));

        return deals;
    }

    private static Deal createDeal(String title, String value, DealStatus status) {
        Deal deal = new Deal(title, new BigDecimal(value), "REP-" + System.currentTimeMillis());
        deal.setStatus(status);
        deal.setCloseDate(LocalDate.now().minusDays((long)(Math.random() * 90)));
        return deal;
    }

    private static CommissionPlanRepository createSamplePlans() {
        CommissionPlanRepository repo = new CommissionPlanRepository();

        CommissionPlan plan1 = new CommissionPlan();
        plan1.setName("Q1 2024 Plan");
        plan1.setStatus(PlanStatus.ACTIVE);
        plan1.setEffectiveStartDate(LocalDate.of(2024, 1, 1));
        plan1.setEffectiveEndDate(LocalDate.of(2024, 3, 31));
        repo.addPlan(plan1);

        CommissionPlan plan2 = new CommissionPlan();
        plan2.setName("Q2 2024 Plan");
        plan2.setStatus(PlanStatus.DRAFT);
        plan2.setEffectiveStartDate(LocalDate.of(2024, 4, 1));
        plan2.setEffectiveEndDate(LocalDate.of(2024, 6, 30));
        repo.addPlan(plan2);

        CommissionPlan plan3 = new CommissionPlan();
        plan3.setName("2023 Annual Plan");
        plan3.setStatus(PlanStatus.ARCHIVED);
        plan3.setEffectiveStartDate(LocalDate.of(2023, 1, 1));
        plan3.setEffectiveEndDate(LocalDate.of(2023, 12, 31));
        repo.addPlan(plan3);

        return repo;
    }
}