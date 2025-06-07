package com.nicolas.chatapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class UserStatusDTO {
    private String userId;
    private boolean isOnline;
    private Instant lastActivityAt;
}