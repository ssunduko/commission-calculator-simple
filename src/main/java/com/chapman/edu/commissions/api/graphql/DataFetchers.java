package com.chapman.edu.commissions.api.graphql;

import com.chapman.edu.commissions.api.rest.Repository;
import com.chapman.edu.commissions.model.*;
import graphql.schema.DataFetcher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Data Fetchers for GraphQL queries and mutations.
 *
 * WHAT IS A DATA FETCHER?
 * -----------------------
 * A DataFetcher is a function that retrieves data for a specific field in your GraphQL schema.
 * It's the bridge between GraphQL queries and your data sources (databases, APIs, repositories, etc.).
 *
 * KEY CONCEPTS:
 * ------------
 * 1. Field Resolution: Each field in your schema can have a DataFetcher
 * 2. Context Access: DataFetchers receive context (environment) with query details
 * 3. Parent Object: DataFetchers can access the parent object for nested resolvers
 * 4. Arguments: DataFetchers can access query arguments (filters, IDs, etc.)
 *
 * DATAFETCHINGENVIRONMENT:
 * -----------------------
 * Every DataFetcher receives a DataFetchingEnvironment which provides:
 * - getArgument("name"): Get query/mutation arguments
 * - getSource(): Get parent object (for nested field resolution)
 * - getContext(): Get shared context object
 * - getFieldDefinition(): Get schema field metadata
 *
 * RESOLVER PATTERN:
 * ----------------
 * This class demonstrates the Resolver Pattern:
 * - Separates data fetching logic from schema definition
 * - Centralizes business logic for GraphQL operations
 * - Provides type-safe data access
 * - Enables testing of data fetching logic
 *
 * DESIGN PATTERNS:
 * ---------------
 * - Data Access Object (DAO): Repository pattern for data access
 * - Dependency Injection: Repositories injected via constructor
 * - Single Responsibility: Each method handles one specific field/operation
 * - Functional Programming: DataFetcher is a functional interface
 */
public class DataFetchers {

    private final Repository<Deal> dealRepository;
    private final Repository<User> userRepository;
    private final Repository<CommissionPlan> planRepository;
    private final Repository<Dispute> disputeRepository;

    /**
     * Constructor with dependency injection.
     * Repositories are injected to follow Dependency Inversion Principle.
     *
     * @param dealRepository Repository for Deal entities
     * @param userRepository Repository for User entities
     * @param planRepository Repository for CommissionPlan entities
     * @param disputeRepository Repository for Dispute entities
     */
    public DataFetchers(Repository<Deal> dealRepository,
                       Repository<User> userRepository,
                       Repository<CommissionPlan> planRepository,
                       Repository<Dispute> disputeRepository) {
        this.dealRepository = dealRepository;
        this.userRepository = userRepository;
        this.planRepository = planRepository;
        this.disputeRepository = disputeRepository;
    }

    // ============================================================================
    // DEAL QUERIES
    // ============================================================================

    /**
     * Fetch all deals.
     *
     * GRAPHQL QUERY EXAMPLE:
     * query {
     *   deals {
     *     id
     *     title
     *     value
     *     status
     *   }
     * }
     *
     * SELECTIVE FIELD FETCHING:
     * GraphQL only returns the fields requested by the client.
     * Even though Deal has many fields, client can request just id and title.
     */
    public DataFetcher<List<Deal>> getAllDeals() {
        return environment -> dealRepository.findAll();
    }

    /**
     * Fetch a single deal by ID.
     *
     * GRAPHQL QUERY EXAMPLE:
     * query {
     *   deal(id: "DEAL-001") {
     *     id
     *     title
     *     salesRep {
     *       firstName
     *       lastName
     *     }
     *   }
     * }
     *
     * ARGUMENTS:
     * The ID is passed as an argument: environment.getArgument("id")
     *
     * NULL HANDLING:
     * Returns null if deal not found (GraphQL automatically handles this)
     */
    public DataFetcher<Deal> getDealById() {
        return environment -> {
            String id = environment.getArgument("id");
            return dealRepository.findById(id).orElse(null);
        };
    }

    /**
     * Fetch deals by status.
     *
     * GRAPHQL QUERY EXAMPLE:
     * query {
     *   dealsByStatus(status: WON) {
     *     id
     *     title
     *     value
     *   }
     * }
     *
     * ENUM HANDLING:
     * GraphQL automatically converts the enum string to DealStatus enum.
     */
    public DataFetcher<List<Deal>> getDealsByStatus() {
        return environment -> {
            Object statusArg = environment.getArgument("status");
            DealStatus status = statusArg instanceof String
                ? DealStatus.valueOf((String) statusArg)
                : (DealStatus) statusArg;
            return dealRepository.findAll().stream()
                    .filter(deal -> deal.getStatus() == status)
                    .collect(Collectors.toList());
        };
    }

    /**
     * Fetch deals by sales representative.
     *
     * FILTERING PATTERN:
     * Uses Java Streams to filter results from repository.
     * In production, this filtering should happen at database level.
     */
    public DataFetcher<List<Deal>> getDealsBySalesRep() {
        return environment -> {
            String salesRepId = environment.getArgument("salesRepId");
            return dealRepository.findAll().stream()
                    .filter(deal -> deal.getSalesRepId().equals(salesRepId))
                    .collect(Collectors.toList());
        };
    }

    /**
     * Resolve the salesRep field on Deal.
     *
     * NESTED OBJECT RESOLUTION:
     * This is a field-level resolver that runs when client requests the salesRep field.
     *
     * GRAPHQL QUERY EXAMPLE:
     * query {
     *   deal(id: "DEAL-001") {
     *     title
     *     salesRep {        # This field triggers this resolver
     *       firstName
     *       lastName
     *     }
     *   }
     * }
     *
     * PARENT OBJECT ACCESS:
     * environment.getSource() returns the parent Deal object.
     * We extract salesRepId from it to fetch the User.
     *
     * N+1 PROBLEM:
     * If fetching many deals with salesRep, this creates N+1 queries.
     * Solution: Use DataLoader pattern (batching) - see advanced GraphQL patterns.
     */
    public DataFetcher<User> getDealSalesRep() {
        return environment -> {
            Deal deal = environment.getSource();
            return userRepository.findById(deal.getSalesRepId()).orElse(null);
        };
    }

    /**
     * Resolve the calculatedTotalValue field on Deal.
     *
     * COMPUTED FIELDS:
     * This demonstrates a computed field that doesn't exist in the database.
     * The value is calculated on-the-fly when requested.
     *
     * LAZY EVALUATION:
     * Only computed if client requests this field.
     */
    public DataFetcher<BigDecimal> getDealCalculatedTotalValue() {
        return environment -> {
            Deal deal = environment.getSource();
            return deal.calculateTotalValue();
        };
    }

    /**
     * Resolve the totalPrice field on DealProduct.
     *
     * Another computed field example at the DealProduct level.
     */
    public DataFetcher<BigDecimal> getDealProductTotalPrice() {
        return environment -> {
            DealProduct product = environment.getSource();
            return product.calculateTotalPrice();
        };
    }

    // ============================================================================
    // USER QUERIES
    // ============================================================================

    /**
     * Fetch all users.
     */
    public DataFetcher<List<User>> getAllUsers() {
        return environment -> userRepository.findAll();
    }

    /**
     * Fetch a single user by ID.
     */
    public DataFetcher<User> getUserById() {
        return environment -> {
            String id = environment.getArgument("id");
            return userRepository.findById(id).orElse(null);
        };
    }

    /**
     * Fetch a user by username.
     *
     * UNIQUE FIELD LOOKUP:
     * Demonstrates searching by a unique field other than ID.
     */
    public DataFetcher<User> getUserByUsername() {
        return environment -> {
            String username = environment.getArgument("username");
            return userRepository.findAll().stream()
                    .filter(user -> user.getUsername().equals(username))
                    .findFirst()
                    .orElse(null);
        };
    }

    /**
     * Fetch users by role.
     *
     * SET MEMBERSHIP CHECK:
     * Filters users who have the specified role in their roles set.
     */
    public DataFetcher<List<User>> getUsersByRole() {
        return environment -> {
            Object roleArg = environment.getArgument("role");
            UserRole role = roleArg instanceof String
                ? UserRole.valueOf((String) roleArg)
                : (UserRole) roleArg;
            return userRepository.findAll().stream()
                    .filter(user -> user.getRoles().contains(role))
                    .collect(Collectors.toList());
        };
    }

    /**
     * Resolve the fullName field on User.
     *
     * COMPUTED STRING FIELD:
     * Concatenates firstName and lastName.
     */
    public DataFetcher<String> getUserFullName() {
        return environment -> {
            User user = environment.getSource();
            return user.getFullName();
        };
    }

    /**
     * Resolve the deals field on User.
     *
     * ONE-TO-MANY RELATIONSHIP:
     * Fetches all deals owned by this user.
     *
     * BIDIRECTIONAL NAVIGATION:
     * Allows querying User -> Deals (reverse of Deal -> User)
     */
    public DataFetcher<List<Deal>> getUserDeals() {
        return environment -> {
            User user = environment.getSource();
            return dealRepository.findAll().stream()
                    .filter(deal -> deal.getSalesRepId().equals(user.getId()))
                    .collect(Collectors.toList());
        };
    }

    // ============================================================================
    // COMMISSION PLAN QUERIES
    // ============================================================================

    /**
     * Fetch all commission plans.
     */
    public DataFetcher<List<CommissionPlan>> getAllCommissionPlans() {
        return environment -> planRepository.findAll();
    }

    /**
     * Fetch a single commission plan by ID.
     */
    public DataFetcher<CommissionPlan> getCommissionPlanById() {
        return environment -> {
            String id = environment.getArgument("id");
            return planRepository.findById(id).orElse(null);
        };
    }

    /**
     * Fetch commission plans by status.
     */
    public DataFetcher<List<CommissionPlan>> getCommissionPlansByStatus() {
        return environment -> {
            PlanStatus status = environment.getArgument("status");
            return planRepository.findAll().stream()
                    .filter(plan -> plan.getStatus() == status)
                    .collect(Collectors.toList());
        };
    }

    /**
     * Fetch active commission plans on a specific date.
     *
     * BUSINESS LOGIC IN RESOLVER:
     * Demonstrates complex business logic (date range checking) in a resolver.
     */
    public DataFetcher<List<CommissionPlan>> getActiveCommissionPlansOnDate() {
        return environment -> {
            LocalDate date = environment.getArgument("date");
            return planRepository.findAll().stream()
                    .filter(plan -> plan.isActiveOn(date))
                    .collect(Collectors.toList());
        };
    }

    // ============================================================================
    // DISPUTE QUERIES
    // ============================================================================

    /**
     * Fetch all disputes.
     */
    public DataFetcher<List<Dispute>> getAllDisputes() {
        return environment -> disputeRepository.findAll();
    }

    /**
     * Fetch a single dispute by ID.
     */
    public DataFetcher<Dispute> getDisputeById() {
        return environment -> {
            String id = environment.getArgument("id");
            return disputeRepository.findById(id).orElse(null);
        };
    }

    /**
     * Fetch disputes by sales representative.
     */
    public DataFetcher<List<Dispute>> getDisputesBySalesRep() {
        return environment -> {
            String salesRepId = environment.getArgument("salesRepId");
            return disputeRepository.findAll().stream()
                    .filter(dispute -> dispute.getSalesRepId().equals(salesRepId))
                    .collect(Collectors.toList());
        };
    }

    /**
     * Fetch disputes by status.
     */
    public DataFetcher<List<Dispute>> getDisputesByStatus() {
        return environment -> {
            DisputeStatus status = environment.getArgument("status");
            return disputeRepository.findAll().stream()
                    .filter(dispute -> dispute.getStatus() == status)
                    .collect(Collectors.toList());
        };
    }

    /**
     * Resolve the salesRep field on Dispute.
     */
    public DataFetcher<User> getDisputeSalesRep() {
        return environment -> {
            Dispute dispute = environment.getSource();
            return userRepository.findById(dispute.getSalesRepId()).orElse(null);
        };
    }

    /**
     * Resolve the manager field on Dispute.
     */
    public DataFetcher<User> getDisputeManager() {
        return environment -> {
            Dispute dispute = environment.getSource();
            String managerId = dispute.getManagerId();
            if (managerId == null) {
                return null;
            }
            return userRepository.findById(managerId).orElse(null);
        };
    }

    // ============================================================================
    // DEAL MUTATIONS
    // ============================================================================

    /**
     * Create a new deal.
     *
     * GRAPHQL MUTATION EXAMPLE:
     * mutation {
     *   createDeal(input: {
     *     title: "New Deal"
     *     value: "100000.00"
     *     salesRepId: "USER-001"
     *   }) {
     *     id
     *     title
     *     value
     *   }
     * }
     *
     * INPUT OBJECTS:
     * GraphQL uses Input types (CreateDealInput) for complex input data.
     * Input types are similar to regular types but for input only.
     *
     * MAP CONVERSION:
     * environment.getArgument("input") returns a Map representing the input object.
     * We manually extract fields from this map to create our Java object.
     */
    public DataFetcher<Deal> createDeal() {
        return environment -> {
            Map<String, Object> input = environment.getArgument("input");

            String title = (String) input.get("title");
            Object valueObj = input.get("value");
            BigDecimal value = valueObj instanceof String
                ? new BigDecimal((String) valueObj)
                : valueObj instanceof Number
                    ? BigDecimal.valueOf(((Number) valueObj).doubleValue())
                    : (BigDecimal) valueObj;
            String salesRepId = (String) input.get("salesRepId");

            Deal deal = new Deal(title, value, salesRepId);

            // Set optional fields
            if (input.containsKey("closeDate")) {
                deal.setCloseDate((LocalDate) input.get("closeDate"));
            }

            // Add products if provided
            if (input.containsKey("products")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> productsInput = (List<Map<String, Object>>) input.get("products");
                for (Map<String, Object> productInput : productsInput) {
                    DealProduct product = createDealProductFromInput(productInput);
                    deal.addProduct(product);
                }
            }

            return dealRepository.save(deal);
        };
    }

    /**
     * Update an existing deal.
     *
     * PARTIAL UPDATES:
     * Only updates fields that are provided in the input.
     * Uses input.containsKey() to check if a field was provided.
     */
    public DataFetcher<Deal> updateDeal() {
        return environment -> {
            String id = environment.getArgument("id");
            Map<String, Object> input = environment.getArgument("input");

            Optional<Deal> dealOpt = dealRepository.findById(id);
            if (dealOpt.isEmpty()) {
                return null;
            }

            Deal deal = dealOpt.get();

            // Update only provided fields
            if (input.containsKey("title")) {
                deal.setTitle((String) input.get("title"));
            }
            if (input.containsKey("value")) {
                String valueStr = (String) input.get("value");
                deal.setValue(new BigDecimal(valueStr));
            }
            if (input.containsKey("status")) {
                Object statusObj = input.get("status");
                DealStatus status = statusObj instanceof String
                    ? DealStatus.valueOf((String) statusObj)
                    : (DealStatus) statusObj;
                deal.setStatus(status);
            }
            if (input.containsKey("salesRepId")) {
                deal.setSalesRepId((String) input.get("salesRepId"));
            }
            if (input.containsKey("closeDate")) {
                deal.setCloseDate((LocalDate) input.get("closeDate"));
            }
            if (input.containsKey("products")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> productsInput = (List<Map<String, Object>>) input.get("products");
                List<DealProduct> products = new ArrayList<>();
                for (Map<String, Object> productInput : productsInput) {
                    products.add(createDealProductFromInput(productInput));
                }
                deal.setProducts(products);
            }

            return dealRepository.save(deal);
        };
    }

    /**
     * Delete a deal.
     *
     * BOOLEAN RETURN:
     * Returns true if deleted, false if not found.
     */
    public DataFetcher<Boolean> deleteDeal() {
        return environment -> {
            String id = environment.getArgument("id");
            return dealRepository.deleteById(id);
        };
    }

    // ============================================================================
    // USER MUTATIONS
    // ============================================================================

    /**
     * Create a new user.
     */
    public DataFetcher<User> createUser() {
        return environment -> {
            Map<String, Object> input = environment.getArgument("input");

            String username = (String) input.get("username");
            String email = (String) input.get("email");
            String firstName = (String) input.get("firstName");
            String lastName = (String) input.get("lastName");

            User user = new User(username, email, firstName, lastName);

            // Set roles
            @SuppressWarnings("unchecked")
            List<UserRole> roles = (List<UserRole>) input.get("roles");
            user.setRoles(new HashSet<>(roles));

            // Set optional fields
            if (input.containsKey("department")) {
                user.setDepartment((String) input.get("department"));
            }
            if (input.containsKey("territory")) {
                user.setTerritory((String) input.get("territory"));
            }
            if (input.containsKey("managerId")) {
                user.setManagerId((String) input.get("managerId"));
            }

            return userRepository.save(user);
        };
    }

    /**
     * Update an existing user.
     */
    public DataFetcher<User> updateUser() {
        return environment -> {
            String id = environment.getArgument("id");
            Map<String, Object> input = environment.getArgument("input");

            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isEmpty()) {
                return null;
            }

            User user = userOpt.get();

            if (input.containsKey("email")) {
                user.setEmail((String) input.get("email"));
            }
            if (input.containsKey("firstName")) {
                user.setFirstName((String) input.get("firstName"));
            }
            if (input.containsKey("lastName")) {
                user.setLastName((String) input.get("lastName"));
            }
            if (input.containsKey("roles")) {
                @SuppressWarnings("unchecked")
                List<UserRole> roles = (List<UserRole>) input.get("roles");
                user.setRoles(new HashSet<>(roles));
            }
            if (input.containsKey("active")) {
                user.setActive((Boolean) input.get("active"));
            }
            if (input.containsKey("department")) {
                user.setDepartment((String) input.get("department"));
            }
            if (input.containsKey("territory")) {
                user.setTerritory((String) input.get("territory"));
            }
            if (input.containsKey("managerId")) {
                user.setManagerId((String) input.get("managerId"));
            }

            return userRepository.save(user);
        };
    }

    /**
     * Delete a user.
     */
    public DataFetcher<Boolean> deleteUser() {
        return environment -> {
            String id = environment.getArgument("id");
            return userRepository.deleteById(id);
        };
    }

    // ============================================================================
    // COMMISSION PLAN MUTATIONS
    // ============================================================================

    /**
     * Create a new commission plan.
     */
    public DataFetcher<CommissionPlan> createCommissionPlan() {
        return environment -> {
            Map<String, Object> input = environment.getArgument("input");

            String name = (String) input.get("name");
            String currencyCode = (String) input.get("currency");
            Currency currency = Currency.getInstance(currencyCode);

            CommissionPlan plan = new CommissionPlan(name, currency);

            if (input.containsKey("effectiveStartDate")) {
                plan.setEffectiveStartDate((LocalDate) input.get("effectiveStartDate"));
            }
            if (input.containsKey("effectiveEndDate")) {
                plan.setEffectiveEndDate((LocalDate) input.get("effectiveEndDate"));
            }

            return planRepository.save(plan);
        };
    }

    /**
     * Update an existing commission plan.
     */
    public DataFetcher<CommissionPlan> updateCommissionPlan() {
        return environment -> {
            String id = environment.getArgument("id");
            Map<String, Object> input = environment.getArgument("input");

            Optional<CommissionPlan> planOpt = planRepository.findById(id);
            if (planOpt.isEmpty()) {
                return null;
            }

            CommissionPlan plan = planOpt.get();

            if (input.containsKey("name")) {
                plan.setName((String) input.get("name"));
            }
            if (input.containsKey("status")) {
                plan.setStatus((PlanStatus) input.get("status"));
            }
            if (input.containsKey("effectiveStartDate")) {
                plan.setEffectiveStartDate((LocalDate) input.get("effectiveStartDate"));
            }
            if (input.containsKey("effectiveEndDate")) {
                plan.setEffectiveEndDate((LocalDate) input.get("effectiveEndDate"));
            }

            return planRepository.save(plan);
        };
    }

    /**
     * Delete a commission plan.
     */
    public DataFetcher<Boolean> deleteCommissionPlan() {
        return environment -> {
            String id = environment.getArgument("id");
            return planRepository.deleteById(id);
        };
    }

    // ============================================================================
    // DISPUTE MUTATIONS
    // ============================================================================

    /**
     * Create a new dispute.
     */
    public DataFetcher<Dispute> createDispute() {
        return environment -> {
            Map<String, Object> input = environment.getArgument("input");

            String calculationId = (String) input.get("calculationId");
            String salesRepId = (String) input.get("salesRepId");
            String title = (String) input.get("title");
            String description = (String) input.get("description");

            Dispute dispute = new Dispute(calculationId, salesRepId, title, description);

            return disputeRepository.save(dispute);
        };
    }

    /**
     * Update an existing dispute.
     */
    public DataFetcher<Dispute> updateDispute() {
        return environment -> {
            String id = environment.getArgument("id");
            Map<String, Object> input = environment.getArgument("input");

            Optional<Dispute> disputeOpt = disputeRepository.findById(id);
            if (disputeOpt.isEmpty()) {
                return null;
            }

            Dispute dispute = disputeOpt.get();

            if (input.containsKey("managerId")) {
                dispute.setManagerId((String) input.get("managerId"));
            }
            if (input.containsKey("status")) {
                dispute.setStatus((DisputeStatus) input.get("status"));
            }
            if (input.containsKey("resolution")) {
                dispute.setResolution((String) input.get("resolution"));
            }
            if (input.containsKey("escalated")) {
                dispute.setEscalated((Boolean) input.get("escalated"));
            }

            return disputeRepository.save(dispute);
        };
    }

    /**
     * Delete a dispute.
     */
    public DataFetcher<Boolean> deleteDispute() {
        return environment -> {
            String id = environment.getArgument("id");
            return disputeRepository.deleteById(id);
        };
    }

    /**
     * Add a comment to a dispute.
     */
    public DataFetcher<Dispute> addDisputeComment() {
        return environment -> {
            Map<String, Object> input = environment.getArgument("input");

            String disputeId = (String) input.get("disputeId");
            String userId = (String) input.get("userId");
            String userName = (String) input.get("userName");
            String text = (String) input.get("text");

            Optional<Dispute> disputeOpt = disputeRepository.findById(disputeId);
            if (disputeOpt.isEmpty()) {
                return null;
            }

            Dispute dispute = disputeOpt.get();
            dispute.addUserComment(userId, userName, text);

            return disputeRepository.save(dispute);
        };
    }

    // ============================================================================
    // HELPER METHODS
    // ============================================================================

    /**
     * Helper method to create a DealProduct from input map.
     *
     * REUSABILITY:
     * Extracted to avoid code duplication between create and update operations.
     */
    private DealProduct createDealProductFromInput(Map<String, Object> input) {
        String productId = (String) input.get("productId");
        String productName = (String) input.get("productName");
        Integer quantity = (Integer) input.get("quantity");
        String priceStr = (String) input.get("price");
        BigDecimal price = new BigDecimal(priceStr);

        DealProduct product = new DealProduct(productId, productName, quantity, price);

        if (input.containsKey("discount")) {
            String discountStr = (String) input.get("discount");
            product.setDiscount(new BigDecimal(discountStr));
        }

        return product;
    }
}