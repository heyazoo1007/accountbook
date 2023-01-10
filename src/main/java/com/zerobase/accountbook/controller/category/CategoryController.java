package com.zerobase.accountbook.controller.category;

import com.zerobase.accountbook.common.dto.ApiResponse;
import com.zerobase.accountbook.controller.category.dto.request.CreateCategoryRequestDto;
import com.zerobase.accountbook.controller.category.dto.response.CreateCategoryResponseDto;
import com.zerobase.accountbook.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping("/v1/category")
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    public ApiResponse<CreateCategoryResponseDto> createCategory(
            @Valid @RequestBody CreateCategoryRequestDto request
    ) {
        CreateCategoryResponseDto response = categoryService.createCategory(request);
        return ApiResponse.success(response);
    }
}
