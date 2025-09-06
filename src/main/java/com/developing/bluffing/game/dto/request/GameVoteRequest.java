package com.developing.bluffing.game.dto.request;

import lombok.Builder;
import lombok.Getter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

@Getter
public class GameVoteRequest {

    @NotBlank(message = "chatRoomId는 필수입니다.")
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

