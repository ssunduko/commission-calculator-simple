package com.chapman.edu.commissions.api.graphql;

import com.chapman.edu.commissions.api.rest.Repository;
import com.chapman.edu.commissions.model.*;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * GraphQL Provider - Builds the GraphQL schema and execution engine.
 *
 * WHAT IS GRAPHQL PROVIDER?
 * -------------------------
 * This class is responsible for:
 * 1. Loading the GraphQL schema definition (SDL file)
 * 2. Wiring data fetchers to schema fields
 * 3. Building the executable GraphQL schema
 * 4. Creating the GraphQL execution engine
 *
 * SCHEMA-FIRST DEVELOPMENT:
 * ------------------------
 * GraphQL supports two approaches:
 * 1. Schema-First: Define schema in .graphqls file, then implement resolvers
 * 2. Code-First: Define schema in Java code using builders
 *
 * This implementation uses Schema-First because:
 * - Clear separation between API contract (schema) and implementation (resolvers)
 * - Schema serves as documentation
 * - Easier to review API changes
 * - Better for frontend-backend collaboration
 *
 * RUNTIME WIRING:
 * --------------
 * Runtime wiring connects schema fields to data fetchers:
 * - Type wiring: Links types in schema to resolvers
 * - Field wiring: Links fields to specific data fetcher functions
 * - Scalar wiring: Links custom scalars to coercion logic
 *
 * BUILDER PATTERN:
 * ---------------
 * Uses builder pattern for constructing complex RuntimeWiring:
 * - Fluent API (method chaining)
 * - Readable configuration
 * - Type-safe construction
 *
 * DEPENDENCY INJECTION:
 * --------------------
 * Repositories are injected and passed to DataFetchers.
 * This follows Dependency Inversion Principle.
 */
public class GraphQLProvider {

    private final Repository<Deal> dealRepository;
    private final Repository<User> userRepository;
    private final Repository<CommissionPlan> planRepository;
    private final Repository<Dispute> disputeRepository;

    private GraphQL graphQL;

    /**
     * Constructor with dependency injection.
     *
     * @param dealRepository Repository for Deal entities
     * @param userRepository Repository for User entities
     * @param planRepository Repository for CommissionPlan entities
     * @param disputeRepository Repository for Dispute entities
     */
    public GraphQLProvider(Repository<Deal> dealRepository,
                          Repository<User> userRepository,
                          Repository<CommissionPlan> planRepository,
                          Repository<Dispute> disputeRepository) {
        this.dealRepository = dealRepository;
        this.userRepository = userRepository;
        this.planRepository = planRepository;
        this.disputeRepository = disputeRepository;
    }

    /**
     * Initialize the GraphQL engine.
     * Loads schema, wires resolvers, and builds executable GraphQL instance.
     *
     * INITIALIZATION PATTERN:
     * Called once during application startup to build the GraphQL engine.
     */
    public void init() {
        // Step 1: Load GraphQL schema from SDL file
        TypeDefinitionRegistry typeRegistry = loadSchema();

        // Step 2: Wire data fetchers to schema fields
        RuntimeWiring runtimeWiring = buildRuntimeWiring();

        // Step 3: Generate executable schema
        GraphQLSchema graphQLSchema = buildSchema(typeRegistry, runtimeWiring);

        // Step 4: Create GraphQL execution engine
        // IMPORTANT: Introspection must be enabled for tools like GraphiQL, Playground, etc.
        // By default, introspection is enabled in graphql-java, but we make it explicit here.
        this.graphQL = GraphQL.newGraphQL(graphQLSchema)
                .build();
    }

    /**
     * Get the GraphQL execution engine.
     *
     * @return GraphQL instance for executing queries and mutations
     */
    public GraphQL getGraphQL() {
        return graphQL;
    }

    /**
     * Load the GraphQL schema from the SDL file.
     *
     * SDL (Schema Definition Language):
     * GraphQL schemas are defined in .graphqls files using SDL syntax.
     * Example:
     *   type Query {
     *     user(id: ID!): User
     *   }
     *
     * CLASSPATH RESOURCE:
     * Schema file is loaded from src/main/resources/graphql/schema.graphqls
     *
     * @return TypeDefinitionRegistry containing parsed schema
     */
    private TypeDefinitionRegistry loadSchema() {
        SchemaParser schemaParser = new SchemaParser();

        // Load schema file from classpath
        InputStream schemaStream = getClass().getClassLoader()
                .getResourceAsStream("graphql/schema.graphqls");

        if (schemaStream == null) {
            throw new RuntimeException("GraphQL schema file not found: graphql/schema.graphqls");
        }

        Reader schemaReader = new InputStreamReader(schemaStream);

        // Parse schema into TypeDefinitionRegistry
        return schemaParser.parse(schemaReader);
    }

    /**
     * Build runtime wiring that connects schema fields to data fetchers.
     *
     * WIRING STRUCTURE:
     * ----------------
     * RuntimeWiring connects three types of components:
     * 1. Scalars: Custom scalar types (Date, DateTime, BigDecimal)
     * 2. Queries: Read operations
     * 3. Mutations: Write operations
     * 4. Type Resolvers: Field-level resolvers for nested objects
     *
     * BUILDER PATTERN:
     * Uses fluent API for readable configuration.
     *
     * @return RuntimeWiring instance with all resolvers configured
     */
    private RuntimeWiring buildRuntimeWiring() {
        // Create DataFetchers instance with repositories
        DataFetchers dataFetchers = new DataFetchers(
                dealRepository,
                userRepository,
                planRepository,
                disputeRepository
        );

        return RuntimeWiring.newRuntimeWiring()
                // Register custom scalars
                .scalar(ScalarTypes.DATE)
                .scalar(ScalarTypes.DATETIME)
                .scalar(ScalarTypes.BIGDECIMAL)

                // Wire Query type fields
                .type("Query", typeWiring -> typeWiring
                        // Deal queries
                        .dataFetcher("deals", dataFetchers.getAllDeals())
                        .dataFetcher("deal", dataFetchers.getDealById())
                        .dataFetcher("dealsByStatus", dataFetchers.getDealsByStatus())
                        .dataFetcher("dealsBySalesRep", dataFetchers.getDealsBySalesRep())

                        // User queries
                        .dataFetcher("users", dataFetchers.getAllUsers())
                        .dataFetcher("user", dataFetchers.getUserById())
                        .dataFetcher("userByUsername", dataFetchers.getUserByUsername())
                        .dataFetcher("usersByRole", dataFetchers.getUsersByRole())

                        // Commission Plan queries
                        .dataFetcher("commissionPlans", dataFetchers.getAllCommissionPlans())
                        .dataFetcher("commissionPlan", dataFetchers.getCommissionPlanById())
                        .dataFetcher("commissionPlansByStatus", dataFetchers.getCommissionPlansByStatus())
                        .dataFetcher("activeCommissionPlansOnDate", dataFetchers.getActiveCommissionPlansOnDate())

                        // Dispute queries
                        .dataFetcher("disputes", dataFetchers.getAllDisputes())
                        .dataFetcher("dispute", dataFetchers.getDisputeById())
                        .dataFetcher("disputesBySalesRep", dataFetchers.getDisputesBySalesRep())
                        .dataFetcher("disputesByStatus", dataFetchers.getDisputesByStatus())
                )

                // Wire Mutation type fields
                .type("Mutation", typeWiring -> typeWiring
                        // Deal mutations
                        .dataFetcher("createDeal", dataFetchers.createDeal())
                        .dataFetcher("updateDeal", dataFetchers.updateDeal())
                        .dataFetcher("deleteDeal", dataFetchers.deleteDeal())

                        // User mutations
                        .dataFetcher("createUser", dataFetchers.createUser())
                        .dataFetcher("updateUser", dataFetchers.updateUser())
                        .dataFetcher("deleteUser", dataFetchers.deleteUser())

                        // Commission Plan mutations
                        .dataFetcher("createCommissionPlan", dataFetchers.createCommissionPlan())
                        .dataFetcher("updateCommissionPlan", dataFetchers.updateCommissionPlan())
                        .dataFetcher("deleteCommissionPlan", dataFetchers.deleteCommissionPlan())

                        // Dispute mutations
                        .dataFetcher("createDispute", dataFetchers.createDispute())
                        .dataFetcher("updateDispute", dataFetchers.updateDispute())
                        .dataFetcher("deleteDispute", dataFetchers.deleteDispute())
                        .dataFetcher("addDisputeComment", dataFetchers.addDisputeComment())
                )

                // Wire Deal type field resolvers
                .type("Deal", typeWiring -> typeWiring
                        .dataFetcher("salesRep", dataFetchers.getDealSalesRep())
                        .dataFetcher("calculatedTotalValue", dataFetchers.getDealCalculatedTotalValue())
                )

                // Wire DealProduct type field resolvers
                .type("DealProduct", typeWiring -> typeWiring
                        .dataFetcher("totalPrice", dataFetchers.getDealProductTotalPrice())
                )

                // Wire User type field resolvers
                .type("User", typeWiring -> typeWiring
                        .dataFetcher("fullName", dataFetchers.getUserFullName())
                        .dataFetcher("deals", dataFetchers.getUserDeals())
                )

                // Wire Dispute type field resolvers
                .type("Dispute", typeWiring -> typeWiring
                        .dataFetcher("salesRep", dataFetchers.getDisputeSalesRep())
                        .dataFetcher("manager", dataFetchers.getDisputeManager())
                )

                .build();
    }

    /**
     * Build the executable GraphQL schema.
     *
     * SCHEMA GENERATION:
     * -----------------
     * Combines type definitions (from SDL) with runtime wiring (resolvers)
     * to create an executable schema.
     *
     * The SchemaGenerator:
     * - Validates that all fields in the schema have resolvers
     * - Ensures type compatibility
     * - Builds optimized execution plan
     *
     * @param typeRegistry Parsed schema definitions
     * @param runtimeWiring Resolver mappings
     * @return Executable GraphQL schema
     */
    private GraphQLSchema buildSchema(TypeDefinitionRegistry typeRegistry,
                                     RuntimeWiring runtimeWiring) {
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }
}