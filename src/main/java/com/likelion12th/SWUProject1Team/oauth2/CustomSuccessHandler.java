package com.likelion12th.SWUProject1Team.oauth2;

import com.likelion12th.SWUProject1Team.Dto.CustomOAuth2User;
import com.likelion12th.SWUProject1Team.jwt.JWTUtil;
import com.likelion12th.SWUProject1Team.Service.ReissueService;
import com.likelion12th.SWUProject1Team.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final ReissueService reissueService;
    private final CookieUtil cookieUtil;

    public CustomSuccessHandler(JWTUtil jwtUtil, ReissueService reissueService, CookieUtil cookieUtil) {

        this.jwtUtil = jwtUtil;
        this.reissueService = reissueService;
        this.cookieUtil = cookieUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        System.out.println("onAuthenticationSuccess start");
        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        //토큰 생성
        String access = jwtUtil.createJwt("access", username, role, 600000L);
        String refresh = jwtUtil.createJwt("refresh", username, role, 86400000L);


        System.out.println("access-------------------------" + access);
        System.out.println("refresh-------------------------" + refresh);

        //Refresh 토큰 저장
        reissueService.createRefreshEntity(username, refresh, 86400000L);

        //응답 설정: response에 값 넣어주기
        response.setHeader("access", access);               // 헤더에 access 토큰을 access키에 넣어줌
        response.addCookie(cookieUtil.createCookie("refresh", refresh));   // 응답 쿠키에 refresh 토큰 넣어줌
        response.setStatus(HttpStatus.OK.value());              // 응답 상태 코드 200으로 설정


        response.sendRedirect("http://localhost:3000/");
    }
}