package com.zerobase.accountbook.service.dailypaymetns.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.querydsl.core.types.dsl.NumberPath;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
public class DailyPaymentsDto {

    private Long memberId;

    private Integer totalAmount;

    @QueryProjection
    public DailyPaymentsDto(Long memberId, Integer totalAmount) {
        this.memberId = memberId;
        this.totalAmount = totalAmount;
    }
}
