package com.zerobase.accountbook.controller.dailypayments;

import com.zerobase.accountbook.common.dto.ApiResponse;
import com.zerobase.accountbook.controller.dailypayments.dto.request.CreateDailyPaymentsRequestDto;
import com.zerobase.accountbook.controller.dailypayments.dto.request.ModifyDailyPaymentsRequestDto;
import com.zerobase.accountbook.controller.dailypayments.dto.response.*;
import com.zerobase.accountbook.controller.dailypayments.dto.request.DeleteDailyPaymentsRequestDto;
import com.zerobase.accountbook.service.dailypaymetns.DailyPaymentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/daily-payments")
@RequiredArgsConstructor
public class DailyPaymentsController {

    private final DailyPaymentsService dailyPaymentsService;

    @PostMapping()
    public ApiResponse<CreateDailyPaymentsResponseDto> createDailyPayments(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody CreateDailyPaymentsRequestDto request
    ) {
        CreateDailyPaymentsResponseDto response = dailyPaymentsService.
                createDailyPayments(user.getUsername(), request);
        return ApiResponse.success(response);
    }

    @PutMapping()
    public ApiResponse<String> modifyDailyPayments(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody ModifyDailyPaymentsRequestDto request
    ) {
        dailyPaymentsService.modifyDailyPayments(user.getUsername(), request);
        return ApiResponse.SUCCESS;
    }

    @DeleteMapping()
    public ApiResponse<String> deleteDailyPayments(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody DeleteDailyPaymentsRequestDto request
    ) {
        dailyPaymentsService.deleteDailyPayments(user.getUsername(), request);
        return ApiResponse.SUCCESS;
    }

    @GetMapping("/{paymentId}")
    public ApiResponse<GetDailyPaymentsResponseDto> getDailyPayment(
            @PathVariable Long paymentId
    ) {
        GetDailyPaymentsResponseDto response = dailyPaymentsService.
                getDailyPayment(paymentId);
        return ApiResponse.success(response);
    }

    @GetMapping("/list/{requestDate}")
    public ApiResponse<List<GetDailyPaymentsResponseDto>> getDailyPaymentsList(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable String requestDate
    ) {
        List<GetDailyPaymentsResponseDto> response = dailyPaymentsService.
                getDailyPaymentsList(user.getUsername(), requestDate);
        return ApiResponse.success(response);
    }

    @GetMapping("/search")
    public ApiResponse<List<SearchDailyPaymentsResponseDto>> searchDailyPayments(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam String keyword
    ) {
        List<SearchDailyPaymentsResponseDto> response = dailyPaymentsService.
                searchDailyPayments(user.getUsername(), keyword);
        return ApiResponse.success(response);
    }

    @GetMapping("/monthly")
    public ApiResponse<GetMonthlyResultResponseDto> getMonthlyResult(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam String date
    ) {
        GetMonthlyResultResponseDto response = dailyPaymentsService.
                getMonthlyDailyPaymentsResult(user.getUsername(), date);
        return ApiResponse.success(response);
    }

    @GetMapping("/yearly")
    public ApiResponse<GetYearlyResultResponseDto> getYearlyResult(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam @DateTimeFormat(pattern = "yyyy")String date
    ) {
        GetYearlyResultResponseDto response = dailyPaymentsService.
                getYearlyResult(user.getUsername(), date);
        return ApiResponse.success(response);
    }
}
