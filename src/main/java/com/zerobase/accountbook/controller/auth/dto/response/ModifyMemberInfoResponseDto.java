package com.zerobase.accountbook.controller.auth.dto.response;

import com.zerobase.accountbook.domain.member.Member;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModifyMemberInfoResponseDto {

    private String memberName;

    private String email;

    private Integer monthlyBudget;

    public static ModifyMemberInfoResponseDto of(Member member) {
        return ModifyMemberInfoResponseDto.builder()
                .memberName(member.getMemberName())
                .email(member.getEmail())
                .monthlyBudget(member.getMonthlyBudget())
                .build();
    }
}
