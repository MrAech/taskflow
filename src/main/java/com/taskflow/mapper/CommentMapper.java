package com.taskflow.mapper;

import com.taskflow.dto.CommentDto;
import com.taskflow.model.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentDto toDto(Comment comment) {
        if (comment == null) return null;
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        if (comment.getTask() != null) dto.setTaskId(comment.getTask().getId());
        if (comment.getAuthor() != null) {
            dto.setAuthorId(comment.getAuthor().getId());
            dto.setAuthorUsername(comment.getAuthor().getUsername());
        }
        return dto;
    }
}
