package com.chapman.edu.commissions.cohesion;

import com.chapman.edu.commissions.model.Deal;
import com.chapman.edu.commissions.model.DealProduct;
import com.chapman.edu.commissions.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

/**
 * Example of Coincidental Cohesion.
 * 
 * Coincidental Cohesion is the weakest form of cohesion where parts of a module are grouped
 * arbitrarily with no meaningful relationship between them. The only relationship is that they
 * exist in the same source file or class.
 * 
 * This class demonstrates coincidental cohesion by grouping unrelated utility methods that have
 * no meaningful relationship to each other. They just happen to be in the same class.
 */
public class CoincidentalCohesion {
    
    private static final Random random = new Random();
    
    /**
     * Generates a random ID string.
     * 
     * @return a random ID string
     */
    public static String generateRandomId() {
        return String.valueOf(Math.abs(random.nextLong()));
    }
    
    /**
     * Converts a string to uppercase.
     * 
     * @param input the input string
     * @return the uppercase version of the input string
     */
    public static String convertToUpperCase(String input) {
        return input != null ? input.toUpperCase() : null;
    }
    
    /**
     * Calculates the factorial of a number.
     * 
     * @param n the number
     * @return the factorial of the number
     */
    public static long factorial(int n) {
        if (n <= 1) {
            return 1;
        }
        return n * factorial(n - 1);
    }
    
    /**
     * Checks if a year is a leap year.
     * 
     * @param year the year to check
     * @return true if the year is a leap year, false otherwise
     */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
    
    /**
     * Formats a deal for display.
     * 
     * @param deal the deal to format
     * @return a formatted string representation of the deal
     */
    public static String formatDeal(Deal deal) {
        if (deal == null) {
            return "N/A";
        }
        return String.format("Deal: %s, Value: $%s", 
                deal.getTitle(), 
                deal.getValue().toString());
    }
    
    /**
     * Validates an email address format.
     * 
     * @param email the email address to validate
     * @return true if the email address is valid, false otherwise
     */
    public static boolean validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    /**
     * Calculates the area of a circle.
     * 
     * @param radius the radius of the circle
     * @return the area of the circle
     */
    public static double calculateCircleArea(double radius) {
        return Math.PI * radius * radius;
    }
    
    /**
     * Reverses a string.
     * 
     * @param input the input string
     * @return the reversed string
     */
    public static String reverseString(String input) {
        if (input == null) {
            return null;
        }
        return new StringBuilder(input).reverse().toString();
    }
}