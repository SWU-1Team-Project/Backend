package com.likelion12th.SWUProject1Team.controller;

import com.likelion12th.SWUProject1Team.service.PersonalityStrengthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resume")
public class PersonalityStrengthsController {

    @Autowired
    private PersonalityStrengthService personalityStrengthService;

    @PostMapping("/strengths")
    public ResponseEntity<String> selectStrengths(@RequestBody List<String> strengths) {
        if (strengths.size() > 5) {
            return ResponseEntity.badRequest().body("최대 5개의 강점만 선택할 수 있습니다");
        }
        personalityStrengthService.saveSelectedStrengths(strengths);
        return ResponseEntity.ok("강점이 성공적으로 저장되었습니다");
    }

    @GetMapping("/strengths")
    public ResponseEntity<List<String>> getAllStrengths() {
        List<String> strengths = personalityStrengthService.getAllStrengths();
        return ResponseEntity.ok(strengths);
    }

}
