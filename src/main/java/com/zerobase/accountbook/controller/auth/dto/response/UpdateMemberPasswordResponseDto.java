package com.zerobase.accountbook.controller.auth.dto.response;

import com.zerobase.accountbook.domain.member.Member;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberPasswordResponseDto {

    private long memberId;

    public static UpdateMemberPasswordResponseDto of(Member member) {
        return UpdateMemberPasswordResponseDto.builder()
                .memberId(member.getId())
                .build();
    }
}
