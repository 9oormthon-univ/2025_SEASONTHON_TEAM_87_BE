package com.developing.bluffing.game.service.impl;

import com.developing.bluffing.game.entity.ChatRoom;
import com.developing.bluffing.game.entity.UserInGameInfo;
import com.developing.bluffing.game.exception.UserInGameInfoException;
import com.developing.bluffing.game.exception.errorCode.UserInGameInfoErrorCode;
import com.developing.bluffing.game.repository.UserInGameInfoRepository;
import com.developing.bluffing.game.scheduler.dto.VoteResult;
import com.developing.bluffing.game.service.UserInGameInfoService;
import com.developing.bluffing.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public List<VoteResult> voteResult(List<UserInGameInfo> userInGameInfos) {
        List<VoteResult> results = new ArrayList<>();

        for (UserInGameInfo u : userInGameInfos) {
            Short voted = u.getVotedUserNumber();
            // 이미 있는 후보 찾기
            VoteResult existing = results.stream()
                    .filter(r -> r.getUserNumber().equals(voted))
                    .findFirst()
                    .orElse(null);

            if (existing == null) {
                results.add(new VoteResult(voted, (short) 1,u.getUserTeam()));
            } else {
                // 값 증가 (새 객체로 교체)
                results.remove(existing);
                results.add(new VoteResult(voted, (short) (existing.getResult() + 1),existing.getUserTeam()));
            }
        }

        return results;
    }

    @Override
    public Long countReady(ChatRoom chatRoom) {
        return repository.countReady(chatRoom.getId());
    }
}
