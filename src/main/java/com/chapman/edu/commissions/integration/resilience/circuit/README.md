# Circuit Breaker Pattern

## Overview
The Circuit Breaker pattern prevents an application from repeatedly trying to execute an operation that's likely to fail. It acts as a protective barrier that monitors for failures and prevents cascading failures in distributed systems.

## Implementation Details

### Files Created
- `CircuitBreakerDemo.java`: Comprehensive demonstration of circuit breaker usage with commission calculation examples

### Key Concepts

#### Three States
1. **CLOSED**: Normal operation
   - Requests flow through normally
   - Failures are counted
   - Circuit opens when failure threshold is exceeded

2. **OPEN**: Circuit is tripped
   - Requests fail immediately without attempting the operation
   - Provides fail-fast behavior
   - Gives the failing service time to recover

3. **HALF_OPEN**: Testing recovery
   - Limited number of requests are allowed through
   - If successful, circuit closes
   - If failures continue, circuit reopens

### Configuration Parameters

| Parameter | Value | Purpose |
|-----------|-------|---------|
| `failureRateThreshold` | 50% | Opens circuit if 50% of calls fail |
| `minimumNumberOfCalls` | 5 | Minimum calls before calculating failure rate |
| `waitDurationInOpenState` | 10 seconds | Time before transitioning to HALF_OPEN |
| `permittedNumberOfCallsInHalfOpenState` | 3 | Test calls allowed in HALF_OPEN state |
| `slidingWindowSize` | 10 | Number of calls to track |
| `slowCallDurationThreshold` | 2 seconds | Calls slower than this are considered failures |

### Features Demonstrated

1. **Basic Circuit Breaker Setup**
   - Configuration with custom thresholds
   - Registry-based management
   - Event listeners for state transitions

2. **Decorated Suppliers**
   - Wrapping remote calls with circuit breaker protection
   - Automatic failure detection

3. **Fallback Mechanism**
   - Graceful degradation when circuit is open
   - Conservative default values

4. **Monitoring and Metrics**
   - State tracking
   - Failure rate calculation
   - Success/failure counts

### Use Cases in Commission System

1. **Remote Commission Calculation**
   - Protecting against failing commission service
   - Providing fallback commission rates

2. **Database Operations**
   - Preventing database overload
   - Graceful handling of database failures

3. **External API Calls**
   - Rate protection for third-party services
   - Preventing timeout exhaustion

## Benefits

1. **Prevents Cascading Failures**: Stops failure propagation through the system
2. **Fail-Fast**: Immediate response when service is down
3. **Self-Recovery**: Automatic testing and recovery
4. **Resource Protection**: Prevents thread pool exhaustion
5. **Better User Experience**: Predictable failure handling

## Running the Demo

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.integration.resilience.circuit.CircuitBreakerDemo"
```

## Expected Output

The demo will show:
- Initial CLOSED state with successful and failed calls
- Transition to OPEN state when failure threshold is exceeded
- Automatic transition to HALF_OPEN after wait duration
- Recovery back to CLOSED state or return to OPEN based on test results
- Metrics showing failure rates and call counts

## Testing

Integration tests are available in:
```
src/test/java/com/chapman/edu/commissions/integration/resilience/
```

## References
- [Resilience4j Documentation](https://resilience4j.readme.io/docs/circuitbreaker)
- [Martin Fowler - Circuit Breaker](https://martinfowler.com/bliki/CircuitBreaker.html)