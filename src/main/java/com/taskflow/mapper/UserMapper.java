package com.taskflow.mapper;

import com.taskflow.dto.UserDto;
import com.taskflow.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /**
     * Converts a User entity to a UserDto.
     *
     */
    public UserDto toDto(User user) {
        if (user == null) return null;
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPasswordHash(user.getPasswordHash());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
