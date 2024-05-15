package com.zerobase.accountbook.controller.dailypayments.dto.response;

import com.zerobase.accountbook.domain.dailypayments.DailyPayments;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchDailyPaymentsResponseDto {

    private long dailyPaymentsId;
    private String payLocation;
    private long categoryId;
    private String methodOfPayment;
    private Integer paidAmount;
    private String memo;
    private String date;

    public static SearchDailyPaymentsResponseDto of(
            DailyPayments dailyPayments
    ) {
        return SearchDailyPaymentsResponseDto.builder()
                .dailyPaymentsId(dailyPayments.getId())
                .payLocation(dailyPayments.getPayLocation())
                .categoryId(dailyPayments.getCategoryId())
                .methodOfPayment(dailyPayments.getMethodOfPayment())
                .paidAmount(dailyPayments.getPaidAmount())
                .memo(dailyPayments.getMemo())
                .date(dailyPayments.getDate())
                .build();
    }
}
