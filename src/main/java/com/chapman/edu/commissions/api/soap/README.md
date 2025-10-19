# Commission Calculator SOAP API

A SOAP (Simple Object Access Protocol) web services implementation for the Commission Calculator system using embedded Tomcat and JAX-WS. This implementation demonstrates traditional SOAP concepts, design patterns, and enterprise web service best practices.

## Table of Contents

1. [Overview](#overview)
2. [What is SOAP?](#what-is-soap)
3. [SOAP vs REST vs GraphQL](#soap-vs-rest-vs-graphql)
4. [Architecture](#architecture)
5. [Running the Server](#running-the-server)
6. [WSDL and Service Discovery](#wsdl-and-service-discovery)
7. [Example SOAP Requests](#example-soap-requests)
8. [Testing](#testing)
9. [Key Concepts](#key-concepts)

## Overview

This SOAP API provides enterprise-grade web services for:

- **Deals**: Sales deals and their associated products
- **Users**: System users with roles and permissions
- **Commission Plans**: Rules and tiers for commission calculations
- **Disputes**: Commission-related disputes and their resolution workflow

### Technology Stack

- **Java 21**: Modern Java features
- **Embedded Tomcat 10.1.15**: Self-contained web server
- **Jakarta XML WS (JAX-WS) 4.0**: SOAP web services framework
- **JAXB 4.0**: XML binding for Java objects
- **JUnit 5**: Integration testing

## What is SOAP?

SOAP (Simple Object Access Protocol) is a protocol specification for exchanging structured information in web services. Key characteristics:

1. **XML-Based**: All messages use XML format
2. **Protocol**: It's a protocol, not an architectural style (unlike REST)
3. **Standards-Based**: W3C standard with strict specifications
4. **WSDL**: Web Services Description Language defines the contract
5. **Platform-Independent**: Works across different platforms and languages
6. **Transport-Agnostic**: Usually HTTP, but can use SMTP, JMS, etc.

### SOAP Message Structure

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
  <soapenv:Header>
    <!-- Optional: Authentication, routing, etc. -->
  </soapenv:Header>
  <soapenv:Body>
    <!-- Required: The actual request/response data -->
    <soap:getAllDeals xmlns:soap="http://soap.api.commissions.edu.chapman.com/"/>
  </soapenv:Body>
</soapenv:Envelope>
```

## SOAP vs REST vs GraphQL

| Aspect | SOAP | REST | GraphQL |
|--------|------|------|---------|
| **Type** | Protocol | Architectural Style | Query Language |
| **Data Format** | XML only | JSON, XML, etc. | JSON |
| **Endpoints** | Multiple (one per service) | Multiple (resource-based) | Single endpoint |
| **Contract** | WSDL (machine-readable) | OpenAPI (optional) | SDL Schema |
| **State** | Stateless | Stateless | Stateless |
| **Standards** | Extensive (WS-*) | Minimal | Minimal |
| **Type Safety** | Strong (WSDL + JAXB) | Weak | Strong (SDL) |
| **Caching** | Difficult | Easy (HTTP caching) | Complex |
| **Error Handling** | SOAP Faults | HTTP Status Codes | GraphQL errors |
| **Versioning** | Namespace-based | URL/Header-based | Schema evolution |
| **Tooling** | Excellent (WSDL tools) | Good | Excellent |
| **Use Case** | Enterprise, Banking, Legacy | Web APIs, Mobile | Modern web, Flexible clients |

### When to Use SOAP?

**Best for:**
- Enterprise applications requiring strict contracts
- Financial services and banking
- Systems requiring WS-Security, WS-AtomicTransaction
- Legacy system integration
- Environments requiring ACID transactions

**NOT ideal for:**
- Public APIs for web/mobile apps (use REST)
- High-performance scenarios (XML overhead)
- Simple CRUD operations (use REST)
- Flexible client requirements (use GraphQL)

## Architecture

```
Client → SOAP Request (XML) → JAX-WS Endpoint → Service Implementation → Repository → Domain Models
                                      ↓
                                    DTOs (via Mapper)
                                      ↓
                              SOAP Response (XML)
```

### Components

| Component | Responsibility | Location |
|-----------|---------------|----------|
| **SoapServer** | Embedded Tomcat server, publishes services | `SoapServer.java` |
| **Service Interfaces** | SOAP service contracts (@WebService) | `service/*Service.java` |
| **Service Implementations** | Business logic, repository access | `service/*ServiceImpl.java` |
| **DTOs** | Data transfer objects with JAXB annotations | `dto/*.java` |
| **DomainMapper** | Converts between domain models and DTOs | `mapper/DomainMapper.java` |

### Services Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                         SoapServer                          │
│  (Port 8082, Embedded Tomcat, Endpoint Publishing)         │
└─────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
   ┌────▼─────┐      ┌─────▼──────┐     ┌─────▼──────┐
   │  Deal    │      │   User     │     │  Dispute   │
   │ Service  │      │  Service   │     │  Service   │
   └────┬─────┘      └─────┬──────┘     └─────┬──────┘
        │                  │                   │
        └──────────────────┼───────────────────┘
                           │
                    ┌──────▼──────┐
                    │ Repositories │
                    └─────────────┘
```

## Running the Server

### From Command Line

```bash
# Run with default settings (port 8082, with sample data)
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.api.soap.SoapServer"

# Custom port
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.api.soap.SoapServer" -Dexec.args="9000"

# Without sample data
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.api.soap.SoapServer" -Dexec.args="--no-sample-data"
```

### Command Line Arguments

| Argument | Description |
|----------|-------------|
| `[port]` | Port number (default: 8082) |
| `--no-sample-data` | Start with empty repositories |

### Expected Output

```
Starting Commission Calculator SOAP Server...
Port: 8082

=== Loading Sample Data ===
Sample data loaded successfully!
  Users: 6
  Deals: 8
  Commission Plans: 4
  Disputes: 4
===========================

SOAP Server started successfully!

Publishing SOAP Web Services...
  - DealService: http://localhost:8082/soap/DealService
    WSDL: http://localhost:8082/soap/DealService?wsdl
  - UserService: http://localhost:8082/soap/UserService
    WSDL: http://localhost:8082/soap/UserService?wsdl
  - CommissionPlanService: http://localhost:8082/soap/CommissionPlanService
    WSDL: http://localhost:8082/soap/CommissionPlanService?wsdl
  - DisputeService: http://localhost:8082/soap/DisputeService
    WSDL: http://localhost:8082/soap/DisputeService?wsdl

All services published successfully!
```

## WSDL and Service Discovery

### What is WSDL?

WSDL (Web Services Description Language) is an XML document that describes:
- Available operations
- Input/output message formats
- Data types
- Service endpoints
- Binding protocols

### Accessing WSDL

Each service publishes its WSDL at `{serviceUrl}?wsdl`:

```
http://localhost:8082/soap/DealService?wsdl
http://localhost:8082/soap/UserService?wsdl
http://localhost:8082/soap/CommissionPlanService?wsdl
http://localhost:8082/soap/DisputeService?wsdl
```

### Using WSDL

**Generate Client Code:**
```bash
# Using wsimport (Java)
wsimport -keep -p com.example.client http://localhost:8082/soap/DealService?wsdl

# Using wsdl2java (Apache CXF)
wsdl2java -p com.example.client http://localhost:8082/soap/DealService?wsdl
```

**Import into SOAP UI:**
1. Open SOAP UI
2. File → New SOAP Project
3. Enter WSDL URL
4. Explore operations and generate test requests

## Example SOAP Requests

### Get All Deals

**Request:**
```xml
POST http://localhost:8082/soap/DealService
Content-Type: text/xml

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:getAllDeals/>
  </soapenv:Body>
</soapenv:Envelope>
```

**Response:**
```xml
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
  <S:Body>
    <ns2:getAllDealsResponse xmlns:ns2="http://soap.api.commissions.edu.chapman.com/">
      <return>
        <id>DEAL-001</id>
        <title>Enterprise Software License</title>
        <value>500000.00</value>
        <status>WON</status>
        <salesRepId>USER-001</salesRepId>
        <calculatedTotalValue>500000.00</calculatedTotalValue>
      </return>
      <!-- More deals... -->
    </ns2:getAllDealsResponse>
  </S:Body>
</S:Envelope>
```

### Get Deal by ID

**Request:**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:getDealById>
      <id>DEAL-001</id>
    </soap:getDealById>
  </soapenv:Body>
</soapenv:Envelope>
```

### Create Deal

**Request:**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:createDeal>
      <deal>
        <title>New SOAP Deal</title>
        <value>250000.00</value>
        <status>OPEN</status>
        <salesRepId>USER-001</salesRepId>
      </deal>
    </soap:createDeal>
  </soapenv:Body>
</soapenv:Envelope>
```

### Update Deal

**Request:**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:updateDeal>
      <id>DEAL-001</id>
      <deal>
        <title>Updated Title</title>
        <status>WON</status>
      </deal>
    </soap:updateDeal>
  </soapenv:Body>
</soapenv:Envelope>
```

### Get Deals by Status

**Request:**
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:soap="http://soap.api.commissions.edu.chapman.com/">
  <soapenv:Header/>
  <soapenv:Body>
    <soap:getDealsByStatus>
      <status>WON</status>
    </soap:getDealsByStatus>
  </soapenv:Body>
</soapenv:Envelope>
```

## Testing

### Running Tests

```bash
# Run all SOAP tests
mvn test -Dtest="*Soap*"

# Run specific test class
mvn test -Dtest=DealSoapIntegrationTest

# Run with verbose output
mvn test -Dtest=DealSoapIntegrationTest -X
```

### Test Coverage

**DealSoapIntegrationTest** (8 tests):
- Get all deals
- Get deal by ID
- Create deal
- Update deal
- Delete deal
- Get deals by status
- Get deals by sales rep
- Handle non-existent deal

**UserSoapIntegrationTest** (8 tests):
- Get all users
- Get user by ID
- Get user by username
- Create user
- Update user
- Delete user
- Get users by role
- Handle non-existent user

### Using SOAP UI

1. Download and install [SOAP UI](https://www.soapui.org/)
2. Create new SOAP project
3. Import WSDL: `http://localhost:8082/soap/DealService?wsdl`
4. SOAP UI generates test requests for all operations
5. Execute requests and inspect responses

## Key Concepts

### JAX-WS Annotations

#### @WebService
Marks an interface or class as a web service.

```java
@WebService(name = "DealService", targetNamespace = "http://soap.api.commissions.edu.chapman.com/")
public interface DealService {
    // Operations
}
```

**Parameters:**
- `name`: Port type name in WSDL
- `targetNamespace`: XML namespace for the service
- `serviceName`: Service name in WSDL
- `portName`: Port name in WSDL

#### @WebMethod
Marks a method as a web service operation.

```java
@WebMethod(operationName = "getAllDeals")
List<DealDTO> getAllDeals();
```

**Parameters:**
- `operationName`: Operation name in WSDL
- `action`: SOAP action header
- `exclude`: Exclude method from WSDL

#### @WebParam
Defines a parameter for a web service operation.

```java
@WebMethod
DealDTO getDealById(@WebParam(name = "id") String id);
```

**Parameters:**
- `name`: Parameter name in WSDL
- `mode`: IN, OUT, or INOUT
- `header`: Whether parameter is in SOAP header

### JAXB Annotations

#### @XmlRootElement
Marks a class as the root of an XML document.

```java
@XmlRootElement(name = "Deal")
public class DealDTO {
    // Fields
}
```

#### @XmlAccessorType
Controls which fields are serialized to XML.

```java
@XmlAccessorType(XmlAccessType.FIELD)  // Serialize all fields
```

**Access Types:**
- `FIELD`: All non-static, non-transient fields
- `PROPERTY`: All getter/setter pairs
- `PUBLIC_MEMBER`: All public fields and properties
- `NONE`: Only annotated fields/properties

#### @XmlElement
Maps a field to an XML element.

```java
@XmlElement(required = true)
private String title;
```

### DTO Pattern

**Why DTOs for SOAP?**

1. **Separation of Concerns**: API contract separate from domain logic
2. **Version Control**: Evolve API without changing domain models
3. **Security**: Control what data is exposed
4. **XML Compatibility**: JAXB works best with simple POJOs
5. **Performance**: Reduce data transfer size

**Example:**
```java
// Domain Model (rich behavior)
public class Deal {
    private String id;
    private BigDecimal value;
    // Complex business logic
    public BigDecimal calculateCommission() { ... }
}

// DTO (data only)
@XmlRootElement
public class DealDTO {
    @XmlElement
    private String id;
    @XmlElement
    private BigDecimal value;
    // No business logic
}
```

### Mapper Pattern

The `DomainMapper` class converts between domain models and DTOs:

```java
// Domain to DTO
DealDTO dto = DomainMapper.toDTO(deal);

// DTO to Domain
Deal deal = DomainMapper.fromDTO(dto);

// List conversion
List<DealDTO> dtos = DomainMapper.dealsToDTO(deals);
```

### Endpoint Publishing

JAX-WS provides `Endpoint.publish()` for publishing services:

```java
Endpoint.publish("http://localhost:8082/soap/DealService", dealService);
```

This:
1. Starts HTTP server
2. Generates WSDL
3. Handles SOAP requests
4. Marshals/unmarshals XML

### SOAP Faults

SOAP uses faults for error handling:

```xml
<S:Fault>
  <faultcode>S:Server</faultcode>
  <faultstring>Deal not found</faultstring>
  <detail>
    <message>No deal with ID: DEAL-999</message>
  </detail>
</S:Fault>
```

**Creating Faults in Java:**
```java
throw new SOAPFaultException(
    SOAPFactory.newInstance().createFault(
        "Deal not found",
        new QName(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE, "Server")
    )
);
```

## Project Structure

```
src/main/java/com/chapman/edu/commissions/api/soap/
├── SoapServer.java                    # Main server
├── dto/                               # Data Transfer Objects
│   ├── DealDTO.java
│   ├── DealProductDTO.java
│   ├── UserDTO.java
│   ├── CommissionPlanDTO.java
│   ├── DisputeDTO.java
│   └── DisputeCommentDTO.java
├── mapper/
│   └── DomainMapper.java             # DTO ↔ Domain conversion
├── service/                          # Web services
│   ├── DealService.java              # Interface
│   ├── DealServiceImpl.java          # Implementation
│   ├── UserService.java
│   ├── UserServiceImpl.java
│   ├── CommissionPlanService.java
│   ├── CommissionPlanServiceImpl.java
│   ├── DisputeService.java
│   └── DisputeServiceImpl.java
└── README.md                         # This file

src/test/java/com/chapman/edu/commissions/api/soap/
├── SoapIntegrationTestBase.java      # Base test class
├── DealSoapIntegrationTest.java      # Deal service tests
└── UserSoapIntegrationTest.java      # User service tests
```

## Design Patterns

1. **Service Interface Pattern**: Separate interface from implementation
2. **Data Transfer Object (DTO)**: Transfer data between layers
3. **Mapper Pattern**: Convert between domain and DTO
4. **Repository Pattern**: Abstract data access
5. **Dependency Injection**: Inject repositories via constructor
6. **Template Method**: Base test class for common setup/teardown

## Comparison: REST vs SOAP vs GraphQL

This project includes ALL THREE implementations:

| Feature | REST (8080) | GraphQL (8081) | SOAP (8082) |
|---------|-------------|----------------|-------------|
| **Protocol** | HTTP | HTTP | SOAP/HTTP |
| **Format** | JSON | JSON | XML |
| **Endpoints** | 20+ | 1 | 4 services |
| **Contract** | OpenAPI (optional) | SDL Schema | WSDL (required) |
| **Type Safety** | Weak | Strong | Strong |
| **Tools** | Postman, curl | GraphiQL | SOAP UI, wsimport |
| **Learning Curve** | Easy | Medium | Steep |
| **Use Case** | Web/Mobile APIs | Flexible clients | Enterprise systems |

All three load the same sample data and use the same repositories.

## Learning Objectives

This implementation demonstrates:

1. SOAP web services fundamentals
2. JAX-WS annotations (@WebService, @WebMethod, @WebParam)
3. JAXB for XML binding (@XmlRootElement, @XmlElement)
4. WSDL generation and service discovery
5. DTO pattern for API/domain separation
6. Mapper pattern for object conversion
7. Endpoint publishing with JAX-WS
8. Integration testing with JAX-WS clients
9. Comparison with REST and GraphQL
10. Enterprise web service patterns

## Next Steps

To extend:

1. **Add WS-Security**: Authentication and encryption
2. **Add MTOM**: Efficient binary data transfer
3. **Add WS-Addressing**: Advanced routing
4. **Add Fault Handling**: Custom SOAP faults
5. **Add Request Validation**: Schema validation
6. **Add Logging**: SOAP message logging
7. **Add Async Operations**: Asynchronous web services
8. **Add UDDI**: Service registry
9. **Replace in-memory with database**: Persistent storage
10. **Add WS-AtomicTransaction**: Distributed transactions

## References

- [JAX-WS Tutorial](https://jakarta.ee/specifications/xml-web-services/)
- [JAXB Tutorial](https://jakarta.ee/specifications/xml-binding/)
- [SOAP Specification](https://www.w3.org/TR/soap/)
- [WSDL Specification](https://www.w3.org/TR/wsdl20/)
- [SOAP UI](https://www.soapui.org/)

---

**Created**: October 2024
**Purpose**: Educational SOAP web services implementation
**License**: Educational use