package com.chapman.edu.commissions.api.grpc;

import com.chapman.edu.commissions.api.grpc.proto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * gRPC Client for the Commission Calculator API.
 *
 * This class demonstrates how to create a client application that connects to
 * the gRPC server and makes RPC calls. It serves as both a testing tool and
 * an educational example of gRPC client development.
 *
 * Key Concepts:
 *
 * 1. Channel Management:
 *    - ManagedChannel: Represents a connection to the gRPC server
 *    - Channel lifecycle: create → use → shutdown
 *    - Channels are expensive to create, should be reused
 *
 * 2. Client Stubs:
 *    - BlockingStub: Synchronous (blocks until response received)
 *    - Stub: Asynchronous callback-based
 *    - FutureStub: Asynchronous future-based
 *    This client uses BlockingStub for simplicity
 *
 * 3. Resource Management:
 *    - Must shutdown channel when done
 *    - Use try-with-resources or explicit shutdown
 *    - Graceful vs. forceful shutdown
 *
 * 4. Error Handling:
 *    - gRPC errors are StatusRuntimeException
 *    - Contains status code and description
 *    - Similar to HTTP error responses
 */
public class GrpcClient implements AutoCloseable {

    // Connection channel to the server
    private final ManagedChannel channel;

    // Blocking stub for synchronous calls (simpler than async)
    private final DealServiceGrpc.DealServiceBlockingStub dealService;

    /**
     * Constructor with default host and port.
     */
    public GrpcClient() {
        this("localhost", 50051);
    }

    /**
     * Constructor with custom host and port.
     *
     * Creates a ManagedChannel and initializes service stubs.
     *
     * @param host Server hostname (e.g., "localhost")
     * @param port Server port (e.g., 50051)
     */
    public GrpcClient(String host, int port) {
        // Create channel to server
        // usePlaintext() = no TLS encryption (for development only!)
        // In production, use TLS with proper certificates
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()  // No encryption (development only)
                .build();

        // Create blocking stub for synchronous calls
        // Each service needs its own stub
        this.dealService = DealServiceGrpc.newBlockingStub(channel);

        System.out.println("✓ Connected to gRPC server at " + host + ":" + port);
    }

    // ==================== Deal Service Methods ====================

    /**
     * Create a new deal.
     *
     * Demonstrates:
     * - Building a protobuf message with builder pattern
     * - Making an RPC call
     * - Handling the response
     *
     * @param title Deal title
     * @param value Deal value
     * @param salesRepId Sales representative ID
     * @return The created deal with generated ID
     */
    public com.chapman.edu.commissions.api.grpc.proto.Deal createDeal(
            String title, BigDecimal value, String salesRepId) {
        try {
            // Build the request using protobuf builder pattern
            // Builders are fluent and chainable
            CreateDealRequest request = CreateDealRequest.newBuilder()
                    .setTitle(title)
                    .setValue(Decimal.newBuilder().setValue(value.toPlainString()).build())
                    .setSalesRepId(salesRepId)
                    .build();

            // Make the RPC call (blocks until response)
            CreateDealResponse response = dealService.createDeal(request);

            System.out.println("✓ Deal created: " + response.getDeal().getId());
            return response.getDeal();

        } catch (StatusRuntimeException e) {
            // Handle gRPC errors
            System.err.println("✗ Failed to create deal: " + e.getStatus().getDescription());
            throw e;
        }
    }

    /**
     * Get a deal by ID.
     *
     * Demonstrates:
     * - Simple request with single field
     * - Handling NOT_FOUND errors
     *
     * @param dealId The deal ID to retrieve
     * @return The deal if found
     */
    public com.chapman.edu.commissions.api.grpc.proto.Deal getDeal(String dealId) {
        try {
            GetDealRequest request = GetDealRequest.newBuilder()
                    .setId(dealId)
                    .build();

            GetDealResponse response = dealService.getDeal(request);

            System.out.println("✓ Deal retrieved: " + response.getDeal().getId());
            return response.getDeal();

        } catch (StatusRuntimeException e) {
            System.err.println("✗ Failed to get deal: " + e.getStatus().getDescription());
            throw e;
        }
    }

    /**
     * List all deals.
     *
     * Demonstrates:
     * - Empty request (no filters)
     * - Handling collections (repeated fields)
     *
     * @return List of all deals
     */
    public ListDealsResponse listDeals() {
        return listDeals(null, null);
    }

    /**
     * List deals with optional filters.
     *
     * Demonstrates:
     * - Optional request parameters
     * - Filtering server-side
     *
     * @param statusFilter Optional status filter (e.g., "OPEN", "WON")
     * @param salesRepIdFilter Optional sales rep ID filter
     * @return Filtered list of deals
     */
    public ListDealsResponse listDeals(String statusFilter, String salesRepIdFilter) {
        try {
            // Build request with optional filters
            ListDealsRequest.Builder requestBuilder = ListDealsRequest.newBuilder();

            if (statusFilter != null && !statusFilter.isEmpty()) {
                requestBuilder.setStatusFilter(statusFilter);
            }
            if (salesRepIdFilter != null && !salesRepIdFilter.isEmpty()) {
                requestBuilder.setSalesRepIdFilter(salesRepIdFilter);
            }

            ListDealsRequest request = requestBuilder.build();
            ListDealsResponse response = dealService.listDeals(request);

            System.out.println("✓ Listed " + response.getTotalCount() + " deals");
            return response;

        } catch (StatusRuntimeException e) {
            System.err.println("✗ Failed to list deals: " + e.getStatus().getDescription());
            throw e;
        }
    }

    /**
     * Update an existing deal.
     *
     * Demonstrates:
     * - Updating resources
     * - Sending complete object
     *
     * @param deal The deal with updated values (must have ID set)
     * @return The updated deal
     */
    public com.chapman.edu.commissions.api.grpc.proto.Deal updateDeal(
            com.chapman.edu.commissions.api.grpc.proto.Deal deal) {
        try {
            UpdateDealRequest request = UpdateDealRequest.newBuilder()
                    .setDeal(deal)
                    .build();

            UpdateDealResponse response = dealService.updateDeal(request);

            System.out.println("✓ Deal updated: " + response.getDeal().getId());
            return response.getDeal();

        } catch (StatusRuntimeException e) {
            System.err.println("✗ Failed to update deal: " + e.getStatus().getDescription());
            throw e;
        }
    }

    /**
     * Delete a deal by ID.
     *
     * Demonstrates:
     * - Delete operations
     * - Boolean response indicating success
     *
     * @param dealId The deal ID to delete
     * @return true if deleted successfully
     */
    public boolean deleteDeal(String dealId) {
        try {
            DeleteDealRequest request = DeleteDealRequest.newBuilder()
                    .setId(dealId)
                    .build();

            DeleteDealResponse response = dealService.deleteDeal(request);

            if (response.getSuccess()) {
                System.out.println("✓ Deal deleted: " + dealId);
            } else {
                System.out.println("✗ Deal not found: " + dealId);
            }

            return response.getSuccess();

        } catch (StatusRuntimeException e) {
            System.err.println("✗ Failed to delete deal: " + e.getStatus().getDescription());
            throw e;
        }
    }

    // ==================== Resource Management ====================

    /**
     * Shutdown the channel gracefully.
     *
     * Channel shutdown:
     * 1. Stop accepting new calls
     * 2. Wait for in-flight calls to complete (up to timeout)
     * 3. Force shutdown if timeout exceeded
     *
     * @throws InterruptedException If interrupted while waiting
     */
    public void shutdown() throws InterruptedException {
        System.out.println("Shutting down gRPC client...");

        // Attempt graceful shutdown
        channel.shutdown();

        // Wait for termination (max 5 seconds)
        if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
            // Force shutdown if graceful shutdown times out
            System.err.println("Channel did not terminate, forcing shutdown...");
            channel.shutdownNow();

            // Wait again after forcing
            if (!channel.awaitTermination(2, TimeUnit.SECONDS)) {
                System.err.println("Channel did not terminate after forced shutdown");
            }
        }

        System.out.println("✓ gRPC client shut down");
    }

    /**
     * AutoCloseable implementation for try-with-resources.
     *
     * Allows usage like:
     * try (GrpcClient client = new GrpcClient()) {
     *     // use client
     * } // automatic shutdown
     */
    @Override
    public void close() {
        try {
            shutdown();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted during shutdown: " + e.getMessage());
        }
    }

    // ==================== Main Method (Demo) ====================

    /**
     * Main method demonstrating client usage.
     *
     * This method shows a complete workflow:
     * 1. Connect to server
     * 2. Create a deal
     * 3. Retrieve the deal
     * 4. List all deals
     * 5. Update the deal
     * 6. Delete the deal
     * 7. Disconnect
     *
     * @param args Command line arguments:
     *             - First arg: Server host (optional, default: localhost)
     *             - Second arg: Server port (optional, default: 50051)
     */
    public static void main(String[] args) {
        // Parse connection parameters
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 50051;

        System.out.println("Commission Calculator gRPC Client");
        System.out.println("==================================");
        System.out.println("Connecting to: " + host + ":" + port);
        System.out.println();

        // Use try-with-resources for automatic cleanup
        try (GrpcClient client = new GrpcClient(host, port)) {

            // 1. Create a deal
            System.out.println("1. Creating a new deal...");
            com.chapman.edu.commissions.api.grpc.proto.Deal createdDeal = client.createDeal(
                    "Enterprise Software License",
                    new BigDecimal("75000.00"),
                    "USER-1"
            );
            System.out.println("   Created deal ID: " + createdDeal.getId());
            System.out.println("   Title: " + createdDeal.getTitle());
            System.out.println("   Value: $" + createdDeal.getValue().getValue());
            System.out.println();

            // 2. Retrieve the deal
            System.out.println("2. Retrieving the deal...");
            com.chapman.edu.commissions.api.grpc.proto.Deal retrievedDeal =
                    client.getDeal(createdDeal.getId());
            System.out.println("   Retrieved deal: " + retrievedDeal.getTitle());
            System.out.println();

            // 3. List all deals
            System.out.println("3. Listing all deals...");
            ListDealsResponse allDeals = client.listDeals();
            System.out.println("   Total deals: " + allDeals.getTotalCount());
            for (com.chapman.edu.commissions.api.grpc.proto.Deal deal : allDeals.getDealsList()) {
                System.out.println("   - " + deal.getId() + ": " + deal.getTitle());
            }
            System.out.println();

            // 4. Update the deal
            System.out.println("4. Updating the deal...");
            com.chapman.edu.commissions.api.grpc.proto.Deal.Builder dealBuilder =
                    retrievedDeal.toBuilder();
            dealBuilder.setTitle("Updated: " + retrievedDeal.getTitle());
            dealBuilder.setStatus(DealStatus.WON);

            com.chapman.edu.commissions.api.grpc.proto.Deal updatedDeal =
                    client.updateDeal(dealBuilder.build());
            System.out.println("   Updated title: " + updatedDeal.getTitle());
            System.out.println("   New status: " + updatedDeal.getStatus());
            System.out.println();

            // 5. Filter deals by status
            System.out.println("5. Filtering deals by status (WON)...");
            ListDealsResponse wonDeals = client.listDeals("WON", null);
            System.out.println("   Won deals: " + wonDeals.getTotalCount());
            System.out.println();

            // 6. Delete the deal
            System.out.println("6. Deleting the deal...");
            boolean deleted = client.deleteDeal(createdDeal.getId());
            System.out.println("   Deletion successful: " + deleted);
            System.out.println();

            // 7. Verify deletion
            System.out.println("7. Verifying deletion...");
            try {
                client.getDeal(createdDeal.getId());
                System.out.println("   ERROR: Deal still exists!");
            } catch (StatusRuntimeException e) {
                System.out.println("   ✓ Deal not found (as expected)");
                System.out.println("   Status: " + e.getStatus().getCode());
            }
            System.out.println();

            System.out.println("==================================");
            System.out.println("All operations completed successfully!");

        } catch (StatusRuntimeException e) {
            System.err.println("RPC failed: " + e.getStatus());
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}