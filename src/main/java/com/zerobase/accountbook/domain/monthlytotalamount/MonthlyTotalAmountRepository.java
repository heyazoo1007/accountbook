package com.zerobase.accountbook.domain.monthlytotalamount;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MonthlyTotalAmountRepository extends JpaRepository<MonthlyTotalAmount, Long> {

    Optional<MonthlyTotalAmount> findByDateInfoAndMemberId(String dateInfo, Long memberId);
}
