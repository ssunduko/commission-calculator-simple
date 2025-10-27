package com.chapman.edu.commissions.api.graphql;

import com.chapman.edu.commissions.api.rest.InMemoryRepository;
import com.chapman.edu.commissions.api.rest.Repository;
import com.chapman.edu.commissions.api.rest.SampleDataLoader;
import com.chapman.edu.commissions.model.*;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import java.io.File;

/**
 * GraphQL Server - Embedded Tomcat server for GraphQL API.
 * Loads sample data by default on startup.
 */
public class GraphQLServer {

    private static final int DEFAULT_PORT = 8081;
    private final Tomcat tomcat;
    private final Repository<Deal> dealRepository;
    private final Repository<User> userRepository;
    private final Repository<CommissionPlan> planRepository;
    private final Repository<Dispute> disputeRepository;

    public GraphQLServer(int port) {
        this.dealRepository = new InMemoryRepository<>("DEAL-", Deal::getId, Deal::setId);
        this.userRepository = new InMemoryRepository<>("USER-", User::getId, User::setId);
        this.planRepository = new InMemoryRepository<>("PLAN-", CommissionPlan::getId, CommissionPlan::setId);
        this.disputeRepository = new InMemoryRepository<>("DISPUTE-", Dispute::getId, Dispute::setId);
        this.tomcat = new Tomcat();
        configureTomcat(port);
    }

    private void configureTomcat(int port) {
        tomcat.setPort(port);
        tomcat.getConnector();
        String baseDir = createTempDirectory();
        tomcat.setBaseDir(baseDir);
        Context context = tomcat.addContext("", baseDir);

        // Initialize GraphQL provider
        GraphQLProvider graphQLProvider = new GraphQLProvider(dealRepository, userRepository, planRepository, disputeRepository);
        graphQLProvider.init();

        // Register GraphQL API endpoint
        GraphQLServlet graphQLServlet = new GraphQLServlet(graphQLProvider);
        Tomcat.addServlet(context, "GraphQLServlet", graphQLServlet);
        context.addServletMappingDecoded("/graphql", "GraphQLServlet");
        System.out.println("GraphQL API endpoint registered at /graphql");

        // Register GraphQL development tools
        String graphQLEndpoint = "http://localhost:" + port + "/graphql";

        // GraphQL Tools Index Page
        GraphQLIndexServlet indexServlet = new GraphQLIndexServlet(port);
        Tomcat.addServlet(context, "GraphQLIndexServlet", indexServlet);
        context.addServletMappingDecoded("/", "GraphQLIndexServlet");
        System.out.println("GraphQL tools index registered at /");

        // GraphiQL IDE
        GraphiQLServlet graphiQLServlet = new GraphiQLServlet(graphQLEndpoint);
        Tomcat.addServlet(context, "GraphiQLServlet", graphiQLServlet);
        context.addServletMappingDecoded("/graphiql", "GraphiQLServlet");
        System.out.println("GraphiQL IDE registered at /graphiql");

        // GraphQL Playground
        GraphQLPlaygroundServlet playgroundServlet = new GraphQLPlaygroundServlet(graphQLEndpoint);
        Tomcat.addServlet(context, "GraphQLPlaygroundServlet", playgroundServlet);
        context.addServletMappingDecoded("/playground", "GraphQLPlaygroundServlet");
        System.out.println("GraphQL Playground registered at /playground");

        // GraphQL Schema Endpoint
        GraphQLSchemaServlet schemaServlet = new GraphQLSchemaServlet(graphQLProvider);
        Tomcat.addServlet(context, "GraphQLSchemaServlet", schemaServlet);
        context.addServletMappingDecoded("/schema", "GraphQLSchemaServlet");
        System.out.println("GraphQL Schema endpoint registered at /schema");
    }

    private String createTempDirectory() {
        try {
            File tempDir = File.createTempFile("tomcat", "");
            tempDir.delete();
            tempDir.mkdir();
            return tempDir.getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create temp directory", e);
        }
    }

    public void start() {
        try {
            tomcat.start();
            int serverPort = tomcat.getConnector().getLocalPort();

            System.out.println("\n╔══════════════════════════════════════════════════════════════════╗");
            System.out.println("║     GraphQL Server Started Successfully!                         ║");
            System.out.println("╚══════════════════════════════════════════════════════════════════╝");

            System.out.println("\n📊 GraphQL Development Tools:");
            System.out.println("  🏠 Index Page:         http://localhost:" + serverPort + "/");
            System.out.println("  🔍 GraphiQL IDE:       http://localhost:" + serverPort + "/graphiql");
            System.out.println("  🎮 Playground:         http://localhost:" + serverPort + "/playground");

            System.out.println("\n🔌 GraphQL API Endpoints:");
            System.out.println("  📡 GraphQL API:        http://localhost:" + serverPort + "/graphql");
            System.out.println("  📄 Schema (SDL):       http://localhost:" + serverPort + "/schema");

            System.out.println("\n📝 Example Queries:");
            System.out.println("  POST http://localhost:" + serverPort + "/graphql");
            System.out.println("  Content-Type: application/json");
            System.out.println("  Body: { \"query\": \"{ deals { id title value } }\" }");

            System.out.println("\n  GET http://localhost:" + serverPort + "/graphql?query={deals{id title}}");

            System.out.println("\n💡 Pro Tip: Open http://localhost:" + serverPort + "/ in your browser");
            System.out.println("            to access all GraphQL development tools!\n");

            System.out.println("Press Ctrl+C to stop the server.\n");

            tomcat.getServer().await();
        } catch (LifecycleException e) {
            throw new RuntimeException("Failed to start server", e);
        }
    }

    public void stop() throws LifecycleException {
        tomcat.stop();
        tomcat.destroy();
        System.out.println("Server stopped successfully");
    }

    public Repository<Deal> getDealRepository() { return dealRepository; }
    public Repository<User> getUserRepository() { return userRepository; }
    public Repository<CommissionPlan> getPlanRepository() { return planRepository; }
    public Repository<Dispute> getDisputeRepository() { return disputeRepository; }

    public static void main(String[] args) {
        try {
            System.out.println("Starting Commission Calculator GraphQL Server...");
            int port = DEFAULT_PORT;
            boolean loadSampleData = true;
            for (String arg : args) {
                if ("--no-sample-data".equals(arg)) {
                    loadSampleData = false;
                } else {
                    try {
                        port = Integer.parseInt(arg);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid argument: " + arg);
                    }
                }
            }
            System.out.println("Port: " + port);
            GraphQLServer server = new GraphQLServer(port);
            if (loadSampleData) {
                System.out.println("\n=== Loading Sample Data ===");
                SampleDataLoader dataLoader = new SampleDataLoader(
                    server.getDealRepository(),
                    server.getUserRepository(),
                    server.getPlanRepository(),
                    server.getDisputeRepository()
                );
                dataLoader.loadAllData();
                System.out.println("===========================\n");
            }
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try { server.stop(); } catch (LifecycleException e) { e.printStackTrace(); }
            }));
            server.start();
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
