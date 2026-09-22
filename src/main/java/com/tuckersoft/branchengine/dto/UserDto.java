package com.tuckersoft.branchengine.dto;

import com.tuckersoft.branchengine.domain.User;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter @Setter
public class UserDto {
    private Long id;
    private String email;
    private String displayName;
    private String role;
    private Instant createdAt;

    public UserDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.displayName = user.getDisplayName();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
    }
}
