package com.chapman.edu.commissions.principles.kiss.original;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A utility class for date operations that violates the KISS principle
 * by implementing overly complex solutions for simple date manipulations.
 */
public class DateUtils {
    
    // Cache for previously formatted dates
    private static final Map<LocalDate, Map<String, String>> formatCache = new HashMap<>();
    
    // Cache for previously calculated age
    private static final Map<LocalDate, Map<LocalDate, Integer>> ageCache = new HashMap<>();
    
    /**
     * Formats a date using an unnecessarily complex approach
     * 
     * @param date the date to format
     * @param pattern the format pattern
     * @return the formatted date string
     */
    public static String formatDate(LocalDate date, String pattern) {
        // Check cache first
        if (formatCache.containsKey(date) && formatCache.get(date).containsKey(pattern)) {
            return formatCache.get(date).get(pattern);
        }
        
        // Unnecessary complexity: implement our own date formatting
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            
            if (c == 'y') {
                // Count consecutive 'y's
                int count = 1;
                while (i + 1 < pattern.length() && pattern.charAt(i + 1) == 'y') {
                    count++;
                    i++;
                }
                
                // Format year based on count
                String yearStr = String.valueOf(date.getYear());
                if (count <= 2) {
                    yearStr = yearStr.substring(yearStr.length() - 2);
                }
                result.append(yearStr);
            } else if (c == 'M') {
                // Count consecutive 'M's
                int count = 1;
                while (i + 1 < pattern.length() && pattern.charAt(i + 1) == 'M') {
                    count++;
                    i++;
                }
                
                // Format month based on count
                Month month = date.getMonth();
                if (count == 1) {
                    result.append(month.getValue());
                } else if (count == 2) {
                    String monthStr = String.valueOf(month.getValue());
                    if (monthStr.length() == 1) {
                        result.append('0');
                    }
                    result.append(monthStr);
                } else if (count == 3) {
                    result.append(month.name().substring(0, 3));
                } else {
                    result.append(month.name());
                }
            } else if (c == 'd') {
                // Count consecutive 'd's
                int count = 1;
                while (i + 1 < pattern.length() && pattern.charAt(i + 1) == 'd') {
                    count++;
                    i++;
                }
                
                // Format day based on count
                int day = date.getDayOfMonth();
                if (count == 1) {
                    result.append(day);
                } else {
                    String dayStr = String.valueOf(day);
                    if (dayStr.length() == 1) {
                        result.append('0');
                    }
                    result.append(dayStr);
                }
            } else {
                // Just append other characters
                result.append(c);
            }
        }
        
        // Store in cache
        formatCache.computeIfAbsent(date, k -> new HashMap<>()).put(pattern, result.toString());
        
        return result.toString();
    }
    
    /**
     * Calculates the age based on birth date using an unnecessarily complex approach
     * 
     * @param birthDate the birth date
     * @param currentDate the current date
     * @return the age in years
     */
    public static int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        // Check cache first
        if (ageCache.containsKey(birthDate) && ageCache.get(birthDate).containsKey(currentDate)) {
            return ageCache.get(birthDate).get(currentDate);
        }
        
        // Unnecessary complexity: calculate age manually
        int age = 0;
        LocalDate tempDate = birthDate;
        
        while (tempDate.isBefore(currentDate)) {
            tempDate = tempDate.plusYears(1);
            if (!tempDate.isAfter(currentDate)) {
                age++;
            }
        }
        
        // Store in cache
        ageCache.computeIfAbsent(birthDate, k -> new HashMap<>()).put(currentDate, age);
        
        return age;
    }
    
    /**
     * Checks if a date is a weekend using an unnecessarily complex approach
     * 
     * @param date the date to check
     * @return true if the date is a weekend, false otherwise
     */
    public static boolean isWeekend(LocalDate date) {
        // Unnecessary complexity: implement our own weekend check
        int dayOfWeek = date.getDayOfWeek().getValue();
        
        // In ISO-8601, 6 is Saturday and 7 is Sunday
        return dayOfWeek == 6 || dayOfWeek == 7;
    }
    
    /**
     * Gets all dates between two dates using an unnecessarily complex approach
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of all dates between startDate and endDate (inclusive)
     */
    public static List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();
        
        // Unnecessary complexity: implement our own date iteration
        LocalDate currentDate = startDate;
        
        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        
        return dates;
    }
    
    /**
     * Gets all business days between two dates using an unnecessarily complex approach
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of all business days between startDate and endDate (inclusive)
     */
    public static List<LocalDate> getBusinessDaysBetween(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> businessDays = new ArrayList<>();
        
        // Unnecessary complexity: implement our own business day iteration
        LocalDate currentDate = startDate;
        
        while (!currentDate.isAfter(endDate)) {
            if (!isWeekend(currentDate)) {
                businessDays.add(currentDate);
            }
            currentDate = currentDate.plusDays(1);
        }
        
        return businessDays;
    }
}