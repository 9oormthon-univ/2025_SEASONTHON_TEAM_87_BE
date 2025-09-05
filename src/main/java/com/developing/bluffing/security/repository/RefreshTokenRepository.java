package com.developing.bluffing.security.repository;

import com.developing.bluffing.security.entity.RefreshToken;
import com.developing.bluffing.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByUser(Users user);

}
