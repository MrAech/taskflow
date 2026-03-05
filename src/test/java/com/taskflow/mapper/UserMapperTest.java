package com.taskflow.mapper;

import com.taskflow.dto.UserDto;
import com.taskflow.model.User;
import com.taskflow.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Public unit tests for UserMapper.
 * Run a single test: mvn test -Dtest=UserMapperTest#<methodName>
 */
class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserMapper();
    }

    private User buildUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setPasswordHash("5f4dcc3b5aa765d61d8327deb882cf99");  // MD5 of "password"
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }



    @Test
    @DisplayName("Issue #26 — toDto() must not include passwordHash in response DTO")
    void testToDtoDoesNotExposePasswordHash() {
        User user = buildUser();
        UserDto dto = mapper.toDto(user);

        assertThat(dto.getPasswordHash())
            .as("UserMapper.toDto() should NOT copy passwordHash but currently does")
            .isNull();
    }


    @Test
    @DisplayName("Issue #26 — toDto() must still map id, username, email, role")
    void testToDtoMapsOtherFieldsCorrectly() {
        User user = buildUser();
        UserDto dto = mapper.toDto(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUsername()).isEqualTo("alice");
        assertThat(dto.getEmail()).isEqualTo("alice@example.com");
        assertThat(dto.getRole()).isEqualTo(Role.USER);
    }
}
