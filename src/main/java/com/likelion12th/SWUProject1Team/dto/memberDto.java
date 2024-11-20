package com.likelion12th.SWUProject1Team.dto;

import com.likelion12th.SWUProject1Team.entity.Member;

public class memberDto {
    private int userId;  // API 응답에서 userId로 노출
    private String username;
    private String email;

    public void MemberDto(Member member) {
        this.userId = member.getMemberId(); // memberId를 userId로 매핑
        this.username = member.getUsername();
        this.email = member.getEmail();
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
