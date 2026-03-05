package com.taskflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskflow.dto.CreateUserRequest;
import com.taskflow.dto.UserDto;
import com.taskflow.mapper.UserMapper;
import com.taskflow.model.User;
import com.taskflow.model.enums.Role;
import com.taskflow.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Public MockMvc tests for UserController.
 * Run a single test: mvn test -Dtest=UserControllerTest#<methodName>
 */
@SpringBootTest(classes = com.taskflow.TaskflowApplication.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean UserService userService;
    @MockBean UserMapper userMapper;
    // All repositories mocked so Spring Data does not validate query method names
    // (the intentional findByStatis typo would break context startup otherwise)
    @org.springframework.boot.test.mock.mockito.MockBean com.taskflow.repository.TaskRepository _taskRepo;
    @org.springframework.boot.test.mock.mockito.MockBean com.taskflow.repository.UserRepository _userRepo;
    @org.springframework.boot.test.mock.mockito.MockBean com.taskflow.repository.ProjectRepository _projectRepo;
    @org.springframework.boot.test.mock.mockito.MockBean com.taskflow.repository.CommentRepository _commentRepo;
    @org.springframework.boot.test.mock.mockito.MockBean com.taskflow.repository.TagRepository _tagRepo;

    private User sampleUser() {
        User u = new User();
        u.setId(1L);
        u.setUsername("alice");
        u.setEmail("alice@example.com");
        u.setRole(Role.USER);
        u.setCreatedAt(LocalDateTime.now());
        return u;
    }

    private UserDto sampleUserDto() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setUsername("alice");
        dto.setEmail("alice@example.com");
        dto.setRole(Role.USER);
        return dto;
    }



    @Test
    @DisplayName("Issue #4 — POST /api/users/register must be accessible (not /registre)")
    void testRegisterEndpointUsesCorrectPath() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("alice");
        request.setEmail("alice@example.com");
        request.setPassword("password123");

        when(userService.createUser(any())).thenReturn(sampleUser());
        when(userMapper.toDto(any())).thenReturn(sampleUserDto());

        mockMvc.perform(post("/api/users/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }



    @Test
    @WithMockUser
    @DisplayName("Issue #5 — GET /api/users/{id} must read id from URL path, not query string")
    void testGetUserByIdReadsFromPath() throws Exception {
        when(userService.getUserById(1L)).thenReturn(sampleUser());
        when(userMapper.toDto(any())).thenReturn(sampleUserDto());

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }


    @Test
    @WithMockUser
    @DisplayName("Issue #26 — GET /api/users/{id} response must not expose passwordHash")
    void testGetUserResponseDoesNotExposePassword() throws Exception {
        when(userService.getUserById(1L)).thenReturn(sampleUser());
        UserDto dtoWithPassword = sampleUserDto();
        dtoWithPassword.setPasswordHash("secret_md5_hash");
        when(userMapper.toDto(any())).thenReturn(dtoWithPassword);

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }
}
