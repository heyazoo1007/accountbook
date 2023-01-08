package com.zerobase.accountbook.controller.dailypayments.dto.response;

import com.zerobase.accountbook.domain.dailypayments.DailyPayments;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDailyPaymentsResponseDto {

    private Integer paidAmount;

    private String paidWhere;

    private String categoryName;

    private String methodOfPayment;

    public static CreateDailyPaymentsResponseDto of(DailyPayments dailyPayments) {
        return CreateDailyPaymentsResponseDto.builder()
                .paidAmount(dailyPayments.getPaidAmount())
                .paidWhere(dailyPayments.getPaidWhere())
                .categoryName(dailyPayments.getCategoryName())
                .methodOfPayment(dailyPayments.getMethodOfPayment())
                .build();
    }
}
