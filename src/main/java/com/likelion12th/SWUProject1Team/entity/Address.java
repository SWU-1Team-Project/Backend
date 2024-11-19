package com.likelion12th.SWUProject1Team.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "address")

public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String postcode; // 우편번호

    @Column(nullable = false)
    private String roadAddress; // 도로명 주소

    @Column(nullable = false)
    private String detailAddress; // 상세 주소

    public Address() {}

    public Address(String postcode, String roadAddress, String detailAddress) {
        this.postcode = postcode;
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
    }

    // 전체 주소를 문자열로 반환하는 메소드
    public String getFullAddress() {
        return postcode + " " + roadAddress + " " + detailAddress;
    }
}
