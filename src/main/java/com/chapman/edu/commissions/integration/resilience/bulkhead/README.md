# Bulkhead Pattern

## Overview
The Bulkhead pattern isolates different parts of an application into pools so that if one fails, the others continue to function. Named after ship compartments, it prevents cascading failures by limiting resource consumption.

## Implementation Details

### Files Created
- `BulkheadDemo.java`: Comprehensive demonstration of both semaphore and thread pool bulkheads

### Key Concepts

#### Ship Analogy
Just as bulkhead compartments in a ship prevent water from flooding the entire vessel when one compartment is breached, software bulkheads prevent one failing component from consuming all resources and bringing down the entire system.

#### Two Implementation Types

**1. Semaphore-Based Bulkhead**
- Uses semaphores to control concurrent access
- No additional threads created
- Lightweight and efficient
- Executes on the calling thread
- Best for: In-memory operations, CPU-bound tasks

**2. Thread Pool Bulkhead**
- Dedicated thread pool with bounded queue
- True isolation between services
- Returns CompletableFuture for async operations
- Best for: I/O operations, network calls, long-running tasks

### Configuration Parameters

#### Semaphore Bulkhead

| Parameter | Value | Purpose |
|-----------|-------|---------|
| `maxConcurrentCalls` | 3 | Maximum concurrent executions allowed |
| `maxWaitDuration` | 500ms | Maximum time to wait for permission |

#### Thread Pool Bulkhead

| Parameter | Value | Purpose |
|-----------|-------|---------|
| `maxThreadPoolSize` | 5 | Maximum threads in the pool |
| `coreThreadPoolSize` | 2 | Core threads always alive |
| `queueCapacity` | 10 | Size of the waiting queue |
| `keepAliveDuration` | 1000ms | Idle time before thread termination |

### Features Demonstrated

1. **Semaphore Bulkhead**
   - Concurrent call limiting
   - Wait/reject behavior
   - Lightweight resource control

2. **Thread Pool Bulkhead**
   - Asynchronous execution with CompletableFuture
   - Queue management
   - True thread isolation

3. **Service Isolation**
   - Multiple bulkheads for different services
   - Critical vs. non-critical service separation
   - Independent failure domains

4. **Metrics and Monitoring**
   - Available concurrent calls tracking
   - Queue depth monitoring
   - Thread pool size tracking
   - Event publishing for success/rejection

### Use Cases in Commission System

1. **Database Connection Pooling**
   ```
   Critical Operations: 10 concurrent connections
   Reporting Queries: 3 concurrent connections
   Analytics: 2 concurrent connections
   ```

2. **External API Calls**
   - Isolate different third-party service calls
   - Prevent one slow API from blocking others
   - Independent timeout and retry policies

3. **Resource-Intensive Calculations**
   - Limit concurrent complex commission calculations
   - Protect CPU resources
   - Queue overflow handling

4. **Multi-Tenant Systems**
   - Separate resource pools per tenant
   - Fair resource allocation
   - Prevent tenant-to-tenant interference

## Benefits

1. **Fault Isolation**: Failures in one component don't affect others
2. **Resource Protection**: Prevents thread pool exhaustion
3. **Predictable Behavior**: Controlled concurrent execution
4. **Improved Resilience**: System degradation instead of total failure
5. **Service Independence**: Critical services remain available

## Running the Demo

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.integration.resilience.bulkhead.BulkheadDemo"
```

## Expected Output

### Demo 1: Semaphore Bulkhead
- Submits 10 concurrent tasks with max 3 concurrent calls
- First 3 tasks execute immediately
- Tasks 4-6 wait for permission (up to 500ms)
- Remaining tasks are rejected (BulkheadFullException)
- Shows concurrent call limiting in action

### Demo 2: Thread Pool Bulkhead
- Submits 20 async tasks
- First 5 execute immediately (thread pool size)
- Next 10 are queued (queue capacity)
- Remaining 5 are rejected (pool + queue full)
- Demonstrates async execution with CompletableFuture

### Demo 3: Service Isolation
- Floods non-critical service with 10 requests (max 2 concurrent)
- Submits 5 critical service requests (max 5 concurrent)
- Shows that critical service remains fully functional
- Demonstrates independence of bulkhead compartments

## Comparison: Semaphore vs Thread Pool

| Aspect | Semaphore Bulkhead | Thread Pool Bulkhead |
|--------|-------------------|---------------------|
| **Execution** | Calling thread | Separate thread pool |
| **Overhead** | Low | Higher (thread management) |
| **Isolation** | Logical only | True thread isolation |
| **Return Type** | Direct result | CompletableFuture |
| **Best For** | CPU-bound, in-memory | I/O-bound, network calls |
| **Queue** | No queue | Bounded queue available |

## Common Patterns

### Pattern 1: Semaphore for Quick Operations
```java
Bulkhead bulkhead = Bulkhead.ofDefaults("quickOps");
Supplier<T> decorated = Bulkhead.decorateSupplier(
    bulkhead,
    () -> quickOperation()
);
T result = decorated.get();
```

### Pattern 2: Thread Pool for Async Operations
```java
ThreadPoolBulkhead bulkhead = ThreadPoolBulkhead.ofDefaults("asyncOps");
Supplier<CompletableFuture<T>> decorated =
    ThreadPoolBulkhead.decorateSupplier(
        bulkhead,
        () -> asyncOperation()
    );
CompletableFuture<T> future = decorated.get();
future.thenAccept(result -> processResult(result));
```

### Pattern 3: Service Isolation
```java
// Critical service gets more resources
Bulkhead criticalBulkhead = Bulkhead.of(
    "critical",
    BulkheadConfig.custom().maxConcurrentCalls(10).build()
);

// Non-critical service gets fewer resources
Bulkhead nonCriticalBulkhead = Bulkhead.of(
    "nonCritical",
    BulkheadConfig.custom().maxConcurrentCalls(2).build()
);
```

## Best Practices

1. **Size Appropriately**:
   - Base limits on actual system capacity
   - Consider thread pool size, CPU cores, memory
   - Test under load to find optimal values

2. **Separate Critical Services**:
   - Use dedicated bulkheads for critical operations
   - Allocate more resources to critical paths
   - Isolate experimental or unstable features

3. **Monitor Metrics**:
   - Track rejection rates
   - Monitor queue depths
   - Alert on sustained high utilization

4. **Choose the Right Type**:
   - Semaphore for lightweight, quick operations
   - Thread pool for I/O-bound, long-running tasks
   - Consider overhead vs. isolation needs

5. **Combine with Other Patterns**:
   - Use with Circuit Breaker for better resilience
   - Combine with Retry for transient failures
   - Add Rate Limiter for additional control

## Testing

Integration tests are available in:
```
src/test/java/com/chapman/edu/commissions/integration/resilience/
```

## References
- [Resilience4j Documentation](https://resilience4j.readme.io/docs/bulkhead)
- [Michael T. Nygard - Release It!](https://pragprog.com/titles/mnee2/release-it-second-edition/)
- [Bulkhead Pattern - Microsoft](https://docs.microsoft.com/en-us/azure/architecture/patterns/bulkhead)