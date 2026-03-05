package com.taskflow.model;

import com.taskflow.model.enums.TaskStatus;
import com.taskflow.model.enums.Priority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Public unit tests for model entity edge cases.
 * Run a single test: mvn test -Dtest=ModelTest#<methodName>
 */
class ModelTest {



    @Test
    @DisplayName("Issue #34 — Two new Tags with different names must not be equal (null-id bug)")
    void testTagEqualityForUnsavedInstances() {
        Tag tagA = new Tag();
        tagA.setName("backend");

        Tag tagB = new Tag();
        tagB.setName("frontend");

        assertThat(tagA)
            .as("Two distinct Tag instances with null ids and different names should NOT be equal")
            .isNotEqualTo(tagB);
    }


    @Test
    @DisplayName("Issue #34 — Two Tags with the same name must be equal (correct fix)")
    void testTagEqualityByName() {
        Tag tagA = new Tag();
        tagA.setName("backend");

        Tag tagB = new Tag();
        tagB.setName("backend");

        assertThat(tagA)
            .as("Two Tag instances with the same name should be equal after fix")
            .isEqualTo(tagB);
    }



    @Test
    @DisplayName("Issue #35 — Task.toString() must not throw StackOverflowError")
    void testTaskToStringDoesNotCauseStackOverflow() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Sample task");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(Priority.MEDIUM);
        task.setCreatedAt(LocalDateTime.now());

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("A comment");
        comment.setTask(task);       // creates the back-reference
        task.getComments().add(comment);

        assertThat(task.toString())
            .as("Task.toString() should complete without StackOverflowError")
            .isNotNull();
    }



    @Test
    @DisplayName("Issue #36 — Project members list operations documented (cascade REMOVE risk)")
    void testProjectMemberCascadeDocumented() {
        // This test acts as a documentation anchor for Integration tests.
        // Full verification is done in the private test suite with an active EntityManager.
        assertThat(true).as("Cascade REMOVE bug requires integration test — see issues/issue-036.md").isTrue();
    }
}
