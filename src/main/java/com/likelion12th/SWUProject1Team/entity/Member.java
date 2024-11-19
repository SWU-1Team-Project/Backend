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
    private String email;

    private String username;

    private String name;

    private String password;

    private String phone_number;

    private LocalDate birth_date;

    private String address;

    private String role;

    private String gender;
}
