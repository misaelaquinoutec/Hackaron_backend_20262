package com.tuckersoft.branchengine.controller;

import com.tuckersoft.branchengine.domain.StoryNode;
import com.tuckersoft.branchengine.domain.User;
import com.tuckersoft.branchengine.dto.StoryNodeDto;
import com.tuckersoft.branchengine.repository.StoryNodeRepository;
import com.tuckersoft.branchengine.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/nodes")
public class NodeController {

    private final StoryNodeRepository nodeRepository;
    private final UserRepository userRepository;

    public NodeController(StoryNodeRepository nodeRepository, UserRepository userRepository) {
        this.nodeRepository = nodeRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> createNode(@Valid @RequestBody StoryNode request, Authentication authentication) {
        String email = authentication.getName();
        Optional<User> admin = userRepository.findByEmail(email);
        if (admin.isEmpty() || !"ROLE_ADMIN".equals(admin.get().getRole())) {
            throw new AccessDeniedException("Forbidden");
        }

        if (nodeRepository.existsByNodeCode(request.getNodeCode())) {
            throw new RuntimeException("NodeCode already in use");
        }

        StoryNode saved = nodeRepository.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new StoryNodeDto(saved));
    }

    @GetMapping
    public ResponseEntity<List<StoryNodeDto>> getNodes() {
        return ResponseEntity.ok(nodeRepository.findAll().stream().map(StoryNodeDto::new).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryNodeDto> getNode(@PathVariable Long id) {
        return nodeRepository.findById(id)
                .map(node -> ResponseEntity.ok(new StoryNodeDto(node)))
                .orElseThrow(() -> new RuntimeException("Node not found"));
    }
}

