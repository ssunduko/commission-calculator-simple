package com.chapman.edu.commissions.principles.kiss.fixed;

/**
 * A calculator class that follows the KISS principle by using simple,
 * straightforward implementations for arithmetic operations.
 */
public class Calculator {
    
    /**
     * Add two numbers
     * 
     * @param a the first number
     * @param b the second number
     * @return the sum of a and b
     */
    public double add(double a, double b) {
        return a + b;
    }
    
    /**
     * Subtract two numbers
     * 
     * @param a the first number
     * @param b the second number
     * @return the difference of a and b
     */
    public double subtract(double a, double b) {
        return a - b;
    }
    
    /**
     * Multiply two numbers
     * 
     * @param a the first number
     * @param b the second number
     * @return the product of a and b
     */
    public double multiply(double a, double b) {
        return a * b;
    }
    
    /**
     * Divide two numbers
     * 
     * @param a the first number
     * @param b the second number
     * @return the quotient of a and b
     * @throws ArithmeticException if b is zero
     */
    public double divide(double a, double b) {
        if (b == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return a / b;
    }
}