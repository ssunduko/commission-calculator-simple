# Design by Contract Examples

This directory contains examples of Design by Contract (DbC) testing for the Commission Calculator application. The examples demonstrate how to apply contract-based testing principles to the model classes in the application.

## What is Design by Contract?

Design by Contract is a software design approach introduced by Bertrand Meyer in connection with the Eiffel programming language. It focuses on clearly defining the responsibilities and expectations between different components of a system through formal specifications called "contracts."

The key elements of Design by Contract are:

1. **Pre-conditions**: Conditions that must be true before a method is executed. They represent the client's obligations to the method.
2. **Post-conditions**: Conditions that must be true after a method is executed. They represent the method's guarantees to the client.
3. **Invariants**: Conditions that must always be true for an object, regardless of which method is called. They represent the consistent state of the object.

## Examples in this Directory

### DealContractTest

This class demonstrates contract testing for the `Deal` class. It includes examples of:

- Pre-conditions for the `addProduct` method (product must not be null)
- Post-conditions for the `addProduct` method (products list contains the added product)
- Pre-conditions for the `calculateTotalValue` method (products list must not be null)
- Post-conditions for the `calculateTotalValue` method (returned value equals the expected sum)
- Class invariants for the `Deal` class (status, title, and salesRepId must not be null)
- A complex contract example with both pre and post conditions for the `setValue` method

### DealProductContractTest

This class demonstrates contract testing for the `DealProduct` class. It includes examples of:

- Pre-conditions for the `setQuantity` method (quantity must be greater than zero)
- Pre-conditions for the `setPrice` method (price must not be null and must be non-negative)
- Post-conditions for the `calculateTotalPrice` method (returned value equals the expected calculation)
- Class invariants for the `DealProduct` class (quantity, price, and discount must be valid)
- A complex contract example with both pre and post conditions for the `setDiscount` method

## Implementation Notes

The test examples in this directory are designed to demonstrate the concept of Design by Contract, but they do not modify the original model classes. Instead, they show how the model classes would behave if they were implemented with contract principles.

Some of the tests will fail when run against the current implementation because the model classes do not enforce the contracts. The tests include commented code examples showing how the model methods could be modified to enforce the contracts.

In a real-world application, you would typically implement the contract checks directly in the model classes, or use a framework or library that supports Design by Contract principles.

## Benefits of Design by Contract

- **Improved Documentation**: Contracts serve as executable documentation that clearly defines the expected behavior of methods.
- **Better Error Detection**: Contract violations are detected early, making it easier to identify and fix bugs.
- **Simplified Debugging**: When a contract is violated, it's clear which party (client or provider) is at fault.
- **Enhanced Testing**: Contracts provide a natural framework for testing, focusing on the expected behavior of methods.
- **Increased Reliability**: By enforcing contracts, the system becomes more robust and reliable.