package com.zerobase.accountbook.controller.auth.dto.response;

import com.zerobase.accountbook.domain.member.Member;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModifyMemberPasswordResponseDto {

    private long memberId;

    public static ModifyMemberPasswordResponseDto of(Member member) {
        return ModifyMemberPasswordResponseDto.builder()
                .memberId(member.getId())
                .build();
    }
}
