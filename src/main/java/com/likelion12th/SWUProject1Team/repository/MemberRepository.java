package com.likelion12th.SWUProject1Team.repository;

import com.likelion12th.SWUProject1Team.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Boolean existsByUsername(String username);
    Member findByUsername(String username);
    Boolean existsByEmail(String email);

    // userId로 존재 여부 확인
    boolean existsById(int userId);

}