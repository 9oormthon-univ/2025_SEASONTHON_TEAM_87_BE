package com.developing.bluffing.user.service;

import com.developing.bluffing.user.entity.Users;

import java.util.UUID;

public interface UserService {

    Users getByLoginId(String loginId);

    Users getById(UUID userId);

    Users saveOrThrow(Users user);

    boolean isExistByLoginId(String loginId);

}
