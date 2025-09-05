package com.developing.bluffing.game.scheduler.task;

import com.developing.bluffing.game.entity.enums.GamePhase;
import com.developing.bluffing.game.scheduler.dto.MatchUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Getter
@Builder
@AllArgsConstructor
public class GameRoomTask implements Delayed {

    private final UUID roomId;
    private final GamePhase phase;
    private final List<MatchUser> matchUsers;
    private final long executeAtEpochMs;

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = executeAtEpochMs - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (o == this) return 0;
        long diff = this.executeAtEpochMs - ((GameRoomTask) o).executeAtEpochMs;
        return Long.compare(diff, 0);
    }

    @Override
    public String toString() {
        return "PhaseTask{" + roomId + " " + phase + " @" + executeAtEpochMs + "}";
    }
}
