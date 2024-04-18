package com.zerobase.accountbook.service.budget;

import com.zerobase.accountbook.controller.budget.dto.response.SendBudgetAlarmDto;
import com.zerobase.accountbook.domain.dailypayments.DailyPayments;
import com.zerobase.accountbook.domain.dailypayments.DailyPaymentsRepository;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final MemberRepository memberRepository;
    private final DailyPaymentsRepository dailyPaymentsRepository;

    public SendBudgetAlarmDto sendBudgetAlarm(long memberId) {
        LocalDateTime now = LocalDateTime.now();
        YearMonth yearMonth = YearMonth.of(now.getYear(), now.getMonth());
        String startDate = yearMonth + "-01";
        String endDate = yearMonth + "-" + yearMonth.lengthOfMonth();

        Member member = memberRepository.findById(memberId).get();
        Integer monthlySum = dailyPaymentsRepository.findMonthlySum(member.getId(), startDate, endDate);
        Integer monthlyBudget = member.getMonthlyBudget();

        String message;
        if (monthlySum == null) {
            message = "지출내역이 존재하지 않습니다.";
        } else if (monthlyBudget > monthlySum) {
            message = "남은 예산은 " + (monthlyBudget - monthlySum) + " 입니다.";
        } else {
            message = "이번달 예산을 초과했습니다! 예산을 다시 설정해보시겠어요?";
        }
        return SendBudgetAlarmDto.of(member.getId(), message);
    }
}
