package com.taskflow.controller;

import com.taskflow.dto.CreateTaskRequest;
import com.taskflow.dto.TaskDto;
import com.taskflow.dto.UpdateTaskRequest;
import com.taskflow.mapper.TaskMapper;
import com.taskflow.model.Task;
import com.taskflow.model.enums.TaskStatus;
import com.taskflow.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    /**
     * Creates a new task.
     *
     */
    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody CreateTaskRequest request) {
        Task task = taskService.createTask(request);
        return ResponseEntity.ok(taskMapper.toDto(task));
    }

    /**
     * Gets a task by ID.
     *
     */
    @GetMapping("/task/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskMapper.toDto(taskService.getTaskById(id)));
    }

    /**
     * Deletes a task by ID.
     *
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskMapper.toDto(taskService.getTaskById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id,
                                               @Valid @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.ok(taskMapper.toDto(taskService.updateTask(id, request)));
    }

    /**
     * Returns a paginated list of tasks.
     *
     * A negative 'page' value (e.g., page=-1) causes an IllegalArgumentException crash.
     */
    @GetMapping
    public ResponseEntity<Page<TaskDto>> getTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Task> tasks = taskService.getPaginatedTasks(page, size);
        return ResponseEntity.ok(tasks.map(taskMapper::toDto));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TaskDto>> searchTasks(@RequestParam String q) {
        return ResponseEntity.ok(taskService.searchTasks(q).stream()
            .map(taskMapper::toDto).toList());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskDto>> getTasksByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getTasksByUser(userId).stream()
            .map(taskMapper::toDto).toList());
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<TaskDto> completeTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskMapper.toDto(taskService.completeTask(id)));
    }

    @PostMapping("/{id}/assign/{userId}")
    public ResponseEntity<TaskDto> assignTask(@PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(taskMapper.toDto(taskService.assignTask(id, userId)));
    }


    @GetMapping("/by-status")
    public ResponseEntity<List<TaskDto>> getTasksByStatus(@RequestParam List<TaskStatus> statuses) {
        List<Task> tasks = taskService.getTasksByStatuses(statuses);
        tasks.forEach(t -> t.getComments().size());
        return ResponseEntity.ok(tasks.stream().map(taskMapper::toDto).toList());
    }
}
