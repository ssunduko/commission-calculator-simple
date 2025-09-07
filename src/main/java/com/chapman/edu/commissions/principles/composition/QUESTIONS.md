# Composition Over Inheritance Knowledge Test

## Multiple Choice Questions

1. What does the "Composition Over Inheritance" principle suggest?
   a) Always use inheritance instead of composition
   b) Favor object composition over class inheritance when designing software
   c) Avoid using both composition and inheritance
   d) Use composition only when inheritance is not possible

2. Which relationship is emphasized by the composition approach?
   a) "is-a" relationship
   b) "has-a" relationship
   c) "uses-a" relationship
   d) "implements-a" relationship

3. Which of the following is NOT a common problem with inheritance?
   a) Fragile Base Class Problem
   b) Rigid Hierarchies
   c) Loose Coupling
   d) Inheritance Explosion

4. What is the primary goal of the "Composition Over Inheritance" principle?
   a) To eliminate all inheritance from code
   b) To create more flexible and maintainable code
   c) To reduce the number of classes in a system
   d) To increase performance of object-oriented systems

5. Which of the following is a benefit of using composition?
   a) It creates tight coupling between components
   b) It allows for method overriding
   c) Components can be swapped at runtime
   d) It enforces "is-a" relationships

## Short Answer Questions

6. Explain the difference between "is-a" and "has-a" relationships in object-oriented design and how they relate to inheritance and composition.

7. Describe two specific scenarios where composition would be a better choice than inheritance.

8. What is the "Fragile Base Class Problem" and how does composition help avoid it?

9. How does the "Composition Over Inheritance" principle relate to the Single Responsibility Principle?

10. In what situations might inheritance still be the appropriate choice over composition?

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