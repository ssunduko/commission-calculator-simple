# Cache Pattern

## Overview
Caching stores results of expensive operations in memory, enabling subsequent requests for the same data to be served quickly without re-executing the operation. It's a fundamental performance optimization technique.

## Implementation Details

### Files Created
- `CacheDemo.java`: Comprehensive demonstration of caching strategies and patterns

### Key Concepts

#### Cache Hit vs Cache Miss

**Cache Hit:**
- Requested data found in cache
- Fast retrieval from memory (microseconds)
- No expensive operation needed
- Reduced load on backend systems

**Cache Miss:**
- Requested data not in cache
- Execute expensive operation (milliseconds to seconds)
- Store result in cache for future use
- First request always a miss ("cold cache")

#### Cache Eviction Strategies

1. **Time-To-Live (TTL)**: Remove after specified time
2. **Least Recently Used (LRU)**: Remove oldest unused entries
3. **Least Frequently Used (LFU)**: Remove least accessed entries
4. **Size-Based**: Remove when cache reaches size limit
5. **Manual**: Explicit removal by application

### Caching Patterns

**1. Cache-Aside (Lazy Loading)**
```
Application → Check Cache → Miss? → Load Data → Cache It → Return
                         → Hit? → Return Cached Data
```
- Application manages cache explicitly
- Most common pattern
- Cache only populated on demand

**2. Read-Through**
```
Application → Request Data → Cache (auto-loads if miss) → Return
```
- Cache automatically loads data on miss
- Simpler application code
- Cache is responsible for loading

**3. Write-Through**
```
Application → Write Data → Cache + Database (simultaneously)
```
- Writes go to both cache and database
- Cache always in sync
- Higher write latency

**4. Write-Behind (Write-Back)**
```
Application → Write to Cache → Async write to Database
```
- Fast writes (only to cache)
- Database updated asynchronously
- Risk of data loss if cache fails

### Configuration Options

| Aspect | Consideration | Recommendation |
|--------|--------------|----------------|
| **TTL** | How long data remains valid | Base on data change frequency |
| **Size** | Maximum cache entries | Base on available memory |
| **Eviction** | What to remove when full | LRU for most cases |
| **Keys** | What identifies cached data | Include all relevant parameters |

### Features Demonstrated

1. **Basic Caching**
   - Simple cache hit/miss demonstration
   - Performance comparison (2000ms → <1ms)
   - Single key caching

2. **Multi-Key Cache**
   - Multiple entries with different keys
   - Key-based retrieval
   - Cache reuse across different requests

3. **Cache Invalidation**
   - Explicit entry removal
   - Data freshness management
   - Handling data updates

4. **TTL-Based Expiry**
   - Automatic entry removal after timeout
   - Time-based data freshness
   - No manual invalidation needed

5. **Caching Patterns**
   - Cache-Aside implementation
   - Write-Through example
   - Read-Through demonstration

### Use Cases in Commission System

1. **Commission Rate Lookup**
   ```java
   // Rates change infrequently - cache for 1 hour
   Cache<String, CommissionRate> rateCache = ...;
   Function<String, CommissionRate> getRatesCached =
       Cache.decorateSupplier(cache,
           () -> database.getCommissionRates(productType));
   ```

2. **User Profile Caching**
   ```java
   // User profiles change rarely during session
   Cache<String, UserProfile> profileCache = ...;
   Function<String, UserProfile> getProfileCached =
       Cache.decorateSupplier(cache,
           () -> database.getUserProfile(userId));
   ```

3. **Complex Calculation Results**
   ```java
   // Cache expensive commission calculations
   Cache<String, Double> calculationCache = ...;
   String cacheKey = dealId + "-" + date;
   Function<String, Double> calculate =
       Cache.decorateSupplier(cache,
           () -> complexCommissionCalculation(deal));
   ```

4. **API Response Caching**
   ```java
   // Cache external validation API responses
   Cache<String, ValidationResult> apiCache = ...;
   Function<String, ValidationResult> validateCached =
       Cache.decorateSupplier(cache,
           () -> externalApi.validate(dealId));
   ```

## Benefits

1. **Performance**: Dramatic latency reduction (milliseconds → microseconds)
2. **Scalability**: Handle more requests with same resources
3. **Cost Reduction**: Fewer database queries, API calls
4. **Availability**: Serve cached data if backend unavailable
5. **User Experience**: Faster response times

## Trade-offs

1. **Memory Usage**: Caches consume RAM
2. **Stale Data**: Cached data may be outdated
3. **Complexity**: Cache invalidation is difficult
4. **Cold Start**: Initial requests are slow (cache empty)
5. **Consistency**: Multiple caches can diverge

## Running the Demo

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.integration.resilience.cache.CacheDemo"
```

## Expected Output

### Demo 1: Basic Caching
- First call: 2000ms (cache miss, expensive operation)
- Second call: <1ms (cache hit, memory retrieval)
- Performance improvement: 2000x faster

### Demo 2: Multi-Key Cache
- DEAL-A: First hit (slow), second hit (fast)
- DEAL-B: First hit (slow), second hit (fast)
- DEAL-C: First hit only (slow)
- DEAL-D: First hit only (slow)

### Demo 3: Cache Invalidation
- Initial: Calculated and cached
- Second: Retrieved from cache
- After invalidation: Recalculated with new value

### Demo 4: TTL
- First call: Calculated and cached
- Second call (immediate): From cache
- Third call (after 6s): Cache expired, recalculated

### Demo 5: Caching Patterns
- Cache-Aside: Manual cache check and population
- Write-Through: Simultaneous cache and database update
- Read-Through: Automatic cache population on miss

## Common Patterns

### Pattern 1: Simple Caching
```java
Cache<K, V> cache = Cache.of(jCache);
Function<K, V> cached = Cache.decorateSupplier(
    cache,
    () -> expensiveOperation(key)
);
V result = cached.apply(key);
```

### Pattern 2: With TTL
```java
MutableConfiguration<K, V> config =
    new MutableConfiguration<K, V>()
        .setTypes(String.class, Double.class)
        .setExpiryPolicyFactory(
            CreatedExpiryPolicy.factoryOf(
                Duration.ONE_HOUR
            )
        );

javax.cache.Cache<K, V> jCache =
    cacheManager.createCache("ttlCache", config);
```

### Pattern 3: Cache Invalidation
```java
// Invalidate single entry
cache.remove(key);

// Invalidate all entries
cache.clear();

// Invalidate based on condition
if (dataChanged) {
    cache.remove(key);
}
```

### Pattern 4: Composite Key
```java
// Cache key includes all relevant parameters
String cacheKey = String.format("%s-%s-%s",
    dealId, userId, date);

Function<String, Result> cached =
    Cache.decorateSupplier(cache,
        () -> calculate(dealId, userId, date));
```

## Best Practices

1. **Choose What to Cache**:
   - Cache expensive operations only
   - Don't cache fast operations (overhead not worth it)
   - Consider memory vs. computation trade-off

2. **Set Appropriate TTL**:
   - Base on data change frequency
   - Shorter TTL for frequently changing data
   - Longer TTL for static reference data
   - Consider "good enough" vs. "always fresh"

3. **Design Good Cache Keys**:
   - Include all parameters affecting result
   - Keep keys short to save memory
   - Use consistent key format
   - Avoid special characters

4. **Handle Cache Misses Gracefully**:
   - Don't assume cache hit
   - Have fallback to source data
   - Monitor cache hit ratio
   - Tune based on metrics

5. **Implement Cache Invalidation Strategy**:
   - Use TTL for time-sensitive data
   - Invalidate on updates
   - Consider cache warming for critical data
   - Document invalidation logic

6. **Monitor Cache Performance**:
   - Track hit/miss ratio (aim for >80% hit rate)
   - Monitor cache size and memory usage
   - Alert on unusual patterns
   - Adjust configuration based on metrics

7. **Prevent Cache Stampede**:
   - Use locking to prevent concurrent loads
   - Implement request coalescing
   - Stagger cache expiry times
   - Pre-warm cache before expiry

## Cache Sizing

### Memory Calculation
```
Cache Size = (Entries × (Key Size + Value Size)) + Overhead
```

Example:
- 10,000 entries
- 50 bytes per key
- 200 bytes per value
- 25% overhead
- Total: ~3 MB

### Sizing Guidelines
- **Small Cache**: < 100 entries, < 1 MB
- **Medium Cache**: 100-10,000 entries, 1-100 MB
- **Large Cache**: > 10,000 entries, > 100 MB

## Combining with Other Patterns

### Cache + Circuit Breaker
```java
// Use cached value when circuit is open
CircuitBreaker cb = CircuitBreaker.ofDefaults("cb");
Cache<K, V> cache = Cache.of(jCache);

Supplier<V> operation = () -> {
    // Try to get fresh data
    return CircuitBreaker.decorateSupplier(cb,
        () -> fetchFromSource(key)
    ).get();
};

try {
    V result = operation.get();
    cache.put(key, result); // Cache fresh data
    return result;
} catch (Exception e) {
    // Circuit open or error - use cached value
    V cached = cache.get(key);
    if (cached != null) {
        return cached;
    }
    throw e;
}
```

### Cache + Retry
```java
// Retry cache population on failure
Retry retry = Retry.ofDefaults("retry");
Cache<K, V> cache = Cache.of(jCache);

Supplier<V> retryableLoader = Retry.decorateSupplier(
    retry,
    () -> fetchFromSource(key)
);

Function<K, V> cached = Cache.decorateSupplier(
    cache,
    retryableLoader
);
```

## Testing

Integration tests are available in:
```
src/test/java/com/chapman/edu/commissions/integration/resilience/
```

## Cache Implementations

### In-Memory (Embedded)
- **Caffeine**: High performance, modern features (✅ Used in this project)
- **Ehcache**: Mature, feature-rich
- **Guava Cache**: Simple, reliable
- **JCache**: Standard API

### JCache Provider
This project uses **Caffeine with JCache support** (`com.github.ben-manes.caffeine:jcache:3.1.8`) which provides:
- High-performance caching with near-optimal hit rates
- JCache (JSR-107) compliance
- Time-based expiration (TTL)
- Size-based eviction
- Low overhead and excellent performance

### Distributed
- **Redis**: Fast, popular, feature-rich
- **Memcached**: Simple, fast
- **Hazelcast**: Distributed data structures
- **Apache Ignite**: In-memory data grid

## References
- [Resilience4j Cache Documentation](https://resilience4j.readme.io/docs/cache)
- [JCache (JSR 107) Specification](https://github.com/jsr107/jsr107spec)
- [Cache Patterns - Martin Fowler](https://martinfowler.com/bliki/TwoHardThings.html)
- [Caffeine Cache](https://github.com/ben-manes/caffeine)
