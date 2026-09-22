package com.tuckersoft.branchengine.controller;

import com.tuckersoft.branchengine.domain.Playthrough;
import com.tuckersoft.branchengine.dto.PlaythroughDto;
import com.tuckersoft.branchengine.service.PlaythroughService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/playthroughs")
public class PlaythroughController {

    private final PlaythroughService playthroughService;

    public PlaythroughController(PlaythroughService playthroughService) {
        this.playthroughService = playthroughService;
    }

    @PostMapping
    public ResponseEntity<PlaythroughDto> createPlaythrough(@RequestBody Map<String, String> request, Authentication authentication) {
        Playthrough playthrough = playthroughService.createPlaythrough(request.get("playerTag"), request.get("startNodeCode"), authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(new PlaythroughDto(playthrough));
    }

    @GetMapping
    public ResponseEntity<List<PlaythroughDto>> getPlaythroughs(Authentication authentication) {
        return ResponseEntity.ok(playthroughService.getPlaythroughs(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaythroughDto> getPlaythrough(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(playthroughService.getPlaythrough(id, authentication.getName()));
    }

    @GetMapping("/{id}/path")
    public ResponseEntity<?> getPlaythroughPath(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(playthroughService.getPlaythroughPath(id, authentication.getName()));
    }
}
