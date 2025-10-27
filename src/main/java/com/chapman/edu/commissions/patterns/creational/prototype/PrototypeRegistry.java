package com.chapman.edu.commissions.patterns.creational.prototype;

import com.chapman.edu.commissions.patterns.creational.prototype.PrototypePatternImplementation.CloneableDeal;

import java.util.HashMap;
import java.util.Map;

/**
 * PROTOTYPE REGISTRY
 *
 * Centralized catalog for managing and retrieving pre-configured prototype objects.
 *
 * PATTERN PURPOSE:
 * The Registry pattern (often used with Prototype) provides a central location to store
 * and retrieve commonly used prototypes by name/key. This eliminates the need to
 * recreate standard prototypes and provides a template catalog.
 *
 * PROBLEM SOLVED:
 * Without a registry, clients would need to:
 * - Create and configure prototypes repeatedly
 * - Duplicate prototype setup code across multiple locations
 * - Maintain consistency of standard configurations manually
 *
 * REGISTRY SOLUTION:
 * - Create standard prototypes once
 * - Store in registry with meaningful keys ("standard", "premium", "enterprise")
 * - Retrieve and clone as needed
 * - Centralized management of template catalog
 *
 * BUSINESS USE CASES:
 * - Deal templates for different product tiers (standard/premium/enterprise)
 * - Regional deal templates (US/EU/APAC)
 * - Industry-specific deal templates (Healthcare/Finance/Tech)
 * - Seasonal campaign templates
 *
 * WORKFLOW:
 * 1. Setup Phase:
 *    - Create fully-configured prototype deals
 *    - Register with descriptive keys
 * 2. Usage Phase:
 *    - Retrieve prototype by key
 *    - Clone (usually deepClone for independence)
 *    - Customize specific fields
 * 3. Runtime Management:
 *    - Add new prototypes
 *    - Update existing prototypes
 *    - Remove deprecated prototypes
 *
 * DESIGN DECISIONS:
 * - Uses Map<String, CloneableDeal> for key-value storage
 * - String keys allow semantic names ("standard", "premium")
 * - Stores CloneableDeal (not Deal) to ensure clonability
 * - Simple CRUD operations (add, get, remove, contains)
 *
 * @see PrototypePatternImplementation.CloneableDeal
 * @see PrototypePatternStructure.Prototype
 */
public class PrototypeRegistry {

    /**
     * Internal storage for prototype objects.
     *
     * KEY DESIGN CHOICES:
     * - HashMap for O(1) average lookup time
     * - String keys for semantic names ("standard", "premium", "enterprise")
     * - CloneableDeal values (not Deal) ensures all stored prototypes are cloneable
     * - Final field ensures map reference doesn't change (registry integrity)
     *
     * ALTERNATIVE DESIGNS CONSIDERED:
     * - ConcurrentHashMap: If thread-safety needed
     * - Map<Enum, CloneableDeal>: If fixed set of prototype types
     * - Map<String, Prototype<?>>: If storing different prototype types
     */
    private final Map<String, CloneableDeal> prototypes;

    /**
     * Constructor initializes empty registry.
     *
     * Creates a new HashMap to store prototypes.
     * Registry starts empty and is populated via addPrototype().
     *
     * USAGE PATTERN:
     * <pre>
     * PrototypeRegistry registry = new PrototypeRegistry();
     * registry.addPrototype("standard", standardDealPrototype);
     * registry.addPrototype("premium", premiumDealPrototype);
     * </pre>
     */
    public PrototypeRegistry() {
        this.prototypes = new HashMap<>();
    }
    
    /**
     * Adds or updates a prototype in the registry.
     *
     * BEHAVIOR:
     * - If key doesn't exist: Adds new prototype
     * - If key exists: Replaces existing prototype (update)
     *
     * NAMING CONVENTIONS:
     * - Use descriptive keys: "standard", "premium", "enterprise"
     * - Consider prefixes: "us-standard", "eu-premium"
     * - Be consistent across the application
     *
     * USAGE:
     * <pre>
     * CloneableDeal standardDeal = new CloneableDeal("Standard Deal", value, "REP-001");
     * // Configure the deal...
     * registry.addPrototype("standard", standardDeal);
     * </pre>
     *
     * @param key The unique identifier for the prototype (e.g., "standard", "premium")
     * @param prototype The fully-configured prototype object to store
     */
    public void addPrototype(String key, CloneableDeal prototype) {
        prototypes.put(key, prototype);
    }

    /**
     * Retrieves a prototype from the registry by key.
     *
     * IMPORTANT: This returns the actual prototype, not a clone!
     * Always clone before modifying to avoid affecting the stored prototype.
     *
     * TYPICAL USAGE PATTERN:
     * <pre>
     * CloneableDeal prototype = registry.getPrototype("premium");
     * CloneableDeal newDeal = prototype.deepClone();  // Clone before using!
     * newDeal.setId("DEAL-NEW-001");
     * newDeal.setSalesRepId("REP-002");
     * </pre>
     *
     * WARNING:
     * <pre>
     * CloneableDeal deal = registry.getPrototype("premium");
     * deal.setTitle("Modified");  // BAD! Modifies the stored prototype
     * </pre>
     *
     * @param key The identifier of the prototype to retrieve
     * @return The prototype object, or null if key not found
     */
    public CloneableDeal getPrototype(String key) {
        return prototypes.get(key);
    }

    /**
     * Removes a prototype from the registry.
     *
     * USE CASES:
     * - Deprecated templates no longer needed
     * - Seasonal templates after campaign ends
     * - Testing/cleanup operations
     *
     * @param key The identifier of the prototype to remove
     */
    public void removePrototype(String key) {
        prototypes.remove(key);
    }

    /**
     * Checks if a prototype with the specified key exists.
     *
     * USE CASES:
     * - Validate key before attempting retrieval
     * - Conditional logic based on prototype availability
     * - User interface enablement
     *
     * @param key The identifier to check
     * @return true if prototype exists, false otherwise
     */
    public boolean containsPrototype(String key) {
        return prototypes.containsKey(key);
    }

    /**
     * Returns the current number of prototypes in the registry.
     *
     * USE CASES:
     * - Monitoring/metrics
     * - Validation (ensure templates are loaded)
     * - UI display
     *
     * @return The number of prototypes currently registered
     */
    public int size() {
        return prototypes.size();
    }

    /**
     * Removes all prototypes from the registry.
     *
     * USE CASES:
     * - Testing (reset state between tests)
     * - Reloading templates from configuration
     * - Administrative operations
     *
     * WARNING: This is a destructive operation.
     * All stored prototypes will be lost.
     */
    public void clear() {
        prototypes.clear();
    }

    /**
     * Returns all registered prototype keys.
     *
     * USE CASES:
     * - Display available templates to users
     * - Iteration over all prototypes
     * - Validation/reporting
     *
     * EXAMPLE:
     * <pre>
     * for (String key : registry.getKeys()) {
     *     System.out.println("Available template: " + key);
     * }
     * </pre>
     *
     * @return Iterable of all prototype keys in the registry
     */
    public Iterable<String> getKeys() {
        return prototypes.keySet();
    }
}