package com.zerobase.accountbook.controller.category.dto.response;

import com.zerobase.accountbook.domain.category.Category;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCategoryListResponseDto {

    private long categoryId;
    private String categoryName;

    public static GetCategoryListResponseDto of(Category category) {
        return GetCategoryListResponseDto.builder()
                .categoryId(category.getId())
                .categoryName(category.getCategoryName())
                .build();
    }
}
