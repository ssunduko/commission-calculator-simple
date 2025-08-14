# SoC Principle Knowledge Test

## Multiple Choice Questions

1. What does SoC stand for in software development?
   a) Separation of Classes
   b) Separation of Concerns
   c) System of Components
   d) Structure of Code

2. According to the SoC principle, a software system should be:
   a) Divided into distinct sections, each addressing a separate concern
   b) Built as a monolithic structure for better performance
   c) Designed with as few modules as possible
   d) Implemented using only object-oriented programming

3. Which of the following is NOT a common SoC violation?
   a) A UI class that also handles database operations
   b) A method that both validates input and processes business logic
   c) Creating separate layers for presentation, business logic, and data access
   d) A class that handles both logging and error handling

4. What is the primary goal of the SoC principle?
   a) To make code more complex
   b) To reduce the size of the codebase
   c) To organize code into distinct parts with minimal overlap
   d) To eliminate all dependencies between components

5. Which of the following is a benefit of following the SoC principle?
   a) Increased coupling between components
   b) More difficult maintenance
   c) Improved testability of individual components
   d) Reduced code reusability

## Short Answer Questions

6. Explain how the SoC principle relates to the concept of "cohesion" in software development.

7. Describe two specific architectural patterns that help enforce the Separation of Concerns principle.

8. What are the potential negative consequences of violating the SoC principle in a large codebase?

9. How does the SoC principle contribute to easier testing of code?

10. In what situations might it be acceptable to have some overlap of concerns rather than strictly adhering to SoC?

## Code Analysis Questions

11. Identify the SoC violations in the following code snippet and explain how you would refactor it:
```java
public class OrderProcessor {
    public void processOrder(Order order) {
        // Validate order
        if (order.getCustomerId() == null) {
            throw new ValidationException("Customer ID cannot be empty");
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new ValidationException("Order must contain at least one item");
        }
        
        // Calculate total
        double total = 0;
        for (OrderItem item : order.getItems()) {
            total += item.getPrice() * item.getQuantity();
        }
        order.setTotal(total);
        
        // Apply discount
        if (order.getCustomer().isVip()) {
            order.setTotal(order.getTotal() * 0.9); // 10% discount for VIP
        }
        
        // Save to database
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/orders", "user", "password");
        PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO orders (customer_id, total) VALUES (?, ?)"
        );
        stmt.setString(1, order.getCustomerId());
        stmt.setDouble(2, order.getTotal());
        stmt.executeUpdate();
        
        // Send confirmation email
        EmailService emailService = new EmailService();
        emailService.sendEmail(
            order.getCustomer().getEmail(),
            "Order Confirmation",
            "Your order has been processed. Total: $" + order.getTotal()
        );
    }
}
```

12. How would you apply the SoC principle to improve this user management system?
```java
public class UserManager {
    public void registerUser(String username, String password, String email) {
        // Validate input
        if (username == null || username.isEmpty()) {
            throw new ValidationException("Username cannot be empty");
        }
        if (password == null || password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters");
        }
        if (email == null || !email.contains("@")) {
            throw new ValidationException("Invalid email format");
        }
        
        // Hash password
        String hashedPassword = DigestUtils.md5Hex(password);
        
        // Save to database
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/users", "user", "password");
        PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO users (username, password, email) VALUES (?, ?, ?)"
        );
        stmt.setString(1, username);
        stmt.setString(2, hashedPassword);
        stmt.setString(3, email);
        stmt.executeUpdate();
        
        // Generate authentication token
        String token = UUID.randomUUID().toString();
        
        // Save token to cache
        Cache cache = CacheManager.getInstance().getCache("authTokens");
        cache.put(username, token);
        
        // Send welcome email
        String emailBody = "Welcome to our system, " + username + "! Your account has been created.";
        sendEmail(email, "Welcome!", emailBody);
        
        // Log the registration
        Logger.getLogger(UserManager.class).info("New user registered: " + username);
    }
    
    private void sendEmail(String to, String subject, String body) {
        // Email sending logic
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.example.com");
        props.put("mail.smtp.port", "587");
        
        Session session = Session.getInstance(props);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("system@example.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);
        
        Transport.send(message);
    }
}
```