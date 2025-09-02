package com.chapman.edu.commissions.documentation.comments.fixed;

import java.math.BigDecimal;

/**
 * FIX: Clear class description that explains business purpose
 * Defines commission calculation rules and rates for sales representatives.
 * Plans are assigned to users and determine their commission structure.
 */
public class CommissionPlan {
    // FIX: No unnecessary comments for obvious fields
    private String id;
    private String name;
    private BigDecimal baseRate;

    // FIX: No redundant comment for default constructor
    public CommissionPlan() {
    }

    // FIX: No redundant JavaDoc for simple getter
    public String getId() {
        // FIX: No unnecessary comment for obvious operation
        return id;
    }

    // FIX: No redundant JavaDoc for simple setter
    public void setId(String id) {
        // FIX: No unnecessary comment for obvious operation
        this.id = id;
    }

    // FIX: No redundant JavaDoc for simple getter
    public BigDecimal getBaseRate() {
        // FIX: No unnecessary comment for obvious operation
        return baseRate;
    }

    // FIX: No redundant JavaDoc for simple setter
    public void setBaseRate(BigDecimal baseRate) {
        // FIX: No unnecessary comment for obvious operation
        this.baseRate = baseRate;
    }
}
