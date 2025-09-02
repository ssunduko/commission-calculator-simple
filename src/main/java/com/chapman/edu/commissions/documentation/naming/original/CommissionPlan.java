package com.chapman.edu.commissions.documentation.naming.original;

import java.math.BigDecimal;

/**
 * VIOLATION: Vague class description that doesn't explain business purpose
 * Plan class
 */
public class CommissionPlan {
    private String id;
    // VIOLATION: Single-letter variable name "n" instead of descriptive name "planName"
    private String n;
    // VIOLATION: Single-letter variable name "r" instead of descriptive name "baseCommissionRate"
    private BigDecimal r;

    public CommissionPlan() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // VIOLATION: Method name "getN" doesn't clearly express intent, should be "getPlanName"
    public String getN() {
        return n;
    }

    // VIOLATION: Method name "setN" doesn't clearly express intent, should be "setPlanName"
    // VIOLATION: Parameter name "n" is not descriptive
    public void setN(String n) {
        this.n = n;
    }

    // VIOLATION: Method name "getRate" doesn't match field name "r", should be "getBaseCommissionRate"
    public BigDecimal getRate() {
        return r;
    }

    // VIOLATION: Method name "setRate" doesn't match field name "r", should be "setBaseCommissionRate"
    // VIOLATION: Parameter name "r" is not descriptive
    public void setRate(BigDecimal r) {
        this.r = r;
    }
}
