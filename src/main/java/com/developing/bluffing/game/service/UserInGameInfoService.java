package com.developing.bluffing.game.service;

import com.developing.bluffing.game.entity.ChatRoom;
import com.developing.bluffing.game.entity.UserInGameInfo;
import com.developing.bluffing.game.repository.dto.GameRecord;
import com.developing.bluffing.game.scheduler.dto.VoteResult;
import com.developing.bluffing.user.entity.Users;

import java.util.List;

public interface UserInGameInfoService {

    UserInGameInfo saveOrThrow(UserInGameInfo entity);

    GameRecord getUserGameRecord(Users user);

    UserInGameInfo getByUserAndChatRoom(Users user, ChatRoom chatRoom);

    List<UserInGameInfo> getByChatRoom(ChatRoom chatRoom);

    Long countVote(ChatRoom chatRoom);

    List<VoteResult> voteResult(List<UserInGameInfo> userInGameInfos);

    Long countReady(ChatRoom chatRoom);
}
