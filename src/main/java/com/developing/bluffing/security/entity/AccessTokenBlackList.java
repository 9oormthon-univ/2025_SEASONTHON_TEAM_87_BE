package com.developing.bluffing.security.entity;

import com.developing.bluffing.user.entity.Users;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccessTokenBlackList {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private Users user;

    @Column(nullable = false , columnDefinition = "TEXT")
    private String accessToken;

    @Column(nullable = false)
    private Date expiredAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public AccessTokenBlackList(UUID id,Users user, String accessToken, Date expiredAt) {
        this.id = id;
        this.user = user;
        this.accessToken = accessToken;
        this.expiredAt = expiredAt;
    }

}
