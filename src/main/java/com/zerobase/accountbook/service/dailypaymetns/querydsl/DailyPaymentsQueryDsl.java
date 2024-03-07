package com.zerobase.accountbook.service.dailypaymetns.querydsl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.accountbook.domain.dailypayments.QDailyPayments;
import com.zerobase.accountbook.domain.totalamountpercategory.QTotalAmountPerCategory;
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
                        dailyPayments.categoryId.as("categoryId"),
                        dailyPayments.paidAmount.sum().as("totalAmount")
                ))
                .from(dailyPayments)
                .where(dailyPayments.createdAt.contains(date),
                        dailyPayments.member.id.eq(memberId))
                .groupBy(dailyPayments.member.id, dailyPayments.categoryId)
                .fetch();
    }

    @Transactional
    public List<DailyPaymentsCategoryDto>
    getYearlyTotalAmountPerCategoryByMemberId(Long memberId, String year) {

        QTotalAmountPerCategory perCategory =
                QTotalAmountPerCategory.totalAmountPerCategory;

        JPAQueryFactory qf = new JPAQueryFactory(entityManager);

        return qf.select(Projections.bean(
                DailyPaymentsCategoryDto.class,
                perCategory.categoryName.as("categoryName"),
                perCategory.totalAmount.sum().as("totalAmount")
        ))
                .from(perCategory)
                .where(perCategory.date.contains(year),
                        perCategory.member.id.eq(memberId))
                .groupBy(perCategory.categoryName)
                .fetch();
    }
}
