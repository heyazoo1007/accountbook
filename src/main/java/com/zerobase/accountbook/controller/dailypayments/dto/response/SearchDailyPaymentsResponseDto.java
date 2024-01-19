package com.zerobase.accountbook.controller.dailypayments.dto.response;

import com.zerobase.accountbook.domain.dailypayments.DailyPayments;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchDailyPaymentsResponseDto {

    private long dailyPaymentsId;

    private String payLocation;

    private String categoryName;

    private String methodOfPayment;

    private Integer paidAmount;

    private String memo;

    private String createdAt;

    private String updatedAt;

    public static SearchDailyPaymentsResponseDto of(
            DailyPayments dailyPayments
    ) {
        return SearchDailyPaymentsResponseDto.builder()
                .dailyPaymentsId(dailyPayments.getId())
                .payLocation(dailyPayments.getPayLocation())
                .categoryName(dailyPayments.getCategoryName())
                .methodOfPayment(dailyPayments.getMethodOfPayment())
                .paidAmount(dailyPayments.getPaidAmount())
                .memo(dailyPayments.getMemo())
                .createdAt(convertDateToYearAndMonth(dailyPayments.getCreatedAt()))
                .updatedAt(convertDateToYearAndMonth(dailyPayments.getUpdatedAt()))
                .build();
    }

    private static String convertDateToYearAndMonth(String date) {
        if (date == null) {
            return "";
        }
        return date.substring(0, 10);
    }
}
