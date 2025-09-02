package com.chapman.edu.commissions.documentation.comments.fixed;

/**
 * FIX: Clear class description that explains business purpose
 * Represents the lifecycle stages of a sales deal.
 * Status transitions follow approval workflows and trigger commission calculations.
 */
public enum DealStatus {
    // FIX: No unnecessary comments for obvious enum values
    OPEN("Open"),
    REQUIRES_APPROVAL("Requires Approval"),
    PROCESSED("Processed"), 
    CLOSED("Closed");

    // FIX: No unnecessary comment for obvious field
    private final String displayName;

    // FIX: No redundant JavaDoc for simple constructor
    DealStatus(String displayName) {
        // FIX: No unnecessary comment for obvious operation
        this.displayName = displayName;
    }

    // FIX: No redundant JavaDoc for simple getter
    public String getDisplayName() {
        // FIX: No unnecessary comment for obvious operation
        return displayName;
    }
}
