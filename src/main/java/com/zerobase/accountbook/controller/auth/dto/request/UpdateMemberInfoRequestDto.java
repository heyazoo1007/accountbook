package com.zerobase.accountbook.controller.auth.dto.request;

import lombok.*;

import javax.validation.constraints.Min;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberInfoRequestDto {

    private long memberId;

    private String memberName;

    @Min(0)
    private Integer monthlyBudget;
}
