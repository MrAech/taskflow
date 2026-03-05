package com.taskflow.dto;

import com.taskflow.model.enums.Priority;
import com.taskflow.model.enums.TaskStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private Long assignedUserId;
    private String assignedUsername;
    private ProjectDto project;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime dueDate;
}
