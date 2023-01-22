package com.zerobase.accountbook.service.dailypaymetns.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class DailyPaymentsCategoryDto {

    private String categoryName;

    private Integer totalAmount;

    @QueryProjection
    public DailyPaymentsCategoryDto(String categoryName, Integer totalAmount) {
        this.categoryName = categoryName;
        this.totalAmount = totalAmount;
    }
}
