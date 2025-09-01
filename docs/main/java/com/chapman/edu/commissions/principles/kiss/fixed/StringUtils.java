package com.chapman.edu.commissions.principles.kiss.fixed;

/**
 * A utility class for string operations that follows the KISS principle
 * by implementing simple, straightforward solutions for string manipulations.
 */
public class StringUtils {
    
    /**
     * Reverses a string using a simple approach
     * 
     * @param input the string to reverse
     * @return the reversed string
     */
    public static String reverseString(String input) {
        return new StringBuilder(input).reverse().toString();
    }
    
    /**
     * Checks if a string is a palindrome using a simple approach
     * 
     * @param input the string to check
     * @return true if the string is a palindrome, false otherwise
     */
    public static boolean isPalindrome(String input) {
        if (input == null) {
            return false;
        }
        
        String normalized = input.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        return normalized.equals(new StringBuilder(normalized).reverse().toString());
    }
    
    /**
     * Normalizes a string by removing non-alphanumeric characters and converting to lowercase
     * 
     * @param input the string to normalize
     * @return the normalized string
     */
    public static String normalizeString(String input) {
        return input.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }
    
    /**
     * Counts the occurrences of a substring in a string
     * 
     * @param input the string to search in
     * @param substring the substring to search for
     * @return the number of occurrences
     */
    public static int countOccurrences(String input, String substring) {
        if (input == null || input.isEmpty() || substring == null || substring.isEmpty()) {
            return 0;
        }
        
        int count = 0;
        int index = 0;
        
        while ((index = input.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        
        return count;
    }
    
    /**
     * Capitalizes the first letter of each word in a string
     * 
     * @param input the string to capitalize
     * @return the capitalized string
     */
    public static String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        
        for (char c : input.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
                result.append(c);
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        
        return result.toString();
    }
}