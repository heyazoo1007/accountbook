package com.zerobase.accountbook.controller.dailypayments.dto.response;

import com.zerobase.accountbook.domain.dailypayments.DailyPayments;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetDailyPaymentsResponseDto {

    private long dailyPaymentId;
    private Integer paidAmount;
    private String payLocation;
    private String methodOfPayment;
    private Long categoryId;
    private String categoryName;
    private String memo;
    private String date;

    public static GetDailyPaymentsResponseDto of(DailyPayments dailyPayments, String categoryName) {
        return GetDailyPaymentsResponseDto
                .builder()
                .dailyPaymentId(dailyPayments.getId())
                .paidAmount(dailyPayments.getPaidAmount())
                .payLocation(dailyPayments.getPayLocation())
                .methodOfPayment(dailyPayments.getMethodOfPayment())
                .categoryId(dailyPayments.getCategoryId())
                .categoryName(categoryName)
                .memo(dailyPayments.getMemo())
                .date(dailyPayments.getDate())
                .build();
    }
}
