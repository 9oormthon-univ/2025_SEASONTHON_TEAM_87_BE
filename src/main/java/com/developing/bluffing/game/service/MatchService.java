package com.developing.bluffing.game.service;

import com.developing.bluffing.game.entity.enums.MatchCategory;
import com.developing.bluffing.user.entity.Users;

public interface MatchService {
    void enqueue(Users user, MatchCategory matchCategory);
}
