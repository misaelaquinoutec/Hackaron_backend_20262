package com.tuckersoft.branchengine.dto;

import com.tuckersoft.branchengine.domain.StoryNode;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter @Setter
public class StoryNodeDto {
    private Long id;
    private String nodeCode;
    private String title;
    private String sceneText;
    private Integer branchCapacity;
    private Integer currentBranches;
    private String primaryBranchCode;
    private String glitchBranchCode;
    private Instant createdAt;

    public StoryNodeDto(StoryNode node) {
        this.id = node.getId();
        this.nodeCode = node.getNodeCode();
        this.title = node.getTitle();
        this.sceneText = node.getSceneText();
        this.branchCapacity = node.getBranchCapacity();
        this.currentBranches = node.getCurrentBranches();
        this.primaryBranchCode = node.getPrimaryBranchCode();
        this.glitchBranchCode = node.getGlitchBranchCode();
        this.createdAt = node.getCreatedAt();
    }
}
