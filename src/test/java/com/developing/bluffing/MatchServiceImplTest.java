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
        // 1. 매치 큐 초기화
        matchService.clearQueue();

        // 2. Users 테이블 초기화
        usersRepository.deleteAll();
    }

    @Test
    public void testEnqueueRegularMatch() {
        // 6명의 테스트 유저 생성 및 DB 저장
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

        // 유저 매치 등록
        matchService.enqueue(user1, MatchCategory.REGULAR);
        matchService.enqueue(user2, MatchCategory.REGULAR);
        matchService.enqueue(user3, MatchCategory.REGULAR);
        matchService.enqueue(user4, MatchCategory.REGULAR);
        matchService.enqueue(user5, MatchCategory.REGULAR);
        matchService.enqueue(user6, MatchCategory.REGULAR);

        System.out.println("6명의 유저가 큐에 정상 등록됨");

        // 중복 등록 시 예외 발생 확인
        assertThrows(UserInGameInfoException.class, () -> {
            matchService.enqueue(user1, MatchCategory.REGULAR);
        });

        System.out.println("중복 등록 시 UserInGameInfoException 발생 확인 완료");
    }
}




