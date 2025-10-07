# Singleton Pattern Implementation

## Overview

This directory contains comprehensive implementations of the **Singleton Pattern** applied to commission calculation management in a sales system. The Singleton Pattern is a creational design pattern that ensures a class has only one instance throughout the application lifecycle while providing a global point of access to that instance. This implementation demonstrates six different approaches to implementing singletons in Java, each with varying levels of thread safety, performance characteristics, and use cases.


## What Is a Singleton Pattern?

The **Singleton Pattern** is a creational design pattern that restricts the instantiation of a class to a single instance and provides a global point of access to that instance. It is one of the original 23 Gang of Four design patterns and remains one of the most widely recognized patterns in software engineering.

### The Core Concept

Imagine you're building an application that needs a configuration manager. You don't want multiple configuration objects floating around with potentially different settings—you want **one single source of truth** that the entire application can reference. This is exactly what the Singleton Pattern provides.

### Real-World Analogy

Think of a singleton like a country's government. There's only one official government for a country at any given time, and everyone in the country references the same government. You can't create your own government—you access the existing one. Similarly, a singleton class prevents you from creating new instances and instead provides access to the one instance that exists.

### Fundamental Characteristics

A proper singleton implementation has three essential characteristics:

1. **Private Constructor**: Prevents external code from creating new instances using `new Singleton()`
2. **Static Instance**: A static variable holds the single instance of the class
3. **Public Static Accessor**: A static method (typically `getInstance()`) provides global access to the instance

### Simple Example

```java
public class DatabaseConnection {
    // The single instance
    private static DatabaseConnection instance;

    // Private constructor prevents instantiation
    private DatabaseConnection() {
        // Initialize database connection
    }

    // Global access point
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Business methods
    public void executeQuery(String sql) {
        // Execute query using the single connection
    }
}

// Usage
DatabaseConnection db1 = DatabaseConnection.getInstance();
DatabaseConnection db2 = DatabaseConnection.getInstance();
// db1 and db2 reference the SAME object
```

### When to Use Singleton

The Singleton Pattern is appropriate when:

- **Exactly one instance** is needed to coordinate actions across the system
- **Global access** to that instance is required from various parts of the codebase
- **Resource management** requires centralized control (logging, caching, thread pools)
- **Configuration management** needs a single source of truth
- **State sharing** must be consistent across the entire application

### When NOT to Use Singleton

Avoid singletons when:

- You simply want to avoid passing parameters (use dependency injection instead)
- The class doesn't truly need to be restricted to one instance
- You need good testability and mockability
- The pattern would introduce unnecessary global state
- A simpler solution (like static utility methods) would suffice

## Pattern Intent

The Singleton Pattern serves to:

- **Ensure Single Instance**: Guarantee that a class has exactly one instance per JVM (or classloader)
- **Global Access Point**: Provide a well-defined access point to that instance from anywhere in the application
- **Controlled Instantiation**: Restrict object creation to ensure the class itself manages its sole instance
- **Resource Management**: Manage shared resources efficiently (e.g., configuration, connection pools, caches)
- **State Consistency**: Maintain consistent state across the entire application through a single object

## Key Benefits

1. **Controlled Access to Sole Instance**: The class controls how and when clients access the single instance
2. **Reduced Namespace Pollution**: Avoids global variables while providing global access
3. **Permits Refinement**: Can be subclassed (with caution) to extend behavior
4. **Resource Efficiency**: Only one instance created, reducing memory footprint
5. **Lazy Initialization**: Instance can be created only when first needed (depending on implementation)
6. **Thread Safety**: Properly implemented singletons are safe in multi-threaded environments
7. **Flexible Implementation**: Multiple approaches available to match different requirements

## Files in This Directory

### Implementation Files

1. **`BasicSingleton.java`**
   - Simplest implementation with lazy initialization
   - NOT thread-safe (educational purposes only)
   - Demonstrates the core singleton concept

2. **`ThreadSafeSingletonSynchronizedMethod.java`**
   - Thread-safe with synchronized `getInstance()` method
   - Performance trade-off due to synchronization overhead
   - Simple but less efficient under high concurrency

3. **`ThreadSafeSingletonDoubleCheckedLocking.java`** ⭐
   - Thread-safe with minimal synchronization overhead
   - Uses `volatile` keyword and double-checked locking pattern
   - Recommended for performance-critical lazy initialization

4. **`ThreadSafeSingletonEagerInitialization.java`**
   - Thread-safe with eager initialization at class loading
   - No lazy initialization
   - Suitable when instance is always needed

5. **`ThreadSafeSingletonInitializationOnDemand.java`** ⭐
   - Thread-safe lazy initialization using static inner class
   - No synchronization overhead
   - Best overall approach for most use cases

6. **`EnumSingleton.java`** ⭐
   - Joshua Bloch's recommended approach (Effective Java)
   - Thread-safe, serialization-safe, reflection-proof
   - Simplest and safest implementation

### Supporting Files

7. **`SingletonDemo.java`**
   - Demonstrates usage of all singleton implementations
   - Verifies single instance property
   - Shows commission calculation integration

8. **`CommissionCalculation.java`**
   - Domain model managed by singleton instances
   - Represents commission calculation state and operations

### Documentation Files

9. **`singleton.puml`**
   - Comprehensive PlantUML diagram showing all six implementations
   - Illustrates relationships with domain model
   - Includes detailed notes on trade-offs

10. **`basic-singleton.puml`**
    - Focused diagram on double-checked locking pattern
    - Step-by-step algorithm explanation
    - Thread safety and performance analysis

11. **`README.md`** (this file)
    - Comprehensive pattern documentation
    - Implementation comparisons and best practices

## Pattern Participants

The Singleton Pattern involves the following key participants:

### 1. Singleton (e.g., `ThreadSafeSingletonDoubleCheckedLocking`)

**Responsibilities:**
- Defines a `getInstance()` static method that returns the sole instance
- May be responsible for creating its own unique instance
- Ensures only one instance exists

**Structure:**
```java
public class Singleton {
    // 1. Static instance variable (holds the single instance)
    private static volatile Singleton instance;

    // 2. Private constructor (prevents external instantiation)
    private Singleton() {
        // Initialization code
    }

    // 3. Static getInstance() method (provides global access)
    public static Singleton getInstance() {
        // Creation logic (varies by implementation)
        return instance;
    }

    // 4. Business methods
    public void doSomething() {
        // Business logic
    }
}
```

### 2. Client (e.g., `SingletonDemo`)

**Responsibilities:**
- Accesses the singleton instance through `getInstance()` method
- Uses the singleton's business methods
- Does NOT create instances directly

**Usage Pattern:**
```java
public class Client {
    public void performOperation() {
        // Access singleton instance
        Singleton singleton = Singleton.getInstance();

        // Use singleton functionality
        singleton.doSomething();
    }
}
```

### Participant Relationships

- The **Singleton** class is responsible for managing its own lifecycle
- **Clients** depend on the Singleton but cannot control instantiation
- The relationship is one-to-many: one Singleton instance serves many clients
- All clients receive the same instance reference

## How It Works

### Common Pitfalls to Avoid

Before diving into how singletons work, it's crucial to understand common pitfalls:

#### 1. Thread Safety Violations
**Problem:** Multiple threads creating multiple instances simultaneously.

**Example (WRONG):**
```java
public class UnsafeSingleton {
    private static UnsafeSingleton instance;

    public static UnsafeSingleton getInstance() {
        if (instance == null) {  // Thread A and B both see null
            instance = new UnsafeSingleton();  // Both create instances!
        }
        return instance;
    }
}
```

**Solution:** Use proper synchronization (double-checked locking, initialization-on-demand, or enum).

#### 2. Serialization Breaking Singleton
**Problem:** Deserializing creates a new instance, violating the singleton property.

**Solution:** Implement `readResolve()`:
```java
protected Object readResolve() {
    return getInstance();
}
```

#### 3. Reflection Attacks
**Problem:** Reflection can access private constructor and create new instances.

**Solution:** Throw exception in constructor if instance exists:
```java
private Singleton() {
    if (instance != null) {
        throw new IllegalStateException("Instance already exists!");
    }
}
```

Or use **Enum Singleton** (immune to reflection).

#### 4. Cloning Violations
**Problem:** Cloning creates a new instance.

**Solution:** Override `clone()`:
```java
@Override
protected Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException("Singleton cannot be cloned");
}
```

#### 5. Multiple Classloader Issues
**Problem:** Different classloaders can create separate instances.

**Scenario:** Common in application servers with multiple web applications.

**Solution:** Be aware of deployment architecture; use dependency injection containers when needed.

#### 6. Testing Difficulties
**Problem:** Global state makes unit testing hard; difficult to mock or reset.

**Solution:** Use dependency injection or design for testability:
```java
// Instead of direct singleton access
public class Service {
    private final Calculator calculator;

    public Service() {
        this.calculator = Calculator.getInstance(); // Hard to mock
    }
}

// Better: inject the dependency
public class Service {
    private final Calculator calculator;

    public Service(Calculator calculator) {
        this.calculator = calculator; // Easy to mock in tests
    }
}
```

#### 7. Unnecessary Singletons
**Problem:** Overusing singletons when simpler alternatives exist.

**Guideline:** Use singletons only when truly needed for:
- Resource pooling (database connections, thread pools)
- Configuration management
- Logging services
- Caching mechanisms

#### 8. State Management Issues
**Problem:** Mutable state in singletons causing unexpected behavior across the application.

**Best Practice:** Prefer stateless singletons or immutable state.

### How the Pattern Works (Implementation Details)

## Six Singleton Implementations

### 1. **Six Singleton Implementations**

Each implementation demonstrates a different approach to creating singletons, with varying levels of thread safety and performance characteristics:

#### BasicSingleton
- **Type:** Lazy initialization, NOT thread-safe
- **Use Case:** Single-threaded environments only
- **Key Feature:** Simplest implementation
- **File:** `BasicSingleton.java`

```java
public class BasicSingleton {
    private static BasicSingleton instance;

    private BasicSingleton() { }

    public static BasicSingleton getInstance() {
        if (instance == null) {
            instance = new BasicSingleton();
        }
        return instance;
    }
}
```

**Problem:** Multiple threads can create multiple instances simultaneously.

#### ThreadSafeSingletonSynchronizedMethod
- **Type:** Lazy initialization, Thread-safe
- **Use Case:** When simplicity is more important than performance
- **Key Feature:** Entire `getInstance()` method is synchronized
- **File:** `ThreadSafeSingletonSynchronizedMethod.java`

**Trade-off:** Synchronization overhead on every call.

#### ThreadSafeSingletonDoubleCheckedLocking ⭐ (Recommended)
- **Type:** Lazy initialization, Thread-safe, High performance
- **Use Case:** Most production scenarios requiring lazy initialization
- **Key Feature:** Minimizes synchronization overhead
- **File:** `ThreadSafeSingletonDoubleCheckedLocking.java`

```java
public class ThreadSafeSingletonDoubleCheckedLocking {
    private static volatile ThreadSafeSingletonDoubleCheckedLocking instance;

    private ThreadSafeSingletonDoubleCheckedLocking() { }

    public static ThreadSafeSingletonDoubleCheckedLocking getInstance() {
        if (instance == null) {                      // First check (no lock)
            synchronized (ThreadSafeSingletonDoubleCheckedLocking.class) {
                if (instance == null) {              // Second check (with lock)
                    instance = new ThreadSafeSingletonDoubleCheckedLocking();
                }
            }
        }
        return instance;
    }
}
```

**Why it works:**
1. **First check:** Fast path when instance already exists
2. **Synchronization:** Only when instance is null
3. **Second check:** Prevents race condition
4. **volatile:** Ensures visibility across threads

#### ThreadSafeSingletonEagerInitialization
- **Type:** Eager initialization, Thread-safe
- **Use Case:** When instance will definitely be used
- **Key Feature:** Instance created at class loading time
- **File:** `ThreadSafeSingletonEagerInitialization.java`

```java
public class ThreadSafeSingletonEagerInitialization {
    private static final ThreadSafeSingletonEagerInitialization instance =
        new ThreadSafeSingletonEagerInitialization();

    private ThreadSafeSingletonEagerInitialization() { }

    public static ThreadSafeSingletonEagerInitialization getInstance() {
        return instance;
    }
}
```

**Trade-off:** Instance created even if never used.

#### ThreadSafeSingletonInitializationOnDemand ⭐ (Best Practice)
- **Type:** Lazy initialization, Thread-safe, No synchronization overhead
- **Use Case:** Production environments (best overall approach)
- **Key Feature:** Uses static inner class (JVM handles synchronization)
- **File:** `ThreadSafeSingletonInitializationOnDemand.java`

```java
public class ThreadSafeSingletonInitializationOnDemand {
    private ThreadSafeSingletonInitializationOnDemand() { }

    private static class Holder {
        private static final ThreadSafeSingletonInitializationOnDemand INSTANCE =
            new ThreadSafeSingletonInitializationOnDemand();
    }

    public static ThreadSafeSingletonInitializationOnDemand getInstance() {
        return Holder.INSTANCE;
    }
}
```

**How it works:** JVM doesn't load `Holder` class until `getInstance()` is called.

#### EnumSingleton ⭐ (Joshua Bloch Recommendation)
- **Type:** Eager initialization, Thread-safe, Serialization-safe
- **Use Case:** When you want the safest, simplest implementation
- **Key Feature:** Uses Java enum type
- **File:** `EnumSingleton.java`

```java
public enum EnumSingleton {
    INSTANCE;

    private CommissionCalculation commissionCalculation;

    EnumSingleton() {
        this.commissionCalculation = new CommissionCalculation();
    }

    public CommissionCalculation getCommissionCalculation() {
        return commissionCalculation;
    }
}
```

**Benefits:**
- Cannot be instantiated via reflection
- Serialization-safe by default
- Thread-safe by JVM guarantee
- Concise and clear

### 2. **Domain Integration**

All singleton implementations manage a `CommissionCalculation` object, demonstrating real-world usage:

```java
public class ThreadSafeSingletonDoubleCheckedLocking {
    private CommissionCalculation commissionCalculation;

    private ThreadSafeSingletonDoubleCheckedLocking() {
        this.commissionCalculation = new CommissionCalculation();
        this.commissionCalculation.setId("SINGLETON-THREAD-SAFE-DOUBLE-CHECKED");
        this.commissionCalculation.setCalculatedBy("ThreadSafeSingletonDoubleCheckedLocking");
    }

    public CommissionCalculation calculateCommission(String dealId,
                                                     String salesRepId,
                                                     BigDecimal amount) {
        CommissionCalculation calculation = new CommissionCalculation(dealId, salesRepId, amount);
        calculation.recalculate();
        return calculation;
    }
}
```

### 3. **Visual Documentation**

#### singleton.puml
Comprehensive diagram showing all six implementations with:
- Class structures
- Relationships with domain model
- Detailed notes explaining each approach
- Trade-offs and considerations

#### basic-singleton.puml
Focused diagram on the **Double-Checked Locking** pattern featuring:
- Step-by-step explanation of the algorithm
- Thread safety scenarios
- Performance characteristics
- When to use this approach

### 4. **Demonstration Code**

`SingletonDemo.java` demonstrates:
- Usage of each singleton implementation
- Instance verification (proving single instance)
- Commission calculation using singletons

## Pattern Components

### Core Elements

1. **Private Constructor**
    - Prevents external instantiation
    - Only the class itself can create instances

2. **Static Instance Variable**
    - Holds the single instance
    - May be `volatile` for thread safety (double-checked locking)

3. **Static getInstance() Method**
    - Provides global access point
    - Creates instance if it doesn't exist (lazy) or returns existing instance

4. **Final Class** (recommended)
    - Prevents subclassing which could break singleton property

## Implementation Comparison

| Implementation | Thread-Safe | Lazy Init | Performance | Complexity | Serialization-Safe |
|---------------|-------------|-----------|-------------|------------|-------------------|
| Basic | ✗ | ✓ | High | Low | ✗ |
| Synchronized Method | ✓ | ✓ | Low | Low | ✗ |
| Double-Checked Locking | ✓ | ✓ | High | Medium | ✗ |
| Eager Initialization | ✓ | ✗ | High | Low | ✗ |
| Initialization-on-Demand | ✓ | ✓ | High | Medium | ✗ |
| Enum | ✓ | ✗ | High | Low | ✓ |

## When to Use Each Implementation

### BasicSingleton
- ✓ Single-threaded applications
- ✓ Learning/educational purposes
- ✗ Production multi-threaded environments

### ThreadSafeSingletonSynchronizedMethod
- ✓ When simplicity trumps performance
- ✓ Low-traffic applications
- ✗ High-concurrency scenarios

### ThreadSafeSingletonDoubleCheckedLocking
- ✓ Multi-threaded environments
- ✓ Performance-critical applications
- ✓ Lazy initialization required
- Requires Java 5+ (for proper `volatile` semantics)

### ThreadSafeSingletonEagerInitialization
- ✓ Instance will definitely be used
- ✓ Lightweight objects
- ✗ Resource-heavy initialization
- ✗ Initialization might fail

### ThreadSafeSingletonInitializationOnDemand (Best Overall)
- ✓ Multi-threaded environments
- ✓ Lazy initialization required
- ✓ No synchronization overhead
- ✓ **Recommended for most use cases**

### EnumSingleton (Best for Safety)
- ✓ Simplicity is paramount
- ✓ Serialization required
- ✓ Protection against reflection attacks
- ✗ Cannot lazy-initialize with parameters

## Real-World Usage in Commission Calculator

### Centralized Commission Configuration
```java
// Single source of truth for commission settings
CommissionConfigSingleton config = CommissionConfigSingleton.getInstance();
config.setDefaultRate(new BigDecimal("0.10"));
config.setTierThresholds(Arrays.asList(10000, 50000, 100000));
```

### Global Calculator Access
```java
// Access the same calculator instance throughout the application
ThreadSafeSingletonDoubleCheckedLocking calculator =
    ThreadSafeSingletonDoubleCheckedLocking.getInstance();

CommissionCalculation result = calculator.calculateCommission(
    "DEAL-001", "REP-001", new BigDecimal("50000")
);
```

### Database Connection Manager (Hypothetical)
```java
public class DatabaseConnectionManager {
    private static volatile DatabaseConnectionManager instance;
    private DataSource dataSource;

    private DatabaseConnectionManager() {
        // Initialize expensive database connection pool
        this.dataSource = createDataSource();
    }

    public static DatabaseConnectionManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnectionManager.class) {
                if (instance == null) {
                    instance = new DatabaseConnectionManager();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
```

## Common Pitfalls and Solutions

### 1. Serialization Breaking Singleton
**Problem:** Deserializing creates a new instance.

**Solution:** Implement `readResolve()`:
```java
protected Object readResolve() {
    return getInstance();
}
```

### 2. Reflection Breaking Singleton
**Problem:** Reflection can access private constructor.

**Solution:** Throw exception in constructor if instance exists:
```java
private Singleton() {
    if (instance != null) {
        throw new IllegalStateException("Instance already exists!");
    }
}
```

Or use **Enum Singleton** (immune to reflection).

### 3. Cloning Breaking Singleton
**Problem:** Cloning creates a new instance.

**Solution:** Override `clone()`:
```java
@Override
protected Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException("Singleton cannot be cloned");
}
```

### 4. Testing Difficulty
**Problem:** Global state makes unit testing hard.

**Solution:** Use dependency injection or make singleton mockable:
```java
// Instead of:
Calculator calc = Calculator.getInstance();

// Use:
Calculator calc;

@Before
public void setUp() {
    calc = Calculator.getInstance(); // or mock
}
```

## Best Practices

1. **Make class `final`** to prevent subclassing
2. **Use `volatile`** for double-checked locking (Java 5+)
3. **Document singleton behavior** clearly
4. **Handle serialization** properly if needed
5. **Prefer Initialization-on-Demand or Enum** for new code
6. **Consider dependency injection** frameworks (Spring, Guice) as alternative
7. **Avoid state** in singletons when possible (prefer stateless)
8. **Lazy initialization** only when beneficial (consider eager init cost)

## Running the Examples

```bash
# Compile
javac com/chapman/edu/commissions/patterns/creational/singleton/*.java

# Run demo
java com.chapman.edu.commissions.patterns.creational.singleton.SingletonDemo
```

Or using Maven:
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.patterns.creational.singleton.SingletonDemo"
```

## Key Takeaways

1. **Singleton ensures one instance** per JVM (usually per classloader)
2. **Thread safety is critical** in multi-threaded environments
3. **Double-Checked Locking** balances lazy init with performance
4. **Initialization-on-Demand** is often the best overall approach
5. **Enum Singleton** is the safest, simplest for most cases
6. **Consider alternatives** like dependency injection
7. **Test carefully** - singletons introduce global state

## Further Reading

- **"Effective Java" by Joshua Bloch** - Item 3: Enforce singleton with private constructor or enum
- **"Design Patterns" (Gang of Four)** - Singleton Pattern
- **Java Concurrency in Practice** - Safe publication and initialization
- **Double-Checked Locking Article** by Doug Lea

## Related Patterns

- **Factory Pattern** - Often implemented as singleton
- **Abstract Factory** - Often implemented as singleton
- **Builder Pattern** - May use singleton for director
- **Dependency Injection** - Modern alternative to singleton

## Conclusion

The Singleton Pattern is one of the most widely recognized and frequently used creational design patterns in software engineering. This directory demonstrates that while the pattern appears simple on the surface, proper implementation requires careful consideration of thread safety, performance, and potential pitfalls.

### Key Insights

**Multiple Valid Approaches:** There is no single "correct" way to implement a singleton. The six implementations in this directory each serve different needs:
- **BasicSingleton** teaches the fundamental concept
- **Synchronized Method** prioritizes simplicity
- **Double-Checked Locking** balances performance with thread safety
- **Eager Initialization** ensures thread safety through JVM guarantees
- **Initialization-on-Demand** provides the best overall balance
- **Enum Singleton** offers the safest, most robust implementation

**Context Matters:** The "best" implementation depends on your specific requirements:
- Need lazy initialization? Consider **Initialization-on-Demand** or **Double-Checked Locking**
- Want maximum safety? Use **Enum Singleton**
- Dealing with legacy code? **Eager Initialization** might be safest
- Learning the pattern? Start with **BasicSingleton** then understand its flaws

**Modern Alternatives:** While singletons solve real problems, modern applications often benefit from dependency injection frameworks (Spring, Guice, Dagger) that provide:
- Better testability through dependency injection
- Lifecycle management
- Scope control (singleton, prototype, request, session, etc.)
- Reduced coupling

**Use Judiciously:** Singletons are powerful but can be overused. Apply them when you genuinely need:
- A single source of truth (configuration, logging)
- Resource management (connection pools, caches)
- Coordinated access to shared resources

Avoid singletons when:
- You're just trying to avoid passing parameters
- The "singleton" nature isn't truly required
- Testing and mockability are priorities

### Educational Value

This implementation demonstrates software engineering principles beyond just the Singleton Pattern:

- **Thread Safety**: Understanding concurrent programming challenges
- **Performance Optimization**: Balancing safety with efficiency
- **API Design**: Creating clear, intuitive interfaces
- **Trade-off Analysis**: Evaluating multiple solutions to the same problem
- **Best Practices**: Learning from expert recommendations (Joshua Bloch, Gang of Four)

### Final Recommendation

For new Java projects:
1. **First choice**: Use **Initialization-on-Demand** for most singletons
2. **Second choice**: Use **Enum Singleton** when serialization safety is needed
3. **Consider**: Dependency injection frameworks as a more flexible alternative

For learning:
1. Start with **BasicSingleton** to understand the core concept
2. Explore **Double-Checked Locking** to understand threading challenges
3. Study **Enum Singleton** to see how language features can simplify patterns

The Singleton Pattern remains relevant because it addresses fundamental concerns in software architecture: controlling object creation, managing shared resources, and providing global access points. Understanding its various implementations and trade-offs makes you a more effective software engineer, capable of choosing the right tool for the right job.

---

**Remember:** A well-implemented singleton is invisible in its correctness and efficiency. It simply works, providing reliable service throughout your application's lifecycle. The examples in this directory show you how to achieve that reliability.