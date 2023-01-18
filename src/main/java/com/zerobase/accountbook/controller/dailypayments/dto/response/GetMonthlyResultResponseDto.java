package com.zerobase.accountbook.controller.dailypayments.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class GetMonthlyResultResponseDto {

    private Integer totalAmount;

    private List<MonthlyResultDto> list;

    public static GetMonthlyResultResponseDto of (
            Integer totalAmount, List<MonthlyResultDto> list
    ) {
        return GetMonthlyResultResponseDto.builder()
                .totalAmount(totalAmount)
                .list(list)
                .build();
    }
}
