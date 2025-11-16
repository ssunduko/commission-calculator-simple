package com.chapman.edu.commissions.integration.repository;

import com.chapman.edu.commissions.api.rest.JsonHelper;
import com.chapman.edu.commissions.api.rest.Repository;
import com.chapman.edu.commissions.integration.database.DatabaseManager;
import com.chapman.edu.commissions.model.User;
import com.chapman.edu.commissions.model.UserRole;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * H2UserRepository - JDBC-based implementation of Repository for User entities.
 *
 * Demonstrates:
 * - User data persistence with H2 database
 * - Password storage (in production, would use bcrypt hashing)
 * - Role-based access control data storage
 * - Email uniqueness constraint handling
 *
 * Layer: Data Access Layer (Repository)
 *
 * @author Sergey L. Sundukovskiy
 * @version 1.0
 */
public class H2UserRepository implements Repository<User> {

    private static final Logger logger = LoggerFactory.getLogger(H2UserRepository.class);
    private final DatabaseManager dbManager;

    public H2UserRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY last_name, first_name";

        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

            logger.debug("Found {} users", users.size());

        } catch (SQLException e) {
            logger.error("Error finding all users", e);
            throw new RuntimeException("Failed to retrieve users", e);
        }

        return users;
    }

    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding user by id: {}", id, e);
            throw new RuntimeException("Failed to retrieve user", e);
        }

        return Optional.empty();
    }

    /**
     * Finds a user by email address.
     * Used for authentication and duplicate checking.
     *
     * @param email The user's email
     * @return Optional containing the user if found
     */
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding user by email: {}", email, e);
            throw new RuntimeException("Failed to retrieve user", e);
        }

        return Optional.empty();
    }

    @Override
    public User save(User user) {
        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(generateId());
            return insert(user);
        } else {
            return update(user);
        }
    }

    private User insert(User user) {
        String sql = """
            INSERT INTO users (id, first_name, last_name, email, password, roles, active, created_date, last_modified_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            setUserParameters(stmt, user);
            stmt.executeUpdate();

            logger.info("Inserted user with id: {}", user.getId());
            return user;

        } catch (SQLException e) {
            // Check for unique constraint violation on email
            if (e.getMessage().contains("Unique index or primary key violation")) {
                throw new RuntimeException("Email already exists: " + user.getEmail(), e);
            }
            logger.error("Error inserting user", e);
            throw new RuntimeException("Failed to insert user", e);
        }
    }

    private User update(User user) {
        String sql = """
            UPDATE users SET first_name = ?, last_name = ?, email = ?, password = ?,
                           roles = ?, active = ?, last_modified_date = ?
            WHERE id = ?
        """;

        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPasswordHash());
            stmt.setString(5, JsonHelper.toJson(user.getRoles()));
            stmt.setBoolean(6, user.isActive());
            stmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            stmt.setString(8, user.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("User not found for update: " + user.getId());
            }

            logger.info("Updated user with id: {}", user.getId());
            return user;

        } catch (SQLException e) {
            logger.error("Error updating user", e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    public boolean deleteById(String id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Deleted user with id: {}", id);
                return true;
            }

            return false;

        } catch (SQLException e) {
            // Check for foreign key constraint violation
            if (e.getMessage().contains("Referential integrity constraint violation")) {
                throw new RuntimeException("Cannot delete user - has associated deals or disputes", e);
            }
            logger.error("Error deleting user", e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    @Override
    public String generateId() {
        return "USER-" + UUID.randomUUID().toString();
    }

    private void setUserParameters(PreparedStatement stmt, User user) throws SQLException {
        stmt.setString(1, user.getId());
        stmt.setString(2, user.getFirstName());
        stmt.setString(3, user.getLastName());
        stmt.setString(4, user.getEmail());
        // NOTE: In production, password should be hashed with bcrypt
        stmt.setString(5, user.getPasswordHash());
        stmt.setString(6, JsonHelper.toJson(user.getRoles()));
        stmt.setBoolean(7, user.isActive());
        stmt.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
        stmt.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password"));
        user.setActive(rs.getBoolean("active"));

        // Deserialize roles from JSON
        String rolesJson = rs.getString("roles");
        if (rolesJson != null && !rolesJson.isEmpty()) {
            UserRole[] rolesArray = JsonHelper.fromJson(rolesJson, UserRole[].class);
            user.setRoles(new java.util.HashSet<>(java.util.Arrays.asList(rolesArray)));
        } else {
            user.setRoles(new java.util.HashSet<>());
        }

        return user;
    }
}