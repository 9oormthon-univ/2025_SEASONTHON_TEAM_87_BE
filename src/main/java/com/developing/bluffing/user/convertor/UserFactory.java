package com.developing.bluffing.user.convertor;

import com.developing.bluffing.game.entity.enums.AgeGroup;
import com.developing.bluffing.auth.dto.request.LocalRegisterRequest;
import com.developing.bluffing.user.dto.response.LoginIdCheckDuplicateResponse;
import com.developing.bluffing.user.dto.response.UserProfileSummaryResponse;
import com.developing.bluffing.user.entity.Users;
import com.developing.bluffing.user.entity.enums.OauthProvider;
import com.developing.bluffing.user.entity.enums.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

public class UserFactory {

    public static Users toUser(LocalRegisterRequest r, PasswordEncoder passwordEncoder){
        return Users.builder()
                .birth( LocalDate.parse(r.getBirth()))
                .name(r.getName())
                .password(passwordEncoder.encode(r.getPassword()))
                .role(UserRole.USER)
                .loginId(r.getLoginId())
                .oauthId(r.getLoginId())
                .oauthType(OauthProvider.LOCAL)
                .build();
    }

    // TODO : 예외 추가
    public static UserProfileSummaryResponse toUserProfileSummaryResponse(Users user){
        LocalDate birth = user.getBirth();
        if (birth == null) throw new IllegalArgumentException("birth is null");
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        if (birth.isAfter(today)) throw new IllegalArgumentException("birth is in the future");
        int userAge = Period.between(birth, today).getYears(); // 생일 안지났으면 자동으로 -1 처리됨
        return UserProfileSummaryResponse.builder()
                .name(user.getName())
                .age(AgeGroup.labelOf(userAge))
                .build();
    }

    public static LoginIdCheckDuplicateResponse toLoginIdCheckDuplicateResponse(String loginId,Boolean isExist){
        return new LoginIdCheckDuplicateResponse(loginId,isExist);
    }
}
