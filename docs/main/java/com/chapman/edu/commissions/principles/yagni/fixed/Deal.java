package com.chapman.edu.commissions.principles.yagni.fixed;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a sales deal in the system, following YAGNI principle.
 * This class contains only the fields and methods needed for the basic functionality.
 */
public class Deal {
    private String id;
    private String title;
    private BigDecimal value;
    private String salesRepId;
    
    /**
     * Default constructor
     */
    public Deal() {
    }
    
    /**
     * Constructor with essential fields
     */
    public Deal(String title, BigDecimal value, String salesRepId) {
        this.title = title;
        this.value = value;
        this.salesRepId = salesRepId;
    }
    
    // Getters and Setters for essential fields
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public BigDecimal getValue() {
        return value;
    }
    
    public void setValue(BigDecimal value) {
        this.value = value;
    }
    
    public String getSalesRepId() {
        return salesRepId;
    }
    
    public void setSalesRepId(String salesRepId) {
        this.salesRepId = salesRepId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deal deal = (Deal) o;
        return Objects.equals(id, deal.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Deal{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", value=" + value +
                ", salesRepId='" + salesRepId + '\'' +
                '}';
    }
}