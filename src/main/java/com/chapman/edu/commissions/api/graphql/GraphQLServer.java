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
        GraphQLProvider graphQLProvider = new GraphQLProvider(dealRepository, userRepository, planRepository, disputeRepository);
        graphQLProvider.init();
        GraphQLServlet graphQLServlet = new GraphQLServlet(graphQLProvider);
        Tomcat.addServlet(context, "GraphQLServlet", graphQLServlet);
        context.addServletMappingDecoded("/graphql", "GraphQLServlet");
        System.out.println("GraphQL servlet registered at /graphql");
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
            System.out.println("\nGraphQL Server started successfully!");
            System.out.println("\nGraphQL Endpoint:");
            System.out.println("  - http://localhost:" + tomcat.getConnector().getLocalPort() + "/graphql");
            System.out.println("\nExample Query:");
            System.out.println("  POST http://localhost:" + tomcat.getConnector().getLocalPort() + "/graphql");
            System.out.println("  Body: { \"query\": \"{ deals { id title value } }\" }");
            System.out.println("\nPress Ctrl+C to stop the server.");
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
