package com.developing.bluffing.security;

import com.developing.bluffing.security.filter.JwtAuthenticationFilter;
import com.developing.bluffing.security.service.impl.UserDetailImplServiceImpl;
import com.developing.bluffing.security.service.AccessTokenBlacklistService;
import com.developing.bluffing.security.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailImplServiceImpl userDetailsService;
    private final AccessTokenBlacklistService accessTokenBlacklistService;

    public SecurityConfig(JwtUtil jwtUtil, UserDetailImplServiceImpl userDetailsService, AccessTokenBlacklistService accessTokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.accessTokenBlacklistService = accessTokenBlacklistService;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 보안 기능 OFF
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 세션 completely off
                .sessionManagement(AbstractHttpConfigurer::disable)

                // 모든 요청 허용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll()
                        .anyRequest().permitAll())

                // JWT 필터는 가장 마지막 선에서만 동작
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtil, userDetailsService, accessTokenBlacklistService),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}
