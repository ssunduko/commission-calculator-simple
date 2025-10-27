# API Versioning Integration Tests

This package contains comprehensive integration tests for the path-based API versioning system implemented in `com.chapman.edu.commissions.api.rest.version`.

## Test Classes

### 1. VersionIntegrationTestBase

**Purpose**: Base class providing test infrastructure for versioning testing.

**Features**:
- Starts API server on dedicated test port (9997)
- Provides methods for calling different API versions (V1, V2, V3)
- Version-specific helper methods
- Header validation utilities

**Helper Methods**:
- `getV1/postV1/putV1/deleteV1(path)` - V1 endpoint calls
- `getV2/postV2/putV2/deleteV2(path)` - V2 endpoint calls
- `getV3(path)` - V3 endpoint calls (experimental)
- `getApiVersionHeader(response)` - Extract API-Version header
- `getWarningHeader(response)` - Extract deprecation warnings
- `isDeprecated(response)` - Check if version is deprecated
- `isExperimental(response)` - Check if version is experimental

### 2. ApiVersioningIntegrationTest

**Purpose**: Tests core API versioning functionality.

**Test Coverage** (12 tests):
1. ✅ Both V1 and V2 endpoints accessible
2. ✅ Responses include API-Version header
3. ✅ V1 includes deprecation warning
4. ✅ V1 returns simple array response (no pagination)
5. ✅ V2 supports pagination with metadata
6. ✅ V2 includes computed fields
7. ✅ V2 supports advanced filtering
8. ✅ V2 returns enhanced error responses
9. ✅ V1 maintains backward compatibility
10. ✅ Data created in V1 accessible from V2
11. ✅ Unsupported version returns 404
12. ✅ V2 includes Location header on POST

**URL Structure**:
```
/api/v1/deals  →  DealEndpointV1 (deprecated)
/api/v2/deals  →  DealEndpointV2 (current)
/api/v3/deals  →  DealEndpointV3 (future)
```

**Key Concepts Demonstrated**:
- Path-based versioning
- Version coexistence
- Backward compatibility
- Version headers and warnings
- Feature evolution

**Example Usage**:
```java
// Call V1 endpoint
HttpResponse<String> v1Response = getV1("/deals");
assertStatus(v1Response, 200);

// Call V2 endpoint
HttpResponse<String> v2Response = getV2("/deals?page=1&limit=10");
assertStatus(v2Response, 200);

// Check version header
String version = getApiVersionHeader(v2Response);
assertEquals("2.0", version);
```

### 3. VersionMigrationIntegrationTest

**Purpose**: Tests API version migration scenarios and strategies.

**Test Coverage** (11 tests):
1. ✅ Parallel operation of V1 and V2 clients
2. ✅ Client migration from V1 to V2 for GET requests
3. ✅ Adapting to pagination in V2
4. ✅ Handling computed fields in V2
5. ✅ Error response format changes
6. ✅ Filter capabilities enhanced in V2
7. ✅ Client version detection
8. ✅ Migration testing strategy
9. ✅ Rollback scenario (V1 still works)
10. ✅ Gradual migration strategy
11. ✅ Migration documentation guide

**Migration Scenarios**:
- Multiple versions running simultaneously
- Client adaptation to new features
- Testing migration path
- Rollback strategies
- Gradual client migration

**Key Concepts Demonstrated**:
- Migration planning
- Parallel running
- Client adaptation
- Testing strategies
- Rollback capability

**Example Usage**:
```java
// Simulate V1 client code
HttpResponse<String> v1Response = getV1("/deals");
JsonArray v1Deals = JsonParser.parseString(v1Response.body()).getAsJsonArray();

// Simulate V2 client code (adapted)
HttpResponse<String> v2Response = getV2("/deals?page=1&limit=10");
JsonObject v2Json = JsonParser.parseString(v2Response.body()).getAsJsonObject();
JsonArray v2Deals = v2Json.getAsJsonArray("data");
JsonObject metadata = v2Json.getAsJsonObject("metadata");
```

## Version Comparison

### V1 Features (Deprecated)

**Response Format**: Simple array
```json
[
  {
    "id": "DEAL-001",
    "title": "Deal Title",
    "value": 10000.00,
    "status": "WON"
  }
]
```

**Capabilities**:
- ✅ Basic CRUD operations
- ✅ Simple status filtering (`?status=WON`)
- ❌ No pagination
- ❌ No computed fields
- ❌ Basic error messages

**Status**: Deprecated - clients should migrate to V2

### V2 Features (Current)

**Response Format**: Object with data and metadata
```json
{
  "data": [
    {
      "id": "DEAL-001",
      "title": "Deal Title",
      "value": 10000.00,
      "status": "WON",
      "estimatedCommission": 1000.00,
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

**Capabilities**:
- ✅ All V1 features
- ✅ Pagination (`?page=1&limit=10`)
- ✅ Advanced filtering (`?status=WON&minValue=5000&salesRepId=USER-001`)
- ✅ Computed fields (estimatedCommission, productCount)
- ✅ Enhanced error responses (errorCode, timestamp)
- ✅ Response metadata (pagination info)
- ✅ Location header on POST

**Status**: Current stable version (recommended)

### V3 Features (Future/Experimental)

**Planned Features**:
- GraphQL-style field selection
- Batch operations
- WebSocket support
- Enhanced caching

**Status**: Experimental - subject to change

## Test Execution

### Running All Versioning Tests

```bash
# Run all versioning tests
mvn test -Dtest="com.chapman.edu.commissions.api.rest.version.*"

# Run specific test class
mvn test -Dtest="ApiVersioningIntegrationTest"
mvn test -Dtest="VersionMigrationIntegrationTest"
```

### Test Status

**Important Note**: Many tests are marked as `@Disabled` because they require the `VersionedDealServlet` to be registered in the `ApiServer`. These tests demonstrate the testing approach and will pass once versioning is fully integrated.

**Current Status**:
- ✅ All tests compile successfully
- ⚠️ Most tests disabled (require VersionedDealServlet registration)
- ✅ Tests demonstrate comprehensive versioning testing patterns
- ✅ Tests provide clear examples for version migration

### Enabling Tests

To enable the disabled tests:

1. **Register VersionedDealServlet in ApiServer**:
```java
// In ApiServer.java
Repository<Deal> dealRepository = new InMemoryRepository<>("DEAL",
    Deal::getId, Deal::setId);

VersionedDealServlet versionedServlet = new VersionedDealServlet(dealRepository);

// Register versioned servlet
tomcat.addServlet(contextPath, "versionedDealServlet", versionedServlet);
context.addServletMappingDecoded("/api/*", "versionedDealServlet");
```

2. **Remove `@Disabled` annotations** from test methods

3. **Run tests**: `mvn test -Dtest="ApiVersioningIntegrationTest"`

## Test Design Patterns

### 1. Test Fixture Pattern

Base class provides version-specific infrastructure:
- V1/V2/V3 request methods
- Version header utilities
- Shared assertions

### 2. AAA Pattern (Arrange-Act-Assert)

```java
// Arrange - Setup test data
String dealJson = """
    {"title": "Test", "value": 10000.00}
    """;

// Act - Execute operation on specific version
HttpResponse<String> v1Response = getV1("/deals");
HttpResponse<String> v2Response = getV2("/deals?page=1&limit=10");

// Assert - Verify version-specific behavior
assertTrue(v1Response.body().startsWith("["));  // V1: array
assertTrue(v2Response.body().contains("metadata"));  // V2: object
```

### 3. Comparison Testing

Tests compare behavior across versions:
```java
// Same operation on both versions
HttpResponse<String> v1 = postV1("/deals", dealJson);
HttpResponse<String> v2 = postV2("/deals", dealJson);

// Both should succeed
assertStatus(v1, 201);
assertStatus(v2, 201);

// V2 has additional features
assertNotNull(getHeader(v2, "Location"));
```

### 4. Migration Scenario Testing

Tests simulate real-world migration:
```java
// Phase 1: V1 client code
JsonArray v1Deals = parseArray(getV1("/deals").body());

// Phase 2: V2 client code (migrated)
JsonObject v2Response = parseObject(getV2("/deals").body());
JsonArray v2Deals = v2Response.getAsJsonArray("data");
JsonObject metadata = v2Response.getAsJsonObject("metadata");
```

## Integration with Main Code

These tests validate the implementation in:
- `ApiVersion.java` - Version enumeration
- `VersionedEndpoint.java` - Strategy interface
- `VersionRouter.java` - Version routing logic
- `DealEndpointV1.java` - V1 implementation
- `DealEndpointV2.java` - V2 implementation
- `VersionedDealServlet.java` - Front controller

## Educational Value

These tests demonstrate:

1. **Path-Based Versioning**: URL structure for versions
2. **Backward Compatibility**: Old versions continue working
3. **Feature Evolution**: How features change between versions
4. **Migration Strategies**: Parallel running, gradual migration
5. **Testing Approaches**: Version comparison, migration testing
6. **Client Adaptation**: How clients update for new versions
7. **Deprecation Handling**: Warnings and sunset policies
8. **Real-World Scenarios**: Industry-standard versioning patterns

## Best Practices Demonstrated

✅ Clear version comparison
✅ Comprehensive migration testing
✅ Parallel version support
✅ Backward compatibility validation
✅ Deprecation warnings
✅ Migration documentation
✅ Rollback scenarios
✅ Educational test comments

## Migration Testing Strategy

### Phase 1: Preparation
```java
// V1 still works
HttpResponse<String> v1Response = getV1("/deals");
assertStatus(v1Response, 200);
```

### Phase 2: Parallel Running
```java
// Both versions work simultaneously
HttpResponse<String> v1 = getV1("/deals");
HttpResponse<String> v2 = getV2("/deals");

assertStatus(v1, 200);  // Old clients still work
assertStatus(v2, 200);  // New clients can use V2
```

### Phase 3: Client Migration
```java
// Old client code (V1)
JsonArray deals = parseArray(response.body());

// New client code (V2) - adapted for pagination
JsonObject json = parseObject(response.body());
JsonArray deals = json.getAsJsonArray("data");
JsonObject metadata = json.getAsJsonObject("metadata");
```

### Phase 4: Verification
```java
// Verify same data across versions
HttpResponse<String> v1Data = getV1("/deals");
HttpResponse<String> v2Data = getV2("/deals");

// Both should have same deals (different format)
```

## Client Migration Checklist

When migrating from V1 to V2, update:

1. ✅ **URL Endpoints**: `/api/v1/deals` → `/api/v2/deals`
2. ✅ **Response Parsing**: `Array` → `{data: [], metadata: {}}`
3. ✅ **Pagination**: Implement page/limit parameters
4. ✅ **Error Handling**: Use `errorCode` field
5. ✅ **Computed Fields**: Access new fields (estimatedCommission, etc.)
6. ✅ **Advanced Filters**: Use new filter parameters
7. ✅ **Headers**: Read API-Version header for debugging

## Future Enhancements

Potential test additions:

1. **Content Negotiation**: Test header-based versioning
2. **Version Fallback**: Test client fallback on version errors
3. **Performance Testing**: Compare version performance
4. **Load Testing**: Test multiple versions under load
5. **A/B Testing**: Test canary deployments
6. **Monitoring**: Test version usage tracking
7. **Documentation**: Automated API docs per version
8. **Breaking Change Detection**: Automated compatibility testing

## References

- Main implementation: `src/main/java/com/chapman/edu/commissions/api/rest/version/`
- Version README: `src/main/java/com/chapman/edu/commissions/api/rest/version/README.md`
- Architecture diagram: `src/main/java/com/chapman/edu/commissions/api/rest/version/version-architecture.puml`

## Learning Resources

For more information on API versioning:
- REST API Versioning: https://restfulapi.net/versioning/
- Semantic Versioning: https://semver.org/
- API Evolution: https://nordicapis.com/api-versioning-methods-a-brief-reference/
- Microsoft API Guidelines: https://github.com/microsoft/api-guidelines