package com.taskflow.service;

import com.taskflow.dto.CreateTaskRequest;
import com.taskflow.dto.UpdateTaskRequest;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.mapper.TaskMapper;
import com.taskflow.model.Task;
import com.taskflow.model.User;
import com.taskflow.model.enums.Priority;
import com.taskflow.model.enums.Role;
import com.taskflow.model.enums.TaskStatus;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Public tests for TaskService.
 * Each test corresponds to a specific bug listed in the issues/ directory.
 * Run a single test: mvn test -Dtest=TaskServiceTest#<methodName>
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock private TaskRepository taskRepository;
    @Mock private UserRepository userRepository;
    @Mock private TaskMapper taskMapper;
    @Mock private NotificationService notificationService;

    @InjectMocks private TaskService taskService;

    private User testUser;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("alice");
        testUser.setEmail("alice@example.com");
        testUser.setRole(Role.USER);
        testUser.setCreatedAt(LocalDateTime.now());

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("A test task");
        testTask.setStatus(TaskStatus.TODO);
        testTask.setPriority(Priority.MEDIUM);
        testTask.setCreatedAt(LocalDateTime.now());
    }



    @Test
    @DisplayName("Issue #8 — createTask() must populate createdAt timestamp")
    void testCreateTaskSetsCreatedAt() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("New Task");
        request.setPriority(Priority.HIGH);

        Task mappedTask = new Task();
        mappedTask.setTitle("New Task");
        mappedTask.setPriority(Priority.HIGH);
        mappedTask.setCreatedAt(null);

        when(taskMapper.fromCreateRequest(request)).thenReturn(mappedTask);
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task saved = taskService.createTask(request);

        assertThat(saved.getCreatedAt())
            .as("createdAt must be set before saving")
            .isNotNull();
    }



    @Test
    @DisplayName("Issue #9 — updateTask() must not throw for id > 127")
    void testUpdateTaskDoesNotThrowForLargeId() {
        Long largeId = 200L;
        Task task = new Task();
        task.setId(largeId);
        task.setTitle("Original");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(Priority.LOW);
        task.setCreatedAt(LocalDateTime.now());

        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTitle("Updated");

        when(taskRepository.findById(largeId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThatCode(() -> taskService.updateTask(largeId, request))
            .as("updateTask() must not throw ResourceNotFoundException for large IDs")
            .doesNotThrowAnyException();
    }



    @Test
    @DisplayName("Issue #10 — getTasksByUser() must return only tasks for the specified user")
    void testGetTasksByUserReturnsOnlyUserTasks() {
        Task otherTask = new Task();
        otherTask.setId(99L);
        otherTask.setTitle("Other user task");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(taskRepository.findByAssignedUser(testUser)).thenReturn(List.of(testTask));

        List<Task> result = taskService.getTasksByUser(1L);

        assertThat(result)
            .as("Must only return tasks assigned to the requested user")
            .hasSize(1)
            .containsExactly(testTask);
    }



    @Test
    @DisplayName("Issue #12 — completeTask() must set status to COMPLETED")
    void testCompleteTaskSetsStatusToCompleted() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Task result = taskService.completeTask(1L);

        assertThat(result.getStatus())
            .as("Status after completeTask() must be COMPLETED, not IN_PROGRESS")
            .isEqualTo(TaskStatus.COMPLETED);
    }



    @Test
    @DisplayName("Issue #13 — getPaginatedTasks() must not subtract 1 from page number")
    void testGetPaginatedTasksDoesNotUseNegativePageIndex() {
        when(taskRepository.findAll(any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(testTask)));

        assertThatCode(() -> taskService.getPaginatedTasks(0, 10))
            .as("getPaginatedTasks(0, 10) must not throw due to page - 1 = -1")
            .doesNotThrowAnyException();
    }


 
    @Test
    @DisplayName("Issue #14 — searchTasks() must be case-insensitive")
    void testSearchTasksCaseInsensitive() {
        testTask.setTitle("Fix Login Bug");
        when(taskRepository.searchByKeyword("fix login bug")).thenReturn(List.of(testTask));

        List<Task> results = taskService.searchTasks("fix login bug");

        assertThat(results)
            .as("searchTasks() must return tasks matching keyword case-insensitively")
            .isNotEmpty();
    }


    @Test
    @DisplayName("Issue #41 — getTasksByStatuses() must use all provided statuses")
    void testGetTasksByStatusesUsesAllStatuses() {
        Task inProgressTask = new Task();
        inProgressTask.setId(2L);
        inProgressTask.setTitle("In Progress Task");
        inProgressTask.setStatus(TaskStatus.IN_PROGRESS);

        List<TaskStatus> statuses = List.of(TaskStatus.TODO, TaskStatus.IN_PROGRESS);
        when(taskRepository.findByStatusIn(List.of(TaskStatus.TODO))).thenReturn(List.of(testTask));

        List<Task> results = taskService.getTasksByStatuses(statuses);

        assertThat(results)
            .as("Must return tasks for all provided statuses, not just the first one")
            .hasSize(2);
    }



    @Test
    @DisplayName("Issue #44 — getOverdueTasks() must pass current time, not 100 years ahead")
    void testGetOverdueTasksUsesCurrentTime() {
        testTask.setDueDate(LocalDateTime.now().minusDays(5));
        when(taskRepository.findOverdueTasks(any(LocalDateTime.class))).thenReturn(List.of(testTask));

        taskService.getOverdueTasks();

        // Capture the argument passed to findOverdueTasks and verify it's not far in the future
        verify(taskRepository).findOverdueTasks(argThat(dt ->
            dt.isBefore(LocalDateTime.now().plusDays(1))
        ));
    }



    @Test
    @DisplayName("Issue #51 — bulkUpdateStatus() must be annotated @Transactional")
    void testBulkUpdateStatusIsTransactional() throws Exception {
        var method = TaskService.class.getMethod("bulkUpdateStatus", List.class, TaskStatus.class);
        boolean hasTransactional = method.isAnnotationPresent(
            org.springframework.transaction.annotation.Transactional.class);
        assertThat(hasTransactional)
            .as("bulkUpdateStatus() must have @Transactional annotation for rollback support")
            .isTrue();
    }
}
