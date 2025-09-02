package com.chapman.edu.commissions.documentation.comments.original;

/**
 * VIOLATION: Vague class description that doesn't explain business context
 * Enum for deal status
 */
public enum DealStatus {
    // VIOLATION: Unnecessary comment for obvious enum value
    // Open status
    OPEN("Open"),
    // VIOLATION: Unnecessary comment for obvious enum value
    // Requires approval status
    REQUIRES_APPROVAL("Requires Approval"),
    // VIOLATION: Unnecessary comment for obvious enum value
    // Processed status
    PROCESSED("Processed"),
    // VIOLATION: Unnecessary comment for obvious enum value
    // Closed status
    CLOSED("Closed");

    // VIOLATION: Unnecessary comment for obvious field
    // Display name field
    private final String displayName;

    /**
     * VIOLATION: Method comment that just restates the method name
     * Constructor that sets display name
     * @param displayName the display name
     */
    DealStatus(String displayName) {
        // VIOLATION: Unnecessary comment for obvious operation
        // Set the display name field
        this.displayName = displayName;
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Gets the display name
     * @return the display name
     */
    public String getDisplayName() {
        // VIOLATION: Unnecessary comment for obvious operation
        // Return the display name field
        return displayName;
    }
}
