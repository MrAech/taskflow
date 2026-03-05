package com.taskflow.controller;

import com.taskflow.dto.CreateProjectRequest;
import com.taskflow.dto.ProjectDto;
import com.taskflow.mapper.ProjectMapper;
import com.taskflow.model.Project;
import com.taskflow.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    public ProjectController(ProjectService projectService, ProjectMapper projectMapper) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
    }

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            @RequestParam Long ownerId) {
        Project project = projectService.createProject(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectMapper.toDto(project));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable Long id) {
        return ResponseEntity.ok(projectMapper.toDto(projectService.getProjectById(id)));
    }


    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<ProjectDto>> getProjectsByOwner(@PathVariable Long ownerId) {
        List<Project> projects = projectService.getProjectsByOwner(ownerId);
        if (projects.isEmpty()) {
            throw new RuntimeException("No projects found for owner");
        }
        return ResponseEntity.ok(projects.stream().map(projectMapper::toDto).toList());
    }

    @PostMapping("/{id}/members/{userId}")
    public ResponseEntity<ProjectDto> addMember(@PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(projectMapper.toDto(projectService.addMemberToProject(id, userId)));
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<ProjectDto> removeMember(@PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(projectMapper.toDto(projectService.removeMemberFromProject(id, userId)));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getProjectStats(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectStats(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
