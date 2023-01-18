package com.zerobase.accountbook.service.monthlytotalamount;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.accountbook.common.exception.model.AccountBookException;
import com.zerobase.accountbook.domain.dailypayments.DailyPaymentsRepository;
import com.zerobase.accountbook.domain.dailypayments.QDailyPayments;
import com.zerobase.accountbook.domain.member.Member;
import com.zerobase.accountbook.domain.member.MemberRepository;
import com.zerobase.accountbook.domain.monthlytotalamount.MonthlyTotalAmount;
import com.zerobase.accountbook.domain.monthlytotalamount.MonthlyTotalAmountRepository;
import com.zerobase.accountbook.service.dailypaymetns.dto.DailyPaymentsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.zerobase.accountbook.common.exception.ErrorCode.NOT_ACCEPTABLE_EXCEPTION;

@Service
@RequiredArgsConstructor
public class MonthlyTotalAmountService {

    @PersistenceContext
    private EntityManager entityManager;
    private final MemberRepository memberRepository;
    private final MonthlyTotalAmountRepository monthlyTotalAmountRepository;


    @Scheduled(cron = "0 0 0 1 * * *") // 매달 1일 정각에 모든 사용자에 대해 실행
    private void saveMonthlyTotalAmount() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthBefore = now.minusMonths(1);

        // (회원 아이디, 기간에 해당하는 총 금액) 객체 리스트
        List<DailyPaymentsDto> dailyPaymentsDtos =
                getMonthlyTotalAmountPerMember(
                oneMonthBefore.toString().substring(0, 9),
                now.toString().substring(0, 9)
        );

        for (DailyPaymentsDto dto : dailyPaymentsDtos) {
            Member member = validateMember(dto);

            monthlyTotalAmountRepository.save(MonthlyTotalAmount.builder()
                    .dateInfo(oneMonthBefore.toString().substring(0, 7)) // 2023-01 형태로 저장
                    .member(member)
                    .totalAmount(dto.getTotalAmount())
                    .createdAt(now)
                    .build());
        }
    }

    @Transactional
    public List<DailyPaymentsDto> getMonthlyTotalAmountPerMember(
            String start, String end
    ) {
        QDailyPayments dailyPayments = QDailyPayments.dailyPayments;

        JPAQueryFactory qf = new JPAQueryFactory(entityManager);

        JPAQuery<DailyPaymentsDto> query =
                        qf.select(Projections.bean(
                                DailyPaymentsDto.class,
                                dailyPayments.member.id.as("memberId"),
                                dailyPayments.paidAmount.sum()
                                        .as("totalAmount")
                            ))
                            .from(dailyPayments)
                            .groupBy(dailyPayments.member.id)
                            .where(dailyPayments.createdAt.between(start, end)
                );

        return query.fetch();
    }

    private Member validateMember(DailyPaymentsDto dto) {
        Member member = memberRepository.findById(dto.getMemberId()).orElseThrow(
                () -> new AccountBookException(
                        "존재하지 않는 회원입니다.",
                        NOT_ACCEPTABLE_EXCEPTION
                ));
        return member;
    }
}
