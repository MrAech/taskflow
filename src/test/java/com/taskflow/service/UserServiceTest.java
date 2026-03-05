package com.taskflow.service;

import com.taskflow.dto.CreateUserRequest;
import com.taskflow.model.User;
import com.taskflow.model.enums.Role;
import com.taskflow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Public tests for UserService.
 * Run a single test: mvn test -Dtest=UserServiceTest#<methodName>
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;

    @InjectMocks private UserService userService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("alice");
        existingUser.setEmail("alice@example.com");
        existingUser.setPasswordHash("hashed");
        existingUser.setRole(Role.USER);
        existingUser.setCreatedAt(LocalDateTime.now());
    }


 
    @Test
    @DisplayName("Issue #15 — findByUsername() must query by username field, not email")
    void testFindByUsernameQueriesUsernameField() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("alice")).thenReturn(Optional.of(existingUser));

        userService.findByUsername("alice");

        verify(userRepository, times(1)).findByUsername("alice");
        verify(userRepository, never()).findByEmail(anyString());
    }



    @Test
    @DisplayName("Issue #16 — createUser() must assign USER role by default")
    void testCreateUserDefaultRoleIsUser() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("securepass123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User created = userService.createUser(request);

        assertThat(created.getRole())
            .as("New user must be assigned USER role, not ADMIN")
            .isEqualTo(Role.USER);
    }



    @Test
    @DisplayName("Issue #17 — updateUser() must preserve passwordHash when newPassword is null")
    void testUpdateUserDoesNotOverwritePasswordWhenNull() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        String originalHash = existingUser.getPasswordHash();
        User updated = userService.updateUser(1L, "alice2", null, null);

        assertThat(updated.getPasswordHash())
            .as("passwordHash must not change when newPassword is null")
            .isEqualTo(originalHash);
    }



    @Test
    @DisplayName("Issue #18 — isUsernameAvailable() must return false for existing username")
    void testIsUsernameAvailableReturnsFalseWhenTaken() {
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        boolean available = userService.isUsernameAvailable("alice");

        assertThat(available)
            .as("isUsernameAvailable() must return false when the username already exists")
            .isFalse();
    }

    @Test
    @DisplayName("Issue #18b — isUsernameAvailable() must return true for a free username")
    void testIsUsernameAvailableReturnsTrueWhenFree() {
        when(userRepository.existsByUsername("brand_new_user")).thenReturn(false);

        boolean available = userService.isUsernameAvailable("brand_new_user");

        assertThat(available)
            .as("isUsernameAvailable() must return true when username does not exist")
            .isTrue();
    }



    @Test
    @DisplayName("Issue #42 — createUser() must hash password with BCrypt, not MD5")
    void testCreateUserHashesPasswordWithBCrypt() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("bobby");
        request.setEmail("bobby@example.com");
        request.setPassword("mypassword99");

        when(userRepository.existsByUsername("bobby")).thenReturn(false);
        when(userRepository.existsByEmail("bobby@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User created = userService.createUser(request);

        assertThat(created.getPasswordHash())
            .as("Password must be hashed with BCrypt (starts with $2a$), not MD5")
            .startsWith("$2a$");
    }



    @Test
    @DisplayName("Issue #49 — getPaginatedUsers() must apply the sort parameter")
    void testGetPaginatedUsersAppliesSortParameter() {
        Page<User> page = new PageImpl<>(List.of(existingUser));
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        when(userRepository.findAll(captor.capture())).thenReturn(page);

        userService.getPaginatedUsers(0, 10, "username");

        Pageable captured = captor.getValue();
        assertThat(captured.getSort().isSorted())
            .as("getPaginatedUsers() must apply sortBy='username' but currently ignores it")
            .isTrue();
        assertThat(captured.getSort().getOrderFor("username"))
            .as("Sort order for 'username' must be present")
            .isNotNull();
    }
}
