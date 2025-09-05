package com.developing.bluffing.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccessTokenResponse {

    private String accessToken;

}
