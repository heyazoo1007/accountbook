package com.zerobase.accountbook.controller.member;

import com.zerobase.accountbook.common.dto.ApiResponse;
import com.zerobase.accountbook.controller.auth.dto.request.ModifyMemberInfoRequestDto;
import com.zerobase.accountbook.controller.auth.dto.request.ModifyMemberPasswordRequestDto;
import com.zerobase.accountbook.controller.auth.dto.response.GetMemberInfoResponseDto;
import com.zerobase.accountbook.controller.auth.dto.response.ModifyMemberInfoResponseDto;
import com.zerobase.accountbook.controller.auth.dto.response.ModifyMemberPasswordResponseDto;
import com.zerobase.accountbook.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{memberId}")
    public ApiResponse<GetMemberInfoResponseDto> getMemberInfo(
            @PathVariable long memberId
    ) {
        GetMemberInfoResponseDto response = memberService.getMemberInfo(memberId);
        return ApiResponse.success(response);
    }

    @PutMapping("/modify")
    public ApiResponse<ModifyMemberInfoResponseDto> modifyMemberInfo(
            @Valid @RequestBody ModifyMemberInfoRequestDto request
    ) {
        ModifyMemberInfoResponseDto response = memberService.modifyMemberInfo(request);
        return ApiResponse.success(response);
    }

    @PutMapping("/modify/password")
    public ApiResponse<ModifyMemberPasswordResponseDto> modifyMemberPassword(
            @RequestBody ModifyMemberPasswordRequestDto request
    ) {
        ModifyMemberPasswordResponseDto response = memberService.modifyMemberPassword(request);
        return ApiResponse.success(response);
    }
}
