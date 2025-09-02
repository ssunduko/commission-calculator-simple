package com.chapman.edu.commissions.documentation.naming.original;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * VIOLATION: Vague class description that doesn't explain business purpose
 * Deal class
 */
public class Deal {
    private String id;
    // VIOLATION: Single-letter variable name instead of descriptive name "title"
    private String t;
    // VIOLATION: Abbreviated variable name instead of descriptive name "value"
    private BigDecimal val;
    // VIOLATION: Abbreviated variable name instead of descriptive name "status"
    private DealStatus stat;
    // VIOLATION: Abbreviated variable name instead of descriptive name "salesRepresentativeId"
    private String rep;
    // VIOLATION: Abbreviated variable name instead of descriptive name "closeDate"
    private LocalDate dt;
    // VIOLATION: Single-letter variable name instead of descriptive boolean name "isCommissionEligible"
    private boolean f;
    // VIOLATION: Inconsistent field naming (not used in any methods)
    private LocalDate startDate;

    public Deal() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // VIOLATION: Method name uses abbreviation instead of descriptive name "getValue"
    public BigDecimal getVal() {
        return val;
    }

    // VIOLATION: Method name and parameter use abbreviation instead of descriptive name "setValue"
    public void setVal(BigDecimal val) {
        this.val = val;
    }

    // VIOLATION: Method name uses abbreviation instead of descriptive name "getStatus"
    public DealStatus getStat() {
        return stat;
    }

    // VIOLATION: Method name and parameter use abbreviation instead of descriptive name "setStatus"
    public void setStat(DealStatus stat) {
        this.stat = stat;
    }

    // VIOLATION: Method name doesn't match field name (dt vs date) and should be "getCloseDate"
    public LocalDate getDate() {
        return dt;
    }

    // VIOLATION: Method name doesn't match field name and parameter uses abbreviation
    public void setDate(LocalDate dt) {
        this.dt = dt;
    }

    // VIOLATION: Boolean getter doesn't follow "is" prefix convention and uses vague name
    public boolean getFlag() {
        return f;
    }

    // VIOLATION: Method name doesn't follow naming convention and parameter uses single letter
    public void setFlag(boolean f) {
        this.f = f;
    }
}
