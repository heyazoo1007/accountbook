package com.zerobase.accountbook.service.budget;

import com.zerobase.accountbook.common.CommonTime;
import com.zerobase.accountbook.common.exception.ErrorCode;
import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.controller.budget.dto.response.SendBudgetAlarmDto;
import com.zerobase.accountbook.domain.budget.BudgetRepository;
import com.zerobase.accountbook.domain.dailypayments.DailyPaymentsRepository;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final MemberRepository memberRepository;
    private final DailyPaymentsRepository dailyPaymentsRepository;
    private final BudgetRepository budgetRepository;

    public SendBudgetAlarmDto sendBudgetAlarm(long memberId) {
        LocalDateTime now = LocalDateTime.now();

        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new AccountBookException("존재하지 않는 회원입니다.",
                                                ErrorCode.NOT_FOUND_USER_EXCEPTION));

        Integer monthlySum = dailyPaymentsRepository.
                findMonthlySum(member.getId(), CommonTime.getStartDate(now), CommonTime.getEndDate(now));
        Integer monthlyBudget = budgetRepository.
                findByMemberIdAndYearMonth(member.getId(), CommonTime.getYearMonthString(now));
        String message = getAlarmMessage(monthlyBudget, monthlySum);

        return SendBudgetAlarmDto.of(member.getId(), message);
    }

    private String getAlarmMessage(Integer monthlyBudget, Integer monthlySum) {
        if (monthlyBudget == null) {
            return "이번달 예산이 설정되어 있지 않습니다. 새로 설정하세요!";
        }

        String message;
        if (monthlySum == null) {
            message = "지출내역이 존재하지 않습니다.";
        } else if (monthlyBudget > monthlySum) {
            message = "남은 예산은 " + (monthlyBudget - monthlySum) + " 입니다.";
        } else {
            message = "이번달 예산을 초과했습니다! 예산을 다시 설정해보시겠어요?";
        }
        return message;
    }
}
