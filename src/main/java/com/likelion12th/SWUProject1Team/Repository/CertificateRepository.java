package com.likelion12th.SWUProject1Team.Repository;

import com.likelion12th.SWUProject1Team.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certification, Long> {
    List<Certification> findByResume_Id(Long resumeId); // 특정 이력서 ID로 조회
    boolean existsById(Long certificateId);
    void deleteById(Long certificateId);
}
