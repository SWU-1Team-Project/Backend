package com.likelion12th.SWUProject1Team.jwt;

import com.likelion12th.SWUProject1Team.entity.RefreshEntity;
import com.likelion12th.SWUProject1Team.repository.RefreshRepository;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// JWT를 만드는 코드
// username, role, 생성/만료일
@Component
public class JWTUtil {

    private SecretKey secretKey;
    @Autowired
    private RefreshRepository refreshRepository;

    // 생성자
    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {

        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // 검증 진행
    public String getUsername(String token) {

        // Jwts.parser().verifyWith(secretKey): 토큰이 우리 서버에서 생성되었는지 확인
        // parseSignedClaims: claim을 확인
        //
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) { // 토큰 만료되었는지 확ㅇ니

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    // access인지, refresh인지 정보 가져오기
    public String getCategory(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    // 토큰 생성
    // expiredMs: 토큰이 유효한 시간
    public String createJwt(String category,String username, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("category", category)        // 이 토큰에 access인지, refresh인지 검증
                .claim("username", username)        //username, role 데이터를 넣음
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis())) // 현재 토큰 발행 시간 넣음
                .expiration(new Date(System.currentTimeMillis() + expiredMs))   // 토큰 만료 시간 설정(현재발행시간 + expriedMs)
                .signWith(secretKey)        // 암호화 진행
                .compact();
    }

}
