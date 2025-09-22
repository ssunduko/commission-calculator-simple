package com.chapman.edu.commissions.autonomy;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.chapman.edu.commissions.model.CommissionCalculation;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.CommissionRule;
import com.chapman.edu.commissions.model.CommissionTier;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Functional Autonomy Example
 * 
 * Functional Autonomy refers to a component's ability to provide a complete business capability
 * without relying on external components. This class demonstrates functional autonomy by
 * implementing a complete commission calculation system that can operate independently.
 * 
 * Key characteristics of Functional Autonomy:
 * 1. The component provides a complete business capability
 * 2. It can operate independently without external dependencies
 * 3. It encapsulates all the logic needed for its business function
 * 4. It has well-defined interfaces for interaction with other components
 */
public class FunctionalAutonomyExample {

    /**
     * Internal data stores
     */
    private final Map<String, Deal> dealStore = new HashMap<>();
    private final Map<String, User> userStore = new HashMap<>();
    private final Map<String, CommissionPlan> planStore = new HashMap<>();
    private final Map<String, CommissionCalculation> calculationStore = new HashMap<>();

    /**
     * Constructor that initializes the data stores with sample data
     */
    public FunctionalAutonomyExample() {
        initializeSampleData();
    }

    /**
     * Creates a new commission plan
     * 
     * @param name The name of the plan
     * @param description The description of the plan
     * @param effectiveDate The effective date of the plan
     * @return The ID of the created plan
     */
    public String createCommissionPlan(String name, String description, LocalDate effectiveDate) {
        CommissionPlan plan = new CommissionPlan();
        String planId = "PLAN-" + UUID.randomUUID().toString().substring(0, 8);

        plan.setId(planId);
        plan.setName(name);
        // No setDescription method in CommissionPlan
        plan.setEffectiveStartDate(effectiveDate);
        plan.setRules(new ArrayList<>());

        planStore.put(planId, plan);

        return planId;
    }

    /**
     * Adds a commission rule to a plan
     * 
     * @param planId The ID of the plan
     * @param name The name of the rule
     * @param description The description of the rule
     * @return The ID of the created rule
     */
    public String addRuleToPlan(String planId, String name, String description) {
        CommissionPlan plan = planStore.get(planId);
        if (plan == null) {
            throw new IllegalArgumentException("Plan not found");
        }

        CommissionRule rule = new CommissionRule();
        String ruleId = "RULE-" + UUID.randomUUID().toString().substring(0, 8);

        rule.setId(ruleId);
        rule.setName(name);
        rule.setDescription(description);

        // CommissionRule doesn't have a setTiers method
        // Tiers are stored in the CommissionPlan
        plan.getRules().add(rule);

        return ruleId;
    }

    /**
     * Adds a commission tier to a rule
     * 
     * @param planId The ID of the plan
     * @param ruleId The ID of the rule
     * @param minAmount The minimum amount for the tier
     * @param maxAmount The maximum amount for the tier
     * @param rate The commission rate for the tier
     */
    public void addTierToRule(String planId, String ruleId, BigDecimal minAmount, BigDecimal maxAmount, BigDecimal rate) {
        CommissionPlan plan = planStore.get(planId);
        if (plan == null) {
            throw new IllegalArgumentException("Plan not found");
        }

        CommissionRule rule = null;
        for (CommissionRule r : plan.getRules()) {
            if (r.getId().equals(ruleId)) {
                rule = r;
                break;
            }
        }

        if (rule == null) {
            throw new IllegalArgumentException("Rule not found");
        }

        CommissionTier tier = new CommissionTier();
        tier.setId("TIER-" + UUID.randomUUID().toString().substring(0, 8));
        // CommissionTier uses lowerBound and upperBound instead of minAmount and maxAmount
        tier.setLowerBound(minAmount);
        tier.setUpperBound(maxAmount);
        tier.setRate(rate);

        // CommissionRule doesn't have a getTiers method
        // Tiers are stored in the CommissionPlan
        plan.addTier(tier);
    }

    /**
     * Creates a new deal
     * 
     * @param title The title of the deal
     * @param value The value of the deal
     * @param salesRepId The ID of the sales representative
     * @return The ID of the created deal
     */
    public String createDeal(String title, BigDecimal value, String salesRepId) {
        // Validate that the sales rep exists
        if (!userStore.containsKey(salesRepId)) {
            throw new IllegalArgumentException("Sales representative not found");
        }

        // Create a new deal with a unique ID
        Deal deal = new Deal(title, value, salesRepId);
        String dealId = "DEAL-" + UUID.randomUUID().toString().substring(0, 8);
        deal.setId(dealId);
        deal.setCreatedDate(LocalDate.now());

        // Store the deal
        dealStore.put(dealId, deal);

        return dealId;
    }

    /**
     * Adds a product to a deal
     * 
     * @param dealId The ID of the deal
     * @param productName The name of the product
     * @param quantity The quantity of the product
     * @param price The price of the product
     * @return The ID of the created product
     */
    public String addProductToDeal(String dealId, String productName, int quantity, BigDecimal price) {
        Deal deal = dealStore.get(dealId);
        if (deal == null) {
            throw new IllegalArgumentException("Deal not found");
        }

        String productId = "PROD-" + UUID.randomUUID().toString().substring(0, 8);
        DealProduct product = new DealProduct(productId, productName, quantity, price);
        product.setId("DP-" + UUID.randomUUID().toString().substring(0, 8));
        product.setDealId(dealId);

        deal.addProduct(product);
        deal.setValue(deal.calculateTotalValue());

        return product.getId();
    }

    /**
     * Updates the status of a deal
     * 
     * @param dealId The ID of the deal
     * @param status The new status of the deal
     */
    public void updateDealStatus(String dealId, DealStatus status) {
        Deal deal = dealStore.get(dealId);
        if (deal == null) {
            throw new IllegalArgumentException("Deal not found");
        }

        deal.setStatus(status);

        if (status == DealStatus.WON) {
            // When a deal is won, calculate commission
            calculateCommission(dealId);
        }
    }

    /**
     * Creates a new user
     * 
     * @param username The username of the user
     * @param email The email of the user
     * @param firstName The first name of the user
     * @param lastName The last name of the user
     * @param role The role of the user
     * @return The ID of the created user
     */
    public String createUser(String username, String email, String firstName, String lastName, UserRole role) {
        User user = new User(username, email, firstName, lastName);
        String userId = "USER-" + UUID.randomUUID().toString().substring(0, 8);
        user.setId(userId);
        user.addRole(role);

        userStore.put(userId, user);

        return userId;
    }

    /**
     * Assigns a commission plan to a user
     * 
     * @param userId The ID of the user
     * @param planId The ID of the plan
     */
    public void assignPlanToUser(String userId, String planId) {
        User user = userStore.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        CommissionPlan plan = planStore.get(planId);
        if (plan == null) {
            throw new IllegalArgumentException("Plan not found");
        }

        // In a real system, we would store this association
        System.out.println("Assigned plan " + plan.getName() + " to user " + user.getFullName());
    }

    /**
     * Calculates commission for a deal
     * 
     * @param dealId The ID of the deal
     * @return The ID of the created commission calculation
     */
    public String calculateCommission(String dealId) {
        Deal deal = dealStore.get(dealId);
        if (deal == null) {
            throw new IllegalArgumentException("Deal not found");
        }

        if (deal.getStatus() != DealStatus.WON) {
            throw new IllegalStateException("Commission can only be calculated for won deals");
        }

        User salesRep = userStore.get(deal.getSalesRepId());
        if (salesRep == null) {
            throw new IllegalStateException("Sales representative not found");
        }

        // For simplicity, we'll use the first plan in the store
        // In a real system, we would look up the plan assigned to the sales rep
        CommissionPlan plan = planStore.values().iterator().next();

        // Calculate commission based on the plan's rules
        BigDecimal commissionAmount = calculateCommissionAmount(deal, plan);

        // Create a commission calculation record
        CommissionCalculation calculation = new CommissionCalculation();
        String calculationId = "CALC-" + UUID.randomUUID().toString().substring(0, 8);

        calculation.setId(calculationId);
        calculation.setDealId(dealId);
        calculation.setSalesRepId(deal.getSalesRepId());
        // CommissionCalculation uses baseCommission instead of amount
        calculation.setBaseCommission(commissionAmount);
        calculation.setCalculationDate(LocalDate.now());

        calculationStore.put(calculationId, calculation);

        return calculationId;
    }

    /**
     * Calculates the commission amount for a deal based on a plan
     * 
     * @param deal The deal
     * @param plan The commission plan
     * @return The calculated commission amount
     */
    private BigDecimal calculateCommissionAmount(Deal deal, CommissionPlan plan) {
        BigDecimal dealValue = deal.getValue();
        BigDecimal commissionAmount = BigDecimal.ZERO;

        // Apply each rule in the plan
        for (CommissionRule rule : plan.getRules()) {
            // Find the applicable tier
            // CommissionRule doesn't have a getTiers method, tiers are stored in the CommissionPlan
            for (CommissionTier tier : plan.getTiers()) {
                // CommissionTier uses lowerBound and upperBound instead of minAmount and maxAmount
                if (dealValue.compareTo(tier.getLowerBound()) >= 0 && 
                    (tier.getUpperBound() == null || dealValue.compareTo(tier.getUpperBound()) <= 0)) {

                    // Calculate commission for this tier
                    BigDecimal tierCommission = dealValue.multiply(tier.getRate())
                            .setScale(2, RoundingMode.HALF_UP);

                    commissionAmount = commissionAmount.add(tierCommission);
                    break;
                }
            }
        }

        return commissionAmount;
    }

    /**
     * Gets all commission calculations for a sales representative
     * 
     * @param salesRepId The ID of the sales representative
     * @return A list of commission calculations
     */
    public List<CommissionCalculation> getCalculationsBySalesRep(String salesRepId) {
        List<CommissionCalculation> result = new ArrayList<>();

        for (CommissionCalculation calculation : calculationStore.values()) {
            if (calculation.getSalesRepId().equals(salesRepId)) {
                result.add(calculation);
            }
        }

        return result;
    }

    /**
     * Gets the total commission amount for a sales representative
     * 
     * @param salesRepId The ID of the sales representative
     * @return The total commission amount
     */
    public BigDecimal getTotalCommissionForSalesRep(String salesRepId) {
        BigDecimal total = BigDecimal.ZERO;

        for (CommissionCalculation calculation : calculationStore.values()) {
            if (calculation.getSalesRepId().equals(salesRepId)) {
                // CommissionCalculation uses baseCommission, grossCommission, or netCommission instead of amount
                total = total.add(calculation.getBaseCommission());
            }
        }

        return total;
    }

    /**
     * Initializes the data stores with sample data
     */
    private void initializeSampleData() {
        // Create users
        String salesRep1Id = createUser("jsmith", "jsmith@example.com", "John", "Smith", UserRole.SALES_REP);
        String salesRep2Id = createUser("mjohnson", "mjohnson@example.com", "Mary", "Johnson", UserRole.SALES_REP);
        createUser("rbrown", "rbrown@example.com", "Robert", "Brown", UserRole.SALES_MANAGER);

        // Create commission plan
        String planId = createCommissionPlan("Standard Sales Plan", "Standard commission plan for sales representatives", LocalDate.now());
        String ruleId = addRuleToPlan(planId, "Base Commission", "Base commission rate for all deals");

        // Add tiers to the rule
        addTierToRule(planId, ruleId, BigDecimal.ZERO, new BigDecimal("5000"), new BigDecimal("0.05"));
        addTierToRule(planId, ruleId, new BigDecimal("5000.01"), new BigDecimal("10000"), new BigDecimal("0.07"));
        addTierToRule(planId, ruleId, new BigDecimal("10000.01"), null, new BigDecimal("0.10"));

        // Assign plan to users
        assignPlanToUser(salesRep1Id, planId);
        assignPlanToUser(salesRep2Id, planId);

        // Create deals
        String deal1Id = createDeal("Software License Deal", BigDecimal.ZERO, salesRep1Id);
        addProductToDeal(deal1Id, "Enterprise License", 1, new BigDecimal("15000.00"));

        String deal2Id = createDeal("Hardware Purchase", BigDecimal.ZERO, salesRep2Id);
        addProductToDeal(deal2Id, "Server", 2, new BigDecimal("2000.00"));
        addProductToDeal(deal2Id, "Workstation", 3, new BigDecimal("1000.00"));

        // Update deal statuses (this will trigger commission calculation for won deals)
        updateDealStatus(deal1Id, DealStatus.WON);
        updateDealStatus(deal2Id, DealStatus.WON);
    }

    /**
     * Main method to demonstrate the usage of the FunctionalAutonomyExample class
     */
    public static void main(String[] args) {
        FunctionalAutonomyExample example = new FunctionalAutonomyExample();

        // Get all sales representatives
        List<User> salesReps = new ArrayList<>();
        for (User user : example.userStore.values()) {
            if (user.hasRole(UserRole.SALES_REP)) {
                salesReps.add(user);
            }
        }

        System.out.println("Sales Representatives and Their Commissions:");
        for (User salesRep : salesReps) {
            System.out.println("- " + salesRep.getFullName() + " (" + salesRep.getEmail() + ")");

            // Get deals for this sales rep
            List<Deal> deals = new ArrayList<>();
            for (Deal deal : example.dealStore.values()) {
                if (deal.getSalesRepId().equals(salesRep.getId())) {
                    deals.add(deal);
                }
            }

            System.out.println("  Deals:");
            for (Deal deal : deals) {
                System.out.println("  - " + deal.getTitle() + " (" + deal.getStatus() + "): $" + deal.getValue());
            }

            // Get commission calculations for this sales rep
            List<CommissionCalculation> calculations = example.getCalculationsBySalesRep(salesRep.getId());

            System.out.println("  Commission Calculations:");
            for (CommissionCalculation calculation : calculations) {
                Deal deal = example.dealStore.get(calculation.getDealId());
                // CommissionCalculation uses baseCommission, grossCommission, or netCommission instead of amount
                System.out.println("  - Deal: " + deal.getTitle() + ", Amount: $" + calculation.getBaseCommission());
            }

            // Get total commission for this sales rep
            BigDecimal totalCommission = example.getTotalCommissionForSalesRep(salesRep.getId());
            System.out.println("  Total Commission: $" + totalCommission);

            System.out.println();
        }
    }
}
