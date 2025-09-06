package com.developing.bluffing.game.service.impl;

import com.developing.bluffing.game.entity.ChatRoom;
import com.developing.bluffing.game.entity.UserInGameInfo;
import com.developing.bluffing.game.exception.UserInGameInfoException;
import com.developing.bluffing.game.exception.errorCode.UserInGameInfoErrorCode;
import com.developing.bluffing.game.repository.UserInGameInfoRepository;
import com.developing.bluffing.game.repository.dto.GameRecord;
import com.developing.bluffing.game.scheduler.dto.VoteResult;
import com.developing.bluffing.game.service.UserInGameInfoService;
import com.developing.bluffing.user.entity.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
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
    public GameRecord getUserGameRecord(Users user){
        return repository.findGameRecordByUserId(user.getId());
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
            // 1) 무효표는 집계 제외
            Short votedBoxed = u.getVotedUserNumber();
            if (votedBoxed == null) {
                continue; // 👈 null이면 스킵! (0으로 치환하지 않음)
            }
            short voted = votedBoxed;

            // 2) 이미 있는 후보 찾기
            VoteResult existing = results.stream()
                    .filter(r -> r.getUserNumber() == voted) // primitive 비교 OK
                    .findFirst()
                    .orElse(null);

            if (existing == null) {
                // NOTE: 여기는 "투표 대상"의 팀을 넣고 싶은 경우라면
                // voted(=대상)의 팀을 찾아서 넣어야 합니다. (지금은 "투표자" 팀을 넣고 있음)
                results.add(new VoteResult(voted, (short) 1, u.getUserTeam()));
            } else {
                results.remove(existing);
                results.add(new VoteResult(voted, (short) (existing.getResult() + 1), existing.getUserTeam()));
            }
            
        }

        return results;
    }


    @Override
    public Long countReady(ChatRoom chatRoom) {
        return repository.countReady(chatRoom.getId());
    }
}
