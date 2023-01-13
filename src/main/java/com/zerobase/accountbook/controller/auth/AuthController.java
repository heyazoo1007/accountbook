package com.zerobase.accountbook.controller.auth;

import com.zerobase.accountbook.common.config.security.dto.TokenResponseDto;
import com.zerobase.accountbook.common.dto.ApiResponse;
import com.zerobase.accountbook.controller.auth.dto.request.*;
import com.zerobase.accountbook.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @PostMapping("/sign-up")
    public ApiResponse<String> createMember(
            @Valid @RequestBody CreateMemberRequestDto request
    ) {
        authService.createMember(request);
        return ApiResponse.SUCCESS;
    }

    @PostMapping("/sign-in")
    public ApiResponse<String> signIn(
            @Valid @RequestBody LoginRequestDto request
    ) {
        TokenResponseDto token =
                authService.signIn(request.getEmail(), request.getPassword());
        return ApiResponse.success(token.getAccessToken());
    }
}
