package com.zerobase.accountbook.service.dailypaymetns.querydsl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.accountbook.domain.dailypayments.QDailyPayments;
import com.zerobase.accountbook.service.dailypaymetns.dto.DailyPaymentsCategoryDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Component
public class DailyPaymentsQueryDsl {

    // 카테고리별 총 금액 계산 로직이 겹쳐서 클래스로 따로 뺐습니다.
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public List<DailyPaymentsCategoryDto> getTotalAmountPerCategoryByMemberId(
            String date, Long memberId
    ) {
        QDailyPayments dailyPayments = QDailyPayments.dailyPayments;

        JPAQueryFactory qf = new JPAQueryFactory(entityManager);

        return qf.select(Projections.bean(
                        DailyPaymentsCategoryDto.class,
                        dailyPayments.createdAt,
                        dailyPayments.categoryName.as("categoryName"),
                        dailyPayments.paidAmount.sum().as("totalAmount")
                ))
                .from(dailyPayments)
                .where(dailyPayments.createdAt.contains(date),
                        dailyPayments.member.id.eq(memberId))
                .groupBy(dailyPayments.member.id, dailyPayments.categoryName)
                .fetch();
    }
}
