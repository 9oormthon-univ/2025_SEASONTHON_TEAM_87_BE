package com.developing.bluffing.security.repository;

import com.developing.bluffing.security.entity.AccessTokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccessTokenBlackListRepository extends JpaRepository<AccessTokenBlackList, UUID> {
}
