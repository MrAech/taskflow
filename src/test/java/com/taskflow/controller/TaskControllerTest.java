package com.taskflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskflow.dto.CreateTaskRequest;
import com.taskflow.dto.TaskDto;
import com.taskflow.mapper.TaskMapper;
import com.taskflow.model.Task;
import com.taskflow.model.enums.Priority;
import com.taskflow.model.enums.TaskStatus;
import com.taskflow.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Public MockMvc tests for TaskController.
 * Run a single test: mvn test -Dtest=TaskControllerTest#<methodName>
 */
@SpringBootTest(classes = com.taskflow.TaskflowApplication.class)
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean TaskService taskService;
    @MockBean TaskMapper taskMapper;

    private Task sampleTask() {
        Task t = new Task();
        t.setId(1L);
        t.setTitle("Sample Task");
        t.setStatus(TaskStatus.TODO);
        t.setPriority(Priority.MEDIUM);
        t.setCreatedAt(LocalDateTime.now());
        return t;
    }

    private TaskDto sampleTaskDto() {
        TaskDto dto = new TaskDto();
        dto.setId(1L);
        dto.setTitle("Sample Task");
        dto.setStatus(TaskStatus.TODO);
        dto.setPriority(Priority.MEDIUM);
        return dto;
    }



    @Test
    @WithMockUser
    @DisplayName("Issue #1 — POST /api/tasks must return 201 Created")
    void testCreateTaskReturns201() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("New Task");
        request.setPriority(Priority.HIGH);

        when(taskService.createTask(any())).thenReturn(sampleTask());
        when(taskMapper.toDto(any())).thenReturn(sampleTaskDto());

        mockMvc.perform(post("/api/tasks")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }



    @Test
    @WithMockUser
    @DisplayName("Issue #2 — GET /api/tasks/{id} must be at the correct path")
    void testGetTaskByIdUsesCorrectPath() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(sampleTask());
        when(taskMapper.toDto(any())).thenReturn(sampleTaskDto());

        mockMvc.perform(get("/api/tasks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }



    @Test
    @WithMockUser
    @DisplayName("Issue #3 — DELETE /api/tasks/{id} must return 204 No Content")
    void testDeleteTaskReturns204() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1").with(csrf()))
            .andExpect(status().isNoContent());
    }



    @Test
    @WithMockUser
    @DisplayName("Issue #40 — GET /api/tasks?page=-1 must return 400, not 500")
    void testGetTasksNegativePageReturnsBadRequest() throws Exception {
        when(taskService.getPaginatedTasks(anyInt(), anyInt()))
            .thenThrow(new IllegalArgumentException("Page index must not be less than zero"));

        mockMvc.perform(get("/api/tasks").param("page", "-1"))
            .andExpect(status().isBadRequest());
    }
}
