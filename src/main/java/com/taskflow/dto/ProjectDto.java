package com.taskflow.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private String ownerUsername;
    private int memberCount;
    private int taskCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
