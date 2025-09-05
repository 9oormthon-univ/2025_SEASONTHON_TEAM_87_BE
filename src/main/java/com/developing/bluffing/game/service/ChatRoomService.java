package com.developing.bluffing.game.service;

import com.developing.bluffing.game.entity.ChatRoom;
import com.developing.bluffing.game.entity.enums.GamePhase;
import com.developing.bluffing.game.entity.enums.GameTeam;

import java.util.UUID;

public interface ChatRoomService {

    ChatRoom saveOrThrow(ChatRoom entity);

    ChatRoom getById(UUID id);

    ChatRoom updatePhaseById(UUID id, GamePhase phase);

    ChatRoom updateChatResult(UUID id, GameTeam winnerTeam);
}
