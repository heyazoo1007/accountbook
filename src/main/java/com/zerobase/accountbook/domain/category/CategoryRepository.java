package com.zerobase.accountbook.domain.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByCategoryName(String categoryName);

    List<Category> findAllByMemberId(Long memberId);

    void deleteAllByMemberId(Long memberId);

    @Transactional
    @Modifying
    @Query("update Category c " +
           "set c.categoryName = '미분류' " +
           "where c.id =:categoryId")
    void updateUncategory(long categoryId);
}
