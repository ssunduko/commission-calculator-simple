# Encapsulation

## Overview

Encapsulation is one of the four fundamental OOP principles. It refers to the bundling of data (attributes) and methods (functions) that operate on the data into a single unit called a class, and restricting direct access to some of the object's components.

## Key Concepts

1. **Data Hiding**: Encapsulation hides the internal state of an object from the outside world, making it accessible only through public methods.
2. **Access Control**: Using access modifiers (private, protected, public) to control the visibility of class members.
3. **Getters and Setters**: Methods that provide controlled access to private data.
4. **Validation**: Ability to validate data before it's assigned to class attributes.

## Examples in This Directory

### `EncapsulatedUser.java`

This class demonstrates encapsulation by:
- Declaring all data members as private
- Providing public getter and setter methods for controlled access
- Implementing validation in setter methods
- Including methods that operate on the encapsulated data

### `UserDemo.java`

This class demonstrates how to use the encapsulated `EncapsulatedUser` class:
- Creating instances of `EncapsulatedUser`
- Accessing data through getter and setter methods
- Showing how encapsulation prevents direct access to private data
- Demonstrating validation in action

## Benefits of Encapsulation

1. **Improved Control**: Control over what data is accessible and how it can be modified
2. **Data Validation**: Ability to validate data before it's assigned to class attributes
3. **Flexibility**: Implementation details can change without affecting the public interface
4. **Maintainability**: Code is easier to maintain as changes are localized
5. **Security**: Sensitive data can be hidden from users