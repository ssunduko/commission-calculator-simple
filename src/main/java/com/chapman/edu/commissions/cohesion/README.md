# Cohesion Examples

This package contains examples of different types of cohesion in software design, implemented using the commission calculator model classes.

## What is Cohesion?

Cohesion refers to the degree to which the elements inside a module belong together. It measures how strongly related the functionality within a single module is. High cohesion is generally desirable as it indicates a well-designed system where each module has a clear, well-defined purpose.

## Types of Cohesion (from weakest to strongest)

### 1. Coincidental Cohesion

**Definition**: The weakest form of cohesion where parts of a module are grouped arbitrarily with no meaningful relationship between them. The only relationship is that they exist in the same source file or class.

**Example**: [CoincidentalCohesion.java](CoincidentalCohesion.java)

This class demonstrates coincidental cohesion by grouping unrelated utility methods that have no meaningful relationship to each other. They just happen to be in the same class.

### 2. Logical Cohesion

**Definition**: Occurs when parts of a module are grouped because they are logically categorized as doing the same kind of function, even though they are different operations that are not related by the flow of data.

**Example**: [LogicalCohesion.java](LogicalCohesion.java)

This class demonstrates logical cohesion by grouping different validation methods that perform similar functions (validation) but on different types of data and with different validation rules.

### 3. Temporal Cohesion

**Definition**: Occurs when parts of a module are grouped together because they are executed at the same time or during the same phase of execution, even though they might be performing different functions.

**Example**: [TemporalCohesion.java](TemporalCohesion.java)

This class demonstrates temporal cohesion by grouping different operations that all need to be performed when a deal is closed (marked as WON). These operations include updating the deal status, calculating commissions, notifying stakeholders, and updating reports.

### 4. Procedural Cohesion

**Definition**: Occurs when parts of a module are grouped together because they follow a specified sequence of execution, where the output from one part serves as input to the next part.

**Example**: [ProceduralCohesion.java](ProceduralCohesion.java)

This class demonstrates procedural cohesion by grouping methods that follow a specific sequence in the deal creation process. Each method performs a step in the process and the output of one method is used as input to the next method.

### 5. Communicational Cohesion

**Definition**: Occurs when parts of a module are grouped together because they operate on the same data or share the same input/output. The functions might be performing different operations, but they all work on the same data structure.

**Example**: [CommunicationalCohesion.java](CommunicationalCohesion.java)

This class demonstrates communicational cohesion by grouping different methods that all operate on the same Deal object. Each method performs a different operation on the deal, but they all share the same data.

### 6. Sequential Cohesion

**Definition**: Occurs when parts of a module are grouped together because the output from one part serves as input to another part, creating a chain of related tasks where data flows from one operation to the next.

**Example**: [SequentialCohesion.java](SequentialCohesion.java)

This class demonstrates sequential cohesion by implementing a commission calculation pipeline where the output of each step becomes the input to the next step, forming a data processing chain.

### 7. Functional Cohesion

**Definition**: The strongest form of cohesion, where all elements of a module contribute to a single, well-defined task or purpose. Every part of the module is essential to the performance of a single function, and the module has a clear, singular purpose.

**Example**: [FunctionalCohesion.java](FunctionalCohesion.java)

This class demonstrates functional cohesion by focusing solely on the task of calculating the total value of a deal with various discounts. All methods in this class contribute to this single, well-defined purpose.

## Importance of Cohesion in Software Design

High cohesion is generally desirable in software design for several reasons:

1. **Maintainability**: Modules with high cohesion are easier to maintain because changes to one part of the module are less likely to affect other parts.

2. **Reusability**: Modules with high cohesion are more reusable because they perform a single, well-defined task that can be used in different contexts.

3. **Understandability**: Modules with high cohesion are easier to understand because they have a clear, singular purpose.

4. **Testability**: Modules with high cohesion are easier to test because they have a well-defined interface and behavior.

5. **Reliability**: Modules with high cohesion are more reliable because they are less likely to contain bugs or unexpected behavior.

The examples in this package demonstrate the different types of cohesion, from the weakest (coincidental) to the strongest (functional), to help understand these concepts in the context of a commission calculator system.