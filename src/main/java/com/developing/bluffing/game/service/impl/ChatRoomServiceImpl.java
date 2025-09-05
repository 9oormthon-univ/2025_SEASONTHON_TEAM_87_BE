package com.developing.bluffing.game.service.impl;

import com.developing.bluffing.game.entity.ChatRoom;
import com.developing.bluffing.game.entity.enums.GamePhase;
import com.developing.bluffing.game.entity.enums.GameTeam;
import com.developing.bluffing.game.exception.ChatRoomException;
import com.developing.bluffing.game.exception.errorCode.ChatRoomErrorCode;
import com.developing.bluffing.game.repository.ChatRoomRepository;
import com.developing.bluffing.game.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository repository;

    @Override
    public ChatRoom saveOrThrow(ChatRoom entity){
        try{
            return repository.save(entity);
        } catch (Exception e) {
            throw new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_CREATE_ERROR);
        }
    }

    @Override
    public ChatRoom getById(UUID id) {
        return repository.findById(id)
                .orElseThrow( () -> new ChatRoomException(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND_ERROR));
    }

    @Override
    @Transactional(readOnly = false)
    public ChatRoom updatePhaseById(UUID id, GamePhase phase) {
        ChatRoom chatRoom = getById(id);
        chatRoom.updatePhase(phase);
        return saveOrThrow(chatRoom);
    }

    @Override
    @Transactional(readOnly = false)
    public ChatRoom updateChatResult(UUID id, GameTeam winnerTeam) {
        ChatRoom chatRoom = getById(id);
        chatRoom.updateChatResult(GamePhase.END,winnerTeam);
        return saveOrThrow(chatRoom);
    }

}
