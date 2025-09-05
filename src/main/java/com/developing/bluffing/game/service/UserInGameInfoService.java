package com.developing.bluffing.game.service;

import com.developing.bluffing.game.entity.ChatRoom;
import com.developing.bluffing.game.entity.UserInGameInfo;
import com.developing.bluffing.user.entity.Users;

import java.util.List;

public interface UserInGameInfoService {

    UserInGameInfo saveOrThrow(UserInGameInfo entity);

    UserInGameInfo getByUserAndChatRoom(Users user, ChatRoom chatRoom);

    List<UserInGameInfo> getByChatRoom(ChatRoom chatRoom);

    Long countVote(ChatRoom chatRoom);

    Long countReady(ChatRoom chatRoom);
}
