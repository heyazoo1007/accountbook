package com.zerobase.accountbook.controller.auth;

import com.zerobase.accountbook.common.dto.ApiResponse;
import com.zerobase.accountbook.controller.auth.dto.request.CompleteAuthEmailRequestDto;
import com.zerobase.accountbook.controller.auth.dto.request.SendAuthEmailRequestDto;
import com.zerobase.accountbook.controller.auth.dto.request.ValidateEmailRequestDto;
import com.zerobase.accountbook.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
