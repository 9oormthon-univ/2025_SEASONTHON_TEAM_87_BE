package com.developing.bluffing.game.dto.response;

import com.developing.bluffing.game.dto.enums.MassageReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class GameChatMessageResponse {
    private UUID roomId;
    private String content;
    private Short senderNumber;
    private MassageReference massageReference;
    private LocalDateTime sendTime;
}
