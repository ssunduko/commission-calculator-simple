package com.chapman.edu.commissions.documentation.naming.original;

/**
 * VIOLATION: Vague class description that doesn't explain business purpose
 * Status enum
 */
public enum DealStatus {
    // VIOLATION: Single-letter abbreviation instead of descriptive name
    O("Open"),
    // VIOLATION: Abbreviation instead of descriptive name
    RA("Req Approval"),
    // VIOLATION: Single-letter abbreviation instead of descriptive name
    P("Processed"),
    // VIOLATION: Single-letter abbreviation instead of descriptive name
    C("Closed");

    // VIOLATION: Cryptic abbreviation "dn" instead of descriptive name "displayName"
    private final String dn;

    DealStatus(String dn) {
        this.dn = dn;
    }

    // VIOLATION: Method name "getDn" doesn't clearly express intent
    public String getDn() {
        return dn;
    }
}
