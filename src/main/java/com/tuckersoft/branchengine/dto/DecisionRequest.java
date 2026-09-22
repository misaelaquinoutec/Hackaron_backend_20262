package com.tuckersoft.branchengine.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DecisionRequest {
    private String playerTag;
    private String rawInput;
    private String impactLevel;
}
