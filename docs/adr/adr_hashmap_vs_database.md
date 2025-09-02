# Title: Using HashMap Instead of Database for Implementation

## Status
Accepted

## Context
When implementing the commission calculator system, we needed to store and retrieve data efficiently. We considered two main options:
1. Using a traditional database system (SQL or NoSQL)
2. Using in-memory data structures like HashMap

The following constraints influenced our decision:
- Development time and complexity
- Performance requirements for the current scale
- Deployment simplicity
- Current team expertise
- Project scope and requirements

## Decision
We have decided to use HashMap instead of a database for storing and retrieving data in our implementation.

The primary reasons for this decision include:
- Simplicity: HashMap provides a straightforward key-value storage mechanism that is easy to implement and understand
- Development speed: Using HashMap eliminates the need for database setup, connection management, and ORM configuration
- No external dependencies: The solution doesn't require external database systems to be installed or configured
- Sufficient for current needs: The current data volume and access patterns can be efficiently handled by in-memory storage
- Java standard library: HashMap is part of the Java standard library, requiring no additional dependencies

## Consequences
### Positive
- Faster development time
- Simpler deployment without database configuration
- Reduced complexity in the codebase
- No need for database connection management
- Easier testing without database mocking

### Negative
- Data is not persistent across application restarts
- Limited scalability for very large datasets
- No built-in support for concurrent access (would require additional synchronization)
- No transaction support or ACID properties
- No query language for complex data retrieval patterns

### Mitigations
- If persistence becomes necessary, we can implement serialization/deserialization to files
- If data volume grows significantly, we can revisit this decision and migrate to a database solution
- For concurrent access, we can replace HashMap with ConcurrentHashMap or implement proper synchronization