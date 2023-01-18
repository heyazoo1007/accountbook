package com.zerobase.accountbook.controller.category.dto.request;

import lombok.*;

import javax.validation.constraints.Email;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryRequestDto {

    @Email
    private String memberEmail;

    private String categoryName;
}
