package com.chapman.edu.commissions.documentation.comments.original;

/**
 * VIOLATION: Vague class description that doesn't explain business context
 * Enum for user roles
 */
public enum UserRole {
    // VIOLATION: Unnecessary comment for obvious enum value
    // Sales representative role
    SALES_REP("Sales Representative"),
    // VIOLATION: Unnecessary comment for obvious enum value  
    // Sales manager role  
    SALES_MANAGER("Sales Manager"),
    // VIOLATION: Unnecessary comment for obvious enum value
    // Finance admin role
    FINANCE_ADMIN("Finance Administrator"),
    // VIOLATION: Unnecessary comment for obvious enum value
    // System admin role
    SYSTEM_ADMIN("System Administrator");

    // VIOLATION: Unnecessary comment for obvious field
    // Display name field
    private final String displayName;

    /**
     * VIOLATION: Method comment that just restates the method name
     * Constructor that sets display name
     * @param displayName the display name
     */
    UserRole(String displayName) {
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
