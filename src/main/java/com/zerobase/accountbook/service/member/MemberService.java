package com.zerobase.accountbook.service.member;

import com.zerobase.accountbook.common.exception.ErrorCode;
import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.controller.auth.dto.request.ModifyMemberInfoRequestDto;
import com.zerobase.accountbook.controller.auth.dto.request.ModifyMemberPasswordRequestDto;
import com.zerobase.accountbook.controller.auth.dto.response.GetMemberInfoResponseDto;
import com.zerobase.accountbook.controller.auth.dto.response.ModifyMemberInfoResponseDto;
import com.zerobase.accountbook.controller.auth.dto.response.ModifyMemberPasswordResponseDto;
import com.zerobase.accountbook.controller.member.dto.request.DeleteMemberRequestDto;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import com.zerobase.accountbook.domain.member.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.zerobase.accountbook.common.exception.ErrorCode.*;
import static com.zerobase.accountbook.domain.member.MemberRole.DELETED;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public GetMemberInfoResponseDto getMemberInfo(
            String memberEmail) {
        return GetMemberInfoResponseDto.of(validateMemberByEmail(memberEmail));
    }

    public ModifyMemberInfoResponseDto modifyMemberInfo(
            String memberEmail, ModifyMemberInfoRequestDto request
    ) {
        Member member = validateMemberByMemberId(request.getMemberId());

        notAuthorizedMember(memberEmail, member);

        member.setMemberName(request.getMemberName());
        member.setMonthlyBudget(request.getMonthlyBudget());
        memberRepository.save(member);

        return ModifyMemberInfoResponseDto.of(member);
    }

    public ModifyMemberPasswordResponseDto modifyMemberPassword(
            String memberEmail, ModifyMemberPasswordRequestDto request
    ) {
        Member member = validateMemberByMemberId(request.getMemberId());

        notAuthorizedMember(memberEmail, member);

        // 사용자 비밀번호를 잘못 입력했을 때
        if (!passwordEncoder.matches(
                request.getBeforePassword(), member.getPassword())
        ) {
            throw new AccountBookException(
                    "비밀번호를 잘못 입력하셨습니다.",
                    VALIDATION_WRONG_PASSWORD_EXCEPTION
            );
        }

        member.setPassword(passwordEncoder.encode(request.getAfterPassword()));

        return ModifyMemberPasswordResponseDto.of(memberRepository.save(member));
    }

    @Transactional
    public void deleteMember(String memberEmail, DeleteMemberRequestDto request) {

        Member targetMember = validateMemberByMemberId(request.getMemberId());

        if (!targetMember.getEmail().equals(memberEmail)) {
            throw new AccountBookException(
                    "본인 계정만 삭제할 수 있습니다.",
                    FORBIDDEN_EXCEPTION
            );
        }

        // soft delete 로 회원 삭제. DB 에는 회원 정보 존재
        targetMember.setRole(DELETED);
        memberRepository.save(targetMember);
    }

    private Member validateMemberByEmail(String memberEmail) {
        Optional<Member> optionalMember = memberRepository.findByEmail(memberEmail);
        if (!optionalMember.isPresent()) {
            throw new AccountBookException(
                    "존재하지 않는 회원의 정보는 조회 불가능합니다.",
                    NOT_FOUND_USER_EXCEPTION
            );
        }
        return optionalMember.get();
    }

    private static void notAuthorizedMember(String memberEmail, Member member) {
        if (!member.getEmail().equals(memberEmail)) {
            throw new AccountBookException(
                    "회원정보는 본인만 확인 가능합니다.",
                    ErrorCode.FORBIDDEN_EXCEPTION
            );
        }
    }

    private Member validateMemberByMemberId(long memberId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (!optionalMember.isPresent()) {
            throw new AccountBookException(
                    "존재하지 않는 회원의 정보는 조회 불가능합니다.",
                    NOT_FOUND_USER_EXCEPTION
            );
        }
        return optionalMember.get();
    }
}
