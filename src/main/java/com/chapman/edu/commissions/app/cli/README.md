# Model-View-Controller (MVC) Architecture in CLI

This directory demonstrates the **Model-View-Controller (MVC)** architectural pattern in two different implementations:
one with direct service access and one with RESTful API client communication.

## Overview

The MVC pattern is a software design pattern that separates application logic into three interconnected components:

- **Model**: Represents the data and business logic
- **View**: Presents data to the user (user interface)
- **Controller**: Handles user input and updates model/view

## MVC Pattern Explained

```
┌─────────────────────────────────────────────────────────────┐
│                   MVC ARCHITECTURE FLOW                     │
└─────────────────────────────────────────────────────────────┘

    USER INPUT                 CONTROLLER                MODEL
        │                          │                       │
        │  1. Menu Selection       │                       │
        ├─────────────────────────>│                       │
        │                          │  2. Fetch Data        │
        │                          ├──────────────────────>│
        │                          │                       │
        │                          │  3. Return Data       │
        │                          │<──────────────────────┤
        │  4. Display Data         │                       │
        │<─────────────────────────┤                       │
        │                          │                       │
        ↓                          ↓                       ↓
      VIEW                    ORCHESTRATOR              BUSINESS
   (Console)                (Menu Handlers)             LOGIC
```

### Key Principles

1. **Separation of Concerns**: Each component has a distinct responsibility
2. **Loose Coupling**: Components interact through well-defined interfaces
3. **Reusability**: Model and Controller can be reused with different views
4. **Testability**: Each component can be tested independently

## Implementations

### 1. Direct Service Access CLI (`DealManagementCLI.java`)

This is a traditional MVC implementation where the CLI directly accesses service layer components.

```
┌───────────────────────────────────────────────────────────┐
│                TRADITIONAL MVC ARCHITECTURE               │
└───────────────────────────────────────────────────────────┘

┌─────────────────┐         ┌─────────────────┐
│      VIEW       │         │   CONTROLLER    │
│   (Console I/O) │◄────────┤ (Menu Handlers) │
│                 │         │                 │
│ - printMainMenu │         │ - listAllDeals  │
│ - printDealSum  │         │ - createNewDeal │
│ - printDetailed │         │ - closeDeal     │
│ - clearScreen   │         │ - deleteDeal    │
└─────────────────┘         └────────┬────────┘
                                     │
                                     │ uses
                                     ↓
                            ┌─────────────────┐
                            │      MODEL      │
                            │  (Domain Layer) │
                            │                 │
                            │ - DealService   │
                            │ - UserService   │
                            │ - Deal Entity   │
                            │ - User Entity   │
                            └─────────────────┘
```

**MVC Components:**

- **Model**:
  - `DealService` - Business logic for deals
  - `UserService` - Business logic for users
  - `Deal` - Domain entity representing a sales deal
  - `User` - Domain entity representing a system user
  - `H2DealRepository` - Data access layer
  - `DatabaseManager` - Database connection management

- **View**:
  - `printWelcomeBanner()` - Displays welcome screen
  - `printMainMenu()` - Displays menu options
  - `printDealSummary()` - Displays deal list view
  - `printDetailedDeal()` - Displays detailed deal view
  - `showDashboard()` - Displays statistics dashboard
  - `clearScreen()` - Console management

- **Controller**:
  - `start()` - Main event loop and menu routing
  - `listAllDeals()` - Handles "List All Deals" action
  - `listDealsByStatus()` - Handles filtered list action
  - `viewDealDetails()` - Handles detail view action
  - `createNewDeal()` - Handles create action
  - `closeDeal()` - Handles close action
  - `deleteDeal()` - Handles delete action

**Data Flow Example (Creating a Deal):**

1. **User Input** → User selects "Create New Deal" from menu
2. **Controller** → `createNewDeal()` method prompts for input
3. **View** → Displays input prompts (title, products, etc.)
4. **Controller** → Collects input and creates `Deal` object
5. **Model** → `dealService.createDeal(deal)` validates and persists
6. **Controller** → Receives created deal from model
7. **View** → `printDetailedDeal()` displays success message

**Characteristics:**

- **Monolithic**: All layers run in same JVM
- **Direct Access**: Controller directly calls service methods
- **Synchronous**: All operations are blocking
- **Simple**: Straightforward method calls, no serialization needed

---

### 2. REST API Client CLI (`DealManagementCLIClient.java`)

This is a modern MVC implementation using REST API communication, demonstrating proper client-server architecture.

```
┌───────────────────────────────────────────────────────────┐
│              CLIENT-SERVER MVC ARCHITECTURE               │
└───────────────────────────────────────────────────────────┘

┌─────────────────┐         ┌──────────────────┐
│      VIEW       │         │   CONTROLLER     │
│   (Console I/O) │◄────────┤ (Menu Handlers)  │
│                 │         │                  │
│ - printMainMenu │         │ - listAllDeals   │
│ - printDealSum  │         │ - createNewDeal  │
│ - printDetailed │         │ - closeDeal      │
│ - clearScreen   │         │ - deleteDeal     │
└─────────────────┘         └─────────┬────────┘
                                      │
                                      │ uses
                                      ↓
                            ┌──────────────────┐
                            │      MODEL       │
                            │  (API Client)    │
                            │                  │
                            │ - HttpClient     │
                            │ - Gson (JSON)    │
                            │ - REST API       │
                            │ - Deal DTO       │
                            │ - User DTO       │
                            └─────────┬────────┘
                                      │
                                      │ HTTP/JSON
                                      ↓
                            ┌──────────────────┐
                            │   BACKEND API    │
                            │  (localhost:8080)│
                            │                  │
                            │ - DealController │
                            │ - DealService    │
                            │ - H2Repository   │
                            │ - Database       │
                            └──────────────────┘
```

**MVC Components:**

- **Model** (Client-Side):
  - `HttpClient` - Java 11+ HTTP client for REST communication
  - `Gson` - JSON serialization/deserialization
  - `Deal` - Data Transfer Object (DTO) for API communication
  - `User` - Data Transfer Object (DTO) for API communication
  - API endpoints configuration (BASE_URL, DEALS_ENDPOINT)

- **View** (Identical to traditional CLI):
  - `printWelcomeBanner()` - Displays welcome screen
  - `printMainMenu()` - Displays menu options
  - `printDealSummary()` - Displays deal list view
  - `printDetailedDeal()` - Displays detailed deal view
  - `showDashboard()` - Displays statistics dashboard
  - `clearScreen()` - Console management

- **Controller** (HTTP-Aware):
  - `start()` - Main event loop with server connectivity check
  - `checkServerConnection()` - Verifies server availability
  - `listAllDeals()` - GET /api/v1/integration/deals
  - `listDealsByStatus()` - GET /api/v1/integration/deals?status=X
  - `viewDealDetails()` - GET /api/v1/integration/deals/{id}
  - `createNewDeal()` - POST /api/v1/integration/deals
  - `closeDeal()` - POST /api/v1/integration/deals/{id}/close
  - `deleteDeal()` - DELETE /api/v1/integration/deals/{id}

**Data Flow Example (Creating a Deal via REST API):**

1. **User Input** → User selects "Create New Deal" from menu
2. **Controller** → `createNewDeal()` prompts for input
3. **View** → Displays input prompts
4. **Controller** → Collects input and creates `Deal` object
5. **Model** → Serializes `Deal` to JSON using Gson
6. **HTTP** → `POST http://localhost:8080/api/v1/integration/deals`
7. **Server** → `DealController` receives request
8. **Server** → `DealService` validates and creates deal
9. **HTTP** → Response with created deal JSON (201 Created)
10. **Model** → Deserializes JSON to `Deal` object
11. **Controller** → Receives deal object
12. **View** → `printDetailedDeal()` displays success message

**Characteristics:**

- **Distributed**: Client and server run separately (potentially on different machines)
- **Network Communication**: Uses HTTP protocol over TCP/IP
- **Asynchronous Capable**: Can handle network latency
- **Serialization**: Objects converted to/from JSON
- **Authentication**: HTTP Basic Auth headers
- **Scalable**: Multiple clients can connect to same server

---

## Comparing the Two Approaches

| Aspect | Direct Service Access | REST API Client |
|--------|----------------------|----------------|
| **Coupling** | Tightly coupled to service layer | Loosely coupled via HTTP |
| **Deployment** | Single JVM required | Can run on separate machines |
| **Data Format** | Java objects | JSON over HTTP |
| **Network** | No network layer | TCP/IP, HTTP protocol |
| **Authentication** | Direct method calls | HTTP Basic Auth |
| **Scalability** | Single instance | Multiple clients supported |
| **Complexity** | Simple, direct | More complex (serialization, networking) |
| **Testing** | Mock services | Can test against real/mock server |
| **Error Handling** | Java exceptions | HTTP status codes + exceptions |
| **Performance** | Faster (no serialization) | Slower (network + serialization) |

---

## MVC Benefits in CLI Context

### 1. Separation of Concerns

Each component has a single, well-defined responsibility:

- **View**: Console rendering and formatting
- **Controller**: Menu navigation and user input handling
- **Model**: Data management and business logic

### 2. Maintainability

Changes to one component don't affect others:

- Change console output format → Only modify View methods
- Add new menu option → Only modify Controller
- Change business rules → Only modify Model (service layer)

### 3. Testability

Each component can be tested in isolation:

```java
// Test Controller logic without UI
@Test
void testCreateDealFlow() {
    // Mock the model (service)
    DealService mockService = mock(DealService.class);

    // Test controller behavior
    // Assert correct service methods called
}

// Test View rendering without logic
@Test
void testPrintDealSummary() {
    Deal testDeal = createTestDeal();

    // Capture console output
    // Assert correct formatting
}
```

### 4. Reusability

The same Model (DealService) can be used by:
- CLI application (this directory)
- Web UI (HTML/JavaScript)
- Mobile app
- REST API (integration layer)

---

## Architecture Patterns Demonstrated

### 1. **MVC Pattern**

Primary pattern organizing the application into Model, View, and Controller.

### 2. **Service Layer Pattern** (in DealManagementCLI)

Business logic encapsulated in service classes (`DealService`, `UserService`).

### 3. **Repository Pattern** (in DealManagementCLI)

Data access abstracted behind repository interfaces (`H2DealRepository`).

### 4. **Client-Server Pattern** (in DealManagementCLIClient)

Clear separation between client presentation and server business logic.

### 5. **DTO Pattern** (in DealManagementCLIClient)

Data Transfer Objects used for API communication, decoupling client from server entities.

### 6. **Singleton Pattern**

`DatabaseManager` uses Singleton to ensure single database connection.

---

## Running the CLI Applications

### Direct Service Access CLI

```bash
# Compile
mvn compile

# Run
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.app.cli.DealManagementCLI"
```

This CLI will:
1. Initialize the database
2. Start the application
3. Display the main menu
4. Handle user interactions
5. Directly call service methods

### REST API Client CLI

```bash
# First, start the server (in one terminal)
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.app.DealManagementApp"

# Then, start the CLI client (in another terminal)
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.app.cli.DealManagementCLIClient"
```

This CLI will:
1. Check server connectivity (http://localhost:8080)
2. Display the main menu
3. Handle user interactions
4. Make HTTP requests to REST API
5. Display responses

---

## Educational Value

These implementations demonstrate:

1. **MVC Pattern**: How to structure interactive applications
2. **Separation of Concerns**: Clean boundaries between layers
3. **Architectural Evolution**: From monolithic to distributed
4. **RESTful Design**: How clients communicate with REST APIs
5. **HTTP Protocol**: Request/response cycle in practice
6. **JSON Serialization**: Object-to-JSON conversion
7. **Authentication**: HTTP Basic Auth implementation
8. **Error Handling**: Graceful failure management
9. **User Experience**: Console-based interaction design

---

## Key Takeaways

**MVC Pattern Principles:**

1. **Model** manages data and business logic
2. **View** handles presentation and display
3. **Controller** coordinates between Model and View

**Benefits:**

- **Modularity**: Each component is independent
- **Maintainability**: Changes are localized
- **Testability**: Components tested separately
- **Flexibility**: Easy to swap implementations

**Real-World Applications:**

- Web applications (Spring MVC, ASP.NET MVC)
- Desktop applications (Swing, JavaFX)
- Mobile applications (iOS MVC, Android MVVM)
- CLI applications (this implementation)

The MVC pattern is foundational to modern software architecture and provides a proven approach to organizing complex applications.