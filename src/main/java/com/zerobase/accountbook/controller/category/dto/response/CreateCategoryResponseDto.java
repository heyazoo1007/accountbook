package com.zerobase.accountbook.controller.category.dto.response;

import com.zerobase.accountbook.domain.category.Category;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryResponseDto {

    private long categoryId;

    private String categoryName;

    public static CreateCategoryResponseDto of(Category category) {
        return CreateCategoryResponseDto.builder()
                .categoryId(category.getId())
                .categoryName(category.getCategoryName())
                .build();
    }
}
