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

    private String paidWhere;

    private String methodOfPayment;

    private String categoryName;

    private String memo;

    private String updatedAt;

    // hashTags 는 추후에 수정할 예정
    public static ModifyDailyPaymentsResponseDto of(DailyPayments dailyPayments) {
        return ModifyDailyPaymentsResponseDto.builder()
                .dailyPaymentsId(dailyPayments.getId())
                .paidAmount(dailyPayments.getPaidAmount())
                .paidWhere(dailyPayments.getPaidWhere())
                .methodOfPayment(dailyPayments.getMethodOfPayment())
                .categoryName(dailyPayments.getCategoryName())
                .memo(dailyPayments.getMemo())
                .updatedAt(dailyPayments.getUpdatedAt())
                .build();
    }
}
