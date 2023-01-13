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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/v1/budget")
@RestController
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping()
    public ApiResponse<CreateBudgetResponseDto> createBudget(
            @Valid @RequestBody CreateBudgetRequestDto request
    ) {
        CreateBudgetResponseDto response =
                budgetService.createBudget(request);
        return ApiResponse.success(response);
    }

    @PutMapping()
    public ApiResponse<ModifyBudgetResponseDto> modifyBudget(
            @Valid @RequestBody ModifyBudgetRequestDto request
    ) {
        ModifyBudgetResponseDto response =
                budgetService.modifyBudget(request);
        return ApiResponse.success(response);
    }

    @GetMapping("/{budgetYearMonth}")
    public ApiResponse<GetBudgetResponseDto> getBudget(
            @DateTimeFormat(pattern = "yyyy-MM")
            @PathVariable String budgetYearMonth
    ) {
        GetBudgetResponseDto response =
                budgetService.getBudget(budgetYearMonth);
        return ApiResponse.success(response);
    }
}
