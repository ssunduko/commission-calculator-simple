package com.chapman.edu.commissions.principles.kiss.fixed;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A utility class for date operations that follows the KISS principle
 * by implementing simple, straightforward solutions for date manipulations.
 */
public class DateUtils {
    
    /**
     * Formats a date using a simple approach with DateTimeFormatter
     * 
     * @param date the date to format
     * @param pattern the format pattern
     * @return the formatted date string
     */
    public static String formatDate(LocalDate date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * Calculates the age based on birth date using a simple approach with Period
     * 
     * @param birthDate the birth date
     * @param currentDate the current date
     * @return the age in years
     */
    public static int calculateAge(LocalDate birthDate, LocalDate currentDate) {
        return Period.between(birthDate, currentDate).getYears();
    }
    
    /**
     * Checks if a date is a weekend using a simple approach
     * 
     * @param date the date to check
     * @return true if the date is a weekend, false otherwise
     */
    public static boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
    
    /**
     * Gets all dates between two dates using a simple approach with Stream
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of all dates between startDate and endDate (inclusive)
     */
    public static List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate) {
        long numOfDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(numOfDays)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all business days between two dates using a simple approach
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return a list of all business days between startDate and endDate (inclusive)
     */
    public static List<LocalDate> getBusinessDaysBetween(LocalDate startDate, LocalDate endDate) {
        return getDatesBetween(startDate, endDate).stream()
                .filter(date -> !isWeekend(date))
                .collect(Collectors.toList());
    }
}