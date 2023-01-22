package com.zerobase.accountbook.domain.category;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByCategoryName(String categoryName);

    List<Category> findAllByMemberId(Long memberId);

    void deleteAllByMemberId(Long memberId);
}
