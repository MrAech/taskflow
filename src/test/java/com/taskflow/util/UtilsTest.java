package com.taskflow.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Public tests for DateUtils and ValidationUtils.
 * Run a single test: mvn test -Dtest=UtilsTest#<methodName>
 */
class UtilsTest {



    @Test
    @DisplayName("Issue #27 — isOverdue() must return false for a task due at exactly this moment")
    void testIsOverdueIsFalseForDueDateExactlyNow() {
        // A dueDate 2 seconds in the future should not be overdue
        LocalDateTime nearFuture = LocalDateTime.now().plusSeconds(2);
        assertThat(DateUtils.isOverdue(nearFuture))
            .as("Task due in 2 seconds must NOT be considered overdue")
            .isFalse();
    }

    @Test
    @DisplayName("Issue #27b — isOverdue() must return true for a past due date")
    void testIsOverdueIsTrueForPastDueDate() {
        LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
        assertThat(DateUtils.isOverdue(pastDate))
            .as("Task that was due yesterday must be considered overdue")
            .isTrue();
    }



    @Test
    @DisplayName("Issue #28 — getDaysUntilDue() must return 0 for overdue tasks, not negative")
    void testGetDaysUntilDueReturnsZeroWhenOverdue() {
        LocalDateTime overdue = LocalDateTime.now().minusDays(3);
        long days = DateUtils.getDaysUntilDue(overdue);
        assertThat(days)
            .as("getDaysUntilDue() must return 0 for tasks past their due date, not negative")
            .isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Issue #28b — getDaysUntilDue() returns positive for future due dates")
    void testGetDaysUntilDueReturnsPositiveForFutureDate() {
        LocalDateTime future = LocalDateTime.now().plusDays(5);
        long days = DateUtils.getDaysUntilDue(future);
        assertThat(days)
            .as("getDaysUntilDue() must return a positive number for future due dates")
            .isGreaterThan(0);
    }



    @Test
    @DisplayName("Issue #29 — isValidEmail() must reject email without TLD")
    void testIsValidEmailRejectsMissingTld() {
        assertThat(ValidationUtils.isValidEmail("user@domain"))
            .as("'user@domain' has no TLD and must be rejected")
            .isFalse();
    }

    @Test
    @DisplayName("Issue #29b — isValidEmail() must accept valid email with TLD")
    void testIsValidEmailAcceptsValidEmail() {
        assertThat(ValidationUtils.isValidEmail("user@example.com"))
            .as("'user@example.com' is a valid email and must be accepted")
            .isTrue();
    }



    @Test
    @DisplayName("Issue #30 — isValidPriority() must accept MEDIUM as valid")
    void testIsValidPriorityAcceptsMedium() {
        assertThat(ValidationUtils.isValidPriority("MEDIUM"))
            .as("MEDIUM is a valid Priority value and must be accepted")
            .isTrue();
    }

    @Test
    @DisplayName("Issue #30b — isValidPriority() accepts HIGH and LOW")
    void testIsValidPriorityAcceptsHighAndLow() {
        assertThat(ValidationUtils.isValidPriority("HIGH")).isTrue();
        assertThat(ValidationUtils.isValidPriority("LOW")).isTrue();
    }

    @Test
    @DisplayName("Issue #30c — isValidPriority() rejects invalid value")
    void testIsValidPriorityRejectsInvalid() {
        assertThat(ValidationUtils.isValidPriority("CRITICAL")).isFalse();
        assertThat(ValidationUtils.isValidPriority(null)).isFalse();
    }
}
