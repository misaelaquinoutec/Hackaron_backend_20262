package com.tuckersoft.branchengine.dto;

import com.tuckersoft.branchengine.domain.Decision;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter @Setter
public class DecisionDto {
    private Long id;
    private Long playthroughId;
    private String nodeCode;
    private String rawInput;
    private String branchType;
    private String impactLevel;
    private String handlerUnit;
    private String outcomeCode;
    private String resolvedNodeCode;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    public DecisionDto(Decision decision) {
        this.id = decision.getId();
        this.playthroughId = decision.getPlaythrough().getId();
        this.nodeCode = decision.getNode().getNodeCode();
        this.rawInput = decision.getRawInput();
        this.branchType = decision.getBranchType();
        this.impactLevel = decision.getImpactLevel();
        this.handlerUnit = decision.getHandlerUnit();
        this.outcomeCode = decision.getOutcomeCode();
        this.resolvedNodeCode = decision.getResolvedNodeCode();
        this.status = decision.getStatus();
        this.createdAt = decision.getCreatedAt();
        this.updatedAt = decision.getUpdatedAt();
    }
}
