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

    public SecurityConfig(JwtUtil jwtUtil,
                          UserDetailImplServiceImpl userDetailsService,
                          AccessTokenBlacklistService accessTokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.accessTokenBlacklistService = accessTokenBlacklistService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .requestCache(rc -> rc.disable())
            .formLogin(fl -> fl.disable())
            .httpBasic(hb -> hb.disable())

            .authorizeHttpRequests(reg -> reg
                // WebSocket/SockJS 허용
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 인증 예외(회원가입/로그인 등 공개)
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/user/id").permitAll()

                // Swagger 허용
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/error" 
                ).permitAll()

                // 나머지 API 인증 필요
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )

            // REST 실패 응답
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .accessDeniedHandler((req, res, e) -> res.sendError(HttpServletResponse.SC_FORBIDDEN))
            )

            // JWT 필터
            .addFilterBefore(
                new JwtAuthenticationFilter(jwtUtil, userDetailsService, accessTokenBlacklistService),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

