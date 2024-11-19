package com.likelion12th.SWUProject1Team.Repository;

import com.likelion12th.SWUProject1Team.entity.RefreshEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {
    Boolean existsByRefresh(String refresh);
    Boolean existsByUsername(String username);
    RefreshEntity findByUsername(String username);

    @Transactional
    void deleteByRefresh(String refresh);
}
