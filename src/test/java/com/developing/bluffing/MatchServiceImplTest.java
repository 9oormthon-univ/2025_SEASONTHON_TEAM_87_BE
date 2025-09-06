package com.developing.bluffing;

import com.developing.bluffing.game.entity.enums.MatchCategory;
import com.developing.bluffing.game.service.MatchService;
import com.developing.bluffing.user.entity.Users;
import com.developing.bluffing.user.entity.enums.OauthProvider;
import com.developing.bluffing.user.entity.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
public class MatchServiceImplTest {

    @Autowired
    private MatchService matchService;

    @Test
    public void testEnqueueRegularMatch() {
        Users user1 = Users.builder()
                .role(UserRole.USER)
                .oauthType(OauthProvider.KAKAO)
                .birth(LocalDate.of(2005, 1, 1)) // 20대
                .build();

        Users user2 = Users.builder()
                .role(UserRole.USER)
                .oauthType(OauthProvider.KAKAO)
                .birth(LocalDate.of(2005, 2, 2))
                .build();

        Users user3 = Users.builder()
                .role(UserRole.USER)
                .oauthType(OauthProvider.KAKAO)
                .birth(LocalDate.of(2005, 3, 3))
                .build();

        Users user4 = Users.builder()
                .role(UserRole.USER)
                .oauthType(OauthProvider.KAKAO)
                .birth(LocalDate.of(2005, 4, 4))
                .build();

        Users user5 = Users.builder()
                .role(UserRole.USER)
                .oauthType(OauthProvider.KAKAO)
                .birth(LocalDate.of(2005, 5, 5))
                .build();

        Users user6 = Users.builder()
                .role(UserRole.USER)
                .oauthType(OauthProvider.KAKAO)
                .birth(LocalDate.of(1990, 6, 6)) // 다른 연령대
                .build();

        // MatchService 호출
        matchService.enqueue(user1, MatchCategory.REGULAR);
        matchService.enqueue(user2, MatchCategory.REGULAR);
        matchService.enqueue(user3, MatchCategory.REGULAR);
        matchService.enqueue(user4, MatchCategory.REGULAR);
        matchService.enqueue(user5, MatchCategory.REGULAR);
        matchService.enqueue(user6, MatchCategory.REGULAR);


    }
}


