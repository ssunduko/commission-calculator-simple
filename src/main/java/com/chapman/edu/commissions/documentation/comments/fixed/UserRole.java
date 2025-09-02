package com.chapman.edu.commissions.documentation.comments.fixed;

/**
 * FIX: Clear class description that explains business purpose and role hierarchy
 * Defines user roles that determine system permissions and commission calculation eligibility.
 * Role hierarchy: SYSTEM_ADMIN > FINANCE_ADMIN > SALES_MANAGER > SALES_REP
 */
public enum UserRole {
    // FIX: No unnecessary comments for obvious enum values
    SALES_REP("Sales Representative"),
    SALES_MANAGER("Sales Manager"), 
    FINANCE_ADMIN("Finance Administrator"),
    SYSTEM_ADMIN("System Administrator");

    // FIX: No unnecessary comment for obvious field
    private final String displayName;

    // FIX: No redundant JavaDoc for simple constructor
    UserRole(String displayName) {
        // FIX: No unnecessary comment for obvious operation
        this.displayName = displayName;
    }

    // FIX: No redundant JavaDoc for simple getter
    public String getDisplayName() {
        // FIX: No unnecessary comment for obvious operation
        return displayName;
    }
}
