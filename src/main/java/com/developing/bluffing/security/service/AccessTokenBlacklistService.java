package com.developing.bluffing.security.service;

import java.util.UUID;

public interface AccessTokenBlacklistService {

    Boolean isBlackListed(UUID jti);

}
