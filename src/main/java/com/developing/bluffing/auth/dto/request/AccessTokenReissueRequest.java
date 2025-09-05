package com.developing.bluffing.auth.dto.request;

import lombok.Getter;

@Getter
public class AccessTokenReissueRequest {
    String refreshToken;
}
