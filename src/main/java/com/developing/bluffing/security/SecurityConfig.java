package com.developing.bluffing.security;

import com.developing.bluffing.security.filter.JwtAuthenticationFilter;
import com.developing.bluffing.security.service.AccessTokenBlacklistService;
import com.developing.bluffing.security.service.impl.UserDetailImplServiceImpl;
import com.developing.bluffing.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
                // REST 기본 세팅
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requestCache(rc -> rc.disable())
                .formLogin(fl -> fl.disable())
                .httpBasic(hb -> hb.disable())

                // 인가 규칙
                .authorizeHttpRequests(reg -> reg
                        // WebSocket/SockJS는 핸드셰이크/프롤로그 때문에 열어둠
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 인증 예외(회원가입/로그인 등 공개)
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/user/id").permitAll()
                        // 그 외 보호 구간
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )

                // 실패 응답을 REST식으로 고정
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler((req, res, e) -> res.sendError(HttpServletResponse.SC_FORBIDDEN))
                )


                // JWT 필터는 가장 마지막 선에서만 동작
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtil, userDetailsService, accessTokenBlacklistService),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
