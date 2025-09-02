# Code Comments Best Practices

## Overview
Effective code commenting is about explaining **WHY** decisions were made, not **WHAT** the code does. Good comments provide business context, document non-obvious requirements, and help future developers understand the reasoning behind implementation choices.

## The Golden Rule
**Comments should explain WHY, not WHAT**

Well-written code should be self-documenting for what it does. Comments add the most value when they explain the reasoning, business context, and non-obvious decisions that aren't apparent from reading the code itself.

## What Comments Should Explain

### 1. Business Context and Requirements
```java
// Apply 15% penalty for deals closed after quarter end
// per CFO directive to discourage revenue manipulation and sandbagging
if (isAfterQuarterEnd(deal.getCloseDate())) {
    dealValue = dealValue.multiply(new BigDecimal("0.85"));
}
```

### 2. Complex Algorithms and Edge Cases
```java
// Handle leap year edge case in quarterly commission calculations
// February 29th should be treated as February 28th for commission periods
// to maintain consistent 90-day quarters across all years
if (isLeapYear && date.getMonthValue() == 2 && date.getDayOfMonth() == 29) {
    date = date.withDayOfMonth(28);
}
```

### 3. Workarounds and Temporary Solutions
```java
// WORKAROUND: CRM system has 2-second processing delay due to legacy database
// Remove this sleep after CRM upgrade scheduled for Q3 2024 (JIRA-5678)
// Performance impact: adds ~2 seconds per deal (acceptable for current volume)
Thread.sleep(2000);
```

### 4. API Contracts and Assumptions
```java
// Validates customer exists in Salesforce and deal data matches
// May throw ValidationException if customer data doesn't match CRM records
// Critical for maintaining data integrity across systems
validateWithCRM(deal);
```

### 5. Non-Obvious Business Rules
```java
// $100K threshold set by CFO to ensure proper revenue recognition
// High-value deals require additional finance team review per SOX compliance
if (deal.getValue().compareTo(new BigDecimal("100000")) > 0) {
    sendToFinanceTeam(deal);
}
```

## What Comments Should NOT Explain

### 1. Obvious Operations
```java
// BAD: States the obvious
count++; // Increment count by 1
user.setName(name); // Set the user name
```

### 2. What Well-Named Functions Do
```java
// BAD: Method name is self-explanatory
calculateTotalPrice(); // Calculate the total price
```

### 3. Information Available in Type Signatures
```java
// BAD: Parameter types and return values are obvious
/**
 * Gets the user ID
 * @param user the user object
 * @return the user ID string
 */
public String getUserId(User user) {
    return user.getId();
}
```

### 4. Step-by-Step Code Execution
```java
// BAD: Explains what each line does
// Get the deal value
BigDecimal dealValue = deal.getValue();
// Check if null
if (dealValue == null) {
    // Return zero
    return BigDecimal.ZERO;
}
```

## Examples in this Directory

This directory contains examples of poor and good commenting practices:

- **Original**: Contains code with **violations** of good commenting practices - over-commented code that explains obvious operations and creates noise
- **Fixed**: Contains the **corrected versions** with properly commented code that focuses on business context and non-obvious decisions

Each subdirectory contains its own README.md file with more detailed explanations of the violations and fixes.

## Common Comment Anti-Patterns

### 1. Obvious Comments
Comments that restate what the code clearly shows:
```java
// BAD
i++; // Increment i
```

### 2. Outdated Comments
Comments that no longer match the code:
```java
// BAD: Comment says "add" but code multiplies
// Add the values together
return a * b;
```

### 3. Redundant JavaDoc
Documentation that just restates method signatures:
```java
// BAD
/**
 * Sets the name
 * @param name the name to set
 */
public void setName(String name) {
    this.name = name;
}
```

### 4. Noise Comments
Comments that add no value:
```java
// BAD
// Default constructor
public User() {
}
```

## Benefits of Good Commenting Practices

### 1. **Improved Maintainability**
- Future developers understand WHY decisions were made
- Business context prevents accidental "improvements" that break requirements
- Reduces time spent deciphering complex logic

### 2. **Better Knowledge Transfer**
- New team members can understand business rules quickly
- Domain knowledge is preserved when team members leave
- Reduces dependency on tribal knowledge

### 3. **Reduced Debugging Time**
- Comments explain expected behavior and edge cases
- Workarounds are documented with context for removal
- Business rules are clear, preventing incorrect "fixes"

### 4. **Enhanced Code Reviews**
- Reviewers can focus on logic rather than trying to understand purpose
- Business context helps identify potential issues
- Comments facilitate discussions about requirements

## Guidelines for Writing Effective Comments

### 1. Focus on Intent and Context
Explain the business reasoning behind code decisions:
```java
// GOOD: Explains business context
// Commission rate reduced by 50% for deals closed after fiscal year end
// per Sarbanes-Oxley compliance requirement - Finance Dept approval required to change
```

### 2. Document Workarounds with Timelines
Include removal plans and context:
```java
// GOOD: Complete workaround documentation
// HACK: Third-party API rate limit workaround
// TODO: Replace with bulk API after vendor upgrade (Q2 2024)
// Contact: vendor-support@example.com
```

### 3. Explain Complex Algorithms
Provide high-level algorithm description and edge cases:
```java
// GOOD: Algorithm explanation
// Implements progressive commission calculation:
// 0-50K: 5%, 50K-100K: 7%, 100K+: 10%
// Edge case: Refunds apply inverse rates with 50% cap
```

### 4. Reference External Documentation
Link to specifications and policies:
```java
// GOOD: External reference
// Algorithm based on "Sales Commission Policy v3.2"
// See: https://company.wiki/policies/sales-commission-2024
```

## When NOT to Comment

### Self-Documenting Code
Well-named variables and methods often eliminate the need for comments:
```java
// Instead of commenting unclear code:
// Calculate 10% commission on sales amount
double c = s * 0.1;

// Write self-documenting code:
BigDecimal commission = salesAmount.multiply(COMMISSION_RATE);
```

### Obvious Operations
Standard programming constructs don't need explanation:
```java
// Don't comment obvious operations
for (User user : users) {          // No comment needed
    if (user.isActive()) {         // No comment needed
        processUser(user);         // No comment needed
    }
}
```

## Testing Your Comments

Ask yourself these questions:
1. **Does this comment explain WHY, not WHAT?**
2. **Would the code be unclear without this comment?**
3. **Does this comment provide business context?**
4. **Will this comment become outdated when code changes?**
5. **Could better naming eliminate the need for this comment?**

If you answer "no" to questions 1-3 or "yes" to questions 4-5, consider removing or improving the comment.

## Remember
Good comments are like good documentation - they provide context and reasoning that isn't obvious from the code itself. They should enhance understanding, not create noise. When in doubt, focus on explaining the business "why" rather than the technical "what".
