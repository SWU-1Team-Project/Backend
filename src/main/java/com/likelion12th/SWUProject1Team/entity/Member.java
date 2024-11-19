package com.likelion12th.SWUProject1Team.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@EntityListeners(value = {AutoCloseable.class})
@Table(name="member")
@Setter @Getter
@ToString
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private int memberId;

    @Column(unique = true)
    private String email; // 이메일

    private String username; // 아이디

    private String name; // 이름

    private String password;  // 비밀번호

    private String phone_number; // 전화번호

    private LocalDate birth_date; // 생년월일

    private String gender; // 성별

    // private String address;
    private String role;

}
