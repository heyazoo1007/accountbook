package com.zerobase.accountbook.controller.dailypayments;

import com.zerobase.accountbook.common.dto.ApiResponse;
import com.zerobase.accountbook.controller.dailypayments.dto.request.CreateDailyPaymentsRequestDto;
import com.zerobase.accountbook.controller.dailypayments.dto.request.ModifyDailyPaymentsRequestDto;
import com.zerobase.accountbook.controller.dailypayments.dto.response.CreateDailyPaymentsResponseDto;
import com.zerobase.accountbook.controller.dailypayments.dto.response.ModifyDailyPaymentsResponseDto;
import com.zerobase.accountbook.service.dailypaymetns.DailyPaymentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/daily-payments")
@RequiredArgsConstructor
public class DailyPaymentsController {

    private final DailyPaymentsService dailyPaymentsService;

    @PostMapping("/create")
    public ApiResponse<CreateDailyPaymentsResponseDto> createDailyPayments(
            @Valid @RequestBody CreateDailyPaymentsRequestDto request
    ) {
        CreateDailyPaymentsResponseDto response = dailyPaymentsService.createDailyPayments(request);
        return ApiResponse.success(response);
    }

    @PutMapping("/modify")
    public ApiResponse<ModifyDailyPaymentsResponseDto> modifyDailyPayments(
            @Valid @RequestBody ModifyDailyPaymentsRequestDto request
    ) {
        ModifyDailyPaymentsResponseDto response = dailyPaymentsService.modifyDailyPayments(request);
        return ApiResponse.success(response);
    }
}
