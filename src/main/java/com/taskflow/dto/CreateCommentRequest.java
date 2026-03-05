package com.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateCommentRequest {

    @NotBlank(message = "Comment content is required")
    @Size(min = 1, max = 2000)
    private String content;
}
