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
import java.time.LocalDateTime;
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
        CreateDailyPaymentsResponseDto response =
                dailyPaymentsService.createDailyPayments(
                        user.getUsername(),
                        request
                );
        return ApiResponse.success(response);
    }

    @PutMapping()
    public ApiResponse<ModifyDailyPaymentsResponseDto> modifyDailyPayments(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody ModifyDailyPaymentsRequestDto request
    ) {
        ModifyDailyPaymentsResponseDto response =
                dailyPaymentsService.modifyDailyPayments(
                        user.getUsername(),
                        request
                );
        return ApiResponse.success(response);
    }

    @DeleteMapping()
    public void deleteDailyPayments(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody DeleteDailyPaymentsRequestDto request
    ) {
        dailyPaymentsService.deleteDailyPayments(user.getUsername(), request);
    }

    @GetMapping("/{dailyPaymentsId}")
    public ApiResponse<GetDailyPaymentsResponseDto> getDailyPayments(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long dailyPaymentsId
    ) {
        GetDailyPaymentsResponseDto response =
                dailyPaymentsService.getDailyPayment(
                        user.getUsername(),
                        dailyPaymentsId
                );
        return ApiResponse.success(response);
    }

    @GetMapping("/list")
    public ApiResponse<List<GetDailyPaymentsResponseDto>> getDailyPaymentsList(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") String date
    ) {
        List<GetDailyPaymentsResponseDto> response =
                dailyPaymentsService.getDailyPaymentsList(
                        user.getUsername(),
                        date
                );
        return ApiResponse.success(response);
    }

    @GetMapping("/search")
    public ApiResponse<List<SearchDailyPaymentsResponseDto>> searchDailyPayments(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam String keyword
    ) {
        List<SearchDailyPaymentsResponseDto> response =
                dailyPaymentsService.searchDailyPayments(
                        user.getUsername(),
                        keyword
                );
        return ApiResponse.success(response);
    }

    @GetMapping("/monthly/{date}")
    public ApiResponse<GetMonthlyResultResponseDto>
    getMonthlyResultResponseDto(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable String date
    ) {
        GetMonthlyResultResponseDto response =
                dailyPaymentsService.getMonthlyDailyPaymentsResult(
                        user.getUsername(),
                        date
                );
        return ApiResponse.success(response);
    }

    @GetMapping("/yearly")
    public ApiResponse<GetYearlyResultResponseDto> getYearlyResult(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam @DateTimeFormat(pattern = "yyyy")String year
            ) {
        GetYearlyResultResponseDto response =
                dailyPaymentsService.getYearlyResult(user.getUsername(), year);
        return ApiResponse.success(response);
    }
}
