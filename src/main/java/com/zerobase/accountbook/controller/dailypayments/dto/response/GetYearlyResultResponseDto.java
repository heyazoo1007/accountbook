package com.zerobase.accountbook.controller.dailypayments.dto.response;

import com.zerobase.accountbook.service.dailypaymetns.dto.DailyPaymentsCategoryDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetYearlyResultResponseDto {

    private Integer yearlyTotalAmount;

    private List<DailyPaymentsCategoryDto> list;

    public static GetYearlyResultResponseDto of(
            Integer totalAmount, List<DailyPaymentsCategoryDto> list
    ) {
        return GetYearlyResultResponseDto.builder()
                .yearlyTotalAmount(totalAmount)
                .list(list)
                .build();
    }
}
