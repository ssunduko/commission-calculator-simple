package com.chapman.edu.commissions.integration.dto;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DealMapper - Converts between Deal entities and Deal DTOs.
 *
 * **MAPPER PATTERN EXPLAINED:**
 *
 * A Mapper (or Converter) class is responsible for transforming data between different
 * representations. In the DTO pattern, mappers convert between domain entities and DTOs.
 *
 * **Why use Mappers?**
 * 1. **Separation of Concerns:** Keeps conversion logic out of controllers and entities.
 *    Controllers don't need to know how to build DTOs from entities.
 *
 * 2. **Reusability:** Mapping logic is centralized and can be reused across controllers.
 *    If conversion logic changes, we only update it in one place.
 *
 * 3. **Testability:** Mapping logic can be unit tested independently.
 *    Ensures correct conversion without needing to test entire controller flow.
 *
 * 4. **Flexibility:** Can handle complex transformations:
 *    - Flattening nested objects
 *    - Combining multiple entities into one DTO
 *    - Formatting dates/numbers for display
 *    - Excluding sensitive or internal fields
 *
 * **Design Decisions:**
 * - Static methods (no instance state needed)
 * - Null-safe (handles null inputs gracefully)
 * - Bidirectional (entity->DTO and DTO->entity)
 *
 * **Alternative Approaches:**
 * In production systems, consider using mapping frameworks:
 * - MapStruct: Compile-time code generation, fastest performance
 * - ModelMapper: Runtime reflection-based mapping
 * - Orika: Runtime bytecode generation
 *
 * For educational purposes, we use manual mapping to show exactly what happens.
 *
 * @author Sergey L. Sundukovskiy
 * @version 1.0
 */
public class DealMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Converts a Deal domain entity to a DealDTO (for API responses).
     *
     * This is used when sending deal data TO the client.
     * Includes all fields that should be visible to API consumers.
     *
     * @param deal The domain entity
     * @return The DTO for API response, or null if input is null
     */
    public static DealDTO toDTO(Deal deal) {
        if (deal == null) {
            return null;
        }

        DealDTO dto = new DealDTO();
        dto.setId(deal.getId());
        dto.setTitle(deal.getTitle());
        dto.setStatus(deal.getStatus());
        dto.setSalesRepId(deal.getSalesRepId());
        dto.setCloseDate(deal.getCloseDate());

        // Convert nested entities to DTOs
        if (deal.getProducts() != null) {
            List<DealProductDTO> productDTOs = deal.getProducts().stream()
                    .map(DealMapper::toProductDTO)
                    .collect(Collectors.toList());
            dto.setProducts(productDTOs);
        }

        // Calculate and include computed field
        dto.setTotalValue(deal.calculateTotalValue());

        // Format dates as strings for consistent API representation
        if (deal.getCreatedDate() != null) {
            dto.setCreatedDate(deal.getCreatedDate().format(DATE_FORMATTER));
        }
        if (deal.getLastModifiedDate() != null) {
            dto.setLastModifiedDate(deal.getLastModifiedDate().format(DATE_FORMATTER));
        }

        return dto;
    }

    /**
     * Converts a list of Deal entities to a list of DealDTOs.
     *
     * Convenience method for converting collections.
     * Used by controller methods that return multiple deals.
     *
     * @param deals List of domain entities
     * @return List of DTOs
     */
    public static List<DealDTO> toDTOList(List<Deal> deals) {
        if (deals == null) {
            return new ArrayList<>();
        }
        return deals.stream()
                .map(DealMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converts a CreateDealRequest DTO to a Deal entity (for creation).
     *
     * This is used when receiving deal data FROM the client for creation.
     * Does NOT set server-controlled fields (id, createdDate, etc.).
     *
     * @param request The create request DTO
     * @return The domain entity ready for service layer processing
     */
    public static Deal fromCreateRequest(CreateDealRequest request) {
        if (request == null) {
            return null;
        }

        Deal deal = new Deal();
        deal.setTitle(request.getTitle());
        deal.setSalesRepId(request.getSalesRepId());
        deal.setStatus(request.getStatus());

        // Convert DTOs to entities
        if (request.getProducts() != null) {
            List<DealProduct> products = request.getProducts().stream()
                    .map(DealMapper::fromProductDTO)
                    .collect(Collectors.toList());
            deal.setProducts(products);
        }

        // Calculate value from products
        deal.setValue(deal.calculateTotalValue());

        // Note: ID, status, createdDate, lastModifiedDate are set by the service/repository
        // We don't allow clients to control these fields

        return deal;
    }

    /**
     * Converts an UpdateDealRequest DTO to a Deal entity (for updates).
     *
     * Similar to fromCreateRequest, but used for update operations.
     * In this simple implementation, they're identical, but they could differ:
     * - Update might handle partial data (PATCH semantics)
     * - Update might preserve certain fields that can't be changed
     *
     * @param request The update request DTO
     * @return The domain entity ready for service layer processing
     */
    public static Deal fromUpdateRequest(UpdateDealRequest request) {
        if (request == null) {
            return null;
        }

        Deal deal = new Deal();
        deal.setTitle(request.getTitle());
        deal.setSalesRepId(request.getSalesRepId());
        
        // Handle status field
        if (request.getStatus() != null) {
            try {
                deal.setStatus(com.chapman.edu.commissions.model.DealStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // If invalid status, leave as null for service layer validation
                deal.setStatus(null);
            }
        }

        // Convert DTOs to entities
        if (request.getProducts() != null) {
            List<DealProduct> products = request.getProducts().stream()
                    .map(DealMapper::fromProductDTO)
                    .collect(Collectors.toList());
            deal.setProducts(products);
        }

        // Calculate value from products
        deal.setValue(deal.calculateTotalValue());

        return deal;
    }

    /**
     * Converts a DealProduct entity to a DealProductDTO.
     *
     * Helper method for converting nested objects.
     * Demonstrates that mappers can be composed for complex object graphs.
     *
     * @param product The domain entity
     * @return The DTO
     */
    private static DealProductDTO toProductDTO(DealProduct product) {
        if (product == null) {
            return null;
        }

        DealProductDTO dto = new DealProductDTO();
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setDiscount(product.getDiscount());

        return dto;
    }

    /**
     * Converts a DealProductDTO to a DealProduct entity.
     *
     * Helper method for converting nested objects from API requests.
     *
     * @param dto The DTO
     * @return The domain entity
     */
    private static DealProduct fromProductDTO(DealProductDTO dto) {
        if (dto == null) {
            return null;
        }

        DealProduct product = new DealProduct();
        product.setProductId(dto.getProductId());
        product.setProductName(dto.getProductName());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setDiscount(dto.getDiscount() != null ? dto.getDiscount() : java.math.BigDecimal.ZERO);

        return product;
    }
}