package com.taskflow.mapper;

import com.taskflow.dto.ProjectDto;
import com.taskflow.model.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectDto toDto(Project project) {
        if (project == null) return null;
        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());
        if (project.getOwner() != null) {
            dto.setOwnerId(project.getOwner().getId());
            dto.setOwnerUsername(project.getOwner().getUsername());
        }
        dto.setMemberCount(project.getMembers() != null ? project.getMembers().size() : 0);
        dto.setTaskCount(project.getTasks() != null ? project.getTasks().size() : 0);
        return dto;
    }
}
