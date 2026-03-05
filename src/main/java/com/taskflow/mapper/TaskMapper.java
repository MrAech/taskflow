package com.taskflow.mapper;

import com.taskflow.dto.CreateTaskRequest;
import com.taskflow.dto.TaskDto;
import com.taskflow.model.Task;
import com.taskflow.model.enums.TaskStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TaskMapper {

    /**
     * Converts a Task entity to a TaskDto.
     *
     */
    public TaskDto toDto(Task task) {
        if (task == null) return null;
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        dto.setDueDate(task.getDueDate());
        if (task.getAssignedUser() != null) {
            dto.setAssignedUserId(task.getAssignedUser().getId());
            dto.setAssignedUsername(task.getAssignedUser().getUsername());
        }
        return dto;
    }

    /**
     * Converts a CreateTaskRequest to a new Task entity.
     *
     */
    public Task fromCreateRequest(CreateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus(TaskStatus.TODO);
        task.setCreatedAt(LocalDateTime.now());
        task.setDueDate(task.getCreatedAt());
        return task;
    }
}
