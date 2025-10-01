# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Sales Commission Calculator** educational project that demonstrates software engineering principles through a commission calculation domain. The codebase serves as a teaching platform for SOLID principles, OOP concepts, design patterns, and JUnit testing fundamentals.

**Main Entry Point:** `com.chapman.edu.commissions.CommissionCalculatorRunner` (pom.xml:95)

## Build and Test Commands

### Building
```bash
mvn clean compile
```

### Running the Application
```bash
mvn exec:java -Dexec.mainClass="com.chapman.edu.commissions.CommissionCalculatorRunner"
```

### Testing
```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=CommissionCalculatorTest

# Run with code coverage (JaCoCo report generated in target/site/jacoco/)
mvn clean test jacoco:report

# Run tests with specific tags (if using @Tag annotations)
mvn test -Dgroups="unit"
```

### Packaging
```bash
# Creates shaded JAR with all dependencies
mvn package

# Run the packaged JAR
java -jar target/commission-calculator.jar
```

Note: The build includes a Maven Antrun plugin that automatically stops processes on port 3000 and runs c4builder for documentation visualization.

## Code Architecture

### Package Structure

The codebase is organized into three main areas:

1. **Domain Models** (`com.chapman.edu.commissions.model`)
   - Core business entities: `Deal`, `User`, `CommissionPlan`, `Dispute`
   - Supporting models: `CommissionRule`, `CommissionTier`, `BonusRule`, `DealProduct`
   - Enums: `DealStatus`, `PlanStatus`, `DisputeStatus`, `UserRole`

2. **Principles Examples** (`com.chapman.edu.commissions.principles`)
   - Each principle has `original/` (violation) and `fixed/` (corrected) implementations
   - Categories:
     - `solid/`: Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion
     - `oop/`: Encapsulation, Abstraction, Inheritance, Polymorphism
     - `dry/`, `kiss/`, `yagni/`, `pols/`, `soc/`, `composition/`: Other design principles

3. **Test Patterns** (`src/test/java/com/chapman/edu/commissions`)
   - `fundamentals/`: JUnit 5 features (assertions, assumptions, lifecycle, parametrized, dynamic, etc.)
   - `patterns/`: Testing patterns (builder, fixture)

### Key Architecture Patterns

**Strategy Pattern for Commission Calculation:**
- Base interfaces: `CommissionStrategy`, `DiscountStrategy`, `TaxStrategy`
- Multiple implementations for different product types (Software, Hardware, Training, Services)
- Located in `principles/solid/fixed/ocp/`

**Separation of Concerns Examples:**
- `OrderProcessor` delegates to specialized services: `OrderValidator`, `OrderFormatter`, `OrderRepository`, `Logger`
- Located in `principles/soc/fixed/`

**Dependency Inversion Examples:**
- High-level `CommissionService` depends on abstractions: `Database`, `EmailService`, `Logger`
- Concrete implementations: `MySqlDatabase`, `SmtpEmailService`, `FileLogger`
- Located in `principles/solid/fixed/dip/`

### Domain Model Relationships

**Deal Management:**
- `Deal` contains multiple `DealProduct` items
- Each `Deal` references a sales representative via `salesRepId` (links to `User`)
- `Deal.calculateTotalValue()` aggregates product prices

**Commission Plans:**
- `CommissionPlan` contains lists of `CommissionRule`, `CommissionTier`, and `BonusRule`
- Plans have effective date ranges and status lifecycle (DRAFT → ACTIVE → ARCHIVED)
- `CommissionPlan.isActiveOn(LocalDate)` validates date-based applicability

**Dispute Handling:**
- `Dispute` tracks commission-related disputes with status workflow
- Contains multiple `DisputeComment` entries for discussion history

## Java Version and Features

- **Java 21** with preview features enabled (pom.xml:84)
- Uses modern Java features throughout the codebase
- Lombok annotations for reducing boilerplate

## Testing Framework

**JUnit 5 (Jupiter)** with the following components:
- `junit-jupiter-api`: Core annotations and assertions
- `junit-jupiter-params`: Parameterized tests
- `mockito-core`: Mocking framework

**Test Organization:**
Tests under `fundamentals/` demonstrate specific JUnit features with accompanying documentation:
- Each test category has `QUESTIONS.md`, `ANSWERS.md`, and `README.md` files
- Examples: AAA pattern, contract testing, lifecycle hooks, nested tests, conditional execution

## Documentation

**Architecture Decision Records (ADRs):**
- Located in `docs/adr/`
- Contains template and examples (e.g., HashMap vs Database decision)
- Educational materials with questions/answers about ADR practices

**C4 Diagrams:**
- Source files in `docs/src/` (markdown format)
- Generated PDFs in `docs/docs/`
- Categories: Use Cases, Sequence Diagrams, Class Diagrams
- Run `docs/run-c4builder.bat` (Windows) or `.sh` (Unix) to generate visualizations

## Development Notes

**Model Behavior Quirks:**
- `Deal.setStatus()` uses a static counter to force different `lastModifiedDate` values for testing purposes (Deal.java:76-82)
- `Deal.equals()` returns false when both IDs are null (Deal.java:152)

**Test-Driven Learning:**
The project is designed for educational exploration. When adding new features, consider creating both "violation" and "fixed" examples to demonstrate principles.

**Code Coverage:**
JaCoCo reports are generated in `target/site/jacoco/index.html` after running tests.