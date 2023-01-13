package com.zerobase.accountbook.controller.category.dto.request;

import lombok.*;

import javax.validation.constraints.Email;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModifyCategoryRequestDto {

    @Email
    private String memberEmail;

    private long categoryId;

    private String categoryName;
}
