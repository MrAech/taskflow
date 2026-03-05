package com.taskflow.controller;

import com.taskflow.dto.ProjectDto;
import com.taskflow.mapper.ProjectMapper;
import com.taskflow.model.Project;
import com.taskflow.model.User;
import com.taskflow.service.ProjectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Public MockMvc tests for ProjectController.
 * Run a single test: mvn test -Dtest=ProjectControllerTest#<methodName>
 */
@SpringBootTest(classes = com.taskflow.TaskflowApplication.class)
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean ProjectService projectService;
    @MockBean ProjectMapper projectMapper;
    // All repositories mocked so Spring Data does not validate query method names
    // (the intentional findByStatis typo would break context startup otherwise)
    @org.springframework.boot.test.mock.mockito.MockBean com.taskflow.repository.TaskRepository _taskRepo;
    @org.springframework.boot.test.mock.mockito.MockBean com.taskflow.repository.UserRepository _userRepo;
    @org.springframework.boot.test.mock.mockito.MockBean com.taskflow.repository.ProjectRepository _projectRepo;
    @org.springframework.boot.test.mock.mockito.MockBean com.taskflow.repository.CommentRepository _commentRepo;
    @org.springframework.boot.test.mock.mockito.MockBean com.taskflow.repository.TagRepository _tagRepo;



    @Test
    @WithMockUser
    @DisplayName("Issue #6 — GET /api/projects/owner/{id} returns 200 with empty list, not 500")
    void testGetProjectsByOwnerEmptyListReturns200() throws Exception {
        when(projectService.getProjectsByOwner(anyLong())).thenReturn(Collections.emptyList());
        when(projectMapper.toDto(org.mockito.ArgumentMatchers.any(Project.class)))
            .thenReturn(new ProjectDto());

        mockMvc.perform(get("/api/projects/owner/99"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    @WithMockUser
    @DisplayName("Issue #6 — GET /api/projects/owner/{id} returns projects when they exist")
    void testGetProjectsByOwnerWithResults() throws Exception {
        Project p = new Project();
        p.setId(1L);
        p.setName("My Project");
        User owner = new User();
        owner.setId(1L);
        p.setOwner(owner);
        p.setCreatedAt(LocalDateTime.now());

        ProjectDto dto = new ProjectDto();
        dto.setId(1L);
        dto.setName("My Project");

        when(projectService.getProjectsByOwner(1L)).thenReturn(List.of(p));
        when(projectMapper.toDto(p)).thenReturn(dto);

        mockMvc.perform(get("/api/projects/owner/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].name").value("My Project"));
    }
}
