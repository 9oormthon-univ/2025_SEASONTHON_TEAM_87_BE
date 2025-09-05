package com.developing.bluffing.security.entity;

import com.developing.bluffing.global.baseEntity.UuidAuditableEntity;
import com.developing.bluffing.user.entity.Users;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends UuidAuditableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String refreshToken;

    @Column(nullable = false)
    private Date expiredAt;

    @Builder
    public RefreshToken(Users user, String refreshToken, Date expiredAt) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.expiredAt = expiredAt;
    }

    public RefreshToken updateRefreshToken(String refreshToken, Date expiredAt) {
        this.refreshToken = refreshToken;
        this.expiredAt = expiredAt;
        return this;
    }
}
