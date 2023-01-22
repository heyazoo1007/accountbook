package com.zerobase.accountbook.domain.budget;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByBudgetYearMonth(String budgetYearMonth);

    void deleteAllByMemberId(Long memberId);
}
