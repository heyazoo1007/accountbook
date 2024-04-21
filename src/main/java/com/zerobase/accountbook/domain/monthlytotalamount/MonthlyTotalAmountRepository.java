package com.zerobase.accountbook.domain.monthlytotalamount;

import com.zerobase.accountbook.domain.dailypayments.DailyPayments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MonthlyTotalAmountRepository extends JpaRepository<MonthlyTotalAmount, Long> {

    Optional<MonthlyTotalAmount> findByDateAndMemberId(String date, Long memberId);

    @Query(
            nativeQuery = true,
            value = "select sum(mt.total_amount) " +
                    "from monthly_total_amount mt " +
                    "where mt.date like :year% " +
                    "and mt.member_id = :memberId"
    )
    Integer sumByMemberIdAndDateInfoContainingYear(Long memberId, String year);
}
