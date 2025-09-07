package com.chapman.edu.commissions.patterns.creational.prototype;

import com.chapman.edu.commissions.patterns.creational.prototype.PrototypePatternImplementation.CloneableDeal;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the Prototype Registry for the Commission System.
 * 
 * The Prototype Registry is a central repository of prototype objects that can be cloned
 * when needed. It provides a way to store, retrieve, and manage prototype objects.
 * 
 * This pattern is useful when:
 * 1. You need to create multiple instances of similar objects
 * 2. You want to avoid creating a factory class hierarchy
 * 3. You want to reduce the number of subclasses
 */
public class PrototypeRegistry {
    
    /**
     * Map to store prototype objects with their identifiers
     */
    private final Map<String, CloneableDeal> prototypes;
    
    /**
     * Constructor initializes an empty registry
     */
    public PrototypeRegistry() {
        this.prototypes = new HashMap<>();
    }
    
    /**
     * Adds a prototype to the registry with the specified key.
     * 
     * @param key The identifier for the prototype
     * @param prototype The prototype object to store
     */
    public void addPrototype(String key, CloneableDeal prototype) {
        prototypes.put(key, prototype);
    }
    
    /**
     * Retrieves a prototype from the registry by its key.
     * 
     * @param key The identifier of the prototype to retrieve
     * @return The prototype object, or null if not found
     */
    public CloneableDeal getPrototype(String key) {
        return prototypes.get(key);
    }
    
    /**
     * Removes a prototype from the registry.
     * 
     * @param key The identifier of the prototype to remove
     */
    public void removePrototype(String key) {
        prototypes.remove(key);
    }
    
    /**
     * Checks if a prototype with the specified key exists in the registry.
     * 
     * @param key The identifier to check
     * @return true if the prototype exists, false otherwise
     */
    public boolean containsPrototype(String key) {
        return prototypes.containsKey(key);
    }
    
    /**
     * Returns the number of prototypes in the registry.
     * 
     * @return The number of prototypes
     */
    public int size() {
        return prototypes.size();
    }
    
    /**
     * Clears all prototypes from the registry.
     */
    public void clear() {
        prototypes.clear();
    }
    
    /**
     * Returns all keys in the registry.
     * 
     * @return A set of all prototype keys
     */
    public Iterable<String> getKeys() {
        return prototypes.keySet();
    }
}