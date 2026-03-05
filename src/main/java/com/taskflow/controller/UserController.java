package com.taskflow.controller;

import com.taskflow.dto.CreateUserRequest;
import com.taskflow.dto.UserDto;
import com.taskflow.mapper.UserMapper;
import com.taskflow.model.User;
import com.taskflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /**
     * Registers a new user.
     *
     */
    @PostMapping("/registre")
    public ResponseEntity<UserDto> register(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(user));
    }

    /**
     * Gets a user by ID.
     *
     * This means the endpoint expects /api/users/{id} as a path but reads id from query string.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@RequestParam("id") Long id) {
        return ResponseEntity.ok(userMapper.toDto(userService.getUserById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        Page<User> users = userService.getPaginatedUsers(page, size, sortBy);
        return ResponseEntity.ok(users.map(userMapper::toDto));
    }

    @GetMapping("/availability")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
        return ResponseEntity.ok(userService.isUsernameAvailable(username));
    }
}
