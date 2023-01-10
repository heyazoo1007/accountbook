package com.zerobase.accountbook.controller.category.dto.response;

import com.zerobase.accountbook.domain.category.Category;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryResponseDto {

    private String categoryName;

    public static CreateCategoryResponseDto of(Category category) {
        return CreateCategoryResponseDto.builder()
                .categoryName(category.getCategoryName())
                .build();
    }
}
