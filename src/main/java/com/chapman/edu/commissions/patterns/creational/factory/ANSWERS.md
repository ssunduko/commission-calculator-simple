# Factory Pattern Answers

## Simple Factory

1. **What is the main difference between a Simple Factory and a Factory Method?**
   
   The main difference is that a Simple Factory is a single class with a static method that creates objects, while a Factory Method is a pattern where a superclass provides an interface for creating objects, but allows subclasses to decide which class to instantiate. Simple Factory centralizes object creation in one class, while Factory Method distributes it across subclasses.

2. **Why is the Simple Factory not considered a formal design pattern?**
   
   The Simple Factory is not considered a formal design pattern in the Gang of Four book because it's a simpler programming idiom rather than a complex pattern. It doesn't involve inheritance or interface implementation to solve a design problem, but rather encapsulates object creation in a single class.

3. **In what scenarios would you choose a Simple Factory over a Factory Method?**
   
   Choose a Simple Factory when:
   - You have a limited number of product types that are unlikely to change
   - You don't need the flexibility of subclassing the factory
   - You want to centralize object creation logic in one place
   - The creation logic is relatively simple and doesn't require complex initialization

4. **What are the limitations of the Simple Factory pattern?**
   
   Limitations include:
   - Violates the Open/Closed Principle as adding new product types requires modifying the factory class
   - Can lead to a large, complex factory class if there are many product types
   - Doesn't provide the flexibility of subclassing to extend functionality
   - Typically relies on conditional logic (switch/if statements) which can become unwieldy

5. **How does the Simple Factory pattern help with code maintenance?**
   
   The Simple Factory pattern helps with maintenance by:
   - Centralizing object creation logic in one place, making it easier to update
   - Hiding the details of object creation from client code
   - Reducing duplication of creation code throughout the application
   - Making it easier to change the implementation of created objects without affecting client code

## Factory Method

1. **How does the Factory Method pattern promote the Open/Closed Principle?**
   
   The Factory Method pattern promotes the Open/Closed Principle by:
   - Allowing new product types to be added by creating new subclasses of the creator, without modifying existing code
   - Defining an interface for creating objects, but letting subclasses decide which classes to instantiate
   - Enabling extension through inheritance rather than modification of existing code

2. **What is the role of the abstract Creator class in the Factory Method pattern?**
   
   The abstract Creator class:
   - Declares the factory method that returns an object of the product type
   - May provide a default implementation of the factory method
   - Often contains the business logic that depends on the product objects, but doesn't know the concrete classes
   - Defines a template method that calls the factory method to create a product object

3. **How does the Factory Method pattern differ from the Simple Factory?**
   
   The Factory Method pattern differs from Simple Factory in that:
   - It uses inheritance and polymorphism rather than conditional logic
   - It allows subclasses to decide which class to instantiate
   - It follows the Open/Closed Principle by allowing extension without modification
   - It distributes the responsibility of object creation across multiple classes

4. **What are the advantages of using the Factory Method pattern?**
   
   Advantages include:
   - Follows the Single Responsibility Principle by separating product creation from product use
   - Provides hooks for subclasses to extend the creation process
   - Connects parallel class hierarchies (creators and products)
   - Eliminates the need for binding application-specific classes into the code

5. **In what scenarios would you choose a Factory Method over an Abstract Factory?**
   
   Choose Factory Method when:
   - You need to create a single type of product
   - You want to delegate the responsibility of object creation to subclasses
   - You don't need to ensure that created products work together as a family
   - You have a class hierarchy of creators that parallels the product hierarchy

## Abstract Factory

1. **What problem does the Abstract Factory pattern solve that the Factory Method doesn't?**
   
   The Abstract Factory pattern solves the problem of creating families of related or dependent objects without specifying their concrete classes. Unlike Factory Method, which creates a single type of product, Abstract Factory creates multiple types of products that are designed to work together.

2. **How does the Abstract Factory pattern ensure that families of related objects work together?**
   
   The Abstract Factory pattern ensures that families of related objects work together by:
   - Providing an interface for creating each type of product in the family
   - Ensuring that all products created by a concrete factory are compatible
   - Enforcing the constraint that products from different families are not mixed
   - Creating products that share a common theme or configuration

3. **What is the relationship between Abstract Factory and Factory Method patterns?**
   
   The relationship is:
   - Abstract Factory often uses Factory Methods to implement its product creation methods
   - Factory Method creates a single type of product, while Abstract Factory creates families of related products
   - Abstract Factory can be seen as a collection of Factory Methods that create related objects
   - Both patterns delegate object creation to subclasses, but at different levels of abstraction

4. **What are the key components of the Abstract Factory pattern?**
   
   Key components include:
   - Abstract Factory: Declares an interface for creating a family of products
   - Concrete Factory: Implements the operations to create concrete products
   - Abstract Product: Declares an interface for a type of product
   - Concrete Product: Implements the Abstract Product interface
   - Client: Uses only the interfaces declared by Abstract Factory and Abstract Product

5. **When would you use the Abstract Factory pattern over other creational patterns?**
   
   Use Abstract Factory when:
   - A system needs to be independent of how its products are created, composed, and represented
   - A system should be configured with one of multiple families of products
   - A family of related product objects is designed to be used together
   - You want to provide a library of products and only reveal their interfaces, not implementations

## General Questions

1. **How do factory patterns help with dependency injection?**
   
   Factory patterns help with dependency injection by:
   - Providing a mechanism to create objects without specifying their concrete classes
   - Allowing the injection of different implementations at runtime
   - Centralizing the creation logic, making it easier to swap implementations
   - Facilitating the use of interfaces rather than concrete classes in client code

2. **How do factory patterns support the principle of "programming to an interface, not an implementation"?**
   
   Factory patterns support this principle by:
   - Returning objects as abstract types (interfaces or abstract classes) rather than concrete classes
   - Hiding the details of object creation from client code
   - Allowing client code to work with the abstract interfaces without knowing the concrete implementations
   - Enabling the substitution of different implementations without changing client code

3. **What are the performance implications of using factory patterns?**
   
   Performance implications include:
   - A slight overhead due to the additional layer of abstraction
   - Potential for improved performance through lazy initialization or object pooling
   - Possible memory benefits from sharing common resources across created objects
   - Generally, the benefits of improved design outweigh the minor performance costs

4. **How do factory patterns help with unit testing?**
   
   Factory patterns help with unit testing by:
   - Making it easier to substitute mock or test implementations
   - Allowing the isolation of components for testing
   - Enabling the creation of test-specific factories that produce controlled test objects
   - Facilitating the use of dependency injection for better testability

5. **Can factory patterns be combined with other design patterns? If so, provide examples.**
   
   Yes, factory patterns can be combined with other patterns:
   - With Singleton: A factory can be implemented as a singleton to ensure a single point of object creation
   - With Builder: A factory can use a builder to create complex objects step by step
   - With Strategy: A factory can create different strategy implementations based on context
   - With Template Method: Factory Method often uses template methods to define the steps of object creation
   - With Prototype: A factory can use prototype instances to create new objects by cloning