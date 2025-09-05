package com.developing.bluffing.user.repository;

import com.developing.bluffing.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<Users, UUID> {
    Optional<Users> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
}
