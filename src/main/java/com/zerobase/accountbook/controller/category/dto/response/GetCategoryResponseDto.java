package com.zerobase.accountbook.controller.category.dto.response;

import com.zerobase.accountbook.domain.category.Category;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCategoryResponseDto {
    long categoryId;
    String categoryName;

    public static GetCategoryResponseDto of(Category category) {
        return GetCategoryResponseDto.builder()
                .categoryId(category.getId())
                .categoryName(category.getCategoryName())
                .build();
    }
}
