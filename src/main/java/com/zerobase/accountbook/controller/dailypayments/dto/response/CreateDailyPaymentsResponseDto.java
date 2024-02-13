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

    private Integer payLocation;

    private String paidWhere;

    private long categoryId;

    private String methodOfPayment;

    private String memo;

    private String createdAt;

    public static CreateDailyPaymentsResponseDto of(DailyPayments dailyPayments) {
        return CreateDailyPaymentsResponseDto.builder()
                .dailyPaymentsId(dailyPayments.getId())
                .payLocation(dailyPayments.getPaidAmount())
                .paidWhere(dailyPayments.getPayLocation())
                .categoryId(dailyPayments.getCategoryId())
                .methodOfPayment(dailyPayments.getMethodOfPayment())
                .memo(dailyPayments.getMemo())
                .createdAt(dailyPayments.getCreatedAt())
                .build();
    }
}
