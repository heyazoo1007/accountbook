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

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final MemberRepository memberRepository;
    private final DailyPaymentsRepository dailyPaymentsRepository;

    public SendBudgetAlarmDto sendBudgetAlarm() {
        LocalDateTime now = LocalDateTime.now();
        YearMonth yearMonth = YearMonth.of(now.getYear(), now.getMonth());
        String startDate = yearMonth + "-01";
        String endDate = yearMonth + "-" + yearMonth.lengthOfMonth();

        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            String message;
            Integer monthlySum = dailyPaymentsRepository.findMonthlySum(member.getId(), startDate, endDate);
            Integer monthlyBudget = member.getMonthlyBudget();
            if (monthlySum == null) continue; // 지출내역이 없을 수도 있어서 monthlySum == null

            if (monthlyBudget > monthlySum) {
                message = "남은 예산은 " + (monthlyBudget - monthlySum) + " 입니다.";
            } else {
                message = "이번달 예산을 초과했습니다! 예산을 다시 설정해보시겠어요?";
            }
            return SendBudgetAlarmDto.of(member.getId(), message);
        }
        return null;
    }
}
