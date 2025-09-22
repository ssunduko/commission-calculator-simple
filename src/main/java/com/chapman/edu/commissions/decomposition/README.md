# Decomposition Approaches in Software Design

This directory contains examples of three different decomposition approaches applied to a commission calculation system:

1. **Functional Decomposition**
2. **Object-Oriented Decomposition**
3. **Data-Driven Decomposition**

Each approach represents a different way of breaking down a complex system into manageable parts, with different trade-offs and benefits.

## Functional Decomposition

Functional decomposition breaks down a system into functions or procedures that perform specific tasks. It focuses on the actions or operations that need to be performed.

**Key Characteristics:**
- Functions are the primary unit of organization
- Organizes code around actions or operations
- Data is passed between functions as parameters and return values
- Follows a top-down approach where complex problems are broken down into simpler sub-problems

**Example:** `FunctionalDecompositionExample.java`

In this example, the commission calculation process is decomposed into distinct functions like `calculateBaseCommission()`, `applyCommissionTiers()`, `calculateBonuses()`, etc. Each function handles a specific part of the calculation process.

## Object-Oriented Decomposition

Object-oriented decomposition organizes code around objects that encapsulate data and behavior. It focuses on modeling real-world entities and their relationships.

**Key Characteristics:**
- Objects are instances of classes that define their structure and behavior
- Emphasizes concepts like encapsulation, inheritance, and polymorphism
- Promotes code reuse and modularity through class hierarchies
- Models real-world entities and their relationships

**Example:** `ObjectOrientedDecompositionExample.java`

In this example, the commission calculation process is decomposed into a set of interacting objects like `CommissionCalculator`, `CommissionRule`, `BonusRule`, etc. Each object has specific responsibilities and collaborates with other objects to perform the calculation.

## Data-Driven Decomposition

Data-driven decomposition organizes code around data structures and their transformations. It focuses on the flow of data through the system.

**Key Characteristics:**
- Focuses on data structures and their transformations
- Emphasizes the flow of data through the system
- Separates data structures from the operations that manipulate them
- Often uses pipelines or transformations to process data
- Can leverage functional programming concepts like pure functions and immutability

**Example:** `DataDrivenDecompositionExample.java`

In this example, the commission calculation process is decomposed based on the data structures involved (`DealData`, `PlanData`, `TierData`, `CommissionData`) and the transformations applied to them. The focus is on how data flows through the system and is transformed at each step.

## Comparison

| Aspect | Functional | Object-Oriented | Data-Driven |
|--------|------------|-----------------|-------------|
| **Primary Unit** | Functions | Objects | Data Structures |
| **Focus** | Actions | Entities | Data Flow |
| **State Management** | Passed as parameters | Encapsulated in objects | Transformed through pipelines |
| **Code Organization** | By task | By entity | By data structure |
| **Reuse Mechanism** | Function composition | Inheritance & composition | Data transformation |
| **Strengths** | Simplicity, clarity for procedural tasks | Modeling complex domains, extensibility | Data processing, transformation pipelines |
| **Weaknesses** | Can lead to global state, harder to extend | Can lead to complex class hierarchies | Can be less intuitive for modeling entities |

## When to Use Each Approach

- **Functional Decomposition**: Good for straightforward algorithmic problems where the focus is on the steps to be performed.
- **Object-Oriented Decomposition**: Good for complex domains with many interacting entities and behaviors.
- **Data-Driven Decomposition**: Good for data processing applications where the focus is on transforming data from one form to another.

In practice, many systems use a combination of these approaches, applying each where it makes the most sense.