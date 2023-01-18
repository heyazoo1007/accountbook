package com.zerobase.accountbook.controller.budget.dto.response;

import com.zerobase.accountbook.domain.budget.Budget;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class GetBudgetResponseDto {

    private String memberName;

    private String yearMonth;

    private Integer monthlyBudget;

    public static GetBudgetResponseDto of(Budget budget) {
        return GetBudgetResponseDto.builder()
                .memberName(budget.getMember().getMemberName())
                .yearMonth(budget.getBudgetYearMonth())
                .monthlyBudget(budget.getMonthlyBudget())
                .build();
    }
}
