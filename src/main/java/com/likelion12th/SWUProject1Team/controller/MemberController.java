package com.likelion12th.SWUProject1Team.controller;


import com.likelion12th.SWUProject1Team.dto.JoinDTO;
import com.likelion12th.SWUProject1Team.dto.PasswordDto;
import com.likelion12th.SWUProject1Team.dto.UpdateMemberDto;
import com.likelion12th.SWUProject1Team.entity.Member;
import com.likelion12th.SWUProject1Team.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

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

    @PutMapping("/updateProfile/{userId}")
    public ResponseEntity<String> updateMember(@PathVariable int userId, @RequestBody UpdateMemberDto updateMemberDto) {
        try{
            memberService.updateMember(updateMemberDto, userId);
            return ResponseEntity.ok("update success");
        } catch (HttpClientErrorException httpClientErrorException) {
            return ResponseEntity.status(httpClientErrorException.getStatusCode()).body("update failed");
        } catch (Error e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/updateProfile/{userId}")
    public ResponseEntity<UpdateMemberDto> updateMember(@PathVariable int userId) {
        try {
            return ResponseEntity.ok( memberService.getUpdateMember(userId));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (Error e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/checkEmail")
    public ResponseEntity<String> checkEmail(@RequestParam(name = "email") String email) {
        if (memberService.checkEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("duplicate email");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("available email");
        }
    }

    @GetMapping("/checkUsername")
    public ResponseEntity<String> checkUsername(@RequestParam(name = "username") String username) {
        if (memberService.checkUsername(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("duplicate username");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("available username");
        }
    }

    @PostMapping("/checkPassword")
    public ResponseEntity<String> checkPassword(@RequestBody PasswordDto passwordDto) {

        if (! memberService.checkPassword(passwordDto)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("password mismatch");
        }
        return ResponseEntity.ok("password match");
    }


    @PutMapping("/updatePassword/{userId}")
    public ResponseEntity<String> updatePassword(@PathVariable int userId, @RequestBody PasswordDto passwordDto){
        try{
            memberService.updatePassword(userId, passwordDto);
            return ResponseEntity.ok("password update success");
        }
        catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body("멤버를 찾을 수 없습니다");
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (Error e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
