package com.likelion12th.SWUProject1Team.service;

import com.likelion12th.SWUProject1Team.entity.PersonalityStrength;
import com.likelion12th.SWUProject1Team.repository.PersonalityStrengthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonalityStrengthService {
    @Autowired
    private PersonalityStrengthRepository personalityStrengthRepository;

    public void saveSelectedStrengths(List<String> strengths) {
        // 강점 저장 로직 (선택한 강점들을 데이터베이스에 저장)
        for (String strength : strengths) {
            PersonalityStrength personalityStrength = new PersonalityStrength();
            personalityStrength.setStrength(strength);
            personalityStrengthRepository.save(personalityStrength);
        }
    }

    public List<String> getAllStrengths() {
        // 미리 정의된 강점 목록을 조회
        return personalityStrengthRepository.findAll().stream()
                .map(PersonalityStrength::getStrength)
                .collect(Collectors.toList());
    }



}
