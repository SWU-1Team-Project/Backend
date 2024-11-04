package com.likelion12th.SWUProject1Team.controller;


import com.likelion12th.SWUProject1Team.dto.JoinDTO;
import com.likelion12th.SWUProject1Team.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("/api/v1/users")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {

        this.memberService = memberService;
    }

    @PostMapping("/join")
    public String joinProcess(JoinDTO joinDTO) {

        memberService.joinMember(joinDTO);

        return "ok";
    }

}
