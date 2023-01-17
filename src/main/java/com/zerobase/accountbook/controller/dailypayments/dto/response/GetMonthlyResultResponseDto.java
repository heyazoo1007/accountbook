package com.zerobase.accountbook.controller.dailypayments.dto.response;

import com.zerobase.accountbook.domain.totalamountpercategory.TotalAmountPerCategory;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class GetMonthlyResultResponseDto {

    private Integer totalAmount;

    private List<MonthlyResultDto> list;

    public static GetMonthlyResultResponseDto of (
            Integer totalAmount, HashMap<String, Integer> hashMap
    ) {
        List<MonthlyResultDto> list = new ArrayList<>();
        for (String key : hashMap.keySet()) {
            list.add(MonthlyResultDto.of(key, hashMap.get(key)));
        }
        return GetMonthlyResultResponseDto.builder()
                .totalAmount(totalAmount)
                .list(list)
                .build();
    }

    public static GetMonthlyResultResponseDto of (
            Integer totalAmount, List<TotalAmountPerCategory> all
    ) {
        List<MonthlyResultDto> list = new ArrayList<>();
        for (TotalAmountPerCategory each : all) {
            list.add(MonthlyResultDto.of(
                    each.getCategoryName(),
                    each.getTotalAmount()
            ));
        }
        return GetMonthlyResultResponseDto.builder()
                .totalAmount(totalAmount)
                .list(list)
                .build();
    }
}
