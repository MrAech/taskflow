package com.taskflow.dto;

import com.taskflow.model.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private Role role;
    private LocalDateTime createdAt;
}
