package com.developing.bluffing.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserProfileSummaryResponse {

    private String name;
    private String age;
}
