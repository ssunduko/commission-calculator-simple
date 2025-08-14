# Liskov Substitution Principle (LSP) - Fixed Implementation

## Overview

This package demonstrates a fixed implementation of the Liskov Substitution Principle (LSP), which states that:

> Objects of a superclass should be replaceable with objects of a subclass without affecting the correctness of the program.

The original implementation in `com.se310.commissions.solid.original.LSPViolation` violated this principle by having a subclass that added preconditions not present in the parent class, causing code that depends on the parent class behavior to break when using the subclass.

## The Problem

In the original implementation:

1. The `CommissionCalculator` class had a `calculateCommission` method with no preconditions.
2. The `SeniorSalesCommissionCalculator` subclass added a precondition (minimum 5 sales) by throwing an exception if the condition wasn't met.
3. Code that worked with the parent class would unexpectedly fail when given a subclass instance.

This led to:
- Unexpected runtime exceptions
- Broken polymorphism
- Violation of client expectations
- Difficulty in substituting subclasses for the parent class

## The Solution

The fixed implementation applies the Liskov Substitution Principle by:

1. Defining a clear contract through an interface:
   - `CommissionCalculator`: Interface with methods for calculating commissions and checking preconditions

2. Making preconditions explicit in the contract:
   - Adding a `canCalculate` method that allows implementations to define their preconditions explicitly
   - Clients should check `canCalculate` before calling `calculateCommission`

3. Implementing classes that adhere to the contract:
   - `BaseCommissionCalculator`: Implements the interface with minimal preconditions
   - `SeniorSalesCommissionCalculator`: Implements the interface with stricter preconditions, but makes them explicit through the `canCalculate` method

4. Ensuring proper substitutability:
   - The `CommissionDemo` class demonstrates how to use the calculators in a way that respects LSP

## Benefits

This approach provides several benefits:

1. **Explicit Preconditions**: Preconditions are part of the contract and explicitly checked.
2. **Proper Substitutability**: Subclasses can be used in place of parent classes without breaking code.
3. **Predictable Behavior**: Clients know when a calculator can be used for a specific set of data.
4. **Improved Error Handling**: Errors are prevented rather than caught after they occur.
5. **Better Design**: The design encourages thinking about contracts and substitutability.

## Demonstration

The `CommissionDemo` class demonstrates how the Liskov Substitution Principle is applied in this implementation. It shows how to properly check if a calculator can handle specific data before using it, ensuring that substitutability is maintained.

## Conclusion

By applying the Liskov Substitution Principle, we've created a more robust and predictable design that allows objects of a superclass to be replaced with objects of a subclass without affecting the correctness of the program.