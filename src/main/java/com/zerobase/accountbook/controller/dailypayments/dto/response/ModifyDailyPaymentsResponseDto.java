package com.zerobase.accountbook.controller.dailypayments.dto.response;

import com.zerobase.accountbook.domain.dailypayments.DailyPayments;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModifyDailyPaymentsResponseDto {

    private long dailyPaymentsId;

    private Integer paidAmount;

    private String payLocation;

    private String methodOfPayment;

    private long categoryId;

    private String memo;

    private String updatedAt;

    public static ModifyDailyPaymentsResponseDto of(DailyPayments dailyPayments) {
        return ModifyDailyPaymentsResponseDto.builder()
                .dailyPaymentsId(dailyPayments.getId())
                .paidAmount(dailyPayments.getPaidAmount())
                .payLocation(dailyPayments.getPayLocation())
                .methodOfPayment(dailyPayments.getMethodOfPayment())
                .categoryId(dailyPayments.getCategoryId())
                .memo(dailyPayments.getMemo())
                .updatedAt(dailyPayments.getUpdatedAt())
                .build();
    }
}
