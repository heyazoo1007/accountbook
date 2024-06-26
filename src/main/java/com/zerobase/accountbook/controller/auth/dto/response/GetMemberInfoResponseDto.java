package com.zerobase.accountbook.controller.auth.dto.response;

import com.zerobase.accountbook.domain.member.Member;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetMemberInfoResponseDto {
    private long memberId;
    private String memberName;
    private String email;
    private String month;
    private Integer monthlyBudget;

    public static GetMemberInfoResponseDto of(Member member) {
        return GetMemberInfoResponseDto.builder()
                .memberId(member.getId())
                .memberName(member.getMemberName())
                .email(member.getEmail())
                .month(LocalDateTime.now().getMonth().toString()) // 조회할 때의 날짜 정보
                .monthlyBudget(member.getMonthlyBudget()) // 추후에 예산 객체 추가 후 수정 예정
                .build();
    }
}
