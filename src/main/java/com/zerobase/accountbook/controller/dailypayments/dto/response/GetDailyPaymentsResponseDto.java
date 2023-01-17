package com.zerobase.accountbook.controller.dailypayments.dto.response;

import com.zerobase.accountbook.domain.dailypayments.DailyPayments;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetDailyPaymentsResponseDto {

    private Integer paidAmount;

    private String paidWhere;

    private String methodOfPayment;

    private String categoryName;

    private String memo;

    public static GetDailyPaymentsResponseDto of(DailyPayments dailyPayments) {
        return GetDailyPaymentsResponseDto
                .builder()
                .paidAmount(dailyPayments.getPaidAmount())
                .paidWhere(dailyPayments.getPaidWhere())
                .methodOfPayment(dailyPayments.getMethodOfPayment())
                .categoryName(dailyPayments.getCategoryName())
                .memo(dailyPayments.getMemo())
                .build();
    }
}
