package com.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateProjectRequest {

    @NotBlank(message = "Project name is required")
    @Size(min = 3, max = 100)
    private String name;

    @Size(max = 1000)
    private String description;
}
