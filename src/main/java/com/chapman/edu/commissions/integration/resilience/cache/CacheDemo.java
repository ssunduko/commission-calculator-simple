package com.chapman.edu.commissions.integration.resilience.cache;

import io.github.resilience4j.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Cache Pattern Implementation using Resilience4j
 *
 * CONCEPT:
 * Caching stores the results of expensive operations so subsequent requests for the same
 * data can be served quickly from memory instead of re-executing the operation. It's like
 * keeping a note of an answer after looking it up once, so you don't have to look it up again.
 *
 * ANALOGY:
 * Think of a calculator user who writes down complex calculation results. The first time they
 * calculate "3847 × 2961", it takes time. But they write down the result (11,393,967).
 * When they need that same calculation later, they just look at their notes instead of
 * recalculating. The notes are the cache.
 *
 * KEY CONCEPTS:
 *
 * 1. CACHE HIT:
 *    - Requested data is in cache
 *    - Fast retrieval from memory
 *    - No expensive operation needed
 *
 * 2. CACHE MISS:
 *    - Requested data not in cache
 *    - Execute expensive operation
 *    - Store result in cache for future use
 *
 * 3. CACHE EVICTION:
 *    - Removing entries from cache
 *    - Strategies: LRU (Least Recently Used), TTL (Time To Live), Size-based
 *    - Prevents cache from growing indefinitely
 *
 * 4. CACHE INVALIDATION:
 *    - Removing stale or outdated entries
 *    - One of the "two hard things in computer science"
 *    - Critical for data consistency
 *
 * PURPOSE:
 * - Reduce latency by avoiding repeated expensive operations
 * - Decrease load on backend systems (databases, APIs)
 * - Improve application throughput and scalability
 * - Reduce costs (fewer API calls, less database load)
 * - Provide faster user experience
 *
 * WHEN TO USE:
 * - Expensive calculations that don't change frequently
 * - Database queries with consistent results
 * - External API calls with rate limits
 * - Reference data that changes infrequently
 * - User session data
 * - Configuration values
 *
 * WHEN NOT TO USE:
 * - Frequently changing data
 * - User-specific data requiring privacy
 * - Small, fast operations (caching overhead not worth it)
 * - Operations with side effects
 *
 * CACHE CONSIDERATIONS:
 * - Memory usage: Caches consume RAM
 * - Staleness: Cached data may become outdated
 * - Consistency: Multiple caches can have different data
 * - Warming: Cold caches have no benefit initially
 */
public class CacheDemo {

    private static final Logger logger = LoggerFactory.getLogger(CacheDemo.class);

    /**
     * Demonstrates basic cache usage with Resilience4j
     */
    public static void demonstrateBasicCaching() {
        logger.info("=== Basic Caching Demo Started ===");

        // Create a simple cache
        javax.cache.Cache<String, Double> jCache = new CacheStructure.SimpleCache<>("commissionCache");

        // Create Resilience4j cache wrapper
        Cache<String, Double> cache = Cache.of(jCache);

        // Create a function that calculates commission (expensive operation)
        Function<String, Double> expensiveCalculation = dealId -> {
            // Extract amount from dealId for demo (in real app, lookup from database)
            double amount = 5000;
            return CacheStructure.calculateCommissionExpensive(dealId, amount);
        };

        // Decorate the function with cache
        Function<String, Double> cachedFunction = Cache.decorateSupplier(
                cache,
                () -> expensiveCalculation.apply("DEAL-001")
        );

        // First call - CACHE MISS (will execute expensive operation)
        logger.info("\n--- First call (expecting CACHE MISS) ---");
        long startTime = System.currentTimeMillis();
        Double result1 = cachedFunction.apply("DEAL-001");
        long duration1 = System.currentTimeMillis() - startTime;
        logger.info("Result: ${}, Duration: {}ms", result1, duration1);

        // Second call - CACHE HIT (will retrieve from cache, much faster)
        logger.info("\n--- Second call (expecting CACHE HIT) ---");
        startTime = System.currentTimeMillis();
        Double result2 = cachedFunction.apply("DEAL-001");
        long duration2 = System.currentTimeMillis() - startTime;
        logger.info("Result: ${}, Duration: {}ms", result2, duration2);

        logger.info("\nPerformance improvement: {}x faster", duration1 / Math.max(duration2, 1));
        logger.info("=== Basic Caching Demo Completed ===\n");
    }

    /**
     * Demonstrates cache with multiple keys
     */
    public static void demonstrateMultiKeyCache() {
        logger.info("=== Multi-Key Cache Demo Started ===");

        javax.cache.Cache<String, Double> jCache = new CacheStructure.SimpleCache<>("multiKeyCache");
        Cache<String, Double> cache = Cache.of(jCache);

        // Process multiple deals
        String[] dealIds = {"DEAL-A", "DEAL-B", "DEAL-C", "DEAL-A", "DEAL-B", "DEAL-D"};
        double[] amounts = {1000, 2000, 3000, 1000, 2000, 4000};

        for (int i = 0; i < dealIds.length; i++) {
            final int index = i;
            String dealId = dealIds[i];
            double amount = amounts[i];

            logger.info("\n--- Processing: {} ---", dealId);

            // Create cached function for this specific deal
            Function<String, Double> cachedCalculation = Cache.decorateSupplier(
                    cache,
                    () -> CacheStructure.calculateCommissionExpensive(dealId, amount)
            );

            long startTime = System.currentTimeMillis();
            Double commission = cachedCalculation.apply(dealId);
            long duration = System.currentTimeMillis() - startTime;

            logger.info("Commission for {}: ${} (took {}ms)", dealId, commission, duration);
        }

        logger.info("\nCache Statistics:");
        logger.info("- DEAL-A: Calculated once, retrieved from cache once");
        logger.info("- DEAL-B: Calculated once, retrieved from cache once");
        logger.info("- DEAL-C: Calculated once");
        logger.info("- DEAL-D: Calculated once");

        logger.info("=== Multi-Key Cache Demo Completed ===\n");
    }

    /**
     * Demonstrates cache invalidation
     */
    public static void demonstrateCacheInvalidation() {
        logger.info("=== Cache Invalidation Demo Started ===");

        javax.cache.Cache<String, Double> jCache = new CacheStructure.SimpleCache<>("invalidationCache");
        Cache<String, Double> cache = Cache.of(jCache);

        String dealId = "DEAL-X";
        double initialAmount = 5000;

        // First calculation
        logger.info("\n--- Initial Calculation ---");
        Function<String, Double> cachedCalc1 = Cache.decorateSupplier(
                cache,
                () -> CacheStructure.calculateCommissionExpensive(dealId, initialAmount)
        );
        Double result1 = cachedCalc1.apply(dealId);
        logger.info("Result: ${}", result1);

        // Second call - from cache
        logger.info("\n--- Second Call (from cache) ---");
        Double result2 = cachedCalc1.apply(dealId);
        logger.info("Result: ${}", result2);

        // Deal amount changed - need to invalidate cache
        logger.info("\n--- Deal Amount Changed - Invalidating Cache ---");
        jCache.remove(dealId); // Explicitly invalidate this entry

        // Third call - cache miss, recalculate with new amount
        logger.info("\n--- Third Call (after invalidation) ---");
        double newAmount = 10000;
        Function<String, Double> cachedCalc2 = Cache.decorateSupplier(
                cache,
                () -> CacheStructure.calculateCommissionExpensive(dealId, newAmount)
        );
        Double result3 = cachedCalc2.apply(dealId);
        logger.info("Result: ${}", result3);

        logger.info("=== Cache Invalidation Demo Completed ===\n");
    }

    /**
     * Demonstrates cache with Time-To-Live (TTL)
     * Uses JCache configuration for expiry
     */
    public static void demonstrateCacheWithTTL() {
        logger.info("=== Cache with TTL Demo Started ===");

        try {
            // Create a cache with 5-second TTL
            CachingProvider cachingProvider = Caching.getCachingProvider();
            CacheManager cacheManager = cachingProvider.getCacheManager();

            // Destroy cache if it already exists
            try {
                cacheManager.destroyCache("ttlCache");
            } catch (Exception e) {
                // Cache doesn't exist, that's fine
            }

            MutableConfiguration<String, Double> config = new MutableConfiguration<String, Double>()
                    .setTypes(String.class, Double.class)
                    .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(java.util.concurrent.TimeUnit.SECONDS, 5)))
                    .setStoreByValue(false);

            javax.cache.Cache<String, Double> jCache =
                    cacheManager.createCache("ttlCache", config);

            Cache<String, Double> cache = Cache.of(jCache);

            String dealId = "TTL-DEAL";

            // First call
            logger.info("\n--- First Call ---");
            Function<String, Double> cachedCalc = Cache.decorateSupplier(
                    cache,
                    () -> CacheStructure.calculateCommissionExpensive(dealId, 3000)
            );
            Double result1 = cachedCalc.apply(dealId);
            logger.info("Result: ${}", result1);

            // Second call immediately - should hit cache
            logger.info("\n--- Second Call (immediate) ---");
            Double result2 = cachedCalc.apply(dealId);
            logger.info("Result: ${}", result2);

            // Wait for TTL to expire
            logger.info("\n--- Waiting 6 seconds for TTL to expire ---");
            Thread.sleep(6000);

            // Third call after TTL - should miss cache
            logger.info("\n--- Third Call (after TTL expiry) ---");
            Double result3 = cachedCalc.apply(dealId);
            logger.info("Result: ${}", result3);

            cacheManager.close();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Demo interrupted", e);
        } catch (javax.cache.CacheException e) {
            logger.warn("JCache provider not available. TTL demo requires a JCache implementation like Ehcache.");
            logger.warn("To enable: Add ehcache dependency to pom.xml");
            logger.info("Demonstrating TTL concept with simple cache instead...");
            demonstrateTTLWithSimpleCache();
        } catch (Exception e) {
            logger.error("Error in TTL demo: {}", e.getMessage(), e);
        }

        logger.info("=== Cache with TTL Demo Completed ===\n");
    }

    /**
     * Fallback TTL demonstration using simple cache with manual expiry tracking
     */
    private static void demonstrateTTLWithSimpleCache() {
        // Helper class to track cache entry time
        class TimedCacheEntry {
            final Double value;
            final long timestamp;

            TimedCacheEntry(Double value) {
                this.value = value;
                this.timestamp = System.currentTimeMillis();
            }

            boolean isExpired(long ttl) {
                return System.currentTimeMillis() - timestamp > ttl;
            }
        }

        CacheStructure.SimpleCache<String, TimedCacheEntry> timeAwareCache = new CacheStructure.SimpleCache<>("ttlFallback");
        String dealId = "TTL-FALLBACK";
        long ttlMillis = 5000; // 5 seconds

        // First call
        logger.info("First call - creating cache entry");
        TimedCacheEntry entry1 = new TimedCacheEntry(300.0);
        timeAwareCache.put(dealId, entry1);
        logger.info("Cached value: ${}", entry1.value);

        // Second call immediately
        logger.info("Second call (immediate) - retrieving from cache");
        TimedCacheEntry entry2 = timeAwareCache.get(dealId);
        if (entry2 != null && !entry2.isExpired(ttlMillis)) {
            logger.info("Cache hit: ${}", entry2.value);
        }

        // Wait for TTL to expire
        logger.info("Waiting 6 seconds for TTL expiry...");
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Third call after TTL
        logger.info("Third call (after TTL) - entry expired");
        TimedCacheEntry entry3 = timeAwareCache.get(dealId);
        if (entry3 != null && entry3.isExpired(ttlMillis)) {
            logger.info("Cache entry expired, would recalculate");
            timeAwareCache.remove(dealId);
            TimedCacheEntry newEntry = new TimedCacheEntry(300.0);
            timeAwareCache.put(dealId, newEntry);
            logger.info("New cache entry created: ${}", newEntry.value);
        }
    }

    /**
     * Demonstrates caching patterns for different scenarios
     */
    public static void demonstrateCachingPatterns() {
        logger.info("=== Caching Patterns Demo Started ===");

        // Pattern 1: Cache-Aside (Lazy Loading)
        logger.info("\n--- Pattern 1: Cache-Aside (Lazy Loading) ---");
        logger.info("Application checks cache first, loads from source on miss, then caches");

        javax.cache.Cache<String, Double> cacheAsideCache = new CacheStructure.SimpleCache<>("cacheAside");

        String dealId = "PATTERN-001";
        Double commission;

        // Check cache
        commission = cacheAsideCache.get(dealId);
        if (commission == null) {
            logger.info("Cache miss - loading from source");
            commission = CacheStructure.calculateCommissionExpensive(dealId, 5000);
            cacheAsideCache.put(dealId, commission);
        } else {
            logger.info("Cache hit - returning cached value");
        }
        logger.info("Commission: ${}", commission);

        // Pattern 2: Write-Through Cache
        logger.info("\n--- Pattern 2: Write-Through Cache ---");
        logger.info("Updates are written to cache and source simultaneously");

        // Simulate updating commission calculation (both cache and "database")
        String dealId2 = "PATTERN-002";
        Double newCommission = 750.0;

        logger.info("Writing commission to both cache and database");
        cacheAsideCache.put(dealId2, newCommission);
        // In real app: also write to database
        logger.info("Database updated with commission: ${}", newCommission);

        // Pattern 3: Read-Through Cache
        logger.info("\n--- Pattern 3: Read-Through Cache ---");
        logger.info("Cache automatically loads data from source on miss");

        Cache<String, Double> readThroughCache = Cache.of(
                new CacheStructure.SimpleCache<>("readThrough")
        );

        Function<String, Double> autoLoader = Cache.decorateSupplier(
                readThroughCache,
                () -> {
                    logger.info("Cache automatically loading from source");
                    return CacheStructure.calculateCommissionExpensive("PATTERN-003", 8000);
                }
        );

        Double result = autoLoader.apply("PATTERN-003");
        logger.info("Result: ${}", result);

        logger.info("=== Caching Patterns Demo Completed ===\n");
    }

    public static void main(String[] args) {
        // Demo 1: Basic caching
        demonstrateBasicCaching();

        logger.info("=".repeat(80) + "\n");

        // Demo 2: Multi-key cache
        demonstrateMultiKeyCache();

        logger.info("=".repeat(80) + "\n");

        // Demo 3: Cache invalidation
        demonstrateCacheInvalidation();

        logger.info("=".repeat(80) + "\n");

        // Demo 4: Cache with TTL
        demonstrateCacheWithTTL();

        logger.info("=".repeat(80) + "\n");

        // Demo 5: Caching patterns
        demonstrateCachingPatterns();
    }
}
