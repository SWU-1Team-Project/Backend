package com.likelion12th.SWUProject1Team.controller;

import com.likelion12th.SWUProject1Team.Dto.CertificateDto;
import com.likelion12th.SWUProject1Team.Service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @PostMapping(value = "/{resumeId}", consumes = "application/json", produces = "application/json; charset=UTF-8")
    public ResponseEntity<String> saveCertificates(@PathVariable Long resumeId,
                                                   @RequestBody List<CertificateDto> certificates) {
        if (certificates.isEmpty()) {
            return ResponseEntity.badRequest().body("자격증 정보가 비어 있습니다.");
        }
        certificateService.saveCertificates(resumeId, certificates);
        return ResponseEntity.ok("자격증이 성공적으로 저장되었습니다.");
    }

    @GetMapping(value = "/{resumeId}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<CertificateDto>> getCertificates(@PathVariable Long resumeId) {
        List<CertificateDto> certificates = certificateService.getCertificatesByResumeId(resumeId);
        if (certificates.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(certificates);
    }

    // 특정 자격증 삭제
    @DeleteMapping("/{certificateId}")
    public ResponseEntity<String> deleteCertificate(@PathVariable Long certificateId) {
        boolean isDeleted = certificateService.deleteCertificateById(certificateId);
        if (isDeleted) {
            return ResponseEntity.ok("자격증이 성공적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
