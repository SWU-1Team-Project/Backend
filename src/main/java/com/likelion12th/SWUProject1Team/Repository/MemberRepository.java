package com.likelion12th.SWUProject1Team.Repository;

import com.likelion12th.SWUProject1Team.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Boolean existsByUsername(String username);
    Member findByUsername(String username);
    Boolean existsByEmail(String email);

}