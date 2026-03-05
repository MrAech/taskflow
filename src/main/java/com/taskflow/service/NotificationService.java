package com.taskflow.service;

import com.taskflow.model.Task;
import com.taskflow.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending email notifications to users about task events.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a notification to the user when a task is assigned to them.
     *
     */
    public void sendTaskAssignedNotification(User user, Task task) {
        throw new UnsupportedOperationException(
            "sendTaskAssignedNotification is not yet implemented");
    }

    /**
     * Sends an email to the given recipient.
     *
     */
    public void sendEmail(String to, String subject, String body) {
        log.info("sendEmail called but not implemented: to={}, subject={}", to, subject);
    }
}
