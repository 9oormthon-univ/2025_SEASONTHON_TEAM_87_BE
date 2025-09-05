package com.developing.bluffing.game.dto.request;

import com.developing.bluffing.game.entity.enums.MatchCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameMatchRequest {
    private MatchCategory matchCategory;
}
