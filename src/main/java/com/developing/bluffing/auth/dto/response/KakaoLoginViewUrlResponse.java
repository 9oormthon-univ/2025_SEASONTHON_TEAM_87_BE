package com.developing.bluffing.auth.dto.response;

import lombok.Getter;

@Getter
public class KakaoLoginViewUrlResponse {

    private String url;

    public KakaoLoginViewUrlResponse(String url){
        this.url = url;
    }


}
