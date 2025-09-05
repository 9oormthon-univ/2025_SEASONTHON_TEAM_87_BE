package com.developing.bluffing.user.facade;

import com.developing.bluffing.user.dto.response.LoginIdCheckDuplicateResponse;
import com.developing.bluffing.user.dto.response.UserProfileSummaryResponse;
import com.developing.bluffing.user.entity.Users;
import org.springframework.transaction.annotation.Transactional;

public interface UserFacade {
    @Transactional(readOnly = true)
    UserProfileSummaryResponse getUserSummaryInfo(Users user);

    @Transactional(readOnly = true)
    LoginIdCheckDuplicateResponse isExistLoginId(String loginId);
}
