package com.zerobase.accountbook.common.config.security;

import com.zerobase.accountbook.domain.member.Member;
import lombok.Getter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

@Getter
public class SecurityUser extends User {

    private Member member;

    public SecurityUser(Member member) {
        super(member.getEmail(), member.getPassword(),
                AuthorityUtils.createAuthorityList(member.getRole().toString()));
        this.member = member;
    }
}
