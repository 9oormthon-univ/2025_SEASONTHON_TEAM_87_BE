package com.developing.bluffing.auth.facade;

import com.developing.bluffing.auth.dto.request.LocalLoginRequest;
import com.developing.bluffing.auth.dto.request.LocalRegisterRequest;
import com.developing.bluffing.auth.dto.response.AllTokenResponse;
import org.springframework.transaction.annotation.Transactional;

public interface LocalAuthFacade {
    AllTokenResponse login(LocalLoginRequest request);

    AllTokenResponse register(LocalRegisterRequest request);

    //AccessToken 재발급
    AllTokenResponse accessTokenReissue(String refreshToken);
}
