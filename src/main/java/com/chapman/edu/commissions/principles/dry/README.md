# DRY - Don't Repeat Yourself

## Overview
DRY (Don't Repeat Yourself) is a software development principle aimed at reducing repetition of code. The principle states that "Every piece of knowledge must have a single, unambiguous, authoritative representation within a system."

## Explanation
The DRY principle encourages developers to:

1. Avoid code duplication by abstracting common functionality
2. Maintain a single source of truth for each piece of knowledge
3. Use methods, classes, and modules to encapsulate reusable code
4. Reduce maintenance overhead by eliminating redundancy
5. Improve code quality and reduce the risk of inconsistencies

## Examples in this Directory

This directory contains examples of DRY violations and their fixes:

- **Original**: Contains code examples that violate the DRY principle by having duplicated code and logic.
- **Fixed**: Contains refactored versions of the same code, adhering to the DRY principle by eliminating duplication.

## Common DRY Violations

1. **Copy-Pasted Code**: Identical or nearly identical code appearing in multiple places
2. **Duplicated Logic**: The same business logic implemented in different ways
3. **Repeated String Literals**: The same string values used throughout the codebase
4. **Duplicated Validation Rules**: The same validation logic implemented multiple times
5. **Redundant Calculations**: The same calculations performed in multiple places

## Benefits of Following DRY

1. **Improved Maintainability**: Changes need to be made in only one place
2. **Reduced Bugs**: Fixing a bug in one place fixes it everywhere
3. **Better Code Organization**: Encourages proper abstraction and modularization
4. **Easier Testing**: Less code to test and maintain
5. **Improved Readability**: Code is more concise and focused