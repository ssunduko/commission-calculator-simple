package com.chapman.edu.commissions.api.grpc;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;

/**
 * Base class for gRPC integration tests.
 *
 * This class manages the lifecycle of the gRPC server for integration testing.
 * It starts the server once before all tests and shuts it down after all tests complete.
 *
 * Key Concepts:
 *
 * 1. Integration Testing:
 *    - Tests the complete system (client → server → repository)
 *    - Uses real gRPC communication (not mocks)
 *    - Validates end-to-end behavior
 *
 * 2. Test Lifecycle Management:
 *    - @BeforeAll: Start server once for all tests (faster than per-test)
 *    - @AfterAll: Clean shutdown after all tests
 *    - Static fields: Server and client shared across test methods
 *
 * 3. Test Isolation:
 *    - Each test should be independent
 *    - Use unique IDs or clean up after tests
 *    - In-memory repository is reset between test classes
 *
 * 4. Port Management:
 *    - Uses different port (50052) to avoid conflicts with dev server
 *    - Could use random port for parallel test execution
 *
 * Usage:
 * ```java
 * public class MyGrpcTest extends GrpcServerTestBase {
 *     @Test
 *     void testSomething() {
 *         // Use 'client' to make RPC calls
 *         // Use 'server' to access repositories for verification
 *     }
 * }
 * ```
 */
public class GrpcServerTestBase {

    // Test server instance (shared across all tests in the class)
    protected static GrpcServer server;

    // Test client instance (shared across all tests in the class)
    protected static GrpcClient client;

    // Test server port (different from dev server to avoid conflicts)
    private static final int TEST_PORT = 50052;

    /**
     * Start the gRPC server before running any tests.
     *
     * This method runs once per test class, not once per test method.
     * Using @BeforeAll is more efficient than @BeforeEach when the server
     * can be safely shared across tests.
     *
     * @throws IOException If server fails to start
     */
    @BeforeAll
    public static void startServer() throws IOException {
        System.out.println("=".repeat(60));
        System.out.println("Starting gRPC server for integration tests...");
        System.out.println("=".repeat(60));

        // Create and start server on test port
        server = new GrpcServer(TEST_PORT);

        // Start server in background thread so tests can run
        Thread serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (IOException | InterruptedException e) {
                System.err.println("Test server failed: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // Set as daemon so it doesn't prevent JVM shutdown
        serverThread.setDaemon(true);
        serverThread.start();

        // Give server time to start
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Create client connected to test server
        client = new GrpcClient("localhost", TEST_PORT);

        System.out.println("✓ Test server and client ready");
        System.out.println();
    }

    /**
     * Stop the gRPC server after all tests complete.
     *
     * This method runs once after all test methods in the class have finished.
     * Ensures proper cleanup of resources.
     *
     * @throws InterruptedException If interrupted during shutdown
     */
    @AfterAll
    public static void stopServer() throws InterruptedException {
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("Shutting down test server...");
        System.out.println("=".repeat(60));

        // Close client
        if (client != null) {
            client.close();
        }

        // Stop server
        if (server != null) {
            server.stop();
        }

        System.out.println("✓ Test server and client stopped");
    }
}