package com.developing.bluffing.game.repository;

import com.developing.bluffing.game.entity.ChatRoom;
import com.developing.bluffing.game.entity.UserInGameInfo;
import com.developing.bluffing.game.repository.dto.GameRecord;
import com.developing.bluffing.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface UserInGameInfoRepository extends JpaRepository<UserInGameInfo, Long> {

    List<UserInGameInfo> findByUser(Users user);

    List<UserInGameInfo> findByChatRoom(ChatRoom chatRoom);

    // 전적 집계 (JPQL: 엔티티 필드명 사용)
    @Query("""
        SELECT new com.developing.bluffing.game.repository.dto.GameRecord(
            COUNT(u),
            COALESCE(SUM(CASE WHEN c.winnerTeam = u.userTeam THEN 1 ELSE 0 END), 0),
            COALESCE(SUM(CASE WHEN c.winnerTeam <> u.userTeam THEN 1 ELSE 0 END), 0)
        )
        FROM UserInGameInfo u
        JOIN u.chatRoom c
        WHERE u.user.id = :userId
          AND u.deletedAt IS NULL
          AND c.deletedAt IS NULL
          AND c.winnerTeam IS NOT NULL
    """)
    GameRecord findGameRecordByUserId(@Param("userId") UUID userId);

    // 투표한 인원 수 (COUNT는 Long로)
    @Query("""
        SELECT COUNT(u)
        FROM UserInGameInfo u
        WHERE u.chatRoom.id = :chatRoomId
          AND u.votedUserNumber IS NOT NULL
          AND u.deletedAt IS NULL
    """)
    long countVote(@Param("chatRoomId") UUID chatRoomId);

    // 레디한 인원 수
    @Query("""
        SELECT COUNT(u)
        FROM UserInGameInfo u
        WHERE u.chatRoom.id = :chatRoomId
          AND u.readyFlag = TRUE
          AND u.deletedAt IS NULL
    """)
    long countReady(@Param("chatRoomId") UUID chatRoomId);

    Optional<UserInGameInfo> findByUserAndChatRoom(Users user, ChatRoom chatRoom);
}
