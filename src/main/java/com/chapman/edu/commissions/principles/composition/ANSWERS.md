# Composition Over Inheritance Knowledge Test - Answers

## Multiple Choice Questions

1. What does the "Composition Over Inheritance" principle suggest?
   **Answer: b) Favor object composition over class inheritance when designing software**

   This principle suggests that developers should prefer building complex functionality by combining simpler objects (composition) rather than creating deep inheritance hierarchies.

2. Which relationship is emphasized by the composition approach?
   **Answer: b) "has-a" relationship**

   Composition emphasizes "has-a" relationships (e.g., a car has an engine) rather than "is-a" relationships (e.g., a car is a vehicle) which are emphasized by inheritance.

3. Which of the following is NOT a common problem with inheritance?
   **Answer: c) Loose Coupling**

   Loose coupling is actually a benefit of good design, not a problem. Inheritance tends to create tight coupling, not loose coupling, between classes.

4. What is the primary goal of the "Composition Over Inheritance" principle?
   **Answer: b) To create more flexible and maintainable code**

   The principle aims to create code that is more flexible, easier to maintain, and less prone to the problems associated with deep inheritance hierarchies.

5. Which of the following is a benefit of using composition?
   **Answer: c) Components can be swapped at runtime**

   Composition allows components to be swapped at runtime, providing greater flexibility than inheritance, which establishes relationships at compile time.

## Short Answer Questions

6. Explain the difference between "is-a" and "has-a" relationships in object-oriented design and how they relate to inheritance and composition.

   **Answer:** In object-oriented design, "is-a" and "has-a" relationships represent two fundamental ways objects can relate to each other:

   An "is-a" relationship indicates that one class is a specialized type of another class. For example, a Dog is an Animal. This relationship is implemented through inheritance, where the subclass (Dog) inherits properties and behaviors from the superclass (Animal). Inheritance establishes a strong, permanent relationship at compile time and creates tight coupling between classes.

   A "has-a" relationship indicates that one class contains or possesses instances of another class. For example, a Car has an Engine. This relationship is implemented through composition, where one class contains references to instances of other classes. Composition creates looser coupling, as the contained objects can be changed or replaced at runtime.

   Inheritance is appropriate when there is a true specialization relationship that is unlikely to change, while composition is more flexible and adaptable to changing requirements. The "Composition Over Inheritance" principle suggests favoring "has-a" relationships over "is-a" relationships when designing software to create more maintainable and flexible code.

7. Describe two specific scenarios where composition would be a better choice than inheritance.

   **Answer:**

   1. **When behavior needs to change dynamically at runtime:** If a class needs to exhibit different behaviors depending on runtime conditions, composition allows you to swap out behavior implementations. For example, a character in a game might need different movement strategies (flying, walking, swimming) that change during gameplay. With composition, you can inject different movement strategy objects as needed, whereas with inheritance, the character would be locked into a specific type.

   2. **When there are multiple orthogonal dimensions of variation:** If a class has multiple aspects that can vary independently, inheritance leads to a combinatorial explosion of subclasses. For example, if you have different types of shapes (circle, square, triangle) and different rendering styles (solid, outlined, dashed), inheritance would require nine different subclasses. With composition, you can separately compose shape objects with renderer objects, keeping the code more manageable and flexible.

8. What is the "Fragile Base Class Problem" and how does composition help avoid it?

   **Answer:** The Fragile Base Class Problem is a fundamental issue in object-oriented programming where changes to a base (parent) class can unexpectedly break its subclasses, even if the changes appear safe. This occurs because inheritance creates tight coupling between the base class and its subclasses, and subclasses often depend on specific implementation details of the base class.

   For example, if a base class changes how it uses its internal methods, subclasses that override those methods might break because they were designed with assumptions about how the base class would use them. Even seemingly innocent changes like adding a new method to the base class can cause problems if that method name happens to be used by a subclass for a different purpose.

   Composition helps avoid this problem by reducing coupling between classes. Instead of inheriting implementation details, a class that uses composition contains references to other objects and delegates to them. This creates clearer boundaries between components and reduces dependencies on implementation details. When changes are made to a component class, only the specific functionality provided by that component is affected, and these changes are less likely to have unexpected ripple effects throughout the system.

9. How does the "Composition Over Inheritance" principle relate to the Single Responsibility Principle?

   **Answer:** The "Composition Over Inheritance" principle and the Single Responsibility Principle (SRP) are complementary design principles that work together to create more maintainable and flexible code:

   The Single Responsibility Principle states that a class should have only one reason to change, meaning it should have only one responsibility or concern. This encourages creating small, focused classes that do one thing well.

   Composition Over Inheritance encourages building complex functionality by combining these small, focused classes rather than creating deep inheritance hierarchies.

   These principles relate in several ways:

   1. Composition naturally supports SRP by allowing you to create small, single-purpose components that can be combined to create complex behavior. Each component handles one aspect of functionality.

   2. Inheritance often violates SRP because subclasses tend to inherit multiple responsibilities from their parent classes, making them dependent on changes in any of those responsibilities.

   3. When refactoring code to follow SRP, you often break down large classes into smaller ones, which then need to be composed together—naturally leading to composition over inheritance.

   4. Both principles aim to create more maintainable code by reducing coupling and increasing cohesion.

   By following both principles together, you create systems of small, focused classes (SRP) that collaborate through composition rather than inheritance, resulting in more flexible, maintainable, and testable code.

10. In what situations might inheritance still be the appropriate choice over composition?

    **Answer:** While composition is often preferred, inheritance remains appropriate in several scenarios:

    1. **Clear and stable "is-a" relationships:** When there is a genuine specialization relationship that is unlikely to change over time. For example, a Square is truly a Rectangle (mathematically speaking), and this relationship is stable.

    2. **Framework design and extension points:** Many frameworks are designed to be extended through inheritance. For example, when creating a custom component in a UI framework, you often need to inherit from a base component class.

    3. **Interface implementation:** Implementing interfaces (which is technically a form of inheritance) is still a best practice for defining contracts that classes must fulfill. This is often combined with composition for the actual implementation.

    4. **Template Method Pattern:** When you want to define the skeleton of an algorithm in a base class but let subclasses override specific steps, the Template Method pattern using inheritance can be appropriate.

    5. **Simple, shallow hierarchies:** When the inheritance hierarchy is simple (not deep) and the base class is well-designed with clear extension points, inheritance can be more straightforward than composition.

    6. **Performance-critical code:** In some cases, inheritance might offer better performance than composition due to reduced indirection, though this should be verified through measurement rather than assumed.

    The key is to use inheritance judiciously, ensuring that it represents true "is-a" relationships and doesn't lead to the problems associated with deep or complex inheritance hierarchies.

## Code Analysis Questions

11. Identify the problems with the following inheritance-based design and explain how you would refactor it using composition:

```java
public class Vehicle {
    protected int speed;
    protected int capacity;
    
    public void accelerate() {
        speed += 10;
    }
    
    public void brake() {
        speed = Math.max(0, speed - 10);
    }
    
    public void addPassenger() {
        // Add passenger logic
    }
}

public class Airplane extends Vehicle {
    private int altitude;
    
    @Override
    public void accelerate() {
        speed += 50; // Airplanes accelerate faster
    }
    
    public void takeOff() {
        accelerate();
        altitude = 1000;
    }
    
    public void land() {
        altitude = 0;
        speed = 0;
    }
}

public class Boat extends Vehicle {
    private int waterDisplacement;
    
    @Override
    public void accelerate() {
        speed += 5; // Boats accelerate slower
    }
    
    public void dock() {
        speed = 0;
    }
}
```

**Answer:** The problems with this inheritance-based design include:

1. **Inappropriate inheritance hierarchy:** Not all vehicles share the same behavior. For example, the way an airplane accelerates is fundamentally different from how a boat accelerates.

2. **Method overriding for behavior variation:** Each subclass needs to override the `accelerate()` method to provide appropriate behavior, indicating that this behavior should be composed rather than inherited.

3. **Tight coupling:** Changes to the `Vehicle` class could unexpectedly affect `Airplane` and `Boat` classes.

4. **Limited flexibility:** It's difficult to change the acceleration behavior at runtime or to reuse these behaviors in other contexts.

Here's a refactored solution using composition:

```java
// Define interfaces for behaviors
interface MovementStrategy {
    void accelerate(Vehicle vehicle);
    void brake(Vehicle vehicle);
}

interface PassengerManagement {
    void addPassenger(Vehicle vehicle);
}

// Base Vehicle class that uses composition
public class Vehicle {
    protected int speed;
    protected int capacity;
    protected MovementStrategy movementStrategy;
    protected PassengerManagement passengerManagement;
    
    public Vehicle(MovementStrategy movementStrategy, PassengerManagement passengerManagement) {
        this.movementStrategy = movementStrategy;
        this.passengerManagement = passengerManagement;
        this.speed = 0;
        this.capacity = 0;
    }
    
    public void accelerate() {
        movementStrategy.accelerate(this);
    }
    
    public void brake() {
        movementStrategy.brake(this);
    }
    
    public void addPassenger() {
        passengerManagement.addPassenger(this);
    }
    
    // Getters and setters
    public int getSpeed() {
        return speed;
    }
    
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}

// Specific vehicle types
public class Airplane {
    private Vehicle vehicle;
    private int altitude;
    
    public Airplane(MovementStrategy movementStrategy, PassengerManagement passengerManagement) {
        this.vehicle = new Vehicle(movementStrategy, passengerManagement);
        this.altitude = 0;
    }
    
    public void accelerate() {
        vehicle.accelerate();
    }
    
    public void brake() {
        vehicle.brake();
    }
    
    public void takeOff() {
        accelerate();
        altitude = 1000;
    }
    
    public void land() {
        altitude = 0;
        vehicle.setSpeed(0);
    }
    
    // Delegate other methods as needed
}

public class Boat {
    private Vehicle vehicle;
    private int waterDisplacement;
    
    public Boat(MovementStrategy movementStrategy, PassengerManagement passengerManagement) {
        this.vehicle = new Vehicle(movementStrategy, passengerManagement);
        this.waterDisplacement = 0;
    }
    
    public void accelerate() {
        vehicle.accelerate();
    }
    
    public void brake() {
        vehicle.brake();
    }
    
    public void dock() {
        vehicle.setSpeed(0);
    }
    
    // Delegate other methods as needed
}

// Concrete strategy implementations
class AirplaneMovement implements MovementStrategy {
    @Override
    public void accelerate(Vehicle vehicle) {
        vehicle.setSpeed(vehicle.getSpeed() + 50);
    }
    
    @Override
    public void brake(Vehicle vehicle) {
        vehicle.setSpeed(Math.max(0, vehicle.getSpeed() - 30));
    }
}

class BoatMovement implements MovementStrategy {
    @Override
    public void accelerate(Vehicle vehicle) {
        vehicle.setSpeed(vehicle.getSpeed() + 5);
    }
    
    @Override
    public void brake(Vehicle vehicle) {
        vehicle.setSpeed(Math.max(0, vehicle.getSpeed() - 3));
    }
}

class StandardPassengerManagement implements PassengerManagement {
    @Override
    public void addPassenger(Vehicle vehicle) {
        // Standard passenger management logic
    }
}
```

This refactored design:
- Separates behaviors into interfaces and implementations
- Uses composition to combine behaviors
- Allows behaviors to be changed at runtime
- Reduces coupling between classes
- Makes it easier to add new vehicle types or behaviors without modifying existing code

12. How would you apply the "Composition Over Inheritance" principle to improve this notification system?

```java
public abstract class NotificationSender {
    protected String message;
    
    public NotificationSender(String message) {
        this.message = message;
    }
    
    public abstract void send();
    
    protected void logNotification() {
        System.out.println("Notification logged: " + message);
    }
}

public class EmailNotificationSender extends NotificationSender {
    private String emailAddress;
    
    public EmailNotificationSender(String message, String emailAddress) {
        super(message);
        this.emailAddress = emailAddress;
    }
    
    @Override
    public void send() {
        System.out.println("Email sent to " + emailAddress + ": " + message);
        logNotification();
    }
}

public class SMSNotificationSender extends NotificationSender {
    private String phoneNumber;
    
    public SMSNotificationSender(String message, String phoneNumber) {
        super(message);
        this.phoneNumber = phoneNumber;
    }
    
    @Override
    public void send() {
        System.out.println("SMS sent to " + phoneNumber + ": " + message);
        logNotification();
    }
}
```

**Answer:** The problems with this inheritance-based notification system include:

1. **Tight coupling:** The notification sending and logging behaviors are tightly coupled through inheritance.
2. **Limited flexibility:** It's difficult to add new notification channels or logging mechanisms without modifying existing code.
3. **Single inheritance limitation:** Java only allows single inheritance, so if we wanted to add different logging strategies, we'd be constrained.

Here's a refactored solution using composition:

```java
// Define interfaces for the different responsibilities
interface MessageSender {
    void send(String message);
}

interface NotificationLogger {
    void log(String message);
}

// Concrete implementations of message senders
class EmailSender implements MessageSender {
    private String emailAddress;
    
    public EmailSender(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    @Override
    public void send(String message) {
        System.out.println("Email sent to " + emailAddress + ": " + message);
    }
}

class SMSSender implements MessageSender {
    private String phoneNumber;
    
    public SMSSender(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    @Override
    public void send(String message) {
        System.out.println("SMS sent to " + phoneNumber + ": " + message);
    }
}

// Concrete implementation of notification logger
class ConsoleLogger implements NotificationLogger {
    @Override
    public void log(String message) {
        System.out.println("Notification logged: " + message);
    }
}

class DatabaseLogger implements NotificationLogger {
    @Override
    public void log(String message) {
        System.out.println("Notification logged to database: " + message);
    }
}

// Main notification class that uses composition
public class NotificationService {
    private MessageSender sender;
    private NotificationLogger logger;
    private String message;
    
    public NotificationService(String message, MessageSender sender, NotificationLogger logger) {
        this.message = message;
        this.sender = sender;
        this.logger = logger;
    }
    
    public void send() {
        sender.send(message);
        logger.log(message);
    }
    
    // Setters to allow changing components at runtime
    public void setSender(MessageSender sender) {
        this.sender = sender;
    }
    
    public void setLogger(NotificationLogger logger) {
        this.logger = logger;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
```

This refactored design:
- Separates responsibilities into interfaces and implementations
- Uses composition to combine different behaviors
- Allows components to be changed at runtime
- Makes it easy to add new notification channels or logging mechanisms
- Follows the Single Responsibility Principle
- Provides greater flexibility and reusability