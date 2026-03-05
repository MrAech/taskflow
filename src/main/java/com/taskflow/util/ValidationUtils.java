package com.taskflow.util;

import com.taskflow.model.enums.Priority;

import java.util.Set;

public class ValidationUtils {

    private ValidationUtils() {}

    /**
     * Returns true if the given string is a valid email address.
     *
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return email.matches("^[\\w._%+\\-]+@[\\w.\\-]+$");
    }

    /**
     * Returns true if the given string represents a valid Priority value.
     *
     */
    public static boolean isValidPriority(String priority) {
        if (priority == null) return false;
        Set<String> valid = Set.of(Priority.HIGH.name(), Priority.LOW.name());
        return valid.contains(priority.toUpperCase());
    }

    public static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    public static boolean isPositive(Long value) {
        return value != null && value > 0;
    }
}
