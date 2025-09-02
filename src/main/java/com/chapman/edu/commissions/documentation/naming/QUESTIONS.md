# Meaningful Naming Conventions Knowledge Test

## Multiple Choice Questions

1. What is the primary goal of meaningful naming in software development?
   a) To make variable names as short as possible
   b) To reduce cognitive load and make code self-documenting
   c) To follow company naming standards regardless of clarity
   d) To use technical jargon to show expertise

2. Which variable name best represents a commission calculation result?
   a) `result`
   b) `calc`
   c) `commissionAmount`
   d) `x`

3. What makes a function name meaningful?
   a) It's short and concise
   b) It clearly describes what the function does and returns
   c) It uses abbreviations to save space
   d) It includes the parameter types

4. Which naming convention reduces cognitive load the most?
   a) Using single-letter variables everywhere
   b) Using consistent patterns and descriptive names
   c) Using the longest possible names
   d) Using numbers in variable names

5. What is wrong with this method name: `processData()`?
   a) It's too short
   b) It's too vague and doesn't specify what processing is done
   c) It uses camelCase
   d) Nothing is wrong with it

## Short Answer Questions

6. Explain how meaningful naming conventions contribute to code maintainability and team collaboration.

7. Describe the difference between naming for "what" versus naming for "why" and provide examples.

8. What are the characteristics of a well-named boolean variable or method?

9. How do consistent naming patterns across a codebase improve developer productivity?

10. What strategies can you use to name variables in complex algorithms while maintaining clarity?

## Code Analysis Questions

11. Analyze the following code and identify naming issues:

```java
public class CP {
    private String n;
    private BigDecimal r;
    private boolean a;
    
    public BigDecimal calc(BigDecimal amt, User u) {
        if (!a || amt == null) return BigDecimal.ZERO;
        
        BigDecimal result = amt.multiply(r);
        if (u.getRole().equals("SR")) {
            result = result.multiply(new BigDecimal("1.1"));
        }
        return result;
    }
    
    public boolean check(User u) {
        return u.getStartDate().isBefore(LocalDate.now().minusYears(2));
    }
}
```

12. Rewrite this poorly named code with meaningful names:

```java
public void process(List<Object> data) {
    for (Object item : data) {
        Deal d = (Deal) item;
        if (d.getStatus() == 1) {
            BigDecimal val = d.getValue();
            if (val.compareTo(new BigDecimal("50000")) > 0) {
                d.setFlag(true);
                notify(d);
            }
        }
    }
}

private void notify(Deal d) {
    System.out.println("Alert: " + d.getId());
}
```

13. Improve the naming in this commission calculation method:

```java
public BigDecimal calculate(Deal deal, User user) {
    BigDecimal base = deal.getValue().multiply(new BigDecimal("0.05"));
    
    // Check for bonus eligibility
    boolean eligible = user.getSalesCount() > 10 && 
                      user.getPerformanceRating() >= 4.0;
    
    if (eligible) {
        BigDecimal bonus = base.multiply(new BigDecimal("0.2"));
        base = base.add(bonus);
    }
    
    // Apply territory multiplier
    String territory = user.getTerritory();
    if ("WEST".equals(territory)) {
        base = base.multiply(new BigDecimal("1.1"));
    } else if ("EAST".equals(territory)) {
        base = base.multiply(new BigDecimal("1.05"));
    }
    
    return base;
}
```

## Practical Application Questions

14. You're working on a complex financial calculation with multiple intermediate steps. How would you approach naming variables to make the calculation logic clear to other developers?

15. Your team is building a user management system with different types of users (customers, employees, administrators). How would you structure your naming conventions to clearly distinguish between these types throughout the codebase?

16. You need to refactor a legacy method called `doStuff()` that handles deal approval workflows. What questions would you ask to determine a better name, and what naming pattern would you follow?

17. A junior developer on your team is using abbreviations extensively (e.g., `usr`, `calc`, `proc`). How would you guide them toward better naming practices while maintaining code consistency?

18. You're implementing a commission calculation system with multiple business rules and edge cases. How would you name methods and variables to make the business logic self-documenting and reduce the need for comments?

## Pattern Recognition Questions

19. Identify the naming pattern issues in this class hierarchy:

```java
public abstract class BaseEntity {
    protected String identifier;
    protected LocalDateTime createdAt;
}

public class User extends BaseEntity {
    private String userName;
    private String emailAddr;
    private Set<Role> userRoles;
}

public class Deal extends BaseEntity {
    private String dealTitle;
    private BigDecimal dealValue;
    private String salesRepID;
}

public class CommissionCalc extends BaseEntity {
    private String dealId;
    private BigDecimal commissionAmt;
    private String calculatedBy;
}
```

20. Design a consistent naming convention for this commission system that includes:
    - Entity classes (User, Deal, Commission)
    - Service classes (calculation, validation, notification)
    - Utility methods (formatting, conversion, validation)
    - Constants (rates, thresholds, status values)
    - Boolean methods and variables