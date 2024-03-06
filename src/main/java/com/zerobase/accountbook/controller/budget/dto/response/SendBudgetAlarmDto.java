package com.zerobase.accountbook.controller.budget.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SendBudgetAlarmDto {
    private long memberId;
    private String message;

    public static SendBudgetAlarmDto of(long memberId, String message) {
        return SendBudgetAlarmDto.builder()
                .memberId(memberId)
                .message(message)
                .build();
    }
}
