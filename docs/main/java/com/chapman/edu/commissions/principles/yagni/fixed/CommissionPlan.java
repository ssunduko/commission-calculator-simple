package com.chapman.edu.commissions.principles.yagni.fixed;

import java.util.Objects;

/**
 * Represents a commission plan in the system, following YAGNI principle.
 * This class contains only the fields and methods needed for the basic functionality.
 */
public class CommissionPlan {
    private String id;
    private String name;
    
    /**
     * Default constructor
     */
    public CommissionPlan() {
    }
    
    /**
     * Constructor with essential fields
     */
    public CommissionPlan(String name) {
        this.name = name;
    }
    
    // Getters and Setters for essential fields
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommissionPlan that = (CommissionPlan) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "CommissionPlan{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}