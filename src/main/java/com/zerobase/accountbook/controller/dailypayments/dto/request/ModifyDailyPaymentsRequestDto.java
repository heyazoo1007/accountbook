package com.zerobase.accountbook.controller.dailypayments.dto.request;


import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModifyDailyPaymentsRequestDto {

    private long dailyPaymentsId;

    @Min(0)
    private Integer paidAmount;

    @Size(max = 10, message = "가게 이름은 10자를 넘어서는 안됩니다.")
    private String paidWhere;

    private String methodOfPayment;

    private String categoryName;

    @Size(max = 30, message = "메모의 길이는 최대 30자입니다.")
    private String memo;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private String createdAt;
}
