package com.zerobase.accountbook.controller.budget;

import com.zerobase.accountbook.common.dto.ApiResponse;
import com.zerobase.accountbook.controller.budget.dto.request.CreateBudgetRequestDto;
import com.zerobase.accountbook.controller.budget.dto.request.ModifyBudgetRequestDto;
import com.zerobase.accountbook.controller.budget.dto.response.CreateBudgetResponseDto;
import com.zerobase.accountbook.controller.budget.dto.response.GetBudgetResponseDto;
import com.zerobase.accountbook.controller.budget.dto.response.ModifyBudgetResponseDto;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.service.budget.BudgetService;
import com.zerobase.accountbook.service.dailypaymetns.DailyPaymentsService;
import com.zerobase.accountbook.service.firebase.FirebaseCloudMessageService;
import com.zerobase.accountbook.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;

@RequestMapping("/v1/budget")
@RestController
@RequiredArgsConstructor
public class BudgetController {

    private final MemberService memberService;
    private final BudgetService budgetService;

    private final DailyPaymentsService dailyPaymentsService;

    private final FirebaseCloudMessageService firebaseCloudMessageService;

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
            @AuthenticationPrincipal UserDetails user,
            @DateTimeFormat(pattern = "yyyy-MM")
            @PathVariable String budgetYearMonth
    ) {
        GetBudgetResponseDto response =
                budgetService.getBudget(user.getUsername(), budgetYearMonth);
        return ApiResponse.success(response);
    }
}
