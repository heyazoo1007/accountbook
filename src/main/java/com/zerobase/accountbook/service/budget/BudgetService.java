package com.zerobase.accountbook.service.budget;

import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.controller.budget.dto.request.CreateBudgetRequestDto;
import com.zerobase.accountbook.controller.budget.dto.request.ModifyBudgetRequestDto;
import com.zerobase.accountbook.controller.budget.dto.response.CreateBudgetResponseDto;
import com.zerobase.accountbook.controller.budget.dto.response.GetBudgetResponseDto;
import com.zerobase.accountbook.controller.budget.dto.response.ModifyBudgetResponseDto;
import com.zerobase.accountbook.domain.budget.Budget;
import com.zerobase.accountbook.domain.budget.BudgetRepository;
import com.zerobase.accountbook.domain.dailypayments.DailyPaymentsRepository;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static com.zerobase.accountbook.common.exception.ErrorCode.*;
import static com.zerobase.accountbook.common.exception.ErrorCode.FORBIDDEN_EXCEPTION;
import static com.zerobase.accountbook.common.exception.ErrorCode.NOT_FOUND_USER_EXCEPTION;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final MemberRepository memberRepository;

    public CreateBudgetResponseDto createBudget(
            String memberEmail, CreateBudgetRequestDto request
    ) {

        Member member = validateMember(memberEmail);

        // 이미 생성된 예산에 대해서는 생성할 수 없고, 따로 수정 버튼을 눌러야 합니다.
        checkBudgetAlreadyCreated();

        member.setMonthlyBudget(request.getMonthlyBudget());
        memberRepository.save(member);

        return CreateBudgetResponseDto.of(
                budgetRepository.save(
                        Budget.builder()
                                .budgetYearMonth(YearMonth.now().toString())
                                .member(member)
                                .monthlyBudget(request.getMonthlyBudget())
                                .createdAt(LocalDateTime.now())
                                .build()
                )
        );
    }

    public ModifyBudgetResponseDto modifyBudget(
            String memberEmail, ModifyBudgetRequestDto request
    ) {

        Integer modifiedBudget = request.getModifyMonthlyBudget();

        Member member = validateMember(memberEmail);

        Budget budget = validateBudget(request.getYearMonth());

        checkBudgetOwner(member, budget);

        budget.setMonthlyBudget(modifiedBudget);

        member.setMonthlyBudget(modifiedBudget);
        memberRepository.save(member);

        return ModifyBudgetResponseDto.of(budgetRepository.save(budget));
    }

    public GetBudgetResponseDto getBudget(
            String memberEmail, String budgetYearMonth
    ) {
        Budget budget = validateBudget(budgetYearMonth);

        checkBudgetOwner(validateMember(memberEmail), budget);

        return GetBudgetResponseDto.of(budget);
    }

    private int getMonthlyBudget(String budgetYearMonth, String memberEmail) {
        return getBudget(memberEmail, budgetYearMonth).getMonthlyBudget();
    }

    private static void checkBudgetOwner(Member member, Budget budget) {
        if (!budget.getMember().equals(member)) {
            throw new AccountBookException(
                    "접근할 수 없는 예산입니다.",
                    FORBIDDEN_EXCEPTION
            );
        }
    }

    private Budget validateBudget(String budgetYearMonth) {
        return budgetRepository.findByBudgetYearMonth(budgetYearMonth)
                .orElseThrow(() -> new AccountBookException(
                        "존재하지 않는 예산입니다.",
                        NOT_FOUND_BUDGET_EXCEPTION));
    }

    private void checkBudgetAlreadyCreated() {
        Optional<Budget> optionalBudget = budgetRepository
                .findByBudgetYearMonth(YearMonth.now().toString());
        if (optionalBudget.isPresent()) {
            throw new AccountBookException(
                    "이미 해당 달에 예산이 설정 되어 있습니다.",
                    CONFLICT_MONTHLY_BUDGET_EXCEPTION
            );
        }
    }

    private Member validateMember(String memberEmail) {
        return memberRepository.findByEmail(memberEmail).orElseThrow(
                () -> new AccountBookException(
                        "존재하지 않는 회원입니다.",
                        NOT_FOUND_USER_EXCEPTION)
        );
    }
}
