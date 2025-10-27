package com.chapman.edu.commissions.api.grpc;

import com.chapman.edu.commissions.api.grpc.proto.*;
import com.chapman.edu.commissions.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for converting between domain model objects and Protocol Buffer messages.
 *
 * This class demonstrates the Adapter pattern and handles the impedance mismatch
 * between Java domain objects and Protocol Buffer message types.
 *
 * Key Concepts:
 * - Bidirectional conversion: domain → proto and proto → domain
 * - Type mapping: BigDecimal ↔ Decimal, LocalDate ↔ Date, enum ↔ enum
 * - Null safety: Protocol Buffers don't have null, use default values
 * - Collection handling: Java List/Set ↔ repeated fields
 *
 * Design Pattern: Adapter Pattern
 * - Adapts incompatible interfaces (Java model vs. protobuf messages)
 * - Provides a clean separation between domain and transport layers
 * - Makes it easy to change either representation independently
 */
public class ModelConverter {

    // ==================== Common Type Conversions ====================

    /**
     * Convert BigDecimal to protobuf Decimal message.
     *
     * Protocol Buffers don't have a native decimal type, so we use a custom
     * message with a string representation to preserve precision.
     */
    public static Decimal toProtoDecimal(BigDecimal value) {
        if (value == null) {
            return Decimal.newBuilder().setValue("0").build();
        }
        return Decimal.newBuilder()
                .setValue(value.toPlainString())
                .build();
    }

    /**
     * Convert protobuf Decimal message to BigDecimal.
     *
     * Parses the string representation back to BigDecimal.
     */
    public static BigDecimal fromProtoDecimal(Decimal decimal) {
        if (decimal == null || decimal.getValue().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(decimal.getValue());
    }

    /**
     * Convert LocalDate to protobuf Date message.
     *
     * LocalDate is represented as year/month/day components.
     */
    public static Date toProtoDate(LocalDate date) {
        if (date == null) {
            return Date.getDefaultInstance();
        }
        return Date.newBuilder()
                .setYear(date.getYear())
                .setMonth(date.getMonthValue())
                .setDay(date.getDayOfMonth())
                .build();
    }

    /**
     * Convert protobuf Date message to LocalDate.
     *
     * Reconstructs LocalDate from year/month/day components.
     */
    public static LocalDate fromProtoDate(Date date) {
        if (date == null || date.getYear() == 0) {
            return null;
        }
        return LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
    }

    /**
     * Convert LocalDateTime to protobuf Timestamp message.
     *
     * LocalDateTime is represented as milliseconds since Unix epoch.
     */
    public static Timestamp toProtoTimestamp(LocalDateTime dateTime) {
        if (dateTime == null) {
            return Timestamp.getDefaultInstance();
        }
        long millis = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        return Timestamp.newBuilder()
                .setMillis(millis)
                .build();
    }

    /**
     * Convert protobuf Timestamp message to LocalDateTime.
     *
     * Reconstructs LocalDateTime from epoch milliseconds.
     */
    public static LocalDateTime fromProtoTimestamp(Timestamp timestamp) {
        if (timestamp == null || timestamp.getMillis() == 0) {
            return null;
        }
        return LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(timestamp.getMillis()),
                ZoneOffset.UTC
        );
    }

    // ==================== Deal Conversions ====================

    /**
     * Convert domain DealProduct to protobuf DealProduct.
     */
    public static com.chapman.edu.commissions.api.grpc.proto.DealProduct toProtoDealProduct(
            com.chapman.edu.commissions.model.DealProduct product) {
        if (product == null) {
            return com.chapman.edu.commissions.api.grpc.proto.DealProduct.getDefaultInstance();
        }

        com.chapman.edu.commissions.api.grpc.proto.DealProduct.Builder builder =
                com.chapman.edu.commissions.api.grpc.proto.DealProduct.newBuilder();

        if (product.getId() != null) {
            builder.setId(product.getId());
        }
        if (product.getProductId() != null) {
            builder.setProductId(product.getProductId());
        }
        if (product.getProductName() != null) {
            builder.setProductName(product.getProductName());
        }
        builder.setQuantity(product.getQuantity());
        builder.setPrice(toProtoDecimal(product.getPrice()));
        builder.setDiscount(toProtoDecimal(product.getDiscount()));
        if (product.getDealId() != null) {
            builder.setDealId(product.getDealId());
        }

        return builder.build();
    }

    /**
     * Convert protobuf DealProduct to domain DealProduct.
     */
    public static com.chapman.edu.commissions.model.DealProduct fromProtoDealProduct(
            com.chapman.edu.commissions.api.grpc.proto.DealProduct protoProduct) {
        if (protoProduct == null) {
            return null;
        }

        com.chapman.edu.commissions.model.DealProduct product =
                new com.chapman.edu.commissions.model.DealProduct();

        if (!protoProduct.getId().isEmpty()) {
            product.setId(protoProduct.getId());
        }
        if (!protoProduct.getProductId().isEmpty()) {
            product.setProductId(protoProduct.getProductId());
        }
        if (!protoProduct.getProductName().isEmpty()) {
            product.setProductName(protoProduct.getProductName());
        }
        product.setQuantity(protoProduct.getQuantity());
        product.setPrice(fromProtoDecimal(protoProduct.getPrice()));
        product.setDiscount(fromProtoDecimal(protoProduct.getDiscount()));
        if (!protoProduct.getDealId().isEmpty()) {
            product.setDealId(protoProduct.getDealId());
        }

        return product;
    }

    /**
     * Convert domain Deal to protobuf Deal.
     *
     * Handles the complete deal structure including nested products list.
     */
    public static com.chapman.edu.commissions.api.grpc.proto.Deal toProtoDeal(
            com.chapman.edu.commissions.model.Deal deal) {
        if (deal == null) {
            return com.chapman.edu.commissions.api.grpc.proto.Deal.getDefaultInstance();
        }

        com.chapman.edu.commissions.api.grpc.proto.Deal.Builder builder =
                com.chapman.edu.commissions.api.grpc.proto.Deal.newBuilder();

        if (deal.getId() != null) {
            builder.setId(deal.getId());
        }
        if (deal.getTitle() != null) {
            builder.setTitle(deal.getTitle());
        }
        builder.setValue(toProtoDecimal(deal.getValue()));
        builder.setStatus(toProtoDealStatus(deal.getStatus()));
        if (deal.getSalesRepId() != null) {
            builder.setSalesRepId(deal.getSalesRepId());
        }
        if (deal.getProducts() != null) {
            deal.getProducts().forEach(product ->
                    builder.addProducts(toProtoDealProduct(product)));
        }
        builder.setCloseDate(toProtoDate(deal.getCloseDate()));
        builder.setCreatedDate(toProtoDate(deal.getCreatedDate()));
        builder.setLastModifiedDate(toProtoDate(deal.getLastModifiedDate()));

        return builder.build();
    }

    /**
     * Convert protobuf Deal to domain Deal.
     *
     * Reconstructs the complete deal structure from the protobuf message.
     */
    public static com.chapman.edu.commissions.model.Deal fromProtoDeal(
            com.chapman.edu.commissions.api.grpc.proto.Deal protoDeal) {
        if (protoDeal == null) {
            return null;
        }

        com.chapman.edu.commissions.model.Deal deal = new com.chapman.edu.commissions.model.Deal();

        if (!protoDeal.getId().isEmpty()) {
            deal.setId(protoDeal.getId());
        }
        if (!protoDeal.getTitle().isEmpty()) {
            deal.setTitle(protoDeal.getTitle());
        }
        deal.setValue(fromProtoDecimal(protoDeal.getValue()));
        deal.setStatus(fromProtoDealStatus(protoDeal.getStatus()));
        if (!protoDeal.getSalesRepId().isEmpty()) {
            deal.setSalesRepId(protoDeal.getSalesRepId());
        }
        if (protoDeal.getProductsCount() > 0) {
            List<com.chapman.edu.commissions.model.DealProduct> products = protoDeal.getProductsList()
                    .stream()
                    .map(ModelConverter::fromProtoDealProduct)
                    .collect(Collectors.toList());
            deal.setProducts(products);
        }
        deal.setCloseDate(fromProtoDate(protoDeal.getCloseDate()));
        deal.setCreatedDate(fromProtoDate(protoDeal.getCreatedDate()));
        deal.setLastModifiedDate(fromProtoDate(protoDeal.getLastModifiedDate()));

        return deal;
    }

    /**
     * Convert domain DealStatus to protobuf DealStatus enum.
     */
    public static com.chapman.edu.commissions.api.grpc.proto.DealStatus toProtoDealStatus(
            com.chapman.edu.commissions.model.DealStatus status) {
        if (status == null) {
            return com.chapman.edu.commissions.api.grpc.proto.DealStatus.DEAL_STATUS_UNSPECIFIED;
        }
        return switch (status) {
            case OPEN -> com.chapman.edu.commissions.api.grpc.proto.DealStatus.OPEN;
            case WON -> com.chapman.edu.commissions.api.grpc.proto.DealStatus.WON;
            case LOST -> com.chapman.edu.commissions.api.grpc.proto.DealStatus.LOST;
            case CANCELLED -> com.chapman.edu.commissions.api.grpc.proto.DealStatus.CANCELLED;
        };
    }

    /**
     * Convert protobuf DealStatus enum to domain DealStatus.
     */
    public static com.chapman.edu.commissions.model.DealStatus fromProtoDealStatus(
            com.chapman.edu.commissions.api.grpc.proto.DealStatus protoStatus) {
        if (protoStatus == null || protoStatus == com.chapman.edu.commissions.api.grpc.proto.DealStatus.DEAL_STATUS_UNSPECIFIED) {
            return com.chapman.edu.commissions.model.DealStatus.OPEN; // Default
        }
        return switch (protoStatus) {
            case OPEN -> com.chapman.edu.commissions.model.DealStatus.OPEN;
            case WON -> com.chapman.edu.commissions.model.DealStatus.WON;
            case LOST -> com.chapman.edu.commissions.model.DealStatus.LOST;
            case CANCELLED -> com.chapman.edu.commissions.model.DealStatus.CANCELLED;
            default -> com.chapman.edu.commissions.model.DealStatus.OPEN;
        };
    }

    // ==================== User Conversions ====================

    /**
     * Convert domain User to protobuf User.
     *
     * Note: Password hash is intentionally excluded for security.
     */
    public static com.chapman.edu.commissions.api.grpc.proto.User toProtoUser(
            com.chapman.edu.commissions.model.User user) {
        if (user == null) {
            return com.chapman.edu.commissions.api.grpc.proto.User.getDefaultInstance();
        }

        com.chapman.edu.commissions.api.grpc.proto.User.Builder builder =
                com.chapman.edu.commissions.api.grpc.proto.User.newBuilder();

        if (user.getId() != null) {
            builder.setId(user.getId());
        }
        if (user.getUsername() != null) {
            builder.setUsername(user.getUsername());
        }
        if (user.getEmail() != null) {
            builder.setEmail(user.getEmail());
        }
        if (user.getFirstName() != null) {
            builder.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            builder.setLastName(user.getLastName());
        }
        if (user.getRoles() != null) {
            user.getRoles().forEach(role ->
                    builder.addRoles(toProtoUserRole(role)));
        }
        builder.setActive(user.isActive());
        if (user.getLastLogin() != null) {
            builder.setLastLogin(toProtoTimestamp(user.getLastLogin()));
        }
        builder.setCreatedDate(toProtoDate(user.getCreatedDate()));
        if (user.getCreatedBy() != null) {
            builder.setCreatedBy(user.getCreatedBy());
        }
        if (user.getManagerId() != null) {
            builder.setManagerId(user.getManagerId());
        }
        if (user.getDepartment() != null) {
            builder.setDepartment(user.getDepartment());
        }
        if (user.getTerritory() != null) {
            builder.setTerritory(user.getTerritory());
        }

        return builder.build();
    }

    /**
     * Convert protobuf User to domain User.
     */
    public static com.chapman.edu.commissions.model.User fromProtoUser(
            com.chapman.edu.commissions.api.grpc.proto.User protoUser) {
        if (protoUser == null) {
            return null;
        }

        com.chapman.edu.commissions.model.User user = new com.chapman.edu.commissions.model.User();

        if (!protoUser.getId().isEmpty()) {
            user.setId(protoUser.getId());
        }
        if (!protoUser.getUsername().isEmpty()) {
            user.setUsername(protoUser.getUsername());
        }
        if (!protoUser.getEmail().isEmpty()) {
            user.setEmail(protoUser.getEmail());
        }
        if (!protoUser.getFirstName().isEmpty()) {
            user.setFirstName(protoUser.getFirstName());
        }
        if (!protoUser.getLastName().isEmpty()) {
            user.setLastName(protoUser.getLastName());
        }
        if (protoUser.getRolesCount() > 0) {
            Set<com.chapman.edu.commissions.model.UserRole> roles = protoUser.getRolesList()
                    .stream()
                    .map(ModelConverter::fromProtoUserRole)
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }
        user.setActive(protoUser.getActive());
        user.setLastLogin(fromProtoTimestamp(protoUser.getLastLogin()));
        user.setCreatedDate(fromProtoDate(protoUser.getCreatedDate()));
        if (!protoUser.getCreatedBy().isEmpty()) {
            user.setCreatedBy(protoUser.getCreatedBy());
        }
        if (!protoUser.getManagerId().isEmpty()) {
            user.setManagerId(protoUser.getManagerId());
        }
        if (!protoUser.getDepartment().isEmpty()) {
            user.setDepartment(protoUser.getDepartment());
        }
        if (!protoUser.getTerritory().isEmpty()) {
            user.setTerritory(protoUser.getTerritory());
        }

        return user;
    }

    /**
     * Convert domain UserRole to protobuf UserRole enum.
     */
    public static com.chapman.edu.commissions.api.grpc.proto.UserRole toProtoUserRole(
            com.chapman.edu.commissions.model.UserRole role) {
        if (role == null) {
            return com.chapman.edu.commissions.api.grpc.proto.UserRole.USER_ROLE_UNSPECIFIED;
        }
        return switch (role) {
            case SALES_REP -> com.chapman.edu.commissions.api.grpc.proto.UserRole.SALES_REP;
            case SALES_MANAGER -> com.chapman.edu.commissions.api.grpc.proto.UserRole.SALES_MANAGER;
            case FINANCE_ADMIN -> com.chapman.edu.commissions.api.grpc.proto.UserRole.FINANCE_ADMIN;
            case SYSTEM_ADMIN -> com.chapman.edu.commissions.api.grpc.proto.UserRole.SYSTEM_ADMIN;
        };
    }

    /**
     * Convert protobuf UserRole enum to domain UserRole.
     */
    public static com.chapman.edu.commissions.model.UserRole fromProtoUserRole(
            com.chapman.edu.commissions.api.grpc.proto.UserRole protoRole) {
        if (protoRole == null || protoRole == com.chapman.edu.commissions.api.grpc.proto.UserRole.USER_ROLE_UNSPECIFIED) {
            return com.chapman.edu.commissions.model.UserRole.SALES_REP; // Default
        }
        return switch (protoRole) {
            case SALES_REP -> com.chapman.edu.commissions.model.UserRole.SALES_REP;
            case SALES_MANAGER -> com.chapman.edu.commissions.model.UserRole.SALES_MANAGER;
            case FINANCE_ADMIN -> com.chapman.edu.commissions.model.UserRole.FINANCE_ADMIN;
            case SYSTEM_ADMIN -> com.chapman.edu.commissions.model.UserRole.SYSTEM_ADMIN;
            default -> com.chapman.edu.commissions.model.UserRole.SALES_REP;
        };
    }

    // Additional converters for CommissionPlan and Dispute would follow the same pattern...
    // Omitted for brevity but would be implemented similarly
}