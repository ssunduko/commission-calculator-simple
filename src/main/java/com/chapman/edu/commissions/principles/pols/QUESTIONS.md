# POLS Principle Knowledge Test

## Multiple Choice Questions

1. What does POLS stand for in software development?
   a) Principle of Least Surprise
   b) Principle of Least Specification
   c) Programming Oriented Language Structure
   d) Principle of Logical Sequencing

2. According to the POLS principle, a component of a system should:
   a) Be as complex as possible to showcase developer skills
   b) Behave in a way that most users will expect it to behave
   c) Change behavior frequently to keep users engaged
   d) Prioritize performance over predictability

3. Which of the following is NOT a common POLS violation?
   a) Methods that calculate values but don't update related state variables
   b) Methods with descriptive names that accurately reflect their behavior
   c) Similar methods that behave differently
   d) Methods that perform actions not implied by their names

4. What is the primary goal of the POLS principle?
   a) To make code more surprising and interesting
   b) To reduce the cognitive load on developers using the code
   c) To eliminate all method documentation
   d) To maximize code complexity

5. Which of the following is a benefit of following the POLS principle?
   a) Increased development time
   b) More bugs due to unexpected behavior
   c) Faster onboarding of new developers
   d) Higher cognitive load for code users

## Short Answer Questions

6. Explain how the POLS principle relates to method naming conventions in software development.

7. Describe two specific strategies you can use to ensure your code follows the Principle of Least Surprise.

8. What are the potential negative consequences of violating the POLS principle in a large codebase?

9. How does the POLS principle contribute to better API design?

10. In what situations might it be acceptable to deviate from the POLS principle?

## Code Analysis Questions

11. Identify the POLS violations in the following code snippet and explain how you would refactor it:
```java
public class UserAccount {
    private double balance;
    private boolean isActive;
    
    public UserAccount(double initialBalance) {
        this.balance = initialBalance;
        this.isActive = true;
    }
    
    public double deposit(double amount) {
        if (!isActive) {
            return balance;
        }
        return balance + amount;
    }
    
    public void checkAccountStatus() {
        if (balance < 0) {
            isActive = false;
            balance = 0;
        }
    }
    
    public double getBalance() {
        return balance;
    }
}
```

12. How would you apply the POLS principle to improve this notification system?
```java
public class NotificationSystem {
    private List<String> subscribers = new ArrayList<>();
    
    public void addSubscriber(String email) {
        subscribers.add(email);
        sendWelcomeEmail(email);
    }
    
    private void sendWelcomeEmail(String email) {
        System.out.println("Welcome email sent to: " + email);
        // Code to send actual email...
    }
    
    public void removeSubscriber(String email) {
        subscribers.remove(email);
    }
    
    public void notify(String message) {
        for (String subscriber : subscribers) {
            // Code to send notification...
            System.out.println("Notification sent to: " + subscriber);
        }
        // Also log the message to database
        logToDatabase(message);
    }
    
    private void logToDatabase(String message) {
        System.out.println("Message logged to database: " + message);
        // Code to log to database...
    }
}
```