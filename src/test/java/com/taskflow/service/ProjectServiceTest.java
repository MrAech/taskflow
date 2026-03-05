package com.taskflow.service;

import com.taskflow.dto.CreateProjectRequest;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.model.Project;
import com.taskflow.model.Task;
import com.taskflow.model.User;
import com.taskflow.model.enums.Priority;
import com.taskflow.model.enums.Role;
import com.taskflow.model.enums.TaskStatus;
import com.taskflow.repository.ProjectRepository;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Public tests for ProjectService.
 * Run a single test: mvn test -Dtest=ProjectServiceTest#<methodName>
 */
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private UserRepository userRepository;
    @Mock private TaskRepository taskRepository;

    @InjectMocks private ProjectService projectService;

    private User owner;
    private User newMember;
    private Project project;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setUsername("alice");
        owner.setRole(Role.USER);
        owner.setCreatedAt(LocalDateTime.now());

        newMember = new User();
        newMember.setId(2L);
        newMember.setUsername("bob");
        newMember.setRole(Role.USER);
        newMember.setCreatedAt(LocalDateTime.now());

        project = new Project();
        project.setId(1L);
        project.setName("Alpha");
        project.setOwner(owner);
        project.setCreatedAt(LocalDateTime.now());
        project.setMembers(new HashSet<>());
        project.setTasks(new ArrayList<>());
    }



    @Test
    @DisplayName("Issue #19 — getProjectsByOwner() must sort by createdAt DESC")
    void testGetProjectsByOwnerSortsByCreatedAtDesc() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(projectRepository.findByOwner(eq(owner), any(Sort.class))).thenReturn(List.of(project));

        projectService.getProjectsByOwner(1L);

        verify(projectRepository).findByOwner(eq(owner), argThat(sort -> {
            Sort.Order order = sort.getOrderFor("createdAt");
            return order != null && order.getDirection() == Sort.Direction.DESC;
        }));
    }



    @Test
    @DisplayName("Issue #20 — addMemberToProject() must add the correct user")
    void testAddMemberToProjectAddsCorrectUser() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(2L)).thenReturn(Optional.of(newMember));
        when(projectRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Project result = projectService.addMemberToProject(1L, 2L);

        assertThat(result.getMembers())
            .as("Must add the new member (id=2), not the owner")
            .contains(newMember)
            .doesNotContain(owner);
    }



    @Test
    @DisplayName("Issue #21 — getTaskCountForProject() must count only this project's tasks")
    void testGetTaskCountForProjectCountsOnlyProjectTasks() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(taskRepository.findByProjectId(1L)).thenReturn(List.of(makeTask(), makeTask()));

        int count = projectService.getTaskCountForProject(1L);

        assertThat(count)
            .as("Task count must equal project-specific tasks (2), not system total (50)")
            .isEqualTo(2);
    }



    @Test
    @DisplayName("Issue #43 — removeMemberFromProject() must persist the updated member set")
    void testRemoveMemberFromProjectPersistsChange() {
        project.getMembers().add(newMember);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(userRepository.findById(2L)).thenReturn(Optional.of(newMember));
        when(projectRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        projectService.removeMemberFromProject(1L, 2L);

        verify(projectRepository, times(1)).save(project);
    }



    @Test
    @DisplayName("Issue #50 — getProjectStats() must use floating-point division for percentage")
    void testGetProjectStatsCalculatesPercentageCorrectly() {
        Task t1 = makeTask(); t1.setStatus(TaskStatus.COMPLETED);
        Task t2 = makeTask(); t2.setStatus(TaskStatus.TODO);
        Task t3 = makeTask(); t3.setStatus(TaskStatus.TODO);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(taskRepository.findByProjectId(1L)).thenReturn(List.of(t1, t2, t3));

        Map<String, Object> stats = projectService.getProjectStats(1L);

        int percent = (int) stats.get("completionPercent");
        assertThat(percent)
            .as("1/3 tasks complete must give ~33%%, not 0%% (integer division)")
            .isGreaterThanOrEqualTo(33);
    }

    private Task makeTask() {
        Task t = new Task();
        t.setTitle("task");
        t.setStatus(TaskStatus.TODO);
        t.setPriority(Priority.LOW);
        t.setCreatedAt(LocalDateTime.now());
        return t;
    }
}
