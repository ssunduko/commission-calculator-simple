package com.chapman.edu.commissions.documentation.naming.fixed;

/**
 * FIX: Clear class description that explains business purpose
 * Defines user roles that determine system permissions and commission calculation eligibility.
 */
public enum UserRole {
    // FIX: Descriptive enum names instead of abbreviations
    SALES_REP("Sales Representative"),
    // FIX: Descriptive enum names instead of abbreviations
    SALES_MANAGER("Sales Manager"), 
    // FIX: Descriptive enum names instead of abbreviations
    FINANCE_ADMIN("Finance Administrator"),
    // FIX: Descriptive enum names instead of abbreviations
    SYSTEM_ADMIN("System Administrator");

    // FIX: Descriptive field name "displayName" instead of cryptic abbreviation "dn"
    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    // FIX: Method name "getDisplayName" clearly expresses intent
    public String getDisplayName() {
        return displayName;
    }
}
