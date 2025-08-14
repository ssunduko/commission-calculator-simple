# SOLID Principles Knowledge Test - Answers

## Multiple Choice Questions

1. What does the acronym SOLID stand for in software development?
   **Answer: b) Single responsibility, Open-closed, Liskov substitution, Interface segregation, Dependency inversion**

   SOLID is an acronym for five design principles intended to make software designs more understandable, flexible, and maintainable.

2. According to the Single Responsibility Principle (SRP), a class should:
   **Answer: b) Have only one reason to change**

   This is the core concept of SRP - a class should have only one responsibility, which means it should have only one reason to change.

3. Which of the following is NOT one of the SOLID principles?
   **Answer: c) Composition Over Inheritance Principle**

   While Composition Over Inheritance is a valuable design principle, it is not one of the five SOLID principles. The other options are all part of SOLID.

4. What is the primary goal of the Open-Closed Principle (OCP)?
   **Answer: a) To ensure that classes are open for extension but closed for modification**

   The Open-Closed Principle states that software entities should be open for extension but closed for modification, meaning you should be able to add new functionality without changing existing code.

5. Which of the following is a benefit of following the SOLID principles?
   **Answer: c) Improved maintainability and extensibility**

   Following SOLID principles leads to code that is more maintainable, extensible, and adaptable to changing requirements. The other options describe negative outcomes that SOLID helps to avoid.

## Short Answer Questions

6. Explain how the Liskov Substitution Principle (LSP) relates to inheritance and polymorphism.

   **Answer:** The Liskov Substitution Principle (LSP) is fundamentally tied to inheritance and polymorphism. It states that objects of a superclass should be replaceable with objects of a subclass without affecting the correctness of the program. In other words, a subclass should behave in such a way that it won't cause problems when used instead of its parent class.

   LSP ensures that inheritance is used correctly by enforcing behavioral compatibility between subclasses and their base classes. When LSP is followed:

   1. **Polymorphism works as expected**: When code uses a reference to a base class, it can work with any derived class without knowing the specific type, and the behavior will be consistent with expectations.

   2. **Inheritance represents true "is-a" relationships**: Subclasses truly represent specializations of their parent classes, not just structural similarities.

   3. **Contracts are respected**: Subclasses must honor the contracts (preconditions and postconditions) established by their parent classes.

   For example, if a function accepts a parameter of type `Bird` and calls a `fly()` method, a subclass like `Penguin` that cannot fly would violate LSP if it inherited and overrode `fly()` with an implementation that throws an exception or does nothing. Instead, a better design might use a `FlyingBird` interface or abstract class that only flying birds implement.

   LSP violations often manifest as type checking in polymorphic code (e.g., `if (bird instanceof Penguin)`), unexpected exceptions, or ignored method calls, all of which undermine the benefits of polymorphism and make code more fragile and harder to maintain.

7. Describe two specific examples of how you would apply the Interface Segregation Principle (ISP) in a real-world application.

   **Answer:** 
   1. **Document Processing System**: Instead of having a single large `Printer` interface that includes methods for printing, scanning, faxing, and copying, I would apply ISP by creating separate interfaces:

      ```java
      public interface Printer {
          void print(Document document);
      }

      public interface Scanner {
          Scan scan(Document document);
      }

      public interface Fax {
          void fax(Document document, String destination);
      }

      public interface Copier {
          void copy(Document document);
      }
      ```

      Then, specific device classes would implement only the interfaces they support:

      ```java
      // A basic printer only implements Printer
      public class BasicPrinter implements Printer {
          // Implementation details
      }

      // A multifunction device implements all interfaces
      public class MultifunctionPrinter implements Printer, Scanner, Fax, Copier {
          // Implementation details
      }
      ```

      This way, clients that only need printing functionality can depend solely on the `Printer` interface without being forced to know about scanning, faxing, or copying capabilities they don't use.

   2. **E-commerce User System**: Instead of having a single `User` interface with methods for all possible user actions, I would segregate interfaces based on different user roles and responsibilities:

      ```java
      public interface Authenticatable {
          boolean login(String username, String password);
          void logout();
          boolean changePassword(String oldPassword, String newPassword);
      }

      public interface Customer {
          void placeOrder(Order order);
          List<Order> viewOrderHistory();
          void updateShippingInfo(Address address);
      }

      public interface Administrator {
          void manageUsers(User user);
          void viewSystemLogs();
          void configureSystemSettings(Settings settings);
      }

      public interface Vendor {
          void addProduct(Product product);
          void updateInventory(Product product, int quantity);
          List<SalesReport> viewSalesReports();
      }
      ```

      Then, specific user types would implement only the relevant interfaces:

      ```java
      // Regular customer
      public class RegularUser implements Authenticatable, Customer {
          // Implementation details
      }

      // Admin user
      public class AdminUser implements Authenticatable, Administrator {
          // Implementation details
      }

      // Vendor user
      public class VendorUser implements Authenticatable, Vendor {
          // Implementation details
      }

      // Super admin might implement multiple interfaces
      public class SuperAdmin implements Authenticatable, Administrator, Vendor {
          // Implementation details
      }
      ```

      This approach ensures that each client interacts only with the interfaces relevant to their needs, reducing coupling and making the system more maintainable.

8. What are the potential negative consequences of violating the Dependency Inversion Principle (DIP) in a large codebase?

   **Answer:** Violating the Dependency Inversion Principle (DIP) in a large codebase can lead to several serious problems:

   - **Tight coupling**: High-level modules become directly dependent on low-level modules, making it difficult to replace or modify components without affecting other parts of the system.

   - **Reduced testability**: When classes depend directly on concrete implementations rather than abstractions, it becomes harder to mock dependencies for unit testing, leading to more complex test setups or a reliance on integration tests.

   - **Decreased flexibility**: The system becomes rigid and resistant to change because modifications to low-level modules can force changes in high-level modules.

   - **Difficulty in parallel development**: Teams cannot work independently on different components because of the tight interdependencies between modules.

   - **Increased risk when making changes**: Changes to one part of the system are more likely to cause unexpected failures in other parts due to the ripple effect of dependencies.

   - **Harder to reuse components**: Modules with direct dependencies on other concrete modules are difficult to reuse in different contexts or projects.

   - **Monolithic architecture**: The codebase tends to evolve into a monolithic structure rather than a modular one, making it harder to understand and maintain.

   - **Technology lock-in**: The system becomes tightly coupled to specific technologies or implementations, making it difficult to adopt new technologies or approaches.

   - **Slower development over time**: As the codebase grows, the accumulated technical debt from DIP violations makes development progressively slower and more error-prone.

   - **Difficulty in scaling the team**: New developers struggle to understand the complex web of dependencies, leading to a steeper learning curve and reduced productivity.

9. How do the SOLID principles collectively contribute to creating more maintainable and extensible software?

   **Answer:** The SOLID principles collectively contribute to creating more maintainable and extensible software by addressing different aspects of software design that, when combined, lead to a robust and flexible architecture:

   - **Single Responsibility Principle (SRP)**: By ensuring each class has only one reason to change, SRP creates focused, cohesive components that are easier to understand, test, and maintain. When changes are needed, they affect fewer parts of the system, reducing the risk of introducing bugs.

   - **Open-Closed Principle (OCP)**: By making classes open for extension but closed for modification, OCP allows new functionality to be added without changing existing code. This reduces the risk of regression bugs and makes the system more extensible.

   - **Liskov Substitution Principle (LSP)**: By ensuring that subclasses can be used in place of their parent classes without issues, LSP enables reliable polymorphic behavior. This creates a consistent object hierarchy that's easier to understand and extend.

   - **Interface Segregation Principle (ISP)**: By keeping interfaces focused and client-specific, ISP reduces coupling between components. This makes the system more modular and allows changes to one part without affecting others.

   - **Dependency Inversion Principle (DIP)**: By depending on abstractions rather than concrete implementations, DIP creates a flexible architecture where high-level and low-level modules can evolve independently. This facilitates easier testing, component replacement, and adaptation to changing requirements.

   Together, these principles create a synergistic effect:

   - They promote **high cohesion** (related functionality grouped together) and **low coupling** (minimal dependencies between components), which are fundamental to good software design.

   - They encourage **modularity**, making it easier to understand, test, and modify individual parts of the system without affecting others.

   - They support **scalability** of both the codebase and the development team, as new features can be added with minimal impact on existing code, and new team members can more easily understand isolated components.

   - They facilitate **adaptability** to changing requirements, as the flexible architecture can accommodate new features and modifications with less refactoring.

   - They improve **testability** by creating smaller, focused components with clear responsibilities and dependencies that can be easily mocked or substituted.

10. In what situations might it be acceptable to deviate from strict adherence to the SOLID principles?

    **Answer:** While SOLID principles generally lead to better software design, there are situations where strict adherence might not be practical or beneficial:

    - **Simple applications or scripts**: For small, straightforward applications or scripts with limited scope and lifespan, the overhead of applying all SOLID principles might not be justified. The additional abstractions could add unnecessary complexity.

    - **Performance-critical sections**: In some cases, adhering strictly to principles like SRP or DIP might introduce performance overhead due to additional method calls, object creation, or indirection. In performance-critical sections, some principles might be relaxed for optimization.

    - **Legacy code maintenance**: When working with legacy systems, perfect adherence to SOLID might require extensive refactoring that carries risk and cost. Incremental improvements might be more practical than complete restructuring.

    - **Rapid prototyping**: During initial exploration or proof-of-concept development, focusing on quick iteration might be more important than perfect design. SOLID principles can be applied more rigorously once the core concepts are validated.

    - **Resource constraints**: Projects with severe time, budget, or resource constraints might need to prioritize delivery over perfect design. In such cases, pragmatic compromises might be necessary.

    - **Overengineering concerns**: Sometimes, overzealous application of SOLID can lead to excessive abstraction and indirection, creating "design pattern astronaut" code that's harder to understand than a simpler, more direct approach.

    - **Domain-specific requirements**: Some domains might have unique requirements that don't align perfectly with SOLID. For example, data-intensive applications might benefit from more data-centric designs than strictly object-oriented approaches.

    - **Team expertise**: If the development team isn't familiar with SOLID principles, forcing their strict application might lead to misunderstandings and incorrect implementations. Gradual adoption might be more effective.

    - **External system integration**: When integrating with external systems or libraries that don't follow SOLID, creating adapter layers that perfectly adhere to the principles might add unnecessary complexity.

    - **Stable, unchanging requirements**: If certain parts of the system have requirements that are extremely stable and unlikely to change, the flexibility provided by strict SOLID adherence might not provide sufficient benefit to justify the additional complexity.

    The key is to apply SOLID principles as guidelines rather than rigid rules, making informed decisions about when and how to apply them based on the specific context and constraints of each project.

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

**Answer:** The SOLID principle violations in this code include:

1. **Single Responsibility Principle (SRP) violation**: The `UserService` class has multiple responsibilities - user validation, password hashing, database operations, email sending, and logging.

2. **Dependency Inversion Principle (DIP) violation**: The `UserService` directly instantiates concrete implementations (`MySQLDatabase`, `SmtpEmailSender`, `FileLogger`) rather than depending on abstractions.

3. **Open-Closed Principle (OCP) violation**: The class is not easily extensible for new types of databases, email senders, or loggers without modifying the existing code.

Refactored solution:

```java
// Interfaces for abstractions
public interface Database {
    void execute(String query);
}

public interface EmailSender {
    void sendEmail(String to, String subject, String body);
}

public interface Logger {
    void log(String message);
}

// User model
public class User {
    private String username;
    private String email;
    private String hashedPassword;

    // Constructor, getters, setters
}

// Separate validator
public class UserValidator {
    public void validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
    }

    public void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    public void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
    }
}

// Security service for password handling
public class SecurityService {
    public String hashPassword(String password) {
        // Password hashing logic
        return "hashed_" + password;
    }
}

// User repository for data access
public class UserRepository {
    private Database database;

    public UserRepository(Database database) {
        this.database = database;
    }

    public void saveUser(User user) {
        database.execute("INSERT INTO users (username, email, password) VALUES ('" + 
                         user.getUsername() + "', '" + user.getEmail() + "', '" + 
                         user.getHashedPassword() + "')");
    }

    public void deleteUser(String username) {
        database.execute("DELETE FROM users WHERE username = '" + username + "'");
    }

    public void updateUserEmail(String username, String newEmail) {
        database.execute("UPDATE users SET email = '" + newEmail + "' WHERE username = '" + username + "'");
    }
}

// Notification service
public class NotificationService {
    private EmailSender emailSender;

    public NotificationService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendWelcomeEmail(String email, String username) {
        emailSender.sendEmail(email, "Welcome to our service", 
                             "Dear " + username + ", thank you for registering.");
    }
}

// Logging service
public class LoggingService {
    private Logger logger;

    public LoggingService(Logger logger) {
        this.logger = logger;
    }

    public void logUserRegistration(String username) {
        logger.log("User registered: " + username);
    }

    public void logUserDeletion(String username) {
        logger.log("User deleted: " + username);
    }

    public void logEmailUpdate(String username) {
        logger.log("Email updated for user: " + username);
    }
}

// Refactored UserService that orchestrates the process
public class UserService {
    private UserValidator validator;
    private SecurityService securityService;
    private UserRepository userRepository;
    private NotificationService notificationService;
    private LoggingService loggingService;

    // Constructor with dependency injection
    public UserService(
            UserValidator validator,
            SecurityService securityService,
            UserRepository userRepository,
            NotificationService notificationService,
            LoggingService loggingService) {
        this.validator = validator;
        this.securityService = securityService;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.loggingService = loggingService;
    }

    public void registerUser(String username, String email, String password) {
        // Validate
        validator.validateUsername(username);
        validator.validateEmail(email);
        validator.validatePassword(password);

        // Create user with hashed password
        String hashedPassword = securityService.hashPassword(password);
        User user = new User(username, email, hashedPassword);

        // Save user
        userRepository.saveUser(user);

        // Send welcome email
        notificationService.sendWelcomeEmail(email, username);

        // Log registration
        loggingService.logUserRegistration(username);
    }

    public void deleteUser(String username) {
        userRepository.deleteUser(username);
        loggingService.logUserDeletion(username);
    }

    public void updateUserEmail(String username, String newEmail) {
        validator.validateEmail(newEmail);
        userRepository.updateUserEmail(username, newEmail);
        loggingService.logEmailUpdate(username);
    }
}

// Concrete implementations
public class MySQLDatabase implements Database {
    @Override
    public void execute(String query) {
        // MySQL-specific implementation
    }
}

public class SmtpEmailSender implements EmailSender {
    @Override
    public void sendEmail(String to, String subject, String body) {
        // SMTP-specific implementation
    }
}

public class FileLogger implements Logger {
    @Override
    public void log(String message) {
        // File logging implementation
    }
}
```

This refactoring:
- Separates responsibilities into distinct classes (SRP)
- Depends on abstractions rather than concrete implementations (DIP)
- Makes the system open for extension without modification (OCP)
- Improves testability through dependency injection
- Creates a more modular and maintainable design

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

**Answer:** The SOLID principle violations in this code include:

1. **Single Responsibility Principle (SRP) violation**: The `PaymentProcessor` class handles multiple responsibilities - payment processing, order management, inventory management, invoicing, and notifications.

2. **Open-Closed Principle (OCP) violation**: Adding a new payment method requires modifying the existing code with additional if-else branches.

3. **Dependency Inversion Principle (DIP) violation**: The class works directly with concrete implementations rather than abstractions.

4. **Interface Segregation Principle (ISP) violation**: The `Order` class seems to have methods for all payment types, forcing clients to depend on methods they don't use.

Refactored solution:

```java
// Payment strategy interface
public interface PaymentStrategy {
    void processPayment(Order order, double amount);
    void sendReceipt(String email, double amount);
}

// Concrete payment strategies
public class CreditCardPaymentStrategy implements PaymentStrategy {
    @Override
    public void processPayment(Order order, double amount) {
        validateCreditCard(order.getCreditCardNumber(), order.getCreditCardExpiry(), order.getCreditCardCVV());
        chargeCreditCard(order.getCreditCardNumber(), order.getCreditCardExpiry(), order.getCreditCardCVV(), amount);
    }

    @Override
    public void sendReceipt(String email, double amount) {
        // Send credit card receipt
    }

    private void validateCreditCard(String cardNumber, String expiry, String cvv) {
        // Credit card validation logic
    }

    private void chargeCreditCard(String cardNumber, String expiry, String cvv, double amount) {
        // Credit card charging logic
    }
}

public class PayPalPaymentStrategy implements PaymentStrategy {
    @Override
    public void processPayment(Order order, double amount) {
        initiatePayPalPayment(order.getPayPalEmail(), amount);
        waitForPayPalConfirmation(order.getPayPalEmail());
    }

    @Override
    public void sendReceipt(String email, double amount) {
        // Send PayPal receipt
    }

    private void initiatePayPalPayment(String paypalEmail, double amount) {
        // PayPal initiation logic
    }

    private void waitForPayPalConfirmation(String paypalEmail) {
        // Wait for confirmation logic
    }
}

public class BankTransferPaymentStrategy implements PaymentStrategy {
    private OrderService orderService;

    public BankTransferPaymentStrategy(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void processPayment(Order order, double amount) {
        provideBankDetails(order.getCustomerEmail());
        orderService.markOrderAsPending(order.getId());
        scheduleBankTransferCheck(order.getId(), 3); // Check after 3 days
    }

    @Override
    public void sendReceipt(String email, double amount) {
        // Bank transfers don't send immediate receipts
    }

    private void provideBankDetails(String email) {
        // Provide bank details logic
    }

    private void scheduleBankTransferCheck(String orderId, int days) {
        // Schedule check logic
    }
}

// Payment factory to create appropriate payment strategy
public class PaymentStrategyFactory {
    private OrderService orderService;

    public PaymentStrategyFactory(OrderService orderService) {
        this.orderService = orderService;
    }

    public PaymentStrategy createPaymentStrategy(String paymentMethod) {
        switch (paymentMethod) {
            case "credit_card":
                return new CreditCardPaymentStrategy();
            case "paypal":
                return new PayPalPaymentStrategy();
            case "bank_transfer":
                return new BankTransferPaymentStrategy(orderService);
            default:
                throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
        }
    }
}

// Order service for order-related operations
public interface OrderService {
    void updateOrderStatus(String orderId, String status);
    void markOrderAsPending(String orderId);
}

public class OrderServiceImpl implements OrderService {
    @Override
    public void updateOrderStatus(String orderId, String status) {
        // Update order status logic
    }

    @Override
    public void markOrderAsPending(String orderId) {
        // Mark as pending logic
    }
}

// Inventory service
public interface InventoryService {
    void updateInventory(String productId, int quantity);
}

public class InventoryServiceImpl implements InventoryService {
    @Override
    public void updateInventory(String productId, int quantity) {
        // Update inventory logic
    }
}

// Invoice service
public interface InvoiceService {
    void generateInvoice(Order order);
}

public class InvoiceServiceImpl implements InvoiceService {
    @Override
    public void generateInvoice(Order order) {
        // Generate invoice logic
    }
}

// Notification service
public interface NotificationService {
    void sendOrderConfirmation(String email, String orderId);
}

public class EmailNotificationService implements NotificationService {
    @Override
    public void sendOrderConfirmation(String email, String orderId) {
        // Send confirmation logic
    }
}

// Price calculator
public interface PriceCalculator {
    double calculateTotal(Order order);
}

public class DefaultPriceCalculator implements PriceCalculator {
    @Override
    public double calculateTotal(Order order) {
        double total = 0;
        for (OrderItem item : order.getItems()) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }
}

// Refactored PaymentProcessor
public class PaymentProcessor {
    private PaymentStrategyFactory paymentStrategyFactory;
    private PriceCalculator priceCalculator;
    private OrderService orderService;
    private InventoryService inventoryService;
    private InvoiceService invoiceService;
    private NotificationService notificationService;

    public PaymentProcessor(
            PaymentStrategyFactory paymentStrategyFactory,
            PriceCalculator priceCalculator,
            OrderService orderService,
            InventoryService inventoryService,
            InvoiceService invoiceService,
            NotificationService notificationService) {
        this.paymentStrategyFactory = paymentStrategyFactory;
        this.priceCalculator = priceCalculator;
        this.orderService = orderService;
        this.inventoryService = inventoryService;
        this.invoiceService = invoiceService;
        this.notificationService = notificationService;
    }

    public void processPayment(Order order, String paymentMethod) {
        // Calculate total
        double amount = priceCalculator.calculateTotal(order);

        // Process payment using appropriate strategy
        PaymentStrategy paymentStrategy = paymentStrategyFactory.createPaymentStrategy(paymentMethod);
        paymentStrategy.processPayment(order, amount);
        paymentStrategy.sendReceipt(order.getCustomerEmail(), amount);

        // Update order status (except for bank transfers which are handled by their strategy)
        if (!paymentMethod.equals("bank_transfer")) {
            orderService.updateOrderStatus(order.getId(), "PAID");
        }

        // Update inventory
        for (OrderItem item : order.getItems()) {
            inventoryService.updateInventory(item.getProductId(), item.getQuantity());
        }

        // Generate invoice
        invoiceService.generateInvoice(order);

        // Send notification
        notificationService.sendOrderConfirmation(order.getCustomerEmail(), order.getId());
    }
}
```

This refactoring:
- Separates responsibilities into distinct services (SRP)
- Uses the Strategy pattern to handle different payment methods (OCP)
- Depends on abstractions rather than concrete implementations (DIP)
- Creates focused interfaces for each service (ISP)
- Makes the system extensible for new payment methods without modifying existing code
- Improves testability through dependency injection
- Creates a more modular and maintainable design
