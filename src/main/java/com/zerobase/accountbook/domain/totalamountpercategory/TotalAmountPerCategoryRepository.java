

package com.zerobase.accountbook.domain.totalamountpercategory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TotalAmountPerCategoryRepository extends JpaRepository<TotalAmountPerCategory, Long> {

    List<TotalAmountPerCategory> findByDateInfoAndMemberId(String dateInfo, Long memberId);
}