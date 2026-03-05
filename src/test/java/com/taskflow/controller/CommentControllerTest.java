package com.taskflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskflow.dto.CommentDto;
import com.taskflow.dto.CreateCommentRequest;
import com.taskflow.mapper.CommentMapper;
import com.taskflow.model.Comment;
import com.taskflow.model.Task;
import com.taskflow.model.User;
import com.taskflow.service.CommentService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Public MockMvc tests for CommentController.
 * Run a single test: mvn test -Dtest=CommentControllerTest#<methodName>
 */
@SpringBootTest(classes = com.taskflow.TaskflowApplication.class)
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean CommentService commentService;
    @MockBean CommentMapper commentMapper;
    // All repositories mocked so Spring Data does not validate query method names
    // (the intentional findByStatis typo would break context startup otherwise)
    @org.springframework.boot.test.mock.mockito.MockBean com.taskflow.repository.TaskRepository _taskRepo;
    @org.springframework.boot.test.mock.mockito.MockBean com.taskflow.repository.UserRepository _userRepo;
    @org.springframework.boot.test.mock.mockito.MockBean com.taskflow.repository.ProjectRepository _projectRepo;
    @org.springframework.boot.test.mock.mockito.MockBean com.taskflow.repository.CommentRepository _commentRepo;
    @org.springframework.boot.test.mock.mockito.MockBean com.taskflow.repository.TagRepository _tagRepo;

    private Comment sampleComment(long taskId) {
        Comment c = new Comment();
        c.setId(1L);
        c.setContent("Some comment");
        c.setCreatedAt(LocalDateTime.now());
        Task t = new Task();
        t.setId(taskId);
        c.setTask(t);
        User u = new User();
        u.setId(1L);
        c.setAuthor(u);
        return c;
    }

    private CommentDto sampleCommentDto() {
        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setContent("Some comment");
        return dto;
    }


    @Test
    @WithMockUser
    @DisplayName("Issue #7 — POST /api/comments/{taskId} must accept at the correct path (no duplicate /comments)")
    void testCreateCommentCorrectPath() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("Some comment");

        when(commentService.createComment(eq(1L), eq(1L), any(CreateCommentRequest.class)))
            .thenReturn(sampleComment(1L));
        when(commentMapper.toDto(any())).thenReturn(sampleCommentDto());

        mockMvc.perform(post("/api/comments/1")
                .with(csrf())
                .param("authorId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    @DisplayName("Issue #7 — POST /api/comments/1/comments (duplicated path) is the buggy mapping, must return 404 after fix")
    void testCreateCommentDuplicatedPathReturns404() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("Some comment");

        mockMvc.perform(post("/api/comments/1/comments")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }
}
