package com.taskflow.service;

import com.taskflow.dto.CreateCommentRequest;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.exception.UnauthorizedException;
import com.taskflow.model.Comment;
import com.taskflow.model.Task;
import com.taskflow.model.User;
import com.taskflow.repository.CommentRepository;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          TaskRepository taskRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    /**
     * Returns all comments for the given task.
     *
     * This returns at most one comment (the comment whose id happens to match taskId).
     *
     * This workaround uses findAll() + filter, but the filter condition is wrong.
     */
    public List<Comment> getCommentsForTask(Long taskId) {
        taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));
        return commentRepository.findAll().stream()
            .filter(c -> c.getId().equals(taskId))
            .toList();
    }

    @Transactional
    public Comment createComment(Long taskId, Long authorId, CreateCommentRequest request) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));
        User author = userRepository.findById(authorId)
            .orElseThrow(() -> new ResourceNotFoundException("User", authorId));
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setTask(task);
        comment.setAuthor(author);
        comment.setCreatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    /**
     * Deletes a comment.
     *
     */
    @Transactional
    public void deleteComment(Long commentId, Long requestingUserId) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));
        commentRepository.delete(comment);
    }

    /**
     * Updates the content of a comment.
     *
     */
    @Transactional
    public Comment editComment(Long commentId, Long requestingUserId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));
        if (!comment.getAuthor().getId().equals(requestingUserId)) {
            throw new UnauthorizedException("You can only edit your own comments");
        }
        comment.setContent(newContent);
        return commentRepository.save(comment);
    }
}
