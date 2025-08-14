# DRY Principle Knowledge Test

## Multiple Choice Questions

1. What does DRY stand for in software development?
   a) Do Repeat Yourself
   b) Don't Repeat Yourself
   c) Design Reusable Yield
   d) Develop Robust Yielding

2. According to the DRY principle, every piece of knowledge should have:
   a) Multiple implementations for flexibility
   b) A single, unambiguous, authoritative representation
   c) At least two different implementations for redundancy
   d) Separate representations for different parts of the system

3. Which of the following is NOT a common DRY violation?
   a) Copy-pasted code
   b) Duplicated logic
   c) Creating reusable utility methods
   d) Repeated string literals

4. What is the primary goal of the DRY principle?
   a) To make code more complex
   b) To reduce code size at all costs
   c) To reduce repetition of code and logic
   d) To eliminate all helper methods

5. Which of the following is a benefit of following the DRY principle?
   a) Increased maintenance effort
   b) More places to fix bugs
   c) Improved code readability
   d) More opportunities for inconsistencies

## Short Answer Questions

6. Explain how the DRY principle relates to the concept of "single source of truth" in software development.

7. Describe two specific strategies you can use to eliminate code duplication in a project.

8. What are the potential negative consequences of violating the DRY principle in a large codebase?

9. How does the DRY principle contribute to easier testing of code?

10. In what situations might it be acceptable to have some duplication rather than strictly adhering to DRY?

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