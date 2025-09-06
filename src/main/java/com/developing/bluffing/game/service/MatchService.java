package com.developing.bluffing.game.service;

import com.developing.bluffing.user.entity.Users;
import com.developing.bluffing.game.entity.enums.MatchCategory;

public interface MatchService {
    void enqueue(Users user, MatchCategory matchCategory);
}

