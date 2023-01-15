package com.zerobase.accountbook.domain.dailypayments;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyPaymentsRepository extends JpaRepository<DailyPayments, Long> {
}
