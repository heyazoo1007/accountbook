package com.zerobase.accountbook.controller.budget.dto.response;

import com.zerobase.accountbook.domain.budget.Budget;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CreateBudgetResponseDto {

    private String memberName;

    private String budgetYearMonth;

    private Integer monthlyBudget;

    public static CreateBudgetResponseDto of(Budget budget) {
        return CreateBudgetResponseDto.builder()
                .memberName(budget.getMember().getMemberName())
                .budgetYearMonth(budget.getBudgetYearMonth())
                .monthlyBudget(budget.getMonthlyBudget())
                .build();
    }
}
