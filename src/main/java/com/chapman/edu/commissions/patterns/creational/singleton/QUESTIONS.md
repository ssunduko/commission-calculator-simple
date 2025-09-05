# Singleton Pattern Knowledge Test

## Multiple Choice Questions

1. What is the primary purpose of the Singleton pattern?
   a) To create multiple instances of a class
   b) To ensure a class has only one instance and provide a global point of access to it
   c) To create a hierarchy of related objects
   d) To separate object construction from its representation

2. Which of the following is NOT a common implementation of the Singleton pattern?
   a) Eager initialization
   b) Lazy initialization with double-checked locking
   c) Factory method implementation
   d) Initialization-on-demand holder idiom

3. Which of these is a key characteristic of the Singleton pattern?
   a) Multiple instances can be created on demand
   b) A private constructor
   c) Public inheritance
   d) Abstract methods

4. In Java, which implementation of the Singleton pattern is considered the most effective?
   a) Basic lazy initialization
   b) Double-checked locking
   c) Enum-based implementation
   d) Static block initialization

5. Which of the following is NOT an advantage of using the Singleton pattern?
   a) Controlled access to the sole instance
   b) Reduced namespace pollution
   c) Flexibility in creating multiple instances
   d) Permits refinement of operations and representation

## Short Answer Questions

6. Explain the difference between eager initialization and lazy initialization in the context of the Singleton pattern.

7. What are the thread safety concerns with the basic Singleton implementation, and how can they be addressed?

8. Describe two real-world scenarios where the Singleton pattern would be appropriate to use.

9. How does the Singleton pattern violate the Single Responsibility Principle, and what are the implications?

10. What are the challenges of implementing Singleton in a distributed system or in a system with multiple class loaders?

## Code Analysis Questions

11. Identify the issues with the following Singleton implementation and explain how you would improve it:
```java
public class DatabaseConnection {
    private static DatabaseConnection instance;
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    private DatabaseConnection() {
        // Initialize database connection
    }
    
    public void executeQuery(String query) {
        // Execute the query
        System.out.println("Executing: " + query);
    }
}
```

12. Analyze the following Singleton implementation and explain its advantages and disadvantages:
```java
public enum ConfigurationManager {
    INSTANCE;
    
    private Properties properties;
    
    ConfigurationManager() {
        properties = new Properties();
        try {
            properties.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            System.err.println("Failed to load configuration: " + e.getMessage());
        }
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
}
```