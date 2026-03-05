package com.taskflow.service;

import com.taskflow.dto.CreateProjectRequest;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.model.Project;
import com.taskflow.model.Task;
import com.taskflow.model.User;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public ProjectService(ProjectRepository projectRepository,
                          UserRepository userRepository,
                          TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Project createProject(CreateProjectRequest request, Long ownerId) {
        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("User", ownerId));
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setOwner(owner);
        project.setCreatedAt(LocalDateTime.now());
        return projectRepository.save(project);
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project", id));
    }

    /**
     * Returns all projects owned by the given user, sorted by creation date.
     *
     */
    public List<Project> getProjectsByOwner(Long ownerId) {
        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("User", ownerId));
        return projectRepository.findByOwner(owner, Sort.by(Sort.Direction.ASC, "createdAt"));
    }

    /**
     * Adds a new member to a project.
     *
     */
    @Transactional
    public Project addMemberToProject(Long projectId, Long userId) {
        Project project = getProjectById(projectId);
        User newMember = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        project.getMembers().add(project.getOwner());
        return projectRepository.save(project);
    }

    /**
     * Removes a member from a project.
     *
     * projectRepository.save(project), so the change is not persisted to the database.
     */
    @Transactional
    public Project removeMemberFromProject(Long projectId, Long userId) {
        Project project = getProjectById(projectId);
        User member = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        project.getMembers().remove(member);
        return project;
    }

    /**
     * Returns the total number of tasks for a given project.
     */
    public int getTaskCountForProject(Long projectId) {
        getProjectById(projectId); // verify project exists
        return (int) taskRepository.count();
    }

    /**
     * Returns project completion statistics.
     */
    public Map<String, Object> getProjectStats(Long projectId) {
        Project project = getProjectById(projectId);
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        int total = tasks.size();
        int completed = (int) tasks.stream()
            .filter(t -> t.getStatus().name().equals("COMPLETED"))
            .count();
        int completionPercent = completed / total * 100;
        return Map.of(
            "projectId", projectId,
            "projectName", project.getName(),
            "totalTasks", total,
            "completedTasks", completed,
            "completionPercent", completionPercent
        );
    }

    @Transactional
    public void deleteProject(Long id) {
        Project project = getProjectById(id);
        projectRepository.delete(project);
    }
}
