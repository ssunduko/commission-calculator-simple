package com.chapman.edu.commissions.documentation.naming.original;

/**
 * VIOLATION: Vague class description that doesn't explain business purpose
 * Role enum
 */
public enum UserRole {
    // VIOLATION: Abbreviation instead of descriptive name
    SR("Sales Rep"),
    // VIOLATION: Abbreviation instead of descriptive name
    SM("Sales Mgr"),
    // VIOLATION: Abbreviation instead of descriptive name
    FA("Finance Admin"),
    // VIOLATION: Abbreviation instead of descriptive name
    SA("System Admin");

    // VIOLATION: Cryptic abbreviation "dn" instead of descriptive name "displayName"
    private final String dn;

    UserRole(String dn) {
        this.dn = dn;
    }

    // VIOLATION: Method name "getDn" doesn't clearly express intent
    public String getDn() {
        return dn;
    }
}
