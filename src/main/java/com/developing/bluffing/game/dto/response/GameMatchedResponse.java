package com.developing.bluffing.game.dto.response;

import com.developing.bluffing.game.entity.enums.AgeGroup;
import com.developing.bluffing.game.entity.enums.GameTeam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class GameMatchedResponse {
    private UUID roomId;
    private Short userRoomNumber;
    private AgeGroup userAge;
    private GameTeam team;
    private List<AgeGroup> citizenTeamAgeList;
    private AgeGroup mafiaTeamAge;
}
