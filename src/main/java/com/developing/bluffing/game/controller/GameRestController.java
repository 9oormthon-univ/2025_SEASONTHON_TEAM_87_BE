package com.developing.bluffing.game.controller;

import com.developing.bluffing.game.dto.request.GameReadyRequest;
import com.developing.bluffing.game.dto.request.GameVoteRequest;
import com.developing.bluffing.game.facade.GameRestFacade;
import com.developing.bluffing.security.entity.UserDetailImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/game")
public class GameRestController {

    private final GameRestFacade facade;

    @PostMapping("/ready")
    public ResponseEntity<Void> ready(@AuthenticationPrincipal UserDetailImpl userDetail, GameReadyRequest r){
        facade.readyAndScheduleFlag(userDetail.getUser(),r);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/vote")
    public ResponseEntity<Void> vote(@AuthenticationPrincipal UserDetailImpl userDetail, GameVoteRequest r){
        facade.voteAndScheduleFlag(userDetail.getUser(),r);
        return ResponseEntity.ok().build();
    }

}
