# gRPC API Implementation for Commission Calculator

## Overview

This directory contains a complete gRPC API implementation for the Commission Calculator system. gRPC is a modern, high-performance RPC (Remote Procedure Call) framework that uses Protocol Buffers for serialization and HTTP/2 for transport.

## What is gRPC?

**gRPC** is an open-source remote procedure call framework developed by Google. Key characteristics:

- **Protocol Buffers**: Uses protobuf for efficient binary serialization (smaller payload than JSON)
- **HTTP/2**: Leverages HTTP/2 features like multiplexing, header compression, and bidirectional streaming
- **Code Generation**: Auto-generates client and server code from .proto definitions
- **Type-Safe**: Strongly typed contracts ensure correctness at compile time
- **Cross-Platform**: Works across multiple languages (Java, Python, Go, C#, etc.)

## Architecture

### Directory Structure

```
src/main/
├── resources/proto/                    # Protocol Buffer definitions
│   ├── commission_common.proto         # Common types (Decimal, Date, enums)
│   ├── deal_service.proto              # Deal service definition
│   ├── user_service.proto              # User service definition
│   ├── commission_plan_service.proto   # Commission plan service definition
│   └── dispute_service.proto           # Dispute service definition
│
└── java/com/chapman/edu/commissions/api/grpc/
    ├── ModelConverter.java             # Converts domain ↔ proto messages
    ├── DealServiceImpl.java            # Deal service implementation
    ├── GrpcServer.java                 # Embedded gRPC server
    ├── GrpcClient.java                 # Java client for testing
    ├── grpc-architecture.puml          # Architecture diagram
    ├── CommissionCalculator-gRPC.postman_collection.json  # Postman collection
    └── README.md                       # This file

src/test/java/com/chapman/edu/commissions/api/grpc/
    ├── GrpcServerTestBase.java         # Base class for integration tests
    └── DealServiceIntegrationTest.java # Integration tests

target/generated-sources/protobuf/java/  # Auto-generated proto classes
```

### Components

#### 1. Protocol Buffer Definitions (.proto files)

Protocol Buffer files define the service contract (API interface):

- **Messages**: Data structures (like Java classes)
- **Services**: RPC methods (like REST endpoints)
- **Enums**: Enumeration types
- **Field Numbers**: Unique identifiers for binary encoding

Example from `deal_service.proto`:
```protobuf
message Deal {
    string id = 1;
    string title = 2;
    Decimal value = 3;
    // ... more fields
}

service DealService {
    rpc CreateDeal(CreateDealRequest) returns (CreateDealResponse);
    rpc GetDeal(GetDealRequest) returns (GetDealResponse);
    // ... more methods
}
```

#### 2. Generated Code

The protobuf compiler (`protoc`) generates Java classes from .proto files:

- **Message classes**: `Deal`, `DealProduct`, `CreateDealRequest`, etc.
- **Service stubs**: `DealServiceGrpc` with client and server base classes
- **Builder pattern**: Immutable messages with fluent builders

#### 3. Service Implementations

Service implementation classes extend generated base classes and provide business logic:

```java
public class DealServiceImpl extends DealServiceGrpc.DealServiceImplBase {
    @Override
    public void createDeal(CreateDealRequest request,
                          StreamObserver<CreateDealResponse> responseObserver) {
        // 1. Convert proto to domain model
        // 2. Execute business logic
        // 3. Convert domain model to proto
        // 4. Send response
    }
}
```

#### 4. Model Converter

`ModelConverter` handles bidirectional conversion between:
- Domain models (`com.chapman.edu.commissions.model.*`)
- Protocol Buffer messages (`com.chapman.edu.commissions.api.grpc.proto.*`)

This separation allows independent evolution of domain and API layers.

#### 5. gRPC Server

`GrpcServer` is an embedded server (similar to embedded Tomcat for REST):

- Binds to port 50051 (gRPC convention)
- Registers service implementations
- Manages server lifecycle
- Handles graceful shutdown

## Key Concepts

### 1. Unary RPC

The most common RPC pattern: client sends one request, server sends one response.

```java
// Server side
public void getDeal(GetDealRequest request,
                   StreamObserver<GetDealResponse> responseObserver) {
    // Process request
    Deal deal = repository.findById(request.getId());

    // Send response
    responseObserver.onNext(GetDealResponse.newBuilder()
        .setDeal(ModelConverter.toProtoDeal(deal))
        .build());

    // Complete the RPC
    responseObserver.onCompleted();
}
```

### 2. StreamObserver Pattern

`StreamObserver` is a callback interface for handling RPC results:

- `onNext(T value)`: Send a response value
- `onCompleted()`: Signal successful completion
- `onError(Throwable t)`: Signal an error

This enables asynchronous, non-blocking RPC handling.

### 3. Error Handling

gRPC uses `Status` codes (similar to HTTP status codes):

```java
// Not found error
responseObserver.onError(
    Status.NOT_FOUND
        .withDescription("Deal not found: " + dealId)
        .asRuntimeException()
);

// Invalid argument error
responseObserver.onError(
    Status.INVALID_ARGUMENT
        .withDescription("Deal ID is required")
        .asRuntimeException()
);
```

Common status codes:
- `OK`: Success
- `NOT_FOUND`: Resource not found (like HTTP 404)
- `INVALID_ARGUMENT`: Invalid request parameters (like HTTP 400)
- `INTERNAL`: Internal server error (like HTTP 500)
- `UNAUTHENTICATED`: Authentication required (like HTTP 401)
- `PERMISSION_DENIED`: Authorization failed (like HTTP 403)

### 4. Protocol Buffer Type Mapping

| Java Type | Proto Type | Notes |
|-----------|------------|-------|
| `BigDecimal` | `Decimal` (custom message) | String representation for precision |
| `LocalDate` | `Date` (custom message) | year/month/day components |
| `LocalDateTime` | `Timestamp` (custom message) | Milliseconds since epoch |
| `String` | `string` | UTF-8 encoded |
| `int` | `int32` | 32-bit signed integer |
| `long` | `int64` | 64-bit signed integer |
| `boolean` | `bool` | Boolean value |
| `List<T>` | `repeated T` | Ordered collection |
| `Set<T>` | `repeated T` | Converted to/from Set in Java |
| `enum` | `enum` | Enumeration type |

### 5. Repository Pattern

Service implementations delegate data storage to `Repository`:

```java
public class DealServiceImpl extends DealServiceGrpc.DealServiceImplBase {
    private final Repository<Deal> dealRepository;

    public DealServiceImpl(Repository<Deal> dealRepository) {
        this.dealRepository = dealRepository;  // Dependency injection
    }
}
```

This provides:
- **Separation of Concerns**: Service handles RPC, repository handles data
- **Testability**: Easy to mock repository for unit tests
- **Flexibility**: Can swap implementations (in-memory, database, etc.)

## Building and Running

### Prerequisites

- Java 21
- Maven 3.6+

### Build

The project uses the `protobuf-maven-plugin` to generate Java code from .proto files:

```bash
# Generate protobuf classes and compile
mvn clean compile

# Generated files will be in:
# target/generated-sources/protobuf/java/
```

### Run the Server

```bash
# Using Maven
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.api.grpc.GrpcServer"

# Using compiled JAR
java -cp target/commission-calculator.jar \
    com.chapman.edu.commissions.api.grpc.GrpcServer

# With custom port
java -cp target/commission-calculator.jar \
    com.chapman.edu.commissions.api.grpc.GrpcServer 50052
```

The server will start on port 50051 (default) and display:

```
Starting Commission Calculator gRPC Server...
Port: 50051
✓ gRPC Server started successfully!

gRPC Services:
  - DealService (port 50051)
  - UserService (port 50051)
  - CommissionPlanService (port 50051)
  - DisputeService (port 50051)

Note: gRPC uses Protocol Buffers over HTTP/2
Use a gRPC client to connect

Press Ctrl+C to stop the server.
```

## Testing the API

### Using grpcurl (Command Line)

Install grpcurl: https://github.com/fullstorydev/grpcurl

```bash
# List all services
grpcurl -plaintext localhost:50051 list

# List methods for a service
grpcurl -plaintext localhost:50051 list commission.DealService

# Call a method
grpcurl -plaintext -d '{
  "title": "Enterprise Software Deal",
  "value": {"value": "50000.00"},
  "sales_rep_id": "USER-1"
}' localhost:50051 commission.DealService/CreateDeal

# Get a deal by ID
grpcurl -plaintext -d '{"id": "DEAL-1"}' \
    localhost:50051 commission.DealService/GetDeal

# List all deals
grpcurl -plaintext -d '{}' \
    localhost:50051 commission.DealService/ListDeals
```

### Using BloomRPC (GUI Client)

1. Download BloomRPC: https://github.com/bloomrpc/bloomrpc
2. Import .proto files from `src/main/resources/proto/`
3. Connect to `localhost:50051`
4. Select service and method
5. Edit request JSON and click "Play"

### Using Postman (GUI Client)

Postman has native gRPC support starting from version 9.7! This provides a user-friendly GUI for testing gRPC APIs.

#### Initial Setup

**Step 1: Import Proto Files**
1. Open Postman
2. Click "New" → "API" → "Import"
3. Select "Protocol Buffers"
4. Navigate to `src/main/resources/proto/`
5. Select ALL .proto files:
   - `commission_common.proto`
   - `deal_service.proto`
   - `user_service.proto`
   - `commission_plan_service.proto`
   - `dispute_service.proto`
6. Click "Import"

Postman will automatically:
- Parse the proto files
- Discover all services and methods
- Generate the API structure
- Create method documentation

**Step 2: Import the Pre-configured Collection (Optional)**
1. Click "Import" → "Upload Files"
2. Select `CommissionCalculator-gRPC.postman_collection.json`
3. This provides pre-configured requests with example data

**Step 3: Create a New gRPC Request**
1. Click "New" → "gRPC Request"
2. Name it (e.g., "Create Deal")
3. Enter server URL: `localhost:50051`
4. Select method from dropdown (e.g., `commission.DealService/CreateDeal`)
5. Ensure "Use TLS" is **unchecked** (we're using plaintext for development)

#### Example 1: Create a Deal

**Request Configuration:**
- **Method**: `commission.DealService/CreateDeal`
- **URL**: `localhost:50051`
- **TLS**: Disabled

**Message (JSON format):**
```json
{
  "title": "Enterprise Software License",
  "value": {
    "value": "50000.00"
  },
  "sales_rep_id": "USER-1",
  "products": [
    {
      "product_id": "PROD-1",
      "product_name": "Software License - Enterprise",
      "quantity": 10,
      "price": {
        "value": "5000.00"
      },
      "discount": {
        "value": "0.00"
      }
    }
  ]
}
```

**Expected Response:**
```json
{
  "deal": {
    "id": "DEAL-001",
    "title": "Enterprise Software License",
    "value": {
      "value": "50000.00"
    },
    "status": "OPEN",
    "sales_rep_id": "USER-1",
    "products": [
      {
        "id": "",
        "product_id": "PROD-1",
        "product_name": "Software License - Enterprise",
        "quantity": 10,
        "price": {
          "value": "5000.00"
        },
        "discount": {
          "value": "0.00"
        },
        "deal_id": "DEAL-001"
      }
    ],
    "close_date": {},
    "created_date": {},
    "last_modified_date": {}
  }
}
```

#### Example 2: Get a Deal by ID

**Request Configuration:**
- **Method**: `commission.DealService/GetDeal`
- **URL**: `localhost:50051`

**Message:**
```json
{
  "id": "DEAL-001"
}
```

**Expected Response:**
Returns the complete deal object with all fields populated.

#### Example 3: List All Deals

**Request Configuration:**
- **Method**: `commission.DealService/ListDeals`
- **URL**: `localhost:50051`

**Message (empty for all deals):**
```json
{}
```

**Expected Response:**
```json
{
  "deals": [
    {
      "id": "DEAL-001",
      "title": "Enterprise Software License",
      "value": {
        "value": "50000.00"
      },
      "status": "OPEN",
      "sales_rep_id": "USER-1",
      "products": [],
      "close_date": {},
      "created_date": {},
      "last_modified_date": {}
    }
  ],
  "total_count": 1
}
```

#### Example 4: List Deals with Filters

**Filter by Status:**

**Message:**
```json
{
  "status_filter": "WON"
}
```

**Filter by Sales Rep:**

**Message:**
```json
{
  "sales_rep_id_filter": "USER-1"
}
```

**Multiple Filters:**

**Message:**
```json
{
  "status_filter": "OPEN",
  "sales_rep_id_filter": "USER-1"
}
```

#### Example 5: Update a Deal

**Request Configuration:**
- **Method**: `commission.DealService/UpdateDeal`
- **URL**: `localhost:50051`

**Message:**
```json
{
  "deal": {
    "id": "DEAL-001",
    "title": "Updated: Enterprise Software License",
    "value": {
      "value": "75000.00"
    },
    "status": "WON",
    "sales_rep_id": "USER-1",
    "products": []
  }
}
```

**Note**: You must include the `id` field to identify which deal to update.

#### Example 6: Delete a Deal

**Request Configuration:**
- **Method**: `commission.DealService/DeleteDeal`
- **URL**: `localhost:50051`

**Message:**
```json
{
  "id": "DEAL-001"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Deal deleted successfully"
}
```

#### Example 7: Create a User

**Request Configuration:**
- **Method**: `commission.UserService/CreateUser`
- **URL**: `localhost:50051`

**Message:**
```json
{
  "username": "john.doe",
  "email": "john.doe@example.com",
  "first_name": "John",
  "last_name": "Doe",
  "roles": ["SALES_REP"],
  "department": "Sales",
  "territory": "West Coast"
}
```

#### Example 8: Create and Manage a Dispute

**Create Dispute:**

**Method**: `commission.DisputeService/CreateDispute`

**Message:**
```json
{
  "calculation_id": "CALC-001",
  "sales_rep_id": "USER-1",
  "title": "Incorrect commission rate applied",
  "description": "The commission was calculated at 10% instead of 15% according to my Q4 plan. Deal DEAL-001 should have earned $7,500 instead of $5,000."
}
```

**Add Comment to Dispute:**

**Method**: `commission.DisputeService/AddComment`

**Message:**
```json
{
  "dispute_id": "DISP-001",
  "user_id": "USER-2",
  "user_name": "Manager Smith",
  "text": "I've reviewed the calculation and confirmed the issue. Escalating to finance for correction."
}
```

**Escalate Dispute:**

**Method**: `commission.DisputeService/EscalateDispute`

**Message:**
```json
{
  "dispute_id": "DISP-001",
  "reason": "Requires senior finance approval for plan interpretation"
}
```

#### Postman Tips & Tricks

**1. View Metadata:**
Click the "Metadata" tab to see gRPC-specific information like:
- Status code
- Status message
- Response time
- Headers

**2. Save Requests:**
After configuring a request:
- Click "Save" to add it to a collection
- Name it descriptively (e.g., "Create Deal - With Products")
- Organize in folders by service

**3. Use Variables:**
Create environment variables for:
- `{{baseUrl}}`: `localhost:50051`
- `{{dealId}}`: Store created deal IDs
- `{{userId}}`: Store user IDs

**4. Server Reflection:**
If server reflection is enabled, Postman can auto-discover services without proto files:
1. Enter server URL
2. Check "Use server reflection"
3. Click "Load services"

**5. Test Scripts:**
Add test scripts to validate responses:
```javascript
pm.test("Status is OK", function() {
    pm.expect(pm.response.status).to.equal("OK");
});

pm.test("Deal created with ID", function() {
    pm.expect(pm.response.json().deal.id).to.exist;
});
```

#### Troubleshooting

**Error: "Failed to connect"**
- Ensure the gRPC server is running (`mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.api.grpc.GrpcServer"`)
- Check the port is correct (50051 by default)
- Verify "Use TLS" is unchecked

**Error: "Method not found"**
- Verify proto files are imported correctly
- Check the method name spelling (case-sensitive)
- Ensure the package name is correct (`commission.DealService/CreateDeal`)

**Error: "Invalid argument"**
- Check JSON format matches the proto definition
- Ensure required fields are present
- Verify field types (strings need quotes, numbers don't)

**Empty Response:**
- Some methods return empty messages
- Check the "Metadata" tab for status code
- Status "OK" (code 0) means success

#### Complete Workflow Example

Here's a complete workflow using Postman:

1. **Start the server** (in terminal):
   ```bash
   mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.api.grpc.GrpcServer"
   ```

2. **Create a user**:
   ```json
   // Method: commission.UserService/CreateUser
   {
     "username": "sales.rep1",
     "email": "rep1@company.com",
     "first_name": "Jane",
     "last_name": "Smith",
     "roles": ["SALES_REP"],
     "department": "Sales",
     "territory": "East"
   }
   ```
   → Save the returned user ID (e.g., "USER-001")

3. **Create a deal**:
   ```json
   // Method: commission.DealService/CreateDeal
   {
     "title": "Acme Corp - Software Suite",
     "value": {"value": "100000.00"},
     "sales_rep_id": "USER-001"
   }
   ```
   → Save the returned deal ID (e.g., "DEAL-001")

4. **List deals for verification**:
   ```json
   // Method: commission.DealService/ListDeals
   {
     "sales_rep_id_filter": "USER-001"
   }
   ```

5. **Update deal status to WON**:
   ```json
   // Method: commission.DealService/UpdateDeal
   {
     "deal": {
       "id": "DEAL-001",
       "title": "Acme Corp - Software Suite",
       "value": {"value": "100000.00"},
       "status": "WON",
       "sales_rep_id": "USER-001"
     }
   }
   ```

6. **Verify the update**:
   ```json
   // Method: commission.DealService/GetDeal
   {
     "id": "DEAL-001"
   }
   ```

The collection includes pre-configured requests for:
- ✅ Creating deals with products
- ✅ Retrieving and listing deals
- ✅ Filtering by status and sales rep
- ✅ Updating and deleting deals
- ✅ User management operations
- ✅ Dispute creation and workflow
- ✅ Adding comments to disputes
- ✅ Escalating disputes

### Using the Java Client (GrpcClient)

The included `GrpcClient` class provides a programmatic way to interact with the API:

```bash
# Run the demo client
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.api.grpc.GrpcClient"

# With custom server address
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.api.grpc.GrpcClient" \
    -Dexec.args="localhost 50051"
```

The client demonstrates:
- Creating a deal
- Retrieving the deal
- Listing all deals
- Updating the deal
- Filtering by status
- Deleting the deal
- Verifying deletion

**Example Output:**
```
Commission Calculator gRPC Client
==================================
Connecting to: localhost:50051

✓ Connected to gRPC server at localhost:50051
1. Creating a new deal...
✓ Deal created: DEAL-1
   Created deal ID: DEAL-1
   Title: Enterprise Software License
   Value: $75000.00

2. Retrieving the deal...
✓ Deal retrieved: DEAL-1
   Retrieved deal: Enterprise Software License

... (continues with all operations)
```

**Using GrpcClient in Your Code:**

```java
// Create client (auto-closeable)
try (GrpcClient client = new GrpcClient()) {
    // Create a deal
    Deal deal = client.createDeal(
        "My Deal",
        new BigDecimal("10000.00"),
        "USER-1"
    );

    // List deals
    ListDealsResponse response = client.listDeals();

    // Filter by status
    ListDealsResponse wonDeals = client.listDeals("WON", null);

    // Update deal
    Deal updated = client.updateDeal(
        deal.toBuilder().setStatus(DealStatus.WON).build()
    );

    // Delete deal
    boolean deleted = client.deleteDeal(deal.getId());
}
```

### Writing a Java Client

```java
import com.chapman.edu.commissions.api.grpc.proto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClient {
    public static void main(String[] args) {
        // Create channel to server
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()  // No TLS for development
                .build();

        // Create stub (client)
        DealServiceGrpc.DealServiceBlockingStub stub =
                DealServiceGrpc.newBlockingStub(channel);

        // Create request
        CreateDealRequest request = CreateDealRequest.newBuilder()
                .setTitle("Test Deal")
                .setValue(Decimal.newBuilder().setValue("10000.00").build())
                .setSalesRepId("USER-1")
                .build();

        // Make RPC call
        CreateDealResponse response = stub.createDeal(request);

        System.out.println("Created deal: " + response.getDeal().getId());

        // Shutdown channel
        channel.shutdown();
    }
}
```

## Integration Tests

The project includes comprehensive integration tests for the gRPC API.

### Running Integration Tests

```bash
# Run all tests
mvn test

# Run only gRPC integration tests
mvn test -Dtest="*grpc*"

# Run with verbose output
mvn test -Dtest="DealServiceIntegrationTest"
```

### Test Structure

The integration tests follow JUnit 5 best practices:

**GrpcServerTestBase** - Base class that:
- Starts the gRPC server once before all tests (`@BeforeAll`)
- Stops the server after all tests complete (`@AfterAll`)
- Provides `server` and `client` instances to test classes
- Uses a different port (50052) to avoid conflicts

**DealServiceIntegrationTest** - Comprehensive tests including:
- ✅ Creating deals with validation
- ✅ Retrieving deals by ID
- ✅ Listing deals with filters (status, sales rep)
- ✅ Updating deals
- ✅ Deleting deals
- ✅ Error handling (NOT_FOUND, INVALID_ARGUMENT)
- ✅ Complex objects (deals with products)
- ✅ AAA pattern (Arrange, Act, Assert)

### Example Test

```java
@Test
@DisplayName("Should create a new deal successfully")
void testCreateDeal() {
    // Arrange: Prepare test data
    String title = "Test Enterprise Deal";
    BigDecimal value = new BigDecimal("50000.00");
    String salesRepId = "USER-TEST-1";

    // Act: Call the API
    Deal createdDeal = client.createDeal(title, value, salesRepId);

    // Assert: Verify the results
    assertNotNull(createdDeal);
    assertTrue(createdDeal.getId().startsWith("DEAL-"));
    assertEquals(title, createdDeal.getTitle());
    assertEquals(value.toPlainString(), createdDeal.getValue().getValue());
}
```

### Test Coverage

The integration tests verify:
- **Functional correctness**: All operations work as expected
- **Error handling**: Proper status codes and error messages
- **Data persistence**: Data is correctly saved and retrieved
- **Filtering**: Server-side filtering logic is correct
- **Type conversion**: Proto ↔ domain model conversion works
- **Edge cases**: Empty lists, non-existent resources, etc.

### Educational Value

The tests demonstrate:
- JUnit 5 features (@DisplayName, @Order, @Nested)
- Integration test patterns (server lifecycle management)
- gRPC client usage
- Exception testing with StatusRuntimeException
- Test organization and readability

## Service API Reference

### DealService

CRUD operations for managing sales deals.

- **CreateDeal**: Create a new deal
- **GetDeal**: Retrieve a deal by ID
- **ListDeals**: List all deals (with optional filters)
- **UpdateDeal**: Update an existing deal
- **DeleteDeal**: Delete a deal by ID

### UserService

CRUD operations for managing users.

- **CreateUser**: Create a new user
- **GetUser**: Retrieve a user by ID
- **ListUsers**: List all users (with optional filters)
- **UpdateUser**: Update an existing user
- **DeleteUser**: Delete a user by ID

### CommissionPlanService

CRUD operations for managing commission plans.

- **CreateCommissionPlan**: Create a new plan
- **GetCommissionPlan**: Retrieve a plan by ID
- **ListCommissionPlans**: List all plans (with optional filters)
- **UpdateCommissionPlan**: Update an existing plan
- **DeleteCommissionPlan**: Delete a plan by ID

### DisputeService

Manage commission disputes with additional workflow operations.

- **CreateDispute**: Create a new dispute
- **GetDispute**: Retrieve a dispute by ID
- **ListDisputes**: List all disputes (with optional filters)
- **UpdateDispute**: Update an existing dispute
- **DeleteDispute**: Delete a dispute by ID
- **AddComment**: Add a comment to a dispute (domain-specific operation)
- **EscalateDispute**: Escalate a dispute to management (workflow operation)

## gRPC vs REST Comparison

| Aspect | gRPC | REST (JSON over HTTP) |
|--------|------|---------------------|
| **Protocol** | HTTP/2 | HTTP/1.1 |
| **Serialization** | Protocol Buffers (binary) | JSON (text) |
| **Performance** | Faster (smaller payload, binary) | Slower (larger payload, text parsing) |
| **Contract** | .proto files (strongly typed) | OpenAPI/Swagger (optional) |
| **Streaming** | Built-in bidirectional streaming | Limited (SSE, WebSocket) |
| **Browser Support** | Requires gRPC-Web proxy | Native |
| **Tooling** | Specialized tools (grpcurl, BloomRPC) | Universal (curl, Postman) |
| **Human Readable** | No (binary format) | Yes (JSON is human-readable) |
| **Code Generation** | Automatic from .proto | Optional (OpenAPI generators) |
| **Error Handling** | Status codes + metadata | HTTP status codes |
| **Use Cases** | Microservices, internal APIs | Public APIs, web applications |

## Educational Concepts Demonstrated

### 1. Interface Definition Language (IDL)

Protocol Buffers serve as an IDL:
- Language-neutral API definition
- Contract-first development
- Backward/forward compatibility
- Multi-language support

### 2. Code Generation

Generated code from .proto files demonstrates:
- Automated client/server scaffolding
- Type safety across languages
- Reduced boilerplate code
- Consistency across implementations

### 3. Adapter Pattern

`ModelConverter` demonstrates the Adapter pattern:
- Bridges incompatible interfaces
- Separates domain and transport layers
- Enables independent evolution
- Provides a clean translation layer

### 4. Dependency Injection

Service implementations receive dependencies via constructor:
- Promotes loose coupling
- Enhances testability
- Enables configuration flexibility
- Follows SOLID principles

### 5. Asynchronous Communication

`StreamObserver` enables non-blocking I/O:
- Callbacks for response handling
- Supports high concurrency
- Efficient resource utilization
- Scalable architecture

## Best Practices

### 1. API Design

- Use descriptive message and field names
- Include comprehensive comments in .proto files
- Design for backward compatibility
- Group related RPCs into services

### 2. Error Handling

- Use appropriate gRPC status codes
- Include descriptive error messages
- Add structured error details when needed
- Log errors for debugging

### 3. Versioning

- Add version to package name (e.g., `commission.v1`)
- Never remove or change field numbers
- Mark deprecated fields instead of removing
- Use separate .proto files for major versions

### 4. Performance

- Reuse channels (expensive to create)
- Use connection pooling for clients
- Consider deadline/timeout settings
- Monitor RPC latency and error rates

### 5. Security

- Use TLS for production
- Implement authentication (e.g., JWT tokens)
- Add authorization checks in services
- Validate all inputs

## Further Reading

- **gRPC Documentation**: https://grpc.io/docs/
- **Protocol Buffers Guide**: https://protobuf.dev/
- **gRPC Java Tutorial**: https://grpc.io/docs/languages/java/
- **HTTP/2 Specification**: https://http2.github.io/

## Summary

This gRPC API implementation provides:

✅ **Complete Protocol Buffer definitions** for all domain models (5 .proto files)
✅ **Four gRPC services** (Deal, User, CommissionPlan, Dispute) with full CRUD operations
✅ **Embedded gRPC server** (similar to REST ApiServer) with graceful shutdown
✅ **Model conversion layer** (domain ↔ proto) using Adapter pattern
✅ **Comprehensive error handling** with proper gRPC status codes
✅ **Java client** (GrpcClient) for programmatic API access
✅ **Postman collection** with pre-configured requests
✅ **Integration tests** with JUnit 5 and comprehensive coverage
✅ **PlantUML architecture diagram** showing component relationships
✅ **Extensive inline documentation** and educational comments
✅ **Proto files in resources** for easy access and packaging

### Files Created

1. **Protocol Buffers** (`src/main/resources/proto/`):
   - commission_common.proto
   - deal_service.proto
   - user_service.proto
   - commission_plan_service.proto
   - dispute_service.proto

2. **Server Implementation** (`src/main/java/.../api/grpc/`):
   - GrpcServer.java
   - DealServiceImpl.java
   - ModelConverter.java

3. **Client & Testing** (`src/main/java/.../api/grpc/`):
   - GrpcClient.java (runnable client with demo)
   - CommissionCalculator-gRPC.postman_collection.json

4. **Integration Tests** (`src/test/java/.../api/grpc/`):
   - GrpcServerTestBase.java
   - DealServiceIntegrationTest.java

5. **Documentation**:
   - README.md (this file)
   - grpc-architecture.puml

The implementation demonstrates how modern RPC frameworks like gRPC provide an alternative to REST APIs with benefits in performance, type safety, and code generation, while requiring specialized tooling and having trade-offs in browser compatibility and debugging.