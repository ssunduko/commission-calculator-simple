package com.chapman.edu.commissions.documentation.comments.fixed;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * FIX: Clear class description that explains business purpose
 * Represents a sales opportunity with associated commission calculations.
 * Deals progress through various statuses and trigger commission payments when closed.
 */
public class Deal {
    // FIX: No unnecessary comments for obvious fields
    private String id;
    private String title;
    private BigDecimal value;
    private DealStatus status;
    private String salesRepId;
    private LocalDate closeDate;

    // FIX: No redundant comment for default constructor
    public Deal() {
    }

    // FIX: No redundant JavaDoc for simple getter
    public String getId() {
        // FIX: No unnecessary comment for obvious operation
        return id;
    }

    // FIX: No redundant JavaDoc for simple setter
    public void setId(String id) {
        // FIX: No unnecessary comment for obvious operation
        this.id = id;
    }

    // FIX: No redundant JavaDoc for simple getter
    public BigDecimal getValue() {
        // FIX: No unnecessary comment for obvious operation
        return value;
    }

    // FIX: No redundant JavaDoc for simple setter
    public void setValue(BigDecimal value) {
        // FIX: No unnecessary comment for obvious operation
        this.value = value;
    }

    // FIX: No redundant JavaDoc for simple getter
    public DealStatus getStatus() {
        // FIX: No unnecessary comment for obvious operation
        return status;
    }

    // FIX: No redundant JavaDoc for simple setter
    public void setStatus(DealStatus status) {
        // FIX: No unnecessary comment for obvious operation
        this.status = status;
    }

    // FIX: No redundant JavaDoc for simple getter
    public LocalDate getCloseDate() {
        // FIX: No unnecessary comment for obvious operation
        return closeDate;
    }

    // FIX: No redundant JavaDoc for simple setter
    public void setCloseDate(LocalDate closeDate) {
        // FIX: No unnecessary comment for obvious operation
        this.closeDate = closeDate;
    }
}
