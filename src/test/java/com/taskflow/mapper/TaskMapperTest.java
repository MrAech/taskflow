package com.taskflow.mapper;

import com.taskflow.dto.TaskDto;
import com.taskflow.model.Project;
import com.taskflow.model.Task;
import com.taskflow.model.enums.Priority;
import com.taskflow.model.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Public unit tests for TaskMapper.
 * Run a single test: mvn test -Dtest=TaskMapperTest#<methodName>
 */
class TaskMapperTest {

    private TaskMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TaskMapper();
    }

    private Task buildTask() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Fix the bug");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(Priority.HIGH);
        task.setCreatedAt(LocalDateTime.of(2024, 1, 10, 9, 0));
        task.setDueDate(LocalDateTime.of(2024, 6, 30, 23, 59));

        Project project = new Project();
        project.setId(5L);
        project.setName("Alpha");
        task.setProject(project);

        return task;
    }



    @Test
    @DisplayName("Issue #24 — toDto() must set priority field")
    void testToDtoMapsPriority() {
        Task task = buildTask();
        TaskDto dto = mapper.toDto(task);

        assertThat(dto.getPriority())
            .as("TaskMapper.toDto() should copy priority but currently skips it")
            .isEqualTo(Priority.HIGH);
    }



    @Test
    @DisplayName("Issue #25 — toDto() dueDate must equal task's dueDate, not createdAt")
    void testToDtoSetsDueDateCorrectly() {
        Task task = buildTask();
        TaskDto dto = mapper.toDto(task);

        assertThat(dto.getDueDate())
            .as("TaskMapper.toDto() should use dueDate not createdAt")
            .isEqualTo(LocalDateTime.of(2024, 6, 30, 23, 59));

        assertThat(dto.getDueDate())
            .as("dueDate must differ from createdAt")
            .isNotEqualTo(task.getCreatedAt());
    }



    @Test
    @DisplayName("Issue #46 — toDto() must set project field")
    void testToDtoMapsProject() {
        Task task = buildTask();
        TaskDto dto = mapper.toDto(task);

        assertThat(dto.getProject())
            .as("TaskMapper.toDto() should map project but currently leaves it null")
            .isNotNull();

        assertThat(dto.getProject().getId())
            .as("Mapped project id should match source task's project id")
            .isEqualTo(5L);
    }


    @Test
    @DisplayName("Issue #24/#25/#46 — toDto() must map priority, dueDate, and project")
    void testToDtoMapsAllThreeFields() {
        Task task = buildTask();
        TaskDto dto = mapper.toDto(task);

        assertThat(dto.getPriority()).isNotNull();
        assertThat(dto.getDueDate()).isEqualTo(task.getDueDate());
        assertThat(dto.getProject()).isNotNull();
    }
}
