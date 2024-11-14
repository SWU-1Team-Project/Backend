package com.likelion12th.SWUProject1Team.config;


import com.likelion12th.SWUProject1Team.jwt.CustomLogoutFilter;
import com.likelion12th.SWUProject1Team.jwt.JWTFilter;
import com.likelion12th.SWUProject1Team.jwt.JWTUtil;
import com.likelion12th.SWUProject1Team.jwt.LoginFilter;
import com.likelion12th.SWUProject1Team.oauth2.CustomSuccessHandler;
import com.likelion12th.SWUProject1Team.repository.MemberRepository;
import com.likelion12th.SWUProject1Team.repository.RefreshRepository;
import com.likelion12th.SWUProject1Team.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final MemberRepository memberRepository;


    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil, RefreshRepository refreshRepository,
                          CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler, MemberRepository memberRepository) {

        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        this.memberRepository = memberRepository;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }


    // 비밀번호를 캐시로 암호화를 진행 후 사용
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //cors 설정
        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                        return configuration;
                    }
                }));


        //csrf disable: jwt 방식은 stateless 상태로 관리하기 때문에 csrf에 대한 공격을 방어하지 않아도 됨
        http
                .csrf((auth) -> auth.disable());

        //From 로그인 방식 disable(jwt 사용 때문)
        http
                .formLogin((auth) -> auth.disable());

        //http basic 인증 방식 disable(jwt 사용 때문)
        http
                .httpBasic((auth) -> auth.disable());

        //oauth2: 람다식을 통해 구현
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                );


        // 경로별 인가 작업: 권한 줌
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/api/v1/users/login", "/", "/api/v1/users/join", "/api/v1/reissue",
                                "/api/v1/users/checkEmail", "/api/v1/users/checkUsername", "/api/v1/users/checkPassword").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated());     // 위 두 줄 외 다른 경로는 모두 로그인이 필요함

        // 로그인 필터 등록 :LoginFilter전에 JWTFilter를 등록
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);
        // 로그인 필터 등록: LoginFilter를 UsernamePasswordAuthenticationFilter 대신 등록
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository, memberRepository), UsernamePasswordAuthenticationFilter.class);

        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        //세션 설정: STATELESS상태로 만들어줘야 함. 가장 중요!!!
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
