package com.zerobase.accountbook.service.member;

import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.controller.auth.dto.request.ModifyMemberInfoRequestDto;
import com.zerobase.accountbook.controller.auth.dto.request.ModifyMemberPasswordRequestDto;
import com.zerobase.accountbook.controller.auth.dto.response.GetMemberInfoResponseDto;
import com.zerobase.accountbook.controller.auth.dto.response.ModifyMemberInfoResponseDto;
import com.zerobase.accountbook.controller.auth.dto.response.ModifyMemberPasswordResponseDto;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.zerobase.accountbook.common.exception.ErrorCode.NOT_FOUND_USER_EXCEPTION;
import static com.zerobase.accountbook.common.exception.ErrorCode.VALIDATION_WRONG_PASSWORD_EXCEPTION;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    public GetMemberInfoResponseDto getMemberInfo(Long memberId) {
        return GetMemberInfoResponseDto.of(validateMemberByMemberId(memberId));
    }

    public ModifyMemberInfoResponseDto modifyMemberInfo(ModifyMemberInfoRequestDto request) {
        Member member = validateMemberByMemberId(request.getMemberId());

        member.setMemberName(request.getMemberName());
        member.setMonthlyBudget(request.getMonthlyBudget());
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);

        return ModifyMemberInfoResponseDto.of(member);
    }

    public ModifyMemberPasswordResponseDto modifyMemberPassword(ModifyMemberPasswordRequestDto request) {
        Member member = validateMemberByMemberId(request.getMemberId());

        // 사용자 비밀번호를 잘못 입력했을 때
        if (!passwordEncoder.matches(request.getBeforePassword(), member.getPassword())) {
            throw new AccountBookException("비밀번호를 잘못 입력하셨습니다.", VALIDATION_WRONG_PASSWORD_EXCEPTION);
        }

        member.setPassword(passwordEncoder.encode(request.getAfterPassword()));

        return ModifyMemberPasswordResponseDto.of(memberRepository.save(member));
    }

    private Member validateMemberByMemberId(long memberId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (!optionalMember.isPresent()) {
            throw new AccountBookException("존재하지 않는 회원의 정보는 조회 불가능합니다.", NOT_FOUND_USER_EXCEPTION);
        }
        return optionalMember.get();
    }
}
