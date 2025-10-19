package com.chapman.edu.commissions.api.rest;

import com.chapman.edu.commissions.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;

/**
 * Utility class for loading sample data into the API repositories.
 *
 * This class demonstrates:
 * - Test Data Builder pattern
 * - Fixture data creation for testing and demos
 * - Separation of test data from production code
 * - Realistic domain model examples
 *
 * Purpose:
 * - Populate repositories with realistic sample data
 * - Support integration testing
 * - Enable API demonstrations
 * - Provide data for Postman testing
 */
public class SampleDataLoader {

    private final Repository<Deal> dealRepository;
    private final Repository<User> userRepository;
    private final Repository<CommissionPlan> planRepository;
    private final Repository<Dispute> disputeRepository;

    /**
     * Constructor with repository dependencies.
     *
     * @param dealRepository Deal repository
     * @param userRepository User repository
     * @param planRepository Commission plan repository
     * @param disputeRepository Dispute repository
     */
    public SampleDataLoader(Repository<Deal> dealRepository,
                           Repository<User> userRepository,
                           Repository<CommissionPlan> planRepository,
                           Repository<Dispute> disputeRepository) {
        this.dealRepository = dealRepository;
        this.userRepository = userRepository;
        this.planRepository = planRepository;
        this.disputeRepository = disputeRepository;
    }

    /**
     * Load all sample data into repositories.
     * Call this method to populate the system with test data.
     */
    public void loadAllData() {
        System.out.println("Loading sample data...");

        loadUsers();
        loadCommissionPlans();
        loadDeals();
        loadDisputes();

        System.out.println("Sample data loaded successfully!");
        System.out.println("  Users: " + userRepository.findAll().size());
        System.out.println("  Deals: " + dealRepository.findAll().size());
        System.out.println("  Commission Plans: " + planRepository.findAll().size());
        System.out.println("  Disputes: " + disputeRepository.findAll().size());
    }

    /**
     * Load sample users with different roles.
     *
     * Demonstrates:
     * - Creating users with various roles
     * - Setting up organizational hierarchy
     * - Realistic user profiles
     */
    private void loadUsers() {
        // Sales Representatives
        User johnSmith = createUser(
                "jsmith",
                "jsmith@company.com",
                "John",
                "Smith",
                new HashSet<>(Arrays.asList(UserRole.SALES_REP)),
                "Sales",
                "West Coast"
        );
        userRepository.save(johnSmith);

        User sarahJohnson = createUser(
                "sjohnson",
                "sjohnson@company.com",
                "Sarah",
                "Johnson",
                new HashSet<>(Arrays.asList(UserRole.SALES_REP)),
                "Sales",
                "East Coast"
        );
        userRepository.save(sarahJohnson);

        User mikeWilliams = createUser(
                "mwilliams",
                "mwilliams@company.com",
                "Mike",
                "Williams",
                new HashSet<>(Arrays.asList(UserRole.SALES_REP)),
                "Sales",
                "Midwest"
        );
        userRepository.save(mikeWilliams);

        // Sales Manager
        User emilyChen = createUser(
                "echen",
                "echen@company.com",
                "Emily",
                "Chen",
                new HashSet<>(Arrays.asList(UserRole.SALES_MANAGER, UserRole.SALES_REP)),
                "Sales",
                "All Territories"
        );
        userRepository.save(emilyChen);

        // Finance Administrator
        User davidBrown = createUser(
                "dbrown",
                "dbrown@company.com",
                "David",
                "Brown",
                new HashSet<>(Arrays.asList(UserRole.FINANCE_ADMIN)),
                "Finance",
                null
        );
        userRepository.save(davidBrown);

        // System Administrator
        User adminUser = createUser(
                "admin",
                "admin@company.com",
                "System",
                "Administrator",
                new HashSet<>(Arrays.asList(UserRole.SYSTEM_ADMIN)),
                "IT",
                null
        );
        userRepository.save(adminUser);
    }

    /**
     * Load sample commission plans.
     *
     * Demonstrates:
     * - Active and draft plans
     * - Date-based plan effectiveness
     * - Different plan configurations
     */
    private void loadCommissionPlans() {
        // Q1 2024 Standard Plan (Active)
        CommissionPlan q1Plan = new CommissionPlan("Q1 2024 Standard Plan", Currency.getInstance("USD"));
        q1Plan.setStatus(PlanStatus.ACTIVE);
        q1Plan.setEffectiveStartDate(LocalDate.of(2024, 1, 1));
        q1Plan.setEffectiveEndDate(LocalDate.of(2024, 3, 31));
        q1Plan.setCreatedBy("dbrown");
        q1Plan.setCreatedDate(LocalDate.of(2023, 12, 15));
        planRepository.save(q1Plan);

        // Q2 2024 Enhanced Plan (Active)
        CommissionPlan q2Plan = new CommissionPlan("Q2 2024 Enhanced Plan", Currency.getInstance("USD"));
        q2Plan.setStatus(PlanStatus.ACTIVE);
        q2Plan.setEffectiveStartDate(LocalDate.of(2024, 4, 1));
        q2Plan.setEffectiveEndDate(LocalDate.of(2024, 6, 30));
        q2Plan.setCreatedBy("dbrown");
        q2Plan.setCreatedDate(LocalDate.of(2024, 3, 1));
        planRepository.save(q2Plan);

        // Q3 2024 Draft Plan
        CommissionPlan q3Plan = new CommissionPlan("Q3 2024 Draft Plan", Currency.getInstance("USD"));
        q3Plan.setStatus(PlanStatus.DRAFT);
        q3Plan.setEffectiveStartDate(LocalDate.of(2024, 7, 1));
        q3Plan.setEffectiveEndDate(LocalDate.of(2024, 9, 30));
        q3Plan.setCreatedBy("dbrown");
        q3Plan.setCreatedDate(LocalDate.of(2024, 5, 15));
        planRepository.save(q3Plan);

        // 2023 Archived Plan
        CommissionPlan archivedPlan = new CommissionPlan("2023 Annual Plan", Currency.getInstance("USD"));
        archivedPlan.setStatus(PlanStatus.ARCHIVED);
        archivedPlan.setEffectiveStartDate(LocalDate.of(2023, 1, 1));
        archivedPlan.setEffectiveEndDate(LocalDate.of(2023, 12, 31));
        archivedPlan.setCreatedBy("dbrown");
        archivedPlan.setCreatedDate(LocalDate.of(2022, 11, 1));
        planRepository.save(archivedPlan);
    }

    /**
     * Load sample deals with various statuses and values.
     *
     * Demonstrates:
     * - Deals in different states
     * - Various deal sizes
     * - Deals with products
     * - Realistic business scenarios
     */
    private void loadDeals() {
        // Get first user ID for assignments
        String user1Id = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals("jsmith"))
                .findFirst()
                .map(User::getId)
                .orElse("USER-001");

        String user2Id = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals("sjohnson"))
                .findFirst()
                .map(User::getId)
                .orElse("USER-002");

        String user3Id = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals("mwilliams"))
                .findFirst()
                .map(User::getId)
                .orElse("USER-003");

        // Large Enterprise Deal - WON
        Deal enterpriseDeal = new Deal(
                "Acme Corp - Enterprise Software License",
                new BigDecimal("500000.00"),
                user1Id
        );
        enterpriseDeal.setStatus(DealStatus.WON);
        enterpriseDeal.setCloseDate(LocalDate.of(2024, 2, 15));
        enterpriseDeal.addProduct(new DealProduct(
                "PROD-SW-001",
                "Enterprise License (1000 users)",
                1,
                new BigDecimal("450000.00")
        ));
        enterpriseDeal.addProduct(new DealProduct(
                "PROD-SUP-001",
                "Premium Support (Annual)",
                1,
                new BigDecimal("50000.00")
        ));
        dealRepository.save(enterpriseDeal);

        // Mid-size Cloud Deal - OPEN
        Deal cloudDeal = new Deal(
                "TechStart Inc - Cloud Infrastructure",
                new BigDecimal("150000.00"),
                user2Id
        );
        cloudDeal.setStatus(DealStatus.OPEN);
        cloudDeal.addProduct(new DealProduct(
                "PROD-CLOUD-001",
                "Cloud Platform (3-year commitment)",
                1,
                new BigDecimal("150000.00")
        ));
        dealRepository.save(cloudDeal);

        // Consulting Services - WON
        Deal consultingDeal = new Deal(
                "Global Finance - Consulting Engagement",
                new BigDecimal("75000.00"),
                user1Id
        );
        consultingDeal.setStatus(DealStatus.WON);
        consultingDeal.setCloseDate(LocalDate.of(2024, 3, 1));
        consultingDeal.addProduct(new DealProduct(
                "PROD-CONS-001",
                "Strategic Consulting (500 hours)",
                1,
                new BigDecimal("75000.00")
        ));
        dealRepository.save(consultingDeal);

        // Small Software Deal - OPEN
        Deal smallDeal = new Deal(
                "StartupXYZ - Small Business Package",
                new BigDecimal("25000.00"),
                user3Id
        );
        smallDeal.setStatus(DealStatus.OPEN);
        smallDeal.addProduct(new DealProduct(
                "PROD-SW-002",
                "Small Business License (50 users)",
                1,
                new BigDecimal("20000.00")
        ));
        smallDeal.addProduct(new DealProduct(
                "PROD-TRAIN-001",
                "Training Package",
                1,
                new BigDecimal("5000.00")
        ));
        dealRepository.save(smallDeal);

        // Hardware Deal - LOST
        Deal hardwareDeal = new Deal(
                "Manufacturing Co - Hardware Refresh",
                new BigDecimal("200000.00"),
                user2Id
        );
        hardwareDeal.setStatus(DealStatus.LOST);
        dealRepository.save(hardwareDeal);

        // Renewal Deal - WON
        Deal renewalDeal = new Deal(
                "Retail Giant - Annual Renewal",
                new BigDecimal("300000.00"),
                user1Id
        );
        renewalDeal.setStatus(DealStatus.WON);
        renewalDeal.setCloseDate(LocalDate.of(2024, 1, 30));
        dealRepository.save(renewalDeal);

        // Large Pipeline Deal - OPEN
        Deal pipelineDeal = new Deal(
                "Healthcare Network - Digital Transformation",
                new BigDecimal("1000000.00"),
                user2Id
        );
        pipelineDeal.setStatus(DealStatus.OPEN);
        dealRepository.save(pipelineDeal);

        // Cancelled Deal
        Deal cancelledDeal = new Deal(
                "Budget Cuts Inc - Postponed Project",
                new BigDecimal("100000.00"),
                user3Id
        );
        cancelledDeal.setStatus(DealStatus.CANCELLED);
        dealRepository.save(cancelledDeal);
    }

    /**
     * Load sample disputes.
     *
     * Demonstrates:
     * - Disputes in various stages
     * - Dispute workflow
     * - Comments and resolution tracking
     */
    private void loadDisputes() {
        // Get user IDs
        String salesRepId = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals("jsmith"))
                .findFirst()
                .map(User::getId)
                .orElse("USER-001");

        String managerId = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals("echen"))
                .findFirst()
                .map(User::getId)
                .orElse("USER-004");

        // Active Dispute - Under Review
        Dispute activeDispute = new Dispute(
                "CALC-001",
                salesRepId,
                "Commission Calculation Discrepancy - Q1 Deal",
                "The commission for the Acme Corp deal was calculated at 8% instead of the agreed 10% rate. " +
                "According to my contract and the Q1 plan, enterprise deals over $500k should receive 10% commission."
        );
        activeDispute.setStatus(DisputeStatus.UNDER_REVIEW);
        activeDispute.setManagerId(managerId);
        activeDispute.addUserComment(managerId, "Emily Chen",
                "I've reviewed your contract and the deal details. You're correct about the 10% rate. " +
                "I'm escalating this to Finance for correction.");
        disputeRepository.save(activeDispute);

        // Escalated Dispute
        Dispute escalatedDispute = new Dispute(
                "CALC-002",
                salesRepId,
                "Missing Bonus for Q4 Achievement",
                "I achieved 150% of quota in Q4 but the bonus was not included in my commission payment."
        );
        escalatedDispute.setStatus(DisputeStatus.ESCALATED);
        escalatedDispute.setEscalated(true);
        escalatedDispute.setManagerId(managerId);
        disputeRepository.save(escalatedDispute);

        // Resolved Dispute
        Dispute resolvedDispute = new Dispute(
                "CALC-003",
                salesRepId,
                "Incorrect Territory Assignment",
                "Deal was assigned to wrong territory, affecting my commission split."
        );
        resolvedDispute.setStatus(DisputeStatus.RESOLVED);
        resolvedDispute.setManagerId(managerId);
        resolvedDispute.setResolvedBy(managerId);
        resolvedDispute.setResolvedDate(LocalDateTime.now().minusDays(5));
        resolvedDispute.setResolution(
                "Territory assignment has been corrected. Commission will be recalculated and " +
                "difference will be paid in next cycle."
        );
        disputeRepository.save(resolvedDispute);

        // Initiated Dispute (New)
        Dispute newDispute = new Dispute(
                "CALC-004",
                salesRepId,
                "Product Mix Calculation Error",
                "Commission rate for software products should be different from services."
        );
        newDispute.setStatus(DisputeStatus.INITIATED);
        disputeRepository.save(newDispute);
    }

    /**
     * Helper method to create a user with all fields.
     *
     * @param username Username
     * @param email Email address
     * @param firstName First name
     * @param lastName Last name
     * @param roles Set of user roles
     * @param department Department name
     * @param territory Sales territory (can be null)
     * @return Configured user object
     */
    private User createUser(String username, String email, String firstName, String lastName,
                           HashSet<UserRole> roles, String department, String territory) {
        User user = new User(username, email, firstName, lastName);
        user.setRoles(roles);
        user.setActive(true);
        user.setDepartment(department);
        user.setTerritory(territory);
        user.setCreatedDate(LocalDate.now().minusMonths(6));
        return user;
    }

    /**
     * Clear all sample data from repositories.
     * Useful for resetting test state.
     */
    public void clearAllData() {
        if (dealRepository instanceof InMemoryRepository) {
            ((InMemoryRepository<?>) dealRepository).clear();
        }
        if (userRepository instanceof InMemoryRepository) {
            ((InMemoryRepository<?>) userRepository).clear();
        }
        if (planRepository instanceof InMemoryRepository) {
            ((InMemoryRepository<?>) planRepository).clear();
        }
        if (disputeRepository instanceof InMemoryRepository) {
            ((InMemoryRepository<?>) disputeRepository).clear();
        }
        System.out.println("All sample data cleared");
    }
}