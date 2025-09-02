package com.chapman.edu.commissions.documentation.naming.fixed;

/**
 * FIX: Clear class description that explains business purpose
 * Represents the lifecycle stages of a sales deal.
 */
public enum DealStatus {
    // FIX: Descriptive enum name instead of single-letter abbreviation
    OPEN("Open"),
    // FIX: Descriptive enum name instead of abbreviation
    REQUIRES_APPROVAL("Requires Approval"),
    // FIX: Descriptive enum name instead of single-letter abbreviation
    PROCESSED("Processed"), 
    // FIX: Descriptive enum name instead of single-letter abbreviation
    CLOSED("Closed");

    // FIX: Descriptive field name "displayName" instead of cryptic abbreviation "dn"
    private final String displayName;

    DealStatus(String displayName) {
        this.displayName = displayName;
    }

    // FIX: Method name "getDisplayName" clearly expresses intent
    public String getDisplayName() {
        return displayName;
    }
}
