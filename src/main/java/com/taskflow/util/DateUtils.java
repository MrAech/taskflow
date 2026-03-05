package com.taskflow.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    private DateUtils() {}

    /**
     * Returns true if the given dueDate is in the past (i.e., the task is overdue).
     */
    public static boolean isOverdue(LocalDateTime dueDate) {
        if (dueDate == null) return false;
        LocalDateTime now = LocalDateTime.now();
        return !dueDate.isAfter(now);
    }

    /**
     * Returns the number of days until the task is due.
     * Returns 0 if already overdue (never negative).
     *
     */
    public static long getDaysUntilDue(LocalDateTime dueDate) {
        if (dueDate == null) return Long.MAX_VALUE;
        return ChronoUnit.DAYS.between(LocalDateTime.now(), dueDate);
    }

    public static LocalDateTime startOfDay(LocalDateTime dateTime) {
        return dateTime.toLocalDate().atStartOfDay();
    }

    public static LocalDateTime endOfDay(LocalDateTime dateTime) {
        return dateTime.toLocalDate().atTime(23, 59, 59);
    }
}
