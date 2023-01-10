package com.zerobase.accountbook.controller.category.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryRequestDto {

    private String memberEmail;

    private String categoryName;
}
