package com.zerobase.accountbook.controller.category.dto.request;

import lombok.*;

import javax.validation.constraints.Email;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryRequestDto {

    private String categoryName;
}
