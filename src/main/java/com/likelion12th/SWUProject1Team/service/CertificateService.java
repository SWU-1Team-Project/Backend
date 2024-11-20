package com.likelion12th.SWUProject1Team.service;

import com.likelion12th.SWUProject1Team.dto.CertificateDto;
import com.likelion12th.SWUProject1Team.entity.Certification;
import com.likelion12th.SWUProject1Team.entity.Resume;
import com.likelion12th.SWUProject1Team.repository.CertificateRepository;
import com.likelion12th.SWUProject1Team.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final ResumeRepository resumeRepository;

    // 자격증 저장
    /*
    @Transactional
    public void saveCertificate(Long resumeId, CertificateDto certificationDto) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        Certification certification = new Certification();
        certification.setName(certificationDto.getName());
        certification.setDate(certificationDto.getDate());
        certification.setResume(resume);

        CertificateRepository.save(certification);
    }

     */

    // 특정 이력서에 자격증 저장
    @Transactional
    public void saveCertificates(Long resumeId, List<CertificateDto> certificates) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        List<Certification> certifications = certificates.stream()
                .map(certDto -> new Certification(
                        certDto.getName(),
                        certDto.getDate(),
                        resume))
                .collect(Collectors.toList());

        certificateRepository.saveAll(certifications);
    }

    public List<CertificateDto> getCertificatesByResumeId(Long resumeId) {
        List<Certification> certifications = certificateRepository.findByResume_Id(resumeId);
        return certifications.stream()
                .map(cert -> new CertificateDto(cert.getName(), cert.getDate()))
                .collect(Collectors.toList());
    }
    @Transactional
    public boolean deleteCertificateById(Long certificateId) {
        if (certificateRepository.existsById(certificateId)) {
            certificateRepository.deleteById(certificateId);
            return true;
        } else {
            return false;
        }
    }

}
