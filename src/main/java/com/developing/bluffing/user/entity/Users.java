package com.developing.bluffing.user.entity;


import com.developing.bluffing.global.baseEntity.UuidAuditableEntity;
import com.developing.bluffing.user.entity.enums.OauthProvider;
import com.developing.bluffing.user.entity.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;

@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users extends UuidAuditableEntity {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OauthProvider oauthType;

    private String oauthId;

    private String name;

    @Column(nullable = false)
    private LocalDate birth;

    @Column(unique = true)
    private String loginId;

    private String password;

}
