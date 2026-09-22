package com.tuckersoft.branchengine.dto;

import com.tuckersoft.branchengine.domain.Playthrough;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter @Setter
public class PlaythroughDto {
    private Long id;
    private String playerTag;
    private Long userId;
    private String startNodeCode;
    private String currentNodeCode;
    private Integer lucidity;
    private Integer controlLevel;
    private String status;
    private String endingCode;
    private Instant createdAt;
    private Instant updatedAt;

    public PlaythroughDto(Playthrough playthrough) {
        this.id = playthrough.getId();
        this.playerTag = playthrough.getPlayerTag();
        this.userId = playthrough.getUser().getId();
        this.startNodeCode = playthrough.getStartNodeCode();
        this.currentNodeCode = playthrough.getCurrentNode() != null ? playthrough.getCurrentNode().getNodeCode() : null;
        this.lucidity = playthrough.getLucidity();
        this.controlLevel = playthrough.getControlLevel();
        this.status = playthrough.getStatus();
        this.endingCode = playthrough.getEndingCode();
        this.createdAt = playthrough.getCreatedAt();
        this.updatedAt = playthrough.getUpdatedAt();
    }
}
