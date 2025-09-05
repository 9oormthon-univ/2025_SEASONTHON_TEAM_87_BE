package com.developing.bluffing.game.dto.request;


import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class GameVoteRequest {

    private UUID chatRoomId;
    private Short votedUserNumber;

    @Builder
    public GameVoteRequest(String chatRoomId, Short votedUserNumber) {
        this.chatRoomId = UUID.fromString(chatRoomId);
        this.votedUserNumber = votedUserNumber;
    }

    @Builder
    public GameVoteRequest(UUID chatRoomId, Short votedUserNumber) {
        this.chatRoomId = chatRoomId;
        this.votedUserNumber = votedUserNumber;
    }
}
