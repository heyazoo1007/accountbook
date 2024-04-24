package com.zerobase.accountbook.domain.monthlytotalamount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MonthlyTotalAmountRepository extends JpaRepository<MonthlyTotalAmount, Long> {

    Optional<MonthlyTotalAmount> findByDateAndMemberId(String date, Long memberId);

    @Query(
            nativeQuery = true,
            value = "select sum(total_amount) " +
                    "from monthly_total_amount " +
                    "where date like :year% " +
                    "  and member_id = :memberId"
    )
    Integer sumOfTheYearByMemberId(@Param("year") String year, @Param("memberId") Long memberId);
}
