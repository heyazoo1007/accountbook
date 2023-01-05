package com.zerobase.accountbook.controller.auth;

import com.zerobase.accountbook.common.dto.ApiResponse;
import com.zerobase.accountbook.controller.auth.dto.request.CompleteAuthEmailRequestDto;
import com.zerobase.accountbook.controller.auth.dto.request.CreateMemberRequestDto;
import com.zerobase.accountbook.controller.auth.dto.request.SendAuthEmailRequestDto;
import com.zerobase.accountbook.controller.auth.dto.request.ValidateEmailRequestDto;
import com.zerobase.accountbook.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/email/validate")
    public ApiResponse<String> validateEmail(
            @Valid @RequestBody ValidateEmailRequestDto request
    ) {
        authService.validateEmail(request.getEmail());
        return ApiResponse.SUCCESS;
    }

    @PostMapping("/email/send")
    public ApiResponse<String> sendAuthEmail(
            @Valid @RequestBody SendAuthEmailRequestDto request
    ) {
        authService.sendAuthEmail(request);
        return ApiResponse.SUCCESS;
    }

    @PostMapping("/email/complete")
    public ApiResponse<String> completeAuthEmail(
            @Valid @RequestBody CompleteAuthEmailRequestDto request
    ) {
        authService.completeAuthEmail(request);
        return ApiResponse.SUCCESS;
    }

    @PostMapping("/signup")
    public ApiResponse<String> createMember(
            @Valid @RequestBody CreateMemberRequestDto request
    ) {
        authService.createMember(request);
        return ApiResponse.SUCCESS;
    }
}
