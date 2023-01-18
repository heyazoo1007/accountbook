package com.zerobase.accountbook.controller.budget.dto.response;

import com.zerobase.accountbook.domain.budget.Budget;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ModifyBudgetResponseDto {

    private String memberName;

    @DateTimeFormat(pattern = "yyyy - MM")
    private String budgetYearMonth;

    private Integer modifyMonthlyBudget;

    public static ModifyBudgetResponseDto of(Budget budget) {
        return ModifyBudgetResponseDto.builder()
                .memberName(budget.getMember().getMemberName())
                .budgetYearMonth(budget.getBudgetYearMonth())
                .modifyMonthlyBudget(budget.getMonthlyBudget())
                .build();

    }
}
