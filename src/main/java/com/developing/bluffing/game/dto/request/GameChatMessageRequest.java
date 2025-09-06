package com.developing.bluffing.game.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameChatMessageRequest {
    private UUID roomId;
    private String content;
    private Short senderNumber;
    private LocalDateTime sendTime;
}
