package com.developing.bluffing.user.service.impl;

import com.developing.bluffing.user.entity.Users;
import com.developing.bluffing.user.exception.UserException;
import com.developing.bluffing.user.exception.errorCode.UserErrorCode;
import com.developing.bluffing.user.repository.UserRepository;
import com.developing.bluffing.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public Users getByLoginId(String loginId) {
        return repository.findByLoginId(loginId)
                .orElseThrow( () -> new UserException(UserErrorCode.USER_NOT_FOUND_ERROR));
    }

    @Override
    public Users getById(UUID userId) {
        return repository.findById(userId)
                .orElseThrow( () -> new UserException(UserErrorCode.USER_NOT_FOUND_ERROR));
    }

    @Override
    public Users saveOrThrow(Users user) {
        try {
            return repository.save(user);
        } catch (Exception e) {
            throw new UserException(UserErrorCode.USER_CREATE_ERROR);
        }
    }

    @Override
    public boolean isExistByLoginId(String loginId) {
        return repository.existsByLoginId(loginId);
    }
}
