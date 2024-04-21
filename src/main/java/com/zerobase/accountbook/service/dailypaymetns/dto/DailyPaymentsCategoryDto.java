package com.zerobase.accountbook.service.dailypaymetns.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public interface DailyPaymentsCategoryDto {

    String getCategoryName();
    Integer getTotalAmount();
}
