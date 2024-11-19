package com.likelion12th.SWUProject1Team.Dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final Oauth2UserDto oauth2UserDto;

    public CustomOAuth2User(Oauth2UserDto oauth2UserDto) {

        this.oauth2UserDto = oauth2UserDto;
    }

    @Override
    public Map<String, Object> getAttributes() {

        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return oauth2UserDto.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getName() {

        return oauth2UserDto.getName();
    }

    public String getUsername() {

        return oauth2UserDto.getUsername();
    }
}