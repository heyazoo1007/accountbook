package com.zerobase.accountbook.controller.dailypayments.dto.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class MonthlyResultDto {

    private String categoryName;

    private Integer totalAmount;

    public static MonthlyResultDto of(String key, Integer value) {
        return MonthlyResultDto.builder()
                .categoryName(key)
                .totalAmount(value)
                .build();
    }
}
