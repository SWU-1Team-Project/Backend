package com.likelion12th.SWUProject1Team.controller;


import com.likelion12th.SWUProject1Team.Dto.TokenDto;
import com.likelion12th.SWUProject1Team.jwt.JWTUtil;
import com.likelion12th.SWUProject1Team.Repository.RefreshRepository;
import com.likelion12th.SWUProject1Team.Service.ReissueService;
import com.likelion12th.SWUProject1Team.util.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequiredArgsConstructor
public class ReissueController {

    private JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    @Autowired
    private CookieUtil cookieUtil;
    @Autowired
    private ReissueService reissueService;

//    @PostMapping("/api/v1/reissue")
//    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
//
//        //get refresh token: 쿠키에서 받을 리프레쉬 토큰 저장할 변수
//        String refresh = null;
//
//        // 모든 쿠키 가져옴
//        Cookie[] cookies = request.getCookies();
//        for (Cookie cookie : cookies) {
//            // 리프레쉬가 있다면 변수에 저장
//            if (cookie.getName().equals("refresh")) {
//
//                refresh = cookie.getValue();
//            }
//        }
//
//        // 리프레쉬 토큰이 널인지(없는지) 확인
//        if (refresh == null) {
//
//            //response status code
//            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
//        }
//
//        //expired check
//        try {
//            jwtUtil.isExpired(refresh);
//        } catch (ExpiredJwtException e) {
//
//            //response status code
//            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
//        }
//
//        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
//        String category = jwtUtil.getCategory(refresh);
//
//        if (!category.equals("refresh")) {
//
//            //response status code
//            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
//        }
//
//        //DB에 저장되어 있는지 확인
//        Boolean isExist = refreshRepository.existsByRefresh(refresh);
//        if (!isExist) {
//
//            //response body
//            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
//        }
//
//        String username = jwtUtil.getUsername(refresh);
//        String role = jwtUtil.getRole(refresh);
//
//        //make new JWT: 새로운 access, refresh 토큰을 발급
//        String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
//        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);
//
//        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
//        refreshRepository.deleteByRefresh(refresh);
//        addRefreshEntity(username, newRefresh, 86400000L);
//
//        //response
//        response.setHeader("access", newAccess);
//        response.addCookie(cookieUtil.createCookie("refresh", newRefresh));
//
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    @PostMapping("/api/v1/reissue")
    public ResponseEntity<String> reissue(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키에서 리프레시 토큰 가져오기
        //get refresh token: 쿠키에서 받을 리프레쉬 토큰 저장할 변수
        String refresh = null;

        // 모든 쿠키 가져옴
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            // 리프레쉬가 있다면 변수에 저장
            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        // 리프레쉬 토큰이 널인지(없는지) 확인
        if (refresh == null) {
            //response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }


        try {
            // 새로운 액세스 토큰을 발급받음
            TokenDto tokenDto = reissueService.reissueAccessToken(refresh);

            // 새 액세스 토큰과 리프레시 토큰을 응답에 설정
            response.setHeader("access", tokenDto.getAccessToken());
            response.addCookie(cookieUtil.createCookie("refresh", tokenDto.getRefreshToken()));

            return ResponseEntity.status(HttpStatus.OK).body("accee token reissue");
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("refresh token expired");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid refresh token");
        } catch (Error error) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
