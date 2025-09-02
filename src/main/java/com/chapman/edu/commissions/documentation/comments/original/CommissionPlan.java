package com.chapman.edu.commissions.documentation.comments.original;

import java.math.BigDecimal;

/**
 * VIOLATION: Vague class description that doesn't explain business context
 * Commission plan class represents a commission plan
 */
public class CommissionPlan {
    // VIOLATION: Unnecessary comment for obvious field
    // Plan ID field
    private String id;
    // VIOLATION: Unnecessary comment for obvious field
    // Plan name field
    private String name;
    // VIOLATION: Unnecessary comment for obvious field
    // Base rate field
    private BigDecimal baseRate;

    /**
     * VIOLATION: Redundant comment for default constructor
     * Default constructor
     */
    public CommissionPlan() {
        // VIOLATION: Unnecessary comment for empty constructor
        // Empty constructor
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Gets the plan ID
     * @return the plan ID
     */
    public String getId() {
        // VIOLATION: Unnecessary comment for obvious operation
        // Return the id field
        return id;
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Sets the plan ID
     * @param id the plan ID to set
     */
    public void setId(String id) {
        // VIOLATION: Unnecessary comment for obvious operation
        // Set the id field
        this.id = id;
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Gets the base rate
     * @return the base rate
     */
    public BigDecimal getBaseRate() {
        // VIOLATION: Unnecessary comment for obvious operation
        // Return the base rate field
        return baseRate;
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Sets the base rate
     * @param baseRate the base rate to set
     */
    public void setBaseRate(BigDecimal baseRate) {
        // VIOLATION: Unnecessary comment for obvious operation
        // Set the base rate field
        this.baseRate = baseRate;
    }
}
