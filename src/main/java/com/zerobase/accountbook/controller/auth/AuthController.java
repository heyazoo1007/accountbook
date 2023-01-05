package com.zerobase.accountbook.controller.auth;

import com.zerobase.accountbook.common.dto.ApiResponse;
import com.zerobase.accountbook.controller.auth.dto.request.*;
import com.zerobase.accountbook.controller.auth.dto.response.GetMemberInfoResponseDto;
import com.zerobase.accountbook.controller.auth.dto.response.UpdateMemberInfoResponseDto;
import com.zerobase.accountbook.controller.auth.dto.response.UpdateMemberPasswordResponseDto;
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

    @PostMapping("/signin") // endpoint에는 소문자만 사용가능
    public ApiResponse<String> signIn(
            @Valid @RequestBody LoginRequestDto request
    ) {
        String token = authService.signIn(request.getEmail(), request.getPassword());
        return ApiResponse.success(token);
    }

    @GetMapping("/member/{memberId}")
    public ApiResponse<GetMemberInfoResponseDto> getMemberInfo(
            @PathVariable long memberId
    ) {
        GetMemberInfoResponseDto response = authService.getMemberInfo(memberId);
        return ApiResponse.success(response);
    }

    @PutMapping("/member/update")
    public ApiResponse<UpdateMemberInfoResponseDto> updateMemberInfo(
            @Valid @RequestBody UpdateMemberInfoRequestDto request
    ) {
        UpdateMemberInfoResponseDto response = authService.updateMemberInfo(request);
        return ApiResponse.success(response);
    }

    @PutMapping("/member/update/password")
    public ApiResponse<UpdateMemberPasswordResponseDto> updateMemberPassword(
            @RequestBody UpdateMemberPasswordRequestDto request
    ) {
        UpdateMemberPasswordResponseDto response = authService.updateMemberPassword(request);
        return ApiResponse.success(response);
    }
}
