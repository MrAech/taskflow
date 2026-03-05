package com.taskflow.controller;

import com.taskflow.dto.CommentDto;
import com.taskflow.dto.CreateCommentRequest;
import com.taskflow.mapper.CommentMapper;
import com.taskflow.model.Comment;
import com.taskflow.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    public CommentController(CommentService commentService, CommentMapper commentMapper) {
        this.commentService = commentService;
        this.commentMapper = commentMapper;
    }

    /**
     * Creates a comment on a task.
     *
     * to "/api/comments", making the full path "/api/comments/{taskId}/comments" — the
     * segment "comments" is duplicated.
     */
    @PostMapping("/{taskId}/comments")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable Long taskId,
            @RequestParam Long authorId,
            @Valid @RequestBody CreateCommentRequest request) {
        Comment comment = commentService.createComment(taskId, authorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentMapper.toDto(comment));
    }


    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<CommentDto>> getCommentsByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(commentService.getCommentsForTask(taskId).stream()
            .map(commentMapper::toDto).toList());
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long requestingUserId) {
        commentService.deleteComment(commentId, requestingUserId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> editComment(
            @PathVariable Long commentId,
            @RequestParam Long requestingUserId,
            @RequestParam String content) {
        return ResponseEntity.ok(
            commentMapper.toDto(commentService.editComment(commentId, requestingUserId, content)));
    }
}
