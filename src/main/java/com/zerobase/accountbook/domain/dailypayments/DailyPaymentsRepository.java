package com.zerobase.accountbook.domain.dailypayments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DailyPaymentsRepository extends JpaRepository<DailyPayments, Long> {

    List<DailyPayments> findAllByMemberIdAndDateContaining(Long memberId, String date);
    List<DailyPayments> findAllByMemberIdAndCreatedAtContaining(Long memberId, String CreatedAt);

    @Query(value = "select sum(dp.paid_amount) " +
                   "from daily_payments dp " +
                   "where dp.member_id = :memberId" +
                   "  and dp.date >= :startDate" +
                   "  and dp.date <= :endDate",
            nativeQuery = true)
    Integer findMonthlySum(Long memberId, String startDate, String endDate);

    @Query(
            nativeQuery = true,
            value = "select * " +
                    "from daily_payments dp " +
                    "where dp.member_id = :memberId " +
                    "and (dp.paid_where like %:keyword% " +
                    "or dp.memo like %:keyword%)"
    )
    List<DailyPayments> searchKeyword(long memberId, String keyword);

    @Query(
            nativeQuery = true,
            value = "select sum(dp.paid_amount) " +
                    "where dp.member_id = :memberId " +
                    "and dp.created_at like :date%"
    )
    int totalPaidAmountSoFarByMemberId(Long memberId, String date);
}
