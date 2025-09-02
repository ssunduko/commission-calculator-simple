package com.chapman.edu.commissions.documentation.naming.fixed;

import java.math.BigDecimal;

/**
 * FIX: Clear class description that explains business purpose
 * Defines commission calculation rules and rates for sales representatives.
 */
public class CommissionPlan {
    private String id;
    // FIX: Descriptive variable name "planName" instead of single-letter "n"
    private String planName;
    // FIX: Descriptive variable name "baseCommissionRate" instead of single-letter "r"
    private BigDecimal baseCommissionRate;

    public CommissionPlan() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // FIX: Method name "getPlanName" clearly expresses intent instead of cryptic "getN"
    public String getPlanName() {
        return planName;
    }

    // FIX: Method name "setPlanName" clearly expresses intent instead of cryptic "setN"
    // FIX: Descriptive parameter name "planName" instead of single-letter "n"
    public void setPlanName(String planName) {
        this.planName = planName;
    }

    // FIX: Method name "getBaseCommissionRate" matches field name instead of inconsistent "getRate"
    public BigDecimal getBaseCommissionRate() {
        return baseCommissionRate;
    }

    // FIX: Method name "setBaseCommissionRate" matches field name instead of inconsistent "setRate"
    // FIX: Descriptive parameter name "baseCommissionRate" instead of single-letter "r"
    public void setBaseCommissionRate(BigDecimal baseCommissionRate) {
        this.baseCommissionRate = baseCommissionRate;
    }
}
