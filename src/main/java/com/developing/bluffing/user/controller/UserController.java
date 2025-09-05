package com.developing.bluffing.user.controller;

import com.developing.bluffing.security.entity.UserDetailImpl;
import com.developing.bluffing.user.dto.response.LoginIdCheckDuplicateResponse;
import com.developing.bluffing.user.dto.response.UserProfileSummaryResponse;
import com.developing.bluffing.user.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserFacade facade;

    @GetMapping("/summary")
    public ResponseEntity<UserProfileSummaryResponse> getUserSummary(@AuthenticationPrincipal UserDetailImpl userDetail) {
        UserProfileSummaryResponse response
                = facade.getUserSummaryInfo(userDetail.getUser());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/id")
    public ResponseEntity<LoginIdCheckDuplicateResponse> checkDuplicateLoginId(@RequestParam String loginId){
        LoginIdCheckDuplicateResponse response =
                facade.isExistLoginId(loginId);
        return ResponseEntity.ok(response);
    }

}
