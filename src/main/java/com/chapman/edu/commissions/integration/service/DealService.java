package com.chapman.edu.commissions.integration.service;

import com.chapman.edu.commissions.api.rest.Repository;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * DealService - Business logic layer for Deal operations.
 *
 * This class demonstrates the Service Layer pattern:
 * - Encapsulates business logic separate from data access and presentation
 * - Coordinates between multiple repositories if needed
 * - Validates business rules before persisting data
 * - Provides transaction boundaries (simplified in this implementation)
 * - Acts as facade to complex operations
 *
 * Key Responsibilities:
 * - Business validation (e.g., deal value must be positive)
 * - Business rules enforcement (e.g., closed deals can't be modified)
 * - Complex queries and filtering
 * - Logging and error handling at business level
 *
 * Layer: Service Layer (Business Logic)
 *
 * @author Sergey L. Sundukovskiy
 * @version 1.0
 */
public class DealService {

    private static final Logger logger = LoggerFactory.getLogger(DealService.class);
    private final Repository<Deal> dealRepository;

    /**
     * Constructor with dependency injection.
     * Receives repository abstraction (not concrete implementation).
     *
     * @param dealRepository The repository for Deal persistence
     */
    public DealService(Repository<Deal> dealRepository) {
        this.dealRepository = dealRepository;
    }

    /**
     * Retrieves all deals.
     *
     * @return List of all deals
     */
    public List<Deal> getAllDeals() {
        logger.debug("Retrieving all deals");
        return dealRepository.findAll();
    }

    /**
     * Retrieves a deal by ID.
     *
     * @param id The deal ID
     * @return Optional containing the deal if found
     */
    public Optional<Deal> getDealById(String id) {
        logger.debug("Retrieving deal by id: {}", id);
        return dealRepository.findById(id);
    }

    /**
     * Retrieves deals by status.
     * Demonstrates filtering logic in the service layer.
     *
     * @param status The deal status to filter by
     * @return List of deals with the specified status
     */
    public List<Deal> getDealsByStatus(DealStatus status) {
        logger.debug("Retrieving deals by status: {}", status);
        return dealRepository.findAll().stream()
                .filter(deal -> deal.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves deals by sales representative.
     *
     * @param salesRepId The sales rep ID
     * @return List of deals for the specified sales rep
     */
    public List<Deal> getDealsBySalesRep(String salesRepId) {
        logger.debug("Retrieving deals for sales rep: {}", salesRepId);
        return dealRepository.findAll().stream()
                .filter(deal -> salesRepId.equals(deal.getSalesRepId()))
                .collect(Collectors.toList());
    }

    /**
     * Creates a new deal with business validation.
     *
     * Validates:
     * - Deal has a title
     * - Deal has a customer name
     * - Deal has at least one product
     * - Total value is positive
     * - Sales rep ID is provided
     *
     * @param deal The deal to create
     * @return The created deal with generated ID
     * @throws IllegalArgumentException if validation fails
     */
    public Deal createDeal(Deal deal) {
        logger.info("Creating new deal: {}", deal.getTitle());

        // Business validation
        validateDeal(deal);

        // Ensure status is set (default to OPEN if not specified)
        if (deal.getStatus() == null) {
            deal.setStatus(DealStatus.OPEN);
        }

        Deal savedDeal = dealRepository.save(deal);
        logger.info("Created deal with id: {}", savedDeal.getId());

        return savedDeal;
    }

    /**
     * Updates an existing deal with business validation.
     *
     * @param id The deal ID
     * @param updatedDeal The updated deal data
     * @return The updated deal
     * @throws IllegalArgumentException if validation fails or deal not found
     */
    public Deal updateDeal(String id, Deal updatedDeal) {
        logger.info("Updating deal: {}", id);

        // Verify deal exists
        Optional<Deal> existingDeal = dealRepository.findById(id);
        if (existingDeal.isEmpty()) {
            throw new IllegalArgumentException("Deal not found: " + id);
        }

        // Business rule: Cannot modify closed/cancelled deals
        Deal existing = existingDeal.get();
        if (existing.getStatus() == DealStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot modify cancelled deals");
        }

        // Validate updated deal
        validateDeal(updatedDeal);

        // Ensure ID is preserved
        updatedDeal.setId(id);

        Deal saved = dealRepository.save(updatedDeal);
        logger.info("Updated deal: {}", id);

        return saved;
    }

    /**
     * Deletes a deal.
     *
     * Business rule: Can only delete OPEN deals (not won/lost/cancelled).
     *
     * @param id The deal ID
     * @return true if deleted, false if not found
     * @throws IllegalArgumentException if deal cannot be deleted
     */
    public boolean deleteDeal(String id) {
        logger.info("Deleting deal: {}", id);

        Optional<Deal> deal = dealRepository.findById(id);
        if (deal.isEmpty()) {
            return false;
        }

        // Business rule validation
        if (deal.get().getStatus() != DealStatus.OPEN) {
            throw new IllegalArgumentException(
                "Can only delete OPEN deals. Current status: " + deal.get().getStatus()
            );
        }

        boolean deleted = dealRepository.deleteById(id);
        if (deleted) {
            logger.info("Deleted deal: {}", id);
        }

        return deleted;
    }

    /**
     * Closes a deal as WON.
     * Demonstrates business logic that coordinates multiple operations.
     *
     * @param id The deal ID
     * @return The updated deal
     * @throws IllegalArgumentException if deal cannot be closed
     */
    public Deal closeDealAsWon(String id) {
        logger.info("Closing deal as WON: {}", id);

        Optional<Deal> dealOpt = dealRepository.findById(id);
        if (dealOpt.isEmpty()) {
            throw new IllegalArgumentException("Deal not found: " + id);
        }

        Deal deal = dealOpt.get();

        // Business rule: Can only close OPEN deals
        if (deal.getStatus() != DealStatus.OPEN) {
            throw new IllegalArgumentException("Can only close OPEN deals");
        }

        // Update status
        deal.setStatus(DealStatus.WON);

        // Set close date if not already set
        if (deal.getCloseDate() == null) {
            deal.setCloseDate(java.time.LocalDate.now());
        }

        Deal updated = dealRepository.save(deal);
        logger.info("Closed deal as WON: {}", id);

        return updated;
    }

    /**
     * Calculates total pipeline value for a sales rep.
     * Demonstrates aggregation logic in service layer.
     *
     * @param salesRepId The sales rep ID
     * @return Total value of all OPEN deals for the rep
     */
    public BigDecimal calculatePipelineValue(String salesRepId) {
        logger.debug("Calculating pipeline value for sales rep: {}", salesRepId);

        return getDealsBySalesRep(salesRepId).stream()
                .filter(deal -> deal.getStatus() == DealStatus.OPEN)
                .map(Deal::calculateTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Validates a deal according to business rules.
     *
     * @param deal The deal to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateDeal(Deal deal) {
        if (deal.getTitle() == null || deal.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Deal title is required");
        }

        if (deal.getSalesRepId() == null || deal.getSalesRepId().trim().isEmpty()) {
            throw new IllegalArgumentException("Sales rep ID is required");
        }

        if (deal.getProducts() == null || deal.getProducts().isEmpty()) {
            throw new IllegalArgumentException("Deal must have at least one product");
        }

        BigDecimal totalValue = deal.calculateTotalValue();
        if (totalValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deal total value must be positive");
        }
    }
}