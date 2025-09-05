package com.developing.bluffing.game.entity;

import com.developing.bluffing.game.entity.enums.*;
import com.developing.bluffing.global.baseEntity.UuidAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends UuidAuditableEntity {

    @Enumerated(EnumType.STRING)
    private GameTeam winnerTeam;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GamePhase gamePhase;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AgeGroup taggerAge;

    @Column(nullable = false)
    private Short taggerNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MatchCategory matchCategory;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ChatTopic topic;

    @Column(nullable = false)
    private Short maxPlayer;

    @Column(nullable = false)
    private Short currentPlayer;

    public ChatRoom updatePhase(GamePhase gamePhase){
        this.gamePhase = gamePhase;
        return this;
    }

    public ChatRoom updateChatResult(GamePhase phase, GameTeam winnerTeam){
        this.gamePhase = phase;
        this.winnerTeam = winnerTeam;
        return this;
    }

}
