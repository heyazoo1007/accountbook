package com.zerobase.accountbook.controller.auth;

import com.zerobase.accountbook.common.config.security.dto.TokenResponseDto;
import com.zerobase.accountbook.common.dto.ApiResponse;
import com.zerobase.accountbook.controller.auth.dto.request.*;
import com.zerobase.accountbook.controller.auth.dto.response.LinkResponseDto;
import com.zerobase.accountbook.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final String redirectURL = "http://www.localhost:8080/index";
    private final AuthService authService;

    @PostMapping("/email/send")
    @ResponseBody // @ResponseBody : 자바 객체를 HTTP 요청의 body 내용으로 매핑하는 역할
    public ApiResponse<String> sendAuthEmail(
            @Valid @RequestBody SendAuthEmailRequestDto request
    ) {
        authService.sendAuthEmail(request);
        return ApiResponse.SUCCESS;
    }

    @PostMapping("/email/complete")
    @ResponseBody
    public ApiResponse<String> completeAuthEmail(
            @Valid @RequestBody CompleteAuthEmailRequestDto request
    ) {
        authService.completeAuthEmail(request);
        return ApiResponse.SUCCESS;
    }

    @PostMapping("/sign-up")
    @ResponseBody
    public ApiResponse<LinkResponseDto> signUp(
            @Valid @RequestBody CreateMemberRequestDto request
    ) {
        authService.createMember(request);
        return ApiResponse.success(LinkResponseDto.of(redirectURL));
    }

    @PostMapping("/login")
    @ResponseBody
    public ApiResponse<LinkResponseDto> signIn(
            @Valid @RequestBody LoginRequestDto request
    ) {
        authService.signIn(request.getEmail(), request.getPassword());
        return ApiResponse.success(LinkResponseDto.of(redirectURL));
    }
}
