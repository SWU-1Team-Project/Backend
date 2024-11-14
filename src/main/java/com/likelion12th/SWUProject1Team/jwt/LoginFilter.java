package com.likelion12th.SWUProject1Team.jwt;

import com.likelion12th.SWUProject1Team.entity.RefreshEntity;
import com.likelion12th.SWUProject1Team.repository.MemberRepository;
import com.likelion12th.SWUProject1Team.repository.RefreshRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final RefreshRepository refreshRepository;
    private final MemberRepository memberRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
                       RefreshRepository refreshRepository, MemberRepository memberRepository) {
        setFilterProcessesUrl("/api/v1/users/login");
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository =  refreshRepository;
        this.memberRepository = memberRepository;
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

    // 쿠키 생성 메소드
    // key
    // value: JWT가 들어감
    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);     // 생명주기
        //cookie.setSecure(true);       // https 통신 진행 시 필요
        cookie.setPath("/");          // 쿠키 적용 범위 설정 가능
        cookie.setHttpOnly(true);       // 자바스크립트로 해당 쿠키 접근을 못하도록

        return cookie;
    }

    // 리프레쉬 토큰을 db에 저장
    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    private void updateRefreshEntity(String username, String refresh, Long expiredMs) {
        // 유저네임으로 엔티티 검색
        RefreshEntity  refreshEntity = refreshRepository.findByUsername(username);

        // 유효시간 생성
        Date date = new Date(System.currentTimeMillis() + expiredMs);
        // 정보 변경
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        // 저장
        refreshRepository.save(refreshEntity);
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
        int userId = memberRepository.findByUsername(username).getId();


        System.out.println("LoginFilter username: " + username);
        //토큰 생성
        String access = jwtUtil.createJwt("access", username, role, 600000L);
        String refresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

//         만약 저장된 Refresh 토큰이 있다면 변경, 없으면 새로 저장
        if (! refreshRepository.existsByUsername(username)) {
            //Refresh 토큰 저장
            addRefreshEntity(username, refresh, 86400000L);
            System.out.println("! refreshRepository.existsByUsername(username)");
        }
        else {
            updateRefreshEntity(username, refresh, 86400000L);
            System.out.println("refresh = refreshRepository.findByRefresh(username);");
        }


        //응답 설정: response에 값 넣어주기
        response.setHeader("access", access);               // 헤더에 access 토큰을 access키에 넣어줌
        response.addCookie(createCookie("refresh", refresh));   // 응답 쿠키에 refresh 토큰 넣어줌
        response.setStatus(HttpStatus.OK.value());              // 응답 상태 코드 200으로 설정
        response.setHeader("userId", userId+"");

    }

    // 로그인 실패시
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        response.setStatus(401);
    }
}