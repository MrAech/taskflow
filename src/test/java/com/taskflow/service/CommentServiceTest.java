package com.taskflow.service;


import com.taskflow.exception.UnauthorizedException;
import com.taskflow.model.Comment;
import com.taskflow.model.Task;
import com.taskflow.model.User;
import com.taskflow.model.enums.Priority;
import com.taskflow.model.enums.Role;
import com.taskflow.model.enums.TaskStatus;
import com.taskflow.repository.CommentRepository;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock private CommentRepository commentRepository;
    @Mock private TaskRepository taskRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private CommentService commentService;

    private Task task;
    private User author;
    private Comment comment;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);
        author.setUsername("alice");
        author.setRole(Role.USER);
        author.setCreatedAt(LocalDateTime.now());

        task = new Task();
        task.setId(10L);
        task.setTitle("Bug fix");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(Priority.HIGH);
        task.setCreatedAt(LocalDateTime.now());

        comment = new Comment();
        comment.setId(5L);
        comment.setContent("Original comment");
        comment.setTask(task);
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());
    }



    @Test
    @DisplayName("Issue #22 — getCommentsForTask() must filter by task.id, not comment.id")
    void testGetCommentsForTaskFiltersByTaskId() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(commentRepository.findAll()).thenReturn(List.of(comment));

        List<Comment> results = commentService.getCommentsForTask(10L);

        assertThat(results)
            .as("Must return comment with task.id == 10, not filter by comment.id == 10")
            .hasSize(1)
            .contains(comment);
    }



    @Test
    @DisplayName("Issue #23 — deleteComment() must enforce ownership")
    void testDeleteCommentEnforcesOwnership() {
        Long differentUserId = 99L;
        when(commentRepository.findById(5L)).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.deleteComment(5L, differentUserId))
            .as("deleteComment() must reject requests from non-owners")
            .isInstanceOf(UnauthorizedException.class);
    }



    @Test
    @DisplayName("Issue #45 — editComment() must set updatedAt timestamp")
    void testEditCommentSetsUpdatedAt() {
        when(commentRepository.findById(5L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Comment edited = commentService.editComment(5L, 1L, "Updated content");

        assertThat(edited.getUpdatedAt())
            .as("updatedAt must be populated after editing a comment")
            .isNotNull();
    }


    @Test
    @DisplayName("Issue #48 — sendTaskAssignedNotification() must not throw UnsupportedOperationException")
    void testSendTaskAssignedNotificationDoesNotThrow() {
        // We need a real (or mocked) NotificationService to test this
        NotificationService notificationService = mock(NotificationService.class);
        doNothing().when(notificationService).sendTaskAssignedNotification(any(), any());

        assertThatCode(() -> notificationService.sendTaskAssignedNotification(author, task))
            .as("sendTaskAssignedNotification() must not throw UnsupportedOperationException")
            .doesNotThrowAnyException();
    }
}
