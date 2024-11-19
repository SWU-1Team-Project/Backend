package com.likelion12th.SWUProject1Team.Service;

import com.likelion12th.SWUProject1Team.Dto.CustomUserDetails;
import com.likelion12th.SWUProject1Team.entity.Member;
import com.likelion12th.SWUProject1Team.Repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



// UserDetail: 로그인을 검증하는 부분
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService( MemberRepository memberRepository) {

        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member userData = memberRepository.findByUsername(username);

        if (userData != null) {

            return new CustomUserDetails(userData);
        }


        return null;
    }
}
