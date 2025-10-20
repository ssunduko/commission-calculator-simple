package com.chapman.edu.commissions.integration.repository;

import com.chapman.edu.commissions.api.rest.JsonHelper;
import com.chapman.edu.commissions.api.rest.Repository;
import com.chapman.edu.commissions.integration.database.DatabaseManager;
import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.DealStatus;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * H2DealRepository - JDBC-based implementation of Repository for Deal entities.
 *
 * This class demonstrates the Repository Pattern with JDBC:
 * - Encapsulates data access logic
 * - Translates between domain objects and database rows
 * - Handles SQL operations and exception handling
 * - Implements CRUD operations
 *
 * Key Concepts:
 * - PreparedStatement for SQL injection prevention
 * - ResultSet mapping to domain objects
 * - JSON serialization for complex nested objects (DealProduct list)
 * - Transaction management (auto-commit mode for simplicity)
 * - Proper resource cleanup with try-with-resources
 *
 * Layer: Data Access Layer (Repository)
 */
public class H2DealRepository implements Repository<Deal> {

    private static final Logger logger = LoggerFactory.getLogger(H2DealRepository.class);
    private final DatabaseManager dbManager;

    /**
     * Constructor with dependency injection.
     * Receives DatabaseManager to obtain connections.
     */
    public H2DealRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * Finds all deals in the database.
     *
     * @return List of all deals
     */
    @Override
    public List<Deal> findAll() {
        List<Deal> deals = new ArrayList<>();
        String sql = "SELECT * FROM deals ORDER BY created_date DESC";

        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                deals.add(mapResultSetToDeal(rs));
            }

            logger.debug("Found {} deals", deals.size());

        } catch (SQLException e) {
            logger.error("Error finding all deals", e);
            throw new RuntimeException("Failed to retrieve deals", e);
        }

        return deals;
    }

    /**
     * Finds a deal by its ID.
     *
     * @param id The deal ID
     * @return Optional containing the deal if found
     */
    @Override
    public Optional<Deal> findById(String id) {
        String sql = "SELECT * FROM deals WHERE id = ?";

        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDeal(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding deal by id: {}", id, e);
            throw new RuntimeException("Failed to retrieve deal", e);
        }

        return Optional.empty();
    }

    /**
     * Saves a deal (insert or update).
     *
     * @param deal The deal to save
     * @return The saved deal with generated ID if new
     */
    @Override
    public Deal save(Deal deal) {
        // Generate ID if new entity
        if (deal.getId() == null || deal.getId().isEmpty()) {
            deal.setId(generateId());
            return insert(deal);
        } else {
            return update(deal);
        }
    }

    /**
     * Inserts a new deal into the database.
     */
    private Deal insert(Deal deal) {
        String sql = """
            INSERT INTO deals (id, title, deal_value, status, sales_rep_id,
                             close_date, products, created_date, last_modified_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            setDealParameters(stmt, deal);
            stmt.executeUpdate();

            logger.info("Inserted deal with id: {}", deal.getId());
            return deal;

        } catch (SQLException e) {
            logger.error("Error inserting deal", e);
            throw new RuntimeException("Failed to insert deal", e);
        }
    }

    /**
     * Updates an existing deal in the database.
     */
    private Deal update(Deal deal) {
        String sql = """
            UPDATE deals SET title = ?, deal_value = ?, status = ?,
                           sales_rep_id = ?, close_date = ?,
                           products = ?, last_modified_date = ?
            WHERE id = ?
        """;

        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, deal.getTitle());
            stmt.setBigDecimal(2, deal.calculateTotalValue());
            stmt.setString(3, deal.getStatus().name());
            stmt.setString(4, deal.getSalesRepId());
            stmt.setDate(5, deal.getCloseDate() != null ? Date.valueOf(deal.getCloseDate()) : null);
            stmt.setString(6, JsonHelper.toJson(deal.getProducts()));
            stmt.setDate(7, deal.getLastModifiedDate() != null ? Date.valueOf(deal.getLastModifiedDate()) : Date.valueOf(LocalDate.now()));
            stmt.setString(8, deal.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("Deal not found for update: " + deal.getId());
            }

            logger.info("Updated deal with id: {}", deal.getId());
            return deal;

        } catch (SQLException e) {
            logger.error("Error updating deal", e);
            throw new RuntimeException("Failed to update deal", e);
        }
    }

    /**
     * Deletes a deal by ID.
     *
     * @param id The deal ID
     * @return true if deleted, false if not found
     */
    @Override
    public boolean deleteById(String id) {
        String sql = "DELETE FROM deals WHERE id = ?";

        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Deleted deal with id: {}", id);
                return true;
            }

            return false;

        } catch (SQLException e) {
            logger.error("Error deleting deal", e);
            throw new RuntimeException("Failed to delete deal", e);
        }
    }

    /**
     * Generates a unique ID for a new deal.
     *
     * @return A unique ID string
     */
    @Override
    public String generateId() {
        return "DEAL-" + UUID.randomUUID().toString();
    }

    /**
     * Sets PreparedStatement parameters from Deal object.
     * Demonstrates proper parameter binding for SQL injection prevention.
     */
    private void setDealParameters(PreparedStatement stmt, Deal deal) throws SQLException {
        stmt.setString(1, deal.getId());
        stmt.setString(2, deal.getTitle());
        stmt.setBigDecimal(3, deal.calculateTotalValue());
        stmt.setString(4, deal.getStatus().name());
        stmt.setString(5, deal.getSalesRepId());
        stmt.setDate(6, deal.getCloseDate() != null ? Date.valueOf(deal.getCloseDate()) : null);
        // Serialize products list to JSON for storage
        stmt.setString(7, JsonHelper.toJson(deal.getProducts()));
        stmt.setDate(8, deal.getCreatedDate() != null ? Date.valueOf(deal.getCreatedDate()) : Date.valueOf(LocalDate.now()));
        stmt.setDate(9, deal.getLastModifiedDate() != null ? Date.valueOf(deal.getLastModifiedDate()) : Date.valueOf(LocalDate.now()));
    }

    /**
     * Maps a ResultSet row to a Deal object.
     * Demonstrates Object-Relational Mapping (ORM) concepts.
     *
     * @param rs The ResultSet positioned at a row
     * @return Deal object
     */
    private Deal mapResultSetToDeal(ResultSet rs) throws SQLException {
        Deal deal = new Deal();
        deal.setId(rs.getString("id"));
        deal.setTitle(rs.getString("title"));
        deal.setStatus(DealStatus.valueOf(rs.getString("status")));
        deal.setSalesRepId(rs.getString("sales_rep_id"));

        // Handle nullable close date
        Date closeDate = rs.getDate("close_date");
        deal.setCloseDate(closeDate != null ? closeDate.toLocalDate() : null);

        // Deserialize products from JSON
        String productsJson = rs.getString("products");
        if (productsJson != null && !productsJson.isEmpty()) {
            DealProduct[] productsArray = JsonHelper.fromJson(productsJson, DealProduct[].class);
            deal.setProducts(java.util.Arrays.asList(productsArray));
        } else {
            deal.setProducts(new ArrayList<>());
        }

        return deal;
    }
}