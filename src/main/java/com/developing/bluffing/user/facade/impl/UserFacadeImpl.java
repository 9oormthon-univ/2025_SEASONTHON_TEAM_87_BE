package com.developing.bluffing.user.facade.impl;

import com.developing.bluffing.user.convertor.UserFactory;
import com.developing.bluffing.user.dto.response.LoginIdCheckDuplicateResponse;
import com.developing.bluffing.user.dto.response.UserProfileSummaryResponse;
import com.developing.bluffing.user.entity.Users;
import com.developing.bluffing.user.facade.UserFacade;
import com.developing.bluffing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserService userService;

    @Transactional(readOnly = true)
    @Override
    public UserProfileSummaryResponse getUserSummaryInfo(Users user){
        return UserFactory.toUserProfileSummaryResponse(user);
    }

    @Transactional(readOnly = true)
    @Override
    public LoginIdCheckDuplicateResponse isExistLoginId(String loginId){
        Boolean isExist = userService.isExistByLoginId(loginId);
        return UserFactory.toLoginIdCheckDuplicateResponse(loginId,isExist);
    }

}
