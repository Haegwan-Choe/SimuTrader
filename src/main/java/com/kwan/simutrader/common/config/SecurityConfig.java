package com.kwan.simutrader.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // HTTP 보안설정을 정의하는 FilterChain Bean
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // REST API에서는 CSRF 보호가 필요하지 않으므로 비활성화(세션 방식이 아니기에)
                .formLogin(AbstractHttpConfigurer::disable) // 기본 폼 로그인 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // Http Basic 인증 비활성화

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 X, JWT 사용 시 필수
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/users/register").permitAll() // 회원가입은 모든 사용자 접근 허용
                        .requestMatchers("/api/users/login").permitAll() // 로그인도 모든 사용자 접근 허용
                        .requestMatchers("/h2-console/**").permitAll() // 개발 단계에서만 접근 허용
                        // api 문서화를 위한
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                );

        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }

}
