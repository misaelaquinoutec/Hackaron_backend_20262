package com.tuckersoft.branchengine.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class DecisionCommittedEvent {
    private Long decisionId;
    private String simulateHeader;
}
