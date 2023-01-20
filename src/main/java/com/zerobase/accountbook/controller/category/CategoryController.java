package com.zerobase.accountbook.controller.category;

import com.zerobase.accountbook.common.dto.ApiResponse;
import com.zerobase.accountbook.controller.category.dto.request.CreateCategoryRequestDto;
import com.zerobase.accountbook.controller.category.dto.request.DeleteCategoryRequestDto;
import com.zerobase.accountbook.controller.category.dto.request.ModifyCategoryRequestDto;
import com.zerobase.accountbook.controller.category.dto.response.CreateCategoryResponseDto;
import com.zerobase.accountbook.controller.category.dto.response.GetCategoryListResponseDto;
import com.zerobase.accountbook.controller.category.dto.response.ModifyCategoryResponseDto;
import com.zerobase.accountbook.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/v1/category")
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping()
    public ApiResponse<CreateCategoryResponseDto> createCategory(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody CreateCategoryRequestDto request
    ) {
        CreateCategoryResponseDto response =
                categoryService.createCategory(user.getUsername(), request);
        return ApiResponse.success(response);
    }

    @PutMapping()
    public ApiResponse<ModifyCategoryResponseDto> modifyCategory(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody ModifyCategoryRequestDto request
    ) {
        ModifyCategoryResponseDto response =
                categoryService.modifyCategory(user.getUsername(), request);
        return ApiResponse.success(response);
    }

    @DeleteMapping()
    public ApiResponse<String> deleteCategory(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody DeleteCategoryRequestDto request
    ) {
        categoryService.deleteCategory(user.getUsername(), request);
        return ApiResponse.SUCCESS;
    }

    @GetMapping("/list")
    public ApiResponse<List<GetCategoryListResponseDto>> getCategoryList() {
        List<GetCategoryListResponseDto> response = categoryService.getCategoryList();
        return ApiResponse.success(response);
    }
}
