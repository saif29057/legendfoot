package com.ecommerce.app.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for date operations.
 * 
 * This class follows SOLID principles:
 * - Single Responsibility: Handles only date-related utility functions
 * - Open/Closed: Open for extension through additional date formats
 * - Liskov Substitution: Can be substituted with any DateUtil implementation
 * - Interface Segregation: Provides only date utility methods
 * - Dependency Inversion: Low coupling, depends only on Java standard library
 * 
 * The class provides consistent date formatting and manipulation
 * throughout the application, following best practices for date handling.
 */
public class DateUtil {

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with static methods only.
     */
    private DateUtil() {
        // Utility class - no instantiation allowed
    }

    /**
     * Formats LocalDateTime to default string format.
     * 
     * @param dateTime the date to format
     * @return formatted date string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DEFAULT_FORMATTER);
    }

    /**
     * Formats LocalDateTime to display format.
     * 
     * @param dateTime the date to format
     * @return formatted display date string
     */
    public static String formatDisplayDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DISPLAY_FORMATTER);
    }

    /**
     * Formats LocalDateTime to date only format.
     * 
     * @param dateTime the date to format
     * @return formatted date string
     */
    public static String formatDateOnly(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATE_ONLY_FORMATTER);
    }

    /**
     * Calculates days between two dates.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return number of days between dates
     */
    public static long daysBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * Gets current date and time.
     * 
     * @return current LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * Checks if a date is in the past.
     * 
     * @param dateTime the date to check
     * @return true if date is before current time
     */
    public static boolean isInPast(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isBefore(LocalDateTime.now());
    }

    /**
     * Checks if a date is within the last N days.
     * 
     * @param dateTime the date to check
     * @param days the number of days to check
     * @return true if date is within the specified days
     */
    public static boolean isWithinLastDays(LocalDateTime dateTime, int days) {
        if (dateTime == null) {
            return false;
        }
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return dateTime.isAfter(cutoffDate);
    }
}
