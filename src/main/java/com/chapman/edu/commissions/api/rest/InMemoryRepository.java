package com.chapman.edu.commissions.api.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * In-memory implementation of the Repository interface.
 *
 * This class provides a simple thread-safe storage mechanism using ConcurrentHashMap.
 * It's suitable for demonstration and development purposes.
 *
 * Concepts demonstrated:
 * - Implementation of Repository interface (DIP compliance)
 * - Thread-safety using ConcurrentHashMap and AtomicLong
 * - Generic programming: Works with any entity type
 * - Function interface: Flexible ID extraction
 *
 * @param <T> The entity type managed by this repository
 */
public class InMemoryRepository<T> implements Repository<T> {

    // Thread-safe map for storing entities by ID
    private final Map<String, T> storage = new ConcurrentHashMap<>();

    // Thread-safe counter for generating unique IDs
    private final AtomicLong idCounter = new AtomicLong(1);

    // Function to extract ID from an entity
    private final Function<T, String> idExtractor;

    // Function to set ID on an entity
    private final java.util.function.BiConsumer<T, String> idSetter;

    // Prefix for generated IDs (e.g., "DEAL-", "USER-")
    private final String idPrefix;

    /**
     * Constructor for InMemoryRepository.
     *
     * @param idPrefix Prefix for generated IDs (e.g., "DEAL-", "USER-")
     * @param idExtractor Function to extract ID from entity
     * @param idSetter Function to set ID on entity
     */
    public InMemoryRepository(String idPrefix,
                              Function<T, String> idExtractor,
                              java.util.function.BiConsumer<T, String> idSetter) {
        this.idPrefix = idPrefix;
        this.idExtractor = idExtractor;
        this.idSetter = idSetter;
    }

    @Override
    public List<T> findAll() {
        // Return a copy of the values to prevent external modification
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<T> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public T save(T entity) {
        String id = idExtractor.apply(entity);

        // If entity has no ID, generate one
        if (id == null || id.isEmpty()) {
            id = generateId();
            idSetter.accept(entity, id);
        }

        // Store the entity
        storage.put(id, entity);
        return entity;
    }

    @Override
    public boolean deleteById(String id) {
        return storage.remove(id) != null;
    }

    @Override
    public String generateId() {
        return idPrefix + String.format("%03d", idCounter.getAndIncrement());
    }

    /**
     * Get the current size of the repository.
     *
     * @return Number of entities stored
     */
    public int size() {
        return storage.size();
    }

    /**
     * Clear all entities from the repository.
     * Useful for testing or resetting state.
     */
    public void clear() {
        storage.clear();
        idCounter.set(1);
    }
}