package com.developing.bluffing.security.service.impl;

import com.developing.bluffing.security.entity.UserDetailImpl;
import com.developing.bluffing.user.entity.Users;
import com.developing.bluffing.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserDetailImplServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;


    public UserDetailImplServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetails loadUserByUserId(UUID userId) throws UsernameNotFoundException {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("user not found with userId : " + userId));
        return new UserDetailImpl(user);
    }

    @Override
    public UserDetails loadUserByUsername(String userIdToString) throws UsernameNotFoundException {
        UUID userId = UUID.fromString(userIdToString);
        Users user =userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("user not found with userId : " + userIdToString));
        return new UserDetailImpl(user);
    }

}
