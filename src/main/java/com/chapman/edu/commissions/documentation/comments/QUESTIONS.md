# Code Comments Best Practices Knowledge Test

## Multiple Choice Questions

1. What is the primary purpose of code comments according to best practices?
   a) To explain what every line of code does
   b) To explain WHY decisions were made, not WHAT the code does
   c) To make the code longer and more detailed
   d) To replace proper variable and method naming

2. Which of the following should NOT be explained in comments?
   a) Complex business rules and edge cases
   b) Workarounds for third-party library limitations
   c) What a method named `calculateTotalPrice()` does
   d) API contract assumptions and requirements

3. When should you comment obvious operations?
   a) Always, for completeness
   b) Never, it creates noise
   c) Only when the operation is complex
   d) Only for junior developers

4. What type of information is most valuable in comments?
   a) Step-by-step code execution
   b) Business context and requirements
   c) Variable type information
   d) Method parameter descriptions

5. Which comment provides the most value?
   a) `// Increment counter by 1`
   b) `// Loop through all users`
   c) `// Using deprecated API due to client requirement - remove after Q2 2024 migration`
   d) `// This method returns a boolean`

## Short Answer Questions

6. Explain the difference between comments that explain "WHAT" versus "WHY" and provide examples of each.

7. Describe three specific scenarios where comments are essential and cannot be replaced by better code structure.

8. How do poorly written comments negatively impact code maintainability?

9. What is the relationship between good naming conventions and the need for comments?

10. When working with complex algorithms, what specific information should comments provide?

## Code Analysis Questions

11. Analyze the following code and identify which comments are valuable and which should be removed:

```java
public class CommissionCalculator {
    
    // This method calculates commission
    public BigDecimal calculateCommission(Deal deal, CommissionPlan plan) {
        // Get the deal value
        BigDecimal dealValue = deal.getValue();
        
        // Check if deal value is null
        if (dealValue == null) {
            // Return zero if null
            return BigDecimal.ZERO;
        }
        
        // Apply 15% penalty for deals closed after quarter end
        // per CFO directive to discourage sandbagging
        if (isAfterQuarterEnd(deal.getCloseDate())) {
            dealValue = dealValue.multiply(new BigDecimal("0.85"));
        }
        
        // Calculate base commission
        BigDecimal baseRate = plan.getBaseRate();
        return dealValue.multiply(baseRate);
    }
    
    // This method checks if date is after quarter end
    private boolean isAfterQuarterEnd(LocalDate date) {
        // Implementation here
        return false;
    }
}
```

12. Rewrite the following over-commented code to follow best practices:

```java
/**
 * User class represents a user
 */
public class User {
    // User ID field
    private String id;
    // User name field  
    private String name;
    // User email field
    private String email;
    
    /**
     * Constructor that creates a new user
     * @param id the user id
     * @param name the user name
     * @param email the user email
     */
    public User(String id, String name, String email) {
        // Set the id field to the id parameter
        this.id = id;
        // Set the name field to the name parameter
        this.name = name;
        // Set the email field to the email parameter
        this.email = email;
    }
    
    /**
     * Gets the user ID
     * @return the user ID
     */
    public String getId() {
        // Return the id field
        return id;
    }
}
```

13. Add appropriate comments to this code that lacks important context:

```java
public class DealProcessor {
    
    public void processDeal(Deal deal) {
        if (deal.getValue().compareTo(new BigDecimal("50000")) > 0) {
            deal.setStatus(DealStatus.REQUIRES_APPROVAL);
            notifyManager(deal);
        }
        
        if (deal.getCloseDate().isAfter(getQuarterEnd())) {
            applyLatePenalty(deal);
        }
        
        Thread.sleep(2000);
        
        validateWithCRM(deal);
    }
    
    private void applyLatePenalty(Deal deal) {
        BigDecimal penalty = deal.getValue().multiply(new BigDecimal("0.1"));
        deal.setValue(deal.getValue().subtract(penalty));
    }
}
```

## Practical Application Questions

14. Your team has a complex commission calculation algorithm with multiple edge cases. How would you approach commenting this code to ensure future maintainability?

15. You discover a workaround in the codebase for a third-party API limitation. What information should you include in comments to help future developers?

16. A junior developer on your team is adding comments to every line of code. How would you guide them toward better commenting practices?

17. You're reviewing code that has no comments but uses cryptic variable names and complex logic. What's the best approach to improve this code's maintainability?

18. How would you comment code that implements a specific business rule that might seem arbitrary to developers unfamiliar with the domain?