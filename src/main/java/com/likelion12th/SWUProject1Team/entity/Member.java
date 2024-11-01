package com.likelion12th.SWUProject1Team.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Setter @Getter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String email;

    private String username;

    private String name;

    private String password;

    private int phone_number;

    private LocalDate birth_date;

    private String address;

    private String role;
}
