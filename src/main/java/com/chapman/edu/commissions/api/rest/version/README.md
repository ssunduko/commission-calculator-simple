# API Versioning - Path-Based Versioning Strategy

This package implements a **path-based versioning strategy** for the REST API, allowing multiple versions of the API to coexist and evolve independently.

## Overview

Path-based versioning embeds the API version directly in the URL path, making it explicit and easy to identify. This approach enables:

- **Backward compatibility**: Old clients continue to work with deprecated versions
- **Independent evolution**: New versions can introduce breaking changes without affecting existing clients
- **Clear migration path**: Clients can migrate to new versions at their own pace
- **Version visibility**: Version is immediately apparent in the URL

## Architecture

### URL Structure

```
/api/{version}/{resource}/{id}
```

**Examples:**
- `/api/v1/deals` - Version 1 of deals endpoint
- `/api/v2/deals?page=1&limit=20` - Version 2 with pagination
- `/api/v3/deals` - Future version 3

### Components

| Component | Responsibility | Design Pattern |
|-----------|---------------|----------------|
| **ApiVersion** | Enumerates available API versions | Enum Pattern |
| **VersionedEndpoint** | Interface for version-specific implementations | Strategy Pattern |
| **VersionRouter** | Routes requests to appropriate version handler | Router + Registry Pattern |
| **DealEndpointV1** | Version 1 implementation (deprecated) | Strategy Implementation |
| **DealEndpointV2** | Version 2 implementation (current) | Strategy Implementation |
| **VersionedDealServlet** | Servlet that delegates to version handlers | Front Controller |

### Architecture Diagram

See `version-architecture.puml` for a detailed PlantUML diagram showing:
- Class structure and relationships
- Design patterns applied
- Component notes and annotations
- Version comparison and features

## Design Patterns

### 1. Strategy Pattern

**Intent**: Define a family of algorithms (version implementations), encapsulate each one, and make them interchangeable.

**Implementation**:
- `VersionedEndpoint` interface defines the contract
- `DealEndpointV1` and `DealEndpointV2` are concrete strategies
- Each version provides different behavior for the same operations

**Benefits**:
- Clean separation between versions
- Easy to add new versions without modifying existing code
- Version-specific logic is isolated

### 2. Router Pattern

**Intent**: Direct requests to appropriate handlers based on routing criteria (version).

**Implementation**:
- `VersionRouter` extracts version from URL path
- Maps versions to their endpoint implementations
- Delegates request handling to the selected version

**Benefits**:
- Centralized routing logic
- Version selection is transparent to clients
- Easy to configure version mappings

### 3. Registry Pattern

**Intent**: Maintain a registry of version-to-handler mappings.

**Implementation**:
- `VersionRouter` maintains a `Map<ApiVersion, VersionedEndpoint>`
- Versions are registered at initialization
- Fast lookup during request processing

## API Versions

### Version 1 (V1) - Deprecated

**Status**: Deprecated (maintained for backward compatibility)

**Features**:
- Basic CRUD operations
- Simple status filtering
- Returns all fields
- No pagination

**Limitations**:
- Performance issues with large datasets
- No advanced filtering
- Basic error messages
- No computed fields

**Example Request**:
```bash
GET /api/v1/deals?status=WON
```

**Example Response**:
```json
[
  {
    "id": "DEAL-001",
    "title": "Enterprise License",
    "value": 50000.00,
    "status": "WON",
    "salesRepId": "USER-001",
    "products": [...]
  }
]
```

### Version 2 (V2) - Current Stable

**Status**: Current stable version (recommended)

**Enhancements over V1**:
- ✅ Pagination support (`page`, `limit` parameters)
- ✅ Advanced filtering (multiple criteria: status, salesRepId, minValue)
- ✅ Enhanced error responses with error codes
- ✅ Response metadata (total count, page info, navigation flags)
- ✅ Computed fields (commission estimates, product counts)
- ✅ Location header on resource creation

**Example Request**:
```bash
GET /api/v2/deals?status=WON&page=1&limit=10&minValue=10000
```

**Example Response**:
```json
{
  "data": [
    {
      "id": "DEAL-001",
      "title": "Enterprise License",
      "value": 50000.00,
      "status": "WON",
      "salesRepId": "USER-001",
      "products": [...],
      "estimatedCommission": 5000.00,
      "productCount": 3
    }
  ],
  "metadata": {
    "page": 1,
    "limit": 10,
    "totalCount": 45,
    "totalPages": 5,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

**Error Response**:
```json
{
  "error": "Deal not found with ID: DEAL-999",
  "errorCode": "RESOURCE_NOT_FOUND",
  "status": 404,
  "timestamp": 1699564800000
}
```

### Version 3 (V3) - Experimental

**Status**: Experimental (subject to change)

**Planned Features**:
- GraphQL-style field selection
- Batch operations
- WebSocket support for real-time updates
- Enhanced caching support

## Usage

### Registering Version Handlers

```java
// Create repository
Repository<Deal> dealRepository = new InMemoryRepository<>("DEAL",
    Deal::getId, Deal::setId);

// Create versioned servlet
VersionedDealServlet servlet = new VersionedDealServlet(dealRepository);

// The servlet automatically registers V1 and V2 implementations:
// router.register(ApiVersion.V1, new DealEndpointV1(dealRepository));
// router.register(ApiVersion.V2, new DealEndpointV2(dealRepository));
```

### Implementing a New Version

To add Version 3:

1. **Create V3 Implementation**:
```java
public class DealEndpointV3 implements VersionedEndpoint {
    private final Repository<Deal> dealRepository;

    public DealEndpointV3(Repository<Deal> dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Override
    public void handleGet(HttpServletRequest request,
                         HttpServletResponse response) throws Exception {
        // V3-specific implementation with GraphQL-style field selection
    }

    @Override
    public ApiVersion getVersion() {
        return ApiVersion.V3;
    }

    // Implement other methods...
}
```

2. **Register in Servlet**:
```java
public VersionedDealServlet(Repository<Deal> dealRepository) {
    this.router = new VersionRouter("deals");
    router.register(ApiVersion.V1, new DealEndpointV1(dealRepository));
    router.register(ApiVersion.V2, new DealEndpointV2(dealRepository));
    router.register(ApiVersion.V3, new DealEndpointV3(dealRepository)); // Add V3
}
```

### Client Migration

**Detecting Version in Use**:
```bash
curl -i http://localhost:8080/api/v2/deals
```

Response includes version header:
```
HTTP/1.1 200 OK
API-Version: 2.0
Content-Type: application/json
```

**Deprecation Warnings**:

When using deprecated V1:
```
HTTP/1.1 200 OK
API-Version: 1.0
Warning: 299 - "API version v1 is deprecated. Please migrate to v2."
```

**Migration Steps**:
1. Test new version in parallel (call both V1 and V2)
2. Update code to handle V2 response format (pagination, metadata)
3. Switch client to use V2 endpoints
4. Monitor for errors and rollback if needed
5. Remove V1 calls once V2 is stable

## Design Principles

### Single Responsibility Principle (SRP)

Each class has one clear responsibility:
- `ApiVersion`: Manages version metadata
- `VersionRouter`: Routes requests to versions
- `DealEndpointV1`: Handles V1 requests
- `DealEndpointV2`: Handles V2 requests

### Open/Closed Principle (OCP)

The system is:
- **Open for extension**: Easy to add new versions by implementing `VersionedEndpoint`
- **Closed for modification**: Adding V3 doesn't require changing V1 or V2 code

### Dependency Inversion Principle (DIP)

- `VersionedDealServlet` depends on `VersionedEndpoint` interface, not concrete implementations
- Version handlers depend on `Repository<T>` interface, not concrete storage

## Benefits

✅ **Backward Compatibility**: Old clients continue to work with deprecated versions

✅ **Parallel Development**: Teams can work on different versions independently

✅ **Clear Migration Path**: Version is explicit in URL, making migration planning easier

✅ **Feature Flexibility**: New versions can introduce breaking changes safely

✅ **Testability**: Each version can be tested independently

✅ **Documentation**: API documentation clearly shows version differences

## Trade-offs

⚠️ **URL Changes**: URLs change between versions (e.g., `/api/v1/deals` → `/api/v2/deals`)

⚠️ **Code Duplication**: Some logic may be duplicated across versions

⚠️ **Maintenance Overhead**: Multiple versions require separate maintenance

⚠️ **Routing Complexity**: Additional routing logic adds some overhead

## Alternative Versioning Strategies

### Header-Based Versioning
```
GET /api/deals
Accept: application/vnd.company.v2+json
```

**Pros**: URLs don't change
**Cons**: Less visible, harder to test in browser

### Query Parameter Versioning
```
GET /api/deals?version=2
```

**Pros**: Flexible, easy to add
**Cons**: Can be overlooked, not RESTful

### Content Negotiation
```
GET /api/deals
Accept: application/json;version=2
```

**Pros**: Standards-based (HTTP)
**Cons**: Complex, less intuitive

**Why Path-Based Was Chosen**:
- Most visible and explicit
- Easy to understand and document
- Simple to implement and test
- Widely used in industry (Stripe, Twitter, GitHub)

## Testing

### Testing V1 Endpoint

```bash
# Create deal (V1)
curl -X POST http://localhost:8080/api/v1/deals \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Deal",
    "value": 10000.00,
    "salesRepId": "USER-001",
    "status": "OPEN"
  }'

# Get all deals (V1 - no pagination)
curl http://localhost:8080/api/v1/deals

# Filter by status (V1)
curl "http://localhost:8080/api/v1/deals?status=WON"
```

### Testing V2 Endpoint

```bash
# Create deal (V2 - includes Location header)
curl -i -X POST http://localhost:8080/api/v2/deals \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Deal",
    "value": 10000.00,
    "salesRepId": "USER-001",
    "status": "OPEN"
  }'

# Get all deals with pagination (V2)
curl "http://localhost:8080/api/v2/deals?page=1&limit=5"

# Advanced filtering (V2)
curl "http://localhost:8080/api/v2/deals?status=WON&salesRepId=USER-001&minValue=5000"
```

## Best Practices

### Version Management

1. **Start with V1**: Don't start at V0 - begin with a stable V1
2. **Deprecation Period**: Give clients adequate time to migrate (6-12 months)
3. **Clear Communication**: Document version changes and migration guides
4. **Version Headers**: Always include `API-Version` header in responses
5. **Default Version**: Have a sensible default (usually latest stable)

### Breaking Changes

Introduce new versions for:
- Removing fields from responses
- Changing field types
- Changing endpoint URLs
- Modifying request/response formats
- Changing authentication requirements

Don't version for:
- Adding optional fields
- Adding new endpoints
- Bug fixes
- Performance improvements

### Sunset Policy

1. **Announce**: Communicate deprecation well in advance
2. **Warn**: Add `Warning` headers to deprecated versions
3. **Monitor**: Track usage of deprecated versions
4. **Support**: Provide migration tools and documentation
5. **Sunset**: Remove version after deprecation period

## References

- `ApiVersion.java` - Version enumeration and metadata
- `VersionedEndpoint.java` - Strategy interface for version implementations
- `VersionRouter.java` - Request routing based on version
- `DealEndpointV1.java` - Version 1 implementation
- `DealEndpointV2.java` - Version 2 implementation
- `VersionedDealServlet.java` - Front controller servlet
- `version-architecture.puml` - Architecture diagram

## Learning Objectives

This implementation demonstrates:

1. **API Versioning Strategies**: Path-based versioning approach
2. **Strategy Pattern**: Version-specific algorithm implementations
3. **Router Pattern**: Request routing and delegation
4. **Backward Compatibility**: Maintaining old versions while evolving
5. **API Evolution**: Managing breaking changes over time
6. **Clean Architecture**: Separation of concerns across versions
7. **Design Principles**: SRP, OCP, DIP applied to versioning
8. **Real-World Patterns**: Industry-standard API versioning

## Future Enhancements

Potential improvements:

1. **Automated Testing**: Integration tests for each version
2. **Version Analytics**: Track usage metrics per version
3. **Auto-migration**: Tools to help clients migrate
4. **API Gateway**: Centralized version routing
5. **Content Negotiation**: Support header-based versioning as alternative
6. **Version Documentation**: Auto-generated API docs per version
7. **Deprecation Schedule**: Automated sunset notifications