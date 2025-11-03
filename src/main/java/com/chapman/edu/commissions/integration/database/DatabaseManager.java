package com.chapman.edu.commissions.integration.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseManager - Manages H2 database connections and initialization.
 *
 * This class demonstrates the Singleton pattern for managing database connections
 * and serves as the central point for database configuration.
 *
 * Key Concepts:
 * - Connection Pooling (simplified): Reuses a single connection
 * - Database initialization with DDL scripts
 * - Centralized configuration management
 * - Resource management with try-with-resources
 *
 * In a production system, this would use a connection pool like HikariCP,
 * but for educational purposes, we demonstrate the core concepts.
 */
public class DatabaseManager {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    // H2 Database configuration
    // Using embedded mode with file persistence
    private static final String DB_URL = "jdbc:h2:./data/commissions;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    // Singleton instance
    private static DatabaseManager instance;
    private Connection connection;

    /**
     * Private constructor to enforce Singleton pattern.
     * Initializes the database connection and creates schema.
     */
    private DatabaseManager() {
        try {
            // Load H2 JDBC driver (optional in modern JDBC, but explicit for clarity)
            Class.forName("org.h2.Driver");

            // Establish connection
            // AUTO_SERVER=TRUE allows multiple connections in embedded mode
            this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            logger.info("Database connection established: {}", DB_URL);

            // Initialize database schema
            initializeSchema();

        } catch (ClassNotFoundException e) {
            logger.error("H2 Driver not found", e);
            throw new RuntimeException("Failed to load database driver", e);
        } catch (SQLException e) {
            logger.error("Failed to connect to database", e);
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    /**
     * Gets the singleton instance of DatabaseManager.
     *
     * @return The DatabaseManager instance
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Gets the database connection.
     *
     * @return Active database connection
     */
    public Connection getConnection() {
        try {
            // Check if connection is still valid, reconnect if needed
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                logger.info("Database connection re-established");
            }
        } catch (SQLException e) {
            logger.error("Failed to get database connection", e);
            throw new RuntimeException("Database connection error", e);
        }
        return connection;
    }

    /**
     * Initializes the database schema.
     * Creates tables for Users, Deals, CommissionPlans, and Disputes.
     *
     * Demonstrates:
     * - DDL (Data Definition Language) execution
     * - Table creation with constraints
     * - Foreign key relationships
     * - Appropriate data types for business entities
     */
    private void initializeSchema() {
        try (Statement stmt = connection.createStatement()) {

            // Create USERS table
            // Stores user accounts with authentication credentials
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id VARCHAR(255) PRIMARY KEY,
                    first_name VARCHAR(100) NOT NULL,
                    last_name VARCHAR(100) NOT NULL,
                    email VARCHAR(255) NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL,
                    roles VARCHAR(500),
                    active BOOLEAN DEFAULT TRUE,
                    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Create DEALS table
            // Stores sales deal information
            // Note: "VALUE" is a reserved keyword in H2, so we use "deal_value" instead
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS deals (
                    id VARCHAR(255) PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    deal_value DECIMAL(19,2) NOT NULL,
                    status VARCHAR(50) NOT NULL,
                    sales_rep_id VARCHAR(255) NOT NULL,
                    close_date DATE,
                    products TEXT,
                    created_date DATE DEFAULT CURRENT_DATE,
                    last_modified_date DATE DEFAULT CURRENT_DATE,
                    FOREIGN KEY (sales_rep_id) REFERENCES users(id)
                )
            """);

            // Create COMMISSION_PLANS table
            // Stores commission plan configurations
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS commission_plans (
                    id VARCHAR(255) PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    description TEXT,
                    status VARCHAR(50) NOT NULL,
                    effective_start_date DATE NOT NULL,
                    effective_end_date DATE,
                    rules TEXT,
                    tiers TEXT,
                    bonus_rules TEXT,
                    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Create DISPUTES table
            // Stores commission dispute records
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS disputes (
                    id VARCHAR(255) PRIMARY KEY,
                    deal_id VARCHAR(255) NOT NULL,
                    reported_by_id VARCHAR(255) NOT NULL,
                    assigned_to_id VARCHAR(255),
                    status VARCHAR(50) NOT NULL,
                    description TEXT NOT NULL,
                    resolution TEXT,
                    comments TEXT,
                    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (deal_id) REFERENCES deals(id),
                    FOREIGN KEY (reported_by_id) REFERENCES users(id),
                    FOREIGN KEY (assigned_to_id) REFERENCES users(id)
                )
            """);

            logger.info("Database schema initialized successfully");

        } catch (SQLException e) {
            logger.error("Failed to initialize database schema", e);
            throw new RuntimeException("Schema initialization failed", e);
        }
    }

    /**
     * Closes the database connection.
     * Should be called when shutting down the application.
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed");
            } catch (SQLException e) {
                logger.error("Error closing database connection", e);
            }
        }
    }

    /**
     * Resets the database by dropping all tables and recreating them.
     * Useful for testing and development.
     *
     * WARNING: This deletes all data!
     */
    public void resetDatabase() {
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS disputes");
            stmt.execute("DROP TABLE IF EXISTS deals");
            stmt.execute("DROP TABLE IF EXISTS commission_plans");
            stmt.execute("DROP TABLE IF EXISTS users");
            logger.info("Database tables dropped");

            initializeSchema();
            logger.info("Database reset complete");

        } catch (SQLException e) {
            logger.error("Failed to reset database", e);
            throw new RuntimeException("Database reset failed", e);
        }
    }
}