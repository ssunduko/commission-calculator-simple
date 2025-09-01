package com.chapman.edu.commissions.principles.kiss.original;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class for string operations that violates the KISS principle
 * by implementing overly complex solutions for simple string manipulations.
 */
public class StringUtils {
    
    // Cache for previously reversed strings
    private static final Map<String, String> reverseCache = new HashMap<>();
    
    // Cache for previously checked palindromes
    private static final Map<String, Boolean> palindromeCache = new HashMap<>();
    
    /**
     * Reverses a string using an unnecessarily complex approach
     * 
     * @param input the string to reverse
     * @return the reversed string
     */
    public static String reverseString(String input) {
        // Check cache first
        if (reverseCache.containsKey(input)) {
            return reverseCache.get(input);
        }
        
        // Unnecessary complexity: convert to char array, then to list, then reverse, then join
        char[] chars = input.toCharArray();
        List<Character> charList = new ArrayList<>();
        
        for (char c : chars) {
            charList.add(c);
        }
        
        List<Character> reversedList = new ArrayList<>();
        for (int i = charList.size() - 1; i >= 0; i--) {
            reversedList.add(charList.get(i));
        }
        
        StringBuilder result = new StringBuilder();
        for (Character c : reversedList) {
            result.append(c);
        }
        
        // Store in cache
        reverseCache.put(input, result.toString());
        
        return result.toString();
    }
    
    /**
     * Checks if a string is a palindrome using an unnecessarily complex approach
     * 
     * @param input the string to check
     * @return true if the string is a palindrome, false otherwise
     */
    public static boolean isPalindrome(String input) {
        // Check cache first
        if (palindromeCache.containsKey(input)) {
            return palindromeCache.get(input);
        }
        
        // Unnecessary complexity: normalize the string first
        String normalized = normalizeString(input);
        
        // Unnecessary complexity: use the reverse method and compare
        String reversed = reverseString(normalized);
        
        boolean result = normalized.equals(reversed);
        
        // Store in cache
        palindromeCache.put(input, result);
        
        return result;
    }
    
    /**
     * Normalizes a string by removing non-alphanumeric characters and converting to lowercase
     * using an unnecessarily complex approach
     * 
     * @param input the string to normalize
     * @return the normalized string
     */
    public static String normalizeString(String input) {
        // Unnecessary complexity: use regex to remove non-alphanumeric characters
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
        Matcher matcher = pattern.matcher(input);
        String alphanumeric = matcher.replaceAll("");
        
        // Unnecessary complexity: convert to char array, then to lowercase, then join
        char[] chars = alphanumeric.toCharArray();
        StringBuilder result = new StringBuilder();
        
        for (char c : chars) {
            if (c >= 'A' && c <= 'Z') {
                result.append((char) (c + 32)); // Convert to lowercase
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    /**
     * Counts the occurrences of a substring in a string using an unnecessarily complex approach
     * 
     * @param input the string to search in
     * @param substring the substring to search for
     * @return the number of occurrences
     */
    public static int countOccurrences(String input, String substring) {
        if (input == null || input.isEmpty() || substring == null || substring.isEmpty()) {
            return 0;
        }
        
        // Unnecessary complexity: implement our own search algorithm
        int count = 0;
        int index = 0;
        
        while (index < input.length()) {
            boolean found = true;
            
            // Check if substring exists at current index
            for (int i = 0; i < substring.length(); i++) {
                if (index + i >= input.length() || input.charAt(index + i) != substring.charAt(i)) {
                    found = false;
                    break;
                }
            }
            
            if (found) {
                count++;
                index += substring.length();
            } else {
                index++;
            }
        }
        
        return count;
    }
    
    /**
     * Capitalizes the first letter of each word in a string using an unnecessarily complex approach
     * 
     * @param input the string to capitalize
     * @return the capitalized string
     */
    public static String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        // Unnecessary complexity: implement our own word splitting and capitalization
        char[] chars = input.toCharArray();
        boolean newWord = true;
        
        for (int i = 0; i < chars.length; i++) {
            if (Character.isWhitespace(chars[i])) {
                newWord = true;
            } else if (newWord) {
                chars[i] = Character.toUpperCase(chars[i]);
                newWord = false;
            } else {
                chars[i] = Character.toLowerCase(chars[i]);
            }
        }
        
        return new String(chars);
    }
}