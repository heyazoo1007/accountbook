package com.zerobase.accountbook.domain.member;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    @Query(
            nativeQuery = true,
            value = "select count(m.id) " +
                    "from member m"
    )
    Integer countAllMember();

    Page<Member> findAll(Pageable pageable);
}
