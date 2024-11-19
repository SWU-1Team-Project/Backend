package com.likelion12th.SWUProject1Team.Service;

import com.likelion12th.SWUProject1Team.Dto.TokenDto;
import com.likelion12th.SWUProject1Team.entity.RefreshEntity;
import com.likelion12th.SWUProject1Team.jwt.JWTUtil;
import com.likelion12th.SWUProject1Team.Repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.ExpiredJwtException;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ReissueService {

    @Autowired
    private final JWTUtil jwtUtil;
    @Autowired
    private final RefreshRepository refreshRepository;

    public TokenDto reissueAccessToken(String refreshToken) throws ExpiredJwtException, IllegalArgumentException{
        // 토큰 만료 확인
        jwtUtil.isExpired(refreshToken);

        // 토큰의 카테고리가 'refresh'인지 확인
        String category = jwtUtil.getCategory(refreshToken);
        if (!category.equals("refresh")) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // DB에서 리프레시 토큰 존재 여부 확인
        Boolean isExist = refreshRepository.existsByRefresh(refreshToken);
        if (!isExist) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // 사용자 정보 얻기
        String username = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // 새로운 액세스 토큰과 리프레시 토큰 생성
        String newAccessToken = jwtUtil.createJwt("access", username, role, 600000L);
        String newRefreshToken = jwtUtil.createJwt("refresh", username, role, 86400000L);

                // 기존 리프레시 토큰 삭제 후 새로운 리프레시 토큰 저장
        refreshRepository.deleteByRefresh(refreshToken);
        this.createRefreshEntity(username, newRefreshToken, 86400000L);

        return new TokenDto(newAccessToken, newRefreshToken);
    }

    public void createRefreshEntity(String username, String refresh, Long expiredMs) {
        // 만약 저장된 Refresh 토큰이 있다면 변경, 없으면 새로 저장
        RefreshEntity refreshEntity;
        if (! refreshRepository.existsByUsername(username)) {
            refreshEntity = new RefreshEntity();
        }
        else {
            refreshEntity = refreshRepository.findByUsername(username);
        }
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }
}
