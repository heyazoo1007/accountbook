package com.zerobase.accountbook.controller.budget;

import com.zerobase.accountbook.common.dto.ApiResponse;
import com.zerobase.accountbook.controller.budget.dto.request.CreateBudgetRequestDto;
import com.zerobase.accountbook.controller.budget.dto.response.CreateBudgetResponseDto;
import com.zerobase.accountbook.service.budget.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
