package com.zerobase.accountbook.service.monthlytotalamount;

import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.domain.dailypayments.DailyPaymentsRepository;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import com.zerobase.accountbook.domain.monthlytotalamount.MonthlyTotalAmount;
import com.zerobase.accountbook.domain.monthlytotalamount.MonthlyTotalAmountRepository;
import com.zerobase.accountbook.service.dailypaymetns.dto.DailyPaymentsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static com.zerobase.accountbook.common.exception.ErrorCode.NOT_ACCEPTABLE_EXCEPTION;

@Service
@RequiredArgsConstructor
public class MonthlyTotalAmountService {

    private final MemberRepository memberRepository;
    private final DailyPaymentsRepository dailyPaymentsRepository;
    private final MonthlyTotalAmountRepository monthlyTotalAmountRepository;


    @Scheduled(cron = "0 0 0 1 * * *") // 매달 1일 정각에 모든 사용자에 대해 지난달 총 지출금액 저장
    private void saveMonthlyTotalAmount() {
        LocalDateTime oneMonthBefore = LocalDateTime.now().minusMonths(1);
        String yearMonth = String.format("%04d", oneMonthBefore.getYear()) + "-" +
                String.format("%02d", oneMonthBefore.getMonthValue());
        String startDate = YearMonth.from(oneMonthBefore).atDay(1).toString();
        String endDate = YearMonth.from(oneMonthBefore).atEndOfMonth().toString();

        // (회원 아이디, 기간에 해당하는 총 금액) 객체 리스트
        List<DailyPaymentsDto> dailyPaymentsDtos = dailyPaymentsRepository.
                findAllTotalAmountByYearMonth(startDate, endDate);
        for (DailyPaymentsDto dto : dailyPaymentsDtos) {
            monthlyTotalAmountRepository.save(
                    MonthlyTotalAmount.builder()
                    .date(yearMonth) // YYYY-mm 형태로 저장
                    .member(validateMember(dto))
                    .totalAmount(dto.getTotalAmount())
                    .build());
        }
    }

    private Member validateMember(DailyPaymentsDto dto) {
        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow(
                () -> new AccountBookException(
                        "존재하지 않는 회원입니다.",
                        NOT_ACCEPTABLE_EXCEPTION));
        return member;
    }
}
