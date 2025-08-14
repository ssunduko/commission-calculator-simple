# DRY Principle Knowledge Test - Answers

## Multiple Choice Questions

1. What does DRY stand for in software development?
   **Answer: b) Don't Repeat Yourself**
   
   DRY is a fundamental principle in software development that aims to reduce repetition of code and information.

2. According to the DRY principle, every piece of knowledge should have:
   **Answer: b) A single, unambiguous, authoritative representation**
   
   This is the core concept of DRY - each piece of knowledge or logic should exist in only one place in your codebase.

3. Which of the following is NOT a common DRY violation?
   **Answer: c) Creating reusable utility methods**
   
   Creating reusable utility methods is actually a way to implement the DRY principle, not a violation of it. The other options represent common violations.

4. What is the primary goal of the DRY principle?
   **Answer: c) To reduce repetition of code and logic**
   
   The main purpose of DRY is to eliminate duplication, making code more maintainable and less prone to errors.

5. Which of the following is a benefit of following the DRY principle?
   **Answer: c) Improved code readability**
   
   Following DRY leads to more concise, focused code that is easier to read and understand. The other options are negative outcomes that DRY helps to avoid.

## Short Answer Questions

6. Explain how the DRY principle relates to the concept of "single source of truth" in software development.

   **Answer:** The DRY principle and the concept of "single source of truth" are closely related. DRY states that every piece of knowledge should have a single, unambiguous, authoritative representation within a system. Similarly, "single source of truth" means that data should be stored exactly once in a system. Both principles aim to eliminate duplication and inconsistency. When you follow DRY, you naturally create a single source of truth for each piece of logic or knowledge in your codebase, ensuring that changes need to be made in only one place, reducing the risk of inconsistencies and making maintenance easier.

7. Describe two specific strategies you can use to eliminate code duplication in a project.

   **Answer:** 
   1. **Abstraction through methods/functions:** Extract duplicated code into reusable methods or functions that can be called from multiple places. This is particularly useful for common operations or calculations.
   
   2. **Inheritance and composition:** Use object-oriented principles to create base classes or components that contain common functionality, which can then be inherited or composed by more specific classes. This allows shared behavior to be defined once and reused across multiple related classes.
   
   Other strategies include using templates/generics for type-independent code, creating utility classes for common operations, and implementing design patterns like Strategy or Template Method to encapsulate varying behavior while keeping common code in one place.

8. What are the potential negative consequences of violating the DRY principle in a large codebase?

   **Answer:** Violating the DRY principle in a large codebase can lead to several serious problems:
   
   - **Maintenance nightmare:** Changes need to be made in multiple places, increasing the risk of missing some instances.
   - **Inconsistent behavior:** When logic is duplicated and one instance is updated but others are not, the system behaves inconsistently.
   - **Increased bug potential:** Each duplicate is an opportunity for bugs to be introduced during maintenance.
   - **Bloated codebase:** Unnecessary duplication makes the codebase larger and harder to understand.
   - **Higher cognitive load:** Developers need to remember all the places where similar code exists.
   - **Increased testing burden:** More code means more test cases needed to ensure proper functionality.
   - **Slower development:** More time is spent maintaining duplicated code rather than adding new features.

9. How does the DRY principle contribute to easier testing of code?

   **Answer:** The DRY principle contributes to easier testing in several ways:
   
   - **Less code to test:** When code is not duplicated, there are fewer code paths to test.
   - **Centralized logic:** Common functionality is centralized, so you can focus testing efforts on these core components.
   - **Better abstraction:** DRY encourages proper abstraction, which typically results in more modular code that's easier to test in isolation.
   - **Reduced test duplication:** Just as code duplication is reduced, test duplication is also minimized.
   - **Higher test coverage with less effort:** With less code to test, you can achieve higher test coverage with the same amount of testing effort.
   - **More reliable tests:** When a bug is fixed in one place, all uses of that code benefit, reducing the chance of inconsistent test results.

10. In what situations might it be acceptable to have some duplication rather than strictly adhering to DRY?

    **Answer:** While DRY is generally a good principle to follow, there are situations where some duplication might be acceptable:
    
    - **When over-abstraction would lead to more complexity:** Sometimes, creating an abstraction to eliminate minor duplication can lead to overly complex code that's harder to understand than the duplication itself.
    - **When the duplicated code is likely to evolve in different directions:** If two similar pieces of code are likely to change in different ways in the future, forcing them into a shared abstraction might create more problems than it solves.
    - **For performance-critical sections:** In some cases, inlining code (duplicating it) can lead to better performance than calling a shared method.
    - **When the cost of change is low and the likelihood of change is low:** If the duplicated code is simple, unlikely to change, and easy to update if needed, the benefits of DRY might not justify the effort.
    - **For improved readability:** Sometimes having complete, self-contained code in one place is easier to understand than code split across multiple abstractions.
    - **When the duplication is coincidental:** If two pieces of code look similar but represent different concepts or are likely to diverge, forcing them to share code might be inappropriate.

## Code Analysis Questions

11. Identify the DRY violations in the following code snippet and explain how you would refactor it:

```java
public void processCustomerPremium(Customer customer) {
    double discount = 0;
    if (customer.getLoyaltyYears() > 5) {
        discount = 0.1;
    } else if (customer.getLoyaltyYears() > 2) {
        discount = 0.05;
    }
    double finalPrice = customer.getBasePremium() * (1 - discount);
    System.out.println("Customer premium: $" + finalPrice);
}

public void processEmployeePremium(Employee employee) {
    double discount = 0;
    if (employee.getLoyaltyYears() > 5) {
        discount = 0.1;
    } else if (employee.getLoyaltyYears() > 2) {
        discount = 0.05;
    }
    double finalPrice = employee.getBasePremium() * (1 - discount);
    System.out.println("Employee premium: $" + finalPrice);
}
```

**Answer:** The DRY violations in this code include:

1. Duplicated discount calculation logic based on loyalty years
2. Duplicated final price calculation formula
3. Similar output formatting

Refactored solution:

```java
// Create an interface or abstract class that both Customer and Employee implement/extend
public interface PremiumEntity {
    int getLoyaltyYears();
    double getBasePremium();
    String getType(); // Returns "Customer" or "Employee"
}

// Implement the interface in both classes
// Customer implements PremiumEntity
// Employee implements PremiumEntity

// Create a single method to process premiums
public void processPremium(PremiumEntity entity) {
    double discount = calculateLoyaltyDiscount(entity.getLoyaltyYears());
    double finalPrice = calculateFinalPrice(entity.getBasePremium(), discount);
    System.out.println(entity.getType() + " premium: $" + finalPrice);
}

private double calculateLoyaltyDiscount(int loyaltyYears) {
    if (loyaltyYears > 5) {
        return 0.1;
    } else if (loyaltyYears > 2) {
        return 0.05;
    }
    return 0;
}

private double calculateFinalPrice(double basePremium, double discount) {
    return basePremium * (1 - discount);
}
```

This refactoring:
- Extracts the discount calculation into a separate method
- Extracts the price calculation into a separate method
- Creates a common interface for both entity types
- Provides a single method to process premiums for any entity type

12. How would you apply the DRY principle to improve this validation code?

```java
public void validateCustomer(Customer customer) {
    if (customer.getName() == null || customer.getName().isEmpty()) {
        throw new ValidationException("Name cannot be empty");
    }
    if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
        throw new ValidationException("Email cannot be empty");
    }
    if (!customer.getEmail().contains("@")) {
        throw new ValidationException("Invalid email format");
    }
}

public void validateSupplier(Supplier supplier) {
    if (supplier.getName() == null || supplier.getName().isEmpty()) {
        throw new ValidationException("Name cannot be empty");
    }
    if (supplier.getEmail() == null || supplier.getEmail().isEmpty()) {
        throw new ValidationException("Email cannot be empty");
    }
    if (!supplier.getEmail().contains("@")) {
        throw new ValidationException("Invalid email format");
    }
    if (supplier.getTaxId() == null || supplier.getTaxId().isEmpty()) {
        throw new ValidationException("Tax ID cannot be empty");
    }
}
```

**Answer:** The DRY violations in this code include:

1. Duplicated name validation logic
2. Duplicated email existence validation logic
3. Duplicated email format validation logic

Refactored solution:

```java
// Create an interface for entities that need validation
public interface ValidatableEntity {
    String getName();
    String getEmail();
}

// Both Customer and Supplier would implement this interface
// Customer implements ValidatableEntity
// Supplier implements ValidatableEntity

// Create utility methods for common validations
private void validateNotEmpty(String value, String fieldName) {
    if (value == null || value.isEmpty()) {
        throw new ValidationException(fieldName + " cannot be empty");
    }
}

private void validateEmailFormat(String email) {
    if (!email.contains("@")) {
        throw new ValidationException("Invalid email format");
    }
}

// Create a base validation method for common validations
private void validateCommonFields(ValidatableEntity entity) {
    validateNotEmpty(entity.getName(), "Name");
    validateNotEmpty(entity.getEmail(), "Email");
    validateEmailFormat(entity.getEmail());
}

// Specific validation methods now only handle unique validations
public void validateCustomer(Customer customer) {
    validateCommonFields(customer);
    // Add any customer-specific validations here
}

public void validateSupplier(Supplier supplier) {
    validateCommonFields(supplier);
    validateNotEmpty(supplier.getTaxId(), "Tax ID");
    // Add any other supplier-specific validations here
}
```

This refactoring:
- Creates utility methods for common validation logic
- Establishes a common interface for entities requiring validation
- Creates a base validation method for shared validations
- Keeps entity-specific validation methods focused only on unique requirements