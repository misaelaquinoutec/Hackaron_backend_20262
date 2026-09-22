package com.tuckersoft.branchengine.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "story_nodes")
@Getter @Setter
public class StoryNode {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(min = 3, max = 40)
    @Column(unique = true, nullable = false, length = 40)
    private String nodeCode;
    
    @NotBlank
    @Size(min = 3, max = 80)
    @Column(nullable = false, length = 80)
    private String title;
    
    @NotBlank
    @Size(min = 10)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String sceneText;
    
    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer branchCapacity;
    
    @Column(nullable = false)
    private Integer currentBranches = 0;
    
    private String primaryBranchCode;
    private String glitchBranchCode;
    
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    
    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (currentBranches == null) currentBranches = 0;
    }
}
