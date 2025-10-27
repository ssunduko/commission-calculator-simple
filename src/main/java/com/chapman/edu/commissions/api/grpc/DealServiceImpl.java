package com.chapman.edu.commissions.api.grpc;

import com.chapman.edu.commissions.api.grpc.proto.*;
import com.chapman.edu.commissions.api.rest.Repository;
import com.chapman.edu.commissions.model.Deal;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * gRPC service implementation for Deal management.
 *
 * This class implements the DealService gRPC service defined in deal_service.proto.
 * It extends the generated DealServiceGrpc.DealServiceImplBase class and provides
 * concrete implementations for all RPC methods.
 *
 * 1. Service Implementation Pattern:
 *    - Extends generated base class (DealServiceGrpc.DealServiceImplBase)
 *    - Implements each RPC method as defined in .proto file
 *    - Uses StreamObserver for asynchronous response handling
 *
 * 2. Unary RPC Pattern:
 *    - Client sends one request, server sends one response
 *    - All methods here use this pattern (most common in gRPC)
 *    - Each method receives: request object and StreamObserver for response
 *
 * 3. StreamObserver:
 *    - Callback interface for handling RPC results
 *    - Call onNext() to send response data
 *    - Call onCompleted() to signal successful completion
 *    - Call onError() to signal an error condition
 *
 * 4. Error Handling:
 *    - gRPC uses Status codes (similar to HTTP status codes)
 *    - Common codes: OK, NOT_FOUND, INVALID_ARGUMENT, INTERNAL
 *    - Errors sent via responseObserver.onError(Status.XXX.asRuntimeException())
 *
 * 5. Repository Pattern:
 *    - Service delegates data storage to Repository
 *    - Separation of concerns: service handles RPC, repository handles data
 *    - Allows easy swapping of data storage implementation
 *
 * 6. Model Conversion:
 *    - Uses ModelConverter to translate domain ↔ proto messages
 *    - Keeps domain model independent of gRPC
 *    - Allows evolution of either layer independently
 */
public class DealServiceImpl extends DealServiceGrpc.DealServiceImplBase {

    // Repository for managing Deal entities
    // Injected via constructor (Dependency Injection pattern)
    private final Repository<Deal> dealRepository;

    /**
     * Constructor with dependency injection.
     *
     * @param dealRepository Repository for deal data access
     */
    public DealServiceImpl(Repository<Deal> dealRepository) {
        this.dealRepository = dealRepository;
    }

    /**
     * Create a new deal.
     *
     * gRPC Method Pattern:
     * 1. Receive request (CreateDealRequest)
     * 2. Convert proto to domain model
     * 3. Execute business logic (save to repository)
     * 4. Convert domain model back to proto
     * 5. Send response via responseObserver.onNext()
     * 6. Complete the RPC via responseObserver.onCompleted()
     *
     * Error Handling:
     * - Catches exceptions and converts to gRPC Status.INTERNAL error
     * - In production, you'd want more specific error handling
     */
    @Override
    public void createDeal(CreateDealRequest request, StreamObserver<CreateDealResponse> responseObserver) {
        try {
            // Convert proto request to domain model
            Deal deal = new Deal();
            deal.setTitle(request.getTitle());
            deal.setValue(ModelConverter.fromProtoDecimal(request.getValue()));
            deal.setSalesRepId(request.getSalesRepId());

            // Set default status for new deals
            deal.setStatus(com.chapman.edu.commissions.model.DealStatus.OPEN);

            // Convert product list
            if (request.getProductsCount() > 0) {
                List<com.chapman.edu.commissions.model.DealProduct> products =
                    request.getProductsList()
                        .stream()
                        .map(ModelConverter::fromProtoDealProduct)
                        .collect(Collectors.toList());
                deal.setProducts(products);
            }

            // Save via repository (repository generates ID)
            Deal savedDeal = dealRepository.save(deal);

            // Convert domain model to proto and build response
            com.chapman.edu.commissions.api.grpc.proto.Deal protoDeal =
                ModelConverter.toProtoDeal(savedDeal);

            CreateDealResponse response = CreateDealResponse.newBuilder()
                    .setDeal(protoDeal)
                    .build();

            // Send response and complete
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            // Handle error by sending gRPC error status
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Failed to create deal: " + e.getMessage())
                    .asRuntimeException()
            );
        }
    }

    /**
     * Get a specific deal by ID.
     *
     * Demonstrates NOT_FOUND error handling when resource doesn't exist.
     * This is similar to HTTP 404 in REST APIs.
     */
    @Override
    public void getDeal(GetDealRequest request, StreamObserver<GetDealResponse> responseObserver) {
        try {
            String dealId = request.getId();

            // Validate request
            if (dealId == null || dealId.isEmpty()) {
                responseObserver.onError(
                    Status.INVALID_ARGUMENT
                        .withDescription("Deal ID is required")
                        .asRuntimeException()
                );
                return;
            }

            // Lookup deal in repository
            Optional<Deal> dealOpt = dealRepository.findById(dealId);

            if (dealOpt.isEmpty()) {
                // Deal not found - return NOT_FOUND error
                responseObserver.onError(
                    Status.NOT_FOUND
                        .withDescription("Deal not found: " + dealId)
                        .asRuntimeException()
                );
                return;
            }

            // Convert and return found deal
            com.chapman.edu.commissions.api.grpc.proto.Deal protoDeal =
                ModelConverter.toProtoDeal(dealOpt.get());

            GetDealResponse response = GetDealResponse.newBuilder()
                    .setDeal(protoDeal)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Failed to get deal: " + e.getMessage())
                    .asRuntimeException()
            );
        }
    }

    /**
     * List all deals with optional filtering.
     *
     * Demonstrates:
     * - Handling collection responses (repeated fields)
     * - Optional filtering based on request parameters
     * - Stream processing for filtering
     */
    @Override
    public void listDeals(ListDealsRequest request, StreamObserver<ListDealsResponse> responseObserver) {
        try {
            // Get all deals from repository
            List<Deal> deals = dealRepository.findAll();

            // Apply status filter if provided
            String statusFilter = request.getStatusFilter();
            if (statusFilter != null && !statusFilter.isEmpty()) {
                try {
                    com.chapman.edu.commissions.model.DealStatus status =
                        com.chapman.edu.commissions.model.DealStatus.valueOf(statusFilter.toUpperCase());

                    deals = deals.stream()
                            .filter(deal -> deal.getStatus() == status)
                            .collect(Collectors.toList());
                } catch (IllegalArgumentException e) {
                    responseObserver.onError(
                        Status.INVALID_ARGUMENT
                            .withDescription("Invalid status value: " + statusFilter)
                            .asRuntimeException()
                    );
                    return;
                }
            }

            // Apply sales rep filter if provided
            String salesRepIdFilter = request.getSalesRepIdFilter();
            if (salesRepIdFilter != null && !salesRepIdFilter.isEmpty()) {
                deals = deals.stream()
                        .filter(deal -> salesRepIdFilter.equals(deal.getSalesRepId()))
                        .collect(Collectors.toList());
            }

            // Convert all deals to proto
            List<com.chapman.edu.commissions.api.grpc.proto.Deal> protoDeals = deals.stream()
                    .map(ModelConverter::toProtoDeal)
                    .collect(Collectors.toList());

            // Build response with deal list and count
            ListDealsResponse response = ListDealsResponse.newBuilder()
                    .addAllDeals(protoDeals)
                    .setTotalCount(protoDeals.size())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Failed to list deals: " + e.getMessage())
                    .asRuntimeException()
            );
        }
    }

    /**
     * Update an existing deal.
     *
     * Demonstrates update pattern:
     * 1. Check if resource exists
     * 2. Update the resource
     * 3. Return the updated resource
     */
    @Override
    public void updateDeal(UpdateDealRequest request, StreamObserver<UpdateDealResponse> responseObserver) {
        try {
            // Convert proto to domain model
            Deal deal = ModelConverter.fromProtoDeal(request.getDeal());

            if (deal.getId() == null || deal.getId().isEmpty()) {
                responseObserver.onError(
                    Status.INVALID_ARGUMENT
                        .withDescription("Deal ID is required for update")
                        .asRuntimeException()
                );
                return;
            }

            // Check if deal exists
            Optional<Deal> existingDeal = dealRepository.findById(deal.getId());
            if (existingDeal.isEmpty()) {
                responseObserver.onError(
                    Status.NOT_FOUND
                        .withDescription("Deal not found: " + deal.getId())
                        .asRuntimeException()
                );
                return;
            }

            // Update the deal
            Deal updatedDeal = dealRepository.save(deal);

            // Convert and return updated deal
            com.chapman.edu.commissions.api.grpc.proto.Deal protoDeal =
                ModelConverter.toProtoDeal(updatedDeal);

            UpdateDealResponse response = UpdateDealResponse.newBuilder()
                    .setDeal(protoDeal)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Failed to update deal: " + e.getMessage())
                    .asRuntimeException()
            );
        }
    }

    /**
     * Delete a deal by ID.
     *
     * Demonstrates delete operation with success/failure indication.
     */
    @Override
    public void deleteDeal(DeleteDealRequest request, StreamObserver<DeleteDealResponse> responseObserver) {
        try {
            String dealId = request.getId();

            if (dealId == null || dealId.isEmpty()) {
                responseObserver.onError(
                    Status.INVALID_ARGUMENT
                        .withDescription("Deal ID is required for deletion")
                        .asRuntimeException()
                );
                return;
            }

            // Attempt deletion
            boolean deleted = dealRepository.deleteById(dealId);

            // Build response based on deletion result
            DeleteDealResponse response = DeleteDealResponse.newBuilder()
                    .setSuccess(deleted)
                    .setMessage(deleted ? "Deal deleted successfully" : "Deal not found: " + dealId)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Failed to delete deal: " + e.getMessage())
                    .asRuntimeException()
            );
        }
    }
}