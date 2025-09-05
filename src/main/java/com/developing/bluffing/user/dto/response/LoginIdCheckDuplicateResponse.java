package com.developing.bluffing.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginIdCheckDuplicateResponse {
    private String loginId;
    private Boolean isExist;

}
