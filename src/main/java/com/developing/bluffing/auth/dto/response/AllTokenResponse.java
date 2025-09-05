package com.developing.bluffing.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AllTokenResponse {
    private String accessToken;
    private String refreshToken;

    @Builder
    public AllTokenResponse(String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken =refreshToken;
    }

}
