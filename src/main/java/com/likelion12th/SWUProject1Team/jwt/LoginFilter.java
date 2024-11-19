package com.likelion12th.SWUProject1Team.jwt;

import com.likelion12th.SWUProject1Team.repository.MemberRepository;
import com.likelion12th.SWUProject1Team.service.ReissueService;
import com.likelion12th.SWUProject1Team.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private  ReissueService reissueService;
    private final MemberRepository memberRepository;
    private CookieUtil cookieUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
                       ReissueService reissueService, MemberRepository memberRepository) {
        setFilterProcessesUrl("/api/v1/users/login");
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.reissueService =  reissueService;
        this.memberRepository = memberRepository;
        this.cookieUtil = new CookieUtil();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        System.out.println(username);

        // username, password, roll을 인자로 함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        return authenticationManager.authenticate(authToken);
    }

    // 로그인 검증이 완료되면 JWT를 발급해서 응답함
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        //유저 정보 가져오기
        String username = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // userId 추출
        int userId = memberRepository.findByUsername(username).getMemberId();


        System.out.println("LoginFilter username: " + username);
        //토큰 생성
        String access = jwtUtil.createJwt("access", username, role, 600000L);
        String refresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        // RefreshEntity 생성
        reissueService.createRefreshEntity(username, refresh, 86400000L);


        //응답 설정: response에 값 넣어주기
        response.setHeader("access", access);               // 헤더에 access 토큰을 access키에 넣어줌
        response.addCookie(cookieUtil.createCookie("refresh", refresh));   // 응답 쿠키에 refresh 토큰 넣어줌
        response.setStatus(HttpStatus.OK.value());              // 응답 상태 코드 200으로 설정
        response.setHeader("userId", userId+"");

    }

    // 로그인 실패시
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        response.setStatus(401);
    }
}