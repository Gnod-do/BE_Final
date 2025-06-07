package com.nicolas.chatapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity(name = "USER_STATUS")
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatus {

    @Id
    private String userId;

    @Column(name = "is_online")
    private boolean isOnline;

    @Column(name = "last_activity_at")
    private Instant lastActivityAt;
}
