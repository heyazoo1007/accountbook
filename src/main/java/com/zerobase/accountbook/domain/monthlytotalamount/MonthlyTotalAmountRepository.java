package com.zerobase.accountbook.domain.monthlytotalamount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MonthlyTotalAmountRepository extends JpaRepository<MonthlyTotalAmount, Long> {

    Optional<MonthlyTotalAmount> findByDateInfoAndMemberId(String dateInfo, Long memberId);

    @Query(
            nativeQuery = true,
            value = "select sum(mt.total_amount) " +
                    "from monthly_total_amount mt " +
                    "where mt.date_info like :year% " +
                    "and mt.member_id = :memberId"
    )
    Integer sumByMemberIdAndDateInfoContainingYear(Long memberId, String year);

    void deleteAllByMemberId(Long memberId);
}
