package com.zerobase.accountbook.controller.auth.dto.response;

import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRole;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMemberResponseDto {

    private Long memberId;
    private String memberName;
    private String email;
    private String password;
    private MemberRole role;
    private LocalDateTime createdAt;

    public static CreateMemberResponseDto of(Member member) {
        return CreateMemberResponseDto.builder()
                .memberId(member.getId())
                .memberName(member.getMemberName())
                .email(member.getEmail())
                .password(member.getEmail())
                .role(member.getRole())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
