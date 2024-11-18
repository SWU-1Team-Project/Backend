package com.likelion12th.SWUProject1Team.util;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    public Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);     // 생명주기
        //cookie.setSecure(true);       // https 통신 진행 시 필요
        cookie.setPath("/");          // 쿠키 적용 범위 설정 가능
        cookie.setHttpOnly(true);       // 자바스크립트로 해당 쿠키 접근을 못하도록

        return cookie;
    }

}
