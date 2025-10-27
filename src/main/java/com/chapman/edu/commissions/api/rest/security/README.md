# Integrated Security Authentication

This package implements a **comprehensive security authentication and authorization system** for the REST API, supporting multiple authentication schemes and role-based access control.

## Overview

The security implementation provides:

- **Multiple Authentication Schemes**: Basic Auth, JWT Bearer tokens, API Keys
- **Role-Based Access Control (RBAC)**: Fine-grained permissions based on user roles
- **Thread-Safe Context**: Request-scoped security context using ThreadLocal
- **Extensible Architecture**: Easy to add new authentication methods
- **Declarative Security**: Annotation-based security configuration

## Architecture

### Security Flow

```
1. HTTP Request arrives
   ↓
2. SecurityFilter intercepts request
   ↓
3. Extract credentials from headers
   ↓
4. AuthenticationManager selects appropriate Authenticator
   ↓
5. Authenticator validates credentials
   ↓
6. UserPrincipal created and stored in SecurityContext
   ↓
7. Request proceeds to servlet
   ↓
8. Servlet checks permissions via AuthorizationHelper
   ↓
9. Business logic executes if authorized
   ↓
10. SecurityFilter clears context after response
```

### Components

| Component | Responsibility | Design Pattern |
|-----------|---------------|----------------|
| **AuthenticationToken** | Encapsulates credentials | Value Object |
| **UserPrincipal** | Represents authenticated user | Principal Pattern |
| **SecurityContext** | Thread-local storage for current user | Thread-Local Storage |
| **Authenticator** | Interface for authentication strategies | Strategy Pattern |
| **BasicAuthenticator** | Username/password authentication | Strategy Implementation |
| **JwtAuthenticator** | JWT token authentication | Strategy Implementation |
| **AuthenticationManager** | Coordinates multiple authenticators | Chain of Responsibility |
| **SecurityFilter** | Intercepts requests for authentication | Intercepting Filter |
| **AuthorizationHelper** | Permission checking utilities | Utility/Helper |
| **Secured** | Annotation for declarative security | Declarative Security |

### Architecture Diagram

See `security-architecture.puml` for a detailed PlantUML diagram showing:
- Class structure and relationships
- Authentication and authorization flow
- Design patterns applied
- Component notes and security considerations

## Design Patterns

### 1. Strategy Pattern

**Intent**: Define a family of authentication algorithms, encapsulate each one, and make them interchangeable.

**Implementation**:
- `Authenticator` interface defines the contract
- `BasicAuthenticator` implements username/password validation
- `JwtAuthenticator` implements JWT token validation
- Easy to add OAuth, SAML, or other authentication methods

**Benefits**:
- Clean separation of authentication logic
- Easy to add new authentication methods
- Test each strategy independently

### 2. Chain of Responsibility Pattern

**Intent**: Pass authentication request through a chain of handlers until one handles it.

**Implementation**:
- `AuthenticationManager` maintains a list of `Authenticator` instances
- Each authenticator checks if it supports the authentication scheme
- First matching authenticator processes the request

**Benefits**:
- Decouples sender from receiver
- Dynamic chain configuration
- Single authentication entry point

### 3. Intercepting Filter Pattern

**Intent**: Intercept requests and responses to perform authentication/authorization before business logic.

**Implementation**:
- `SecurityFilter` implements Jakarta Servlet `Filter` interface
- Wraps all HTTP requests
- Performs authentication before request reaches servlets

**Benefits**:
- Separation of concerns (security vs. business logic)
- Centralized security enforcement
- No security code in servlets

### 4. Thread-Local Storage Pattern

**Intent**: Store request-scoped data without passing it explicitly.

**Implementation**:
- `SecurityContext` uses `ThreadLocal<UserPrincipal>`
- Each request thread has isolated security context
- Automatically cleared after request completes

**Benefits**:
- No need to pass user through every method
- Thread-safe storage
- Clean API (no context parameters)

### 5. Value Object Pattern

**Intent**: Represent credentials as immutable objects.

**Implementation**:
- `AuthenticationToken` is immutable
- Factory methods for different authentication types
- No setters, only getters

**Benefits**:
- Thread-safe by design
- Clear intent (basic, bearer, apiKey)
- Prevents accidental modification

## Authentication Schemes

### 1. Basic Authentication

**Header Format**:
```
Authorization: Basic base64(username:password)
```

**Implementation**: `BasicAuthenticator`

**Process**:
1. Decode Base64-encoded credentials
2. Extract username and password
3. Look up user in repository
4. Validate password (simplified for educational purposes)
5. Check if user is active
6. Create `UserPrincipal` with user's roles

**Example**:
```bash
# Username: john.doe, Password: secret
# Base64(john.doe:secret) = am9obi5kb2U6c2VjcmV0

curl -H "Authorization: Basic am9obi5kb2U6c2VjcmV0" \
  http://localhost:8080/api/v2/deals
```

**Security Note**:
This is an educational implementation. Production systems should:
- Hash passwords using bcrypt, Argon2, or PBKDF2
- Use HTTPS exclusively
- Implement rate limiting to prevent brute force attacks
- Log authentication attempts

### 2. JWT Bearer Tokens

**Header Format**:
```
Authorization: Bearer <jwt-token>
```

**Implementation**: `JwtAuthenticator`

**Process** (Educational):
1. Extract token from Authorization header
2. Validate token (simplified using token cache)
3. Extract user information from token
4. Create `UserPrincipal`

**Production Process** (using JWT library):
```java
DecodedJWT jwt = JWT.require(Algorithm.HMAC256(secretKey))
    .build()
    .verify(token);

String userId = jwt.getClaim("userId").asString();
List<String> roles = jwt.getClaim("roles").asList(String.class);
```

**Example**:
```bash
curl -H "Authorization: Bearer sample-token-sales-rep" \
  http://localhost:8080/api/v2/deals
```

**Sample Tokens** (for testing):
- `sample-token-sales-rep` - Sales representative
- `sample-token-manager` - Sales manager
- `sample-token-admin` - System administrator

**Production Libraries**:
- `auth0/java-jwt` - Auth0's JWT library
- `io.jsonwebtoken/jjwt` - JSON Web Token library
- `com.nimbusds/nimbus-jose-jwt` - Nimbus JOSE + JWT

### 3. API Key Authentication

**Header Format**:
```
X-API-Key: <api-key>
```

**Implementation**: Future enhancement (can extend `Authenticator`)

**Example**:
```bash
curl -H "X-API-Key: my-api-key-12345" \
  http://localhost:8080/api/v2/deals
```

## Role-Based Access Control

### User Roles

Defined in `com.chapman.edu.commissions.model.UserRole`:

| Role | Description | Permissions |
|------|-------------|-------------|
| **SALES_REP** | Sales representative | View own deals, create deals |
| **SALES_MANAGER** | Sales manager | View all deals, approve deals, manage team |
| **FINANCE_ADMIN** | Finance administrator | View all deals, manage commissions |
| **SYSTEM_ADMIN** | System administrator | Full access to all resources |

### Permission Checking

**In Servlets** (imperative):

```java
@Override
protected void doPost(HttpServletRequest request,
                     HttpServletResponse response) throws IOException {
    // Require authentication
    if (!AuthorizationHelper.requireAuthentication(response)) {
        return; // 401 sent automatically
    }

    // Require specific role
    if (!AuthorizationHelper.requireRole(UserRole.SALES_MANAGER, response)) {
        return; // 403 sent automatically
    }

    // Require any of multiple roles
    if (!AuthorizationHelper.requireAnyRole(response,
            UserRole.SALES_MANAGER, UserRole.SYSTEM_ADMIN)) {
        return; // 403 sent automatically
    }

    // Business logic here
}
```

**Using SecurityContext**:

```java
// Get current user
UserPrincipal user = SecurityContext.getCurrentUser();

// Check roles manually
if (user.hasRole(UserRole.SALES_MANAGER)) {
    // Manager-specific logic
}

// Check ownership
String dealOwnerId = deal.getSalesRepId();
if (!AuthorizationHelper.requireOwnerOrAdmin(dealOwnerId, response)) {
    return; // Only owner or admin can access
}
```

**Declarative** (using annotation):

```java
@Secured(roles = {UserRole.SALES_MANAGER, UserRole.SYSTEM_ADMIN})
public void doPost(HttpServletRequest req, HttpServletResponse resp) {
    // Only accessible to managers and admins
}
```

Note: `@Secured` annotation is defined but requires aspect/filter implementation to enforce.

## Usage

### Setting Up Security Filter

```java
// Create user repository
Repository<User> userRepository = new InMemoryRepository<>("USER",
    User::getId, User::setId);

// Create authentication manager
AuthenticationManager authManager = new AuthenticationManager()
    .addAuthenticator(new BasicAuthenticator(userRepository))
    .addAuthenticator(new JwtAuthenticator("secret-key-123"));

// Create security filter
SecurityFilter securityFilter = new SecurityFilter(authManager, false);

// Register filter in Tomcat
Context context = tomcat.addContext("/", baseDir);
FilterDef filterDef = new FilterDef();
filterDef.setFilterName("SecurityFilter");
filterDef.setFilter(securityFilter);
context.addFilterDef(filterDef);

FilterMap filterMap = new FilterMap();
filterMap.setFilterName("SecurityFilter");
filterMap.addURLPattern("/api/*");
context.addFilterMap(filterMap);
```

### Configuration Options

**Allow Anonymous Access** (default):
```java
SecurityFilter filter = new SecurityFilter(authManager, false);
// Requests without credentials are allowed through
// Servlets can check authentication individually
```

**Require Authentication for All Endpoints**:
```java
SecurityFilter filter = new SecurityFilter(authManager, true);
// All requests must include valid credentials
// 401 returned automatically for unauthenticated requests
```

### Accessing Current User

```java
// In any servlet or service class
UserPrincipal user = SecurityContext.getCurrentUser();

System.out.println("User ID: " + user.getUserId());
System.out.println("Username: " + user.getUsername());
System.out.println("Roles: " + user.getRoles());

// Check authentication
if (!user.isAuthenticated()) {
    // Anonymous user
}

// Check specific role
if (user.hasRole(UserRole.SALES_MANAGER)) {
    // User is a sales manager
}

// Check multiple roles
if (user.hasAnyRole(UserRole.SALES_MANAGER, UserRole.SYSTEM_ADMIN)) {
    // User has at least one of these roles
}
```

### Securing Endpoints

**Example: Secure Deal Creation**

```java
public class SecuredDealServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        // Only sales reps and managers can create deals
        if (!AuthorizationHelper.requireAnyRole(response,
                UserRole.SALES_REP, UserRole.SALES_MANAGER)) {
            return; // 403 Forbidden sent automatically
        }

        // Get current user
        UserPrincipal user = SecurityContext.getCurrentUser();

        // Create deal logic
        Deal deal = // ... parse from request
        deal.setSalesRepId(user.getUserId()); // Set creator

        dealRepository.save(deal);

        sendJsonResponse(response, deal, HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doPut(HttpServletRequest request,
                        HttpServletResponse response) throws IOException {
        String dealId = extractResourceId(request);
        Deal existingDeal = dealRepository.findById(dealId).orElse(null);

        if (existingDeal == null) {
            sendErrorResponse(response, 404, "Deal not found");
            return;
        }

        // Only owner or admin can update
        if (!AuthorizationHelper.requireOwnerOrAdmin(
                existingDeal.getSalesRepId(), response)) {
            return; // 403 Forbidden
        }

        // Update logic
        // ...
    }
}
```

## HTTP Status Codes

| Code | Meaning | When to Use |
|------|---------|-------------|
| **401 Unauthorized** | Authentication required or failed | No credentials provided, or invalid credentials |
| **403 Forbidden** | Authenticated but insufficient permissions | Valid user but wrong role for operation |

**401 Response**:
```json
{
  "error": "Authentication required",
  "status": 401,
  "timestamp": 1699564800000
}
```

**403 Response**:
```json
{
  "error": "Insufficient permissions",
  "status": 403,
  "timestamp": 1699564800000
}
```

## Testing

### Testing with curl

**Basic Authentication**:
```bash
# Authenticate as user
curl -u john.doe:password http://localhost:8080/api/v2/deals

# Or with explicit header
echo -n "john.doe:password" | base64
# Output: am9obi5kb2U6cGFzc3dvcmQ=

curl -H "Authorization: Basic am9obi5kb2U6cGFzc3dvcmQ=" \
  http://localhost:8080/api/v2/deals
```

**JWT Bearer Token**:
```bash
# Use sample token for sales rep
curl -H "Authorization: Bearer sample-token-sales-rep" \
  http://localhost:8080/api/v2/deals

# Use sample token for manager
curl -H "Authorization: Bearer sample-token-manager" \
  http://localhost:8080/api/v2/deals

# Use sample token for admin
curl -H "Authorization: Bearer sample-token-admin" \
  http://localhost:8080/api/v2/deals
```

**Test Unauthorized Access**:
```bash
# No authentication (should fail if required)
curl -i http://localhost:8080/api/v2/deals
# HTTP/1.1 401 Unauthorized

# Invalid credentials
curl -u invalid:wrong http://localhost:8080/api/v2/deals
# HTTP/1.1 401 Unauthorized
```

**Test Forbidden Access**:
```bash
# Sales rep trying to access admin endpoint
curl -H "Authorization: Bearer sample-token-sales-rep" \
  http://localhost:8080/api/v2/admin/settings
# HTTP/1.1 403 Forbidden
```

### Unit Testing

**Testing Authenticators**:
```java
@Test
void testBasicAuthenticator() {
    Repository<User> userRepo = createTestUserRepository();
    BasicAuthenticator authenticator = new BasicAuthenticator(userRepo);

    AuthenticationToken token = AuthenticationToken.basic("john.doe", "password");
    Optional<UserPrincipal> result = authenticator.authenticate(token);

    assertTrue(result.isPresent());
    assertEquals("john.doe", result.get().getUsername());
    assertTrue(result.get().hasRole(UserRole.SALES_REP));
}
```

**Testing Authorization**:
```java
@Test
void testAuthorizationHelper() {
    // Set up security context
    UserPrincipal user = new UserPrincipal("USER-001", "john.doe",
        "john@example.com", Set.of(UserRole.SALES_REP));
    SecurityContext.setCurrentUser(user);

    try {
        // Test role checking
        assertTrue(SecurityContext.isAuthenticated());
        assertTrue(SecurityContext.getCurrentUser().hasRole(UserRole.SALES_REP));
        assertFalse(SecurityContext.getCurrentUser().hasRole(UserRole.SYSTEM_ADMIN));
    } finally {
        SecurityContext.clear();
    }
}
```

## Design Principles

### Single Responsibility Principle (SRP)

Each class has one clear responsibility:
- `SecurityFilter`: Request interception and authentication
- `AuthenticationManager`: Coordinating authentication strategies
- `BasicAuthenticator`: Basic authentication logic
- `JwtAuthenticator`: JWT authentication logic
- `SecurityContext`: Thread-local storage management
- `AuthorizationHelper`: Permission checking

### Open/Closed Principle (OCP)

The system is:
- **Open for extension**: Easy to add new authentication schemes by implementing `Authenticator`
- **Closed for modification**: Adding OAuth doesn't require changing existing authenticators

### Dependency Inversion Principle (DIP)

- `SecurityFilter` depends on `AuthenticationManager` interface concept
- `AuthenticationManager` depends on `Authenticator` interface, not concrete implementations
- High-level security logic doesn't depend on low-level authentication details

### Interface Segregation Principle (ISP)

- `Authenticator` interface is focused and minimal
- Clients only depend on methods they use
- No "fat interfaces" with unused methods

## Security Best Practices

### ✅ Implemented

- **Immutable Credentials**: `AuthenticationToken` is immutable
- **Thread-Safe Context**: `SecurityContext` uses ThreadLocal
- **Credential Hiding**: `toString()` doesn't expose passwords/tokens
- **Role-Based Access**: Fine-grained permission control
- **Centralized Security**: Filter-based authentication
- **Clear Separation**: Authentication vs. authorization logic separated

### ⚠️ Educational Simplifications

This is an educational implementation. Production systems need:

1. **Password Hashing**:
   ```java
   // Production: Use BCrypt, Argon2, or PBKDF2
   BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
   boolean matches = encoder.matches(plainPassword, hashedPassword);
   ```

2. **JWT Validation**:
   ```java
   // Production: Use proper JWT library
   DecodedJWT jwt = JWT.require(Algorithm.HMAC256(secretKey))
       .withIssuer("commission-calculator")
       .build()
       .verify(token);
   ```

3. **Rate Limiting**: Prevent brute force attacks
   ```java
   // Track failed login attempts
   // Block after N failures
   // Implement exponential backoff
   ```

4. **HTTPS Only**: Never send credentials over HTTP
   ```java
   if (!request.isSecure()) {
       response.sendError(403, "HTTPS required");
   }
   ```

5. **Audit Logging**: Log all authentication attempts
   ```java
   logger.info("Authentication attempt: user={}, success={}, ip={}",
       username, success, request.getRemoteAddr());
   ```

6. **Token Expiration**: JWT tokens should expire
   ```java
   jwt.withExpiresAt(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
   ```

7. **CSRF Protection**: For state-changing operations
8. **SQL Injection Prevention**: Use parameterized queries
9. **XSS Protection**: Sanitize output
10. **Session Management**: Secure session handling

## Common Patterns

### Resource Ownership

```java
// Only owner or admin can modify resource
@Override
protected void doPut(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
    String resourceId = extractResourceId(req);
    Deal deal = dealRepository.findById(resourceId).orElse(null);

    if (deal == null) {
        sendErrorResponse(resp, 404, "Not found");
        return;
    }

    // Check ownership
    if (!AuthorizationHelper.requireOwnerOrAdmin(deal.getSalesRepId(), resp)) {
        return; // 403 Forbidden
    }

    // Update logic
}
```

### Manager Can See All, Rep Sees Own

```java
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
    UserPrincipal user = SecurityContext.getCurrentUser();

    List<Deal> deals = dealRepository.findAll();

    // Sales reps only see their own deals
    if (user.hasRole(UserRole.SALES_REP) &&
            !user.hasAnyRole(UserRole.SALES_MANAGER, UserRole.SYSTEM_ADMIN)) {
        deals = deals.stream()
            .filter(deal -> deal.getSalesRepId().equals(user.getUserId()))
            .collect(Collectors.toList());
    }

    sendJsonResponse(resp, deals);
}
```

### Admin Override

```java
// Admins can bypass certain restrictions
if (SecurityContext.getCurrentUser().hasRole(UserRole.SYSTEM_ADMIN)) {
    // Admin can do anything
} else {
    // Normal permission checks
}
```

## References

- `AuthenticationToken.java` - Credential value object
- `UserPrincipal.java` - Authenticated user representation
- `SecurityContext.java` - Thread-local user storage
- `Authenticator.java` - Authentication strategy interface
- `BasicAuthenticator.java` - Basic auth implementation
- `JwtAuthenticator.java` - JWT auth implementation
- `AuthenticationManager.java` - Authentication coordinator
- `SecurityFilter.java` - Request interception filter
- `AuthorizationHelper.java` - Permission checking utilities
- `Secured.java` - Security annotation
- `security-architecture.puml` - Architecture diagram

## Learning Objectives

This implementation demonstrates:

1. **Multiple Authentication Schemes**: Basic, JWT, API Key
2. **Strategy Pattern**: Pluggable authentication strategies
3. **Chain of Responsibility**: Authentication delegation
4. **Intercepting Filter**: Request interception for security
5. **Thread-Local Storage**: Request-scoped context
6. **Role-Based Access Control**: Permission management
7. **Security Principles**: Authentication vs. authorization
8. **Production Considerations**: What's needed beyond educational code
9. **Design Principles**: SRP, OCP, DIP, ISP applied to security
10. **Real-World Patterns**: Industry-standard security patterns

## Future Enhancements

Potential improvements:

1. **OAuth 2.0 Support**: Third-party authentication
2. **Two-Factor Authentication (2FA)**: Enhanced security
3. **Audit Logging**: Track all security events
4. **Rate Limiting**: Prevent brute force attacks
5. **Password Reset**: Secure password recovery
6. **Session Management**: Manage user sessions
7. **CSRF Protection**: Cross-site request forgery prevention
8. **API Key Management**: Create and revoke API keys
9. **Permission System**: More granular permissions beyond roles
10. **Security Metrics**: Dashboard for security analytics