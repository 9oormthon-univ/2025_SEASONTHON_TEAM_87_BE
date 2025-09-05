package com.developing.bluffing.game.service.impl;

import com.developing.bluffing.game.entity.ChatRoom;
import com.developing.bluffing.game.entity.UserInGameInfo;
import com.developing.bluffing.game.exception.UserInGameInfoException;
import com.developing.bluffing.game.exception.errorCode.UserInGameInfoErrorCode;
import com.developing.bluffing.game.repository.UserInGameInfoRepository;
import com.developing.bluffing.game.service.UserInGameInfoService;
import com.developing.bluffing.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserInGameInfoServiceImpl implements UserInGameInfoService {

    private final UserInGameInfoRepository repository;


    @Override
    public UserInGameInfo saveOrThrow(UserInGameInfo entity) {
        try{
            return repository.save(entity);
        } catch (Exception e) {
            throw new UserInGameInfoException(UserInGameInfoErrorCode.USER_IN_GAME_INFO_NOT_FOUND_ERROR);
        }
    }

    @Override
    public UserInGameInfo getByUserAndChatRoom(Users user, ChatRoom chatRoom) {
        return repository.findByUserAndChatRoom(user, chatRoom)
                .orElseThrow( () -> new UserInGameInfoException(UserInGameInfoErrorCode.USER_IN_GAME_INFO_NOT_FOUND_ERROR));
    }

    @Override
    public List<UserInGameInfo> getByChatRoom(ChatRoom chatRoom) {
        return repository.findByChatRoom(chatRoom);
    }

    @Override
    public Long countVote(ChatRoom chatRoom) {
        return repository.countVote(chatRoom.getId());
    }

    @Override
    public Long countReady(ChatRoom chatRoom) {
        return repository.countReady(chatRoom.getId());
    }
}
