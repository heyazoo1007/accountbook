package com.zerobase.accountbook.controller.dailypayments.dto.request;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDailyPaymentsRequestDto {

    @Min(0)
    private Integer paidAmount;

    @Size(max = 10, message = "가게 이름은 10자를 넘어서는 안됩니다.")
    private String paidWhere;

    private String methodOfPayment;

    private long categoryId;

    @Size(max = 30, message = "메모의 길이는 최대 30자입니다.")
    private String memo;
}
