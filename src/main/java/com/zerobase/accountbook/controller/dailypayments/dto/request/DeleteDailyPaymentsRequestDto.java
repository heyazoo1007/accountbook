package com.zerobase.accountbook.controller.dailypayments.dto.request;

import lombok.*;

import javax.validation.constraints.Email;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteDailyPaymentsRequestDto {

    private Long dailyPaymentsId;
}
