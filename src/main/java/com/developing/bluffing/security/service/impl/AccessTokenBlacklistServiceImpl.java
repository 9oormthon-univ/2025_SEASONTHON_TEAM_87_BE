package com.developing.bluffing.security.service.impl;

import com.developing.bluffing.security.repository.AccessTokenBlackListRepository;
import com.developing.bluffing.security.service.AccessTokenBlacklistService;
import com.developing.bluffing.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessTokenBlacklistServiceImpl implements AccessTokenBlacklistService {

    private final AccessTokenBlackListRepository repository;
    private final JwtUtil jwtUtil;

    //블랙리스트 여부 확인
    @Override
    public Boolean isBlackListed(UUID jti) {
        return repository.existsById(jti);
    }
}
