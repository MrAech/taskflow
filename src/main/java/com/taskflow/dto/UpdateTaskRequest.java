package com.taskflow.dto;

import com.taskflow.model.enums.Priority;
import com.taskflow.model.enums.TaskStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UpdateTaskRequest {
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private Long assignedUserId;
    private LocalDateTime dueDate;
}
