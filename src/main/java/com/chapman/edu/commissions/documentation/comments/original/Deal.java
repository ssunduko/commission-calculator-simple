package com.chapman.edu.commissions.documentation.comments.original;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * VIOLATION: Vague class description that doesn't explain business context
 * Deal class represents a deal
 */
public class Deal {
    // VIOLATION: Unnecessary comment for obvious field
    // Deal ID field
    private String id;
    // VIOLATION: Unnecessary comment for obvious field
    // Deal title field
    private String title;
    // VIOLATION: Unnecessary comment for obvious field
    // Deal value field
    private BigDecimal value;
    // VIOLATION: Unnecessary comment for obvious field
    // Deal status field
    private DealStatus status;
    // VIOLATION: Unnecessary comment for obvious field
    // Sales rep ID field
    private String salesRepId;
    // VIOLATION: Unnecessary comment for obvious field
    // Close date field
    private LocalDate closeDate;

    /**
     * VIOLATION: Redundant comment for default constructor
     * Default constructor
     */
    public Deal() {
        // VIOLATION: Unnecessary comment for empty constructor
        // Empty constructor
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Gets the deal ID
     * @return the deal ID
     */
    public String getId() {
        // VIOLATION: Unnecessary comment for obvious operation
        // Return the id field
        return id;
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Sets the deal ID
     * @param id the deal ID to set
     */
    public void setId(String id) {
        // VIOLATION: Unnecessary comment for obvious operation
        // Set the id field
        this.id = id;
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Gets the deal value
     * @return the deal value
     */
    public BigDecimal getValue() {
        // VIOLATION: Unnecessary comment for obvious operation
        // Return the value field
        return value;
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Sets the deal value
     * @param value the deal value to set
     */
    public void setValue(BigDecimal value) {
        // VIOLATION: Unnecessary comment for obvious operation
        // Set the value field
        this.value = value;
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Gets the deal status
     * @return the deal status
     */
    public DealStatus getStatus() {
        // VIOLATION: Unnecessary comment for obvious operation
        // Return the status field
        return status;
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Sets the deal status
     * @param status the deal status to set
     */
    public void setStatus(DealStatus status) {
        // VIOLATION: Unnecessary comment for obvious operation
        // Set the status field
        this.status = status;
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Gets the close date
     * @return the close date
     */
    public LocalDate getCloseDate() {
        // VIOLATION: Unnecessary comment for obvious operation
        // Return the close date field
        return closeDate;
    }

    /**
     * VIOLATION: Method comment that just restates the method name
     * Sets the close date
     * @param closeDate the close date to set
     */
    public void setCloseDate(LocalDate closeDate) {
        // VIOLATION: Unnecessary comment for obvious operation
        // Set the close date field
        this.closeDate = closeDate;
    }
}
