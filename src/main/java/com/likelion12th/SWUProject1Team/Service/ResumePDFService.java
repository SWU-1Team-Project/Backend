package com.likelion12th.SWUProject1Team.Service;

import com.likelion12th.SWUProject1Team.Repository.ResumeRepository;
import com.likelion12th.SWUProject1Team.entity.Member;
import com.likelion12th.SWUProject1Team.entity.Resume;
import com.likelion12th.SWUProject1Team.util.ImgReplaceElementFactory;
import com.likelion12th.SWUProject1Team.util.TemplateParser;
import com.lowagie.text.pdf.BaseFont;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ResumePDFService {
    private final TemplateParser templateParser;
    private final ImgReplaceElementFactory imgReplaceElementFactory;

    public String createPdf(Map<String, Object> map) {
        String processHtml = templateParser.parserHtmlFileToString("resume", map);
        return processHtml;
    }


    public byte[] generatePdf(Resume resume) throws IOException {

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Member member = resume.getMember();
            // 만약 멤버가 없으면 오류처리

            // 데이터를 Map에 담기
            Map<String, Object> map = new HashMap<>();
            map.put("member_name", member.getName());
            map.put("member_img", "file:" + resume.getProfileImage()); // member 이미지 가져오기
            map.put("member_birth", member.getBirth_date());
            map.put("member_phone", member.getPhone_number());
            map.put("member_email", member.getEmail());
            map.put("member_addr", resume.getFullAddress());
            map.put("academicInfo", resume.getAcademicInfoList());
            map.put("workExperiences", resume.getWorkExperienceList());
            map.put("strengths", resume.getStrengths());
            map.put("certifications", resume.getCertifications());
            map.put("growthProcess", resume.getGrowthProcess());
            map.put("personality", resume.getPersonality());
            map.put("reason", resume.getReason());
            map.put("job", resume.getJob());
            map.put("specialNote", resume.getSpecialNote());

            // HTML 템플릿으로 PDF 생성
            String processHtml = this.createPdf( map);
            ITextRenderer renderer = new ITextRenderer();
            SharedContext sharedContext = renderer.getSharedContext();
            sharedContext.setPrint(true);
            sharedContext.setInteractive(false);
//            sharedContext.setReplacedElementFactory(imgReplaceElementFactory);
            sharedContext.getTextRenderer().setSmoothingThreshold(0);

            // 폰트 설정
            renderer.getFontResolver().addFont(
                    new ClassPathResource("/static/font/NanumGothic.ttf").getURL().toString(),
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
            );

            // PDF 생성
            renderer.setDocumentFromString(processHtml);
            renderer.layout();
            renderer.createPDF(outputStream);

            return outputStream.toByteArray();
        }
    }
}
