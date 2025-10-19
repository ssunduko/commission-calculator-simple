package com.chapman.edu.commissions.api.soap.mapper;

import com.chapman.edu.commissions.api.soap.dto.*;
import com.chapman.edu.commissions.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to convert between domain models and DTOs.
 *
 * WHY A MAPPER?
 * ------------
 * - Centralized conversion logic
 * - Separation of API layer from domain layer
 * - Consistent date/enum formatting
 * - Reusable across multiple services
 *
 * DESIGN PATTERN:
 * --------------
 * This is the Mapper pattern (also known as Converter pattern).
 * It provides bidirectional conversion between domain objects and DTOs.
 */
public class DomainMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // ============================================================================
    // DEAL MAPPINGS
    // ============================================================================

    public static DealDTO toDTO(Deal deal) {
        if (deal == null) return null;

        DealDTO dto = new DealDTO();
        dto.setId(deal.getId());
        dto.setTitle(deal.getTitle());
        dto.setValue(deal.getValue());
        dto.setStatus(deal.getStatus().name());
        dto.setSalesRepId(deal.getSalesRepId());

        if (deal.getCloseDate() != null) {
            dto.setCloseDate(deal.getCloseDate().format(DATE_FORMATTER));
        }
        if (deal.getCreatedDate() != null) {
            dto.setCreatedDate(deal.getCreatedDate().format(DATE_FORMATTER));
        }
        if (deal.getLastModifiedDate() != null) {
            dto.setLastModifiedDate(deal.getLastModifiedDate().format(DATE_FORMATTER));
        }

        dto.setProducts(deal.getProducts().stream()
                .map(DomainMapper::toDTO)
                .collect(Collectors.toList()));

        dto.setCalculatedTotalValue(deal.calculateTotalValue());

        return dto;
    }

    public static Deal fromDTO(DealDTO dto) {
        if (dto == null) return null;

        Deal deal = new Deal(dto.getTitle(), dto.getValue(), dto.getSalesRepId());
        deal.setId(dto.getId());

        if (dto.getStatus() != null) {
            deal.setStatus(DealStatus.valueOf(dto.getStatus()));
        }
        if (dto.getCloseDate() != null) {
            deal.setCloseDate(LocalDate.parse(dto.getCloseDate(), DATE_FORMATTER));
        }

        if (dto.getProducts() != null) {
            dto.getProducts().forEach(productDTO -> {
                DealProduct product = fromDTO(productDTO);
                deal.addProduct(product);
            });
        }

        return deal;
    }

    // ============================================================================
    // DEAL PRODUCT MAPPINGS
    // ============================================================================

    public static DealProductDTO toDTO(DealProduct product) {
        if (product == null) return null;

        DealProductDTO dto = new DealProductDTO();
        dto.setId(product.getId());
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setQuantity(product.getQuantity());
        dto.setPrice(product.getPrice());
        dto.setDiscount(product.getDiscount());
        dto.setDealId(product.getDealId());
        dto.setTotalPrice(product.calculateTotalPrice());

        return dto;
    }

    public static DealProduct fromDTO(DealProductDTO dto) {
        if (dto == null) return null;

        DealProduct product = new DealProduct(
                dto.getProductId(),
                dto.getProductName(),
                dto.getQuantity(),
                dto.getPrice()
        );
        product.setId(dto.getId());
        product.setDiscount(dto.getDiscount());
        product.setDealId(dto.getDealId());

        return product;
    }

    // ============================================================================
    // USER MAPPINGS
    // ============================================================================

    public static UserDTO toDTO(User user) {
        if (user == null) return null;

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setFullName(user.getFullName());
        dto.setActive(user.isActive());

        dto.setRoles(user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toList()));

        if (user.getLastLogin() != null) {
            dto.setLastLogin(user.getLastLogin().format(DATETIME_FORMATTER));
        }
        if (user.getCreatedDate() != null) {
            dto.setCreatedDate(user.getCreatedDate().format(DATE_FORMATTER));
        }

        dto.setCreatedBy(user.getCreatedBy());
        dto.setManagerId(user.getManagerId());
        dto.setDepartment(user.getDepartment());
        dto.setTerritory(user.getTerritory());

        return dto;
    }

    public static User fromDTO(UserDTO dto) {
        if (dto == null) return null;

        User user = new User(dto.getUsername(), dto.getEmail(), dto.getFirstName(), dto.getLastName());
        user.setId(dto.getId());
        user.setActive(dto.getActive() != null ? dto.getActive() : true);

        if (dto.getRoles() != null) {
            user.setRoles(dto.getRoles().stream()
                    .map(UserRole::valueOf)
                    .collect(Collectors.toSet()));
        } else {
            user.setRoles(new HashSet<>());
        }

        if (dto.getLastLogin() != null) {
            user.setLastLogin(LocalDateTime.parse(dto.getLastLogin(), DATETIME_FORMATTER));
        }

        user.setCreatedBy(dto.getCreatedBy());
        user.setManagerId(dto.getManagerId());
        user.setDepartment(dto.getDepartment());
        user.setTerritory(dto.getTerritory());

        return user;
    }

    // ============================================================================
    // COMMISSION PLAN MAPPINGS
    // ============================================================================

    public static CommissionPlanDTO toDTO(CommissionPlan plan) {
        if (plan == null) return null;

        CommissionPlanDTO dto = new CommissionPlanDTO();
        dto.setId(plan.getId());
        dto.setName(plan.getName());
        dto.setCurrency(plan.getCurrency().getCurrencyCode());
        dto.setStatus(plan.getStatus().name());

        if (plan.getEffectiveStartDate() != null) {
            dto.setEffectiveStartDate(plan.getEffectiveStartDate().format(DATE_FORMATTER));
        }
        if (plan.getEffectiveEndDate() != null) {
            dto.setEffectiveEndDate(plan.getEffectiveEndDate().format(DATE_FORMATTER));
        }
        if (plan.getCreatedDate() != null) {
            dto.setCreatedDate(plan.getCreatedDate().format(DATE_FORMATTER));
        }
        if (plan.getLastModifiedDate() != null) {
            dto.setLastModifiedDate(plan.getLastModifiedDate().format(DATE_FORMATTER));
        }

        dto.setCreatedBy(plan.getCreatedBy());

        return dto;
    }

    public static CommissionPlan fromDTO(CommissionPlanDTO dto) {
        if (dto == null) return null;

        Currency currency = Currency.getInstance(dto.getCurrency());
        CommissionPlan plan = new CommissionPlan(dto.getName(), currency);
        plan.setId(dto.getId());

        if (dto.getStatus() != null) {
            plan.setStatus(PlanStatus.valueOf(dto.getStatus()));
        }
        if (dto.getEffectiveStartDate() != null) {
            plan.setEffectiveStartDate(LocalDate.parse(dto.getEffectiveStartDate(), DATE_FORMATTER));
        }
        if (dto.getEffectiveEndDate() != null) {
            plan.setEffectiveEndDate(LocalDate.parse(dto.getEffectiveEndDate(), DATE_FORMATTER));
        }

        plan.setCreatedBy(dto.getCreatedBy());

        return plan;
    }

    // ============================================================================
    // DISPUTE MAPPINGS
    // ============================================================================

    public static DisputeDTO toDTO(Dispute dispute) {
        if (dispute == null) return null;

        DisputeDTO dto = new DisputeDTO();
        dto.setId(dispute.getId());
        dto.setCalculationId(dispute.getCalculationId());
        dto.setSalesRepId(dispute.getSalesRepId());
        dto.setManagerId(dispute.getManagerId());
        dto.setTitle(dispute.getTitle());
        dto.setDescription(dispute.getDescription());
        dto.setStatus(dispute.getStatus().name());
        dto.setEscalated(dispute.isEscalated());

        dto.setComments(dispute.getComments().stream()
                .map(DomainMapper::toDTO)
                .collect(Collectors.toList()));

        if (dispute.getCreatedDate() != null) {
            dto.setCreatedDate(dispute.getCreatedDate().format(DATETIME_FORMATTER));
        }
        if (dispute.getLastUpdatedDate() != null) {
            dto.setLastUpdatedDate(dispute.getLastUpdatedDate().format(DATETIME_FORMATTER));
        }
        if (dispute.getResolvedDate() != null) {
            dto.setResolvedDate(dispute.getResolvedDate().format(DATETIME_FORMATTER));
        }

        dto.setResolvedBy(dispute.getResolvedBy());
        dto.setResolution(dispute.getResolution());

        return dto;
    }

    public static Dispute fromDTO(DisputeDTO dto) {
        if (dto == null) return null;

        Dispute dispute = new Dispute(
                dto.getCalculationId(),
                dto.getSalesRepId(),
                dto.getTitle(),
                dto.getDescription()
        );
        dispute.setId(dto.getId());
        dispute.setManagerId(dto.getManagerId());

        if (dto.getStatus() != null) {
            dispute.setStatus(DisputeStatus.valueOf(dto.getStatus()));
        }
        if (dto.getEscalated() != null) {
            dispute.setEscalated(dto.getEscalated());
        }

        dispute.setResolvedBy(dto.getResolvedBy());
        dispute.setResolution(dto.getResolution());

        return dispute;
    }

    // ============================================================================
    // DISPUTE COMMENT MAPPINGS
    // ============================================================================

    public static DisputeCommentDTO toDTO(DisputeComment comment) {
        if (comment == null) return null;

        DisputeCommentDTO dto = new DisputeCommentDTO();
        dto.setId(comment.getId());
        dto.setDisputeId(comment.getDisputeId());
        dto.setUserId(comment.getUserId());
        dto.setUserName(comment.getUserName());
        dto.setText(comment.getText());
        dto.setIsSystemComment(comment.isSystemComment());

        if (comment.getTimestamp() != null) {
            dto.setCreatedDate(comment.getTimestamp().format(DATETIME_FORMATTER));
        }

        return dto;
    }

    public static DisputeComment fromDTO(DisputeCommentDTO dto) {
        if (dto == null) return null;

        DisputeComment comment = new DisputeComment(
                dto.getDisputeId(),
                dto.getUserId(),
                dto.getUserName(),
                dto.getText()
        );
        comment.setId(dto.getId());

        if (dto.getIsSystemComment() != null) {
            comment.setSystemComment(dto.getIsSystemComment());
        }

        if (dto.getCreatedDate() != null) {
            comment.setTimestamp(LocalDateTime.parse(dto.getCreatedDate(), DATETIME_FORMATTER));
        }

        return comment;
    }

    // ============================================================================
    // LIST CONVERSIONS
    // ============================================================================

    public static List<DealDTO> dealsToDTO(List<Deal> deals) {
        return deals.stream().map(DomainMapper::toDTO).collect(Collectors.toList());
    }

    public static List<UserDTO> usersToDTO(List<User> users) {
        return users.stream().map(DomainMapper::toDTO).collect(Collectors.toList());
    }

    public static List<CommissionPlanDTO> plansToDTO(List<CommissionPlan> plans) {
        return plans.stream().map(DomainMapper::toDTO).collect(Collectors.toList());
    }

    public static List<DisputeDTO> disputesToDTO(List<Dispute> disputes) {
        return disputes.stream().map(DomainMapper::toDTO).collect(Collectors.toList());
    }
}