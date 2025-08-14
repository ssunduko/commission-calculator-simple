# KISS Principle Knowledge Test

## Multiple Choice Questions

1. What does KISS stand for in software development?
   a) Keep It Super Simple
   b) Keep It Simple, Stupid
   c) Keep Implementation Short and Sweet
   d) Knowledge In Software Systems

2. According to the KISS principle, what should be a key goal in design?
   a) Maximum flexibility
   b) Extensive feature set
   c) Simplicity
   d) Clever code optimization

3. Which of the following is NOT a common KISS violation?
   a) Overly complex algorithms
   b) Excessive abstraction
   c) Writing clear, straightforward code
   d) Premature optimization

4. What is the primary goal of the KISS principle?
   a) To make code as short as possible
   b) To reduce the number of files in a project
   c) To make systems easier to understand and maintain
   d) To eliminate all design patterns

5. Which of the following is a benefit of following the KISS principle?
   a) More impressive code to show off to colleagues
   b) Reduced bugs
   c) More opportunities to use advanced language features
   d) Increased job security due to complex code

## Short Answer Questions

6. Explain how the KISS principle relates to code maintainability.

7. Describe two specific strategies you can use to simplify overly complex code.

8. What are the potential negative consequences of violating the KISS principle in a large codebase?

9. How does the KISS principle contribute to better team collaboration?

10. In what situations might it be acceptable to implement a more complex solution rather than the simplest one?

## Code Analysis Questions

11. Identify the KISS violations in the following code snippet and explain how you would simplify it:
```java
public boolean isEligibleForDiscount(Customer customer) {
    boolean isEligible = false;
    if (customer != null) {
        if (customer.getPurchaseHistory() != null) {
            if (customer.getPurchaseHistory().getTotalPurchases() > 0) {
                double totalAmount = 0;
                for (Purchase purchase : customer.getPurchaseHistory().getPurchases()) {
                    if (purchase.getAmount() > 0 && purchase.getStatus().equals("COMPLETED")) {
                        totalAmount += purchase.getAmount();
                    }
                }
                if (totalAmount > 1000) {
                    if (customer.getLoyaltyYears() > 2) {
                        isEligible = true;
                    } else {
                        if (customer.getCategory().equals("PREMIUM")) {
                            isEligible = true;
                        } else {
                            isEligible = false;
                        }
                    }
                }
            }
        }
    }
    return isEligible;
}
```

12. How would you apply the KISS principle to improve this date validation code?
```java
public boolean isValidDate(String dateStr) {
    if (dateStr == null || dateStr.length() != 10) {
        return false;
    }
    
    char separator = dateStr.charAt(4);
    if (separator != '-') {
        return false;
    }
    
    if (dateStr.charAt(7) != '-') {
        return false;
    }
    
    String yearStr = dateStr.substring(0, 4);
    String monthStr = dateStr.substring(5, 7);
    String dayStr = dateStr.substring(8, 10);
    
    int year;
    int month;
    int day;
    
    try {
        year = Integer.parseInt(yearStr);
        month = Integer.parseInt(monthStr);
        day = Integer.parseInt(dayStr);
    } catch (NumberFormatException e) {
        return false;
    }
    
    if (year < 1900 || year > 2100) {
        return false;
    }
    
    if (month < 1 || month > 12) {
        return false;
    }
    
    int maxDay = 31;
    if (month == 4 || month == 6 || month == 9 || month == 11) {
        maxDay = 30;
    } else if (month == 2) {
        boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
        maxDay = isLeapYear ? 29 : 28;
    }
    
    if (day < 1 || day > maxDay) {
        return false;
    }
    
    return true;
}
```