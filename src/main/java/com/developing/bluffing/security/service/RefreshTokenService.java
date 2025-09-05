package com.developing.bluffing.security.service;

import com.developing.bluffing.security.entity.RefreshToken;
import com.developing.bluffing.user.entity.Users;

public interface RefreshTokenService {

    RefreshToken createOrUpdateRefreshToken(Users user, String refreshToken);
    RefreshToken getByUser(Users user);
}
