package com.zerobase.accountbook.controller.member;

import com.zerobase.accountbook.common.dto.ApiResponse;
import com.zerobase.accountbook.controller.auth.dto.request.ModifyMemberInfoRequestDto;
import com.zerobase.accountbook.controller.auth.dto.request.ModifyMemberPasswordRequestDto;
import com.zerobase.accountbook.controller.auth.dto.response.GetMemberInfoResponseDto;
import com.zerobase.accountbook.controller.auth.dto.response.ModifyMemberInfoResponseDto;
import com.zerobase.accountbook.controller.auth.dto.response.ModifyMemberPasswordResponseDto;
import com.zerobase.accountbook.controller.member.dto.request.DeleteMemberRequestDto;
import com.zerobase.accountbook.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{memberId}")
    public ApiResponse<GetMemberInfoResponseDto> getMemberInfo(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable long memberId
    ) {
        GetMemberInfoResponseDto response =
                memberService.getMemberInfo(user.getUsername(), memberId);
        return ApiResponse.success(response);
    }

    @PutMapping()
    public ApiResponse<ModifyMemberInfoResponseDto> modifyMemberInfo(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody ModifyMemberInfoRequestDto request
    ) {
        ModifyMemberInfoResponseDto response =
                memberService.modifyMemberInfo(user.getUsername(), request);
        return ApiResponse.success(response);
    }

    @PatchMapping("/password")
    public ApiResponse<ModifyMemberPasswordResponseDto> modifyMemberPassword(
            @AuthenticationPrincipal UserDetails user,
            @RequestBody ModifyMemberPasswordRequestDto request
    ) {
        ModifyMemberPasswordResponseDto response =
                memberService.modifyMemberPassword(user.getUsername(), request);
        return ApiResponse.success(response);
    }

    @DeleteMapping
    public ApiResponse<String> deleteMember(
            @AuthenticationPrincipal UserDetails user,
            @RequestBody DeleteMemberRequestDto request
    ) {
        memberService.deleteMember(user.getUsername(), request);
        return ApiResponse.SUCCESS;
    }
}
