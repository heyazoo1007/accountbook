package com.zerobase.accountbook.domain.budget;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.YearMonth;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByBudgetYearMonth(String date);

    @Query(nativeQuery = true,
            value = "select monthly_budget " +
                    "from budget b " +
                    "where b.member_id =:memberId " +
                    "  and b.budget_year_month =:yearMonth "
    )
    int findByMemberIdAndYearMonth(@Param("memberId") Long memberId,
                                   @Param("yearMonth") String yearMonth);
}
