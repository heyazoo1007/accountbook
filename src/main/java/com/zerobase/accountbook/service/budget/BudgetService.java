package com.zerobase.accountbook.service.budget;

import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.controller.budget.dto.request.CreateBudgetRequestDto;
import com.zerobase.accountbook.controller.budget.dto.request.ModifyBudgetRequestDto;
import com.zerobase.accountbook.controller.budget.dto.response.CreateBudgetResponseDto;
import com.zerobase.accountbook.controller.budget.dto.response.ModifyBudgetResponseDto;
import com.zerobase.accountbook.domain.budget.Budget;
import com.zerobase.accountbook.domain.budget.BudgetRepository;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Optional;

import static com.zerobase.accountbook.common.exception.ErrorCode.*;
import static com.zerobase.accountbook.common.exception.ErrorCode.FORBIDDEN_EXCEPTION;
import static com.zerobase.accountbook.common.exception.ErrorCode.NOT_FOUND_USER_EXCEPTION;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;

    private final MemberRepository memberRepository;

    // 매달 첫번째 날에 사용자가 로그인 하면 생성하라고 알람 보내기
    public CreateBudgetResponseDto createBudget(CreateBudgetRequestDto request) {

        Member member = validateMember(request.getMemberEmail());

        // 이미 생성된 예산에 대해서는 생성할 수 없고, 따로 수정 버튼을 눌러야 합니다.
        Optional<Budget> optionalBudget =
                budgetRepository.findByBudgetYearMonth(YearMonth.now().toString());
        if (optionalBudget.isPresent()) {
            throw new AccountBookException(
                    "이미 해당 달에 예산이 설정 되어 있습니다.",
                    CONFLICT_MONTHLY_BUDGET_EXCEPTION
            );
        }

        member.setMonthlyBudget(request.getMonthlyBudget());

        return CreateBudgetResponseDto.of(
                budgetRepository.save(
                        Budget.builder()
                                .budgetYearMonth(YearMonth.now().toString())
                                .member(member)
                                .monthlyBudget(request.getMonthlyBudget())
                                .createdAt(LocalDateTime.now())
                                .build()
                ));
    }

    public ModifyBudgetResponseDto modifyBudget(ModifyBudgetRequestDto request) {

        Member member = validateMember(request.getMemberEmail());

        Budget budget = validateBudget(request.getYearMonth());

        requestMemberMismatchBudgetOwner(member, budget);

        budget.setMonthlyBudget(request.getModifyMonthlyBudget());

        return ModifyBudgetResponseDto.of(budgetRepository.save(budget));
    }

    private static void requestMemberMismatchBudgetOwner(
            Member member, Budget budget
    ) {
        Member budgetOwner = budget.getMember();
        if (!budgetOwner.equals(member)) {
            throw new AccountBookException(
                    "접근할 수 없는 예산입니다.",
                    FORBIDDEN_EXCEPTION
            );
        }
    }

    private Budget validateBudget(String yearMonth) {
        Optional<Budget> optionalBudget =
                budgetRepository.findByBudgetYearMonth(yearMonth);
        if (!optionalBudget.isPresent()) {
            throw new AccountBookException(
                    "존재하지 않는 예산입니다.",
                    NOT_FOUND_BUDGET_EXCEPTION
            );
        }
        return optionalBudget.get();
    }

    private Member validateMember(String memberEmail) {
        Optional<Member> optionalMember = memberRepository.findByEmail(memberEmail);
        if (!optionalMember.isPresent()) {
            throw new AccountBookException(
                    "존재하지 않는 회원입니다.",
                    NOT_FOUND_USER_EXCEPTION
            );
        }
        return optionalMember.get();
    }
}
