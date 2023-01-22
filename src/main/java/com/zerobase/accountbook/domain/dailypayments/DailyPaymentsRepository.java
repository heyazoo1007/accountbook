package com.zerobase.accountbook.domain.dailypayments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DailyPaymentsRepository extends JpaRepository<DailyPayments, Long> {

    List<DailyPayments> findAllByMemberIdAndCreatedAtContaining(Long memberId, String createdAt);

    @Query(
            nativeQuery = true,
            value = "select * " +
                    "from daily_payments dp " +
                    "where dp.member_id = :memberId " +
                    "and (dp.paid_where like %:keyword% " +
                    "or dp.memo like %:keyword%)"
    )
    List<DailyPayments> searchKeyword(long memberId, String keyword);

    List<DailyPayments> findByMemberIdAndCreatedAtBetween(Long memberId, String before, String now);

    @Query(
            nativeQuery = true,
            value = "select sum(dp.paid_amount) " +
                    "where dp.member_id = :memberId " +
                    "and dp.created_at like :date%"
    )
    int totalPaidAmountSoFarByMemberId(Long memberId, String date);
}
