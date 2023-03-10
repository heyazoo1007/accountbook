package com.zerobase.accountbook.service.member;

import com.zerobase.accountbook.common.exception.ErrorCode;
import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.controller.auth.dto.request.ModifyMemberInfoRequestDto;
import com.zerobase.accountbook.controller.auth.dto.request.ModifyMemberPasswordRequestDto;
import com.zerobase.accountbook.controller.auth.dto.response.GetMemberInfoResponseDto;
import com.zerobase.accountbook.controller.auth.dto.response.ModifyMemberInfoResponseDto;
import com.zerobase.accountbook.controller.auth.dto.response.ModifyMemberPasswordResponseDto;
import com.zerobase.accountbook.controller.member.dto.request.DeleteMemberRequestDto;
import com.zerobase.accountbook.domain.budget.BudgetRepository;
import com.zerobase.accountbook.domain.category.CategoryRepository;
import com.zerobase.accountbook.domain.dailypayments.DailyPaymentsRepository;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import com.zerobase.accountbook.domain.monthlytotalamount.MonthlyTotalAmountRepository;
import com.zerobase.accountbook.domain.totalamountpercategory.TotalAmountPerCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.zerobase.accountbook.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final BudgetRepository budgetRepository;

    private final CategoryRepository categoryRepository;

    private final DailyPaymentsRepository dailyPaymentsRepository;

    private final MonthlyTotalAmountRepository monthlyTotalAmountRepository;

    private final TotalAmountPerCategoryRepository totalAmountPerCategoryRepository;

    public GetMemberInfoResponseDto getMemberInfo(
            String memberEmail, Long memberId
    ) {

        notAuthorizedMember(memberEmail, validateMemberByMemberId(memberId));

        return GetMemberInfoResponseDto.of(validateMemberByMemberId(memberId));
    }

    public ModifyMemberInfoResponseDto modifyMemberInfo(
            String memberEmail, ModifyMemberInfoRequestDto request
    ) {
        Member member = validateMemberByMemberId(request.getMemberId());

        notAuthorizedMember(memberEmail, member);

        member.setMemberName(request.getMemberName());
        member.setMonthlyBudget(request.getMonthlyBudget());
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);

        return ModifyMemberInfoResponseDto.of(member);
    }

    public ModifyMemberPasswordResponseDto modifyMemberPassword(
            String memberEmail, ModifyMemberPasswordRequestDto request
    ) {
        Member member = validateMemberByMemberId(request.getMemberId());

        notAuthorizedMember(memberEmail, member);

        // ????????? ??????????????? ?????? ???????????? ???
        if (!passwordEncoder.matches(
                request.getBeforePassword(), member.getPassword())
        ) {
            throw new AccountBookException(
                    "??????????????? ?????? ?????????????????????.",
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
                    "?????? ????????? ????????? ??? ????????????.",
                    FORBIDDEN_EXCEPTION
            );
        }

        Long memberId = targetMember.getId();

        budgetRepository.deleteAllByMemberId(memberId);

        categoryRepository.deleteAllByMemberId(memberId);

        dailyPaymentsRepository.deleteAllByMemberId(memberId);

        monthlyTotalAmountRepository.deleteAllByMemberId(memberId);

        totalAmountPerCategoryRepository.deleteAllByMemberId(memberId);

        memberRepository.deleteAllById(memberId);
    }

    private static void notAuthorizedMember(String memberEmail, Member member) {
        if (!member.getEmail().equals(memberEmail)) {
            throw new AccountBookException(
                    "???????????? ??? ??? ?????? ???????????? ?????????.",
                    ErrorCode.FORBIDDEN_EXCEPTION
            );
        }
    }

    private Member validateMemberByMemberId(long memberId) {
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if (!optionalMember.isPresent()) {
            throw new AccountBookException(
                    "???????????? ?????? ????????? ????????? ?????? ??????????????????.",
                    NOT_FOUND_USER_EXCEPTION
            );
        }
        return optionalMember.get();
    }
}
