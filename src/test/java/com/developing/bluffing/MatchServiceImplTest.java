package com.developing.bluffing;

import com.developing.bluffing.game.entity.enums.MatchCategory;
import com.developing.bluffing.game.exception.UserInGameInfoException;
import com.developing.bluffing.game.service.MatchService;
import com.developing.bluffing.user.entity.Users;
import com.developing.bluffing.user.entity.enums.OauthProvider;
import com.developing.bluffing.user.entity.enums.UserRole;
import com.developing.bluffing.user.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class MatchServiceImplTest {

    @Autowired
    private MatchService matchService;

    @Autowired
    private UsersRepository usersRepository;

    @BeforeEach
    void setUp() {
        // 테스트 독립성을 위해 매치 큐 초기화
        matchService.clearQueue();

        // Users 테이블 초기화 (선택 사항, DB에 따라 필요)
        usersRepository.deleteAll();
    }

    @Test
    public void testEnqueueRegularMatch() {
        // 테스트용 유저 6명 생성 및 저장
        Users user1 = usersRepository.save(Users.builder()
                .role(UserRole.USER)
                .oauthType(OauthProvider.KAKAO)
                .birth(LocalDate.of(2005, 1, 1))
                .build());

        Users user2 = usersRepository.save(Users.builder()
                .role(UserRole.USER)
                .oauthType(OauthProvider.KAKAO)
                .birth(LocalDate.of(2005, 2, 2))
                .build());

        Users user3 = usersRepository.save(Users.builder()
                .role(UserRole.USER)
                .oauthType(OauthProvider.KAKAO)
                .birth(LocalDate.of(2005, 3, 3))
                .build());

        Users user4 = usersRepository.save(Users.builder()
                .role(UserRole.USER)
                .oauthType(OauthProvider.KAKAO)
                .birth(LocalDate.of(2005, 4, 4))
                .build());

        Users user5 = usersRepository.save(Users.builder()
                .role(UserRole.USER)
                .oauthType(OauthProvider.KAKAO)
                .birth(LocalDate.of(2005, 5, 5))
                .build());

        Users user6 = usersRepository.save(Users.builder()
                .role(UserRole.USER)
                .oauthType(OauthProvider.KAKAO)
                .birth(LocalDate.of(1990, 6, 6))
                .build());

        // MatchService에 유저 등록
        matchService.enqueue(user1, MatchCategory.REGULAR);
        matchService.enqueue(user2, MatchCategory.REGULAR);
        matchService.enqueue(user3, MatchCategory.REGULAR);
        matchService.enqueue(user4, MatchCategory.REGULAR);
        matchService.enqueue(user5, MatchCategory.REGULAR);
        matchService.enqueue(user6, MatchCategory.REGULAR);

        // 이미 큐에 있는 유저를 다시 등록하면 예외 발생
        assertThrows(UserInGameInfoException.class, () -> {
            matchService.enqueue(user1, MatchCategory.REGULAR);
        });
    }
}



