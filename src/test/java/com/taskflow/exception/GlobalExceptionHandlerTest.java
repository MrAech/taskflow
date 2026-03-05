package com.taskflow.exception;

import com.taskflow.controller.TaskController;
import com.taskflow.mapper.TaskMapper;
import com.taskflow.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = com.taskflow.TaskflowApplication.class)
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired MockMvc mockMvc;

    @MockBean TaskService taskService;
    @MockBean TaskMapper taskMapper;
    // All repositories mocked so Spring Data does not validate query method names
    // (the intentional findByStatis typo would break context startup otherwise)
    @org.springframework.boot.test.mock.mockito.MockBean com.taskflow.repository.TaskRepository _taskRepo;
    @org.springframework.boot.test.mock.mockito.MockBean com.taskflow.repository.UserRepository _userRepo;
    @org.springframework.boot.test.mock.mockito.MockBean com.taskflow.repository.ProjectRepository _projectRepo;
    @org.springframework.boot.test.mock.mockito.MockBean com.taskflow.repository.CommentRepository _commentRepo;
    @org.springframework.boot.test.mock.mockito.MockBean com.taskflow.repository.TagRepository _tagRepo;



    @Test
    @WithMockUser
    @DisplayName("Issue #31 — ResourceNotFoundException must produce 404, not 500")
    void testResourceNotFoundReturns404() throws Exception {
        when(taskService.getTaskById(anyLong()))
            .thenThrow(new ResourceNotFoundException("Task", 999L));

        mockMvc.perform(get("/api/tasks/999"))
            .andExpect(status().isNotFound())
            .andExpect(status().is4xxClientError());
    }



    @Test
    @WithMockUser
    @DisplayName("Issue #32 — Error response must not expose raw exception message details")
    void testErrorResponseDoesNotLeakExceptionMessage() throws Exception {
        when(taskService.getTaskById(anyLong()))
            .thenThrow(new ResourceNotFoundException("Task", 999L));

        mockMvc.perform(get("/api/tasks/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").doesNotExist())
            .andExpect(jsonPath("$.error").exists());
    }
}
