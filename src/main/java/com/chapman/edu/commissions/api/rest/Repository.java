package com.chapman.edu.commissions.api.rest;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface for CRUD operations.
 *
 * This interface demonstrates several design principles:
 * - Dependency Inversion Principle (DIP): High-level servlets depend on this abstraction
 * - Interface Segregation Principle (ISP): Provides only essential CRUD methods
 * - Generic programming: Reusable for any entity type
 *
 * @param <T> The entity type managed by this repository
 */
public interface Repository<T> {

    /**
     * Find all entities.
     *
     * @return List of all entities
     */
    List<T> findAll();

    /**
     * Find an entity by its ID.
     *
     * @param id The entity ID
     * @return Optional containing the entity if found, empty otherwise
     */
    Optional<T> findById(String id);

    /**
     * Save a new entity or update an existing one.
     *
     * @param entity The entity to save
     * @return The saved entity
     */
    T save(T entity);

    /**
     * Delete an entity by its ID.
     *
     * @param id The entity ID
     * @return true if the entity was deleted, false if not found
     */
    boolean deleteById(String id);

    /**
     * Generate a new unique ID for an entity.
     *
     * @return A unique ID string
     */
    String generateId();
}