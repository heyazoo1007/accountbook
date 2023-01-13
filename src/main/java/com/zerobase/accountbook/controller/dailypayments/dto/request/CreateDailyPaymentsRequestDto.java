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

    @Email
    private String memberEmail;

    @Min(0)
    private Integer paidAmount;

    @Size(max = 10, message = "가게 이름은 10자를 넘어서는 안됩니다.")
    private String paidWhere;

    private String methodOfPayment;

    private String categoryName;

    @Size(max = 30, message = "해시태그의 총 30자가 넘어서는 안됩니다.")
    private String hashTags; // "#abc, #dbe, #acds" 형태로 전달 받음
}
