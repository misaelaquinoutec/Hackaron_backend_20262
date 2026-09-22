package com.tuckersoft.branchengine.controller;

import com.tuckersoft.branchengine.dto.DecisionRequest;
import com.tuckersoft.branchengine.service.DecisionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/decisions")
public class DecisionController {

    private final DecisionService decisionService;

    public DecisionController(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @PostMapping
    public ResponseEntity<com.tuckersoft.branchengine.dto.DecisionDto> createDecision(
            @jakarta.validation.Valid @RequestBody DecisionRequest request,
            @RequestHeader(value = "X-Bandersnatch-Simulate", required = false) String simulateHeader,
            Authentication authentication) {
        
        com.tuckersoft.branchengine.domain.Decision decision = (com.tuckersoft.branchengine.domain.Decision) decisionService.processDecision(request, authentication.getName(), simulateHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(new com.tuckersoft.branchengine.dto.DecisionDto(decision));
    }

    @GetMapping
    public ResponseEntity<java.util.Map<String, Object>> getDecisions(
            @RequestParam(required = false) Long playthroughId,
            @RequestParam(required = false) String branchType,
            org.springframework.data.domain.Pageable pageable,
            Authentication authentication) {
        org.springframework.data.domain.Page<com.tuckersoft.branchengine.dto.DecisionDto> page = decisionService.getDecisions(playthroughId, branchType, pageable, authentication.getName());
        return ResponseEntity.ok(java.util.Map.of(
            "content", page.getContent(),
            "currentPage", page.getNumber(),
            "totalPages", page.getTotalPages(),
            "totalElements", page.getTotalElements(),
            "size", page.getSize()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<com.tuckersoft.branchengine.dto.DecisionDto> getDecision(@PathVariable Long id) {
        com.tuckersoft.branchengine.domain.Decision decision = decisionService.getDecision(id);
        return ResponseEntity.ok(new com.tuckersoft.branchengine.dto.DecisionDto(decision));
    }

    @GetMapping("/{id}/reality-logs")
    public ResponseEntity<java.util.List<com.tuckersoft.branchengine.domain.RealityLog>> getRealityLogs(@PathVariable Long id) {
        return ResponseEntity.ok(decisionService.getRealityLogs(id));
    }
}
