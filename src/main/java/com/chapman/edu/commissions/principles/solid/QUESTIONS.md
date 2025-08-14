# SOLID Principles Knowledge Test

## Multiple Choice Questions

1. What does the acronym SOLID stand for in software development?
   a) Simple Object-Linked Interface Design
   b) Single responsibility, Open-closed, Liskov substitution, Interface segregation, Dependency inversion
   c) Structured Object Logic In Development
   d) Software Organization, Logic, Implementation, and Design

2. According to the Single Responsibility Principle (SRP), a class should:
   a) Have multiple responsibilities to maximize code reuse
   b) Have only one reason to change
   c) Implement as many interfaces as possible
   d) Be open for extension but closed for modification

3. Which of the following is NOT one of the SOLID principles?
   a) Liskov Substitution Principle
   b) Interface Segregation Principle
   c) Composition Over Inheritance Principle
   d) Dependency Inversion Principle

4. What is the primary goal of the Open-Closed Principle (OCP)?
   a) To ensure that classes are open for extension but closed for modification
   b) To ensure that a subclass can always be substituted for its parent class
   c) To ensure that interfaces are specific to clients
   d) To ensure that high-level modules don't depend on low-level modules

5. Which of the following is a benefit of following the SOLID principles?
   a) Increased coupling between components
   b) More rigid and less adaptable code
   c) Improved maintainability and extensibility
   d) Faster initial development at the expense of long-term maintenance

## Short Answer Questions

6. Explain how the Liskov Substitution Principle (LSP) relates to inheritance and polymorphism.

7. Describe two specific examples of how you would apply the Interface Segregation Principle (ISP) in a real-world application.

8. What are the potential negative consequences of violating the Dependency Inversion Principle (DIP) in a large codebase?

9. How do the SOLID principles collectively contribute to creating more maintainable and extensible software?

10. In what situations might it be acceptable to deviate from strict adherence to the SOLID principles?

## Code Analysis Questions

11. Identify the SOLID principle violations in the following code snippet and explain how you would refactor it:
```java
public class UserService {
    private Database database;
    private EmailSender emailSender;
    private Logger logger;
    
    public UserService() {
        this.database = new MySQLDatabase();
        this.emailSender = new SmtpEmailSender();
        this.logger = new FileLogger();
    }
    
    public void registerUser(String username, String email, String password) {
        // Validate input
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        
        // Hash password
        String hashedPassword = hashPassword(password);
        
        // Save to database
        database.execute("INSERT INTO users (username, email, password) VALUES ('" + 
                         username + "', '" + email + "', '" + hashedPassword + "')");
        
        // Send welcome email
        emailSender.sendEmail(email, "Welcome to our service", 
                             "Dear " + username + ", thank you for registering.");
        
        // Log action
        logger.log("User registered: " + username);
    }
    
    public void deleteUser(String username) {
        database.execute("DELETE FROM users WHERE username = '" + username + "'");
        logger.log("User deleted: " + username);
    }
    
    public void updateUserEmail(String username, String newEmail) {
        if (newEmail == null || !newEmail.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        database.execute("UPDATE users SET email = '" + newEmail + "' WHERE username = '" + username + "'");
        logger.log("Email updated for user: " + username);
    }
    
    private String hashPassword(String password) {
        // Password hashing logic
        return "hashed_" + password;
    }
}
```

12. How would you apply the SOLID principles to improve this payment processing system?
```java
public class PaymentProcessor {
    public void processPayment(Order order, String paymentMethod) {
        double amount = calculateTotal(order);
        
        if (paymentMethod.equals("credit_card")) {
            // Process credit card payment
            validateCreditCard(order.getCreditCardNumber(), order.getCreditCardExpiry(), order.getCreditCardCVV());
            chargeCreditCard(order.getCreditCardNumber(), order.getCreditCardExpiry(), order.getCreditCardCVV(), amount);
            sendCreditCardReceipt(order.getCustomerEmail(), amount);
        } 
        else if (paymentMethod.equals("paypal")) {
            // Process PayPal payment
            initiatePayPalPayment(order.getPayPalEmail(), amount);
            waitForPayPalConfirmation(order.getPayPalEmail());
            sendPayPalReceipt(order.getCustomerEmail(), amount);
        }
        else if (paymentMethod.equals("bank_transfer")) {
            // Process bank transfer
            provideBankDetails(order.getCustomerEmail());
            markOrderAsPending(order.getId());
            scheduleBankTransferCheck(order.getId(), 3); // Check after 3 days
        }
        
        // Update order status
        updateOrderStatus(order.getId(), "PAID");
        
        // Update inventory
        for (OrderItem item : order.getItems()) {
            updateInventory(item.getProductId(), item.getQuantity());
        }
        
        // Generate invoice
        generateInvoice(order);
        
        // Send notification
        sendOrderConfirmation(order.getCustomerEmail(), order.getId());
    }
    
    private double calculateTotal(Order order) {
        double total = 0;
        for (OrderItem item : order.getItems()) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }
    
    private void validateCreditCard(String cardNumber, String expiry, String cvv) {
        // Credit card validation logic
    }
    
    private void chargeCreditCard(String cardNumber, String expiry, String cvv, double amount) {
        // Credit card charging logic
    }
    
    private void sendCreditCardReceipt(String email, double amount) {
        // Send receipt logic
    }
    
    private void initiatePayPalPayment(String paypalEmail, double amount) {
        // PayPal initiation logic
    }
    
    private void waitForPayPalConfirmation(String paypalEmail) {
        // Wait for confirmation logic
    }
    
    private void sendPayPalReceipt(String email, double amount) {
        // Send receipt logic
    }
    
    private void provideBankDetails(String email) {
        // Provide bank details logic
    }
    
    private void markOrderAsPending(String orderId) {
        // Mark as pending logic
    }
    
    private void scheduleBankTransferCheck(String orderId, int days) {
        // Schedule check logic
    }
    
    private void updateOrderStatus(String orderId, String status) {
        // Update order status logic
    }
    
    private void updateInventory(String productId, int quantity) {
        // Update inventory logic
    }
    
    private void generateInvoice(Order order) {
        // Generate invoice logic
    }
    
    private void sendOrderConfirmation(String email, String orderId) {
        // Send confirmation logic
    }
}
```