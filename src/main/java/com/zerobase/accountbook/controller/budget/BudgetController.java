package com.zerobase.accountbook.controller.budget;

import com.zerobase.accountbook.common.dto.ApiResponse;
import com.zerobase.accountbook.controller.budget.dto.request.CreateBudgetRequestDto;
import com.zerobase.accountbook.controller.budget.dto.request.ModifyBudgetRequestDto;
import com.zerobase.accountbook.controller.budget.dto.response.CreateBudgetResponseDto;
import com.zerobase.accountbook.controller.budget.dto.response.GetBudgetResponseDto;
import com.zerobase.accountbook.controller.budget.dto.response.ModifyBudgetResponseDto;
import com.zerobase.accountbook.service.budget.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/v1/budget")
@RestController
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping()
    public ApiResponse<CreateBudgetResponseDto> createBudget(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody CreateBudgetRequestDto request
    ) {
        CreateBudgetResponseDto response =
                budgetService.createBudget(user.getUsername(), request);
        return ApiResponse.success(response);
    }

    @PutMapping()
    public ApiResponse<ModifyBudgetResponseDto> modifyBudget(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody ModifyBudgetRequestDto request
    ) {
        ModifyBudgetResponseDto response =
                budgetService.modifyBudget(user.getUsername(), request);
        return ApiResponse.success(response);
    }

    @GetMapping("/{budgetYearMonth}")
    public ApiResponse<GetBudgetResponseDto> getBudget(
            @AuthenticationPrincipal UserDetails user,
            @DateTimeFormat(pattern = "yyyy-MM")
            @PathVariable String budgetYearMonth
    ) {
        GetBudgetResponseDto response =
                budgetService.getBudget(user.getUsername(), budgetYearMonth);
        return ApiResponse.success(response);
    }
}
