package com.zerobase.accountbook.controller.dailypayments.dto.response;

import com.zerobase.accountbook.domain.dailypayments.DailyPayments;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDailyPaymentsResponseDto {

    private Long dailyPaymentsId;

    private Integer paidAmount;

    private String paidWhere;

    private String categoryName;

    private String methodOfPayment;

    private String createdAt;

    public static CreateDailyPaymentsResponseDto of(DailyPayments dailyPayments) {
        return CreateDailyPaymentsResponseDto.builder()
                .dailyPaymentsId(dailyPayments.getId())
                .paidAmount(dailyPayments.getPaidAmount())
                .paidWhere(dailyPayments.getPaidWhere())
                .categoryName(dailyPayments.getCategoryName())
                .methodOfPayment(dailyPayments.getMethodOfPayment())
                .createdAt(dailyPayments.getCreatedAt())
                .build();
    }
}
