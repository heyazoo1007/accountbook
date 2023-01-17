package com.zerobase.accountbook.service.monthlytotalamount;

import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.domain.dailypayments.DailyPayments;
import com.zerobase.accountbook.domain.dailypayments.DailyPaymentsRepository;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import com.zerobase.accountbook.domain.monthlytotalamount.MonthlyTotalAmount;
import com.zerobase.accountbook.domain.monthlytotalamount.MonthlyTotalAmountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.zerobase.accountbook.common.exception.ErrorCode.NOT_FOUND_USER_EXCEPTION;

@Service
@RequiredArgsConstructor
public class MonthlyTotalAmountService {

    private final MemberRepository memberRepository;
    private final DailyPaymentsRepository dailyPaymentsRepository;
    private final MonthlyTotalAmountRepository monthlyTotalAmountRepository;


    @Scheduled(cron = "0 0 0 1 * * *") // 매달 1일 정각에 모든 사용자에 대해 실행
    private MonthlyTotalAmount saveMonthlyTotalAmount() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthBefore = now.minusMonths(1);

        List<DailyPayments> all =
                dailyPaymentsRepository.findByCreatedAtBetween(
                        oneMonthBefore.toString(),
                        now.toString()
                );

        // 회원 정보를 가져올 수 없어서 매일 지출내역 전체를 탐색하며 회원 정보 가져오기
        List<Member> members = memberRepository.findAll();
        for (Member each : members) {
            Integer memberSum = 0;
            String memberEmail = each.getEmail();
            for (DailyPayments dailyPayments : all) {
                if (memberEmail.equals(dailyPayments.getMember().getEmail())) {
                    memberSum += dailyPayments.getPaidAmount();
                }
            }

            Member member = validateMember(memberEmail);

            return monthlyTotalAmountRepository.save(MonthlyTotalAmount.builder()
                    .dateInfo(oneMonthBefore.toString().substring(0, 7)) // 2023-01 형태로 저장
                    .member(member)
                    .totalAmount(memberSum)
                    .createdAt(now)
                    .build());
        }
        return null;
    }

    private Member validateMember(String memberEmail) {
        return memberRepository.findByEmail(memberEmail).orElseThrow(
                () -> new AccountBookException(
                        "존재하지 않는 회원입니다.",
                        NOT_FOUND_USER_EXCEPTION
                )
        );
    }
}
