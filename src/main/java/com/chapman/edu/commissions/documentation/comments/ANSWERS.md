# Code Comments Best Practices Knowledge Test - Answers

## Multiple Choice Questions

1. What is the primary purpose of code comments according to best practices?
   **Answer: b) To explain WHY decisions were made, not WHAT the code does**
   
   Good comments explain the reasoning behind code decisions, business context, and non-obvious requirements. The code itself should be self-documenting for what it does.

2. Which of the following should NOT be explained in comments?
   **Answer: c) What a method named `calculateTotalPrice()` does**
   
   Well-named methods are self-documenting. Comments should focus on business context, edge cases, and non-obvious decisions rather than restating what clear method names already convey.

3. When should you comment obvious operations?
   **Answer: b) Never, it creates noise**
   
   Commenting obvious operations adds clutter and can become outdated. Focus comments on non-obvious business logic, edge cases, and important context.

4. What type of information is most valuable in comments?
   **Answer: b) Business context and requirements**
   
   Business context helps developers understand why code exists and how it fits into larger requirements, which is often not apparent from the code alone.

5. Which comment provides the most value?
   **Answer: c) `// Using deprecated API due to client requirement - remove after Q2 2024 migration`**
   
   This comment explains WHY a seemingly poor decision was made, provides business context, and includes actionable information for future maintenance.

## Short Answer Questions

6. Explain the difference between comments that explain "WHAT" versus "WHY" and provide examples of each.

   **Answer:**
   
   **WHAT comments** describe what the code is doing - these are generally unnecessary because well-written code should be self-explanatory:
   ```java
   // BAD: Explains WHAT
   // Loop through all users
   for (User user : users) {
       // Check if user is active
       if (user.isActive()) {
           // Add user to active list
           activeUsers.add(user);
       }
   }
   ```
   
   **WHY comments** explain the reasoning, business context, or non-obvious decisions:
   ```java
   // GOOD: Explains WHY
   // Filter to active users only - inactive users should not receive commission
   // calculations per compliance requirement SEC-2023-001
   for (User user : users) {
       if (user.isActive()) {
           activeUsers.add(user);
       }
   }
   ```
   
   WHY comments provide context that helps future developers understand the purpose and constraints behind the code.

7. Describe three specific scenarios where comments are essential and cannot be replaced by better code structure.

   **Answer:**
   
   1. **Business Rules and Compliance Requirements:**
   ```java
   // Commission rate reduced by 50% for deals closed after fiscal year end
   // per Sarbanes-Oxley compliance requirement - Finance Dept approval required to change
   if (isAfterFiscalYearEnd(deal.getCloseDate())) {
       commissionRate = commissionRate.multiply(new BigDecimal("0.5"));
   }
   ```
   
   2. **Workarounds and Technical Debt:**
   ```java
   // HACK: CRM API has 2-second rate limit, sleep prevents 429 errors
   // TODO: Implement proper retry mechanism with exponential backoff (JIRA-1234)
   Thread.sleep(2000);
   ```
   
   3. **Complex Algorithms and Edge Cases:**
   ```java
   // Handle leap year edge case in quarterly commission calculations
   // February 29th should be treated as February 28th for commission periods
   // to maintain consistent 90-day quarters across all years
   if (isLeapYear && date.getMonthValue() == 2 && date.getDayOfMonth() == 29) {
       date = date.withDayOfMonth(28);
   }
   ```

8. How do poorly written comments negatively impact code maintainability?

   **Answer:** Poorly written comments harm maintainability in several ways:
   
   - **Outdated information:** Comments that don't match the current code mislead developers and create confusion
   - **Noise and clutter:** Obvious comments make it harder to find truly important information
   - **False sense of security:** Developers may rely on incorrect comments instead of reading the actual code
   - **Maintenance burden:** Unnecessary comments require updates when code changes, adding to development overhead
   - **Reduced readability:** Too many comments can make code harder to scan and understand
   - **Inconsistent information:** When comments contradict the code, developers waste time figuring out which is correct
   
   Example of harmful commenting:
   ```java
   // This method adds two numbers (WRONG - it actually multiplies)
   public int calculate(int a, int b) {
       return a * b; // Code was changed but comment wasn't updated
   }
   ```

9. What is the relationship between good naming conventions and the need for comments?

   **Answer:** Good naming conventions significantly reduce the need for comments by making code self-documenting:
   
   **Poor naming requiring comments:**
   ```java
   // Calculate commission with bonus multiplier
   public BigDecimal calc(BigDecimal amt, BigDecimal mult) {
       return amt.multiply(mult);
   }
   ```
   
   **Good naming eliminating need for comments:**
   ```java
   public BigDecimal calculateCommissionWithBonus(BigDecimal baseAmount, BigDecimal bonusMultiplier) {
       return baseAmount.multiply(bonusMultiplier);
   }
   ```
   
   **The relationship:**
   - Descriptive names explain WHAT the code does
   - Comments should focus on WHY and business context
   - Better naming reduces comment maintenance burden
   - Self-documenting code is more reliable than comments
   - Good naming + targeted comments = optimal maintainability

10. When working with complex algorithms, what specific information should comments provide?

    **Answer:** For complex algorithms, comments should provide:
    
    1. **High-level algorithm description:**
    ```java
    // Implements tiered commission calculation using progressive rate structure
    // Rate increases at $50K, $100K, and $250K thresholds
    ```
    
    2. **Mathematical formulas and business rules:**
    ```java
    // Formula: base_rate + (excess_over_threshold * tier_multiplier)
    // Tier 1: 0-50K at 5%, Tier 2: 50K-100K at 7%, Tier 3: 100K+ at 10%
    ```
    
    3. **Edge cases and special handling:**
    ```java
    // Handle negative deal values (refunds) - apply inverse commission calculation
    // but cap maximum clawback at 50% of rep's quarterly earnings per policy
    ```
    
    4. **Performance considerations:**
    ```java
    // Using BigDecimal for precision - financial calculations require exact arithmetic
    // Performance impact acceptable given regulatory requirements for accuracy
    ```
    
    5. **References to external documentation:**
    ```java
    // Algorithm based on "Progressive Commission Structure v2.1" 
    // See: https://company.wiki/finance/commission-structure-2024
    ```

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

**Answer:**

**Comments to REMOVE (explain obvious WHAT):**
- `// This method calculates commission` - method name is self-explanatory
- `// Get the deal value` - obvious from the code
- `// Check if deal value is null` - obvious from the if statement
- `// Return zero if null` - obvious from the return statement
- `// Calculate base commission` - obvious from the variable names
- `// This method checks if date is after quarter end` - method name is clear

**Comments to KEEP (explain valuable WHY/context):**
- `// Apply 15% penalty for deals closed after quarter end per CFO directive to discourage sandbagging` - provides business context and reasoning

**Improved version:**
```java
public class CommissionCalculator {
    
    public BigDecimal calculateCommission(Deal deal, CommissionPlan plan) {
        BigDecimal dealValue = deal.getValue();
        
        if (dealValue == null) {
            return BigDecimal.ZERO;
        }
        
        // Apply 15% penalty for deals closed after quarter end
        // per CFO directive to discourage sandbagging
        if (isAfterQuarterEnd(deal.getCloseDate())) {
            dealValue = dealValue.multiply(new BigDecimal("0.85"));
        }
        
        BigDecimal baseRate = plan.getBaseRate();
        return dealValue.multiply(baseRate);
    }
    
    private boolean isAfterQuarterEnd(LocalDate date) {
        return false;
    }
}
```

12. Rewrite the following over-commented code to follow best practices:

**Answer:**

**Original over-commented code has these issues:**
- Comments state obvious information available in method/variable names
- Redundant parameter documentation
- Comments that just restate the code

**Improved version:**
```java
/**
 * Represents a system user with authentication and profile information.
 */
public class User {
    private String id;
    private String name;
    private String email;
    
    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    public String getId() {
        return id;
    }
    
    // Additional getters would follow the same pattern
}
```

**Key improvements:**
- Removed obvious field comments
- Removed redundant constructor parameter documentation
- Removed getter method documentation (self-explanatory)
- Kept only the class-level comment that provides context about the class purpose
- Let the code speak for itself through clear naming

13. Add appropriate comments to this code that lacks important context:

**Answer:**

```java
public class DealProcessor {
    
    public void processDeal(Deal deal) {
        // Deals over $50K require manager approval per company policy
        // to ensure proper oversight of high-value transactions
        if (deal.getValue().compareTo(new BigDecimal("50000")) > 0) {
            deal.setStatus(DealStatus.REQUIRES_APPROVAL);
            notifyManager(deal);
        }
        
        // Apply late penalty for deals closed after quarter end
        // to discourage revenue manipulation and maintain accurate quarterly reporting
        if (deal.getCloseDate().isAfter(getQuarterEnd())) {
            applyLatePenalty(deal);
        }
        
        // WORKAROUND: CRM system has 2-second processing delay
        // Remove this sleep after CRM upgrade scheduled for Q3 2024 (JIRA-5678)
        Thread.sleep(2000);
        
        // Final validation against CRM ensures data consistency
        // May throw ValidationException if CRM data doesn't match
        validateWithCRM(deal);
    }
    
    private void applyLatePenalty(Deal deal) {
        // 10% penalty applied to deal value for late closure
        // per finance department directive to maintain quarterly accuracy
        BigDecimal penalty = deal.getValue().multiply(new BigDecimal("0.1"));
        deal.setValue(deal.getValue().subtract(penalty));
    }
}
```

**Comments added explain:**
- Business rules and their reasoning ($50K threshold, late penalties)
- Workaround with timeline for removal
- Potential exceptions and side effects
- Policy context for penalties

## Practical Application Questions

14. Your team has a complex commission calculation algorithm with multiple edge cases. How would you approach commenting this code to ensure future maintainability?

    **Answer:** For complex commission calculations, I would structure comments as follows:
    
    **1. High-level algorithm overview:**
    ```java
    /**
     * Calculates tiered commission using progressive rate structure.
     * 
     * Business Rules:
     * - Base rates: 0-50K (5%), 50K-100K (7%), 100K+ (10%)
     * - Quarterly bonuses applied after base calculation
     * - Late closure penalties reduce final amount
     * - Refunds trigger commission clawback with 50% cap
     * 
     * See: Finance Policy FP-2024-003 for complete specification
     */
    ```
    
    **2. Document edge cases with business context:**
    ```java
    // EDGE CASE: Negative deal values (refunds/chargebacks)
    // Apply inverse commission but cap clawback at 50% of quarterly earnings
    // per union agreement section 4.2.1
    if (dealValue.compareTo(BigDecimal.ZERO) < 0) {
        // Implementation with business rule explanation
    }
    ```
    
    **3. Explain non-obvious calculations:**
    ```java
    // Progressive tier calculation: only amount ABOVE threshold gets higher rate
    // Example: $75K deal = $50K at 5% + $25K at 7% = $4,250 total
    ```
    
    **4. Reference external documentation:**
    ```java
    // Algorithm implements "Commission Structure 2024" specification
    // https://company.wiki/finance/commission-rules-2024
    ```

15. You discover a workaround in the codebase for a third-party API limitation. What information should you include in comments to help future developers?

    **Answer:** A comprehensive workaround comment should include:
    
    ```java
    // WORKAROUND: Salesforce API rate limiting issue
    // 
    // Problem: SF API allows max 100 calls/minute, but our batch processing
    // needs 300+ calls during peak hours, causing 429 rate limit errors
    // 
    // Root Cause: SF changed rate limits in v52.0 (March 2023) without
    // backward compatibility for existing integrations
    // 
    // Temporary Solution: Added 2-second delay between calls to stay under limit
    // Performance impact: Batch processing now takes 3x longer (acceptable for now)
    // 
    // Permanent Fix: Implement bulk API calls (JIRA-1234)
    // Timeline: Planned for Q2 2024 after SF bulk API training
    // 
    // Contact: John Smith (john.smith@company.com) - SF integration owner
    // 
    // Remove this workaround after bulk API implementation
    Thread.sleep(2000);
    ```
    
    **Key elements:**
    - Clear problem description
    - Root cause analysis
    - Current solution and its trade-offs
    - Planned permanent solution with timeline
    - Contact information for domain expert
    - Clear removal instructions

16. A junior developer on your team is adding comments to every line of code. How would you guide them toward better commenting practices?

    **Answer:** I would guide them through these steps:
    
    **1. Explain the principle:** "Comments should explain WHY, not WHAT"
    
    **2. Show examples of good vs. bad comments:**
    ```java
    // BAD: States the obvious
    count++; // Increment count by 1
    
    // GOOD: Explains business context
    count++; // Track failed login attempts for security audit
    ```
    
    **3. Teach the "self-documenting code" concept:**
    ```java
    // Instead of commenting unclear code:
    int x = u.getA() * 0.1; // Calculate 10% commission
    
    // Write self-documenting code:
    BigDecimal commission = user.getSalesAmount().multiply(COMMISSION_RATE);
    ```
    
    **4. Provide a commenting checklist:**
    - Does this comment explain WHY, not WHAT?
    - Would the code be unclear without this comment?
    - Does this comment provide business context?
    - Will this comment become outdated when code changes?
    
    **5. Code review feedback:** Focus on teaching moments during reviews, explaining why specific comments add or remove value.

17. You're reviewing code that has no comments but uses cryptic variable names and complex logic. What's the best approach to improve this code's maintainability?

    **Answer:** Address this in priority order:
    
    **1. First, improve naming (highest impact):**
    ```java
    // Before: Cryptic names
    public int calc(int x, int y, boolean f) {
        return f ? x * y : x + y;
    }
    
    // After: Self-documenting names
    public int calculateCommission(int salesAmount, int rate, boolean hasBonus) {
        return hasBonus ? salesAmount * rate : salesAmount + rate;
    }
    ```
    
    **2. Extract complex logic into well-named methods:**
    ```java
    // Before: Complex inline logic
    if (user.getStartDate().isBefore(LocalDate.now().minusYears(2)) && 
        user.getSales() > 100000 && user.getTerritory().equals("WEST")) {
        // complex calculation
    }
    
    // After: Extracted method with clear name
    if (isEligibleForSeniorBonus(user)) {
        calculateSeniorBonus(user);
    }
    ```
    
    **3. Add strategic comments for remaining complexity:**
    ```java
    // Only add comments where business context is needed
    // Commission rate doubles for Q4 to incentivize year-end sales push
    if (isQ4()) {
        rate = rate.multiply(new BigDecimal("2"));
    }
    ```
    
    **4. Refactor gradually:** Make incremental improvements rather than rewriting everything at once.

18. How would you comment code that implements a specific business rule that might seem arbitrary to developers unfamiliar with the domain?

    **Answer:** For domain-specific business rules, provide comprehensive context:
    
    ```java
    /**
     * Apply "clawback" commission adjustment for returned/cancelled deals.
     * 
     * Business Context:
     * When customers return products or cancel services within 90 days,
     * sales reps must return a portion of their earned commission to
     * prevent gaming the system with fake sales.
     * 
     * Rules (per Sales Policy SP-2024-001):
     * - 0-30 days: 100% clawback
     * - 31-60 days: 50% clawback  
     * - 61-90 days: 25% clawback
     * - 90+ days: No clawback (sale considered final)
     * 
     * Exception: VIP customers (annual spend >$1M) have extended 120-day
     * return window but follow same clawback schedule.
     * 
     * Legal Requirement: Sarbanes-Oxley compliance requires accurate
     * revenue recognition - commissions must align with recognized revenue.
     * 
     * Contact: Finance team (finance@company.com) for policy questions
     */
    public BigDecimal calculateClawbackAmount(Deal deal, LocalDate returnDate) {
        long daysSinceSale = ChronoUnit.DAYS.between(deal.getCloseDate(), returnDate);
        
        // Apply clawback percentage based on return timing
        if (daysSinceSale <= 30) {
            return deal.getCommissionAmount(); // 100% clawback
        } else if (daysSinceSale <= 60) {
            return deal.getCommissionAmount().multiply(new BigDecimal("0.5")); // 50% clawback
        } else if (daysSinceSale <= 90) {
            return deal.getCommissionAmount().multiply(new BigDecimal("0.25")); // 25% clawback
        } else {
            return BigDecimal.ZERO; // No clawback after 90 days
        }
    }
    ```
    
    **Key elements for business rule comments:**
    - Business context and reasoning
    - Complete rule specification with examples
    - Policy references and version numbers
    - Legal/compliance requirements
    - Contact information for domain experts
    - Exception cases and their handling