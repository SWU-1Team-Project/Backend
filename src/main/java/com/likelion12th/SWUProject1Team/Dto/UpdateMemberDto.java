package com.likelion12th.SWUProject1Team.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class UpdateMemberDto {
    private String name;
    private String phone_number;
    private String email;
    private String gender;
    private LocalDate birth_date;
}