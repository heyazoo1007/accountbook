package com.zerobase.accountbook.controller.budget.dto.request;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ModifyBudgetRequestDto {

    @DateTimeFormat(pattern = "yyyy - MM")
    private String yearMonth;

    @Min(0)
    private Integer modifyMonthlyBudget;
}
