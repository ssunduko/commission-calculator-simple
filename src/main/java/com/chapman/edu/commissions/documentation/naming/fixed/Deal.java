package com.chapman.edu.commissions.documentation.naming.fixed;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * FIX: Clear class description that explains business purpose
 * Represents a sales opportunity with associated commission calculations.
 */
public class Deal {
    private String id;
    // FIX: Descriptive variable name "title" instead of single-letter "t"
    private String title;
    // FIX: Descriptive variable name "value" instead of abbreviation "val"
    private BigDecimal value;
    // FIX: Descriptive variable name "status" instead of abbreviation "stat"
    private DealStatus status;
    // FIX: Descriptive variable name "salesRepresentativeId" instead of abbreviation "rep"
    private String salesRepresentativeId;
    // FIX: Descriptive variable name "closeDate" instead of abbreviation "dt"
    private LocalDate closeDate;
    // FIX: Descriptive boolean name with "is" prefix instead of single-letter "f"
    private boolean isCommissionEligible;

    public Deal() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // FIX: Added method for the "title" field that was missing in original
    public String getTitle() {
        return title;
    }

    // FIX: Added method for the "title" field that was missing in original
    public void setTitle(String title) {
        this.title = title;
    }

    // FIX: Descriptive method name "getValue" instead of abbreviated "getVal"
    public BigDecimal getValue() {
        return value;
    }

    // FIX: Descriptive method name and parameter "setValue" instead of abbreviated "setVal"
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    // FIX: Descriptive method name "getStatus" instead of abbreviated "getStat"
    public DealStatus getStatus() {
        return status;
    }

    // FIX: Descriptive method name and parameter "setStatus" instead of abbreviated "setStat"
    public void setStatus(DealStatus status) {
        this.status = status;
    }

    // FIX: Descriptive method name "getSalesRepresentativeId" instead of abbreviated "getRep"
    public String getSalesRepresentativeId() {
        return salesRepresentativeId;
    }

    // FIX: Descriptive method name and parameter instead of abbreviated version
    public void setSalesRepresentativeId(String salesRepresentativeId) {
        this.salesRepresentativeId = salesRepresentativeId;
    }

    // FIX: Descriptive method name "getCloseDate" that matches field name instead of generic "getDate"
    public LocalDate getCloseDate() {
        return closeDate;
    }

    // FIX: Descriptive method name and parameter that match field name
    public void setCloseDate(LocalDate closeDate) {
        this.closeDate = closeDate;
    }

    // FIX: Boolean getter with "is" prefix following convention instead of "getFlag"
    public boolean isCommissionEligible() {
        return isCommissionEligible;
    }

    // FIX: Descriptive method name and parameter instead of vague "setFlag"
    public void setCommissionEligible(boolean commissionEligible) {
        this.isCommissionEligible = commissionEligible;
    }
}
