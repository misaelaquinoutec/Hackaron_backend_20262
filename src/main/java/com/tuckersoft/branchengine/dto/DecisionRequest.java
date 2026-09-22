package com.tuckersoft.branchengine.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@Getter @Setter
public class DecisionRequest {
    @NotNull
    private Long playthroughId;
    
    @NotBlank
    @Size(min = 10)
    private String rawInput;
    
    @NotBlank
    @Pattern(regexp = "LEVE|MODERADO|GRAVE|CRITICO")
    private String impactLevel;
}
