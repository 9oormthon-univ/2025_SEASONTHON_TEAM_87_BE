package com.developing.bluffing.game.entity;

import com.developing.bluffing.game.entity.enums.AgeGroup;
import com.developing.bluffing.game.entity.enums.GameTeam;
import com.developing.bluffing.global.baseEntity.LongAuditableEntity;
import com.developing.bluffing.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        indexes = {
                @Index(name = "idx_uigi_chat_room_id", columnList = "chat_room_id")
        }
)
public class UserInGameInfo extends LongAuditableEntity{

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", columnDefinition = "BINARY(16)", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_room_id", columnDefinition = "BINARY(16)", nullable = false)
    private ChatRoom chatRoom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameTeam userTeam;

    @Column(nullable = false)
    private AgeGroup userAge;

    @Column(nullable = false)
    private Short userNumber;

    private Short votedUserNumber;

    @Column(nullable = false)
    private boolean readyFlag;

    public void vote(Short votedUserNumber){
        this.votedUserNumber = votedUserNumber;
    }

    public void ready(){
        this.readyFlag = true;
    }

    public void readyCancel(){
        this.readyFlag = false;
    }
}
