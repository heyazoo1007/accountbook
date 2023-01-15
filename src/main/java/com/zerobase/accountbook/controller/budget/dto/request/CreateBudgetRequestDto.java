package com.zerobase.accountbook.controller.budget.dto.request;

import lombok.*;

import javax.validation.constraints.Min;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CreateBudgetRequestDto {

    private String memberEmail;

    @Min(0)
    private Integer monthlyBudget;
}
