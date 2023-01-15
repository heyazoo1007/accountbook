package com.zerobase.accountbook.controller.category.dto.response;

import com.zerobase.accountbook.domain.category.Category;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModifyCategoryResponseDto {

    private String categoryName;

    public static ModifyCategoryResponseDto of(Category category) {
        return ModifyCategoryResponseDto.builder()
                .categoryName(category.getCategoryName())
                .build();
    }
}
