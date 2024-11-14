package com.likelion12th.SWUProject1Team.service;


import com.likelion12th.SWUProject1Team.dto.PasswordDto;
import com.likelion12th.SWUProject1Team.dto.JoinDTO;
import com.likelion12th.SWUProject1Team.dto.UpdateMemberDto;
import com.likelion12th.SWUProject1Team.entity.Member;
import com.likelion12th.SWUProject1Team.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public MemberService( MemberRepository memberRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.memberRepository = memberRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinMember(JoinDTO joinDTO) {

        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        Boolean isExist = memberRepository.existsByUsername(username);

        if (isExist) {

            return;
        }

        Member data = new Member();

        data.setUsername(username);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setRole("ROLE_USER");

        memberRepository.save(data);
    }

    public void updateMember(UpdateMemberDto updateMemberDto, int userId) {
        Optional<Member> optionalMember = memberRepository.findById(userId);

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();

            if (updateMemberDto.getEmail() != null) {
                member.setEmail(updateMemberDto.getEmail());
            }
            if (updateMemberDto.getGender() != null) {
                member.setGender(updateMemberDto.getGender());
            }
            if (updateMemberDto.getPhone_number() != null) {
                member.setPhone_number(updateMemberDto.getPhone_number());
            }
            if (updateMemberDto.getName() != null) {
                member.setName(updateMemberDto.getName());
            }

            memberRepository.save(member);
        }
        else {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "ID에 해당하는 멤버를 찾을 수 없습니다: " + userId);
        }


    }

    public UpdateMemberDto getUpdateMember(int userId) {
        Optional<Member> optionalMember = memberRepository.findById(userId);
        if(optionalMember.isPresent()) {
            Member member = optionalMember.get();
            UpdateMemberDto updateMemberDto = new UpdateMemberDto();
            updateMemberDto.setName(member.getName());
            updateMemberDto.setEmail(member.getEmail());
            updateMemberDto.setGender(member.getGender());
            updateMemberDto.setPhone_number(member.getPhone_number());
            return updateMemberDto;
        } else {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "ID에 해당하는 멤버를 찾을 수 없습니다: " + userId);
        }
    }

    public boolean checkPassword(PasswordDto passwordDto) throws IllegalArgumentException{
        return passwordDto.getPassword().equals(passwordDto.getConfirmPassword());
    }

    public void updatePassword(int userId, PasswordDto passwordDto) throws HttpClientErrorException{
        Optional<Member> optionalMember = memberRepository.findById(userId);

        if (optionalMember.isPresent()) {
            if (checkPassword(passwordDto)) {
                Member member = optionalMember.get();
                member.setPassword(bCryptPasswordEncoder.encode(passwordDto.getPassword()));
                memberRepository.save(member);
            }
            else{
                throw new IllegalArgumentException("password not match");
            }
        } else {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "ID에 해당하는 멤버를 찾을 수 없습니다: " + userId);
        }


    }

    public boolean checkEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    public boolean checkUsername(String username) {
        return memberRepository.existsByUsername(username);
    }


}
