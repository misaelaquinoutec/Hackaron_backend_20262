package com.tuckersoft.branchengine.controller;

import com.tuckersoft.branchengine.domain.User;
import com.tuckersoft.branchengine.dto.UserDto;
import com.tuckersoft.branchengine.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getMe(Authentication authentication) {
        String email = authentication.getName();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return ResponseEntity.ok(new UserDto(user.get()));
        }
        throw new RuntimeException("User not found");
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(Authentication authentication) {
        String email = authentication.getName();
        Optional<User> admin = userRepository.findByEmail(email);
        if (admin.isEmpty() || !"ROLE_ADMIN".equals(admin.get().getRole())) {
            throw new AccessDeniedException("Forbidden");
        }
        List<UserDto> users = userRepository.findAll().stream().map(UserDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserDto> updateRole(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication authentication) {
        String email = authentication.getName();
        Optional<User> admin = userRepository.findByEmail(email);
        if (admin.isEmpty() || !"ROLE_ADMIN".equals(admin.get().getRole())) {
            throw new AccessDeniedException("Forbidden");
        }

        String newRole = body.get("role");
        if (!"ROLE_ADMIN".equals(newRole) && !"ROLE_USER".equals(newRole)) {
            throw new RuntimeException("Invalid role");
        }

        Optional<User> targetUser = userRepository.findById(id);
        if (targetUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = targetUser.get();
        if (user.getEmail().equals(email)) {
            throw new RuntimeException("Cannot change own role");
        }

        user.setRole(newRole);
        userRepository.save(user);

        return ResponseEntity.ok(new UserDto(user));
    }
}
