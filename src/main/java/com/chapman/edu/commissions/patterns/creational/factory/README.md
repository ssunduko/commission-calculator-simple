#  Factory Pattern Implementation

This section contains examples of various factory patterns implemented using the commission calculator model classes.

#### Overview of Factory Patterns

Factory patterns are creational design patterns that provide an interface for creating objects without specifying their concrete classes. They help to decouple the client code from the concrete classes, making the code more maintainable and flexible.

#### Patterns Implemented

##### Simple Factory

The Simple Factory pattern provides a static method that creates and returns objects based on a parameter. It's not a formal design pattern but a common programming idiom.

**Files:**
- `SimpleFactory.java` - Implementation of the Simple Factory pattern
- `SimpleFactoryUsage.java` - Example of how to use the Simple Factory

##### Factory Method

The Factory Method pattern defines an interface for creating objects, but lets subclasses decide which classes to instantiate. It allows a class to defer instantiation to subclasses.

**Files:**
- `FactoryMethodStructure.java` - Basic structure of the Factory Method pattern
- `FactoryMethodImplementation.java` - More complete implementation using Deal classes
- `FactoryMethodUsage.java` - Example of how to use the Factory Method pattern
- `FactoryMethodClasses.java` - Example of how to use the Factory Method implementation

##### Abstract Factory

The Abstract Factory pattern provides an interface for creating families of related or dependent objects without specifying their concrete classes. It allows a system to be independent of how its products are created, composed, and represented.

**Files:**
- `AbstractFactoryStructure.java` - Basic structure of the Abstract Factory pattern
- `AbstractFactoryImplementation.java` - More complete implementation
- `AbstractInterfaces.java` - Abstract interfaces used in the Abstract Factory pattern
- `ConcreteImplementation.java` - Concrete implementations of the abstract interfaces
- `AbstractFactoryUsage.java` - Example of how to use the Abstract Factory pattern
- `AbstractFactoryCallingCode.java` - Example of how to use the more complete Abstract Factory implementation

#### Key Concepts

1. **Factory**: A component responsible for creating objects without exposing the instantiation logic to the client.
2. **Product**: The object created by the factory.
3. **Client**: The code that uses the factory to create objects.

#### Benefits of Factory Patterns

1. **Encapsulation**: The creation logic is encapsulated within the factory, hiding the details from the client.
2. **Flexibility**: New product types can be added without changing the client code.
3. **Decoupling**: The client code is decoupled from the concrete product classes.
4. **Centralization**: Object creation code is centralized, making it easier to maintain.

#### When to Use Factory Patterns

- When a class cannot anticipate the type of objects it needs to create
- When a class wants its subclasses to specify the objects it creates
- When you want to provide a library of products and only reveal their interfaces, not their implementations
- When creating objects with a high level of complexity or configuration
