package com.likelion12th.SWUProject1Team.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Certification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume; // 이 필드가 없어서 오류가 발생한 것

    private String name; // 자격증 이름
    private String date; // 취득일

    public Certification() {}

    // 생성자 추가: 자격증 이름, 취득일, 이력서 객체를 받는 생성자
    public Certification(String name, String date, Resume resume) {
        this.name = name;
        this.date = date;
        this.resume = resume;
    }
}

