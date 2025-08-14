package com.chapman.edu.commissions.principles.kiss.original;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * A calculator class that violates the KISS principle by using an overly complex
 * approach to perform simple arithmetic operations.
 */
public class Calculator {
    
    // Using a strategy pattern with lambda expressions for simple arithmetic operations
    private final Map<String, BiFunction<Double, Double, Double>> operations;
    
    // Singleton instance
    private static Calculator instance;
    
    // Operation history
    private final Map<Integer, String> operationHistory;
    private int operationCount;
    
    /**
     * Private constructor for singleton pattern
     */
    private Calculator() {
        operations = new HashMap<>();
        operations.put("add", (a, b) -> a + b);
        operations.put("subtract", (a, b) -> a - b);
        operations.put("multiply", (a, b) -> a * b);
        operations.put("divide", (a, b) -> {
            if (Math.abs(b) < 0.0001) {
                throw new ArithmeticException("Division by zero");
            }
            return a / b;
        });
        
        operationHistory = new HashMap<>();
        operationCount = 0;
    }
    
    /**
     * Get the singleton instance
     */
    public static synchronized Calculator getInstance() {
        if (instance == null) {
            instance = new Calculator();
        }
        return instance;
    }
    
    /**
     * Perform an arithmetic operation
     * 
     * @param operation the operation to perform (add, subtract, multiply, divide)
     * @param a the first operand
     * @param b the second operand
     * @return the result of the operation
     * @throws IllegalArgumentException if the operation is not supported
     */
    public double calculate(String operation, double a, double b) {
        if (!operations.containsKey(operation)) {
            throw new IllegalArgumentException("Unsupported operation: " + operation);
        }
        
        double result = operations.get(operation).apply(a, b);
        
        // Record the operation in history
        operationCount++;
        operationHistory.put(operationCount, 
                String.format("Operation %d: %f %s %f = %f", 
                        operationCount, a, operation, b, result));
        
        return result;
    }
    
    /**
     * Get the operation history
     */
    public Map<Integer, String> getOperationHistory() {
        return new HashMap<>(operationHistory);
    }
    
    /**
     * Clear the operation history
     */
    public void clearHistory() {
        operationHistory.clear();
        operationCount = 0;
    }
    
    /**
     * Factory method to create a new calculator (violates singleton pattern)
     */
    public static Calculator createCalculator() {
        return new Calculator();
    }
    
    /**
     * Unnecessarily complex method to add two numbers
     */
    public double complexAdd(double a, double b) {
        // Unnecessary decomposition of addition
        double result = a;
        boolean isNegative = b < 0;
        double absB = Math.abs(b);
        
        if (isNegative) {
            for (int i = 0; i < absB; i++) {
                result--;
            }
            // Handle fractional part
            result -= absB - (int)absB;
        } else {
            for (int i = 0; i < absB; i++) {
                result++;
            }
            // Handle fractional part
            result += absB - (int)absB;
        }
        
        return result;
    }
    
    /**
     * Unnecessarily complex method to multiply two numbers
     */
    public double complexMultiply(double a, double b) {
        // Unnecessary implementation of multiplication as repeated addition
        double result = 0;
        boolean isNegative = (a < 0 && b > 0) || (a > 0 && b < 0);
        double absA = Math.abs(a);
        double absB = Math.abs(b);
        
        // Use the smaller number for iterations
        if (absA > absB) {
            double temp = absA;
            absA = absB;
            absB = temp;
        }
        
        // Multiply using repeated addition
        for (int i = 0; i < (int)absA; i++) {
            result += absB;
        }
        
        // Handle fractional part
        result += absB * (absA - (int)absA);
        
        return isNegative ? -result : result;
    }
}