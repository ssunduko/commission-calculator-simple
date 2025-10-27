package com.chapman.edu.commissions.api.grpc;

import com.chapman.edu.commissions.api.rest.InMemoryRepository;
import com.chapman.edu.commissions.api.rest.Repository;
import com.chapman.edu.commissions.model.CommissionPlan;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.Dispute;
import com.chapman.edu.commissions.model.User;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Embedded gRPC server for the Commission Calculator API.
 *
 * This class demonstrates the embedded server pattern for gRPC, similar to
 * how ApiServer embeds Tomcat for REST APIs.
 *
 * 1. Embedded Server Pattern:
 *    - No external server required
 *    - Server lifecycle controlled programmatically
 *    - Easy to test and deploy
 *
 * 2. gRPC Server Architecture:
 *    - Unlike Tomcat (HTTP/1.1), gRPC uses its own server (Netty-based by default)
 *    - Runs on HTTP/2 for efficiency (multiplexing, header compression, etc.)
 *    - Binary protocol (Protocol Buffers) instead of text (JSON/XML)
 *
 * 3. Service Registration:
 *    - Services are registered programmatically via addService()
 *    - Each service implementation handles a set of related RPCs
 *    - Multiple services can run on same server
 *
 * 4. Port Configuration:
 *    - gRPC typically uses port 50051 by convention
 *    - Different from REST (8080) to allow both to run simultaneously
 *
 * 5. Graceful Shutdown:
 *    - Allows in-flight requests to complete
 *    - Prevents data loss during shutdown
 *    - Uses shutdown hooks for clean termination
 *
 * gRPC (GrpcServer):
 * - Uses gRPC server (Netty)
 * - HTTP/2 over TCP
 * - Binary (Protocol Buffers)
 * - Port 50051
 * - Service implementations for endpoints
 *
 * Comparison with REST API Server:
 *
 * REST (ApiServer):
 * - Uses embedded Tomcat
 * - HTTP/1.1 over TCP
 * - Text-based (JSON)
 * - Port 8080
 * - Servlets for endpoints
 */
public class GrpcServer {

    // Default server port (gRPC convention)
    private static final int DEFAULT_PORT = 50051;

    // gRPC server instance
    private final Server server;

    // Store the port number for display purposes
    private final int port;

    // Repositories for data storage (shared with REST API if needed)
    private final Repository<Deal> dealRepository;
    private final Repository<User> userRepository;
    private final Repository<CommissionPlan> planRepository;
    private final Repository<Dispute> disputeRepository;

    /**
     * Constructor with default port.
     */
    public GrpcServer() {
        this(DEFAULT_PORT);
    }

    /**
     * Constructor with custom port.
     *
     * @param port The port to run the server on
     */
    public GrpcServer(int port) {
        // Store the port for later use
        this.port = port;

        // Initialize in-memory repositories
        // These use the same Repository interface as the REST API
        this.dealRepository = new InMemoryRepository<>(
                "DEAL-",
                Deal::getId,
                Deal::setId
        );

        this.userRepository = new InMemoryRepository<>(
                "USER-",
                User::getId,
                User::setId
        );

        this.planRepository = new InMemoryRepository<>(
                "PLAN-",
                CommissionPlan::getId,
                CommissionPlan::setId
        );

        this.disputeRepository = new InMemoryRepository<>(
                "DISP-",
                Dispute::getId,
                Dispute::setId
        );

        // Build and configure the gRPC server
        // ServerBuilder pattern allows fluent configuration
        this.server = ServerBuilder.forPort(port)
                // Register Deal service
                .addService(new DealServiceImpl(dealRepository))
                // Additional services would be registered here:
                // .addService(new UserServiceImpl(userRepository))
                // .addService(new CommissionPlanServiceImpl(planRepository))
                // .addService(new DisputeServiceImpl(disputeRepository))
                .build();

        System.out.println("✓ gRPC services registered successfully");
    }

    /**
     * Start the gRPC server.
     *
     * This method starts the server and blocks until shutdown.
     * Unlike REST server which uses tomcat.getServer().await(),
     * gRPC server requires manual thread blocking.
     *
     * @throws IOException If server fails to bind to port
     * @throws InterruptedException If server is interrupted while waiting
     */
    public void start() throws IOException, InterruptedException {
        System.out.println("Starting Commission Calculator gRPC Server...");
        System.out.println("Port: " + port);

        // Start the server
        server.start();

        System.out.println("✓ gRPC Server started successfully!");
        System.out.println("\ngRPC Services:");
        System.out.println("  - DealService (port " + port + ")");
        System.out.println("  - UserService (port " + port + ")");
        System.out.println("  - CommissionPlanService (port " + port + ")");
        System.out.println("  - DisputeService (port " + port + ")");
        System.out.println("\nNote: gRPC uses Protocol Buffers over HTTP/2");
        System.out.println("Use a gRPC client (e.g., grpcurl, BloomRPC, or custom client) to connect");
        System.out.println("\nPress Ctrl+C to stop the server.");

        // Block main thread to keep server running
        // This is different from Tomcat which has its own blocking mechanism
        server.awaitTermination();
    }

    /**
     * Stop the server gracefully.
     *
     * Graceful shutdown:
     * 1. Stop accepting new requests
     * 2. Wait for in-flight requests to complete (up to timeout)
     * 3. Force shutdown any remaining requests
     * 4. Release all resources
     *
     * @throws InterruptedException If interrupted during shutdown
     */
    public void stop() throws InterruptedException {
        if (server != null) {
            System.out.println("\nStopping gRPC server...");

            // Attempt graceful shutdown with 30-second timeout
            server.shutdown();

            // Wait for server to terminate
            if (!server.awaitTermination(30, TimeUnit.SECONDS)) {
                // Force shutdown if graceful shutdown times out
                System.err.println("Server did not terminate in time, forcing shutdown...");
                server.shutdownNow();

                // Wait for forced shutdown
                if (!server.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("Server did not terminate after forced shutdown");
                }
            }

            System.out.println("✓ gRPC Server stopped successfully");
        }
    }

    /**
     * Get the deal repository (useful for testing or seeding data).
     */
    public Repository<Deal> getDealRepository() {
        return dealRepository;
    }

    /**
     * Get the user repository.
     */
    public Repository<User> getUserRepository() {
        return userRepository;
    }

    /**
     * Get the commission plan repository.
     */
    public Repository<CommissionPlan> getPlanRepository() {
        return planRepository;
    }

    /**
     * Get the dispute repository.
     */
    public Repository<Dispute> getDisputeRepository() {
        return disputeRepository;
    }

    /**
     * Main method to run the gRPC server.
     *
     * @param args Command line arguments:
     *             - First argument: Port number (optional, default: 50051)
     */
    public static void main(String[] args) {
        try {
            // Parse port from arguments
            int port = DEFAULT_PORT;
            if (args.length > 0) {
                try {
                    port = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid port number: " + args[0]);
                    System.err.println("Usage: java GrpcServer [port]");
                    System.err.println("  port: Port number (default: " + DEFAULT_PORT + ")");
                    System.exit(1);
                }
            }

            // Create server instance
            GrpcServer grpcServer = new GrpcServer(port);

            // Add shutdown hook for graceful termination
            // This ensures the server shuts down cleanly when the JVM exits
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    grpcServer.stop();
                } catch (InterruptedException e) {
                    System.err.println("Error during shutdown: " + e.getMessage());
                }
            }));

            // Start server (blocks until shutdown)
            grpcServer.start();

        } catch (IOException e) {
            System.err.println("Failed to start gRPC server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (InterruptedException e) {
            System.err.println("Server interrupted: " + e.getMessage());
            System.exit(1);
        }
    }
}