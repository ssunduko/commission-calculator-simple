package com.chapman.edu.commissions.principles.composition.original;

import java.util.Objects;

/**
 * Represents a condition that must be met for a commission rule to apply.
 */
public class RuleCondition {
    private String id;
    private String field;
    private String operator;
    private String value;
    private String ruleId;
    
    /**
     * Default constructor
     */
    public RuleCondition() {
    }
    
    /**
     * Constructor with essential fields
     */
    public RuleCondition(String field, String operator, String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getField() {
        return field;
    }
    
    public void setField(String field) {
        this.field = field;
    }
    
    public String getOperator() {
        return operator;
    }
    
    public void setOperator(String operator) {
        this.operator = operator;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getRuleId() {
        return ruleId;
    }
    
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }
    
    /**
     * Evaluate this condition against a given value
     * @param actualValue the value to evaluate against
     * @return true if the condition is met, false otherwise
     */
    public boolean evaluate(String actualValue) {
        if (operator.equals("equals")) {
            return value.equals(actualValue);
        } else if (operator.equals("contains")) {
            return actualValue.contains(value);
        } else if (operator.equals("startsWith")) {
            return actualValue.startsWith(value);
        } else if (operator.equals("endsWith")) {
            return actualValue.endsWith(value);
        } else if (operator.equals("greaterThan")) {
            try {
                double valueDouble = Double.parseDouble(value);
                double actualDouble = Double.parseDouble(actualValue);
                return actualDouble > valueDouble;
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (operator.equals("lessThan")) {
            try {
                double valueDouble = Double.parseDouble(value);
                double actualDouble = Double.parseDouble(actualValue);
                return actualDouble < valueDouble;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        
        return false;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleCondition that = (RuleCondition) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "RuleCondition{" +
                "field='" + field + '\'' +
                ", operator='" + operator + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}