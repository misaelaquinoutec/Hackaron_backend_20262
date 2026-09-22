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
            @RequestBody DecisionRequest request,
            @RequestHeader(value = "X-Bandersnatch-Simulate", required = false) String simulateHeader,
            Authentication authentication) {
        
        com.tuckersoft.branchengine.domain.Decision decision = (com.tuckersoft.branchengine.domain.Decision) decisionService.processDecision(request, authentication.getName(), simulateHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(new com.tuckersoft.branchengine.dto.DecisionDto(decision));
    }
}
