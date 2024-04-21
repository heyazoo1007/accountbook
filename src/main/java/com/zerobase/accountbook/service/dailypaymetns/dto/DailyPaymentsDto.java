package com.zerobase.accountbook.service.dailypaymetns.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
public class DailyPaymentsDto {
    private Long memberId;
    private Integer totalAmount;

    public DailyPaymentsDto(Long memberId, Integer totalAmount) {
        this.memberId = memberId;
        this.totalAmount = totalAmount;
    }
}
